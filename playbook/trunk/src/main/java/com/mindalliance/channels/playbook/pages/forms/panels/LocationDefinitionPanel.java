package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.renderers.RefChoiceRenderer;
import com.mindalliance.channels.playbook.ifm.definition.LocationDefinition;
import com.mindalliance.channels.playbook.ifm.model.AreaType;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.Model;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 24, 2008
 * Time: 1:10:46 PM
 */
public class LocationDefinitionPanel extends AbstractDefinitionPanel {

    protected static final String WITHIN = "within";
    protected static final String NEAR = "near";
    protected static final String IN_THE_SAME = "in the same";
    protected static final String IN_A_NEARBY = "in a neighboring";
    protected static final String THE_JURISDICTION_OF = "the jurisdiction of";
    protected static final String THE_LOCATION_OF = "the location of";
    protected static final String THIS_PLACE = "this place";


    protected LocationDefinition locationDefinition;
    protected AjaxCheckBox isPlaceCheckBox;
    protected AjaxCheckBox isGeoLocationCheckBox;
    protected WebMarkupContainer placeTypeDiv;
    protected DynamicFilterTree placeTypeTree;
    protected AjaxCheckBox isAffirmedCheckBox;
    protected AjaxCheckBox isNegatedCheckBox;
    protected AjaxCheckBox isByGeoLocationCheckBox;
    protected DropDownChoice byGeoLocationRelationChoice;
    protected GeoLocationPanel geoLocationPanel;
    protected AjaxCheckBox isByJurisdictionCheckBox;
    protected DropDownChoice byJurisdictionRelationChoice;
    protected DynamicFilterTree jurisdictionableTree;
    protected AjaxCheckBox isByProximityCheckBox;
    protected DropDownChoice byProximityRelationChoice;
    protected DropDownChoice placeTypesChoice;
    protected DropDownChoice areaTypesChoice;
    protected Label asToLabel;
    protected DropDownChoice kindOfTargetChoice;
    protected DynamicFilterTree targetTree;

    public LocationDefinitionPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }
    
    protected void load() {
        super.load();
        locationDefinition = (LocationDefinition)getComponent();
        isPlaceCheckBox = new AjaxCheckBox("isPlace", new Model((Boolean)locationDefinition.getLocationIsAPlace())) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean isPlace = (Boolean)isPlaceCheckBox.getModelObject();
                setProperty("locationIsAPlace", isPlace);
                setVisibility(placeTypeDiv, isPlace, target);
                toggle(isPlaceCheckBox, isGeoLocationCheckBox, target);
            }
        };
        addReplaceable(isPlaceCheckBox);
        isGeoLocationCheckBox = new AjaxCheckBox("isGeoLocation", new Model((Boolean)!locationDefinition.getLocationIsAPlace())) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean isGeoLocation = (Boolean)isPlaceCheckBox.getModelObject();
                setProperty("locationIsAPlace", !isGeoLocation);
                setVisibility(placeTypeDiv, !isGeoLocation, target);
                toggle(isGeoLocationCheckBox, isPlaceCheckBox, target);
            }
        };
        addReplaceable(isGeoLocationCheckBox);
        placeTypeDiv = new WebMarkupContainer("placeTypeDiv");
        setVisibility(placeTypeDiv, locationDefinition.getLocationIsAPlace());
        addReplaceable(placeTypeDiv);
        placeTypeTree = new DynamicFilterTree("placeType", new RefPropertyModel(getComponent(), "placeType"),
                                               new RefPropertyModel(getProject(), "places"),
                                               SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref placeType = placeTypeTree.getNewSelection();
                setProperty("placeType", placeType);
                setVisibility(placeTypesChoice, isFresh(placeType), target);
            }
        };
        addReplaceableTo(placeTypeTree, placeTypeDiv);
        isAffirmedCheckBox = new AjaxCheckBox("isAffirmed", new Model((Boolean)!locationDefinition.isNegated())) {
            protected void onUpdate(AjaxRequestTarget target) {
                setProperty("negated", !(Boolean)isAffirmedCheckBox.getModelObject());
                toggle(isAffirmedCheckBox, isNegatedCheckBox, target);
            }
        };
        addReplaceable(isAffirmedCheckBox);
        isNegatedCheckBox = new AjaxCheckBox("isNegated", new Model((Boolean)locationDefinition.isNegated())) {
            protected void onUpdate(AjaxRequestTarget target) {
                setProperty("negated", (Boolean)isNegatedCheckBox.getModelObject());
                toggle(isNegatedCheckBox, isAffirmedCheckBox, target);
            }
        };
        addReplaceable(isNegatedCheckBox);
        isByGeoLocationCheckBox = new AjaxCheckBox("isByGeoLocation", new RefPropertyModel(getComponent(), "byGeoLocation")) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean isByGeoLocation = (Boolean)isByGeoLocationCheckBox.getModelObject();
                setProperty("byGeoLocation", isByGeoLocation);
                enable(byGeoLocationRelationChoice,isByGeoLocation, target);
                geoLocationPanel = new GeoLocationPanel("geoLocation", LocationDefinitionPanel.this, propPath+".geoLocation", isReadOnly(), feedback);
                addReplaceable(geoLocationPanel);
                setVisibility(geoLocationPanel,isByGeoLocation, target);
            }
        };
        addReplaceable(isByGeoLocationCheckBox);
        byGeoLocationRelationChoice = new DropDownChoice("byGeoLocationRelation",
                                                      new RefQueryModel(this, new Query("getGeoLocationRelation")),
                                                      new Model((Serializable)getRelationChoices()));
        byGeoLocationRelationChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean isWithin = meansIsWithin(byGeoLocationRelationChoice.getModelObjectAsString());
                setProperty("withinGeoLocation", isWithin);
            }
        });
        byGeoLocationRelationChoice.setEnabled(locationDefinition.isByGeoLocation());
        addReplaceable(byGeoLocationRelationChoice);
        geoLocationPanel = new GeoLocationPanel("geoLocation", this, propPath+".geoLocation", isReadOnly(), feedback);
        setVisibility(geoLocationPanel, locationDefinition.isByGeoLocation());
        addReplaceable(geoLocationPanel);
        isByJurisdictionCheckBox = new AjaxCheckBox("isByJurisdiction", new RefPropertyModel(getElement(), propPath+".byJurisdiction")) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean isByJurisdiction = (Boolean)isByJurisdictionCheckBox.getModelObject();
                enable(byJurisdictionRelationChoice,isByJurisdiction, target);
                if (!isByJurisdiction) setProperty("jurisdictionable", null);
                jurisdictionableTree.modelChanged();
                setVisibility(jurisdictionableTree,isByJurisdiction, target);
            }
        };
        addReplaceable(isByJurisdictionCheckBox);
        byJurisdictionRelationChoice = new DropDownChoice("byJurisdictionRelation",
                                                      new RefQueryModel(this, new Query("getJurisdictionRelation")),
                                                      new Model((Serializable)getRelationChoices()));
        byJurisdictionRelationChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean isWithin = meansIsWithin(byJurisdictionRelationChoice.getModelObjectAsString());
                setProperty("withinJurisdiction", isWithin);
            }
        });
        byJurisdictionRelationChoice.setEnabled(locationDefinition.isByJurisdiction());
        addReplaceable(byJurisdictionRelationChoice);
        jurisdictionableTree = new DynamicFilterTree("jurisdictionable", new RefPropertyModel(getComponent(), "jurisdictionable"),
                                                      new RefQueryModel(getScope(), new Query("findAllJurisdictionables")), 
                                                      SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selected = jurisdictionableTree.getNewSelection();
                setProperty("jurisdictionable", selected);
            }
        };
        setVisibility(jurisdictionableTree, locationDefinition.isByJurisdiction());
        addReplaceable(jurisdictionableTree);
        isByProximityCheckBox = new AjaxCheckBox("isByProximity", new RefPropertyModel(getElement(), propPath+".byProximity")) {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean isByProximity = (Boolean)isByProximityCheckBox.getModelObject();
                enable(byProximityRelationChoice,isByProximity, target);
                enable(placeTypesChoice,isByProximity, target);
                enable(areaTypesChoice,isByProximity, target);
                enable(kindOfTargetChoice,isByProximity, target);
                targetTree.modelChanged();
                setVisibility(targetTree,isByProximity, target);
            }
        };
        addReplaceable(isByProximityCheckBox);
        byProximityRelationChoice = new DropDownChoice("byProximityRelation",
                                                      new RefQueryModel(this, new Query("getProximityRelation")),
                                                      new Model((Serializable)getProximityRelationChoices()));
        byProximityRelationChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                boolean isWithin = meansIsWithin(byProximityRelationChoice.getModelObjectAsString());
                setProperty("withinProximity", isWithin);
                if (isWithin) {
                    asToLabel.setModelObject("as");
                }
                else {
                    asToLabel.setModelObject("of");
                }
                target.addComponent(asToLabel);
                target.addComponent(targetTree);
            }
        });
        byProximityRelationChoice.setEnabled(locationDefinition.isByProximity());
        addReplaceable(byProximityRelationChoice);
        areaTypesChoice = new DropDownChoice("areaTypes",
                                            new Model(getProximityAreaTypeName()),
                                            new Model((Serializable) AreaType.allAreaTypeNames()));
        areaTypesChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                Ref areaType = AreaType.areaTypeNamed(areaTypesChoice.getModelObjectAsString());
                setProperty("proximityAreaType", areaType);
                placeTypesChoice.setModelObject(null);
                target.addComponent(placeTypesChoice); // should show no selection in proximity place types
                targetTree.modelChanged();
                target.addComponent(targetTree);
            }
        });
        areaTypesChoice.setEnabled(locationDefinition.isByProximity() && isFresh(locationDefinition.getProximityAreaType()));
        addReplaceable(areaTypesChoice);
        placeTypesChoice = new DropDownChoice("placeTypes",
                                           new RefPropertyModel(getElement(), propPath+".proximityPlaceType"),
                                           new Model((Serializable)getProximityPlaceTypeChoices()),
                                           new RefChoiceRenderer("name", "id"));
        placeTypesChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                areaTypesChoice.setEnabled(locationDefinition.isByProximity() && isFresh(locationDefinition.getProximityAreaType()));
                areaTypesChoice.setModelObject(null);
                target.addComponent(areaTypesChoice); // should show no selection in proximity area types
                targetTree.modelChanged();
                target.addComponent(targetTree);
            }
        });
        placeTypesChoice.setEnabled(locationDefinition.isByProximity() && isFresh(locationDefinition.getProximityPlaceType()));
        setVisibility(placeTypesChoice, isFresh(locationDefinition.getPlaceType()));
        addReplaceable(placeTypesChoice);
        asToLabel = new Label("as-of", new Model("as"));
        addReplaceable(asToLabel);
        kindOfTargetChoice = new DropDownChoice("kindOfTarget", new Model(getProximityTargetKind()),
                                                            new Model((Serializable)getProximityTargetKindChoices()));
        kindOfTargetChoice.add(new AjaxFormComponentUpdatingBehavior("onchange"){
            protected void onUpdate(AjaxRequestTarget target) {
                targetTree.modelChanged();
                target.addComponent(targetTree);
            }
        });
        kindOfTargetChoice.setEnabled(locationDefinition.isByProximity());
        addReplaceable(kindOfTargetChoice);
        targetTree = new DynamicFilterTree("targetTree", new RefQueryModel(this, new Query("getSelectedProximityTarget")),
                                                         new RefQueryModel(this, new Query("getProximityTargetChoices")),
                                            SINGLE_SELECTION){
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selected = targetTree.getNewSelection();
                setProximityTarget(selected);
            }
        };
        setVisibility(targetTree, locationDefinition.isByProximity());
        addReplaceable(targetTree);
    }

    public String getGeoLocationRelation() {
        return locationDefinition.isWithinGeoLocation() ? WITHIN : NEAR;
    }

    public String getJurisdictionRelation() {
        return locationDefinition.isWithinJurisdiction() ? WITHIN : NEAR;
    }

    public String getProximityRelation() {
        return locationDefinition.isWithinProximity() ? IN_THE_SAME : IN_A_NEARBY;
    }

    private List<String> getRelationChoices() {
        return Arrays.asList(WITHIN, NEAR);
    }

    private List<String> getProximityRelationChoices() {
        return Arrays.asList(IN_THE_SAME, IN_A_NEARBY);
    }

    private boolean meansIsWithin(String value) {
        return (value.equals(WITHIN) || value.equals(IN_THE_SAME));
    }

    public String getProximityAreaTypeName() {
        Ref areaType = locationDefinition.getProximityAreaType();
        if (isFresh(areaType)) {
            return (String)RefUtils.get(areaType, "name");
        }
        else {
            return null;
        }
    }

    private List<Ref> getProximityPlaceTypeChoices() {
        List<Ref> placeTypeChoices;
        Ref locationPlaceType = locationDefinition.getPlaceType();
        boolean targetIsWithin = locationDefinition.getWithinProximity();
        if (isFresh(locationPlaceType)) {
            placeTypeChoices= (List<Ref>)Query.execute(getProject(), "findAllCompatiblePlaceTypes", locationPlaceType, targetIsWithin); // TODO
        }
        else {
            placeTypeChoices = new ArrayList<Ref>();
        }
        return placeTypeChoices;
    }

    private List<String> getProximityTargetKindChoices() {
         return Arrays.asList(THIS_PLACE, THE_LOCATION_OF, THE_JURISDICTION_OF);
    }

    public String getProximityTargetKind() {
        if (isFresh(locationDefinition.getProximalJurisdictionable())) return THE_JURISDICTION_OF;
        else if (isFresh(locationDefinition.getProximalLocatable())) return THE_LOCATION_OF;
        else if (isFresh(locationDefinition.getProximalPlace())) return THIS_PLACE;
        else return null;
    }

    public List<Ref> getProximityTargetChoices() {
        Ref proximityPlaceType = locationDefinition.getProximityPlaceType();
        if (isFresh(proximityPlaceType)) {
            if (targetIsPlace()) {
                return (List<Ref>)Query.execute(getProject(), "findAllPlacesOfTypeImplying", proximityPlaceType);
            }
            else if (targetIsLocatable()) {
                return (List<Ref>)Query.execute(getScope(), "findAllAgentsLocatedInPlacesOfTypeImplying", proximityPlaceType);
            }
            else if (targetIsJurisdictionable()) {
                return (List<Ref>)Query.execute(getScope(), "findAllAgentsWithJurisdictionsInPlacesOfTypeImplying", proximityPlaceType);
            }
            else return new ArrayList<Ref>();
        }
        else {
            if (targetIsPlace()) {
                return (List<Ref>)Query.execute(getProject(), "findAllPlacesInAreasOfTypeImplying", proximityPlaceType);
            }
            else if (targetIsLocatable()) {
                return (List<Ref>)Query.execute(getScope(), "findAllAgentsLocatedInAreasOfTypeImplying", proximityPlaceType);
            }
            else if (targetIsJurisdictionable()) {
                return (List<Ref>)Query.execute(getScope(), "findAllAgentsWithJurisdictionsInAreasOfTypeImplying", proximityPlaceType);
            }
            else return new ArrayList<Ref>();
        }
    }

    private boolean targetIsPlace() {
        return kindOfTargetChoice.getModelObjectAsString().equals(THIS_PLACE);
    }

    private boolean targetIsLocatable() {
        return kindOfTargetChoice.getModelObjectAsString().equals(THE_LOCATION_OF);
    }

    private boolean targetIsJurisdictionable() {
        return kindOfTargetChoice.getModelObjectAsString().equals(THE_JURISDICTION_OF);
    }

    public Ref getSelectedProximityTarget() {
        if (isFresh(locationDefinition.getProximalJurisdictionable())) return locationDefinition.getProximalJurisdictionable();
        else if (isFresh(locationDefinition.getProximalLocatable())) return locationDefinition.getProximalLocatable();
        else if (isFresh(locationDefinition.getProximalPlace())) return locationDefinition.getProximalPlace();
        else return null;
    }

    private void setProximityTarget(Ref selected) {
        String proximityTargetKind = kindOfTargetChoice.getModelObjectAsString();
        if (proximityTargetKind != null) {
            if (proximityTargetKind.equals(THIS_PLACE)) locationDefinition.setProximalPlace(selected);
            else if (proximityTargetKind.equals(THE_LOCATION_OF)) locationDefinition.setProximalLocatable(selected);
            else if (proximityTargetKind.equals(THE_JURISDICTION_OF)) locationDefinition.setProximalJurisdictionable(selected);
            else throw new RuntimeException("Unknown kind of proximity");   // Should never get here
        }
    }

}
