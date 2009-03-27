package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.Identifiable;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.ResourceSpec;
import com.mindalliance.channels.Actor;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.IssuesPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 25, 2009
 * Time: 5:39:31 PM
 */
public class EntityIssuesPanel extends AbstractTablePanel {

    /**
     * Container.
     */
    private WebMarkupContainer includeContainer;
    /**
     * Whether to include members.
     */
    private boolean membersIncluded = false;
    /**
     * Issues on entity per se.
     */
    private IssuesPanel entityIssues;
    /**
     *  Issues table.
     */
    private IssuesTablePanel issuesTable;

    public EntityIssuesPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init( expansions );
    }

    private void init( final Set<Long> expansions ) {
        includeContainer = new WebMarkupContainer("include");
         includeContainer.setVisible( !(getEntity() instanceof Actor ) );
         add(includeContainer);
        CheckBox specificCheckBox = new CheckBox(
                "members-included",
                new PropertyModel<Boolean>( this, "membersIncluded" ) );
        specificCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addIssuesTable( expansions );
                target.addComponent( issuesTable );
            }
        } );
        includeContainer.add( specificCheckBox );
        Label entityLabel = new Label( "entity", new PropertyModel( this, "entity.label" ) );
        includeContainer.add( entityLabel );
        addIssuesTable( expansions );
        entityIssues = new IssuesPanel(
                "issues",
                new Model<ModelObject>( getEntity() ),
                getExpansions() );
        entityIssues.setOutputMarkupId( true );
        add( entityIssues );
        makeVisible( entityIssues, Project.analyst().hasIssues( getEntity(), false ) );
    }

    private void addIssuesTable( Set<Long> expansions ) {
        issuesTable = new IssuesTablePanel(
                "issues-table",
                new PropertyModel<ResourceSpec>( this, "resourceSpec" ),
                new PropertyModel<Boolean>( this, "membersExcluded" ),
                20,
                expansions );
        issuesTable.setOutputMarkupId( true );
        addOrReplace( issuesTable );
    }

    public boolean isMembersExcluded() {
        return !membersIncluded;
    }

    public boolean isMembersIncluded() {
        return membersIncluded;
    }


    public void setMembersIncluded( boolean val ) {
        membersIncluded = val;
    }

    public ModelObject getEntity() {
        return (ModelObject) getModel().getObject();
    }

    public ResourceSpec getResourceSpec() {
        return ResourceSpec.with( getEntity() );
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        makeVisible( target, entityIssues, Project.analyst().hasIssues( getEntity(), false ) );
        super.updateWith( target, change );
    }


}
