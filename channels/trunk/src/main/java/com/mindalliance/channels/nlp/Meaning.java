package com.mindalliance.channels.nlp;

import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;

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
    public class Meaning {
        private String lemma;
        private POS pos;
        private List<Synset> synsets;

    public static Meaning from( IndexWord indexWord ) throws JWNLException {
        Meaning meaning = new Meaning();
        meaning.setLemma(indexWord.getLemma());
        meaning.setPos(indexWord.getPOS());
        meaning.setSynsets(Arrays.asList( indexWord.getSenses() ));
        return meaning;
    }

    public static Meaning from( Word word ) {
        Meaning meaning = new Meaning();
        meaning.setLemma(word.getLemma());
        meaning.setPos(word.getPOS());
        meaning.setSynsets(Arrays.asList( word.getSynset() ));
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
}