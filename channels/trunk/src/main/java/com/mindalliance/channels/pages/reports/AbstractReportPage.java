// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.dao.PlanDao;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.imaging.ImagingService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.query.Assignments;
import com.mindalliance.channels.query.PlanService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * ...
 */
public class AbstractReportPage extends WebPage implements ReportHelper {

    public static final String FLOW_PARM = "flow";

    public static final String TASK_PARM = "task";

    private transient PlanService service;

    private transient Assignment assignment;

    private Flow flow;


    @SpringBean
    private User user;

    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private AttachmentManager attachmentManager;

    @SpringBean
    private ImagingService imagingService;

    public AbstractReportPage( PageParameters parameters ) {
        super( parameters );
        setAsDefaultModel( this );


    }

    public void setAsDefaultModel( Component component ) {
        component.setDefaultModel( new CompoundPropertyModel<Object>( this ) {
            @Override
            public void detach() {
                super.detach();
                service = null;
                assignment = null;
            }
        } );
    }

    public AttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    public PlanManager getPlanManager() {
        return planManager;
    }

    public ImagingService getImagingService() {
        return imagingService;
    }

    @Override
    public boolean isSending() {
        return getPart().equals( getFlow().getSource() );
    }

    public User getUser() {
        return user;
    }

    //--------------------------------
    public Assignment getAssignment() {
        if ( assignment == null )
            try {
                PageParameters parameters = getPageParameters();
                assignment = getAssignment( parameters.getLong( SelectorPanel.ACTOR_PARM, 0 ),
                                            parameters.getLong( TASK_PARM, 0 ) );

            } catch ( StringValueConversionException ignored ) {
                throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_NOT_FOUND );

            } catch ( NotFoundException ignored ) {
                throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_NOT_FOUND );
            }

        return assignment;
    }

    private Assignment getAssignment( long actorId, long taskId ) throws NotFoundException {
        Part task = getPlanService().find( Part.class, taskId );
        Specable actor = getActor( actorId );

        Assignments assignments = getPlanService().getAssignments()
                                    .assignedTo( task ).with( actor );
        if ( assignments.isEmpty() )
            throw new NotFoundException();

        else if ( assignments.size() != 1 )
            LoggerFactory.getLogger( AssignmentReportPage.class ).warn(
                        "More than one assignment for task {} and actor {}", taskId, actorId );

        return assignments.getAssignments().iterator().next();
    }

    public PlanService getPlanService() {
        if ( service == null )
            try {
                PageParameters parameters = getPageParameters();
                service = getService( parameters.getString( SelectorPanel.PLAN_PARM, user.getPlanUri() ),
                                      parameters.getInt( SelectorPanel.VERSION_PARM, 0 ) );

            } catch ( StringValueConversionException ignored ) {
                throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_NOT_FOUND );

            } catch ( NotFoundException ignored ) {
                throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_NOT_FOUND );
            }
        return service;
    }

    private PlanService getService( String uri, int version ) throws NotFoundException {
        boolean development;
        if ( uri == null )
            throw new NotFoundException();

        else if ( user.isPlanner( uri ) ) {
            int number = planManager.getDefinitionManager().get( uri )
                            .getDevelopmentVersion().getNumber();

            development = version == 0 || version == number;

        } else if ( user.isParticipant( uri ) )
            development = false;

        else
            throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_FORBIDDEN );

        PlanDao planDao = planManager.getDao( uri, development );
        if ( planDao == null )
            throw new NotFoundException();

        return new PlanService( planManager, attachmentManager, planDao.getPlan() );
    }

    private Specable getActor( long actorId ) throws NotFoundException {
        try {
            return getPlanService().find( Actor.class, actorId );

        } catch ( NotFoundException ignored ) {
            return getPlanService().find( Role.class, actorId );
        }
    }

    public Specable getActor() {
        return getAssignment().getSpecableActor();
    }

    public String getType() {
        Part part = getAssignment().getPart();
        return Assignments.isImmediate( part )    ? "Immediate Tasks"
             : Assignments.isOptional( part )     ? "Optional Task"
             : Assignments.isNotification( part ) ? "Information Received"
             : Assignments.isRequest( part )      ? "Information Requested"
                                                  : "Other";
    }

    public PageParameters getTopParameters() {
        Plan plan = getPlanService().getPlan();

        PageParameters parms = new PageParameters();
        parms.put( SelectorPanel.PLAN_PARM, plan.getUri() );
        parms.put( SelectorPanel.VERSION_PARM, plan.getVersion() );
        return parms;
    }

    public Part getPart() {
        return getAssignment().getPart();
    }

    public String getReportTitle() {
        return "Channels - SOPs - " + getActor();
    }

    public String getPageTitle() {
        return getReportTitle();
    }

    public AttributeModifier newCssClass( String cssClass ) {
        return new AttributeModifier( "class", true, new Model<String>( cssClass ) );
    }

    public Component newFlowLink( Flow flow ) {
         PageParameters parms = getTopParameters();
         parms.put( SelectorPanel.ACTOR_PARM,
                    Long.toString( ( (Identifiable) getActor() ) .getId() ) );
         parms.put( TASK_PARM, Long.toString( getPart().getId() ) );
         String delay;
         if ( flow == null )
             delay = "";
         else {
             parms.put( FLOW_PARM, Long.toString( flow.getId() ) );
             delay = flow.getMaxDelay().toString();
         }

         Component result =
                 new BookmarkablePageLink<CommitmentReportPage>( "flow", CommitmentReportPage.class, parms )
                         .add( new Label( "delay", delay ) );

         if ( flow != null )
             result.add( newCssClass( getPlanService().computeSharingPriority( flow )
                                             .toString().toLowerCase() ));

         return result.setVisible( flow != null );
     }

    public Component newFlowLink( Part part, Specable actor ) {
         Plan plan = getPlanService().getPlan();

         PageParameters parms = new PageParameters();
         parms.put( SelectorPanel.ACTOR_PARM, Long.toString( ( (Identifiable) actor ).getId() ) );
         parms.put( SelectorPanel.PLAN_PARM, plan.getUri() );
         parms.put( SelectorPanel.VERSION_PARM, Long.toString( plan.getVersion() ) );
         parms.put( "task", Long.toString( part.getId() ) );

         return new BookmarkablePageLink<AssignmentReportPage>(
                 "task", AssignmentReportPage.class, parms )
                 .add( new Label( "name", getFlowString( part ) ) );
     }

    public String getFlowString( Part part ) {
        StringBuilder result = new StringBuilder();
        Set<String> flowNames = new HashSet<String>();

        Iterator<Flow> iterator = part.flows();
        while ( iterator.hasNext() ) {
            Flow flow = iterator.next();
            if ( part.equals( flow.getSource() ) && flow.isTriggeringToSource()
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

    public  MarkupContainer newTaskLink( Part part, Specable actor ) {
        Plan plan = getPlanService().getPlan();

        PageParameters parms = new PageParameters();
        parms.put( SelectorPanel.ACTOR_PARM, Long.toString( ( (Identifiable) actor ).getId() ) );
        parms.put( SelectorPanel.PLAN_PARM, plan.getUri() );
        parms.put( SelectorPanel.VERSION_PARM, Long.toString( plan.getVersion() ) );
        parms.put( AbstractReportPage.TASK_PARM, Long.toString( part.getId() ) );

        return new BookmarkablePageLink<AssignmentReportPage>(
                "task", AssignmentReportPage.class, parms )
                .add( new Label( "name", part.getTask() ) );
    }

    public Flow getFlow() {
        if ( flow == null ) {
            try {
                flow = getPlanService().find( Flow.class, getPageParameters().getAsLong( FLOW_PARM ) );

            } catch ( NumberFormatException ignored ) {
                throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_NOT_FOUND );

            } catch ( NotFoundException ignored ) {
                throw new AbortWithWebErrorCodeException( HttpServletResponse.SC_NOT_FOUND );
            }
        }

        return flow;
    }


}
