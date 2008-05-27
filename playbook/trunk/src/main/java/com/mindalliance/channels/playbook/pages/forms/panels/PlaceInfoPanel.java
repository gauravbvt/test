package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.panels.AbstractComponentPanel;
import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.info.PlaceInfo;
import com.mindalliance.channels.playbook.ifm.info.PlaceItem;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.renderers.RefChoiceRenderer;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
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

    public PlaceInfoPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        placeInfo = (PlaceInfo) RefUtils.get(getElement(), propPath);
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
                // Add place item placeType dropdown
                final Ref placeType = (Ref) RefUtils.get(placeItem, "placeType");
                List<Ref> placeTypeChoices = placeTypeChoicesFor(placeItem);
                final DropDownChoice placeTypeChoice = new DropDownChoice("placeType",
                                                                           new Model(placeType),
                                                                           placeTypeChoices,
                                                                           new RefChoiceRenderer("name", "id"));
                placeTypeChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    protected void onUpdate(AjaxRequestTarget target) {
                        Ref placeType = (Ref)placeTypeChoice.getModelObject();
                        placeItem.setPlaceType(placeType);
                        // remove all place items downstream, if any
                        List<PlaceItem> placeItems = placeInfo.getPlaceItems();
                        int index = placeItems.indexOf(placeItem);
                        if (index != -1) { // drop subsequent place items
                            placeInfo.setPlaceItems(placeItems.subList(0, index));
                        } else {
                            placeItems.add(placeItem); // new place item at end
                        }
                        elementChanged(propPath, target);
                        target.addComponent(placeItemsDiv);
                    }
                });
                item.add(placeTypeChoice);
                // Add place item name textfield
                TextField placeName = new TextField("placeName", new RefPropertyModel(placeItem, "placeName"));
                placeName.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    protected void onUpdate(AjaxRequestTarget target) {
                        elementChanged(propPath, target);
                    }
                });
                item.add(placeName);
            }
        };
        placeItemsDiv.add(placeItemsView);
        addReplaceable(placeItemsDiv);
    }

    private List<Ref> placeTypeChoicesFor(PlaceItem placeItem) {
        List<PlaceItem> placeItems = placeInfo.getPlaceItems();
        int index = placeItems.indexOf(placeItem);
        List<Ref> priorPlaceTypes = new ArrayList<Ref>();
        if (placeItems.isEmpty() || index == 0) { // narrow parent's place's placeType, if set
            priorPlaceTypes = (List<Ref>) RefUtils.get(parentPanel.getObject(), "place.placeTypes");
        } else { // narrow
            int priorIndex;
            if (index == -1) { // new place item
                priorIndex = placeItems.size() - 1;
            }
            else {
                priorIndex = index - 1;
            }
            if (priorIndex >= 0) priorPlaceTypes.add(placeItems.get(priorIndex).getPlaceType());
        }
        return narrowingPlaceTypes(priorPlaceTypes);
    }

    private List<Ref> narrowingPlaceTypes(List<Ref> priorPlaceTypes) {
        List<Ref> placeTypes;
        if (priorPlaceTypes == null || priorPlaceTypes.isEmpty()) {
            placeTypes = (List<Ref>)Query.execute(getProject(), "findAllTypes", "PlaceType");
        } else {
            placeTypes = (List<Ref>)Query.execute(getProject(),"findAllTypesNarrowingAny", priorPlaceTypes);
        }
        return placeTypes;
    }

    // case 1 - no place items set and there is at least one defined PlaceType in a model
    // case 2 - last place item set has a PlaceType that can be narrowed
    private boolean moreNarrowingPossible() {
        List<PlaceItem> placeItems = placeInfo.getPlaceItems();
        if (placeItems.size() == 0) {
            return (Boolean)Query.execute(getProject(),"atleastOnePlaceTypeDefined");
        } else {
            PlaceItem lastPlaceItem = placeItems.get(placeItems.size() - 1);
            List<Ref> placeTypes = new ArrayList<Ref>();
            placeTypes.add(lastPlaceItem.getPlaceType());
            return !narrowingPlaceTypes(placeTypes).isEmpty();
        }
    }

}
