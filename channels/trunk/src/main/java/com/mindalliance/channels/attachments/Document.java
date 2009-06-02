package com.mindalliance.channels.attachments;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * An external document.
 */
public interface Document extends Serializable {

    /** The specific kind of document. */
    enum Type {

        /** A miscellaneous document. */
        Reference( "Reference" ),

        /** A policy document that mandates whatever the document is attached to. */
        PolicyMust( "Mandating policy" ),

        /** A policy document that prohibits whatever the document is attached to. */
        PolicyCant( "Prohibiting policy" ),

        /** A document that allows whatever the document is attached to. */
        MOU( "MOU" );

        //--------------------------------
        /** A description of the type that will hopefully make sense to the user. */
        private String label;

        Type( String label ) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public String getStyle() {
            return MessageFormat.format( "doc_{0}", toString() );                         // NON-NLS
        }

    }

    /**
     * The text of the link to this document.
     * @return a label
     */
    String getLabel();

    /**
     * @return the key to use in the file map.
     */
    String getKey();

    /**
     * The actual url of the document.
     * @return a url string, local or not
     */
    String getUrl();

    /**
     * @return the type of this document
     */
    Type getType();

    /**
     * @return the document's digest.
     */
    String getDigest();

    /**
     * Whether the document indicates a policy violation
     * @return a boolean
     */
    boolean isPolicyViolation();

    /** Get rid of persistent part, if any. */
    void delete();

    /**
     * Is URL document?
     * @return a boolean
     */
    boolean isUrl();

    /**
     * Is file document?
     * @return a boolean
     */
    boolean isFile();
}
