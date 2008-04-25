package com.mindalliance.channels.playbook.pages.forms;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IFormVisitorParticipant;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.Component;
import org.apache.log4j.Logger;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Referenceable;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 28, 2008
 * Time: 2:40:09 PM
 */
abstract public class AbstractElementPanel extends Panel {

    Ref element;
    FeedbackPanel feedback;
    Form elementForm;
    List<FormComponent> fields = new ArrayList<FormComponent>();
    Map<String, AbstractComponentPanel> components = new HashMap<String, AbstractComponentPanel>();
    List<Panel> refListPanels = new ArrayList<Panel>();

    public AbstractElementPanel(String id, Ref element) {
        super(id);
        this.element = element;
        element.begin(); // put element in session
        init();
        load();
    }

    protected void init() {
        this.add(new SimpleAttributeModifier("class", "element"));
    }

    protected Form getElementForm() {
        return elementForm;
    }

    protected void load() {
        // feedback panel
        feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);
        elementForm = new Form("elementForm") {
            @Override
            public boolean isTransparentResolver() { // so that the children fields will resolve under it
                return true;
            }
        };
        elementForm.removePersistentFormComponentValues(true);
        add(elementForm);
    }

    protected void addElementField(final FormComponent field) {
        fields.add(field);
        field.setRequired(true);
        field.setPersistent(false);
        field.setOutputMarkupId(true);
        field.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                updatedField(getFormComponent(), target);
                target.addComponent(feedback);
            }
            protected void onError(AjaxRequestTarget target, RuntimeException e) {
                Logger.getLogger(this.getClass()).error("Error updating "+ field + ": " + e);
                field.clearInput();
                target.addComponent(field);
                target.addComponent(feedback);
            }
        });
        elementForm.add(field);
    }

/*    private void validateFormComponents(AjaxRequestTarget target) {
        elementForm.visitFormComponents(new FormComponent.IVisitor() {
            public Object formComponent(IFormVisitorParticipant iFormVisitorParticipant) {
               FormComponent formComponent = (FormComponent)iFormVisitorParticipant;
               formComponent.validate();
                return formComponent;
            }
        });
    }*/

    // DEFAULT
    protected void updatedField(Component component, AjaxRequestTarget target) {
        // do nothing
    }

    protected void addComponentPanel(AbstractComponentPanel componentPanel, String propName) {
        components.put(propName, componentPanel);
        elementForm.add(componentPanel);
    }

    protected void addRefListPanel(Panel refListPanel) {
        refListPanels.add(refListPanel);
        refListPanel.setOutputMarkupId(true);
        elementForm.add(refListPanel);
    }

    protected String valueOf(Component field) {
        return (String)field.getModel().getObject();
    }

    protected Referenceable getElement() {
        return element.deref();
    }


}
