// Copyright (C) 2007 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.search;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.RAMDirectory;

/**
 * A Searcher manages an in-memory index of Searchable objects. The
 * fundamental methods are insert, search, and delete. Since the index
 * is in-memory, it needs to be rebuilt each time the Channels
 * application restarts. The index needs to be kept in synch with
 * object inserts, deletes, and updates.
 *
 * @author ebax
 * @version $Revision:$
 */
public class Searcher {

    /**
     * This is the Lucene index, stored in RAM.
     */
    private RAMDirectory dir = new RAMDirectory();

    /**
     * Constructor.
     */
    public Searcher() {
    }

    /**
     * Insert an object into the index.
     * @param obj the object
     * @throws IOException on errors
     */
    public void insert( Searchable obj ) throws IOException {
        Document doc = new Document();
        doc.add( new Field( "guid", obj.getGUID(), Field.Store.YES,
                Field.Index.UN_TOKENIZED ) );
        doc.add( new Field( "name", obj.getName(), Field.Store.YES,
                Field.Index.UN_TOKENIZED ) );
        doc.add( new Field( "kind", obj.getKind(), Field.Store.YES,
                Field.Index.TOKENIZED ) );
        doc.add( new Field( "project", obj.getProject(), Field.Store.YES,
                Field.Index.NO ) );
        doc.add( new Field( "projectGuid", obj.getProjectGUID(),
                Field.Store.YES, Field.Index.NO ) );
        doc.add( new Field( "text", obj.getText(), Field.Store.NO,
                Field.Index.TOKENIZED ) );

        IndexWriter writer = new IndexWriter( dir, new StandardAnalyzer() );
        writer.addDocument( doc );
        writer.close();
    }

    /**
     * Perform a search. Return a list of SearchResults.
     * @param query a query using syntax accepted by Lucene's QueryParser.
     * @param maxResults maximum number of search results to return.
     * @return search results
     * @throws IOException on errors
     * @throws ParseException on errors
     */
    public List<SearchResult> search( String query, int maxResults )
        throws IOException, ParseException {
        
        Hits hits = search( query );
        return SearchResult.fromHits( hits, maxResults );
    }

    /**
     * Perform a search.
     * @param query a query using syntax accepted by Lucene's
     *            QueryParser.
     * @return search results
     * @throws IOException on errors
     * @throws ParseException on errors
     */
    public Hits search( String query ) throws IOException, ParseException {
        QueryParser parser = new QueryParser( "text", new StandardAnalyzer() );
        Query q = parser.parse( query );
        IndexSearcher is = new IndexSearcher( dir );
        Hits hits = is.search( q );
        is.close();
        return hits;
    }

    /**
     * Display search results on System.out. Useful for debugging.
     * @param hits search results
     * @throws IOException on errors
     */
    public void showHits( Hits hits ) throws IOException {
        for ( int i = 0; i < hits.length(); i++ ) {
            Document doc = hits.doc( i );
            System.out.println( i + " -- " + doc.get( "guid" ) );
        }
    }

    /**
     * Delete an object from the index.
     * @param guid globally unique id of object to delete
     * @throws IOException on errors
     */
    public void delete( String guid ) throws IOException {
        IndexWriter writer = new IndexWriter( dir, new StandardAnalyzer() );
        writer.deleteDocuments( new Term( "guid", guid ) );
        writer.close();
    }
}
