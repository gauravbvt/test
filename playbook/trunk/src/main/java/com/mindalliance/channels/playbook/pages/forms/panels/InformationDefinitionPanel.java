package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ifm.definition.InformationDefinition;
import com.mindalliance.channels.playbook.ifm.definition.EventSpecification;
import com.mindalliance.channels.playbook.ifm.definition.AgentSpecification;
import com.mindalliance.channels.playbook.ifm.info.ElementOfInformation;
import com.mindalliance.channels.playbook.ifm.model.EventType;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;
import org.apache.wicket.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 1, 2008
 * Time: 12:55:35 PM
 */
public class InformationDefinitionPanel extends AbstractDefinitionPanel {


    protected InformationDefinition informationDefinition;
    protected AjaxCheckBox anyEventCheckBox;
    protected WebMarkupContainer eventSpecificationDiv;
    protected EventSpecificationPanel eventSpecificationPanel;
    protected AjaxCheckBox anyEventTypeCheckBox;
    protected WebMarkupContainer eventTypesDiv;
    protected DynamicFilterTree eventTypesTree;
    protected AjaxCheckBox anySourceCheckBox;
    protected WebMarkupContainer sourceSpecificationDiv;
    protected Component sourceSpecificationPanel;
    protected AjaxCheckBox anyEoiCheckBox;
    protected WebMarkupContainer eoisDiv;
    protected EOIsPanel eoisPanel;

    public InformationDefinitionPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        informationDefinition = (InformationDefinition) getComponent();
        anyEventCheckBox = new AjaxCheckBox("anyEvent", new Model((Boolean) informationDefinition.getEventSpec().matchesAll())) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyEvent = (Boolean) anyEventCheckBox.getModelObject();
                if (anyEvent) {
                    setProperty("eventSpecification", new EventSpecification());
                    eventSpecificationPanel = new EventSpecificationPanel("eventSpecification", InformationDefinitionPanel.this, propPath + ".eventSpec", isReadOnly(), feedback);
                    addReplaceableTo(eventSpecificationPanel, eventSpecificationDiv);
                }
                setVisibility(eventSpecificationDiv, !anyEvent, target);
            }
        };
        addReplaceable(anyEventCheckBox);
        eventSpecificationDiv = new WebMarkupContainer("eventSpecificationDiv");
        setVisibility(eventSpecificationDiv, !informationDefinition.getEventSpec().matchesAll());
        addReplaceable(eventSpecificationDiv);
        eventSpecificationPanel = new EventSpecificationPanel("eventSpecification", this, propPath + ".eventSpec", isReadOnly(), feedback);
        addReplaceableTo(eventSpecificationPanel, eventSpecificationDiv);

        anyEventTypeCheckBox = new AjaxCheckBox("anyEventType", new Model((Boolean)informationDefinition.getEventTypes().isEmpty())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyEventType = (Boolean)anyEventTypeCheckBox.getModelObject();
                if (anyEventType) {
                    setProperty("eventTypes", new ArrayList<Ref>());
                }
                setVisibility(eventTypesDiv, !anyEventType, target);
            }
        };
        addReplaceable(anyEventTypeCheckBox);
        eventTypesDiv = new WebMarkupContainer("eventTypesDiv");
        setVisibility(eventTypesDiv, !informationDefinition.getEventTypes().isEmpty());
        addReplaceable(eventTypesDiv);
        eventTypesTree = new DynamicFilterTree("eventTypes",
                                          new RefPropertyModel(getElement(), propPath+".eventTypes"),
                                          new RefQueryModel(getProject(), new Query("findAllTypes", "EventType"))){
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selected = eventTypesTree.getNewSelections();
                setProperty("eventTypes", selected, target);
            }
        };
        addReplaceableTo(eventTypesTree, eventTypesDiv);

        anySourceCheckBox = new AjaxCheckBox("anySource", new Model((Boolean) informationDefinition.getSourceAgentSpec().matchesAll())) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anySource = (Boolean) anySourceCheckBox.getModelObject();
                if (anySource) {
                    setProperty("sourceAgentSpec", new AgentSpecification());
                    sourceSpecificationPanel = new AgentSpecificationPanel("sourceSpecification", InformationDefinitionPanel.this, propPath + ".sourceAgentSpec", isReadOnly(), feedback);
                    addReplaceableTo(sourceSpecificationPanel, sourceSpecificationDiv);
                }
                setVisibility(sourceSpecificationDiv, !anySource, target);
            }
        };
        addReplaceable(anySourceCheckBox);
        sourceSpecificationDiv = new WebMarkupContainer("sourceSpecificationDiv");
        setVisibility(sourceSpecificationDiv, !informationDefinition.getSourceAgentSpec().matchesAll());
        addReplaceable(sourceSpecificationDiv);
        sourceSpecificationPanel = new AgentSpecificationPanel("sourceSpecification", this, propPath + ".sourceAgentSpec", isReadOnly(), feedback);
        addReplaceableTo(sourceSpecificationPanel, sourceSpecificationDiv);

        anyEoiCheckBox = new AjaxCheckBox("anyEoi", new Model((Boolean) informationDefinition.getElementsOfInformation().isEmpty())) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyEoi = (Boolean) anyEoiCheckBox.getModelObject();
                if (anyEoi) {
                    setProperty("elementsOfInformation", new ArrayList<ElementOfInformation>());
                    eoisPanel = new EOIsPanel("eois", InformationDefinitionPanel.this, propPath + ".elementsOfInformation", isReadOnly(), feedback, getTopicChoicesModel());
                    addReplaceableTo(eoisPanel, eoisDiv);
                }
                setVisibility(eoisDiv, !anyEoi, target);
            }
        };
        addReplaceable(anyEoiCheckBox);
        eoisDiv = new WebMarkupContainer("eoisDiv");
        setVisibility(eoisDiv, !informationDefinition.getElementsOfInformation().isEmpty());
        addReplaceable(eoisDiv);
        eoisPanel = new EOIsPanel("eois", this, propPath + ".elementsOfInformation", isReadOnly(), feedback, getTopicChoicesModel());
        addReplaceableTo(eoisPanel, eoisDiv);
    }

    private IModel getTopicChoicesModel() {
        return new RefQueryModel(this, new Query("findAllKnownTopics"));
    }

    private List<String> findAllKnownTopics() {
        return (List<String>) Query.execute(EventType.class, "findAllTopicsIn", informationDefinition.getEventTypes());
    }

    @Override
    public void elementChanged(String propPath, AjaxRequestTarget target) {
        super.elementChanged(propPath, target);
        if (propPath.matches(".*\\.informationSpec\\.eventSpec\\.definitions\\[\\d+\\]\\.description")) {
            target.addComponent(eoisPanel);
        }
    }


}
