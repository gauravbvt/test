package com.mindalliance.channels.service;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.Medium;
import com.mindalliance.channels.dao.EvacuationScenario;
import com.mindalliance.channels.dao.FireScenario;
import com.mindalliance.channels.util.Play;
import com.mindalliance.channels.util.SemMatch;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.collections.Predicate;

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
     * Channel media registered at startup
     */
    private List<Medium> media = new ArrayList<Medium>();

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
        final Scenario result = new Scenario();
        Scenario.initializeScenario( result );
        dao.add( result );
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Connector createConnector( Scenario scenario ) {
        final Connector result = dao.createConnector();
        scenario.addNode( result );
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Part createPart( Scenario scenario ) {
        final Part result = dao.createPart();
        scenario.addNode( result );
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Scenario findScenario( String name ) throws NotFoundException {
        for ( Iterator<Scenario> it = dao.iterate( Scenario.class ); it.hasNext(); ) {
            final Scenario s = it.next();
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
    public <T extends ModelObject> Iterator<T> iterate( Class<T> clazz ) {
        return dao.iterate( clazz );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( {"unchecked"} )
    public Iterator<ModelObject> iterateEntities() {
        return new FilterIterator( dao.iterate( ModelObject.class ), new Predicate() {
            public boolean evaluate( Object obj ) {
                return ( (ModelObject) obj ).isEntity();
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
        return dao.iterate( Scenario.class ).next();
    }

    public Dao getDao() {
        return dao;
    }

    /**
     * Use a specific dao. If 'addingSamples', add Fire and Evacuation scenarios.
     *
     * @param dao the dao
     */
    @Transactional
    public final void setDao( Dao dao ) {
        this.dao = dao;
        if ( addingSamples ) {
            LoggerFactory.getLogger( getClass() ).info( "Adding sample models" );
            // TODO initialize memory to default scenario instead of test scenario
            // dao.add( Scenario.createDefault() );
            try {
                final EvacuationScenario evac = new EvacuationScenario( this );
                dao.add( evac );
                dao.add( new FireScenario( this, evac ) );
            } catch ( NotFoundException e ) {
                throw new RuntimeException( e );
            }

        }
    }

    public boolean isAddingSamples() {
        return addingSamples;
    }

    public final void setAddingSamples( boolean addingSamples ) {
        this.addingSamples = addingSamples;
    }

    public List<Medium> getMedia() {
        return media;
    }

    public void setMedia( List<Medium> media ) {
        this.media = media;
    }

    /** {@inheritDoc}*/
    public void addMedium( Medium medium ) {
        media.add( medium );
    }

    /** {@inheritDoc} */
    public Medium mediumNamed( String name ) throws NotFoundException {
        Medium medium = null;
        for (Medium m: media) {
            if (m.getName().equalsIgnoreCase(name)) {
                medium = m;
                break;
            }
        }
        if (medium == null) throw new NotFoundException();
        return medium;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ModelObject> T findOrCreate( Class<T> clazz, String name ) {
        T result = null;
        final Iterator<T> objects = dao.iterate( clazz );
        while ( result == null && objects.hasNext() ) {
            final T object = objects.next();
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
    public Set<ResourceSpec> getAllResourceSpecs() {
        final Set<ResourceSpec> result = new HashSet<ResourceSpec>();

        // Permanent specs
        Iterator<ResourceSpec> specs = iterate( ResourceSpec.class );
        while ( specs.hasNext() ) result.add( ResourceSpec.with( specs.next() ) );

        // Transient specs from entities
        Iterator<Actor> actors = iterate( Actor.class );
        while ( actors.hasNext() ) result.add( ResourceSpec.with( actors.next() ) );
        Iterator<Role> roles = iterate( Role.class );
        while ( roles.hasNext() ) result.add( ResourceSpec.with( roles.next() ) );
        Iterator<Organization> orgs = iterate( Organization.class );
        while ( orgs.hasNext() ) result.add( ResourceSpec.with( orgs.next() ) );

        // Transient specs from scenario parts
        Iterator<Scenario> allScenarios = iterate( Scenario.class );
        while ( allScenarios.hasNext() ) {
            Scenario scenario = allScenarios.next();
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                ResourceSpec partResourceSpec = part.resourceSpec();
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

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceSpec> findAllResourcesNarrowingOrEqualTo( ResourceSpec resourceSpec ) {
        final List<ResourceSpec> list = new ArrayList<ResourceSpec>();
        for ( ResourceSpec spec : getAllResourceSpecs() ) {
            if ( spec.narrowsOrEquals( resourceSpec ) )
                list.add( spec );
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceSpec> findAllContacts( ResourceSpec resourceSpec, boolean isSelf ) {
        final Set<ResourceSpec> contacts = new HashSet<ResourceSpec>();
        if ( isSelf ) {
            contacts.addAll( findAllResourcesNarrowingOrEqualTo( resourceSpec ) );
        } else {
            final List<Play> plays = findAllPlays( resourceSpec );
            for ( Play play : plays ) {
                contacts.add( play.getPart().resourceSpec() );
                contacts.add( play.getOtherPart().resourceSpec() );
            }
        }
        return new ArrayList<ResourceSpec>( contacts );
    }

    /**
     * {@inheritDoc}
     */
    public List<Play> findAllPlays( ResourceSpec resourceSpec ) {
        final Set<Play> plays = new HashSet<Play>();
        final Iterator<Scenario> allScenarios = iterate( Scenario.class );
        while ( allScenarios.hasNext() ) {
            final Scenario scenario = allScenarios.next();
            final Iterator<Flow> flows = scenario.flows();
            while ( flows.hasNext() ) {
                final Flow flow = flows.next();
                if ( Play.hasPlay( flow ) ) {
                    if ( flow.getSource().isPart()
                            && ( (Part) flow.getSource() ).involves( resourceSpec ) ) {
                        // role sends
                        final Play play = new Play( (Part) flow.getSource(), flow, true );
                        plays.add( play );
                    }
                    if ( flow.getTarget().isPart()
                            && ( (Part) flow.getTarget() ).involves( resourceSpec ) ) {
                        // role receives
                        final Play play = new Play( (Part) flow.getTarget(), flow, false );
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
        final List<Issue> foundIssues = new ArrayList<Issue>();
        for ( Iterator<UserIssue> it = iterate( UserIssue.class ); it.hasNext(); ) {
            final UserIssue userIssue = it.next();
            if ( userIssue.getAbout().equals( identifiable ) )
                foundIssues.add( userIssue );
        }
        return foundIssues;
    }

    /**
     * Register default media - used in test setups
     * @param service a service
     */
    public static void registerDefaultMedia(Service service) {
        service.addMedium(new Medium("Phone", "\\d{3}-\\d{3}-\\d{4}"));
        service.addMedium(new Medium("Fax", "\\d{3}-\\d{3}-\\d{4}"));
        service.addMedium(new Medium("Cell", "\\d{3}-\\d{3}-\\d{4}"));
        service.addMedium(new Medium("Email", "[^@\\s]+@[^@\\s]+\\.\\w+"));
        service.addMedium(new Medium("IM", ".+"));
        service.addMedium(new Medium("Radio", ".+"));
        service.addMedium(new Medium("Television", ".+"));
        service.addMedium(new Medium("Courier", ".+"));
        service.addMedium(new Medium("Face-to-face", ".*"));
        service.addMedium(new Medium("SendWordNow", ".+"));
        service.addMedium(new Medium("Other", ".+"));

    }


}
