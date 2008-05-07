package com.mindalliance.channels.playbook.pages.forms.tabs.eventType;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractModelElementFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.AjaxLink;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 3:58:40 PM
 */
public class EventTypeTopicsTab extends AbstractModelElementFormTab {

    protected TextField newTopicField;
    protected AjaxButton addTopicButton;
    protected WebMarkupContainer topicsDiv;
    protected RefreshingView topicsView;
    protected WebMarkupContainer inheritedTopicsDiv;
    protected RefreshingView inheritedTopicsView;
    private String newTopic;

    public EventTypeTopicsTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }


    protected void load() {
        super.load();
        newTopicField = new TextField("newTopic", new Model());
        newTopicField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                String topic = newTopicField.getModelObjectAsString();
                topicToAddInputed(topic, target);
            }
        });
        inheritedTopicsDiv = new WebMarkupContainer("inheritedTopicsDiv");
        inheritedTopicsView = new RefreshingView("inheritedTopics", new RefQueryModel(this, new Query("matchingInheritedTopics"))) {
            protected Iterator getItemModels() {
                return new ModelIteratorAdapter(getInheritedTopics().iterator()) {
                    protected IModel model(Object topic) {
                        return new Model((String) topic);
                    }
                };
            }
            protected void populateItem(Item item) {
                String inheritedTopic = item.getModelObjectAsString();
                Label inheritedTopicLabel = new Label("inheritedTopic", inheritedTopic);
                item.add(inheritedTopicLabel);
                final Ref narrowedEventType = (Ref)Query.execute(getElement(), "findNarrowedEventTypeWithTopic", inheritedTopic);
                AjaxLink narrowedEventTypeLink = new AjaxLink("narrowedEventTypeLink") {
                    public void onClick(AjaxRequestTarget target) {
                        edit(narrowedEventType, target);
                    }
                };
                narrowedEventTypeLink.add(inheritedTopicLabel);
                item.add(narrowedEventTypeLink);
            }
        };
        addReplaceable(inheritedTopicsDiv);
        addReplaceable(newTopicField);
        addTopicButton = new AjaxButton("addTopic") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                RefUtils.add(getElement(), "topics", newTopic);
                newTopicField.clearInput();
                newTopic = null;
                addTopicButton.setEnabled(false);
                target.addComponent(addTopicButton);
                target.addComponent(newTopicField);
                target.addComponent(topicsDiv);
            }
        };
        topicsDiv = new WebMarkupContainer("topicsDiv");
        topicsView = new RefreshingView("topic", new RefPropertyModel(getElement(), "topics")) {
            protected Iterator getItemModels() {
                List<String> topics = (List<String>) RefUtils.get(getElement(), "topics");
                return new ModelIteratorAdapter(topics.iterator()) {
                    protected IModel model(Object topic) {
                        return new Model((String) topic);
                    }
                };
            }
            protected void populateItem(final Item item) {
                Label topicLabel = new Label("topic", item.getModelObjectAsString());
                item.add(topicLabel);
                AjaxLink deleteTopicLink = new AjaxLink("deleteTopic"){
                    public void onClick(AjaxRequestTarget target) {
                        String topic = item.getModelObjectAsString();
                        RefUtils.remove(getElement(), "topics", topic);
                        target.addComponent(topicsDiv);
                    }
                };
            }
        };
        addReplaceable(topicsDiv);
    }

    private void topicToAddInputed(String topic, AjaxRequestTarget target) {
        newTopic = topic.trim();
        addTopicButton.setEnabled(isNewTopic(topic));
        target.addComponent(addTopicButton);
        target.addComponent(inheritedTopicsDiv);
    }

    private boolean isNewTopic(String topic) {
        return !(newTopic == null ||
                 newTopic.isEmpty() ||
                 getInheritedTopics().contains(topic) ||
                 ((List<Ref>)RefUtils.get(getElement(), "topics")).contains(topic));
    }

    private List<String> getInheritedTopics() {
        List<String> inheritedTopics = (List<String>)Query.execute(getIfmModel(), "findInheritedTopics", getElement());
        return inheritedTopics;
    }

    private List<String> matchingInheritedTopics() {
        List<String> matches = new ArrayList<String>();
        for (String topic : getInheritedTopics()) {
            if (topic.toLowerCase().startsWith(newTopic)) {
                matches.add(topic);
            }
        }
        return matches;
    }
}
