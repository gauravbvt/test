package com.mindalliance.channels.playbook.graph.svg

import com.mindalliance.channels.playbook.graph.svg.SVGTransform

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 6, 2008
 * Time: 1:41:16 PM
 */
class SVGScale extends SVGTransform {

    double x = 1.0
    double y = 1.0

    boolean isScale() {
        return true
    }

    SVGScale combineWith(SVGTransform xform) {
        if (xform.isScale()) {
            return new SVGScale(x: xform.x * x, y: xform.y * y)
        }
        else {
            throw new IllegalArgumentException("Can't combine svg scale with $xform")
        }
    }

    String toString() {
        return "scale($x $y)"
    }

    boolean isIdentity() {
        return x == 1.0 && y == 1.0
    }

}