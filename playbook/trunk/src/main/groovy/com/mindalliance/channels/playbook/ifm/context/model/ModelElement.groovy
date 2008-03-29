package com.mindalliance.channels.playbook.ifm.context.model

import com.mindalliance.channels.playbook.ifm.IfmElement

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 29, 2008
* Time: 7:54:22 AM
*/
class ModelElement extends IfmElement {

    String name = ''
    String description = ''
    List<String> documents = []   // list of String describing documentation (may contain URLs in the text)

}