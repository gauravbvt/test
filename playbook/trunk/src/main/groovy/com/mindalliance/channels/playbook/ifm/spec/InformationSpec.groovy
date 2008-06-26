package com.mindalliance.channels.playbook.ifm.spec

import com.mindalliance.channels.playbook.ifm.info.InformationTemplate
import com.mindalliance.channels.playbook.ref.Bean
import com.mindalliance.channels.playbook.ifm.info.Information

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 27, 2008
 * Time: 10:02:39 PM
 */
class InformationSpec extends SpecImpl {

    InformationTemplate informationTemplate  = new InformationTemplate()
    ResourceSpec sourceSpec = new ResourceSpec() // source

    @Override
    List<String> transientProperties() {
        return super.transientProperties() + ['defined']
    }

    public boolean isDefined() {
        return eventSpec.isDefined()
    }

    public boolean doesMatch(Bean bean) {
        Information info = (Information)bean
        // about the specified event
        if (!informationTemplate.eventSpec.matches(info)) return false
        // info from specified sources
        if (!info.sourceAgents.any {agent -> sourceSpec.matches(agent.deref())}) return false
        // at least one of the specified EOIs matches at least one of the info's eoi
        if (!informationTemplate.eventDetails.any {seoi -> info.eventDetails.any {eoi -> eoi.matches(seoi)}}) return false
        return true
    }

    public boolean narrows(Spec spec) {
        return false;  //TODO
    }

}