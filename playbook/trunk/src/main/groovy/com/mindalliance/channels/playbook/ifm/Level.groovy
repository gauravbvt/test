package com.mindalliance.channels.playbook.ifm
/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Mar 29, 2008
 * Time: 12:29:01 PM
 */
class Level implements Serializable {

    static int NONE = 0
    static int LOW = 1
    static int MEDIUM = 2
    static int HIGH = 3
    static int EXTREME = 4

    static Level LEVEL_NONE = new Level(NONE)
    static Level LEVEL_LOW = new Level(LOW)
    static Level LEVEL_MEDIUM = new Level(MEDIUM)
    static Level LEVEL_HIGH = new Level(HIGH)
    static Level LEVEL_EXTREME = new Level(EXTREME)


    private int value = 0

    Level(int value) {
        if ((0..4).contains(value)) this.value = value
        else throw new IllegalArgumentException("Invalid level value $value")
    }

    int getValue() {
        return value
    }

    String toString() {
        switch(value) {
            case NONE: 'None'; break
            case LOW: 'Low'; break
            case MEDIUM: 'Medium'; break
            case HIGH: 'High'; break
            case EXTREME: 'Extreme'; break
        }
    }

}