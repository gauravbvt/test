package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Place;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateProjectObject;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.components.ChannelListPanel;
import com.mindalliance.channels.util.SemMatch;
import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 4, 2009
 * Time: 2:28:58 PM
 */
public class OrganizationDetailsPanel extends EntityDetailsPanel {

    public OrganizationDetailsPanel(
            String id,
            IModel<? extends ModelObject> model,
            Set<Long> expansions ) {
        super( id, model, expansions );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        Organization organization= getOrganization();
        moDetailsDiv.add( new ModelObjectLink(
                "org-link",
                new PropertyModel<Organization>( organization, "parent" ) ) );
        final List<String> parentChoices = findCandidateParents();
        final TextField parentField = new AutoCompleteTextField<String>( "parent",
                new PropertyModel<String>( this, "parentOrganization" ) ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : parentChoices ) {
                    if ( SemMatch.matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        parentField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                target.addComponent( parentField );
                update( target, new Change( Change.Type.Updated, getModel().getObject(), "parent" ) );
            }
        } );
        moDetailsDiv.add( parentField );
        moDetailsDiv.add(
                new ModelObjectLink( "loc-link",
                        new PropertyModel<Organization>( organization, "location" ) ) );
        final List<String> locationChoices = getService().findAllNames( Place.class );
        TextField locationField = new AutoCompleteTextField<String>( "location",
                new PropertyModel<String>( this, "locationName" ) ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : locationChoices ) {
                    if ( SemMatch.matches( s, choice ) ) candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        locationField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getModel().getObject(), "location" ) );
            }
        } );
        moDetailsDiv.add( locationField );

        moDetailsDiv.add( new ChannelListPanel(
                "channels",
                new Model<Channelable>( organization ) ) );
        moDetailsDiv.add( new JobsPanel( "jobs", new Model<Organization>( organization  ), getExpansions() ) );

    }

    @SuppressWarnings( "unchecked" )
    private List<String> findCandidateParents() {
        Organization organization = getOrganization();
        List<String> candidateNames = new ArrayList<String>();
        List<Organization> ancestors = organization.ancestors();
        List<Organization> allOrganizations = getService().list( Organization.class );
        allOrganizations.remove( organization );
        Collection<Organization> candidates = CollectionUtils.subtract( allOrganizations, ancestors );
        for ( Organization candidate : candidates ) {
            if ( !candidate.ancestors().contains( organization ) )
                candidateNames.add( candidate.getName() );
        }
        Collections.sort( candidateNames );
        return candidateNames;
    }

    /**
     * Set organization's parent from name if not null ir empty.
     *
     * @param name a String
     */
    public void setParentOrganization( String name ) {
        Organization oldOrg = getOrganization().getParent();
        String oldName = oldOrg == null ? "" : oldOrg.getName();
        Organization newOrg = null;
        if ( name == null || name.trim().isEmpty() )
            newOrg = null;
        else {
            if ( oldOrg == null || !isSame( name, oldName ) ) {
                newOrg = getService().findOrCreate( Organization.class, name );
                if (newOrg.ancestors().contains( getOrganization() )) {
                    newOrg = oldOrg;
                    getCommander().cleanup( Organization.class, name );
                }
            }
        }
        doCommand( new UpdateProjectObject( getOrganization(), "parent", newOrg ) );
        getCommander().cleanup( Organization.class, oldName );
    }

    /**
     * Get organization's parent's name.
     *
     * @return a String
     */
    public String getParentOrganization() {
        Organization parent = ( getOrganization() ).getParent();
        return parent == null ? "" : parent.getName();
    }

    /**
     * Set organization's location from name, if not null or empty.
     *
     * @param name a String
     */
    public void setLocationName( String name ) {
        Organization org = getOrganization();
        Place oldPlace = org.getLocation();
        String oldName = oldPlace == null ? "" : oldPlace.getName();
        Place newPlace = null;
        if ( name == null || name.trim().isEmpty() )
            newPlace = null;
        else {
            if ( oldPlace == null || !isSame( name, oldName ) )
                newPlace = getService().findOrCreate( Place.class, name );
        }
        doCommand( new UpdateProjectObject( org, "location", newPlace ) );
        getCommander().cleanup( Place.class, oldName );
    }

    /**
     * Get organization's location's name.
     *
     * @return a String
     */
    public String getLocationName() {
        Place location = ( getOrganization() ).getLocation();
        return location == null ? "" : location.getName();
    }

    private Organization getOrganization() {
        return (Organization) getEntity();
    }

}
