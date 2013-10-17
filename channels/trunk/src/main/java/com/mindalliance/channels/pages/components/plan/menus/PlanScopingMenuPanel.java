package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Plan scoping menu panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 7/4/12
 * Time: 3:04 PM
 */
public class PlanScopingMenuPanel extends MenuPanel {
    public PlanScopingMenuPanel( String id, IModel<Segment> segmentModel, Set<Long> expansions ) {
        super( id, "Scoping", segmentModel, expansions );
    }

    @Override
    public String getHelpTopicId() {
        return "scoping-menu";
    }

    @Override
    public List<LinkMenuItem> getMenuItems() {
        synchronized ( getCommander() ) {
            List<LinkMenuItem> menuItems = new ArrayList<LinkMenuItem>();
            menuItems.add( collapsible( Channels.ALL_EVENTS, "Hide all events and phases", "All events and phases" ) );
            menuItems.add( collapsible( Channels.ALL_GOALS, "Hide all goals", "All goals" ) );
            menuItems.add( collapsible( Channels.ALL_INVOLVEMENTS, "Hide all involvements", "All involvements" ) );
            menuItems.add( collapsible( Channels.ALL_CLASSIFICATIONS, "Hide classification systems", "Classification systems" ) );
            return menuItems;
        }
    }

}
