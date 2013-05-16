package com.mindalliance.channels.pages.components.help;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A gallery group.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/16/13
 * Time: 1:53 PM
 */
public class GalleryGroup implements Serializable {

    private String name;
    private List<GalleryItem> galleryItems;

    public GalleryGroup( String name ) {
        this.name = name;
        galleryItems = new ArrayList<GalleryItem>();
    }

    public String getName() {
        return name;
    }

    public List<GalleryItem> getGalleryItems() {
        return galleryItems;
    }

    public GalleryGroup add( String image, String caption, String title ) {
        galleryItems.add( new GalleryItem( image, caption, title ) );
        return this;
    }
}
