package com.mindalliance.channels.playbook.graph.svg

import com.mindalliance.channels.playbook.graph.svg.SVGTransform

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 6, 2008
 * Time: 1:40:31 PM
 */
class SVGClear extends SVGTransform {

    boolean isClear() {
        return true
    }

    String toString() {
        return "clear"
    }

}