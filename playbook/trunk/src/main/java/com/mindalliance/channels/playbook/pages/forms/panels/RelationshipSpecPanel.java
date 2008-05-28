package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.components.AutoCompleteTextFieldWithChoices;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.Channels;
import com.mindalliance.channels.playbook.ifm.spec.Spec;
import com.mindalliance.channels.playbook.ifm.spec.RelationshipSpec;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.model.Model;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 23, 2008
 * Time: 11:14:07 AM
 */
public class RelationshipSpecPanel extends AbstractSpecComponentPanel {

    protected RelationshipSpec relationshipSpec;

    protected AutoCompleteTextFieldWithChoices relationshipNameField;
    protected DynamicFilterTree agentTree;
    protected WebMarkupContainer fromToDiv;
    protected CheckBox fromSpecifiedCheckbox;
    protected CheckBox toSpecifiedCheckbox;
    protected Label fromRelationshipString;
    protected Label toRelationshipString;
    protected Label toAgentString;
    protected Label fromAgentString;

    public RelationshipSpecPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        relationshipSpec = (RelationshipSpec) getComponent();
        relationshipNameField = new AutoCompleteTextFieldWithChoices("relationshipName",
                new RefPropertyModel(getComponent(), "relationshipName"),
                new RefQueryModel(Channels.instance(), new Query("findAllRelationshipNames")));
        relationshipNameField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                updateFromToChoice(target);
                elementChanged(propPath + ".relationshipName", target);
            }
        });
        addReplaceable(relationshipNameField);
        agentTree = new DynamicFilterTree("agent", new RefPropertyModel(getComponent(), "agent"),
                new RefQueryModel(getScope(), new Query("findAllAgents")), SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selected = agentTree.getNewSelection();
                RefUtils.set(getComponent(), "agent", selected);
                elementChanged(propPath + ".agent", target);
                updateFromToChoice(target);
            }
        };
        addReplaceable(agentTree);
        fromToDiv = new WebMarkupContainer("fromTo");
        fromSpecifiedCheckbox = new CheckBox("fromSpecified", new Model());
        fromSpecifiedCheckbox.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                boolean fromSpecified = (Boolean) fromSpecifiedCheckbox.getModelObject();
                RefUtils.set(getElement(), propPath + ".relationshipFromSpecified", fromSpecified);
                toSpecifiedCheckbox.setModelObject(!fromSpecified);
                updateFromToChoice(target);
                target.addComponent(toSpecifiedCheckbox);
            }
        });
        addReplaceableTo(fromSpecifiedCheckbox, fromToDiv);
        toSpecifiedCheckbox = new CheckBox("toSpecified", new Model());
        toSpecifiedCheckbox.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                boolean toSpecified = (Boolean) toSpecifiedCheckbox.getModelObject();
                RefUtils.set(getElement(), propPath + ".relationshipFromSpecified", !toSpecified);
                fromSpecifiedCheckbox.setModelObject(!toSpecified);
                updateFromToChoice(target);
                target.addComponent(fromSpecifiedCheckbox);
            }
        });
        addReplaceableTo(toSpecifiedCheckbox,fromToDiv);
        fromToDiv.add(new AttributeModifier("style", new Model("display:none")));
        fromRelationshipString = new Label("fromRelationshipString", new Model());
        addReplaceableTo(fromRelationshipString, fromToDiv);
        toRelationshipString = new Label("toRelationshipString", new Model());
        addReplaceableTo(toRelationshipString, fromToDiv);
        fromAgentString = new Label("fromAgentString", new Model());
        addReplaceableTo(fromAgentString, fromToDiv);
        toAgentString = new Label("toAgentString", new Model());
        addReplaceableTo(toAgentString, fromToDiv);
        setFromToComponents();
        setVisibility();
        addReplaceable(fromToDiv);
    }

    @Override
    protected String getAnyLabelString() {
        return "any relationship";
    }

    protected Spec makeNewSpec() {
        return new RelationshipSpec();
    }

    private void updateFromToChoice(AjaxRequestTarget target) {
        setFromToComponents();
        setVisibility();
        target.addComponent(fromSpecifiedCheckbox);
        target.addComponent(toSpecifiedCheckbox);
        target.addComponent(toRelationshipString);
        target.addComponent(fromRelationshipString);
        target.addComponent(toAgentString);
        target.addComponent(fromAgentString);
        target.addComponent(fromToDiv);
    }

    private void setVisibility() {
        if (relationshipSpec.isDefined()) {
            fromToDiv.add(new AttributeModifier("style", true, new Model("display:block")));
        } else {
            fromToDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        }
    }

    private void setFromToComponents() {
        if (relationshipSpec.isDefined()) {
            fromSpecifiedCheckbox.setModelObject(relationshipSpec.getRelationshipFromSpecified());
            toSpecifiedCheckbox.setModelObject(!relationshipSpec.getRelationshipFromSpecified());
            Ref agent = (Ref) RefUtils.get(getComponent(), "agent");
            String agentString = (agent == null) ? "any agent" : (String) RefUtils.get(agent, "name");
            fromAgentString.setModelObject(agentString);
            toAgentString.setModelObject(agentString);
            fromRelationshipString.setModelObject(relationshipSpec.getRelationshipName());
            toRelationshipString.setModelObject(relationshipSpec.getRelationshipName());
        }
    }
}
