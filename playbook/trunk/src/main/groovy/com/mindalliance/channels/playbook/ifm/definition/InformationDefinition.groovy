package com.mindalliance.channels.playbook.ifm.definition

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.info.ElementOfInformation
import com.mindalliance.channels.playbook.ifm.info.Information
import com.mindalliance.channels.playbook.ifm.Agent
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 20, 2008
 * Time: 3:18:36 PM
 */
class InformationDefinition extends Definition {

    EventSpecification eventSpec = new EventSpecification()   // what the information must be about
    List<Ref> eventTypes = []  // ANDed -- classification
    List<ElementOfInformation> elementsOfInformation = []  // ORed - what element the information must contain, if any
    AgentSpecification sourceAgentSpec = new AgentSpecification() // trusted source (of any if spec matches all)

    Class<? extends Bean> getMatchingDomainClass() {
        return Information.class
    }

    boolean matchesAll() {
        return !elementsOfInformation && sourceAgentSpec.matchesAll() && eventSpec.matchesAll()
    }

    MatchResult match(Bean bean, InformationAct informationAct) {
        Information info = (Information)bean
        if (eventTypes && !eventTypes.every {set-> info.eventTypes.any {iet -> iet.implies(set)}}) {
            return new MatchResult(matched:false, failures:["The classification of the event in $info does not match"])
        }
        if (info.event as boolean && !eventSpec.matches(info.event.deref(), informationAct)) {
            return new MatchResult(matched:false, failures:["$info is not about a specified event"])
        }
        if (!sourceAgentSpec.matchesAll() && !info.sourceAgents.any {sa -> sourceAgentSpec.matches((Agent)sa.deref(), informationAct)}) {
            return new MatchResult(matched:false, failures:["Not one of $info sources matches specified source"])
        }
        if (elementsOfInformation && !elementsOfInformation.any {seoi -> info.eventDetails.any{eoi -> seoi.matches(eoi)}}) {
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