package com.mindalliance.channels.dao;

import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.InternalFlow;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Scenario;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;

import java.util.Iterator;

/**
 * Persistence through Hibernate.
 */
@Transactional( isolation = Isolation.DEFAULT )
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
        Scenario result = (Scenario) getHibernateTemplate().get( Scenario.class, id  );
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
        if ( !( object instanceof Node ) && !( object instanceof Flow ) ) {
            LoggerFactory.getLogger( getClass() ).debug( "Adding " + object.getClass().getSimpleName() + " " + object );
            getHibernateTemplate().save( object );
        }
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
        Session session = getSession( false );
        try {
            return (Integer) session.createQuery( "select count(*) from Scenario" ).uniqueResult();

        } catch ( HibernateException ex ) {
            throw convertHibernateAccessException( ex );
        }
    }

    /** {@inheritDoc} */
    public Part createPart() {
        Part part = new Part();
        getHibernateTemplate().save( part );
        return part;
    }

    /** {@inheritDoc} */
    public Connector createConnector() {
        Connector connector = new Connector();
        getHibernateTemplate().save( connector );
        return connector;
    }

    /** {@inheritDoc} */
    public ExternalFlow createExternalFlow( Node source, Node target, String name ) {
        return new ExternalFlow( source, target, name );
    }

    /** {@inheritDoc} */
    public InternalFlow createInternalFlow( Node source, Node target, String name ) {
        return new InternalFlow( source, target, name );
    }

    /** {@inheritDoc} */
    @SuppressWarnings( { "unchecked" } )
    public <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException {
        T result = (T) getHibernateTemplate().get( clazz, id );
        if ( result == null )
            throw new NotFoundException();
        return result;
    }

    /** {@inheritDoc} */
    public boolean isPermanent( ResourceSpec resourceSpec ) {
        return !getHibernateTemplate().getEntityInterceptor().isTransient( resourceSpec );
    }
}
