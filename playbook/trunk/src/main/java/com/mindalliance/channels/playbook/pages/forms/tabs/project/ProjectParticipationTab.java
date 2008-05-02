package com.mindalliance.channels.playbook.pages.forms.tabs.project;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractProjectElementFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.Participation;
import com.mindalliance.channels.playbook.ifm.Channels;
import com.mindalliance.channels.playbook.support.PlaybookApplication;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefModel;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.AttributeModifier;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 1, 2008
 * Time: 6:44:49 AM
 */
public class ProjectParticipationTab extends AbstractProjectElementFormTab {

    Ref selectedParticipation;
    Ref selectedUser;

    DynamicFilterTree usersTree;
    RefreshingView participationsView;
    Button addParticipationButton;
    WebMarkupContainer participationsDiv;
    WebMarkupContainer participationDiv;

    DynamicFilterTree personsTree;

    Channels channels;

    public ProjectParticipationTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        channels = (Channels) PlaybookApplication.current().getChannels().deref();
        // Users
        List<Ref> allUsers = channels.findUsersNotInProject(getElement());
        usersTree = new DynamicFilterTree("users", new Model(new ArrayList<Ref>()), new Model((Serializable) allUsers), true) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> newSelections = usersTree.getNewSelections();
                if (newSelections.size() > 0) {
                    selectedUser = newSelections.get(0);
                } else {
                    selectedUser = null;
                }
                addParticipationButton.setEnabled(selectedUser != null);
                target.addComponent(addParticipationButton);
            }

        };
        addReplaceable(usersTree);
        // Add participation
        addParticipationButton = new Button("addParticipation");
        addParticipationButton.setEnabled(false);
        addParticipationButton.add(new AjaxEventBehavior("onclick") {
            protected void onEvent(AjaxRequestTarget target) {
                addParticipation(target);
            }
        });
        addReplaceable(addParticipationButton);
        // Participations
        participationsDiv = new WebMarkupContainer("participationsDiv");
        participationsView = new RefreshingView("participations", new RefPropertyModel(getElement(), "participations")) {
            protected Iterator getItemModels() {
                List<Ref> participations = (List<Ref>) getModelObject();
                return new ModelIteratorAdapter(participations.iterator()) {
                    protected IModel model(Object object) {
                        return new RefModel(object);
                    }
                };
            }

            protected void populateItem(Item item) {
                final Ref participation = (Ref) item.getModelObject();
                AjaxLink participationLink = new AjaxLink("participationLink") {
                    public void onClick(AjaxRequestTarget target) {
                        selectedParticipation = participation;
                        addOtherElement(participation);
                        updateParticipationDiv(target);
                    }
                };
                item.add(participationLink);
                Label participantLabel = new Label("participant", new RefPropertyModel(participation, "user.userId"));
                item.add(participantLabel);
                AjaxLink participationDelete = new AjaxLink("deleteParticipation") {
                    public void onClick(AjaxRequestTarget target) {
                        RefUtils.remove(getElement(), "participations", participation);
                        participation.begin();
                        participation.delete();
                        selectedParticipation = null;
                        target.addComponent(participationsDiv);
                        updateParticipationDiv(target);
                    }
                };
                item.add(participationDelete);
            }
        };
        participationsDiv.add(participationsView);
        addReplaceable(participationsDiv);
        // Participation
        participationDiv = new WebMarkupContainer("participation");
        loadParticipationDiv();
        participationDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        addReplaceable(participationDiv);
    }

    private void loadParticipationDiv() {
        AjaxLink userLink = new AjaxLink("userLink") {
            public void onClick(AjaxRequestTarget target) {
                edit((Ref) RefUtils.get(selectedParticipation, "user"), target);
            }
        };
        participationDiv.addOrReplace(userLink);
        Label userLabel = new Label("user", new RefPropertyModel(selectedParticipation, "user.userId"));
        userLink.addOrReplace(userLabel);
        List<Ref> allPersons = getProject().findAllResourcesOfType("Person");
        List<Ref> personsSelection = new ArrayList<Ref>();
        if (selectedParticipation != null) {
            Ref person = (Ref) RefUtils.get(selectedParticipation, "person");
            if (person != null) personsSelection.add(person);
        }
        personsTree = new DynamicFilterTree("persons", new Model((Serializable) personsSelection), new Model((Serializable) allPersons), true) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> newSelections = personsTree.getNewSelections();
                if (newSelections.size() > 0) {
                    Ref selectedPerson = newSelections.get(0);
                    RefUtils.set(selectedParticipation, "person", selectedPerson);
                }
            }
        };
        participationDiv.addOrReplace(personsTree);
        CheckBox managerField = new CheckBox("manager", new RefPropertyModel(selectedParticipation, "manager"));
        managerField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                // Do nothing more
            }
        });
        if (selectedParticipation != null) {
            managerField.setEnabled((Boolean) RefUtils.get(selectedParticipation, "user.manager") == true);
        }
        participationDiv.addOrReplace(managerField);
    }

    private void addParticipation(AjaxRequestTarget target) {
        Participation newParticipation = new Participation();
        newParticipation.setProject(getElement());
        newParticipation.setUser(selectedUser);
        selectedParticipation = newParticipation.persist();
        addOtherElement(selectedParticipation); // let the owner getElement() form about this new persisted element
        RefUtils.add(getElement(), "participations", selectedParticipation);
        resetUsersTree(target);
        target.addComponent(participationsDiv);
        updateParticipationDiv(target);
    }

    private void updateParticipationDiv(AjaxRequestTarget target) {
        if (selectedParticipation == null) {
            participationDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        } else {
            loadParticipationDiv();
            participationDiv.add(new AttributeModifier("style", true, new Model("display:block")));
        }
        target.addComponent(participationDiv);

    }

    private void resetUsersTree(AjaxRequestTarget target) {
        List<Ref> allUsers = channels.findUsersNotInProject(getElement());
        usersTree.detach();
        usersTree.setChoices(new Model((Serializable) allUsers));
        usersTree.setSelections(new Model(new ArrayList<Ref>()));
        usersTree.modelChanged();
        selectedUser = null;
        addParticipationButton.setEnabled(false);
        target.addComponent(addParticipationButton);
        target.addComponent(usersTree);
    }

}
