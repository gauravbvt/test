package com.mindalliance.channels.core.nlp;

import java.io.Serializable;

/**
 * Semantic proximity.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 9, 2009
 * Time: 10:08:32 PM
 */
public enum Proximity implements Serializable {
        /**
         *  No match.
         */
        NONE,
        /**
         *  Low match.
         */
        LOW,
        /**
         * Medium match.
         */
        MEDIUM,
        /**
         * High match.
         */
        HIGH,
        /**
         * Very high match.
         */
        VERY_HIGH;

        /**
         * A string represneting the severity level
         *
         * @return a String
         */
        public String getLabel() {
            return this.toString();
        }

        /**
         * A sortable value
         *
         * @return an int
         */
        public int getOrdinal() {
            return this.ordinal();
        }
}
