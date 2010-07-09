package com.mindalliance.channels.social;

import com.mindalliance.channels.dao.PlanDefinition;
import com.mindalliance.channels.dao.User;
import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.ComposedExpression;
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
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

    private String odbDir;


    public DefaultPlannerMessagingService() {
    }

    public void setOdbDir( String odbDir ) {
        this.odbDir = odbDir;
    }

    public PlannerMessage broadcastMessage( String text ) {
        PlannerMessage message = new PlannerMessage( text );
        addSentMessage( message );
        return message;
    }

    private synchronized void addSentMessage( PlannerMessage message ) {
        ODB odb = null;
        try {
            odb = getOdb();
            odb.store( message );
        } finally {
            if ( odb != null && !odb.isClosed() )
                odb.close();
        }
    }

    public void sendMessage( PlannerMessage message ) {
        addSentMessage( message );
    }

    public synchronized PlannerMessage getMessage( String messageId ) {
        ODB odb = null;
        try {
            odb = getOdb();
            Iterator<PlannerMessage> messages;
            IQuery query = new CriteriaQuery(
                    PlannerMessage.class,
                    Where.and()
                            .add( Where.equal( "messageId", messageId ) )
                            .add( receivedQuery() ) );
            Objects<PlannerMessage> results;
            try {
                results = odb.getObjects( query );
                messages = results.iterator();
            } catch ( Exception e ) {
                LOG.warn( "Failed to query for planning events", e );
                messages = new ArrayList<PlannerMessage>().iterator();
            }
            if ( messages.hasNext() )
                return messages.next();
            else
                return null;
        } finally {
            if ( odb != null && !odb.isClosed() )
                odb.close();
        }
    }

    private ComposedExpression receivedQuery() {
        return Where.and()
                .add( Where.equal( "planId", getPlanId() ) )
                .add(
                        Where.or()
                                .add( Where.equal( "toUsername", getUsername() ) )
                                .add( Where.isNull( "toUsername" ) )
                );
    }

    private ComposedExpression sentQuery() {
        return Where.and()
                .add( Where.equal( "planId", getPlanId() ) )
                .add( Where.equal( "fromUsername", getUsername() ) );
    }

    public synchronized Iterator<PlannerMessage> getReceivedMessages() {
        ODB odb = null;
        try {
            odb = getOdb();
            Iterator<PlannerMessage> messages;
            IQuery query = new CriteriaQuery(
                    PlannerMessage.class,
                    receivedQuery() );
            query.orderByDesc( "date" );
            Objects<PlannerMessage> results;
            try {
                results = odb.getObjects( query );
                messages = results.iterator();
            } catch ( Exception e ) {
                LOG.warn( "Failed to query for received messages", e );
                messages = new ArrayList<PlannerMessage>().iterator();
            }
            return messages;
        } finally {
            if ( odb != null && !odb.isClosed() )
                odb.close();
        }
    }

    private ODB getOdb() {
        return ODBFactory.open( odbDir
                + File.separator
                + PlanDefinition.sanitize( getPlanUri() )
                + File.separator
                + "db" );
    }

    private String getPlanUri() {
        return User.current().getPlan().getUri();
    }

    private long getPlanId() {
        return User.current().getPlan().getId();
    }

    public synchronized void deleteMessage( String messageId ) {
        PlannerMessage message = getMessage( messageId );
        if ( message != null ) {
            ODB odb = null;
            try {
                odb = getOdb();
                odb.delete( message );
            } finally {
                if ( odb != null && !odb.isClosed() )
                    odb.close();
            }
        } else {
            LOG.warn( "Failed to delete planner message " + messageId );
        }
    }

    public Iterator<PlannerMessage> getSentMessages() {
        ODB odb = null;
        try {
            odb = getOdb();
            Iterator<PlannerMessage> messages;
            IQuery query = new CriteriaQuery(
                    PlannerMessage.class,
                    sentQuery() );
            query.orderByDesc( "date" );
            Objects<PlannerMessage> results;
            try {
                results = odb.getObjects( query );
                messages = results.iterator();
            } catch ( Exception e ) {
                LOG.warn( "Failed to query for sent messages", e );
                messages = new ArrayList<PlannerMessage>().iterator();
            }
            return messages;
        } finally {
            if ( odb != null && !odb.isClosed() )
                odb.close();
        }
    }

    private String getUsername() {
        return User.current().getUsername();
    }

}
