package com.mindalliance.channels.playbook.pages.forms.tabs.analysisElement;

import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.pages.forms.tabs.AbstractFormTab;
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
    private static final long serialVersionUID = 8894682106582210691L;

    public AnalysisElementBasicTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    @Override
    protected void load() {
        super.load();
/*
        rationaleLabel = new Label("rationale", new RefPropertyModel(getElement(), "rationale"));
        addReplaceable(rationaleLabel);
*/
    }
}
