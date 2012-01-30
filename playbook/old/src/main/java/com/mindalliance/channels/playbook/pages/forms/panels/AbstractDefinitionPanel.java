package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.Component;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved. Proprietary
 * and Confidential.
 * <p/>
 * User: jf Date: Jul 1, 2008 Time: 12:47:36 PM
 */
public abstract class AbstractDefinitionPanel extends AbstractComponentPanel {

    protected static final int MAX_CHOICE_ROWS = 3;
    private static final long serialVersionUID = 1527943895824239414L;

    protected AbstractDefinitionPanel(
            String id, AbstractPlaybookPanel parentPanel, String propPath ) {
        super( id, parentPanel, propPath );
    }

    @Override
    protected void load() {
        super.load();
        Component descriptionField = new TextArea<String>(
                "description",
                new RefPropertyModel<String>(
                        getElement(),
                        propPath + ".description" ) );
        
        descriptionField.add(
                new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    private static final long serialVersionUID = -1L;

                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        elementChanged( propPath + ".description", target );
                    }
                } );
        addReplaceable( descriptionField );
    }
}
