package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.ifm.Timing;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.NumberValidator;
import org.apache.wicket.Component;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 29, 2008
 * Time: 4:07:06 PM
 */
public class TimingPanel extends AbstractComponentPanel {

    private Timing timing;
    private static final long serialVersionUID = -2287022076559659197L;

    public TimingPanel(
            String id, AbstractPlaybookPanel parentPanel, String propPath ) {
        super( id, parentPanel, propPath );
    }

    @Override
    protected void load() {
        super.load();
        timing = (Timing) getComponent();
        // amount
        final TextField<Integer> amountField = new TextField<Integer>(
                "amount", new Model<Integer>( timing.getAmount() ) );

        amountField.setType( Integer.class );
        amountField.add( NumberValidator.minimum( 0L ) );
        amountField.add(
                new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    private static final long serialVersionUID =
                            895219833724019868L;

                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        int value = 0;
                        try {
                            value = new Integer(
                                amountField.getDefaultModelObjectAsString() );
                        } catch ( NumberFormatException ignored ) {
                            amountField.error( "Not a number" );
                            amountField.setModelObject( 0 );
                            target.addComponent( amountField );
                        }
                        timing.setAmount( value );
                        elementChanged( propPath, target );
                        target.addComponent( getFeedback() );
                    }

                    @Override
                    protected void onError(
                            AjaxRequestTarget target, RuntimeException e ) {
                        Logger.getLogger( getClass() ).error(
                                "Error updating " + amountField + ": " + e );
                        amountField.clearInput();
                        target.addComponent( amountField );
                        target.addComponent( getFeedback() );
                    }
                } );
        addReplaceable( amountField );
        // units
        final Component unitChoice = new DropDownChoice<String>(
                "units", new Model<String>(
                timing.getUnit() ), Timing.getUnits() );
        unitChoice.add(
                new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    private static final long serialVersionUID =
                            -1674495656334278390L;

                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        String newUnit =
                                unitChoice.getDefaultModelObjectAsString();
                        timing.setUnit( newUnit );
                        elementChanged( propPath, target );
                    }
                } );
        addReplaceable( unitChoice );
    }
}
