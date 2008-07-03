package com.mindalliance.channels.playbook.pages.forms.tabs.group;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.AgentSpecificationPanel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.TextArea;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 15, 2008
 * Time: 4:10:00 PM
 */
public class GroupWhoTab extends AbstractFormTab {

    protected TextField nameField;
    protected TextArea descriptionField;
    protected AgentSpecificationPanel agentSpecPanel;

    public GroupWhoTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        nameField = new TextField("name", new RefPropertyModel(getElement(), "name"));
        addInputField(nameField);
        descriptionField = new TextArea("description", new RefPropertyModel(getElement(), "description"));
        addInputField(descriptionField);
        agentSpecPanel = new AgentSpecificationPanel("agentSpec", this, "agentSpec", EDITABLE, feedback);
        addReplaceable(agentSpecPanel);
    }
}
