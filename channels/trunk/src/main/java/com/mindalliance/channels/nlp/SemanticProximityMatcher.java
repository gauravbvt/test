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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * Semantic proximity matcher.
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
    private static final double POWER = 0.6; // between 0 and 1, lower value lifts asymptotic scoring curve more
    private static final double BEST_MATCH_FACTOR = 1.00001; // how much weight to give best match vs average match (1.0 -> 1/2, 2.0 -> 2/3 etc.)
    private static final List<String> PROPER_NOUN_TAGS = Arrays.asList( "NNP", "NNPS" );
    private static final List<String> ADJECTIVE_TAGS = Arrays.asList( "JJ" );
    private static final List<String> VERB_TAGS = Arrays.asList( "VB", "VBD", "VBG", "VBN", "VBP", "VBZ" );
    private static final List<String> COMMON_NOUN_TAGS = Arrays.asList( "NN", "NNS" );

    private String taggerData; // "./src/main/webapp/data/left3words-wsj-0-18.tagger"
    private static final String JWNL_PROPERTIES = "jwnl_properties.xml";
    private static final String SIMILARITY_DATA = "ic-bnc-resnik-add1.dat";
    private String wordnetDict; //  "./src/main/webapp/data/wordnet-2.0/dict"
    private String simType = "shef.nlp.wordnet.similarity.JCn";

    private boolean initialized;
    private Dictionary dictionary;
    private MaxentTagger tagger;
    private MorphologicalProcessor morpher;
    private SimilarityMeasure similarityMeasure;

    public SemanticProximityMatcher() {
    }

    private void initialize() throws Exception {
        if ( !initialized ) {
            LOG.debug("Initializing semantic proximity matcher");
            initializeJWNL();
            dictionary = Dictionary.getInstance();
            morpher = dictionary.getMorphologicalProcessor();
            tagger = new MaxentTagger( taggerData );
            similarityMeasure = initializeSimilarityMeasure();
            initialized = true;
        }
    }

    public void setTaggerData( String taggerData ) {
        this.taggerData = taggerData;
    }

    public void setWordnetDict( String wordnetDict ) {
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
        String adjustedTemplate = template.replaceFirst( "_WORDNET_DICT_", wordnetDict );
        return new ByteArrayInputStream( adjustedTemplate.getBytes() );
    }

    private String getText( URL url) throws IOException {
        BufferedInputStream in = (BufferedInputStream)url.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringWriter writer = new StringWriter();
        String line;
        do {
          line = reader.readLine();
            if (line != null) {
                writer.write( line );
                writer.write( '\n');
            }
        } while (line != null);
        reader.close();
        return writer.toString();
    }

    private SimilarityMeasure initializeSimilarityMeasure() throws Exception {
        // return SimilarityMeasure.newInstance([simType: "shef.nlp.wordnet.similarity.JCn", infocontent: SIMILARITY_DATA])
        URL url = SemanticProximityMatcher.class.getResource( SIMILARITY_DATA );
        Map<String, String> config = new HashMap<String, String>();
        config.put( "simType", simType );
        String externalUrl = url.toExternalForm(); //.replace("file:/", "file://"); 
        new URL(externalUrl);
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
            if ( text.trim().length() == 0 || otherText.trim().length() == 0 ) return Proximity.NONE;
            LOG.debug( "==== Matching: [" + text + "] and [" + otherText + "]" );
            // Do POS analysis on each text
            List<HasWord> words = posAnalyze( text );
            List<HasWord> otherWords = posAnalyze( otherText );
            // Get the synsets for the common nouns in each text
            double conceptualSimilarity = computeSynsetsSimilarity( extractPhrases( words ),
                    extractPhrases( otherWords ) );
            LOG.trace( "Conceptual similarity = " + conceptualSimilarity );
            // Calculate shared proper noun ratio between texts
            double instanceOverlap = computeInstanceOverlap( extractProperNouns( words ),
                    extractProperNouns( otherWords ) );
            // Combine both measures into a proximity rating
            LOG.trace( "Instance overlap = " + instanceOverlap );
            double score = conceptualSimilarity;
            if ( instanceOverlap > 0 ) {// instance overlap is a big deal
                double boost = conceptualSimilarity * instanceOverlap;
                LOG.trace( boost + " : boost from instance overlap = conceptualSimilarity * instanceOverlap" );
                score = minimum( 1.0, score + boost );
            }
            score = Math.pow( score, POWER ); // to lift the curve
            Proximity matchingLevel = matchingLevel( score );
            LOG.debug( "---- Match is " + matchingLevel );
            return matchingLevel;
        } catch ( Exception e ) {
            LOG.error( "Semantic matching failed", e );
            throw new RuntimeException( e );
        }
    }

    private Proximity matchingLevel( double score ) throws Exception {
        if ( score > 1.0 ) throw new Exception( "Illegal matching score" );
        if ( score > 0.75 ) return Proximity.VERY_HIGH;
        if ( score > 0.5 ) return Proximity.HIGH;
        if ( score > 0.25 ) return Proximity.MEDIUM;
        if ( score > 0 ) return Proximity.LOW;
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
                composed += " " + word.word();
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
        List<Synset> synsets = findSynsets( words );
        List<Synset> otherSynsets = findSynsets( otherWords );
        double sim1 = computeCombinedSimilarity( synsets, otherSynsets );
        double sim2 = computeCombinedSimilarity( otherSynsets, synsets );
        return ( sim1 + sim2 ) / 2.0;
    }

    private List<Synset> findSynsets( List<HasWord> words ) throws JWNLException {
        List<Synset> synsets = new ArrayList<Synset>();  // should be a set of synsets, not a list
        for ( HasWord word : words ) {
            POS pos = posOf( word );
            IndexWord indexWord = morpher.lookupBaseForm( pos, word.word() );
            if ( indexWord != null ) {
                for ( Long offset : indexWord.getSynsetOffsets() ) {
                    Synset synset = dictionary.getSynsetAt( pos, offset );

                    if ( !isRedundant( synsets, synset ) ) {
                        synsets.add( synset );
                        LOG.trace( "[" + word + "] => " + synset );
                    }
                }
            }
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

    private double computeCombinedSimilarity( List<Synset> synsets, List<Synset> otherSynsets ) throws JWNLException {
        if ( synsets.isEmpty() || otherSynsets.isEmpty() ) return 0.0;
        List<Double> similarities = new ArrayList<Double>();
        double overallBest = 0.0;
        for ( Synset synset : synsets ) {
            double best = 0.0;
            for ( Synset otherSynset : otherSynsets ) {
                best = maximum( best, similarity( synset, otherSynset ) );
            }
            LOG.trace( best + " is best match score for " + synset );
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

    private double similarity( Synset synset, Synset otherSynset ) throws JWNLException {
        if ( synset.getOffset() == otherSynset.getOffset() ) return 1.0;
        double similarity = similarityMeasure.getSimilarity( synset, otherSynset );
        similarity = minimum( 1.0, similarity ); // cap it at 1.0
        LOG.trace( "Similarity = " + similarity + " for " + synset + " and " + otherSynset );
        return similarity;
    }

    @SuppressWarnings( "unchecked" )
    private double computeInstanceOverlap( List<String> properNouns, List<String> otherProperNouns ) {
        List<String> shared = (List<String>) CollectionUtils.intersection( properNouns, otherProperNouns );
        double minSampleSize = minimum( properNouns.size(), otherProperNouns.size() );
        return ( minSampleSize > 0 ) ? ( shared.size() / minSampleSize ) : 0.0;
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
