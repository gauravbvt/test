package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 6, 2008
 * Time: 2:59:44 PM
 */
public class ResponsibilityPanel extends AbstractComponentPanel {

    InformationDefinitionPanel infoSpecPanel;
    TaskDefinitionPanel taskSpecPanel;

    public ResponsibilityPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        infoSpecPanel = new InformationDefinitionPanel("informationSpec", this, propPath+".informationSpec", isReadOnly(), feedback);
        addReplaceable(infoSpecPanel);
        taskSpecPanel = new TaskDefinitionPanel("taskSpec", this, propPath+".taskSpec", isReadOnly(), feedback);
        addReplaceable(taskSpecPanel);
    }
}
