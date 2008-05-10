package com.mindalliance.channels.playbook.pages.forms.tabs.task;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.InformationTemplatePanel;
import com.mindalliance.channels.playbook.ifm.info.InformationTemplate;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.AttributeModifier;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 9:26:02 PM
 */
public class TaskInfoNeedsTab  extends AbstractFormTab {

    protected ListChoice infoTemplatesChoice;
    protected AjaxButton deleteInformationNeedButton;
    protected AjaxButton addInformationNeedButton;

    protected WebMarkupContainer infoTemplateDiv;
    protected InformationTemplate selectedInfoTemplate;

    public TaskInfoNeedsTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        infoTemplatesChoice = new ListChoice("informationNeeds", new Model(),
                                              new RefPropertyModel(getElement(), "informationNeeds"));
        infoTemplatesChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                selectedInfoTemplate = (InformationTemplate)infoTemplatesChoice.getModelObject();
                loadInfoTemplatePanel();
                setInfoTemplatePanelVisibility(target);
                deleteInformationNeedButton.setEnabled(selectedInfoTemplate != null);
                target.addComponent(deleteInformationNeedButton);
            }
        });
        addReplaceable(infoTemplatesChoice);
        addInformationNeedButton = new AjaxButton("addInformationNeed") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                InformationTemplate infoTemplate = new InformationTemplate();
                RefUtils.add(getElement(), "informationNeeds", infoTemplate);
                target.addComponent(infoTemplatesChoice);
            }
        };
        addReplaceable(addInformationNeedButton);
        deleteInformationNeedButton = new AjaxButton("deleteInformationNeed") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                RefUtils.remove(getElement(), "informationNeeds", selectedInfoTemplate);
                selectedInfoTemplate = null;
                deleteInformationNeedButton.setEnabled(false);
                loadInfoTemplatePanel();
                setInfoTemplatePanelVisibility(target);
                target.addComponent(deleteInformationNeedButton);
                target.addComponent(infoTemplatesChoice);
            }
        };
        deleteInformationNeedButton.setEnabled(false);
        addReplaceable(deleteInformationNeedButton);

        infoTemplateDiv = new WebMarkupContainer("infoTemplateDiv");
        loadInfoTemplatePanel();
        addReplaceable(infoTemplateDiv);
        infoTemplateDiv.add(new AttributeModifier("style", true, new Model("display:none")));
    }

    private void setInfoTemplatePanelVisibility(AjaxRequestTarget target) {
        if (selectedInfoTemplate != null) {
            infoTemplateDiv.add(new AttributeModifier("style", true, new Model("display:block")));
        } else {
            infoTemplateDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        }
        target.addComponent(infoTemplateDiv);
    }

    private void loadInfoTemplatePanel() {
        if (selectedInfoTemplate == null) {
            Label dummyInfoTemplatePanel = new Label("informationTemplate", "dummy");
            infoTemplateDiv.addOrReplace(dummyInfoTemplatePanel);
        } else {
            int index = ((List<InformationTemplate>) RefUtils.get(getElement(), "informationNeeds")).indexOf(selectedInfoTemplate);
            InformationTemplatePanel infoTemplatePanel = new InformationTemplatePanel("informationTemplate", this,
                    "informationNeeds[" + index + "]",
                    EDITABLE, feedback);
            infoTemplateDiv.addOrReplace(infoTemplatePanel);
        }
    }

    public void elementChanged(String propPath, AjaxRequestTarget target) {
        super.elementChanged(propPath, target);
        if (propPath.matches(".*informationNeeds.*")) {
            target.addComponent(infoTemplatesChoice);
        }
    }

}
