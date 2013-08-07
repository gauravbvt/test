/*
 * Copyright (C) 2011 Mind-Alliance Systems LLC.
 * All rights reserved.
 * Proprietary and Confidential.
 */

package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.core.Matcher;
import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.command.commands.RedirectFlow;
import com.mindalliance.channels.core.command.commands.SatisfyNeed;
import com.mindalliance.channels.core.command.commands.StandardizeInformation;
import com.mindalliance.channels.core.command.commands.UpdateSegmentObject;
import com.mindalliance.channels.core.model.Connector;
import com.mindalliance.channels.core.model.ElementOfInformation;
import com.mindalliance.channels.core.model.ExternalFlow;
import com.mindalliance.channels.core.model.Flow;
import com.mindalliance.channels.core.model.InfoProduct;
import com.mindalliance.channels.core.model.ModelObject;
import com.mindalliance.channels.core.model.Node;
import com.mindalliance.channels.core.model.Part;
import com.mindalliance.channels.core.model.Segment;
import com.mindalliance.channels.core.model.SegmentObject;
import com.mindalliance.channels.core.model.Taggable;
import com.mindalliance.channels.core.query.QueryService;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.DelayPanel;
import com.mindalliance.channels.pages.components.IssuesPanel;
import com.mindalliance.channels.pages.components.TagsPanel;
import com.mindalliance.channels.pages.components.plan.floating.PlanSearchingFloatingPanel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Details of an expanded flow.
 */
public abstract class ExpandedFlowPanel extends AbstractFlowPanel {
    /**
     * Unspecified intent.
     */
    private static String NO_INTENT = "Unspecified";

    /**
     * True if this flow is marked for deletion.
     */
    private boolean markedForDeletion;
    /**
     * The information name input field.
     */
    private TextField<String> nameField;

    /**
     * Information label or link (if standdardized)
     */
    private Component infoLinkOrLabel;

    /**
     * The askedFor buttons.
     */
    private RadioGroup<Boolean> askedForButtons;
    /**
     * The container for significance to target.
     */
    private WebMarkupContainer significanceToTargetLabel;
    /**
     * Choices of values for significance to target.
     */
    private DropDownChoice significanceToTargetChoice;
    /**
     * The "all" field.
     */
    private FormComponentLabel allField;
    /**
     * Choice of other node in flow.
     */
    private OtherNodeSelectorPanel otherChoice;
    /**
     * The channel field.
     */
    private WebMarkupContainer channelRow;

    /**
     * The max delay panel.
     */
    private DelayPanel delayPanel;
    /**
     * The row of max delay fields.
     */
    private WebMarkupContainer timingContainer;
    /**
     * The row of fields about significance to source
     */
    private WebMarkupContainer significanceToSourceContainer;
    /**
     * Markup for source triggering.
     */
    private WebMarkupContainer triggersSourceContainer;
    /**
     * Markup for source terminating.
     */
    private WebMarkupContainer terminatesSourceContainer;
    /**
     * The checkbox for setting Terminates significance to source
     */
    private CheckBox terminatesSourceCheckBox;

    /**
     * The checkbox for setting Triggers significance to source
     */
    private CheckBox triggersSourceCheckBox;
    /**
     * Flow description.
     */
    private TextArea<String> flowDescription;

    /**
     * Issues panel.
     */
    private IssuesPanel issuesPanel;
    /**
     * Link to other linked part
     */
    private ModelObjectLink otherLink;
    /**
     * Intent choice.
     */
    private DropDownChoice<String> intentChoice;
    /**
     * Restricted checkbox.
     */
    private CheckBox restrictedCheckBox;
    /**
     * Restriction choice.
     */
    private Component restrictionsPanel;
    /**
     * Editing restriction link.
     */
    private AjaxLink<String> editingRestrictionsLink;

    private boolean editingRestrictions = false;
    /**
     * If task fails checkbox container.
     */
    private WebMarkupContainer ifTaskFailsContainer;
    /**
     * If task fails checkbox.
     */
    private CheckBox ifTaskFailsCheckBox;
    /**
     * Prohibited checkbox container.
     */
    private WebMarkupContainer prohibitedContainer;
    /**
     * If prohibited.
     */
    private CheckBox prohibitedCheckBox;

    /**
     * Flow modalities.
     */
    private WebMarkupContainer referencesEventPhaseContainer;
    /**
     * If sender can reference event phase.
     */
    private CheckBox referencesEventPhaseCheckBox;
    /**
     * Can bypass intermediate.
     */
    private CheckBox canBypassIntermediateCheckBox;
    /**
     * Receipt confirmation requested.
     */
    private CheckBox receiptConfirmationRequestedCheckBox;

    /**
     * Show simple vs advanced form.
     */
    private Label simpleAdvanced;
    /**
     * Tags fields container.
     */
    private WebMarkupContainer tagsContainer;
    /**
     * Classification fields container.
     */
    private WebMarkupContainer classificationContainer;

    /**
     * EOIS aspect.
     */
    public static final String EOIS = "eois";
    /**
     * Restrictions fields container.
     */
    private WebMarkupContainer restrictionsContainer;
    /**
     * Flow is to be restricted.
     */
    private boolean restricted;
    /**
     * The containing plan page.
     */
    private PlanPage planPage;
    /**
     * Flow header.
     */
    private FlowTitlePanel titlePanel;
    /**
     * Can bypass intermediate container
     */
    private WebMarkupContainer canBypassIntermediateContainer;
    /**
     * Receipt confirmation request container.
     */
    private WebMarkupContainer receiptConfirmationRequestedContainer;
    /**
     * Whether the information is standardized as an info product.
     */
    private CheckBox standardizedCheckbox;
    /**
     * EOIs list container.
     */
    private WebMarkupContainer eoisContainer;
    /**
     * Flow privacy container.
     */
    private WebMarkupContainer privacyContainer;
    /**
     * Privacy checkbox.
     */
    private AjaxCheckBox privateCheckBox;

    protected ExpandedFlowPanel(
            String id,
            IModel<Flow> model,
            boolean send,
            Set<Long> expansions,
            int index,
            PlanPage planPage ) {
        super( id, model, send, false, expansions, index );
        this.planPage = planPage;
        init();
    }

    private PlanPage getPlanPage() {
        return planPage;
    }


    private void init() {
        setOutputMarkupId( true );
        addHeader();
        addSimpleAdvanced();
        addInfoLabelOrLink();
        addStandardizedField();
        addNameField();
        addTagsPanel();
        addClassificationFields();
        addEOIs();
        addReferencesEventPhaseField();
        addPrivacy();
        addAskedForRadios();
        addSignificanceToTarget();
        addOtherField();
        addRestrictionFields();
        addIfTaskFails();
        addAllField();

        Node node = getOther();
        if ( node.isConnector() && node.getSegment().equals( getNode().getSegment() ) ) {
            add( new ConnectedFlowList( "others", (Connector) node ) );                   // NON-NLS
        } else {
            add( new Label( "others", "" ) );                                             // NON-NLS
        }
        // ChannelListPanel configures itself according
        // to the flow's canGetChannels() and canSetChannels() values
        channelRow = createChannelRow();
        add( channelRow );
        addMaxDelayRow();
        addSignificanceToSource();
        addFlowDescription();
        addCanBypassIntermediate();
        addReceiptConfirmationRequested();
        add( new AttachmentPanel( "attachments", new PropertyModel<Flow>( this, "flow" ) ) );
        issuesPanel = new IssuesPanel(
                "issues",
                new PropertyModel<ModelObject>( this, "flow" ),
                getExpansions() );
        issuesPanel.setOutputMarkupId( true );
        add( issuesPanel );
        adjustFields( getFlow() );
    }

    private boolean isShowSimpleForm() {
        return getPlanPage().isShowSimpleForm( getFlow() );
    }

    private void setShowSimpleForm( boolean val ) {
        getPlanPage().setShowSimpleForm( getFlow(), val );
    }

    private void addTagsPanel() {
        tagsContainer = new WebMarkupContainer( "tagsContainer" );
        tagsContainer.setOutputMarkupId( true );
        makeVisible( tagsContainer, !isShowSimpleForm() );
        add( tagsContainer );
        AjaxLink tagsLink = new AjaxLink( "tagsLink" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.AspectViewed, Channels.PLAN_SEARCHING, PlanSearchingFloatingPanel.TAGS ) );
            }
        };
        tagsLink.add( new AttributeModifier( "class", new Model<String>( "model-object-link" ) ) );
        tagsContainer.add( tagsLink );
        TagsPanel tagsPanel = new TagsPanel( "tags", new Model<Taggable>( getFlow() ) );
        tagsContainer.add( tagsPanel );
    }

    private void addClassificationFields() {
        classificationContainer = new WebMarkupContainer( "classificationContainer" );
        classificationContainer.setOutputMarkupId( true );
        makeVisible( classificationContainer, !isShowSimpleForm() );
        add( classificationContainer );
        addIntentField();
        addProhibitedField();
    }


    private void addIntentField() {
        intentChoice = new DropDownChoice<String>(
                "intent",
                new PropertyModel<String>( this, "intentLabel" ),
                getAllIntentLabels() );
        intentChoice.setOutputMarkupId( true );
        Flow.Intent intent = getFlow().getIntent();
        if ( intent != null ) {
            intentChoice.add( new AttributeModifier(
                    "title",
                    new Model<String>( intent.getHint() ) ) );
        }
        intentChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addIntentField();
                target.add( intentChoice );
                update( target, new Change( Change.Type.Updated, getFlow(), "intent" ) );
            }
        } );
        classificationContainer.addOrReplace( intentChoice );
    }

    private void addRestrictionFields() {
        restrictionsContainer = new WebMarkupContainer( "restrictionContainer" );
        restrictionsContainer.setOutputMarkupId( true );
        // makeVisible( restrictionsContainer, !isShowSimpleForm() );
        addOrReplace( restrictionsContainer );
        addRestrictedCheckBox();
        addRestrictionsSummary();
        addEditRestrictionsLink();
        addRestrictionsPanel();
    }

    private void addRestrictedCheckBox() {
        restrictedCheckBox = new CheckBox(
                "restricted",
                new PropertyModel<Boolean>( this, "restricted" ) );
        restrictedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addRestrictionsSummary();
                addEditRestrictionsLink();
                addRestrictionsPanel();
                target.add( restrictionsContainer );
                if ( !isRestricted() ) {
                    update( target, new Change( Change.Type.Updated, getFlow(), "restrictions" ) );
                }
            }
        } );
        restrictedCheckBox.setEnabled( isLockedByUser( getFlow() ) );
        restrictionsContainer.add( restrictedCheckBox );
    }

    private void addRestrictionsSummary() {
        String summary = !getFlow().getRestrictions().isEmpty()
                ? getFlow().getRestrictionString( isSend() )
                : "(No restriction)";
        Label restrictionsSummaryLabel = new Label( "restrictionsSummary", summary );
        restrictionsSummaryLabel.setOutputMarkupId( true );
        makeVisible( restrictionsSummaryLabel, isRestricted() && !editingRestrictions );
        restrictionsContainer.addOrReplace( restrictionsSummaryLabel );
    }

    private void addEditRestrictionsLink() {
        editingRestrictionsLink = new AjaxLink<String>(
                "editRestrictions"
        ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                editingRestrictions = !editingRestrictions;
                addRestrictionsSummary();
                addEditOrCloseLabel();
                addRestrictionsPanel();
                target.add( restrictionsContainer );
            }
        };
        makeVisible( editingRestrictionsLink, isRestricted() && isLockedByUser( getFlow() ) );
        addEditOrCloseLabel();
        restrictionsContainer.addOrReplace( editingRestrictionsLink );
    }

    private void addEditOrCloseLabel() {
        Label editOrCloseLabel = new Label( "editOrClose", editingRestrictions ? "Done" : "Change" );
        editOrCloseLabel.setOutputMarkupId( true );
        editingRestrictionsLink.addOrReplace( editOrCloseLabel );
    }

    private void addRestrictionsPanel() {
        restrictionsPanel = editingRestrictions
                ? new RestrictionsPanel( "restrictions", new PropertyModel<Flow>( this, "flow" ), isSend() )
                : new Label( "restrictions", "" );
        restrictionsPanel.setOutputMarkupId( true );
        makeVisible( restrictionsPanel, isRestricted() && editingRestrictions );
        restrictionsContainer.addOrReplace( restrictionsPanel );
    }

    private List<String> getAllIntentLabels() {
        List<String> labels = new ArrayList<String>();
        labels.add( NO_INTENT );
        labels.addAll( Flow.Intent.getAllLabels() );
        return labels;
    }

    private void addEOIs() {
        addEOIsEditLink();
        addEOIsList();
    }

    private void addEOIsEditLink() {
        AjaxLink editEOIsLink = new AjaxLink( "editEOIs" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.AspectViewed, getFlow(), EOIS ) );
            }
        };
        editEOIsLink.add( new AttributeModifier( "class", new Model<String>( "model-object-link" ) ) );
        add( editEOIsLink );
    }

    private void addEOIsList() {
        eoisContainer = new WebMarkupContainer( "eoisContainer" );
        eoisContainer.setOutputMarkupId( true );
        ListView<String> eoisList = new ListView<String>(
                "eois",
                new PropertyModel<List<String>>( this, "eoiSummaries" ) ) {
            @Override
            protected void populateItem( ListItem<String> item ) {
                item.add( new Label( "summary", item.getModelObject() ) );
            }
        };
        eoisContainer.add( eoisList );
        addOrReplace( eoisContainer );
    }

    /**
     * Get list of eois as strings.
     *
     * @return a list of strings
     */
    public List<String> getEoiSummaries() {
        List<String> eoiStrings = new ArrayList<String>();
        for ( ElementOfInformation eoi : getFlow().getEffectiveEois() ) {
            eoiStrings.add( eoi.getLabel() + ( eoi.isTimeSensitive() ? "*" : "" ) );
        }
        if ( eoiStrings.isEmpty() ) {
            eoiStrings.add( "(none)" );
        }
        return eoiStrings;
    }

    /**
     * Show/hide/enable/disable parts of the panel given the state of the flow.
     *
     * @param f the flow
     */
    private void adjustFields( Flow f ) {
        boolean lockedByUser = isLockedByUser( f );

        nameField.setEnabled( lockedByUser && f.canSetNameAndElements() && !f.isStandardized() );
        standardizedCheckbox.setEnabled( !f.getName().isEmpty() && lockedByUser && f.canSetNameAndElements() );
        intentChoice.setEnabled( lockedByUser && f.canSetNameAndElements() );
        askedForButtons.setEnabled( lockedByUser && f.canSetAskedFor() );
        allField.setVisible( isSend() && f.canGetAll() );
        allField.setEnabled( lockedByUser && isSend() && f.canSetAll() );
        significanceToTargetLabel.setVisible( f.canGetSignificanceToTarget() );
        significanceToTargetChoice.setEnabled(
                lockedByUser && f.canSetSignificanceToTarget() );
        channelRow.setVisible( f.canGetChannels() );
        this.timingContainer.setVisible( f.canGetMaxDelay() );
        delayPanel.enable( lockedByUser && f.canSetMaxDelay() );
        significanceToSourceContainer.setVisible( f.canGetSignificanceToSource() );
        triggersSourceContainer.setVisible( ( !isSend() || f.isAskedFor() ) && f.canGetTriggersSource() );
        triggersSourceCheckBox.setEnabled( lockedByUser && f.canSetTriggersSource() );
        terminatesSourceContainer.setVisible( f.canGetTerminatesSource() );
        terminatesSourceCheckBox.setEnabled( lockedByUser && f.canSetTerminatesSource() );
        otherChoice.setEnabled( lockedByUser );
        restrictedCheckBox.setEnabled( lockedByUser );
        flowDescription.setEnabled(
                ( isSend() && f.isNotification() || !isSend() && f.isAskedFor() ) && isLockedByUser( getFlow() ) );
        makeVisible( issuesPanel, getAnalyst().hasIssues( getQueryService(), getFlow(), false ) );
        makeVisible( ifTaskFailsContainer, canGetIfTaskFails() );
        ifTaskFailsCheckBox.setEnabled( canSetIfTaskFails() );
        makeVisible( prohibitedContainer, f.canGetProhibited() );
        prohibitedCheckBox.setEnabled( lockedByUser && f.canSetProhibited() );
        makeVisible( referencesEventPhaseContainer, f.canGetReferencesEventPhase() );
        makeVisible( canBypassIntermediateContainer, !isShowSimpleForm() && f.canGetCanBypassIntermediate() );
        makeVisible( receiptConfirmationRequestedContainer, !isShowSimpleForm() && f.canGetReceiptConfirmationRequested() );
        referencesEventPhaseCheckBox.setEnabled( lockedByUser && f.canSetReferencesEventPhase() );
        canBypassIntermediateCheckBox.setEnabled( lockedByUser && f.canSetCanBypassIntermediate() );
        receiptConfirmationRequestedCheckBox.setEnabled( lockedByUser && f.canSetReceiptConfirmationRequested() );
        privateCheckBox.setEnabled( lockedByUser && canSetPrivacy() );
    }

    /**
     * Show/hide/enable/disable parts of the panel given the state of the flow.
     *
     * @param f      the flow
     * @param target ajax request target
     */
    private void adjustFieldsOnUpdate( Flow f, AjaxRequestTarget target ) {
        triggersSourceContainer.setVisible(
                ( !isSend() || f.isAskedFor() ) && f.canGetTriggersSource() );
        target.add( triggersSourceContainer );
        flowDescription.setEnabled(
                ( isSend() && f.isNotification() || !isSend() && f.isAskedFor() ) && isLockedByUser( getFlow() ) );
        target.add( flowDescription );
        makeVisible( issuesPanel, getAnalyst().hasIssues( getQueryService(), getFlow(), false ) );
        target.add( issuesPanel );
        addRestrictionsSummary();
        target.add( restrictionsContainer );
    }

    private boolean canSetIfTaskFails() {
        Flow f = getFlow();
        return isLockedByUser( f )
                && canGetIfTaskFails();
    }

    private boolean canGetIfTaskFails() {
        Flow f = getFlow();
        // true if agent has the initiative (notifies or asks)
        //return isSend() && f.isNotification() || !isSend() && f.isAskedFor();
        return isSend() && f.isNotification();
    }

    private boolean canSetPrivacy() {
        Flow f = getFlow();
        return isSend() && ( f.isCapability() || f.isNotification() )
                || !isSend() && ( f.isNeed() || f.isAskedFor() );
    }

    private void addInfoLabelOrLink() {
        if ( getFlow().isStandardized() ) {
            InfoProduct infoProduct = getFlow().getInfoProduct();
            infoLinkOrLabel = new ModelObjectLink(
                    "information",
                    new Model<InfoProduct>( infoProduct ),
                    new Model<String>( "Information" ),
                    "Click to see the definition of the information product" );
        } else {
            infoLinkOrLabel = new Label( "information", "Information" );
        }
        infoLinkOrLabel.setOutputMarkupId( true );
        addOrReplace( infoLinkOrLabel );
    }

    private void addStandardizedField() {
        standardizedCheckbox = new AjaxCheckBox(
                "standardized",
                new PropertyModel<Boolean>( this, "standardized" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addInfoLabelOrLink();
                target.add( infoLinkOrLabel );
                adjustFields( getFlow() );
                target.add( nameField );
                addEOIsList();
                target.add( eoisContainer );
                update( target, new Change( Change.Type.Updated, getFlow(), "standardized" ) );
            }
        };
        Flow f = getFlow();
        standardizedCheckbox.setOutputMarkupId( true );
        standardizedCheckbox.setEnabled( !f.getName().isEmpty()
                && isLockedByUser( f )
                && f.canSetNameAndElements() );
        addOrReplace( standardizedCheckbox );
    }

    private void addNameField() {
        nameField = new AutoCompleteTextField<String>(
                "name",
                new PropertyModel<String>( this, "name" ),
                getAutoCompleteSettings() ) {
            @Override
            protected Iterator<String> getChoices( String s ) {
                return getFlowNameChoices( s );
            }
        };
        nameField.setOutputMarkupId( true );
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {

            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addIssuesAnnotation( nameField, getFlow(), "name" );
                target.add( nameField );
                addInfoLabelOrLink();
                target.add( infoLinkOrLabel );
                addStandardizedField();
                target.add( standardizedCheckbox );
                update( target, new Change( Change.Type.Updated, getFlow(), "name" ) );
            }
        } );

        nameField.add( new AjaxFormComponentUpdatingBehavior( "onkeyup" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                addOtherChoice();
                target.add( otherChoice );
            }
        } );
        addInputHint(
                nameField,
                "The name of the info "
                        + ( getFlow().isNeed()
                        ? "needed"
                        : getFlow().isCapability()
                        ? "that could be shared"
                        : isSend()
                        ? "to be sent"
                        : "to be received" ) );
        addIssuesAnnotation( nameField, getFlow(), nameField.getId() );
        add( nameField );
    }

    /**
     * Find all candidate names for a flow given partial name.
     * Look in sibling flows and all connector flows of the right "polarity".
     * //TODO - inefficient
     *
     * @param s partial name
     * @return an iterator on strings
     */
    private Iterator<String> getFlowNameChoices( String s ) {
        QueryService queryService = getQueryService();
        Set<String> choices = new HashSet<String>();
        if ( s.length() > 1 ) {
            Node node = getNode();
            Iterator<Flow> nodeFlows = node.sends();
            while ( nodeFlows.hasNext() ) {
                String name = nodeFlows.next().getName();
                if ( queryService.likelyRelated( s, name ) )
                    choices.add( name );
            }
            nodeFlows = node.receives();
            while ( nodeFlows.hasNext() ) {
                String name = nodeFlows.next().getName();
                if ( getQueryService().likelyRelated( s, name ) )
                    choices.add( name );
            }
            // all name-matching in-segment flows of the right polarity
            for ( Flow flow : findRelevantInternalFlows() ) {
                String name = flow.getName();
                if ( queryService.likelyRelated( s, name ) )
                    choices.add( name );
            }
            // all name-matching connector inner flows of the right "polarity"
            for ( Connector connector : findAllRelevantConnectors() ) {
                String name = connector.getInnerFlow().getName();
                if ( queryService.likelyRelated( s, name ) )
                    choices.add( name );
            }
        }
        return choices.iterator();
    }

    private void addPrivacy() {
        privacyContainer = new WebMarkupContainer( "privacy" );
        privacyContainer.setOutputMarkupId( true );
        makeVisible( privacyContainer, getFlow().isNeed() || getFlow().isCapability() );
        addOrReplace( privacyContainer );
        Label needOrCapabilityLabel = new Label( "needOrCapability", getFlow().isNeed() ? " need for a" : " capability to share via" );
        needOrCapabilityLabel.setOutputMarkupId( true );
        privacyContainer.addOrReplace( needOrCapabilityLabel );
        privateCheckBox = new AjaxCheckBox(
                "private",
                new PropertyModel<Boolean>( this, "private" ) ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getFlow(), "published" ) );
            }
        };
        if ( getFlow().isNeed() || getFlow().isCapability() ) {
            addTipTitle( privateCheckBox, "The occurrence of a private "
                    + ( getFlow().isNeed()
                    ? "need"
                    : getFlow().isCapability()
                    ? "capability"
                    : "sharing" )
                    + " is not to be revealed" );
        }
        privateCheckBox.setEnabled( canSetPrivacy() );
        privacyContainer.add( privateCheckBox );
    }

    private void addAskedForRadios() {
        askedForButtons = new RadioGroup<Boolean>(
                "askedFor",
                new PropertyModel<Boolean>( this, "askedFor" ) );
        askedForButtons.add( new Radio<Boolean>( "askedForTrue",
                new Model<Boolean>( true ) ) );
        askedForButtons.add( new Radio<Boolean>( "askedForFalse",
                new Model<Boolean>( false ) ) );
        askedForButtons.add( new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                makeVisible( ifTaskFailsContainer, canGetIfTaskFails() );
                ifTaskFailsCheckBox.setEnabled( canSetIfTaskFails() );
                target.add( ifTaskFailsContainer );
                addSignificanceToTarget();
                target.add( significanceToTargetLabel );
                addSignificanceToSource();
                target.add( significanceToSourceContainer );
                addPrivacy();
                target.add( privacyContainer );
                update( target, new Change( Change.Type.Updated, getFlow(), "askedFor" ) );
            }
        } );

        add( askedForButtons );
    }

    private void addSignificanceToTarget() {
        significanceToTargetLabel = new WebMarkupContainer( "target-significance-label" );
        significanceToTargetLabel.setOutputMarkupId( true );
        addOrReplace( significanceToTargetLabel );
        significanceToTargetLabel.add(
                new Label( "target-label", isSend() ? "the recipient's task" : "this task" ) );
        significanceToTargetChoice = new DropDownChoice<Flow.Significance>(
                "significance-to-target",
                new PropertyModel<Flow.Significance>( this, "significanceToTarget" ),
                new PropertyModel<List<? extends Flow.Significance>>(
                        this,
                        "significanceToTargetChoices" ),
                new IChoiceRenderer<Flow.Significance>() {

                    @Override
                    public Object getDisplayValue( Flow.Significance significance ) {
                        return significance.getLabel();
                    }

                    @Override
                    public String getIdValue( Flow.Significance significance, int i ) {
                        return significance.toString();
                    }
                }
        );
        significanceToTargetChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update(
                        target,
                        new Change( Change.Type.Updated, getFlow(), "significanceToTarget" ) );
            }
        } );
        significanceToTargetLabel.add( significanceToTargetChoice );
    }

    private void addSignificanceToSource() {
        significanceToSourceContainer = new WebMarkupContainer( "significance-to-source" );
        significanceToSourceContainer.setOutputMarkupId( true );
        makeVisible( significanceToSourceContainer, !isShowSimpleForm() );
        addOrReplace( significanceToSourceContainer );
        Component sourceTaskReference;
        if ( isSend() ) {
            sourceTaskReference = new Label( "source-task", "This task" );
        } else {
            sourceTaskReference = new ModelObjectLink(
                    "source-task",
                    new Model<ModelObject>( getFlow().getSource() ),
                    new Model<String>( "Sender's task" ) );
        }
        significanceToSourceContainer.add( sourceTaskReference );
        triggersSourceContainer = new WebMarkupContainer( "triggers-source-container" );
        triggersSourceContainer.setOutputMarkupId( true );
        significanceToSourceContainer.add( triggersSourceContainer );
        triggersSourceCheckBox = new CheckBox(
                "triggers-source",
                new PropertyModel<Boolean>( this, "triggeringToSource" ) );
        triggersSourceCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update(
                        target,
                        new Change( Change.Type.Updated, getFlow(), "significanceToSource" ) );
            }
        } );
        triggersSourceContainer.add( triggersSourceCheckBox );
        terminatesSourceContainer = new WebMarkupContainer( "terminates-source-container" );
        significanceToSourceContainer.add( terminatesSourceContainer );
        terminatesSourceCheckBox = new CheckBox(
                "terminates-source",
                new PropertyModel<Boolean>( this, "terminatingToSource" ) );
        terminatesSourceCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update(
                        target,
                        new Change( Change.Type.Updated, getFlow(), "significanceToSource" ) );
            }
        } );
        terminatesSourceContainer.add( terminatesSourceCheckBox );
        terminatesSourceContainer.add(
                new Label(
                        "notifying-or-replying",
                        new PropertyModel<String>( this, "replyingOrNotifying" ) ) );
    }

    private void addIfTaskFails() {
        ifTaskFailsContainer = new WebMarkupContainer( "ifTaskFailsContainer" );
        ifTaskFailsContainer.setOutputMarkupId( true );
        classificationContainer.add( ifTaskFailsContainer );
        ifTaskFailsCheckBox = new CheckBox(
                "ifTaskFails",
                new PropertyModel<Boolean>( this, "ifTaskFails" )
        );
        ifTaskFailsCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update(
                        target,
                        new Change( Change.Type.Updated, getFlow(), "ifTaskFails" ) );
            }
        } );
        ifTaskFailsContainer.add( ifTaskFailsCheckBox );
    }

    private void addProhibitedField() {
        prohibitedContainer = new WebMarkupContainer( "prohibitedContainer" );
        prohibitedContainer.setOutputMarkupId( true );
        classificationContainer.add( prohibitedContainer );
        prohibitedCheckBox = new CheckBox(
                "prohibited",
                new PropertyModel<Boolean>( this, "prohibited" )
        );
        prohibitedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update(
                        target,
                        new Change( Change.Type.Updated, getFlow(), "prohibited" ) );
            }
        } );
        prohibitedContainer.add( prohibitedCheckBox );
    }

    private void addReferencesEventPhaseField() {
        referencesEventPhaseContainer = new WebMarkupContainer( "referencesEventPhaseContainer" );
        referencesEventPhaseContainer.setOutputMarkupId( true );
        makeVisible( referencesEventPhaseContainer, !isShowSimpleForm() );
        add( referencesEventPhaseContainer );
        // references event phase
        referencesEventPhaseCheckBox = new CheckBox(
                "referencesEventPhase",
                new PropertyModel<Boolean>( this, "referencesEventPhase" )
        );
        referencesEventPhaseCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update(
                        target,
                        new Change( Change.Type.Updated, getFlow(), "referencesEventPhase" ) );
            }
        } );
        referencesEventPhaseContainer.add( new Label( "eventPhaseTitle", getEventPhaseTitle() ) );
        referencesEventPhaseContainer.add( referencesEventPhaseCheckBox );
    }

    private String getEventPhaseTitle() {
        return getFlow().getSegment().getPhaseEventTitle();
    }

    private void addFlowDescription() {
        flowDescription = new TextArea<String>( "description",
                new PropertyModel<String>( this, "description" ) );
        flowDescription.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getFlow(), "description" ) );
            }
        } );
        flowDescription.setOutputMarkupId( true );
        addInputHint( flowDescription, "Brief advice" );
        add( flowDescription );
    }

    private void addCanBypassIntermediate() {
        canBypassIntermediateContainer = new WebMarkupContainer( "canBypassIntermediateContainer" );
        canBypassIntermediateContainer.setOutputMarkupId( true );
        makeVisible( canBypassIntermediateContainer, !isShowSimpleForm() );
        add( canBypassIntermediateContainer );
        // can bypass intermediate
        canBypassIntermediateCheckBox = new CheckBox(
                "canBypassIntermediate",
                new PropertyModel<Boolean>( this, "canBypassIntermediate" )
        );
        canBypassIntermediateCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update(
                        target,
                        new Change( Change.Type.Updated, getFlow(), "canBypassIntermediate" ) );
            }
        } );
        canBypassIntermediateContainer.add( canBypassIntermediateCheckBox );
        // receipt requested
    }

    private void addReceiptConfirmationRequested() {
        receiptConfirmationRequestedContainer = new WebMarkupContainer( "receiptConfirmationRequestedContainer" );
        receiptConfirmationRequestedContainer.setOutputMarkupId( true );
        makeVisible( receiptConfirmationRequestedContainer, !isShowSimpleForm() );
        add( receiptConfirmationRequestedContainer );
        // receipt confirmation requested
        receiptConfirmationRequestedCheckBox = new CheckBox(
                "receiptConfirmationRequested",
                new PropertyModel<Boolean>( this, "receiptConfirmationRequested" )
        );
        receiptConfirmationRequestedCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update(
                        target,
                        new Change( Change.Type.Updated, getFlow(), "receiptConfirmationRequested" ) );
            }
        } );
        receiptConfirmationRequestedContainer.add( receiptConfirmationRequestedCheckBox );
    }


    /**
     * Return label string according to type of flow.
     *
     * @return a string
     */
    public String getReplyingOrNotifying() {
        return getFlow().isAskedFor() ? "replying" : "notifying";
    }

    /**
     * Get the list of candidate significances to the target
     *
     * @return a list of significances
     */
    public List<Flow.Significance> getSignificanceToTargetChoices() {
        List<Flow.Significance> significances = new ArrayList<Flow.Significance>();
        significances.add( Flow.Significance.Useful );
        significances.add( Flow.Significance.Critical );
        significances.add( Flow.Significance.Terminates );
        if ( !getFlow().isAskedFor() ) significances.add( Flow.Significance.Triggers );
        return significances;
    }

    private void addHeader() {
        titlePanel = new FlowTitlePanel( "title", getFlow(), isSend() );
        // Add style classes
        titlePanel.add( new AttributeModifier( "class", new Model<String>( getCssClasses() ) ) );
        titlePanel.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                Change change = new Change( Change.Type.Collapsed, getFlow() );
                change.addQualifier( "updated", isFlowUpdated() );
                update( target, change );
            }
        } );
        titlePanel.setOutputMarkupId( true );
        addOrReplace( titlePanel );
    }

    private void addSimpleAdvanced() {
        simpleAdvanced = new Label(
                "simpleAdvanced",
                new Model<String>( isShowSimpleForm() ? "Show advanced form" : "Show simple form" ) );
        simpleAdvanced.setOutputMarkupId( true );
        simpleAdvanced.add( new AjaxEventBehavior( "onclick" ) {
            @Override
            protected void onEvent( AjaxRequestTarget target ) {
                setShowSimpleForm( !isShowSimpleForm() );
                adjustSimpleAdvancedFields( target );
                addSimpleAdvanced();
                target.add( simpleAdvanced );
            }
        } );
        addOrReplace( simpleAdvanced );
    }

    private void adjustSimpleAdvancedFields( AjaxRequestTarget target ) {
        Flow f = getFlow();
        boolean showSimpleForm = isShowSimpleForm();
        makeVisible( tagsContainer, !showSimpleForm );
        target.add( tagsContainer );
        makeVisible( classificationContainer, !showSimpleForm );
        target.add( classificationContainer );
        makeVisible( restrictionsContainer, !showSimpleForm );
        target.add( restrictionsContainer );
        makeVisible( referencesEventPhaseContainer, !showSimpleForm );
        target.add( referencesEventPhaseContainer );
        makeVisible( canBypassIntermediateContainer, !showSimpleForm
                && f.canGetCanBypassIntermediate() );
        target.add( canBypassIntermediateContainer );
        makeVisible( receiptConfirmationRequestedContainer, !showSimpleForm && f.canGetReceiptConfirmationRequested() );
        target.add( receiptConfirmationRequestedContainer );
        makeVisible( timingContainer, !showSimpleForm );
        target.add( timingContainer );
        makeVisible( significanceToSourceContainer, !showSimpleForm );
        target.add( significanceToSourceContainer );
    }


    private void addAllField() {
        CheckBox checkBox = new CheckBox(
                "all",
                new PropertyModel<Boolean>( this, "all" ) );
        checkBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {

            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getFlow(), "all" ) );
            }
        } );
        allField = new FormComponentLabel( "all-label", checkBox );
        allField.add( checkBox );
        add( allField );
    }

    /*
        private FormComponentLabel addLabeled( String id, FormComponent<?> component ) {
            component.setOutputMarkupId( true );
            FormComponentLabel result = new FormComponentLabel( id, component );
            add( result );
            add( component );
            addIssuesAnnotation( component, getFlow(), component.getId() );
            return result;
        }
    */
    private void addOtherField() {
        addOtherLink();
        addOtherChoice();
    }

    private void addOtherLink() {
        otherLink = new ModelObjectLink( "other-link",
                new PropertyModel<Part>( this, "otherPart" ),
                new Model<String>( isSend() ? "To task" : "From task" ) );
        otherLink.setOutputMarkupId( true );
        otherLink.add( new AttributeModifier( "class", new Model<String>( "part-link" ) ) );
        addOrReplace( otherLink );
    }

    private void addOtherChoice() {
        otherChoice = new OtherNodeSelectorPanel(
                "other",
                new PropertyModel<Node>( this, "node" ),
                new PropertyModel<Node>( this, "other" ),
                new PropertyModel<String>( this, "name" ),
                new PropertyModel<List<Node>>( this, "firstChoices" ),
                new PropertyModel<List<Node>>( this, "secondChoices" ) );
        addTipTitle( otherChoice, new Model<String>( getOtherPart().displayString() ) );
        otherChoice.setOutputMarkupId( true );
        addOrReplace( otherChoice );
    }

    /**
     * @return the node on this side of the flow
     */
    public Node getNode() {
        return isSend() ? getFlow().getSource() : getFlow().getTarget();
    }

    /**
     * Get the node at the other side of this flow: the source if receive, the target if
     * send.
     *
     * @return the other side of this flow.
     */
    public final Node getOther() {
        Flow f = getFlow();
        return f.isInternal()
                ? isSend()
                ? f.getTarget()
                : f.getSource()
                : ( (ExternalFlow) f ).getConnector();
    }

    /**
     * Get list of obvious candidates: parts in segment with synonymous capability/need, implied or explicit.
     *
     * @return a list of nodes
     */
    public List<Node> getFirstChoices() {
        Set<Node> firstChoices = new HashSet<Node>();
        // find non-redundant connectors of matching needs or capabilities.
        for ( Connector connector : findRelatedLocalConnectors() ) {
            if ( !isRedundant( connector ) )
                firstChoices.add( connector );
        }
        // Find related external connectors
        for ( Connector connector : findRelatedExternalConnectors() ) {
            if ( !isRedundant( connector ) )
                firstChoices.add( connector );
        }
        // Find all parts involved in related sharing but without related need or capability
        for ( Part part : findRelatedParts() ) {
            if ( !isRedundant( part ) )
                firstChoices.add( part );
        }
        return new ArrayList<Node>( firstChoices );
    }

    private List<Connector> findRelatedLocalConnectors() {
        Node node = getNode();
        Segment segment = node.getSegment();
        Set<Connector> result = new HashSet<Connector>();
        Iterator<Node> nodes = segment.nodes();
        while ( nodes.hasNext() ) {
            Node n = nodes.next();
            if ( !node.equals( n ) ) {
                if ( n.isConnector() ) {
                    Connector connector = (Connector) n;
                    Flow connectorFlow = connector.getInnerFlow();
                    if ( isEmptyOrEquivalent( connectorFlow ) ) {
                        if ( isSend() ) {
                            if ( connector.isSource()
                                    && !connectorFlow.getTarget().equals( node ) )
                                result.add( connector );
                        } else {
                            if ( connector.isTarget()
                                    && !connectorFlow.getSource().equals( node ) )
                                result.add( connector );
                        }
                    }
                }
            }
        }
        return new ArrayList<Connector>( result );
    }

    private List<Connector> findRelatedExternalConnectors() {
        Node node = getNode();
        Node other = getOther();
        Segment segment = node.getSegment();
        Set<Connector> result = new HashSet<Connector>();
        QueryService queryService = getQueryService();
        for ( Segment s : queryService.list( Segment.class ) ) {
            if ( !segment.equals( s ) ) {
                Iterator<Connector> c = isSend() ? s.inputs() : s.outputs();
                while ( c.hasNext() ) {
                    Connector connector = c.next();
                    Flow connectorFlow = connector.getInnerFlow();
                    if ( isEmptyOrEquivalent( connectorFlow ) ) {
                        if ( other.equals( connector )
                                || ( !node.isConnectedTo( isSend(), connector, getFlow().getName() ) ) )
                            result.add( connector );
                    }
                }
            }
        }
        return new ArrayList<Connector>( result );
    }

    private List<Part> findRelatedParts() {
        Node node = getNode();
        Set<Part> relatedParts = new HashSet<Part>();
        String info = getFlow().getName();
        for ( Iterator<Part> parts = node.getSegment().parts(); parts.hasNext(); ) {
            Part part = parts.next();
            if ( isSend() ) {
                if ( part.receivesNamed( info ).hasNext() )
                    relatedParts.add( part );
            } else {
                if ( part.sendsNamed( info ).hasNext() )
                    relatedParts.add( part );
            }
        }
        return new ArrayList<Part>( relatedParts );
    }

    // Is there's already a flow for the node with the part that has the need or capability composed of the connector?
    private boolean isRedundant( Connector connector ) {
        // Local connector
        if ( connector.getSegment().equals( getNode().getSegment() ) ) {
            Part connectingPart = connector.getInnerFlow().getLocalPart();
            return getNode().isConnectedTo( isSend(), connectingPart, getFlow().getName() );
            // external connector
        } else if ( getFlow().hasConnector() ) {
            // Tested connector is from another segment -> would lead to an external flow
            Flow flow = getFlow();
            // only a target connector (externalized connector)  registers the external flows
            // external flows are to a part which is itself the target of an internal flow
            // with another (internalized) connector as source
            Connector externalizedConnector = (Connector) ( isSend() ? flow.getTarget() : connector );
            final Connector internalizedConnector = (Connector) (
                    isSend()
                            ? connector :
                            flow.isExternal()
                                    ? ( (ExternalFlow) flow ).getConnector()
                                    : flow.getSource() );
            return CollectionUtils.exists(
                    IteratorUtils.toList( externalizedConnector.externalFlows() ),
                    new Predicate() {
                        @Override
                        public boolean evaluate( Object object ) {
                            ExternalFlow externalFlow = (ExternalFlow) object;
                            return externalFlow.getPart()
                                    .equals( internalizedConnector.getInnerFlow().getContactedPart() );
                        }
                    }
            );
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private boolean isRedundant( Part part ) {
        String info = getFlow().getName();
        // redundant if part has a matching need or capability
        List<Flow> needsOrCapabilities = isSend()
                ? IteratorUtils.toList( part.receivesNamed( info ) )
                : IteratorUtils.toList( part.sendsNamed( info ) );
        return CollectionUtils.exists(
                needsOrCapabilities,
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return !( (Flow) object ).isSharing();
                    }
                }
        );
    }

    /**
     * Get list of all non-redundant, local parts that are not redundant.
     *
     * @return a list of parts
     */
    @SuppressWarnings("unchecked")
    public List<Node> getSecondChoices() {
        final List<Part> relatedParts = findRelatedParts();
        final Node node = getNode();
        final Node other = getOther();
        return (List<Node>) CollectionUtils.select(
                IteratorUtils.toList( node.getSegment().parts() ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        Part part = (Part) object;
                        return !part.equals( node )
                                && !part.equals( other )
                                && !relatedParts.contains( part );
                    }
                }
        );
    }

    /**
     * Add the channels section.
     *
     * @return the channels section
     */
    protected abstract WebMarkupContainer createChannelRow();

    private void addMaxDelayRow() {
        this.timingContainer = new WebMarkupContainer( "timing" );
        this.timingContainer.setOutputMarkupId( true );
        makeVisible( this.timingContainer, !isShowSimpleForm() );
        add( this.timingContainer );
        delayPanel = new DelayPanel(
                "max-delay",
                new PropertyModel<ModelObject>( this, "flow" ),
                "maxDelay" );
        this.timingContainer.add( delayPanel );
    }

    /**
     * Figure out if channel field is relevant.
     *
     * @param f the flow being displayed
     * @return true if field should be visible
     */
    protected abstract boolean isChannelRelevant( Flow f );

    /**
     * Figure out if channel field is editable.
     *
     * @param f the flow being displayed
     * @return true if field can be edited by the user on this side
     */
    protected abstract boolean isChannelEditable( Flow f );

    /**
     * Get list of potential source/targets for this flow (the other node or connectors).
     *
     * @return the list of nodes
     */
    public List<? extends Node> getOtherNodes() {
        Node node = getNode();
        Node other = getOther();
        Segment segment = node.getSegment();
        Set<Node> result = new HashSet<Node>();

        // Add other parts of this segment
        Iterator<Node> nodes = segment.nodes();
        while ( nodes.hasNext() ) {
            Node n = nodes.next();
            if ( !node.equals( n ) ) {
                if ( n.equals( other ) ) {
                    result.add( n );
                } else if ( n.isConnector() ) {
                    Connector connector = (Connector) n;
                    Flow connectorFlow = connector.getInnerFlow();
                    if ( isEmptyOrEquivalent( connectorFlow ) ) {
                        if ( isSend() ) {
                            if ( connector.isSource()
                                    && !connectorFlow.getTarget().equals( node )
                                    && !isRedundant( connector ) )
                                result.add( connector );
                        } else {
                            if ( connector.isTarget()
                                    && !connectorFlow.getSource().equals( node )
                                    && !isRedundant( connector ) )
                                result.add( connector );
                        }
                    }
                }
                /**
                 else {
                 // a part in segment with same flow to/from part
                 if ( hasPartFlowWithSameName( n ) ) result.add( n );
                 }
                 **/
            }
        }
        // Add inputs/outputs of other segments
        QueryService queryService = getQueryService();
        for ( Segment s : queryService.list( Segment.class ) ) {
            if ( !segment.equals( s ) ) {
                Iterator<Connector> c = isSend() ? s.inputs() : s.outputs();
                while ( c.hasNext() ) {
                    Connector connector = c.next();
                    Flow connectorFlow = connector.getInnerFlow();
                    if ( isEmptyOrEquivalent( connectorFlow ) ) {
                        if ( other.equals( connector )
                                || ( !node.isConnectedTo( isSend(), connector, getFlow().getName() )
                                && !isRedundant( connector )
                        )
                                )
                            result.add( connector );
                    }
                }
            }
        }
        return new ArrayList<Node>( result );
    }
/*
    private boolean hasPartFlowWithSameName( Node n ) {
        String name = getFlow().getFlowName();
        Iterator<Flow> flows = isSend() ? n.receives() : n.sends();
        boolean hasSameName = false;
        while ( !hasSameName && flows.hasNext() ) {
            Flow otherFlow = flows.next();
            hasSameName = !otherFlow.hasConnector() && Matcher.same( otherFlow.getFlowName(), name );
        }
        return hasSameName;
    }
*/

    private boolean isEmptyOrEquivalent( SegmentObject connectorFlow ) {
        return getFlow().getName().isEmpty()
                || Matcher.same( getFlow().getName(), connectorFlow.getName() );
    }

    private List<Flow> findRelevantInternalFlows() {
        List<Flow> result = new ArrayList<Flow>();
        Iterator<Part> parts = getFlow().getSegment().parts();
        while ( parts.hasNext() ) {
            Iterator<Flow> flows = isSend()
                    ? parts.next().receives()
                    : parts.next().sends();
            while ( flows.hasNext() ) {
                Flow otherFlow = flows.next();
                if ( !otherFlow.equals( getFlow() ) ) result.add( otherFlow );
            }
        }
        return result;
    }

    private List<Connector> findAllRelevantConnectors() {
        List<Connector> result = new ArrayList<Connector>();
        QueryService queryService = getQueryService();
        for ( Segment s : queryService.list( Segment.class ) ) {
            Iterator<Connector> connectorIterator = isSend()
                    ? s.inputs()
                    : s.outputs();
            while ( connectorIterator.hasNext() )
                result.add( connectorIterator.next() );
        }
        return result;
    }


    /**
     * Set the node at the other side of this flow by connecting "through" a connector
     * to the part in the connector's innerflow.
     *
     * @param other the new source or target
     */
    public void setOther( Node other ) {
        Change change;
        if ( other.isConnector() && getFlow().hasConnector() ) {
            Connector connector = (Connector) other;
            Flow need = isSend() ? connector.getInnerFlow() : getFlow();
            Flow capability = isSend() ? getFlow() : connector.getInnerFlow();
            change = doCommand( new SatisfyNeed( getUser().getUsername(), need,
                    capability,
                    SatisfyNeed.KEEP_CAPABILITY,
                    SatisfyNeed.KEEP_NEED ) );
        } else {
            change = doCommand( new RedirectFlow( getUser().getUsername(), getFlow(), other, isSend() ) );
        }
        Flow newFlow = (Flow) change.getSubject( getCommunityService() );
        if ( newFlow != null ) { // TODO Find out why this has happened...
            // requestLockOn( newFlow );
            setFlow( newFlow );
        }
    }

    /**
     * Get other part connected to.
     *
     * @return a part
     */
    public Part getOtherPart() {
        Node other = getOther();
        if ( other.isConnector() ) {
            return ( (Connector) other ).getInnerFlow().getLocalPart();
        } else {
            return (Part) other;
        }
    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public void setMarkedForDeletion( boolean delete ) {
        markedForDeletion = delete;
    }

    /**
     * Get flow name.
     *
     * @return a string
     */
    public String getName() {
        return getFlow().getName();
    }

    /**
     * Set flow name via command.
     *
     * @param name a string
     */
    public void setName( String name ) {
        if ( !getFlow().isStandardized() ) {
            doCommand( new UpdateSegmentObject( getUser().getUsername(), getFlow(), "name", name ) );
        }
    }

    /**
     * Get flow description.
     *
     * @return a string
     */
    public String getDescription() {
        return getFlow().getDescription();
    }

    /**
     * Set flow description via command.
     *
     * @param val a string
     */
    public void setDescription( String val ) {
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getFlow(), "description", val ) );
    }


    public boolean isAskedFor() {
        return getFlow().isAskedFor();
    }

    /**
     * Sets asked for.
     *
     * @param val a boolean
     */
    public void setAskedFor( boolean val ) {
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getFlow(), "askedFor", val ) );
    }

    public boolean isAll() {
        return getFlow().isAll();
    }

    /**
     * Sets all.
     *
     * @param value a boolean
     */
    public void setAll( boolean value ) {
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getFlow(), "all", value ) );
    }

    public Flow.Significance getSignificanceToTarget() {
        return getFlow().getSignificanceToTarget();
    }

    /**
     * Sets significance to target.
     *
     * @param val a flow significance
     */
    public void setSignificanceToTarget( Flow.Significance val ) {
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getFlow(), "significanceToTarget", val ) );
    }

    public Flow.Significance getSignificanceToSource() {
        return getFlow().getSignificanceToSource();
    }

    /**
     * Sets significance to source.
     *
     * @param val a flow significance
     */
    public void setSignificanceToSource( Flow.Significance val ) {
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getFlow(), "significanceToSource", val ) );
    }

    /**
     * Whether the flow triggers the source
     *
     * @return a boolean
     */
    public boolean isTriggeringToSource() {
        return getFlow().getSignificanceToSource() == Flow.Significance.Triggers;
    }

    /**
     * Set the triggering significance to the source
     *
     * @param triggers a boolean
     */
    public void setTriggeringToSource( boolean triggers ) {
        if ( triggers ) {
            setSignificanceToSource( Flow.Significance.Triggers );
        } else {
            setSignificanceToSource( Flow.Significance.None );
        }
    }

    /**
     * Whether the flow terminates the source
     *
     * @return a boolean
     */
    public boolean isTerminatingToSource() {
        return getFlow().getSignificanceToSource() == Flow.Significance.Terminates;
    }

    /**
     * Set the terminating significance to the source
     *
     * @param terminates a boolean
     */
    public void setTerminatingToSource( boolean terminates ) {
        if ( terminates ) {
            setSignificanceToSource( Flow.Significance.Terminates );
        } else {
            setSignificanceToSource( Flow.Significance.None );
        }
    }

    /**
     * Get label for flow's intent.
     *
     * @return a string
     */
    public String getIntentLabel() {
        Flow.Intent intent = getFlow().getIntent();
        return intent == null ? NO_INTENT : intent.getLabel();
    }

    /**
     * Set the flow's intent given the intent's label.
     *
     * @param label a string
     */
    public void setIntentLabel( String label ) {
        if ( label != null ) {
            Flow.Intent intent = label.equals( NO_INTENT ) ? null : Flow.Intent.valueOfLabel( label );
            doCommand( new UpdateSegmentObject( getUser().getUsername(), getFlow(), "intent", intent ) );
        }
    }

    /**
     * Whether the flow is restricted.
     *
     * @return a boolean
     */
    public boolean isRestricted() {
        return restricted || !getFlow().getRestrictions().isEmpty();
    }

    /**
     * Restrict or unrestrict the flow.
     *
     * @param val a boolean
     */
    public void setRestricted( boolean val ) {
        this.restricted = val;
        if ( !val && !getFlow().getRestrictions().isEmpty() ) {
            doCommand(
                    new UpdateSegmentObject( getUser().getUsername(),
                            getFlow(),
                            "restrictions",
                            new ArrayList<Flow.Restriction>() ) );
        }
    }

    /**
     * Get whether flow is if task fails.
     *
     * @return a boolean
     */
    public boolean isIfTaskFails() {
        return getFlow().isIfTaskFails();
    }

    public void setIfTaskFails( boolean val ) {
        if ( val != getFlow().isIfTaskFails() ) {
            doCommand( new UpdateSegmentObject( getUser().getUsername(), getFlow(), "ifTaskFails", val ) );
        }
    }

    /**
     * Get whether flow is prohibited.
     *
     * @return a boolean
     */
    public boolean isProhibited() {
        return getFlow().isProhibited();
    }

    public void setProhibited( boolean val ) {
        if ( val != getFlow().isProhibited() ) {
            doCommand( new UpdateSegmentObject( getUser().getUsername(), getFlow(), "prohibited", val ) );
        }
    }

    /**
     * Get whether sender can reference event phase.
     *
     * @return a boolean
     */
    public boolean isReferencesEventPhase() {
        return getFlow().isReferencesEventPhase();
    }

    public void setReferencesEventPhase( boolean val ) {
        if ( val != getFlow().isReferencesEventPhase() ) {
            doCommand( new UpdateSegmentObject( getUser().getUsername(), getFlow(), "referencesEventPhase", val ) );
        }
    }

    /**
     * Get whether sender can bypass intermediate.
     *
     * @return a boolean
     */
    public boolean isCanBypassIntermediate() {
        return getFlow().isCanBypassIntermediate();
    }

    public void setCanBypassIntermediate( boolean val ) {
        if ( val != getFlow().isCanBypassIntermediate() ) {
            doCommand( new UpdateSegmentObject( getUser().getUsername(), getFlow(), "canBypassIntermediate", val ) );
        }
    }

    /**
     * Get whether information is standardized as an information product.
     *
     * @return a boolean
     */
    public boolean isStandardized() {
        return getFlow().isStandardized();
    }

    public void setStandardized( boolean val ) {
        doCommand( new StandardizeInformation( getUser().getUsername(), getFlow(), val ) );
    }

    /**
     * Get whether the flow is NOT published.
     *
     * @return a boolean
     */
    public boolean isPrivate() {
        return !getFlow().isPublished();
    }

    public void setPrivate( boolean val ) {
        doCommand( new UpdateSegmentObject( getUser().getUsername(), getFlow(), "published", !val ) );
    }


    /**
     * Get whether receiver must confirm receipt.
     *
     * @return a boolean
     */
    public boolean isReceiptConfirmationRequested() {
        return getFlow().isReceiptConfirmationRequested();
    }

    public void setReceiptConfirmationRequested( boolean val ) {
        if ( val != getFlow().isReceiptConfirmationRequested() ) {
            doCommand( new UpdateSegmentObject( getUser().getUsername(), getFlow(), "receiptConfirmationRequested", val ) );
        }
    }

    @Override
    public void changed( Change change ) {
        // ignore selection of other node - don't propagate selection
        if ( !( change.isSelected()
                && change.isForInstanceOf( Node.class )
                && change.isForProperty( "other" ) ) ) {
            if ( change.isUpdated() ) {
                setFlowUpdated( true );
            }
            super.changed( change );
        }
    }

    @Override
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        change.addQualifier( "updated", isFlowUpdated() );
        if ( change.isSelected()
                && change.isForInstanceOf( Node.class )
                && change.isForProperty( "other" ) ) {
            Flow oldFlow = getFlow();
            setOther( (Node) change.getSubject( getCommunityService() ) );
            Flow newFlow = getFlow();
            if ( newFlow != null ) adjustFields( newFlow );
            update( target, new Change( Change.Type.Updated, getFlow() ) ); // was getNode()
            if ( getFlow() != null && oldFlow != null && !getFlow().equals( oldFlow ) ) {
                update( target, new Change( Change.Type.Collapsed, oldFlow.getId() ) );
                update( target, new Change( Change.Type.Expanded, newFlow ) );
            }
        } else {
            if ( change.isUpdated() ) {
                Flow flow = getFlow();
                if ( flow != null ) {
                    adjustFieldsOnUpdate( flow, target );
                    addHeader();
                    target.add( titlePanel );
                }
            }
            super.updateWith( target, change, updated );
        }
    }

}
