package com.mindalliance.channels.pages;

import com.mindalliance.channels.core.community.PlanCommunity;
import com.mindalliance.channels.core.model.CollaborationModel;
import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;

/**
 * That which can be navigated via breadcrumbs.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/7/13
 * Time: 9:52 AM
 */
public interface Breadcrumbable {

    public static final String PAGE_ITEM_LINK_ID = "pageItemLink";
    public static final String PAGE_ITEM_LINK_NAME = "pageItemName";

    boolean isPlanContext();

    CollaborationModel getCollaborationModel();

    boolean isCommunityContext();

    PlanCommunity getPlanCommunity();

    PageParameters makePlanParameters();

    PageParameters makePlanParameters( CollaborationModel collaborationModel );

    boolean isInCommunityContext();

    PlanCommunity getCommunityInContext();

    PageParameters addFromCommunityParameters( PageParameters params, PlanCommunity communityInContext );

    PageParameters makeCommunityParameters();

    PageParameters makeCommunityParameters( PlanCommunity planCommunity );

    List<CollaborationModel> getOtherPlans();

    List<PlanCommunity> getOtherPlanCommunities();

    void setCollaborationModel( CollaborationModel collaborationModel );

    void setPlanCommunity( PlanCommunity planCommunity );

    List<PagePathItem> getIntermediatePagesPathItems();

    String getPageName();

    PageParameters getPageParameters();

    List<PagePathItem> getPreContextPagesPathItems();

    Class<? extends Page> getWebPageClass();

    boolean hasInnerPagePathItems();

    PagePathItem getSelectedInnerPagePathItem();

    List<PagePathItem> getOtherInnerPagePathItems();

    PagePathItem getCurrentContextPagePathItem();

    List<PagePathItem> getOtherContextsPagePathItems();
}
