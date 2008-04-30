package com.mindalliance.channels.playbook.ifm.project.resources

import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 11:19:24 AM
*/
class Person extends Resource {

    String firstName = ''
    String middleName = ''
    String lastName = ''
    List<Ref> positions = []

    void beforeStore() {
        super.beforeStore()
    }


    String getName() {
        return toString()
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

    List<Ref> findOrganizationsWithOtherPositions() {  // that are not yet assigned to this Person
        Ref project = Project.current()
        List<Ref> orgs = project.resources.findAll {res ->
             def dres =  res.deref()
             def list = (res.type == 'Organization') ? res.findPositions() : []
             res.type == 'Organization' && res.findPositions().any {position -> !this.positions.contains(position)}
        }
        return orgs.sort{a,b -> a.toString().compareTo(b.toString())}
    }

    List<String> findOtherPositionNamesInOrganizationNamed(String orgName) { // leaving out those of positions this person has
        Ref org = Project.current().findResourceNamed("Organization", orgName)
        List<Ref> list = org.findPositions().findAll {position ->
            position.organization.name == orgName  && !this.positions.contains(position)
        }
        List<String> names = list.collect {position -> position.name}
        return names.sort()
    }


}