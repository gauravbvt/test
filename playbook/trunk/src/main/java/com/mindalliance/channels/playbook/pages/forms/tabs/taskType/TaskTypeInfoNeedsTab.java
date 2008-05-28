package com.mindalliance.channels.playbook.pages.forms.tabs.taskType;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.InformationSpecPanel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.spec.InformationSpec;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.Model;
import org.apache.wicket.AttributeModifier;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 8:23:43 PM
 */
public class TaskTypeInfoNeedsTab extends AbstractFormTab {

    protected ListChoice infoSpecsChoice;
    protected AjaxButton deleteInfoSpecButton;
    protected AjaxButton addInfoSpecButton;

    protected WebMarkupContainer infoSpecDiv;
    protected InformationSpec selectedInfoSpec;

    public TaskTypeInfoNeedsTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        infoSpecsChoice = new ListChoice("informationSpecs", new Model(),
                                              new RefPropertyModel(getElement(), "informationSpecs"));
        infoSpecsChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                selectedInfoSpec = (InformationSpec)infoSpecsChoice.getModelObject();
                loadInfoSpecPanel();
                setInfoSpecPanelVisibility(target);
                deleteInfoSpecButton.setEnabled(selectedInfoSpec != null);
                target.addComponent(deleteInfoSpecButton);
            }
        });
        addReplaceable(infoSpecsChoice);
        addInfoSpecButton = new AjaxButton("addInfoSpec") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                InformationSpec infoSpec = new InformationSpec();
                RefUtils.add(getElement(), "informationSpecs", infoSpec);
                target.addComponent(infoSpecsChoice);
            }
        };
        addReplaceable(addInfoSpecButton);
        deleteInfoSpecButton = new AjaxButton("deleteInfoSpec") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                RefUtils.remove(getElement(), "informationSpecs", selectedInfoSpec);
                selectedInfoSpec = null;
                deleteInfoSpecButton.setEnabled(false);
                loadInfoSpecPanel();
                setInfoSpecPanelVisibility(target);
                target.addComponent(deleteInfoSpecButton);
                target.addComponent(infoSpecsChoice);
            }
        };
        deleteInfoSpecButton.setEnabled(false);
        addReplaceable(deleteInfoSpecButton);

        infoSpecDiv = new WebMarkupContainer("infoSpecDiv");
        loadInfoSpecPanel();
        addReplaceable(infoSpecDiv);
        infoSpecDiv.add(new AttributeModifier("style", true, new Model("display:none")));
    }

    private void setInfoSpecPanelVisibility(AjaxRequestTarget target) {
        if (selectedInfoSpec != null) {
            infoSpecDiv.add(new AttributeModifier("style", true, new Model("display:block")));
        } else {
            infoSpecDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        }
        target.addComponent(infoSpecDiv);
    }

    private void loadInfoSpecPanel() {
        if (selectedInfoSpec == null) {
            Label dummyInfoSpecPanel = new Label("informationSpec", "dummy");
            infoSpecDiv.addOrReplace(dummyInfoSpecPanel);
        } else {
            int index = ((List<InformationSpec>) RefUtils.get(getElement(), "informationSpecs")).indexOf(selectedInfoSpec);
            InformationSpecPanel infoSpecPanel = new InformationSpecPanel("informationSpec", this,
                    "informationSpecs[" + index + "]",
                    EDITABLE, feedback);
            infoSpecDiv.addOrReplace(infoSpecPanel);
        }
    }

    public void elementChanged(String propPath, AjaxRequestTarget target) {
        super.elementChanged(propPath, target);
        if (propPath.matches(".*informationSpecs.*")) {
            target.addComponent(infoSpecsChoice);
        }
    }
}
