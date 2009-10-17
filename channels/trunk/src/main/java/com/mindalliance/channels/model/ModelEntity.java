package com.mindalliance.channels.model;

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
     * All universal types.
     */
    public static List<ModelEntity> UNIVERSAL_TYPES;
    /**
     * Type set.
     */
    private List<ModelEntity> tags = new ArrayList<ModelEntity>();
    /**
     * Whether the entity is actual, a type or TBD (null).
     */
    private Kind kind;

    static {
        UNIVERSAL_TYPES = new ArrayList<ModelEntity>();
        ANY_ACTOR_TYPE = new Actor( "any actor" );
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
    }

    public ModelEntity() {
    }

    public ModelEntity( String name ) {
        super( name );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEntity() {
        return true;
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
                || equals( ANY_PHASE_TYPE );
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
        if ( tags.contains( entity ) ) return true;
        visited.add( this );
        for ( ModelEntity tag : tags ) {
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
        return entity == null && other == null
                || entity != null
                && other != null
                && ( entity.equals( other ) || entity.hasTag( other ) );
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
            allTags.addAll( tags );
            visited.add( this );
            for ( ModelEntity tag : tags ) {
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
            if ( tags.contains( tag ) ) {
                inheritance.add( this );
            } else {
                List<List<ModelEntity>> allPaths = new ArrayList<List<ModelEntity>>();
                for ( ModelEntity t : tags ) {
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

}
