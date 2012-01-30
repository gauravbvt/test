package com.mindalliance.channels.playbook.graph.svg

import com.mindalliance.channels.playbook.graph.svg.SVGTransform

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 6, 2008
 * Time: 1:41:56 PM
 */
class SVGTranslate extends SVGTransform {

    double x = 0.0
    double y = 0.0

    boolean isTranslate() {
        return true
    }

    SVGTranslate combineWith(SVGTransform xform) {
        if (xform.isTranslate()) {
            return new SVGTranslate(x: xform.x + x, y: xform.y + y)
        }
        else {
            throw new IllegalArgumentException("Can't combine svg translate with $xform")
        }
    }

    String toString() {
        return "translate($x $y)"
    }

    boolean isIdentity() {
        return x == 0.0 && y == 0.0
    }

}