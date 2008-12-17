package com.mindalliance.channels.export.xml;

import com.thoughtworks.xstream.XStream;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.Location;
import com.mindalliance.channels.Jurisdiction;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 16, 2008
 * Time: 1:15:43 PM
 */
public class ConfiguredXStream {


    public ConfiguredXStream() {
    }

    private static void configure( XStream xstream ) {
        xstream.aliasType( "scenario", Scenario.class );
        xstream.alias( "part", Part.class );
        xstream.aliasType( "flow", Flow.class );
        xstream.alias( "role", Role.class );
        xstream.alias( "actor", Actor.class );
        xstream.alias( "location", Location.class );
        xstream.alias( "jurisdiction", Jurisdiction.class );
        xstream.registerConverter( new ScenarioConverter() );
        xstream.registerConverter( new PartConverter() );
        xstream.registerConverter( new FlowConverter() );
    }

    public static XStream getNew() {
        XStream xstream = new XStream();
        configure( xstream );
        return xstream;
    }
}
