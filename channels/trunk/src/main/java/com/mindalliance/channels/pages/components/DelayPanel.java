package com.mindalliance.channels.pages.components;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.Component;
import com.mindalliance.channels.Delay;

import java.util.List;

/**
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
    /**
     * A TextField with the amount.
     */
    private TextField amountField;
    /**
     * DropDownChoice with the units.
     */
    private DropDownChoice unitChoice;

    public DelayPanel( String id, IModel<Delay> model ) {
        super( id, model );
        delay = model.getObject();
        if ( delay == null ) delay = new Delay();
        init();
    }

    private void init() {
        amountField = new TextField<String>( "delay-amount",
                new PropertyModel<String>( delay, "amountString" ) );
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
        add( unitChoice );

    }

    /**
     * Enables or disables the fields.
     *
     * @param enabled a boolean
     */
    public void enable( boolean enabled ) {
        amountField.setEnabled( enabled );
        unitChoice.setEnabled( enabled );
    }
}
