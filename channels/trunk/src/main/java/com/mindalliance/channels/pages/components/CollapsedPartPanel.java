package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Part;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.Set;

/**
 * Collapsed part panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Dec 28, 2009
 * Time: 7:38:19 PM
 */
public class CollapsedPartPanel extends AbstractUpdatablePanel {
    /**
     * Summary label.
     */
    private Label summaryLabel;
    /**
     * Part issues panel.
     */
    private IssuesPanel partIssuesPanel;

    public CollapsedPartPanel( String id, IModel<? extends Identifiable> model, Set<Long> expansions ) {
        super( id, model, expansions );
        init();
    }

    private void init() {
        setOutputMarkupId( true );
        addSummaryLabel();
        addIssuesPanel();
        adjustFields();
    }

    private void addSummaryLabel() {
        summaryLabel = new Label( "partSummary", new PropertyModel( getPart(), "summary" ) );
        addOrReplace( summaryLabel );
    }

    private void addIssuesPanel() {
        partIssuesPanel = new IssuesPanel(
                "issues",
                new PropertyModel<Part>( this, "part" ),
                getExpansions() );
        partIssuesPanel.setOutputMarkupId( true );
        addOrReplace( partIssuesPanel );
    }

    private void adjustFields() {
        boolean partHasIssues = getAnalyst().hasIssues( getPart(), false );
        makeVisible( partIssuesPanel, partHasIssues );
    }

    public Part getPart() {
        return (Part) getModel().getObject();
    }

    /**
     * {@inheritDoc}
     */
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        target.addComponent( summaryLabel );
        addIssuesPanel();
        adjustFields();
        target.addComponent( partIssuesPanel );
    }
}
