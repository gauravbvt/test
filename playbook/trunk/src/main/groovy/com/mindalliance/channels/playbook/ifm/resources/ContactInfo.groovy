package com.mindalliance.channels.playbook.ifm.resources

import com.mindalliance.channels.playbook.ref.impl.BeanImpl

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
    
    String medium // one of MEDIA
    String endPoint = ''


}