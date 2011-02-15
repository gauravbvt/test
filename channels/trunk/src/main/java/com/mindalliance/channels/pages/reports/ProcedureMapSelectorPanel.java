package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Assignment;
import com.mindalliance.channels.model.Commitment;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Participation;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.plan.PlanProcedureMapPanel;
import com.mindalliance.channels.query.Assignments;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/14/11
 * Time: 2:42 PM
 */
public class ProcedureMapSelectorPanel extends AbstractUpdatablePanel implements AssignmentsSelector {

    private Assignments selectedAssignments;
    private ModelEntity focusEntity;

    public ProcedureMapSelectorPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        PlanProcedureMapPanel procedureMapPanel = new PlanProcedureMapPanel( "procedure-map" );
        add( procedureMapPanel );
    }

    @Override
    public void changed( Change change ) {
        // Grab selected part or flow, plus summarization to reconstruct assignments or commitments
        if ( change.isSelected() ) {
            Identifiable identifiable =  change.getSubject( getQueryService() );
            focusEntity = (ModelEntity)change.getQualifier( "focus" );
            if ( identifiable instanceof Part ) {
                computeAssignments( (Part)identifiable );
            } else if ( identifiable instanceof Flow ) {
                computeAssignments( (Flow) identifiable );
            } else {
                selectedAssignments = null;
            }
        }
        super.changed( change );
    }

    private void computeAssignments( Part part ) {
        selectedAssignments = new Assignments( getPlan().getLocale() );
        List<Assignment> assignments = getQueryService().findAllAssignments( part, true );
        for ( Assignment assignment : assignments ) {
            if ( isRetained( assignment ) ) {
                selectedAssignments.add( assignment );
            }
        }
    }

    private boolean isRetained( Assignment assignment ) {
        // If not planner, don't retain someone else's assignment
        if ( !isPlanner() && !userIsParticipant( assignment.getActor() ) ) return false;
        // if focusing on an actor or organization, only retain if its assignment
        if ( focusEntity != null ) {
            if ( focusEntity instanceof Actor ) {
                return assignment.getActor().equals( focusEntity );
            } else {
                // focus on organization
                return assignment.getOrganization().narrowsOrEquals( focusEntity, getPlan().getLocale() );
            }
        } else {
            return true;
        }
    }

    private void computeAssignments( Flow flow ) {
        selectedAssignments = new Assignments( getPlan().getLocale() );
        List<Commitment> commitments = getQueryService().findAllCommitments( flow );
        for ( Commitment commitment : commitments ) {
            if ( isRetained( commitment.getCommitter() )) {
                selectedAssignments.add( commitment.getCommitter() );
            }
            if ( isRetained( commitment.getBeneficiary() )) {
                selectedAssignments.add( commitment.getBeneficiary() );
            }
        }
    }

    private boolean userIsParticipant( Actor actor ) {
        Participation participation = getQueryService().findParticipation( User.current().getUsername() );
        return participation != null && participation.getActor().equals( actor );
    }


    @Override
    public Assignments getAssignments() {
        return getAllAssignments();
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
        return selectedAssignments == null
                ?  getQueryService().getAssignments()
                : selectedAssignments;
    }

    @Override
    public boolean isPlanner() {
        return User.current().isPlanner();
    }

    @Override
    public Organization getOrganization() {
        return focusEntity instanceof Organization ? (Organization)focusEntity : null;
    }

    private Actor getActor() {
        return focusEntity instanceof Actor ? (Actor)focusEntity : null;
    }

    @Override
    public boolean isOrgSelected() {
        return getOrganization() != null;
    }

    @Override
    public boolean isActorSelected() {
        return getActor() != null;
    }
}
