package com.mindalliance.channels.pages.components.entities;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.command.commands.UpdatePlanObject;
import com.mindalliance.channels.model.Channelable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Organization;
import com.mindalliance.channels.model.Place;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.components.ChannelListPanel;
import com.mindalliance.channels.util.SemMatch;
import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
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
 * Organization details panel.
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
        Organization organization = getOrganization();
        moDetailsDiv.add( new ModelObjectLink(
                "org-link",
                new PropertyModel<Organization>( organization, "parent" ),
                new Model<String>( "Parent" ) ) );
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
                        new PropertyModel<Organization>( organization, "location" ),
                        new Model<String>( "Location" ) ) );
        final List<String> locationChoices = getQueryService().findAllNames( Place.class );
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
        CheckBox actorsRequiredCheckBox = new CheckBox(
                "actorsRequired",
                new PropertyModel<Boolean>( this, "actorsRequired" ) );
        actorsRequiredCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getOrganization(), "actorsRequired" ) );
            }
        } );
        actorsRequiredCheckBox.setEnabled( isLockedByUser( getOrganization() ) );
        moDetailsDiv.add( actorsRequiredCheckBox );
        moDetailsDiv.add( new AjaxTabbedPanel( "tabs", getTabs() ) );
//        moDetailsDiv.add( new JobsPanel( "jobs", new Model<Organization>( organization ), getExpansions() ) );

    }

    private List<ITab> getTabs() {
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add( new AbstractTab( new Model<String>( "Jobs" ) ) {
            public Panel getPanel( String id ) {
                return new JobsPanel(
                        id,
                        new PropertyModel<Organization>( OrganizationDetailsPanel.this, "organization" ),
                        getExpansions() );
            }
        } );
        tabs.add( new AbstractTab( new Model<String>( "Chart" ) ) {
            public Panel getPanel( String id ) {
                return new OrgChartPanel(
                        id,
                        new PropertyModel<Organization>( OrganizationDetailsPanel.this, "organization" ),
                        getExpansions() );
            }
        } );
        return tabs;
    }

    @SuppressWarnings( "unchecked" )
    private List<String> findCandidateParents() {
        Organization organization = getOrganization();
        List<String> candidateNames = new ArrayList<String>();
        List<Organization> ancestors = organization.ancestors();
        List<Organization> allOrganizations =
                new ArrayList<Organization>( getQueryService().list( Organization.class ) );
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
                newOrg = getQueryService().findOrCreate( Organization.class, name );
                if ( newOrg.ancestors().contains( getOrganization() ) ) {
                    newOrg = oldOrg;
                    getCommander().cleanup( Organization.class, name );
                }
            }
        }
        doCommand( new UpdatePlanObject( getOrganization(), "parent", newOrg ) );
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
                newPlace = getQueryService().findOrCreate( Place.class, name );
        }
        doCommand( new UpdatePlanObject( org, "location", newPlace ) );
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

    /**
     * Get edited organization.
     *
     * @return an organization
     */
    public Organization getOrganization() {
        return (Organization) getEntity();
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
     * Update actors requirement.
     *
     * @param val a boolean
     */
    public void setActorsRequired( boolean val ) {
        doCommand(
                new UpdatePlanObject(
                        getOrganization(),
                        "actorsRequired",
                        val,
                        UpdateObject.Action.Set ) );
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        // Don't propagate UI updates because of changes to jobs
        if ( change.getProperty() == null || !change.getProperty().equals( "jobs" ) ) {
            super.updateWith( target, change );
        }
    }
}
