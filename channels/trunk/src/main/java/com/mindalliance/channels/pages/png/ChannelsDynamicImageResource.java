package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.AttachmentManager;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUser;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import com.mindalliance.channels.core.model.Plan;
import com.mindalliance.channels.pages.AbstractChannelsWebPage;
import com.mindalliance.channels.pages.Channels;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;

import java.io.File;

/**
 * Channels Dynamic Image Resource.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/12/12
 * Time: 9:43 PM
 */
public abstract class ChannelsDynamicImageResource extends DynamicImageResource {

    private ChannelsUserDao userDao;
    private PlanManager planManager;

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

}
