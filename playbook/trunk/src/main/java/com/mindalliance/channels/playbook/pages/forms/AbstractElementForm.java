package com.mindalliance.channels.playbook.pages.forms;

import com.mindalliance.channels.playbook.ifm.Channels;
import com.mindalliance.channels.playbook.ifm.playbook.Playbook;
import com.mindalliance.channels.playbook.ifm.project.Project;
import com.mindalliance.channels.playbook.ifm.taxonomy.Taxonomy;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.RefUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 23, 2008
 * Time: 4:43:09 PM
 */
public abstract class AbstractElementForm extends Panel implements ElementPanel {

    protected Ref element;
    protected Set<Ref> otherElements = new HashSet<Ref>();
    protected Form elementForm;
    protected List<ITab> tabs = new ArrayList<ITab>();
    protected Label typeLabel;
    protected AjaxTabbedPanel tabbedPanel;
    private static final long serialVersionUID = 6987033791125697755L;

    public AbstractElementForm(String id, Ref element) {
        super(id);
        this.element = element;
        element.begin();
        beforeLoad();
        load();
        init();
    }

    protected void beforeLoad() {
        // do nothing
    }

    protected void load() {
        typeLabel = new Label("type", element.getType());
        add(typeLabel);
        elementForm = new Form("elementForm") {
            private static final long serialVersionUID = 3207844118187786163L;

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
        add(new SimpleAttributeModifier("class", "element"));
        setOutputMarkupId(true);
    }

    // ElementPanel

    public Object getObject() {
        return element;
    }

    public Ref getElement() {
        return element;
    }

    public void elementChanged(String propPath, AjaxRequestTarget target) {
        RefUtils.changed(element, propPath);
    }

    public void addOtherElement(Ref otherElement) {
        otherElement.begin(); // make editable
        otherElements.add(otherElement);
    }

    public AbstractElementForm getTopElementPanel() {
        return this;
    }

    public boolean isProjectPanel() {
        return false;
    }

    public boolean isTaxonomyPanel() {
        return false;
    }

    public boolean isPlaybookPanel() {
        return false;
    }

    public boolean isReadOnly() {
        return element.isReadOnly();
    }

    public Project getProject() {
        throw new RuntimeException("No project identified to this panel");
    }

    public Taxonomy getTaxonomy() {
        throw new RuntimeException("No taxonomy identified to this panel");
    }

    public Playbook getPlaybook() {
        throw new RuntimeException("No playbook identified to this panel");
    }

    public Ref getScope() {
        if (isProjectPanel()) return getProject().getReference();
        if (isTaxonomyPanel()) return getTaxonomy().getReference();
        if (isPlaybookPanel()) return getPlaybook().getReference();
        return Channels.instance().getReference();
    }

    public void edit(Ref ref, AjaxRequestTarget target) {
        System.out.println("TODO: EDIT " + ref);
        if (ref != null) {
            // TODO - open breadcrumbed editor on agreement
        }
    }

    // end ElementPanel

    public void reset() {
        element.begin(); // make sure element stays in session
        for (Ref otherElement : otherElements) {
            otherElement.begin();
        }
        load();
        init();
    }

    // Called when detaching the form
    public void terminate() {
        if (!element.isModified()) {   // only release from session if *not* modified
            element.reset();  // removes unmodified element(s) from session and session releases locks if held
        }
        for (Ref otherElement : otherElements) {    // only release from session if *not* modified
            if (otherElement.isModified()) {
                otherElement.reset();
            }
        }
    }

    public boolean isElementModified() {
        return element.isModified();
    }

    protected void setProperty(String property, Object value) {
        RefUtils.set(getElement(), property, value);
    }

    protected void setProperty(String property, Object value, AjaxRequestTarget target) {
        setProperty(property, value);
        elementChanged(property, target);
    }

    protected Object getProperty(String property) {
        return (Object)RefUtils.get(getElement(), property);
    }

    abstract void loadTabs();

}
