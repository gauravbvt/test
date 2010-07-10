package com.mindalliance.channels.social;

import com.mindalliance.channels.dao.PlanDefinition;
import com.mindalliance.channels.dao.User;
import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;

import java.io.File;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 9, 2010
 * Time: 10:38:04 PM
 */
public class DefaultDatabaseFactory implements DatabaseFactory {

   // private static final String COMMAND_EVENT_INDEX = "commandEventIndex";
   // private static final String PRESENCE_EVENT_INDEX = "presenceEventIndex";
   // private static final String MESSAGE_INDEX = "messageIndex";
   // private static Map<String, String[]> indices;
    private String odbDir;

   /* static {
        indices = new HashMap<String, String[]>();
        String[] presenceIndices = {"planId", "username"};
        indices.put( PRESENCE_EVENT_INDEX, presenceIndices );
        String[] commandIndices = {"planId", "username"};
        indices.put( COMMAND_EVENT_INDEX, commandIndices );
        String[] messageIndices = {"planId", "username", "toUsername", "fromUsername"};
        indices.put( MESSAGE_INDEX, messageIndices );
    }*/

    public DefaultDatabaseFactory() {
    }

    public ODB getDatabase() {
        return  ODBFactory.open( odbDir
                + File.separator
                + PlanDefinition.sanitize( getPlanUri() )
                + File.separator
                + "db" );
    }

/*
    private void buildIndicesIfNeeded() {
        ODB odb = getDatabase();
        ClassRepresentation classRepr;
        try {
            classRepr = odb.getClassRepresentation( PresenceEvent.class );
            if ( !classRepr.existIndex( PRESENCE_EVENT_INDEX ) ) {
                classRepr.addIndexOn( PRESENCE_EVENT_INDEX, indices.get( PRESENCE_EVENT_INDEX ), true );
            }
        } finally {
            if ( odb != null && !odb.isClosed() ) odb.close();
        }
        odb = getDatabase();
        try {
            classRepr = odb.getClassRepresentation( CommandEvent.class );
            if ( !classRepr.existIndex( COMMAND_EVENT_INDEX ) ) {
                classRepr.addIndexOn( COMMAND_EVENT_INDEX, indices.get( COMMAND_EVENT_INDEX ), true );
            }
        } finally {
            if ( odb != null && !odb.isClosed() ) odb.close();
        }
        odb = getDatabase();
        try {
            classRepr = odb.getClassRepresentation( PlannerMessage.class );
            if ( !classRepr.existIndex( MESSAGE_INDEX ) ) {
                classRepr.addIndexOn( MESSAGE_INDEX, indices.get( MESSAGE_INDEX ), true );
            }
        } finally {
            if ( odb != null && !odb.isClosed() ) odb.close();
        }
    }
*/

    private String getPlanUri() {
        return User.current().getPlan().getUri();
    }


    public void setOdbDir( String odbDir ) {
        this.odbDir = odbDir;
    }
}
