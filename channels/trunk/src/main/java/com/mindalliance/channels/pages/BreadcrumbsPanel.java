package com.mindalliance.channels.pages;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Breadcrumbs panel.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 2/12/13
 * Time: 4:27 PM
 */
public class BreadcrumbsPanel extends Panel {

    private Breadcrumbable page;

    public BreadcrumbsPanel( String id, Breadcrumbable page ) {
        super( id );
        this.page = page;
        init();
    }

    private void init() {
        addHomeInPath();
        addPreContextItemsInPath();
        addCurrentContext();
        addPathPageItems();
        addInnerPagePathItems();
    }

    private void addHomeInPath() {
        BookmarkablePageLink<String> homeLink = new BookmarkablePageLink<String>(
                "homeLink",
                HomePage.class,
                new PageParameters()  );
       /* AjaxLink<String> homeLink = new AjaxLink<String>( "homeLink" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                setResponsePage( HomePage.class, new PageParameters() );
            }
        };*/
        add( homeLink );
    }

    private void addCurrentContext() {
        WebMarkupContainer currentContextContainer = new WebMarkupContainer( "currentContext" );
        add( currentContextContainer );
        addSelectedContextInPath( currentContextContainer );
        addOtherContextsInPath( currentContextContainer );
    }

    private void addSelectedContextInPath( WebMarkupContainer currentContextContainer ) {
         currentContextContainer.add( page.getCurrentContextPagePathItem().getLink() );
    }

     private void addOtherContextsInPath( WebMarkupContainer currentContextContainer ) {
         ListView<PagePathItem> otherContextsListView = new ListView<PagePathItem>(
                 "otherContexts",
                 page.getOtherContextsPagePathItems()
         ) {
             @Override
             protected void populateItem( final ListItem<PagePathItem> item ) {
                 item.add( item.getModelObject().getLink() );
             }
         };
         currentContextContainer.add( otherContextsListView );
    }


    private void addPreContextItemsInPath() {
        ListView<PagePathItem> pagePathItems = new ListView<PagePathItem>(
                "preContextItems",
                getPreContextPagePathItems()
        ) {
            @Override
            protected void populateItem( ListItem<PagePathItem> item ) {
                PagePathItem pagePathItem = item.getModelObject();
                item.add( pagePathItem.getLink() );
            }
        };
        pagePathItems.setVisible( page.getClass() != HomePage.class );
        add( pagePathItems );
    }

    private void addPathPageItems() {
        ListView<PagePathItem> pagePathItems = new ListView<PagePathItem>(
                "pageItems",
                getPagePathItems()
        ) {
            @Override
            protected void populateItem( ListItem<PagePathItem> item ) {
                PagePathItem pagePathItem = item.getModelObject();
                item.add( pagePathItem.getLink() );
            }
        };
        pagePathItems.setVisible( page.getClass() != HomePage.class );
        add( pagePathItems );
    }

    private List<PagePathItem> getPagePathItems() {
        List<PagePathItem> pagePathItems = new ArrayList<PagePathItem>();
        pagePathItems.addAll( page.getIntermediatePagesPathItems() );
        if ( !page.getPageName().isEmpty() )
            pagePathItems.add( new PagePathItem( page.getWebPageClass(), page.getPageParameters(), page.getPageName() ) );
        return pagePathItems;
    }

    private void addInnerPagePathItems() {
        WebMarkupContainer innerPagePathItemsContainer = new WebMarkupContainer( "innerPageItems" );
        innerPagePathItemsContainer.setVisible( page.hasInnerPagePathItems() );
        addSelectedInnerPageItem( innerPagePathItemsContainer );
        addOtherInnerPageItems( innerPagePathItemsContainer );
        add( innerPagePathItemsContainer );
    }

    private void addSelectedInnerPageItem( WebMarkupContainer innerPagePathItemsContainer ) {
        PagePathItem pagePathItem = page.getSelectedInnerPagePathItem();
        innerPagePathItemsContainer.add(  pagePathItem.getLink() );
    }

    private void addOtherInnerPageItems( WebMarkupContainer innerPagePathItemsContainer ) {
        ListView<PagePathItem> otherInnersListView = new ListView<PagePathItem>(
                "otherInnerPageItems",
                page.getOtherInnerPagePathItems()
        ) {
            @Override
            protected void populateItem( final ListItem<PagePathItem> item ) {
                item.add( item.getModelObject().getLink() );
            }
        };
        innerPagePathItemsContainer.add( otherInnersListView );
    }

    private List<? extends PagePathItem> getPreContextPagePathItems() {
        return page.getPreContextPagesPathItems();
    }


}
