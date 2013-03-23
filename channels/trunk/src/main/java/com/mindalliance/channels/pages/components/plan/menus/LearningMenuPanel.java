package com.mindalliance.channels.pages.components.plan.menus;

import com.mindalliance.channels.core.command.CommandException;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.pages.components.menus.LinkMenuItem;
import com.mindalliance.channels.pages.components.menus.MenuPanel;
import com.mindalliance.channels.social.model.Feedback;
import com.mindalliance.channels.social.model.rfi.RFISurvey;
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
public class LearningMenuPanel extends MenuPanel {
    public LearningMenuPanel( String id, IModel<Segment> segmentModel, Set<Long> expansions ) {
        super( id, "Learning", segmentModel, expansions );
    }

    @Override
    public String getHelpTopicId() {
        return "learning-menu";
    }

    @Override
    public List<LinkMenuItem> getMenuItems() throws CommandException {
        List<LinkMenuItem> menuItems = new ArrayList<LinkMenuItem>();
        menuItems.add( collapsible( Feedback.UNKNOWN, "Hide all feedback", "All feedback" ) );
        menuItems.add( collapsible( RFISurvey.UNKNOWN, "Hide all surveys", "All surveys" ) );
        return menuItems;
    }
}
