package com.mindalliance.channels.core.nlp;

import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Part-of-speech word according to WordNet.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 4, 2009
 * Time: 11:11:12 AM
 */
public class POSWord implements Serializable {
    /**
     * A possibly composed word from some text.
     */
    private String word;
    /**
     * Meanings for the word.
     */
    private List<Meaning> meanings = new ArrayList<Meaning>();
    /**
     * Whether word is a proper noun.
     */
    private boolean properNoun;
    /**
     * Qualifying djective or adverb.
     */
    private POSWord qualifier;

    /**
     * Create POS word on a word.
     *
     * @param word a string
     */
    public POSWord( String word ) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setWord( String word ) {
        this.word = word;
    }

    public List<Meaning> getMeanings() {
        return meanings;
    }

    /**
     * Add a meaning.
     *
     * @param meaning a meaning
     */
    public void addMeaning( Meaning meaning ) {
        if ( meaning != null ) {
            meanings.add( meaning );
        }
    }

    /**
     * Add the meaning of a Wordnet index word.
     *
     * @param indexWord an index word
     * @throws JWNLException if fails
     */
    public void addMeaning( IndexWord indexWord ) throws JWNLException {
        if ( indexWord != null ) {
            meanings.add( Meaning.from( indexWord ) );
        }
    }

    /**
     * Add the meaning of a Wordnet word.
     *
     * @param word a word
     * @throws JWNLException if fails
     */
    public void addMeaning( Word word ) throws JWNLException {
        if ( word != null ) {
            meanings.add( Meaning.from( word ) );
        }
    }

    public POSWord getQualifier() {
        return qualifier;
    }

    public void setQualifier( POSWord qualifier ) {
        this.qualifier = qualifier;
    }

    public boolean isQualified() {
        return qualifier != null;
    }

    public boolean isProperNoun() {
        return properNoun;
    }

    public void setProperNoun( boolean val ) {
        properNoun = val;
    }

    /**
     * Whether this is a common noun.
     *
     * @return a boolean
     */
    public boolean isCommonNoun() {
        return !properNoun && isNoun();
    }

    /**
     * Whether this is a noun.
     *
     * @return a boolean
     */
    public boolean isNoun() {
        return hasPOS( POS.NOUN );
    }

    /**
     * Whether this is a verb.
     *
     * @return a boolean
     */
    public boolean isVerb() {
        return hasPOS( POS.VERB );
    }

    /**
     * Whether this is an adjective.
     *
     * @return a boolean
     */
    public boolean isAdjective() {
        return hasPOS( POS.ADJECTIVE );
    }

    /**
     * Whether this is an adverb.
     *
     * @return a boolean
     */
    public boolean isAdverb() {
        return hasPOS( POS.ADVERB );
    }

    private boolean hasPOS( final POS pos ) {
        return asPOS( pos ) != null;
    }

    /**
     * Return the meaning, if any, associated with a part-of-speech.
     *
     * @param pos part-of-speech
     * @return a meaning
     */
    public Meaning asPOS( final POS pos ) {
        return (Meaning) CollectionUtils.find(
                meanings,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return ( (Meaning) obj ).getPos().equals( pos );
                    }
                }
        );
    }

    /**
     * Whether this is not a known part-of-speech.
     *
     * @return a boolean
     */

    public boolean isUnknown() {
        return !properNoun && meanings.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( word );
        sb.append( "[ " );
        if ( properNoun ) {
            sb.append( "Proper " );
        }
        for ( Meaning meaning : meanings ) {
            sb.append( meaning.getLemma() );
            sb.append( '/' );
            sb.append( meaning.getPos().getLabel() );
            sb.append( ' ' );
        }
        sb.append( ']' );
        return sb.toString();
    }

    /**
     * Get all synsets from all meanings.
     *
     * @return a list of synsets
     * @throws JWNLException if fails
     */
    public List<Synset> getSynsets() throws JWNLException {
        List<Synset> synsets = new ArrayList<Synset>();
        for ( Meaning meaning : meanings ) {
            synsets.addAll( meaning.getSynsets() );
        }
        return synsets;
    }

    /**
     * Whether the word has any meaning.
     *
     * @return a boolean
     */
    public boolean hasMeaning() {
        return !meanings.isEmpty();
    }

    /**
     * Get the lemma from the noun part-of-speech.
     *
     * @return a string
     */
    public String getNoun() {
        Meaning meaning = asPOS( POS.NOUN );
        if ( meaning == null ) {
            return null;
        } else {
            return meaning.getLemma();
        }
    }

    /**
     * Whether can have its similarity to another pos word assessed.
     *
     * @return a boolean
     */
    public boolean isComparable() {
        return isNoun() || isVerb();
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = 1;
        if ( word != null ) hash = hash * 31 + word.hashCode();
        hash = hash * 31 + Boolean.valueOf( properNoun ).hashCode();
        for ( Meaning meaning : meanings ) {
            hash = hash * 31 + meaning.hashCode();
        }
        if ( qualifier != null ) hash = hash * 31 + qualifier.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj ) {
        if ( !( obj instanceof POSWord ) ) return false;
        POSWord other = (POSWord) obj;
        if ( word != null ) {
            if ( other.getWord() == null || !word.equals( other.getWord() ) ) return false;
        } else {
            if ( other.getWord() != null ) return false;
        }
        if ( !CollectionUtils.isEqualCollection( getMeanings(), other.getMeanings() ) ) return false;
        if ( qualifier != null ) {
            if ( other.getQualifier() == null || !qualifier.equals( other.getQualifier() ) ) return false;
        } else {
            if ( other.getQualifier() != null ) return false;
        }
        return true;
    }
}
