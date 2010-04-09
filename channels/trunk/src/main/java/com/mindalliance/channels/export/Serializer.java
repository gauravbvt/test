package com.mindalliance.channels.export;

import com.mindalliance.channels.export.Exporter;
import com.mindalliance.channels.export.Importer;
import com.mindalliance.channels.dao.Journal;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.Segment;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Import/Export segments using serialization.
 */
public class Serializer implements Importer, Exporter {

    private static final String IMPORT_FAILED = "Import failed";

    public Serializer() {
    }

    /**
     * {@inheritDoc}
     */
    public Segment importSegment( InputStream stream ) throws IOException {
        try {
            return (Segment) new ObjectInputStream( stream ).readObject();

        } catch ( ClassNotFoundException e ) {
            LoggerFactory.getLogger( getClass() ).error( IMPORT_FAILED, e );
            throw new IOException( IMPORT_FAILED, e );
        }
    }

    /**
      * {@inheritDoc}
      */
    public Segment restoreSegment( String xml ) {
        try {
            return importSegment( new ByteArrayInputStream( xml.getBytes()) );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void export( Segment segment, OutputStream stream ) throws IOException {
        final ObjectOutputStream oos = new ObjectOutputStream( stream );
        oos.writeObject( segment );
        oos.flush();
    }

    /**
     * {@inheritDoc}
     */
    public String getMimeType() {
        return "application/x-java-serialized-object";
    }

    /**
     * Current version
     *
     * @return -- a string
     */
    public String getVersion() {
        return "1.0";
    }

    /**
     * {@inheritDoc}
     */
    public void export( OutputStream stream ) throws IOException {
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    public void importPlan( FileInputStream stream ) throws IOException {
        //Todo
    }

    /**
     * {@inheritDoc}
     */
   public void export( Journal journal, OutputStream stream ) throws IOException {
        //Todo
    }

    /**
     * {@inheritDoc}
     */
    public Journal importJournal( FileInputStream stream ) throws IOException {
        return null;  //Todo
    }

    public Map<String, Object> loadSegment( InputStream inputStream ) throws IOException {
        // TODO
        return null;
    }

    public void reconnectExternalFlows(
            Map<Connector,
            List<ConnectionSpecification>> proxyConnectors,
            boolean importingPlan ) {
        //Todo
    }


    /**
     * {@inheritDoc}
     */
    public Map<String, Long> getIdMap() {
        return null;  //Todo
    }


}
