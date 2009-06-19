package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.geo.GeoLocatable;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.GeoMapPage;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.List;
import java.util.Set;

/**
 * Plan incidents panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 7, 2009
 * Time: 1:45:18 PM
 */
public class PlanEventsPanel extends AbstractCommandablePanel {

    public PlanEventsPanel( String id, IModel<? extends Identifiable> iModel, Set<Long> expansions ) {
        super( id, iModel, expansions );
        init();
    }

    private void init() {
        addEventsMapLink();
        addEventList();
    }

    private void addEventsMapLink() {
        List<? extends GeoLocatable> geoLocatables = getQueryService().list(Event.class );
        BookmarkablePageLink<GeoMapPage> eventsMapLink = GeoMapPage.makeLink(
                "mapLink",
                new Model<String>( "All events in plan"),
                geoLocatables );
        add( eventsMapLink );
    }

    private void addEventList() {
        EventListPanel incidentListPanel = new EventListPanel(
                "incidents",
                new PropertyModel<Plan>(this, "plan"),
                getExpansions() );
        add(incidentListPanel);
    }

    /**
     * Get the plan being edited.
     * @return a plan
     */
    public Plan getPlan() {
        return (Plan)getModel().getObject();
    }


}
