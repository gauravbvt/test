package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.ifm.info.ElementOfInformation;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    AutoCompleteTextField<String> adHocTopicField;
    WebMarkupContainer topicChoiceDiv;
    ListChoice<String> topicChoiceList;
    Button addTopicButton;
    WebMarkupContainer eoisDiv;
    RefreshingView eoisView;

    String topicToAdd;
    private static final long serialVersionUID = -2083107339838991260L;

    public EOIsPanel(String id, AbstractPlaybookPanel parentPanel, String propPath, IModel topicChoicesModel) {
        super(id, parentPanel, propPath);
        this.availableTopicChoicesModel = new RefQueryModel(this, new Query("availableTopics", topicChoicesModel));
        doLoad();
    }

    protected void doLoad() {  // load after setting availableTopicChoicesModel
        eois = getEois();
        if (eois == null) {
            eois = new ArrayList<ElementOfInformation>();
            RefUtils.set(getElement(), propPath, eois);
        }

        // Topic choices
        // add hoc topic
        adHocTopicField = new AutoCompleteTextField<String>("adHocTopic", new Model<String>()) {
            private static final long serialVersionUID = 7222015781276564838L;

            @Override
            protected Iterator<String> getChoices(String input) {
                return topicIterator(input, 10);
            }
        };
        adHocTopicField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 2947107379052903211L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                String topic = adHocTopicField.getDefaultModelObjectAsString();
                topicToAddInputed(topic, target);
            }
        });
        addReplaceable(adHocTopicField);
        // Topic choices
        topicChoiceDiv = new WebMarkupContainer("topicChoiceDiv");
        topicChoiceList = new ListChoice<String>("topicChoices", new Model<String>(), availableTopicChoicesModel);
        topicChoiceList.setMaxRows(4);
        topicChoiceList.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 2022319035477795720L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                topicToAdd = topicChoiceList.getDefaultModelObjectAsString();
                adHocTopicField.setModelObject("");
                target.addComponent(adHocTopicField);
            }
        });
        topicChoiceList.setMaxRows(4);
        topicChoiceDiv.add(topicChoiceList);
        addReplaceable(topicChoiceDiv);
        // Add button
        addTopicButton = new Button("addTopic");
        addTopicButton.add(new AjaxEventBehavior("onclick") {
            private static final long serialVersionUID = 6919490657377068019L;

            @Override
            protected void onEvent(AjaxRequestTarget target) {
                addNewEOI(target);
            }
        });
        addReplaceable(addTopicButton);
        // EOIs
        eoisDiv = new WebMarkupContainer("eoisDiv");
        eoisView = new RefreshingView<ElementOfInformation>("eois", new Model<Serializable>((Serializable)eois)) {
            private static final long serialVersionUID = -4119452880940422115L;

            @Override
            protected Iterator<IModel<ElementOfInformation>> getItemModels() {
                return new ModelIteratorAdapter<ElementOfInformation>(eois.iterator()) {
                    @Override
                    protected IModel<ElementOfInformation> model(ElementOfInformation eoi) {
                        return new Model<ElementOfInformation>(eoi);
                    }
                };
            }
            @Override
            protected void populateItem(Item<ElementOfInformation> item) {
                final ElementOfInformation eoi = item.getModelObject();
                Label topicLabel = new Label("topic", new Model<String>(eoi.getTopic()));
                item.add(topicLabel);
                final TextArea contentField = new TextArea<String>("content", new Model<String>(eoi.getContent()));
                contentField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    private static final long serialVersionUID =
                            -4770523741955929952L;

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        eoi.setContent(contentField.getDefaultModelObjectAsString());
                        elementChanged(propPath, target);
                    }
                });
                item.add(contentField);
                AjaxLink deleteLink = new AjaxLink("deleteEoi") {
                    private static final long serialVersionUID =
                            -296690121632797043L;

                    @Override
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

    @SuppressWarnings( { "unchecked" } )
    private List<ElementOfInformation> getEois() {
        return (List<ElementOfInformation>) getComponent();
    }

    private Iterator<String> topicIterator(String input, int max) {
        List<String> matches = new ArrayList<String>();
        for (String topic : getAvailableTopicChoices() ) {
            if (topic.toLowerCase().startsWith(input.toLowerCase())) {
                matches.add(topic);
                if (matches.size() >= max) break;
            }
        }
        return matches.iterator();
    }

    @SuppressWarnings( { "unchecked" } )
    private List<String> getAvailableTopicChoices() {
        return (List<String>) availableTopicChoicesModel.getObject();
    }

    private void topicToAddInputed(String topic, AjaxRequestTarget target) {
        String toAdd = topic.trim();
        if (toAdd.length() == 0) toAdd = null;
        topicToAdd = toAdd;
        updateTopicChoicesSelection();
        target.addComponent(topicChoiceList);
    }

    private void updateTopicChoicesSelection() {
        for (String topic : getAvailableTopicChoices() ) {
            if (topic.equalsIgnoreCase(topicToAdd)) {
                topicChoiceList.setModelObject(topic);
                topicChoiceList.modelChanged();
                break;
            }
        }
    }

    private void addNewEOI(AjaxRequestTarget target) {
        if (topicToAdd != null && !topicToAdd.isEmpty()) {
            adHocTopicField.setModelObject("");
            ElementOfInformation eoi = new ElementOfInformation();
            eoi.setTopic(topicToAdd);
            eois.add(eoi);
            elementChanged(propPath, target);
            topicToAdd = null;
            target.addComponent(adHocTopicField);
            target.addComponent(topicChoiceList);
            target.addComponent(eoisDiv);
        }
    }

    private void removeEoi(ElementOfInformation eoi, AjaxRequestTarget target) {
        eois.remove(eoi);
        elementChanged(propPath, target);
        target.addComponent(topicChoiceList);
        target.addComponent(eoisDiv);
    }

//    private List<String> availableTopics(List<String>choices) {
//        List<String> chosenTopics = new ArrayList<String>();
//        for (ElementOfInformation eoi : eois) {
//            chosenTopics.add(eoi.getTopic());
//        }
//        Collection availableTopics = CollectionUtils.subtract(choices, chosenTopics);
//        List<String> results = new ArrayList<String>();
//        results.addAll(availableTopics);
//        Collections.sort(results);
//        return results;
//    }

}
