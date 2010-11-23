package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Availability;
import com.mindalliance.channels.model.Available;
import com.mindalliance.channels.model.TimePeriod;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Availability panel;
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 20, 2010
 * Time: 2:20:14 PM
 */
public class AvailabilityPanel extends AbstractCommandablePanel {

    private IModel<Available> availableModel;
    // private WebMarkupContainer regularHoursContainer;
    private WebMarkupContainer timePeriodsContainer;
    // private boolean regularHours;

    public AvailabilityPanel( String id,
                              IModel<Available> iModel ) {
        super( id, iModel );
        availableModel = iModel;
        init();
    }

    private void init() {
        addTwentyFourSeven();
        // addRegularHours();
        addTimePeriods();
    }

    private void addTwentyFourSeven() {
        AjaxCheckBox twentyFourSevenCheckBox = new AjaxCheckBox(
                "twentyFourSeven",
                new PropertyModel<Boolean>( this, "twentyFourSeven" ) ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addTimePeriods();
                makeVisible( timePeriodsContainer, !isTwentyFourSeven() );
                // target.addComponent( regularHoursContainer );
                target.addComponent( timePeriodsContainer );
                update(target, new Change(Change.Type.Updated, getAvailable(), "availability" ));
            }
        };
        twentyFourSevenCheckBox.setEnabled( isLockedByUser( getAvailable() ) );
        add( twentyFourSevenCheckBox );
    }

    /*private void addRegularHours() {
        regularHoursContainer = new WebMarkupContainer( "regularHoursContainer" );
        regularHoursContainer.setOutputMarkupId( true );
        add( regularHoursContainer );
        AjaxCheckBox regularHoursCheckBox = new AjaxCheckBox(
                "regularHours",
                new PropertyModel<Boolean>( this, "regularHours" ) ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addTimePeriods();
                makeVisible( timePeriodsContainer, !isTwentyFourSeven() );
                target.addComponent( twentyFourSevenCheckBox );
                target.addComponent( timePeriodsContainer );
            }
        };
        regularHoursContainer.add( regularHoursCheckBox );
    }
*/
    private void addTimePeriods() {
        timePeriodsContainer = new WebMarkupContainer( "timePeriodsContainer" );
        timePeriodsContainer.setOutputMarkupId( true );
        addOrReplace( timePeriodsContainer );
        ListView<TimePeriod> timePeriodsList = new ListView<TimePeriod>(
                "dayAvailability",
                getTimePeriods()
        ) {
            protected void populateItem( ListItem<TimePeriod> item ) {
                item.add( new TimePeriodPanel(
                        "timePeriod",
                        availableModel,
                        "availability",
                        item.getIndex()
                ) );
            }
        };
        timePeriodsContainer.add( timePeriodsList );
    }

    private List<TimePeriod> getTimePeriods() {
        Availability availability = getAvailable().getAvailability();
        return availability == null
                ? new ArrayList<TimePeriod>()
                : availability.getTimePeriods();
    }

    private Available getAvailable() {
        return availableModel.getObject();
    }

    public boolean isTwentyFourSeven() {
        return getAvailable().getAvailability() == null;
    }

    public void setTwentyFourSeven( boolean val ) {
        Availability availability = val ? null : new Availability();
        doCommand( new UpdatePlanObject(
                getAvailable(),
                "availability",
                availability,
                UpdateObject.Action.Set
                ) );
//        regularHours = !twentyFourSeven;
    }

/*
    public boolean isRegularHours() {
        return regularHours;
    }

    public void setRegularHours( boolean regularHours ) {
        this.regularHours = regularHours;
        twentyFourSeven = !regularHours;
    }
*/


}
