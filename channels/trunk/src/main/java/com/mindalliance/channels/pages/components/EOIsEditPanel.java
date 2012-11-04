package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.LinkClassifications;
import com.mindalliance.channels.core.command.commands.UpdateObject;
import com.mindalliance.channels.core.model.EOIsHolder;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Subject;
import com.mindalliance.channels.core.model.Transformation;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.components.segment.TransformationPanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.TransformerUtils;
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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * EOIs edit panel.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 10/31/12
 * Time: 8:01 PM
 */
public class EOIsEditPanel extends AbstractCommandablePanel {

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
    private boolean canTransform;

    public EOIsEditPanel(
            String id,
            IModel<EOIsHolder> eoiHolderModel,
            boolean canTransform,
            Set<Long> expansions ) {
        super( id, eoiHolderModel, expansions );
        this.canTransform = canTransform;
        init();
    }

    private void init() {
        eoisContainer = new WebMarkupContainer( "eoisContainer" );
        eoisContainer.setOutputMarkupId( true );
        add( eoisContainer );
        addHeaders();
        addEOIs();
        addLinkToClassifications();
    }

    private void addLinkToClassifications() {
        AjaxFallbackLink classificationsLink = new AjaxFallbackLink( "classifications" ) {
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Expanded, Channels.ALL_CLASSIFICATIONS ) );
            }
        };
        classificationsLink.add( new AttributeModifier( "class", new Model<String>( "window" ) ) );
        add( classificationsLink );
    }


    private void addHeaders() {
        WebMarkupContainer timeSensitivityHeaderContainer =
                new WebMarkupContainer( "timeSensitivityContainer" );
        timeSensitivityHeaderContainer.setOutputMarkupId( true );
        timeSensitivityHeaderContainer.setVisible( getEOIHolder().isTimeSensitive() );
        eoisContainer.addOrReplace( timeSensitivityHeaderContainer );
        WebMarkupContainer classificationsHeaderContainer =
                new WebMarkupContainer( "classificationsHeaderContainer" );
        classificationsHeaderContainer.setOutputMarkupId( true );
        classificationsHeaderContainer.setVisible( getEOIHolder().isClassificationsAccessible()
                && !getPlan().classificationSystems().isEmpty() );
        eoisContainer.addOrReplace( classificationsHeaderContainer );
        WebMarkupContainer descriptionHeaderContainer =
                new WebMarkupContainer( "descriptionHeaderContainer" );
        descriptionHeaderContainer.setOutputMarkupId( true );
        descriptionHeaderContainer.setVisible( getEOIHolder().isDescriptionChangeable() );
        eoisContainer.addOrReplace( descriptionHeaderContainer );
        WebMarkupContainer handlingHeaderContainer =
                new WebMarkupContainer( "handlingHeaderContainer" );
        handlingHeaderContainer.setVisible( getEOIHolder().isSpecialHandlingChangeable() );
        handlingHeaderContainer.setOutputMarkupId( true );
        eoisContainer.addOrReplace( handlingHeaderContainer );
        WebMarkupContainer transformationHeaderContainer =
                new WebMarkupContainer( "transformationHeaderContainer" );
        transformationHeaderContainer.setVisible( canTransform );
        transformationHeaderContainer.setOutputMarkupId( true );
        eoisContainer.addOrReplace( transformationHeaderContainer );
        // Auto populate eois
        AjaxFallbackLink autoPopulateLink = new AjaxFallbackLink( "autoPopulate" ) {
            public void onClick( AjaxRequestTarget target ) {
                boolean changed = autoPopulate();
                if ( changed ) {
                    addHeaders();
                    addEOIs();
                    target.add( eoisContainer );
                    update( target, new Change( Change.Type.Updated, getEOIHolder(), "eois" ) );
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
                    target.add( eoisContainer );
                    update( target, new Change( Change.Type.Updated, getEOIHolder(), "eois" ) );
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
        makeVisible( linkedClassificationsLink, !isReadOnly() && isClassificationsLinked() );
        classificationsHeaderContainer.add( linkedClassificationsLink );
    }

    private void updateLinkingLinks( AjaxRequestTarget target ) {
        makeVisible( unlinkedClassificationsLink, !isClassificationsLinked() );
        makeVisible( linkedClassificationsLink, isClassificationsLinked() );
        target.add( unlinkedClassificationsLink );
        target.add( linkedClassificationsLink );
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
                addTimeSensitive( item );
                addClassifications( item );
                addDescription( item );
                addSpecialHandling( item );
                addTransformation( item );
                addTrace( item );
                item.add( new AttributeModifier(
                        "class",
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
                addHeaders();
                addEOIs();
                target.add( eoisContainer );
                update( target, new Change( Change.Type.Updated, getEOIHolder(), "eois" ) );
            }
        } );
        makeVisible( confirmedCheckBox,
                !wrapper.isMarkedForCreation()
                        && !isReadOnly()
        );
        confirmedCheckBox.setEnabled( wrapper.isModifiable() );
        if ( !wrapper.isModifiable() ) {
            confirmedCheckBox.add( new AttributeModifier(
                    "title",
                    "Standard element of information" ) );
        }
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
                    target.add( eoisContainer );
                } else {
                    target.add( item );
                }
                update( target, new Change( Change.Type.Updated, getEOIHolder(), "eois" ) );
            }
        } );
        contentText.setEnabled( !isReadOnly() && wrapper.isModifiable() );
        item.addOrReplace( contentText );
    }

    private void addTimeSensitive( ListItem<EOIWrapper> item ) {
        WebMarkupContainer timeSensitiveContainer = new WebMarkupContainer( "timeSensitiveContainer" );
        timeSensitiveContainer.setOutputMarkupId( true );
        timeSensitiveContainer.setVisible( getEOIHolder().isTimeSensitive() );
        item.addOrReplace( timeSensitiveContainer );
        EOIWrapper wrapper = item.getModelObject();
        CheckBox timeSensitiveCheckBox = new CheckBox(
                "timeSensitive",
                new PropertyModel<Boolean>( wrapper, "timeSensitive" )
        );
        timeSensitiveCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getEOIHolder(), "eois" ) );
            }
        } );
        makeVisible(
                timeSensitiveCheckBox,
                !canTransform && getEOIHolder().canSetTimeSensitivity() && !wrapper.isMarkedForCreation() );
        timeSensitiveCheckBox.setEnabled( !isReadOnly() );
        timeSensitiveContainer.add( timeSensitiveCheckBox );
    }


    private void addClassifications( ListItem<EOIWrapper> item ) {
        WebMarkupContainer classificationsContainer = new WebMarkupContainer( "classificationsContainer" );
        classificationsContainer.setOutputMarkupId( true );
        classificationsContainer.setVisible(
                getEOIHolder().isClassificationsAccessible()
                        && !getPlan().classificationSystems().isEmpty() );
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
                    new Model<Identifiable>( getEOIHolder() ),
                    "effectiveEois[" + index + "].classifications",
                    !isReadOnly() && wrapper.isModifiable()
            );
            makeVisible( classificationsPanel, true );
            classificationsContainer.add( classificationsPanel );
        }
    }

    private void addDescription( ListItem<EOIWrapper> item ) {
        WebMarkupContainer descriptionContainer = new WebMarkupContainer( "descriptionContainer" );
        descriptionContainer.setOutputMarkupId( true );
        descriptionContainer.setVisible( getEOIHolder().isDescriptionChangeable() );
        item.addOrReplace( descriptionContainer );
        EOIWrapper wrapper = item.getModelObject();
        TextArea<String> descriptionText = new TextArea<String>(
                "description",
                new PropertyModel<String>( wrapper, "description" )
        );
        descriptionText.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getEOIHolder(), "eois" ) );
            }
        } );
        makeVisible( descriptionText, !wrapper.isMarkedForCreation() );
        descriptionText.setEnabled( !isReadOnly() && wrapper.isModifiable() );
        descriptionContainer.addOrReplace( descriptionText );
    }

    private void addSpecialHandling( ListItem<EOIWrapper> item ) {
        WebMarkupContainer handlingContainer = new WebMarkupContainer( "handlingContainer" );
        handlingContainer.setOutputMarkupId( true );
        handlingContainer.setVisible( getEOIHolder().isSpecialHandlingChangeable() );
        item.addOrReplace( handlingContainer );
        EOIWrapper wrapper = item.getModelObject();
        TextArea<String> specialHandlingText = new TextArea<String>(
                "specialHandling",
                new PropertyModel<String>( wrapper, "specialHandling" )
        );
        specialHandlingText.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getEOIHolder(), "eois" ) );
            }
        } );
        makeVisible( specialHandlingText, !wrapper.isMarkedForCreation() );
        specialHandlingText.setEnabled( !isReadOnly() && wrapper.isModifiable() );
        handlingContainer.addOrReplace( specialHandlingText );
    }

    private void addTransformation( ListItem<EOIWrapper> item ) {
        WebMarkupContainer transformationContainer = new WebMarkupContainer( "transformationContainer" );
        transformationContainer.setOutputMarkupId( true );
        transformationContainer.setVisible( canTransform );
        item.addOrReplace( transformationContainer );
        EOIWrapper wrapper = item.getModelObject();
        if ( wrapper.isMarkedForCreation() || !canTransform ) {
            Label emptyLabel = new Label( "transformation", "" );
            makeVisible( emptyLabel, false );
            transformationContainer.add( emptyLabel );
        } else {
            TransformationPanel transformationPanel = new TransformationPanel(
                    "transformation",
                    new Model<EOIsHolder>( getEOIHolder() ),
                    item.getIndex()
            );
            makeVisible( transformationPanel, true );
            transformationContainer.add( transformationPanel );
        }
    }

    private void addTrace( ListItem<EOIWrapper> item ) {
        final EOIWrapper eoiWrapper = item.getModelObject();
        WebMarkupContainer traceContainer = new WebMarkupContainer( "traceContainer" );
        traceContainer.setOutputMarkupId( true );
        traceContainer.setVisible( getEOIHolder().isFlow()
                && !( (Flow) getEOIHolder() ).isNeed()
                && !eoiWrapper.isMarkedForCreation() );
        item.addOrReplace( traceContainer );
        AjaxFallbackLink traceLink = new AjaxFallbackLink( "trace" ) {
            public void onClick( AjaxRequestTarget target ) {
                Change change = new Change( Change.Type.AspectViewed, getEOIHolder(), "dissemination" );
                change.addQualifier( "show", canTransform ? "targets" : "sources" );   // todo - isSend ? ... : ...
                change.addQualifier(
                        "subject",
                        new Subject(
                                getEOIHolder().getName(),
                                eoiWrapper.getContent() ) );
                update( target, change );
            }
        };
        traceContainer.add( traceLink );
    }

    private List<EOIWrapper> getWrappers() {
        List<EOIWrapper> wrappers = new ArrayList<EOIWrapper>();
        List<ElementOfInformation> eois = getEOIHolder().getEffectiveEois();
        int index = 0;
        for ( ElementOfInformation eoi : eois ) {
            wrappers.add( new EOIWrapper( eoi, index ) );
            index++;
        }
        if ( !isReadOnly() ) {
            wrappers.add( new EOIWrapper() );
        }
        return wrappers;
    }

    private boolean autoPopulate() {
        boolean populated = false;
        List<ElementOfInformation> newEOIs = guessNewEOIs();
        if ( !newEOIs.isEmpty() ) {
            List<ElementOfInformation> allEOIs = new ArrayList<ElementOfInformation>();
            allEOIs.addAll( getEOIHolder().getEffectiveEois() );
            allEOIs.addAll( newEOIs );
            doCommand(
                    UpdateObject.makeCommand( getUser().getUsername(), getEOIHolder(),
                            "eois",
                            allEOIs,
                            UpdateObject.Action.Set )
            );
            populated = true;
        }
        return populated;
    }

    private boolean canBePopulated() {
        return !guessNewEOIs().isEmpty();
    }

    /**
     * Incrementally guess new EOIs.
     *
     * @return a list of new EOIs
     */
    private List<ElementOfInformation> guessNewEOIs() {
        List<ElementOfInformation> newEois = new ArrayList<ElementOfInformation>();
        if ( getEOIHolder().isFlow() ) {
            Node source = getEOIHolder().getSource();
            Node target = getEOIHolder().getTarget();
            boolean added = false;
            if ( getEOIHolder().isSharing() && !added )
                added = addGuessedFromNeed( newEois );
            if ( getEOIHolder().isSharing() && !added )
                added = addGuessedFromCapability( newEois );
            if ( source.isPart() && !added )
                added = addGuessedFromOtherSends( (Part) source, newEois );
            if ( target.isPart() && !added )
                added = addGuessedFromOtherReceives( (Part) target, newEois );
            if ( source.isPart() && !added )
                addGuessedSentFromReceives( ( (Part) source ), newEois );
        }
        return newEois;
    }

    @SuppressWarnings( "unchecked" )
    private boolean addGuessedFromCapability( List<ElementOfInformation> newEois ) {
        if ( getEOIHolder().isFlow() ) {
            Flow flow = (Flow) getEOIHolder();
            int size = newEois.size();
            List<String> contents = (List<String>) CollectionUtils.collect(
                    flow.getEffectiveEois(),
                    TransformerUtils.invokerTransformer( "getContent" ) );
            contents.addAll( (List<String>) CollectionUtils.collect(
                    newEois,
                    TransformerUtils.invokerTransformer( "getContent" ) ) );
            Flow capability = findRelatedCapability();
            if ( capability != null ) {
                for ( ElementOfInformation capabilityEoi : capability.getEffectiveEois() ) {
                    if ( !Matcher.contains( contents, capabilityEoi.getContent() ) ) {
                        // Use the first one as-is. Will improve later. Maybe.
                        newEois.add( new ElementOfInformation( capabilityEoi ) );
                        contents.add( capabilityEoi.getContent() );
                    }
                }
            }
            return size < newEois.size();
        } else {
            return false;
        }
    }

    private Flow findRelatedCapability() {
        Flow capability = null;
        Node source = getEOIHolder().getSource();
        if ( source != null && source.isPart() ) {
            final String flowName = getEOIHolder().getName();
            capability = (Flow) CollectionUtils.find(
                    ( (Part) source ).getCapabilities(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            Flow capability = (Flow) object;
                            return !capability.equals( getEOIHolder() )
                                    && Matcher.same( flowName, capability.getName() );
                        }
                    }
            );
        }
        return capability;
    }

    @SuppressWarnings( "unchecked" )
    private boolean addGuessedFromNeed( List<ElementOfInformation> newEois ) {
        if ( getEOIHolder().isFlow() ) {
            Flow flow = (Flow) getEOIHolder();
            int size = newEois.size();
            List<String> contents = (List<String>) CollectionUtils.collect(
                    flow.getEffectiveEois(),
                    TransformerUtils.invokerTransformer( "getContent" ) );
            contents.addAll( (List<String>) CollectionUtils.collect(
                    newEois,
                    TransformerUtils.invokerTransformer( "getContent" ) ) );
            Flow need = findRelatedNeed();
            if ( need != null ) {
                if ( !need.equals( flow ) && Matcher.same( flow.getName(), need.getName() ) ) {
                    for ( ElementOfInformation needEoi : need.getEffectiveEois() ) {
                        if ( !Matcher.contains( contents, needEoi.getContent() ) ) {
                            // Use the first one as-is. Will improve later. Maybe.
                            newEois.add( new ElementOfInformation( needEoi ) );
                            contents.add( needEoi.getContent() );
                        }
                    }
                }
            }
            return size < newEois.size();
        } else {
            return false;
        }
    }

    private Flow findRelatedNeed() {
        Flow need = null;
        Node target = getEOIHolder().getTarget();
        if ( target.isPart() ) {
            final String flowName = getEOIHolder().getName();
            need = (Flow) CollectionUtils.find(
                    ( (Part) target ).getNeeds(),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            Flow need = (Flow) object;
                            return !need.equals( getEOIHolder() )
                                    && Matcher.same( flowName, need.getName() );
                        }
                    }
            );
        }
        return need;
    }

    @SuppressWarnings( "unchecked" )
    private boolean addGuessedFromOtherReceives( Part target, List<ElementOfInformation> newEois ) {
        if ( getEOIHolder().isFlow() ) {
            Flow flow = (Flow) getEOIHolder();
            int size = newEois.size();
            List<String> contents = (List<String>) CollectionUtils.collect(
                    flow.getEffectiveEois(),
                    TransformerUtils.invokerTransformer( "getContent" ) );
            contents.addAll( (List<String>) CollectionUtils.collect(
                    newEois,
                    TransformerUtils.invokerTransformer( "getContent" ) ) );
            Iterator<Flow> sharingReceives = target.receives();
            while ( sharingReceives.hasNext() ) {
                Flow sharingReceive = sharingReceives.next();
                if ( Matcher.same( flow.getName(), sharingReceive.getName() ) ) {
                    for ( ElementOfInformation needEoi : sharingReceive.getEffectiveEois() ) {
                        if ( !Matcher.contains( contents, needEoi.getContent() ) ) {
                            // Use the first one as-is. Will improve later. Maybe.
                            newEois.add( new ElementOfInformation( needEoi ) );
                            contents.add( needEoi.getContent() );
                        }
                    }
                }
            }
            return size < newEois.size();
        } else {
            return false;
        }
    }

    @SuppressWarnings( "unchecked" )
    private boolean addGuessedFromOtherSends( Part source, List<ElementOfInformation> newEois ) {
        if ( getEOIHolder().isFlow() ) {
            Flow flow = (Flow) getEOIHolder();
            int size = newEois.size();
            List<String> contents = (List<String>) CollectionUtils.collect(
                    flow.getEffectiveEois(),
                    TransformerUtils.invokerTransformer( "getContent" ) );
            contents.addAll( (List<String>) CollectionUtils.collect(
                    newEois,
                    TransformerUtils.invokerTransformer( "getContent" ) ) );
            Iterator<Flow> sends = source.sends();
            while ( sends.hasNext() ) {
                Flow send = sends.next();
                if ( !send.equals( flow ) && Matcher.same( flow.getName(), send.getName() ) ) {
                    for ( ElementOfInformation sourceEoi : send.getEffectiveEois() ) {
                        if ( !Matcher.contains( contents, sourceEoi.getContent() ) ) {
                            // Use the first one as-is. Will improve later. Maybe.
                            newEois.add( new ElementOfInformation( sourceEoi ) );
                            contents.add( sourceEoi.getContent() );
                        }
                    }
                }
            }
            return size < newEois.size();
        } else {
            return false;
        }
    }

    @SuppressWarnings( "unchecked" )
    private boolean addGuessedSentFromReceives( Part source, List<ElementOfInformation> newEois ) {
        if ( getEOIHolder().isFlow() ) {
            Flow flow = (Flow) getEOIHolder();
            int size = newEois.size();
            String flowName = flow.getName();
            List<String> contents = (List<String>) CollectionUtils.collect(
                    flow.getEffectiveEois(),
                    TransformerUtils.invokerTransformer( "getContent" ) );
            contents.addAll( (List<String>) CollectionUtils.collect(
                    newEois,
                    TransformerUtils.invokerTransformer( "getContent" ) ) );
            List<Flow> receives = source.getAllSharingReceives();
            Set<String> newContents = new HashSet<String>();
            for ( Flow receive : receives ) {
                newContents.addAll( (List<String>) CollectionUtils.collect(
                        receive.getEffectiveEois(),
                        TransformerUtils.invokerTransformer( "getContent" ) ) );
            }
            // Only keep new eoi content
            Matcher.removeAll( newContents, contents );
            for ( final String newContent : newContents ) {
                ElementOfInformation newEoi = new ElementOfInformation( newContent );
                Set<String> infos = new HashSet<String>();
                for ( Flow receive : receives ) {
                    if ( !receive.equals( flow ) ) {
                        String receiveName = receive.getName();
                        ElementOfInformation sourceEoi = (ElementOfInformation) CollectionUtils.find(
                                receive.getEffectiveEois(),
                                new Predicate() {
                                    @Override
                                    public boolean evaluate( Object object ) {
                                        return Matcher.same( newContent, ( (ElementOfInformation) object ).getContent() );
                                    }
                                }
                        );
                        if ( sourceEoi != null ) {
                            if ( !Matcher.same( receiveName, flowName ) ) {
                                // transformations
                                if ( !infos.contains( receiveName ) ) {
                                    Transformation xform = newEoi.getTransformation();
                                    if ( xform.isNone() ) {
                                        xform = new Transformation( Transformation.Type.Renaming );
                                        newEoi.setTransformation( xform );
                                    }
                                    xform.addSubject( new Subject( receiveName, newContent ) );
                                    infos.add( receiveName );
                                }
                            }
                            // classifications
                            newEoi.addClassifications( sourceEoi.getClassifications(), getPlan() );
                            // description
                            String description = newEoi.getDescription();
                            String sourceDescription = sourceEoi.getDescription();
                            if ( !sourceDescription.isEmpty() && !description.contains( sourceDescription ) ) {
                                newEoi.setDescription(
                                        description + ( description.isEmpty() ? "" : " " ) + sourceDescription
                                );
                            }
                            // handling
                            String handling = newEoi.getSpecialHandling();
                            String sourceHandling = sourceEoi.getSpecialHandling();
                            if ( !sourceHandling.isEmpty() && !handling.contains( sourceHandling ) ) {
                                newEoi.setSpecialHandling(
                                        handling + ( handling.isEmpty() ? "" : " " ) + sourceHandling
                                );
                            }
                        }
                    }
                }
                newEois.add( newEoi );
            }
            return size < newEois.size();
        } else {
            return false;
        }
    }


    private boolean isClassificationsLinked() {
        return getEOIHolder().isClassificationsLinked() && getEOIHolder().areAllEOIClassificationsSame();
    }

    private void unlinkClassifications() {
        doCommand( UpdateObject.makeCommand( getUser().getUsername(), getEOIHolder(),
                "classificationsLinked",
                false,
                UpdateObject.Action.Set ) );
    }

    private Change linkClassifications() {
        return doCommand( new LinkClassifications( getUser().getUsername(), getEOIHolder() ) );
    }

    private EOIsHolder getEOIHolder() {
        return (EOIsHolder) getModel().getObject();
    }

    private boolean isReadOnly() {
        return !isLockedByUser( getEOIHolder() ) || !getEOIHolder().canSetElements();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refresh( AjaxRequestTarget target, Change change, String aspect ) {
        if ( change.isUnknown() || change.isModified() ) {
            addHeaders();
            addEOIs();
            target.add( eoisContainer );
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
            if ( getEOIHolder().isClassificationsLinked() ) {
                eoi.setClassifications( getEOIHolder().getAllEOIClassifications() );
            }
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
            if ( !val && isModifiable() ) {
                doCommand( UpdateObject.makeCommand( getUser().getUsername(), getEOIHolder(),
                        "localEois",
                        eoi,
                        UpdateObject.Action.Remove ) );
            }
        }

        public String getContent() {
            return eoi.getContent();
        }

        public void setContent( String val ) {
            String content = val == null ? "" : val.trim();
            if ( !content.isEmpty() ) {
                if ( !markedForCreation && isModifiable() ) {
                    if ( !getEOIHolder().hasEffectiveEoiNamedExactly( content ) ) {
                        doCommand( UpdateObject.makeCommand( getUser().getUsername(), getEOIHolder(),
                                "effectiveEois[" + index + "].content",
                                content,
                                UpdateObject.Action.Set ) );
                    }
                } else {
                    if ( !getEOIHolder().getEffectiveEois().contains( eoi ) ) {
                        eoi.setContent( content );
                        doCommand( UpdateObject.makeCommand( getUser().getUsername(), getEOIHolder(),
                                "localEois",
                                eoi,
                                UpdateObject.Action.Add ) );
                    }
                }
            }
        }

        public boolean isTimeSensitive() {
            return eoi.isTimeSensitive();
        }

        public void setTimeSensitive( boolean val ) {
            if ( isModifiable() ) {
                doCommand( UpdateObject.makeCommand( getUser().getUsername(), getEOIHolder(),
                        "effectiveEois[" + index + "].timeSensitive",
                        val,
                        UpdateObject.Action.Set ) );
            }
        }

        public String getDescription() {
            return eoi.getDescription();
        }

        public void setDescription( String val ) {
            if ( isModifiable() ) {
                String value = val == null ? "" : val.trim();
                doCommand( UpdateObject.makeCommand( getUser().getUsername(), getEOIHolder(),
                        "effectiveEois[" + index + "].description",
                        value,
                        UpdateObject.Action.Set ) );
            }
        }

        public String getSpecialHandling() {
            return eoi.getSpecialHandling();
        }

        public void setSpecialHandling( String val ) {
            if ( isModifiable() ) {
                String value = val == null ? "" : val.trim();
                doCommand( UpdateObject.makeCommand( getUser().getUsername(), getEOIHolder(),
                        "effectiveEois[" + index + "].specialHandling",
                        value,
                        UpdateObject.Action.Set ) );
            }
        }

        public boolean isModifiable() {
            return isMarkedForCreation() ||
                    getEOIHolder().getEffectiveEois().get( index ).equals( eoi ) // still aiming at right target
                    && getEOIHolder().isLocalAndEffective( eoi );
        }
    }


}
