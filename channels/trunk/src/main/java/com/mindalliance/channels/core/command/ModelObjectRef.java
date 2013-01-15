package com.mindalliance.channels.core.command;

import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.SegmentObject;
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
// TODO: This class is one big hack. Clean it up.
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
     * Kind of model object.
     */
    private String typeName;
    /**
     * The entity's name, if an entity is referenced.
     */
    private String name;
    /**
     * Kind of entity
     */
    private String entityKind;
    /**
     * Segment name.
     */
    private String segmentName;
    /* *
      * The identifiable itself if not a model object.
     */
    private Identifiable identifiable;

    public ModelObjectRef() {

    }

    public ModelObjectRef( Identifiable identifiable ) {
        assert identifiable != null;
        id = identifiable.getId();
        className = identifiable.getClass().getName();
        typeName = identifiable.getTypeName();
        if ( identifiable instanceof ModelObject && !((ModelObject)identifiable).isTransient() ) {
            if ( ( (ModelObject) identifiable ).isUnknown() ) {
                this.identifiable = identifiable;
            } else {
                if ( identifiable instanceof ModelEntity ) {
                    name = identifiable.getName();
                    ModelEntity.Kind kind = ( (ModelEntity) identifiable ).getKind();
                    // TODO - hack: remove this patch once all Phases are correctly initialized
                    if ( kind == null )
                        kind = ModelEntity.defaultKindFor( (Class<? extends ModelEntity>) identifiable.getClass() );
                    entityKind = kind.name();
                    // Store identifiable if not a model object.
                    // TODO - hack: a referenced identifiable should always be, ugh, referenced to be later resolved from id if still exists.
                } else {
                    ModelObject mo = (ModelObject) identifiable;
                    name = mo instanceof Part ? ( (Part) mo ).getTask() : mo.getName();
                    if ( identifiable instanceof SegmentObject ) {
                        Segment segment = ( (SegmentObject) identifiable ).getSegment();
                        // Segment can be null if the identifiable was deleted (detached from segment).
                        segmentName = segment == null ? null : segment.getName();
                    }
                }
            }
        } else {
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

    public void setName( String name ) {
        this.name = name;
    }

    public String getEntityKind() {
        return entityKind == null ? "" : entityKind;
    }

    public String getName() {
        return name;
    }

    public void setEntityKind( String entityKind ) {
        this.entityKind = entityKind;
    }

    public String getTypeName() {
        return typeName == null ? className : typeName;
    }

    public void setTypeName( String typeName ) {
        this.typeName = typeName;
    }

    public String getSegmentName() {
        return segmentName == null ? "" : segmentName;
    }

    public void setSegmentName( String segmentName ) {
        this.segmentName = segmentName;
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
     * Resolve the reference to an in-memory identifiable, fails otherwise.
     *
     * @param queryService a query service
     * @return an identifiable
     * @throws NotFoundException if not found
     */
    @SuppressWarnings( "unchecked" )
    public Identifiable resolve( QueryService queryService ) {
        if ( identifiable != null ) {
            return identifiable;
        } else {
            try {
                Identifiable identifiable;
                Class<? extends Identifiable> clazz = getIdentifiableClass();
                assert ModelObject.class.isAssignableFrom( clazz );
                if ( entityKind == null || entityKind.isEmpty() ) {
                    identifiable = queryService.find( (Class<? extends ModelObject>) clazz, id );
                } else {
                    assert ModelEntity.class.isAssignableFrom( clazz );
                    if ( entityKind.equals( ModelEntity.Kind.Actual.name() ) ) {
                        identifiable = queryService.findOrCreate(
                                (Class<ModelEntity>) getIdentifiableClass(),
                                name,
                                id );
                    } else {
                        identifiable = queryService.findOrCreateType(
                                (Class<ModelEntity>) getIdentifiableClass(),
                                name,
                                id );
                    }
                }
                return identifiable;
            } catch ( NotFoundException e ) {
                LOG.debug( className + " not found at " + id );
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
        StringBuilder sb = new StringBuilder();
        if ( identifiable == null ) {
            sb.append( getClassName() );
            sb.append( SEPARATOR );
            sb.append( getId() );
            sb.append( SEPARATOR );
            sb.append( name );
            sb.append( SEPARATOR );
            sb.append( typeName );
            sb.append( SEPARATOR );
            sb.append( getEntityKind().isEmpty() ? "--" : entityKind );
            sb.append( SEPARATOR );
            String segmentName = getSegmentName();
            sb.append( getSegmentName().isEmpty() ? "--" : segmentName );
            sb.append( SEPARATOR );
        }
        return sb.toString();
    }

    /**
     * Builds a modelObjectRef from string.
     * Can be an empty one if it was built from an Identifiable which was not a ModelObject.
     *
     * @param s a string encoding a model object ref
     * @return a model object ref
     */
    public static ModelObjectRef fromString( String s ) {
        String[] items = s.split( SEPARATOR );
        ModelObjectRef moref = new ModelObjectRef();
        moref.setClassName( items[0] );
        moref.setId( Long.parseLong( items[1] ) );
        moref.setName( items[2] );
        moref.setTypeName( items[3] );
        moref.setEntityKind( items[4].equals( "--" ) ? null : items[4] );
        moref.setSegmentName( items[5].equals( "--" ) ? null : items[5] );
        return moref;
    }


    public static ModelObject resolveFromString( String moRefString, QueryService queryService ) {
        ModelObjectRef moRef = fromString( moRefString );
        if ( moRef != null ) {
            return (ModelObject) moRef.resolve( queryService );
        } else {
            return null;
        }
    }
}