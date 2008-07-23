package com.mindalliance.channels.playbook.pages.forms.tabs.taskType;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.InformationDefinitionPanel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.definition.InformationDefinition;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 8:23:43 PM
 */
public class TaskTypeInputsTab extends AbstractFormTab {

    protected ListChoice infoSpecsChoice;
    protected AjaxButton deleteInfoSpecButton;
    protected AjaxButton addInfoSpecButton;

    protected WebMarkupContainer infoSpecDiv;
    protected InformationDefinition selectedInfoSpec;

    public TaskTypeInputsTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        infoSpecsChoice = new ListChoice("inputs", new Model(selectedInfoSpec),
                                          new RefPropertyModel(getElement(), "inputs"),
                                          new ChoiceRenderer("summary"));
        infoSpecsChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                selectedInfoSpec = (InformationDefinition)infoSpecsChoice.getModelObject();
                setVisibility(infoSpecDiv, selectedInfoSpec != null, target);
                enable(deleteInfoSpecButton, selectedInfoSpec != null, target);
            }
        });
        addReplaceable(infoSpecsChoice);
        addInfoSpecButton = new AjaxButton("addInfoSpec") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                selectedInfoSpec = new InformationDefinition();
                RefUtils.add(getElement(), "inputs", selectedInfoSpec);
                infoSpecsChoice.setModelObject(selectedInfoSpec);
                target.addComponent(infoSpecsChoice);
                loadInfoSpecPanel();
                setVisibility(infoSpecDiv, true, target);
            }
        };
        addReplaceable(addInfoSpecButton);
        deleteInfoSpecButton = new AjaxButton("deleteInfoSpec") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                RefUtils.remove(getElement(), "inputs", selectedInfoSpec);
                selectedInfoSpec = null;
                enable(deleteInfoSpecButton, false, target);
                loadInfoSpecPanel();
                setVisibility(infoSpecDiv, selectedInfoSpec != null, target);
                target.addComponent(infoSpecsChoice);
            }
        };
        deleteInfoSpecButton.setEnabled(false);
        addReplaceable(deleteInfoSpecButton);

        infoSpecDiv = new WebMarkupContainer("infoSpecDiv");
        loadInfoSpecPanel();
        addReplaceable(infoSpecDiv);
        hide(infoSpecDiv);
    }


    private void loadInfoSpecPanel() {
        if (selectedInfoSpec == null) {
            Label dummyInfoSpecPanel = new Label("informationSpec", "dummy");
            infoSpecDiv.addOrReplace(dummyInfoSpecPanel);
        } else {
            int index = ((List<InformationDefinition>) getProperty("inputs")).indexOf(selectedInfoSpec);
            InformationDefinitionPanel infoSpecPanel = new InformationDefinitionPanel("informationSpec", this,
                    "inputs[" + index + "]");
            infoSpecDiv.addOrReplace(infoSpecPanel);
        }
    }

    public void elementChanged(String propPath, AjaxRequestTarget target) {
        super.elementChanged(propPath, target);
        if (propPath.matches(".*inputs.*")) {
            target.addComponent(infoSpecsChoice);
        }
    }
}
