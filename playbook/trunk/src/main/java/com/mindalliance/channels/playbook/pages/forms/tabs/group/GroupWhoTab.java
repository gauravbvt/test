package com.mindalliance.channels.playbook.pages.forms.tabs.group;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.playbook.Group;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;

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
    protected ListMultipleChoice kindsChoice;
    protected DynamicFilterTree rolesTree;

    public GroupWhoTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        nameField = new TextField("name", new RefPropertyModel(getElement(), "name"));
        addInputField(nameField);
        descriptionField = new TextArea("description", new RefPropertyModel(getElement(), "description"));
        addInputField(descriptionField);
        kindsChoice = new ListMultipleChoice("kinds", new RefPropertyModel(getElement(), "kinds"), Group.resourceKinds);
        kindsChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                List<String> selected = (List<String>)kindsChoice.getModelObject();
                RefUtils.set(getElement(), "kinds", selected);
            }
        });
        addReplaceable(kindsChoice);
        rolesTree = new DynamicFilterTree("roles", new RefPropertyModel(getElement(), "roles"),
                                           new RefQueryModel(getProject(), new Query("findAllTypes", "Role"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selected = rolesTree.getNewSelections();
                RefUtils.set(getElement(), "roles", selected);
            }
        };
        addReplaceable(rolesTree);
    }
}
