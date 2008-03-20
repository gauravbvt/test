package com.mindalliance.channels.playbook.ifm
/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 19, 2008
 * Time: 2:10:46 PM
 */
class Project extends IfmElement {

    String name
    List<Reference> scenarios

    void addScenario(Scenario scenario) {
        scenarios.add(scenario.reference)
        changed('scenarios')
    }

}