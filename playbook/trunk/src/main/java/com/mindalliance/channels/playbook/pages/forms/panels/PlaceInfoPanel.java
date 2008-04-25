package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractComponentPanel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.info.PlaceInfo;
import com.mindalliance.channels.playbook.ifm.info.PlaceItem;
import com.mindalliance.channels.playbook.ifm.model.PlaceType;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 22, 2008
 * Time: 3:02:09 PM
 */
public class PlaceInfoPanel extends AbstractComponentPanel {

    PlaceInfo placeInfo;
    WebMarkupContainer placeItemsDiv;
    RefreshingView placeItemsView;

    public PlaceInfoPanel(String id, Ref element, String propPath, boolean readOnly) {
        super(id, element, propPath, readOnly);
    }

    public PlaceInfoPanel(String id, Ref element, String propPath) {
        super(id, element, propPath);
    }

    protected void load() {
        super.load();
        placeInfo = (PlaceInfo) RefUtils.get(element, propPath);
        placeItemsDiv = new WebMarkupContainer("placeItemsDiv");
        placeItemsView = new RefreshingView("placeItems", new RefPropertyModel(placeInfo, "placeItems")) {

            protected Iterator getItemModels() {
                IModel model = getModel();
                List items = new ArrayList();
                items.addAll((List) model.getObject());
                // Add an empty place item if it makes sense to do so
                if (moreNarrowingPossible()) {
                    items.add(new PlaceItem()); // add one to be specified, if at all
                }
                return new ModelIteratorAdapter(items.iterator()) {
                    protected IModel model(Object placeItem) {
                        return new Model((PlaceItem) placeItem);
                    }
                };
            }

            protected void populateItem(Item item) {
                final PlaceItem placeItem = (PlaceItem) item.getModelObject();
                List<String> placeTypeNameChoices = placeTypeNameChoicesFor(placeItem);
                // Add place item placeType dropdown
                final String placeTypeName = (String) RefUtils.get(placeItem, "placeType.name");
                final DropDownChoice placeTypeChoice = new DropDownChoice("placeType", new Model(placeTypeName), placeTypeNameChoices);
                placeTypeChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    protected void onUpdate(AjaxRequestTarget target) {
                        String newPlaceTypeName = placeTypeChoice.getModelObjectAsString();
                        Ref placeType = project.findElementTypeNamed("PlaceType", newPlaceTypeName);
                        placeItem.setPlaceType(placeType);
                        // remove all place items downstream, if any
                        List<PlaceItem> placeItems = placeInfo.getPlaceItems();
                        int index = placeItems.indexOf(placeItem);
                        if (index != -1) { // drop subsequent place items
                            placeInfo.setPlaceItems(placeItems.subList(0, index));
                        } else {
                            placeItems.add(placeItem); // new place item at end
                        }
                        elementChanged();
                        target.addComponent(placeItemsDiv);
                    }
                });
                item.add(placeTypeChoice);
                // Add place item name textfield
                TextField placeName = new TextField("placeName", new RefPropertyModel(placeItem, "placeName"));
                placeName.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    protected void onUpdate(AjaxRequestTarget target) {
                        elementChanged();
                    }
                });
                item.add(placeName);
            }
        };
        placeItemsDiv.add(placeItemsView);
        addContainer(placeItemsDiv);
    }

    private List<String> placeTypeNameChoicesFor(PlaceItem placeItem) {
        List<Ref> placeTypes;
        List<String> choices = new ArrayList<String>();
        List<PlaceItem> placeItems = placeInfo.getPlaceItems();
        int index = placeItems.indexOf(placeItem);
        Ref priorPlaceType;
        if (index <= 0) { // narrow resource's place's placeType, if set
            priorPlaceType = (Ref) RefUtils.get(element, "location.place.placeType");
        } else {
            priorPlaceType = placeItems.get(index - 1).getPlaceType();
        }
        if (priorPlaceType == null) {
            placeTypes = project.findAllTypes("PlaceType");
        } else {
            placeTypes = project.findAllTypesNarrowing(priorPlaceType);
        }
        for (Ref placeType : placeTypes) {
            choices.add((String) RefUtils.get(placeType, "name"));
        }
        return choices;
    }

    // case 1 - no place items set and there is at least one defined PlaceType in a model
    // case 2 - last place item set has a PlaceType that can be narrowed
    private boolean moreNarrowingPossible() {
        List<PlaceItem> placeItems = placeInfo.getPlaceItems();
        if (placeItems.size() == 0) {
            return project.atleastOnePlaceTypeDefined();
        } else {
            PlaceItem lastPlaceItem = placeItems.get(placeItems.size() - 1);
            return !placeTypeNameChoicesFor(lastPlaceItem).isEmpty();
        }
    }

}
