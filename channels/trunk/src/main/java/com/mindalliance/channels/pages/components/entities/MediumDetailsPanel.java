package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelEntity;
import com.mindalliance.channels.model.TransmissionMedium;
import com.mindalliance.channels.pages.components.ClassificationsPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

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
    /**
     * Unicast checkbox.
     */
    private CheckBox unicastCheckBox;
    /**
     * Container for address pattern field.
     */
    private WebMarkupContainer addressPatternContainer;
    /**
     * Container for security classifications panel.
     */
    private WebMarkupContainer securityContainer;
    /**
     * Address pattern field.
     */
    private TextField<String> addressPatternField;

    public MediumDetailsPanel( String id, PropertyModel<ModelEntity> entityModel, Set<Long> expansions ) {
        super( id, entityModel, expansions );
    }

    /**
     * {@inheritDoc}
     */
    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        this.moDetailsDiv = moDetailsDiv;
        addAddressPattern();
        addUnicast();
        addSecurity();
        adjustFields();
    }

    private void adjustFields() {
        unicastCheckBox.setEnabled( isLockedByUser( getMedium() ) );
        addressPatternContainer.setVisible( getMedium().isActual() );
        addressPatternField.setEnabled( isLockedByUser( getMedium() ) );
        securityContainer.setVisible( getMedium().isActual() );
    }

    private void addUnicast() {
        unicastCheckBox = new CheckBox( "unicast", new PropertyModel<Boolean>( this, "unicast" ) );
        unicastCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getMedium(), "unicast" ) );
            }
        } );
        moDetailsDiv.add( unicastCheckBox );
    }

    private void addAddressPattern() {
        addressPatternContainer = new WebMarkupContainer( "addressPatternContainer" );
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

    private void addSecurity() {
        securityContainer = new WebMarkupContainer( "securityContainer" );
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
     * Set whether medium is unicast.
     *
     * @param value a boolean
     */
    public void setUnicast( boolean value ) {
        TransmissionMedium medium = getMedium();
        if ( medium.isUnicast() != value ) {
            doCommand( new UpdatePlanObject( medium, "unicast", value ) );
        }
    }

    /**
     * Whether medium is unicast.
     *
     * @return a boolean
     */
    public boolean isUnicast() {
        return getMedium().isUnicast();
    }

    /**
     * Get the medium's address pattern.
     *
     * @return a string
     */
    public String getAddressPattern() {
        return getMedium().getAddressPattern();
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
}
