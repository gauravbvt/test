package com.mindalliance.channels.pages.png;

import com.mindalliance.channels.core.dao.PlanManager;
import com.mindalliance.channels.core.dao.user.ChannelsUserDao;
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
    private ChannelsUserDao userDao;
    private PlanManager planManager;

    public PngReference( Class<? extends ChannelsDynamicImageResource> dynamicPngClass ) {
        super( dynamicPngClass, dynamicPngClass.getSimpleName()  );
        this.dynamicPngClass = dynamicPngClass;
    }

    public PngReference(
            Class<? extends ChannelsDynamicImageResource> dynamicPngClass,
            ChannelsUserDao userDao,
            PlanManager planManager ) {
        super( dynamicPngClass, dynamicPngClass.getSimpleName()  );
        this.dynamicPngClass = dynamicPngClass;
        this.userDao = userDao;
        this.planManager = planManager;
    }


    @Override
    public IResource getResource() {
        try {
            ChannelsDynamicImageResource channelsDynamicImageResource = dynamicPngClass.newInstance();
            channelsDynamicImageResource.setPlanManager( planManager );
            channelsDynamicImageResource.setUserDao( userDao );
            return channelsDynamicImageResource;
        } catch ( Exception e ) {
            throw new RuntimeException( "Failed to generate image" );
        }
    }
}
