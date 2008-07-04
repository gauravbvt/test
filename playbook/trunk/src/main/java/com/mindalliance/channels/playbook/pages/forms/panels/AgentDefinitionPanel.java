package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.definition.AgentDefinition;
import com.mindalliance.channels.playbook.ifm.definition.OrganizationSpecification;
import com.mindalliance.channels.playbook.ifm.definition.LocationDefinition;
import com.mindalliance.channels.playbook.ifm.definition.RelationshipDefinition;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.Model;
import org.apache.wicket.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 30, 2008
 * Time: 3:46:21 PM
 */
public class AgentDefinitionPanel extends AbstractDefinitionPanel {

    protected AgentDefinition agentDefinition;
    protected AjaxCheckBox anyRoleCheckBox;
    protected WebMarkupContainer rolesDiv;
    protected DynamicFilterTree rolesTree;
    protected AjaxCheckBox anyOrganizationCheckBox;
    protected WebMarkupContainer organizationSpecificationDiv;
    protected OrganizationSpecificationPanel organizationSpecificationPanel;
    protected AjaxCheckBox anyLocationCheckBox;
    protected WebMarkupContainer locationDefinitionDiv;
    protected LocationDefinitionPanel locationDefinitionPanel;
    protected AjaxCheckBox anyJurisdictionCheckBox;
    protected WebMarkupContainer jurisdictionDefinitionDiv;
    protected LocationDefinitionPanel jurisdictionDefinitionPanel;
    protected AjaxCheckBox anyRelationshipCheckBox;
    protected WebMarkupContainer relationshipDefinitionsDiv;
    protected ListChoice relationshipDefinitionsChoice;
    protected Button addRelationshipDefinitionButton;
    protected Button removeRelationshipDefinitionButton;
    protected WebMarkupContainer relationshipDefinitionDiv;
    protected Component relationshipDefinitionPanel;
    protected RelationshipDefinition selectedRelationshipDefinition;

    public AgentDefinitionPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        agentDefinition = (AgentDefinition)getComponent();
        anyRoleCheckBox = new AjaxCheckBox("anyRole", new Model((Boolean)agentDefinition.getRoles().isEmpty())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyRole = (Boolean)anyRoleCheckBox.getModelObject();
                if (anyRole) {
                    setProperty("roles", new ArrayList<Ref>());
                }
                setVisibility(rolesDiv, !anyRole, target);
            }
        };
        addReplaceable(anyRoleCheckBox);
        rolesDiv = new WebMarkupContainer("rolesDiv");
        setVisibility(rolesDiv, !agentDefinition.getRoles().isEmpty());
        addReplaceable(rolesDiv);
        rolesTree = new DynamicFilterTree("roles",
                                          new RefPropertyModel(getElement(), propPath+".roles"),
                                          new RefQueryModel(getProject(), new Query("findAllTypes", "Role"))){
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selected = rolesTree.getNewSelections();
                setProperty("roles", selected);
            }
        };
        addReplaceableTo(rolesTree, rolesDiv);
        anyOrganizationCheckBox = new AjaxCheckBox("anyOrganization", new Model((Boolean)agentDefinition.getOrganizationSpecification().matchesAll())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyOrganization = (Boolean)anyOrganizationCheckBox.getModelObject();
                if (anyOrganization) {
                    setProperty("organizationSpecification", new OrganizationSpecification());
                    organizationSpecificationPanel = new OrganizationSpecificationPanel("organizationSpecification", AgentDefinitionPanel.this, propPath+".organizationSpecification", isReadOnly(), feedback);
                    addReplaceableTo(organizationSpecificationPanel, organizationSpecificationDiv);
                }
                setVisibility(organizationSpecificationDiv, !anyOrganization, target);
            }
        };
        addReplaceable(anyOrganizationCheckBox);
        organizationSpecificationDiv = new WebMarkupContainer("organizationSpecificationDiv");
        setVisibility(organizationSpecificationDiv, agentDefinition.getOrganizationSpecification().matchesAll());
        addReplaceable(organizationSpecificationDiv);
        organizationSpecificationPanel = new OrganizationSpecificationPanel("organizationSpecification", this, propPath+".organizationSpecification", isReadOnly(), feedback);
        addReplaceableTo(organizationSpecificationPanel, organizationSpecificationDiv);
        anyLocationCheckBox = new AjaxCheckBox("anyLocation", new Model((Boolean)agentDefinition.getLocationDefinition().matchesAll())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyLocation = (Boolean)anyLocationCheckBox.getModelObject();
                if (anyLocation) {
                    setProperty("locationDefinition", new LocationDefinition());
                    locationDefinitionPanel = new LocationDefinitionPanel("locationDefinition", AgentDefinitionPanel.this, propPath+".locationDefinition", isReadOnly(), feedback);
                    addReplaceableTo(locationDefinitionPanel, locationDefinitionDiv);
                }
                setVisibility(locationDefinitionDiv, !anyLocation, target);
            }
        };
        addReplaceable(anyLocationCheckBox);
        locationDefinitionDiv = new WebMarkupContainer("locationDefinitionDiv");
        setVisibility(locationDefinitionDiv, agentDefinition.getLocationDefinition().matchesAll());
        addReplaceable(locationDefinitionDiv);
        locationDefinitionPanel = new LocationDefinitionPanel("locationDefinition", this, propPath+".locationDefinition", isReadOnly(), feedback);
        addReplaceableTo(locationDefinitionPanel, locationDefinitionDiv);
        anyJurisdictionCheckBox = new AjaxCheckBox("anyJurisdiction", new Model((Boolean)agentDefinition.getJurisdictionDefinition().matchesAll())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyLocation = (Boolean)anyJurisdictionCheckBox.getModelObject();
                if (anyLocation) {
                    setProperty("jurisdictionDefinition", new LocationDefinition());
                    jurisdictionDefinitionPanel = new LocationDefinitionPanel("jurisdictionDefinition", AgentDefinitionPanel.this, propPath+".jurisdictionDefinition", isReadOnly(), feedback);
                    addReplaceableTo(jurisdictionDefinitionPanel, jurisdictionDefinitionDiv);
                }
                setVisibility(jurisdictionDefinitionDiv, !anyLocation, target);
            }
        };
        addReplaceable(anyJurisdictionCheckBox);
        jurisdictionDefinitionDiv = new WebMarkupContainer("jurisdictionDefinitionDiv");
        setVisibility(jurisdictionDefinitionDiv, agentDefinition.getJurisdictionDefinition().matchesAll());
        addReplaceable(jurisdictionDefinitionDiv);
        jurisdictionDefinitionPanel = new LocationDefinitionPanel("jurisdictionDefinition", this, propPath+".jurisdictionDefinition", isReadOnly(), feedback);
        addReplaceableTo(jurisdictionDefinitionPanel, jurisdictionDefinitionDiv);
        anyRelationshipCheckBox = new AjaxCheckBox("anyRelationship", new Model((Boolean)agentDefinition.getRelationshipDefinitions().isEmpty())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyRelationships = (Boolean)anyRelationshipCheckBox.getModelObject();
                if (anyRelationships) {
                    setProperty("relationshipDefinitions", new ArrayList<RelationshipDefinition>());
                }
                setVisibility(relationshipDefinitionsDiv, !anyRelationships, target);
            }
        };
        addReplaceable(anyRelationshipCheckBox);
        relationshipDefinitionsDiv = new WebMarkupContainer("relationshipDefinitionsDiv");
        setVisibility(relationshipDefinitionsDiv, agentDefinition.getRelationshipDefinitions().isEmpty());
        addReplaceable(relationshipDefinitionsDiv);
        relationshipDefinitionsChoice = new ListChoice("relationshipDefinitions",
                                                        new Model(selectedRelationshipDefinition),
                                                        new RefPropertyModel(getComponent(), "relationshipDefinitions"),
                                                        new ChoiceRenderer("summary"));
        relationshipDefinitionsChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                selectedRelationshipDefinition = (RelationshipDefinition)relationshipDefinitionsChoice.getModelObject();
                setVisibility(removeRelationshipDefinitionButton, selectedRelationshipDefinition != null, target);
                updateRelationshipDefinitionPanel();
                setVisibility(relationshipDefinitionDiv, selectedRelationshipDefinition != null, target);
            }
        });
        addReplaceableTo(relationshipDefinitionsChoice, relationshipDefinitionsDiv);
        addRelationshipDefinitionButton = new Button("addRelationshipDefinition");
        addRelationshipDefinitionButton.add(new AjaxEventBehavior("onclick") {
            protected void onEvent(AjaxRequestTarget target) {
                selectedRelationshipDefinition = new RelationshipDefinition();
                RefUtils.add(getElement(), propPath+".relationshipDefinitions", selectedRelationshipDefinition);
                setVisibility(removeRelationshipDefinitionButton, true, target);
                updateRelationshipDefinitionPanel();
                setVisibility(relationshipDefinitionDiv, true, target);
            }
        });
        addReplaceableTo(addRelationshipDefinitionButton, relationshipDefinitionsDiv);
        removeRelationshipDefinitionButton = new Button("deleteRelationshipDefinition");
        removeRelationshipDefinitionButton.add(new AjaxEventBehavior("onclick") {
            protected void onEvent(AjaxRequestTarget target) {
                RefUtils.remove(getElement(), propPath+".relationshipDefinitions", selectedRelationshipDefinition);
                selectedRelationshipDefinition = null;
                target.addComponent(relationshipDefinitionsChoice);
                setVisibility(removeRelationshipDefinitionButton, false, target);
                setVisibility(relationshipDefinitionDiv, false, target);
            }
        });
        addReplaceableTo(removeRelationshipDefinitionButton, relationshipDefinitionsDiv);
        relationshipDefinitionDiv = new WebMarkupContainer("relationshipDefinitionDiv");
        hide(relationshipDefinitionDiv);
        addReplaceableTo(relationshipDefinitionDiv, relationshipDefinitionsDiv);
        relationshipDefinitionPanel = new Label("relationshipDefinition", new Model("dummy"));
        addReplaceableTo(relationshipDefinitionPanel, relationshipDefinitionDiv);
    }

    private void updateRelationshipDefinitionPanel() {
        if (selectedRelationshipDefinition != null) {
            int index = agentDefinition.getRelationshipDefinitions().indexOf(selectedRelationshipDefinition);
            relationshipDefinitionPanel = new RelationshipDefinitionPanel("relationshipDefinition", this, propPath+".relationshipDefinitions["+index+"]", isReadOnly(), feedback);
        }
        else {
           relationshipDefinitionPanel = new Label("relationshipDefinition", new Model("dummy"));
        }
        addReplaceableTo(relationshipDefinitionPanel, relationshipDefinitionDiv);
    }

}
