package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.LinkFlowClassifications;
import com.mindalliance.channels.command.commands.UpdateObject;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.nlp.Matcher;
import com.mindalliance.channels.pages.components.ClassificationsPanel;
import com.mindalliance.channels.pages.components.FloatingCommandablePanel;
import com.mindalliance.channels.pages.components.plan.PlanEditPanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
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
import java.util.HashSet;
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
    /**
     * Unlinked classification link.
     */
    AjaxFallbackLink unlinkedClassificationsLink;
    /**
     * Linked classification link.
     */
    AjaxFallbackLink linkedClassificationsLink;
    private boolean isSend;

    public FlowEOIsPanel( String id, Model<Flow> flowModel, boolean isSend, Set<Long> expansions ) {
        super( id, flowModel, expansions );
        this.isSend = isSend;
        init();
    }

    private void init() {
        addDoneButton();
        eoisContainer = new WebMarkupContainer( "eoisContainer" );
        eoisContainer.setOutputMarkupId( true );
        add( eoisContainer );
        addHeaders();
        addAboutFlow();
        addEOIs();
        addLinkToClassifications();
    }

    /**
     * {@inheritDoc}
     */
    protected String getTitle() {
        return getFlow().getName();
    }

    private void addLinkToClassifications() {
        AjaxFallbackLink classificationsLink = new AjaxFallbackLink( "classifications" ) {
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Expanded, getPlan(), PlanEditPanel.CLASSIFICATIONS ) );
            }
        };
        classificationsLink.add( new AttributeModifier( "class", true, new Model<String>( "window" ) ) );
        add( classificationsLink );
    }

    private void addDoneButton() {
        AjaxFallbackLink doneLink = new AjaxFallbackLink( "done" ) {
            public void onClick( AjaxRequestTarget target ) {
                close( target );
            }
        };
        add( doneLink );
    }


    private void addHeaders() {
        WebMarkupContainer classificationsHeaderContainer =
                new WebMarkupContainer( "classificationsHeaderContainer" );
        classificationsHeaderContainer.setOutputMarkupId( true );
        classificationsHeaderContainer.setVisible( !getFlow().isNeed()
                && !getPlan().classificationSystems().isEmpty() );
        eoisContainer.addOrReplace( classificationsHeaderContainer );
        WebMarkupContainer descriptionHeaderContainer =
                new WebMarkupContainer( "descriptionHeaderContainer" );
        descriptionHeaderContainer.setOutputMarkupId( true );
        descriptionHeaderContainer.setVisible( !getFlow().isNeed() );
        eoisContainer.addOrReplace( descriptionHeaderContainer );
        WebMarkupContainer handlingHeaderContainer =
                new WebMarkupContainer( "handlingHeaderContainer" );
        handlingHeaderContainer.setVisible( !getFlow().isNeed() );
        handlingHeaderContainer.setOutputMarkupId( true );
        eoisContainer.addOrReplace( handlingHeaderContainer );
        WebMarkupContainer transformationHeaderContainer =
                new WebMarkupContainer( "transformationHeaderContainer" );
        transformationHeaderContainer.setVisible( isSend );
        transformationHeaderContainer.setOutputMarkupId( true );
        eoisContainer.addOrReplace( transformationHeaderContainer );
        // Auto populate eois
        AjaxFallbackLink autoPopulateLink = new AjaxFallbackLink( "autoPopulate" ) {
            public void onClick( AjaxRequestTarget target ) {
                boolean changed = autoPopulate();
                if ( changed ) {
                    addEOIs();
                    target.addComponent( eoisContainer );
                    update( target, new Change( Change.Type.Updated, getFlow(), "eois" ) );
                }
            }
        };
        autoPopulateLink.setOutputMarkupId( true );
        makeVisible( autoPopulateLink, !isReadOnly() && canBePopulated() );
        // Link classifications
        eoisContainer.addOrReplace( autoPopulateLink );
        unlinkedClassificationsLink = new AjaxFallbackLink( "unlinkClassifications" ) {
            public void onClick( AjaxRequestTarget target ) {
                Change change = linkClassifications();
                updateLinkingLinks( target );
                if ( change.isForProperty( "eois" ) ) {
                    addEOIs();
                    target.addComponent( eoisContainer );
                    update( target, new Change( Change.Type.Updated, getFlow(), "eois" ) );
                }
            }
        };
        makeVisible( unlinkedClassificationsLink, !isReadOnly() && !isClassificationsLinked() );
        classificationsHeaderContainer.add( unlinkedClassificationsLink );
        // Unlink classifications
        eoisContainer.add( autoPopulateLink );
        linkedClassificationsLink = new AjaxFallbackLink( "linkClassifications" ) {
            public void onClick( AjaxRequestTarget target ) {
                unlinkClassifications();
                updateLinkingLinks( target );
            }
        };
        makeVisible( linkedClassificationsLink, isClassificationsLinked() );
        classificationsHeaderContainer.add( linkedClassificationsLink );
    }

    private void updateLinkingLinks( AjaxRequestTarget target ) {
        makeVisible( unlinkedClassificationsLink, !isClassificationsLinked() );
        makeVisible( linkedClassificationsLink, isClassificationsLinked() );
        target.addComponent( unlinkedClassificationsLink );
        target.addComponent( linkedClassificationsLink );
    }

    private void addAboutFlow() {
        Label flowTypeLabel = new Label(
                "flowType",
                new Model<String>( getFlow().isNeed()
                        ? "need for"
                        : getFlow().isCapability()
                        ? "availability of"
                        : "sharing of" ) );
        flowTypeLabel.setOutputMarkupId( true );
        addOrReplace( flowTypeLabel );
        Label flowNameLabel = new Label( "flowName", new Model<String>( getFlow().getName() ) );
        addOrReplace( flowNameLabel );
        Label sourceLabel = new Label(
                "source",
                new Model<String>( getFlow().isNeed()
                        ? ""
                        : getFlow().isCapability()
                        ? "from " + "\"" + getFlow().getSource().getTitle() + "\""
                        : " by " + "\"" + getFlow().getSource().getTitle() + "\""
                )
        );
        sourceLabel.setOutputMarkupId( true );
        makeVisible( sourceLabel, !getFlow().isNeed() );
        addOrReplace( sourceLabel );
        Label targetLabel = new Label(
                "target",
                new Model<String>( getFlow().isCapability()
                        ? ""
                        : getFlow().isNeed()
                        ? "by " + "\"" + getFlow().getTarget().getTitle() + "\""
                        : "with " + "\"" + getFlow().getTarget().getTitle() + "\""
                )
        );
        targetLabel.setOutputMarkupId( true );
        makeVisible( targetLabel, !getFlow().isCapability() );
        addOrReplace( targetLabel );
    }

    private void addEOIs() {
        final List<EOIWrapper> wrappers = getWrappers();
        ListView<EOIWrapper> eoisListView = new ListView<EOIWrapper>(
                "eois",
                wrappers
        ) {
            protected void populateItem( ListItem<EOIWrapper> item ) {
                item.setOutputMarkupId( true );
                addConfirmed( item );
                addContent( item );
                addClassifications( item );
                addDescription( item );
                addSpecialHandling( item );
                addTransformation( item );
                addTrace( item );
                item.add( new AttributeModifier(
                        "class",
                        true,
                        new Model<String>( cssClasses( item, wrappers.size() ) ) ) );
            }
        };
        eoisContainer.addOrReplace( eoisListView );
    }

    private String cssClasses( ListItem<EOIWrapper> item, int count ) {
        int index = item.getIndex();
        String cssClasses = index % 2 == 0 ? "even" : "odd";
        if ( index == count - 1 ) cssClasses += " last";
        return cssClasses;
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

    private void addContent( final ListItem<EOIWrapper> item ) {
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
                } else {
                    target.addComponent( item );
                }
                update( target, new Change( Change.Type.Updated, getFlow(), "eois" ) );
            }
        } );
        contentText.setEnabled( !isReadOnly() );
        item.addOrReplace( contentText );
    }

    private void addClassifications( ListItem<EOIWrapper> item ) {
        WebMarkupContainer classificationsContainer = new WebMarkupContainer( "classificationsContainer" );
        classificationsContainer.setOutputMarkupId( true );
        classificationsContainer.setVisible( !getFlow().isNeed() && !getPlan().classificationSystems().isEmpty() );
        item.addOrReplace( classificationsContainer );
        EOIWrapper wrapper = item.getModelObject();
        int index = item.getIndex();
        if ( wrapper.isMarkedForCreation() ) {
            Label emptyLabel = new Label( "classifications", "" );
            makeVisible( emptyLabel, false );
            classificationsContainer.add( emptyLabel );
        } else {
            ClassificationsPanel classificationsPanel = new ClassificationsPanel(
                    "classifications",
                    new Model<Identifiable>( getFlow() ),
                    "eois[" + index + "].classifications",
                    !isReadOnly()
            );
            makeVisible( classificationsPanel, true );
            classificationsContainer.add( classificationsPanel );
        }
    }

    private void addDescription( ListItem<EOIWrapper> item ) {
        WebMarkupContainer descriptionContainer = new WebMarkupContainer( "descriptionContainer" );
        descriptionContainer.setOutputMarkupId( true );
        descriptionContainer.setVisible( !getFlow().isNeed() );
        item.addOrReplace( descriptionContainer );
        EOIWrapper wrapper = item.getModelObject();
        TextArea<String> descriptionText = new TextArea<String>(
                "description",
                new PropertyModel<String>( wrapper, "description" )
        );
        descriptionText.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getFlow(), "eois" ) );
            }
        } );
        makeVisible( descriptionText, !wrapper.isMarkedForCreation() );
        descriptionText.setEnabled( !isReadOnly() );
        descriptionContainer.addOrReplace( descriptionText );
    }

    private void addSpecialHandling( ListItem<EOIWrapper> item ) {
        WebMarkupContainer handlingContainer = new WebMarkupContainer( "handlingContainer" );
        handlingContainer.setOutputMarkupId( true );
        handlingContainer.setVisible( !getFlow().isNeed() );
        item.addOrReplace( handlingContainer );
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
        handlingContainer.addOrReplace( specialHandlingText );
    }

    private void addTransformation( ListItem<EOIWrapper> item ) {
        WebMarkupContainer transformationContainer = new WebMarkupContainer( "transformationContainer" );
        transformationContainer.setOutputMarkupId( true );
        transformationContainer.setVisible( isSend );
        item.addOrReplace( transformationContainer );
        EOIWrapper wrapper = item.getModelObject();
        if ( wrapper.isMarkedForCreation() || !isSend ) {
            Label emptyLabel = new Label( "transformation", "" );
            makeVisible( emptyLabel, false );
            transformationContainer.add( emptyLabel );
        } else {
            TransformationPanel transformationPanel = new TransformationPanel(
                    "transformation",
                    new Model<Flow>( getFlow() ),
                    item.getIndex()
            );
            makeVisible( transformationPanel, true );
            transformationContainer.add( transformationPanel );
        }
    }

    private void addTrace( ListItem<EOIWrapper> item ) {
        WebMarkupContainer traceContainer = new WebMarkupContainer( "traceContainer" );
        traceContainer.setOutputMarkupId( true );
        traceContainer.setVisible( !getFlow().isNeed() && !item.getModelObject().isMarkedForCreation() );
        item.addOrReplace( traceContainer );
        AjaxFallbackLink traceLink = new AjaxFallbackLink( "trace" ) {
            public void onClick( AjaxRequestTarget target ) {
                // todo
            }
        };
        traceContainer.add( traceLink );
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

    private boolean autoPopulate() {
        boolean populated = false;
        List<ElementOfInformation> newEOIs = getNewEOIs();
        if ( !newEOIs.isEmpty() ) {
            List<ElementOfInformation> allEOIs = new ArrayList<ElementOfInformation>();
            allEOIs.addAll( getFlow().getEois() );
            allEOIs.addAll( newEOIs );
            doCommand(
                    UpdateObject.makeCommand(
                            getFlow(),
                            "eois",
                            allEOIs,
                            UpdateObject.Action.Set
                    )
            );
            populated = true;
        }
        return populated;
    }

    private boolean canBePopulated() {
        return !getNewEOIs().isEmpty();
    }

    @SuppressWarnings( "unchecked" )
    private List<ElementOfInformation> getNewEOIs() {
        Set<ElementOfInformation> population = new HashSet<ElementOfInformation>();
        for ( Flow flow : relatedFlows() ) {
            for ( ElementOfInformation eoi : flow.getEois() ) {
                population.add( new ElementOfInformation( eoi ) );
            }
        }
        return new ArrayList<ElementOfInformation>(
                CollectionUtils.subtract( population, getFlow().getEois() ) );
    }

    @SuppressWarnings( "unchecked" )
    private List<Flow> relatedFlows() {
        final Flow flow = getFlow();
        Set<Flow> flows = new HashSet<Flow>();
        flows.addAll( (List<Flow>) CollectionUtils.select(
                IteratorUtils.toList(
                        IteratorUtils.chainedIterator(
                                IteratorUtils.chainedIterator(
                                        flow.getSource().sends(),
                                        flow.getSource().receives() ),
                                IteratorUtils.chainedIterator(
                                        flow.getTarget().sends(),
                                        flow.getTarget().receives() ) ) ),
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return Matcher.getInstance().same( ( (Flow) obj ).getName(),
                                flow.getName() );
                    }
                }
        ) );

        return new ArrayList<Flow>( flows );
    }

    private boolean isClassificationsLinked() {
        return getFlow().isClassificationsLinked() && getFlow().areAllEOIClassificationsSame();
    }

    private void unlinkClassifications() {
        doCommand( UpdateObject.makeCommand(
                getFlow(),
                "classificationsLinked",
                false,
                UpdateObject.Action.Set
        ) );
    }

    private Change linkClassifications() {
        return doCommand( new LinkFlowClassifications( getFlow() ) );
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
        return !isLockedByUser( getFlow() ) || !getFlow().canSetNameAndElements();
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
     * {@inheritDoc}
     */
    @Override
    protected void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        if ( change.isModified() ) {
            addHeaders();
            addAboutFlow();
            addEOIs();
            target.addComponent( eoisContainer );
        }
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
            if ( getFlow().isClassificationsLinked() ) {
                eoi.setClassifications( getFlow().getAllEOIClassifications() );
            }
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
                if ( !markedForCreation ) {
                    if ( !getFlow().hasEoiNamed( content ) ) {
                        doCommand( UpdateObject.makeCommand(
                                getFlow(),
                                "eois[" + index + "].content",
                                content,
                                UpdateObject.Action.Set
                        ) );
                    }
                } else {
                    if ( !getFlow().getEois().contains( eoi ) ) {
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
        }

        public String getDescription() {
            return eoi.getDescription();
        }

        public void setDescription( String val ) {
            assert !markedForCreation;
            String value = val == null ? "" : val.trim();
            doCommand( UpdateObject.makeCommand(
                    getFlow(),
                    "eois[" + index + "].description",
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
