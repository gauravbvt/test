// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.model;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.util.GUID;

/**
 * Obligations and prohibitions imposed by an organization on
 * other organization regarding certain transfers of information.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 */
public class Policy extends AbstractNamedObject {

    private Organization organization;
    private List<ExchangeRequirement> forbiddenExchanges =
                new ArrayList<ExchangeRequirement>();
    private List<ExchangeRequirement> requiredExchanges =
                new ArrayList<ExchangeRequirement>();

    /**
     * Default constructor.
     * @param guid the unique ID for this object
     */
    Policy( GUID guid ) {
        super( guid );
    }

    /**
     * Return the value of forbiddenExchanges.
     */
    public List<ExchangeRequirement> getForbiddenExchanges() {
        return this.forbiddenExchanges;
    }

    /**
     * Set the value of forbiddenExchanges.
     * @param forbiddenExchanges The new value of forbiddenExchanges
     */
    public void setForbiddenExchanges(
            List<ExchangeRequirement> forbiddenExchanges ) {

        this.forbiddenExchanges = forbiddenExchanges;
    }

    /**
     * Return the value of organization.
     */
    public Organization getOrganization() {
        return this.organization;
    }

    /**
     * Set the value of organization.
     * @param organization The new value of organization
     */
    public void setOrganization( Organization organization ) {
        this.organization = organization;
    }

    /**
     * Return the value of requiredExchanges.
     */
    public List<ExchangeRequirement> getRequiredExchanges() {
        return this.requiredExchanges;
    }

    /**
     * Set the value of requiredExchanges.
     * @param requiredExchanges The new value of requiredExchanges
     */
    public void setRequiredExchanges(
            List<ExchangeRequirement> requiredExchanges ) {

        this.requiredExchanges = requiredExchanges;
    }
}
