package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractComponentPanel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.model.EventType;
import com.mindalliance.channels.playbook.ifm.info.ElementOfInformation;
import com.mindalliance.channels.playbook.support.RefUtils;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 29, 2008
 * Time: 9:03:34 PM
 */
public class EOIsPanel extends AbstractComponentPanel {

    List<Ref> eventTypes;

    List<ElementOfInformation> eois;

    AutoCompleteTextField adHocTopicField;
    WebMarkupContainer topicChoiceDiv;
    ListChoice topicChoiceList;
    Button addTopicButton;
    WebMarkupContainer eoisDiv;
    RefreshingView eoisView;

    List<String> topicChoices;
    String topicToAdd;

    public EOIsPanel(String id, Ref element, String propPath, boolean readOnly, FeedbackPanel feedback, List<Ref> eventTypes) {
        super(id, element, propPath, readOnly, feedback);
        this.eventTypes = eventTypes;
    }

    protected void load() {
        super.load();
        eois = (List<ElementOfInformation>) RefUtils.get(element, propPath);
        List<String> topicChoices = EventType.findAllTopicsIn(eventTypes);

        // Topic choices
        // add hoc topic
        adHocTopicField = new AutoCompleteTextField("adHocTopic", new Model()) {
            protected Iterator getChoices(String input) {
                return topicIterator(input, 10);
            }
        };
        adHocTopicField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                String topic = adHocTopicField.getModelObjectAsString();
                topicToAddInputed(topic, target);
            }
        });
        addReplaceable(adHocTopicField);
        // Topic choices
        topicChoiceDiv = new WebMarkupContainer("topicChoiceDiv");
        topicChoiceList = new ListChoice("topicChoices", new Model(), new Model((Serializable) topicChoices));
        topicChoiceList.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                topicToAdd = topicChoiceList.getModelObjectAsString();
                adHocTopicField.clearInput();
                addTopicButton.setEnabled(true);
                target.addComponent(adHocTopicField);
                target.addComponent(addTopicButton);
            }
        });
        topicChoiceDiv.add(topicChoiceList);
        addReplaceable(topicChoiceDiv);
        // Add button
        addTopicButton = new Button("addTopic");
        addTopicButton.setEnabled(false);
        addTopicButton.add(new AjaxEventBehavior("onclick") {
            protected void onEvent(AjaxRequestTarget target) {
                addNewEOI(target);
            }
        });
        // EOIs
        eoisView = new RefreshingView("eois", new Model((Serializable) eois)) {
            protected Iterator getItemModels() {
                return new ModelIteratorAdapter(eois.iterator()) {
                    protected IModel model(Object eoi) {
                        return new Model((ElementOfInformation) eoi);
                    }
                };
            }

            protected void populateItem(Item item) {
                final ElementOfInformation eoi = (ElementOfInformation) item.getModelObject();
                Label topicLabel = new Label("topic", new Model(eoi.getTopic()));
                item.add(topicLabel);
                final TextArea contentField = new TextArea("content", new Model(eoi.getContent()));
                contentField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    protected void onUpdate(AjaxRequestTarget target) {
                        eoi.setContent(contentField.getModelObjectAsString());
                        elementChanged();
                    }
                });
                item.add(contentField);
                AjaxLink deleteLink = new AjaxLink("deleteEoi") {
                    public void onClick(AjaxRequestTarget target) {
                        removeEoi(eoi, target);
                    }
                };
            }
        };
    }

    private Iterator topicIterator(String input, int max) {
        List<String> matches = new ArrayList<String>();
        for (String topic : topicChoices) {
            if (topic.toLowerCase().startsWith(input.toLowerCase())) {
                matches.add(topic);
                if (matches.size() >= max) break;
            }
        }
        return matches.iterator();
    }

    private void topicToAddInputed(String topic, AjaxRequestTarget target) {
        String toAdd = topic.trim();
        if (toAdd.length() == 0) toAdd = null;
        topicToAdd = toAdd;
        addTopicButton.setEnabled(topicToAdd != null);
        target.addComponent(addTopicButton);
        updateTopicChoicesSelection();
        target.addComponent(topicChoiceList);
    }

    private void updateTopicChoicesSelection() {
        for (String topic : topicChoices) {
            if (topic.equalsIgnoreCase(topicToAdd)) {
                topicChoiceList.setModelObject(topic);
                topicChoiceList.modelChanged();
                break;
            }
        }
    }

    private void addNewEOI(AjaxRequestTarget target) {
        ElementOfInformation eoi = new ElementOfInformation();
        eoi.setTopic(topicToAdd);
        eois.add(eoi);
        elementChanged();
        topicChoices.remove(topicToAdd);
        topicToAdd = null;
        addTopicButton.setEnabled(false);
        target.addComponent(addTopicButton);
        target.addComponent(topicChoiceList);
        target.addComponent(eoisDiv);
    }

    private void removeEoi(ElementOfInformation eoi, AjaxRequestTarget target) {
        eois.remove(eoi);
        elementChanged();
        topicChoices.add(eoi.getTopic());
        target.addComponent(topicChoiceList);
        target.addComponent(eoisDiv);
    }


}
