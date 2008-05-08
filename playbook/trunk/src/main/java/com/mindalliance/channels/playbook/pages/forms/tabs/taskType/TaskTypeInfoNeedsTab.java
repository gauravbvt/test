package com.mindalliance.channels.playbook.pages.forms.tabs.taskType;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractModelElementFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.InformationTemplatePanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.info.InformationTemplate;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;
import org.apache.wicket.AttributeModifier;

import java.util.List;
import java.util.Iterator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 8:23:43 PM
 */
public class TaskTypeInfoNeedsTab extends AbstractModelElementFormTab {

    protected DynamicFilterTree eventTypesTree;
    protected WebMarkupContainer infoTemplatesDiv;
    protected AjaxButton addInfoTemplateButton;
    protected RefreshingView infoTemplatesView;
    protected WebMarkupContainer infoTemplateDiv;
    protected List<Ref> selectedEventTypes;
    protected InformationTemplate selectedInfoTemplate;

    public TaskTypeInfoNeedsTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        eventTypesTree = new DynamicFilterTree("eventTypes", new Model(),
                                               new RefQueryModel(getScope(), new Query("findAllTypes", "EventType"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                selectedEventTypes = eventTypesTree.getNewSelections();
                addInfoTemplateButton.setEnabled(!selectedEventTypes.isEmpty());
            }
        };
        addReplaceable(eventTypesTree);
        addInfoTemplateButton = new AjaxButton("addInfoTemplate") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                selectedInfoTemplate = new InformationTemplate();
                selectedInfoTemplate.getEventSpec().setEventTypes(selectedEventTypes);
                RefUtils.add(getElement(), "informationTemplates", selectedInfoTemplate);
                resetInfoTemplatePanel(target);
                target.addComponent(infoTemplatesDiv);
            }
        };
        addReplaceable(addInfoTemplateButton);
        infoTemplatesDiv = new WebMarkupContainer("infoTemplatesDiv");
        infoTemplatesView = new RefreshingView("informationTemplates", new RefPropertyModel(getElement(), "informationTemplates")) {
            protected Iterator getItemModels() {
                List<InformationTemplate> infoTemplates = (List<InformationTemplate>)RefUtils.get(getElement(), "informationTemplates");
                return new ModelIteratorAdapter(infoTemplates.iterator()) {
                     protected IModel model(Object infoTemplate) {
                         return new Model((InformationTemplate) infoTemplate);
                     }
                 };
            }
            protected void populateItem(final Item item) {
                final InformationTemplate infoTemplate = (InformationTemplate)item.getModelObject();
                AjaxLink infoTemplateLink = new AjaxLink("infoTemplateLink") {
                    public void onClick(AjaxRequestTarget target) {
                        selectedInfoTemplate = infoTemplate;
                        resetInfoTemplatePanel(target);
                    }
                };
                Label infoTemplateLabel = new Label("infoTemplateString", infoTemplate.toString());
                infoTemplateLink.add(infoTemplateLabel);
                item.add(infoTemplateLink);
                AjaxLink deleteInfoTemplateLink = new AjaxLink("deleteInfoTemplateLink") {
                    public void onClick(AjaxRequestTarget target) {
                        RefUtils.remove(getElement(), "informationTemplates", infoTemplate);
                        selectedInfoTemplate = null;
                        resetInfoTemplatePanel(target);
                        target.addComponent(infoTemplatesDiv);
                    }
                };
            }
        };
        addReplaceable(infoTemplatesDiv);

        infoTemplateDiv = new WebMarkupContainer("infoTemplateDiv");
        infoTemplateDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        addReplaceable(infoTemplateDiv);
    }

    private void resetInfoTemplatePanel(AjaxRequestTarget target) {
        if (selectedInfoTemplate != null) {
            loadInfoTemplatePanel();
            infoTemplateDiv.add(new AttributeModifier("style", true, new Model("display:block")));
        }
        else {
            infoTemplateDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        }
        target.addComponent(infoTemplateDiv);
    }

    private void loadInfoTemplatePanel() {
        int index = ((List<InformationTemplate>)RefUtils.get(getElement(), "informationTemplates")).indexOf(selectedInfoTemplate);
        InformationTemplatePanel infoTemplatePanel = new InformationTemplatePanel("informationTemplate", this,
                                                                                  "informationTemplates[" + index + "]",
                                                                                   EDITABLE, feedback);
        infoTemplateDiv.addOrReplace(infoTemplatePanel);
    }
}
