package com.mindalliance.channels.dao;

import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.ExternalFlow;
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

import java.text.MessageFormat;
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
        Scenario result = (Scenario) getHibernateTemplate().get( Scenario.class, id  );
        if ( result == null )
            throw new NotFoundException();
        return result;
    }

    /** {@inheritDoc} */
    @SuppressWarnings( { "unchecked" } )
    public <T extends ModelObject> List<T> getAll( Class<T> clazz ) {
        return getHibernateTemplate().find( "from " + clazz.getSimpleName() );
    }

    /** {@inheritDoc} */
    public void add( ModelObject object ) {
        LoggerFactory.getLogger( getClass() ).debug(
            MessageFormat.format(
                "Adding {0} {1}", object.getClass().getSimpleName(), object ) );
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
    public long getScenarioCount() {
        Session session = getSession( true );
        try {
            return (Long) session.createQuery( "select count(*) from Scenario" ).uniqueResult();

        } catch ( HibernateException ex ) {
            throw convertHibernateAccessException( ex );
        }
    }

    /** {@inheritDoc} */
    public Part createPart( Scenario scenario ) {
        Part part = new Part();
        part.setScenario( scenario );
        getHibernateTemplate().save( part );
        return part;
    }

    /** {@inheritDoc} */
    public Connector createConnector( Scenario scenario ) {
        Connector connector = new Connector();
        connector.setScenario( scenario );
        getHibernateTemplate().save( connector );
        return connector;
    }

    /** {@inheritDoc} */
    public ExternalFlow createExternalFlow( Node source, Node target, String name ) {
        ExternalFlow externalFlow = new ExternalFlow( source, target, name );
        getHibernateTemplate().save( externalFlow );
        return externalFlow;
    }

    /** {@inheritDoc} */
    public InternalFlow createInternalFlow( Node source, Node target, String name ) {
        InternalFlow internalFlow = new InternalFlow( source, target, name );
        getHibernateTemplate().save( internalFlow );
        return internalFlow;
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
