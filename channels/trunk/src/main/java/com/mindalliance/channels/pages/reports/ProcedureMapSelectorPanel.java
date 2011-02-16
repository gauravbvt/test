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
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.plan.PlanProcedureMapPanel;
import com.mindalliance.channels.query.Assignments;
import com.mindalliance.channels.query.PlanService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.spring.injection.annot.SpringBean;

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

    @SpringBean
    private AttachmentManager attachmentManager;

    @SpringBean
    private ImagingService imagingService;

    @SpringBean
    private PlanManager planManager;

    private PlanProcedureMapPanel procedureMapPanel;

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
            Identifiable identifiable = change.getSubject( getQueryService() );
            focusEntity = (ModelEntity) change.getQualifier( "focus" );
           if ( identifiable instanceof Part ) {
                resetSelected();
                selectedPart = (Part) identifiable;
            } else if ( identifiable instanceof Flow ) {
                resetSelected();
                selectedFlow = (Flow) identifiable;
                selectedPart = (Part) change.getQualifier( "part" );
                if ( selectedPart == null )
                    selectedPart = (Part) ( (Flow) identifiable ).getSource();
                selectedActor = (Actor) change.getQualifier( "actor" );
            } else if ( identifiable instanceof Segment ) {
                resetSelected();
                segment = (Segment) identifiable;
            } else if ( identifiable instanceof Plan ) {
                resetSelected();
                segment = null;
            } else if ( identifiable instanceof Assignment ) {
                resetSelected();
                setAssignment( (Assignment) identifiable );
            } else {
                resetSelected();
            }
        }
    }

    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        Segment impliedSegment = (Segment) change.getQualifier( "segment" );
         if ( impliedSegment != null && ( segment == null || !segment.equals( impliedSegment) ) ) {
             segment = impliedSegment;
             procedureMapPanel.refreshSegment( target, segment );
         }
         super.updateWith( target, change, updated );
    }

    public void resetSelected() {
        assignment = null;
        selectedPart = null;
        selectedFlow = null;
        selectedActor = null;
    }

    @Override
    public Assignments getAssignments() {
        Assignments as = getAllAssignments();
        Assignments partAssignments;
        if ( selectedPart != null )
             partAssignments = as.assignedTo( selectedPart );
        else
            partAssignments = as;
        Assignments focused = isActorSelected()
                ? partAssignments.notFrom( (Actor) focusEntity ).with( (Actor) focusEntity )
                : isOrgSelected()
                ? partAssignments.with( (Organization) focusEntity )
                : partAssignments;
        return segment != null
                ? focused.forSegment( segment )
                : focused;
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
        return focusEntity instanceof Organization ? (Organization) focusEntity : null;
    }

    public Actor getActor() {
        return selectedActor;
        // return focusEntity instanceof Actor ? (Actor) focusEntity : null;
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
            String title = "";
            if ( ! assignment.getActor().isUnknown() ) {
                title += assignment.getActor().getName() + " doing \"";
            } else {
            title += "Task \"";
            }
            title +=  assignment.getPart().getTask() + "\"";
            return title;
        } else if ( selectedFlow != null && selectedPart != null && selectedActor != null ) {
            return selectedActor.getName()
                    + ( isSending() ? " sending " : " receiving " )
                    + "\""
                    + selectedFlow.getName() + "\"";
        }
        else if ( selectedPart != null ) {
                return "Task \"" + selectedPart.getTask() + "\"";
        } else if ( segment != null ) {
                return "Procedures in \"" + segment.getName() + "\"";
        } else {
                return "All procedures";
        }

    }

    private boolean isSending() {
        return getPart().equals( getFlow().getSource() );
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
