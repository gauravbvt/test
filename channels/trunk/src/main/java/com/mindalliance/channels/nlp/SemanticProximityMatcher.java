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
     * How much weight to give best match vs average match (1.0 -> 1/2, 2.0 -> 2/3 etc.)
     */
    // private static final double BEST_MATCH_FACTOR = 0; // 1.0
    /**
     * Wordnet library config.
     */
    private static final String JWNL_PROPERTIES = "jwnl_properties.xml";
    /**
     * WordNet dictionary.
     * ("./src/main/webapp/data/wordnet-3/dict")
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
    private static final double INSTANCE_OVERLAP_FACTOR = 2.0;
    /**
     * Similarity metric.
     */
    private static final String SIMILARITY_METRIC = SimilarityAssessor.JIANG_METRIC;
    private static final String SEPARATORS = " ,.:;/?!\"'|\\'";

    public SemanticProximityMatcher() {
    }

    private void initialize() throws Exception {
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
            List<POSWord> words = posAnalyze( text );
            List<POSWord> otherWords = posAnalyze( otherText );
            List<POSWord> phrases = extractPhrases( words );
            List<POSWord> otherPhrases = extractPhrases( otherWords );
            List<String> properNouns = extractProperNouns( words );
            List<String> otherProperNouns = extractProperNouns( otherWords );
            int size = properNouns.size() + phrases.size();
            int otherSize = otherProperNouns.size() + otherPhrases.size();
            int maxSize = Math.max( size, otherSize );
            // Get the synsets for the common nouns in each text
            double conceptualSimilarity = computeConceptualSimilarity( phrases, otherPhrases );
            LOG.trace( "Conceptual similarity = " + conceptualSimilarity );
            // Calculate shared proper noun ratio between texts
            double instanceOverlap = computeInstanceOverlap(
                    properNouns,
                    otherProperNouns );
            // Combine both measures into a proximity rating
            LOG.trace( "Instance overlap = " + instanceOverlap );
            double score = ( conceptualSimilarity + ( instanceOverlap * INSTANCE_OVERLAP_FACTOR ) ) / maxSize;
            score = Math.min( 1.0, score );
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
        if ( score > 1.0 ) throw new Exception( "Illegal matching score" );
        if ( score > 0.85 ) return Proximity.VERY_HIGH;
        if ( score > 0.70 ) return Proximity.HIGH;
        if ( score > 0.40 ) return Proximity.MEDIUM;
        if ( score > 0.20 ) return Proximity.LOW;
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
                } else {
                    if ( !word.isProperNoun() && word.isComparable() ) phrases.add( word );
                    index++;
                }
            } else {
                if ( !word.isProperNoun() && word.isComparable() ) phrases.add( word );
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
        if ( posWords.size() >= otherPosWords.size() ) {
            sim = computeCombinedPOSWordSimilarity( posWords, otherPosWords );
        } else {
            sim = computeCombinedPOSWordSimilarity( otherPosWords, posWords );
        }
        return sim;
    }

    // Compare each of a list of words to another list of words
    private double computeCombinedPOSWordSimilarity(
            List<POSWord> posWords,
            List<POSWord> otherPosWords ) throws JWNLException {
        if ( posWords.isEmpty() || otherPosWords.isEmpty() ) return 0.0;
        List<Double> similarities = new ArrayList<Double>();
        double overallBest = 0.0;
        for ( POSWord posWord : posWords ) {
            double best = 0.0;
            Iterator<POSWord> iter = otherPosWords.iterator();
            while ( best < 1.0 && iter.hasNext() ) {
                POSWord otherPosWord = iter.next();
                double similarity = computePOSWordSimilarity( posWord, otherPosWord );
                best = Math.max( best, similarity );
            }
            similarities.add( best );
            overallBest = Math.max( overallBest, best );
        }
        LOG.trace( overallBest + " for overall best match" );
        double sum = 0.0;
        for ( Double similarity : similarities ) {
            sum += similarity;
        }
        return sum;
    }

    private double computePOSWordSimilarity( POSWord posWord, POSWord otherPosWord ) throws JWNLException {
        if ( posWord.isQualified() && otherPosWord.isQualified() ) {
            // If qualifiers are antonyms, negate similarity.
            if ( antonyms(
                    posWord.getQualifier().asPOS( POS.ADJECTIVE ),
                    otherPosWord.getQualifier().asPOS( POS.ADJECTIVE ) ) ) {
                return 0.0;
            }
        }
        double similarity = 0.0;
        for ( Meaning meaning : posWord.getMeanings() ) {
            for ( Meaning otherMeaning : otherPosWord.getMeanings() ) {
                if ( meaning.getPos().equals( otherMeaning.getPos() ) ) {
                    if ( meaning.getLemma().equals( otherMeaning.getLemma() ) ) return 1.0;
                    Set<String> nouns = getNominalizations( posWord );
                    Set<String> otherNouns = getNominalizations( otherPosWord );
                    double sim = assessSimilarity( nouns, otherNouns );
                    if ( sim > similarity ) {
                        similarity = sim;
                    }
                }
            }
        }
        if ( posWord.isQualified() && otherPosWord.isQualified() ) {
            // If both qualified and qualifications not synonymous, half similarity.
            if ( !synonyms(
                    posWord.getQualifier().asPOS( POS.ADJECTIVE ),
                    otherPosWord.getQualifier().asPOS( POS.ADJECTIVE ) ) ) {
                similarity = similarity / 2.0;
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

    private double assessSimilarity ( Set<String> nouns, Set<String> otherNouns ) {
        double best = 0.0;
        for ( String noun : nouns ) {
            for ( String otherNoun : otherNouns ) {
                double sim = assessSimilarity( noun, otherNoun );
                if ( sim == 1.0 ) return 1.0;
                if ( sim > best ) {
                    best = sim;
                }
            }
        }
        return best;
    }

    public Double assessSimilarity ( String noun, String otherNoun ) {
        double similarity = 0.0;
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
        return similarity;
    }

    @SuppressWarnings( "unchecked" )
    // Calculate percentage of proper nouns shared.
    private double computeInstanceOverlap ( List<String> properNouns, List<String> otherProperNouns ) {
        List<String> shared = (List<String>) CollectionUtils.intersection( properNouns, otherProperNouns );
        double maxSampleSize = Math.max( properNouns.size(), otherProperNouns.size() );
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
