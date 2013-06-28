package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.community.CommunityServiceFactory;
import com.mindalliance.channels.core.community.PlanCommunityManager;
import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.db.services.users.UserRecordService;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 1/25/12
 * Time: 8:45 AM
 */
public class PngReference extends ResourceReference {

    private Class<? extends ChannelsDynamicImageResource> dynamicPngClass;
    private UserRecordService userInfoService;
    private PlanManager planManager;
    private CommunityServiceFactory communityServiceFactory;
    private PlanCommunityManager planCommunityManager;

    public PngReference(
            Class<? extends ChannelsDynamicImageResource> dynamicPngClass,
            UserRecordService userInfoService,
            PlanManager planManager,
            CommunityServiceFactory communityServiceFactory,
            PlanCommunityManager planCommunityManager
            ) {
        super( dynamicPngClass, dynamicPngClass.getSimpleName()  );
        this.dynamicPngClass = dynamicPngClass;
        this.userInfoService = userInfoService;
        this.planManager = planManager;
        this.communityServiceFactory = communityServiceFactory;
        this.planCommunityManager = planCommunityManager;
    }


    @Override
    public IResource getResource() {
        try {
            ChannelsDynamicImageResource channelsDynamicImageResource = dynamicPngClass.newInstance();
            channelsDynamicImageResource.setPlanManager( planManager );
            channelsDynamicImageResource.setUserInfoService( userInfoService );
            channelsDynamicImageResource.setCommunityServiceFactory( communityServiceFactory );
            channelsDynamicImageResource.setPlanCommunityManager( planCommunityManager );
            return channelsDynamicImageResource;
        } catch ( Exception e ) {
            throw new RuntimeException( "Failed to generate image" );
        }
    }
}
