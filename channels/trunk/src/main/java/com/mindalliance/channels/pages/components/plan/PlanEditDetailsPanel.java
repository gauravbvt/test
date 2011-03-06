package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.PlanRename;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.dao.DefinitionManager;
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
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

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

    /** The plan definition manager. */
    @SpringBean
    private DefinitionManager definitionManager;

    /**
     * Issues panel.
     */
    private IssuesPanel issuesPanel;

    public PlanEditDetailsPanel(
            String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {

        super( id, model, expansions );

        init( getPlan() );
    }

    private void init( final Plan plan ) {
        add(    new Label( "uri", plan.getUri() ),
                new TextField<String>( "name", new PropertyModel<String>( this, "name" ) )
                .add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            update( target, new Change( Change.Type.Updated, getPlan(), "name" ) );
                        }
                    } )
                .setEnabled( isLockedByUser( plan ) ),

             new TextArea<String>( "description", new PropertyModel<String>( this, "description" ) )
                .add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                        @Override
                        protected void onUpdate( AjaxRequestTarget target ) {
                            update( target,
                                    new Change( Change.Type.Updated, getPlan(), "description" ) );
                        }
                    } )
                .setEnabled( isLockedByUser( plan ) ),
             new AjaxCheckBox(
                     "template",
                     new PropertyModel<Boolean>( this, "template") ) {
                 @Override
                 protected void onUpdate( AjaxRequestTarget target ) {
                     update( target, new Change( Change.Type.Updated, plan) );
                 }
             }.setEnabled( isLockedByUser( plan ) ),
             new PhaseListPanel( "phases" ),

             new ModelObjectLink( "locale-link",
                        new PropertyModel<Organization>( plan, "locale" ),
                        new Model<String>( "Locale" ) ),

             createScopePanel().setEnabled( isLockedByUser( plan ) ),

             new AttachmentPanel( "attachments", new Model<ModelObject>( plan ) )
        );

        addOrReplace( createIssuePanel() );
        
        adjustComponents();
    }


    private EntityReferencePanel<Place> createScopePanel() {
        return new EntityReferencePanel<Place>(
                "localePanel",
                new Model<Plan>( getPlan() ), getQueryService().findAllEntityNames( Place.class ),
                "locale",
                Place.class
        );
    }

    private IssuesPanel createIssuePanel() {
        issuesPanel = new IssuesPanel( "issues",
                                       new PropertyModel<ModelObject>( this, "plan" ),
                                       getExpansions() );
        issuesPanel.setOutputMarkupId( true );

        return issuesPanel;
    }

    protected void adjustComponents() {
        makeVisible( issuesPanel, getAnalyst().hasIssues( getPlan(), false ) );
    }

    /**
     * Get the plan being edited.
     *
     * @return a plan
     */
    @Override
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
        if ( name != null && !isSame( getName(), name ) )
            doCommand( new PlanRename( getPlan(), definitionManager.makeUniqueName( name ) ) );
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

    public boolean isTemplate() {
        return getPlan().isTemplate();
    }

    public void setTemplate( boolean val ) {
        if ( val != isTemplate() ) {
            doCommand(
                    new UpdatePlanObject(
                            getPlan(),
                            "template",
                            val,
                            UpdateObject.Action.Set )
                    );
        }
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

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isForProperty( "phases" ) ) {
            // Only unused phases can be removed, added ones at not yet referenced.
            addOrReplace( createIssuePanel() );
            target.addComponent( issuesPanel );
        } else {
            super.updateWith( target, change, updated );
        }
    }


}
