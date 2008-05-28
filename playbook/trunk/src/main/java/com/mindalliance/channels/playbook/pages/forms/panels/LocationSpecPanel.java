package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.ifm.spec.LocationSpec;
import com.mindalliance.channels.playbook.ifm.spec.Spec;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.renderers.RefChoiceRenderer;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.query.Query;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.CheckBox;
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
public class LocationSpecPanel extends AbstractSpecComponentPanel {

    CheckBox definedField;
    DynamicFilterTree placeTypeTree;
    DropDownChoice areaTypeChoice;
    DropDownChoice relationChoice;

    public LocationSpecPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        final LocationSpec locationSpec = (LocationSpec)getComponent();
        super.load();
        // place types
        placeTypeTree = new DynamicFilterTree("placeTypes", new RefPropertyModel(getComponent(), "placeTypes"),
                new RefQueryModel(getScope(), new Query("findAllTypes", "PlaceType"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> newSelections = placeTypeTree.getNewSelections();
                RefUtils.set(locationSpec, "placeTypes", newSelections);
                elementChanged(propPath, target);
            }
        };
        addReplaceable(placeTypeTree);
        // area type
        // List<Ref> allAreaTypes = project.findAllTypes("AreaType");
        areaTypeChoice = new DropDownChoice("areaType", new Model((Serializable) RefUtils.get(locationSpec, "areaType")),
                new RefQueryModel(getScope(), new Query("findAllTypes", "AreaType")),
                new RefChoiceRenderer("name", "id"));
        areaTypeChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                Ref selectedAreaType = (Ref) areaTypeChoice.getModelObject();
                locationSpec.setAreaType(selectedAreaType);
                elementChanged(propPath, target);
            }
        });
        addReplaceable(areaTypeChoice);
        // relation to context location
        relationChoice = new DropDownChoice("relation", new Model((Serializable) RefUtils.get(locationSpec, "relation")), LocationSpec.getRelations());
        relationChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                String selectedRelation = relationChoice.getModelObjectAsString();
                locationSpec.setRelation(selectedRelation);
                elementChanged(propPath, target);
            }
        });
        addReplaceable(relationChoice);
    }

    @Override
    protected String getAnyLabelString() {
        return "anywhere";
    }

    protected Spec makeNewSpec() {
        return new LocationSpec();
    }

}
