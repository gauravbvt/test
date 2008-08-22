package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;

import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 20, 2008
 * Time: 3:21:21 PM
 */
public class ReferencesPanel extends AbstractComponentPanel {

    private static final int MAX_SIZE = 30;
    private static final int MAX_ROWS = 4;

    private IModel<List<Ref>> choices;
    private String property;
    private DynamicFilterTree referencesTree;
    private Button selectButton;
    private Button deselectButton;
    private ListChoice<Ref> referencesList;
    private Button editButton;


    public ReferencesPanel(String id, AbstractPlaybookPanel parentPanel, String property, IModel<List<Ref>> choices) {
        super(id, parentPanel, "");
        this.choices = choices;
        this.property = property;
        doLoad();
    }

    private void doLoad() {
        referencesTree = new DynamicFilterTree("choices", new Model<Ref>(),
                choices, SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                refreshButtons(target);
            }
        };
        addReplaceable(referencesTree);

        selectButton = new Button("select");
        selectButton.add(
                new AjaxEventBehavior("onclick") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        Ref choice = referencesTree.getNewSelection();
                        if (choice != null) {
                            RefUtils.add(getElement(), property, choice);
                            referencesList.setDefaultModelObject(choice);
                            target.addComponent(referencesList);
                            refreshButtons(target);
                        }
                    }
                });
        addReplaceable(selectButton);

        deselectButton = new Button("deselect");
        deselectButton.add(
                new AjaxEventBehavior("onclick") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        Ref selection = getSelection();
                        if (selection != null) {
                            RefUtils.remove(getElement(), property, selection);
                            referencesList.setDefaultModelObject(null);
                            refreshButtons(target);
                            target.addComponent(referencesList);
                        }
                    }
                });
        addReplaceable(deselectButton);

        referencesList = new ListChoice<Ref>("references",
                new Model<Ref>(),
                new RefPropertyModel(getElement(), property),
                new ChoiceRenderer<Ref>() {
                    @Override
                    public String getDisplayValue(Ref reference) {
                        return RefUtils.summarize(reference.deref().about(), MAX_SIZE);
                    }
                }
        );
        referencesList.setMaxRows(MAX_ROWS);
        referencesList.add(
                new AjaxFormComponentUpdatingBehavior("onchange") {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        refreshButtons(target);
                    }
                });
        addReplaceable(referencesList);

        editButton = new Button("edit");
        editButton.add(
                new AjaxEventBehavior("onclick") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        if (getSelection() != null) {
                            parentPanel.edit(getSelection(), target);
                        }
                    }
                });
        addReplaceable(editButton);
        updateEditButton();
        updateSelectButton();
        updateDeselectButton();
    }

    private void updateSelectButton() {
        Ref choice = referencesTree.getNewSelection();
        List<Ref> references = (List<Ref>) RefUtils.get(getElement(), property);
        selectButton.setEnabled(choice != null && !references.contains(choice));
    }

    private void updateDeselectButton() {
        List<Ref> references = (List<Ref>) RefUtils.get(getElement(), property);
        deselectButton.setEnabled(!references.isEmpty() && getSelection() != null);
    }

    private void updateEditButton() {
        setVisibility(editButton, getSelection() != null);
    }

    private Ref getSelection() {
        return (Ref)referencesList.getDefaultModelObject();
    }

    private void refreshButtons(AjaxRequestTarget target) {
        updateEditButton();
        updateSelectButton();
        updateDeselectButton();
        target.addComponent(deselectButton);
        target.addComponent(selectButton);
        target.addComponent(editButton);
    }

}
