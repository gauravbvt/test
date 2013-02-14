package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.ModelObjectContext;
import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.Plan;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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

    private AbstractChannelsBasicPage page;

    public BreadcrumbsPanel( String id, AbstractChannelsBasicPage page ) {
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
                AjaxLink<String> otherModelObjectContextLink = new AjaxLink<String>( "otherContextLink" ) {
                    @Override
                    public void onClick( AjaxRequestTarget target ) {
                        PageParameters params = null;
                        if ( page.isPlanContext() ) {
                            page.setPlan( (Plan)item.getModelObject() );
                            params = page.makePlanParameters();
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
                item.add( pagePathItem.getLink( "pageItemLink" ) );
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
                item.add( pagePathItem.getLink( "pageItemLink" ) );
            }
        };
        pagePathItems.setVisible( page.getClass() != HomePage.class );
        add( pagePathItems );
    }

    private List<PagePathItem> getPagePathItems() {
        List<PagePathItem> pagePathItems = new ArrayList<PagePathItem>();
        pagePathItems.addAll( page.getIntermediatePagesPathItems() );
        if ( !page.getPageName().isEmpty() )
            pagePathItems.add( new PagePathItem( page.getClass(), page.getPageParameters(), page.getPageName() ) );
        return pagePathItems;
    }

    private List<? extends PagePathItem> getPreContextPagePathItems() {
        return page.getPreContextPagesPathItems();
    }


}
