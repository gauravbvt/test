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
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.support.JpaDaoSupport;

import java.text.MessageFormat;
import java.util.List;

/**
 * Persistence through Hibernate.
 */
public class HibernateDao extends JpaDaoSupport implements Dao {

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
        Scenario result = getJpaTemplate().find( Scenario.class, id  );
        if ( result == null )
            throw new NotFoundException();
        return result;
    }

    /** {@inheritDoc} */
    @SuppressWarnings( { "unchecked" } )
    public <T extends ModelObject> List<T> list( Class<T> clazz ) {
        return getJpaTemplate().find(
                MessageFormat.format( "from {0}", clazz.getSimpleName() ) );              // NON-NLS
    }

    /** {@inheritDoc} */
    public void add( ModelObject object ) {
        LoggerFactory.getLogger( getClass() ).debug(
            MessageFormat.format(
                "Adding {0} {1}", object.getClass().getSimpleName(), object ) );
        getJpaTemplate().persist( object );
    }

    /** {@inheritDoc} */
    public void remove( ModelObject object ) {
        if ( object instanceof Scenario )
            remove( (Scenario) object );
        else
            getJpaTemplate().remove( object );
    }

    /**
     * Delete a scenario. Will not delete the last scenario (silently succeeds).
     * @param scenario the scenario to delete
     */
    private void remove( Scenario scenario ) {
        if ( getScenarioCount() > 1L )
            getJpaTemplate().remove( scenario );
    }

    /** {@inheritDoc} */
    public long getScenarioCount() {

        return (Long) getJpaTemplate()
                .find( "select count(*) from Scenario"  ).get( 0 );                       // NON-NLS
    }

    /** {@inheritDoc} */
    public Part createPart( Scenario scenario ) {
        Part part = new Part();
        part.setScenario( scenario );
        getJpaTemplate().persist( part );
        return part;
    }

    /** {@inheritDoc} */
    public Connector createConnector( Scenario scenario ) {
        Connector connector = new Connector();
        connector.setScenario( scenario );
        getJpaTemplate().persist( connector );
        return connector;
    }

    /** {@inheritDoc} */
    public ExternalFlow createExternalFlow( Node source, Node target, String name ) {
        ExternalFlow externalFlow = new ExternalFlow( source, target, name );
        getJpaTemplate().persist( externalFlow );
        return externalFlow;
    }

    /** {@inheritDoc} */
    public InternalFlow createInternalFlow( Node source, Node target, String name ) {
        InternalFlow internalFlow = new InternalFlow( source, target, name );
        getJpaTemplate().persist( internalFlow );
        return internalFlow;
    }

    /** {@inheritDoc} */
    @SuppressWarnings( { "unchecked" } )
    public <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException {
        T result = getJpaTemplate().find( clazz, id );
        if ( result == null )
            throw new NotFoundException();
        return result;
    }

    /** {@inheritDoc} */
    @SuppressWarnings( { "unchecked" } )
    public <T extends ModelObject> T find( Class<T> clazz, String name ) {
        List<T> list = (List<T>) getJpaTemplate().find(
                MessageFormat.format( "from {0} where name = ?",                          // NON-NLS
                                      clazz.getSimpleName() ),
                name );
        return list.isEmpty() ? null : list.get( 0 );
    }
}
