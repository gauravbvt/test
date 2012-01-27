package com.mindalliance.channels.pages.procedures;

import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Employment;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/14/11
 * Time: 12:51 PM
 */
public class AssignmentsReportPanel extends AbstractUpdatablePanel {

    private final AssignmentsSelector selector;
    private final ReportHelper reportHelper;

    public AssignmentsReportPanel( String id, AssignmentsSelector selector, ReportHelper reportHelper ) {
        super( id );
        this.selector = selector;
        this.reportHelper = reportHelper;
        init();
    }

    private void init() {
        add( new Label( "selector.plan.name" ) );
        add( new Label( "selector.plan.description" ) );

        add( new ListView<Event>( "selector.assignments.events" ) {
            @Override
            protected IModel<Event> getListItemModel(
                    IModel<? extends List<Event>> listViewModel, int index ) {
                return new CompoundPropertyModel<Event>(
                        super.getListItemModel( listViewModel, index ) );
            }

            @Override
            protected void populateItem( ListItem<Event> item ) {
                Event event = item.getModelObject();
                item.add(
                        new WebMarkupContainer( "evName" )
                                .add( new Label( "name" ) )
                                .add( new AttributeModifier( "name", true, new Model<String>( String
                                        .valueOf(
                                                event
                                                        .getId() ) ) ) ),
                        new Label( "description" ),
                        newPhaseList( selector.getAssignments().with( event ) )
                );
            }
        } );
    }

    private ListView<Phase> newPhaseList( final Assignments eventAssignments ) {
        return new ListView<Phase>( "phases", eventAssignments.getPhases() ) {
            @Override
            protected IModel<Phase> getListItemModel(
                    IModel<? extends List<Phase>> listViewModel, int index ) {
                return new CompoundPropertyModel<Phase>(
                        super.getListItemModel( listViewModel, index ) );
            }

            @Override
            protected void populateItem( ListItem<Phase> item ) {
                Assignments phaseAssignments = eventAssignments.with( item.getModelObject() );

                Phase phase = item.getModelObject();
                item.add(
                        new WebMarkupContainer( "pName" )
                                .add( new Label( "name" ) )
                                .add( new AttributeModifier( "name", true, new Model<String>( String
                                        .valueOf(
                                                phase
                                                        .getId() ) ) ) ),
                        new Label( "description" ),
                        newTaskList( "immediates",
                                "a" + phase.getId(),
                                phaseAssignments.getImmediates( getQueryService() ) ),
                        newIncomingList( "notified",
                                "b" + phase.getId(),
                                reportHelper.getNotifications( phaseAssignments, getQueryService() ) ),
                        newIncomingList(
                                "requested",
                                "c" + phase.getId(),
                                reportHelper.getRequests( phaseAssignments, getQueryService() ) ),
                        newTaskList(
                                "optionals",
                                "d" + phase.getId(),
                                phaseAssignments.getOptionals( getQueryService() ) )
                );
            }
        };
    }

    private Component newTaskList( String id, String anchor, Assignments assignments ) {
        List<Assignment> a = assignments.getAssignments();
        Collections.sort( a, new Comparator<Assignment>() {
            public int compare( Assignment o1, Assignment o2 ) {
                int i = Assignments.stringify( o1.getSpecableActor() )
                        .compareTo( Assignments.stringify( o2.getSpecableActor() ) );

                return i == 0 ? o1.getPart().getTask().compareTo( o2.getPart().getTask() )
                        : i;
            }
        } );

        final Component tasks = new ListView<Assignment>( "tasks", a ) {
            @Override
            protected void populateItem( final ListItem<Assignment> item ) {
                Assignment assignment = item.getModelObject();
                Actor actor = assignment.getActor();
                if ( actor == null || actor.isUnknown() )
                    actor = (Actor) selector.getActor();
                List<Assignment> subtasks = getSubtasks( assignment );
                final Component subtaskList = newSubtaskList( subtasks ).setOutputMarkupId( true );
                item.add( reportHelper.newTaskLink( assignment.getPart(), assignment.getEmployment() ), new Label( "to",
                        getToLabel(
                                assignment ) )
                        .setVisible( !selector.isActorSelected() ), subtaskList );
                item.setOutputMarkupId( true );
                if ( !subtasks.isEmpty() ) {
                    item.add( new AttributeModifier( "class",
                            true,
                            new AbstractReadOnlyModel<Object>() {
                                @Override
                                public Object getObject() {
                                    return subtaskList.isVisible() ? ""
                                            : "collapsed";
                                }
                            } ) );
                    item.add( new AjaxEventBehavior( "onclick" ) {
                        @Override
                        protected void onEvent( AjaxRequestTarget target ) {
                            subtaskList.setVisible( !subtaskList.isVisible() );
                            target.add( item );
                        }
                    } );
                }
            }
        }
                .setVisible( false )
                .setOutputMarkupId( true );

        final WebMarkupContainer anchorContainer = new WebMarkupContainer( "anchor" );
        anchorContainer.setOutputMarkupId( true );
        anchorContainer.add( new AttributeModifier( "name", true, new Model<String>( anchor ) ) );
        markAnchorExpansionState( anchorContainer, tasks.isVisible() );
        final Component component = new WebMarkupContainer( id )
                .add( tasks )
                .add( anchorContainer )
                .setVisible( !assignments.isEmpty() )
                .setOutputMarkupId( true );

        component.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                tasks.setVisible( !tasks.isVisible() );
                markAnchorExpansionState( anchorContainer, tasks.isVisible() );
                target.add( component );
            }
        } );

        return component;
    }

    private Component newIncomingList( String id, String anchor, final Assignments assignments ) {
        final Component tasks = new ListView<Assignment>( "tasks", toSortedFlowList( assignments ) ) {
            @Override
            protected void populateItem( final ListItem<Assignment> item ) {
                Assignment assignment = item.getModelObject();
                Part part = assignment.getPart();
                Actor actor = assignment.getActor();
                Assignments sources = selector.getSources( part );

                ResourceSpec prefix = sources.getCommonSpec( null );
                List<Assignment> subtasks = getSubtasks( assignment );
                final Component subtaskList = newSubtaskList( subtasks ).setOutputMarkupId( true );
                item.add( reportHelper.newFlowLink( part, assignment.getEmployment() ), new Label( "to", getToLabel(
                        assignment ) ).setVisible( !selector.isActorSelected() ), new Label( "source",
                        prefix
                                .getReportSource( "" ) )
                        .add( new AttributeModifier( "title", true, new Model<String>( getSourcesList(
                                sources,
                                prefix ) ) ) ),

                        subtaskList );
                item.setOutputMarkupId( true );
                if ( !subtasks.isEmpty() ) {
                    item.add( new AttributeModifier( "class",
                            true,
                            new AbstractReadOnlyModel<Object>() {
                                @Override
                                public Object getObject() {
                                    return subtaskList.isVisible() ? ""
                                            : "collapsed";
                                }
                            } ) );
                    item.add( new AjaxEventBehavior( "onclick" ) {
                        @Override
                        protected void onEvent( AjaxRequestTarget target ) {
                            subtaskList.setVisible( !subtaskList.isVisible() );
                            target.add( item );
                        }
                    } );
                }
            }
        }
                .setVisible( false );
        final WebMarkupContainer anchorContainer = new WebMarkupContainer( "anchor" );
        anchorContainer.setOutputMarkupId( true );
        anchorContainer.add( new AttributeModifier( "name", true, new Model<String>( anchor ) ) );
        markAnchorExpansionState( anchorContainer, tasks.isVisible() );
        final Component component = new WebMarkupContainer( id )
                .add( tasks )
                .add( anchorContainer )
                .setVisible( !assignments.isEmpty() )
                .setOutputMarkupId( true );

        component.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                tasks.setVisible( !tasks.isVisible() );
                markAnchorExpansionState( anchorContainer, tasks.isVisible() );
                target.add( component );
            }
        } );

        return component;
    }

    private void markAnchorExpansionState( Component comp, boolean expanded ) {
        comp.add( new AttributeModifier(
                "class",
                true,
                new Model<String>( expanded ? "anchor-expanded" : "anchor-collapsed" ) ) );
    }

    private List<Assignment> toSortedFlowList( Assignments assignments ) {
        List<Assignment> result = new ArrayList<Assignment>( assignments.getAssignments() );
        Collections.sort( result, new Comparator<Assignment>() {
            public int compare( Assignment o1, Assignment o2 ) {
                int toComparison = getToLabel( o1 ).compareTo( getToLabel( o2 ) );
                if ( toComparison == 0 ) {

                    int fromComparison = getFromLabel( o1 ).compareTo( getFromLabel( o2 ) );
                    return fromComparison == 0 ?
                            reportHelper.getFlowString( o1.getPart() )
                                    .compareTo( reportHelper.getFlowString( o2.getPart() ) )
                            : fromComparison;
                } else
                    return toComparison;
            }
        } );
        return result;
    }

    private List<Assignment> toSortedTaskList( Assignments assignments ) {
        List<Assignment> result = new ArrayList<Assignment>( assignments.getAssignments() );
        Collections.sort( result, new Comparator<Assignment>() {
            public int compare( Assignment o1, Assignment o2 ) {
                int toComparison = getToLabel( o1 ).compareTo( getToLabel( o2 ) );
                if ( toComparison == 0 ) {

                    int fromComparison = getFromLabel( o1 ).compareTo( getFromLabel( o2 ) );
                    return fromComparison == 0 ?
                            o1.getPart().getTask().compareTo( o2.getPart().getTask() )
                            : fromComparison;
                } else
                    return toComparison;
            }
        } );
        return result;
    }

    private String getFromLabel( Assignment assignment ) {
        Part part = assignment.getPart();
        return selector.getAllAssignments()
                .getSources( part ).without( assignment.getActor() )
                .getCommonSpec( part ).getReportSource( "" );
    }

    private Component newSubtaskList( List<Assignment> subtasks ) {
        return new WebMarkupContainer( "subtasks" )
                .add( new ListView<Assignment>( "tasks", subtasks ) {
                    @Override
                    protected void populateItem( ListItem<Assignment> item ) {
                        Assignment a = item.getModelObject();
                        item.add( reportHelper.newTaskLink( a.getPart(), a.getActor() ) );
                    }
                } )
                .setVisible( false );
    }

    private List<Assignment> getSubtasks( Assignment parent ) {
        Assignments all = selector.getAllAssignments();
        Assignments from = all.from( parent );
        Specable specable = parent.getEmployment();
        Assignments with = from.with( specable );
        return toSortedTaskList( with );

    }

    private static String getToLabel( Assignment assignment ) {
        return "Done by "
                + assignment.getSpecableActor()
                + organizationToLabel( assignment )
                + " - ";
    }

    private static String organizationToLabel( Assignment assignment ) {
        Organization org = assignment.getOrganization();
        if ( org == null || org.isUnknown() ) {
            return "";
        } else {
            return " at " + org.getName();
        }
    }

    private static String getSourcesList( Assignments assignments, ResourceSpec prefix ) {
        StringBuilder buf = new StringBuilder();

        Set<Employment> es = new HashSet<Employment>();
        for ( Assignment assignment : assignments )
            es.add( assignment.getEmployment() );

        if ( es.size() > 1 ) {
            boolean first = true;
            for ( Employment employment : es ) {
                if ( !first )
                    buf.append( "; " );

                first = false;
                ResourceSpec spec = new ResourceSpec(
                        prefix.getActor() != null && prefix.getActor().isActual()
                                || employment.getActor() == null || employment.getActor().isArchetype() ?
                                null : employment.getActor(),
                        prefix.getRole() == null ? employment.getRole() : null,
                        prefix.getOrganization() == null ? employment.getOrganization() : null,
                        prefix.getJurisdiction() == null ? employment.getJurisdiction() : null
                );
                buf.append( spec.getReportSource( "" ) );
            }
        }
        return buf.toString();
    }

    public AssignmentsSelector getSelector() {
        return selector;
    }


}
