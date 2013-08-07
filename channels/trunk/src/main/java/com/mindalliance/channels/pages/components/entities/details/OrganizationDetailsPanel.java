package com.mindalliance.channels.pages.components.entities.details;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Organization details panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 4, 2009
 * Time: 2:28:58 PM
 */
public class OrganizationDetailsPanel extends EntityDetailsPanel implements Guidable {
    /**
     * Whether actors are required for each role in the organization.
     */
//    private CheckBox actorsRequiredCheckBox;
    /**
     * Whether agreements are required for each sharing commitment from the organization.
     */
//    private CheckBox agreementsRequiredCheckBox;
    /**
     * Container for details.
     */
    private WebMarkupContainer moDetailsDiv;
    private WebMarkupContainer participationContainer;
    private AutoCompleteTextField<String> custodianField;
    private AjaxCheckBox placeHolderCheckbox;
    private Component organizationProfile;

    public OrganizationDetailsPanel(
            String id,
            IModel<? extends ModelEntity> model,
            Set<Long> expansions ) {
        super( id, model, expansions );
    }

    @Override
    public String getHelpSectionId() {
        return "profiling";
    }

    @Override
    public String getHelpTopicId() {
        return "profiling-organization";
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {

        this.moDetailsDiv = moDetailsDiv;
        addParticipation();
        addProfile();
//        addConstraintFields();
        adjustFields();
    }

    private void addParticipation() {
        participationContainer = new WebMarkupContainer( "participationContainer" );
        participationContainer.setVisible( getOrganization().isActual() );
        placeHolderCheckbox = new AjaxCheckBox(
                "placeHolder",
                new PropertyModel<Boolean>( this, "placeHolder" )) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addProfile();
                adjustFields();
                target.add( moDetailsDiv );
            }
        };
        participationContainer.add( placeHolderCheckbox );
        addCustodian();
        moDetailsDiv.add( participationContainer );
    }

    private void addCustodian() {
        final List<String> custodianChoices = getQueryService().findAllEntityNames(
                Actor.class,
                ModelEntity.Kind.Actual );
        custodianField = new AutoCompleteTextField<String>(
                "custodian",
                new PropertyModel<String>( this, "custodianName" ),
                getAutoCompleteSettings()
        ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : custodianChoices ) {
                    if ( getOrganization().isType() ) {
                        if ( getQueryService().likelyRelated( s, choice ) ) candidates.add( choice );
                    } else if ( Matcher.matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        custodianField.setOutputMarkupId( true );
        addInputHint( custodianField, "The name of an actual agent" );
        participationContainer.add( custodianField );
    }


    private void addProfile() {
        if ( getOrganization().isPlaceHolder() ) {
            organizationProfile = new Label("profile", "");
            organizationProfile.setVisible( false );
            organizationProfile.setOutputMarkupId( true );
        } else {
        organizationProfile = new OrganizationProfile(
                "profile",
                new PropertyModel<Identifiable>( this, "organization" ) );
        }
        moDetailsDiv.addOrReplace( organizationProfile );
    }


 /*   private void addConstraintFields() {
        final Organization organization = getOrganization();
        WebMarkupContainer constraintsContainersContainers = new WebMarkupContainer( "constraintsContainers" );
        constraintsContainersContainers.setVisible( organization.isActual() );
        moDetailsDiv.add( constraintsContainersContainers );
        actorsRequiredCheckBox = new CheckBox(
                "actorsRequired",
                new PropertyModel<Boolean>( this, "actorsRequired" ) );
        actorsRequiredCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, organization, "actorsRequired" ) );
            }
        } );
        actorsRequiredCheckBox.setEnabled( isLockedByUser( organization ) );
        constraintsContainersContainers.add( actorsRequiredCheckBox );
        agreementsRequiredCheckBox = new CheckBox(
                "agreementsRequired",
                new PropertyModel<Boolean>( this, "agreementsRequired" ) );
        agreementsRequiredCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, organization, "agreementsRequired" ) );
            }
        } );
        Organization requiringParent = organization.agreementRequiringParent();
        if ( requiringParent != null ) {
            addTipTitle(
                    agreementsRequiredCheckBox,
                    new Model<String>( "Agreements required by parent organization " + requiringParent.getName() )  );
        }
        agreementsRequiredCheckBox.setEnabled( requiringParent == null && isLockedByUser( organization ) );
        constraintsContainersContainers.add( agreementsRequiredCheckBox );
    }
*/
    private void adjustFields() {
        Organization org = getOrganization();
//        actorsRequiredCheckBox.setEnabled( isLockedByUser( org ) );
        placeHolderCheckbox.setEnabled( isLockedByUser( org ) );
        custodianField.setEnabled( org.isPlaceHolder() && isLockedByUser( org ) );
    }


    /**
     * Get edited organization.
     *
     * @return an organization
     */
    public Organization getOrganization() {
        return (Organization) getEntity();
    }

    public boolean isPlaceHolder() {
        return getOrganization().isPlaceHolder();
    }

    public void setPlaceHolder( boolean val ) {
        doCommand(
                new UpdatePlanObject( getUser().getUsername(), getOrganization(),
                        "placeHolder",
                        val,
                        UpdateObject.Action.Set ) );
    }

    public String getCustodianName() {
        Actor custodian = getOrganization().getCustodian();
        return custodian == null ? "" : custodian.getName();
    }

    public void setCustodianName( String name ) {
        Organization org = getOrganization();
        Actor oldCustodian = org.getCustodian();
        String oldName = oldCustodian == null ? "" : oldCustodian.getName();
        Actor newCustodian = null;
        if ( name == null || name.trim().isEmpty() )
            newCustodian = null;
        else {
            if ( oldCustodian == null || !isSame( name, oldName ) )
                newCustodian = doSafeFindOrCreateActual( Actor.class, name );
        }
        doCommand( new UpdatePlanObject( getUser().getUsername(), org, "custodian", newCustodian ) );
        getCommander().cleanup( Actor.class, oldName );
    }

    /**
     * Are actors required in roles?
     *
     * @return a boolean
     */
    public boolean isActorsRequired() {
        return getOrganization().isActorsRequired();
    }

    /**
     * Update actors constraint.
     *
     * @param val a boolean
     */
    public void setActorsRequired( boolean val ) {
        doCommand(
                new UpdatePlanObject( getUser().getUsername(), getOrganization(),
                        "actorsRequired",
                        val,
                        UpdateObject.Action.Set ) );
    }

    /**
     * Are actors required in roles?
     *
     * @return a boolean
     */
    public boolean isAgreementsRequired() {
        return getOrganization().isEffectiveAgreementsRequired();
    }

    /**
     * Update actors constraint.
     *
     * @param val a boolean
     */
    public void setAgreementsRequired( boolean val ) {
        doCommand(
                new UpdatePlanObject( getUser().getUsername(), getOrganization(),
                        "agreementsRequired",
                        val,
                        UpdateObject.Action.Set ) );
    }


}
