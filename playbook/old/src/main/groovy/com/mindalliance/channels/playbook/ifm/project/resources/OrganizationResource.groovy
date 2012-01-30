package com.mindalliance.channels.playbook.ifm.project.resources

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.query.Query
import com.mindalliance.channels.playbook.ifm.Channels
import com.mindalliance.channels.playbook.ifm.project.InOrganization
import com.mindalliance.channels.playbook.ifm.project.InOrganization

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 10:15:50 AM
 */
abstract class OrganizationResource extends Resource implements InOrganization {

    private Ref cachedOrganization     // cached value found by query

    @Override
    List<String> transientProperties() {
        return (List<String>) (super.transientProperties() + ['project', 'organization', 'cachedOrganization'])
    }

    void detach() {
        super.detach()
        cachedOrganization = null
    }

    boolean isOrganizationResource() {
        return true
    }

    Ref getProject() {
        Ref org = getOrganization()
        return (org as boolean) ? org.project : null
    }

    Ref getOrganization() {
        if (cachedOrganization == null) {
            cachedOrganization = (Ref)Query.execute(Channels.instance(), "findOrganizationOfResource", this.reference)
        }
        return cachedOrganization
    }

}