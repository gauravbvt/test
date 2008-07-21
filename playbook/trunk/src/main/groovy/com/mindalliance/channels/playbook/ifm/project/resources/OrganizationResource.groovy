package com.mindalliance.channels.playbook.ifm.project.resources

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.ProjectElement
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.InProject
import com.mindalliance.channels.playbook.query.Query

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 5, 2008
 * Time: 10:15:50 AM
 */
abstract class OrganizationResource extends Resource {

    Ref organization     // set only via organization.add|remove<Resource>()

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['project'])
    }

    boolean isOrganizationResource() {
        return true
    }

    Ref getProject() {
        return (organization as boolean) ? organization.project : null
    }


}