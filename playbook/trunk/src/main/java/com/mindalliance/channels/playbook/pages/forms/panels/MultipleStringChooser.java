package com.mindalliance.channels.playbook.pages.forms.panels;

import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;
import com.mindalliance.channels.playbook.support.components.AutoCompleteTextFieldWithChoices;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.ElementPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 13, 2008
 * Time: 9:52:16 PM
 */
public class MultipleStringChooser extends AbstractComponentPanel {

    private static final int MAX_SIZE = 30;
    private static final int MAX_ROWS = 4;

    protected IModel choices;

    protected AutoCompleteTextFieldWithChoices newStringField;
    protected Button addStringButton;
    protected ListChoice stringsChoice;
    protected String selectedString;
    protected String newString = "";
    protected Button deleteStringButton;
    protected Label fullStringLabel;


    public MultipleStringChooser(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback, IModel choices) {
        super(id, parentPanel, propPath, readOnly, feedback);
        this.choices = choices;
        doLoad();
    }

    protected void doLoad() {
        newStringField = new AutoCompleteTextFieldWithChoices("string",
                new Model(),
                choices);
        newStringField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget
                    target) {
                newString = RefUtils.summarize(newStringField.getModelObjectAsString(), Integer.MAX_VALUE);
            }
        });
        addReplaceable(newStringField);
        addStringButton = new Button("addString");
        addStringButton.add(new AjaxEventBehavior("onclick") {
            protected void onEvent(AjaxRequestTarget target) {
                if (!newString.isEmpty()) {
                    RefUtils.add(getElement(), propPath, newString);
                    newStringField.setModelObject("");
                    newString = "";
                    target.addComponent(newStringField);
                    target.addComponent(stringsChoice);
                }
            }
        });
        addReplaceable(addStringButton);
        stringsChoice = new ListChoice("strings", new Model(), new RefPropertyModel(getElement(), propPath),
                        new ChoiceRenderer() {
                            public String getDisplayValue(Object item) {
                                return RefUtils.summarize((String)item, MAX_SIZE);
                            }
                        });
        stringsChoice.setMaxRows(MAX_ROWS);
        stringsChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget
                    target) {
                selectedString = stringsChoice.getModelObjectAsString();
                updateFullStringLabel(target);
                updateDeleteButton(target);
            }
        });
        addReplaceable(stringsChoice);
        deleteStringButton = new Button("deleteString");
        deleteStringButton.add(new AjaxEventBehavior("onclick") {
            protected void onEvent(AjaxRequestTarget target) {
                if (selectedString != null) {
                    RefUtils.remove(getElement(), propPath, selectedString);
                    newString = selectedString;
                    newStringField.setModelObject(selectedString);
                    selectedString = null;
                    updateFullStringLabel(target);
                    updateDeleteButton(target);
                    target.addComponent(newStringField);
                    target.addComponent(stringsChoice);
                }
            }
        });
        deleteStringButton.setEnabled(false);
        addReplaceable(deleteStringButton);
        fullStringLabel = new Label("fullString", new Model());
        hide(fullStringLabel);
        addReplaceable(fullStringLabel);
    }

    private void updateFullStringLabel(AjaxRequestTarget target) {
        fullStringLabel.setModelObject(selectedString);
        setVisibility(fullStringLabel, selectedString != null, target);
    }

    private void updateDeleteButton(AjaxRequestTarget target) {
        deleteStringButton.setEnabled(selectedString != null);
        target.addComponent(deleteStringButton);
    }

}
