package com.mindalliance.channels.nlp;

import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A meaning of a word.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 4, 2009
 * Time: 2:53:18 PM
 */
public class Meaning implements Serializable {
    /**
     * A lemma.
     */
    private String lemma;
    /**
     * A part-of-speech.
     */
    private POS pos;
    /**
     * List of synsets.
     */
    private List<Synset> synsets = new ArrayList<Synset>();

    /**
     * Create meaning from WordNet index word.
     *
     * @param indexWord an index word
     * @return a meaning
     * @throws JWNLException if fails
     */
    public static Meaning from( IndexWord indexWord ) throws JWNLException {
        Meaning meaning = new Meaning();
        meaning.setLemma( indexWord.getLemma() );
        meaning.setPos( indexWord.getPOS() );
        meaning.setSynsets( Arrays.asList( indexWord.getSenses() ) );
        return meaning;
    }

    /**
     * Create meaning from index word.
     *
     * @param word a WordNet word
     * @return a meaning
     */
    public static Meaning from( Word word ) {
        Meaning meaning = new Meaning();
        meaning.setLemma( word.getLemma() );
        meaning.setPos( word.getPOS() );
        meaning.setSynsets( Arrays.asList( word.getSynset() ) );
        return meaning;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma( String lemma ) {
        this.lemma = lemma;
    }

    public POS getPos() {
        return pos;
    }

    public void setPos( POS pos ) {
        this.pos = pos;
    }

    public List<Synset> getSynsets() {
        return synsets;
    }

    public void setSynsets( List<Synset> synsets ) {
        this.synsets = synsets;
    }

    public String toString() {
        return lemma + "/" + pos.getLabel();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj ) {
        if ( !( obj instanceof Meaning ) ) return false;
        Meaning other = (Meaning) obj;
        if ( lemma != null ) {
            if ( other.getLemma() == null || !lemma.equals( other.getLemma() ) ) return false;
        } else {
            if ( other.getLemma() != null ) return false;
        }
        if ( pos != null ) {
            if ( other.getPos() == null || !pos.equals( other.getPos() ) ) return false;
        } else {
            if ( other.getPos() != null ) return false;
        }
        return CollectionUtils.isEqualCollection( synsets, other.getSynsets() );
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = 1;
        if ( lemma != null ) hash = hash * 31 + lemma.hashCode();
        if ( pos != null ) hash = hash * 31 + pos.hashCode();
        for ( Synset synset : synsets ) {
            hash = hash * 31 + synset.hashCode();
        }
        return hash;
    }
}