package com.mindalliance.channels.pages.components;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 23, 2009
 * Time: 7:51:38 PM
 */
public class ExpandedIssuePanel extends Panel {

    private Issue issue;

    private boolean markedForDeletion = false;

    public ExpandedIssuePanel( String id, Issue issue ) {
        super( id );
        this.issue = issue;
        init();
    }

    private void init() {
        setOutputMarkupId( true );
        String url = getRequest().getURL().replaceAll( "&expand=" + issue.getId(), "" );
        ExternalLink expandLink = new ExternalLink( "hide", url );
        add( expandLink );
        add( new CheckBox( "delete",                                                      // NON-NLS
                new PropertyModel<Boolean>( new IssuesPanel.DeletableWrapper( issue ), "markedForDeletion" ) ) );      // NON-NLS
        add( new TextArea<String>( "description", new PropertyModel<String>( issue, "description" ) ) );
        add( new TextArea<String>( "remediation", new PropertyModel<String>( issue, "remediation" ) ) );
        add( new AttachmentPanel( "attachments", (ModelObject) issue ) );                                // NON-NLS
        add( new Label( "reported-by", new PropertyModel<String>( issue, "reportedBy" ) ) );
    }

}
