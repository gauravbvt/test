package com.mindalliance.channels.playbook.ifm.project.environment

import com.mindalliance.channels.playbook.ifm.info.InformationTemplate
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.ProjectElement
import com.mindalliance.channels.playbook.ifm.Describable
import com.mindalliance.channels.playbook.ifm.info.AgentSpec

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 12:38:52 PM
*/
class Policy extends ProjectElement implements Describable {

    static final public List<String> edictKinds = ['interdiction', 'obligation']

    String name = ''
    String description = ''
    boolean effective = false // whether the policy is in place in the real world
    String edict =  'interdiction' // either interdicts or obligates
    AgentSpec sourceAgentSpec = new AgentSpec()
    AgentSpec recipientAgentSpec = new AgentSpec()
    List<String> relationshipNames // relationships from source to recipient (ORed)
    InformationTemplate informationTemplate // specification of information (not) to be shared -- required
    List<String> purposes = [] // constrained (interdicted|obligation-causing) usages of the information
    List<Ref> mediumTypes = [] // what types of communication media must (or must not) be used

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['edictKinds'])
    }

    static List<String> getEdictKinds() {
        return  edictKinds
    }

/*
    String partiesMeaning() {
        String meaning = "The policy applies to any source such that  ${sourceAgentSpec.toString()} "
        meaning += " that ${relationshipsSummary()} with any recipient such that ${recipientAgentSpec.toString()}."
        return meaning
    }


    private String relationshipsSummary() {
        String summary
        if (orgTypes.isEmpty()) {
            summary = "has any kind of relationship"
        }
        else {
            summary = "is "
            relationshipNames.each {name -> summary += "$name or "}
            summary = summary.substring(0, summary.size()- 4)
        }
        return summary
    }
*/

}