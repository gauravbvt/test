package com.mindalliance.channels.playbook.ifm.definition
/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 19, 2008
 * Time: 9:36:55 AM
 */
class MatchResult {

    boolean matched = false
    List<String> failures = []
    List<String> successes = []

    String toString() {
        String s = matched ? "MATCH\n" : "NO MATCH\n"
        s += "\tSuccesses:\n"
        successes.each { s += "\t\t$it\n" }
        s += "\tFailures:\n"
        failures.each { s += "\t\t$it\n" }
        return s
    }

}