package com.mindalliance.channels.playbook.graph.svg

import com.mindalliance.channels.playbook.graph.svg.SVGTransform

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 6, 2008
 * Time: 9:51:00 AM
 */
class SVGTransformation implements Serializable {

    List<SVGTransform> transforms = []

    void appendTransform(SVGTransform transform) {
        if (transform.isClear()) {
            clear()
        }
        else {
            transforms.add(transform)
        }
    }

    void clear() {
        transforms = []
    }

    String toString() {
        StringBuilder sb = new StringBuilder()
        transforms.each {xform ->
            sb.append(xform.toString())
            sb.append(' ')
        }
        return sb.toString()
    }

}