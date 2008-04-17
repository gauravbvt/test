package com.mindalliance.channels.playbook.ifm.model

import com.mindalliance.channels.playbook.ifm.IfmElement

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 12:48:16 PM
*/
class ElementType extends IfmElement {

    String name = ''           // -- required
    String description = ''
    List<? extends ElementType> extendedTypes  = []

}