package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.ifm.definition.*;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 19, 2008
 * Time: 2:38:10 PM
 */
public class AgentDefinitionPanel  extends AbstractDefinitionPanel {

    protected AgentDefinition agentDefinition;
    protected AjaxCheckBox anyRoleCheckBox;
    protected WebMarkupContainer rolesDiv;
    protected DynamicFilterTree rolesTree;
    protected AjaxCheckBox anyResourceCheckBox;
    protected WebMarkupContainer resourceSpecDiv;
    protected ResourceSpecificationPanel resourceSpecPanel;
    protected AjaxCheckBox anyOrganizationCheckBox;
    protected WebMarkupContainer organizationSpecificationDiv;
    protected OrganizationSpecificationPanel organizationSpecificationPanel;
    protected AjaxCheckBox anyJurisdictionCheckBox;
    protected WebMarkupContainer jurisdictionDefinitionDiv;
    protected LocationDefinitionPanel jurisdictionDefinitionPanel;

    public AgentDefinitionPanel(String id, AbstractPlaybookPanel parentPanel, String propPath) {
        super(id, parentPanel, propPath);
    }

    protected void load() {
        super.load();
        agentDefinition = (AgentDefinition)getComponent();
        anyRoleCheckBox = new AjaxCheckBox("anyRole", new Model<Boolean>(agentDefinition.getRoles().isEmpty())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyRole = anyRoleCheckBox.getModelObject();
                if (anyRole) {
                    setProperty("roles", new ArrayList<Ref>(), target);
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
                setProperty("roles", selected, target);
            }
        };
        addReplaceableTo(rolesTree, rolesDiv);

        anyResourceCheckBox = new AjaxCheckBox("anyResource", new Model<Boolean>(agentDefinition.getResourceSpec().matchesAll())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyResource = anyResourceCheckBox.getModelObject();
                if (anyResource) {
                    setProperty("resourceSpec", new ResourceSpecification(), target);
                    resourceSpecPanel = new ResourceSpecificationPanel("resourceSpec", AgentDefinitionPanel.this, propPath+".resourceSpec");
                    addReplaceableTo(resourceSpecPanel, resourceSpecDiv);
                }
                setVisibility(resourceSpecDiv, !anyResource, target);
            }
        };
        addReplaceable(anyResourceCheckBox);
        resourceSpecDiv = new WebMarkupContainer("resourceSpecDiv");
        setVisibility(resourceSpecDiv, !agentDefinition.getResourceSpec().matchesAll());
        addReplaceable(resourceSpecDiv);
        resourceSpecPanel = new ResourceSpecificationPanel("resourceSpec", this, propPath+".resourceSpec");
        addReplaceableTo(resourceSpecPanel, resourceSpecDiv);

        anyOrganizationCheckBox = new AjaxCheckBox("anyOrganization", new Model<Boolean>(agentDefinition.getOrganizationSpec().matchesAll())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyOrganization = anyOrganizationCheckBox.getModelObject();
                if (anyOrganization) {
                    setProperty("organizationSpec", new OrganizationSpecification(), target);
                    organizationSpecificationPanel = new OrganizationSpecificationPanel("organizationSpecification", AgentDefinitionPanel.this, propPath+".organizationSpec");
                    addReplaceableTo(organizationSpecificationPanel, organizationSpecificationDiv);
                }
                setVisibility(organizationSpecificationDiv, !anyOrganization, target);
            }
        };
        addReplaceable(anyOrganizationCheckBox);
        organizationSpecificationDiv = new WebMarkupContainer("organizationSpecificationDiv");
        setVisibility(organizationSpecificationDiv, !agentDefinition.getOrganizationSpec().matchesAll());
        addReplaceable(organizationSpecificationDiv);
        organizationSpecificationPanel = new OrganizationSpecificationPanel("organizationSpecification", this, propPath+".organizationSpec");
        addReplaceableTo(organizationSpecificationPanel, organizationSpecificationDiv);

        anyJurisdictionCheckBox = new AjaxCheckBox("anyJurisdiction", new Model<Boolean>(agentDefinition.getJurisdictionDefinition().matchesAll())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyLocation = anyJurisdictionCheckBox.getModelObject();
                if (anyLocation) {
                    setProperty("jurisdictionDefinition", new LocationDefinition(), target);
                    jurisdictionDefinitionPanel = new LocationDefinitionPanel("jurisdictionDefinition", AgentDefinitionPanel.this, propPath+".jurisdictionDefinition");
                    addReplaceableTo(jurisdictionDefinitionPanel, jurisdictionDefinitionDiv);
                }
                setVisibility(jurisdictionDefinitionDiv, !anyLocation, target);
            }
        };
        addReplaceable(anyJurisdictionCheckBox);
        jurisdictionDefinitionDiv = new WebMarkupContainer("jurisdictionDefinitionDiv");
        setVisibility(jurisdictionDefinitionDiv, !agentDefinition.getJurisdictionDefinition().matchesAll());
        addReplaceable(jurisdictionDefinitionDiv);
        jurisdictionDefinitionPanel = new LocationDefinitionPanel("jurisdictionDefinition", this, propPath+".jurisdictionDefinition");
        addReplaceableTo(jurisdictionDefinitionPanel, jurisdictionDefinitionDiv);
     }

}
