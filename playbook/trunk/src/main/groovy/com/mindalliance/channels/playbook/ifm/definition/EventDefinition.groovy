package com.mindalliance.channels.playbook.ifm.definition

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.info.Information
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.playbook.Event

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 19, 2008
 * Time: 5:34:24 PM
 */
class EventDefinition extends Definition {

    LocationDefinition locationDefinition = new LocationDefinition()
    List<EventSpecification> causeEventSpecs = []  // ORed

    Class<? extends Bean> getMatchingDomainClass() {
        return Event.class
    }

    boolean matchesAll() {
        return locationDefinition.matchesAll() && !causeEventSpecs
    }

    MatchResult match(Bean bean, InformationAct informationAct) {
        Information info = (Information)bean
        if (!locationDefinition.matches(info.event.deref(), informationAct)) {
            return new MatchResult(matched:false, failures: ["The location of the event in $info does not match"])
        }
        if (causeEventSpecs) {
            Ref trigger = info.event.cause.trigger
            if (trigger as boolean && !causeEventSpecs.any {ces -> ces.matches((Event)trigger.deref(), informationAct)}) {
                return new MatchResult(matched:false, failures: ["The cause of the event in $info does not match"])
            }
        }
        return new MatchResult(matched:true)
    }

    MatchResult fullMatch(Bean bean, InformationAct informationAct) {
        return null;  // TODO
    }

    boolean narrows(MatchingDomain matchingDomain) {
        return false;  // TODO
    }

}