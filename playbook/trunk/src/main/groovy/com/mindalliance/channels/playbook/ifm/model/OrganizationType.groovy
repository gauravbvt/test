package com.mindalliance.channels.playbook.ifm.model

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.Project

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 12:49:52 PM
*/
class OrganizationType extends ElementType {

    Ref domain // domain for this type of organization

    // Queries

    // Find all domain names of known Organization Types
    static List<String> findDomainNames() {
        Ref project = Project.current()
        Set<String> domainNames = new HashSet<String>()
        project.organizationTypes.each{ me ->
                if (me.domain && me.domain.name) domainNames.add(me.domain.name)
        }
        return (domainNames as List<String>).sort()
    }

    static Ref findOrganizationTypeNamed(String name) {
        Ref project = Project.current()
        Ref res = (Ref) project.organizationTypes.find {me ->
            me.name.equalsIgnoreCase(name)
        }
        return res
    }

    // end queries

}