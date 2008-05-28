package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.ifm.spec.LocationSpec;
import com.mindalliance.channels.playbook.ifm.spec.Spec;
import com.mindalliance.channels.playbook.support.RefUtils;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 25, 2008
 * Time: 8:41:02 PM
 */
abstract public class AbstractSpecComponentPanel extends AbstractComponentPanel {

    protected WebMarkupContainer specDiv;
    protected AjaxCheckBox definedField;
    protected Label anyString;

    public AbstractSpecComponentPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        definedField = new AjaxCheckBox("defined", new Model((Boolean) !getSpec().isDefined())) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean isAny = (Boolean) definedField.getModelObject();
                if (isAny) {
                    Spec spec = new LocationSpec();
                    RefUtils.set(getElement(), propPath, spec);
                    specDiv.add(new AttributeModifier("style", true, new Model("display:none")));
                } else {
                    specDiv.add(new AttributeModifier("style", true, new Model("display:block")));
                }
                target.addComponent(specDiv);
            }
        };
        definedField.setOutputMarkupId(true);
        addOrReplace(definedField);
        anyString = new Label("anyString", new Model(getAnyLabelString()));
        anyString.setOutputMarkupId(true);
        addOrReplace(anyString);
        specDiv = new WebMarkupContainer("specDiv");
        specDiv.setOutputMarkupId(true);
        addOrReplace(specDiv);
        setSpecVisibility();
    }

    protected void setSpecVisibility() {
        if (getSpec().isDefined()) {
            specDiv.add(new AttributeModifier("style", true, new Model("display:block")));
        } else {
            specDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        }
    }

    protected Spec getSpec() {
        return (Spec) getComponent();
    }

    @Override
    protected void addReplaceable(Component component) {
        addReplaceableTo(component, specDiv);
    }

    abstract protected Spec makeNewSpec();

    protected String getAnyLabelString() {  // DEFAULT
        return "Any";
    }
}
