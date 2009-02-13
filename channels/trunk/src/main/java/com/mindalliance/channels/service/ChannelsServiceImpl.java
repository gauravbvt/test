package com.mindalliance.channels.service;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.dao.EvacuationScenario;
import com.mindalliance.channels.dao.FireScenario;
import com.mindalliance.channels.util.Play;
import com.mindalliance.channels.util.SemMatch;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Utility class for common functionality for all Dao implementations.
 * Done this way because of HibernateDao requires a specific superclass
 * and java does not support multiple inheritance...
 */
public class ChannelsServiceImpl implements Service {

    /**
     * The implementation dao.
     */
    private Dao dao;

    /**
     * True if defaults scenarios will be added when dao is set.
     */
    private boolean addingSamples;

    public ChannelsServiceImpl() {
    }

    public ChannelsServiceImpl( Dao dao ) {
        setAddingSamples( false );
        setDao( dao );
    }

    /**
     * {@inheritDoc}
     */
    public Scenario createScenario() {
        Scenario result = new Scenario();
        dao.add( result );

        result.setName( Scenario.DEFAULT_NAME );
        result.setDescription( Scenario.DEFAULT_DESCRIPTION );
        createPart( result );

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Connector createConnector( Scenario scenario ) {
        Connector result = dao.createConnector( scenario );
        scenario.addNode( result );
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Part createPart( Scenario scenario ) {
        Part result = dao.createPart( scenario );
        scenario.addNode( result );
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Flow connect( Node source, Node target, String name ) {
        Flow result;

        if ( isInternal( source, target ) ) {
            if ( getFlow( source, target, name ) != null )
                throw new IllegalArgumentException();

            result = dao.createInternalFlow( source, target, name );
            source.addOutcome( result );
            target.addRequirement( result );

        } else if ( isExternal( source, target ) ) {
            result = dao.createExternalFlow( source, target, name );
            if ( source.isConnector() ) {
                target.addRequirement( result );
                ( (Connector) source ).addExternalFlow( (ExternalFlow) result );
            } else {
                source.addOutcome( result );
                ( (Connector) target ).addExternalFlow( (ExternalFlow) result );
            }

        } else
            throw new IllegalArgumentException();

        return result;
    }

    private static boolean isInternal( Node source, Node target ) {
        Scenario scenario = source.getScenario();
        return scenario != null && scenario.equals( target.getScenario() );
    }

    private static boolean isExternal( Node source, Node target ) {
        Scenario scenario = source.getScenario();
        return scenario != null
                && !scenario.equals( target.getScenario() )
                && ( target.isConnector() || source.isConnector() );
    }

    /**
     * Find flow between a source and a target.
     *
     * @param source the source
     * @param target the target
     * @param name   the name of the flow
     * @return the connecting flow, or null if none
     */
    private static Flow getFlow( Node source, Node target, String name ) {
        for ( Iterator<Flow> flows = source.outcomes(); flows.hasNext(); ) {
            Flow f = flows.next();
            if ( target.equals( f.getTarget() ) && f.getName().equals( name ) )
                return f;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Scenario findScenario( String name ) throws NotFoundException {
        for ( Scenario s : dao.getAll( Scenario.class ) ) {
            if ( name.equals( s.getName() ) )
                return s;
        }

        throw new NotFoundException();
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException {
        return dao.find( clazz, id );
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelObject> List<T> list( Class<T> clazz ) {
        return dao.getAll( clazz );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( {"unchecked"} )
    public Iterator<ModelObject> iterateEntities() {
        return new FilterIterator( dao.getAll( ModelObject.class ).iterator(), new Predicate() {
            public boolean evaluate( Object object ) {
                return ( (ModelObject) object ).isEntity();
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    public void add( ModelObject object ) {
        dao.add( object );
    }

    /**
     * {@inheritDoc}
     */
    public void remove( ModelObject object ) {
        dao.remove( object );
    }

    /**
     * {@inheritDoc}
     */
    public Scenario getDefaultScenario() {
        return dao.getAll( Scenario.class ).iterator().next();
    }

    public Dao getDao() {
        return dao;
    }

    /**
     * Use a specific dao.
     *
     * @param dao the dao
     */
    public void setDao( Dao dao ) {
        this.dao = dao;
    }

    /**
     * {@inheritDoc}
     */
    public void initialize() {
        if ( !dao.getAll( Scenario.class ).iterator().hasNext() ) {
            if ( addingSamples ) {
                LoggerFactory.getLogger( getClass() ).info( "Adding sample models" );
                Scenario evac = createScenario();
                EvacuationScenario.initialize( evac, this );
                Scenario fireScenario = createScenario();
                FireScenario.initialize( fireScenario, this, evac );
            } else
                createScenario();
        }
    }

    public boolean isAddingSamples() {
        return addingSamples;
    }

    public void setAddingSamples( boolean addingSamples ) {
        this.addingSamples = addingSamples;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelObject> T findOrCreate( Class<T> clazz, String name ) {
        T result = null;
        Iterator<T> objects = dao.getAll( clazz ).iterator();
        while ( result == null && objects.hasNext() ) {
            T object = objects.next();
            if ( SemMatch.same( object.getName(), name ) ) result = object;
        }
        if ( result == null ) {
            try {
                result = clazz.newInstance();
                result.setName( name );
                dao.add( result );
            } catch ( InstantiationException e ) {
                throw new RuntimeException( e );
            } catch ( IllegalAccessException e ) {
                throw new RuntimeException( e );
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceSpec> findAllResourceSpecs() {
        Set<ResourceSpec> result = new HashSet<ResourceSpec>();

        addSpecs( result, ResourceSpec.class );
        addSpecs( result, Actor.class );
        addSpecs( result, Role.class );
        addSpecs( result, Organization.class );

        // Transient specs from scenario parts
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                ResourceSpec partResourceSpec = part.resourceSpec();
                if ( !partResourceSpec.isAnyone() ) {
                    // Find all channels used to communicate with this part
                    Iterator<Flow> flows = scenario.flows();
                    while ( flows.hasNext() ) {
                        Flow flow = flows.next();
                        if ( !flow.getChannels().isEmpty() ) {
                            if ( flow.getTarget() == part && !flow.isAskedFor() ) {
                                partResourceSpec.addChannels( flow.getChannels() );
                            }
                            if ( flow.getSource() == part && flow.isAskedFor() ) {
                                partResourceSpec.addChannels( flow.getChannels() );
                            }
                        }
                    }
                    result.add( partResourceSpec );
                }
            }
        }

        return new ArrayList<ResourceSpec>( result );
    }

    private void addSpecs( Set<ResourceSpec> result, Class<? extends ModelObject> clazz ) {
        for ( ModelObject modelObject : list( clazz ) )
            if ( modelObject instanceof ResourceSpec )
                result.add( (ResourceSpec) modelObject );
            else
                result.add( ResourceSpec.with( modelObject ) );
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceSpec> findAllResourcesNarrowingOrEqualTo( ResourceSpec resourceSpec ) {
        List<ResourceSpec> list = new ArrayList<ResourceSpec>();
        for ( ResourceSpec spec : findAllResourceSpecs() ) {
            if ( spec.narrowsOrEquals( resourceSpec ) )
                list.add( spec );
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceSpec> findAllResourcesBroadeningOrEqualTo( ResourceSpec resourceSpec ) {
        List<ResourceSpec> list = new ArrayList<ResourceSpec>();
        for ( ResourceSpec spec : findAllResourceSpecs() ) {
            if ( resourceSpec.narrowsOrEquals( spec ) )
                list.add( spec );
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceSpec> findAllContacts( ResourceSpec resourceSpec, boolean isSelf ) {
        Set<ResourceSpec> contacts = new HashSet<ResourceSpec>();
        if ( isSelf ) {
            contacts.addAll( findAllResourcesNarrowingOrEqualTo( resourceSpec ) );
        } else {
            List<Play> plays = findAllPlays( resourceSpec );
            for ( Play play : plays ) {
                ResourceSpec partSpec = play.getPart().resourceSpec();
                ResourceSpec otherPartSpec = play.getOtherPart().resourceSpec();
                if (!partSpec.isAnyone()) contacts.add( partSpec );
                if (!otherPartSpec.isAnyone()) contacts.add( otherPartSpec );
            }
        }
        return new ArrayList<ResourceSpec>( contacts );
    }

    /**
     * {@inheritDoc}
     */
    public List<Play> findAllPlays( ResourceSpec resourceSpec ) {
        Set<Play> plays = new HashSet<Play>();
        for ( Scenario scenario : list( Scenario.class ) ) {
            Iterator<Flow> flows = scenario.flows();
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                if ( Play.hasPlay( flow ) ) {
                    if ( flow.getSource().isPart()
                            && ( (Part) flow.getSource() ).involves( resourceSpec ) ) {
                        // role sends
                        Play play = new Play( (Part) flow.getSource(), flow, true );
                        plays.add( play );
                    }
                    if ( flow.getTarget().isPart()
                            && ( (Part) flow.getTarget() ).involves( resourceSpec ) ) {
                        // role receives
                        Play play = new Play( (Part) flow.getTarget(), flow, false );
                        plays.add( play );
                    }
                }
            }
        }
        return new ArrayList<Play>( plays );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPermanent( ResourceSpec resourceSpec ) {
        return dao.isPermanent( resourceSpec );
    }

    /**
     * {@inheritDoc}
     */
    public List<Issue> findAllUserIssues( ModelObject identifiable ) {
        List<Issue> foundIssues = new ArrayList<Issue>();
        for ( UserIssue userIssue : list( UserIssue.class ) ) {
            if ( userIssue.getAbout().equals( identifiable ) )
                foundIssues.add( userIssue );
        }
        return foundIssues;
    }

    /**
     * {@inheritDoc}
     */
    public ResourceSpec findPermanentResourceSpec( ResourceSpec resourceSpec ) {
        ResourceSpec permanent = null;
        Iterator<ResourceSpec> iterator = list( ResourceSpec.class ).iterator();
        while ( permanent == null && iterator.hasNext() ) {
            ResourceSpec rs = iterator.next();
            if ( rs.equals( resourceSpec ) )
                permanent = rs;
        }
        return permanent;
    }

    /**
     * {@inheritDoc}
     */
    public void addOrUpdate( ResourceSpec resourceSpec ) {
        ResourceSpec permanent = findPermanentResourceSpec( resourceSpec );
        if ( permanent == null ) {
            add( resourceSpec );
        } else {
            permanent.setChannels( resourceSpec.getChannels() );
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( {"unchecked"} )
    public List<Actor> findAllActors( ResourceSpec resourceSpec ) {
        Set<Actor> actors = new HashSet<Actor>();
        Iterator<ResourceSpec> actorResourceSpecs = new FilterIterator(
                findAllResourceSpecs().iterator(),
                new Predicate() {
                    public boolean evaluate( Object object ) {
                        return ( (ResourceSpec) object ).getActor() != null;
                    }
                } );
        while ( actorResourceSpecs.hasNext() ) {
            ResourceSpec actorResourceSpec = actorResourceSpecs.next();
            if ( actorResourceSpec.narrowsOrEquals( resourceSpec ) ) {
                actors.add( actorResourceSpec.getActor() );
            }
        }
        return new ArrayList<Actor>( actors );
    }
}
