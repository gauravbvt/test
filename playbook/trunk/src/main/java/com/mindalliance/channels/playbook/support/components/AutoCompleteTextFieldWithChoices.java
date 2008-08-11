package com.mindalliance.channels.playbook.support.components;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems.
 * All Rights Reserved. Proprietary and Confidential.
 * User: jf
 * Date: May 13, 2008
 * Time: 8:26:52 PM
 */
public class AutoCompleteTextFieldWithChoices
        extends AutoCompleteTextField<String> {

    private IModel<List<String>> choicesModel;
    private static final long serialVersionUID = 1931789431136750779L;

    public AutoCompleteTextFieldWithChoices(
            String s, IModel<String> iModel,
            IModel<List<String>> choicesModel ) {
        super( s, iModel );
        this.choicesModel = choicesModel;
    }

    @Override
    protected Iterator<String> getChoices( String input ) {
        return choicesIterator( input, 10 );
    }

    private Iterator<String> choicesIterator( String input, int max ) {
        Iterable<String> results =
                choicesModel.getObject();   // is sorted on frequency
        List<String> choices = new ArrayList<String>();
        for ( String s : results ) {
            if ( s.toLowerCase().startsWith( input.toLowerCase() ) ) {
                choices.add( s );
                if ( choices.size() >= max )
                    break;
            }
        }
        return choices.iterator();
    }
}
