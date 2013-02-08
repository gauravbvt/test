package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.CommunityServiceFactory;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.Channels;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Channels Dynamic Image Resource.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/12/12
 * Time: 9:43 PM
 */
public abstract class ChannelsDynamicImageResource extends DynamicImageResource {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( ChannelsDynamicImageResource.class );


    private ChannelsUserDao userDao;
    private PlanManager planManager;
    private CommunityServiceFactory communityServiceFactory;
    private PlanCommunityManager planCommunityManager;


    public ChannelsDynamicImageResource() {
    }

    protected ChannelsDynamicImageResource( String format ) {
        super( format );
    }

    public PlanManager getPlanManager() {
        return planManager;
    }

    public void setPlanManager( PlanManager planManager ) {
        this.planManager = planManager;
    }

    public ChannelsUserDao getUserDao() {
        return userDao;
    }

    public void setUserDao( ChannelsUserDao userDao ) {
        this.userDao = userDao;
    }

    public CommunityServiceFactory getCommunityServiceFactory() {
        return communityServiceFactory;
    }

    public void setCommunityServiceFactory( CommunityServiceFactory communityServiceFactory ) {
        this.communityServiceFactory = communityServiceFactory;
    }

    public PlanCommunityManager getPlanCommunityManager() {
        return planCommunityManager;
    }

    public void setPlanCommunityManager( PlanCommunityManager planCommunityManager ) {
        this.planCommunityManager = planCommunityManager;
    }

    protected File getFile( String fileName, PageParameters parameters ) {
        AttachmentManager attachmentManager = ( (Channels) Channels.get() ).getAttachmentManager();
        return new File( attachmentManager.getUploadDirectory( getPlan( parameters ) ), fileName );
    }

    private Plan getPlan( PageParameters parameters ) {
        Plan plan;
        if ( parameters.getNamedKeys().contains( AbstractChannelsWebPage.PLAN_PARM ) ) {
            ChannelsUser user = ChannelsUser.current( getUserDao() );
            plan = AbstractChannelsWebPage.getPlanFromParameters( getPlanManager(), user, parameters );
            if ( !user.isParticipant( plan.getUri() ) )
                return null;
        } else {
            plan = ChannelsUser.plan();
        }
        return plan;
    }

    protected CommunityService getCommunityService( PageParameters parameters ) {
        return communityServiceFactory.getService( getPlanCommunity( parameters ) );
    }

    protected PlanCommunity getPlanCommunity(PageParameters parameters ) {
        return planCommunityManager.getPlanCommunity( getPlanCommunityUri( parameters ) );
    }

    public String getPlanCommunityUri( PageParameters parameters ) {
        String communityUri = null;
        if ( parameters.getNamedKeys().contains( AbstractChannelsWebPage.COMMUNITY_PARM ) ) {
            try {
                communityUri = URLDecoder.decode( parameters.get( AbstractChannelsWebPage.COMMUNITY_PARM ).toString(), "UTF-8" );
            } catch ( UnsupportedEncodingException e ) {
                LOG.error( "Failed to decode community uri", e );
            }
        }
        if ( communityUri == null ) {
            communityUri = ChannelsUser.current().getPlanCommunityUri();
        }
        return communityUri;
    }


}
