package com.mindalliance.channels.playbook.ifm.context.environment

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Location
import com.mindalliance.channels.playbook.ifm.project.Project

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 21, 2008
* Time: 12:10:42 PM
*/
class Organization extends Resource {

    Location address = new Location() // not a Ref because not an independent element (is a component of the Organization)
    Ref parent
    // List<Ref> positions = []
    List<Ref> systems = []
    Location jurisdiction = new Location()
    List<Ref> organizationTypes = []

    void beforeStore() {
        address.detach()
        jurisdiction.detach()
    }

    List<Ref>findPositions() {
        Project.currentProject().resources.findAll {res ->
            res.type == 'Position' && res.organization == this.reference
        }
    }

    Ref findPositionNamed(String name) {
        return (Ref)findPositions().find {position -> position.name.equalsIgnoreCase(name)}
    }

    List<String> findJurisdictionTypesOfOrganizationTypesInDomainNamed(String domainName) {
        // Find all OrgTypes in a given domain
        // Collect all jurisdiction types
        Ref project = Project.currentProject()
        Set<String> jurTypes = new HashSet<String>()
        project.modelElements.each { me->
            if (me.type == 'OrganizationType') {
               if (me.domain.name.equalsIgnoreCase(domainName)) jurTypes.add(me.jurisdictionType)
            }
        }
        return (jurTypes as List<String>).sort()
    }

    List<String>findNamesOfOrganizationTypesInNamedDomainAndOfJurisdictionType(String domainName, String jurType) {
        Ref project = Project.currentProject()
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
        Ref project = Project.currentProject()
        return project.resources.findAll {res ->
            res.type == 'Organization'
        }
    }

    static List<String> findAllOrganizationNames() {
        return findAllOrganizations().collect {org -> org.name}
    }

}