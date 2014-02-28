package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateModelObject;
import com.mindalliance.channels.core.command.commands.UpdateSegmentObject;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Role;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Entity reference panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Oct 8, 2009
 * Time: 10:46:29 AM
 */
public class EntityReferencePanel<T extends ModelEntity> extends AbstractCommandablePanel {

    /**
     * Class logger.
     */
    public static final Logger LOG = LoggerFactory.getLogger( EntityReferencePanel.class );
    /**
     * Property of the model object which value is the entity (actual or type) named.
     */
    private String property;
    /**
     * Name choices combining both types and actuals.
     */
    private List<String> choices;
    /**
     * Choice of actual or type.
     */
    private DropDownChoice<ModelEntity.Kind> actualOrTypeChoice;
    /**
     * Entity name field.
     */
    private TextField nameField;
    /**
     * Kind of entity selected.
     */
    private ModelEntity.Kind entityKind;
    /**
     * Entity class.
     */
    private Class<T> entityClass;
    /**
     * Default entity to set as default. Can be null.
     */
    private T defaultEntity;
    /**
     * If set, set to kind of reference.
     */
    private ModelEntity.Kind referenceKind = null;

    public EntityReferencePanel(
            String id,
            IModel<? extends Identifiable> iModel,
            List<String> choices,
            String property,
            Class<T> entityClass ) {
        this( id, iModel, choices, property, entityClass, null, null );
    }

    public EntityReferencePanel(
            String id,
            IModel<? extends Identifiable> iModel,
            List<String> choices,
            String property,
            Class<T> entityClass,
            T defaultEntity,
            ModelEntity.Kind referenceKind ) {
        super( id, iModel );
        entityKind = ModelEntity.defaultKindFor ( entityClass );
        this.choices = choices;
        this.property = property;
        this.entityClass = entityClass;
        this.defaultEntity = defaultEntity;
        this.referenceKind = referenceKind;

        add( new AttributeModifier( "class", new Model<String>( "entityReference" ) ) );
        init();

    }

    private void init() {
        this.setOutputMarkupId( true );
        addKindChoice();
        addEntityName();
        adjustFields();
    }

    private void addKindChoice() {
        ModelEntity.Kind[] kinds = { ModelEntity.Kind.Type, ModelEntity.Kind.Actual };
        // Actual vs type
        actualOrTypeChoice = new DropDownChoice<ModelEntity.Kind>(
                "actualOrType",
                new PropertyModel<ModelEntity.Kind>( this, "entityKind" ),
                Arrays.asList( kinds ),
                new ChoiceRenderer<ModelEntity.Kind>() {
                    public Object getDisplayValue( ModelEntity.Kind kind ) {
                        return kind.name();
                    }
                }
        );
        actualOrTypeChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                String oldName = getEntityName();
                adjustFields();
                target.add( nameField );
                setEntityName( null );
                if ( !oldName.equals( getEntityName() ) ) {
                    update( target, new Change( Change.Type.Updated, getReferencer(), property ) );
                }
            }
        } );
        actualOrTypeChoice.setOutputMarkupId( true );
        actualOrTypeChoice.setVisible(
                referenceKind == null
                && ModelEntity.canBeActualOrType( entityClass )
        );
        add( actualOrTypeChoice );
    }

    private void addEntityName() {
        if ( choices == null ) {
            nameField = new TextField<String>(
                    "name",
                    new PropertyModel<String>( this, "entityName" ) );
        } else {
            nameField = new AutoCompleteTextField<String>(
                    "name",
                    new PropertyModel<String>( this, "entityName" ),
                    getAutoCompleteSettings() ) {
                protected Iterator<String> getChoices( String s ) {
                    List<String> candidates = new ArrayList<String>();
                    for ( String choice : choices ) {
                        ModelEntity entity = getQueryService().getDao().find( entityClass, choice );
                        if ( entity != null
                                && entity.isType() == isType()
                                && matches( s, choice ) ) candidates.add( choice );
                    }
                    return candidates.iterator();
                }
            };
        }
        nameField.setOutputMarkupId( true );
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields();
                target.add( actualOrTypeChoice );
                target.add( nameField );
                update( target, new Change( Change.Type.Updated, getReferencer(), property ) );
            }
        } );
        add( nameField );
    }

    private void adjustFields() {
        if ( getReferencer() instanceof ModelObject )
            addIssues( nameField, (ModelObject) getReferencer(), property );
        nameField.setEnabled( nameField.isEnabled() && getEntityKind() != null );
        addInputHint(
                nameField,
                ModelEntity.getNameHint( entityClass, getEntityKind() ) );
    }

    /**
     * Get the entity's kind.
     * (For internal use only)
     *
     * @return a model entity's kind (actual or type)
     */
    public ModelEntity.Kind getEntityKind() {
        ModelEntity entity = getEntity();
        if ( entity == null ) {
            return referenceKind != null ? referenceKind : entityKind;
        } else {
            return entity.getKind();
        }
    }

    /**
     * Get the entity's kind.
     * (For internal use only)
     *
     * @param kind a model entity's kind
     */
    public void setEntityKind( ModelEntity.Kind kind ) {
        entityKind = kind;
    }

    private ModelEntity getEntity() {
        return (ModelEntity) ChannelsUtils.getProperty(
                getReferencer(),
                property,
                null );
    }

    private boolean matches( String text, String otherText ) {
        if ( entityClass.isAssignableFrom( Role.class ) || getEntityKind().equals( ModelEntity.Kind.Type ) ) {
            return getQueryService().likelyRelated( text, otherText );
        } else {
            return Matcher.matches( text, otherText );
        }
    }

    private Identifiable getReferencer() {
        return (Identifiable) getDefaultModelObject();
    }

    /**
     * Get entity's name.
     * (For internal use only)
     *
     * @return a string
     */
    public String getEntityName() {
        ModelEntity entity = getEntity();
        if ( entity != null ) {
            return entity.getName();
        } else {
            return "";
        }
    }

    /**
     * Set entity's name.
     * (For internal use only)
     *
     * @param name a string
     */
    public void setEntityName( String name ) {
        ModelEntity oldEntity = getEntity();
        String oldName = oldEntity == null ? "" : oldEntity.getName();
        ModelEntity newEntity = null;
        if ( name != null && !name.trim().isEmpty()
                && ( oldEntity == null || !isSame( name, oldName ) ) ) {
            if ( isActual() ) {
                newEntity = doSafeFindOrCreateActual( entityClass, name );
            } else {
                newEntity = doSafeFindOrCreateType( entityClass, name );
            }
        }
        if ( newEntity == null && defaultEntity != null ) {
           newEntity = defaultEntity;
        }
        Identifiable referencer = getReferencer();
        if ( referencer instanceof SegmentObject ) {
            doCommand( new UpdateSegmentObject( getUser().getUsername(), getReferencer(), property, newEntity ) );
        } else if ( referencer instanceof ModelObject ) {
            doCommand( new UpdateModelObject( getUser().getUsername(), getReferencer(), property, newEntity ) );
        } else {
            // Probably some wrapper is the referencer
            try {
                PropertyUtils.setProperty( referencer, property, newEntity );
            } catch ( Exception e ) {
                throw new RuntimeException( e );
            }
        }
        if ( oldEntity != null ) {
            getCommander().cleanup( oldEntity.getClass(), oldName );
        }
    }

    /**
     * Enable or disable.
     *
     * @param enabled a boolean
     */
    public void enable( boolean enabled ) {
        actualOrTypeChoice.setEnabled( enabled );
        nameField.setEnabled( enabled );
    }

    private boolean isActual() {
        ModelEntity.Kind kind = getEntityKind();
        return kind == null || kind.equals( ModelEntity.Kind.Actual );
    }

    private boolean isType() {
        ModelEntity.Kind kind = getEntityKind();
        return kind != null && kind.equals( ModelEntity.Kind.Type );
    }

    /**
     * Update property issues highlighting.
     */
    public void updateIssues() {
        addIssues( nameField, (ModelObject) getReferencer(), property );
    }

}
