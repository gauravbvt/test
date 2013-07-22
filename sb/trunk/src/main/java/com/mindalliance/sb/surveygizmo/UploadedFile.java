package com.mindalliance.sb.surveygizmo;

import com.mindalliance.sb.model.PlanFile;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Wrapper for dealing with file uploads.
 */
public class UploadedFile {

    private static final int MAS_SG_ID = 56191;

    private static final String CHECKSUM_ALGO = "SHA-256";

    private final String name;
    private final String location;
    private final int size;
    private final String mimeType;
    
    private URL url = null;
    private final byte[] contents;
    private final byte[] checksum;

    private static final Logger LOG = LoggerFactory.getLogger( UploadedFile.class );

    /**
     * Create a new object.
     * @param fileSpec something in like:
     * <p>{filedata=, filename=53-c3205ba77a7d77b76e16c4596f5f9d34_victory-day.jpg, filelocation=S3, filesize=82, filetype=image/jpeg}</p>
     * @param survey the id of the survey for this upload
     */
    public UploadedFile( String fileSpec, int survey ) {        
        Map<String,String> parts = parse( fileSpec );

        try {
            String filename = parts.get( "filename" );

            name = filename.substring( filename.indexOf( '_' ) + 1, filename.length() );
            location = parts.get( "filelocation" );
            mimeType = parts.get( "filetype" );

            url = new URL( MessageFormat.format(
                        "http://surveygizmoresponseuploads.s3.amazonaws.com/fileuploads/{0,number,#}/{1,number,#}/{2}",
                        MAS_SG_ID,
                        survey,
                        filename ) );

            MessageDigest digest = MessageDigest.getInstance( CHECKSUM_ALGO );
            DigestInputStream stream = new DigestInputStream( url.openStream(), digest );
            try {
                contents = IOUtils.toByteArray( stream );

            } finally {
                stream.close();
            }

            size = contents.length;
            checksum = digest.digest();        
            
        } catch ( NumberFormatException e ) {
            throw new IllegalArgumentException( "Unable to parse " + fileSpec, e );

        } catch ( MalformedURLException e ) {
            throw new IllegalArgumentException( "Unable to parse " + fileSpec, e );
            
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to download file @ " + url, e );

        } catch ( NoSuchAlgorithmException e ) {
            throw new RuntimeException( CHECKSUM_ALGO + " is not available on this jvm", e );

        }
    }

    private static Map<String, String> parse( String spec ) {
        Map<String, String> result = new HashMap<String, String>();

        StringTokenizer t = new StringTokenizer( spec.substring( 1, spec.length() - 1 ), ", " );
        while ( t.hasMoreTokens() ) {
            String part = t.nextToken();
            int i = part.indexOf( '=' );
            result.put( part.substring( 0, i ), part.substring( i + 1, part.length() ) );
        }

        return result;
    }

    public byte[] getContents() {
        return contents;
    }

    public String getLocation() {
        return location;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public URL getUrl() {
        return url;
    }

    public byte[] getChecksum() {
        return checksum;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append( "UploadedFile" );
        sb.append( "{name='" ).append( name ).append( '\'' );
        sb.append( ", mimeType='" ).append( mimeType ).append( '\'' );
        sb.append( ", size=" ).append( size );
        sb.append( '}' );
        return sb.toString();
    }

    public PlanFile toPlanFile() {
        PlanFile planFile = new PlanFile();
        planFile.setName( name );
        planFile.setChecksum( checksum );
        planFile.setContents( contents );
        planFile.setMimeType( mimeType );
        planFile.setSize( size );
        
        return planFile;
    }
}
