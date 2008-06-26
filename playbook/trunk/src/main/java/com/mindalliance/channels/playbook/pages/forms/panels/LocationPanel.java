package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.ifm.info.Location;
import com.mindalliance.channels.playbook.ifm.info.GeoLocation;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 21, 2008
 * Time: 7:32:03 PM
 */
public class LocationPanel extends AbstractComponentPanel {

    Location location;
    AjaxCheckBox isAPlaceCheckBox;
    AjaxCheckBox isAGeoLocationCheckBox;
    WebMarkupContainer placeDiv;
    DynamicFilterTree placeTree;
    WebMarkupContainer geoLocationDiv;
    GeoLocationPanel geoLocationPanel;
    Ref priorPlace;
    GeoLocation priorGeoLocation;

    public LocationPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    @Override
    protected void load() {
        super.load();
        location = (Location)getComponent();
        priorPlace = location.getPlace();
        priorGeoLocation = location.getGeoLocation();
        isAPlaceCheckBox = new AjaxCheckBox("isAPlace", new Model((Boolean)location.isAPlace())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean isAPlace = (Boolean)isAPlaceCheckBox.getModelObject();
                isAGeoLocationCheckBox.setModel(new Model((Boolean)!isAPlace));
                updateLocationFlavor();
                target.addComponent(geoLocationDiv);
                target.addComponent(placeDiv);
            }
        };
        addReplaceable(isAPlaceCheckBox);
        isAGeoLocationCheckBox = new AjaxCheckBox("isAGeoLocation", new Model((Boolean)location.isAGeoLocation())){
            protected void onUpdate(AjaxRequestTarget target) {
                boolean isAGeoLocation = (Boolean)isAGeoLocationCheckBox.getModelObject();
                isAPlaceCheckBox.setModel(new Model((Boolean)!isAGeoLocation));
                updateLocationFlavor();
                target.addComponent(geoLocationDiv);
                target.addComponent(placeDiv);
            }
        };
        addReplaceable(isAGeoLocationCheckBox);
        placeDiv = new WebMarkupContainer("placeDiv");
        addReplaceable(placeDiv);
        placeTree = new DynamicFilterTree("place", new RefPropertyModel(getElement(), propPath+".location"),
                                          new RefPropertyModel(getProject(), "places"), SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref place = placeTree.getNewSelection();
                RefUtils.set(getElement(), propPath+".place", place);
            }
        };
        addReplaceableTo(placeTree, placeDiv);
        geoLocationDiv = new WebMarkupContainer("geoLocationDiv");
        addReplaceable(geoLocationDiv);
        geoLocationPanel = new GeoLocationPanel("geoLocation", this, "geoLocation", isReadOnly(), feedback);
        addReplaceableTo(geoLocationPanel, geoLocationDiv);
        setVisibility();
    }

    private void updateLocationFlavor() {
        boolean isAPlace = (Boolean)isAPlaceCheckBox.getModelObject();
        if (isAPlace) {
            priorGeoLocation = location.getGeoLocation();
            RefUtils.set(getElement(), propPath+".geoLocation", new GeoLocation());
            RefUtils.set(getElement(), propPath+".place", priorPlace);
            placeTree.modelChanged();
            geoLocationPanel.modelChanged();
        }
        else {
            priorPlace = location.getPlace();
            RefUtils.set(getElement(), propPath+".place", null);
            RefUtils.set(getElement(), propPath+".geoLocation", priorGeoLocation);
            geoLocationPanel = new GeoLocationPanel("geoLocation", this, "geoLocation", isReadOnly(), feedback);
            geoLocationDiv.addOrReplace(geoLocationPanel);
            placeTree.modelChanged();
        }
        setVisibility();
    }

    private void setVisibility() {
       boolean isAPlace = (Boolean)isAPlaceCheckBox.getModelObject();
        if (isAPlace) {
            display(placeDiv);
            hide(geoLocationDiv);
        }
        else {
            display(geoLocationDiv);
            hide(placeDiv);
        }
    }


}
