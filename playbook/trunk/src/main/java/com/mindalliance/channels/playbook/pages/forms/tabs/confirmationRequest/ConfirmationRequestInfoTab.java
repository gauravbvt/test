package com.mindalliance.channels.playbook.pages.forms.tabs.confirmationRequest;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.tabs.informationAct.InformationActInfoTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.InformationPanel;
import com.mindalliance.channels.playbook.pages.forms.panels.AgentSpecPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ifm.playbook.ConfirmationRequest;
import com.mindalliance.channels.playbook.ifm.spec.AgentSpec;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.AttributeModifier;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 13, 2008
 * Time: 9:48:54 AM
 */
public class ConfirmationRequestInfoTab extends InformationActInfoTab {

    protected ConfirmationRequest confirmationRequest;
    protected AjaxCheckBox isAgentSpecCheckBox;
    protected AjaxCheckBox isAgentCheckBox;
    protected AgentSpecPanel agentSpecPanel;
    protected DynamicFilterTree agentTree;
    protected AgentSpec priorAgentSpec;
    protected Ref priorAgent;

    public ConfirmationRequestInfoTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        confirmationRequest = (ConfirmationRequest)getElement().deref();
        isAgentSpecCheckBox = new AjaxCheckBox("isAgentSpec", new Model(confirmationRequest.isSourceSpecified())) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean isSourceSpecified = (Boolean) isAgentSpecCheckBox.getModelObject();
                isAgentCheckBox.setModelObject(!isSourceSpecified);
                updateSource(isSourceSpecified, target);
                target.addComponent(isAgentCheckBox);
            }
        };
        addReplaceable(isAgentSpecCheckBox);
        isAgentCheckBox = new AjaxCheckBox("isAgent", new Model(!confirmationRequest.isSourceSpecified())) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean isSourceIdentified = (Boolean) isAgentCheckBox.getModelObject();
                isAgentSpecCheckBox.setModelObject(!isSourceIdentified);
                updateSource(!isSourceIdentified, target);
                target.addComponent(isAgentSpecCheckBox);
            }
        };
        addReplaceable(isAgentCheckBox);
        agentSpecPanel = new AgentSpecPanel("agentSpec", this, "sourceSpec", EDITABLE, feedback);
        addReplaceable(agentSpecPanel);
        agentTree = new DynamicFilterTree("agent", new RefPropertyModel(getElement(), "sourceAgent"),
                                          new RefQueryModel(getPlaybook(), new Query("findAllAgentsExcept", getElement(), "sourceAgent")),
                                          SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref agent = agentTree.getNewSelection();
                RefUtils.set(getElement(), "sourceAgent", agent);
            }
        };
        addReplaceable(agentTree);
        setSourceVisibility(confirmationRequest.isSourceSpecified());
        priorAgentSpec = confirmationRequest.getSourceSpec();
        priorAgent = confirmationRequest.getSourceAgent();
    }

    private void updateSource(boolean isSourceSpecified, AjaxRequestTarget target) {
        if (isSourceSpecified) {
           priorAgent = confirmationRequest.getSourceAgent();
           confirmationRequest.setSourceSpec(priorAgentSpec);
        }
        else {
           priorAgentSpec = confirmationRequest.getSourceSpec();
           confirmationRequest.setSourceSpec(priorAgentSpec);
           agentSpecPanel = new AgentSpecPanel("agentSpec", this, "sourceSpec", EDITABLE, feedback);
           addReplaceable(agentSpecPanel);
        }
        setSourceVisibility(isSourceSpecified);
        target.addComponent(agentSpecPanel);
        target.addComponent(agentTree);
    }

    private void setSourceVisibility(boolean isSourceSpecified) {
        if (isSourceSpecified) {
            agentSpecPanel.add(new AttributeModifier("style", true, new Model("display:block")));
            agentTree.add(new AttributeModifier("style", true, new Model("display:none")));
        }
        else {
            agentSpecPanel.add(new AttributeModifier("style", true, new Model("display:none")));
            agentTree.add(new AttributeModifier("style", true, new Model("display:block")));

        }
    }
}
