package com.mindalliance.channels.playbook.ifm.info

import com.mindalliance.channels.playbook.ref.Ref

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 25, 2008
 * Time: 12:38:03 PM
 */
interface Spec extends Defineable {

    boolean matches(Ref element)
    boolean narrows(Spec spec)

}