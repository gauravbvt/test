package com.mindalliance.channels.playbook.ref

import com.mindalliance.channels.playbook.support.persistence.Mappable

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 26, 2008
 * Time: 6:55:19 AM
 */
interface Bean extends Serializable, Mappable {

   Bean copy()
   void detach()  // detach any field value that should or can not be serialized
   void setFrom(Bean bean)
   Map beanProperties()
   String shortClassName()
   String makeLabel(int maxWidth)
   String about() // user-friendly toString()
}