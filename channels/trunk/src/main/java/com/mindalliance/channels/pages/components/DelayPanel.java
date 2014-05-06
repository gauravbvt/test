package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateModelObject;
import com.mindalliance.channels.core.command.commands.UpdateSegmentObject;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.model.time.TimeUnit;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Panel for editing a Delay.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 18, 2009
 * Time: 2:01:00 PM
 */
public class DelayPanel extends AbstractCommandablePanel {
    /**
     * A Delay.
     */
    private IModel<ModelObject> model;
    /**
     * Unit choice.
     */
    private DropDownChoice unitChoice;
    /**
     * Amount field.
     */
    private TextField amountField;
    /**
     * Property with delay as value.
     */
    private String property;

    public DelayPanel( String id, IModel<ModelObject> model, String property ) {
        super( id, model, null );
        this.model = model;
        this.property = property;
        init();
    }

    private void init() {
        amountField = new TextField<String>( "delay-amount",
                new PropertyModel<String>( this, "amountString" ) );
        amountField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, model.getObject(), property ) );
            }
        } );
        add( amountField );
        unitChoice = new DropDownChoice<TimeUnit>(
                "delay-unit",
                new PropertyModel<TimeUnit>( this, "unit" ),
                new PropertyModel<List<? extends TimeUnit>>( this, "units" ),
                new IChoiceRenderer<TimeUnit>() {
                    public Object getDisplayValue( TimeUnit unit ) {
                        return unit.toString();
                    }

                    public String getIdValue( TimeUnit unit, int index ) {
                        return unit.toString();
                    }
                }
        ) {
        };
        unitChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, model.getObject(), property ) );
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

    public String getAmountString() {
        return (String) getProperty( "amountString" );
    }

    /**
     * Set amount as string.
     * (Used by PropertyModel)
     * @param val a string
     */
    public void setAmountString( String val ) {
        setProperty( "amountString", val );
    }

    /**
     * Set unit.
     * (Used by PropertyModel)
     * @param val a a delay unit
     */
    public void setUnit( TimeUnit val ) {
        setProperty( "unit", val );
    }

    public TimeUnit getUnit() {
        return (TimeUnit) getProperty( "unit" );
    }

    @SuppressWarnings( "unchecked" )
    public List<? extends TimeUnit> getUnits() {
        return (List<? extends TimeUnit>) getProperty( "units" );
    }

    private Object getProperty( String prop ) {
        try {
            return PropertyUtils.getProperty( model.getObject(), property + "." + prop );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        } catch ( InvocationTargetException e ) {
            throw new RuntimeException( e );
        } catch ( NoSuchMethodException e ) {
            throw new RuntimeException( e );
        }
    }

    private void setProperty( String prop, Object val ) {
        if ( model.getObject() instanceof SegmentObject ) {
            doCommand( new UpdateSegmentObject( getUser().getUsername(),
                                                model.getObject(), property + "." + prop, val ) );
        } else {
            doCommand( new UpdateModelObject( getUser().getUsername(),
                                             model.getObject(), property + "." + prop, val ) );
        }
    }

}
