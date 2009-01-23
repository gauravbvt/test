package com.mindalliance.channels.pages.components;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.collections.Transformer;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.analysis.Analyst;

import java.util.Iterator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 8, 2009
 * Time: 10:34:20 AM
 */
public class IssuesPanel<T extends ModelObject> extends Panel {
    /**
     * The model object possibly with issues.
     */
    private ModelObject modelObject;

    public IssuesPanel( String s, IModel<T> iModel ) {
        super( s, iModel );
        modelObject = iModel.getObject();
        init();
    }

    private void init() {
        Link newIssueLink = new Link("new-issue") {
            public void onClick() {
                UserIssue userIssue = new UserIssue( modelObject );
                userIssue.setReportedBy( Project.getUserName() );
                Project.dao().addUserIssue(userIssue);
            }
        };
        add( newIssueLink );
        final WebMarkupContainer issuesList = new WebMarkupContainer( "issues" );     // NON-NLS
        issuesList.add( new RefreshingView<Issue>( "issue" ) {                        // NON-NLS

            @SuppressWarnings( { "unchecked" } )
            @Override
            protected Iterator<IModel<Issue>> getItemModels() {
                final Project project = (Project) getApplication();
                final Analyst analyst = project.getAnalyst();
                return new TransformIterator(
                        analyst.findIssues( modelObject, false ),
                        new Transformer() {
                            public Object transform( Object o ) {
                                return new Model<Issue>( (Issue) o );
                            }
                        } );
            }

            @Override
            protected void populateItem( Item<Issue> item ) {
                final Issue issue = item.getModelObject();
                item.add( new Label( "description", issue.getDescription() ) );           // NON-NLS
                item.add( new Label( "remediation", issue.getRemediation() ) );           // NON-NLS
            }
        } );

        final Analyst analyst = ( (Project) getApplication() ).getAnalyst();
        setVisible( analyst.hasIssues( modelObject, false ) );
        add( issuesList );
    }
}
