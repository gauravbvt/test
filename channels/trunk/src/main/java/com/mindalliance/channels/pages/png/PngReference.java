package com.mindalliance.channels.pages.png;

import org.apache.wicket.request.resource.DynamicImageResource;
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

    private Class<? extends DynamicImageResource> dynamicPngClass;

    public PngReference( Class<? extends DynamicImageResource> dynamicPngClass ) {
        super( dynamicPngClass, dynamicPngClass.getSimpleName()  );
        this.dynamicPngClass = dynamicPngClass;
    }

    @Override
    public IResource getResource() {
        try {
            return dynamicPngClass.newInstance();
        } catch ( Exception e ) {
            throw new RuntimeException( "Failed to generate image" );
        }
    }
}
