package com.mindalliance.channels.guide;

import java.io.Serializable;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/8/13
 * Time: 11:14 AM
 */
public class TopicDocument implements Serializable {

    private String title;

    private String url;

    public TopicDocument() {}

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl( String url ) {
        this.url = url;
    }
}
