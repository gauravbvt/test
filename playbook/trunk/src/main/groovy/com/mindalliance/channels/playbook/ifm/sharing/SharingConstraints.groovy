package com.mindalliance.channels.playbook.ifm.sharing

import com.mindalliance.channels.playbook.ref.impl.BeanImpl
import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 13, 2008
 * Time: 9:18:56 AM
 */
class SharingConstraints extends BeanImpl {

    List<String> allowedPurposes = []
    List<String> privateTopics = []

    String toString() {
        // TODO verify this
        switch( allowedPurposes.size() ) {
            case 0 : return "";
            case 1 : return allowedPurposes.get(0);
            default:
                StringBuffer b = new StringBuffer();
                b.print( allowedPurposes.get(0) );
                for ( int i=1; i < allowedPurposes.size(); i++ ) {
                    b.print( ", " );
                    b.print( allowedPurposes.get(i) );
                }
                return b.toString();
        }
    }
}