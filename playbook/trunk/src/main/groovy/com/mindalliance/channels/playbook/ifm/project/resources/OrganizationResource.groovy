package com.mindalliance.channels.playbook.ifm.project.resources

import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 10:15:50 AM
 */
class OrganizationResource extends Resource {

    Ref organization     // set only via organization.add|remove<Resource>()

    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['project']
    }
    

    boolean isOrganizationResource() {
        return true
    }

    Ref getProject() {
        return organization.project
    }

}