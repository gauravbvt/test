package com.mindalliance.channels.playbook.pages.forms.tabs.elementType;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.validators.UniqueValidator;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 3:21:20 PM
 */
public class ElementTypeBasicTab extends AbstractFormTab {

    protected AutoCompleteTextField nameField;
    protected TextArea descriptionField;
    protected DynamicFilterTree narrowedTypesTree;

    public ElementTypeBasicTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        nameField = new AutoCompleteTextField("name", new RefPropertyModel(getElement(), "name")) {
            protected Iterator getChoices(String input) {
                return otherModelElementNames(input, 10);
            }
        };
        nameField.add(UniqueValidator.inQuery(getScope(), new Query("findAllOtherTypeNames", getElement())));
        addInputField(nameField);
        descriptionField = new TextArea("description", new RefPropertyModel(getElement(), "description"));
        addInputField(descriptionField);
        narrowedTypesTree = new DynamicFilterTree("narrowedTypes",
                new RefPropertyModel(getElement(), "narrowedTypes"),
                new RefQueryModel(this, new Query("findAllOtherTypes"))
        ) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selectedTypes = narrowedTypesTree.getNewSelections();
                RefUtils.set(getElement(), "narrowedTypes", selectedTypes);
            }
        };
        addReplaceable(narrowedTypesTree);
    }

    private Iterator otherModelElementNames(String input, int max) {
        List<Ref> otherTypes = findAllOtherTypes();
        List<String> names = new ArrayList<String>();
        for (Ref type : otherTypes) {
            String name = (String) RefUtils.get(type, "name");
            if (name != null && name.toLowerCase().startsWith(input.trim().toLowerCase())) names.add(name);
            if (names.size() >= max) break;
        }
        return names.iterator();
    }

    public List<Ref> findAllOtherTypes() {
        List<Ref> types = (List<Ref>) Query.execute(getScope(), "findAllTypes", getElement().getType());
        types.remove(getElement());
        return types;
    }

}
