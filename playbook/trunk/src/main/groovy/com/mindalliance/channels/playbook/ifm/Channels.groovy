package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.ifm.context.model.Model

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
    List<Ref> models = []

    @Override
    protected List transientProperties() {
        return super.transientProperties() + ['allItems']
    }

    Ref findProjectNamed(String name) {
        Ref ref = (Ref) projects.find {it.name == name}
        return ref
    }

    Ref findModelNamed(String name) {
        Ref ref = (Ref) models.find {it.name == name}
        return ref
    }

    Ref findUser(String id) {
        return (Ref) users.find{it.id == id}
    }

    Ref findModel(String id) {
        return (Ref) models.find{it.id == id}
    }

    public List<Ref> findProjectsForUser(Ref user) {
        List<Ref> result = []
        projects.each {
            if (it.isParticipant(user)) result.add(it)
        }
        return result;
    }

    public List<Ref> findModelsForUser(Ref user) {
        List<Ref> result = []
        if ( user.analyst )
            models.each {
                if (it.isAnalyst(user)) result.add(it)
            }
        return result;
    }

    // Queries

    List<Ref> findNamedResource(Map args) {  // args = [type:<type>, name:<name>]
        List<Ref> results = []
        Ref project = Project.currentProject()
        Ref resource = project.findResourceNamed(args.type, args.name)
        results.add(resource)
        return results
    }

    List<Ref> findAResource(Map args) {  // args = [type:<type>, name:<name>]
        List<Ref> results = []
        Ref project = Project.currentProject()
        Ref resource = project.findAResource(args.type)
        results.add(resource)
        return results
    }

    static List<Class<?>> adminClasses() {
        [ User.class ]
    }

    static List<Class<?>> contentClasses() {
        [ User.class, Project.class, Model.class ]
    }
}