package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.Level;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.pages.components.IssuesPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
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
    private PartSummaryPanel summaryPanel;
    /**
     * Instructions label.
     */
    private Label descriptionLabel;
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
        addSummaryPanel();
        addDescriptionLabel();
        addIssuesPanel();
        adjustFields();
    }

    private void addSummaryPanel() {
        summaryPanel = new PartSummaryPanel( "partSummary", new PropertyModel<Part>( this, "part" ) );
        summaryPanel.setOutputMarkupId( true );
        summaryPanel.add( new AjaxEventBehavior( "onclick" ) {
            protected void onEvent( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Expanded, getPart() ) );
            }
        } );
        summaryPanel.add( new AttributeModifier( "class", true, new Model<String>( getCssClasses() ) ) );
        addOrReplace( summaryPanel );
    }

    private String getCssClasses() {
        Level priority = getQueryService().computePartPriority( getPart() );
        return "summary " + priority.getNegativeLabel().toLowerCase();
    }

    private void addDescriptionLabel() {
        descriptionLabel = new Label( "description", new PropertyModel( getPart(), "description" ) );
        descriptionLabel.setOutputMarkupId( true );
        addOrReplace( descriptionLabel );
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
        target.addComponent( descriptionLabel );
        target.addComponent( summaryPanel );
        addIssuesPanel();
        adjustFields();
        target.addComponent( partIssuesPanel );
    }
}
