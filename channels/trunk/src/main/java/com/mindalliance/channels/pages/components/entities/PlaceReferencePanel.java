package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.PlaceReference;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.EntityReferencePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 21, 2009
 * Time: 8:47:58 PM
 */
public class PlaceReferencePanel extends AbstractCommandablePanel {
    /**
     * Name of property set to a PlaceReference.
     */
    private String property;
    /**
     * Place reference.
     */
    private PlaceReference placeRef = new PlaceReference();
    private boolean placeReferenced = false;
    private boolean eventReferenced = false;
    private CheckBox eventCheckBox;
    private CheckBox placeCheckBox;
    private ModelObjectLink eventLink;
    private ModelObjectLink placeLink;
    private DropDownChoice eventChoice;
    private EntityReferencePanel<Place> entityReferencePanel;

    public PlaceReferencePanel( String id, IModel<? extends Identifiable> model, String property ) {
        super( id, model );
        this.property = property;
        init();
    }

    private void init() {
        this.setOutputMarkupId( true );
        addEventCheckBox();
        addPlaceCheckBox();
        addEventLink();
        addPlaceLink();
        addEventChoice();
        addEntityReferencePanel();
        adjustFields();
    }

    private void addEventCheckBox() {
        eventCheckBox = new CheckBox( "event", new PropertyModel<Boolean>( this, "eventReferenced" ) );
        eventCheckBox.setOutputMarkupId( true );
        eventCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields();
                refreshAll( target );
            }
        } );
        add( eventCheckBox );
    }

    private void addPlaceCheckBox() {
        placeCheckBox = new CheckBox( "place", new PropertyModel<Boolean>( this, "placeReferenced" ) );
        placeCheckBox.setOutputMarkupId( true );
        placeCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields();
                refreshAll( target );
            }
        } );
        add( placeCheckBox );
    }

    private void addEventLink() {
        eventLink = new ModelObjectLink(
                "eventLink",
                new PropertyModel<Place>( this, "refEvent" ),
                new Model<String>( "event" ) );
        eventLink.setOutputMarkupId( true );
        add( eventLink );
    }

    private void addPlaceLink() {
        placeLink = new ModelObjectLink(
                "placeLink",
                new PropertyModel<Place>( this, "refPlace" ),
                new Model<String>( "place" )
        );
        placeLink.setOutputMarkupId( true );
        add( placeLink );
    }

    private void addEventChoice() {
        eventChoice = new DropDownChoice<Event>(
                "refEvents",
                new PropertyModel<Event>( this, "refEvent" ),
                getQueryService().list( Event.class ) );
        eventChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields();
                refreshAll( target );
                update( target, new Change( Change.Type.Updated, getEntity(), property ) );
            }
        } );
        eventChoice.setOutputMarkupId( true );
        add( eventChoice );
    }

    private void addEntityReferencePanel() {
        entityReferencePanel = new EntityReferencePanel<Place>(
                "refPlace",
                getModel(),
                getPlaceChoices(),
                property + ".place",
                Place.class,
                null
        );
        entityReferencePanel.setOutputMarkupId( true );
        add( entityReferencePanel );
    }

    private void adjustFields() {
        eventChoice.setEnabled( eventCheckBox.isEnabled() && isEventReferenced() );
        entityReferencePanel.enable( placeCheckBox.isEnabled() && isPlaceReferenced() );
    }

    private void refreshAll( AjaxRequestTarget target ) {
        target.addComponent( this );
    }

    public boolean isEventReferenced() {
        return placeRef.getEvent() != null || eventReferenced;
    }

    public void setEventReferenced( boolean val ) {
        eventReferenced = val;
        if (val) placeReferenced = false;
        placeRef = new PlaceReference();
    }

    public boolean isPlaceReferenced() {
        return placeRef.getPlace() != null || placeReferenced;
    }

    public void setPlaceReferenced( boolean val ) {
        placeReferenced = val;
        if (val) eventReferenced = false;
        placeRef = new PlaceReference();
    }

    public Event getRefEvent() {
        return eventReferenced ? placeRef.getEvent() : null;
    }

    public void setRefEvent( Event event ) {
        placeRef.setEvent( event );
        doCommand( new UpdatePlanObject( getEntity(), property, placeRef) );
    }

    public Place getRefPlace() {
        return !eventReferenced ? placeRef.getPlace() : null;
    }

    private ModelEntity getEntity() {
        return (ModelEntity) getModel().getObject();
    }

    private List<String> getPlaceChoices() {
        return getQueryService().findAllEntityNames( Place.class );
    }

    public void enable( boolean enabled ) {
        eventCheckBox.setEnabled( enabled );
        placeCheckBox.setEnabled( enabled );
        adjustFields();
    }
}