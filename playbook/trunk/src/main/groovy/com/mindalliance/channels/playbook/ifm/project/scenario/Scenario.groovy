package com.mindalliance.channels.playbook.ifm.project.scenario

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.ProjectElement

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 19, 2008
* Time: 2:11:36 PM
*/
class Scenario extends ProjectElement {

    String name = 'No name'  // Required, unique
    String description = ''
    List<Ref> agents = []
    List<Ref> occurrences = []
    List<Ref> informations = []
    List<Ref> informationNeeds = []
    List<Ref> assignments = []

    List<Ref> findInformationActsForAgent(String name) {
        List<Ref> ias = occurrences.findall {occ ->
            occ.isInformationAct() && ((occ.agent.name.equalsIgnoreCase(name)) || occ.target && occ.target.name.equalsIgnoreCase(name))
        }
    }

}