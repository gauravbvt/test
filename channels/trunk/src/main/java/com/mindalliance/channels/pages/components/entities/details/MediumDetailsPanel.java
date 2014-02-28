package com.mindalliance.channels.pages.components.entities.details;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateModelObject;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.TransmissionMedium;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.components.ClassificationsPanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Transmission medium details panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 7, 2009
 * Time: 11:29:49 AM
 */
public class MediumDetailsPanel extends EntityDetailsPanel implements Guidable {

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
    private AjaxLink castResetLink;
    /**
     * Address pattern field.
     */
    private TextField<String> addressPatternField;
    /**
     * Delegated-to media panel.
     */
    private DelegatedToMediaPanel delegatedToMediaPanel;

    /**
     * Qualification link.
     */
    private ModelObjectLink qualificationLink;
    /**
     * Agent type name field.
     */
    private TextField qualificationField;
    /**
     * Synchronous checkbox field.
     */
    private CheckBox synchronousCheckBox;
    private CheckBox forContactInfoCheckBox;


    public MediumDetailsPanel( String id, PropertyModel<ModelEntity> entityModel, Set<Long> expansions ) {
        super( id, entityModel, expansions );
    }

    @Override
    public String getHelpSectionId() {
        return "profiling";
    }

    @Override
    public String getHelpTopicId() {
        return "profiling-medium";
    }



    /**
     * {@inheritDoc}
     */
    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        this.moDetailsDiv = moDetailsDiv;
        addAddressPattern();
        addCastLabel();
        addCastChoiceAndReset();
        addIsSynchronous();
        addIsForContactInfo();
        addDelegatedToMedia();
        addSecurity();
        addQualificationLink();
        addQualificationField();
        adjustFields();
    }

    private void adjustFields() {
        TransmissionMedium medium = getMedium();
        castChoice.setEnabled( isLockedByUser( medium ) );
        castResetLink.setVisible( isLockedByUser( medium ) );
        addressPatternField.setEnabled( isLockedByUser( medium ) );
        qualificationField.setEnabled( isLockedByUser( medium ) );
        synchronousCheckBox.setEnabled( isLockedByUser( medium ) );
        forContactInfoCheckBox.setEnabled( isLockedByUser( medium ) );
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

    private void addIsSynchronous() {
        synchronousCheckBox = new CheckBox( "synchronous", new PropertyModel<Boolean>( this, "synchronous" ) );
        synchronousCheckBox.setOutputMarkupId( true );
        synchronousCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getMedium(), "synchronous" ) );
            }
        } );
        moDetailsDiv.add( synchronousCheckBox );
    }

    private void addIsForContactInfo() {
        forContactInfoCheckBox = new CheckBox( "forContactInfo", new PropertyModel<Boolean>( this, "forContactInfo" ) );
        forContactInfoCheckBox.setOutputMarkupId( true );
        forContactInfoCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getMedium(), "forContactInfo" ) );
            }
        } );
        moDetailsDiv.add( forContactInfoCheckBox );
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
                target.add( castResetLink );
                target.add( delegatedToMediaPanel );
                update( target, new Change( Change.Type.Updated, getMedium(), "cast" ) );
            }
        } );
        castChoice.setOutputMarkupId( true );
        castChoiceContainer.addOrReplace( castChoice );
    }

    private void addCastReset() {
        castResetLink = new AjaxLink( "resetChoice" ) {
            public void onClick( AjaxRequestTarget target ) {
                setCast( null );
                addCastChoice();
                addDelegatedToMedia();
                addCastReset();
                target.add( castChoice );
                target.add( castResetLink );
                target.add( delegatedToMediaPanel );
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
        addInputHint( addressPatternField, "A regular expression" );
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

    private void addQualificationLink() {
        qualificationLink = new ModelObjectLink(
                "qualification-link",
                new PropertyModel<Actor>( getMedium(), "qualification" ),
                new Model<String>( "Required qualification" ) );
        moDetailsDiv.addOrReplace( qualificationLink );
    }

    private void addQualificationField() {
        qualificationField = new AutoCompleteTextField<String>(
                "qualification",
                new PropertyModel<String>( this, "qualificationName" ),
                getAutoCompleteSettings() ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                final List<String> qualificationChoices = findCandidateQualifications();
                List<String> candidates = new ArrayList<String>();
                for ( String choice : qualificationChoices ) {
                    if ( Matcher.matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        qualificationField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addQualificationLink();
                target.add( qualificationLink );
                target.add( qualificationField );
                update( target, new Change( Change.Type.Updated, getMedium(), "qualification" ) );
            }
        } );
        addInputHint( qualificationField, "The name of a type of agent" );
        moDetailsDiv.add( qualificationField );
    }

    private List<String> findCandidateQualifications() {
        List<String> candidateNames = new ArrayList<String>();
        for ( Actor actorType : getQueryService().listTypeEntities( Actor.class ) ) {
            candidateNames.add( actorType.getName() );
        }
        Collections.sort( candidateNames );
        return candidateNames;
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
            doCommand( new UpdateModelObject( getUser().getUsername(), medium, "cast", value ) );
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
            doCommand( new UpdateModelObject( getUser().getUsername(), getMedium(), "addressPattern", value ) );
        }
    }

    /**
     * Is medium synchronous?
     *
     * @return a boolean
     */
    public boolean isSynchronous() {
        return getMedium().isSynchronous();
    }

    /**
     * Set if medium synchronous.
     *
     * @param value a string
     */
    public void setSynchronous( boolean value ) {
        TransmissionMedium medium = getMedium();
        if ( medium.isSynchronous() != value ) {
            doCommand( new UpdateModelObject( getUser().getUsername(), medium, "synchronous", value ) );
        }
    }

    /**
     * Is medium for contact info?
     *
     * @return a boolean
     */
    public boolean isForContactInfo() {
        return getMedium().isForContactInfo();
    }

    /**
     * Set if medium is for contact info.
     *
     * @param value a string
     */
    public void setForContactInfo( boolean value ) {
        TransmissionMedium medium = getMedium();
        if ( medium.isForContactInfo() != value ) {
            doCommand( new UpdateModelObject( getUser().getUsername(), medium, "forContactInfo", value ) );
        }
    }


    /**
     * Get qualification's name.
     *
     * @return a string
     */
    public String getQualificationName() {
        Actor qualification = getMedium().getQualification();
        return qualification == null ? "" : qualification.getName();
    }

    /**
     * Set the medium's qualification.
     *
     * @param name a string
     */
    public void setQualificationName( String name ) {
        String oldName = getQualificationName();
        String newName = name == null ? "" : name.trim();
        if ( !isSame( oldName, newName ) ) {
            Actor newQualification = newName.isEmpty() ? null : doSafeFindOrCreateType( Actor.class, name );
            doCommand(
                    new UpdateModelObject( getUser().getUsername(), getEntity(),
                            "qualification",
                            newQualification,
                            UpdateObject.Action.Set ) );
            getCommander().cleanup( Actor.class, oldName );
        }
    }


    /**
     * {@inheritDoc}
     */
    protected void typesChanged( AjaxRequestTarget target ) {
        super.typesChanged( target );
        addCastChoice();
        addDelegatedToMedia();
        target.add( castChoice );
        target.add( delegatedToMediaPanel );
    }
}
