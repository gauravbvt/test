package com.mindalliance.channels.playbook.pages.forms.panels;

import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.Component;
import org.apache.log4j.Logger;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.Bean;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.project.Project;
import com.mindalliance.channels.playbook.ifm.model.PlaybookModel;
import com.mindalliance.channels.playbook.ifm.playbook.Playbook;
import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 28, 2008
 * Time: 3:15:33 PM
 */
// Not much to abstract...
abstract public class AbstractComponentPanel extends AbstractPlaybookPanel {

    public final static boolean SINGLE_SELECTION = true;
    protected static final boolean READONLY = true;
    protected static final boolean EDITABLE = false;

    protected boolean readOnly = false;
    protected ElementPanel parentPanel;
    protected String propPath; // path to the element's property which value is the component to be edited
    // protected WebMarkupContainer div;
    protected FeedbackPanel feedback;

    private Object editedComponent;

    public AbstractComponentPanel(String id, ElementPanel parentPanel, String propPath , boolean readOnly, FeedbackPanel feedback) {
        super(id);
        this.parentPanel = parentPanel;
        this.propPath = propPath;
        this.readOnly = readOnly;
        this.feedback = feedback;
        load();
        init();
    }

    protected FeedbackPanel getFeedback() {
        return feedback;
    }

    // ElementPanel

    public Ref getElement() {
        return parentPanel.getElement();
    }

    public Object getObject() {
        return getComponent();
    }

    public Object getComponent() {
        if (editedComponent == null) {
         editedComponent = RefUtils.get(getElement(), propPath);
        }
        return editedComponent;
    }

    public Object getParentObject() {
        return parentPanel.getObject();
    }

    public void elementChanged(String propPath, AjaxRequestTarget target) {
        parentPanel.elementChanged(propPath, target);
    }

    public void addOtherElement(Ref otherElement) {
        parentPanel.addOtherElement(otherElement);
    }

    public AbstractElementForm getTopElementPanel() {
        return parentPanel.getTopElementPanel();
    }

    public boolean isProjectPanel() {
        return parentPanel.isProjectPanel();
    }

    public boolean isModelPanel() {
        return parentPanel.isModelPanel();
    }

    public boolean isPlaybookPanel() {
        return parentPanel.isModelPanel();
    }

    public Project getProject() {
        return parentPanel.getProject();
    }

    public PlaybookModel getPlaybookModel() {
        return parentPanel.getPlaybookModel();
    }

    public Playbook getPlaybook() {
        return parentPanel.getPlaybook();
    }

    public Ref getScope() {
        return parentPanel.getScope();
    }

    public  void edit(Ref ref, AjaxRequestTarget target) {
        parentPanel.edit(ref, target);
    }
    

    // end ElementPanel

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setFeedbackPanel(FeedbackPanel feedback) {
        this.feedback = feedback;
    }

    protected void beforeLoad() {
        // do nothing
    }

    protected void load() {
    /*  div = new WebMarkupContainer("component");
      div.setOutputMarkupId(true);
      add(div);*/
    }


    public void onDetach() {
        try {
            Object component = getComponent();
            if ( component != null && component instanceof Bean )
                ((Bean)component).detach();
        } catch (RuntimeException e) {
            Logger.getLogger(this.getClass()).error("Error detaching " + getElement() + "'s " + propPath);
            throw e;
        }
        super.onDetach();
    }

    protected void addReplaceable(Component component) {
        addReplaceableTo(component, this);
    }

    protected void addReplaceableTo(Component component, WebMarkupContainer container) {
        component.setOutputMarkupId(true);
        container.addOrReplace(component);
        if (isReadOnly()) component.setEnabled(false);
    }


}
