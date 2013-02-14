package com.mindalliance.channels.pages;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.io.Serializable;

/**
* Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: 2/13/13
* Time: 1:01 PM
*/
public class PagePathItem implements Serializable {

    private Class<? extends AbstractChannelsWebPage> pageClass;
    private PageParameters pageParameters;
    private String name;

    public PagePathItem(
            Class<? extends AbstractChannelsWebPage> pageClass,
            PageParameters pageParameters,
            String name ) {
        this.pageClass = pageClass;
        this.pageParameters = pageParameters;
        this.name = name;
    }

    protected BookmarkablePageLink<String> getLink( String id ) {
        BookmarkablePageLink<String> link = new BookmarkablePageLink<String>( id, pageClass, pageParameters );
        link.add( new Label( "pageName", name ) );
        return link;
    }
}
