package com.mindalliance.channels.playbook.pages.forms;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.mindalliance.channels.playbook.ref.Ref;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 6:03:21 PM
 */
abstract public class AbstractPlaybookPanel extends Panel  implements ElementPanel {

    protected List<FormComponent> inputFields = new ArrayList<FormComponent>();
    protected Map<Component, List<Component>> dependencies = new HashMap<Component, List<Component>>();

    public AbstractPlaybookPanel(String id) {
        super(id);
    }

    public AbstractPlaybookPanel(String id, IModel model) {
        super(id, model);
    }

    abstract protected FeedbackPanel getFeedback();

    abstract protected void addReplaceable(Component component);

    protected void init() {
        this.setOutputMarkupId(true);
        for (final FormComponent inputField : inputFields) {
            inputField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    List<Component> dependents = dependencies.get(inputField);
                    if (dependents != null) {
                        for (Component dependent : dependents) {
                            target.addComponent(dependent);
                        }
                    }
                    target.addComponent(getFeedback());
                }

                protected void onError(AjaxRequestTarget target, RuntimeException e) {
                    Logger.getLogger(this.getClass()).error("Error updating " + inputField + ": " + e);
                    inputField.clearInput();
                    target.addComponent(inputField);
                    target.addComponent(getFeedback());
                }
            });
        }
    }

    protected void addInputField(FormComponent inputField, Component dependentField) {
        addInputField(inputField);
        List<Component> dependents = getDependentsOf(inputField);
        dependents.add(dependentField);
    }

    List<Component> getDependentsOf(Component component) {
        List<Component> dependents = dependencies.get(component);
        if (dependents == null) {
            dependents = new ArrayList<Component>();
            dependencies.put(component, dependents);
        }
        return dependents;
    }

    protected void addInputField(FormComponent inputField) {
        inputFields.add(inputField);
        addReplaceable(inputField);
    }




}
