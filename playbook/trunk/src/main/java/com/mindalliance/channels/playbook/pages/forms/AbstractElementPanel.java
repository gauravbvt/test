package com.mindalliance.channels.playbook.pages.forms;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import com.mindalliance.channels.playbook.ref.Ref;

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

    protected void addElementField(FormComponent field) {
        fields.add(field);
        field.setRequired(true);
        field.setPersistent(false);
        field.setOutputMarkupId(true);
        field.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.addComponent(feedback);
            }
        });
        elementForm.add(field);
    }

    protected void addComponentPanel(AbstractComponentPanel componentPanel, String propName) {
        components.put(propName, componentPanel);
        elementForm.add(componentPanel);
    }

    protected void addRefListPanel(Panel refListPanel) {
        refListPanels.add(refListPanel);
        elementForm.add(refListPanel);
    }

    public void refresh(AjaxRequestTarget target) {
        for (FormComponent field : fields) {
            target.addComponent(field);
        }
        for (String propName : components.keySet()) {
            element.changed(propName); // forces an immediate persist to session - needed to ensure component and element are in sync
            AbstractComponentPanel componentPanel = components.get(propName);
            componentPanel.refresh(target);
        }
        for (Panel refListPanel : refListPanels) {
            target.addComponent(refListPanel);
        }
    }
}
