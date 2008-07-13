package com.mindalliance.channels.playbook.ifm.definition

import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.ifm.playbook.Task
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Timing
import com.mindalliance.channels.playbook.matching.SemanticMatcher
import com.mindalliance.channels.playbook.support.Level

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 19, 2008
 * Time: 8:56:10 PM
 */
class TaskDefinition extends Definition {

    List<Ref> taskTypes = [] // ANDed -- classification
    List<String> specificPurposes = [] // ORed -- specific purposes (other than from classification)
    Timing responseTiming = new Timing(amount:0)

    Class<? extends Bean> getMatchingDomainClass() {
        return Task.class
    }

    boolean matchesAll() {
        return !taskTypes && !responseTiming.amount
    }

    MatchResult match(Bean bean, InformationAct informationAct) {
        Task task = (Task)bean
        if (!taskTypes.every {stt -> task.taskTypes.any {tt -> tt.implies(stt)}}) {
            return new MatchResult(matched:false, failures:["$task is not of the specified types"])
        }
        if (responseTiming.amount && responseTiming.duration < task.cause.delay.duration) {
            return new MatchResult(matched:false, failures:["$task does not meet minimum, specified response time"])
        }
        if (specificPurposes) {
            SemanticMatcher semMatcher = SemanticMatcher.getInstance()
            if (!specificPurposes.any {ssp -> task.specificPurposes.any {sp -> semMatcher.semanticProximity(ssp, sp) > Level.MEDIUM}}) {
                return new MatchResult(matched:false, failures:["$task does not have a matching specific purpose"])
            }
        }
        return new MatchResult(matched:true)
    }

    MatchResult fullMatch(Bean bean, InformationAct informationAct) {
        return null;  // TODO
    }

    boolean implies(MatchingDomain matchingDomain) {
        TaskDefinition other = (TaskDefinition)matchingDomain
        if (other.matchesAll()) return true
        if (other.taskTypes && !other.taskTypes.every{ott -> taskTypes.any{tt-> tt.implies(ott)}}) return false
        if (other.specificPurposes && !other.specificPurposes.every{osp ->
                specificPurposes.any{sp -> SemanticMatcher.matches(sp, osp, Level.HIGH) }}) return false
        if (other.responseTiming.isDefined() && !responseTiming.isShorterOrEqualTo(other.responseTiming)) return false
        return true
    }

}