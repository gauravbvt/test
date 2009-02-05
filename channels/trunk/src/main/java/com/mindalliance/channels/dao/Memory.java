package com.mindalliance.channels.dao;

import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Dao;
import com.mindalliance.channels.DuplicateKeyException;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.NotFoundException;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.InternalFlow;
import com.mindalliance.channels.Organization;
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

    /** Counter for generated modelobjects id. */
    private long idCounter = 1L;

    /** The scenarios, for convenience... */
    private Set<Scenario> scenarios = new HashSet<Scenario>();

    /**
     * ModelObjects, indexed by id.
     */
    private Map<Long, ModelObject> idIndex = new HashMap<Long, ModelObject>( INITIAL_CAPACITY );

    public Memory() {
    }

    /** {@inheritDoc} */
    @SuppressWarnings( { "unchecked" } )
    public <T extends ModelObject> Iterator<T> iterate( final Class<T> clazz ) {
        return new FilterIterator( idIndex.values().iterator(), new Predicate() {
            public boolean evaluate( Object object ) {
                return clazz.isAssignableFrom( object.getClass() );
            }
        } );
    }

    /** {@inheritDoc} */
    public void add( ModelObject object ) {
        if ( object instanceof ResourceSpec ) {
            ResourceSpec resourceSpec = (ResourceSpec) object;
            if ( resourceSpec.isEmpty()/* || resourceSpec.isEntityOnly()*/ )
                return;
        }
        if ( idIndex.containsKey( object.getId() ) )
            throw new DuplicateKeyException();

        object.setId( newId() );
        idIndex.put( object.getId(), object );

        if ( object instanceof Scenario )
            scenarios.add( (Scenario) object );

    }

    /** {@inheritDoc} */
    public int getScenarioCount() {
        return scenarios.size();
    }

    /** {@inheritDoc} */
    public Part createPart() {
        Part part = new Part();
        part.setId( newId() );
        return part;
    }

    /** {@inheritDoc} */
    public Connector createConnector() {
        Connector connector = new Connector();
        connector.setId( newId() );
        return connector;
    }

    /** {@inheritDoc} */
    public ExternalFlow createExternalFlow( Node source, Node target, String name ) {
        ExternalFlow externalFlow = new ExternalFlow( source, target, name );
        externalFlow.setId( newId() );
        return externalFlow;
    }

    /** {@inheritDoc} */
    public InternalFlow createInternalFlow( Node source, Node target, String name ) {
        InternalFlow internalFlow = new InternalFlow( source, target, name );
        internalFlow.setId( newId() );
        return internalFlow;
    }

    private long newId() {
        return idCounter++;
    }

    /** {@inheritDoc} */
    @SuppressWarnings( { "unchecked" } )
    public <T extends ModelObject> T find( Class<T> clazz, long id ) throws NotFoundException {
        return (T) find( id );
    }

    /** {@inheritDoc} */
    public void remove( ModelObject object ) {
        if ( object instanceof Scenario )
            remove( (Scenario) object );
        else if ( object instanceof ResourceSpec )
            remove( (ResourceSpec) object );
        else
            idIndex.remove( object.getId() );
    }

    private void remove( Scenario scenario ) {
        if ( scenarios.size() > 1 ) {
            scenarios.remove( scenario );
            idIndex.remove( scenario.getId() );

            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                Part p = parts.next();
                p.removeAllOutcomes();
                p.removeAllRequirements();
            }
        }
    }

    private void remove( ResourceSpec resourceSpec ) {
        //  Remove all permanent specs equal or narrowing resourceSpec
        List<ResourceSpec> toDelete = new ArrayList<ResourceSpec>();
        Iterator<ResourceSpec> resourceSpecs = iterate( ResourceSpec.class );
        while ( resourceSpecs.hasNext() ) {
            ResourceSpec spec = resourceSpecs.next();
            if ( spec.narrowsOrEquals( resourceSpec ) ) toDelete.add( spec );
        }
        for ( ResourceSpec spec : toDelete ) {
            idIndex.remove( spec.getId() );
        }
        //  Cascade delete operation to parts and entities
        cascadeDeleteResourceSpecToParts( resourceSpec );
        cascadeDeleteResourceSpecToEntities( resourceSpec );
    }


    private ModelObject find( long id ) throws NotFoundException {
        ModelObject result = idIndex.get( id );
        if ( result == null ) {
            Iterator<Scenario> iterator = iterate( Scenario.class );
            while ( result == null && iterator.hasNext() ) {
                Scenario scenario = iterator.next();
                result = scenario.getNode( id );
                if ( result == null ) {
                    Iterator<Flow> flows = scenario.flows();
                    while ( result == null && flows.hasNext() ) {
                        Flow flow = flows.next();
                        if ( flow.getId() == id ) result = flow;
                    }
                }
            }
        }
        if ( result == null ) throw new NotFoundException();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPermanent( ResourceSpec resourceSpec ) {
        return idIndex.containsKey( resourceSpec.getId() );
    }

    /**
     * Remove an entity if the resourceSpec to be deleted amounts to that entity
     *
     * @param resourceSpec a resource specification being deleted
     */
    private void cascadeDeleteResourceSpecToEntities( ResourceSpec resourceSpec ) {
        if ( resourceSpec.isActorOnly() ) {
            remove( resourceSpec.getActor() );
        } else if ( resourceSpec.isRoleOnly() ) {
            remove( resourceSpec.getRole() );
        } else if ( resourceSpec.isOrganizationOnly() ) {
            remove( resourceSpec.getOrganization() );
            cascadeDeleteOrganizationToEntities(resourceSpec.getOrganization());
        } else if ( resourceSpec.isJurisdictionOnly() ) {
            remove( resourceSpec.getJurisdiction() );
        }
    }

    /**
     * Remove the deleted organization from entity definitions
     * @param organization the deleted organization
     */
    private void cascadeDeleteOrganizationToEntities( Organization organization ) {
        Iterator<Organization> organizations = iterate(Organization.class);
        while (organizations.hasNext()) {
            Organization org = organizations.next();
            Organization parent = org.getParent();
            if (parent != null && parent == organization) {
                // replace deleted parent by parent's parent (could be null)
                org.setParent( parent.getParent() );
            }
        }
    }

    /**
     * Clear resource spec properties of the part to reflect a resource spec being deleted
     *
     * @param resourceSpec a resource specification being deleted
     */
    private void cascadeDeleteResourceSpecToParts( ResourceSpec resourceSpec ) {
        Iterator<Scenario> allScenarios = iterate( Scenario.class );
        while ( allScenarios.hasNext() ) {
            Scenario scenario = allScenarios.next();
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                part.removeResourceSpec( resourceSpec );
            }
        }
    }


}
