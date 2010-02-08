package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.ImagingService;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.IssuesPanel;
import com.mindalliance.channels.util.Matcher;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
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

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 12, 2009
 * Time: 7:05:45 PM
 */
public class EntityDetailsPanel extends AbstractCommandablePanel {

    @SpringBean
    /**
     * Imaging service.
     */
            ImagingService imagingService;

    /**
     * The entity being edited
     */
    private IModel<? extends ModelEntity> model;
    /**
     * Container.
     */
    private WebMarkupContainer moDetailsDiv;
    /**
     * Image tag.
     */
    WebMarkupContainer image;
    /**
     * Name field.
     */
    private TextField<String> nameField;
    /**
     * Description field.
     */
    private TextArea<String> descriptionField;
    /**
     * Tags panel.
     */
    private TagsPanel tagsPanel;
    /**
     * Entity issues panel.
     */
    private IssuesPanel issuesPanel;

    /**
     * Maximum image height.
     */
    private static final int MAX_IMAGE_HEIGHT = 200;

    public EntityDetailsPanel( String id, IModel<? extends ModelEntity> model, Set<Long> expansions ) {
        super( id, model, expansions );
        this.model = model;
        init();
    }

    private void init() {
        ModelEntity mo = getEntity();
        moDetailsDiv = new WebMarkupContainer( "mo-details" );
        add( moDetailsDiv );
        addImage();
        addNameField();
        addDescriptionField();
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
    }

    private void addDescriptionField() {
        descriptionField = new TextArea<String>( "description",
                new PropertyModel<String>( this, "description" ) );
        descriptionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getEntity(), "description" ) );
            }
        } );
        moDetailsDiv.add( descriptionField );
    }

    private void addIssuesPanel() {
        issuesPanel = new IssuesPanel(
                "issues",
                new PropertyModel<ModelEntity>( this, "entity" ),
                getExpansions() );
        issuesPanel.setOutputMarkupId( true );
        moDetailsDiv.addOrReplace( issuesPanel );
    }

    private void addTagsPanel() {
        tagsPanel = new TagsPanel(
                "tags",
                new PropertyModel<ModelEntity>( this, "entity" ) );
        tagsPanel.setOutputMarkupId( true );
        moDetailsDiv.addOrReplace( tagsPanel );
    }

    private void addEntityReferencesAndMatchesPanel() {
        ModelEntity entity = getEntity();
        Label indexTitleLabel = new Label(
                "indexTitle",
                new Model<String>(
                        entity.isActual()
                                ? "References to"
                                : "Index of type" ) );
        moDetailsDiv.add( indexTitleLabel );
        Label referencedLabel = new Label(
                "indexedName",
                new Model<String>(
                        "\"" + entity.getName() + "\"" ) );
        moDetailsDiv.add( referencedLabel );
        EntityReferencesAndMatchesPanel refsPanel = new EntityReferencesAndMatchesPanel(
                "referencesOrMatches",
                new PropertyModel<ModelEntity>( this, "entity" ),
                getExpansions() );
        moDetailsDiv.add( refsPanel );
    }


    private void adjustFields() {
        if ( getEntity().hasImage() ) {
            String url = getEntity().getImageUrl();
            image.add( new AttributeModifier(
                    "src",
                    true,
                    new Model<String>( url ) ) );
            int[] size = imagingService.getImageSize( url );
            int height = size[1];
            if ( height > MAX_IMAGE_HEIGHT ) {
                image.add( new AttributeModifier(
                        "height",
                        true,
                        new Model<String>( "" + MAX_IMAGE_HEIGHT )
                ) );
            }
        }
        makeVisible( image, getEntity().hasImage() );
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
                List<String> namesTaken = getQueryService().findAllEntityNames( getEntity().getClass() );
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
     * @return an entity
     */
    public ModelEntity getEntity() {
        return model.getObject();
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isUpdated() && change.getProperty().equals( "tags" ) ) {
            addTagsPanel();
            target.addComponent( tagsPanel );
            tagsChanged( target );
        }
        if ( change.isUpdated() && change.getProperty().equals( "attachments" ) ) {
            addImage();
            adjustFields();
            target.addComponent( image );
        }
        if ( change.isUpdated() ) {
            addIssuesPanel();
            adjustFields();
            target.addComponent( issuesPanel );
        }
        super.updateWith( target, change, updated );
    }

    /**
     * React to change in tags.
     *
     * @param target an ajax request target
     */
    protected void tagsChanged( AjaxRequestTarget target ) {
        // do nothing
    }
}
