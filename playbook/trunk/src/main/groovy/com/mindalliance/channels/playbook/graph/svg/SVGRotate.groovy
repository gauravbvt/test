package com.mindalliance.channels.playbook.graph.svg
/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 7, 2008
 * Time: 12:10:07 PM
 */
class SVGRotate extends SVGTransform {

    double angle

    boolean isRotate() {
        return false
    }

    SVGTranslate combineWith(SVGTransform xform) {
        if (xform.isRotate()) {
            double newAngle = xform.angle + angle
            if (newAngle >= 360) newAngle = newAngle - 360
            return new SVGRotate(angle: newAngle)
        }
        else {
            throw new IllegalArgumentException("Can't combine svg rotate with $xform")
        }
    }

    String toString() {
        return "rotate($angle)"
    }

    boolean isIdentity() {
        return angle == 0.0
    }

}