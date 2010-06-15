package com.mindalliance.channels.command;

import com.mindalliance.channels.dao.NotFoundException;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.query.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * A reference to an Identifiable.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 8, 2009
 * Time: 7:34:16 PM
 */
// TODO -  move to util
public class ModelObjectRef implements Serializable {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( ModelObjectRef.class );

    /**
     * A model object's id.
     */
    private long id;
    /**
     * The model object's class name
     */
    private String className;
    /**
     * The entity's name, if an entity is referenced.
     */
    private String entityName;
    /**
     * Kind of entity
     */
    private String entityKind;
    /* *
      * The identifiable itself if not a model object.
     */
    private Identifiable identifiable;

    public ModelObjectRef( Identifiable identifiable ) {
        if ( identifiable instanceof ModelEntity ) {
            entityName = identifiable.getName();
            entityKind = ( (ModelEntity) identifiable ).getKind().name();
        }
        id = identifiable.getId();
        className = identifiable.getClass().getName();
        // Store identifiable if not a model object.
        // TODO - hack: a referenced identifiable should always be, ugh, referenced to be later resolved from id if still exists.
        if ( !( identifiable instanceof ModelObject ) ) {
            this.identifiable = identifiable;
        }
    }

    public long getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    /**
     * Get identifiable class given class name.
     *
     * @return a class
     * @throws NotFoundException if class not found
     */
    @SuppressWarnings( "unchecked" )
    public Class<? extends Identifiable> getIdentifiableClass() throws NotFoundException {
        try {
            return (Class<? extends Identifiable>) Class.forName( className );
        } catch ( ClassNotFoundException e ) {
            throw new NotFoundException();
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return className + ":" + id;
    }

    /**
     * Resolve the reference to a model object, fails otherwise.
     *
     * @param queryService a query service
     * @return a model object
     * @throws NotFoundException if not found
     */
    @SuppressWarnings( "unchecked" )
    public Identifiable resolve( QueryService queryService ) {
        if ( identifiable != null ) {
            return identifiable;
        } else {
            try {
                Identifiable mo;
                Class<? extends Identifiable> clazz = getIdentifiableClass();
                assert ModelObject.class.isAssignableFrom( clazz );
                if ( entityName == null ) {
                    mo = queryService.find( (Class<? extends ModelObject>) clazz, id );
                } else {
                    assert ModelEntity.class.isAssignableFrom( clazz );
                    if ( entityKind.equals( ModelEntity.Kind.Actual.name() ) ) {
                        mo = queryService.findOrCreate(
                                (Class<ModelEntity>) getIdentifiableClass(),
                                entityName,
                                id );
                    } else {
                        mo = queryService.findOrCreateType(
                                (Class<ModelEntity>) getIdentifiableClass(),
                                entityName,
                                id );
                    }
                }
                return mo;
            } catch ( NotFoundException e ) {
                LOG.warn( className + " not found at " + id);
                return null;
            }
        }
    }

    /**
     * Whether change is of an instance of a given class.
     *
     * @param clazz a class extending Identifiable
     * @return a boolean
     */
    public boolean isForInstanceOf
            ( Class<? extends Identifiable> clazz ) {
        return clazz.isAssignableFrom( getIdentifiableClass() );
    }


}