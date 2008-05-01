package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractComponentPanel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.info.Timing;
import com.mindalliance.channels.playbook.support.RefUtils;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.NumberValidator;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.log4j.Logger;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 29, 2008
 * Time: 4:07:06 PM
 */
public class TimingPanel extends AbstractComponentPanel {

    Timing timing;

    public TimingPanel(String id, Ref element, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, element, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        timing = (Timing) RefUtils.get(element, propPath);
        // amount
        final TextField amountField = new TextField("amount", new Model(timing.getAmount()));
        amountField.setType(Integer.class);
        amountField.add(NumberValidator.minimum(0));
        amountField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
              int value = 0;
              try {
                 value = new Integer(amountField.getModelObjectAsString());
              }
              catch(NumberFormatException e) {
                 amountField.error("Not a number");
                 amountField.setModelObject(0);
                 target.addComponent(amountField);
              }
              timing.setAmount(value);
              elementChanged();
              target.addComponent(feedback);
            }

            @Override
            protected void onError(AjaxRequestTarget target, RuntimeException e) {
                Logger.getLogger(this.getClass()).error("Error updating " + amountField + ": " + e);
                amountField.clearInput();
                target.addComponent(amountField);
                target.addComponent(feedback);
            }
        });
        addReplaceable(amountField);
        // units
        final DropDownChoice unitChoice = new DropDownChoice("units", new Model(timing.getUnit()), Timing.getUnits());
        unitChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
               String newUnit = unitChoice.getModelObjectAsString();
               timing.setUnit(newUnit);
               elementChanged();
            }
        });
        addReplaceable(unitChoice);
    }

}
