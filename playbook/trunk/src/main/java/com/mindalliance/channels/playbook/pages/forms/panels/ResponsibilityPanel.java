package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.CheckBox;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 6, 2008
 * Time: 2:59:44 PM
 */
public class ResponsibilityPanel extends AbstractComponentPanel {

    protected InformationDefinitionPanel infoSpecPanel;
    protected CheckBox negatedField;
    protected TaskDefinitionPanel taskSpecPanel;

    public ResponsibilityPanel(String id, AbstractPlaybookPanel parentPanel, String propPath) {
        super(id, parentPanel, propPath);
    }

    protected void load() {
        super.load();
        infoSpecPanel = new InformationDefinitionPanel("informationSpec", this, propPath+".informationSpec");
        addReplaceable(infoSpecPanel);
        negatedField = new CheckBox("negated", new RefPropertyModel(getElement(), propPath+".negated"));
        addInputField(negatedField);
        taskSpecPanel = new TaskDefinitionPanel("taskSpec", this, propPath+".taskSpec");
        addReplaceable(taskSpecPanel);
    }
}
