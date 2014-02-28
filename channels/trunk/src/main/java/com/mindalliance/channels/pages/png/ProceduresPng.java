package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.query.ModelService;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import com.mindalliance.channels.pages.ModelPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Procedures map png page.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/9/11
 * Time: 12:41 PM
 */
public class ProceduresPng extends DiagramPng {



    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ProceduresPng.class );
    public static final String SUMMARIZE = "summarize";
    public static final String SUMMARIZE_BY_ORG = "org";
    public static final String SUMMARIZE_BY_ROLE = "role";
    public static final String FOCUS_CLASS = "focusClass";
    public static final String FOCUS_ID = "focusId";
    public static final String SUMMARIZE_BY_ORG_TYPE = "orgType";
    public static final String SUMMARIZE_BY_ORG_TYPE_AND_ROLE = "orgTypeRole";

    /**
     * {@inheritDoc}
     */
    protected Diagram makeDiagram( double[] size,
                                   String orientation,
                                   PageParameters parameters,
                                   CommunityService communityService,
                                   DiagramFactory diagramFactory ) throws DiagramException {
        Segment segment = null;
        boolean summarizeByOrgType = false;
        boolean summarizeByOrg = false;
        boolean summarizeByRole = false;
        ModelEntity focusEntity = null;
        ModelService modelService = communityService.getModelService();
        segment = ModelPage.findSegment( modelService, parameters );
        if ( parameters.getNamedKeys().contains( SUMMARIZE ) ) {
            String summarizeBy = parameters.get( SUMMARIZE ).toString();
            if ( summarizeBy.equals( SUMMARIZE_BY_ORG_TYPE_AND_ROLE ) ) {
                summarizeByOrgType = true;
                summarizeByRole = true;
            }
            else if ( summarizeBy.equals( SUMMARIZE_BY_ORG_TYPE ) ) summarizeByOrgType = true;
            else if ( summarizeBy.equals( SUMMARIZE_BY_ORG ) ) summarizeByOrg = true;
            else if ( summarizeBy.equals( SUMMARIZE_BY_ROLE ) ) summarizeByRole = true;
        }
        if ( parameters.getNamedKeys().contains( FOCUS_CLASS )
                && parameters.getNamedKeys().contains( FOCUS_ID ) ) {
            try {
                long id = parameters.get( FOCUS_ID ).toLong();
                String focusClass = parameters.get( FOCUS_CLASS ).toString();
                try {
                    if ( focusClass.equals( Organization.class.getSimpleName() ) ) {
                        focusEntity = modelService.find( Organization.class, id );
                    } else if ( focusClass.equals( Actor.class.getSimpleName() ) ) {
                        focusEntity = modelService.find( Actor.class, id );
                    }
                } catch ( NotFoundException e ) {
                    LOG.error( "Failed to find focus entity " + focusClass + ":" + id );
                }
            } catch ( Exception ignored ) {
                LOG.error( "Invalid focus entity specified in parameters.", ignored );
            }
        }
        return diagramFactory.newChecklistsMapDiagram(
                segment,
                summarizeByOrgType,
                summarizeByOrg,
                summarizeByRole,
                focusEntity,
                size,
                orientation );
    }

}
