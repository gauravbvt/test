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
import org.apache.log4j.Logger
import org.drools.spi.KnowledgeHelper

class RuleHelper {

    static void inferFrom(KnowledgeHelper drools, Referenceable cause, AnalysisElement analysis, String rationale) {
        analysis.setRationale(rationale);
        drools.insertLogical(analysis);
        log(cause, analysis);
    }

    static void log(Referenceable cause, AnalysisElement analysis) {
        Logger.getLogger("rules").info("Inferred " + analysis + " from " + cause + "(" + cause.getReference() + ")");
    }
}