package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Deletable;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.ScenarioObject;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.ScenarioPage;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.List;
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

    private IModel<ModelObject> model;

    public IssuesPanel( String id, IModel<ModelObject> model ) {
        super( id, model );
        this.model = model;
        init();
    }

    private void init() {
        add( new Label( "kind", new PropertyModel<String>( this, "kind" ) ) );

        final Link<String> newIssueLink = new Link<String>( "new-issue",                  // NON-NLS
                new Model<String>( "New" ) ) {
            @Override
            public void onClick() {
                ModelObject modelObject = model.getObject();
                UserIssue userIssue = new UserIssue( model.getObject() );
                userIssue.setReportedBy( Project.getUserName() );
                getService().add( userIssue );
                // TODO - Denis: Fix problem and remove patch
                PageParameters parameters;
                if ( modelObject instanceof ScenarioObject )
                    parameters = ( (ScenarioPage) getWebPage() )
                            .getParametersCollapsing( ((ScenarioObject)modelObject).getScenario().getId() );
                else
                    parameters = getWebPage().getPageParameters();
                parameters.add( ScenarioPage.EXPAND_PARM, Long.toString( userIssue.getId() ) );
                setResponsePage( getWebPage().getClass(), parameters );
            }
        };
        add( newIssueLink );

    }

    protected void onBeforeRender() {
        super.onBeforeRender();
        Set<Long> expansions = ( (ScenarioPage) getPage() ).findExpansions();
        createIssuePanels( expansions );
        setVisible( Project.analyst().hasIssues( model.getObject(), false ) );
    }


    /**
     * Return the kind of issue  -- scenario vs flow vs part (aka "task").
     *
     * @return a string
     */
    public String getKind() {
        ModelObject modelObject = model.getObject();
        if ( modelObject instanceof Scenario ) return "Scenario";
        else if ( modelObject instanceof Part ) return "Task";
        else if ( modelObject instanceof Flow ) return "Flow";
        else return modelObject.getClass().getSimpleName();
    }

    private Service getService() {
        return ( (Project) getApplication() ).getService();
    }

    private void createIssuePanels( final Set<Long> expansions ) {
        ListView issuesList = new ListView<Issue>( "issues", new PropertyModel<List<Issue>>(this, "modelObjectIssues")) {
            protected void populateItem( ListItem<Issue> listItem ) {
                Issue issue = listItem.getModelObject();
                final long id = issue.getId();
                final Panel issuePanel;
                if ( expansions.contains( id ) )
                    issuePanel = new ExpandedIssuePanel( "issue", new Model<Issue>(issue) );
                else
                    issuePanel = new CollapsedIssuePanel( "issue", new Model<Issue>(issue) );
                
                listItem.add( issuePanel );
            }
        };
        addOrReplace( issuesList );
    }

    /**
     * Find all issues of model object
     * @return list of issues
     */
    public List<Issue> getModelObjectIssues() {
        List<Issue> issues = Project.analyst().listIssues( model.getObject(), false );
        return issues;
    }


    //==================================================
    /**
     * A wrapper to keep track of the deletion state of an attachment.
     */
    public static class DeletableWrapper implements Deletable {

        /**
         * The underlying issue.
         */
        private Issue issue;

        /**
         * True if user marked item for deletion.
         */
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
