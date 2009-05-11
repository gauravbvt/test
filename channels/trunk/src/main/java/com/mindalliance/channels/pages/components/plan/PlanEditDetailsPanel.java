package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Issue;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.IssuesPanel;
import com.mindalliance.channels.util.SemMatch;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Plan edit details panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 7, 2009
 * Time: 5:31:25 AM
 */
public class PlanEditDetailsPanel extends AbstractCommandablePanel {

    /**
     * Issues panel.
     */
    private IssuesPanel issuesPanel;

    public PlanEditDetailsPanel( String id, IModel<? extends Identifiable>model,  Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        final List<String> choices = getUniqueNameChoices( getPlan() );
        TextField<String> nameField = new AutoCompleteTextField<String>( "name",
                new PropertyModel<String>( this, "plan.name" ) ) {
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( SemMatch.matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPlan(), "name" ) );
            }
        } );
        add( nameField );
        TextArea<String> descriptionField = new TextArea<String>( "description",
                new PropertyModel<String>( this, "description" ) );
        descriptionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPlan(), "description" ) );
            }
        } );
        add( descriptionField );
        issuesPanel = new IssuesPanel(
                "issues",
                new PropertyModel<ModelObject>( this, "plan" ),
                getExpansions() );
        issuesPanel.setOutputMarkupId( true );
        add( issuesPanel );
        add( new AttachmentPanel( "attachments", new Model<ModelObject>( getPlan() ) ) );
        adjustComponents();
    }

    protected void adjustComponents() {
        makeVisible( issuesPanel, getAnalyst().hasIssues( getPlan(), false ) );
    }

    /**
     * Get the plan being edited.
     * @return a plan
     */
    public Plan getPlan() {
        return (Plan)getModel().getObject();
    }

    /**
     * Get the model object's name
     *
     * @return a string
     */
    public String getName() {
        return getPlan().getName();
    }

    /**
     * Set the model object's unique new name.
     *
     * @param name a string
     */
    public void setName( String name ) {
        if ( name != null ) {
            String oldName = getPlan().getName();
            String uniqueName = name.trim();
            if ( !isSame( oldName, name ) ) {
                List<String> namesTaken = getQueryService().findAllNames( Plan.class );
                int count = 2;
                while ( namesTaken.contains( uniqueName ) ) {
                    uniqueName = name + "(" + count++ + ")";
                }
                doCommand(
                        new UpdatePlanObject(
                                getPlan(),
                                "name",
                                uniqueName,
                                UpdateObject.Action.Set
                        )
                );
            }
        }
    }

    /**
     * Get the model object's description
     *
     * @return a string
     */
    public String getDescription() {
        return getPlan().getDescription();
    }

    /**
     * Set the model object's description.
     *
     * @param desc a string
     */
    public void setDescription( String desc ) {
        if ( desc != null )
            doCommand(
                    new UpdatePlanObject(
                            getPlan(),
                            "description",
                            desc,
                            UpdateObject.Action.Set ) );
    }


    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        if ( change.isUpdated() || change.getSubject() instanceof Issue ) {
            adjustComponents();
            target.addComponent( this );
        }
        super.updateWith( target, change );
    }

}
