package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ModelObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Flows between entities of the same kind.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 6, 2009
 * Time: 5:03:25 PM
 */
public class EntityRelationship<T extends ModelObject> implements Identifiable {
       /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( EntityRelationship.class );

    /**
     * Entity sending information.
     */
    private Long fromEntityId;
    /**
     * Entity receiving information.
     */
    private Long toEntityId;

    /**
     * External flows in fromScenario referencing node in toScenario
     */
    private List<Flow> flows = new ArrayList<Flow>();

    public EntityRelationship() {
    }

    public EntityRelationship( T fromEntity, T toEntity ) {
        fromEntityId = fromEntity.getId();
        toEntityId = toEntity.getId();
    }

    /**
     * Long value of(<fromEntity id as string>
     * concatenated to  <toEntity id as string of lenght 9, left padded with 0>.
     *
     * @return a long
     */
    public long getId() {
        String toId = Long.toString( toEntityId );
        toId = StringUtils.leftPad( toId, 9, '0' );
        String fromId = Long.toString( fromEntityId );
        return Long.valueOf( fromId + toId );
    }

    public void setId( long id, QueryService queryService ) {
        String s = Long.toString( id );
        String toId = s.substring( s.length() - 9 );
        String fromId = s.substring( 0, s.length() - 9 );
        fromEntityId = Long.valueOf( fromId );
        toEntityId = Long.valueOf( toId );
        EntityRelationship entityRel = queryService.findEntityRelationship(
                getFromEntity( queryService ),
                getToEntity( queryService ) );
        if ( entityRel != null ) flows = entityRel.getFlows();
    }

    public String getName() {
        return "From " + fromEntityId + " to " + toEntityId;
    }

    public String getDescription() {
        return "";
    }

    public Long getFromEntityId() {
        return fromEntityId;
    }

    public Long getToEntityId() {
        return toEntityId;
    }

    public List<Flow> getFlows() {
        return flows;
    }

    public void setFlows( List<Flow> flows ) {
        this.flows = flows;
    }

    /**
     * Get from-entity.
     *
     * @param queryService a query service
     * @return an entity
     */
    public T getFromEntity( QueryService queryService ) {
        try {
            return (T)queryService.find( ModelObject.class, fromEntityId );
        } catch ( NotFoundException e ) {
            LOG.warn( "From-entity not found", e );
            return null;
        }
    }

    /**
     * Get to-entity.
     *
     * @param queryService a query service
     * @return an entity
     */
    public T getToEntity( QueryService queryService ) {
        try {
            return (T)queryService.find( ModelObject.class, toEntityId );
        } catch ( NotFoundException e ) {
            LOG.warn( "To-entity not found", e );
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
        Iterator<Flow> iterator = flows.iterator();
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
        for ( Flow flow : flows ) {
            count += analyst.listUnwaivedIssues( flow, Analyst.INCLUDE_PROPERTY_SPECIFIC ).size();
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
                || obj instanceof EntityRelationship
                && getId() == ( (EntityRelationship) obj ).getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Long.valueOf( getId() ).hashCode();
    }
}
