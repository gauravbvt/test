package com.mindalliance.channels.playbook.pages.forms.tabs.team;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 15, 2008
 * Time: 10:03:49 AM
 */
public class TeamDefinitionTab extends AbstractFormTab {

    protected TextField nameField;
    protected TextArea descriptionField;
    protected DynamicFilterTree resourcesTree;

    public TeamDefinitionTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        nameField= new TextField("name", new RefPropertyModel(getElement(), "name"));
        addInputField(nameField);
        descriptionField = new TextArea("description", new RefPropertyModel(getElement(), "description"));
        addInputField(descriptionField);
        resourcesTree = new DynamicFilterTree("resources", new RefPropertyModel(getElement(), "resources"),
                                              new RefQueryModel(getProject(), new Query("findAllResources"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selected = resourcesTree.getNewSelections();
                RefUtils.set(getElement(), "resources", selected);
            }
        };
        addReplaceable(resourcesTree);
    }
}
