package com.mindalliance.channels.playbook.ifm.environment

import com.mindalliance.channels.playbook.ifm.info.InformationTemplate
import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.IfmElement

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 12:38:52 PM
*/
class Policy extends IfmElement {

    String name = ''
    String description = ''
    Ref sourceOrganizationType// -- required
    Ref recipientOrganizationType // -- required
    Ref relationshipType // relationship from source to recipient
    String edict = 'interdiction' // one of {interdiction, obligation}
    InformationTemplate informationShared // specification of information (not) to be shared -- required
    Ref purposeType // what the shared information would be used for
    String conditions // details re. applicability -- requires human interpretation to validate applicability
    boolean effective = false // whether the policy is in place in the real world

}