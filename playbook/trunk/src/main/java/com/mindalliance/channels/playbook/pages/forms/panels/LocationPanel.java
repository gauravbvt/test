package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.renderers.RefChoiceRenderer;
import com.mindalliance.channels.playbook.ifm.info.Location;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.AttributeModifier;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 21, 2008
 * Time: 7:32:03 PM
 */
public class LocationPanel extends AbstractComponentPanel {

    Location location;
    DropDownChoice placeField;
    WebMarkupContainer placeInfoDiv;
    PlaceInfoPanel placeInfoPanel;
    LocationInfoPanel locationInfoPanel;

    public LocationPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    @Override
    protected void load() {
        super.load();
        location = (Location) RefUtils.get(getElement(), propPath);
        loadPlaceField();
        placeInfoDiv = new WebMarkupContainer("placeInfoDiv");
        placeInfoDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        addReplaceable(placeInfoDiv);
        placeInfoPanel = new PlaceInfoPanel("placeInfo", this, propPath + ".placeInfo", readOnly, feedback);
        placeInfoDiv.add(placeInfoPanel);
        locationInfoPanel = makeLocationInfoPanel();
        addReplaceable(locationInfoPanel);
    }

    // placeField
    private void loadPlaceField() {
        String placeName = (String) RefUtils.get(getElement(), "place.name");
        // List<String> placeNames = project.findAllPlaceNames();
        placeField = new DropDownChoice("place", new Model(placeName),  // TODO -- replace with DynamicFilterTree if too many places
                         getProject().getPlaces(),
                         new RefChoiceRenderer("name", "id"));
        placeField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                Ref newPlace = (Ref)placeField.getModelObject();
                location.setPlace(newPlace);
                elementChanged(propPath, target);
                locationInfoPanel = makeLocationInfoPanel();  // recreate locationInfoPanel
                addReplaceable(locationInfoPanel);
                updatePlaceInfoPanel(target);
                target.addComponent(locationInfoPanel);
            }
        });
        addReplaceable(placeField);
    }

    private void updatePlaceInfoPanel(AjaxRequestTarget target) {
        if (location.getPlace() != null) {
            placeInfoDiv.add(new AttributeModifier("style", true, new Model("display:block")));
        }
        else {
            placeInfoDiv.add(new AttributeModifier("style", true, new Model("display:none")));
        }
        target.addComponent(placeInfoDiv);
    }

    // locationInfoPanel
    private LocationInfoPanel makeLocationInfoPanel() {
        // If place is set, show the place's locationInfo (readOnly)
        // else edit this locationInfo
        LocationInfoPanel lip;
        if (location.getPlace() != null) {
            lip = new LocationInfoPanel("locationInfo", this, propPath + ".place.locationInfo", true, feedback); // readOnly
        } else {
            lip = new LocationInfoPanel("locationInfo", this, propPath + ".locationInfo", false, feedback);
        }
        return lip;
    }

}
