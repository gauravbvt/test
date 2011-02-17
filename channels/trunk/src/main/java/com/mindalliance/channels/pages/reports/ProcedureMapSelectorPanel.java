package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.attachments.AttachmentManager;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.imaging.ImagingService;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.plan.PlanProcedureMapPanel;
import com.mindalliance.channels.query.Assignments;
import com.mindalliance.channels.query.PlanService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Procedure map selector panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/14/11
 * Time: 2:42 PM
 */
public class ProcedureMapSelectorPanel extends AbstractUpdatablePanel implements AssignmentsSelector {

    private Part selectedPart;
    private ModelEntity focusEntity;
    private Segment segment;
    private Assignment assignment;
    private Flow selectedFlow;
    private Actor selectedActor;
    private Role selectedRole;
    private Organization selectedOrganization;

    @SpringBean
    private AttachmentManager attachmentManager;

    @SpringBean
    private ImagingService imagingService;

    @SpringBean
    private PlanManager planManager;

    private PlanProcedureMapPanel procedureMapPanel;

    private List<Change> history = new ArrayList<Change>();
    ;

    private boolean goingBack = false;

    public ProcedureMapSelectorPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        procedureMapPanel = new PlanProcedureMapPanel( "procedure-map" );
        add( procedureMapPanel );
    }

    @Override
    public void changed( Change change ) {
        if ( change.isSelected() ) {
            resetSelected();
            setSelected( change );
            Identifiable identifiable = change.getSubject( getQueryService() );
            focusEntity = (ModelEntity) change.getQualifier( "focus" );
            if ( identifiable instanceof Part ) {
                selectedPart = (Part) identifiable;
            } else if ( identifiable instanceof Flow ) {
                selectedFlow = (Flow) identifiable;
                selectedPart = (Part) change.getQualifier( "part" );
                if ( selectedPart == null )
                    selectedPart = (Part) ( (Flow) identifiable ).getSource();
            } else if ( identifiable instanceof Segment ) {
                segment = (Segment) identifiable;
            } else if ( identifiable instanceof Plan ) {
                segment = null;
            } else if ( identifiable instanceof Assignment ) {
                setAssignment( (Assignment) identifiable );
            }
            addToHistory( change );
        }
    }

    public boolean canGoBack() {
        return history.size() > 0;
    }

    public void addToHistory( Change change ) {
        if ( !change.isForInstanceOf( Plan.class ) && !change.isForInstanceOf( Segment.class ) ) {
            goingBack = change.hasQualifier( "goingBack" );
            if ( !goingBack ) {
                if ( history.isEmpty() ) history.add( baseChange() );
                history.add( change );
            }
        }
    }

    public Change goBack() {
        Change change;
        if ( !canGoBack() ) {
            change = baseChange();
        } else {
            change = history.remove( history.size() - 1 );
            if ( !goingBack && !history.isEmpty() ) change = history.remove( history.size() - 1 );
        }
        change.addQualifier( "goingBack", true );
        goingBack = true;
        return change;
    }

    private Change baseChange() {
        Change change = new Change( Change.Type.Selected, segment == null ? getPlan() : segment );
        if ( focusEntity != null )
            change.addQualifier( "focus", focusEntity );
        change.setProperty( "showReport" );
        return change;
    }

    private void resetHistory() {
        history = new ArrayList<Change>();
    }

    public void setGoingForward() {
        goingBack = false;
        resetHistory();
    }

    private void setSelected( Change change ) {
        selectedActor = (Actor) change.getQualifier( "actor" );
        selectedRole = (Role) change.getQualifier( "role" );
        selectedOrganization = (Organization) change.getQualifier( "organization" );
    }

    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( !procedureMapPanel.isPlanSelected() ) {
            Segment impliedSegment = (Segment) change.getQualifier( "segment" );
            if ( impliedSegment != null && !segment.equals( impliedSegment ) ) {
                segment = impliedSegment;
                procedureMapPanel.refreshSegment( target, segment );
            }
        }
        super.updateWith( target, change, updated );
    }

    public void resetSelected() {
        assignment = null;
        selectedPart = null;
        selectedFlow = null;
        selectedActor = null;
        selectedRole = null;
        selectedOrganization = null;
    }

    @Override
    public Assignments getAssignments() {
        return getAllAssignments()
                .assignedTo( selectedPart )
                .with( selectedActor )
                .with( selectedOrganization )
                .with( selectedRole )
                .forSegment( segment );
    }

    private boolean isOrgFocused() {
        return focusEntity != null && focusEntity instanceof Organization;
    }

    private boolean isActorFocused() {
        return focusEntity != null && focusEntity instanceof Actor;
    }

    @Override
    public ResourceSpec getSelection() {
        return new ResourceSpec(
                getActor(),
                null,
                getOrganization(),
                null
        );
    }

    @Override
    public Assignments getAllAssignments() {
        return getQueryService().getAssignments();
    }

    @Override
    public boolean isPlanner() {
        return User.current().isPlanner();
    }

    @Override
    public Organization getOrganization() {
        return selectedOrganization;
    }

    public Actor getActor() {
        return selectedActor;
    }

    public Role getRole() {
        return selectedRole;
    }

    @Override
    public boolean isOrgSelected() {
        return getOrganization() != null;
    }

    @Override
    public boolean isActorSelected() {
        return getActor() != null;
    }

    public String getTitle() {
        if ( assignment != null ) {
            return titleForTask( assignment );
        } else if ( selectedFlow != null && selectedPart != null && selectedActor != null ) {
            return titleForFlow();
        } else if ( selectedPart != null ) {
            return titleForTask( selectedPart, selectedActor );
        } else if ( segment != null ) {
            return "Procedures in \"" + segment.getName() + "\"";
        } else {
            return "All procedures";
        }

    }

    private String titleForTask( Assignment assign ) {
        Actor actor = selectedActor;
        if ( actor == null ) {
            actor = assign.getActor();
        }
        Part part = assign.getPart();
        return titleForTask( part, actor );
    }

    private String titleForTask( Part part, Actor actor ) {
        return titlePersona( part, actor )
                +  " doing...";
    }

    private String titlePersona(Part part, Actor actor ) {
        String persona = "";
        String acting = ( actor != null && !actor.isUnknown() )
                ? actor.getName()
                : "";
        persona += acting;
        Role role = selectedRole != null
                ? selectedRole
                : part != null
                ? part.getRole()
                : null;
        if ( role != null && !role.isUnknown() ) {
            persona += (persona.isEmpty() ? "" : " as ") + role.getName();
        }
        Organization org = selectedOrganization != null
                ? selectedOrganization
                : part != null
                ? part.getOrganization()
                : null;
        if ( org != null && !org.isUnknown() ) {
            persona += (persona.isEmpty() ? "Someone at " : " at ") + org.getName();
        }
        if ( persona.isEmpty() ) persona = "Someone";
        return persona;
    }

    private String titleForFlow() {
        return titlePersona( selectedPart, selectedActor )
                + ( isSending( selectedPart ) ? " sending..." : " receiving..." );
    }

    private boolean isSending( Part part ) {
        return part.equals( getFlow().getSource() );
    }


    public void setAssignment( Assignment assignment ) {
        this.assignment = assignment;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public Flow getFlow() {
        return selectedFlow;
    }

    public Part getPart() {
        return selectedPart;
    }

    @Override
    public AttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    @Override
    public PlanService getPlanService() {
        return new PlanService( planManager, attachmentManager, getPlan() );
    }

    @Override
    public ImagingService getImagingService() {
        return imagingService;
    }

}
