package com.mindalliance.channels.playbook.pages.forms.tabs.policy;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.MultipleStringChooser;
import com.mindalliance.channels.playbook.pages.forms.panels.AgentSpecPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.project.environment.Policy;
import com.mindalliance.channels.playbook.ifm.Channels;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.model.Model;
import org.apache.wicket.AttributeModifier;

import java.util.List;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 10, 2008
 * Time: 8:26:54 PM
 */
public class PolicyPartiesTab extends AbstractFormTab {

    protected Policy policy;
    protected AjaxCheckBox anyRelationshipField;
    protected WebMarkupContainer relationshipsDiv;
    protected AgentSpecPanel sourceAgentSpecPanel;
    protected MultipleStringChooser relationshipNamesChooser;
    protected AgentSpecPanel recipientAgentSpecPanel;

    public PolicyPartiesTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        policy = (Policy)getElement().deref();
        sourceAgentSpecPanel = new AgentSpecPanel("sourceAgentSpec", this, "sourceAgentSpec", EDITABLE, feedback);
        addReplaceable(sourceAgentSpecPanel);
        relationshipsDiv = new WebMarkupContainer("relationshipsDiv");
        addReplaceable(relationshipsDiv);
        setRelationshipsVisibility();
        anyRelationshipField = new AjaxCheckBox("anyRelationship", new Model(policy.getRelationshipNames().isEmpty())) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean any = (Boolean)anyRelationshipField.getModelObject();
                if (any) {
                    policy.setRelationshipNames(new ArrayList<String>());
                    relationshipsDiv.add(new AttributeModifier("style", true, new Model("display:none")));
                }
                else {
                    relationshipsDiv.add(new AttributeModifier("style", true, new Model("display:block")));
                }
                target.addComponent(relationshipsDiv);
            }
        };
        addReplaceable(anyRelationshipField);
        relationshipNamesChooser = new MultipleStringChooser("relationshipNames", this, "relationshipNames", EDITABLE, feedback,
                                                              new RefQueryModel(Channels.instance(),new Query("findAllRelationshipNames")));
        addReplaceableTo(relationshipNamesChooser, relationshipsDiv);
        recipientAgentSpecPanel = new AgentSpecPanel("recipientAgentSpec", this, "recipientAgentSpec", EDITABLE, feedback);
        addReplaceable(recipientAgentSpecPanel);
    }

    private void setRelationshipsVisibility() {
        if (policy.getMediumTypes().isEmpty()) {
            relationshipsDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        } else {
            relationshipsDiv.add(new AttributeModifier("style", true, new Model("display:block")));
        }
    }



}