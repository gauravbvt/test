package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.Reference

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 2:08:19 PM
*/
class Channels extends IfmElement {

    String about
    List<Reference> projects = []
    List<Reference> users = []
    List<Reference> participations = []

    Reference findProjectNamed(String name) {
        Reference ref = (Reference) projects.find {it.name == name}
        return ref
    }

    Reference findUserNamed(String name) {
        return (Reference) users.find(it.name == name)
    }

    public List<Reference> findProjectsForUser(Reference user) {
        List<Reference> result = []
        if (user.admin)
            result.addAll(projects);
        else {
            participations.each {
                if (it.user == user) result.add(it.project)
            }
        }
        return result;
    }

    public Reference findParticipation(Reference project, Reference user) {
        return participations.find(it.user == user && it.project == project)
    }

}