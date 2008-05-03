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

    Referenceable doAddToField( String field, Object object ) {
        object.playbook = this.reference
        super.doAddToField("informationActs", object )
    }

    public Referenceable doRemoveFromField(String name, Object val) {
        return super.doRemoveFromField("informationActs", val);
    }


    List<Ref> findInformationActsOfResource(Ref resource) {
        List<Ref> ias = informationActs.findall {ia ->
            (ia.actor == resource) || (ia.isFlowAct() && ia.targetResource == resource)
        }
    }

    /**
     * Return classes a project participant can add.
     */
    static List<Class<?>> contentClasses() {
        [ Assignation.class, Confirmation.class, Denial.class,
          InformationTransfer.class, Observation.class, Query.class,
          Task.class, Verification.class
        ]
    }

    void addContents( List<Ref> results ) {
        results.addAll( informationActs )
    }
}