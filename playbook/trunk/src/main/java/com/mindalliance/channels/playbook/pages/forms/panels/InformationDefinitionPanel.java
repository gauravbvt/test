package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.ifm.definition.InformationDefinition;
import com.mindalliance.channels.playbook.ifm.definition.LocationDefinition;
import com.mindalliance.channels.playbook.ifm.definition.EventSpecification;
import com.mindalliance.channels.playbook.ifm.definition.AgentSpecification;
import com.mindalliance.channels.playbook.ifm.info.ElementOfInformation;
import com.mindalliance.channels.playbook.ifm.model.EventType;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
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
    protected AjaxCheckBox anySourceCheckBox;
    protected WebMarkupContainer sourceSpecificationsDiv;
    protected ListChoice sourceSpecificationsChoice;
    protected AjaxButton addSourceSpecificationButton;
    protected AjaxButton removeSourceSpecificationButton;
    protected WebMarkupContainer sourceSpecificationDiv;
    protected Component sourceSpecificationPanel;
    protected AgentSpecification selectedSourceSpecification;
    protected AjaxCheckBox anyEoiCheckBox;
    protected WebMarkupContainer eoisDiv;
    protected EOIsPanel eoisPanel;


    public InformationDefinitionPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        informationDefinition = (InformationDefinition)getComponent();
        anyEventCheckBox = new AjaxCheckBox("anyEvent", new Model((Boolean)informationDefinition.getEventSpec().matchesAll())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyEvent = (Boolean)anyEventCheckBox.getModelObject();
                if (anyEvent) {
                    setProperty("eventSpecification", new EventSpecification());
                    eventSpecificationPanel = new EventSpecificationPanel("eventSpecification", InformationDefinitionPanel.this, propPath+".eventSpec", isReadOnly(), feedback);
                    addReplaceableTo(eventSpecificationPanel, eventSpecificationDiv);
                }
                setVisibility(eventSpecificationDiv, !anyEvent, target);
            }
        };
        addReplaceable(anyEventCheckBox);
        eventSpecificationDiv = new WebMarkupContainer("eventSpecificationDiv");
        setVisibility(eventSpecificationDiv, !informationDefinition.getEventSpec().matchesAll());
        addReplaceable(eventSpecificationDiv);
        eventSpecificationPanel = new EventSpecificationPanel("eventSpecification", this, propPath+".eventSpec", isReadOnly(), feedback);
        addReplaceableTo(eventSpecificationPanel, eventSpecificationDiv);

        anySourceCheckBox = new AjaxCheckBox("anySource", new Model((Boolean)informationDefinition.getSourceAgentSpecs().isEmpty())){
             protected void onUpdate(AjaxRequestTarget target) {
                 boolean anySource = (Boolean)anySourceCheckBox.getModelObject();
                 if (anySource) {
                     setProperty("sourceAgentSpecs", new ArrayList<AgentSpecification>());
                 }
                 setVisibility(sourceSpecificationsDiv, !anySource, target);
             }
         };
         addReplaceable(anySourceCheckBox);
         sourceSpecificationsDiv = new WebMarkupContainer("sourceSpecificationsDiv");
         setVisibility(sourceSpecificationsDiv, !informationDefinition.getSourceAgentSpecs().isEmpty());
         addReplaceable(sourceSpecificationsDiv);
         sourceSpecificationsChoice = new ListChoice("sourceSpecifications",
                                                         new Model(selectedSourceSpecification),
                                                         new RefPropertyModel(getComponent(), "sourceAgentSpecs"),
                                                         new ChoiceRenderer("summary"));
         sourceSpecificationsChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
             protected void onUpdate(AjaxRequestTarget target) {
                 selectedSourceSpecification = (AgentSpecification)sourceSpecificationsChoice.getModelObject();
                 setVisibility(removeSourceSpecificationButton, selectedSourceSpecification != null, target);
                 updateSourceSpecificationPanel();
                 setVisibility(sourceSpecificationDiv, selectedSourceSpecification != null, target);
             }
         });
         addReplaceableTo(sourceSpecificationsChoice, sourceSpecificationsDiv);
         addSourceSpecificationButton = new AjaxButton("addSourceSpecification"){
             protected void onSubmit(AjaxRequestTarget target, Form form) {
                 selectedSourceSpecification = new AgentSpecification();
                 RefUtils.add(getElement(), propPath+".sourceAgentSpecs", selectedSourceSpecification);
                 setVisibility(removeSourceSpecificationButton, true, target);
                 updateSourceSpecificationPanel();
                 setVisibility(sourceSpecificationDiv, true, target);
             }
         };
         addReplaceableTo(addSourceSpecificationButton, sourceSpecificationsDiv);
         removeSourceSpecificationButton = new AjaxButton("deleteSourceSpecification"){
             protected void onSubmit(AjaxRequestTarget target, Form form) {
                 RefUtils.remove(getElement(), propPath+".sourceAgentSpecs", selectedSourceSpecification);
                 selectedSourceSpecification = null;
                 target.addComponent(sourceSpecificationsChoice);
                 setVisibility(removeSourceSpecificationButton, false, target);
                 setVisibility(sourceSpecificationDiv, false, target);
             }
         };
         addReplaceableTo(removeSourceSpecificationButton, sourceSpecificationsDiv);
         sourceSpecificationDiv = new WebMarkupContainer("sourceSpecificationDiv");
         hide(sourceSpecificationDiv);
         addReplaceableTo(sourceSpecificationDiv, sourceSpecificationsDiv);
         sourceSpecificationPanel = new Label("sourceSpecification", new Model("dummy"));
         addReplaceableTo(sourceSpecificationPanel, sourceSpecificationDiv);

        anyEoiCheckBox = new AjaxCheckBox("anyEoi", new Model((Boolean)informationDefinition.getElementsOfInformation().isEmpty())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyEoi = (Boolean)anyEoiCheckBox.getModelObject();
                if (anyEoi) {
                    setProperty("elementsOfInformation", new ArrayList<ElementOfInformation>());
                    eoisPanel = new EOIsPanel("eois", InformationDefinitionPanel.this, propPath+".elementsOfInformation", isReadOnly(), feedback, getTopicChoicesModel());
                    addReplaceableTo(eoisPanel, eoisDiv);
                }
                setVisibility(eoisDiv, !anyEoi, target);
            }
        };
        addReplaceable(anyEoiCheckBox);
        eoisDiv = new WebMarkupContainer("eoisDiv");
        setVisibility(eoisDiv, !informationDefinition.getElementsOfInformation().isEmpty());
        addReplaceable(eoisDiv);
        eoisPanel = new EOIsPanel("eois", this, propPath+".elementsOfInformation", isReadOnly(), feedback, getTopicChoicesModel());
        addReplaceableTo(eoisPanel, eoisDiv);
    }

    private void updateSourceSpecificationPanel() {
        if (selectedSourceSpecification != null) {
            int index = informationDefinition.getSourceAgentSpecs().indexOf(selectedSourceSpecification);
            sourceSpecificationPanel = new EventSpecificationPanel("sourceSpecification", this, propPath+".sourceAgentSpecifications["+index+"]", isReadOnly(), feedback);
        }
        else {
           sourceSpecificationPanel = new Label("sourceSpecification", new Model("dummy"));
        }
        addReplaceableTo(sourceSpecificationPanel, sourceSpecificationDiv);
    }

    private IModel getTopicChoicesModel() {
      return new RefQueryModel(this, new Query("findAllKnownTopics"));
    }

    private List<String> findAllKnownTopics() {
         return (List<String>)Query.execute(EventType.class, "findAllTopicsIn", informationDefinition.getEventSpec().allEventTypes());
    }

}
