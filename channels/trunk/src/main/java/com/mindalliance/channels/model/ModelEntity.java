package com.mindalliance.channels.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import javax.persistence.Transient;
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
    public static Actor ANY_ACTOR_TYPE;
    /**
     * Universal event type.
     */
    public static Event ANY_EVENT_TYPE;
    /**
     * Universal organization type.
     */
    public static Organization ANY_ORGANIZATION_TYPE;
    /**
     * Universal place type.
     */
    public static Place ANY_PLACE_TYPE;
    /**
     * Universal role type.
     */
    public static Role ANY_ROLE_TYPE;
    /**
     * Universal phase type.
     */
    public static Phase ANY_PHASE_TYPE;
    /**
     * Universal medium type.
     */
    public static TransmissionMedium ANY_MEDIUM_TYPE;
    /**
     * All universal types.
     */
    public static List<ModelEntity> UNIVERSAL_TYPES;
    /**
     * Type set.
     */
    private List<ModelEntity> tags = new ArrayList<ModelEntity>();

    /**
     * Whether the entity is immutable.
     */
    private boolean immutable = false;
    /**
     * Whether the entity is actual, a type or TBD (null).
     */
    private Kind kind;

    static {
        UNIVERSAL_TYPES = new ArrayList<ModelEntity>();
        ANY_ACTOR_TYPE = new Actor( "any individual" );
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
    }

    public ModelEntity() {
    }

    public ModelEntity( String name ) {
        super( name );
    }

    /**
     * Whether two entities (either can be null) are compatible.
     *
     * @param entity an entity
     * @param other  an entity
     * @return a boolean
     */
    public static boolean compatible( ModelEntity entity, ModelEntity other ) {
        if ( entity == null || other == null || entity.isUnknown() || other.isUnknown() ) return true;
        if ( entity.narrowsOrEquals( other ) ) return true;
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
        );
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
    public boolean isEntity() {
        return true;
    }

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
    @Transient
    public boolean isUndefined() {
        return super.isUndefined() && tags.isEmpty();
    }

    public Kind getKind() {
        return kind;
    }

    public boolean isActual() {
        return kind == Kind.Actual;
    }

    public boolean isType() {
        return kind == Kind.Type;
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
                || equals( ANY_MEDIUM_TYPE );
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
        if ( getTags().contains( entity ) ) return true;
        visited.add( this );
        for ( ModelEntity tag : getTags() ) {
            if ( !visited.contains( tag ) && tag.hasTagSafe( entity, visited ) ) return true;
        }
        return false;
    }

    /**
     * Entity equals or implies another (null means any)
     *
     * @param entity an entity
     * @param other  an entity
     * @return a boolean
     */
    public static boolean implies( ModelEntity entity, ModelEntity other ) {
        return other == null
                || entity != null && entity.narrowsOrEquals( other );
    }

    /**
     * Whether two entity values are equivalent, or the first one is defined in terms of the other.
     *
     * @param entity an entity or null
     * @param other  an entity
     * @return a boolean
     */
    public static boolean isEquivalentToOrIsA( ModelEntity entity, ModelEntity other ) {
        if ( other == null )
            return false;
        else if ( entity == null )
            return other.isUnknown();
        else
            return entity.equals( other ) || ( other.isType() && entity.hasTag( other ) );
    }


    /**
     * Whether two entity values are equivalent, or the first one is defined in terms of the other.
     *
     * @param entity an entity or null
     * @param other  an entity
     * @return a boolean
     */
    public static boolean isEquivalentToOrDefinedUsing( ModelEntity entity, ModelEntity other ) {
        if ( other == null )
            return false;
        else if ( entity == null )
            return other.isUnknown();
        else
            return entity.equals( other ) || entity.isDefinedUsing( other );
    }

    /**
     * Is defined in terms of another entity.
     *
     * @param entity an entity
     * @return a boolean
     */
    public boolean isDefinedUsing( ModelEntity entity ) {
        return entity.isType() && hasTag( entity );
    }

    /**
     * Whether an entity is the same as the other,
     * or has all the tags (transitively) of the other, type entity.
     *
     * @param other a model entity
     * @return a boolean
     */
    public boolean narrowsOrEquals( ModelEntity other ) {
        if ( other == null ) return false;
        if ( isUnknown() || other.isUnknown() ) return false;
        // Can't compare apples with oranges
        if ( !getClass().isAssignableFrom( other.getClass() ) ) return false;
        // same entity
        if ( equals( other ) ) return true;
        if ( overrideNarrows( other ) ) return true;
        // a type of entity can't narrow an actual entity
        // and an actual entity can't narrow a different actual entity
        if ( other.isActual() ) return false;
        // entity explicitly is classified as other entity type
        if ( hasTag( other ) ) return true;
        // entity (actual or type) must at least have all the tags of the other entity type
        boolean isSubCollection = CollectionUtils.isSubCollection(
                other.getTyping(),
                getAllTags() );
        if (!isSubCollection) return false;
        // meets specific and inherited requirement tests of entity type
        if (!meetsTypeRequirementTests( other ) ) return false;
        boolean meetsInheritedTests = CollectionUtils.selectRejected(
                        other.getAllTags(),
                        new Predicate() {
                            public boolean evaluate( Object obj ) {
                                return meetsTypeRequirementTests( (ModelEntity) obj );
                            }
                        }
                ).isEmpty();
        return meetsInheritedTests;
    }

    /**
     * Override test for narrowing.
     *
     * @param other a model entity
     * @return a boolean
     */
    protected boolean overrideNarrows( ModelEntity other ) {
        return false;
    }

    /**
     * Apply tests specific to the class of entity.
     *
     * @param entityType an entity type
     * @return a boolean
     */
    protected boolean meetsTypeRequirementTests( ModelEntity entityType ) {
        // Default
        return true;
    }

    /**
     * Is consistent with the definition of an entity type.
     *
     * @param entityType a model entity
     * @return a boolean
     */
    public boolean isConsistentWith( ModelEntity entityType ) {
        assert entityType.isType();
        return overrideNarrows( entityType ) || meetsTypeRequirementTests( entityType );
    }

    private List<ModelEntity> getTyping() {
        List<ModelEntity> typing = getAllTags();
        typing.add( this );
        return typing;
    }

    /**
     * Transitively find all tags, avoiding circularities.
     *
     * @return a list of model entities
     */
    public List<ModelEntity> getAllTags() {
        return safeAllTags( new HashSet<ModelEntity>() );
    }

    private List<ModelEntity> safeAllTags( Set<ModelEntity> visited ) {
        List<ModelEntity> allTags = new ArrayList<ModelEntity>();
        if ( !visited.contains( this ) ) {
            allTags.addAll( getTags() );
            visited.add( this );
            for ( ModelEntity tag : getTags() ) {
                allTags.addAll( tag.safeAllTags( visited ) );
            }
        }
        return allTags;
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
                sb.append( "\"" );
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
    public List<ModelEntity> inheritanceTo( ModelEntity tag ) {
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
                    public int compare( List<ModelEntity> path1, List<ModelEntity> path2 ) {
                        int size1 = path1.size();
                        int size2 = path2.size();
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
    public boolean references( final ModelObject mo ) {
        return CollectionUtils.exists(
                getTags(),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ModelObject.areIdentical( ( (ModelEntity) obj ), mo );
                    }
                }
        );
    }

    /**
     * Get a label for the kind of entity.
     *
     * @return a string
     */
    public String getKindLabel() {
        return getTypeName();
    }


}
