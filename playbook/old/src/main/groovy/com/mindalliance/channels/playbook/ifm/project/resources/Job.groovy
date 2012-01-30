package com.mindalliance.channels.playbook.ifm.project.resources

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ifm.Responsibility

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 18, 2008
 * Time: 12:25:52 PM
 */
class Job extends OrganizationResource implements Agent {

    Ref individual
    Ref position
    List<ContactInfo> contactInfos = []

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['roles', 'aJob', 'name'])
    }

    boolean isAgent() {
        return true
    }

    List<Ref> getRoles() {
        return position as boolean ? position.roles : []
    }

    boolean hasRole(Ref role) {
        return position as boolean ? position.hasRole(role) : false
    }

    boolean isAJob() {
        return false
    }

    Ref getOrganization() {
        return position as boolean ? position.organization : null
    }

    String getName() {
        return "${individual as boolean ? (individual.name ?: '?') : '?'} as ${position as boolean ? (position.name ?: '?') : '?'}"
    }

    String toString() {
        return getName()
    }

}