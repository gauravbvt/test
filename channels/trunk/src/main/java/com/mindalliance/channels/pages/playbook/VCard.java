package com.mindalliance.channels.pages.playbook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Wrapping of all needed information for a vcard of a resource.
 */
public class VCard {

    /** the formatted name string associated with the vCard object. */
    private String formattedName;

    /** an image or photograph of the individual associated with the vCard. */
    private String photo;

    /**
     * The canonical number string for a telephone number for telephony communication with the
     * vCard object.
     */
    private List<Address> addresses = new ArrayList<Address>();

    /**
     * The canonical number string for a telephone number for telephony communication with the
     * vCard object. */
    private Map<String,String> telephones;

    /** The property specifies a latitude and longitude. */
    private String geo;

    /**
     * Specifies the job title, functional position or function of the individual associated with
     * the vCard object within an organization (V. P. Research and Development). */
    private String title;

    /**
     * The role, occupation, or business category of the vCard object within an organization
     * (eg. Executive). */
    private String role;

    /**
     * An image or graphic of the logo of the organization that is associated with the individual
     * to which the vCard belongs. */
    private String logo;

    /**
     * Organization Name or Organizational unit
     * The name and optionally the unit(s) of the organization associated with the vCard object.
     * This property is based on the X.520 Organization Name attribute and the X.520
     * Organization Unit attribute.
     */
    private String org;

    /** Specifies supplemental information or a comment that is associated with the vCard. */
    private String note;

    /** Combination of the calendar date and time of day of the last update to the vCard object. */
    private final String rev;

    //-----------------------------
    public VCard() {
        rev = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ" ).format( new Date() );
    }

    public String getRev() {
        return rev;
    }

    /**
     * @return the version of the vCard Specification.
     */
    public static String getVersion() {
        return "3.0";
    }

    /**
     * @return A structured representation of the name of the person, place or thing associated with
     * the vCard object. */
    public String getName() {
        List<String> tokens = new ArrayList<String>();
        for ( StringTokenizer t = new StringTokenizer( formattedName ); t.hasMoreTokens(); )
            tokens.add( t.nextToken() );

        StringBuilder name = new StringBuilder();
        for ( int i = tokens.size() - 1 ; i >= 0 ; i-- ) {
            name.append(  tokens.get( i ) );
            if ( i != 0 )
                name.append( ';' );
        }
        return name.toString();
    }

    public String getFormattedName() {
        return formattedName;
    }

    public void setFormattedName( String formattedName ) {
        this.formattedName = formattedName;
    }

    /**
     * Returns a string representation of the object.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return super.toString();
    }

    //====================================================
    /**
     * A vcard address.
     */
    public static final class Address {

        public enum Type {
            /** A home address. */
            Home,
            /** A work address. */
            Work
        };

        private Type type;
        private String content;

        //--------------------------
        public Address( Type type, String content ) {
            this.content = content;
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public Type getType() {
            return type;
        }
    }
}
