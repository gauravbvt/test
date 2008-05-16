package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Describable
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.project.ProjectElement
import com.mindalliance.channels.playbook.support.RefUtils
import com.mindalliance.channels.playbook.support.util.CountedSet

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 1:26:15 PM
*/
class Playbook extends ProjectElement implements Describable {

    String name = ''
    String description = ''
    List<Ref> teams = []
    List<Ref> groups = []
    List<Ref> events = []
    List<Ref> informationActs = []

    void addElement(PlaybookElement element) {
         doAddToField("informationActs", element)
     }


    Referenceable doAddToField( String field, Object object ) {
        object.playbook = this.reference
        super.doAddToField("informationActs", object )
    }

    public Referenceable doRemoveFromField(String name, Object val) {
        return super.doRemoveFromField("informationActs", val);
    }

    // Queries

    List<Ref> findAllTypes(String typeType) {
        return this.project.findAllTypes(typeType)
    }

    List<String> findAllOtherTypeNames(Ref elementType) {
         return this.project.findAllOtherTypeNames(elementType)
    }

    List<Ref> findCandidateCauses(Ref causable) {
         List<Ref> candidates = informationActs.findAll { act ->
              !act.isAfter(causable)
         }
        candidates.addAll(events.findAll {event ->
            !event.isAfter(causable)
        })
        return candidates
    }

    List<Ref> findAllAgents() {
        List<Ref> agents = getProject().findAllResources()
        agents.addAll(teams)
        agents.addAll(groups)
        return agents
    }

    List<Ref> findAllAgentsExcept(def holder, String propPath) {
        List<Ref> agents = findAllAgents()
        Ref agent = RefUtils.get(holder, propPath)
        agents.remove(agent)
        return agents
    }

    List<Ref> findAllInformationActs(String type) {
        return informationActs.findAll {act -> act.type == type}
    }

    List<String> findAllEventNames() {
        return events.collect {event -> event.name}
    }


    // end queries

    /**
     * Return classes a project participant can add.
     */
    static List<Class<?>> contentClasses() {
        [ Assignation.class, Confirmation.class, Denial.class,
          InformationTransfer.class, Detection.class, InformationRequest.class,
          Task.class, Verification.class
        ]
    }

    void addContents( List<Ref> results ) {
        results.addAll( informationActs )
    }
}