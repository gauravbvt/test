package com.mindalliance.channels.playbook.pages.forms;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.log4j.Logger;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Bean;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.project.Project;

import java.util.List;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 28, 2008
 * Time: 3:15:33 PM
 */
// Not much to abstract...
abstract public class AbstractComponentPanel extends Panel implements ElementPanel {

    protected boolean readOnly = false;
    protected Project project;
    protected ElementPanel parentPanel;     // element containing the component to be edited
    protected String propPath; // path to the element's property which value is the component to be edited
    WebMarkupContainer div;
    protected FeedbackPanel feedback;

    public AbstractComponentPanel(String id, ElementPanel parentPanel, String propPath , boolean readOnly, FeedbackPanel feedback) {
        super(id);
        this.parentPanel = parentPanel;
        this.propPath = propPath;
        this.readOnly = readOnly;
        this.feedback = feedback;
        load();
        init();
    }

    // ElementPanel

    public Ref getElement() {
        return parentPanel.getElement();
    }

    public void elementChanged(String propPath, AjaxRequestTarget target) {
        parentPanel.elementChanged(propPath, target);
    }

    public void addOtherElement(Ref otherElement) {
        parentPanel.addOtherElement(otherElement);
    }

    // end ElementPanel

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setFeedbackPanel(FeedbackPanel feedback) {
        this.feedback = feedback;
    }

    protected void init() {
        this.setOutputMarkupId(true);
        this.add(new SimpleAttributeModifier("class", "component"));
    }

    protected void load() {
      project = (Project)Project.current().deref();
      div = new WebMarkupContainer("component");
      div.setOutputMarkupId(true);
      add(div);
    }

    public void onDetach() {
        try {
            Object component = RefUtils.get(getElement(), propPath);
            if ( component != null && component instanceof Bean )
                ((Bean)component).detach();
        } catch (RuntimeException e) {
            Logger.getLogger(this.getClass()).error("Error detaching " + getElement() + "'s " + propPath);
            throw e;
        }
        super.onDetach();
    }

    public void addReplaceable(Component component) {
        component.setOutputMarkupId(true);
        div.addOrReplace(component);
        component.setEnabled(!this.isReadOnly());
    }

}
