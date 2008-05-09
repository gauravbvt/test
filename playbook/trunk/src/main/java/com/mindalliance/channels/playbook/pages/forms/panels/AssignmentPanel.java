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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
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
    protected WebMarkupContainer informationTemplatesDiv;
    protected RefreshingView informationTemplatesView;
    protected AjaxButton newTemplateButton;
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
        loadInformationTemplatesDiv();
        newTemplateButton = new AjaxButton("newInformationTemplate") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                selectedInfoTemplate = new InformationTemplate();
                assignment.getInformationTemplates().add(selectedInfoTemplate);
                loadInfoTemplatePanel();
                resetInfoTemplatePanel(target);
                elementChanged(propPath, target);
                target.addComponent(informationTemplatesDiv) ;
            }
        };
        addReplaceable(newTemplateButton);
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

    private void loadInformationTemplatesDiv() {
        informationTemplatesDiv = new WebMarkupContainer("informationTemplatesDiv");
        informationTemplatesView = new RefreshingView("informationTemplates", new RefPropertyModel(assignment, "informationTemplates")) {
            protected Iterator getItemModels() {
                List<InformationTemplate> templates = (List<InformationTemplate>)RefUtils.get(assignment, "informationTemplates");
                return new ModelIteratorAdapter(templates.iterator()) {
                    protected IModel model(Object template) {
                        return new Model((InformationTemplate) template);
                    }
                };
            }
            protected void populateItem(final Item item) {
                final InformationTemplate infoTemplate = (InformationTemplate)item.getModelObject();
                AjaxLink infoTemplateLink = new AjaxLink("infoTemplateLink") {
                    public void onClick(AjaxRequestTarget target) {
                       selectedInfoTemplate = infoTemplate;
                       loadInfoTemplatePanel();
                       resetInfoTemplatePanel(target);
                    }
                };
                Label infoTemplateLabel = new Label("infoTemplateString", new Model(infoTemplate.toString()));
                infoTemplateLink.add(infoTemplateLabel);
                item.add(infoTemplateLink);
                AjaxLink infoTemplateDeleteLink = new AjaxLink("infoTemplateDelete") {
                    public void onClick(AjaxRequestTarget target) {
                        assignment.getInformationTemplates().remove(infoTemplate);
                        selectedInfoTemplate = null;
                        resetInfoTemplatePanel(target);
                        target.addComponent(informationTemplatesDiv);
                    }
                };
                item.add(infoTemplateDeleteLink);
            }
        };
        informationTemplatesDiv.add(informationTemplatesView);
        addReplaceable(informationTemplatesDiv);
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

    private void resetInfoTemplatePanel(AjaxRequestTarget target) {
        if (selectedInfoTemplate != null) {
            infoTemplateDiv.add(new AttributeModifier("style", true, new Model("display:block")));
        }
        else {
            infoTemplateDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        }
        target.addComponent(infoTemplateDiv);
    }

    @Override
    public void elementChanged(String propPath, AjaxRequestTarget target) {
        super.elementChanged(propPath, target);
        if (propPath.matches(".*informationTemplates.*")) {
            target.addComponent(informationTemplatesDiv);
        }
    }
}
