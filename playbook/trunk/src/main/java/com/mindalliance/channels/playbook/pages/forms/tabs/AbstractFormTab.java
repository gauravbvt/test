package com.mindalliance.channels.playbook.pages.forms.tabs;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.log4j.Logger;
import com.mindalliance.channels.playbook.ref.Ref;

import java.util.List;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 23, 2008
 * Time: 7:43:46 PM
 */
public class AbstractFormTab extends Panel {

    protected Ref element;
    protected FeedbackPanel feedback;
    private List<FormComponent> inputFields = new ArrayList<FormComponent>();

    public AbstractFormTab(String id, Ref element) {
        super(id);
        this.element = element;
        load();
        init();
    }

    protected void load() {
        // feedback panel
        feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);
    }

    protected void init() {
       for (final FormComponent inputField : inputFields) {
           inputField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
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

    protected void addInputField(FormComponent inputField) {
        inputFields.add(inputField);
        add(inputField);
    }
    
}
