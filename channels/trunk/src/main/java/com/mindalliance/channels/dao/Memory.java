package com.mindalliance.channels.dao;

import com.mindalliance.channels.DuplicateKeyException;
import com.mindalliance.channels.ScenarioDao;
import com.mindalliance.channels.model.Scenario;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * ...
 */
public class Memory implements ScenarioDao {

    private Set<Scenario> scenarios = new TreeSet<Scenario>();
    private Map<String,Scenario> nameIndex = new HashMap<String,Scenario>();
    private Map<Long,Scenario> idIndex = new HashMap<Long,Scenario>();

    public Memory() {
    }

    public Scenario findScenario( String name ) {
        return nameIndex.get( name );
    }

    public Scenario findScenario( long id ) {
        return idIndex.get( id );
    }

    public Iterator<Scenario> scenarios() {
        return scenarios.iterator();
    }

    public void removeScenario( Scenario scenario ) {
        scenarios.remove( scenario );
        nameIndex.remove( scenario.getName() );
        idIndex.remove( scenario.getId() );
    }

    public void addScenario( Scenario scenario ) throws DuplicateKeyException {
        if ( nameIndex.containsKey( scenario.getName() )
                || nameIndex.containsKey( scenario.getId() ) )
            throw new DuplicateKeyException();

        scenarios.add( scenario );
        nameIndex.put( scenario.getName(), scenario );
        idIndex.put( scenario.getId(), scenario );
    }
}
