package com.mindalliance.channels.playbook.pages.forms.tabs.task;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.InformationNeedPanel;
import com.mindalliance.channels.playbook.ifm.info.InformationNeed;
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

    protected ListChoice infoNeedsChoice;
    protected AjaxButton deleteInformationNeedButton;
    protected AjaxButton addInformationNeedButton;

    protected WebMarkupContainer infoNeedDiv;
    protected InformationNeed selectedInfoNeed;

    public TaskInfoNeedsTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        infoNeedsChoice = new ListChoice("informationNeeds", new Model(),
                                              new RefPropertyModel(getElement(), "informationNeeds"));
        infoNeedsChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                selectedInfoNeed = (InformationNeed) infoNeedsChoice.getModelObject();
                loadInfoTemplatePanel();
                setInfoNeedPanelVisibility(target);
                deleteInformationNeedButton.setEnabled(selectedInfoNeed != null);
                target.addComponent(deleteInformationNeedButton);
            }
        });
        addReplaceable(infoNeedsChoice);
        addInformationNeedButton = new AjaxButton("addInformationNeed") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                InformationNeed infoNeed = new InformationNeed();
                RefUtils.add(getElement(), "informationNeeds", infoNeed);
                target.addComponent(infoNeedsChoice);
            }
        };
        addReplaceable(addInformationNeedButton);
        deleteInformationNeedButton = new AjaxButton("deleteInformationNeed") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                RefUtils.remove(getElement(), "informationNeeds", selectedInfoNeed);
                selectedInfoNeed = null;
                deleteInformationNeedButton.setEnabled(false);
                loadInfoTemplatePanel();
                setInfoNeedPanelVisibility(target);
                target.addComponent(deleteInformationNeedButton);
                target.addComponent(infoNeedsChoice);
            }
        };
        deleteInformationNeedButton.setEnabled(false);
        addReplaceable(deleteInformationNeedButton);

        infoNeedDiv = new WebMarkupContainer("infoNeedDiv");
        loadInfoTemplatePanel();
        addReplaceable(infoNeedDiv);
        infoNeedDiv.add(new AttributeModifier("style", true, new Model("display:none")));
    }

    private void setInfoNeedPanelVisibility(AjaxRequestTarget target) {
        if (selectedInfoNeed != null) {
            infoNeedDiv.add(new AttributeModifier("style", true, new Model("display:block")));
        } else {
            infoNeedDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        }
        target.addComponent(infoNeedDiv);
    }

    private void loadInfoTemplatePanel() {
        if (selectedInfoNeed == null) {
            Label dummyInfoTemplatePanel = new Label("informationNeed", "dummy");
            infoNeedDiv.addOrReplace(dummyInfoTemplatePanel);
        } else {
            int index = ((List<InformationNeed>) RefUtils.get(getElement(), "informationNeeds")).indexOf(selectedInfoNeed);
            InformationNeedPanel infoNeedPanel = new InformationNeedPanel("informationNeed", this,
                    "informationNeeds[" + index + "]",
                    EDITABLE, feedback);
            infoNeedDiv.addOrReplace(infoNeedPanel);
        }
    }

    public void elementChanged(String propPath, AjaxRequestTarget target) {
        super.elementChanged(propPath, target);
        if (propPath.matches(".*informationNeeds.*")) {
            target.addComponent(infoNeedsChoice);
        }
    }

}
