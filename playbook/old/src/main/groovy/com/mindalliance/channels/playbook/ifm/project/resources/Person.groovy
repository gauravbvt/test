package com.mindalliance.channels.playbook.ifm.project.resources

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.query.Query
import com.mindalliance.channels.playbook.ifm.Agent

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 17, 2008
 * Time: 11:19:24 AM
 */
class Person extends Resource implements Individual {

    String firstName = ''
    String middleName = ''
    String lastName = ''

    @Override
    List<String> transientProperties() {
        return (List<String>) (super.transientProperties() + ['name', 'jobs'])
    }

    Set hiddenProperties() {
        return (super.hiddenProperties() + ['firstName', 'middleName', 'lastName']) as Set
    }

    Set keyProperties() {
        return (super.keyProperties() + ['firstName', 'middleName', 'lastName']) as Set
    }

    boolean isAnIndividual() {
        return true
    }

    boolean isAPerson() {
        return true
    }

    String getName() {
        return toString()
    }

    List<Ref> getJobs() {
        return (List<Ref>) Query.execute(project, "findAllJobsOf", this.reference)
    }


    @Override
    String toString() {
        String s = ''
        if (firstName.trim()) s += firstName
        if (middleName.trim()) {
            if (s) s += ' '
            s += middleName
        }
        if (lastName.trim()) {
            if (s) s += ' '
            s += lastName
        }
        return s
    }

    // Queries

    // end queries


}