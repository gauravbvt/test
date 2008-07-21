package com.mindalliance.channels.playbook.pages.forms.tabs.category;

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
import com.mindalliance.channels.playbook.ifm.taxonomy.Category;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.Component;

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
public class CategoryBasicTab extends AbstractFormTab {         // TODO -- make nice (patched for implicit Categories; event types only so far

    protected Label readonly;
    protected Category category;
    protected AutoCompleteTextField nameField;
    protected TextArea descriptionField;
    protected Component narrowedTypesTree;

    public CategoryBasicTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        category = (Category) getElement().deref();
        readonly = new Label("readonly", new Model("This category is built-in and can not be edited."));
        addReplaceable(readonly);
        setVisibility(readonly, getElement().isComputed());
        nameField = new AutoCompleteTextField("name", new RefPropertyModel(getElement(), "name")) {
            protected Iterator getChoices(String input) {
                return otherCategoryNames(input, 10);
            }
        };
        nameField.setEnabled(!getElement().isComputed());
        if (!getElement().isComputed()) {
            nameField.add(UniqueValidator.inQuery(getScope(), new Query("findAllOtherTypeNames", getElement())));
        } else {
            nameField.setEnabled(false);
        }
        addInputField(nameField);
        descriptionField = new TextArea("description", new RefPropertyModel(getElement(), "description"));
        descriptionField.setEnabled(!getElement().isComputed());
        addInputField(descriptionField);
        if (!getElement().isComputed()) {
            narrowedTypesTree = new DynamicFilterTree("narrowedTypes",
                    new RefPropertyModel(getElement(), "narrowedTypes"),
                    new RefQueryModel(this, new Query("findAllOtherTypes"))
            ) {
                public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                    List<Ref> selectedTypes = ((DynamicFilterTree) narrowedTypesTree).getNewSelections();
                    RefUtils.set(getElement(), "narrowedTypes", selectedTypes);
                }
            };
        } else {
            narrowedTypesTree = new Label("narrowedTypes", new Model(narrowedTypesAsString()));
        }
        addReplaceable(narrowedTypesTree);
    }

    private String narrowedTypesAsString() {
        StringBuilder sb = new StringBuilder();
        for (Ref ref : (List<Ref>)category.getNarrowedTypes()) {
            Category broad = (Category) ref.deref();
            if (broad != null) {
                sb.append(broad.getName());
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }

    private Iterator otherCategoryNames(String input, int max) {
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
