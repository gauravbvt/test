package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Available;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.util.ChannelsUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * Time spinner.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 22, 2010
 * Time: 1:12:09 PM
 */
public class TimeSpinner extends AbstractCommandablePanel {
    private String pathToTime;
    private int increment;
    private int minValue;
    private int maxValue;
    private IModel<Available> availableModel;
    private TextField<String> timeField;

    public TimeSpinner(
            String id,
            IModel<Available> iModel,
            String pathToTime,
            int increment,
            int minValue,
            int maxValue ) {
        super( id, iModel );
        availableModel = iModel;
        this.pathToTime = pathToTime;
        this.increment = increment;
        this.minValue = minValue;
        this.maxValue = maxValue;
        init();
    }

    private void init() {
        addDec();
        addInc();
        addTime();
    }

    private void addDec() {
        AjaxLink decButton = new AjaxLink( "dec" ) {
            public void onClick( AjaxRequestTarget target ) {
                setTime( getTime() - increment );
                target.addComponent( timeField );
                update( target, new Change( Change.Type.Updated, getAvailable(),  pathToTime ) );
            }
        };
        decButton.setVisible( isLockedByUser( getAvailable() ) );
        decButton.setEnabled( getTime() >= minValue + increment );
        add( decButton );
    }

    private void addInc() {
        AjaxLink incButton = new AjaxLink( "inc" ) {
            public void onClick( AjaxRequestTarget target ) {
                setTime( getTime() + increment );
                target.addComponent( timeField );
                update( target, new Change( Change.Type.Updated, getAvailable(),  pathToTime ) );
            }
        };
        incButton.setVisible( isLockedByUser( getAvailable() ) );
        incButton.setEnabled( getTime() <= maxValue - increment );
        add( incButton );
    }

    private void addTime() {
        timeField = new TextField<String>(
                "time",
                new PropertyModel<String>( this, "timeString" ) );
        timeField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                target.addComponent( timeField );
                update( target, new Change( Change.Type.Updated, getAvailable(),  pathToTime ) );
            }
        } );
        timeField.setEnabled( isLockedByUser( getAvailable() ) );
        add( timeField );
    }

    public String getTimeString() {
        int time = getTime();
        return time < 10 ? "0" + time : Integer.toString( time );
    }

    public void setTimeString( String s ) {
        try {
            setTime( Integer.parseInt( s ) );
        } catch ( NumberFormatException e ) {
            setTime( 0 );
        }
    }

    private int getTime() {
        return (Integer) ChannelsUtils.getProperty( getAvailable(), pathToTime, 0 );
    }

    private void setTime( int val ) {
        if ( val != getTime() && val >= minValue &&  val <= maxValue ) {
            doCommand( new UpdatePlanObject(
                    getAvailable(),
                    pathToTime,
                    val,
                    UpdateObject.Action.Set
            ) );
        }
    }

    private Available getAvailable() {
        return availableModel.getObject();
    }
}
