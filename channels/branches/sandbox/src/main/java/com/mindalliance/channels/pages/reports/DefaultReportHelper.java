package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.imaging.ImagingService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.query.Assignments;
import com.mindalliance.channels.query.PlanService;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/15/11
 * Time: 10:47 PM
 */
public class DefaultReportHelper implements ReportHelper, Serializable {

    private final AssignmentsSelector selector;
    private final Updatable updatable;

    private Assignment assignment;
    private Flow flow;
    private Part part;

    public DefaultReportHelper( AssignmentsSelector selector, Updatable updatable ) {
        this.selector = selector;
        this.updatable = updatable;
    }

    public DefaultReportHelper( AssignmentsSelector selector, Updatable updatable, Assignment assignment ) {
        this( selector, updatable );
        this.assignment = assignment;
    }

    public DefaultReportHelper(
            AssignmentsSelector selector,
            Updatable updatable,
            Flow flow,
            Part part ) {
        this( selector, updatable );
        this.flow = flow;
        this.part = part;
    }


    @Override
    public void setAsDefaultModel( Component component ) {
        component.setDefaultModel( new CompoundPropertyModel<Object>( this ) {
            @Override
            public void detach() {
                super.detach();
            }
        } );
    }

    @Override
    public Part getPart() {
        return assignment == null ? part : assignment.getPart();
    }

    @Override
    public AttributeModifier newCssClass( String cssClass ) {
        return new AttributeModifier( "class", true, new Model<String>( cssClass ) );
    }

    @Override
    public AttachmentManager getAttachmentManager() {
        return selector.getAttachmentManager();
    }

    @Override
    public PlanService getPlanService() {
        return selector.getPlanService();
    }

    @Override
    public Assignment getAssignment() {
        return assignment;
    }

    @Override
    public Specable getActor() {
        return assignment != null ? assignment.getActor() : selector.getActor();
    }

    @Override
    public Component newFlowLink( final Flow flow ) {
        AjaxLink link = new AjaxLink<String>( "flow" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                Change change = new Change( Change.Type.Selected, flow );
                change.addQualifier( "part", getPart() );
                change.addQualifier( "actor", (Actor) getActor() );
                update( target, change );
            }
        };
        String delay;
        if ( flow == null )
            delay = "";
        else {
            delay = flow.getMaxDelay().toString();
        }
        link.add( new Label( "delay", delay ) );

        if ( flow != null )
            link.add( newCssClass( getPlanService().computeSharingPriority( flow )
                    .toString().toLowerCase() ) );

        link.setVisible( flow != null );
        return link;

    }

    @Override
    public Component newFlowLink( Part part, final Specable actor ) {
        final Assignment assign = (Assignment) CollectionUtils.find(
                selector.getAllAssignments().assignedTo( part ).getAssignments(),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Assignment) object ).getActor().equals( actor );
                    }
                } );
        AjaxLink link = new AjaxLink<String>( "task" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target,
                        new Change( Change.Type.Selected, assign ) );
            }
        };
        link.add( new Label( "name", getFlowString( part ) ) );
        return link;

    }

    @Override
    public String getFlowString( Part part ) {
        StringBuilder result = new StringBuilder();
        Set<String> flowNames = new HashSet<String>();

        Iterator<Commitment> iterator = selector.getCommitmentsTriggering( part ).iterator();
        while ( iterator.hasNext() ) {
            Flow flow = iterator.next().getSharing();
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


    @Override
    public MarkupContainer newTaskLink( Part part, final Specable actor ) {
        final Assignment assign = (Assignment) CollectionUtils.find(
                getPlanService().findAllAssignments( part, true ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return actor == null
                                || ( (Actor) actor ).isUnknown()
                                || ( (Assignment) object ).getActor().equals( actor );
                    }
                } );
        AjaxLink link = new AjaxLink<String>( "task" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                if ( assign != null ) {
                    // assign should never be null
                    Change change = new Change( Change.Type.Selected, assign );
                    if ( actor != null ) change.addQualifier( "actor", (Actor) actor );
                    update( target, change );
                }
            }
        };
        link.add( new Label( "name", part.getTask() ) );
        return link;
    }

    @Override
    public Flow getFlow() {
        return flow;
    }

    @Override
    public ImagingService getImagingService() {
        return selector.getImagingService();
    }

    @Override
    public boolean isSending() {
        return getPart().equals( getFlow().getSource() );
    }

    @Override
    public Specable getFocusEntity() {
        return selector.getFocusEntity();
    }

    @Override
    public Assignments getNotifications( Assignments assignments, QueryService queryService ) {
        Assignments result = new Assignments( queryService.getPlan().getLocale() );
        for ( Assignment assignment : assignments.getAssignments() ) {
            if ( !selector.getCommitmentsTriggering( assignment.getPart() ).isEmpty() )
                result.add( assignment );
        }
        return result;
    }

    @Override
    public Assignments getRequests( Assignments assignments, QueryService queryService ) {
        Assignments result = new Assignments( queryService.getPlan().getLocale() );
        for ( final Assignment assignment : assignments.getAssignments() ) {
            boolean triggeredByRequest = CollectionUtils.exists(
                    selector.getCommitments(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            Flow flow = ( (Commitment) object ).getSharing();
                            Part part = assignment.getPart();
                            return part.equals( flow.getSource() )
                                    && flow.isTriggeringToSource()
                                    && flow.isAskedFor();
                        }
                    }
            );
            if ( triggeredByRequest )
                result.add( assignment );
        }
        return result;
    }

    private void update( AjaxRequestTarget target, Change change ) {
        updatable.changed( change );
        updatable.updateWith(
                target,
                change,
                new ArrayList<Updatable>() );
    }

}
