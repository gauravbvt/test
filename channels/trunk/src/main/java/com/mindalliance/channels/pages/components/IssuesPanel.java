/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.AddUserIssue;
import com.mindalliance.channels.core.dao.User;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.pages.Updatable;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * An issues panel.
 */
public class IssuesPanel extends AbstractCommandablePanel {

    /**
     * Maximum length of string displayed.
     */
    public static final int MAX_LENGTH = 80;

    /**
     * A model on a model object which issues are shown, if any.
     */
    private final IModel<? extends ModelObject> model;

    /**
     * Issues container.
     */
    private WebMarkupContainer issuesContainer;

    public IssuesPanel( String id, IModel<? extends ModelObject> model, Set<Long> expansions ) {
        super( id, model, expansions );
        this.model = model;
        init();
    }

    private void init() {
        add( new Label( "kind", new PropertyModel<String>( this, "kind" ) ) );

        AjaxFallbackLink<?> newIssueLink = new AjaxFallbackLink( "new-issue" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                Change change = doCommand( new AddUserIssue( User.current().getUsername(), model.getObject() ) );
                /*
                                makeVisible( this, Channels.analyst().hasIssues( model.getObject(), false ) );
                                target.addComponent( this );
                */
                update( target, change );
            }
        };
        newIssueLink.setVisible( getPlan().isDevelopment() );
        add( newIssueLink );
        createIssuePanels();
    }

    /**
     * Return the kind of issue  -- segment vs flow vs part (aka "task").
     *
     * @return a string
     */
    public String getKind() {
        ModelObject modelObject = model.getObject();
        return StringUtils.capitalize( modelObject.getKindLabel() );
    }

    private void createIssuePanels() {
        issuesContainer = new WebMarkupContainer( "issues-container" );
        issuesContainer.setOutputMarkupId( true );
        add( issuesContainer );
        issuesContainer.add( new ListView<Issue>( "issues",
                                                  new PropertyModel<List<Issue>>( this, "modelObjectIssues" ) ) {
            @Override
            protected void populateItem( ListItem<Issue> item ) {
                Issue issue = item.getModelObject();
                item.add( getExpansions().contains( issue.getId() ) ?
                          new ExpandedIssuePanel( "issue", new Model<Issue>( issue ) ) :
                          new CollapsedIssuePanel( "issue", new Model<Issue>( issue ) ) );
            }
        } );
    }

    /**
     * Find all issues of model object.
     *
     * @return list of issues
     */
    public List<Issue> getModelObjectIssues() {
        List<Issue> issues =
                new ArrayList<Issue>( getAnalyst().listIssues( getQueryService(), model.getObject(), false ) );
        Collections.sort( issues, new Comparator<Issue>() {
            public int compare( Issue issue, Issue other ) {
                return other.getSeverity().compareTo( issue.getSeverity() );
            }
        } );
        return issues;
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        target.addComponent( issuesContainer );
        super.updateWith( target, change, updated );
    }
}
