package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ifm.definition.EventDefinition;
import com.mindalliance.channels.playbook.ifm.definition.EventSpecification;
import com.mindalliance.channels.playbook.ifm.definition.LocationDefinition;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.Component;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 1, 2008
 * Time: 10:10:59 AM
 */
public class EventDefinitionPanel extends AbstractDefinitionPanel {

    protected EventDefinition eventDefinition;
    protected AjaxCheckBox anyEventTypeCheckBox;
    protected WebMarkupContainer eventTypesDiv;
    protected DynamicFilterTree eventTypesTree;
    protected AjaxCheckBox anyLocationCheckBox;
    protected WebMarkupContainer locationDefinitionDiv;
    protected LocationDefinitionPanel locationDefinitionPanel;
    protected AjaxCheckBox anyCauseCheckBox;
    protected WebMarkupContainer causeSpecificationsDiv;
    protected ListChoice causeSpecificationsChoice;
    protected Button addCauseSpecificationButton;
    protected Button removeCauseSpecificationButton;
    protected WebMarkupContainer causeSpecificationDiv;
    protected Component causeSpecificationPanel;
    protected EventSpecification selectedCauseSpecification;


    public EventDefinitionPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        EventDefinition eventDefinition = (EventDefinition)getComponent();
        anyEventTypeCheckBox = new AjaxCheckBox("anyEventType", new Model((Boolean)eventDefinition.getEventTypes().isEmpty())){
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
        setVisibility(eventTypesDiv, !eventDefinition.getEventTypes().isEmpty());
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
        anyLocationCheckBox = new AjaxCheckBox("anyLocation", new Model((Boolean)eventDefinition.getLocationDefinition().matchesAll())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyLocation = (Boolean)anyLocationCheckBox.getModelObject();
                if (anyLocation) {
                    setProperty("locationDefinition", new LocationDefinition());
                    locationDefinitionPanel = new LocationDefinitionPanel("locationDefinition", EventDefinitionPanel.this, propPath+".locationDefinition", isReadOnly(), feedback);
                    addReplaceableTo(locationDefinitionPanel, locationDefinitionDiv);
                }
                setVisibility(locationDefinitionDiv, !anyLocation, target);
            }
        };
        addReplaceable(anyLocationCheckBox);
        locationDefinitionDiv = new WebMarkupContainer("locationDefinitionDiv");
        setVisibility(locationDefinitionDiv, !eventDefinition.getLocationDefinition().matchesAll());
        addReplaceable(locationDefinitionDiv);
        locationDefinitionPanel = new LocationDefinitionPanel("locationDefinition", this, propPath+".locationDefinition", isReadOnly(), feedback);
        addReplaceableTo(locationDefinitionPanel, locationDefinitionDiv);
        anyCauseCheckBox = new AjaxCheckBox("anyCause", new Model((Boolean)eventDefinition.getCauseEventSpecs().isEmpty())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyCause = (Boolean)anyCauseCheckBox.getModelObject();
                if (anyCause) {
                    setProperty("causeEventSpecs", new ArrayList<EventSpecification>());
                }
                setVisibility(causeSpecificationsDiv, !anyCause, target);
            }
        };
        addReplaceable(anyCauseCheckBox);
        causeSpecificationsDiv = new WebMarkupContainer("causeSpecificationsDiv");
        setVisibility(causeSpecificationsDiv, !eventDefinition.getCauseEventSpecs().isEmpty());
        addReplaceable(causeSpecificationsDiv);
        causeSpecificationsChoice = new ListChoice("causeSpecifications",
                                                        new Model(selectedCauseSpecification),
                                                        new RefPropertyModel(getComponent(), "causeEventSpecs"),
                                                        new ChoiceRenderer("summary"));
        causeSpecificationsChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                selectedCauseSpecification = (EventSpecification)causeSpecificationsChoice.getModelObject();
                setVisibility(removeCauseSpecificationButton, selectedCauseSpecification != null, target);
                updateEventSpecificationPanel();
                setVisibility(causeSpecificationDiv, selectedCauseSpecification != null, target);
            }
        });
        addReplaceableTo(causeSpecificationsChoice, causeSpecificationsDiv);
        addCauseSpecificationButton = new Button("addCauseSpecification");
        addCauseSpecificationButton.add(new AjaxEventBehavior("onclick"){
            protected void onEvent(AjaxRequestTarget target) {
                selectedCauseSpecification = new EventSpecification();
                RefUtils.add(getElement(), propPath+".causeEventSpecs", selectedCauseSpecification);
                setVisibility(removeCauseSpecificationButton, true, target);
                updateEventSpecificationPanel();
                setVisibility(causeSpecificationDiv, true, target);
            }
        });
        addReplaceableTo(addCauseSpecificationButton, causeSpecificationsDiv);
        removeCauseSpecificationButton = new Button("deleteCauseSpecification");
        removeCauseSpecificationButton.add(new AjaxEventBehavior("onclick"){
            protected void onEvent(AjaxRequestTarget target) {
                RefUtils.remove(getElement(), propPath+".causeEventSpecs", selectedCauseSpecification);
                selectedCauseSpecification = null;
                target.addComponent(causeSpecificationsChoice);
                setVisibility(removeCauseSpecificationButton, false, target);
                setVisibility(causeSpecificationDiv, false, target);
            }
        });
        addReplaceableTo(removeCauseSpecificationButton, causeSpecificationsDiv);
        causeSpecificationDiv = new WebMarkupContainer("causeSpecificationDiv");
        hide(causeSpecificationDiv);
        addReplaceableTo(causeSpecificationDiv, causeSpecificationsDiv);
        causeSpecificationPanel = new Label("causeSpecification", new Model("dummy"));
        addReplaceableTo(causeSpecificationPanel, causeSpecificationDiv);
    }

    private void updateEventSpecificationPanel() {
        if (selectedCauseSpecification != null) {
            int index = eventDefinition.getCauseEventSpecs().indexOf(selectedCauseSpecification);
            causeSpecificationPanel = new EventSpecificationPanel("causeSpecification", this, propPath+".causeEventSpecs["+index+"]", isReadOnly(), feedback);
        }
        else {
           causeSpecificationPanel = new Label("causeSpecification", new Model("dummy"));
        }
        addReplaceableTo(causeSpecificationPanel, causeSpecificationDiv);
    }
}
