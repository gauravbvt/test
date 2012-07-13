package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
import org.apache.wicket.request.resource.DynamicImageResource;

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
}
