package com.mindalliance.channels.playbook.pages.forms;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
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
abstract public class AbstractComponentPanel extends Panel {

    protected boolean readOnly = false;
    protected Project project;
    protected Ref element;     // element containing the component to be edited
    protected String propPath; // path to the element's property which value is the component to be edited
    WebMarkupContainer div;
    protected FeedbackPanel feedback;

    public AbstractComponentPanel(String id, Ref element, String propPath , boolean readOnly, FeedbackPanel feedback) {
        super(id);
        this.element = element;
        this.propPath = propPath;
        this.readOnly = readOnly;
        this.feedback = feedback;
        load();
        init();
    }

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

    protected void elementChanged() {
        int index = propPath.indexOf('.');
        String propName;
        if (index < 0) {
            propName = propPath;
        }
        else {
            propName = propPath.substring(0, index);
        }
        element.changed(propName);
    }

    public void onDetach() {
        Bean bean = (Bean) RefUtils.get(element, propPath);
        if ( bean != null )
            bean.detach();
        super.onDetach();
    }

    public void addReplaceable(Component component) {
        component.setOutputMarkupId(true);
        div.addOrReplace(component);
        component.setEnabled(!this.isReadOnly());
    }

}
