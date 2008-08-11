package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.ifm.info.GeoLocation;
import com.mindalliance.channels.playbook.ifm.info.Location;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;

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
    private static final long serialVersionUID = 2792462610238553884L;

    public LocationPanel(String id, AbstractPlaybookPanel parentPanel, String propPath) {
        super(id, parentPanel, propPath);
    }

    @Override
    protected void load() {
        super.load();
        location = (Location)getComponent();
        priorPlace = location.getPlace();
        priorGeoLocation = location.getGeoLocation();
        isAPlaceCheckBox = new AjaxCheckBox("isAPlace", new Model<Boolean>(location.isAPlace())){
            private static final long serialVersionUID = -5585840326866305753L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                toggle(isAPlaceCheckBox, isAGeoLocationCheckBox, target);
                updateLocationFlavor(target);
                target.addComponent(geoLocationDiv);
                target.addComponent(placeDiv);
            }
        };
        addReplaceable(isAPlaceCheckBox);
        isAGeoLocationCheckBox = new AjaxCheckBox("isAGeoLocation", new Model<Boolean>(location.isAGeoLocation())){
            private static final long serialVersionUID = -5838536172248136177L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                toggle(isAGeoLocationCheckBox,isAPlaceCheckBox,  target);
                updateLocationFlavor(target);
                target.addComponent(geoLocationDiv);
                target.addComponent(placeDiv);
            }
        };
        addReplaceable(isAGeoLocationCheckBox);
        placeDiv = new WebMarkupContainer("placeDiv");
        addReplaceable(placeDiv);
        replacePlaceTree(null);
        geoLocationDiv = new WebMarkupContainer("geoLocationDiv");
        addReplaceable(geoLocationDiv);
        geoLocationPanel = new GeoLocationPanel("geoLocation", this, propPath+".geoLocation");
        addReplaceableTo(geoLocationPanel, geoLocationDiv);
        setVisibility();
    }

    private void updateLocationFlavor(AjaxRequestTarget target) {
        boolean isAPlace = isAPlaceCheckBox.getModelObject();
        if (isAPlace) {
            priorGeoLocation = location.getGeoLocation();
            setProperty("geoLocation", new GeoLocation(), target);
            setProperty("place", priorPlace, target);
            replacePlaceTree(target);
            replaceGeoLocationPanel(target);
        }
        else {
            priorPlace = location.getPlace();
            setProperty("place", null, target);
            setProperty("geoLocation", priorGeoLocation, target);
            replaceGeoLocationPanel(target);
            replacePlaceTree(target);
        }
        setVisibility();
    }

    private void replacePlaceTree(AjaxRequestTarget target) {
        placeTree = new DynamicFilterTree("place", new RefPropertyModel(getElement(), propPath+".place"),
                                          new RefPropertyModel(getProject(), "places"), SINGLE_SELECTION) {
            private static final long serialVersionUID = -6468641120444278840L;

            @Override
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                setProperty("place", placeTree.getNewSelection() );
            }
        };
        addReplaceableTo(placeTree, placeDiv);
        if (target != null) target.addComponent(placeDiv);
    }

    private void replaceGeoLocationPanel(AjaxRequestTarget target) {
        geoLocationPanel = new GeoLocationPanel("geoLocation", this, propPath+".geoLocation");
        addReplaceableTo(geoLocationPanel, geoLocationDiv);
        target.addComponent(geoLocationDiv);
    }

    private void setVisibility() {
       boolean isAPlace = isAPlaceCheckBox.getModelObject();
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
