package com.mindalliance.channels.playbook.support.components;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.model.IModel;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import com.mindalliance.channels.playbook.query.Query;
import com.mindalliance.channels.playbook.ifm.Channels;
import com.mindalliance.channels.playbook.support.models.RefQueryModel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: May 13, 2008
 * Time: 8:26:52 PM
 */
public class AutoCompleteTextFieldWithChoices extends AutoCompleteTextField {

    IModel choices;

    public AutoCompleteTextFieldWithChoices(String s, IModel iModel, IModel choices) {
        super(s, iModel);
        this.choices = choices;
    }

    protected Iterator getChoices(String input) {
        return choicesIterator(input, 10);
    }

    private Iterator choicesIterator(String input, int max) {
         List<String> results = (List<String>)choices.getObject();    // is sorted on frequency
         List<String> choices = new ArrayList<String>();
         for (String s : results) {
             if (s.toLowerCase().startsWith(input.toLowerCase())) {
                 choices.add(s);
                 if (choices.size() >= max) break;
             }
         }
         return choices.iterator();
     }

}
