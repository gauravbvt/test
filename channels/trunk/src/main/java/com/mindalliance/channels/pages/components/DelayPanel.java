package com.mindalliance.channels.pages.components;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.commons.beanutils.PropertyUtils;
import com.mindalliance.channels.Delay;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.ScenarioObject;
import com.mindalliance.channels.command.commands.UpdateScenarioObject;
import com.mindalliance.channels.command.commands.UpdateProjectObject;

import java.util.List;
import java.lang.reflect.InvocationTargetException;

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

    private DropDownChoice unitChoice;

    private TextField amountField;

    private String property;

    public DelayPanel( String id, IModel<ModelObject> model, String property ) {
        super( id, model );
        this.model = model;
        this.property = property;
        init();
    }

    private void init() {
        amountField = new TextField<String>( "delay-amount",
                new PropertyModel<String>( this, "amountString" ) );
        amountField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                updateWith( target, model.getObject() );
            }
        } );
        add( amountField );
        unitChoice = new DropDownChoice<Delay.Unit>(
                "delay-unit",
                new PropertyModel<Delay.Unit>( this, "unit" ),
                new PropertyModel<List<? extends Delay.Unit>>( this, "units" ),
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
                updateWith( target, model.getObject() );
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

    public void setAmountString( String val ) {
        setProperty( "amountString", val );
    }

    public void setUnit( Delay.Unit val ) {
        setProperty( "unit", val );
    }

    public Delay.Unit getUnit() {
        return (Delay.Unit) getProperty( "unit" );
    }

    @SuppressWarnings("unchecked")
    public List<? extends Delay.Unit>getUnits() {
        return(List<? extends Delay.Unit>)getProperty("units");
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
        if ( model.getObject() instanceof ScenarioObject ) {
            doCommand( new UpdateScenarioObject( (ScenarioObject) model.getObject(), property + "." + prop, val ) );
        } else {
            doCommand( new UpdateProjectObject( model.getObject(), property + "." + prop, val ) );
        }
    }

}
