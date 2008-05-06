package com.mindalliance.channels.playbook.pages.forms.tabs.modelElement;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractModelElementFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 3:21:20 PM
 */
public class ModelElementBasicTab extends AbstractModelElementFormTab {

    protected TextField nameField;
    protected TextArea descriptionField;
    protected DynamicFilterTree narrowedTypesTree;

    public ModelElementBasicTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        nameField = new TextField("name", new RefPropertyModel(getElement(), "name"));
        addInputField(nameField);
        descriptionField = new TextArea("description", new RefPropertyModel(getElement(), "description"));
        narrowedTypesTree = new DynamicFilterTree("narrowedTypes",
                                                  new RefPropertyModel(getElement(), "narrowedTypes"),
                                                  new RefQueryModel(getScope(), new Query("findAllTypes", getElement().getType()))
                            ) {
            public void onFilterSelect( AjaxRequestTarget target, Filter filter ) {
                List<Ref> selectedTypes = narrowedTypesTree.getNewSelections();
                RefUtils.set(getElement(), "narrowedTypes", selectedTypes);
            }
        };
        addReplaceable(narrowedTypesTree);
    }

}
