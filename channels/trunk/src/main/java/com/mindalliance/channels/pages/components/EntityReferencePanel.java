package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.CommandUtils;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.command.commands.UpdateScenarioObject;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Role;
import com.mindalliance.channels.model.ScenarioObject;
import com.mindalliance.channels.util.Matcher;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
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
    private ModelEntity.Kind entityKind = ModelEntity.Kind.Actual;
    /**
     * Entity class.
     */
    private Class<T> entityClass;
    /**
     * Default entity to set as default. Can be null.
     */
    private T defaultEntity;

    public EntityReferencePanel(
            String id,
            IModel<? extends Identifiable> iModel,
            List<String> choices,
            String property,
            Class<T> entityClass,
            T defaultEntity ) {
        super( id, iModel );
        this.choices = choices;
        this.property = property;
        this.entityClass = entityClass;
        this.defaultEntity = defaultEntity;
        init();
    }

    private void init() {
        this.setOutputMarkupId( true );
        addKindChoice();
        addEntityName();
        adjustFields();
    }

    private void addKindChoice() {
        // Actual vs type
        actualOrTypeChoice = new DropDownChoice<ModelEntity.Kind>(
                "actualOrType",
                new PropertyModel<ModelEntity.Kind>( this, "entityKind" ),
                Arrays.asList( ModelEntity.Kind.values() ),
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
                target.addComponent( nameField );
                setEntityName( null );
                if ( !oldName.equals( getEntityName() ) ) {
                    update( target, new Change( Change.Type.Updated, getReferencer(), property ) );
                }
            }
        } );
        actualOrTypeChoice.setOutputMarkupId( true );
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
                    new PropertyModel<String>( this, "entityName" ) ) {
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
                target.addComponent( actualOrTypeChoice );
                target.addComponent( nameField );
                update( target, new Change( Change.Type.Updated, getReferencer(), property ) );
            }
        } );
        add( nameField );
    }

    private void adjustFields() {
        if ( getReferencer() instanceof ModelObject )
            addIssues( nameField, (ModelObject) getReferencer(), property );
        nameField.setEnabled( nameField.isEnabled() && getEntityKind() != null );
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
            return entityKind;
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
        return (ModelEntity) CommandUtils.getProperty(
                getReferencer(),
                property,
                null );
    }

    private boolean matches( String text, String otherText ) {
        if ( entityClass.isAssignableFrom( Role.class ) || entityKind.equals( ModelEntity.Kind.Type ) ) {
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
                newEntity = getQueryService().safeFindOrCreate( entityClass, name );
            } else {
                newEntity = getQueryService().safeFindOrCreateType( entityClass, name );
            }
        }
        if ( newEntity == null ) {
            if ( entityKind.equals( ModelEntity.Kind.Type ) ) {
                newEntity = ModelEntity.getUniversalTypeFor( entityClass );
            } else {
                newEntity = defaultEntity;
            }
        }
        Identifiable referencer = getReferencer();
        if ( referencer instanceof ScenarioObject ) {
            doCommand( new UpdateScenarioObject( getReferencer(), property, newEntity ) );
        } else if ( referencer instanceof ModelObject ) {
            doCommand( new UpdatePlanObject( getReferencer(), property, newEntity ) );
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
