package com.mindalliance.channels.playbook.ifm.project.environment

import com.mindalliance.channels.playbook.ifm.spec.InformationSpec
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.project.ProjectElement
import com.mindalliance.channels.playbook.ifm.Described
import com.mindalliance.channels.playbook.ifm.spec.AgentSpec
import com.mindalliance.channels.playbook.ifm.spec.ResourceSpec
import com.mindalliance.channels.playbook.ifm.Described
import com.mindalliance.channels.playbook.ifm.Named

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 12:38:52 PM
*/
class Policy extends ProjectElement implements Named, Described {

    static final public List<String> edictKinds = ['forbidden', 'required', 'restricted']

    String name = ''
    String description = ''
    boolean effective = false // whether the policy is in place in the real world
    String edict =  'forbidden' // either interdicts or obligates
    ResourceSpec sourceSpec = new ResourceSpec()
    ResourceSpec recipientSpec = new ResourceSpec()
    List<String> relationshipNames = [] // relationships from source to recipient (ORed)
    InformationSpec informationSpec = new InformationSpec() // specification of information (not) to be shared -- required
    List<String> purposes = [] // constrained (interdicted|obligation-causing) usages of the information
    List<Ref> mediumTypes = [] // what types of communication media must (or must not) be used

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['edictKinds','forbidden','restricted','required'])
    }

    static List<String> getEdictKinds() {
        return  edictKinds
    }

    boolean isForbidden() {
        return edict == 'forbidden'
    }

    boolean isRestricted() {
         return edict == 'restricted'
     }

    boolean isRequired() {
         return edict == 'required'
     }

/*
    String partiesMeaning() {
        String meaning = "The policy applies to any source such that  ${sourceSpec.toString()} "
        meaning += " that ${relationshipsSummary()} with any recipient such that ${recipientSpec.toString()}."
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