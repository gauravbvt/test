package com.mindalliance.channels.attachments;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * An external attachment.
 */
public interface Attachment extends Serializable {

    /** The specific kind of attachment. */
    enum Type {

        /** A miscellaneous attachment. */
        Document( "Document" ),

        /** A policy document that mandates whatever the attachment is attached to. */
        PolicyMust( "Mandating policy" ),

        /** A policy document that prohibits whatever the attachment is attached to. */
        PolicyCant( "Prohibiting policy" ),

        /** A document that allows whatever the attachment is attached to. */
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
     * The text of the link to this attachment.
     * @return a label
     */
    String getLabel();

    /**
     * The actual url of the attachment.
     * @return a url, local or not
     */
    String getLink();

    /**
     * @return the type of this attachment
     */
    Type getType();

}
