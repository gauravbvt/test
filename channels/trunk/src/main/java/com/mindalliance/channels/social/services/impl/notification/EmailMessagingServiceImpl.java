package com.mindalliance.channels.social.services.impl.notification;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.db.data.users.UserRecord;
import com.mindalliance.channels.social.services.notification.EmailMessagingService;
import com.mindalliance.channels.social.services.notification.Messageable;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * An email messaging service.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/8/12
 * Time: 11:02 AM
 */
@Component
public class EmailMessagingServiceImpl extends AbstractMessageServiceImpl implements EmailMessagingService {

    /**
     * The logger.
     */
    private final Logger LOG = LoggerFactory.getLogger( EmailMessagingServiceImpl.class );

    @Autowired
    private MailSender mailSender;
    private static final int MAX_SUBJECT_SIZE = 120;

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public List<String> sendMessage(
            Messageable messageable,
            String topic,
            CommunityService communityService ) {
        List<String> successes = new ArrayList<String>();
        List<UserRecord> toUsers = getToUsers( messageable, topic, communityService );
        UserRecord fromUser = getFromUser( messageable, topic );
        String subject = StringUtils.abbreviate(
                messageable.getSubject( topic, Messageable.Format.TEXT, communityService ),
                MAX_SUBJECT_SIZE );
        String content = StringUtils.abbreviate(
                messageable.getContent( topic, Messageable.Format.TEXT, communityService ),
                Integer.MAX_VALUE );
        for ( UserRecord toUser : toUsers ) {
            boolean success = sendEmail( toUser.getEmail(),
                    fromUser == null ? getDefaultFromAddress( getPlan( messageable ) ) : fromUser.getEmail(),
                    subject,
                    content,
                    false
            );
            if ( success ) {
                successes.add( toUser.getUsername() );
            }
        }
        return successes;
    }

    @Override
    public boolean sendReport(
            List<UserRecord> recipients,
            List<? extends Messageable> messageables,
            String topic,
            CommunityService communityService ) {
        boolean reported = false;
        CollaborationModel collaborationModel = communityService.getPlan();
        if ( !messageables.isEmpty() ) {
            String subject = makeReportSubject( collaborationModel.getUri(), messageables, topic, communityService );
            String content = makeReportContent( Messageable.Format.TEXT, messageables, topic, communityService );  // always as text for now
            for ( UserRecord recipient : recipients ) {
                boolean success = sendEmail(
                        recipient.getEmail(),
                        collaborationModel.getPlannerSupportCommunity( getDefaultSupportCommunity() ),
                        subject,
                        content,
                        false );
                reported = reported || success;
            }
        }
        return reported;
    }

    private boolean sendEmail(
            String toAddress,
            String fromAddress,
            String subject,
            String content,
            boolean ccSelf ) {
        if ( fromAddress.equals( toAddress ) ) {
            LOG.warn( "Not sending email about " + subject + " from " + fromAddress + " to self" );
        }
        boolean done = !ChannelsUtils.isValidEmailAddress( toAddress ) || fromAddress.equals( toAddress );
        if ( !done ) { // todo - Bad semantics: Sending to self or to invalid address is considered "already done".
            try {
                SimpleMailMessage email = new SimpleMailMessage();
                email.setTo( toAddress );
                if ( ChannelsUtils.isValidEmailAddress( fromAddress ) ) {
                    email.setFrom( fromAddress );
                    email.setReplyTo( fromAddress );
                }
                if ( ccSelf ) {
                    email.setBcc( fromAddress );
                }
                email.setSubject( subject );
                email.setText( content );
                LOG.info( "Emailing " + subject + " to " + toAddress + " from " + fromAddress );
                mailSender.send( email );

                done = true;
            } catch ( Exception e ) {
                LOG.warn( "Failed to email " + subject + " to " + toAddress, e );

            }
        }
        return done;
    }

    private String getDefaultFromAddress( CollaborationModel collaborationModel ) {
        return collaborationModel == null
                ? ""
                : collaborationModel.getPlannerSupportCommunity( getModelManager().getDefaultSupportCommunity() );
    }


    @Override
    public boolean sendInvitation( ChannelsUser fromUser, String emailAddress, String message ) {
        return sendEmail(
                emailAddress,
                fromUser.getEmail(),
                "Invitation to participate in collaboration planning",
                message,
                true );
    }
}
