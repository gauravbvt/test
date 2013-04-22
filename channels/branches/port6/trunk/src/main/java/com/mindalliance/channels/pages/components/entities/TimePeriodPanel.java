package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.Availability;
import com.mindalliance.channels.core.model.Available;
import com.mindalliance.channels.core.model.TimePeriod;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 20, 2010
 * Time: 2:20:28 PM
 */
public class TimePeriodPanel extends AbstractCommandablePanel {

    private String pathToAvailability;
    private int dayIndex;
    private WebMarkupContainer periodContainer;
    private IModel<Available> availableModel;
    private TimeSpinner fromHourSpinner;
    private TimeSpinner fromMinuteSpinner;
    private TimeSpinner toHourSpinner;
    private TimeSpinner toMinuteSpinner;


    public TimePeriodPanel(
            String id,
            IModel<Available> iModel,
            String pathToAvailability,
            int dayIndex ) {
        super( id, iModel );
        availableModel = iModel;
        this.pathToAvailability = pathToAvailability;
        this.dayIndex = dayIndex;
        init();
    }

    private void init() {
        this.setRenderBodyOnly( true );
        addDay();
        addPeriod();
    }

    private void addDay() {
        AjaxCheckBox dayOnCheckBox = new AjaxCheckBox(
                "dayOn",
                new PropertyModel<Boolean>( this, "dayOn" ) ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                makeVisible( periodContainer, isDayOn() );
                target.add( periodContainer );
                update( target, new Change( Change.Type.Updated, getAvailable(), "availability" ) );
            }
        };
        dayOnCheckBox.setEnabled( isLockedByUser( getAvailable() ) );
        add( dayOnCheckBox );
        String dayName = Availability.dayOfWeek( dayIndex );
        add( new Label( "dayName", dayName ) );
    }

    private void addPeriod() {
        periodContainer = new WebMarkupContainer( "periodContainer" );
        periodContainer.setOutputMarkupId( true );
        add( periodContainer );
        addFromHour();
        addFromMinute();
        addToHour();
        addToMinute();
        makeVisible( periodContainer, isDayOn() ) ;
    }

    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        addFromHour();
        addFromMinute();
        addToHour();
        addToMinute();
        target.add( fromHourSpinner );
        target.add( fromMinuteSpinner );
        target.add( toHourSpinner );
        target.add( toMinuteSpinner );
        super.updateWith( target, change, updated );
    }

    private void addFromHour() {
        fromHourSpinner = new TimeSpinner(
                "fromHour",
                availableModel,
                pathToTimePeriod() + ".fromHour",
                1,
                0,
                getMaxFromHour()
        );
        fromHourSpinner.setOutputMarkupId( true );
        periodContainer.addOrReplace( fromHourSpinner );
    }

    private void addFromMinute() {
        fromMinuteSpinner = new TimeSpinner(
                "fromMinute",
                availableModel,
                pathToTimePeriod() + ".fromMinute",
                15,
                0,
                getMaxFromMinute()
        );
        fromMinuteSpinner.setOutputMarkupId( true );
        periodContainer.addOrReplace( fromMinuteSpinner );
    }


    private void addToHour() {
        toHourSpinner = new TimeSpinner(
                "toHour",
                availableModel,
                pathToTimePeriod() + ".toHour",
                1,
                getMinToHour(),
                24
        );
        toHourSpinner.setOutputMarkupId( true );
        periodContainer.addOrReplace( toHourSpinner );
    }

    private void addToMinute() {
        toMinuteSpinner = new TimeSpinner(
                "toMinute",
                availableModel,
                pathToTimePeriod() + ".toMinute",
                15,
                getMinToMinute(),
                45
        );
        toMinuteSpinner.setOutputMarkupId( true );
        periodContainer.addOrReplace( toMinuteSpinner );
    }


    private int getMaxFromHour() {
        int fromMinute = getFromMinute();
        int toHour = getToHour();
        int toMinute = getToMinute();
        return toMinute >= fromMinute
                ? toHour
                : Math.max( 0, toHour - 1 );
    }

    private int getMaxFromMinute() {
        int fromHour = getFromHour();
        int toHour = getToHour();
        int toMinute = getToMinute();
        return fromHour == toHour
                ? Math.max( 0, toMinute - 15 )
                : 45;
    }


    private int getMinToHour() {
        int fromHour = getFromHour();
        int fromMinute = getFromMinute();
        int toMinute = getToMinute();
        return fromMinute <= toMinute
                ? fromHour
                : Math.min( 24, fromHour + 1 );
    }

    private int getMinToMinute() {
        int fromHour = getFromHour();
        int toHour = getToHour();
        int fromMinute = getFromMinute();
        return fromHour == toHour
                ? Math.min( 45, fromMinute + 15 )
                : 0;
    }


    public int getFromHour() {
        return getTimePeriod().getFromTime() / 60;
    }

    public int getFromMinute() {
        return getTimePeriod().getFromTime() % 60;
    }

    public int getToHour() {
        return getTimePeriod().getToTime() / 60;
    }


    public int getToMinute() {
        return getTimePeriod().getToTime() % 60;
    }

    private TimePeriod getTimePeriod() {
        return (TimePeriod) ChannelsUtils.getProperty(
                getAvailable(),
                pathToTimePeriod(),
                null );
    }

    private Available getAvailable() {
        return (Available) getModel().getObject();
    }

    public boolean isDayOn() {
        TimePeriod period = getTimePeriod();
        return !( period == null || period.isNil() );
    }

    public void setDayOn( boolean dayOn ) {
        doCommand( new UpdatePlanObject( getUser().getUsername(), getAvailable(),
                pathToTimePeriod(),
                dayOn ? TimePeriod.allDayPeriod() : TimePeriod.nilTimePeriod(),
                UpdateObject.Action.Set ) );
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex( int dayIndex ) {
        this.dayIndex = dayIndex;
    }

    private String pathToTimePeriod() {
        return pathToAvailability + ".timePeriods[" + dayIndex + "]";
    }
}
