package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Nov 5, 2009
 * Time: 9:01:12 AM
 */
public class FlowEOIsPanel extends FloatingCommandablePanel {

    /**
     * Pad top on move.
     */
    private static final int PAD_TOP = 68;
    /**
     * Pad left on move.
     */
    private static final int PAD_LEFT = 5;
    /**
     * Pad bottom on move and resize.
     */
    private static final int PAD_BOTTOM = 5;
    /**
     * Pad right on move and resize.
     */
    private static final int PAD_RIGHT = 6;
    /**
     * Min width on resize.
     */
    private static final int MIN_WIDTH = 500;
    /**
     * Min height on resize.
     */
    private static final int MIN_HEIGHT = 300;
    /**
     * EOIs list container.
     */
    private WebMarkupContainer eoisContainer;

    public FlowEOIsPanel( String id, Model<Flow> flowModel, Set<Long> expansions ) {
        super( id, flowModel, expansions );
        init();
    }

    private void init() {
        eoisContainer = new WebMarkupContainer( "eoisContainer" );
        eoisContainer.setOutputMarkupId( true );
        add( eoisContainer );
        addAboutFlow();
        addEOIs();
    }

    private void addAboutFlow() {
        Label flowTypeLabel = new Label(
                "flowType",
                new Model<String>( getFlow().isNeed()
                        ? "need for"
                        : getFlow().isCapability()
                        ? "availability of"
                        : "sharing of" ) );
        add( flowTypeLabel );
        Label flowNameLabel = new Label( "flowName", new Model<String>( getFlow().getName() ) );
        add( flowNameLabel );
        Label sourceLabel = new Label(
                "source",
                new Model<String>( getFlow().isNeed()
                        ? ""
                        : getFlow().isCapability()
                        ? "from " + "\"" + getFlow().getSource().getTitle() + "\""
                        : " by " + "\"" + getFlow().getSource().getTitle() + "\""
                )
        );
        makeVisible( sourceLabel, !getFlow().isNeed() );
        add( sourceLabel );
        Label targetLabel = new Label(
                "target",
                new Model<String>( getFlow().isCapability()
                        ? ""
                        : getFlow().isNeed()
                        ? "by " + "\"" + getFlow().getTarget().getTitle() + "\""
                        : "with " + "\"" + getFlow().getTarget().getTitle() + "\""
                )
        );
        makeVisible( targetLabel, !getFlow().isCapability() );
        add( targetLabel );
    }

    private void addEOIs() {
        ListView<EOIWrapper> eoisListView = new ListView<EOIWrapper>(
                "eois",
                getWrappers()
        ) {
            protected void populateItem( ListItem<EOIWrapper> item ) {
                item.setOutputMarkupId( true );
                addConfirmed( item );
                addContent( item );
                addClassifications( item );
                addSourcing( item );
                addSpecialHandling( item );
            }
        };
        eoisContainer.addOrReplace( eoisListView );
    }

    private void addConfirmed( ListItem<EOIWrapper> item ) {
        EOIWrapper wrapper = item.getModelObject();
        CheckBox confirmedCheckBox = new CheckBox(
                "confirmed",
                new PropertyModel<Boolean>( wrapper, "confirmed" )
        );
        confirmedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addEOIs();
                target.addComponent( eoisContainer );
                update( target, new Change( Change.Type.Updated, getFlow(), "eois" ) );
            }
        } );
        makeVisible( confirmedCheckBox, !wrapper.isMarkedForCreation() && !isReadOnly() );
        item.addOrReplace( confirmedCheckBox );
    }

    private void addContent( ListItem<EOIWrapper> item ) {
        final EOIWrapper wrapper = item.getModelObject();
        TextField<String> contentText = new TextField<String>(
                "content",
                new PropertyModel<String>( wrapper, "content" )
        );
        contentText.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                if ( wrapper.isMarkedForCreation() ) {
                    addEOIs();
                    target.addComponent( eoisContainer );
                }
                update( target, new Change( Change.Type.Updated, getFlow(), "eois" ) );
            }
        } );
        contentText.setEnabled( !isReadOnly() );
        item.addOrReplace( contentText );
    }

    private void addClassifications( ListItem<EOIWrapper> item ) {
        EOIWrapper wrapper = item.getModelObject();
        int index = item.getIndex();
        if ( wrapper.isMarkedForCreation() ) {
            Label emptyLabel = new Label( "classifications", "" );
            makeVisible( emptyLabel, false );
            item.add( emptyLabel );
        } else {
            ClassificationsPanel classificationsPanel = new ClassificationsPanel(
                    "classifications",
                    new Model<Identifiable>( getFlow() ),
                    "eois[" + index + "].classifications",
                    !isReadOnly()
            );
            item.add( classificationsPanel );
        }
    }

    private void addSourcing( ListItem<EOIWrapper> item ) {
        EOIWrapper wrapper = item.getModelObject();
        TextArea<String> sourcingText = new TextArea<String>(
                "sources",
                new PropertyModel<String>( wrapper, "sources" )
        );
        sourcingText.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getFlow(), "eois" ) );
            }
        } );
        makeVisible( sourcingText, !wrapper.isMarkedForCreation() );
        sourcingText.setEnabled( !isReadOnly() );
        item.addOrReplace( sourcingText );
    }

    private void addSpecialHandling( ListItem<EOIWrapper> item ) {
        EOIWrapper wrapper = item.getModelObject();
        TextArea<String> specialHandlingText = new TextArea<String>(
                "specialHandling",
                new PropertyModel<String>( wrapper, "specialHandling" )
        );
        specialHandlingText.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getFlow(), "eois" ) );
            }
        } );
        makeVisible( specialHandlingText, !wrapper.isMarkedForCreation() );
        specialHandlingText.setEnabled( !isReadOnly() );
        item.addOrReplace( specialHandlingText );
    }

    private List<EOIWrapper> getWrappers() {
        List<EOIWrapper> wrappers = new ArrayList<EOIWrapper>();
        List<ElementOfInformation> eois = getFlow().getEois();
        for ( int i = 0; i < eois.size(); i++ ) {
            ElementOfInformation eoi = eois.get( i );
            wrappers.add( new EOIWrapper( eoi, i ) );
        }
        if ( !isReadOnly() ) {
            wrappers.add( new EOIWrapper() );
        }
        return wrappers;
    }

    /**
     * {@inheritDoc}
     */
    protected void close( AjaxRequestTarget target ) {
        Change change = new Change( Change.Type.AspectClosed, getFlow(), "eois" );
        update( target, change );
    }

    private Flow getFlow() {
        return (Flow) getModel().getObject();
    }

    private boolean isReadOnly() {
        return !isLockedByUser( getFlow() );
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadTop() {
        return PAD_TOP;
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadLeft() {
        return PAD_LEFT;
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadBottom() {
        return PAD_BOTTOM;
    }

    /**
     * {@inheritDoc}
     */
    protected int getPadRight() {
        return PAD_RIGHT;
    }

    /**
     * {@inheritDoc}
     */
    protected int getMinWidth() {
        return MIN_WIDTH;
    }

    /**
     * {@inheritDoc}
     */
    protected int getMinHeight() {
        return MIN_HEIGHT;
    }

    /**
     * Wrapped EOI.
     */
    public class EOIWrapper implements Serializable {
        /**
         * Marked for creation.
         */
        private boolean markedForCreation;
        /**
         * Element of information.
         */
        private ElementOfInformation eoi;
        /**
         * Index of eoi.
         */
        private int index;

        public EOIWrapper( ElementOfInformation eoi, int index ) {
            this.eoi = eoi;
            this.index = index;
            markedForCreation = false;
        }

        public EOIWrapper() {
            this.eoi = new ElementOfInformation();
            index = -1;
            markedForCreation = true;
        }

        public boolean isMarkedForCreation() {
            return markedForCreation;
        }

        public ElementOfInformation getEoi() {
            return eoi;
        }

        public boolean isConfirmed() {
            return !markedForCreation;
        }

        public void setConfirmed( boolean val ) {
            assert !markedForCreation;
            if ( !val ) {
                doCommand( UpdateObject.makeCommand(
                        getFlow(),
                        "eois",
                        eoi,
                        UpdateObject.Action.Remove
                ) );
            }
        }

        public String getContent() {
            return eoi.getContent();
        }

        public void setContent( String val ) {
            String content = val == null ? "" : val.trim();
            if ( !content.isEmpty() ) {
                eoi.setContent( content );
                if ( !markedForCreation ) {
                    doCommand( UpdateObject.makeCommand(
                            getFlow(),
                            "eois[" + index + "].content",
                            content,
                            UpdateObject.Action.Set
                    ) );
                } else {
                    eoi.setContent( content );
                    doCommand( UpdateObject.makeCommand(
                            getFlow(),
                            "eois",
                            eoi,
                            UpdateObject.Action.Add
                    ) );
                }
            }
        }

        public String getSources() {
            return eoi.getSources();
        }

        public void setSources( String val ) {
            assert !markedForCreation;
            String value = val == null ? "" : val.trim();
            doCommand( UpdateObject.makeCommand(
                    getFlow(),
                    "eois[" + index + "].sources",
                    value,
                    UpdateObject.Action.Set
            ) );
        }

        public String getSpecialHandling() {
            return eoi.getSpecialHandling();
        }

        public void setSpecialHandling( String val ) {
            assert !markedForCreation;
            String value = val == null ? "" : val.trim();
            doCommand( UpdateObject.makeCommand(
                    getFlow(),
                    "eois[" + index + "].specialHandling",
                    value,
                    UpdateObject.Action.Set
            ) );
        }

    }

}
