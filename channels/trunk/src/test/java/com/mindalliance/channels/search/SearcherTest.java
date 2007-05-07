package com.mindalliance.channels.search;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import org.apache.lucene.search.Hits;
import org.apache.lucene.queryParser.ParseException;

import java.io.IOException;
import java.util.*;

/**
 * Tests Searcher functionality. 
 * 
 * @author ebax
 *
 */

public class SearcherTest {

	Searcher searcher;
	
	@Before
	public void setUp() throws Exception {
		Searchable[] ss = new Searchable[] {
			new Phrase("hello", "hello old friend sunshine"),
		    new Phrase("goodbye moo", "goodbye old bear"),
		    new Phrase("tree", "trees are tall"),
		    new Phrase("sunshine", "sunshine on the trees")
		};

		searcher = new Searcher();

		for (Searchable s: ss) searcher.insert(s);

		// searcher.showHits(searcher.search("bear")); 
	}

    /**
     * Test method for {@link Searcher#search()}.
     */
    @Test
    public final void testSearch() throws IOException, ParseException {
    	Hits hits = searcher.search("sunshine");
    	assertEquals(2, hits.length());
    	assertEquals("sunshine", hits.doc(0).get("guid"));
    	assertEquals("hello", hits.doc(1).get("guid"));
    }

    /**
     * Test method for {@link Searcher#search()}.
     */
    @Test
    public final void testSearch2() throws IOException, ParseException {
    	List<SearchResult> results = searcher.search("sunshine", 10);
    	assertEquals(2, results.size());
    	assertEquals("sunshine", results.get(0).getGuid());
    	assertEquals("hello", results.get(1).getGuid());
    }
    
    /**
     * Test method for {@link Searcher#search()}.
     */
    @Test
    public final void testSearch3() throws IOException, ParseException {
    	List<SearchResult> results = searcher.search("sunshine", 1);
    	assertEquals(1, results.size());
    	assertEquals("sunshine", results.get(0).getGuid());
    }
    
    /**
     * Test method for {@link Searcher#delete()}.
     */
    @Test
    public final void testDelete() throws IOException, ParseException {
        Hits hits = searcher.search("bear");
        assertEquals(1, hits.length());
        
        searcher.delete("goodbye moo");
        
        hits = searcher.search("bear");
        assertEquals(0, hits.length()); 
    }
 
}
