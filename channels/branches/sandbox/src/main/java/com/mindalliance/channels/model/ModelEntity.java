package com.mindalliance.channels.model;

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
public abstract class ModelEntity extends ModelObject {

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
     * Universal participation type.
     */
    private static final Participation ANY_PARTICIPATION_TYPE;
    /**
     * All universal types.
     */
    private static final List<ModelEntity> UNIVERSAL_TYPES;
    /**
     * Type set.
     */
    private List<ModelEntity> tags = new ArrayList<ModelEntity>();

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
        ANY_PARTICIPATION_TYPE = new Participation( "any participation" );
        ANY_PARTICIPATION_TYPE.setId( 10000000L - 17 );
        ANY_PARTICIPATION_TYPE.setType();
        UNIVERSAL_TYPES.add( ANY_PARTICIPATION_TYPE );
    }

    protected ModelEntity() {
    }

    protected ModelEntity( String name ) {
        super( name );
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
        );
    }

    /**
     * Find the narrowest of two entities, if applicable.
     *
     * @param entity      an entity
     * @param otherEntity an entity
     * @param locale the default location
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
        if ( Event.class.isAssignableFrom( entityClass )
                || Role.class.isAssignableFrom( entityClass ) ) {
            return Kind.Type;
        } else {
            return Kind.Actual;
        }
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
        return super.isUndefined() && tags.isEmpty();
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

    public List<ModelEntity> getTags() {
        return tags;
    }

    public void setTags( List<ModelEntity> tags ) {
        this.tags = tags;
    }

    public void addTag( ModelEntity tag ) {
        assert tag.appliesTo( this );
        if ( !tags.contains( tag ) ) tags.add( tag );
    }

    private boolean appliesTo( ModelEntity modelEntity ) {
        return modelEntity.getDomain().equals( getDomain() );
    }

    /**
     * Whether the entity has tags.
     *
     * @return a boolean
     */
    public boolean hasTags() {
        return !getTags().isEmpty();
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
                || equals( ANY_PARTICIPATION_TYPE );
    }

    @SuppressWarnings( "unchecked" )
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
        } else if ( entityClass == Participation.class ) {
            return (T) ANY_PARTICIPATION_TYPE;
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
     * Is this tagged by the entity? (transitive, ignores circularities)
     *
     * @param entity an entity
     * @return a boolean
     */
    public boolean hasTag( ModelEntity entity ) {
        return entity.isType() && hasTagSafe( entity, new HashSet<ModelEntity>() );
    }

    private boolean hasTagSafe( ModelEntity entity, Set<ModelEntity> visited ) {
        if ( tags.contains( entity ) || getImplicitTags().contains( entity ) )
            return true;

        visited.add( this );

        for ( ModelEntity tag : tags )
            if ( !visited.contains( tag ) && tag.hasTagSafe( entity, visited ) )
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
                              : equals( entity ) || isType() && entity.hasTag( this );
    }

    /**
     *   Get the list of implicit tags.
     * @return  a list of model entities
     */
    public List<ModelEntity> getImplicitTags() {
        return new ArrayList<ModelEntity>();
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

    /**
     * Whether an entity is the same as the other,
     * or has all the tags (transitively) of the other, type entity.
     *
     * @param other a model entity
     * @param locale the default location
     * @return a boolean
     */
    public boolean narrowsOrEquals( ModelEntity other, Place locale ) {

        // UNKNOWN does not narrow or equals UNKNOWN
        if ( other == null || isUnknown() || other.isUnknown() )
            return false;

        // same entity
        if ( equals( other ) )
            return true;

        // Can't compare rotten apples with rotten oranges
        return getClass().isAssignableFrom( other.getClass() )
            && !isInvalid( locale )

            && hasTag( other );
    }

    /**
     * Whether the model object is consistent in its definition.
     * @param locale default location
     * @return a boolean
     */
    public boolean isInvalid( Place locale ) {
        for ( ModelEntity tag : getAllTags() )
            if ( !tag.validates( this, locale ) )
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
     * Transitively find all tags, avoiding circularities.
     *
     * @return a list of model entities
     */
    public List<ModelEntity> getAllTags() {
        return safeAllTags( new HashSet<ModelEntity>() );
    }

    /**
     * Transitively find all implicit tags, avoiding circularities.
     *
     * @return a list of model entities
     */
    public List<ModelEntity> getAllImplicitTags() {
        return safeAllImplicitTags( new HashSet<ModelEntity>() );
    }

    private List<ModelEntity> safeAllTags( Set<ModelEntity> visited ) {
        List<ModelEntity> allTags = new ArrayList<ModelEntity>();
        if ( !visited.contains( this ) ) {
            allTags.addAll( getTags() );
            allTags.addAll( getImplicitTags() );
            visited.add( this );
            for ( ModelEntity tag : getTags() ) {
                allTags.addAll( tag.safeAllTags( visited ) );
            }
        }
        return allTags;
    }

    private List<ModelEntity> safeAllImplicitTags( Set<ModelEntity> visited ) {
        List<ModelEntity> allImplicitTags = new ArrayList<ModelEntity>();
        if ( !visited.contains( this ) ) {
            allImplicitTags.addAll( getImplicitTags() );
            visited.add( this );
            for ( ModelEntity tag : getTags() ) {
                allImplicitTags.addAll( tag.safeAllImplicitTags( visited ) );
            }
        }
        return allImplicitTags;
    }

    /**
     * Shortest inheritance path to a tag as string (excludes this from the path).
     *
     * @param tag a model entity
     * @return a string
     */
    public String inheritancePathTo( ModelEntity tag ) {
        List<ModelEntity> inheritance = inheritanceTo( tag );
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
     * Shortest inheritance path to a tag.
     *
     * @param tag a model entity
     * @return a list of model entities
     */
    private List<ModelEntity> inheritanceTo( ModelEntity tag ) {
        return safeInheritanceTo( tag, new HashSet<ModelEntity>() );
    }

    private List<ModelEntity> safeInheritanceTo( ModelEntity tag, Set<ModelEntity> visited ) {
        List<ModelEntity> inheritance = new ArrayList<ModelEntity>();
        if ( !visited.contains( this ) ) {
            visited.add( this );
            if ( getTags().contains( tag ) ) {
                inheritance.add( this );
            } else {
                List<List<ModelEntity>> allPaths = new ArrayList<List<ModelEntity>>();
                for ( ModelEntity t : getTags() ) {
                    List<ModelEntity> inh = t.safeInheritanceTo( tag, visited );
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
        if ( tags != null && mo != null )
            for ( ModelEntity tag : tags )
                if ( tag.equals( mo ) )
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


}
