package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.core.model.Requirement;
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
    public List<LinkMenuItem> getMenuItems() {
        synchronized ( getCommander() ) {
            List<LinkMenuItem> menuItems = new ArrayList<LinkMenuItem>();
            menuItems.add( collapsible( Requirement.UNKNOWN, "Hide plan requirements", "Plan requirements" ) );
            menuItems.add( collapsible( Channels.ALL_EVENTS, "Hide events in scope", "Events in scope" ) );
            menuItems.add( collapsible( Channels.ALL_ORGANIZATIONS, "Hide organizations in scope", "Organizations in scope" ) );
            menuItems.add( collapsible( Channels.ALL_SEGMENTS, "Hide segments map", "Map of all segments" ) );
            menuItems.add( collapsible( Channels.ALL_CLASSIFICATIONS, "Hide classification systems", "Classification systems" ) );
            return menuItems;
        }
    }

}
