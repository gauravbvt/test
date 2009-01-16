package com.mindalliance.channels.dao;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.DuplicateKeyException;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Jurisdiction;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.analysis.profiling.Play;
import com.mindalliance.channels.util.SemMatch;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An in-memory, no-transactions implementation of a store.
 */
public final class Memory implements Dao {

    /**
     * The sorted scenarios.
     */
    private Set<Scenario> scenarios = new HashSet<Scenario>();

    /**
     * Scenarios, indexed by id.
     */
    private Map<Long, ModelObject> idIndex = new HashMap<Long, ModelObject>( INITIAL_CAPACITY );

    public Memory() {
        // TODO initialize memory to default scenario instead of test scenario
        // addScenario( Scenario.createDefault() );
        final EvacuationScenario evac = new EvacuationScenario( this );
        addScenario( evac );
        addScenario( new FireScenario( this, evac ) );
    }

    /**
     * {@inheritDoc}
     */
    public Scenario createScenario() {
        // TODO factor out id initialization
        final Scenario result = new Scenario();
        result.setDao( this );
        Scenario.initializeScenario( result );
        addScenario( result );
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Scenario findScenario( String name ) throws NotFoundException {
        for ( Scenario s : scenarios ) {
            if ( name.equals( s.getName() ) )
                return s;
        }

        throw new NotFoundException();
    }

    /**
     * {@inheritDoc}
     */
    public Scenario findScenario( long id ) throws NotFoundException {
        return (Scenario) find( id );
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Scenario> scenarios() {
        return scenarios.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public void removeScenario( Scenario scenario ) {
        if ( scenarios.size() > 1 ) {
            scenarios.remove( scenario );
            idIndex.remove( scenario.getId() );

            final Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                final Part p = parts.next();
                p.removeAllOutcomes();
                p.removeAllRequirements();
            }

            // TODO hook to dao for nodes/flows deletions?
        }

    }

    /**
     * {@inheritDoc}
     */
    public void addScenario( Scenario scenario ) {
        final long id = scenario.getId();
        if ( idIndex.containsKey( id ) )
            throw new DuplicateKeyException();
        scenarios.add( scenario );
        idIndex.put( id, scenario );
    }

    /**
     * {@inheritDoc}
     */
    public Scenario getDefaultScenario() {
        return scenarios.iterator().next();
    }

    /**
     * {@inheritDoc}
     */
    public int getScenarioCount() {
        return scenarios.size();
    }

    /**
     * {@inheritDoc}
     */
    public Part createPart() {
        return new Part();
    }

    /**
     * {@inheritDoc}
     */
    public Connector createConnector() {
        return new Connector();
    }

    /**
     * Find a role given its id.
     *
     * @param id the id
     * @return the corresponding role, or null if not found.
     * @throws com.mindalliance.channels.NotFoundException
     *          when not found
     */
    public Role findRole( long id ) throws NotFoundException {
        return (Role) find( id );
    }

    /**
     * Find an actor given its id.
     *
     * @param id the id
     * @return the corresponding actor, or null if not found.
     * @throws com.mindalliance.channels.NotFoundException
     *          when not found
     */
    public Actor findActor( long id ) throws NotFoundException {
        return (Actor) find( id );
    }

    /**
     * {@inheritDoc}
     */
    public Organization findOrganization( long id ) throws NotFoundException {
        return (Organization) find( id );
    }

    /**
     * {@inheritDoc}
     */
    public Jurisdiction findJurisdiction( long id ) throws NotFoundException {
        return (Jurisdiction) find( id );
    }

    /**
     * {@inheritDoc}
     */
    // TODO - Inefficient
    @SuppressWarnings( {"unchecked"} )
    public Iterator<Role> roles() {
        return new FilterIterator( idIndex.values().iterator(), new Predicate() {
            public boolean evaluate( Object obj ) {
                return obj instanceof Role;
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    // TODO - Inefficient
    @SuppressWarnings( {"unchecked"} )
    public Iterator<Actor> actors() {
        return new FilterIterator( idIndex.values().iterator(), new Predicate() {
            public boolean evaluate( Object obj ) {
                return obj instanceof Actor;
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( {"unchecked"} )
    public Iterator<Organization> organizations() {
        return new FilterIterator( idIndex.values().iterator(), new Predicate() {
            public boolean evaluate( Object obj ) {
                return obj instanceof Organization;
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( {"unchecked"} )
    public Iterator<Jurisdiction> jurisdictions() {
        return new FilterIterator( idIndex.values().iterator(), new Predicate() {
            public boolean evaluate( Object obj ) {
                return obj instanceof Jurisdiction;
            }
        } );
    }


    /**
     * {@inheritDoc}
     */
    public Role findOrMakeRole( String name ) {
        Role role = null;
        Iterator<Role> roles = roles();
        while ( role == null && roles.hasNext() ) {
            Role r = roles.next();
            if ( SemMatch.same( r.getName(), name ) ) role = r;
        }
        if ( role == null ) {
            role = new Role( name );
            idIndex.put( role.getId(), role );
        }
        return role;
    }

    /**
     * {@inheritDoc}
     */
    public Actor findOrMakeActor( String name ) {
        Actor actor = null;
        Iterator<Actor> actors = actors();
        while ( actor == null && actors.hasNext() ) {
            Actor r = actors.next();
            if ( SemMatch.same( r.getName(), name ) ) actor = r;
        }
        if ( actor == null ) {
            actor = new Actor( name );
            idIndex.put( actor.getId(), actor );
        }
        return actor;
    }

    /**
     * {@inheritDoc}
     */
    public Organization findOrMakeOrganization( String name ) {
        Organization organization = null;
        Iterator<Organization> organizations = organizations();
        while ( organization == null && organizations.hasNext() ) {
            Organization r = organizations.next();
            if ( SemMatch.same( r.getName(), name ) ) organization = r;
        }
        if ( organization == null ) {
            organization = new Organization( name );
            idIndex.put( organization.getId(), organization );
        }
        return organization;
    }

    /**
     * {@inheritDoc}
     */
    public Jurisdiction findOrMakeJurisdiction( String name ) {
        Jurisdiction jurisdiction = null;
        Iterator<Jurisdiction> jurisdictions = jurisdictions();
        while ( jurisdiction == null && jurisdictions.hasNext() ) {
            Jurisdiction j = jurisdictions.next();
            if ( SemMatch.same( j.getName(), name ) ) jurisdiction = j;
        }
        if ( jurisdiction == null ) {
            jurisdiction = new Jurisdiction( name );
            idIndex.put( jurisdiction.getId(), jurisdiction );
        }
        return jurisdiction;
    }

    /**
     * {@inheritDoc}
     */
    public void removeRole( Role role ) {
        idIndex.remove( role.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public void removeActor( Actor actor ) {
        idIndex.remove( actor.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public void removeOrganization( Organization organization ) {
        idIndex.remove( organization.getId() );
    }

    /**
     * {@inheritDoc}
     */
    public void removeJurisdiction( Jurisdiction jurisdiction ) {
        idIndex.remove( jurisdiction.getId() );
    }

    private ModelObject find( long id ) throws NotFoundException {
        final ModelObject result = idIndex.get( id );
        if ( result == null )
            throw new NotFoundException();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public List<ResourceSpec> findAllResourcesNarrowingOrEqualTo( ResourceSpec resourceSpec ) {
        Set<ResourceSpec> set = new HashSet<ResourceSpec>();
        Iterator<Scenario> allScenarios = scenarios();
        // Todo look in role and organization definitions
        while ( allScenarios.hasNext() ) {
            Scenario scenario = allScenarios.next();
            Iterator<Part> parts = scenario.parts();
            // Looks for part resources that narrow or equal the given resourceSpec
            while ( parts.hasNext() ) {
                Part part = parts.next();
                ResourceSpec res = part.resourceSpec();
                if ( res.narrowsOrEquals( resourceSpec ) ) {
                    set.add( res );
                    // Find all channels used to communicate with this part
                    Iterator<Flow> flows = scenario.flows();
                    while ( flows.hasNext() ) {
                        Flow flow = flows.next();
                        if ( flow.getChannel() != null && !flow.getChannel().isEmpty() ) {
                            if ( flow.getTarget() == part && !flow.isAskedFor() ) {
                                res.addChannel( flow.getChannel() );
                            }
                            if ( flow.getSource() == part && flow.isAskedFor() ) {
                                res.addChannel( flow.getChannel() );
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<ResourceSpec>( set );
    }

    /**
     * {@inheritDoc}
     */
    public List<Play> findAllPlays( ResourceSpec resourceSpec ) {
        Set<Play> plays = new HashSet<Play>();
        Iterator<Scenario> allScenarios = scenarios();
        while ( allScenarios.hasNext() ) {
            Scenario scenario = allScenarios.next();
            Iterator<Flow> flows = scenario.flows();
            while ( flows.hasNext() ) {
                Flow flow = flows.next();
                if ( Play.hasPlay( flow ) ) {
                    if ( flow.getSource().isPart() && ( (Part) flow.getSource() ).involves( resourceSpec ) ) {
                        // role sends
                        Play play = new Play( (Part) flow.getSource(), flow, true );
                        plays.add( play );
                    }
                    if ( flow.getTarget().isPart() && ( (Part) flow.getTarget() ).involves( resourceSpec ) ) {
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
    public List<ResourceSpec> findAllContacts( ResourceSpec resourceSpec, boolean isSelf ) {
        Set<ResourceSpec> contacts = new HashSet<ResourceSpec>();
        if ( isSelf ) {
            contacts.addAll( this.findAllResourcesNarrowingOrEqualTo( resourceSpec ) );
        } else {
            List<Play> plays = findAllPlays( resourceSpec );
            for ( Play play : plays ) {
                contacts.add( play.getPart().resourceSpec() );
                contacts.add( play.getOtherPart().resourceSpec() );
            }
        }
        return new ArrayList<ResourceSpec>( contacts );
    }


}
