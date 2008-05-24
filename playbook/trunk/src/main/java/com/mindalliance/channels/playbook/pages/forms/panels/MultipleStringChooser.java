package com.mindalliance.channels.playbook.pages.forms.panels;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
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

    protected IModel choices;

    protected AutoCompleteTextFieldWithChoices newStringField;
    protected AjaxButton addStringButton;
    protected ListChoice stringsChoice;
    protected String selectedString;
    protected String newString;
    protected AjaxButton deleteStringButton;


    public MultipleStringChooser(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback, IModel choices) {
        super(id, parentPanel, propPath , readOnly, feedback);
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
                newString = newStringField.getModelObjectAsString().trim().toLowerCase();
                updateAddButton(target);
            }
        });
        add(newStringField);
        addStringButton = new AjaxButton("addString") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                RefUtils.add(getElement(), propPath, newString);
                newStringField.clearInput();
                newString = null;
                updateAddButton(target);
                target.addComponent(newStringField);
                target.addComponent(stringsChoice);
            }
        };
        addStringButton.setEnabled(false);
        add(addStringButton);
        stringsChoice = new ListChoice("strings", new Model(), new RefPropertyModel(getElement(), propPath));
        stringsChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget
                    target) {
                selectedString = stringsChoice.getModelObjectAsString();
                updateDeleteButton(target);
            }
        });
        add(stringsChoice);
        deleteStringButton = new AjaxButton("deleteString") {
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                RefUtils.remove(getElement(), propPath, selectedString);
                selectedString = null;
                updateDeleteButton(target);
                target.addComponent(stringsChoice);
            }
        };
        add(deleteStringButton);
    }

    private void updateDeleteButton(AjaxRequestTarget target) {
        deleteStringButton.setEnabled(selectedString != null);
        target.addComponent(deleteStringButton);
    }

    private void updateAddButton(AjaxRequestTarget target) {
        addStringButton.setEnabled(newString != null);
        target.addComponent(addStringButton);
    }

}
