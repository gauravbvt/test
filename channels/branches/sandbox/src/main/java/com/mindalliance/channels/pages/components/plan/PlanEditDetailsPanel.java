package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Phase;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.model.Plan;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.IssuesPanel;
import com.mindalliance.channels.pages.components.entities.EntityReferencePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Collections;
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
     * Entity reference panel for scope of event (a place).
     */
    private EntityReferencePanel<Place> scopePanel;
    /**
     * Issues panel.
     */
    private IssuesPanel issuesPanel;

    public PlanEditDetailsPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        addIdentityFields();
        addPhaseListPanel();
        addScopePanel();
        addIssuesPanel();
        add( new AttachmentPanel( "attachments", new Model<ModelObject>( getPlan() ) ) );
        adjustComponents();
    }

    private void addPhaseListPanel() {
        PhaseListPanel phaseListPanel = new PhaseListPanel( "phases" );
        add( phaseListPanel );
    }


    private void addIdentityFields() {
        TextField<String> nameField = new TextField<String>( "name",
                new PropertyModel<String>( this, "plan.name" ) );
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPlan(), "name" ) );
            }
        } );
        nameField.setEnabled( getPlan().isDevelopment() );
        add( nameField );
        TextArea<String> descriptionField = new TextArea<String>( "description",
                new PropertyModel<String>( this, "description" ) );
        descriptionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getPlan(), "description" ) );
            }
        } );
        descriptionField.setEnabled( getPlan().isDevelopment() );
        add( descriptionField );
    }

    private void addScopePanel() {
        add(
                new ModelObjectLink( "locale-link",
                        new PropertyModel<Organization>( getPlan(), "locale" ),
                        new Model<String>( "Locale" ) ) );
        final List<String> choices = getQueryService().findAllEntityNames( Place.class );
        scopePanel = new EntityReferencePanel<Place>(
                "localePanel",
                new Model<Plan>( getPlan() ),
                choices,
                "locale",
                Place.class
        );
        add( scopePanel );
    }
    private void addIssuesPanel() {
        issuesPanel = new IssuesPanel(
                "issues",
                new PropertyModel<ModelObject>( this, "plan" ),
                getExpansions() );
        issuesPanel.setOutputMarkupId( true );
        addOrReplace( issuesPanel );
    }

    protected void adjustComponents() {
        makeVisible( issuesPanel, getAnalyst().hasIssues( getPlan(), false ) );
    }

    /**
     * Get the plan being edited.
     *
     * @return a plan
     */
    public Plan getPlan() {
        return (Plan) getModel().getObject();
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
                List<String> namesTaken = getPlanManager().getPlanNames();
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
     * Get a sorted list of plan phases.
     *
     * @return a list of phases
     */
    public List<Phase> getPhases() {
        List<Phase> phases = new ArrayList<Phase>( getPlan().getPhases() );
        Collections.sort( phases );
        return phases;
    }

    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isForProperty( "phases" ) ) {
            // Only unused phases can be removed, added ones at not yet referenced.
            addIssuesPanel();
            target.addComponent( issuesPanel );
        } else {
            super.updateWith( target, change, updated );
        }
    }


}
