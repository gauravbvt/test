/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.ModelRename;
import com.mindalliance.channels.core.command.commands.UpdateModelObject;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.dao.ModelDefinitionManager;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.model.Phase;
import com.mindalliance.channels.core.model.Place;
import com.mindalliance.channels.core.model.CollaborationModel;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.IssuesPanel;
import com.mindalliance.channels.pages.components.entities.EntityReferencePanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Plan edit details panel.
 */
public class ModelEditDetailsPanel extends AbstractCommandablePanel implements Guidable {

    /**
     * The plan definition manager.
     */
    @SpringBean
    private ModelDefinitionManager modelDefinitionManager;

    /**
     * Issues panel.
     */
    private IssuesPanel issuesPanel;
    private ModelObjectLink localeLink;

    public ModelEditDetailsPanel(
            String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {

        super( id, model, expansions );

        init();
    }

    private void init() {
        addUri();
        addName();
        addDescription();
        addIsViewableByAll();
        addLocale();
        addDefaultLanguage();
        addAttachments();
        addOrReplace( createIssuePanel() );
        adjustComponents();
    }

    @Override
    public String getHelpSectionId() {
        return "concepts";
    }

    @Override
    public String getHelpTopicId() {
        return "model";
    }

    private void addUri() {
        add( new Label( "uri", getCollaborationModel().getUri() ) );
    }

    private void addName() {
        TextField<String> planNameField = new TextField<String>( "name", new PropertyModel<String>( this, "name" ) );
        planNameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getCollaborationModel(), "name" ) );
            }
        } );
        planNameField.setEnabled( isLockedByUser( getCollaborationModel() ) );
        addInputHint( planNameField, "The name of the model" );
        add( planNameField );
    }

    private void addDescription() {
        TextArea<String> planDescriptionField = new TextArea<String>( "description", new PropertyModel<String>( this, "description" ) );
        planDescriptionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target,
                        new Change( Change.Type.Updated, getCollaborationModel(), "description" ) );
            }
        } );
        planDescriptionField.setEnabled( isLockedByUser( getCollaborationModel() ) );
        addInputHint( planDescriptionField, "A brief overview of the model" );
        add( planDescriptionField );
    }

    private void addIsViewableByAll() {
        final CollaborationModel collaborationModel = getCollaborationModel();
        add( new AjaxCheckBox(
                "viewableByAll",
                new PropertyModel<Boolean>( this, "viewableByAll" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, collaborationModel ) );
            }
        }.setEnabled( isLockedByUser( collaborationModel ) ) );
    }

    private void addLocale() {
        addLocaleLink();
        final List<Place> choices = getQueryService().listActualEntities( Place.class, true );
        AutoCompleteTextField<String> localeField = new AutoCompleteTextField<String>(
                "locale",
                new PropertyModel<String>( this, "localeName" ) ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                if ( choices != null ) {
                    for ( Place place : choices ) {
                        String choice = place.getName();
                        if ( getQueryService().likelyRelated( s, choice ) )
                            candidates.add( choice );
                    }
                    Collections.sort( candidates );
                }
                return candidates.iterator();
            }
        };
        localeField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addLocaleLink();
                target.add( localeLink );
                update( target, new Change( Change.Type.Updated, getCollaborationModel(), "locale" ));
            }
        });
        localeField.setEnabled( isLockedByUser( getCollaborationModel() ) );
        addInputHint( localeField, "Enter an actual place" );
        add( localeField );
    }

    public String getLocaleName() {
        Place place = getCollaborationModel().getLocale();
        return place == null ? "" : place.getName();
    }

    public void setLocaleName( String val ) {
        Place locale;
        if ( val == null || val.isEmpty() ) {
            locale = null;
        } else {
            locale = doSafeFindOrCreateActual( Place.class, val );
        }
        Place oldLocale = getCollaborationModel().getLocale();
        if ( !ModelObject.areEqualOrNull( oldLocale, locale ) ) {
            doCommand( new UpdateModelObject(
                    getUser().getUsername(),
                    getCollaborationModel(),
                    "locale",
                    locale ) );
            if ( oldLocale != null )
                getCommander().cleanup( Place.class, oldLocale.getName() );
        }
    }


    private void addLocaleLink() {
        localeLink = new ModelObjectLink( "locale-link",
                new PropertyModel<Organization>( getCollaborationModel(), "locale" ),
                new Model<String>( "Locale" ) );
        addOrReplace( localeLink );
    }

    private void addDefaultLanguage() {
        TextField<String> languageField = new TextField<String>( "defaultLanguage", new PropertyModel<String>( this, "defaultLanguage" ) );
        languageField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getCollaborationModel(), "defaultLanguage" ) );
            }
        } );
        languageField.setEnabled( isLockedByUser( getCollaborationModel() ) );
        addInputHint( languageField, "The default language spoken by agents" );
        add( languageField );
    }

    private void addAttachments() {
        add( new AttachmentPanel( "attachments", new Model<ModelObject>( getCollaborationModel() ) ) );
    }


    private EntityReferencePanel<Place> createScopePanel() {
        return new EntityReferencePanel<Place>(
                "localePanel",
                new Model<CollaborationModel>( getCollaborationModel() ), getQueryService().findAllEntityNames( Place.class ),
                "locale",
                Place.class
        );
    }

    private IssuesPanel createIssuePanel() {
        issuesPanel = new IssuesPanel( "issues",
                new PropertyModel<ModelObject>( this, "collaborationModel" ),
                getExpansions() );
        issuesPanel.setOutputMarkupId( true );

        return issuesPanel;
    }

    protected void adjustComponents() {
        makeVisible( issuesPanel, getCommunityService().getDoctor().hasIssues( getCommunityService(), getCollaborationModel(), false ) );
    }

    /**
     * Get the plan being edited.
     *
     * @return a plan
     */
    @Override
    public CollaborationModel getCollaborationModel() {
        return (CollaborationModel) getModel().getObject();
    }

    /**
     * Get the model object's name.
     *
     * @return a string
     */
    public String getName() {
        return getCollaborationModel().getName();
    }

    /**
     * Set the model object's unique new name.
     *
     * @param name a string
     */
    public void setName( String name ) {
        if ( name != null && !isSame( getName(), name ) )
            doCommand( new ModelRename( getUser().getUsername(),
                    getCollaborationModel(), modelDefinitionManager.makeUniqueName( name ) ) );
    }

    /**
     * Get the model object's default language.
     *
     * @return a string
     */
    public String getDefaultLanguage() {
        return getCollaborationModel().getDefaultLanguage();
    }

    /**
     * Set the model object's default language.
     *
     * @param defaultLanguage a string
     */
    public void setDefaultLanguage( String defaultLanguage ) {
        if ( defaultLanguage != null && !isSame( getDefaultLanguage(), defaultLanguage ) )
            doCommand(
                    new UpdateModelObject( getUser().getUsername(), getCollaborationModel(),
                            "defaultLanguage",
                            defaultLanguage,
                            UpdateObject.Action.Set ) );
    }

    /**
     * Get the model object's description.
     *
     * @return a string
     */
    public String getDescription() {
        return getCollaborationModel().getDescription();
    }

    /**
     * Set the model object's description.
     *
     * @param desc a string
     */
    public void setDescription( String desc ) {
        if ( desc != null )
            doCommand(
                    new UpdateModelObject( getUser().getUsername(), getCollaborationModel(),
                            "description",
                            desc,
                            UpdateObject.Action.Set ) );
    }

    public boolean isViewableByAll() {
        return getCollaborationModel().isViewableByAll();
    }

    public void setViewableByAll( boolean val ) {
        if ( val != isViewableByAll() ) {
            doCommand(
                    new UpdateModelObject( getUser().getUsername(), getCollaborationModel(),
                            "viewableByAll",
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
        List<Phase> phases = new ArrayList<Phase>( getCollaborationModel().getPhases() );
        Collections.sort( phases );
        return phases;
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isUpdated() ) {
            addOrReplace( createIssuePanel() );
            target.add( issuesPanel );
        }
        if ( change.isForProperty( "locale" ) ) {
            // Only unused phases can be removed, added ones at not yet referenced.
            addLocaleLink();
            target.add( localeLink );
        }
        super.updateWith( target, change, updated );
    }


}
