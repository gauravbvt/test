package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.ifm.definition.RelationshipDefinition;
import com.mindalliance.channels.playbook.ifm.definition.LocationDefinition;
import com.mindalliance.channels.playbook.ifm.definition.AgentSpecification;
import com.mindalliance.channels.playbook.ifm.Channels;
import com.mindalliance.channels.playbook.support.components.AutoCompleteTextFieldWithChoices;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.model.Model;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 30, 2008
 * Time: 7:31:19 PM
 */
public class RelationshipDefinitionPanel extends AbstractDefinitionPanel {

    protected RelationshipDefinition relationshipDefinition;
    protected AutoCompleteTextFieldWithChoices relationshipNameField;
    protected AjaxCheckBox anyAgentCheckBox;
    protected WebMarkupContainer agentSpecificationDiv;
    protected AgentSpecificationPanel agentSpecificationPanel;

    public RelationshipDefinitionPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        relationshipDefinition = (RelationshipDefinition)getComponent();
        relationshipNameField = new AutoCompleteTextFieldWithChoices("relationshipName",
                new RefPropertyModel(getElement(), propPath+".relationshipName"),
                new RefQueryModel(getProject(), new Query("findAllRelationshipNames")));
        relationshipNameField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                // nothing to do
            }
        });
        addReplaceable(relationshipNameField);

        anyAgentCheckBox = new AjaxCheckBox("anyAgent", new Model((Boolean)relationshipDefinition.getWithAgentSpecification().matchesAll())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyAgent = (Boolean)anyAgentCheckBox.getModelObject();
                if (anyAgent) {
                    setProperty("withAgentSpecification", new AgentSpecification());
                    agentSpecificationPanel = new AgentSpecificationPanel("agentSpecification", RelationshipDefinitionPanel.this, propPath+".withAgentSpecification", isReadOnly(), feedback);
                    addReplaceableTo(agentSpecificationPanel, agentSpecificationDiv);
                }
                setVisibility(agentSpecificationDiv, !anyAgent, target);
            }
        };
        addReplaceable(anyAgentCheckBox);
        agentSpecificationDiv = new WebMarkupContainer("agentSpecificationDiv");
        setVisibility(agentSpecificationDiv, relationshipDefinition.getWithAgentSpecification().matchesAll());
        addReplaceable(agentSpecificationDiv);
        agentSpecificationPanel = new AgentSpecificationPanel("agentSpecification", this, propPath+".withAgentSpecification", isReadOnly(), feedback);
        addReplaceableTo(agentSpecificationPanel, agentSpecificationDiv);
    }
}
