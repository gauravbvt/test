package com.mindalliance.channels.playbook.pages.forms.tabs.informationAct;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.TimingPanel;
import com.mindalliance.channels.playbook.pages.forms.panels.CausePanel;
import com.mindalliance.channels.playbook.pages.forms.panels.ReferencesPanel;
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

import java.util.List;

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
    protected ReferencesPanel actorsPanel;

    public InformationActBasicTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        nameField = new TextField("name", new RefPropertyModel(getElement(), "name"));
        addInputField(nameField);
        descriptionField = new TextArea("description", new RefPropertyModel(getElement(), "description"));
        addInputField(descriptionField);
        /*actorsTree = new DynamicFilterTree("actors", new RefPropertyModel(getElement(), "actors"),
                new RefQueryModel(getPlaybook(), new Query("findAllAgents"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selectedAgents = actorsTree.getNewSelections();
                setProperty("actors", selectedAgents);
            }
        };*/
        actorsPanel = new ReferencesPanel("actors", this, "actors", new RefQueryModel(getPlaybook(), new Query("findAllAgents"))) ;
        addReplaceable(actorsPanel);
    }
}
