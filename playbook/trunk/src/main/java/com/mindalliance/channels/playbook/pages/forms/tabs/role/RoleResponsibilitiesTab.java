package com.mindalliance.channels.playbook.pages.forms.tabs.role;

import com.mindalliance.channels.playbook.ifm.Responsibility;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.ResponsibilityPanel;
import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 7:09:26 PM
 */
public class RoleResponsibilitiesTab extends AbstractFormTab {

    protected ListChoice responsibilitiesChoice;
    protected AjaxButton deleteResponsibilityButton;
    protected AjaxButton addResponsibilityButton;

    protected WebMarkupContainer responsibilityDiv;
    protected Responsibility selectedResponsibility;

    public RoleResponsibilitiesTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        responsibilitiesChoice = new ListChoice("responsibilities", new Model(),
                                              new RefPropertyModel(getElement(), "responsibilities"));
        responsibilitiesChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                selectedResponsibility = (Responsibility)responsibilitiesChoice.getModelObject();
                loadResponsibilityPanel();
                setResponsibilityPanelVisibility(target);
                deleteResponsibilityButton.setEnabled(selectedResponsibility != null);
                target.addComponent(deleteResponsibilityButton);
            }
        });
        addReplaceable(responsibilitiesChoice);
        addResponsibilityButton = new AjaxButton("addResponsibility") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                Responsibility responsibility = new Responsibility();
                RefUtils.add(getElement(), "responsibilities", responsibility);
                target.addComponent(responsibilitiesChoice);
            }
        };
        addReplaceable(addResponsibilityButton);
        deleteResponsibilityButton = new AjaxButton("deleteResponsibility") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                RefUtils.remove(getElement(), "responsibilities", selectedResponsibility);
                selectedResponsibility = null;
                deleteResponsibilityButton.setEnabled(false);
                loadResponsibilityPanel();
                setResponsibilityPanelVisibility(target);
                target.addComponent(deleteResponsibilityButton);
                target.addComponent(responsibilitiesChoice);
            }
        };
        deleteResponsibilityButton.setEnabled(false);
        addReplaceable(deleteResponsibilityButton);

        responsibilityDiv = new WebMarkupContainer("responsibilityDiv");
        loadResponsibilityPanel();
        addReplaceable(responsibilityDiv);
        responsibilityDiv.add(new AttributeModifier("style", true, new Model("display:none")));
    }

    private void setResponsibilityPanelVisibility(AjaxRequestTarget target) {
        if (selectedResponsibility != null) {
            responsibilityDiv.add(new AttributeModifier("style", true, new Model("display:block")));
        } else {
            responsibilityDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        }
        target.addComponent(responsibilityDiv);
    }

    private void loadResponsibilityPanel() {
        if (selectedResponsibility == null) {
            Label dummyResponsibilityPanel = new Label("responsibility", "dummy");
            responsibilityDiv.addOrReplace(dummyResponsibilityPanel);
        } else {
            int index = ((List<Responsibility>) RefUtils.get(getElement(), "responsibilities")).indexOf(selectedResponsibility);
            ResponsibilityPanel responsibilityPanel = new ResponsibilityPanel("responsibility", this,
                    "responsibilities[" + index + "]",
                    EDITABLE, feedback);
            responsibilityDiv.addOrReplace(responsibilityPanel);
        }
    }

    public void elementChanged(String propPath, AjaxRequestTarget target) {
        super.elementChanged(propPath, target);
        if (propPath.matches(".*responsibilities.*")) {
            target.addComponent(responsibilitiesChoice);
        }
    }

}
