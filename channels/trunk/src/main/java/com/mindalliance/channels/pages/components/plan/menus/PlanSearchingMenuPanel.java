package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
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
        menuItems.add( collapsible( Channels.PLAN_INDEX, "Hide index", "Index" ) );
        menuItems.add( collapsible( Channels.ALL_TAGS, "Hide all tags", "All tags" ) );
        menuItems.add( collapsible( Channels.ALL_TYPES, "Hide taxonomies", "Taxonomies" ) );
        menuItems.add( collapsible( Channels.WHOS_WHO, "Hide who's who", "Who's who" ) );
        menuItems.add( collapsible( Channels.BIBLIOGRAPHY, "Hide all documents", "All documents" ) );
        return menuItems;
    }
}
