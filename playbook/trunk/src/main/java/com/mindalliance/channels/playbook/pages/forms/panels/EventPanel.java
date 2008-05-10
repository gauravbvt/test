package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 9, 2008
 * Time: 3:00:25 PM
 */
public class EventPanel extends AbstractComponentPanel {

    protected TextArea descriptionField;
    protected DynamicFilterTree eventTypesTree;
    protected LocationPanel locationPanel;

    public EventPanel(String id, ElementPanel parentPanel, String propPath, boolean readOnly, FeedbackPanel feedback) {
        super(id, parentPanel, propPath, readOnly, feedback);
    }

    protected void load() {
        super.load();
        descriptionField = new TextArea("description", new RefPropertyModel(getElement(), propPath));
        addInputField(descriptionField);
        eventTypesTree = new DynamicFilterTree("eventTypes", new RefPropertyModel(getElement(), propPath+".eventTypes"),
                                                new RefQueryModel(getScope(), new Query("findAllTypes", "EventType"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selectedTypes = eventTypesTree.getNewSelections();
                RefUtils.set(getElement(), propPath+".eventTypes", selectedTypes);
                elementChanged(propPath+".eventTypes", target);
            }
        };
        addReplaceable(eventTypesTree);
        locationPanel = new LocationPanel("location", this, propPath+".location", isReadOnly(), feedback);
        addReplaceable(locationPanel);
    }
}
