package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.pages.components.plan.floating.ModelSearchingFloatingPanel;
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
public class ModelSearchingMenuPanel extends MenuPanel {
    public ModelSearchingMenuPanel( String id, IModel<Segment> segmentModel, Set<Long> expansions ) {
        super( id, "Searching", segmentModel, expansions );
    }

    @Override
    public String getHelpTopicId() {
        return "searching-menu";
    }

    @Override
    public List<LinkMenuItem> getMenuItems() throws CommandException {
        List<LinkMenuItem> menuItems = new ArrayList<LinkMenuItem>();
        menuItems.add( showAspect( Channels.MODEL_SEARCHING, ModelSearchingFloatingPanel.INDEX ) );
        menuItems.add( showAspect( Channels.MODEL_SEARCHING, ModelSearchingFloatingPanel.TAGS ) );
        menuItems.add( showAspect( Channels.MODEL_SEARCHING, ModelSearchingFloatingPanel.TAXONOMIES ) );
        menuItems.add( showAspect( Channels.MODEL_SEARCHING, ModelSearchingFloatingPanel.WHOSWHO ) );
        menuItems.add( showAspect( Channels.MODEL_SEARCHING, ModelSearchingFloatingPanel.ATTACHMENTS ) );
        menuItems.add( showAspect( Channels.MODEL_SEARCHING, ModelSearchingFloatingPanel.ASSETS ) );
        return menuItems;
    }
}
