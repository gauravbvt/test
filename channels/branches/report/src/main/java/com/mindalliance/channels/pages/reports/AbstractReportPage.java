// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.
package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.dao.PlanDao;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Specable;
import com.mindalliance.channels.query.Assignments;
import com.mindalliance.channels.query.PlanService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValueConversionException;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

/**
 * ...
 */
public class AbstractReportPage extends WebPage {

    public static final String FLOW_PARM = "flow";

    public static final String TASK_PARM = "task";

    private transient PlanService service;

    private transient Assignment assignment;

    @SpringBean
    private User user;

    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private AttachmentManager attachmentManager;

    public AbstractReportPage( PageParameters parameters ) {
        super( parameters );
        setDefaultModel( new CompoundPropertyModel<Object>( this ) {
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
        Part task = getService().find( Part.class, taskId );
        Specable actor = getActor( actorId );

        Assignments assignments = getService().getAssignments()
                                    .assignedTo( task ).with( actor );
        if ( assignments.isEmpty() )
            throw new NotFoundException();

        else if ( assignments.size() != 1 )
            LoggerFactory.getLogger( AssignmentReportPage.class ).warn(
                        "More than one assignment for task {} and actor {}", taskId, actorId );

        return assignments.getAssignments().iterator().next();
    }

    protected PlanService getService() {
        if ( service == null )
            try {
                PageParameters parameters = getPageParameters();
                service = getService( parameters.getString( SelectorPanel.PLAN_PARM, null ),
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
            return getService().find( Actor.class, actorId );

        } catch ( NotFoundException ignored ) {
            return getService().find( Role.class, actorId );
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
        Plan plan = getService().getPlan();

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
}
