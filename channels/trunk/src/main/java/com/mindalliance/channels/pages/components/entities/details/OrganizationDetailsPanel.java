package com.mindalliance.channels.pages.components.entities.details;

import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.ModelEntity;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.pages.components.guide.Guidable;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

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
     * Container for details.
     */
    private WebMarkupContainer moDetailsDiv;
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
        addProfile();
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

    /**
     * Get edited organization.
     *
     * @return an organization
     */
    public Organization getOrganization() {
        return (Organization) getEntity();
    }


}
