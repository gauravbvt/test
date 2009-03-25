package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Flow;
import com.mindalliance.channels.Issue;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.AddUserIssue;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.List;
import java.util.Set;

/**
 * An issues panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 8, 2009
 * Time: 10:34:20 AM
 */
public class IssuesPanel extends AbstractCommandablePanel {

    /**
     * Maximum length of string displayed
     */
    public static final int MAX_LENGTH = 80;
    /**
     * A model on a model object which issues are shown, if any.
     */
    private IModel<ModelObject> model;
    /**
     * Expansions.
     */
    private Set<Long> expansions;
    /**
     * Issues container.
     */
    private WebMarkupContainer issuesContainer;

    public IssuesPanel( String id, IModel<ModelObject> model, Set<Long> expansions ) {
        super( id, model );
        this.model = model;
        this.expansions = expansions;
        init();
    }

    private void init() {
        add( new Label( "kind", new PropertyModel<String>( this, "kind" ) ) );

        AjaxFallbackLink newIssueLink = new AjaxFallbackLink( "new-issue" ) {
            public void onClick( AjaxRequestTarget target ) {
                Change change = doCommand( new AddUserIssue( model.getObject() ) );
/*
                makeVisible( this, Project.analyst().hasIssues( model.getObject(), false ) );
                target.addComponent( this );
*/
                update( target, change );
            }
        };
        add( newIssueLink );
        createIssuePanels();
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

    private void createIssuePanels() {
        issuesContainer = new WebMarkupContainer( "issues-container" );
        issuesContainer.setOutputMarkupId( true );
        add( issuesContainer );
        issuesContainer.add( new ListView<Issue>(
                "issues",
                new PropertyModel<List<Issue>>( this, "modelObjectIssues" ) ) {
            protected void populateItem( ListItem<Issue> listItem ) {
                Issue issue = listItem.getModelObject();
                final long id = issue.getId();
                final Panel issuePanel;
                if ( expansions.contains( id ) ) {
                    issuePanel = new ExpandedIssuePanel( "issue", new Model<Issue>( issue ) );
                } else {
                    issuePanel = new CollapsedIssuePanel( "issue", new Model<Issue>( issue ) );
                }

                listItem.add( issuePanel );
            }
        } );
    }

    /**
     * Find all issues of model object
     *
     * @return list of issues
     */
    public List<Issue> getModelObjectIssues() {
        return Project.analyst().listIssues( model.getObject(), false );
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        target.addComponent( issuesContainer );
        super.updateWith( target, change );
    }

}
