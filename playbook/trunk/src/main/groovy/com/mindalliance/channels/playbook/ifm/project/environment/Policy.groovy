package com.mindalliance.channels.playbook.ifm.project.environment

import com.mindalliance.channels.playbook.ifm.info.InformationTemplate
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.IfmElement
import com.mindalliance.channels.playbook.ifm.project.ProjectElement
import com.mindalliance.channels.playbook.ifm.Describable

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
    List<Ref> sourceOrganizationTypes = [] // -- required   (ORed)
    List<Ref> recipientOrganizationTypes = [] // -- required (ORed)
    List<Ref> relationshipTypes // relationships from source to recipient (ORed)
    InformationTemplate informationShared // specification of information (not) to be shared -- required
    List<Ref> purposeTypes = [] // what the shared information would be used for

    @Override
    List<String> transientProperties() {
        return (List<String>)(super.transientProperties() + ['edictKinds'])
    }

}