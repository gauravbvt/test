package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.pages.ScenarioPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.IModel;

import java.util.Arrays;

/**
 * Editable (user) issue
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 23, 2009
 * Time: 7:51:38 PM
 */
public class ExpandedIssuePanel extends Panel {
    /**
     * Issue shown in panel
     */
    private Issue issue;

    public ExpandedIssuePanel( String id, IModel<Issue> model ) {
        super( id );
        this.issue = model.getObject();
        init();
    }

    private void init() {
        setOutputMarkupId( true );
        // TODO - hack - adjust for Bookmarkable link
        String url = getRequest().getURL()
                .replaceAll( "&" + ScenarioPage.EXPAND_PARM + "=" + issue.getId(), "" );
        ExternalLink expandLink = new ExternalLink( "hide", url );
        add( expandLink );
        add( new CheckBox( "delete",                                                      // NON-NLS
                new PropertyModel<Boolean>(
                        new IssuesPanel.DeletableWrapper( issue ),
                        "markedForDeletion" ) ) );

        TextArea<String> descriptionArea = new TextArea<String>( "description",                                      // NON-NLS
                new PropertyModel<String>( issue, "description" ) );
        descriptionArea.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        });
        add( descriptionArea );
        DropDownChoice<Issue.Level> levelChoice = new DropDownChoice<Issue.Level>(
                "severity",
                new PropertyModel<Issue.Level>( issue, "severity" ),
                Arrays.asList( Issue.Level.values() ) );
        levelChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        });
        add( levelChoice );
        TextArea<String> remediationArea = new TextArea<String>( "remediation",
                new PropertyModel<String>( issue, "remediation" ) );
        remediationArea.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                // do nothing
            }
        });
        add( remediationArea );
        add( new AttachmentPanel( "attachments", (ModelObject) issue ) );                 // NON-NLS
        add( new Label( "reported-by",
                new PropertyModel<String>( issue, "reportedBy" ) ) );
    }


}
