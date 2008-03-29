package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.impl.BeanImpl

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 9:06:25 AM
*/
class InformationSpecification extends BeanImpl {

    String about // description of the subject
    String source // description of suitable sources
    List<ElementOfInformation> elementOfInformations = []    // as specifications
}