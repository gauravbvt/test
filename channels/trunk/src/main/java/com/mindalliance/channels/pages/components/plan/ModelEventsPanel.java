package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.GeoLocatable;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.GeomapLinkPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
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
public class ModelEventsPanel extends AbstractCommandablePanel {

    public ModelEventsPanel( String id, IModel<? extends Identifiable> iModel, Set<Long> expansions ) {
        super( id, iModel, expansions );
        init();
    }

    @Override
    public void redisplay( AjaxRequestTarget target ) {
        init();
        super.redisplay( target );
    }


    private void init() {
        addEventsMapLink();
        addEventList();
    }

    private void addEventsMapLink() {
        List<? extends GeoLocatable> geoLocatables = getQueryService().listReferencedEntities( Event.class );
        GeomapLinkPanel eventsMapLink = new GeomapLinkPanel(
                "geomapLink",
                new Model<String>( "All events n the model with known locations" ),
                geoLocatables,
                new Model<String>( "Show events in the model" ) );
        eventsMapLink.setOutputMarkupId( true );
        addOrReplace( eventsMapLink );
    }

    private void addEventList() {
        EventListPanel incidentListPanel = new EventListPanel(
                "incidents",
                new PropertyModel<CollaborationModel>( this, "collaborationModel" ),
                getExpansions() );
        addOrReplace( incidentListPanel );
    }

    /**
     * Get the plan being edited.
     *
     * @return a plan
     */
    public CollaborationModel getCollaborationModel() {
        return (CollaborationModel) getModel().getObject();
    }


}
