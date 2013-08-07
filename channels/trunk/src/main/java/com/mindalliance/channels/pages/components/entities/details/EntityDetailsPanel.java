/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.entities.details;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.engine.imaging.ImagingService;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.IssuesPanel;
import com.mindalliance.channels.pages.components.TagsPanel;
import com.mindalliance.channels.pages.components.entities.EntityReferencesAndMatchesPanel;
import com.mindalliance.channels.pages.components.entities.TypesPanel;
import com.mindalliance.channels.pages.components.plan.floating.PlanSearchingFloatingPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class EntityDetailsPanel extends AbstractCommandablePanel {

    /**
     * Maximum image height.
     */
    private static final int MAX_IMAGE_HEIGHT = 200;

    /**
     * Image tag.
     */
    WebMarkupContainer image;

    /**
     * Imaging service.
     */
    @SpringBean
    ImagingService imagingService;

    /**
     * Description field.
     */
    private TextArea<String> descriptionField;

    /**
     * Entity issues panel.
     */
    private IssuesPanel issuesPanel;

    /**
     * Container.
     */
    private WebMarkupContainer moDetailsDiv;

    /**
     * The entity being edited.
     */
    private IModel<? extends ModelEntity> model;

    /**
     * Name field.
     */
    private TextField<String> nameField;

    /**
     * Types panel.
     */
    private TypesPanel typesPanel;

    //-------------------------------
    public EntityDetailsPanel( String id, IModel<? extends ModelEntity> model, Set<Long> expansions ) {
        super( id, model, expansions );
        this.model = model;
        init();
    }

    private void init() {
        ModelEntity mo = getEntity();
        moDetailsDiv = new WebMarkupContainer( "mo-details" );
        moDetailsDiv.setOutputMarkupId( true );
        addOrReplace( moDetailsDiv );
        addImage();
        addNameField();
        addDescriptionField();
        addTypesPanel();
        addTagsPanel();
        moDetailsDiv.add( new AttachmentPanel( "attachments", new Model<ModelEntity>( mo ) ) );
        addEntityReferencesAndMatchesPanel();
        addSpecifics( moDetailsDiv );
        addIssuesPanel();
        adjustFields();
    }

    private void addImage() {
        image = new WebMarkupContainer( "image" );
        image.setOutputMarkupId( true );
        moDetailsDiv.addOrReplace( image );
    }

    private void addNameField() {
        final List<String> choices = getUniqueNameChoices( getEntity() );
        nameField = new AutoCompleteTextField<String>(
                "name",
                new PropertyModel<String>( this, "name" ),
                getAutoCompleteSettings() ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( getQueryService().likelyRelated( s, choice ) )
                        candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getEntity(), "name" ) );
            }
        } );
        addInputHint( nameField, "The name of this " + getEntity().getTypeName() );
        moDetailsDiv.add( nameField );
    }

    private void addDescriptionField() {
        moDetailsDiv.add(  new Label(
                "descriptionLabel",
                getEntity().isType() ? "Definition" : "Description"
                ) );
        descriptionField = new TextArea<String>( "description", new PropertyModel<String>( this, "description" ) );
        descriptionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getEntity(), "description" ) );
            }
        } );
        addInputHint( descriptionField, "A brief description of this " + getEntity().getTypeName() );
        moDetailsDiv.add( descriptionField );
    }

    private void addTypesPanel() {
        typesPanel = new TypesPanel( "types", new PropertyModel<ModelEntity>( this, "entity" ) );
        typesPanel.setOutputMarkupId( true );
        moDetailsDiv.addOrReplace( typesPanel );
    }

    private void addTagsPanel() {
        AjaxLink tagsLink = new AjaxLink( "tagsLink" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.AspectViewed, Channels.PLAN_SEARCHING, PlanSearchingFloatingPanel.TAGS) );
            }
        };
        tagsLink.add( new AttributeModifier( "class", new Model<String>( "model-object-link" ) ) );
        moDetailsDiv.add( tagsLink );
        TagsPanel tagsPanel = new TagsPanel( "tags", new Model<ModelEntity>( getEntity() ) );
        moDetailsDiv.add( tagsPanel );
    }

    private void addEntityReferencesAndMatchesPanel() {
        ModelEntity entity = getEntity();
        Label indexTitleLabel =
                new Label( "indexTitle", new Model<String>( entity.isActual() ? "References to" : "Index of type" ) );
        moDetailsDiv.add( indexTitleLabel );
        Label referencedLabel = new Label( "indexedName", new Model<String>( "\"" + entity.getName() + "\"" ) );
        moDetailsDiv.add( referencedLabel );
        EntityReferencesAndMatchesPanel refsPanel = new EntityReferencesAndMatchesPanel( "referencesOrMatches",
                                                                                         new PropertyModel<ModelEntity>(
                                                                                                 this,
                                                                                                 "entity" ),
                                                                                         getExpansions() );
        moDetailsDiv.add( refsPanel );
    }

    private void addIssuesPanel() {
        issuesPanel = new IssuesPanel( "issues", new PropertyModel<ModelEntity>( this, "entity" ), getExpansions() );
        issuesPanel.setOutputMarkupId( true );
        moDetailsDiv.addOrReplace( issuesPanel );
    }

    private void adjustFields() {
        if ( getEntity().hasImage() ) {
            String url = getEntity().getImageUrl();
            image.add( new AttributeModifier( "src", new Model<String>( url ) ) );
            int[] size = imagingService.getImageSize( getCommunityService(), url );
            int height = size[1];
            if ( height > MAX_IMAGE_HEIGHT ) {
                image.add( new AttributeModifier( "height", new Model<String>( "" + MAX_IMAGE_HEIGHT ) ) );
            }
        }
        makeVisible( image, getEntity().hasImage() );
        nameField.setEnabled( isLockedByUser( getEntity() ) );
        descriptionField.setEnabled( isLockedByUser( getEntity() ) );
        makeVisible( issuesPanel, getAnalyst().hasIssues( getQueryService(), getEntity(), false ) );
    }

    //-------------------------------
    /**
     * Add class-specific input fields.
     *
     * @param moDetailsDiv the web markup container to add them to
     */
    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        // do nothing
    }

    /**
     * Get the model object's description.
     *
     * @return a string
     */
    public String getDescription() {
        return getEntity().getDescription();
    }

    /**
     * Get model object.
     *
     * @return an entity
     */
    public ModelEntity getEntity() {
        return model.getObject();
    }

    /**
     * Get the model object's name.
     *
     * @return a string
     */
    public String getName() {
        return getEntity().getName();
    }

    /**
     * Set the model object's description.
     *
     * @param val a string
     */
    public void setDescription( String val ) {
        String desc = val == null ? "" : val;
        doCommand( new UpdatePlanObject( getUser().getUsername(),
                                         getEntity(),
                                         "description",
                                         desc,
                                         UpdateObject.Action.Set ) );
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
                List<String> namesTaken = getQueryService().findAllEntityNames( getEntity().getClass() );
                int count = 2;
                while ( namesTaken.contains( uniqueName ) ) {
                    uniqueName = name + "(" + count++ + ")";
                }
                doCommand( new UpdatePlanObject( getUser().getUsername(),
                                                 getEntity(),
                                                 "name",
                                                 uniqueName,
                                                 UpdateObject.Action.Set ) );
            }
        }
    }

    /**
     * React to change in types.
     *
     * @param target an ajax request target
     */
    protected void typesChanged( AjaxRequestTarget target ) {
        // do nothing
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isUpdated() && change.isForProperty( "types" ) ) {
            addTypesPanel();
            target.add( typesPanel );
            typesChanged( target );
        }
        if ( change.isUpdated() && change.isForProperty( "attachments" ) ) {
            addImage();
            adjustFields();
            target.add( image );
        }
        if ( change.isUpdated() ) {
            addIssuesPanel();
            adjustFields();
            target.add( issuesPanel );
        }
        super.updateWith( target, change, updated );
    }
}
