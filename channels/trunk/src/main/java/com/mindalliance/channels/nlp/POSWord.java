package com.mindalliance.channels.nlp;

import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

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
public class POSWord {

    private String word;
    private List<Meaning> meanings = new ArrayList<Meaning>();
    private boolean properNoun;
    private POSWord qualifier;

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

    public void addMeaning( Meaning meaning ) {
        if ( meaning != null ) {
            meanings.add( meaning );
        }
    }

    public void addMeaning( IndexWord indexWord ) throws JWNLException {
        if ( indexWord != null ) {
            meanings.add( Meaning.from( indexWord ) );
        }
    }

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

    public boolean isCommonNoun() {
        return !properNoun && isNoun();
    }

    public boolean isNoun() {
        return hasPOS( POS.NOUN );
    }

    public boolean isVerb() {
        return hasPOS( POS.VERB );
    }

    public boolean isAdjective() {
        return hasPOS( POS.ADJECTIVE );
    }

    public boolean isAdverb() {
        return hasPOS( POS.ADVERB );
    }

    private boolean hasPOS( final POS pos ) {
        return asPOS( pos ) != null;
    }

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

    public boolean isUnknown() {
        return !properNoun && meanings.isEmpty();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( word );
        sb.append( "[ " );
        if ( properNoun ) {
            sb.append("Proper ");
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

    public List<Synset> getSynsets() throws JWNLException {
        List<Synset> synsets = new ArrayList<Synset>();
        for ( Meaning meaning : meanings ) {
            synsets.addAll( meaning.getSynsets() );
        }
        return synsets;
    }

    public boolean hasMeaning() {
        return !meanings.isEmpty();
    }

    public String getNoun() {
        Meaning meaning = asPOS(POS.NOUN);
        if (meaning == null) {
            return null;
        } else {
            return meaning.getLemma();
        }
    }

    public boolean isComparable() {
        return isNoun() || isVerb();
    }
}
