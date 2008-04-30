package com.mindalliance.channels.playbook.pages.forms;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import com.mindalliance.channels.playbook.ref.Ref;

import java.util.List;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 23, 2008
 * Time: 4:43:09 PM
 */
abstract public class AbstractElementForm extends Panel {

    protected Ref element;
    protected Form elementForm;
    protected List tabs = new ArrayList();
    protected AjaxTabbedPanel tabbedPanel;

    public AbstractElementForm(String id, Ref element) {
        super(id);
        this.element = element;
        element.begin();
        load();
        init();
    }

    protected void load() {
        elementForm = new Form("elementForm") {
            @Override
            public boolean isTransparentResolver() { // so that the children fields will resolve under it
                return true;
            }
        };
        elementForm.removePersistentFormComponentValues(true);
        elementForm.setOutputMarkupId(true);
        loadTabs();
        tabbedPanel = new AjaxTabbedPanel("tabbedPanel", tabs);
        tabbedPanel.setOutputMarkupId(true);
        elementForm.add(tabbedPanel);
        addOrReplace(elementForm);
    }

    protected void init() {
        this.add(new SimpleAttributeModifier("class", "element"));
        this.setOutputMarkupId(true);
    }

    public void reset() {
        element.begin(); // make sure element stays in session
        load();
        init();
    }

    public void terminate() { // quietly commit element if modified else kick it out of the session
        if (element.isModified()) {
            element.commit();
        }
        else {
            element.reset();
        }
    }

    public boolean isElementModified() {
        return element.isModified();
    }


    abstract void loadTabs();

}
