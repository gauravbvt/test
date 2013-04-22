package com.mindalliance.channels.pages.components.entities.agreements;

import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.Set;

/**
 * Organization agreements panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 11/6/12
 * Time: 2:32 PM
 */
public class OrganizationAgreementsPanel extends AbstractCommandablePanel {
    public OrganizationAgreementsPanel( String id, IModel<Organization> model, Set<Long> expansions, String prefixDomIdentifier ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        addAgreementsPanel();
    }

    private void addAgreementsPanel() {
        add( new AgreementsPanel(
                "agreements",
                new PropertyModel<Organization>( this, "organization" ),
                getExpansions() ) );

    }

    public Organization getOrganization() {
        return (Organization)getModel().getObject();
    }

}
