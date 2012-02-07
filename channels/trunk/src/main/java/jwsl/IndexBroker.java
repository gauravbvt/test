package jwsl;
/**
 * 
 *  
 * Java WordNet Similarity Library
 * authors: Giuseppe Pirr? and Nuno Seco
 * 
 * for information contact Giuseppe at  gpirro@deis.unical.it
 *
 */

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

import java.io.IOException;

public class IndexBroker {
	/**
	 * A static constant that represents the field name that holds the offset
	 * value of each document.
	 */
	public static final String SYNSET = "synset";

	/**
	 * A static constant that represents the field name that holds the list of
	 * words of each document.
	 */
	public static final String WORDS = "word";

	/**
	 * A static constant that represents the field name that holds the list of
	 * hypernym offsets of each document. This list also contains the offset of
	 * the documented in which it is contained.
	 */
	public static final String HYPERNYM = "hypernym";

	/**
	 * A static constant that represents the field name that holds the
	 * information Content value of each document.
	 */
	public static final String INFORMATION_CONTENT = "ic";

	/**
	 * The directory where the broker will look for the Lucene index.
	 */
    // private final String INDEX_DIR = "/home/jf/workspace/channels/src/main/java/jwsl/wn_index";
	/**
	 * Holds a reference to an instance of a Searcher that allows searches to be
	 * conducted in the opened index.
	 */
	private Searcher _searcher;

	/**
	 * Holds a reference to an instance of a Parser; a parser parses the query.
	 */
	private QueryParser _parser;

	/**
	 * A static reference to an instance of an Index Broker. This variable
	 * guarantees that only one instance of the broker will be allowed for each
	 * Java Virtual Machine launched.
	 */
	private static IndexBroker _instance;

    private static final int MAX_HITS = 1000;

    /**
	 * The Constructor. Has private access to allow the implementation of the
	 * singleton design pattern. Points the searcher to the index directory,
	 * sets the default field to lookup and the defualt operator that is to be
	 * assumed when more than one token is given.
	 */
	private IndexBroker(String indexDir, Searcher searcher) {
			_searcher = searcher;
			_parser = new QueryParser( Version.LUCENE_35, WORDS, new WhitespaceAnalyzer());

	}

	/**
	 * Static method that allows other objects to acquire a reference to an
	 * existing broker. If no broker exists than a new one is created.
	 * 
	 * @return IndexBroker
	 */
	public static IndexBroker getInstance(String indexDir, Searcher searcher) {
		if (_instance == null) {
			_instance = new IndexBroker(indexDir, searcher);
        }

		return _instance;
	}

	/**
	 * Returns the list of documents that fulfill the given query.
	 * 
	 * @param query
	 *            String The query to be searched
	 * @return Hits A list of hits
	 */
	public TopDocs getHits(String query) {
		Query q;
		try {
			q = _parser.parse(query);

			return _searcher.search(q, MAX_HITS);
		} catch (ParseException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
