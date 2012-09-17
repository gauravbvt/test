package com.mindalliance.channels.core.export;

import com.mindalliance.channels.core.dao.Exporter;
import com.mindalliance.channels.core.dao.ImportExportFactory;
import com.mindalliance.channels.core.dao.Importer;
import com.mindalliance.channels.core.dao.Journal;
import com.mindalliance.channels.core.dao.PlanDao;
import com.mindalliance.channels.core.model.Segment;
import org.apache.wicket.markup.MarkupType;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Bogus, no-persistence import/exporter.
 */
public class DummyExporter implements ImportExportFactory, Importer, Exporter {

    public DummyExporter() {
    }

    /**
     * Create an import context.
     *
     * @param userName
     * @param service the query service
     * @return an importer
     */
    public Importer createImporter( String userName, PlanDao service ) {
        return this;
    }

    /**
     * Create an export context.
     *
     * @param userName
     * @param planDao
     * @return an exporter
     */
    public Exporter createExporter( String userName, PlanDao planDao ) {
        return this;
    }

    /**
     * Export a journal on the given stream.
     * @param journal a journal
     * @param stream the stream
     * @throws IOException on errors
     */
    public void export( Journal journal, OutputStream stream ) throws IOException {
    }

    /**
     * Export all data on the given stream.
     * @param stream the stream
     * @throws IOException on errors
     */
    public void export( OutputStream stream ) throws IOException {
    }

    /**
     * Export a segment on the given stream.
     * @param segment the segment
     * @param stream the stream
     * @throws IOException on errors
     */
    public void export( Segment segment, OutputStream stream ) throws IOException {
    }

    /**
     * The mime type of files from which segments are imported.
     *
     * @return -- a mime type
     */
    public MarkupType getMimeType() {
        return new MarkupType( "txt", "text/plain" );
    }

    /**
     * Current version
     *
     * @return -- a string
     */
    public String getVersion() {
        return "";
    }

    /**
     * Import a journal from a stream.
     *
     * @param stream an input stream
     * @return a journal
     * @throws IOException on errors
     */
    public Journal importJournal( FileInputStream stream ) throws IOException {
        return new Journal();
    }

    /**
     * Import all persisted data from a stream.
     *
     * @param stream an input stream @throws IOException on errors
     * @throws IOException on errors
     */
    public void importPlan( FileInputStream stream ) throws IOException {
    }

    /**
     * Import a segment from a stream.
     *
     * @param stream an input stream
     * @return a map with segment and proxy connectors
     * @throws IOException on errors
     */
    public Segment importSegment( InputStream stream ) throws IOException {
        return new Segment();
    }

    /**
     * Load segment, do not reconnect external flows.
     *
     *
     * @param inputStream an input stream
     * @return mapped results
     * @throws IOException on errors
     */
    public Map<String, Object> loadSegment( InputStream inputStream ) throws IOException {
        return new HashMap<String,Object>();
    }

    /**
     * Reload a segment from xml.
     *
     * @param xml an xml string
     * @return a map with segment and proxy connectors
     */
    public Segment restoreSegment( String xml ) {
        return new Segment();
    }
}