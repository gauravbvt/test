package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.Event;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.ConfirmedAjaxFallbackLink;
import com.mindalliance.channels.pages.components.plan.floating.PlanSearchingFloatingPanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
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
 * Types panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 16, 2009
 * Time: 7:27:02 AM
 */
public class TypesPanel extends AbstractCommandablePanel {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( TypesPanel.class );
    /**
     * Collator.
     */
    private static Collator collator = Collator.getInstance();

    private IModel<ModelEntity> entityModel;


    public TypesPanel( String id, IModel<ModelEntity> iModel ) {
        super( id );
        entityModel = iModel;
        init();
    }

    private void init() {
        WebMarkupContainer typesDiv = new WebMarkupContainer( "typesDiv" );
        typesDiv.setOutputMarkupId( true );
        add( typesDiv );
        addTypologiesLink( typesDiv );
        typesDiv.add( makeTypesTable() );
    }

    private void addTypologiesLink( WebMarkupContainer typesDiv ) {
        AjaxLink classificationsLink = new AjaxLink( "typologies" ) {
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.AspectViewed, Channels.PLAN_SEARCHING, PlanSearchingFloatingPanel.TAXONOMIES) );
            }
        };
        classificationsLink.add( new AttributeModifier( "class", new Model<String>( "window" ) ) );
        typesDiv.add( classificationsLink );
    }

    private ListView<TypeWrapper> makeTypesTable() {
        return new ListView<TypeWrapper>( "types", getWrappedTypes() ) {
            /** {@inheritDoc} */
            protected void populateItem( ListItem<TypeWrapper> item ) {
                addTypeCell( item );
                addDeleteCell( item );
            }
        };
    }

    @SuppressWarnings( "unchecked" )
    private void addTypeCell( final ListItem<TypeWrapper> item ) {
        final TypeWrapper wrapper = item.getModelObject();
        item.setOutputMarkupId( true );
        WebMarkupContainer nameContainer = new WebMarkupContainer( "typeContainer" );
        item.add( nameContainer );
        final List<String> choices;
        if ( wrapper.isMarkedForCreation() ) {
            choices = (List<String>) CollectionUtils.select(
                    getQueryService().findAllEntityNames( getEntity().getClass(), ModelEntity.Kind.Type ),
                    new Predicate() {
                        public boolean evaluate( Object obj ) {
                            ModelEntity type = getQueryService().safeFindOrCreateType(
                                    getEntity().getClass(),
                                    ( (String) obj ) );
                            return !type.equals( getEntity() ) &&
                                    !getEntity().hasType( type );
                        }
                    }
            );
        } else {
            choices = null;
        }
        // text field
        AutoCompleteTextField<String> nameField = new AutoCompleteTextField<String>(
                "newType",
                new PropertyModel<String>( wrapper, "typeName" ),
                getAutoCompleteSettings() ) {
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( getQueryService().likelyRelated( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();

            }
        };
        nameField.setOutputMarkupId( true );
        makeVisible( nameField, wrapper.isMarkedForCreation() && isLockedByUser( getEntity() ) );
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                target.add( item );
                update( target, new Change( Change.Type.Updated, getEntity(), "types" ) );
            }
        } );
        addInputHint( nameField, "Enter the name of a type of " + getEntity().getTypeName() + " (then press enter)");
        nameContainer.add( nameField );
        // Link to entity type
        EntityLink entityLink = new EntityLink( "typeLink", new PropertyModel<Event>( wrapper, "type" ) );
        entityLink.setVisible( !wrapper.isMarkedForCreation() && !wrapper.isUniversal() );
        if ( wrapper.isInherited() || wrapper.isImmutable() )
            entityLink.add(
                    new AttributeModifier( "style", new Model<String>( "font-style:oblique" ) ) );
        addTipTitle( entityLink, new Model<String>( wrapper.getTitle() ) );
        nameContainer.add( entityLink );
        // Universal type
        Label universalLabel = new Label( "universal", new Model<String>( wrapper.getTypeName() ) );
        universalLabel.setVisible( wrapper.isUniversal() );
        universalLabel.add(
                new AttributeModifier( "style", new Model<String>( "font-style:oblique" ) ) );
        addTipTitle( universalLabel, new Model<String>( wrapper.getTitle() ) );
        nameContainer.add( universalLabel );
    }

    private void addDeleteCell( ListItem<TypeWrapper> item ) {
        final TypeWrapper wrapper = item.getModelObject();
        ConfirmedAjaxFallbackLink deleteLink = new ConfirmedAjaxFallbackLink(
                "delete",
                "Remove type?" ) {
            public void onClick( AjaxRequestTarget target ) {
                wrapper.removeType();
                update( target,
                        new Change(
                                Change.Type.Updated,
                                getEntity(),
                                "types"
                        ) );
            }
        };
        makeVisible( deleteLink, isLockedByUser( getEntity() ) && wrapper.isRemovable() );
        item.addOrReplace( deleteLink );
    }

    private List<TypeWrapper> getWrappedTypes() {
        List<TypeWrapper> wrappers = new ArrayList<TypeWrapper>();
        wrappers.add( new TypeWrapper( ModelEntity.getUniversalTypeFor( getEntity().getClass() ) ) );
        List<TypeWrapper> current = new ArrayList<TypeWrapper>();
        for ( ModelEntity type : getEntity().getAllTypes() ) {
            current.add( new TypeWrapper( type ) );
        }
        // Sort
        Collections.sort( current, new Comparator<TypeWrapper>() {
            public int compare( TypeWrapper tw1, TypeWrapper tw2 ) {
                return collator.compare( tw1.getTypeName(), tw2.getTypeName() );
            }
        } );
        wrappers.addAll( current );
        if ( isLockedByUser( getEntity() ) ) {
            // to-be-created type
            wrappers.add( new TypeWrapper() );
        }
        return wrappers;
    }


    private ModelEntity getEntity() {
        return entityModel.getObject();
    }

    /**
     * A wrapped entity type.
     */
    public class TypeWrapper implements Serializable {

        private ModelEntity type;


        private TypeWrapper( ModelEntity type ) {
            assert type.isType();
            assert type.getDomain().equals( getEntity().getDomain() );
            this.type = type;
        }

        private TypeWrapper() {

        }

        public ModelEntity getType() {
            return type;
        }

        public boolean isMarkedForCreation() {
            return type == null;
        }

        public boolean isImmutable() {
            return type != null && type.isImmutable();
        }

        public boolean isInherited() {
            return type != null
                    && !getEntity().getTypes().contains( type )
                   // && !isImplicit()
                    && getEntity().hasType( type );
        }

        public boolean isImplicit() {
            return getEntity().getAllImplicitTypes().contains( type );
        }

        public boolean isExplicit() {
            return getEntity().getTypes().contains( type );
        }

        public boolean isUniversal() {
            return type != null && type.isUniversal();
        }

        public String getTypeName() {
            return type == null
                    ? ""
                    : type.getName();
        }

        public String getTitle() {
            if ( isUniversal() ) {
                return "Default type";
            } else if ( isImplicit() ) {
                return "Implicit";
            } else if ( isInherited() ) {
                return getEntity().inheritancePathTo( type );
            } else {
                return "";
            }
        }

        public void setTypeName( String name ) {
            assert isMarkedForCreation();
            if ( name != null && !name.isEmpty()) {
                type = doSafeFindOrCreateType( getEntity().getClass(), name );
                if ( type != null  && !type.isUniversal() && !getEntity().hasType( type ) ) {
                    doCommand( new UpdatePlanObject( getUser().getUsername(), getEntity(),
                            "types",
                            type,
                            UpdateObject.Action.Add ) );
                }
            }
        }

        public void removeType() {
            assert !isMarkedForCreation();
            doCommand( new UpdatePlanObject( getUser().getUsername(), getEntity(),
                    "types",
                    type,
                    UpdateObject.Action.Remove ) );
        }

        public boolean isRemovable() {
            return !isMarkedForCreation() && isExplicit();
        }
    }
}

