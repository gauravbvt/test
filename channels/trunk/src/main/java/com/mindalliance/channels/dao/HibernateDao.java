package com.mindalliance.channels.dao;

import com.mindalliance.channels.Channels;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.command.Command;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.InternalFlow;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.model.Scenario;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.support.JpaDaoSupport;

import javax.persistence.EntityManager;
import java.text.MessageFormat;
import java.util.List;

/**
 * Persistence through Hibernate.
 */
public class HibernateDao extends JpaDaoSupport implements Dao {

    private Channels channels;

    public HibernateDao() {
    }

    public Channels getChannels() {
        return channels;
    }

    public void setChannels( Channels channels ) {
        this.channels = channels;
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

    public void add( ModelObject object, Long id ) {
        //Todo
        add( object );
    }

    /** {@inheritDoc} */
    public void update( ModelObject object ) {
        LoggerFactory.getLogger( getClass() ).debug(
            MessageFormat.format(
                "Updating {0} {1}", object.getClass().getSimpleName(), object ) );
        getJpaTemplate().merge( object );
    }

    /** {@inheritDoc} */
    public void remove( ModelObject object ) {
        LoggerFactory.getLogger( getClass() ).debug(
            MessageFormat.format(
                "Removing {0} {1}", object.getClass().getSimpleName(), object ) );
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

    public Plan createPlan() {
        return new Plan();
    }

    /** {@inheritDoc} */
    public long getScenarioCount() {
        return (Long) getJpaTemplate()
                .find( "select count(*) from Scenario"  ).get( 0 );                       // NON-NLS
    }

    /** {@inheritDoc} */
    public Part createPart( Scenario scenario, Long id ) {
        // TODO - account for id
        Part part = new Part();
        part.setScenario( scenario );
        getJpaTemplate().persist( part );
        return part;
    }

    /** {@inheritDoc} */
    public Connector createConnector( Scenario scenario, Long id ) {
        // TODO - account for id
        Connector connector = new Connector();
        connector.setScenario( scenario );
        getJpaTemplate().persist( connector );
        return connector;
    }

    /** {@inheritDoc} */
    public ExternalFlow createExternalFlow( Node source, Node target, String name, Long id ) {
        ExternalFlow externalFlow = new ExternalFlow( source, target, name );
        // TODO - set id if not null
        getJpaTemplate().persist( externalFlow );
        return externalFlow;
    }

    /** {@inheritDoc} */
    public InternalFlow createInternalFlow( Node source, Node target, String name, Long id ) {
        InternalFlow internalFlow = new InternalFlow( source, target, name );
        // TODO - set id if not null
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

    /** {@inheritDoc} */
    public void flush() {
        getJpaTemplate().execute( new JpaCallback() {
            public Object doInJpa( EntityManager em ) {
                em.flush();
                return null;
            }
        } );
    }

    /**
      * {@inheritDoc}
      */
     public void load() {
        // TODO - Do nothing for now
     }

    /**
      * {@inheritDoc}
      */
    public void afterInitialize() {
        //Todo - Do nothing for now
    }

    public void onDestroy() {
        // TODO - Do nothing for now
    }

    /**
      * {@inheritDoc}
      */
    public Long getLastAssignedId() {
        // TODO
        return null;
    }

    /**
       * {@inheritDoc}
       */
     public void setLastAssignedId( Long lastId ) {
         // TODO
     }

    /**
     * {@inheritDoc}
     */
    public void onAfterCommand( Command command ) {
        // TODO - Do nothing for now
    }

}
