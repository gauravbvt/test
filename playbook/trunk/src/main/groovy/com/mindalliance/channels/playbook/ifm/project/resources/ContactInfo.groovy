package com.mindalliance.channels.playbook.ifm.project.resources

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Apr 17, 2008
* Time: 11:18:04 AM
*/
class ContactInfo extends BeanImpl {

    public static List<String> getMedia() {
        return ['email', 'phone', 'fax', 'web', 'messaging', 'cell', 'pager', 'radio']
    }
    
    Ref mediumType // one of MEDIA
    String endPoint = ''


}