package com.mindalliance.channels.playbook.ifm.model

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.ifm.info.AreaInfo

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 12:49:52 PM
*/
class OrganizationType extends ElementType {

    Ref domain // domain for this type of organization
    AreaInfo within // containing area of the jurisdiction e.g. New Jersey -- required
    Ref jurisdictionType  // a AreaType -- jurisdiction over what types of locations

    // Find all domain names of known Organization Types
    static List<String> findDomainNames() {
        Ref project = Project.current()
        Set<String> domainNames = new HashSet<String>()
        project.modelElements.each{ me ->
            if (me.type == "OrganizationType") {
                if (me.domain && me.domain.name) domainNames.add(me.domain.name)
            }
        }
        return (domainNames as List<String>).sort()
    }

    static Ref findOrganizationTypeNamed(String name) {
        Ref project = Project.current()
        Ref res = (Ref) project.modelElements.find {me ->
            me.type == 'OrganizationType' && me.name.equalsIgnoreCase(name)
        }
        return res
    }

}