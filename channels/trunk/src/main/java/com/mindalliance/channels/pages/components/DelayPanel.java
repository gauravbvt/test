package com.mindalliance.channels.pages.components;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import com.mindalliance.channels.Delay;

import java.util.List;

/**
 * Panel for editing a Delay.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 18, 2009
 * Time: 2:01:00 PM
 */
public class DelayPanel extends Panel {
    /**
     * A Delay.
     */
    private Delay delay;

    DropDownChoice unitChoice;

    TextField amountField;

    public DelayPanel( String id, IModel<Delay> model ) {
        super( id, model );
        delay = model.getObject();
        init();
    }

    private void init() {
        amountField = new TextField<String>( "delay-amount",
                new PropertyModel<String>( delay, "amountString" ) );
        amountField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                updateWith( target );
            }
        } );
        add( amountField );
        unitChoice = new DropDownChoice<Delay.Unit>(
                "delay-unit",
                new PropertyModel<Delay.Unit>( delay, "unit" ),
                new PropertyModel<List<? extends Delay.Unit>>( delay, "units" ),
                new IChoiceRenderer<Delay.Unit>() {
                    public Object getDisplayValue( Delay.Unit unit ) {
                        return unit.toString();
                    }

                    public String getIdValue( Delay.Unit unit, int index ) {
                        return unit.toString();
                    }
                }
        ) {
        };
        unitChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                updateWith( target );
            }
        } );
        add( unitChoice );
    }

    /**
     * Enable or disable fields.
     *
     * @param enabled a boolean
     */
    public void enable( boolean enabled ) {
        amountField.setEnabled( enabled );
        unitChoice.setEnabled( enabled );
    }

    private void updateWith( AjaxRequestTarget target ) {
        Updatable updatableParent = findParent( Updatable.class );
        if ( updatableParent != null ) updatableParent.update( target );
    }

}
