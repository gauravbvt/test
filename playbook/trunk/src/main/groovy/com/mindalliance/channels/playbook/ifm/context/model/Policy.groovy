package com.mindalliance.channels.playbook.ifm.context.model

import com.mindalliance.channels.playbook.ifm.Location

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 8:59:24 AM
*/
class Policy extends ModelElement {

    Location within = new Location() // the parent area e.g. New Jersey  -- required
    String jurisdictionType // an Area type, one of Area.areaTypeNames() , e.g. 'County' -- optional

}