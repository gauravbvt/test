package com.mindalliance.channels.pages.components.help;

import java.io.Serializable;

/**
 * A gallery item.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 5/16/13
 * Time: 1:54 PM
 */
public class GalleryItem implements Serializable {

    private String title;
    private String image;
    private String caption;

    public GalleryItem( String image, String caption, String title ) {
        this.image = image;
        this.caption = caption;
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getCaption() {
        return caption;
    }
}
