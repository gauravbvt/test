package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 6:03:21 PM
 */
public abstract class AbstractPlaybookPanel extends Panel  implements ElementPanel {

    protected List<FormComponent> inputFields = new ArrayList<FormComponent>();
    protected Map<Component, List<Component>> dependencies = new HashMap<Component, List<Component>>();
    private static final long serialVersionUID = -3397328755932980127L;

    public AbstractPlaybookPanel(String id) {
        super(id);
    }

    public AbstractPlaybookPanel(String id, IModel model) {
        super(id, model);
    }

    abstract public FeedbackPanel getFeedback();

    abstract protected void addReplaceable(Component component);

    protected void init() {
        this.setOutputMarkupId(true);
        for (final FormComponent inputField : inputFields) {
            inputField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                private static final long serialVersionUID = 6239144101227811010L;

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

    protected void setVisibility(Component component, boolean visible) {
        if (visible) {
            display(component);
        }
        else {
            hide(component);
        }
    }

    protected void setVisibility(Component component, boolean visible, AjaxRequestTarget target) {
        setVisibility(component, visible);
        target.addComponent(component);
    }

    protected void hide(Component component) {
        component.add(new AttributeModifier("style", true, new Model<String>("display:none")));
    }

    protected void display(Component component) {
        component.add(new AttributeModifier("style", true, new Model<String>("display:block")));
    }

    protected void display(Component component, String style) {
        component.add(new AttributeModifier("style", true, new Model<String>("display:" + style)));
    }

    protected void toggle(CheckBox setCheckBox, CheckBox toggledCheckBox, AjaxRequestTarget target) {
        toggledCheckBox.setModelObject(!setCheckBox.getModelObject());
        target.addComponent(toggledCheckBox);
    }

    protected void enable(Component component, boolean enabled, AjaxRequestTarget target) {
        component.setEnabled(enabled);
        target.addComponent(component);
    }

    protected boolean isFresh(Ref ref) {
        return ref != null && ref.isFresh();
    }

}
