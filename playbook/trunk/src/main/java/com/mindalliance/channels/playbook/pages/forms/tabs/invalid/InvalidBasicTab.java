package com.mindalliance.channels.playbook.pages.forms.tabs.invalid;

import com.mindalliance.channels.playbook.pages.forms.tabs.analysisElement.AnalysisElementBasicTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.analysis.problem.Invalidation;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 16, 2008
 * Time: 3:16:42 PM
 */
public class InvalidBasicTab extends AnalysisElementBasicTab {

    Invalidation invalid;
    protected AjaxLink elementLink;
    protected Label elementLabel;
    protected Label tagLabel;

    public InvalidBasicTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        invalid = (Invalidation)getElement().deref();
        elementLink = new AjaxLink("elementLink"){
                    public void onClick(AjaxRequestTarget target) {
                        edit(invalid.getElement().getReference(), target);
                    }
                };
        addReplaceable(elementLink);
        elementLabel = new Label("element", new Model(invalid.getElement().toString()));
        elementLink.add(elementLabel);
        tagLabel = new Label("tag", new Model(invalid.labelText()));
        addReplaceable(tagLabel);
    }
}
