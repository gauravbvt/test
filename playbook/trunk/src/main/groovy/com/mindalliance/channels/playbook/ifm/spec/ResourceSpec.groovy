package com.mindalliance.channels.playbook.ifm.spec

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 27, 2008
 * Time: 9:41:28 PM
 */
class ResourceSpec extends BeanImpl implements Spec {

    protected List<Ref> roles = [] // agent plays any of these roles, or any role at all if empty list
    protected List<Ref> organizationTypes = [] // agent is or is on any of these organization types, or any at all if empty list

    @Override
    List<String> transientProperties() {
        return (List<String>) (super.transientProperties() + ['defined'])
    }

    public boolean isDefined() {
        return !roles.isEmpty() || !organizationTypes.isEmpty()
    }

    public boolean matches(Ref element) {
        return false;  // TODO
    }

    public boolean narrows(Spec spec) {
        return false;  // TODO
    }

    protected String orgTypesSummary() {
        String summary
        if (organizationTypes.isEmpty()) {
            summary = "of any type"
        }
        else {
            summary = "classified as "
            organizationTypes.each {type -> summary += "${type.name} or "}
            summary = summary.substring(0, summary.size()- 4)
        }
        return summary
    }

}