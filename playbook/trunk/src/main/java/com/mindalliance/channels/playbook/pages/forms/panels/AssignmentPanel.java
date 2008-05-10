package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ifm.info.Assignment;
import com.mindalliance.channels.playbook.ifm.info.InformationTemplate;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.AttributeModifier;

import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 6, 2008
 * Time: 2:59:44 PM
 */
public class AssignmentPanel extends AbstractComponentPanel {

    Assignment assignment;
    protected ListChoice infoTemplatesChoice;
    protected AjaxButton deleteInfoTemplateButton;
    protected AjaxButton addInfoTemplateButton;
    protected DynamicFilterTree taskTypesTree;
    protected InformationTemplate selectedInfoTemplate;
    protected WebMarkupContainer infoTemplateDiv;
    protected TimingPanel timingPanel;

    public AssignmentPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        assignment = (Assignment) RefUtils.get(getElement(), propPath);
        infoTemplatesChoice = new ListChoice("informationTemplates", new Model(),
                                              new RefPropertyModel(assignment, "informationTemplates"));
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
                RefUtils.add(assignment, "informationTemplates", infoTemplate);
                target.addComponent(infoTemplatesChoice);
                elementChanged(propPath, target);
            }
        };
        addReplaceable(addInfoTemplateButton);
        deleteInfoTemplateButton = new AjaxButton("deleteInfoTemplate") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                RefUtils.remove(assignment, "informationTemplates", selectedInfoTemplate);
                selectedInfoTemplate = null;
                deleteInfoTemplateButton.setEnabled(false);
                loadInfoTemplatePanel();
                setInfoTemplatePanelVisibility(target);
                target.addComponent(deleteInfoTemplateButton);
                target.addComponent(infoTemplatesChoice);
                elementChanged(propPath, target);
            }
        };
        deleteInfoTemplateButton.setEnabled(false);
        addReplaceable(deleteInfoTemplateButton);
        taskTypesTree = new DynamicFilterTree("taskTypes", new RefPropertyModel(assignment, "taskTypes"),
                                                           new RefQueryModel(getScope(), new Query("findAllTypes", "TaskType"))) {
            @Override
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selectedTaskTypes = taskTypesTree.getNewSelections();
                assignment.setTaskTypes(selectedTaskTypes);
                elementChanged(propPath, target);
            }
        };
        addReplaceable(taskTypesTree);
        timingPanel = new TimingPanel("timing", this, propPath + ".timing", isReadOnly(), feedback);
        addReplaceable(timingPanel);
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

    protected void loadInfoTemplatePanel() {
        if (selectedInfoTemplate != null) {
            int index = assignment.getInformationTemplates().indexOf(selectedInfoTemplate);
            InformationTemplatePanel infoTemplatePanel = new InformationTemplatePanel("informationTemplate", this,
                                                                                      propPath + ".informationTemplates[" + index + "]",
                                                                                      isReadOnly(), feedback);
            infoTemplateDiv.addOrReplace(infoTemplatePanel);
        }
        else {
            Label infoTemplateLabel = new Label("informationTemplate", "dummy");
            infoTemplateDiv.addOrReplace(infoTemplateLabel);
        }
    }

    @Override
    public void elementChanged(String propPath, AjaxRequestTarget target) {
        super.elementChanged(propPath, target);
        if (propPath.matches(".*informationTemplates.*")) {
            target.addComponent(infoTemplatesChoice);
        }
    }

}
