package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.ModelObjectContext;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Plan;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
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
        addSelectedContextInPath();
        addOtherContextsInPath();
        addPathPageItems();
        addInnerPagePathItems();
    }

     private void addHomeInPath() {
        AjaxLink<String> homeLink = new AjaxLink<String>( "homeLink" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                setResponsePage( HomePage.class, new PageParameters() );
            }
        };
        add( homeLink );
    }

    private void addSelectedContextInPath() {
        Label selectedModelObjectContextName = new Label(
                "selectedContext",
                page.isPlanContext()
                        ? page.getPlan().toString()
                        : page.isCommunityContext()
                        ? page.getPlanCommunity().toString()
                        : "" );
        add( selectedModelObjectContextName );
        selectedModelObjectContextName.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                PageParameters params = null;
                if ( page.isPlanContext() ) {
                    params = page.makePlanParameters();
                    setResponsePage( PlansPage.class, params );
                    if ( page.isInCommunityContext() ) {
                        page.addFromCommunityParameters( params, page.getCommunityInContext() );
                    }
                }
                else if ( page.isCommunityContext() ) {
                    params = page.makeCommunityParameters();
                    setResponsePage( CommunityPage.class, params );
                }
            }
        } );
    }

     private void addOtherContextsInPath() {
        ListView<ModelObjectContext> otherPlansListView = new ListView<ModelObjectContext>(
                "otherContexts",
                page.isPlanContext()
                    ? page.getOtherPlans()
                    : page.isCommunityContext()
                        ? page.getOtherPlanCommunities()
                        : new ArrayList<ModelObjectContext>()
        ) {
            @Override
            protected void populateItem( final ListItem<ModelObjectContext> item ) {
                AjaxLink<String> otherModelObjectContextLink = new AjaxLink<String>( Breadcrumbable.PAGE_ITEM_LINK_ID ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        PageParameters params = null;
                        if ( page.isPlanContext() ) {
                            page.setPlan( (Plan)item.getModelObject() );
                            params = page.makePlanParameters();
                            if ( page.isInCommunityContext() ) {
                                page.addFromCommunityParameters( params, page.getCommunityInContext() );
                            }
                            setResponsePage( PlansPage.class, params );
                        }
                        else if ( page.isCommunityContext() ) {
                            page.setPlanCommunity( (PlanCommunity)item.getModelObject() );
                            params = page.makeCommunityParameters();
                            setResponsePage( CommunitiesPage.class, params );
                        }
                    }
                };
                otherModelObjectContextLink.add( new Label( "otherContextName", item.getModelObject().toString() ) );
                item.add( otherModelObjectContextLink );
            }
        };
        add( otherPlansListView );
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
