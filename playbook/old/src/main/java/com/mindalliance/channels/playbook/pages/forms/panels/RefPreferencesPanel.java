package com.mindalliance.channels.playbook.pages.forms.panels;

import com.mindalliance.channels.playbook.pages.filters.DynamicFilterTree;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.forms.AbstractPlaybookPanel;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.RefUtils;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import com.mindalliance.channels.playbook.support.renderers.RefChoiceRenderer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 *
 * User: jf
 * Date: May 14, 2008
 * Time: 11:28:51 AM
 */
public class RefPreferencesPanel extends AbstractComponentPanel {

    IModel choices;
    DynamicFilterTree choicesTree;
    ListChoice<Ref> preferredList;
    Button addPreferredButton;
    Button removePreferred;
    Button upPreferredButton;
    Button downPreferredButton;
    Button removePreferredButton;
    List<Ref> selectedElements = new ArrayList<Ref>();
    Ref selectedPreferred;
    private static final long serialVersionUID = -2926908548203293852L;

    public RefPreferencesPanel(
            String id, AbstractPlaybookPanel parentPanel, String propPath,
            IModel choices ) {
        super( id, parentPanel, propPath );
        this.choices = choices;
        doLoad();
    }

    protected void doLoad() {
        super.load();
        choicesTree = new DynamicFilterTree(
                "choices",
                new RefPropertyModel( getElement(), propPath ),
                choices ) {
            private static final long serialVersionUID = 49805989865151100L;

            @Override
            public void onFilterSelect(
                    AjaxRequestTarget target, Filter filter ) {
                selectedElements = new ArrayList<Ref>();
                Collection<Ref> preferred = getPreferred();
                for ( Ref type : choicesTree.getNewSelections() ) {
                    if ( !preferred.contains( type ) )
                        selectedElements.add( type );
                }
                updateAddButtonVisibility( target );
            }
        };
        addReplaceable( choicesTree );
        addPreferredButton = new Button( "addPreferred" );
        addPreferredButton.add(
                new AjaxEventBehavior( "onclick" ) {
                    private static final long serialVersionUID =
                            -9056387399079472793L;

                    @Override
                    protected void onEvent( AjaxRequestTarget target ) {
                        for ( Object selected : selectedElements ) {
                            RefUtils.add( getElement(), propPath, selected );
                        }
                        target.addComponent( preferredList );
                        selectedElements = new ArrayList<Ref>();
                        updateAddButtonVisibility( target );
                        updateUpDownRemoveButtonsVisibility( target );
                    }
                } );
        addPreferredButton.setEnabled( false );
        addReplaceable( addPreferredButton );
        preferredList = new ListChoice<Ref>(
                "preferredElements",
                new Model<Ref>(),
                new RefPropertyModel( getElement(), propPath ),
                new RefChoiceRenderer( "name", "id" ) );
        preferredList.add(
                new AjaxFormComponentUpdatingBehavior( "onchange" ) {
                    private static final long serialVersionUID =
                            7413102140114265831L;

                    @Override
                    protected void onUpdate( AjaxRequestTarget target ) {
                        selectedPreferred = preferredList.getModelObject();
                        updateUpDownRemoveButtonsVisibility( target );
                    }
                } );
        preferredList.setMaxRows( 4 );
        addReplaceable( preferredList );
        upPreferredButton = new Button( "upPreferred" );
        upPreferredButton.add(
                new AjaxEventBehavior( "onclick" ) {
                    private static final long serialVersionUID =
                            2393493794523020893L;

                    @Override
                    protected void onEvent( AjaxRequestTarget target ) {
                        List<Ref> preferred = getPreferred();
                        int index = preferred.indexOf( selectedPreferred );
                        if ( index > 0 ) {
                            preferred.remove( index );
                            preferred.add( index - 1, selectedPreferred );
                            elementChanged( propPath, target );
                        }
                        target.addComponent( preferredList );
                        updateUpDownRemoveButtonsVisibility( target );
                    }
                } );
        addReplaceable( upPreferredButton );
        downPreferredButton = new Button( "downPreferred" );
        downPreferredButton.add(
                new AjaxEventBehavior( "onclick" ) {
                    private static final long serialVersionUID =
                            -8951528362891023162L;

                    @Override
                    protected void onEvent( AjaxRequestTarget target ) {
                        List<Ref> preferred = getPreferred();
                        int index = preferred.indexOf( selectedPreferred );
                        if ( index != preferred.size() - 1 ) {
                            preferred.remove( index );
                            preferred.add( index + 1, selectedPreferred );
                            elementChanged( propPath, target );
                        }
                        target.addComponent( preferredList );
                        updateUpDownRemoveButtonsVisibility( target );
                    }
                } );
        downPreferredButton.setEnabled( false );
        addReplaceable( downPreferredButton );
        removePreferredButton = new Button( "removePreferred" );
        removePreferredButton.add(
                new AjaxEventBehavior( "onclick" ) {
                    private static final long serialVersionUID =
                            -6177718716106446185L;

                    @Override
                    protected void onEvent( AjaxRequestTarget target ) {
                        RefUtils.remove(
                                getElement(), propPath, selectedPreferred );
                        elementChanged( propPath, target );
                        selectedPreferred = null;
                        target.addComponent( preferredList );
                        updateUpDownRemoveButtonsVisibility( target );
                    }
                } );
        removePreferredButton.setEnabled( false );
        addReplaceable( removePreferredButton );
    }

    private void updateUpDownRemoveButtonsVisibility(
            AjaxRequestTarget target ) {
        if ( selectedPreferred == null ) {
            downPreferredButton.setEnabled( false );
            upPreferredButton.setEnabled( false );
        } else {
            List<Ref> preferred = getPreferred();
            int index = preferred.indexOf( selectedPreferred );
            upPreferredButton.setEnabled( index != 0 );
            downPreferredButton.setEnabled( index < preferred.size() - 1 );
        }
        removePreferredButton.setEnabled( selectedPreferred != null );
        target.addComponent( downPreferredButton );
        target.addComponent( upPreferredButton );
        target.addComponent( removePreferredButton );
    }

    private void updateAddButtonVisibility( AjaxRequestTarget target ) {
        Collection<Ref> preferred = getPreferred();
        boolean enabled = false;
        for ( Object type : selectedElements ) {
            if ( !preferred.contains( type ) ) {
                enabled = true;
                break;
            }
        }
        addPreferredButton.setEnabled( enabled );
        target.addComponent( addPreferredButton );
    }

    @SuppressWarnings( { "unchecked" } )
    private List<Ref> getPreferred() {
        return (List<Ref>) getComponent();
    }
}
