package com.mindalliance.channels.playbook.pages.forms.tabs.place;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.project.environment.Place;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 10, 2008
 * Time: 7:41:42 PM
 */
public class PlaceAboutTab extends AbstractFormTab {

    protected TextField nameField;
    protected TextArea descriptionField;
    protected DynamicFilterTree placeTypeTree;
    protected DynamicFilterTree enclosingPlaceTree;

    public PlaceAboutTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        final Place place = (Place)getElement();
        nameField = new TextField("name", new RefPropertyModel(getElement(), "name"));
        addInputField(nameField);
        descriptionField = new TextArea("description", new RefPropertyModel(getElement(), "description"));
        addInputField(descriptionField);
        placeTypeTree = new DynamicFilterTree("placeType", new RefPropertyModel(getElement(), "placeType"),
                                               new RefQueryModel(getProject(), new Query("findAllTypes", "PlaceType")),
                SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                Ref selectedType = placeTypeTree.getNewSelection();
                RefUtils.set(getElement(), "placeType", selectedType);
            }
        };
        addReplaceable(placeTypeTree);
        enclosingPlaceTree = new DynamicFilterTree("enclosingPlace", new RefPropertyModel(getElement(), "enclosingPlace"),
                                                   new RefQueryModel(place, new Query("findAllCandidateEnclosingPlaces")),
                                                   SINGLE_SELECTION) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
               Ref selectedPlace = enclosingPlaceTree.getNewSelection();
                RefUtils.set(getElement(), "enclosingPlace", selectedPlace);
            }
        };
    }
}
