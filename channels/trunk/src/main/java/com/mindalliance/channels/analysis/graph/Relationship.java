package com.mindalliance.channels.analysis.graph;

import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A basic relationship between identifiable objects.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 26, 2009
 * Time: 8:30:10 PM
 */
public class Relationship<T extends Identifiable> implements Identifiable {

    /**
     * Class logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( Relationship.class );
    /**
     * Entity sending information.
     */
    private Long fromIdentifiable;
    /**
     * Entity receiving information.
     */
    private Long toIdentifiable;

    private static final long SEED = 100000000 - ( 2 * PlanManager.IMMUTABLE_RANGE );

    public Relationship() {
    }

    public Relationship( T fromIdentifiable, T toIdentifiable ) {
        this.fromIdentifiable = fromIdentifiable.getId();
        this.toIdentifiable = toIdentifiable.getId();
    }

    /**
     * {@inheritDoc }
     */
    public String getTypeName() {
        return getClass().getSimpleName().toLowerCase();
    }

    /**
     * Long value of(<fromIdentifiable id as string>
     * concatenated to  <toIdentifiable id as string of length 9, left padded with 0>.
     *
     * @return a long
     */
    public long getId() {
        String toId = Long.toString( SEED - toIdentifiable );
        toId = StringUtils.leftPad( toId, 9, '0' );
        String fromId = Long.toString( SEED - fromIdentifiable );
        return Long.valueOf( fromId + toId );
    }

    public void setId( long id, QueryService queryService ) {
        String s = Long.toString( id );
        String toId = s.substring( s.length() - 9 );
        String fromId = s.substring( 0, s.length() - 9 );
        fromIdentifiable = (Long.valueOf( fromId ) * -1) + SEED;
        toIdentifiable = (Long.valueOf( toId ) * -1) + SEED;
    }

    public String getName() {
        return "From " + fromIdentifiable + " to " + toIdentifiable;
    }

    public String getDescription() {
        return "";
    }

    public Long getFromIdentifiable() {
        return fromIdentifiable;
    }

    public Long getToIdentifiable() {
        return toIdentifiable;
    }

    /**
     * Get from-entity.
     *
     * @param queryService a query service
     * @return an entity
     */
    @SuppressWarnings( "unchecked" )
    public T getFromIdentifiable( QueryService queryService ) {
        try {
            // TODO - Should be find( Identifiable.class,...)
            return (T) queryService.find( ModelObject.class, fromIdentifiable );
        } catch ( NotFoundException e ) {
            LOG.warn( "From-identifiable not found", e );
            return null;
        }
    }

    /**
     * Get to-entity.
     *
     * @param queryService a query service
     * @return an entity
     */
    @SuppressWarnings( "unchecked" )
    public T getToIdentifiable( QueryService queryService ) {
        try {
            // TODO - Should be find( Identifiable.class,...)
            return (T) queryService.find( ModelObject.class, toIdentifiable );
        } catch ( NotFoundException e ) {
            LOG.warn( "To-identifiable not found", e );
            return null;
        }
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
                || obj instanceof Relationship
                && getId() == ( (Relationship) obj ).getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Long.valueOf( getId() ).hashCode();
    }

}
