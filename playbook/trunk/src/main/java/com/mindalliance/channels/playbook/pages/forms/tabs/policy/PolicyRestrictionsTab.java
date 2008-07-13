package com.mindalliance.channels.playbook.pages.forms.tabs.policy;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.panels.MultipleStringChooser;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.project.environment.Policy;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.AttributeModifier;

import java.util.List;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 26, 2008
 * Time: 3:31:51 PM
 */
public class PolicyRestrictionsTab extends AbstractFormTab {

    protected Policy policy;
    protected WebMarkupContainer mediumTypesDiv;
    protected AjaxCheckBox anyMediumField;
    protected Label mediumLegendLabel;
    protected Label anyMediumLabel;
    protected Label purposesLegendLabel;
    protected Label anyPurposeLabel;
    protected DynamicFilterTree mediumTypesTree;
    protected WebMarkupContainer purposesDiv;
    protected AjaxCheckBox anyPurposeField;
    protected MultipleStringChooser purposesChooser;

    public PolicyRestrictionsTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        policy = (Policy) getElement().deref();
        setLabels(policy);
        addReplaceable(mediumLegendLabel);
        anyMediumField = new AjaxCheckBox("anyMedium", new Model(policy.getMediumTypes().isEmpty())) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean any = (Boolean) anyMediumField.getModelObject();
                if (any) {
                    RefUtils.set(policy, "mediumTypes", new ArrayList<Ref>());
                    mediumTypesDiv.add(new AttributeModifier("style", true, new Model("display:none")));
                } else {
                    mediumTypesDiv.add(new AttributeModifier("style", true, new Model("display:block")));
                }
                target.addComponent(mediumTypesDiv);
            }
        };
        addReplaceable(anyMediumField);
        addReplaceable(anyMediumLabel);
        mediumTypesDiv = new WebMarkupContainer("mediumTypesDiv");
        addReplaceable(mediumTypesDiv);
        setVisibility(mediumTypesDiv, !policy.getMediumTypes().isEmpty());
        mediumTypesTree = new DynamicFilterTree("mediumTypes", new RefPropertyModel(getElement(), "mediumTypes"),
                new RefQueryModel(getScope(), new Query("findAllTypes", "MediumType"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selectedTypes = mediumTypesTree.getNewSelections();
                RefUtils.set(getElement(), "mediumTypes", selectedTypes);
            }
        };
        addReplaceableTo(mediumTypesTree, mediumTypesDiv);
        addReplaceable(purposesLegendLabel);
        anyPurposeField = new AjaxCheckBox("anyPurpose", new Model(((List<Ref>) RefUtils.get(policy, "purposes")).isEmpty())) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean any = (Boolean) anyPurposeField.getModelObject();
                if (any) {
                    RefUtils.set(policy, "purposes", new ArrayList<String>());
                }
                setVisibility(purposesDiv, !any, target);
            }
        };
        addReplaceable(anyPurposeLabel);
        purposesDiv = new WebMarkupContainer("purposesDiv");
        addReplaceable(purposesDiv);
        setVisibility(purposesDiv, !policy.getPurposes().isEmpty());
        addReplaceable(anyPurposeField);
        purposesChooser = new MultipleStringChooser("purposes", this, "purposes", EDITABLE, feedback,
                new RefQueryModel(getProject(), new Query("findAllPurposes")));
        addReplaceableTo(purposesChooser, purposesDiv);
    }

    private void setLabels(Policy policy) {
        if (policy.isAllowing()) {
            mediumLegendLabel = new Label("mediumLegend","Sharing is allowed only when using one of these media");
            anyMediumLabel = new Label("anyMediumLabel","no restriction");
            purposesLegendLabel = new Label("purposesLegend","and only for one of these purposes");
            anyPurposeLabel = new Label("anyPurposeLabel","no restriction");
        } else {
            mediumLegendLabel = new Label("mediumLegend","Sharing is forbidden except when using any of these media");
            anyMediumLabel = new Label("anyMediumLabel","no exception");
            purposesLegendLabel = new Label("purposesLegend","and except for any of these purposes");
            anyPurposeLabel = new Label("anyPurposeLabel","no exception");
        }
    }

}
