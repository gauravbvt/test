package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.components.AutoCompleteTextFieldWithChoices;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved. Proprietary
 * and Confidential. User: jf Date: May 13, 2008 Time: 9:52:16 PM
 */
public class MultipleStringChooser extends AbstractComponentPanel {

    private static final int MAX_SIZE = 30;
    private static final int MAX_ROWS = 4;
    protected IModel<List<String>> choices;
    protected AutoCompleteTextFieldWithChoices newStringField;
    protected Button addStringButton;
    protected ListChoice<String> stringsChoice;
    protected String selectedString;
    protected String newString = "";
    protected Button deleteStringButton;
    protected Label fullStringLabel;
    private static final long serialVersionUID = -5125625685931267940L;

    public MultipleStringChooser(
            String id, AbstractPlaybookPanel parentPanel, String propPath,
            IModel<List<String>> choices ) {
        super( id, parentPanel, propPath );
        this.choices = choices;
        doLoad();
    }

    protected void doLoad() {
        newStringField = new AutoCompleteTextFieldWithChoices(
                "string", new Model<String>(), choices );
        newStringField.add(
                new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    private static final long serialVersionUID =
                            2678909912270663401L;

                    @Override
                    protected void onUpdate(
                            AjaxRequestTarget target ) {
                        newString = RefUtils.summarize(
                                newStringField.getDefaultModelObjectAsString(),
                                Integer.MAX_VALUE );
                    }
                } );
        addReplaceable( newStringField );
        addStringButton = new Button( "addString" );
        addStringButton.add(
                new AjaxEventBehavior( "onclick" ) {
                    private static final long serialVersionUID =
                            -9154498254683042114L;

                    @Override
                    protected void onEvent( AjaxRequestTarget target ) {
                        if ( !newString.isEmpty() ) {
                            RefUtils.add( getElement(), propPath, newString );
                            newStringField.setModelObject( "" );
                            newString = "";
                            target.addComponent( newStringField );
                            target.addComponent( stringsChoice );
                        }
                    }
                } );
        addReplaceable( addStringButton );
        stringsChoice = new ListChoice<String>(
                "strings",
                new Model<String>(),
                new RefPropertyModel( getElement(), propPath ),
                new ChoiceRenderer<String>() {
                    private static final long serialVersionUID =
                            -1212581560145345612L;

                    @Override
                    public String getDisplayValue( String object ) {
                        return RefUtils.summarize( object, MAX_SIZE );
                    }
                } );
        stringsChoice.setMaxRows( MAX_ROWS );
        stringsChoice.add(
                new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    private static final long serialVersionUID =
                            842100373028139937L;

                    @Override
                    protected void onUpdate(
                            AjaxRequestTarget target ) {
                        selectedString =
                                stringsChoice.getDefaultModelObjectAsString();
                        updateFullStringLabel( target );
                        updateDeleteButton( target );
                    }
                } );
        addReplaceable( stringsChoice );
        deleteStringButton = new Button( "deleteString" );
        deleteStringButton.add(
                new AjaxEventBehavior( "onclick" ) {
                    private static final long serialVersionUID =
                            -8735097052415750985L;

                    @Override
                    protected void onEvent( AjaxRequestTarget target ) {
                        if ( selectedString != null ) {
                            RefUtils.remove(
                                    getElement(), propPath, selectedString );
                            newString = selectedString;
                            newStringField.setModelObject( selectedString );
                            selectedString = null;
                            updateFullStringLabel( target );
                            updateDeleteButton( target );
                            target.addComponent( newStringField );
                            target.addComponent( stringsChoice );
                        }
                    }
                } );
        deleteStringButton.setEnabled( false );
        addReplaceable( deleteStringButton );
        fullStringLabel = new Label( "fullString", new Model() );
        hide( fullStringLabel );
        addReplaceable( fullStringLabel );
    }

    private void updateFullStringLabel( AjaxRequestTarget target ) {
        fullStringLabel.setDefaultModelObject( selectedString );
        setVisibility( fullStringLabel, selectedString != null, target );
    }

    private void updateDeleteButton( AjaxRequestTarget target ) {
        deleteStringButton.setEnabled( selectedString != null );
        target.addComponent( deleteStringButton );
    }
}
