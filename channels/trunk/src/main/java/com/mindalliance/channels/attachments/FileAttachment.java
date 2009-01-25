package com.mindalliance.channels.attachments;

import com.mindalliance.channels.Dao;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.NotFoundException;

import java.io.File;
import java.text.MessageFormat;

/**
 * An actual file.
 */
public class FileAttachment implements Attachment {

    /** The separator used in encoded filenames (_). */
    private static final int SEPARATOR = (int) '_';

    /** Character used to escape funny characters. */
    private static final char ESCAPE = '%';

    /** Characters to be escaped in file name. */
    private static final String CHARS = "%_/\\:?&";                                       // NON-NLS

    /** The file on the server side. */
    private File file;

    /** The type of this attachment */
    private Type type;

    /** The external link to this file. Set by the manager. */
    private String link;

    /** The model object of the attachment. */
    private ModelObject object;

    public FileAttachment() {
    }

    public FileAttachment( ModelObject object, Type type, File file ) {
        this();
        setObject( object );
        setType( type );
        setFile( file );
    }

    public FileAttachment( ModelObject object, File file ) {
        this( object, typeFrom( file ), file );
    }

    public FileAttachment( Dao dao, File file ) throws NotFoundException {
        this( objectFrom( dao, file ), file );
    }

    /**
     * Create an encoded file name from given parameters.
     * @param object the attachment's object
     * @param type the attachment's type
     * @param label the user-visible label
     * @return an encoded string from which the parameters can be reconstructed
     */
    public static String createFileName( ModelObject object, Type type, String label ) {
        return MessageFormat.format( "f{0}_{1}_{2}",                                      // NON-NLS
                                  Long.toString( object.getId() ),
                                  Integer.toString( type.ordinal() ),
                                  escape( label ) );
    }

    private static String escape( String name ) {
        final StringBuilder buf = new StringBuilder();
        for ( int i = 0; i < name.length(); i++ ) {
            final char c = name.charAt( i );
            if ( CHARS.indexOf( (int) c ) >= 0 ) {
                buf.append( ESCAPE );
                buf.append( Integer.toHexString( (int) c ) );
                buf.append( ESCAPE );
            } else {
                buf.append( c );
            }
        }
        return buf.toString();
    }

    private static String unescape( String name ) {
        final StringBuilder buf = new StringBuilder();
        for ( int i = 0; i < name.length(); i++ ) {
            final char c = name.charAt( i );
            if ( c == ESCAPE ) {
                final int pos = name.indexOf( ESCAPE, i + 1 );
                buf.append( (char) Integer.parseInt( name.substring( i + 1, pos ), 16 ) );
                i = pos;
            } else {
                buf.append( c );
            }
        }
        return buf.toString();
    }

    /**
     * Find the object encoded in a given file's name.
     * @param dao where to look for objects.
     * @param file the file
     * @return a model object
     * @throws NotFoundException when no corresponding object was found
     */
    static ModelObject objectFrom( Dao dao, File file ) throws NotFoundException {
        final String fileName = file.getName();
        return dao.find( Long.parseLong( fileName.substring( 1, fileName.indexOf( SEPARATOR ) ) ) );
    }

    /**
     * Extract an attachment type from a file.
     * @param file the file
     * @return the type
     */
    static Type typeFrom( File file ) {
        final String fileName = file.getName();
        return Type.values()[
                Integer.parseInt(
                    fileName.substring( fileName.indexOf( SEPARATOR ) + 1,
                                        fileName.lastIndexOf( SEPARATOR ) ) ) ];
    }

    /**
     * Extract a displayable label from a file.
     * @param file the file
     * @return usually, the file name as given by the user.
     */
    static String labelFrom( File file ) {
        final String fileName = file.getName();
        return unescape( fileName.substring( fileName.lastIndexOf( SEPARATOR ) + 1 ) );
    }

    public final File getFile() {
        return file;
    }

    public final void setFile( File file ) {
        this.file = file;
    }

    public String getLabel() {
        return labelFrom( getFile() );
    }

    public final Type getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPolicyViolation() {
        return getType() == Type.PolicyCant;
    }

    public final void setType( Type type ) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink( String link ) {
        this.link = link;
    }

    public final ModelObject getObject() {
        return object;
    }

    public final void setObject( ModelObject object ) {
        this.object = object;
    }
}
