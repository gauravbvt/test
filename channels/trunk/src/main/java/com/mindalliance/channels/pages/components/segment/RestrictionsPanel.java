package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.command.commands.UpdateSegmentObject;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.TabIndexable;
import com.mindalliance.channels.pages.components.TabIndexer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Flow restrictions editing panel.
 * Assumes that flow is editable and locked by user.
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 6/2/13
 * Time: 10:59 AM
 */
public class RestrictionsPanel extends AbstractCommandablePanel implements TabIndexable {

    private IModel<Flow> flowModel;
    private boolean isSend;
    private WebMarkupContainer restrictionsEditorContainer;
    private DropDownChoice<Flow.Restriction> newRestrictionChoice;
    private TabIndexer tabIndexer;


    public RestrictionsPanel( String id, IModel<Flow> flowModel, boolean isSend ) {
        super( id, flowModel );
        this.flowModel = flowModel;
        this.isSend = isSend;
        init();
    }

    @Override
    public void initTabIndexing( TabIndexer tabIndexer ) {
        this.tabIndexer = tabIndexer;
        tabIndexer.giveTabIndexTo( newRestrictionChoice );
    }

    private void init() {
        restrictionsEditorContainer = new WebMarkupContainer( "restrictionsEditorContainer" );
        restrictionsEditorContainer.setOutputMarkupId( true );
        add( restrictionsEditorContainer );
        addRestrictionsList();
        addNewRestrictionChoice();
    }

    private void addRestrictionsList() {
        ListView<Flow.Restriction> restrictionsListView = new ListView<Flow.Restriction>(
                "restrictions",
                getFlow().getRestrictions()
        ) {
            @Override
            protected void populateItem( ListItem<Flow.Restriction> item ) {
                final Flow.Restriction restriction = item.getModelObject();
                // name
                Label nameLabel = new Label( "name", restriction.getLabel( isSend ) );
                item.add( nameLabel );
                // delete
                WebMarkupContainer deleteImg = new WebMarkupContainer( "delete" );
                deleteImg.add( new AjaxEventBehavior( "onclick" ) {
                    @Override
                    protected void onEvent( AjaxRequestTarget target ) {
                        removeRestriction( restriction );
                        restrictionsChanged( target );
                    }
                } );
                item.add( deleteImg );
            }
        };
        restrictionsListView.setOutputMarkupId( true );
        restrictionsEditorContainer.addOrReplace( restrictionsListView );
    }

    private void restrictionsChanged( AjaxRequestTarget target ) {
        addRestrictionsList();
        addNewRestrictionChoice();
        target.add( restrictionsEditorContainer );
        target.add( newRestrictionChoice );
        update( target, new Change( Change.Type.Updated, getFlow(), "restrictions" ) );
    }

    private void removeRestriction( Flow.Restriction restriction ) {
        doCommand(
                new UpdateSegmentObject( getUsername(),
                        getFlow(),
                        "restrictions",
                        restriction,
                        UpdateObject.Action.Remove ) );
    }

    private void addNewRestrictionChoice() {
        newRestrictionChoice = new DropDownChoice<Flow.Restriction>(
                "newRestriction",
                new PropertyModel<Flow.Restriction>( this, "newRestriction" ),
                getOtherRestrictions(),
                new ChoiceRenderer<Flow.Restriction>() {
                    @Override
                    public Object getDisplayValue( Flow.Restriction restriction ) {
                        return restriction.getLabel( isSend );
                    }
                }
        );
        newRestrictionChoice.setOutputMarkupId( true );
        newRestrictionChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                restrictionsChanged( target );
            }
        } );
        addOrReplace( newRestrictionChoice );
        applyTabIndexTo( newRestrictionChoice, tabIndexer );
    }

    private List<Flow.Restriction> getOtherRestrictions() {
        List<Flow.Restriction> otherRestrictions = new ArrayList<Flow.Restriction>();
        List<Flow.Restriction> setRestrictions = getFlow().getRestrictions();
        for ( final Flow.Restriction restriction : Flow.Restriction.values() ) {
            if ( !setRestrictions.contains( restriction ) &&
                    !CollectionUtils.exists(
                            setRestrictions,
                            new Predicate() {
                                @Override
                                public boolean evaluate( Object object ) {
                                    Flow.Restriction otherRestriction = (Flow.Restriction) object;
                                    return otherRestriction.contradicts( restriction ) ||
                                            Flow.Restriction.implies( restriction, otherRestriction );
                                }
                            }
                    ) ) {
                otherRestrictions.add( restriction );
            }
        }
        Collections.sort(
                otherRestrictions,
                new Comparator<Flow.Restriction>() {
                    @Override
                    public int compare( Flow.Restriction r1, Flow.Restriction r2 ) {
                        return r1.getLabel( isSend ).compareTo( r2.getLabel( isSend ) );
                    }
                } );
        return otherRestrictions;
    }

    public Flow.Restriction getNewRestriction() {
        return null;
    }

    public void setNewRestriction( Flow.Restriction restriction ) {
        if ( restriction != null && !getFlow().getRestrictions().contains( restriction ) )
            doCommand(
                    new UpdateSegmentObject( getUsername(),
                            getFlow(),
                            "restrictions",
                            restriction,
                            UpdateObject.Action.AddUnique ) );
    }

    private Flow getFlow() {
        return flowModel.getObject();
    }
}
