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
    private boolean twentyFourSeven = true;
    private AjaxCheckBox twentyFourSevenCheckBox;

    public AvailabilityPanel( String id,
                              IModel<Available> iModel ) {
        super( id, iModel );
        availableModel = iModel;
        init();
    }

    private void init() {
        addTwentyFourSeven();
        addTimePeriods();
    }

    private void addTwentyFourSeven() {
        twentyFourSevenCheckBox = new AjaxCheckBox(
                "twentyFourSeven",
                new PropertyModel<Boolean>( this, "twentyFourSeven" ) ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addTimePeriods();
                makeVisible( timePeriodsContainer, !isTwentyFourSeven() );
                // target.addComponent( regularHoursContainer );
                target.addComponent( timePeriodsContainer );
                target.addComponent( twentyFourSevenCheckBox );
                update(target, new Change(Change.Type.Updated, getAvailable(), "availability" ));
            }
        };
        twentyFourSevenCheckBox.setEnabled( isLockedByUser( getAvailable() ) );
        add( twentyFourSevenCheckBox );
    }

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
        makeVisible( timePeriodsContainer, !isTwentyFourSeven() );
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
        return twentyFourSeven && getAvailable().getAvailability().isAlways();
    }

    public void setTwentyFourSeven( boolean val ) {
        twentyFourSeven = val;
        if ( val )
            doCommand( new UpdatePlanObject(
                    getAvailable(),
                    "availability",
                    new Availability(),
                    UpdateObject.Action.Set
                    ) );
    }



}
