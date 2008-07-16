package com.mindalliance.channels.playbook.pages.forms.tabs.informationAct;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.TimingPanel;
import com.mindalliance.channels.playbook.pages.forms.panels.CausePanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 1:24:16 PM
 */
public class InformationActBasicTab extends AbstractFormTab {

    protected TextField nameField;
    protected TextArea descriptionField;
    protected DynamicFilterTree actorAgentTree;

    public InformationActBasicTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        nameField = new TextField("name", new RefPropertyModel(getElement(), "name"));
        addInputField(nameField);
        descriptionField = new TextArea("description", new RefPropertyModel(getElement(), "description"));
        addInputField(descriptionField);
        actorAgentTree = new DynamicFilterTree("actorAgent", new RefPropertyModel(getElement(), "actorAgent"),
                new RefQueryModel(getPlaybook(), new Query("findAllAgents")),
                SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selectedResource = actorAgentTree.getNewSelection();
                RefUtils.set(getElement(), "actorAgent", selectedResource);
            }
        };
        addReplaceable(actorAgentTree);
    }
}
