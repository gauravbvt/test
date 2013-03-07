package com.mindalliance.channels.pages;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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

    private Class<? extends Page> pageClass;
    private PageParameters pageParameters;
    private String name;
    private AjaxLink namedLink;

    public PagePathItem(  ) {
    }

    public PagePathItem( Class<? extends Page> pageClass,
            PageParameters pageParameters,
            String name ) {
        this.pageClass = pageClass;
        this.pageParameters = pageParameters;
        this.name = name;
    }

    public PagePathItem( AjaxLink namedLink ) {
        this.namedLink = namedLink;
    }

    public Component getLink() {
        return namedLink != null
                ? getInPageLink()
                : pageClass != null
                    ? getPageLink()
                    : makeEmptyLink();
    }

    private BookmarkablePageLink<String> getPageLink() {
        BookmarkablePageLink<String> link = new BookmarkablePageLink<String>(
                Breadcrumbable.PAGE_ITEM_LINK_ID,
                pageClass, pageParameters );
        link.add( new Label( Breadcrumbable.PAGE_ITEM_LINK_NAME, name ) );
        return link;
    }

    private AjaxLink getInPageLink() {
        return namedLink;
    }

    private Component makeEmptyLink() {
        AjaxLink<String> empty = new AjaxLink<String>( Breadcrumbable.PAGE_ITEM_LINK_ID ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                // Do nothing
            }
        };
        empty.add( new Label( Breadcrumbable.PAGE_ITEM_LINK_NAME, "" ) );
        return empty;
    }

    public boolean isEmpty() {
        return namedLink == null && pageClass == null;
    }
}
