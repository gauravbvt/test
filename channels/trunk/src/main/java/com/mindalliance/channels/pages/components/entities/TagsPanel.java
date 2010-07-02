package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Event;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import com.mindalliance.channels.nlp.Matcher;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Tags panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 16, 2009
 * Time: 7:27:02 AM
 */
public class TagsPanel extends AbstractCommandablePanel {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( TagsPanel.class );
    /**
     * Collator.
     */
    private static Collator collator = Collator.getInstance();

    private IModel<ModelEntity> entityModel;


    public TagsPanel( String id, IModel<ModelEntity> iModel ) {
        super( id );
        entityModel = iModel;
        init();
    }

    private void init() {
        WebMarkupContainer tagsDiv = new WebMarkupContainer( "tagsDiv" );
        tagsDiv.setOutputMarkupId( true );
        add( tagsDiv );
        tagsDiv.add( makeTagsTable() );
    }

    private ListView<TagWrapper> makeTagsTable() {
        return new ListView<TagWrapper>( "tags", getWrappedTags() ) {
            /** {@inheritDoc} */
            protected void populateItem( ListItem<TagWrapper> item ) {
                addTagCell( item );
                addDeleteCell( item );
            }
        };
    }

    @SuppressWarnings( "unchecked" )
    private void addTagCell( final ListItem<TagWrapper> item ) {
        final TagWrapper wrapper = item.getModelObject();
        item.setOutputMarkupId( true );
        WebMarkupContainer nameContainer = new WebMarkupContainer( "tagContainer" );
        item.add( nameContainer );
        final List<String> choices;
        if ( wrapper.isMarkedForCreation() ) {
            choices = (List<String>) CollectionUtils.select(
                    getQueryService().findAllEntityNames( getEntity().getClass(), ModelEntity.Kind.Type ),
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            ModelEntity type = getQueryService().findOrCreateType(
                                    getEntity().getClass(),
                                    ( (String) obj ) );
                            return !type.equals( getEntity() ) &&
                                    !getEntity().hasTag( type );
                        }
                    }
            );
        } else {
            choices = null;
        }
        // text field
        TextField<String> nameField = new AutoCompleteTextField<String>(
                "newTag",
                new PropertyModel<String>( wrapper, "tagName" ) ) {
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( Matcher.getInstance().matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();

            }
        };
        nameField.setVisible( wrapper.isMarkedForCreation() && isLockedByUser( getEntity() ) );
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                target.addComponent( item );
                update( target, new Change( Change.Type.Updated, getEntity(), "tags" ) );
            }
        } );
        nameContainer.add( nameField );
        // Link to entity type
        EntityLink entityLink = new EntityLink( "tagLink", new PropertyModel<Event>( wrapper, "tag" ) );
        entityLink.setVisible( !wrapper.isMarkedForCreation() && !wrapper.isUniversal() );
        if ( wrapper.isInherited() || wrapper.isImmutable() )
            entityLink.add(
                    new AttributeModifier( "style", true, new Model<String>( "font-style:oblique" ) ) );
        entityLink.add( new AttributeModifier(
                "title",
                true,
                new Model<String>( wrapper.getTitle() ) ) );
        nameContainer.add( entityLink );
        // Universal type
        Label universalLabel = new Label( "universal", new Model<String>( wrapper.getTagName() ) );
        universalLabel.setVisible( wrapper.isUniversal() );
        universalLabel.add(
                new AttributeModifier( "style", true, new Model<String>( "font-style:oblique" ) ) );
        universalLabel.add( new AttributeModifier(
                "title",
                true,
                new Model<String>( wrapper.getTitle() ) ) );
        nameContainer.add( universalLabel );
    }

    private void addDeleteCell( ListItem<TagWrapper> item ) {
        final TagWrapper wrapper = item.getModelObject();
        ConfirmedAjaxFallbackLink deleteLink = new ConfirmedAjaxFallbackLink(
                "delete",
                "Delete tag?" ) {
            public void onClick( AjaxRequestTarget target ) {
                wrapper.removeTag();
                update( target,
                        new Change(
                                Change.Type.Updated,
                                getEntity(),
                                "tags"
                        ) );
            }
        };
        makeVisible( deleteLink, isLockedByUser( getEntity() ) && wrapper.isRemovable() );
        item.addOrReplace( deleteLink );
    }

    private List<TagWrapper> getWrappedTags() {
        List<TagWrapper> wrappers = new ArrayList<TagWrapper>();
        wrappers.add( new TagWrapper( ModelEntity.getUniversalTypeFor( getEntity().getClass() ) ) );
        List<TagWrapper> current = new ArrayList<TagWrapper>();
        for ( ModelEntity tag : getEntity().getAllTags() ) {
            current.add( new TagWrapper( tag ) );
        }
        // Sort
        Collections.sort( current, new Comparator<TagWrapper>() {
            public int compare( TagWrapper tw1, TagWrapper tw2 ) {
                return collator.compare( tw1.getTagName(), tw2.getTagName() );
            }
        } );
        wrappers.addAll( current );
        if ( isLockedByUser( getEntity() ) ) {
            // to-be-created tag
            wrappers.add( new TagWrapper() );
        }
        return wrappers;
    }


    private ModelEntity getEntity() {
        return entityModel.getObject();
    }

    /**
     * A wrapped entity type.
     */
    public class TagWrapper implements Serializable {

        private ModelEntity tag;


        private TagWrapper( ModelEntity tag ) {
            assert tag.isType();
            assert tag.getDomain().equals( getEntity().getDomain() );
            this.tag = tag;
        }

        private TagWrapper() {

        }

        public ModelEntity getTag() {
            return tag;
        }

        public boolean isMarkedForCreation() {
            return tag == null;
        }

        public boolean isImmutable() {
            return tag != null && tag.isImmutable();
        }

        public boolean isInherited() {
            return tag != null
                    && !getEntity().getTags().contains( tag )
                    && !isImplicit()
                    && getEntity().hasTag( tag );
        }

        public boolean isImplicit() {
            return getEntity().getAllImplicitTags().contains( tag );
        }

        public boolean isUniversal() {
            return tag != null && tag.isUniversal();
        }

        public String getTagName() {
            return tag == null
                    ? ""
                    : tag.getName();
        }

        public String getTitle() {
            if ( isUniversal() ) {
                return "Default type";
            } else if ( isImplicit() ) {
                return "Implicit";
            } else if ( isInherited() ) {
                return getEntity().inheritancePathTo( tag );
            } else {
                return "";
            }
        }

        public void setTagName( String name ) {
            assert isMarkedForCreation();
            if ( name != null && !name.isEmpty() ) {
                tag = doSafeFindOrCreateType( getEntity().getClass(), name );
                if ( tag != null ) {
                    doCommand( new UpdatePlanObject(
                            getEntity(),
                            "tags",
                            tag,
                            UpdateObject.Action.Add
                    ) );
                }
            }
        }

        public void removeTag() {
            assert !isMarkedForCreation();
            doCommand( new UpdatePlanObject(
                    getEntity(),
                    "tags",
                    tag,
                    UpdateObject.Action.Remove
            ) );
        }

        public boolean isRemovable() {
            return !isMarkedForCreation() && !isImplicit() && !isInherited() && !isUniversal();
        }
    }
}

