package com.mindalliance.channels.playbook.ifm.context.model

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Location
import com.mindalliance.channels.playbook.ifm.project.Project

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 8:58:23 AM
*/
class OrganizationType extends ModelElement {

    List<Ref> extendedTypes = []
    Ref domain // -- required
    Location within = new Location() // the parent area e.g. New Jersey
    String jurisdictionType // an Area type, one of Area.areaTypeNames() , e.g. 'County'  -- REQUIRED
    List<Ref> roles = [] // roles each expected to be played by at least one Position in an Organization of this type 

    // Find all domain names of known Organization Types
    static List<String> findDomainNames() {
        Ref project = Project.currentProject()
        Set<String> domainNames = new HashSet<String>()
        project.modelElements.each{ me ->
            if (me.type == "OrganizationType") {
                if (me.domain && me.domain.name) domainNames.add(me.domain.name)
            }
        }
        return (domainNames as List<String>).sort()
    }

    static Ref findOrganizationTypeNamed(String name) {
        Ref project = Project.currentProject()
        Ref res = (Ref) project.modelElements.find {me ->
            me.type == 'OrganizationType' && me.name.equalsIgnoreCase(name)
        }
        return res
    }

}