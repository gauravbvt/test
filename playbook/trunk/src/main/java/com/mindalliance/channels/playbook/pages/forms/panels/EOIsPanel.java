package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractComponentPanel;
import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.ifm.info.ElementOfInformation;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.query.Query;
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
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 29, 2008
 * Time: 9:03:34 PM
 */
public class EOIsPanel extends AbstractComponentPanel {

    RefQueryModel availableTopicChoicesModel;

    List<ElementOfInformation> eois;

    AutoCompleteTextField adHocTopicField;
    WebMarkupContainer topicChoiceDiv;
    ListChoice topicChoiceList;
    Button addTopicButton;
    WebMarkupContainer eoisDiv;
    RefreshingView eoisView;

    String topicToAdd;

    public EOIsPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback, IModel topicChoicesModel) {
        super(id, parentPanel, propPath, readOnly, feedback);
        this.availableTopicChoicesModel = new RefQueryModel(this, new Query("availableTopics", topicChoicesModel));
        doLoad();
    }

    protected void doLoad() {  // load after setting availableTopicChoicesModel
        eois = (List<ElementOfInformation>) RefUtils.get(getElement(), propPath);
        if (eois == null) {
            eois = new ArrayList<ElementOfInformation>();
            RefUtils.set(getElement(), propPath, eois);
        }

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
        topicChoiceList = new ListChoice("topicChoices", new Model(), availableTopicChoicesModel);
        topicChoiceList.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                topicToAdd = topicChoiceList.getModelObjectAsString();
                adHocTopicField.clearInput();
                addTopicButton.setEnabled(topicToAdd != null);
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
        addReplaceable(addTopicButton);
        // EOIs
        eoisDiv = new WebMarkupContainer("eoisDiv");
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
                        elementChanged(propPath, target);
                    }
                });
                item.add(contentField);
                AjaxLink deleteLink = new AjaxLink("deleteEoi") {
                    public void onClick(AjaxRequestTarget target) {
                        removeEoi(eoi, target);
                    }
                };
                item.add(deleteLink);
            }
        };
        eoisDiv.add(eoisView);
        addReplaceable(eoisDiv);
    }

    private Iterator topicIterator(String input, int max) {
        List<String> matches = new ArrayList<String>();
        for (String topic : (List<String>) availableTopicChoicesModel.getObject()) {
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
        for (String topic : (List<String>) availableTopicChoicesModel.getObject()) {
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
        elementChanged(propPath, target);
        topicToAdd = null;
        addTopicButton.setEnabled(false);
        target.addComponent(addTopicButton);
        target.addComponent(topicChoiceList);
        target.addComponent(eoisDiv);
    }

    private void removeEoi(ElementOfInformation eoi, AjaxRequestTarget target) {
        eois.remove(eoi);
        elementChanged(propPath, target);
        target.addComponent(topicChoiceList);
        target.addComponent(eoisDiv);
    }

    private List<String> availableTopics(List<String>choices) {
        List<String> chosenTopics = new ArrayList<String>();
        for (ElementOfInformation eoi : eois) {
            chosenTopics.add(eoi.getTopic());
        }
        Collection availableTopics = CollectionUtils.subtract(choices, chosenTopics);
        List<String> results = new ArrayList<String>();
        results.addAll(availableTopics);
        Collections.sort(results);
        return results;
    }

}
