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

    List<Ref> eventTypes = []  // ANDed -- classification
    LocationDefinition locationDefinition = new LocationDefinition()
    List<EventSpecification> causeEventSpecifications = []  // ORed

    Class<? extends Bean> getMatchingDomainClass() {
        return Information.class
    }

    boolean matchesAll() {
        return !eventTypes && locationDefinition.matchesAll() && !causeEventSpecifications
    }

    MatchResult match(Bean bean, InformationAct informationAct) {
        Information info = (Information)bean
        if (!eventTypes.every {set-> info.eventTypes.any {iet -> iet.implies(set)}}) {
            return new MatchResult(matched:false, failures:["The classification of the event in $info does not match"])
        }
        if (!locationDefinition.matches(info.event.deref(), informationAct)) {
            return new MatchResult(matched:false, failures: ["The location of the event in $info does not match"])
        }
        if (causeEventSpecifications) {
            Ref trigger = info.event.cause.trigger
            if (trigger as boolean && !causeEventSpecifications.any {ces -> ces.matches((Event)trigger.deref(), informationAct)}) {
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