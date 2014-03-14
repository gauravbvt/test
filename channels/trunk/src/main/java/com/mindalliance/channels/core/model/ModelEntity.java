package com.mindalliance.channels.core.model;

import com.mindalliance.channels.core.model.asset.MaterialAsset;
import com.mindalliance.channels.core.query.Assignments;
import com.mindalliance.channels.core.query.Commitments;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.core.util.ChannelsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An entity model object.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 7, 2009
 * Time: 1:05:30 PM
 */
public abstract class ModelEntity extends ModelObject implements Hierarchical {

    public static final int MAX_NAME_SIZE = 100;
    public static final String NEW_NAME = "UNNAMED";


    /**
     * Actual or Type.
     */
    public enum Kind {
        Type,
        Actual
    }

    /**
     * Universal actor type.
     */
    private static final Actor ANY_ACTOR_TYPE;
    /**
     * Universal event type.
     */
    private static final Event ANY_EVENT_TYPE;
    /**
     * Universal organization type.
     */
    private static final Organization ANY_ORGANIZATION_TYPE;
    /**
     * Universal place type.
     */
    private static final Place ANY_PLACE_TYPE;
    /**
     * Universal role type.
     */
    private static final Role ANY_ROLE_TYPE;
    /**
     * Universal phase type.
     */
    private static final Phase ANY_PHASE_TYPE;
    /**
     * Universal medium type.
     */
    private static final TransmissionMedium ANY_MEDIUM_TYPE;
    /**
     * Universal info product type.
     */
    private static final InfoProduct ANY_INFO_PRODUCT_TYPE;
    /**
     * Universal info format type.
     */
    private static final InfoFormat ANY_INFO_FORMAT_TYPE;
    /**
     * Universal function.
     */
    private static final Function ANY_FUNCTION_TYPE;
    /**
     * Universal asset.
     */
    private static final MaterialAsset ANY_ASSET_TYPE;
    /**
     * All universal types.
     */
    private static final List<ModelEntity> UNIVERSAL_TYPES;
    /**
     * Type set.
     */
    private List<ModelEntity> types = new ArrayList<ModelEntity>();

    /**
     * Whether the entity is immutable.
     */
    private boolean immutable;
    /**
     * Whether the entity is actual, a type or TBD (null).
     */
    private Kind kind;

    static {
        UNIVERSAL_TYPES = new ArrayList<ModelEntity>();

        ANY_ACTOR_TYPE = new Actor( "any agent" );
        ANY_ACTOR_TYPE.setId( 10000000L - 10 );
        ANY_ACTOR_TYPE.setType();
        UNIVERSAL_TYPES.add( ANY_ACTOR_TYPE );

        ANY_EVENT_TYPE = new Event( "any event" );
        ANY_EVENT_TYPE.setId( 10000000L - 11 );
        ANY_EVENT_TYPE.setType();
        UNIVERSAL_TYPES.add( ANY_EVENT_TYPE );

        ANY_ORGANIZATION_TYPE = new Organization( "any organization" );
        ANY_ORGANIZATION_TYPE.setId( 10000000L - 12 );
        ANY_ORGANIZATION_TYPE.setType();
        UNIVERSAL_TYPES.add( ANY_ORGANIZATION_TYPE );

        ANY_PLACE_TYPE = new Place( "any place" );
        ANY_PLACE_TYPE.setId( 10000000L - 13 );
        ANY_PLACE_TYPE.setType();
        UNIVERSAL_TYPES.add( ANY_PLACE_TYPE );

        ANY_ROLE_TYPE = new Role( "any role" );
        ANY_ROLE_TYPE.setId( 10000000L - 14 );
        ANY_ROLE_TYPE.setType();
        UNIVERSAL_TYPES.add( ANY_ROLE_TYPE );

        ANY_PHASE_TYPE = new Phase( "any phase" );
        ANY_PHASE_TYPE.setId( 10000000L - 15 );
        ANY_PHASE_TYPE.setType();
        UNIVERSAL_TYPES.add( ANY_PHASE_TYPE );

        ANY_MEDIUM_TYPE = new TransmissionMedium( "any medium" );
        ANY_MEDIUM_TYPE.setId( 10000000L - 16 );
        ANY_MEDIUM_TYPE.setType();
        UNIVERSAL_TYPES.add( ANY_MEDIUM_TYPE );

        ANY_INFO_PRODUCT_TYPE = new InfoProduct( "any information product" );
        ANY_INFO_PRODUCT_TYPE.setId( 10000000L - 17 );
        ANY_INFO_PRODUCT_TYPE.setType();
        UNIVERSAL_TYPES.add( ANY_INFO_PRODUCT_TYPE );

        ANY_INFO_FORMAT_TYPE = new InfoFormat( "any format" );
        ANY_INFO_FORMAT_TYPE.setId( 10000000L - 18 );
        ANY_INFO_FORMAT_TYPE.setType();
        UNIVERSAL_TYPES.add( ANY_INFO_FORMAT_TYPE );

        ANY_FUNCTION_TYPE = new Function( "any function" );
        ANY_FUNCTION_TYPE.setId( 10000000L - 19 );
        ANY_FUNCTION_TYPE.setType();
        UNIVERSAL_TYPES.add( ANY_FUNCTION_TYPE );

        ANY_ASSET_TYPE = new MaterialAsset( "any asset" );
        ANY_ASSET_TYPE.setId( 10000000L - 20 );
        ANY_ASSET_TYPE.setType();
        UNIVERSAL_TYPES.add( ANY_ASSET_TYPE );

    }

    protected ModelEntity() {
    }

    protected ModelEntity( String name ) {
        super( name );
    }

    /**
     * Whether this entity is involved in any assignment or commitment.
     *
     * @param allAssignments all assignments
     * @param allCommitments all commitments
     * @return a boolean
     */
    public boolean isInvolvedIn( Assignments allAssignments, Commitments allCommitments ) {
        return false; // DEFAULT
    }

    public static String getPluralClassLabelOf( Class<? extends ModelEntity> entityClass ) {
        return entityClass == Actor.class
                ? Actor.classLabel()
                : entityClass == Event.class
                ? Event.classLabel()
                : entityClass == Organization.class
                ? Organization.classLabel()
                : entityClass == Place.class
                ? Place.classLabel()
                : entityClass == Role.class
                ? Role.classLabel()
                : entityClass == Phase.class
                ? Phase.classLabel()
                : entityClass == TransmissionMedium.class
                ? TransmissionMedium.classLabel()
                : entityClass == InfoProduct.class
                ? InfoProduct.classLabel()
                : entityClass == InfoFormat.class
                ? InfoFormat.classLabel()
                : entityClass == Function.class
                ? Function.classLabel()
                : entityClass == MaterialAsset.class
                ? MaterialAsset.classLabel()
                : "UNKNOWN";
    }

    public static String getSingularClassLabelOf( Class<? extends ModelEntity> entityClass ) {
        return entityClass == Actor.class
                ? "agent"
                : entityClass == Event.class
                ? new Event().getTypeName()
                : entityClass == Organization.class
                ? new Organization().getTypeName()
                : entityClass == Place.class
                ? new Place().getTypeName()
                : entityClass == Role.class
                ? new Role().getTypeName()
                : entityClass == Phase.class
                ? new Phase().getTypeName()
                : entityClass == TransmissionMedium.class
                ? new TransmissionMedium().getTypeName()
                : entityClass == InfoProduct.class
                ? new InfoProduct().getTypeName()
                : entityClass == InfoFormat.class
                ? new InfoFormat().getTypeName()
                : entityClass == Function.class
                ? new Function().getTypeName()
                : entityClass == MaterialAsset.class
                ? new MaterialAsset().getTypeName()

                : "UNKNOWN";
    }

    public static Class<? extends ModelEntity> classFromLabel( String val ) {
        String label = val.toLowerCase();
        return label.equals( Actor.classLabel().toLowerCase() )
                ? Actor.class
                : label.equals( Event.classLabel().toLowerCase() )
                ? Event.class
                : label.equals( Organization.classLabel().toLowerCase() )
                ? Organization.class
                : label.equals( Place.classLabel().toLowerCase() )
                ? Place.class
                : label.equals( Role.classLabel().toLowerCase() )
                ? Role.class
                : label.equals( Phase.classLabel().toLowerCase() )
                ? Phase.class
                : label.equals( TransmissionMedium.classLabel().toLowerCase() )
                ? TransmissionMedium.class
                : label.equals( InfoProduct.classLabel().toLowerCase() )
                ? InfoProduct.class
                : label.equals( InfoFormat.classLabel().toLowerCase() )
                ? InfoFormat.class
                : label.equals( Function.classLabel().toLowerCase() )
                ? Function.class
                : label.equals( MaterialAsset.classLabel().toLowerCase() )
                ? MaterialAsset.class
                : null;
    }


    public static List<String> classLabels() {
        List<String> classLabels = new ArrayList<String>();
        classLabels.add( Actor.classLabel() );
        classLabels.add( Event.classLabel() );
        classLabels.add( Organization.classLabel() );
        classLabels.add( Place.classLabel() );
        classLabels.add( Role.classLabel() );
        classLabels.add( Phase.classLabel() );
        classLabels.add( TransmissionMedium.classLabel() );
        classLabels.add( InfoProduct.classLabel() );
        classLabels.add( InfoFormat.classLabel() );
        classLabels.add( Function.classLabel() );
        classLabels.add( MaterialAsset.classLabel() );
        return classLabels;
    }

    public static String getNameHint( Class<? extends ModelEntity> clazz, Kind kind ) {
        try {
            StringBuilder sb = new StringBuilder();
            String label = clazz.newInstance().getKindLabel();
            sb.append( "The name of " );
            if ( canBeActualOrType( clazz ) ) {
                sb.append( kind == Kind.Actual ? "an actual " : "a type of " );
            } else {
                sb.append( ChannelsUtils.startsWithVowel( label ) ? "an " : "a " );
            }
            sb.append( label );
            return sb.toString();
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }


    @Override
    public boolean isSegmentObject() {
        return false;
    }

    /**
     * Whether instances of a given entity class can be either actuals or types.
     * Roles and events are always types.
     *
     * @param entityClass an entity class
     * @return a boolean
     */
    public static boolean canBeActualOrType( Class<? extends ModelEntity> entityClass ) {
        return !(
                Event.class.isAssignableFrom( entityClass )
                        || Role.class.isAssignableFrom( entityClass )
                        || TransmissionMedium.class.isAssignableFrom( entityClass )
                        || Function.class.isAssignableFrom( entityClass )
                        || InfoProduct.class.isAssignableFrom( entityClass )
                        || InfoFormat.class.isAssignableFrom( entityClass )
                // || Phase.class.isAssignableFrom( entityClass )
        );
    }

    /**
     * Find the narrowest of two entities, if applicable.
     *
     * @param entity      an entity
     * @param otherEntity an entity
     * @param locale      the default location
     * @return an entity or null
     */
    public static <T extends ModelEntity> T narrowest( T entity, T otherEntity, Place locale ) {
        if ( entity.narrowsOrEquals( otherEntity, locale ) ) return entity;
        if ( otherEntity.narrowsOrEquals( entity, locale ) ) return otherEntity;
        return null;
    }


    /**
     * Return the default kind when creating a model entity of a given class.
     *
     * @param entityClass an entity class
     * @return a kind
     */
    public static Kind defaultKindFor( Class<? extends ModelEntity> entityClass ) {
        /* if ( Event.class.isAssignableFrom( entityClass )
                || Role.class.isAssignableFrom( entityClass ) ) {
            return Kind.Type;
        } else {
            return Kind.Actual;
        }*/
        return Kind.Type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEntity() {
        return true;
    }

    @Override
    public boolean isImmutable() {
        return immutable;
    }

    /**
     * Make the entity immutable.
     */
    public void makeImmutable() {
        immutable = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUndefined() {
        return super.isUndefined() && types.isEmpty();
    }

    public Kind getKind() {
        return kind;
    }

    public boolean isActual() {
        return getKind() == Kind.Actual;
    }

    public boolean isType() {
        return getKind() == Kind.Type;
    }

    public void setType() {
        kind = Kind.Type;
    }

    public void setActual() {
        kind = Kind.Actual;
    }

    public void setKind( Kind kind ) {
        this.kind = kind;
    }

    public List<ModelEntity> getTypes() {
        return types;
    }

    public void setTypes( List<ModelEntity> types ) {
        this.types = types;
    }

    public void addType( ModelEntity type ) {
        assert type.appliesTo( this );
        if ( !types.contains( type ) ) types.add( type );
    }

    private boolean appliesTo( ModelEntity modelEntity ) {
        return modelEntity.getDomain().equals( getDomain() );
    }

    /**
     * Whether the entity has types.
     *
     * @return a boolean
     */
    public boolean hasTypes() {
        return !getTypes().isEmpty();
    }


    public String getDomain() {
        return getClass().getSimpleName();
    }

    /**
     * Whether this entity type is universal (built-in)
     *
     * @return a boolean
     */
    public boolean isUniversal() {
        return equals( ANY_ACTOR_TYPE )
                || equals( ANY_EVENT_TYPE )
                || equals( ANY_ORGANIZATION_TYPE )
                || equals( ANY_PLACE_TYPE )
                || equals( ANY_ROLE_TYPE )
                || equals( ANY_PHASE_TYPE )
                || equals( ANY_MEDIUM_TYPE )
                || equals( ANY_INFO_PRODUCT_TYPE )
                || equals( ANY_INFO_FORMAT_TYPE )
                || equals( ANY_FUNCTION_TYPE )
                || equals( ANY_ASSET_TYPE );
    }

    @SuppressWarnings("unchecked")
    public static <T extends ModelEntity> T getUniversalTypeFor( Class<T> entityClass ) {
        if ( entityClass == Actor.class ) {
            return (T) ANY_ACTOR_TYPE;
        } else if ( entityClass == Event.class ) {
            return (T) ANY_EVENT_TYPE;
        } else if ( entityClass == Organization.class ) {
            return (T) ANY_ORGANIZATION_TYPE;
        } else if ( entityClass == Place.class ) {
            return (T) ANY_PLACE_TYPE;
        } else if ( entityClass == Role.class ) {
            return (T) ANY_ROLE_TYPE;
        } else if ( entityClass == Phase.class ) {
            return (T) ANY_PHASE_TYPE;
        } else if ( entityClass == TransmissionMedium.class ) {
            return (T) ANY_MEDIUM_TYPE;
        } else if ( entityClass == InfoProduct.class ) {
            return (T) ANY_INFO_PRODUCT_TYPE;
        } else if ( entityClass == InfoFormat.class ) {
            return (T) ANY_INFO_FORMAT_TYPE;
        } else if ( entityClass == Function.class ) {
            return (T) ANY_FUNCTION_TYPE;
        } else if ( entityClass == MaterialAsset.class ) {
            return (T) ANY_ASSET_TYPE;
        } else {
            throw new RuntimeException( "No known universal type for " + entityClass.getSimpleName() );
        }
    }

    public static <T extends ModelEntity> T getUniversalType( String name, Class<T> clazz ) {
        T universalType = getUniversalTypeFor( clazz );
        if ( universalType.getName().equals( name ) )
            return universalType;
        else
            return null;
    }

    /**
     * Is this typed by the entity? (transitive, ignores circularities)
     *
     * @param entity an entity
     * @return a boolean
     */
    public boolean hasType( ModelEntity entity ) {
        return entity.isType() && hasTypeSafe( entity, new HashSet<ModelEntity>() );
    }

    private boolean hasTypeSafe( ModelEntity entity, Set<ModelEntity> visited ) {
        if ( types.contains( entity ) || getImplicitTypes().contains( entity ) )
            return true;

        visited.add( this );

        for ( ModelEntity type : types )
            if ( !visited.contains( type ) && type.hasTypeSafe( entity, visited ) )
                return true;

        return false;
    }

    /**
     * Whether two entity values are equivalent, or the entity is defined in terms of this one.
     *
     * @param entity an entity or null
     * @param aClass type of the 'unknown' object
     * @return a boolean
     */
    public boolean isEquivalentToOrIsA( ModelEntity entity, Class<? extends ModelEntity> aClass ) {

        return entity == null ? isUnknown() && getClass().equals( aClass )
                : equals( entity ) || isType() && entity.hasType( this );
    }

    /**
     * Get the list of implicit types.
     *
     * @return a list of model entities
     */
    public List<ModelEntity> getImplicitTypes() {
        return safeImplicitTypes( new HashSet<ModelEntity>() );
    }

    /**
     * Entity equals or implies another (null means any)
     *
     * @param entity an entity
     * @param other  an entity
     * @param locale the default location
     * @return a boolean
     */
    public static boolean implies( ModelEntity entity, ModelEntity other, Place locale ) {
        return other == null
                || entity != null && entity.narrowsOrEquals( other, locale );
    }

    public boolean narrowsOrEquals( ModelEntity other ) {
        return narrowsOrEquals( other, Place.UNKNOWN );
    }

    /**
     * Whether an entity is the same as the other,
     * or has all the types (transitively) of the other, type entity.
     *
     * @param other  a model entity
     * @param locale the default location
     * @return a boolean
     */
    public boolean narrowsOrEquals( ModelEntity other, Place locale ) {

        // UNKNOWN does not narrow or equals UNKNOWN
        if ( other == null || other.isUnknown() )
            return false;

        // same entity
        if ( equals( other ) )
            return true;

        // Can't compare rotten apples with rotten oranges
        return getClass().isAssignableFrom( other.getClass() )
                && !isInvalid( locale )

                && hasType( other );
    }

    /**
     * Whether the model object is consistent in its definition.
     *
     * @param locale default location
     * @return a boolean
     */
    public boolean isInvalid( Place locale ) {
        for ( ModelEntity type : getAllTypes() )
            if ( !type.validates( this, locale ) )
                return true;

        return false;
    }

    /**
     * Apply type tests to an actual entity.
     *
     * @param entity an entity type
     * @param locale the default location
     * @return a boolean
     */
    public boolean validates( ModelEntity entity, Place locale ) {
        return isType()
                && entity != null && !entity.isUnknown()
                && getClass().isAssignableFrom( entity.getClass() );
    }

    /**
     * Transitively find all types, avoiding circularities.
     *
     * @return a list of model entities
     */
    public List<ModelEntity> getAllTypes() {
        return safeAllTypes( new HashSet<ModelEntity>() );
    }

    /**
     * Transitively find all implicit types, avoiding circularities.
     *
     * @return a list of model entities
     */
    public List<ModelEntity> getAllImplicitTypes() {
        return safeAllImplicitTypes( new HashSet<ModelEntity>() );
    }

    // Default
    protected List<ModelEntity> safeImplicitTypes( Set<ModelEntity> visited ) {
        return new ArrayList<ModelEntity>();
    }

    protected List<ModelEntity> safeAllTypes( Set<ModelEntity> visited ) {
        Set<ModelEntity> allTypes = new HashSet<ModelEntity>();
        if ( !visited.contains( this ) ) {
            allTypes.addAll( getTypes() );
            allTypes.addAll( safeAllImplicitTypes( new HashSet<ModelEntity>() ) );
            visited.add( this );
            for ( ModelEntity type : getTypes() ) {
                allTypes.addAll( type.safeAllTypes( visited ) );
            }
        }
        return new ArrayList<ModelEntity>( allTypes );
    }

    private List<ModelEntity> safeAllImplicitTypes( Set<ModelEntity> visited ) {
        List<ModelEntity> allImplicitTypes = new ArrayList<ModelEntity>();
        if ( !visited.contains( this ) ) {
            allImplicitTypes.addAll( safeImplicitTypes( visited ) );
            visited.add( this );
            for ( ModelEntity type : getTypes() ) {
                allImplicitTypes.addAll( type.safeAllImplicitTypes( visited ) );
            }
        }
        return allImplicitTypes;
    }

    /**
     * Shortest inheritance path to a type as string (excludes this from the path).
     *
     * @param type a model entity
     * @return a string
     */
    public String inheritancePathTo( ModelEntity type ) {
        List<ModelEntity> inheritance = inheritanceTo( type );
        StringBuilder sb = new StringBuilder();
        if ( !inheritance.isEmpty() ) {
            inheritance.remove( inheritance.size() - 1 );
            for ( ModelEntity t : inheritance ) {
                sb.append( "from " );
                sb.append( '\"' );
                sb.append( t.getName() );
                sb.append( "\" " );
            }
        }
        return sb.toString();
    }

    /**
     * Shortest inheritance path to a type.
     *
     * @param type a model entity
     * @return a list of model entities
     */
    private List<ModelEntity> inheritanceTo( ModelEntity type ) {
        return safeInheritanceTo( type, new HashSet<ModelEntity>() );
    }

    private List<ModelEntity> safeInheritanceTo( ModelEntity type, Set<ModelEntity> visited ) {
        List<ModelEntity> inheritance = new ArrayList<ModelEntity>();
        if ( !visited.contains( this ) ) {
            visited.add( this );
            if ( getTypes().contains( type ) ) {
                inheritance.add( this );
            } else {
                List<List<ModelEntity>> allPaths = new ArrayList<List<ModelEntity>>();
                for ( ModelEntity t : getTypes() ) {
                    List<ModelEntity> inh = t.safeInheritanceTo( type, visited );
                    if ( !inh.isEmpty() ) {
                        inh.add( this );
                        allPaths.add( inh );
                    }
                }
                Collections.sort( allPaths, new Comparator<List<ModelEntity>>() {
                    public int compare( List<ModelEntity> o1, List<ModelEntity> o2 ) {
                        int size1 = o1.size();
                        int size2 = o2.size();
                        return size1 < size2 ? -1 : size1 > size2 ? 1 : 0;
                    }
                } );
                if ( !allPaths.isEmpty() ) inheritance.addAll( allPaths.get( 0 ) );
            }
        }
        return inheritance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean references( ModelObject mo ) {
        if ( types != null && mo != null )
            for ( ModelEntity type : types )
                if ( type.equals( mo ) )
                    return true;

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKindLabel() {
        return getTypeName();
    }

    // Hierarchical


    @Override
    public List<? extends Hierarchical> getSuperiors( QueryService queryService ) {
        List<ModelEntity> superiors = new ArrayList<ModelEntity>();
        if ( isType() ) {
            superiors.addAll( getTypes() );
            if ( !isUniversal() && superiors.isEmpty() ) {
                superiors.add( ModelEntity.getUniversalTypeFor( getClass() ) );
            }
        }
        return superiors;
    }

    public static boolean canBeActual( ModelObject mo ) {
        return !( mo instanceof Role
                || mo instanceof Event
                || mo instanceof TransmissionMedium
                || mo instanceof Phase );
    }

}
