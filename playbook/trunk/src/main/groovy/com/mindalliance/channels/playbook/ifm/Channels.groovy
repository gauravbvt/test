package com.mindalliance.channels.playbook.ifm

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.Project
import com.mindalliance.channels.playbook.ifm.model.PlaybookModel
import org.apache.wicket.Application
import com.mindalliance.channels.playbook.support.PlaybookApplication
import com.mindalliance.channels.playbook.ifm.model.PlaybookModel
import org.apache.commons.collections.bag.TreeBag
import com.mindalliance.channels.playbook.support.util.CountedSet
import com.mindalliance.channels.playbook.support.util.CountedSet
import com.mindalliance.channels.playbook.ifm.playbook.SharingCommitment

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

    // Queries

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

    List<String> findAllOtherTypeNames(Ref elementType) {
        List<Ref> allTypes = findAllTypes(elementType.type)
        List<String> otherNames = []
        allTypes.each {type ->
            if (type != elementType) otherNames.add(type.name)
        }
        return otherNames
    }

    List<String> findAllRelationshipNames() {  // TODO write a VocabularyManager to keep vocabulary usage count from everywhere
        CountedSet countedSet = new CountedSet();
        projects.each { p ->
            p.relationships.each {rel -> if (rel.name) countedSet.add(rel.name)}
            // In playbooks
            p.playbooks.each {playbook ->
                playbook.agents.each {agent -> countedSet.addAll(agent.relationshipNames)}
                playbook.informationActs.each {act ->
                    if (act.type == "Association") {
                        countedSet.add(act.relationshipName)
                    }
                }
            }
            // In policies
            p.policies.each {pol ->
               countedSet.addAll(pol.relationshipNames)
            }
        }
        // TODO -- Missing all usages of relationship names in InformationTemplate.eventSpec.relationshipNames
        return countedSet.toList()
    }

    List<String> findAllPurposes() {
        CountedSet countedSet = new CountedSet();
        projects.each {p ->
            p.policies.each {pol -> countedSet.addAll(pol.purposes)}
            p.sharingAgreements.each {agr -> countedSet.addAll(agr.sharingConstraints.allowedPurposes)}
            p.playbooks.each {playbook ->
                playbook.informationActs.each {act ->
                    if (act instanceof SharingCommitment) {
                        countedSet.addAll(act.sharingConstraints.allowedPurposes)
                    }
                }
            }
        }
        models.each {m ->
            m.taskTypes.each {tt -> if (tt.purpose) countedSet.add(tt.purpose)}
        }
        return countedSet.toList()
      }

    // end queries

    static List<Class<?>> adminClasses() {
        [ User.class ]
    }

    static List<Class<?>> contentClasses() {
        [ User.class, Project.class, PlaybookModel.class ]
    }
}