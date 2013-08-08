package com.mindalliance.channels.pages.components.entities.participation;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdatePlanObject;
import com.mindalliance.channels.core.model.Actor;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 8/8/13
 * Time: 12:03 PM
 */
public class OrganizationParticipationPanel extends AbstractCommandablePanel implements Guidable {

    private WebMarkupContainer participationContainer;
    private WebMarkupContainer custodianContainer;
    private AutoCompleteTextField<String> custodianField;
    private AjaxCheckBox placeHolderCheckbox;


    public OrganizationParticipationPanel( String id, PropertyModel<Organization> organizationModel, Set<Long> expansions ) {
        super( id, organizationModel, expansions );
        init();
    }

    @Override
    public String getHelpSectionId() {
        return "profiling";
    }

    @Override
    public String getHelpTopicId() {
        return "organization-participation";
    }


    private void init() {
        addParticipation();
        adjustFields();
    }

    private void addParticipation() {
        participationContainer = new WebMarkupContainer( "participationContainer" );
        participationContainer.setOutputMarkupId( true );
        participationContainer.setVisible( getOrganization().isActual() );
        placeHolderCheckbox = new AjaxCheckBox(
                "placeHolder",
                new PropertyModel<Boolean>( this, "placeHolder" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields();
                target.add( participationContainer );
                update( target, new Change( Change.Type.Updated, getOrganization() ) );
            }
        };
        participationContainer.add( placeHolderCheckbox );
        addCustodian();
        add( participationContainer );
    }

    private void addCustodian() {
        custodianContainer = new WebMarkupContainer( "custodianContainer" );
        custodianContainer.setOutputMarkupId( true );
        participationContainer.add( custodianContainer );
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
        custodianField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getOrganization() ) );
            }
        });
        addInputHint( custodianField, "The name of an actual agent" );
        custodianContainer.add( custodianField );
    }

    private void adjustFields() {
        Organization org = getOrganization();
        placeHolderCheckbox.setEnabled( isLockedByUser( org ) );
        makeVisible( custodianContainer, org.isPlaceHolder() );
        custodianField.setEnabled(  isLockedByUser( org ) );
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


    public Organization getOrganization() {
        return (Organization) getModel().getObject();
    }


}
