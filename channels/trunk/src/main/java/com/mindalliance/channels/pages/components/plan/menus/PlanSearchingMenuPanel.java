package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.plan.floating.PlanSearchingFloatingPanel;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Plan searching menu panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 3:07 PM
 */
public class PlanSearchingMenuPanel extends MenuPanel {
    public PlanSearchingMenuPanel( String id, IModel<Segment> segmentModel, Set<Long> expansions ) {
        super( id, "Searching", segmentModel, expansions );
    }

    @Override
    public List<LinkMenuItem> getMenuItems() throws CommandException {
        List<LinkMenuItem> menuItems = new ArrayList<LinkMenuItem>();
        menuItems.add( showAspect( Channels.PLAN_SEARCHING, PlanSearchingFloatingPanel.INDEX ) );
        menuItems.add( showAspect( Channels.PLAN_SEARCHING, PlanSearchingFloatingPanel.TAGS ) );
        menuItems.add( showAspect( Channels.PLAN_SEARCHING, PlanSearchingFloatingPanel.TAXONOMIES ) );
        menuItems.add( showAspect( Channels.PLAN_SEARCHING, PlanSearchingFloatingPanel.WHOSWHO ) );
        menuItems.add( showAspect( Channels.PLAN_SEARCHING, PlanSearchingFloatingPanel.DOCUMENTS ) );
        return menuItems;
    }
}
