package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.ifm.model.PlaybookModel
import org.apache.wicket.Application
import com.mindalliance.channels.playbook.support.PlaybookApplication
import com.mindalliance.channels.playbook.ifm.model.PlaybookModel

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

    static Channels instance() {
        return (Channels)PlaybookApplication.current().getChannels().deref()
    }

    Ref findEnvironmentNamed(String name) {
        Ref ref = (Ref) environments.find {it.name == name}
        return ref
    }

    Ref findProjectNamed(String name) {
        Ref ref = (Ref) projects.find {it.name == name}
        return ref
    }

    Ref findModelNamed(String name) {
        Ref ref = (Ref) models.find {it.name == name}
        return ref
    }

    Ref findUser(String uid) {
        return (Ref) users.find{it.userId == uid}
    }

    Ref findModel(String mid) {
        return (Ref) models.find{it.id == mid}
    }

    List<Ref> findProjectsForUser(Ref user) {
        List<Ref> result = []
        projects.each {
            if (it.isParticipant(user)) result.add(it)
        }
        return result
    }

    List<Ref> findUsersNotInProject(Ref project) {
        List<Ref> results
        results = users.findAll {user ->
            project.participations.every {it.user != user}
        }
        return results
    }

    List<Ref> findModelsForUser(Ref user) {
        List<Ref> result = []
        if ( user.analyst )
            models.each {
                if (it.isAnalyst(user)) result.add(it)
            }
        return result
    }

    List<Ref> findAllTypes(String typeType) {
        List<Ref> types = []
        models.each {model ->
            types.addAll(model.findAllTypes(typeType))
        }
        return types
    }

    static List<Class<?>> adminClasses() {
        [ User.class ]
    }

    static List<Class<?>> contentClasses() {
        [ User.class, Project.class, PlaybookModel.class ]
    }
}