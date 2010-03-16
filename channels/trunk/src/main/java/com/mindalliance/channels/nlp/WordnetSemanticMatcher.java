package com.mindalliance.channels.nlp;

import com.mindalliance.channels.SemanticMatcher;
import jwsl.SimilarityAssessor;
import jwsl.WordNotFoundException;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerTarget;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.data.relationship.RelationshipFinder;
import net.didion.jwnl.data.relationship.RelationshipList;
import net.didion.jwnl.dictionary.Dictionary;
import net.didion.jwnl.dictionary.MorphologicalProcessor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Semantic proximity matcher of texts based on WordNet.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 9, 2009
 * Time: 4:29:02 PM
 */
public class WordnetSemanticMatcher implements SemanticMatcher {
    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger( WordnetSemanticMatcher.class );
    /**
     * Maximum match score.
     */
    private static final double MAX_SCORE = 1.0;
    /**
     * Minimum match score.
     */
    private static final double MIN_SCORE = 0.0;
    /**
     * Minimum score for very high proximity.
     */
    private static double VERY_HIGH_THRESHOLD = 0.85;
    /**
     * Minimum score for high proximity.
     */
    private static double HIGH_THRESHOLD = 0.7;
    /**
     * Minimum score for medium proximity.
     */
    private static double MEDIUM_THRESHOLD = 0.4;
    /**
     * Minimum score for low proximity.
     */
    private static double LOW_THRESHOLD = 0.2;
    /**
     * Wordnet library config.
     */
    private static final String JWNL_PROPERTIES = "jwnl_properties.xml";
    /**
     * WordNet dictionary.
     * ("./src/main/webapp/web-inf/data/wordnet-2/dict")
     */
    private Resource wordnetDict;
    /**
     * Lucene index of WordNet used for similarity assessment.
     */
    private Resource simIndex;
    /**
     * Whether the matcher was (lazily) initialized.
     */
    private boolean initialized;
    /**
     * Morphological processor.
     */
    private MorphologicalProcessor morpher;
    /**
     * Relationship finder.
     */
    private RelationshipFinder relationshipFinder;
    /**
     * Similarity assessor.
     */
    private SimilarityAssessor similarityAssessor;
    /**
     * How by how much instance overlap multiplies the conceptual similarity.
     */
    private static final double INSTANCE_OVERLAP_FACTOR = 1.0;
    /**
     * Similarity metric.
     */
    private static final String SIMILARITY_METRIC = SimilarityAssessor.JIANG_METRIC;
    /**
     * Separators used for tokenizing.
     */
    private static final String SEPARATORS = " ,.:;/?!\"'|\\'()[]{}=";

    public WordnetSemanticMatcher() {
    }

    private synchronized void initialize() throws Exception {
        if ( !initialized ) {
            LOG.debug( "Initializing semantic proximity matcher" );
            initializeJWNL();
            Dictionary dictionary = Dictionary.getInstance();
            morpher = dictionary.getMorphologicalProcessor();
            relationshipFinder = RelationshipFinder.getInstance();
            similarityAssessor = new SimilarityAssessor( simIndex.getFile().getAbsolutePath() );
            initialized = true;
        }
    }

    public void setWordnetDict( Resource wordnetDict ) {
        this.wordnetDict = wordnetDict;
    }

    public void setSimIndex( Resource simIndex ) {
        this.simIndex = simIndex;
    }

    private void initializeJWNL() throws JWNLException, IOException {
        JWNL.initialize( getJWNLProperties() );
    }

    private InputStream getJWNLProperties() throws IOException {
        URL url = WordnetSemanticMatcher.class.getResource( JWNL_PROPERTIES );
        // Class[] classes = {String.class};
        String template = getText( url );
        String dictPath = wordnetDict.getFile().getAbsolutePath().replaceAll("\\\\", "\\\\\\\\");
        String adjustedTemplate = template.replaceFirst(
                "_WORDNET_DICT_",
                dictPath );
        LOG.info( adjustedTemplate );
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


    /**
     * {@inheritDoc}
     */
    public synchronized boolean matches( String text, String otherText, Proximity minLevel ) {
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
            List<POSWord> words = posAnalyze( text );
            List<POSWord> otherWords = posAnalyze( otherText );
            List<POSWord> phrases = extractPhrases( words );
            List<POSWord> otherPhrases = extractPhrases( otherWords );
            List<String> properNouns = extractProperNouns( words );
            List<String> otherProperNouns = extractProperNouns( otherWords );
            int size = properNouns.size() + phrases.size();
            int otherSize = otherProperNouns.size() + otherPhrases.size();
            int minSize = Math.min( size, otherSize );
            // Get the synsets for the common nouns in each text
            double conceptualSimilarity = computeConceptualSimilarity( phrases, otherPhrases );
            LOG.trace( "Conceptual similarity = " + conceptualSimilarity );
            // Calculate shared proper noun ratio between texts
            double instanceOverlap = computeInstanceOverlap(
                    properNouns,
                    otherProperNouns );
            // Combine both measures into a proximity rating
            LOG.trace( "Instance overlap = " + instanceOverlap );
            double score = ( conceptualSimilarity + ( instanceOverlap * INSTANCE_OVERLAP_FACTOR ) ) / minSize;
            score = Math.min( MAX_SCORE, score );
            LOG.trace( "Score: " + score );
            Proximity matchingLevel = matchingLevel( score );
            LOG.trace( "---- Match is " + matchingLevel );
            LOG.debug( matchingLevel.getLabel()
                    + "(" + String.format( "%.3f", score )
                    + " in " + ( System.currentTimeMillis() - msecs ) + " ms" + "): "
                    + "\"" + text + "\" <=> " + "\"" + otherText + "\"" );
            return matchingLevel;
        } catch ( Exception e ) {
            LOG.error( "Semantic matching failed", e );
            throw new RuntimeException( e );
        }
    }

    private Proximity matchingLevel( double score ) throws Exception {
        if ( score > MAX_SCORE || score < MIN_SCORE ) throw new Exception( "Illegal matching score " + score );
        if ( score > VERY_HIGH_THRESHOLD ) return Proximity.VERY_HIGH;
        if ( score > HIGH_THRESHOLD ) return Proximity.HIGH;
        if ( score > MEDIUM_THRESHOLD ) return Proximity.MEDIUM;
        if ( score > LOW_THRESHOLD ) return Proximity.LOW;
        else return Proximity.NONE;
    }

    private List<POSWord> posAnalyze( String text ) throws JWNLException {
        LOG.trace( "POS analysis: [" + text + "]" );
        List<String> words = tokenizeText( text );
        return processWords( words );
    }

    private List<String> tokenizeText( String text ) {
        String[] tokens = StringUtils.split( StringUtils.replaceChars( text, '\n', ' ' ), SEPARATORS );
        return Arrays.asList( tokens );
    }

    private List<POSWord> processWords( List<String> words ) throws JWNLException {
        List<POSWord> posWords = new ArrayList<POSWord>();
        for ( String word : words ) {
            POSWord posWord = new POSWord( word );
            posWord.setProperNoun( isProperNoun( word ) );
            posWord.addMeaning( lookupBaseForm( POS.NOUN, word ) );
            posWord.addMeaning( lookupBaseForm( POS.VERB, word ) );
            posWord.addMeaning( lookupBaseForm( POS.ADJECTIVE, word ) );
            posWord.addMeaning( lookupBaseForm( POS.ADVERB, word ) );
            posWords.add( posWord );
        }
        return posWords;
    }

    /**
     * {@inheritDoc}
     */
    public IndexWord lookupBaseForm( POS pos, String word ) throws JWNLException {
        return morpher.lookupBaseForm( pos, word );
    }


    private boolean isProperNoun( String word ) throws JWNLException {
        byte[] bytes = word.getBytes();
        return bytes[0] >= 'A' && bytes[0] <= 'Z';
    }

    // Collate words into meaningful phrases where possible. Drop proper names and meaningless words.
    private List<POSWord> extractPhrases( List<POSWord> words ) throws JWNLException {
        Set<POSWord> phrases = new HashSet<POSWord>();
        int lastIndex = words.size() - 1;
        int index = 0;
        while ( index <= lastIndex ) {
            POSWord word = words.get( index );
            if ( index < lastIndex ) {
                POSWord nextWord = words.get( index + 1 );
                POSWord phrase = makeVerbPhrase( word, nextWord );
                if ( phrase == null ) {
                    phrase = makeCommonNounPhrase( word, nextWord );
                }
                if ( phrase == null ) {
                    phrase = makeQualifiedNounPhrase( word, nextWord );
                }
                if ( phrase != null ) {
                    phrases.add( phrase );
                    index = index + 2;
                    LOG.trace( "Extracted phrase: " + phrase );
                } else {
                    if ( !word.isProperNoun() && word.isComparable() ) {
                        phrases.add( word );
                        LOG.trace( "Extracted phrase: " + word );
                    }
                    index++;
                }
            } else {
                if ( !word.isProperNoun() && word.isComparable() ) {
                    phrases.add( word );
                    LOG.trace( "Extracted phrase: " + word );
                }
                index++;
            }
        }
        return new ArrayList<POSWord>( phrases );
    }

    private POSWord makeCommonNounPhrase( POSWord posWord, POSWord nextPosWord ) throws JWNLException {
        POSWord nounPhrase = null;
        if ( posWord.isCommonNoun() && nextPosWord.isCommonNoun() ) {
            String composed =
                    posWord.asPOS( POS.NOUN ).getLemma()
                            + " "
                            + nextPosWord.asPOS( POS.NOUN ).getLemma();
            IndexWord indexWord = lookupBaseForm( POS.NOUN, composed );
            if ( indexWord != null && indexWord.getLemma().equals( composed ) ) {
                nounPhrase = new POSWord( composed );
                nounPhrase.addMeaning( indexWord );
            }
        }
        return nounPhrase;
    }

    private POSWord makeVerbPhrase( POSWord posWord, POSWord nextPosWord ) throws JWNLException {
        POSWord verbPhrase = null;
        if ( posWord.isVerb() && nextPosWord.isAdverb() ) {
            String composed =
                    posWord.asPOS( POS.VERB ).getLemma()
                            + " "
                            + nextPosWord.asPOS( POS.ADVERB ).getLemma();
            IndexWord indexWord = lookupBaseForm( POS.VERB, composed );
            if ( indexWord != null && indexWord.getLemma().equals( composed ) ) {
                verbPhrase = new POSWord( composed );
                verbPhrase.addMeaning( indexWord );
            } else {
                posWord.setQualifier( nextPosWord );
                return posWord;
            }
        }
        return verbPhrase;
    }

    private POSWord makeQualifiedNounPhrase( POSWord posWord, POSWord nextPosWord ) throws JWNLException {
        POSWord qualifiedNounPhrase = null;
        if ( posWord.isAdjective() && nextPosWord.isCommonNoun() ) {
            String composed =
                    posWord.asPOS( POS.ADJECTIVE ).getLemma()
                            + " "
                            + nextPosWord.asPOS( POS.NOUN ).getLemma();
            qualifiedNounPhrase = new POSWord( composed );
            qualifiedNounPhrase.addMeaning( nextPosWord.asPOS( POS.NOUN ) );
            qualifiedNounPhrase.setQualifier( posWord );
        }
        return qualifiedNounPhrase;
    }

    private List<String> extractProperNouns( List<POSWord> words ) {
        Set<String> properNouns = new HashSet<String>();
        String composed = "";
        for ( POSWord word : words ) {
            if ( word.isProperNoun() ) {
                if ( composed.length() > 0 ) composed += " ";
                composed += word.getWord();
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


    @SuppressWarnings( "unchecked" )
    private double computeConceptualSimilarity(
            List<POSWord> posWords,
            List<POSWord> otherPosWords ) throws JWNLException {
        // computeCombinedSimilarity( A, B ) != computeCombinedSimilarity( B, A )
        double sim;
        if ( posWords.size() <= otherPosWords.size() ) {
            sim = computeCombinedPOSWordSimilarity( posWords, otherPosWords );
        } else {
            sim = computeCombinedPOSWordSimilarity( otherPosWords, posWords );
        }
        return sim;
    }

    // Compare each of a list of words to another list of words (no posWord must be matched twice)
    private double computeCombinedPOSWordSimilarity(
            List<POSWord> posWords,
            List<POSWord> otherPosWords ) throws JWNLException {
        if ( posWords.isEmpty() || otherPosWords.isEmpty() ) return MIN_SCORE;
        List<Double> similarities = new ArrayList<Double>();
        Set<POSWord> matchedOthers = new HashSet<POSWord>();
        for ( POSWord posWord : posWords ) {
            double bestSim = MAX_SCORE * -1;
            POSWord bestMatch = null;
            Iterator<POSWord> iter = otherPosWords.iterator();
            while ( bestSim < MAX_SCORE && iter.hasNext() ) {
                POSWord otherPosWord = iter.next();
                if ( !matchedOthers.contains( otherPosWord ) ) {
                    double sim = computePOSWordSimilarity( posWord, otherPosWord );
                    if ( sim > bestSim ) {
                        bestSim = sim;
                        bestMatch = otherPosWord;
                    }
                }
            }
            if ( bestMatch != null ) {
                matchedOthers.add( bestMatch );
                similarities.add( bestSim );
            }
        }
        double sum = MIN_SCORE;
        for ( Double similarity : similarities ) {
            sum += similarity;
        }
        return sum;
    }

    // Note: wordnet librairies seems to be thread-unsafe.
    private synchronized double computePOSWordSimilarity( POSWord posWord, POSWord otherPosWord ) throws JWNLException {
        if ( posWord.isQualified() && otherPosWord.isQualified() ) {
            // If qualifiers are antonyms, negate similarity.
            if ( antonyms(
                    posWord.getQualifier().asPOS( POS.ADJECTIVE ),
                    otherPosWord.getQualifier().asPOS( POS.ADJECTIVE ) ) ) {
                LOG.trace( "Anotnyms: " + posWord + ", " + otherPosWord );
                return MIN_SCORE;
            }
            if ( antonyms(
                    posWord.getQualifier().asPOS( POS.ADVERB ),
                    otherPosWord.getQualifier().asPOS( POS.ADVERB ) ) ) {
                LOG.trace( "Anotnyms: " + posWord + ", " + otherPosWord );
                return MIN_SCORE;
            }
        }
        double similarity = MIN_SCORE;
        for ( Meaning meaning : posWord.getMeanings() ) {
            Iterator<Meaning> iter = otherPosWord.getMeanings().iterator();
            while ( similarity < MAX_SCORE && iter.hasNext() ) {
                Meaning otherMeaning = iter.next();
                if ( meaning.getPos().equals( otherMeaning.getPos() ) ) {
                    double sim;
                    if ( meaning.getLemma().equals( otherMeaning.getLemma() ) ) {
                        sim = MAX_SCORE;
                    } else {
                        Set<String> nouns = getNominalizations( posWord );
                        Set<String> otherNouns = getNominalizations( otherPosWord );
                        sim = assessSimilarity( nouns, otherNouns );
                    }
                    if ( sim > similarity ) {
                        similarity = sim;
                    }
                }
            }
        }
        if ( posWord.isQualified() && otherPosWord.isQualified() ) {
            // If both qualified and qualifications not synonymous, half similarity.
            boolean synonyms = synonyms(
                    posWord.getQualifier().asPOS( POS.ADJECTIVE ),
                    otherPosWord.getQualifier().asPOS( POS.ADJECTIVE ) )
                    || synonyms(
                    posWord.getQualifier().asPOS( POS.ADVERB ),
                    otherPosWord.getQualifier().asPOS( POS.ADVERB ) );
            if ( !synonyms ) {
                similarity = similarity / 2.0;
                LOG.trace( "NOT synonyms: " + posWord + ", " + otherPosWord );
            }
        }
        return similarity;
    }

    // Get related nouns for non-noun word.
    private Set<String> getNominalizations( POSWord posWord ) throws JWNLException {
        Set<String> nouns = new HashSet<String>();
        if ( posWord.isNoun() ) {
            nouns.add( posWord.getNoun() );
        } else {
            for ( Synset synset : posWord.getSynsets() ) {
                PointerTarget[] pointerTargets = synset.getTargets( PointerType.NOMINALIZATION );
                for ( PointerTarget pointerTarget : pointerTargets ) {
                    Word[] words;
                    if ( pointerTarget instanceof Synset ) {
                        Synset targetSynset = (Synset) pointerTarget;
                        words = targetSynset.getWords();
                    } else {
                        words = new Word[1];
                        words[0] = (Word) pointerTarget;
                    }
                    for ( Word word : words ) {
                        nouns.add( word.getLemma() );
                        LOG.trace( "Nominalized " + posWord + " to " + word.getLemma() );
                    }
                }
            }
        }
        return nouns;
    }

    private boolean antonyms( Meaning meaning, Meaning otherMeaning ) throws JWNLException {
        return !synonyms( meaning, otherMeaning )
                && hasRelationship( meaning, otherMeaning, PointerType.ANTONYM );
    }

    private boolean hasRelationship( Meaning meaning, Meaning otherMeaning, PointerType pointerType ) throws JWNLException {
        if ( meaning == null || otherMeaning == null ) return false;
        for ( Synset synset : meaning.getSynsets() ) {
            for ( Synset otherSynset : otherMeaning.getSynsets() ) {
                RelationshipList rels = relationshipFinder.findRelationships(
                        synset,
                        otherSynset,
                        pointerType
                );
                if ( !rels.isEmpty() )
                    return true;
            }
        }
        return false;
    }

    private boolean synonyms( Meaning meaning, Meaning otherMeaning ) throws JWNLException {
        return !( meaning == null || otherMeaning == null )
                && CollectionUtils.containsAny( meaning.getSynsets(), otherMeaning.getSynsets() );
    }

    private double assessSimilarity( Set<String> nouns, Set<String> otherNouns ) {
        double best = MIN_SCORE;
        for ( String noun : nouns ) {
            for ( String otherNoun : otherNouns ) {
                double sim = assessSimilarity( noun, otherNoun );
                if ( sim == MAX_SCORE ) return MAX_SCORE;
                if ( sim > best ) {
                    best = sim;
                }
            }
        }
        return best;
    }

    /**
     * {@inheritDoc}
     */
    public Double assessSimilarity( String noun, String otherNoun ) {
        double similarity = MIN_SCORE;
        try {
            if ( noun != null && otherNoun != null ) {
                similarity = similarityAssessor.getSimilarity(
                        noun,
                        otherNoun,
                        SIMILARITY_METRIC );
            }
        } catch ( WordNotFoundException e ) {
            LOG.trace( "Word not found in: " + noun + ", " + otherNoun );
        }
        LOG.trace( "Similarity of " + noun + " and " + otherNoun + " = " + similarity );
        return similarity;
    }

    @SuppressWarnings( "unchecked" )
    // Calculate percentage of proper nouns shared.
    private double computeInstanceOverlap( List<String> properNouns, List<String> otherProperNouns ) {
        List<String> shared = (List<String>) CollectionUtils.intersection( properNouns, otherProperNouns );
        double maxSampleSize = Math.max( properNouns.size(), otherProperNouns.size() );
        return ( maxSampleSize > 0 ) ? ( shared.size() / maxSampleSize ) : MIN_SCORE;
    }

/*
Calculating the semantic proximity between strings A and B
==========================================================

-- Two strings are compared for semantic proximity and a fuzzy measure is returned (none, low, medium, high, very high or highest)

If the strings are identical, return HIGHEST
If any string is empty, return NONE

Find phrases in each string
tokenize into parts-of-speech (POS set)
extract phrases from POS set (phrase = noun, verb, adjective + noun , noun + noun, verb + adjective)

Calculate synsets similarity between the phrases in A and the phrases in B

*/
}
