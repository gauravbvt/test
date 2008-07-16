package com.mindalliance.channels.playbook.pages.forms.tabs.analysisElement;

import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.markup.html.basic.Label;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 16, 2008
 * Time: 3:12:39 PM
 */
public class AnalysisElementBasicTab extends AbstractFormTab {

    protected Label rationaleLabel;

    public AnalysisElementBasicTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
/*
        rationaleLabel = new Label("rationale", new RefPropertyModel(getElement(), "rationale"));
        addReplaceable(rationaleLabel);
*/
    }
}
