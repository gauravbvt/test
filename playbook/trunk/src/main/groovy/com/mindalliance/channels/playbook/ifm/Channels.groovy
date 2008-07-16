package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.ifm.model.PlaybookModel
import com.mindalliance.channels.playbook.support.PlaybookApplication
import com.mindalliance.channels.playbook.ifm.playbook.Event

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
    protected List<String> transientProperties() {
        return (List<String>) (super.transientProperties() + ['allItems'])
    }

    static Channels instance() {
        return (Channels) PlaybookApplication.current().getChannels().deref()
    }

    static Ref reference() {   // CAUTION: this will *not* force an initial load of Channels root data
       PlaybookApplication.current().getRoot()
    }

    // Queries

    List<Ref> findAllImpliedTypes(String typeType) {
        List<Ref> types = []
        switch (typeType) {
            case 'EventType': types.add(Event.impliedEventType()); break
        }
        return types
    }

    Ref findProjectNamed(String name) {
        Ref ref = (Ref) projects.find {project -> project as boolean && project.name == name}
        return ref
    }

    Ref findModelNamed(String name) {
        Ref ref = (Ref) models.find {model -> model as boolean && model.name == name}
        return ref
    }

    Ref findUser(String uid) {
        return (Ref) users.find {user -> user as boolean && user.userId == uid}
    }

    Ref findModel(String mid) {
        return (Ref) models.find {model -> model as boolean && model.id == mid}
    }
    
    List<Ref> findProjectsForUser(Ref user) {
        List<Ref> result = []
        projects.each {project ->
            if (project as boolean && project.isParticipant(user)) result.add(project)
        }
        return result
    }

    List<Ref> findUsersNotInProject(Ref project) {
        List<Ref> results
        results = users.findAll {user ->
            user as boolean &&
                    project.participations.every {part -> part as boolean && part.user != user}
        }
        return results
    }

    List<Ref> findModelsForUser(Ref user) {
        List<Ref> result = []
        if (user.analyst)
            models.each {model ->
                if (model as boolean && model.isAnalyst(user)) result.add(model)
            }
        return result
    }

    List<Ref> findAllTypes(String typeType) {
        List<Ref> types = []
        types.addAll(findAllImpliedTypes(typeType))
        models.each {model ->
            if (model as boolean) types.addAll(model.findAllTypes(typeType))
        }
        return types
    }

    List<String> findAllOtherTypeNames(Ref elementType) {
        List<Ref> allTypes = findAllTypes(elementType.type)
        List<String> otherNames = []
        allTypes.each {type ->
            if (type as boolean && type != elementType) otherNames.add(type.name)
        }
        return otherNames
    }

    List<String> findAllPurposes() {
        return [] // TODO - return empty, else inter-project privacy breach
    }

    List<Ref> findAllProjectsOfUser(Ref user) {
        return (List<Ref>)projects.findAll{project -> project.participations.any{part -> part.user == user}}
    }

    // end queries

    static List<Class<?>> adminClasses() {
        [User.class]
    }

    static List<Class<?>> contentClasses() {
        [User.class, Project.class, PlaybookModel.class]
    }

    static boolean isSet(Ref ref) {  // always called in application scope
        return ref != null && (ref.isComputed() || ref.isAttached() || PlaybookApplication.current().isStored(ref))
    }
}