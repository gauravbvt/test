package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 2:08:19 PM
*/
class Channels extends IfmElement {

    String about
    List<Ref> projects = []
    List<Ref> users = []
    List<Ref> participations = []

    Ref findProjectNamed(String name) {
        Ref ref = (Ref) projects.find {it.name == name}
        return ref
    }

    Ref findUser(String id) {
        return (Ref) users.find{it.id == id}
    }

    public List<Ref> findProjectsForUser(Ref user) {
        List<Ref> result = []
        if (user.admin)
            result.addAll(projects);
        else {
            participations.each {
                if (it.user == user) result.add(it.project)
            }
        }
        return result;
    }

    public Ref findParticipation(Ref project, Ref user) {
        return (Ref)participations.find() {it.user == user && it.project == project}
    }

    public List<Ref> getAll() {
        List<Ref> result = []
        result.addAll( projects )
        result.addAll( users )
        result.addAll( participations )

        return result
    }

}