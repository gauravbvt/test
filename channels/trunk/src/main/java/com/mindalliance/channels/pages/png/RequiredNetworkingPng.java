package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.participation.Agency;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.engine.analysis.graph.RequirementRelationship;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Required networking page.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/6/11
 * Time: 6:49 PM
 */
public class RequiredNetworkingPng extends DiagramPng {

    /**
     * The log.
     */
    private static final Logger LOG = LoggerFactory.getLogger( RequiredNetworkingPng.class );

    protected Diagram makeDiagram( double[] size,
                                   String orientation,
                                   PageParameters parameters,
                                   PlanCommunity planCommunity,
                                   DiagramFactory diagramFactory ) throws DiagramException {
        Event event = null;
        Agency selectedAgency = null;
        RequirementRelationship selectedRequirementRelationship = null;
        PlanService planService = planCommunity.getPlanService();
        Phase.Timing timing = null;
        if ( parameters.getNamedKeys().contains( "timing" ) ) {
            String name = parameters.get( "timing" ).toString();
            try {
                timing = Phase.Timing.valueOf( parameters.get( "timing" ).toString() );
            } catch ( Exception e ) {
                LOG.warn( "Invalid timing name: " + name );
            }
        }
        if ( parameters.getNamedKeys().contains( "event" ) ) {
            Long eventId = parameters.get( "event" ).toLong();
            try {
                event = planService.find( Event.class, eventId );
            } catch ( NotFoundException e ) {
                LOG.warn( "Selected event not found at :" + eventId, e );
            }
        }
        if ( parameters.getNamedKeys().contains( "agency" )
                && !parameters.get( "agency" ).toString().equals( "NONE" ) ) {
            Long agencyId = parameters.get( "agency" ).toLong();
            try {
                selectedAgency = planCommunity.getParticipationManager().findAgencyById( agencyId, planCommunity );
            } catch ( NotFoundException e ) {
                LOG.warn( "Selected organization not found at :" + agencyId, e );
            }
        }
        if ( parameters.getNamedKeys().contains( "connection" )
                && !parameters.get( "connection" ).toString().equals( "NONE" ) ) {
            String relId = parameters.get( "connection" ).toString();
            selectedRequirementRelationship = new RequirementRelationship();
            selectedRequirementRelationship.setId( relId, planCommunity );
        }
        return diagramFactory.newRequiredNetworkingDiagram(
                timing,
                event,
                selectedAgency,
                selectedRequirementRelationship,
                size,
                orientation );
    }
}
