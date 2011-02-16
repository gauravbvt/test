package com.mindalliance.channels.pages.reports;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.ResourceSpec;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.plan.PlanProcedureMapPanel;
import com.mindalliance.channels.query.Assignments;

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
    private Assignments assignments;

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
        if ( change.isSelected() ) {
            Identifiable identifiable = change.getSubject( getQueryService() );
            focusEntity = (ModelEntity) change.getQualifier( "focus" );
            segment = (Segment) change.getQualifier( "segment" );
            if ( identifiable instanceof Part ) {
                selectedPart = (Part) identifiable;
            } else if ( identifiable instanceof Flow ) {
                selectedPart = (Part)((Flow) identifiable).getSource();
            } else if ( identifiable instanceof Segment ) {
                selectedPart = null;
                segment = (Segment) identifiable;
            } else if ( identifiable instanceof Plan ) {
                selectedPart = null;
                segment = null;
            } else {
                selectedPart = null;
            }
        }
        super.changed( change );
    }

    @Override
    public Assignments getAssignments() {
        Assignments as = getAllAssignments();
        Assignments focused =  isActorSelected()
                ? as.notFrom( (Actor)focusEntity ).with( (Actor)focusEntity )
                : isOrgSelected()
                ? as.with( (Organization)focusEntity )
                : as;
        Assignments inSegment =  segment != null
                ? focused.forSegment( segment )
                : focused;

        if ( selectedPart != null )
            return inSegment.assignedTo( selectedPart );
        else
            return inSegment;
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
        if ( assignments == null )
            assignments = getQueryService().getAssignments();
        return assignments;
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
        return focusEntity instanceof Actor ? (Actor) focusEntity : null;
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
        return  selectedPart != null
                ? "Assignments to \"" + selectedPart.getTask() + "\""
                : segment != null
                ? "All assignments in \"" + segment.getName() + "\""
                : "All assignments";

    }
}
