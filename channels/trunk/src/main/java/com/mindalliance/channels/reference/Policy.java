// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.reference;

import java.util.ArrayList;
import java.util.List;

import com.mindalliance.channels.data.elements.resources.Organization;
import com.mindalliance.channels.rules.Exchange;
import com.mindalliance.channels.util.AbstractJavaBean;

/**
 * Obligations and prohibitions imposed by an organization on
 * other organization regarding certain transfers of information.
 *
 * @author <a href="mailto:denis@mind-alliance.com">denis</a>
 * @version $Revision$
 *
 * @opt attribute
 * @navassoc - - 1 Organization
 */
public class Policy extends AbstractJavaBean {

    private Organization organization;
    private List<Exchange> forbiddenExchanges =
                new ArrayList<Exchange>();
    private List<Exchange> requiredExchanges =
                new ArrayList<Exchange>();

    /**
     * Default constructor.
     */
    Policy() {
        super();
    }

    /**
     * Return the value of forbiddenExchanges.
     */
    public List<Exchange> getForbiddenExchanges() {
        return this.forbiddenExchanges;
    }

    /**
     * Set the value of forbiddenExchanges.
     * @param forbiddenExchanges The new value of forbiddenExchanges
     */
    public void setForbiddenExchanges(
            List<Exchange> forbiddenExchanges ) {

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
    public List<Exchange> getRequiredExchanges() {
        return this.requiredExchanges;
    }

    /**
     * Set the value of requiredExchanges.
     * @param requiredExchanges The new value of requiredExchanges
     */
    public void setRequiredExchanges(
            List<Exchange> requiredExchanges ) {

        this.requiredExchanges = requiredExchanges;
    }
}
