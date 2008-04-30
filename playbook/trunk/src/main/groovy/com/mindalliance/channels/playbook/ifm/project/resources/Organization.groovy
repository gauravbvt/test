package com.mindalliance.channels.playbook.ifm.project.resources

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.info.Location
import com.mindalliance.channels.playbook.ifm.project.Project

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 11:24:14 AM
*/
class Organization extends Resource {

    Ref parent
    List<Ref> positions = []
    List<Ref> systems = []
    Location jurisdiction
    List<Ref> organizationTypes = []

    void beforeStore() {
        super.beforeStore()
        if (jurisdiction) jurisdiction.detach()
    }

    List<Ref>findPositions() {
        Project.current().resources.findAll {res ->
            res.type == 'Position' && res.organization == this.reference
        }
    }

    Ref findPositionNamed(String name) {
        return (Ref)findPositions().find {position -> position.name.equalsIgnoreCase(name)}
    }

    List<String> findJurisdictionTypesOfOrganizationTypesInDomainNamed(String domainName) {
        // Find all OrgTypes in a given domain
        // Collect all jurisdiction types
        Ref project = Project.current()
        Set<String> jurTypes = new HashSet<String>()
        project.modelElements.each { me->
            if (me.type == 'OrganizationType') {
               if (me.domain.name.equalsIgnoreCase(domainName)) jurTypes.add(me.jurisdictionType)
            }
        }
        return (jurTypes as List<String>).sort()
    }

    List<String>findNamesOfOrganizationTypesInNamedDomainAndOfJurisdictionType(String domainName, String jurType) {
        Ref project = Project.current()
        Set<String> names = new HashSet<String>()
        project.modelElements.each { me->
            if (me.type == 'OrganizationType') {
               if (me.domain.name.equalsIgnoreCase(domainName) && me.jurisdictionType == jurType) {
                   names.add(me.name)
               }
            }
        }
        return (names as List<String>).sort()
    }

    static List<Ref> findAllOrganizations() {
        Ref project = Project.current()
        return project.resources.findAll {res ->
            res.type == 'Organization'
        }
    }

    static List<String> findAllOrganizationNames() {
        return findAllOrganizations().collect {org -> org.name}
    }


}