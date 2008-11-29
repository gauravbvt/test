package com.mindalliance.channels.dao;

import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.ScenarioNode;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Organization;

/**
 * The fire in the building scenario...
 * @todo Move to test package when far enough along
 */
public class FireScenario extends Scenario {

    public FireScenario() {
        setName( "Fire in the building" );
        setDescription( "A fire happens" );

        final Node defNode = nodes().next();

        final Actor joe = new Actor( "Joe Smith" );

        final Part js1    = new Part( joe, "investigating fire" );
        js1.setRole( new Role( "Fire Warden" ) );
        js1.setDescription( "Fire alarms must be investigated carefully." );

        final Part js2    = new Part( joe, "monitoring evacuation" );
        final Part tenant = new Part( new Role( "Tenant" ), "noticing fire" );
        final Part chief  = new Part( new Role( "Fire Chief" ), "supervising operations" );
        final Connector connector = new Connector();
        final ScenarioNode evac = new ScenarioNode( new Scenario( "Building Evacuation" ) );
        final Actor system = new Actor( "Fire Alarm" );
        system.setSystem( true );
        final Part alarm  = new Part( system, "ringing" );

        final Part fd     = new Part();
        fd.setOrganization( new Organization( "Fire Department" ) );
        fd.setTask( "responding" );

        addNode( connector );
        addNode( evac );
        addNode( js1 );
        addNode( js2 );
        addNode( tenant );
        addNode( chief );
        addNode( alarm );
        addNode( fd );

        connect( tenant, alarm );
        final Flow f1 = connect( alarm, js1 );
        f1.setName( "location" );
        f1.setAskedFor( true );
        f1.setChannel( "wall panel" );
        f1.setMaxDelay( "0min" );
        f1.setDescription( "The fire location reported by the system" );

        final Flow f2 = connect( js1, chief );
        f2.setName( "fire location" );
        f2.setAskedFor( true );
        f2.setMaxDelay( "0min" );
        f2.setDescription( "Communicate the location of the fire" );

        connect( alarm, fd ).setName( "address" );
        connect( fd, chief );
        connect( chief, connector ).setName( "all-clear" );
        connect( js1, evac ).setName( "go-ahead" );
        connect( evac, js2 ).setName( "end" );
        final Flow flow = connect( js2, chief );
        flow.setName( "evacuation status" );
        flow.setAskedFor( true );

        removeNode( defNode );
    }
}
