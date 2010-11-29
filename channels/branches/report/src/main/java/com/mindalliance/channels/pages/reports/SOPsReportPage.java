package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.command.Commander;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Employment;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.support.FeedbackWidget;
import com.mindalliance.channels.query.Assignments;
import com.mindalliance.channels.dao.PlanManager;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.servlet.AbortWithHttpStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Comparator;

/**
 * The plan SOPs report.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 5, 2009
 * Time: 5:13:56 PM
 */
public class SOPsReportPage extends WebPage {

    /**
     * Restrictions to report generation.
     */
    private SelectorPanel selector;

    public SOPsReportPage( PageParameters parameters ) {
        super( parameters );
        setDefaultModel( new CompoundPropertyModel<Object>( this ) );

        selector = new SelectorPanel( "selector", parameters );
        if ( !selector.isValid() ) {
            if ( selector.getPlans().isEmpty() )
                throw new AbortWithHttpStatusException( HttpServletResponse.SC_FORBIDDEN, false );

            setRedirect( true );
            throw new RestartResponseException( getClass(), selector.getParameters() );
        }

        add(
            new Label( "pageTitle" ),
            newFeedbackWidget( selector.getPlanManager(), selector.getPlan() ),
            new Label( "reportTitle" ),
            selector,

            new BookmarkablePageLink<SOPsReportPage>( "top-link", SOPsReportPage.class ),
            new ListView<Organization>( "breadcrumbs" ) {
                @Override
                protected void populateItem( ListItem<Organization> item ) {
                    Organization organization = item.getModelObject();
                    item.add( new WebMarkupContainer( "crumb" )
                                .add( new Label( "text", organization.getName() ) )
                                .setRenderBodyOnly( true ) );
                }
            },
            new Label( "selector.actor.name" ),
            new Label( "selector.plan.name" ),
            new Label( "selector.plan.description" ),

            new ListView<Event>( "selector.assignments.events" ) {
                @Override
                protected IModel<Event> getListItemModel(
                        IModel<? extends List<Event>> listViewModel, int index ) {
                    return new CompoundPropertyModel<Event>(
                            super.getListItemModel( listViewModel, index ) );
                }

                @Override
                protected void populateItem( ListItem<Event> item ) {
                    item.add(
                        new Label( "name" ),
                        new Label( "description" ),
                        newPhaseList( selector.getAssignments().withSome( item.getModelObject() ) )
                    );
                }
            }
        );
    }

    public List<Organization> getBreadcrumbs() {
        List<Organization> result = new ArrayList<Organization>();
        if ( selector.isOrgSelected() )
            for ( Organization o = selector.getOrganization(); o != null; o = o.getParent() )
                result.add( 0, o );
        return result;
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
                Assignments phaseAssignments = eventAssignments.withSome( item.getModelObject() );

                item.add(
                    new Label( "name" ),
                    new Label( "description" ),
                    newTaskList( "immediates", phaseAssignments.getImmediates() ),
                    newTaskList( "optionals", phaseAssignments.getOptionals() ),
                    newIncomingList( "notified", phaseAssignments.getNotifications() ),
                    newIncomingList( "requested", phaseAssignments.getRequests() )
                    );
            }
        };
    }

    private Component newTaskList( String id, Assignments assignments ) {
        List<Assignment> a = assignments.getAssignments();
        Collections.sort( a, new Comparator<Assignment>() {
            public int compare( Assignment o1, Assignment o2 ) {
                int i = Assignments.stringify( o1.getSpecableActor() )
                            .compareTo( Assignments.stringify( o2.getSpecableActor() ) );

                return i == 0 ? o1.getPart().getTask().compareTo( o2.getPart().getTask() )
                              : i;
            }
        } );

        return new WebMarkupContainer( id )
            .add( new ListView<Assignment>( "tasks", a ) {
                @Override
                protected void populateItem( ListItem<Assignment> item ) {
                    Assignment assignment = item.getModelObject();
                    Actor actor = assignment.getActor();
                    item.add(
                        newTaskLink( assignment.getPart(), actor ),
                        new Label( "to", getToLabel( assignment ) )
                            .setVisible( !selector.isActorSelected() ),
                        newSubtaskList( getSubtasks( assignment ) )
                    );
                }
            } )
            .setVisible( !assignments.isEmpty() );
    }

    private Component newIncomingList( String id, final Assignments assignments ) {
        return new WebMarkupContainer( id )
            .add( new ListView<Assignment>( "tasks", toSortedFlowList( assignments ) ) {
                @Override
                protected void populateItem( ListItem<Assignment> item ) {
                    Assignment assignment = item.getModelObject();
                    Part part = assignment.getPart();
                    Actor actor = assignment.getActor();
                    Assignments sources = selector.getAllAssignments().getSources( part );

                    ResourceSpec prefix = sources.getCommonSpec( null );
                    item.add(
                        newFlowLink( part, actor ),
                        new Label( "to", getToLabel( assignment ) )
                            .setVisible( !selector.isActorSelected() ),
                        new Label( "source", prefix.getReportSource() )
                            .add( new AttributeModifier( "title", true,
                                    new Model<String>( getSourcesList( sources, prefix ) ) ) ),

                        newSubtaskList( getSubtasks( assignment ) )
                    );
                }
            } )
            .setVisible( !assignments.isEmpty() );
    }

    private List<Assignment> toSortedFlowList( Assignments assignments ) {
        List<Assignment> result = new ArrayList<Assignment>( assignments.getAssignments() );
        Collections.sort( result, new Comparator<Assignment>() {
            public int compare( Assignment o1, Assignment o2 ) {
                int toComparison = getToLabel( o1 ).compareTo( getToLabel( o2 ) );
                if ( toComparison == 0 ) {

                    int fromComparison = getFromLabel( o1 ).compareTo( getFromLabel( o2 ) );
                    return fromComparison == 0 ?
                           getFlowString( o1.getPart() ).compareTo( getFlowString( o2.getPart() ) )
                         : fromComparison;
                }
                else
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
                }
                else
                    return toComparison;
            }
        } );
        return result;
    }

    private String getFromLabel( Assignment assignment ) {
        Part part = assignment.getPart();
        return selector.getAllAssignments()
                .getSources( part ).without( assignment.getActor() )
                .getCommonSpec( part ).getReportSource();
    }

    private Component newSubtaskList( List<Assignment> subtasks ) {
        return new WebMarkupContainer( "subtasks" )
            .add( new ListView<Assignment>( "tasks", subtasks ) {
                @Override
                protected void populateItem( ListItem<Assignment> item ) {
                    Assignment a = item.getModelObject();
                    item.add( newTaskLink( a.getPart(), a.getActor() ) );
                }
            } )
            .setVisible( !subtasks.isEmpty() );
    }

    private MarkupContainer newFlowLink( Part part, Specable actor ) {
        Plan plan = selector.getPlan();

        PageParameters parms = new PageParameters();
        parms.put( SelectorPanel.ACTOR_PARM, Long.toString( ( (Identifiable) actor ).getId() ) );
        parms.put( SelectorPanel.PLAN_PARM, plan.getUri() );
        parms.put( SelectorPanel.VERSION_PARM, Long.toString( plan.getVersion() ) );
        parms.put( "task", Long.toString( part.getId() ) );

        return new BookmarkablePageLink<AssignmentReportPage>(
                "task", AssignmentReportPage.class, parms )
                .add( new Label( "name", getFlowString( part ) ) );
    }

    private MarkupContainer newTaskLink( Part part, Specable actor ) {
        Plan plan = selector.getPlan();

        PageParameters parms = new PageParameters();
        parms.put( SelectorPanel.ACTOR_PARM, Long.toString( ( (Identifiable) actor ).getId() ) );
        parms.put( SelectorPanel.PLAN_PARM, plan.getUri() );
        parms.put( SelectorPanel.VERSION_PARM, Long.toString( plan.getVersion() ) );
        parms.put( AssignmentReportPage.TASK_PARM, Long.toString( part.getId() ) );

        return new BookmarkablePageLink<AssignmentReportPage>(
                "task", AssignmentReportPage.class, parms )
                .add( new Label( "name", part.getTask() ) );
    }

    private static String getFlowString( Part part ) {
        StringBuilder result = new StringBuilder();
        Set<String> flowNames = new HashSet<String>();

        Iterator<Flow> iterator = part.flows();
        while ( iterator.hasNext() ) {
            Flow flow = iterator.next();
            if (    part.equals( flow.getSource() ) && flow.isTriggeringToSource()
                 || part.equals( flow.getTarget() ) && flow.isTriggeringToTarget() )
                flowNames.add( flow.getName() );
        }

        List<String> sortedNames = new ArrayList<String>( flowNames );
        if ( sortedNames.size() > 1 )
            Collections.sort( sortedNames );
        for ( int i = 0; i < sortedNames.size(); i++ ) {
            if ( i != 0 )
                result.append( i == sortedNames.size() - 1 ? " or " : ", " );

            result.append( sortedNames.get( i ) );
        }

        return result.toString();
    }

    private List<Assignment> getSubtasks( Assignment parent ) {
        return toSortedTaskList(
                selector.getAllAssignments()
                        .from( parent )
                        .withSome( parent.getActor() ) );

    }

    private static String getToLabel( Assignment assignment ) {
        return "By " + assignment.getSpecableActor() + " - ";
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
                        ( prefix.getActor() == null || !prefix.getActor().isActual() )
                            && employment.getActor() != null
                            && !employment.getActor().isArchetype() ?
                                           employment.getActor() : null,
                        prefix.getRole() == null ? employment.getRole() : null,
                        prefix.getOrganization() == null ? employment.getOrganization() : null,
                        prefix.getJurisdiction() == null ? employment.getJurisdiction() : null
                );
                buf.append( spec.getReportSource() );
            }
        }
        return buf.toString();
    }

    public String getPageTitle() {
        return "Channels - " + getReportTitle();
    }

    public String getReportTitle() {
        return "SOPs - " + selector.getSelection().toString();
    }

    public SelectorPanel getSelector() {
        return selector;
    }

    static Component newFeedbackWidget( PlanManager planManager, Plan plan ) {
        FeedbackWidget feedbackWidget = new FeedbackWidget(
                "feedback-widget",
                new Model<String>(
                    plan.getUserSupportCommunityUri( planManager.getDefaultSupportCommunity() ) ),
                true );

        makeVisible( feedbackWidget, false );
        return feedbackWidget;
    }

    /**
     * Set the headers of the Page being served.
     *
     * @param response the response.
     */
    @Override
    protected void setHeaders( WebResponse response ) {
        super.setHeaders( response );

        Channels channels = (Channels) getApplication();
        Commander commander = channels.getCommander( selector.getPlan() );
        long longTime = commander.getLastModified();
        long now = System.currentTimeMillis();

        response.setDateHeader( "Date", now );
//        response.setDateHeader( "Expires", now + 24L*60*60*1000 );
        response.setDateHeader( "Last-Modified", longTime );
    }

    /**
     * Set a component's visibility.
     *
     * @param component a component
     * @param visible   a boolean
     */
    private static void makeVisible( Component component, boolean visible ) {
        component.add( new AttributeModifier( "style", true, new Model<String>(
                visible ? "" : "display:none" ) ) );
    }


}
