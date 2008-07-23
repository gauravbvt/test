package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.ElementPanel;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.ifm.info.Risk;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;
import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ref.Ref;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 17, 2008
 * Time: 9:23:09 PM
 */
public class RiskPanel extends AbstractComponentPanel {

    protected Risk risk;
    protected TextArea descriptionField;
    protected TextField probabilityField;
    protected DynamicFilterTree possibleOutcomesTree;
    protected TextField valuationField;

    public RiskPanel(String id, AbstractPlaybookPanel parentPanel, String propPath) {
        super(id, parentPanel, propPath);
    }

    protected void load() {
        super.load();
        risk = (Risk)getComponent();
        descriptionField = new TextArea("description", new RefPropertyModel(getElement(), propPath+".description"));
        addInputField(descriptionField);
        probabilityField = new TextField("probability", new RefPropertyModel(getElement(), propPath+".probabilityString"));
        addInputField(probabilityField);
        possibleOutcomesTree = new DynamicFilterTree("possibleOutcomes", new RefPropertyModel(getComponent(), "possibleOutcomes"),
                                             new RefQueryModel(getProject(), new Query("findAllTypes", "EventType"))) {
            public void onFilterSelect(AjaxRequestTarget target, Filter filter) {
                List<Ref> selected = possibleOutcomesTree.getNewSelections();
                setProperty("possibleOutcomes", selected);
            }
        };
        addReplaceable(possibleOutcomesTree);
        valuationField = new TextField("valuation", new RefPropertyModel(getElement(), propPath+".valuationString"));
        addInputField(valuationField);
    }
}
