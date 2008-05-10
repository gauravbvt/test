package com.mindalliance.channels.playbook.pages.forms.tabs.taskType;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.InformationTemplatePanel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.info.InformationTemplate;
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

    protected ListChoice infoTemplatesChoice;
    protected AjaxButton deleteInfoTemplateButton;
    protected AjaxButton addInfoTemplateButton;

    protected WebMarkupContainer infoTemplateDiv;
    protected InformationTemplate selectedInfoTemplate;

    public TaskTypeInfoNeedsTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        infoTemplatesChoice = new ListChoice("informationTemplates", new Model(),
                                              new RefPropertyModel(getElement(), "informationTemplates"));
        infoTemplatesChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                selectedInfoTemplate = (InformationTemplate)infoTemplatesChoice.getModelObject();
                loadInfoTemplatePanel();
                setInfoTemplatePanelVisibility(target);
                deleteInfoTemplateButton.setEnabled(selectedInfoTemplate != null);
                target.addComponent(deleteInfoTemplateButton);
            }
        });
        addReplaceable(infoTemplatesChoice);
        addInfoTemplateButton = new AjaxButton("addInfoTemplate") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                InformationTemplate infoTemplate = new InformationTemplate();
                RefUtils.add(getElement(), "informationTemplates", infoTemplate);
                target.addComponent(infoTemplatesChoice);
            }
        };
        addReplaceable(addInfoTemplateButton);
        deleteInfoTemplateButton = new AjaxButton("deleteInfoTemplate") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                RefUtils.remove(getElement(), "informationTemplates", selectedInfoTemplate);
                selectedInfoTemplate = null;
                deleteInfoTemplateButton.setEnabled(false);
                loadInfoTemplatePanel();
                setInfoTemplatePanelVisibility(target);
                target.addComponent(deleteInfoTemplateButton);
                target.addComponent(infoTemplatesChoice);
            }
        };
        deleteInfoTemplateButton.setEnabled(false);
        addReplaceable(deleteInfoTemplateButton);

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
            int index = ((List<InformationTemplate>) RefUtils.get(getElement(), "informationTemplates")).indexOf(selectedInfoTemplate);
            InformationTemplatePanel infoTemplatePanel = new InformationTemplatePanel("informationTemplate", this,
                    "informationTemplates[" + index + "]",
                    EDITABLE, feedback);
            infoTemplateDiv.addOrReplace(infoTemplatePanel);
        }
    }

    public void elementChanged(String propPath, AjaxRequestTarget target) {
        super.elementChanged(propPath, target);
        if (propPath.matches(".*informationTemplates.*")) {
            target.addComponent(infoTemplatesChoice);
        }
    }
}
