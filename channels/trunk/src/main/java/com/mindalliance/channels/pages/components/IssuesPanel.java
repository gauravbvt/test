package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.pages.Submitter;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.Submitable;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 8, 2009
 * Time: 10:34:20 AM
 */
public class IssuesPanel extends Panel implements Submitable {

    /**
     * Maximum length of string displayed
     */
    public static int MAX_LENGTH = 80;

    /**
     * The model object possibly with issues.
     */
    private ModelObject modelObject;

    /**
     * Panels of expanded user issues.
     */
    private List<DeletableIssue> deletableIssues;

    public IssuesPanel( String id, IModel<ModelObject> model, PageParameters parameters ) {
        super( id, model );
        modelObject = model.getObject();
        init( parameters );
    }

    private void init( PageParameters parameters ) {
        final Set<Long> expansions = Project.findExpansions( parameters );
        Link<String> newIssueLink = new Link<String>( "new-issue", new Model<String>( "New" ) ) {
            public void onClick() {
                UserIssue userIssue = new UserIssue( modelObject );
                userIssue.setReportedBy( Project.getUserName() );
                Project.dao().addUserIssue( userIssue );
                final Set<Long> newExpansions = new HashSet<Long>( expansions );
                newExpansions.add( userIssue.getId() );
                PageParameters params = getWebPage().getPageParameters();
                params.add( Project.EXPAND_PARM, Long.toString( userIssue.getId() ) );
                setResponsePage( getWebPage().getClass(), params );
            }
        };
        add( newIssueLink );
        add( createIssuePanels( expansions ) );
        setVisible( Project.analyst().hasIssues( modelObject, false ) );
    }

    public void onAfterRender() {
        super.onAfterRender();
        this.visitParents( WebPage.class, new IVisitor<Component>() {
            // Register this panel to participate in onSubmit events with expansions
/*        if (getWebPage() instanceof Expandable ) {
            ((Expandable)getWebPage()).register( this );
        }*/
            public Object component( Component component ) {
                if (component instanceof Submitter ) {
                    ((Submitter)component).register( IssuesPanel.this );
                    return component;
                }
                else {
                    return null;
                }
            }
        } );
    }


    private RepeatingView createIssuePanels( Set<Long> expansions ) {
        final RepeatingView issuesList = new RepeatingView( "issues" );
        Iterator<Issue> issues = Project.analyst().findIssues( modelObject, false );
        deletableIssues = new ArrayList<DeletableIssue>();
        while ( issues.hasNext() ) {
            final Issue issue = issues.next();
            final Panel issuePanel;
            long id = issue.getId();
            if ( expansions.contains( id ) ) {
                ExpandedIssuePanel panel = new ExpandedIssuePanel( Long.toString(id), issue );
                if (!issue.isDetected()) deletableIssues.add(panel);
                issuePanel = panel;
            } else {
                CollapsedIssuePanel panel = new CollapsedIssuePanel( Long.toString(id), issue );
                if (!issue.isDetected()) deletableIssues.add(panel);
                issuePanel = panel;
            }
            issuesList.add( issuePanel );
        }
        return issuesList;
    }

    /**
     * Delete issues that are marked for deletion.
     * @param expansions the component expansion list to modify on deletions
     */
    public void deleteSelectedIssues( Set<Long> expansions ) {
        for ( DeletableIssue panel : deletableIssues) {
            if ( panel.isMarkedForDeletion() ) {
                expansions.remove( panel.getIssue().getId() );
                Project.dao().removeUserIssue( (UserIssue)panel.getIssue() );
            }
        }
    }

    /**
     * React to submit event
     */
    public void onSubmit( Set<Long> expansions ) {
        deleteSelectedIssues( expansions );
    }
}
