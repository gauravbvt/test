package com.mindalliance.channels.pages.components.community;

import com.mindalliance.channels.core.community.LocationBinding;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/4/13
 * Time: 7:46 PM
 */
public class LocationBindingsPanel extends AbstractCommandablePanel {

    private WebMarkupContainer bindingsContainer;

    private List<LocationBinding> tempBindings;

    public LocationBindingsPanel( String id, List<LocationBinding> locationBindings ) {
        super( id );
        tempBindings = locationBindings;
        init();
    }

    private void init() {
        bindingsContainer = new WebMarkupContainer( "bindingsContainer" );
        bindingsContainer.setOutputMarkupId( true );
        add( bindingsContainer );
        addBindingsList();
    }


    private void addBindingsList() {

        ListView<LocationBindingWrapper> bindingsList = new ListView<LocationBindingWrapper>(
                "bindings",
                new PropertyModel<List<LocationBindingWrapper>>( this, "bindings" )
        ) {
            @Override
            protected void populateItem( ListItem<LocationBindingWrapper> item ) {
                LocationBindingWrapper locationBindingWrapper = item.getModelObject();
                Place locale = getPlanLocale();
                String label = locationBindingWrapper.getPlaceholder().getName();
                if ( locale != null && locationBindingWrapper.getPlaceholder().equals( locale ) )
                        label += " (required)";
                item.add( new Label( "placeholder", label ) );
                item.add( makeBindingField( locationBindingWrapper ) );
            }
        };
        bindingsList.setOutputMarkupId( true );
        bindingsContainer.addOrReplace( bindingsList );
    }

    @SuppressWarnings( "unchecked" )
    private Component makeBindingField( LocationBindingWrapper locationBindingWrapper ) {
        final List<Place> choices = (List<Place>) CollectionUtils.select(
                getQueryService().listActualEntities( Place.class, true ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !( (Place) object ).isPlaceholder();
                    }
                } );
        AutoCompleteTextField<String> locationField = new AutoCompleteTextField<String>(
                "location",
                new PropertyModel<String>( locationBindingWrapper, "locationName" ) ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                if ( choices != null ) {
                    for ( Place place : choices ) {
                        String choice = place.getName();
                        if ( getQueryService().likelyRelated( s, choice ) )
                            candidates.add( choice );
                    }
                    Collections.sort( candidates );
                }
                return candidates.iterator();
            }
        };
        locationField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        } );
        locationField.setEnabled( isPlanner() );
        addInputHint( locationField, "Enter a known place" );
        return locationField;
    }

    public List<LocationBindingWrapper> getBindings() {
        List<LocationBindingWrapper> bindings =
                new ArrayList<LocationBindingWrapper>();
        for ( LocationBinding locationBinding : tempBindings ) {
            bindings.add( new LocationBindingWrapper( locationBinding ) );
        }
        return bindings;
    }



    public class LocationBindingWrapper implements Serializable {

        private LocationBinding locationBinding;

        public LocationBindingWrapper( LocationBinding locationBinding ) {
            this.locationBinding = locationBinding;
        }

        public LocationBindingWrapper( Place placeholder ) {
            locationBinding = new LocationBinding( placeholder );
        }

        public Place getLocation() {
            return locationBinding.getLocation();
        }

        public Place getPlaceholder() {
            return locationBinding.getPlaceholder();
        }

        public void setLocation( Place location ) {
            locationBinding.setLocation( location );
        }

        public boolean isBound() {
            return locationBinding.getLocation() != null;
        }

        public String getLocationName() {
            Place location = locationBinding.getLocation();
            return location != null
                    ? location.getName()
                    : "";
        }

        public void setLocationName( String val ) {
            if ( locationBinding.isBound() ) {
                if ( val == null || val.isEmpty() ) {
                    locationBinding.setLocation( null );
                } else {
                    Place location = getCommunityService().safeFindOrCreate( Place.class, val );
                    assert location.isActual();
                    locationBinding.setLocation( location );
                }
            } else {
                if ( val != null && !val.isEmpty() ) {
                    Place location = getCommunityService().safeFindOrCreate( Place.class, val );
                    assert location.isActual();
                    locationBinding.setLocation( location );
                }
            }
        }
    }
}
