package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.ifm.definition.RelationshipDefinition;
import com.mindalliance.channels.playbook.ifm.definition.ResourceSpecification;
import com.mindalliance.channels.playbook.ifm.definition.AgentSpecification;
import com.mindalliance.channels.playbook.support.components.AutoCompleteTextFieldWithChoices;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.model.Model;
import org.apache.wicket.Component;

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
    protected WebMarkupContainer resourceSpecificationDiv;
    protected Component resourceSpecificationPanel;

    public RelationshipDefinitionPanel(String id, AbstractPlaybookPanel parentPanel, String propPath) {
        super(id, parentPanel, propPath);
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

        anyAgentCheckBox = new AjaxCheckBox("anyAgent", new Model((Boolean)relationshipDefinition.getWithResourceSpec().matchesAll())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyAgent = (Boolean)anyAgentCheckBox.getModelObject();
                if (anyAgent) {
                    setProperty("withAgentSpecification", new AgentSpecification());
                    resourceSpecificationPanel = new ResourceSpecificationPanel("resourceSpecification", RelationshipDefinitionPanel.this, propPath+".withresourceSpecification");
                    addReplaceableTo(resourceSpecificationPanel, resourceSpecificationDiv);
                }
                setVisibility(resourceSpecificationDiv, !anyAgent, target);
            }
        };
        addReplaceable(anyAgentCheckBox);
        resourceSpecificationDiv = new WebMarkupContainer("resourceSpecificationDiv");
        setVisibility(resourceSpecificationDiv, !relationshipDefinition.getWithResourceSpec().matchesAll());
        addReplaceable(resourceSpecificationDiv);
        resourceSpecificationPanel = new ResourceSpecificationPanel("resourceSpecification", this, propPath+".withResourceSpecification");
        addReplaceableTo(resourceSpecificationPanel, resourceSpecificationDiv);
    }
}
