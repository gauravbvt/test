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
import com.mindalliance.channels.playbook.ifm.Channels;
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
    protected Label notApplicableLabel;
    protected WebMarkupContainer restrictedDiv;
    protected WebMarkupContainer mediumTypesDiv;
    protected AjaxCheckBox anyMediumField;
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
        notApplicableLabel = new Label("notApplicable", new Model("Not applicable"));
        addReplaceable(notApplicableLabel);
        restrictedDiv = new WebMarkupContainer("restrictedDiv");
        addReplaceable(restrictedDiv);
        setRestrictedVisibility();
        mediumTypesDiv = new WebMarkupContainer("mediumTypesDiv");
        addReplaceableTo(mediumTypesDiv, restrictedDiv);
        anyMediumField = new AjaxCheckBox("anyMedium", new Model(policy.getMediumTypes().isEmpty())) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean any = (Boolean) anyMediumField.getModelObject();
                if (any) {
                    RefUtils.set(policy, "mediumTypes", new ArrayList<Ref>());
                    mediumTypesDiv.add(new AttributeModifier("style", true, new Model("display:none")));
                }
                else {
                    mediumTypesDiv.add(new AttributeModifier("style", true, new Model("display:block")));
                }
                target.addComponent(mediumTypesDiv);
            }
        };
        setMediumTypesVisibility();
        addReplaceableTo(anyMediumField, restrictedDiv);
        mediumTypesTree = new DynamicFilterTree("mediumTypes", new RefPropertyModel(getElement(), "mediumTypes"),
                new RefQueryModel(getScope(), new Query("findAllTypes", "MediumType"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selectedTypes = mediumTypesTree.getNewSelections();
                RefUtils.set(getElement(), "mediumTypes", selectedTypes);
            }
        };
        addReplaceableTo(mediumTypesTree, mediumTypesDiv);
        purposesDiv = new WebMarkupContainer("purposesDiv");
        addReplaceableTo(purposesDiv, restrictedDiv);
        anyPurposeField = new AjaxCheckBox("anyPurpose", new Model(true)) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean any = (Boolean) anyPurposeField.getModelObject();
                if (any) {
                    RefUtils.set(policy, "purposes", new ArrayList<String>());
                    purposesDiv.add(new AttributeModifier("style", true, new Model("display:none")));
                }
                else {
                    purposesDiv.add(new AttributeModifier("style", true, new Model("display:block")));
                }
                target.addComponent(purposesDiv);
            }
        };
        setPurposesVisibility();
        addReplaceableTo(anyPurposeField, restrictedDiv);
        purposesChooser = new MultipleStringChooser("purposes", this, "purposes", EDITABLE, feedback,
                new RefQueryModel(getProject(), new Query("findAllPurposes")));       // TODO getProject(), not Channels.instance()
        addReplaceableTo(purposesChooser, purposesDiv);
    }

    private void setMediumTypesVisibility() {
        if (policy.getMediumTypes().isEmpty()) {
            mediumTypesDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        } else {
            mediumTypesDiv.add(new AttributeModifier("style", true, new Model("display:block")));
        }
    }


    private void setPurposesVisibility() {
        if (policy.getPurposes().isEmpty()) {
            purposesDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        } else {
            purposesDiv.add(new AttributeModifier("style", true, new Model("display:block")));
        }
    }

    private void setRestrictedVisibility() {
        if (policy.isForbidden()) {
            restrictedDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        } else {
            notApplicableLabel.add(new AttributeModifier("style", true, new Model("display:none")));
        }
    }
}
