package com.mindalliance.channels.social.services.impl.notification;

import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.dao.user.ChannelsUserInfo;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.core.query.PlanService;
import com.mindalliance.channels.social.services.SurveysDAO;
import com.mindalliance.channels.social.services.notification.Messageable;
import com.mindalliance.channels.social.services.notification.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/** Abstract messaging service implementation.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/8/12
 * Time: 12:21 PM
 */
abstract public class AbstractMessageServiceImpl implements MessagingService {

    @Autowired
    private PlanManager planManager;

    @Autowired
    private ChannelsUserDao userDao;

    protected List<ChannelsUserInfo> getToUsers( Messageable messageable, String topic ) {
        List<ChannelsUser> toUsers = new ArrayList<ChannelsUser>();
        String toUsername = messageable.getToUsername( topic );
        String urn = messageable.getPlanUri();
        if ( toUsername.equals( ChannelsUserInfo.PLANNERS ) )
            toUsers = userDao.getPlanners( urn );
        else if ( toUsername.equals( ChannelsUserInfo.USERS ) )
            toUsers = userDao.getUsers( urn );
        else
            toUsers.add( userDao.getUserNamed( toUsername ) );
        List<ChannelsUserInfo> answer = new ArrayList<ChannelsUserInfo>(  );
        for ( ChannelsUser toUser : toUsers ) {
            answer.add( toUser.getUserInfo() );
        }
        return answer;
    }

    protected ChannelsUserInfo getFromUser( Messageable messageable, String topic ) {
        String fromUsername = messageable.getFromUsername( topic );
        if ( fromUsername != null ) {
            ChannelsUser fromUser = userDao.getUserNamed( fromUsername );
            return fromUser.getUserInfo();
        } else {
            return null;
        }
    }

    protected Plan getPlan( Messageable messageable ) {
        return planManager.getPlan( messageable.getPlanUri(), messageable.getPlanVersion() );
    }

    protected PlanManager getPlanManager() {
        return planManager;
    }

    protected String getDefaultSupportCommunity() {
        return planManager.getDefaultSupportCommunity();
    }

    protected String makeReportSubject(
            String planUri,
            List<? extends Messageable> messageables,
            String topic,
            PlanService planService,
            SurveysDAO surveysDAO ) {
        StringBuilder sb = new StringBuilder();
        int n = messageables.size();
        String kind = messageables.get( 0 ).getLabel() + " " + topic;
        sb.append( kind )
                .append( " report" )
                .append( " (" )
                .append( n )
                .append( ") for plan " )
                .append( planUri );
        return sb.toString();
    }

    protected String makeReportContent(
            Messageable.Format format,
            List<? extends Messageable> messageables,
            String topic,
            PlanService planService,
            SurveysDAO surveysDAO ) {
        StringBuilder sb = new StringBuilder();
        for ( Messageable messageable : messageables ) {
            sb.append( messageable.getContent( topic, format, planService, surveysDAO ) );
            sb.append( "\n============================================\n\n" );
        }
        return sb.toString();
    }

}
