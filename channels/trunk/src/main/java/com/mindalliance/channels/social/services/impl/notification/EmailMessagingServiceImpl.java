package com.mindalliance.channels.social.services.impl.notification;

import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.social.services.notification.EmailMessagingService;
import com.mindalliance.channels.social.services.notification.Messageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

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
    public boolean sendMessage( Messageable messageable ) {
        List<ChannelsUserInfo> toUsers = getToUsers( messageable );
        ChannelsUserInfo fromUser = getFromUser( messageable );
        String subject = messageable.getSubject( Messageable.Format.TEXT, MAX_SUBJECT_SIZE );
        String content = messageable.getContent( Messageable.Format.TEXT, Integer.MAX_VALUE );
        boolean notificationSent = false;
        for ( ChannelsUserInfo toUser : toUsers ) {
            boolean success = sendEmail( toUser.getEmail(),
                    fromUser == null ? getDefaultFromAddress( getPlan( messageable ) ) : fromUser.getEmail(),
                    subject,
                    content
            );
            notificationSent = notificationSent || success;
        }
        return notificationSent;
    }

    @Override
    public boolean sendReport(
            Plan plan,
            List<ChannelsUserInfo> recipients,
            List<? extends Messageable> messageables ) {
        boolean reported = false;
        if ( !messageables.isEmpty() ) {
            String subject = makeReportSubject( plan.getUri(), messageables );
            String content = makeReportContent( Messageable.Format.TEXT, messageables );  // always as text for now
            for ( ChannelsUserInfo recipient : recipients ) {
                boolean success = sendEmail(
                        recipient.getEmail(),
                        plan.getPlannerSupportCommunity( getDefaultSupportCommunity() ),
                        subject,
                        content );
                reported = reported || success;
            }
        }
        return reported;
    }

    private boolean sendEmail(
            String toAddress,
            String fromAddress,
            String subject,
            String content ) {
        boolean success = false;
        if ( !fromAddress.equals( toAddress ) && ChannelsUtils.isValidEmailAddress( toAddress ) ) {
            try {
                SimpleMailMessage email = new SimpleMailMessage();
                email.setTo( toAddress );
                if ( ChannelsUtils.isValidEmailAddress( fromAddress ) ) {
                    email.setFrom( fromAddress );
                    email.setReplyTo( fromAddress );
                }
                email.setSubject( subject );
                email.setText( content );
                LOG.info( " emailing \"" + subject + "\" to "
                        + toAddress );
                mailSender.send( email );

                success = true;
            } catch ( Exception e ) {
                LOG.warn( "Failed to email " + subject + " to " + toAddress, e );

            }
        }
        return success;
    }

    private String getDefaultFromAddress( Plan plan ) {
        return plan == null
                ? ""
                : plan.getPlannerSupportCommunity( getPlanManager().getDefaultSupportCommunity() );
    }


}
