package com.mindalliance.channels.export;

import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.dao.Journal;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.util.Map;

/**
 * Import/Export scenarios using serialization.
 */
public class Serializer implements Importer, Exporter {

    private static final String IMPORT_FAILED = "Import failed";

    public Serializer() {
    }

    /**
     * {@inheritDoc}
     */
    public Scenario importScenario( InputStream stream ) throws IOException {
        try {
            return (Scenario) new ObjectInputStream( stream ).readObject();

        } catch ( ClassNotFoundException e ) {
            LoggerFactory.getLogger( getClass() ).error( IMPORT_FAILED, e );
            throw new IOException( IMPORT_FAILED, e );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void exportScenario( Scenario scenario, OutputStream stream ) throws IOException {
        final ObjectOutputStream oos = new ObjectOutputStream( stream );
        oos.writeObject( scenario );
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
    public void exportProject( OutputStream stream ) throws IOException {
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    public Map<String,Long> importProject( FileInputStream stream ) throws IOException {
        return null; //Todo
    }

    /**
     * {@inheritDoc}
     */
   public void exportJournal( Journal journal, OutputStream stream ) throws IOException {
        //Todo
    }

    /**
     * {@inheritDoc}
     */
    public Journal importJournal( FileInputStream stream ) throws IOException {
        return null;  //Todo
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Long> getIdMap() {
        return null;  //Todo
    }


}
