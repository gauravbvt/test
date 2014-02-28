package com.mindalliance.channels.pages.components.community.requirements;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateModelObject;
import com.mindalliance.channels.core.model.Requirement;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/3/11
 * Time: 3:23 PM
 */
public class CardinalityRequiredPanel extends AbstractCommandablePanel {

    private final String cardinalityProperty;
    private TextField<String> minCountField;
    private TextField<String> safeCountField;
    private TextField<String> maxCountField;

    public CardinalityRequiredPanel(
            String id,
            IModel<Requirement> requirementModel,
            String cardinalityProperty ) {
        super( id, requirementModel );
        this.cardinalityProperty = cardinalityProperty;
        init();
    }

    private void init() {
        addMinCount();
        addSafeCount();
        addMaxCount();
        adjustFields();
    }

    private void adjustFields() {
        minCountField.setEnabled( isLockedByUser( getRequirement() ) );
        safeCountField.setEnabled( isLockedByUser( getRequirement() ) );
        maxCountField.setEnabled( isLockedByUser( getRequirement() ) );
    }

    private void updateFields( AjaxRequestTarget target ) {
        init();
        target.add( this );
    }

    private void addMinCount() {
        minCountField = new TextField<String>(
                "minCount",
                new PropertyModel<String>( this, "minCount" ) );
        minCountField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateFields( target );
                update( target, new Change( Change.Type.Updated, getRequirement(), cardinalityProperty ) );
            }
        } );
        minCountField.setOutputMarkupId( true );
        addOrReplace( minCountField );
    }

    private void addSafeCount() {
        safeCountField = new TextField<String>(
                "safeCount",
                new PropertyModel<String>( this, "safeCount" ) );
        safeCountField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateFields( target );
                update( target, new Change( Change.Type.Updated, getRequirement(), cardinalityProperty ) );
            }
        } );
        safeCountField.setOutputMarkupId( true );
        addOrReplace( safeCountField );
    }

    private void addMaxCount() {
        maxCountField = new TextField<String>(
                "maxCount",
                new PropertyModel<String>( this, "maxCount" ) );
        maxCountField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                updateFields( target );
                update( target, new Change( Change.Type.Updated, getRequirement(), cardinalityProperty ) );
            }
        } );
        maxCountField.setOutputMarkupId( true );
        addOrReplace( maxCountField );
    }

    public String getMinCount() {
        int minCount = getCardinality().getMinCount();
        return Integer.toString( minCount );
    }

    public void setMinCount( String val ) {
        try {
            int minCount = ( val == null || val.trim().isEmpty() ) ? 0 : Integer.parseInt( val );
            doCommand( new UpdateModelObject(
                    getUsername(),
                    getRequirement(),
                    cardinalityProperty + ".minCount", minCount ) );
        } catch ( NumberFormatException e ) {
            // do nothing
        }
    }

    public String getSafeCount() {
        Integer safeCount = getCardinality().getSafeCount();
        return safeCount == null ? "" : Integer.toString( safeCount );
    }

    public void setSafeCount( String val ) {
        try {
            Integer safeCount = ( val == null || val.trim().isEmpty() ) ? 0 : Integer.parseInt( val );
            doCommand( new UpdateModelObject(
                    getUsername(),
                    getRequirement(),
                    cardinalityProperty + ".safeCount", safeCount ) );
        } catch ( NumberFormatException e ) {
            // do nothing
        }
    }

    public String getMaxCount() {
        Integer maxCount = getCardinality().getMaxCount();
        return maxCount == null ? "" : Integer.toString( maxCount );
    }

    public void setMaxCount( String val ) {
        try {
            Integer maxCount = ( val == null || val.trim().isEmpty() ) ? null : Integer.parseInt( val );
            doCommand( new UpdateModelObject(
                    getUsername(),
                    getRequirement(),
                    cardinalityProperty + ".maxCount", maxCount ) );
        } catch ( NumberFormatException e ) {
            // do nothing
        }

    }


    private Requirement getRequirement() {
        return (Requirement) getModel().getObject();
    }

    private Requirement.Cardinality getCardinality() {
        return (Requirement.Cardinality) ChannelsUtils.getProperty(
                getRequirement(),
                cardinalityProperty,
                null );
    }

}
