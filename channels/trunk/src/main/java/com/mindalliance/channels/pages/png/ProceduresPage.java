package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.graph.Diagram;
import com.mindalliance.channels.graph.DiagramException;
import com.mindalliance.channels.model.Actor;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.NotFoundException;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.query.QueryService;
import org.apache.wicket.PageParameters;
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
public class ProceduresPage extends PngWebPage {

    private Segment segment = null;
    private boolean summarizeByOrgType = false;
    private boolean summarizeByOrg = false;
    private boolean summarizeByRole = false;
    private ModelEntity focusEntity = null;


    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ProceduresPage.class );
    public static final String SUMMARIZE = "summarize";
    public static final String SUMMARIZE_BY_ORG = "org";
    public static final String SUMMARIZE_BY_ROLE = "role";
    public static final String FOCUS_CLASS = "focusClass";
    public static final String FOCUS_ID = "focusId";
    public static final String SUMMARIZE_BY_ORG_TYPE = "orgType";

    public ProceduresPage( PageParameters parameters ) {
        super( parameters );
        QueryService queryService = getQueryService();
        segment = PlanPage.findSegment( queryService, parameters );
        if ( parameters.containsKey( SUMMARIZE ) ) {
            String summarizeBy = parameters.getString( SUMMARIZE );
            if ( summarizeBy.equals( SUMMARIZE_BY_ORG_TYPE ) ) summarizeByOrgType = true;
            else if ( summarizeBy.equals( SUMMARIZE_BY_ORG ) ) summarizeByOrg = true;
            else if ( summarizeBy.equals( SUMMARIZE_BY_ROLE ) ) summarizeByRole = true;
        }
        if ( parameters.containsKey( FOCUS_CLASS ) && parameters.containsKey( FOCUS_ID ) ) {
            try {
                long id = parameters.getLong( FOCUS_ID );
                String focusClass = parameters.getString( FOCUS_CLASS );
                try {
                    if ( focusClass.equals( Organization.class.getSimpleName() ) ) {
                        focusEntity = queryService.find( Organization.class, id );
                    } else if ( focusClass.equals( Actor.class.getSimpleName() ) ) {
                        focusEntity = queryService.find( Actor.class, id );
                    }
                } catch ( NotFoundException e ) {
                    LOG.error( "Failed to find focus entity " + focusClass + ":" + id );
                }
            } catch ( Exception ignored ) {
                LOG.error( "Invalid focus entity specified in parameters.", ignored );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected Diagram makeDiagram( double[] size, String orientation ) throws DiagramException {
        return getDiagramFactory().newProcedureMapDiagram(
                segment,
                summarizeByOrgType,
                summarizeByOrg,
                summarizeByRole,
                focusEntity,
                size,
                orientation );
    }

}
