package com.mindalliance.channels.pages.reports;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.pages.Project;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 6, 2009
 * Time: 9:51:11 AM
 */
public class IssuesReportPanel extends Panel {
    /**
     *  The model object with issues
     */
    private ModelObject modelObject;

    public IssuesReportPanel( String id, IModel<ModelObject> model ) {
        super( id, model );
        modelObject = model.getObject();
        init( Project.analyst());
    }

    private void init( Analyst analyst ) {
        List<Issue> issues = analyst.listIssues( modelObject, true );
        add( new ListView<Issue>("issues", issues) {
            protected void populateItem( ListItem<Issue> item ) {
                Issue issue = item.getModelObject();
                item.add( new Label("reported-by", issue.getReportedBy()) );
                item.add( new Label("description", issue.getDescription()) );
                item.add( new Label("suggestion", issue.getRemediation()) );
            }
        } );
    }
}
