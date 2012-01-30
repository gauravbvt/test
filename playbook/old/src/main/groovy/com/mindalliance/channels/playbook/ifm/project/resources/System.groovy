package com.mindalliance.channels.playbook.ifm.project.resources

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.query.Query

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 11:28:47 AM
*/
class System extends OrganizationResource implements Individual {

    Ref adminPosition
    String instructions = '' // access instructions

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['jobs'])
    }

    Set hiddenProperties() {
        return (super.hiddenProperties() + ['instructions']) as Set
    }

    boolean isAnIndividual() {
        return true
    }

    boolean isASystem() {
        return true
    }

    List<Ref> getJobs() {
        return (List<Ref>)Query.execute(project, "findAllJobsOf", this.reference)
    }
}