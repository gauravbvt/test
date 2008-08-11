package com.mindalliance.channels.playbook.pages.forms.tabs.invalid;

import com.mindalliance.channels.playbook.pages.forms.tabs.analysisElement.AnalysisElementBasicTab;
import com.mindalliance.channels.playbook.pages.forms.AbstractElementForm;
import com.mindalliance.channels.playbook.analysis.problem.Invalidation;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jul 16, 2008
 * Time: 3:16:42 PM
 */
public class InvalidBasicTab extends AnalysisElementBasicTab {

    private Invalidation invalid;
    private static final long serialVersionUID = 9193448169954816664L;

    public InvalidBasicTab( String id, AbstractElementForm elementForm ) {
        super( id, elementForm );
    }

    @Override
    protected void load() {
        super.load();
        invalid = (Invalidation) getElement().deref();

        AjaxLink<?> elementLink = new AjaxLink( "elementLink" ) {
            private static final long serialVersionUID = 3615464809321222524L;

            @Override
            public void onClick( AjaxRequestTarget target ) {
                edit( invalid.getElement().getReference(), target );
            }
        };
        elementLink.add(
                new Label(
                        "element",
                        new Model<String>( invalid.getElement().toString() ) ) );
        addReplaceable( elementLink );

        addReplaceable(
                new Label(
                        "tag", new Model<String>( invalid.labelText() ) ) );
    }
}
