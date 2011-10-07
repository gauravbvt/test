package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.engine.analysis.graph.RequirementRelationship;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import org.apache.wicket.PageParameters;
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
public class RequiredNetworkingPage extends PngWebPage {

    /**
     * The log.
     */
    private static final Logger LOG = LoggerFactory.getLogger( RequiredNetworkingPage.class );
    private Event event = null;
    private Organization selectedOrganization = null;
    private RequirementRelationship selectedRequirementRelationship = null;

    private Phase.Timing timing = null;

    //-------------------------------
    public RequiredNetworkingPage( PageParameters parameters ) {
        super( parameters );
        QueryService queryService = getQueryService();
        if ( parameters.containsKey( "timing" ) && !parameters.getString( "timing" ).equals( "NONE" ) ) {
            String name = parameters.getString( "timing" );
            try {
                timing = Phase.Timing.valueOf( parameters.getString( "timing" ) );
            } catch ( Exception e ) {
                LOG.warn( "Invalid timing name: " + name );
            }
        }
        if ( parameters.containsKey( "event" ) && !parameters.getString( "event" ).equals( "ALL" ) ) {
            Long eventId = Long.valueOf( parameters.getString( "event" ) );
            try {
                event = queryService.find( Event.class, eventId );
            } catch ( NotFoundException e ) {
                LOG.warn( "Selected event not found at :" + eventId, e );
            }
        }
        if ( parameters.containsKey( "organization" ) && !parameters.getString( "organization" ).equals( "NONE" ) ) {
            Long orgId = Long.valueOf( parameters.getString( "organization" ) );
            try {
                selectedOrganization = queryService.find( Organization.class, orgId );
            } catch ( NotFoundException e ) {
                LOG.warn( "Selected organization not found at :" + orgId, e );
            }
        }
        if ( parameters.containsKey( "connection" ) && !parameters.getString( "connection" ).equals( "NONE" ) ) {
            Long relId = Long.valueOf( parameters.getString( "connection" ) );
            selectedRequirementRelationship = new RequirementRelationship();
            selectedRequirementRelationship.setId( relId, getQueryService() );
        }
    }

    //-------------------------------
    protected Diagram makeDiagram( double[] size, String orientation ) throws DiagramException {
        DiagramFactory<Organization, RequirementRelationship> factory = getDiagramFactory();
        return factory.newRequiredNetworkingDiagram(
                timing,
                event,
                selectedOrganization,
                selectedRequirementRelationship,
                size,
                orientation );
    }
}
