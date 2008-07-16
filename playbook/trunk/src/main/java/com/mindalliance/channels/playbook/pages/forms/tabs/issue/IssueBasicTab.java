package com.mindalliance.channels.playbook.pages.forms.tabs.issue;

import com.mindalliance.channels.playbook.pages.forms.tabs.analysisElement.AnalysisElementBasicTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.analysis.Issue;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 16, 2008
 * Time: 3:14:14 PM
 */
public class IssueBasicTab extends AnalysisElementBasicTab {

    protected AjaxLink elementLink;
    protected Label elementLabel;
    protected Label tagLabel;
    protected Issue issue;

    public IssueBasicTab(String id, AbstractElementForm elementForm) {
        super(id, elementForm);
    }

    protected void load() {
        super.load();
        issue = (Issue)getElement().deref();
        elementLink = new AjaxLink("elementLink"){
                    public void onClick(AjaxRequestTarget target) {
                        edit(issue.getElement().getReference(), target);
                    }
                };
        addReplaceable(elementLink);
        elementLabel = new Label("element", new Model(issue.getElement().toString()));
        elementLink.add(elementLabel);
        tagLabel = new Label("tag", new Model(issue.labelText()));
        addReplaceable(tagLabel);
    }
}
