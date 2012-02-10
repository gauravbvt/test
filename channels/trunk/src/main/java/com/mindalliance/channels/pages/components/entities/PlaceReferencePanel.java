package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.PlaceReference;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

/**
 * Place reference panel.
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
    private boolean planReferenced = false;
    private boolean placeReferenced = false;
    private boolean eventReferenced = false;
    private CheckBox planCheckBox;
    private CheckBox eventCheckBox;
    private CheckBox placeCheckBox;
    private DropDownChoice eventChoice;
    private EntityReferencePanel<Place> entityReferencePanel;
    private boolean reset = false;

    public PlaceReferencePanel( String id, IModel<? extends Identifiable> model, String property ) {
        super( id, model );
        this.property = property;
        init();
    }

    private void init() {
        planReferenced = getPlaceReference().isPlanReferenced();
        placeReferenced = getPlaceReference().isPlaceReferenced();
        eventReferenced = getPlaceReference().isEventReferenced();
        assert !( placeReferenced && eventReferenced );
        this.setOutputMarkupId( true );
        addPlanCheckBox();
        addEventCheckBox();
        addPlaceCheckBox();
        addEventLink();
        addPlaceLink();
        addEventChoice();
        addEntityReferencePanel();
        adjustFields();
    }

    private void addPlanCheckBox() {
        planCheckBox = new CheckBox( "plan-locale", new PropertyModel<Boolean>( this, "planReferenced" ) );
        planCheckBox.setOutputMarkupId( true );
        planCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields();
                target.add( eventCheckBox );
                target.add( eventChoice );
                target.add( placeCheckBox );
                target.add( entityReferencePanel );
                if ( reset ) {
                    reset = false;
                    update( target, new Change( Change.Type.Updated, getEntity(), property ) );
                }
            }
        } );
        add( planCheckBox );
    }

    private void addEventCheckBox() {
        eventCheckBox = new CheckBox( "event", new PropertyModel<Boolean>( this, "eventReferenced" ) );
        eventCheckBox.setOutputMarkupId( true );
        eventCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields();
                target.add( planCheckBox );
                target.add( eventChoice );
                target.add( placeCheckBox );
                target.add( entityReferencePanel );
                if ( reset ) {
                    reset = false;
                    update( target, new Change( Change.Type.Updated, getEntity(), property ) );
                }
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
                target.add( planCheckBox );
                target.add( eventCheckBox );
                target.add( eventChoice );
                target.add( entityReferencePanel );
                if ( reset ) {
                    reset = false;
                    update( target, new Change( Change.Type.Updated, getEntity(), property ) );
                }
            }
        } );
        add( placeCheckBox );
    }

    private void addEventLink() {
        ModelObjectLink eventLink = new ModelObjectLink(
                "eventLink",
                new PropertyModel<Place>( this, "refEvent" ),
                new Model<String>( "event" ) );
        eventLink.setOutputMarkupId( true );
        add( eventLink );
    }

    private void addPlaceLink() {
        ModelObjectLink placeLink = new ModelObjectLink(
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
                Place.class
        );
        entityReferencePanel.setOutputMarkupId( true );
        add( entityReferencePanel );
    }

    private void adjustFields() {
        boolean editable =
                getPlan().isDevelopment()
                        && getLockManager().isLockedByUser( getUser().getUsername(), getEntity().getId() )
                        && !ModelObject.areIdentical( getPlan().getLocale(), getEntity() );
        if ( !editable ) {
            planCheckBox.setEnabled( false );
            eventCheckBox.setEnabled( false );
            placeCheckBox.setEnabled( false );
        }
        eventChoice.setEnabled( eventCheckBox.isEnabled() && isEventReferenced() );
        entityReferencePanel.enable( placeCheckBox.isEnabled() && isPlaceReferenced() );
    }

    private void refreshAll( AjaxRequestTarget target ) {
        reset = false;
        target.add( this );
    }

    public boolean isPlanReferenced() {
        return planReferenced;
    }

    public void setPlanReferenced( boolean val ) {
        planReferenced = val;
        if ( val ) {
            placeReferenced = false;
            eventReferenced = false;
            getPlaceReference().setPlanReferenced( val );
            doCommand( new UpdatePlanObject( getUser().getUsername(), getEntity(), property, getPlaceReference() ) );
        } else {
            resetPlaceReference();
        }
    }

    public boolean isEventReferenced() {
        return eventReferenced;
    }

    public void setEventReferenced( boolean val ) {
        eventReferenced = val;
        if ( val ) {
            planReferenced = false;
            placeReferenced = false;
        } else {
            resetPlaceReference();
        }
    }

    public boolean isPlaceReferenced() {
        return placeReferenced;
    }

    public void setPlaceReferenced( boolean val ) {
        placeReferenced = val;
        if ( val ) {
            planReferenced = false;
            eventReferenced = false;
        } else {
            resetPlaceReference();
        }
    }

    private void resetPlaceReference() {
        doCommand( new UpdatePlanObject( getUser().getUsername(), getEntity(), property, new PlaceReference() ) );
        reset = true;
    }

    public Event getRefEvent() {
        return getPlaceReference().getEvent();
    }

    public void setRefEvent( Event event ) {
        getPlaceReference().setEvent( event );
        doCommand( new UpdatePlanObject( getUser().getUsername(), getEntity(), property, getPlaceReference() ) );
    }

    public Place getRefPlace() {
        return getPlaceReference().getPlace();
    }

    private ModelEntity getEntity() {
        return (ModelEntity) getModel().getObject();
    }

    private List<String> getPlaceChoices() {
        return getQueryService().findAllEntityNames( Place.class );
    }

    public void enable( boolean enabled ) {
        planCheckBox.setEnabled( enabled );
        eventCheckBox.setEnabled( enabled );
        placeCheckBox.setEnabled( enabled );
        adjustFields();
    }

    private PlaceReference getPlaceReference() {
        return (PlaceReference) ChannelsUtils.getProperty(
                getEntity(),
                property,
                new PlaceReference() );
    }

}