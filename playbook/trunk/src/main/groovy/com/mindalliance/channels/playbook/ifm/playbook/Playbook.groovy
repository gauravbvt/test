package com.mindalliance.channels.playbook.ifm.playbook

import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Describable
import com.mindalliance.channels.playbook.ref.Referenceable
import com.mindalliance.channels.playbook.ifm.project.ProjectElement

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

    List<Ref> findCandidateCauses(Ref infoAct) {
         List<Ref> candidates = informationActs.findAll { act ->
              !act.isAfter(infoAct)
         }
    }

    // end queries

    /**
     * Return classes a project participant can add.
     */
    static List<Class<?>> contentClasses() {
        [ Assignation.class, Confirmation.class, Denial.class,
          InformationTransfer.class, Observation.class, InformationRequest.class,
          Task.class, Verification.class
        ]
    }

    void addContents( List<Ref> results ) {
        results.addAll( informationActs )
    }
}