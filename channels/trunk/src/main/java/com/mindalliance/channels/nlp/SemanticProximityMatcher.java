package com.mindalliance.channels.nlp;

import edu.stanford.nlp.ling.HasTag;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.dictionary.Dictionary;
import net.didion.jwnl.dictionary.MorphologicalProcessor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import shef.nlp.wordnet.similarity.SimilarityMeasure;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mindalliance.channels.SemanticMatcher;

/**
 * Semantic proximity matcher based POS tagging and WordNet similarity measures.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 9, 2009
 * Time: 4:29:02 PM
 */
public class SemanticProximityMatcher implements SemanticMatcher {
    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( SemanticProximityMatcher.class );
    /**
     * Between 0 and 1, lower value lifts asymptotic scoring curve more.
     */
    private static final double HIGH_POWER = 1.0;
    private static final double LOW_POWER = 0.2;
    private static final int MANY_WORDS = 15;
    /**
     * How much weight to give best match vs average match (1.0 -> 1/2, 2.0 -> 2/3 etc.)
     */
    private static final double BEST_MATCH_FACTOR = 0; // 1.0
    /**
     * POS tags.
     */
    private static final List<String> PROPER_NOUN_TAGS = Arrays.asList( "NNP", "NNPS" );
    /**
     * POS tags.
     */
    private static final List<String> ADJECTIVE_TAGS = Arrays.asList( "JJ" );
    /**
     * POS tags.
     */
    private static final List<String> VERB_TAGS = Arrays.asList( "VB", "VBD", "VBG", "VBN", "VBP", "VBZ" );
    /**
     * POS tags.
     */
    private static final List<String> COMMON_NOUN_TAGS = Arrays.asList( "NN", "NNS" );
    /**
     * Tagger trained data.
     * (in "./src/main/webapp/data/left3words-wsj-0-18.tagger")
     */
    private Resource taggerData;
    /**
     * Wordnet library config.
     */
    private static final String JWNL_PROPERTIES = "jwnl_properties.xml";
    /**
     * Wordnet similarity data.
     */
    private static final String SIMILARITY_DATA = "ic-bnc-resnik-add1.dat";
    /**
     * WordNet dictionary.
     * ("./src/main/webapp/data/wordnet-2.0/dict")
     */
    private Resource wordnetDict;
    /**
     * Similarity algorithm.
     */
    private String simType = "shef.nlp.wordnet.similarity.JCn";
    /**
     * Whether the matcher was (lazily) initialized.
     */
    private boolean initialized;
    /**
     * In-memory wordnet dictionary.
     */
    private Dictionary dictionary;
    /**
     * POS tagger.
     */
    private MaxentTagger tagger;
    /**
     * Morphological processor.
     */
    private MorphologicalProcessor morpher;
    /**
     * Similarity meaasure calculator.
     */
    private SimilarityMeasure similarityMeasure;
    /**
     * How by how much instance overlap multiplies the conceptual similarity.
     */
    private static final double INSTANCE_OVERLAP_FACTOR = 2.0;

    public SemanticProximityMatcher() {
    }

    private void initialize() throws Exception {
        if ( !initialized ) {
            LOG.debug( "Initializing semantic proximity matcher" );
            initializeJWNL();
            dictionary = Dictionary.getInstance();
            morpher = dictionary.getMorphologicalProcessor();
            tagger = new MaxentTagger( taggerData.getFile().getAbsolutePath() );
            similarityMeasure = initializeSimilarityMeasure();
            initialized = true;
        }
    }

    public void setTaggerData( Resource taggerData ) {
        this.taggerData = taggerData;
    }

    public void setWordnetDict( Resource wordnetDict ) {
        this.wordnetDict = wordnetDict;
    }

    public void setSimType( String simType ) {
        this.simType = simType;
    }

    private void initializeJWNL() throws JWNLException, IOException {
        JWNL.initialize( getJWNLProperties() );
    }

    private InputStream getJWNLProperties() throws IOException {
        URL url = SemanticProximityMatcher.class.getResource( JWNL_PROPERTIES );
        // Class[] classes = {String.class};
        String template = getText( url );
        String adjustedTemplate = template.replaceFirst( "_WORDNET_DICT_",
                wordnetDict.getFile().getAbsolutePath() );
        return new ByteArrayInputStream( adjustedTemplate.getBytes() );
    }

    private String getText( URL url ) throws IOException {
        BufferedInputStream in = (BufferedInputStream) url.getContent();
        BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
        StringWriter writer = new StringWriter();
        String line;
        do {
            line = reader.readLine();
            if ( line != null ) {
                writer.write( line );
                writer.write( '\n' );
            }
        } while ( line != null );
        reader.close();
        return writer.toString();
    }

    private SimilarityMeasure initializeSimilarityMeasure() throws Exception {
        // return SimilarityMeasure.newInstance([simType: "shef.nlp.wordnet.similarity.JCn", infocontent: SIMILARITY_DATA])
        URL url = SemanticProximityMatcher.class.getResource( SIMILARITY_DATA );
        Map<String, String> config = new HashMap<String, String>();
        config.put( "simType", simType );
        String externalUrl = url.toExternalForm(); //.replace("file:/", "file://"); 
        new URL( externalUrl );
        config.put( "infocontent", externalUrl );
        return SimilarityMeasure.newInstance( config );
    }

    /**
     * {@inheritDoc}
     */
    public boolean matches( String text, String otherText, Proximity minLevel ) {
        try {
            initialize();
            Proximity level = semanticProximity( text, otherText );
            return level.getOrdinal() >= minLevel.getOrdinal();
        } catch ( Exception e ) {
            LOG.error( "Semantic matching failed", e );
            throw new RuntimeException( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    public Proximity bestMatch( List<String> strings, String string ) {
        try {
            initialize();
            Proximity max = Proximity.NONE;
            for ( String s : strings ) {
                Proximity level = semanticProximity( string, s );
                if ( level.getOrdinal() > max.getOrdinal() ) max = level;
            }
            return max;
        } catch ( Exception e ) {
            LOG.error( "Semantic matching failed", e );
            throw new RuntimeException( e );
        }
    }

    /**
     * {@inheritDoc}
     */
    public Proximity semanticProximity( String text, String otherText ) {
        try {
            initialize();
            long msecs = System.currentTimeMillis();
            if ( text.trim().length() == 0 || otherText.trim().length() == 0 ) return Proximity.NONE;
            LOG.trace( "==== Matching: [" + text + "] and [" + otherText + "]" );
            // Do POS analysis on each text
            List<HasWord> words = posAnalyze( text );
            List<HasWord> otherWords = posAnalyze( otherText );
            List<HasWord> phrases = extractPhrases( words );
            List<HasWord> otherPhrases = extractPhrases( otherWords );
            List<String> properNouns = extractProperNouns( words );
            List<String> otherProperNouns = extractProperNouns( otherWords );
            int size = properNouns.size() + phrases.size();
            int otherSize = otherProperNouns.size() + otherPhrases.size();
            // Get the synsets for the common nouns in each text
            double conceptualSimilarity = computeSynsetsSimilarity( phrases, otherPhrases );
            LOG.trace( "Conceptual similarity = " + conceptualSimilarity );
            // Calculate shared proper noun ratio between texts
            double instanceOverlap = computeInstanceOverlap(
                    properNouns,
                    otherProperNouns );
            // Combine both measures into a proximity rating
            LOG.trace( "Instance overlap = " + instanceOverlap );
            double score = conceptualSimilarity;
            if ( instanceOverlap > 0 ) {
                // instance overlap is a big deal
                // TODO boost based on sizes
                double boost = ( instanceOverlap / maximum( size, otherSize ) ) * INSTANCE_OVERLAP_FACTOR;
                LOG.trace( boost + " : boost from instance overlap = conceptualSimilarity * instanceOverlap" );
                score = minimum( 1.0, score + boost );
            }
            // to lift the curve
            double power = getPower( Math.min( size, otherSize ) );
            double finalScore = Math.pow( score, power );
            LOG.trace( "Score: " + score + " Power: " + power + " Final: " + finalScore );
            Proximity matchingLevel = matchingLevel( finalScore );
            LOG.trace( "---- Match is " + matchingLevel );
            LOG.debug( matchingLevel.getLabel()
                    + "(" + String.format("%.3f", finalScore)
                    + " in " + ( System.currentTimeMillis() - msecs ) + " ms" + "): "
                    + "\"" + text + "\" <=> " + "\"" + otherText + "\"\n" );
            return matchingLevel;
        } catch ( Exception e ) {
            LOG.error( "Semantic matching failed", e );
            throw new RuntimeException( e );
        }
    }

    private double getPower( int size ) {
        if ( size <= 1 ) return HIGH_POWER;
        if ( size > MANY_WORDS ) return LOW_POWER;
        return HIGH_POWER - ( ( HIGH_POWER - LOW_POWER ) / ( MANY_WORDS + 1 - size ) );
    }

    private Proximity matchingLevel( double score ) throws Exception {
        if ( score > 1.0 ) throw new Exception( "Illegal matching score" );
        if ( score > 0.75 ) return Proximity.VERY_HIGH;
        if ( score > 0.50 ) return Proximity.HIGH;
        if ( score > 0.30 ) return Proximity.MEDIUM;
        if ( score > 0.10 ) return Proximity.LOW;
        else return Proximity.NONE;
    }

    private List<HasWord> posAnalyze( String text ) {
        LOG.trace( "POS analysis: [" + text + "]" );
        List<Sentence<? extends HasWord>> sentences = MaxentTagger.tokenizeText( new StringReader( text ) );
        List<Sentence<TaggedWord>> processedSentences = tagger.process( sentences );
        List<HasWord> words = new ArrayList<HasWord>();
        for ( Sentence<TaggedWord> sentence : processedSentences ) {
            for ( TaggedWord word : sentence ) {
                words.add( word );
            }
        }
        return words;
    }

    // Collect all individual nouns or verbs, all adjective + noun phrases, and all noun + noun phrases
    private List<HasWord> extractPhrases( List<HasWord> words ) {
        Set<HasWord> phrases = new HashSet<HasWord>();
        String phrase = "";
        for ( HasWord word : words ) {
            if ( isVerb( word ) ) {
                LOG.trace( "Found phrase [" + word + "]" );
                phrases.add( word );
                phrase = ""; // reset phrase
            } else if ( isAdjective( word ) ) {
                phrase = word.word(); // start a new phrase with it
            } else if ( isCommonNoun( word ) ) {
                LOG.trace( "Found phrase [" + word + "]" );
                phrases.add( word );
                if ( phrase.length() > 0 ) {
                    phrase += " " + word.word();
                    TaggedWord taggedWord = new TaggedWord( phrase, ( (TaggedWord) word ).tag() );
                    LOG.trace( "Found phrase [" + taggedWord + "]" );
                    phrases.add( taggedWord );
                    phrase = "";
                } else {
                    phrase = word.word(); // start a new phrase with it
                }
            } else {
                phrase = ""; // reset the phrase
            }

        }
        return new ArrayList<HasWord>( phrases );
    }

    private List<String> extractProperNouns( List<HasWord> words ) {
        Set<String> properNouns = new HashSet<String>();
        String composed = "";
        for ( HasWord word : words ) {
            if ( isProperNoun( word ) ) {
                if ( composed.length() > 0 ) composed += " ";
                composed += word.word();
            } else {
                if ( composed.length() > 0 ) {
                    properNouns.add( composed );
                    LOG.trace( "Proper noun [" + composed + "]" );
                    composed = ""; // reset composed
                }
            }
        }
        if ( !composed.isEmpty() ) {
            properNouns.add( composed );
            LOG.trace( "Proper noun [" + composed + "]" );
        }
        return new ArrayList<String>( properNouns );
    }


    private boolean isCommonNoun( HasWord word ) {
        return word instanceof HasTag && COMMON_NOUN_TAGS.contains( ( (HasTag) word ).tag() );
    }

    private boolean isVerb( HasWord word ) {
        return word instanceof HasTag && VERB_TAGS.contains( ( (HasTag) word ).tag() );
    }

    private boolean isAdjective( HasWord word ) {
        return word instanceof HasTag && ADJECTIVE_TAGS.contains( ( (HasTag) word ).tag() );
    }

    private boolean isProperNoun( HasWord word ) {
        return word instanceof HasTag && PROPER_NOUN_TAGS.contains( ( (HasTag) word ).tag() );
    }

    private double computeSynsetsSimilarity( List<HasWord> words, List<HasWord> otherWords ) throws JWNLException {
        List<List<Synset>> synsets = findSynsets( words );
        List<List<Synset>> otherSynsets = findSynsets( otherWords );
        // computeCombinedSimilarity( A, B ) != computeCombinedSimilarity( B, A )
        double sim;
        if ( words.size() >= otherWords.size() ) {
            sim = computeCombinedSimilarity( synsets, otherSynsets );
        } else {
            sim = computeCombinedSimilarity( otherSynsets, synsets );
        }
        return sim;
    }

    private List<List<Synset>> findSynsets( List<HasWord> words ) throws JWNLException {
        List<List<Synset>> synsets = new ArrayList<List<Synset>>();  // should be a set of synsets, not a list
        for ( HasWord word : words ) {
            List<Synset> wordSynsets = new ArrayList<Synset>();  // should be a set of synsets, not a list
            POS pos = posOf( word );
            IndexWord indexWord = morpher.lookupBaseForm( pos, word.word() );
            if ( indexWord != null ) {
                for ( Long offset : indexWord.getSynsetOffsets() ) {
                    Synset synset = dictionary.getSynsetAt( pos, offset );

                    if ( !isRedundant( wordSynsets, synset ) ) {
                        wordSynsets.add( synset );
                        LOG.trace( "[" + word + "] => " + synset );
                    }
                }
            }
            synsets.add( wordSynsets );
        }
        return synsets;
    }

    private boolean isRedundant( List<Synset> synsets, final Synset synset ) {
        return CollectionUtils.exists(
                synsets,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (Synset) obj ).getOffset() == synset.getOffset();
                    }
                } );
    }

    private POS posOf( HasWord word ) {
        if ( isCommonNoun( word ) ) return POS.NOUN;
        else if ( isVerb( word ) ) return POS.VERB;
        else throw new IllegalArgumentException( "No recognized POS for $word" );
    }

    // Compare each word's synsets to other words synsets
    private double computeCombinedSimilarity(
            List<List<Synset>> synsets,
            List<List<Synset>> otherSynsets ) throws JWNLException {
        if ( synsets.isEmpty() || otherSynsets.isEmpty() ) return 0.0;
        List<Double> similarities = new ArrayList<Double>();
        double overallBest = 0.0;
        for ( List<Synset> wordSynsets : synsets ) {
            double best = 0.0;
            for ( List<Synset> otherWordSynsets : otherSynsets ) {
                double similarity = similarity( wordSynsets, otherWordSynsets );
                best = maximum( best, similarity );
            }
            similarities.add( best );
            overallBest = maximum( overallBest, best );
        }
        LOG.trace( overallBest + " for overall best match" );
        double sum = 0.0;
        for ( Double similarity : similarities ) {
            sum += similarity;
        }
        double average = sum / similarities.size();
        LOG.trace( average + " for average best match in " + similarities.size() );
        return ( ( BEST_MATCH_FACTOR * overallBest ) + average ) / ( 1 + BEST_MATCH_FACTOR );
    }

    private double maximum( double x, double y ) {
        return ( x > y ) ? x : y;
    }

    private double minimum( double x, double y ) {
        return ( x < y ) ? x : y;
    }

    // Find best similarity between one of the synset's of a word and one of the synsets of another word.
    private double similarity(
            List<Synset> wordSynsets,
            List<Synset> otherWordSynsets ) throws JWNLException {
        double best = 0.0;
        for ( Synset synset : wordSynsets ) {
            for ( Synset otherSynset : otherWordSynsets ) {
                best = maximum( best, similarity( synset, otherSynset ) );
            }
        }
        return best;
    }

    private double similarity( Synset synset, Synset otherSynset ) throws JWNLException {
        if ( synset.getOffset() == otherSynset.getOffset() ) return 1.0;
        double similarity = similarityMeasure.getSimilarity( synset, otherSynset );
        similarity = minimum( 1.0, similarity ); // cap it at 1.0
        LOG.trace( "Similarity = " + similarity + " for " + synset + " and " + otherSynset );
        return similarity;
    }

    @SuppressWarnings( "unchecked" )
    // Calculate percentage of proper nouns shared.
    private double computeInstanceOverlap( List<String> properNouns, List<String> otherProperNouns ) {
        List<String> shared = (List<String>) CollectionUtils.intersection( properNouns, otherProperNouns );
        double maxSampleSize = maximum( properNouns.size(), otherProperNouns.size() );
        return ( maxSampleSize > 0 ) ? ( shared.size() / maxSampleSize ) : 0.0;
    }

/*
Calculating the semantic proximity between strings A and B
==========================================================

-- Two strings are compared for semantic proximity and a fuzzy measure is returned (none, low, medium, high, very high or highest)

If the strings are identical, return HIGHEST
If any string is empty, return NONE

Find phrases in each string
tokenize into parts-of-speech (POS set)
extract phrases from POS set (phrase = noun, verb, adjective + noun or noun + noun)

Calculate synsets similarity between the phrases in A and the phrases in B

find all unique synsets for the phrases in each string
for each phrase
 lookup its canonical form
 lookup the Wordnet synsets, if any, for the canonical form

measure the proximity synsets of A to the synsets of B
 for each synset of A
   find the highest proximity measure (Jiang and Conrath) with a synset in B
 find the overall best match of any synset in A with a synset in B
 calculate the average of all best matches of synsets in A siwht a synset in B
 proximity of A to B = (K * overall_best) / (1 + K) --- where K is tuned to 1

measure the proximity synsets of B to the synsets of A
...

average of the two proximity measures => CONCEPTUAL_SIMILARITY

Calculate the shared proper noun ratio between POS sets of A and B
for each string
extract the list (composed) proper nouns
calculate how many are in both strings
size of overlap / size of smallest POS set => INSTANCE_OVERLAP

Combine both measures into a semantic proximity rating
OVERLAP_BOOST = CONCEPTUAL_SIMILARITY * INSTANCE_OVERLAP
SCORE = min(1.0, CONCEPTUAL_SIMILARITY + OVERLAP_BOOST) ^ P -- where P is between 0 and 1 and tuned to 0.6

Convert score to fuzzy measure
> 0.75  => VERY_HIGH
> 0.5   => HIGH
> 0.25  => MEDIUM
> 0     => LOW
= 0     => NONE

*/
}
