package com.mindalliance.channels.engine.analysis.graph;

import com.mindalliance.channels.engine.analysis.Analyst;
import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.engine.query.QueryService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A link from a plan segment to another composed of aggregated external flows.
 * The external flows are defined in the "from" plan segment and reference connectors in the "to" segment.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 31, 2009
 * Time: 7:11:08 PM
 */
public class SegmentRelationship implements Identifiable {
    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( SegmentRelationship.class );

    /**
     * Plan segment where external links are defined.
     */
    private Long fromSegmentId;
    /**
     * Plan segment where referenced connectors are defined.
     */
    private Long toSegmentId;

    /**
     * External flows in fromSegment referencing node in toSegment
     */
    private List<ExternalFlow> externalFlows = new ArrayList<ExternalFlow>();
    /**
     * Parts in fromSegment that initiate the to-segment
     */
    private List<Part> initiators = new ArrayList<Part>();

    /**
     * Parts in from-segment that terminate the to-segment
     */
    private List<Part> terminators = new ArrayList<Part>();

    public SegmentRelationship() {
    }

    public SegmentRelationship( Segment fromSegment, Segment toSegment ) {
        fromSegmentId = fromSegment.getId();
        toSegmentId = toSegment.getId();
    }

    /**
     * {@inheritDoc }
     */
    public String getTypeName() {
        return "segment relationship";
    }

    @Override
    public boolean isModifiableInProduction() {
        return false;
    }

    /**
     * Long value of(<fromSegment id as string>
     * concatenated to  <toSegment id as string of lenght 9, left padded with 0>.
     *
     * @return a long
     */
    public long getId() {
        String toId = Long.toString( toSegmentId );
        toId = StringUtils.leftPad( toId, 9, '0' );
        String fromId = Long.toString( fromSegmentId );
        return Long.valueOf( fromId + toId );
    }

    public void setId( long id, QueryService queryService, Analyst analyst ) {
        String s = Long.toString( id );
        String toId = s.substring( s.length() - 9 );
        String fromId = s.substring( 0, s.length() - 9 );
        fromSegmentId = Long.valueOf( fromId );
        toSegmentId = Long.valueOf( toId );
        SegmentRelationship scRel = analyst.findSegmentRelationship(
                                        getFromSegment( queryService ),
                                        getToSegment( queryService ) );
        if ( scRel != null ) {
            externalFlows = scRel.getExternalFlows();
            initiators = scRel.getInitiators();
        }
    }

    public String getName() {
        return "From " + fromSegmentId + " to " + toSegmentId;
    }

    public String getDescription() {
        return "";
    }

    public Long getFromSegmentId() {
        return fromSegmentId;
    }

    public Long getToSegmentId() {
        return toSegmentId;
    }

    public List<ExternalFlow> getExternalFlows() {
        return externalFlows;
    }

    public void setExternalFlows( List<ExternalFlow> externalFlows ) {
        this.externalFlows = externalFlows;
    }

    public List<Part> getInitiators() {
        return initiators;
    }

    public void setInitiators( List<Part> initiators ) {
        this.initiators = initiators;
    }

    public List<Part> getTerminators() {
        return terminators;
    }

    public void setTerminators( List<Part> terminators ) {
        this.terminators = terminators;
    }

    /**
     * Get from-segment.
     *
     * @param queryService a query service
     * @return a plan segment
     */
    public Segment getFromSegment( QueryService queryService ) {
        try {
            return queryService.find( Segment.class, fromSegmentId );
        } catch ( NotFoundException e ) {
            LOG.warn( "From-segment not found", e );
            return null;
        }
    }

    /**
     * Get to-segment.
     *
     * @param queryService a query service
     * @return a plan segment
     */
    public Segment getToSegment( QueryService queryService ) {
        try {
            return queryService.find( Segment.class, toSegmentId );
        } catch ( NotFoundException e ) {
            LOG.warn( "To-segment not found", e );
            return null;
        }
    }

    /**
     * Does any of the external flows have issues?
     *
     * @param analyst an analyst
     * @return a boolean
     */
    public boolean hasIssues( Analyst analyst ) {
        boolean hasIssues = false;
        Iterator<ExternalFlow> iterator = externalFlows.iterator();
        while ( !hasIssues && iterator.hasNext() ) {
            hasIssues = analyst.hasUnwaivedIssues( iterator.next(), Analyst.INCLUDE_PROPERTY_SPECIFIC );
        }
        return hasIssues;
    }

    /**
     * Tell the number of issues on all external flows.
     *
     * @param analyst an analyst
     * @return a string
     */
    public String getIssuesSummary( Analyst analyst ) {
        int count = 0;
        for ( ExternalFlow externalFlow : externalFlows ) {
            count += analyst.listUnwaivedIssues( externalFlow, Analyst.INCLUDE_PROPERTY_SPECIFIC ).size();
        }
        return count + ( count > 1 ? " issues" : " issue" );
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object obj ) {
        return this == obj
                || obj instanceof SegmentRelationship
                && getId() == ( (SegmentRelationship) obj ).getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Long.valueOf( getId() ).hashCode();
    }

    /**
     * Whether has external flows.
     * @return a boolean
     */
    public boolean hasExternalFlows() {
        return !externalFlows.isEmpty();
    }

    /**
     * Whether has external initiators.
     * @return a boolean
     */
    public boolean hasInitiators() {
        return !initiators.isEmpty();
    }

    /**
     * Whether has external initiators.
     * @return a boolean
     */
    public boolean hasTerminators() {
        return !terminators.isEmpty();
    }

     /**
     * Clear out initiators.
     */
    public void clearInitiators() {
        initiators = new ArrayList<Part>();
    }

    /**
    * Clear out initiators.
    */
   public void clearTerminators() {
       terminators = new ArrayList<Part>();
   }

    /**
     * Clear out external flows.
     */
    public void clearExternalFlows() {
        externalFlows = new ArrayList<ExternalFlow>();
    }

}
