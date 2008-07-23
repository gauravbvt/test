package com.mindalliance.channels.playbook.pages.forms.tabs;

import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.Component;
import org.apache.wicket.model.Model;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.ifm.project.Project;
import com.mindalliance.channels.playbook.ifm.taxonomy.Taxonomy;
import com.mindalliance.channels.playbook.ifm.playbook.Playbook;
import com.mindalliance.channels.playbook.support.RefUtils;

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
public class AbstractFormTab extends AbstractPlaybookPanel {

    protected static final boolean EDITABLE = false;
    protected static final boolean SINGLE_SELECTION = true;

    protected Label readOnlyLabel;
    protected FeedbackPanel feedback;
    protected AbstractElementForm elementForm;
    private List<FormComponent> inputFields = new ArrayList<FormComponent>();
    private Map<Component, List<Component>> dependencies = new HashMap<Component, List<Component>>();

    public AbstractFormTab(String id, AbstractElementForm elementForm) {
        super(id);
        this.elementForm = elementForm;
        beforeLoad();
        load();
        init();
    }

    public FeedbackPanel getFeedback() {
        return feedback;
    }

    protected void beforeLoad() {
        // do nothing
    }

    protected void load() {       
        // feedback panel
        String owner = getElement().getOwner();
        String reasonReadOnly;
        if (owner != null) {
           reasonReadOnly = "The element is being edited by " + owner + ". Any changes you make will not be saved."; 
        }
        else {
            reasonReadOnly = "This element can not be changed.";
        }
        readOnlyLabel = new Label("readOnly", new Model(reasonReadOnly));
        setVisibility(readOnlyLabel, getElement().isReadOnly());
        addReplaceable(readOnlyLabel);
        feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);
    }

    // ElementPanel

    public Object getObject() {
        return elementForm.getObject();
    }

    public Ref getElement() {
        return elementForm.getElement();
    }

    public void elementChanged(String propPath, AjaxRequestTarget target) {
        elementForm.elementChanged(propPath, target);
    }

    protected void setProperty(String propPath, Object value) {
        RefUtils.set(getElement(), propPath, value);
    }

    protected Object getProperty(String propPath) {
        return RefUtils.get(getElement(), propPath);
    }

    public void addOtherElement(Ref otherElement) {
        elementForm.addOtherElement(otherElement);
    }

    public AbstractElementForm getTopElementPanel() {
        return elementForm;
    }

    public boolean isProjectPanel() {
        return elementForm.isProjectPanel();
    }

    public boolean isTaxonomyPanel() {
        return elementForm.isTaxonomyPanel();
    }

    public boolean isPlaybookPanel() {
        return elementForm.isPlaybookPanel();
    }

    public boolean isReadOnly() {
        return getElement().isReadOnly();
    }

    public Project getProject() {
        return elementForm.getProject();
    }

    public Taxonomy getTaxonomy() {
        return elementForm.getTaxonomy();
    }

    public Playbook getPlaybook() {
        return elementForm.getPlaybook();
    }

    public Ref getScope() // either Channels.instance(), a Project, a Taxonomy or a Playbook
    {
        return elementForm.getScope();
    }

    public void edit(Ref ref, AjaxRequestTarget target) {
        elementForm.edit(ref, target);
    }

    // end ElementPanel


    protected void addReplaceable(Component component) {
        addReplaceableTo(component, this);
    }

    protected void addReplaceableTo(Component component, WebMarkupContainer container) {
        component.setOutputMarkupId(true);
        container.addOrReplace(component);
    }

}
