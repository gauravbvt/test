package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractComponentPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ref.impl.RefImpl;
import com.mindalliance.channels.playbook.ifm.info.LocationSpec;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.renderers.RefChoiceRenderer;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.Model;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;

import java.util.List;
import java.io.Serializable;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 29, 2008
 * Time: 10:52:39 PM
 */
public class LocationSpecPanel extends AbstractComponentPanel {

    LocationSpec locationSpec;
    DynamicFilterTree placeTypeTree;
    DropDownChoice areaTypeChoice;
    DropDownChoice relationChoice;

    public LocationSpecPanel(String id, Ref element, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, element, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        // place types
        locationSpec = (LocationSpec)RefUtils.get(element, propPath);
        List<Ref> allPlaceTypes = project.findAllTypes("PlaceType");
        placeTypeTree = new DynamicFilterTree("placeTypes", new RefPropertyModel(locationSpec, "placeTypes"), new Model((Serializable)allPlaceTypes)) {
            public void onFilterSelect( AjaxRequestTarget target, Filter filter ) {
                List<Ref> newSelections = placeTypeTree.getNewSelections();
                RefUtils.set(locationSpec, "placeTypes", newSelections);
                elementChanged();
             }
        };
        addReplaceable(placeTypeTree);
        // area type
        List<Ref> allAreaTypes = project.findAllTypes("AreaType");
        areaTypeChoice = new DropDownChoice("areaType", new Model((Serializable)RefUtils.get(locationSpec, "areaType.name")), allAreaTypes, new RefChoiceRenderer("name", "id"));
        areaTypeChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                String id = areaTypeChoice.getModelObjectAsString();
                Ref selectedAreaType = new RefImpl(id);
                locationSpec.setAreaType(selectedAreaType);
                elementChanged();
            }
        });
        // relation to context location
        relationChoice = new DropDownChoice("relation", new Model((Serializable)RefUtils.get(locationSpec, "relation")), LocationSpec.getRelations());
        relationChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                String selectedRelation = relationChoice.getModelObjectAsString();
                locationSpec.setRelation(selectedRelation);
                elementChanged();
            }
        });

    }
}
