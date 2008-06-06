package com.mindalliance.channels.playbook.graph.svg

import com.mindalliance.channels.playbook.graph.svg.SVGClear
import com.mindalliance.channels.playbook.graph.svg.SVGScale
import com.mindalliance.channels.playbook.graph.svg.SVGTranslate

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 6, 2008
 * Time: 1:12:15 PM
 */
/*abstract*/ class SVGTransform {

    static SVGTransform fromString(String s) {
        if (s == 'clear') return new SVGClear()
        String name = s.substring(0,s.indexOf('('))
        String[] args = s.substring(s.indexOf('(')+1,s.indexOf(')')).split(',')
        switch (name) {
            case 'scale':
                return new SVGScale(x:args[0],y:args[1])
            case 'translate':
                return new SVGTranslate(x:args[0],y:args[1])
        }
    }

    String toString() {
       throw new Exception("Not allowed")
    }

    boolean isScale() {
        return false
    }

    boolean isTranslate() {
        return false
    }

    boolean isClear() {
        return false
    }

    SVGTransform combineWith(SVGTransform transform) {
        throw new Exception("Not allowed")
    }

}
