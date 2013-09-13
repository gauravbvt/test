package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.community.CommunityServiceFactory;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.db.services.users.UserRecordService;
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


    private UserRecordService userRecordService;
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

    public UserRecordService getUserRecordService() {
        return userRecordService;
    }

    public void setUserRecordService( UserRecordService userRecordService ) {
        this.userRecordService = userRecordService;
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
        return new File( attachmentManager.getUploadDirectory( getCommunityService( parameters ) ), fileName );
    }

    protected CommunityService getCommunityService() {
        return communityServiceFactory.getService( getPlanCommunity( ) );
    }

    private PlanCommunity getPlanCommunity() {
        ChannelsUser user = ChannelsUser.current( getUserRecordService() );
        if ( user.getPlan() != null ) {
            return planCommunityManager.getDomainPlanCommunity( user.getPlan() );
        } else {
            return planCommunityManager.getPlanCommunity( user.getPlanCommunityUri() );
        }
    }

    protected CommunityService getCommunityService( PageParameters parameters ) {
        return communityServiceFactory.getService( getPlanCommunity( parameters ) );
    }

    public PlanCommunity getPlanCommunity( PageParameters parameters ) {
        ChannelsUser user = ChannelsUser.current( getUserRecordService() );
        if ( parameters.getNamedKeys().contains( AbstractChannelsWebPage.COLLAB_PLAN_PARM ) ) {
            try {
                String communityUri = URLDecoder.decode( parameters.get( AbstractChannelsWebPage.COLLAB_PLAN_PARM ).toString(), "UTF-8" );
                PlanCommunity planCommunity = planCommunityManager.getPlanCommunity( communityUri );
                if ( planCommunity != null ) {
                    return planCommunity;
                } else {
                    return null;
                }
            } catch ( UnsupportedEncodingException e ) {
                LOG.error( "Failed to decode community uri", e );
                return null;
            }
        } else if ( parameters.getNamedKeys().contains( AbstractChannelsWebPage.TEMPLATE_PARM ) ) {
            try {
                String planUri = URLDecoder.decode( parameters.get( AbstractChannelsWebPage.TEMPLATE_PARM ).toString(), "UTF-8" );
                int planVersion = parameters.get( AbstractChannelsWebPage.VERSION_PARM ).toInt();
                Plan plan = planManager.getPlan( planUri, planVersion );
                if ( !user.isParticipant( planUri ) )
                    return null;
                else
                    return planCommunityManager.getDomainPlanCommunity( plan );
            } catch ( UnsupportedEncodingException e ) {
                LOG.error( "Failed to decode community uri", e );
                return null;
            }
        }
        return null;
    }


}
