package com.mindalliance.channels.social;

import com.mindalliance.channels.dao.PlanDefinition;
import com.mindalliance.channels.dao.User;
import com.mindalliance.channels.model.ModelObject;
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

    public PlannerMessage broadcastMessage( String text, ModelObject about ) {
        PlannerMessage message = new PlannerMessage( text, about );
        addSentMessage( message );
        return message;
    }

    public PlannerMessage sendMessage( String text, String toUserName ) {
        PlannerMessage message = new PlannerMessage( text );
        message.setToUsername( toUserName );
        addSentMessage( message );
        return message;
    }

    public PlannerMessage sendMessage( String text, ModelObject about, String toUserName ) {
        PlannerMessage message = new PlannerMessage( text, about );
        message.setToUsername( toUserName );
        addSentMessage( message );
        return message;
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
                            .add( visibilityQuery() ) );
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

    private ComposedExpression visibilityQuery() {
        return Where.and()
                .add( Where.equal( "planId", getPlanId() ) )
                .add(
                        Where.or()
                                .add( Where.equal( "toUsername", getUsername() ) )
                                .add( Where.isNull( "toUsername" ) )
                );
    }

    public synchronized Iterator<PlannerMessage> getMessages() {
        ODB odb = null;
        try {
            odb = getOdb();
            Iterator<PlannerMessage> messages;
            IQuery query = new CriteriaQuery(
                    PlannerMessage.class,
                    visibilityQuery() );
            query.orderByDesc( "date" );
            Objects<PlannerMessage> results;
            try {
                results = odb.getObjects( query );
                messages = results.iterator();
            } catch ( Exception e ) {
                LOG.warn( "Failed to query for planning events", e );
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

    public synchronized int getUnreadCount() {
        int count = 0;
        Iterator<PlannerMessage> messages = getMessages();
        while ( messages.hasNext() ) {
            PlannerMessage message = messages.next();
            PlannerMessageStatus status = getMessageStatus( message.getId() );
            if ( !status.isRead() ) count++;
        }
        return count;
    }


    public synchronized void markAsRead( String messageId ) {
        PlannerMessageStatus status = new PlannerMessageStatus( messageId, getUsername() );
        status.setRead( true );
        ODB odb = null;
        try {
            odb = getOdb();
            odb.store( status );
        } finally {
            if ( odb != null && !odb.isClosed() )
                odb.close();
        }
    }

    private String getUsername() {
        return User.current().getUsername();
    }

    public synchronized PlannerMessageStatus getMessageStatus( String messageId ) {
        PlannerMessageStatus status = null;
        ODB odb = null;
        try {
            odb = getOdb();
            PlannerMessage message = getMessage( messageId );
            if ( message != null ) {
                IQuery query = new CriteriaQuery(
                        PlannerMessageStatus.class,
                        Where.and()
                                .add( Where.equal( "messageId", message.getId() ) )
                                .add( Where.equal( "username", getUsername() ) ) );
                Objects<PlannerMessageStatus> results = odb.getObjects( query );
                if ( results.hasNext() ) {
                    status = results.next();
                }
            }
        } finally {
            if ( odb != null && !odb.isClosed() )
                odb.close();
        }
        if ( status == null ) {
            LOG.warn( "Failed to get status of message " + messageId );
        }
        return status;
    }


}
