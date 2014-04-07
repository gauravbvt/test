package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.core.query.ModelService;
import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.graph.DiagramFactory;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/3/14
 * Time: 5:23 PM
 */
public class SupplyChainsPng extends DiagramPng {

    private static final Logger LOG = LoggerFactory.getLogger( SupplyChainsPng.class );
    public static final String SUMMARIZE = "summarize";
    public static final String SUMMARIZE_BY_ORG = "org";
    public static final String SUMMARIZE_BY_ROLE = "role";
    public static final String SUMMARIZE_BY_ORG_TYPE = "orgType";
    public static final String SUMMARIZE_BY_ORG_TYPE_AND_ROLE = "orgTypeRole";

    public static final String ASSET_PARM = "asset";
    public static final String NONE = "NONE";
    public static final String ORPHANS = "orphans";
    public static final String AVAILABILITY = "availability";

    @Override
    protected Diagram makeDiagram( double[] diagramSize,
                                   String orientation,
                                   PageParameters parameters,
                                   CommunityService communityService,
                                   DiagramFactory diagramFactory ) throws DiagramException {
        MaterialAsset assetFocus = null;
        boolean summarizeByOrgType = false;
        boolean summarizeByOrg = false;
        boolean summarizeByRole = false;
        boolean showingOrphans = false;
        boolean showingAvailability = false;
        ModelService modelService = communityService.getModelService();
        if ( parameters.getNamedKeys().contains( SUMMARIZE ) ) {
            String summarizeBy = parameters.get( SUMMARIZE ).toString();
            if ( summarizeBy.equals( SUMMARIZE_BY_ORG_TYPE_AND_ROLE ) ) {
                summarizeByOrgType = true;
                summarizeByRole = true;
            } else if ( summarizeBy.equals( SUMMARIZE_BY_ORG_TYPE ) ) summarizeByOrgType = true;
            else if ( summarizeBy.equals( SUMMARIZE_BY_ORG ) ) summarizeByOrg = true;
            else if ( summarizeBy.equals( SUMMARIZE_BY_ROLE ) ) summarizeByRole = true;
        }
        if ( parameters.getNamedKeys().contains( ORPHANS ) ) {
            showingOrphans = parameters.get( ORPHANS ).toBoolean();
        }
        if ( parameters.getNamedKeys().contains( AVAILABILITY ) ) {
            showingAvailability = parameters.get( AVAILABILITY ).toBoolean();
        }
        if ( parameters.getNamedKeys().contains( ASSET_PARM ) ) {
            if ( !parameters.get( ASSET_PARM ).toString().equals( NONE ) )
                try {
                    long id = parameters.get( ASSET_PARM ).toLong();
                    try {
                        assetFocus = modelService.find( MaterialAsset.class, id );

                    } catch ( NotFoundException e ) {
                        LOG.error( "Failed to find focus material asset:" + id );
                    }
                } catch ( Exception ignored ) {
                    LOG.error( "Invalid focus material asset specified in parameters.", ignored );
                }
        }
        return diagramFactory.newSupplyChainsDiagram(
                assetFocus,
                summarizeByOrgType,
                summarizeByOrg,
                summarizeByRole,
                showingOrphans,
                showingAvailability,
                diagramSize,
                orientation );
    }
}
