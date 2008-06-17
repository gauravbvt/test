package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.model.EventType;
import com.mindalliance.channels.playbook.ifm.info.InformationNeed;
import com.mindalliance.channels.playbook.ifm.spec.EventSpec;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.model.Model;
import org.apache.wicket.AttributeModifier;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 28, 2008
 * Time: 8:34:37 AM
 */
public class InformationNeedPanel extends AbstractComponentPanel {

    protected DynamicFilterTree agentTree;
    protected AjaxCheckBox eventSpecCheckBox;
    protected AjaxCheckBox eventCheckBox;
    protected WebMarkupContainer eventSpecDiv;
    protected EventSpecPanel eventSpecPanel;
    protected WebMarkupContainer eventDiv;
    protected DynamicFilterTree eventTree;
    protected EOIsPanel eoisPanel;
    protected AgentSpecPanel agentSpecPanel;
    protected InformationNeed informationNeed;
    protected Ref priorEvent;
    protected EventSpec priorEventSpec;
    protected TimingPanel deadlinePanel;

    public InformationNeedPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        informationNeed = (InformationNeed)getComponent();
        agentTree = new DynamicFilterTree("agent", new RefPropertyModel(informationNeed, "agent"),
                                          new RefQueryModel(getPlaybook(), new Query("findAllAgents")),
                                          SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selected = agentTree.getNewSelection();
                informationNeed.setAgent(selected);
                elementChanged(propPath+".agent", target);
            }
        };
        addReplaceable(agentTree);
        eventSpecCheckBox = new AjaxCheckBox("aboutEventSpec", new Model(!informationNeed.isAboutSpecificEvent())) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean isAboutEventSpec = (Boolean)eventSpecCheckBox.getModelObject();
                eventCheckBox.setModelObject(!isAboutEventSpec);
                target.addComponent(eventCheckBox);
                updateAbout(isAboutEventSpec, target);
            }
        };
        addReplaceable(eventSpecCheckBox);
        eventCheckBox = new AjaxCheckBox("aboutEvent", new Model(informationNeed.isAboutSpecificEvent())) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean isAboutEvent = (Boolean)eventSpecCheckBox.getModelObject();
                 eventSpecCheckBox.setModelObject(!isAboutEvent);
                 target.addComponent(eventSpecCheckBox);
                 updateAbout(!isAboutEvent, target);
            }
        };
        addReplaceable(eventCheckBox);
        eventSpecDiv = new WebMarkupContainer("eventSpecDiv");
        addReplaceable(eventSpecDiv);
        eventSpecPanel = new EventSpecPanel("eventSpec", this, propPath + ".eventSpec", isReadOnly(), feedback);
        this.addReplaceableTo(eventSpecPanel, eventSpecDiv);
        eventDiv = new WebMarkupContainer("eventDiv");
        addReplaceable(eventDiv);
        eventTree = new DynamicFilterTree("event", new RefPropertyModel(getComponent(), "event"),
                                                   new RefQueryModel(getElement(), new Query("findAllPriorEvents")),
                                                   SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                 RefUtils.set(getComponent(), "event", eventTree.getNewSelection());
                 elementChanged(propPath+".event", target);
            }
        };
        addReplaceableTo(eventTree, eventDiv);
        setAboutVisibility();
        deadlinePanel = new TimingPanel("deadline", this, propPath+".deadline", isReadOnly(), feedback);
        addReplaceable(deadlinePanel);
        RefQueryModel topicChoicesModel = new RefQueryModel(this, new Query("findAllKnownTopics"));
        eoisPanel = new EOIsPanel("eventDetails", this, propPath + ".eventDetails", readOnly, feedback, topicChoicesModel);
        addReplaceable(eoisPanel);
        agentSpecPanel = new AgentSpecPanel("sourceSpec", this, propPath+".sourceSpec", isReadOnly(), feedback);
        addReplaceable(agentSpecPanel);
        priorEvent = informationNeed.getEvent();
        priorEventSpec = informationNeed.getEventSpec();
    }

    private void setAboutVisibility() {
        if ((Boolean)eventCheckBox.getModelObject()) {
           eventDiv.add(new AttributeModifier("style", true, new Model("display:block")));
           eventSpecDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        }
        else {
            eventDiv.add(new AttributeModifier("style", true, new Model("display:none")));
            eventSpecDiv.add(new AttributeModifier("style", true, new Model("display:block")));
        }
    }

    private void updateAbout(boolean isAboutEventSpec, AjaxRequestTarget target) {
        if (isAboutEventSpec) {
            priorEvent = informationNeed.getEvent();
            informationNeed.setEvent(null);
            informationNeed.setEventSpec(priorEventSpec);
            elementChanged(propPath+".eventSpec", target);
        }
        else { // is about event
            priorEventSpec = informationNeed.getEventSpec();
            informationNeed.setEventSpec(new EventSpec());
            informationNeed.setEvent(priorEvent);
            elementChanged(propPath+".event", target);
        }
        eventSpecPanel = new EventSpecPanel("eventSpec", this, propPath + ".eventSpec", isReadOnly(), feedback);
        this.addReplaceableTo(eventSpecPanel, eventSpecDiv);
        setAboutVisibility();
        target.addComponent(eventDiv);
        target.addComponent(eventSpecDiv);
    }

    @Override
    public void elementChanged(String propPath, AjaxRequestTarget target) {
        super.elementChanged(propPath, target);
        if (propPath.endsWith(".eventSpec.eventTypes")) {
            target.addComponent(eoisPanel);
        }
        if (propPath.endsWith(".event")) {
            target.addComponent(eoisPanel);
        }
    }

    private List<String> findAllKnownTopics() {
        if (informationNeed.isAboutSpecificEvent()) {
            return (List<String>)Query.execute(getPlaybook(), "findAllTopicsAboutEvent", informationNeed.getEvent());
        }
        else {
            return (List<String>)Query.execute(EventType.class, "findAllTopicsIn", informationNeed.getEventSpec().getEventTypes());
        }
    }

}
