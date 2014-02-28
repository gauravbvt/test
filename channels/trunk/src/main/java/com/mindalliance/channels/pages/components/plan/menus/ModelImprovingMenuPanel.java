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
 * Time: 3:06 PM
 */
public class ModelImprovingMenuPanel extends MenuPanel {
    public ModelImprovingMenuPanel( String id, IModel<Segment> segmentModel, Set<Long> expansions ) {
        super( id, "Improving", segmentModel, expansions );
    }

    @Override
    public String getHelpTopicId() {
        return "improving-menu";
    }

    @Override
    public List<LinkMenuItem> getMenuItems() throws CommandException {
        List<LinkMenuItem> menuItems = new ArrayList<LinkMenuItem>();
        menuItems.add( collapsible( Channels.ALL_CHECKLISTS, "Hide all checklists", "All checklists" ) );
        if ( isPlanner() ) menuItems.add( collapsible( Channels.TASK_MOVER, "Hide task mover", "Task mover" ) );
        menuItems.add( collapsible( Channels.CHECKLISTS_MAP, "Hide checklists map", "Checklists map" ) );
        menuItems.add( collapsible( Channels.MODEL_EVALUATION, "Hide model evaluation", "Model evaluation" ) );
        menuItems.add( collapsible( Channels.ALL_ISSUES, "Hide all issues", "All issues" ) );
        menuItems.add( collapsible( Channels.MODEL_VERSIONS, "Hide model versions", "Model versions" ) );
        return menuItems;
    }
}
