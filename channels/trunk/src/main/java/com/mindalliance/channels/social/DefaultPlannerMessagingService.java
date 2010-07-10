package com.mindalliance.channels.social;

import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.odb.ODBAccessor;
import com.mindalliance.channels.odb.ODBTransactionFactory;
import org.neodatis.odb.core.query.criteria.Where;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * Default implementation of the planner messaging service.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 2, 2010
 * Time: 3:36:33 PM
 */
public class DefaultPlannerMessagingService implements PlannerMessagingService {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultPlannerMessagingService.class );
    private ODBTransactionFactory databaseFactory;

    public DefaultPlannerMessagingService() {
    }

    public PlannerMessage broadcastMessage( String text ) {
        PlannerMessage message = new PlannerMessage( text );
        addSentMessage( message );
        return message;
    }

    public void setDatabaseFactory( ODBTransactionFactory databaseFactory ) {
        this.databaseFactory = databaseFactory;
    }

    private synchronized void addSentMessage( PlannerMessage message ) {
        getOdb().store( message );
    }

    public void sendMessage( PlannerMessage message ) {
        addSentMessage( message );
    }

    public synchronized Iterator<PlannerMessage> getReceivedMessages() {
        return getOdb().iterate(
                PlannerMessage.class,
                Where.and()
                        .add( Where.equal( "planId", getPlanId() ) )
                        .add(
                        Where.or()
                                .add( Where.equal( "toUsername", getUsername() ) )
                                .add( Where.isNull( "toUsername" ) )
                ),
                ODBAccessor.Ordering.Descendant,
                "date"
        );
    }

    private ODBAccessor getOdb() {
        return databaseFactory.getODBAccessor();
    }


    private long getPlanId() {
        return User.current().getPlan().getId();
    }

    public Iterator<PlannerMessage> getSentMessages() {
        return getOdb().iterate(
                PlannerMessage.class,
                Where.and()
                        .add( Where.equal( "planId", getPlanId() ) )
                        .add( Where.equal( "fromUsername", getUsername() ) ),
                ODBAccessor.Ordering.Descendant,
                "date"
        );
    }

    private String getUsername() {
        return User.current().getUsername();
    }

}
