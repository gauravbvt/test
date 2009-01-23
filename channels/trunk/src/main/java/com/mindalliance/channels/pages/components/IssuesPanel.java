package com.mindalliance.channels.pages.components;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.PageParameters;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.ScenarioPage;
import com.mindalliance.channels.analysis.Analyst;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 8, 2009
 * Time: 10:34:20 AM
 */
public class IssuesPanel extends Panel {
    /**
     * Maximum length of string displayed
     */
    private static int MAX_LENGTH = 80;

    /**
     * The model object possibly with issues.
     */
    private ModelObject modelObject;

    /** Panels of expanded user issues. */
    private List<UserIssue> deletableIssues;

    public IssuesPanel( String id, IModel<ModelObject> model, final Set<Long> expansions ) {
        super( id, model );
        modelObject = model.getObject();
        init(expansions);
    }

    private void init( final Set<Long> expansions ) {
        final WebMarkupContainer issuesList = new WebMarkupContainer( "issues" );     // NON-NLS
        Link<String> newIssueLink = new Link<String>( "new-issue", new Model<String>( "New" ) ) {
            public void onClick() {
                UserIssue userIssue = new UserIssue( modelObject );
                userIssue.setReportedBy( Project.getUserName() );
                Project.dao().addUserIssue( userIssue );
                final Set<Long> newExpansions = new HashSet<Long>( expansions );
                newExpansions.add( userIssue.getId() );
                PageParameters params = getWebPage().getPageParameters();
                params.add( ScenarioPage.EXPAND_PARM, Long.toString(userIssue.getId()) );
                setResponsePage( getWebPage().getClass(), params );
            }
        };
        issuesList.add( newIssueLink );
//        add( createIssuePanels, expansions );

        // TODO 
        issuesList.add( new RefreshingView<Issue>( "issue" ) {                        // NON-NLS

            @SuppressWarnings( {"unchecked"} )
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
                String label;
                String suggestion;
                if (issue.isDetected()) {
                   label = issue.getDescription();
                    suggestion = issue.getRemediation();
                }
                else {
                   label = issue.getLabel( MAX_LENGTH );
                    suggestion = StringUtils.abbreviate( issue.getRemediation(), MAX_LENGTH );
                }
                item.add( new Label( "label", label ) );           // NON-NLS
                item.add( new Label( "suggestion", suggestion ) );           // NON-NLS
            }
        } );

        final Analyst analyst = ( (Project) getApplication() ).getAnalyst();
        setVisible( analyst.hasIssues( modelObject, false ) );
        add( issuesList );
    }
}
