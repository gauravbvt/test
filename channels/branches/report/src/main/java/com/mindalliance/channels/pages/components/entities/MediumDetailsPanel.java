package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.pages.components.ClassificationsPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.Arrays;
import java.util.Set;

/**
 * Transmission medium details panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 7, 2009
 * Time: 11:29:49 AM
 */
public class MediumDetailsPanel extends EntityDetailsPanel {

    /**
     * Container to add components to.
     */
    private WebMarkupContainer moDetailsDiv;

    private WebMarkupContainer castChoiceContainer;
    /**
     * Choice between unicast, multicast or broadcast.
     */
    private DropDownChoice<TransmissionMedium.Cast> castChoice;
    /**
     * Reset to null cast choice link.
     */
    private AjaxFallbackLink castResetLink;
    /**
     * Address pattern field.
     */
    private TextField<String> addressPatternField;
    /**
     * Delegated-to media panel.
     */
    private DelegatedToMediaPanel delegatedToMediaPanel;

    public MediumDetailsPanel( String id, PropertyModel<ModelEntity> entityModel, Set<Long> expansions ) {
        super( id, entityModel, expansions );
    }

    /**
     * {@inheritDoc}
     */
    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        this.moDetailsDiv = moDetailsDiv;
        addAddressPattern();
        addCastLabel();
        addCastChoiceAndReset();
        addDelegatedToMedia();
        addSecurity();
        adjustFields();
    }

    private void adjustFields() {
        castChoice.setEnabled( isLockedByUser( getMedium() ) );
        castResetLink.setVisible( isLockedByUser( getMedium() ) );
        addressPatternField.setEnabled( isLockedByUser( getMedium() ) );
    }

    private void addCastLabel() {
        TransmissionMedium.Cast effectiveCast = getMedium().getEffectiveCast();
        Label castLabel = new Label(
                "castLabel",
                effectiveCast == null ? "" : effectiveCast.name() );
        makeVisible( castLabel, getMedium().getInheritedCast() != null );
        moDetailsDiv.add( castLabel );
    }

    private void addCastChoiceAndReset() {
        castChoiceContainer = new WebMarkupContainer( "castChoiceContainer" );
        castChoiceContainer.setOutputMarkupId( true );
        makeVisible( castChoiceContainer, getMedium().getInheritedCast() == null );
        moDetailsDiv.addOrReplace( castChoiceContainer );
        addCastChoice();
        addCastReset();
    }

    private void addCastChoice() {
        castChoice = new DropDownChoice<TransmissionMedium.Cast>(
                "cast",
                new PropertyModel<TransmissionMedium.Cast>( this, "cast" ),
                Arrays.asList( TransmissionMedium.Cast.values() )
        );
        castChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addCastReset();
                addDelegatedToMedia();
                target.addComponent( castResetLink );
                target.addComponent( delegatedToMediaPanel );
                update( target, new Change( Change.Type.Updated, getMedium(), "cast" ) );
            }
        } );
        castChoice.setOutputMarkupId( true );
        castChoiceContainer.addOrReplace( castChoice );
    }

    private void addCastReset() {
        castResetLink = new AjaxFallbackLink( "resetChoice" ) {
            public void onClick( AjaxRequestTarget target ) {
                setCast( null );
                addCastChoice();
                addDelegatedToMedia();
                addCastReset();
                target.addComponent( castChoice );
                target.addComponent( castResetLink );
                target.addComponent( delegatedToMediaPanel );
                update( target, new Change( Change.Type.Updated, getMedium(), "cast" ) );
            }
        };
        castResetLink.setOutputMarkupId( true );
        makeVisible( castResetLink, getMedium().getCast() != null );
        castChoiceContainer.addOrReplace( castResetLink );
    }

    private void addAddressPattern() {
        WebMarkupContainer addressPatternContainer = new WebMarkupContainer( "addressPatternContainer" );
        moDetailsDiv.add( addressPatternContainer );
        addressPatternField = new TextField<String>(
                "addressPattern",
                new PropertyModel<String>( this, "addressPattern" ) );
        addressPatternField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getMedium(), "addressPattern" ) );
            }
        } );
        addressPatternContainer.add( addressPatternField );
    }

    private void addDelegatedToMedia() {
        delegatedToMediaPanel = new DelegatedToMediaPanel(
                "delegatedToMedia",
                new Model<TransmissionMedium>( getMedium() )
        );
        delegatedToMediaPanel.setOutputMarkupId( true );
        moDetailsDiv.addOrReplace( delegatedToMediaPanel );
    }

    private void addSecurity() {
        WebMarkupContainer securityContainer = new WebMarkupContainer( "securityContainer" );
        moDetailsDiv.add( securityContainer );
        securityContainer.add( new ClassificationsPanel(
                "security",
                new Model<Identifiable>( getMedium() ),
                "security",
                isLockedByUser( getMedium() )
        ) );
    }

    private TransmissionMedium getMedium() {
        return (TransmissionMedium) getEntity();
    }

    /**
     * Set whether medium cast.
     *
     * @param value a boolean
     */
    public void setCast( TransmissionMedium.Cast value ) {
        TransmissionMedium medium = getMedium();
        if ( medium.getCast() != value ) {
            doCommand( new UpdatePlanObject( medium, "cast", value ) );
        }
    }

    /**
     * Get medium's cast.
     *
     * @return a casting type (unicast etc.)
     */
    public TransmissionMedium.Cast getCast() {
        return getMedium().getCast();
    }

    /**
     * Get the medium's address pattern.
     *
     * @return a string
     */
    public String getAddressPattern() {
        return getMedium().getEffectiveAddressPattern();
    }

    /**
     * Set the medium's address pattern.
     *
     * @param value a string
     */
    public void setAddressPattern( String value ) {
        TransmissionMedium medium = getMedium();
        if ( !medium.getAddressPattern().equals( value ) ) {
            doCommand( new UpdatePlanObject( getMedium(), "addressPattern", value ) );
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void tagsChanged( AjaxRequestTarget target ) {
        super.tagsChanged( target );
        addCastChoice();
        addDelegatedToMedia();
        target.addComponent( castChoice );
        target.addComponent( delegatedToMediaPanel );
    }
}
