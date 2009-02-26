package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Deletable;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Iterator;
import java.util.Set;

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
    public static final int MAX_LENGTH = 80;

    /**
     * The model object possibly with issues.
     */
    private ModelObject modelObject;
    /**
     * Expansions from request parameters
     */
    private Set<Long> expansions;

    public IssuesPanel( String id, IModel<ModelObject> model, PageParameters parameters ) {
        super( id, model );
        modelObject = model.getObject();
        expansions = Project.findExpansions( parameters );
        init();
    }

    public IssuesPanel( String id, IModel<ModelObject> model, Set<Long> expansions ) {
        super( id, model );
        modelObject = model.getObject();
        this.expansions = expansions;
        init();
    }


    private void init() {
        // setRenderBodyOnly( true );
        final Link<String> newIssueLink = new Link<String>( "new-issue",                  // NON-NLS
                new Model<String>( "New" ) ) {
            @Override
            public void onClick() {
                final UserIssue userIssue = new UserIssue( modelObject );
                userIssue.setReportedBy( Project.getUserName() );
                getService().add( userIssue );
                final PageParameters params = getWebPage().getPageParameters();
                params.add( Project.EXPAND_PARM, Long.toString( userIssue.getId() ) );
                setResponsePage( getWebPage().getClass(), params );
            }
        };
        add( newIssueLink );
        add( createIssuePanels( expansions ) );
        setVisible( Project.analyst().hasIssues( modelObject, false ) );
    }

    private Service getService() {
        return ( (Project) getApplication() ).getService();
    }

    private RepeatingView createIssuePanels( Set<Long> expansions ) {
        final RepeatingView issuesList = new RepeatingView( "issues" );                   // NON-NLS
        final Iterator<Issue> issues = Project.analyst().findIssues( modelObject, false );
        while ( issues.hasNext() ) {
            final Issue issue = issues.next();
            final long id = issue.getId();
            final Panel issuePanel;
            if ( expansions.contains( id ) )
                issuePanel = new ExpandedIssuePanel( Long.toString( id ), issue );
            else
                issuePanel = new CollapsedIssuePanel( Long.toString( id ), issue );
            issuesList.add( issuePanel );
        }
        return issuesList;
    }


    //==================================================
    /** A wrapper to keep track of the deletion state of an attachment. */
    public static class DeletableWrapper implements Deletable {

        /** The underlying issue. */
        private Issue issue;

       /** True if user marked item for deletion. */
        private boolean markedForDeletion;

        public DeletableWrapper( Issue issue ) {
            this.issue = issue;
        }

        public boolean isMarkedForDeletion() {
            return markedForDeletion;
        }

          /**
           * {@inheritDoc}
           */
        public void setMarkedForDeletion( boolean delete ) {
            markedForDeletion = delete;
            if ( delete && !issue.isDetected() ) {
                Project.service().remove( (ModelObject) issue );
            }
        }

        public Issue getIssue() {
            return issue;
        }
    }
}
