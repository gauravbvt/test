package com.mindalliance.channels.playbook.support.drools
/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 14, 2008
 * Time: 12:01:17 PM
 */

import com.mindalliance.channels.playbook.analysis.*
import com.mindalliance.channels.playbook.ref.*
import org.slf4j.LoggerFactory
import org.drools.spi.KnowledgeHelper
import com.mindalliance.channels.playbook.ifm.playbook.InformationAct
import com.mindalliance.channels.playbook.analysis.profile.ProfileElement

class RuleHelper {

    static void inferFrom(KnowledgeHelper drools, Referenceable cause, AnalysisElement analysis, String rationale) {
        analysis.setRationale(rationale);
        drools.insertLogical(analysis);
        log(cause, analysis);
    }

    static void log(Referenceable cause, AnalysisElement analysis) {
        LoggerFactory.getLogger(RuleBaseSession.class).debug("Inferred " + analysis + " from " + cause + "(" + cause.getReference() + ")");
    }

    // does information act start during a profile element?
    static boolean startsDuring(InformationAct act, ProfileElement profileElement) {
        // ! $start.isLongerThan($act.startTime())  && ( $locality.isForever() || !$act.startTime().isLongerThan($end)
        if (profileElement.start.isLongerThan(act.startTime())) return false // profile element starts after act
        if (profileElement.isForever()) return true // act starts after profile element which lasts forever
        return !act.startTime().isLongerThan(profileElement.end) // act starts after profile element and before element ends
    }
}