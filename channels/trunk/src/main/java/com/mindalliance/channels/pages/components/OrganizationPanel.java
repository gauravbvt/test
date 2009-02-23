package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Place;
import com.mindalliance.channels.pages.ModelObjectLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;

import java.text.Collator;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Feb 4, 2009
 * Time: 2:28:58 PM
 */
public class OrganizationPanel extends ModelObjectPanel {

    /**
     * String comparator for equality tests.
     */
    private static final Collator COMPARATOR = Collator.getInstance();

    public OrganizationPanel( String id, IModel<? extends ModelObject> model ) {
        super( id, model );
    }

    protected void addSpecifics( WebMarkupContainer moDetailsDiv ) {
        moDetailsDiv.add( new ModelObjectLink("org-link", new PropertyModel<Organization>(mo, "parent")) );
        moDetailsDiv.add(
                new TextField<String>( "parent",                                            // NON-NLS
                        new PropertyModel<String>( this, "parentOrganization" ) ) );
        moDetailsDiv.add(new ModelObjectLink( "loc-link", new PropertyModel<Organization>(mo, "location")));
        moDetailsDiv.add(
                new TextField<String>( "location",                                            // NON-NLS
                        new PropertyModel<String>( this, "locationName" ) ) );

    }

    public void setParentOrganization( String name ) {
        Organization org = (Organization) mo;
        if ( name == null || name.trim().isEmpty() ) {
            org.setParent( null );
        } else {
            Organization parent = org.getParent();
            if ( parent == null || COMPARATOR.compare( name, parent.getName() ) != 0 ) {
                org.setParent( Organization.named( name ) );
            }
        }
    }

    public String getParentOrganization() {
        Organization parent = ((Organization) mo).getParent();
        return parent == null ? "" : parent.getName();
    }

    public void setLocationName( String name ) {
        Organization org = (Organization) mo;
        if ( name == null || name.trim().isEmpty() ) {
            org.setLocation( null );
        } else {
            Place location = org.getLocation();
            if ( location == null || COMPARATOR.compare( name, location.getName() ) != 0 ) {
                org.setLocation( Place.named( name ) );
            }
        }

    }

    public String getLocationName() {
        Place location = ((Organization) mo).getLocation();
        return location == null ? "" : location.getName();
    }

}
