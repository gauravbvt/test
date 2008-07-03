package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ifm.definition.OrganizationDefinition;
import com.mindalliance.channels.playbook.ifm.definition.LocationDefinition;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 1, 2008
 * Time: 2:37:25 PM
 */
public class OrganizationDefinitionPanel extends AbstractDefinitionPanel {

    protected OrganizationDefinition organizationDefinition;
    protected AjaxCheckBox anyOrganizationTypeCheckBox;
    protected WebMarkupContainer organizationTypesDiv;
    protected DynamicFilterTree organizationTypesTree;
    protected AjaxCheckBox anyLocationCheckBox;
    protected WebMarkupContainer locationDefinitionDiv;
    protected LocationDefinitionPanel locationDefinitionPanel;
    protected AjaxCheckBox anyJurisdictionCheckBox;
    protected WebMarkupContainer jurisdictionDefinitionDiv;
    protected LocationDefinitionPanel jurisdictionDefinitionPanel;


    public OrganizationDefinitionPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        organizationDefinition = (OrganizationDefinition)getComponent();
        anyOrganizationTypeCheckBox = new AjaxCheckBox("anyOrganizationType", new Model((Boolean)organizationDefinition.getOrganizationTypes().isEmpty())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyOrganizationType = (Boolean)anyOrganizationTypeCheckBox.getModelObject();
                if (anyOrganizationType) {
                    setProperty("organizationTypes", new ArrayList<Ref>());
                }
                setVisibility(organizationTypesDiv, !anyOrganizationType, target);
            }
        };
        addReplaceable(anyOrganizationTypeCheckBox);
        organizationTypesDiv = new WebMarkupContainer("organizationTypesDiv");
        setVisibility(organizationTypesDiv, !organizationDefinition.getOrganizationTypes().isEmpty());
        addReplaceable(organizationTypesDiv);
        organizationTypesTree = new DynamicFilterTree("organizationTypes",
                                          new RefPropertyModel(getElement(), propPath+".organizationTypes"),
                                          new RefQueryModel(getProject(), new Query("findAllTypes", "OrganizationType"))){
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selected = organizationTypesTree.getNewSelections();
                setProperty("organizationTypes", selected);
            }
        };
        addReplaceableTo(organizationTypesTree, organizationTypesDiv);

        anyLocationCheckBox = new AjaxCheckBox("anyLocation", new Model((Boolean)organizationDefinition.getLocationDefinition().matchesAll())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyLocation = (Boolean)anyLocationCheckBox.getModelObject();
                if (anyLocation) {
                    setProperty("locationDefinition", new LocationDefinition());
                    locationDefinitionPanel = new LocationDefinitionPanel("locationDefinition", OrganizationDefinitionPanel.this, propPath+".locationDefinition", isReadOnly(), feedback);
                    addReplaceableTo(locationDefinitionPanel, locationDefinitionDiv);
                }
                setVisibility(locationDefinitionDiv, !anyLocation, target);
            }
        };
        addReplaceable(anyLocationCheckBox);
        locationDefinitionDiv = new WebMarkupContainer("locationDefinitionDiv");
        setVisibility(locationDefinitionDiv, organizationDefinition.getLocationDefinition().matchesAll());
        addReplaceable(locationDefinitionDiv);
        locationDefinitionPanel = new LocationDefinitionPanel("locationDefinition", this, propPath+".locationDefinition", isReadOnly(), feedback);
        addReplaceableTo(locationDefinitionPanel, locationDefinitionDiv);

        anyJurisdictionCheckBox = new AjaxCheckBox("anyJurisdiction", new Model((Boolean)organizationDefinition.getJurisdictionDefinition().matchesAll())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean anyLocation = (Boolean)anyJurisdictionCheckBox.getModelObject();
                if (anyLocation) {
                    setProperty("jurisdictionDefinition", new LocationDefinition());
                    jurisdictionDefinitionPanel = new LocationDefinitionPanel("jurisdictionDefinition", OrganizationDefinitionPanel.this, propPath+".jurisdictionDefinition", isReadOnly(), feedback);
                    addReplaceableTo(jurisdictionDefinitionPanel, jurisdictionDefinitionDiv);
                }
                setVisibility(jurisdictionDefinitionDiv, !anyLocation, target);
            }
        };
        addReplaceable(anyJurisdictionCheckBox);
        jurisdictionDefinitionDiv = new WebMarkupContainer("jurisdictionDefinitionDiv");
        setVisibility(jurisdictionDefinitionDiv, organizationDefinition.getJurisdictionDefinition().matchesAll());
        addReplaceable(jurisdictionDefinitionDiv);
        jurisdictionDefinitionPanel = new LocationDefinitionPanel("jurisdictionDefinition", this, propPath+".jurisdictionDefinition", isReadOnly(), feedback);
        addReplaceableTo(jurisdictionDefinitionPanel, jurisdictionDefinitionDiv);
    }
}
