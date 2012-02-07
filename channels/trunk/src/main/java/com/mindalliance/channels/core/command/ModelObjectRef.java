package com.mindalliance.channels.core.command;

import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.query.QueryService;
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
    
    public static final String SEPARATOR = "::";

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
    
    public ModelObjectRef( ) {

    }

    public ModelObjectRef( Identifiable identifiable ) {
        assert identifiable != null;
        if ( identifiable instanceof ModelEntity ) {
            entityName = identifiable.getName();
            ModelEntity.Kind kind = ( (ModelEntity) identifiable ).getKind();
            // TODO - hack: remove this patch once all Phases are correctly initialized
            if ( kind == null ) kind = ModelEntity.defaultKindFor( (Class<? extends ModelEntity>)identifiable.getClass() );
            entityKind = kind.name();
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

    public void setId( long id ) {
        this.id = id;
    }

    public void setClassName( String className ) {
        this.className = className;
    }

    public void setEntityName( String entityName ) {
        this.entityName = entityName;
    }

    public void setEntityKind( String entityKind ) {
        this.entityKind = entityKind;
    }

    public boolean isSerializable() {
        return identifiable == null;
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
                LOG.debug( className + " not found at " + id);
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
        try {
            return clazz.isAssignableFrom( getIdentifiableClass() );
        } catch ( NotFoundException e ) {
            return false;
        }
    }
    
    public String asString() {
        StringBuilder sb = new StringBuilder(  );
        if ( identifiable == null )  {
            sb.append( getClassName() );
            sb.append( SEPARATOR );
            sb.append( getId() );
            if ( entityKind != null ) {
                sb.append( SEPARATOR );
                sb.append( entityKind );
                sb.append( SEPARATOR );
                sb.append( entityName );
            }
        }
        return sb.toString();
    }

    /**
     * Builds a modelObjectRef from string.
     * Can be an empty one if it was built from an Identifiable which was not a ModelObject.
     * @param s
     * @return
     */
    public static ModelObjectRef fromString( String s ) {
        String[] items = s.split( SEPARATOR );
        ModelObjectRef moref = new ModelObjectRef(  );
        moref.setClassName( items[0] );
        moref.setId( Long.parseLong( items[1] ) );
        if ( items.length > 2 ) {
            moref.setEntityKind( items[2] );
            moref.setEntityName( items[3] );
        }
        return moref;
    }


}