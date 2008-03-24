package com.mindalliance.channels.playbook.support

import org.apache.commons.beanutils.PropertyUtilsBean

/**
* Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
* Proprietary and Confidential.
* User: jf
* Date: Mar 24, 2008
* Time: 9:06:46 AM
*/
class RefPropertyUtilsBean extends PropertyUtilsBean {

    @Override
    def getSimpleProperty(def obj, String prop) {
      def result = obj."$prop"
      return result
    }

}