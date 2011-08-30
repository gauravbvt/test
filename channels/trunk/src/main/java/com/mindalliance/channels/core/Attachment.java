/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.core;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * An attachment.
 */
public interface Attachment extends Serializable, Comparable<Attachment> {

    /**
     * The specific kind of document.
     */
    public enum Type {

        /**
         * A miscellaneous document.
         */
        Reference( "Reference" ),
        /**
         * A related policy that does not specifically mandate nor prohibit.
         */
        Policy( "Policy" ),
        /**
         * A photo or icon.
         */
        Image( "Picture" ),
        /**
         * A policy document that mandates whatever the document is attached to.
         */
        PolicyMust( "Mandating policy" ),

        /**
         * A policy document that prohibits whatever the document is attached to.
         */
        PolicyCant( "Prohibiting policy" ),
        /**
         * A memorandum of understanding document.
         */
        MOU( "Memorandum of Understanding" ),
        /**
         * List of end-line or comma-separated tags to pre-load into a plan.
         */
        TAGS( "Tags" ),
        /**
         * Info standards.
         */
        InfoStandards( "Info standards" ),

        Help( "Help" );

        /**
         * A description of the type that will hopefully make sense to the user.
         */
        private String label;

        //-------------------------------
        Type( String label ) {
            this.label = label;
        }

        public String getStyle() {
            return MessageFormat.format( "doc_{0}", toString() );                         // NON-NLS
        }

        public String getLabel() {
            return label;
        }

    }

    /**
     * Get a label.
     *
     * @return a string
     */
    String getLabel();

    String getName();

    Type getType();

    String getUrl();

    boolean isHelp();

    boolean isInfoStandards();

    /**
     * Is MOU.
     *
     * @return a boolean
     */
    boolean isMOU();

    /**
     * Is a mandating policy.
     *
     * @return a boolean
     */
    boolean isMandate();

    /**
     * Represents an image.
     *
     * @return a boolean
     */
    boolean isPicture();

    /**
     * Is a prohibiting policy.
     *
     * @return a boolean
     */
    boolean isProhibition();

    /**
     * Represents a reference document.
     *
     * @return a boolean
     */
    boolean isReference();

    /**
     * Is tags.
     *
     * @return a boolean
     */
    boolean isTags();
}
