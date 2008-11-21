package com.mindalliance.channels.dao;

import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Person;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.ScenarioNode;
import com.mindalliance.channels.model.System;

/**
 * The fire in the building scenario...
 * @todo Move to test package when far enough along
 */
public class FireScenario extends Scenario {

    public FireScenario() {
        final Person joe = new Person( "Joe Smith" );

        final Part js1    = new Part( joe, "investigating fire" );
        js1.setRole( new Role( "Fire Warden" ) );
        js1.setDescription( "Fire alarms must be investigated carefully." );

        final Part js2    = new Part( joe, "monitoring evacuation" );
        final Part tenant = new Part( new Role( "Tenant" ), "noticing fire" );
        final Part chief  = new Part( new Role( "Fire Chief" ), "supervising operations" );
        final Connector connector = new Connector();
        final ScenarioNode evac = new ScenarioNode( new Scenario( "Building Evacuation" ) );
        final Part alarm  = new Part( new System( "Fire Alarm" ), "ringing" );

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
        connect( js1, evac ).setName( "started" );
        connect( evac, js2 ).setName( "ended" );
        connect( js2, chief ).setName( "status" );
    }
}
