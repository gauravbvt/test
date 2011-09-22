package com.mindalliance.channels.pages.procedures;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.CommanderFactory;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.dao.UserDao;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Assignment;
import com.mindalliance.channels.core.model.Commitment;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.model.ResourceSpec;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.Specable;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.engine.nlp.SemanticMatcher;
import com.mindalliance.channels.engine.query.Assignments;
import com.mindalliance.channels.engine.query.QueryService;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.plan.PlanProcedureMapPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Specable focusEntity;
    private Segment segment;
    private Assignment assignment;
    private Flow selectedFlow;
    private Actor selectedActor;
    private Role selectedRole;
    private Organization selectedOrganization;
    private Assignments allAssignments;
    private List<Commitment> allCommitments;

    @SpringBean
    private AttachmentManager attachmentManager;

    @SpringBean
    private ImagingService imagingService;

    @SpringBean
    private PlanManager planManager;

    @SpringBean
    private UserDao userDao;

    @SpringBean
    private CommanderFactory commanderFactory;

    @SpringBean
    private SemanticMatcher semanticMatcher;

    private PlanProcedureMapPanel procedureMapPanel;

    private List<Change> history = new ArrayList<Change>();

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
            if ( change.hasQualifier( "focus" ) )
                focusEntity = (Specable) change.getQualifier( "focus" );
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
        allCommitments = null;
        allAssignments = null;
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
                .with( selectedRole );

    }

    public Assignments getAllAssignments() {
        if ( allAssignments == null ) {
            Set<Assignment> assignments = new HashSet<Assignment>();
            for ( Commitment commitment : getCommitments() ) {
                assignments.add( commitment.getCommitter() );
                assignments.add( commitment.getBeneficiary() );
            }
            Assignments results = new Assignments( getPlan().getLocale() );
            for ( Assignment assignment : assignments )
                results.add( assignment );
            allAssignments = results;
        }
        return allAssignments;
    }

    @Override
    public List<Commitment> getCommitments() {
        if ( allCommitments == null ) {
            QueryService queryService = getQueryService();
            List<Commitment> commitments = new ArrayList<Commitment>();
            List<Flow> allFlows = queryService.findAllSharingFlows( segment );
            for ( Flow flow : allFlows ) {
                commitments.addAll( queryService.findAllCommitments( flow, true ) );
            }
            List<Commitment> results = new ArrayList<Commitment>();
            for ( Commitment commitment : commitments ) {
                if ( focusEntity == null || isFocusedOn( commitment ) ) {
                    results.add( commitment );
                }
            }
            allCommitments = results;
        }
        return allCommitments;
    }

    private boolean isFocusedOn( Commitment commitment ) {
        return focusEntity != null
                && (
                isFocusedOn( commitment.getCommitter() )
                        || isFocusedOn( commitment.getBeneficiary() )
        );
    }

    private boolean isFocusedOn( Assignment assignment ) {
        return isFocusedOnAgent( assignment ) || isFocusedOnOrganization( assignment );
    }

    private boolean isFocusedOnAgent( Assignment assignment ) {
        return focusEntity != null && assignment.getActor().equals( focusEntity );
    }

    private boolean isFocusedOnOrganization( Assignment assignment ) {
        return focusEntity != null
                && assignment.getOrganization().narrowsOrEquals(
                (ModelEntity) focusEntity,
                getQueryService().getPlan().getLocale() );
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

/*
    @Override
    public Assignments getAllAssignments() {
        return getQueryService().getAssignments();
    }
*/

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
        } else if ( selectedFlow != null && selectedPart != null /*&& selectedActor != null*/ ) {
            return titleForFlow();
        } else if ( selectedPart != null ) {
            return titleForTask( selectedPart, selectedActor );
        } else if ( segment != null ) {
            return titleForSegment( segment );
        } else {
            return titleForPlan();
        }

    }

    private String titleForPlan() {
        return "All procedures in " + getPlan().getName()
                + ( focusEntity != null
                ? ( " for " + ( (ModelEntity) focusEntity ).getName() )
                : "" )
                ;
    }

    private String titleForSegment( Segment seg ) {
        if ( focusEntity != null ) {
           return ( (ModelEntity) focusEntity ).getName()
                   + " in \""
                   +  seg.getName()
                   + "\"";
        } else {
           return "Procedures in \""
                + seg.getName()
                + "\"";
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
                + " doing...";
    }

    private String titlePersona( Part part, Actor actor ) {
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
            persona += ( persona.isEmpty() ? "" : " as " ) + role.getName();
        }
        Organization org = selectedOrganization != null
                ? selectedOrganization
                : part != null
                ? part.getOrganization()
                : null;
        if ( org != null && !org.isUnknown() ) {
            persona += ( persona.isEmpty() ? "Someone at " : " at " ) + org.getName();
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

    @Override
    public Segment getSegment() {
        return segment;
    }

    @Override
    public Assignments getSources( Part part ) {
        Assignments results = new Assignments( getPlan().getLocale() );
        for ( Commitment commitment : getCommitmentsTriggering( part ) ) {
            results.add( commitment.getCommitter() );
        }
        return results;
    }

    @Override
    public List<Commitment> getCommitmentsTriggering( Part part ) {
        List<Commitment> results = new ArrayList<Commitment>();
        for ( Commitment commitment : getCommitments() ) {
            if ( commitment.getBeneficiary().getPart().equals( part )
                    && commitment.getSharing().isTriggeringToTarget()
                    || commitment.getCommitter().getPart().equals( part )
                    && commitment.getSharing().isTriggeringToSource() )
                results.add( commitment );
        }
        return results;
    }

    public Part getPart() {
        return selectedPart;
    }

    @Override
    public AttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    @Override
    public QueryService getPlanService() {
        return commanderFactory.getCommander( getPlan() ).getQueryService();
    }

    @Override
    public ImagingService getImagingService() {
        return imagingService;
    }

    public boolean hasProcedures() {
        return !getAssignments().isEmpty();
    }

    public Specable getFocusEntity() {
        return focusEntity;
    }

}
