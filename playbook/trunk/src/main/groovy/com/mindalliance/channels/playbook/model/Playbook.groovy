package com.mindalliance.channels.playbook.model

/**
 * ...
 */
class Playbook {
    Agent agent
    Scenario scenario

    Playbook( Agent agent, Scenario scenario ) {
        this.agent = agent
        this.scenario = scenario
    }
}