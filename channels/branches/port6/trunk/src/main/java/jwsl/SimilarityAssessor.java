/**
 *
 *
 * Java WordNet Similarity Library
 * authors: Giuseppe Pirr? and Nuno Seco
 *
 * for information contact Giuseppe at  gpirro@deis.unical.it
 *
 */
package jwsl;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class SimilarityAssessor {

    public static final String RESNIK_METRIC = "Resnik";
    public static final String JIANG_METRIC = "Jiang";
    public static final String LIN = "Lin";
    public static final String PIRRO_SECO_METRIC = "Pirr? and Seco";


    /**
     * Holds a reference to an instance of a Searcher that allows searches to be
     * conducted in the opened index.
     */
    private Searcher _searcher;


    /**
     * Holds a reference to an instance of an Index Broker.
     */
    private IndexBroker _broker;

    /**
     * The constructor. Obtains an instance of an Index Broker.
     */
    public SimilarityAssessor( String simIndex ) {
        try {
            if ( _searcher == null ) {
                _searcher = new IndexSearcher( new NIOFSDirectory( new File( simIndex ) ) );
            }
            _broker = IndexBroker.getInstance( simIndex, _searcher );
        } catch ( IOException ex ) {
            ex.printStackTrace();
            System.err.println( "" );
            System.err.println( "Please place the " + simIndex
                    + " in the working directory." );
        }
    }

    /**
     * Calculates the similarity between two specific senses.
     *
     * @param word1         String
     * @param senseForWord1 int The sense number for the first word
     * @param word2         String
     * @param senseForWord2 int The sense number for the second word
     * @return double The degree of similarity between the words; 0 means no
     *         similarity and 1 means that they may belong to the same synset.
     * @throws WordNotFoundException An exception is thrown if one of the words is not contained
     *                               in the WordNet dictionary.
     */
    public synchronized double getSenseSimilarity( String word1, int senseForWord1,
                                                   String word2, int senseForWord2, String metric ) throws WordNotFoundException {
        TopDocs synsets1 = _broker.getHits( word1 + "." + senseForWord1 );
        TopDocs synsets2 = _broker.getHits( word2 + "." + senseForWord2 );

        if ( synsets1.totalHits == 0 ) {
            throw new WordNotFoundException( "Word " + word1 + "."
                    + senseForWord1 + " is not in the dictionary." );
        }

        if ( synsets2.totalHits == 0 ) {
            throw new WordNotFoundException( "Word " + word2 + "."
                    + senseForWord2 + " is not in the dictionary." );
        }

        try {
            return getSimilarity( _searcher.doc( synsets1.scoreDocs[0].doc ), _searcher.doc( synsets2.scoreDocs[0].doc ), metric );

        } catch ( IOException ex ) {
            ex.printStackTrace();
            return 0.0;
        }

    }

    /**
     * Does the actual calculation between synsets.
     *
     * @param synset1 Document
     * @param synset2 Document
     * @return double
     */
    private synchronized double getSimilarity( Document synset1, Document synset2, String metric ) {
        double msca = getBestMSCAValue( synset1, synset2 );

        if ( msca == -1 ) {
            return 0;
        }


        if ( metric.equalsIgnoreCase( JIANG_METRIC ) )
            return getJiangSimilarity( synset1, synset2 );
        else if ( metric.equalsIgnoreCase( LIN ) )
            return getLinSimilarity( synset1, synset2 );

        else if ( metric.equalsIgnoreCase( PIRRO_SECO_METRIC ) )
            return getPirroAndSecoSimilarity( synset1, synset2 );
        else if ( metric.equalsIgnoreCase( RESNIK_METRIC ) )
            return getResnikSimilarity( synset1, synset2 );
        return -1;

    }


    /**
     * Calculates the similarity between the two words
     *
     * @param word1 String
     * @param word2 String
     * @return double The degree of similarity between the words; 0 means no
     *         similarity and 1 means that they may belong to the same synset.
     * @throws WordNotFoundException An exception is thrown if one of the words is not contained
     *                               in the WordNet dictionary.
     */
    public synchronized double getSimilarity( String word1, String word2, String metric )
            throws WordNotFoundException {
        TopDocs synsets1 = _broker.getHits( word1 + ".*" );
        TopDocs synsets2 = _broker.getHits( word2 + ".*" );

        if ( synsets1.scoreDocs.length == 0 ) {
            throw new WordNotFoundException( "Word " + word1
                    + " is not in the dictionary." );
        }

        if ( synsets2.scoreDocs.length == 0 ) {
            throw new WordNotFoundException( "Word " + word2
                    + " is not in the dictionary." );
        }

        double current = 0;
        double best = 0;

        try {
            for ( int i = 0; i < synsets1.scoreDocs.length-1; i++ ) {
                for ( int j = 0; j < synsets2.scoreDocs.length-1; j++ ) {

                    current = getSimilarity(
                            _searcher.doc( synsets1.scoreDocs[i].doc ),
                            _searcher.doc( synsets2.scoreDocs[j].doc ),
                            metric );

                    if ( current > best ) {
                        best = current;
                    }
                }
            }
        } catch ( IOException ex ) {
            ex.printStackTrace();
        }

        return best;
    }

    /**
     * Does the actual calculation between synsets.
     *
     * @param synset1 Document
     * @param synset2 Document
     * @return double
     */
    private double getJiangSimilarity( Document synset1, Document synset2 ) {
        double msca = getBestMSCAValue( synset1, synset2 );

        if ( msca == -1 ) {
            return 0;
        }

        double ic_synset1 = ( ( Double.parseDouble( synset1
                .get( IndexBroker.INFORMATION_CONTENT ) ) ) );

        double ic_synset2 = ( ( Double.parseDouble( synset2
                .get( IndexBroker.INFORMATION_CONTENT ) ) ) );

        return 1 - ( ( ( ic_synset1 + ic_synset2 ) - 2 * msca ) ) / 2;
    }

    private double getResnikSimilarity( Document synset1, Document synset2 ) {
        double msca = getBestMSCAValue( synset1, synset2 );

        if ( msca == -1 ) {
            return 0;
        }

        return msca;
    }

    private double getPirroAndSecoSimilarity( Document synset1, Document synset2 ) {
        double msca = getBestMSCAValue( synset1, synset2 );

        if ( msca == -1 ) {
            return 0;
        }

        double ic_synset1 = ( ( Double.parseDouble( synset1
                .get( IndexBroker.INFORMATION_CONTENT ) ) ) );

        double ic_synset2 = ( ( Double.parseDouble( synset2
                .get( IndexBroker.INFORMATION_CONTENT ) ) ) );

        return ( 2 + ( 3 * msca ) - ic_synset1 - ic_synset2 ) / 3;

    }

    private double getLinSimilarity( Document synset1, Document synset2 ) {
        double msca = getBestMSCAValue( synset1, synset2 );

        if ( msca == -1 ) {
            return 0;
        }

        double ic_synset1 = ( ( Double.parseDouble( synset1
                .get( IndexBroker.INFORMATION_CONTENT ) ) ) );

        double ic_synset2 = ( ( Double.parseDouble( synset2
                .get( IndexBroker.INFORMATION_CONTENT ) ) ) );

        return ( 2 * msca ) / ( ic_synset1 + ic_synset2 );

    }

    /**
     * Discovers the best Most Specific Common Abstraction (MSCA) value for the
     * two given Synsets. Note that synsets are represented as Lucene documents.
     *
     * @param doc1 Document One synset
     * @param doc2 Document Another synset
     * @return double The value of the MSCA with the highest IC value
     */
    private double getBestMSCAValue( Document doc1, Document doc2 ) {
        double current = 0;
        double best = 0;
        String offset;

        LinkedList intersection = getIntersection( doc1
                .getValues( IndexBroker.HYPERNYM )[0].split( " " ), doc2
                .getValues( IndexBroker.HYPERNYM )[0].split( " " ) );

        if ( intersection.isEmpty() ) {
            return -1;
        }

        while ( !intersection.isEmpty() ) {
            offset = intersection.removeFirst().toString();

            current = getIC( offset );
            if ( current > best ) {
                best = current;
            }
        }

        return best;
    }

    /**
     * Obtains the Information Content (IC) value for a given synset offset.
     *
     * @param offset String The offset to be queried
     * @return double The IC value
     */
    private double getIC( String offset ) {
        TopDocs synset = _broker.getHits( IndexBroker.SYNSET + ":" + offset );
        try {
            return Double.parseDouble( _searcher.doc( synset.scoreDocs[0].doc ).get(
                    IndexBroker.INFORMATION_CONTENT ) );
        } catch ( Exception ex ) {
            ex.printStackTrace();
            return 0.0;
        }
    }

    /**
     * Gets a list of strings that are contained in both arrays. The strings in
     * the arrays represent different synsets.
     *
     * @param values1 String[] An array of synsets
     * @param values2 String[] Another array of synsets.
     * @return LinkedList The list of synsets common to each array
     */
    private LinkedList getIntersection( String[] values1, String[] values2 ) {
        LinkedList intersection = new LinkedList();

        for ( int i = 0; i < values1.length; i++ ) {
            for ( int j = 0; j < values2.length; j++ ) {
                if ( values1[i].equals( values2[j] ) ) {
                    intersection.add( values1[i] );
                    break;
                }
            }
        }

        return intersection;
    }

}
