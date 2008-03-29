package com.mindalliance.channels.playbook.ifm.context.model

import com.mindalliance.channels.playbook.ref.Ref
import com.mindalliance.channels.playbook.ifm.Location

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 8:58:23 AM
*/
class OrganizationType extends ModelElement {

    List<Ref> extendedTypes = []
    Location within = new Location() // the parent area e.g. New Jersey
    String jurisdictionType // an Area type, one of Area.areaTypeNames() , e.g. 'County'
    List<Ref> roles = [] // roles each expected to be played by at least one Position in an Organization of this type 

}