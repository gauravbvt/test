package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.IssuesPanel;
import com.mindalliance.channels.util.Matcher;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 12, 2009
 * Time: 7:05:45 PM
 */
public class EntityDetailsPanel extends AbstractCommandablePanel {
    /**
     * The model object being edited
     */
    private IModel<? extends ModelObject> model;
    /**
     * Name field.
     */
    private TextField<String> nameField;
    /**
     * Description field.
     */
    private TextArea<String> descriptionField;
    /**
     * Entity issues panel.
     */
    private IssuesPanel issuesPanel;

    public EntityDetailsPanel( String id, IModel<? extends ModelObject> model, Set<Long> expansions ) {
        super( id, model, expansions );
        this.model = model;
        init();
    }

    private void init() {
        ModelObject mo = getEntity();
        WebMarkupContainer moDetailsDiv = new WebMarkupContainer( "mo-details" );
        add( moDetailsDiv );
        final List<String> choices = getUniqueNameChoices( getEntity() );
        nameField = new AutoCompleteTextField<String>( "name",
                new PropertyModel<String>( this, "name" ) ) {
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( Matcher.matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getEntity(), "name" ) );
            }
        } );
        moDetailsDiv.add( nameField );
        descriptionField = new TextArea<String>( "description",
                new PropertyModel<String>( this, "description" ) );
        descriptionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getEntity(), "description" ) );
            }
        } );
        moDetailsDiv.add( descriptionField );
        moDetailsDiv.add( new AttachmentPanel( "attachments", new Model<ModelObject>( mo ) ) );
        addSpecifics( moDetailsDiv );
        issuesPanel = new IssuesPanel(
                "issues",
                new PropertyModel<ModelObject>( this, "entity" ),
                getExpansions() );
        issuesPanel.setOutputMarkupId( true );
        moDetailsDiv.add( issuesPanel );
        adjustFields();
    }

    private void adjustFields() {
        nameField.setEnabled( isLockedByUser( getEntity() ) );
        descriptionField.setEnabled( isLockedByUser( getEntity() ) );
        makeVisible( issuesPanel, getAnalyst().hasIssues( getEntity(), false ) );
    }

    /**
     * Add class-specific input fields.
     *
     * @param moDetailsDiv the web markup container to add them to
     */
    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        // do nothing
    }

    /**
     * Get the model object's name
     *
     * @return a string
     */
    public String getName() {
        return getEntity().getName();
    }

    /**
     * Set the model object's unique new name.
     *
     * @param name a string
     */
    public void setName( String name ) {
        if ( name != null ) {
            String oldName = getEntity().getName();
            String uniqueName = name.trim();
            if ( !isSame( oldName, name ) ) {
                List<String> namesTaken = getQueryService().findAllNames( getEntity().getClass() );
                int count = 2;
                while ( namesTaken.contains( uniqueName ) ) {
                    uniqueName = name + "(" + count++ + ")";
                }
                doCommand(
                        new UpdatePlanObject(
                                getEntity(),
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
        return getEntity().getDescription();
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
                            getEntity(),
                            "description",
                            desc,
                            UpdateObject.Action.Set ) );
    }

    /**
     * Get model object.
     *
     * @return a model object
     */
    public ModelObject getEntity() {
        return model.getObject();
    }

}
