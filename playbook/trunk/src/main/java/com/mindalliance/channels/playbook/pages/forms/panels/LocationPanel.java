package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractComponentPanel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.ifm.info.Location;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.Model;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import java.util.List;
import java.util.ArrayList;

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
    PlaceInfoPanel placeInfoPanel;
    LocationInfoPanel locationInfoPanel;

    public LocationPanel(String id, Ref element, String propPath) {
        super(id, element, propPath);
    }

    @Override
    protected void load() {
        super.load();
        location = (Location)RefUtils.get(element, propPath);
        loadPlaceField();
        placeInfoPanel = new PlaceInfoPanel("placeInfo", element, propPath + ".placeInfo");
        addToPanel(placeInfoPanel);
        locationInfoPanel = makeLocationInfoPanel();
        addToPanel(locationInfoPanel);
    }

    // placeField
    private void loadPlaceField() {
        List<String> placeNames = project.findAllPlaceNames();
        String placeName = (String)RefUtils.get(element, "place.name");
        placeField = new DropDownChoice("place",  new Model(placeName), placeNames);
        placeField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    String newPlaceName = placeField.getModelObjectAsString();
                    Ref newPlace = project.findPlaceNamed(newPlaceName);
                    if (location.getPlace() != newPlace) {
                        elementChanged();
                        location.setPlace(newPlace);
                        locationInfoPanel = makeLocationInfoPanel();  // recreate locationInfoPanel
                        target.addComponent(locationInfoPanel);
                    }
                }
        });
        addToPanel(placeField);
    }

    // locationInfoPanel
    private LocationInfoPanel makeLocationInfoPanel() {
        // If place is set, show the place's locationInfo (readOnly)
        // else edit this locationInfo
       LocationInfoPanel lip;
       if (location.getPlace() != null) {
        lip = new LocationInfoPanel("locationInfo", location.getPlace(), "locationInfo", true); // readOnly
       }
       else {
        lip = new LocationInfoPanel("locationInfo", element, "location.locationInfo");
       }
        return lip;
    }

}
