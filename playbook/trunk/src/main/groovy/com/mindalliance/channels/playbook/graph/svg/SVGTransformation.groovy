package com.mindalliance.channels.playbook.graph.svg

import com.mindalliance.channels.playbook.graph.svg.SVGTransform

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 6, 2008
 * Time: 9:51:00 AM
 */
class SVGTransformation {

    SVGScale scale = new SVGScale()
    SVGTranslate translate = new SVGTranslate()

    void appendTransform(SVGTransform transform) {
        switch (transform) {
            case {it.isScale()}: scale = scale.combineWith(transform); break
            case {it.isTranslate()}: translate = translate.combineWith(transform); break
            case {it.isClear()}: clear(); break;
            default: throw new IllegalArgumentException("Invalid transform $transform")
        }
    }

    void clear() {
        scale = new SVGScale()
        translate = new SVGTranslate()
    }

    String toString() {
        return "$scale $translate"
    }

}