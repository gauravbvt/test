package com.mindalliance.channels.dao;

import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.util.Play;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

/**
 * Persistence through Hibernate.
 */
public class HibernateDao extends HibernateDaoSupport implements Dao {

    public HibernateDao() {
    }

    /**
     * Find a scenario given its id.
     *
     * @param id the id
     * @return the corresponding scenario, or null if not found.
     * @throws NotFoundException when not found
     */
    public Scenario findScenario( long id ) throws NotFoundException {
        final Scenario result = (Scenario) getHibernateTemplate().get( Scenario.class, id  );
        if ( result == null )
            throw new NotFoundException();
        return result;
    }

    /** {@inheritDoc} */
    @SuppressWarnings( { "unchecked" } )
    public <T extends ModelObject> Iterator<T> iterate( Class<T> clazz ) {
        return getHibernateTemplate().iterate( "from " + clazz.getSimpleName() );
    }

    /** {@inheritDoc} */
    public void add( ModelObject object ) {
        getHibernateTemplate().save( object );
    }

    /** {@inheritDoc} */
    public void remove( ModelObject object ) {
        if ( object instanceof Scenario )
            remove( (Scenario) object );
        else
            getHibernateTemplate().delete( object );
    }

    /**
     * Delete a scenario. Will not delete the last scenario (silently succeeds).
     *
     * @param scenario the scenario to delete
     */
    private void remove( Scenario scenario ) {
        if ( getScenarioCount() > 1 )
            getHibernateTemplate().delete( scenario );
    }

    /** {@inheritDoc} */
    public int getScenarioCount() {
        final Session session = getSession( false );
        try {
            return (Integer) session.createQuery( "select count(*) from Scenario" ).uniqueResult();

        } catch ( HibernateException ex ) {
            throw convertHibernateAccessException( ex );
        }
    }

    /** {@inheritDoc} */
    public Part createPart() {
        final Part part = new Part();
        getHibernateTemplate().save( part );
        return part;
    }

    /** {@inheritDoc} */
    public Connector createConnector() {
        final Connector connector = new Connector();
        getHibernateTemplate().save( connector );
        return connector;
    }

    /** {@inheritDoc} */
    @SuppressWarnings( { "unchecked" } )
    public <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException {
        final T result = (T) getHibernateTemplate().get( clazz, id );
        if ( result == null )
            throw new NotFoundException();
        return result;
    }

    /** {@inheritDoc} */
    public boolean isPermanent( ResourceSpec resourceSpec ) {
        return !getHibernateTemplate().getEntityInterceptor().isTransient( resourceSpec );
    }
}
