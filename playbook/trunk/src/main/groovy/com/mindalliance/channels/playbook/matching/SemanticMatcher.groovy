package com.mindalliance.channels.playbook.matching

import net.didion.jwnl.dictionary.Dictionary
import net.didion.jwnl.JWNL
import edu.stanford.nlp.tagger.maxent.MaxentTagger
import edu.stanford.nlp.ling.HasWord
import edu.stanford.nlp.ling.HasTag
import net.didion.jwnl.data.Synset
import net.didion.jwnl.dictionary.MorphologicalProcessor
import net.didion.jwnl.data.IndexWord
import net.didion.jwnl.data.POS
import shef.nlp.wordnet.similarity.SimilarityMeasure
import edu.stanford.nlp.ling.TaggedWord
import org.apache.log4j.Logger
import com.mindalliance.channels.playbook.support.Level

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 15, 2008
 * Time: 7:14:23 PM
 */
class SemanticMatcher {

    static final double POWER = 0.6 // between 0 and 1, lower value lifts asymptotic scoring curve more
    static final double BEST_MATCH_FACTOR = 1.0 // how much weight to give best match vs average match (1.0 -> 1/2, 2.0 -> 2/3 etc.)

    static final String TAGGER_TRAINED_DATA = './data/wsj3t0-18-bidirectional/train-wsj-0-18.holder'
    static final String JWNL_PROPERTIES = '/config/semantic/jwnl_properties.xml'
    static final String SIMILARITY_DATA = '/config/semantic/ic-bnc-resnik-add1.dat'

    static SemanticMatcher instance  // singleton

    private Dictionary dictionary
    private MaxentTagger tagger
    private MorphologicalProcessor morpher
    private SimilarityMeasure similarityMeasure
    private Logger logger

    static SemanticMatcher getInstance() {
        if (!instance) {
            instance = new SemanticMatcher()
            initializeJWNL()
            instance.dictionary = Dictionary.getInstance()
            instance.morpher = instance.dictionary.morphologicalProcessor
            instance.tagger = new MaxentTagger(TAGGER_TRAINED_DATA)
            instance.similarityMeasure = initializeSimilarityMeasure()
            instance.logger = Logger.getLogger(instance.class)
        }
        return instance
    }

    static initializeJWNL() {
        InputStream is = ClassLoader.getResourceAsStream(JWNL_PROPERTIES)
        JWNL.initialize(is)
    }

    static SimilarityMeasure initializeSimilarityMeasure() {
       // return SimilarityMeasure.newInstance([simType: "shef.nlp.wordnet.similarity.JCn", infocontent: SIMILARITY_DATA])
       URL url = ClassLoader.getResource(SIMILARITY_DATA)
       SimilarityMeasure sm = SimilarityMeasure.newInstance([simType: "shef.nlp.wordnet.similarity.JCn", infocontent: url.toExternalForm()])
       return sm
    }

    Level semanticProximity(String text, String otherText) {
        if (text.trim().size() == 0 || otherText.trim().size() == 0) return Level.NONE
        logger.info("==== Matching: [$text] and [$otherText]")
        // Do POS analysis on each text
        List words = posAnalyze(text)
        List otherWords = posAnalyze(otherText)
        // Get the synsets for the common nouns in each text
        double conceptualSimilarity = computeSynsetsSimilarity(extractPhrases(words),
                extractPhrases(otherWords))
        logger.info("Conceptual similarity = $conceptualSimilarity")
        // Calculate shared proper noun ratio between texts
        double instanceOverlap = computeInstanceOverlap(extractProperNouns(words),
                extractProperNouns(otherWords))
        // Combine both measures into a proximity rating
        logger.info("Instance overlap = $instanceOverlap")
        double score = conceptualSimilarity
        if (instanceOverlap) {// and there's instance overlap, which is a big deal
            double boost = conceptualSimilarity * instanceOverlap
            logger.debug("$boost : boost from instance overlap = conceptualSimilarity * instanceOverlap")
            score = minimum(1.0, score + boost)
        }
        score = Math.pow(score, POWER) // to lift the curve
        Level matchingLevel = matchingLevel(score)
        logger.info("---- Match is ${matchingLevel}")
        return matchingLevel
    }

    private Level matchingLevel(double score) {
        if (score > 1.0) throw new Exception("Illegal matching score")
        if (score > 0.75) return Level.VERY_HIGH
        if (score > 0.5) return Level.HIGH
        if (score > 0.25) return Level.MEDIUM
        if (score > 0) return Level.LOW
        else return Level.NONE
    }

    private List<HasWord> posAnalyze(String text) {
        logger.debug("POS analysis: [$text]")
        List sentences = tagger.tokenizeText(new StringReader(text))
        sentences = tagger.process(sentences)
        List<HasWord> words = []
        sentences.each {sentence ->
            sentence.each {word ->
                words.add(word)
            }
        }
        return words
    }

    // Collect all individual nouns or verbs, all adjective + noun phrases, and all noun + noun phrases
    private List<HasWord> extractPhrases(List<HasWord> words) {
        Set<HasWord> phrases = new HashSet<HasWord>()
        String phrase = ""
        words.each {word ->
            if (isVerb(word)) {
                logger.debug("Found phrase [$word]")
                phrases.add(word)
                phrase = "" // reset phrase
            }
            else if (isAdjective(word)) {
                phrase = word.value() // start a new phrase with it
            }
            else if (isCommonNoun(word)) {
                logger.debug("Found phrase [$word]")
                phrases.add(word)
                if (phrase.size() > 0) {
                    phrase += " ${word.value()}"
                    TaggedWord taggedWord = new TaggedWord(phrase, word.tag())
                    logger.debug("Found phrase [$taggedWord]")
                    phrases.add(taggedWord)
                    phrase = ""
                }
                else {
                    phrase = word.value() // start a new phrase with it
                }
            }
            else {
                phrase = "" // reset the phrase
            }

        }
        return phrases as List<HasWord>
    }

    private List<String> extractProperNouns(List<HasWord> words) {
        Set<String> properNouns = new HashSet<String>()
        String composed = ""
        words.each {word ->
            if (isProperNoun(word)) {
                if (composed.size() > 0) composed += " "
                composed += "${word.value()}"
            }
            else {
                if (composed.size() > 0) {
                    properNouns.add(composed)
                    logger.debug("Proper noun [$composed]")
                    composed = "" // reset composed
                }
            }
        }
        if (composed) {
            properNouns.add(composed)
            logger.debug("Proper noun [$composed]")
        }
        return properNouns as List<String>
    }

    private boolean isCommonNoun(HasWord word) {
        if (word instanceof HasTag && ['NN', 'NNS'].contains(word.tag())) {
            return true
        }
        else {
            return false
        }
    }

    private boolean isVerb(HasWord word) {
        if (word instanceof HasTag && ['VB', 'VBD', 'VBG', 'VBN', 'VBP', 'VBZ'].contains(word.tag())) {
            return true
        }
        else {
            return false
        }
    }

    private boolean isAdjective(HasWord word) {
        if (word instanceof HasTag && ['JJ'].contains(word.tag())) {
            return true
        }
        else {
            return false
        }
    }

    private boolean isProperNoun(HasWord word) {
        if (word instanceof HasTag && ['NNP', 'NNPS'].contains(word.tag())) {
            return true
        }
        else {
            return false
        }
    }

    private double computeSynsetsSimilarity(List words, List otherWords) {
        List<Synset> synsets = findSynsets(words)
        List<Synset> otherSynsets = findSynsets(otherWords)
        double sim1 = computeCombinedSimilarity(synsets, otherSynsets)
        double sim2 = computeCombinedSimilarity(otherSynsets, synsets)
        return (sim1 + sim2) / 2.0
    }

    private List<Synset> findSynsets(List<HasWord> words) {
        List<Synset> synsets = []
        words.each {word ->
            POS pos = posOf(word)
            IndexWord indexWord = morpher.lookupBaseForm(pos, word.value())
            if (indexWord) {
                indexWord.synsetOffsets.each {offset ->
                    Synset synset = dictionary.getSynsetAt(pos, offset)
                    if (!synsets.any {it.offset == synset.offset}) {
                        synsets.add(synset)
                        logger.debug("[$word] => $synset")
                    }
                }
            }
        }
        return synsets
    }

    private POS posOf(HasWord word) {
        if (isCommonNoun(word)) return POS.NOUN
        else if (isVerb(word)) return POS.VERB
        else throw new IllegalArgumentException("No recognized POS for $word")
    }

    private double computeCombinedSimilarity(List<Synset> synsets, List<Synset> otherSynsets) {
        if (!synsets || !otherSynsets) return 0.0
        List similarities = []
        double overallBest = 0.0
        synsets.each {synset ->
            double best = 0.0
            otherSynsets.each {otherSynset ->
                best = maximum(best, similarity(synset, otherSynset))
            }
            logger.debug("$best is best match score for $synset")
            similarities.add(best)
            overallBest = maximum(overallBest, best)
        }
        logger.debug("$overallBest for overall best match (66% of score)")
        double sum = 0.0
        similarities.each {sum += it}
        double average = sum / similarities.size()
        logger.debug("$average for average best match in ${similarities.size()} (33% of score)")
        double score = ((BEST_MATCH_FACTOR * overallBest) + average) / (1 + BEST_MATCH_FACTOR)
        return score
    }

    private double maximum(double x, double y) {
        return (x > y) ? x : y
    }

    private double minimum(double x, double y) {
        return (x < y) ? x : y
    }

    private double similarity(Synset synset, Synset otherSynset) {
        if (synset.offset == otherSynset.offset) return 1.0
        double similarity = similarityMeasure.getSimilarity(synset, otherSynset)
        similarity = minimum(1.0, similarity) // cap it at 1.0
        logger.debug("Similarity = $similarity for $synset and $otherSynset")
        similarityMeasure.addToCache(synset, otherSynset, similarity)
        return similarity
    }

    private double computeInstanceOverlap(List<String> properNouns, List<String> otherProperNouns) {
        List<String> shared = properNouns.intersect(otherProperNouns)
        int minSampleSize = minimum(properNouns.size(), otherProperNouns.size())
        double score = 0.0
        score = (minSampleSize) ? score = (double) (shared.size() / minSampleSize) : 0.0
        return score
    }

}