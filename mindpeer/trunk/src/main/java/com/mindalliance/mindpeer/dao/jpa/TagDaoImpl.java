// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.mindpeer.dao.jpa;

import com.mindalliance.mindpeer.dao.TagDao;
import com.mindalliance.mindpeer.model.Tag;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Tag DAO JPA implementation.
 */
@Transactional
public class TagDaoImpl extends JpaDaoSupport implements TagDao {

    /**
     * Create a new TagDaoImpl instance.
     */
    public TagDaoImpl() {
    }

    /**
     * Get or create a persistent tag from a string description.
     *
     * @param description the given description
     * @return the corresponding tag
     */
    public Tag get( final String description ) {
        if ( description == null )
            throw new IllegalArgumentException();

        return (Tag) getJpaTemplate().execute( new JpaCallback() {
            public Object doInJpa( EntityManager em ) {
                String desc = description.toLowerCase();
                Query query = em.createQuery(
                        "select e from Tag e where description = '" + desc + '\'' );
                List<?> resultList = query.getResultList();
                return resultList.isEmpty() ? em.merge( new Tag( desc ) )
                                            : resultList.get( 0 );
            }
        } );
    }

    /**
     * Get a tag given its id.
     *
     * @param id the given id
     * @return the corresponding tag or null
     */
    public Tag get( long id ) {
        return getJpaTemplate().find( Tag.class, id );
    }

    /**
     * Convert a collection of tag descriptions into a set of persistent tags.
     *
     * @param descriptions the given descriptions
     * @return a set of corresponding tag
     */
    public Set<Tag> get( Collection<String> descriptions ) {
        Set<Tag> result = new HashSet<Tag>( descriptions.size() );
        for ( String description : descriptions )
            result.add( get( description ) );

        return result;
    }

    /**
     * Delete a tag.
     * @param tag the tag to delete. May raise an exception when used somewhere else.
     */
    public void delete( Tag tag ) {
        getJpaTemplate().remove( tag );
    }

    /**
     * Return all defined tags.
     * @return list of tags
     */
    @SuppressWarnings( { "unchecked" } )
    public List<Tag> getAll() {
        return (List<Tag>) getJpaTemplate().execute( new JpaCallback() {
            public Object doInJpa( EntityManager em ) {
                String className = Tag.class.getSimpleName();
                Query query = em.createQuery( "select e from " + className + " e" );
                return query.getResultList();
            }
        } );
    }

    /**
     * Count all tags defined in the system.
     * @return the number of tags
     */
    public int countAll() {
        return (Integer) getJpaTemplate().execute( new JpaCallback() {
            public Object doInJpa( EntityManager em ) {
                String className = Tag.class.getSimpleName();
                Query query = em.createQuery( "select count (e) from " + className + " e" );
                return ( (Long) query.getSingleResult() ).intValue();
            }
        } );
    }

}
