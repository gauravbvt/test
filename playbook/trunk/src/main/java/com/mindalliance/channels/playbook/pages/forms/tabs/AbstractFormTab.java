package com.mindalliance.channels.playbook.pages.forms.tabs;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.log4j.Logger;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.AbstractComponentPanel;
import com.mindalliance.channels.playbook.ifm.project.Project;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 23, 2008
 * Time: 7:43:46 PM
 */
public class AbstractFormTab extends Panel {

    protected Ref element;
    protected Project project;
    protected FeedbackPanel feedback;
    private List<FormComponent> inputFields = new ArrayList<FormComponent>();
    private Map<Component, List<Component>> dependencies = new HashMap<Component, List<Component>>();

    public AbstractFormTab(String id, Ref element) {
        super(id);
        this.element = element;
        load();
        init();
    }

    protected void load() {       
        project = (Project)Project.current().deref();
        // feedback panel
        feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);
    }

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
                    target.addComponent(feedback);
                }

                protected void onError(AjaxRequestTarget target, RuntimeException e) {
                    Logger.getLogger(this.getClass()).error("Error updating " + inputField + ": " + e);
                    inputField.clearInput();
                    target.addComponent(inputField);
                    target.addComponent(feedback);
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

    protected void addReplaceable(Component component) {
        component.setOutputMarkupId(true);
        addOrReplace(component);
    }

    protected void edit(Ref ref, AjaxRequestTarget target) {
        System.out.println("TODO: EDIT agreement" + ref);
        if (ref != null) {
            // TODO - open breadcrumbed editor on agreement             
        }
    }

}
