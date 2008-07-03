package com.mindalliance.channels.playbook.ifm.definition

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.info.ElementOfInformation
import com.mindalliance.channels.playbook.ifm.info.Information

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 20, 2008
 * Time: 3:18:36 PM
 */
class InformationDefinition extends Definition {

    EventSpecification eventSpec = new EventSpecification()   // what the information must be about
    List<ElementOfInformation> elementsOfInformation = []  // ORed - what element the information must contain
    List<AgentSpecification> sourceAgentSpecs = [] // ORed -- what a source must be

    Class<? extends Bean> getMatchingDomainClass() {
        return Information.class
    }

    boolean matchesAll() {
        return !elementsOfInformation && !sourceAgentSpecs && eventSpec.matchesAll()
    }

    MatchResult match(Bean bean, InformationAct informationAct) {
        Information info = (Information)bean
        if (!eventSpec.matches(info.event, informationAct)) {
            return new MatchResult(matched:false, failures:["$info is not about a specified event"])
        }
        if (!sourceAgentSpecs.any {sas -> info.sourceAgents.any {sa -> sas.matches(sa.deref(), informationAct)}}) {
            return new MatchResult(matched:false, failures:["Not one specified sources matched by $info sources"])
        }
        if (!elementsOfInformation.any {seoi -> info.eventDetails.any{eoi -> seoi.matches(eoi)}}) {
            return new MatchResult(matched:false, failures:["Not one specified element of information matches one in $info"])
        }
        return new MatchResult(matched:true)
    }

    MatchResult fullMatch(Bean bean, InformationAct informationAct) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    boolean narrows(MatchingDomain matchingDomain) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

}