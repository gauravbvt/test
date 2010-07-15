package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.RedirectFlow;
import com.mindalliance.channels.command.commands.SatisfyNeed;
import com.mindalliance.channels.command.commands.UpdateSegmentObject;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.ElementOfInformation;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.SegmentObject;
import com.mindalliance.channels.nlp.Matcher;
import com.mindalliance.channels.pages.Channels;
import com.mindalliance.channels.pages.ModelObjectLink;
import com.mindalliance.channels.pages.Updatable;
import com.mindalliance.channels.pages.components.AttachmentPanel;
import com.mindalliance.channels.pages.components.DelayPanel;
import com.mindalliance.channels.pages.components.IssuesPanel;
import com.mindalliance.channels.query.QueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
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
     * True if this flow is marked for deletion.
     */
    private boolean markedForDeletion;
    /**
     * The name field.
     */
    private TextField<String> nameField;

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
    private WebMarkupContainer maxDelayRow;
    /**
     * The row of fields about significance to source
     */
    private WebMarkupContainer significanceToSourceRow;
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

    protected ExpandedFlowPanel(
            String id,
            IModel<Flow> model,
            boolean send,
            Set<Long> expansions,
            int index ) {
        super( id, model, send, false, expansions, index );
        init();
    }

    private void init() {
        setOutputMarkupId( true );
        addHeader();
        addNameField();
        addLabeled( "name-label", nameField );
        addEOIs();
        addAskedForRadios();
        addSignificanceToTarget();
        addOtherField();
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
        add( new AttachmentPanel( "attachments", new PropertyModel<Flow>( this, "flow" ) ) );
        issuesPanel = new IssuesPanel(
                "issues",
                new PropertyModel<ModelObject>( this, "flow" ),
                getExpansions() );
        issuesPanel.setOutputMarkupId( true );
        add( issuesPanel );
        adjustFields( getFlow() );
    }

    private void addEOIs() {
        AjaxFallbackLink editEOIsLink = new AjaxFallbackLink( "editEOIs" ) {
            public void onClick( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.AspectViewed, getFlow(), "eois" ) );
            }
        };
        add( editEOIsLink );
        ListView<String> eoisList = new ListView<String>(
                "eois",
                new PropertyModel<List<String>>( this, "eoiSummaries" ) ) {
            protected void populateItem( ListItem<String> item ) {
                item.add( new Label( "summary", item.getModelObject() ) );
            }
        };
/*
        TextArea<String> eoisDescriptionField = new TextArea<String>(
                "eois",
                new PropertyModel<String>( getFlow(), "eoisSummary" ) );
        eoisDescriptionField.setEnabled( false );
*/
        add( eoisList );
    }

    /**
     * Get list of eois as strings.
     *
     * @return a list of strings
     */
    public List<String> getEoiSummaries() {
        List<String> eoiStrings = new ArrayList<String>();
        for ( ElementOfInformation eoi : getFlow().getEois() ) {
            eoiStrings.add( eoi.toString() );
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
        // TODO exception wnem f just got disconnected on undo
        boolean lockedByUser = isLockedByUser( f );

        nameField.setEnabled( lockedByUser && f.canSetNameAndElements() );
        askedForButtons.setEnabled( lockedByUser && f.canSetAskedFor() );
        allField.setVisible( isSend() && f.canGetAll() );
        allField.setEnabled( lockedByUser && isSend() && f.canSetAll() );
        significanceToTargetLabel.setVisible( f.canGetSignificanceToTarget() );
        significanceToTargetChoice.setEnabled(
                lockedByUser && f.canSetSignificanceToTarget() );
        channelRow.setVisible( f.canGetChannels() );
        maxDelayRow.setVisible( f.canGetMaxDelay() );
        delayPanel.enable( lockedByUser && f.canSetMaxDelay() );
        significanceToSourceRow.setVisible( f.canGetSignificanceToSource() );
        triggersSourceContainer.setVisible(
                ( !isSend() || f.isAskedFor() ) && f.canGetTriggersSource() );
        triggersSourceCheckBox.setEnabled( lockedByUser && f.canSetTriggersSource() );
        terminatesSourceContainer.setVisible( f.canGetTerminatesSource() );
        terminatesSourceCheckBox.setEnabled( lockedByUser && f.canSetTerminatesSource() );
        otherChoice.setEnabled( lockedByUser );
        flowDescription.setEnabled( ( isSend() && f.isNotification() || !isSend() && f.isAskedFor() )
                && isLockedByUser( getFlow() ) );
        makeVisible( issuesPanel, getAnalyst().hasIssues( getFlow(), false ) );
    }

    private void addNameField() {
        nameField = new AutoCompleteTextField<String>(
                "name",
                new PropertyModel<String>( this, "name" ) ) {
            protected Iterator<String> getChoices( String s ) {
                return getFlowNameChoices( s );
            }
        };
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {

            protected void onUpdate( AjaxRequestTarget target ) {
                addIssuesAnnotation( nameField, getFlow(), "name" );
                target.addComponent( nameField );
                addOtherField();
                target.addComponent( otherLink );
                target.addComponent( otherChoice );
                update( target, new Change( Change.Type.Updated, getFlow(), "name" ) );
            }
        } );
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
                makeVisible( channelRow, isChannelRelevant( getFlow() ) );
                update( target, new Change( Change.Type.Updated, getFlow(), "askedFor" ) );
            }
        } );

        add( askedForButtons );
    }

    private void addSignificanceToTarget() {
        significanceToTargetLabel = new WebMarkupContainer( "target-significance-label" );
        add( significanceToTargetLabel );
        significanceToTargetLabel.add(
                new Label( "target-label", isSend() ? "the recipient's task" : "this task" ) );
        significanceToTargetChoice = new DropDownChoice<Flow.Significance>(
                "significance-to-target",
                new PropertyModel<Flow.Significance>( this, "significanceToTarget" ),
                new PropertyModel<List<? extends Flow.Significance>>(
                        this,
                        "significanceToTargetChoices" ),
                new IChoiceRenderer<Flow.Significance>() {

                    public Object getDisplayValue( Flow.Significance significance ) {
                        return significance.getLabel();
                    }

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
        significanceToSourceRow = new WebMarkupContainer( "significance-to-source" );
        add( significanceToSourceRow );
        Component sourceTaskReference;
        if ( isSend() ) {
            sourceTaskReference = new Label( "source-task", "This task" );
        } else {
            sourceTaskReference = new ModelObjectLink(
                    "source-task",
                    new Model<ModelObject>( getFlow().getSource() ),
                    new Model<String>( "Sender's task" ) );
        }
        significanceToSourceRow.add( sourceTaskReference );
        triggersSourceContainer = new WebMarkupContainer( "triggers-source-container" );
        significanceToSourceRow.add( triggersSourceContainer );
        triggersSourceCheckBox = new CheckBox(
                "triggers-source",
                new PropertyModel<Boolean>( this, "triggeringToSource" ) );
        triggersSourceCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onclick" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update(
                        target,
                        new Change( Change.Type.Updated, getFlow(), "significanceToSource" ) );
            }
        } );
        triggersSourceContainer.add( triggersSourceCheckBox );
        terminatesSourceContainer = new WebMarkupContainer( "terminates-source-container" );
        significanceToSourceRow.add( terminatesSourceContainer );
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

    private void addFlowDescription() {
        flowDescription = new TextArea<String>( "description",
                new PropertyModel<String>( this, "description" ) );
        flowDescription.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getFlow(), "description" ) );
            }
        } );
        flowDescription.setOutputMarkupId( true );
        add( flowDescription );
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
        FlowTitlePanel titlePanel = new FlowTitlePanel( "title", getFlow() ,isSend() );
        // Add style classes
        titlePanel.add( new AttributeModifier( "class", true, new Model<String>( getCssClasses(  ) ) ) );
        titlePanel.add( new AjaxEventBehavior( "onclick" ) {
            protected void onEvent( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Collapsed, getFlow() ) );
            }
        } );
        titlePanel.setOutputMarkupId( true );
        add( titlePanel );
        addFlowActionMenu();
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

    /**
     * Add a component with an attached label.
     *
     * @param id        the id. Label is "id-label".
     * @param component the component
     * @return the label component
     */
    private FormComponentLabel addLabeled( String id, FormComponent<?> component ) {
        component.setOutputMarkupId( true );
        FormComponentLabel result = new FormComponentLabel( id, component );
        add( result );
        add( component );
        addIssuesAnnotation( component, getFlow(), component.getId() );
        return result;
    }

    /**
     * Add issues annotations to a component.
     *
     * @param component the component
     * @param object    the object of the issues
     * @param property  the property of concern. If null, get issues of object
     */
    private void addIssuesAnnotation(
            FormComponent<?> component,
            ModelObject object,
            String property ) {
        Analyst analyst = ( (Channels) getApplication() ).getAnalyst();
        String summary = property == null ?
                analyst.getIssuesSummary( object, false ) :
                analyst.getIssuesSummary( object, property );
        boolean hasIssues = analyst.hasIssues( object, Analyst.INCLUDE_PROPERTY_SPECIFIC );
        if ( !summary.isEmpty() ) {
            component.add(
                    new AttributeModifier(
                            "class", true, new Model<String>( "error" ) ) );              // NON-NLS
            component.add(
                    new AttributeModifier(
                            "title", true, new Model<String>( summary ) ) );                // NON-NLS
        } else {
            if ( hasIssues ) {
                // All waived issues
                component.add(
                        new AttributeModifier( "class", true, new Model<String>( "waived" ) ) );
                component.add(
                        new AttributeModifier( "title", true, new Model<String>( "All issues waived" ) ) );
            }
        }
    }

    private void addOtherField() {
        otherLink = new ModelObjectLink( "other-link",
                new PropertyModel<Part>( this, "otherPart" ),
                new Model<String>( isSend() ? "To" : "From" ) );
        otherLink.setOutputMarkupId( true );
        addOrReplace( otherLink );

        otherChoice = new OtherNodeSelectorPanel(
                "other",
                new PropertyModel<Node>( this, "node" ),
                new PropertyModel<Node>( this, "other" ),
                new PropertyModel<String>( this, "name" ),
                new PropertyModel<List<Node>>( this, "firstChoices" ),
                new PropertyModel<List<Node>>( this, "secondChoices" ) );
        otherChoice.add( new AttributeModifier(
                "title",
                true,
                new Model<String>( getOtherPart().displayString() ) ) );
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

    @SuppressWarnings( "unchecked" )
    private boolean isRedundant( Part part ) {
        String info = getFlow().getName();
        // redundant if part has a matching need or capability
        List<Flow> needsOrCapabilities = isSend()
                ? IteratorUtils.toList( part.receivesNamed( info ) )
                : IteratorUtils.toList( part.sendsNamed( info ) );
        return CollectionUtils.exists(
                needsOrCapabilities,
                new Predicate() {
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
    @SuppressWarnings( "unchecked" )
    public List<Node> getSecondChoices() {
        final List<Part> relatedParts = findRelatedParts();
        final Node node = getNode();
        final Node other = getOther();
        return (List<Node>) CollectionUtils.select(
                IteratorUtils.toList( node.getSegment().parts() ),
                new Predicate() {
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
        maxDelayRow = new WebMarkupContainer( "max-delay-row" );
        add( maxDelayRow );
        delayPanel = new DelayPanel(
                "max-delay",
                new PropertyModel<ModelObject>( this, "flow" ),
                "maxDelay" );
        maxDelayRow.add( delayPanel );
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
        String name = getFlow().getName();
        Iterator<Flow> flows = isSend() ? n.receives() : n.sends();
        boolean hasSameName = false;
        while ( !hasSameName && flows.hasNext() ) {
            Flow otherFlow = flows.next();
            hasSameName = !otherFlow.hasConnector() && Matcher.same( otherFlow.getName(), name );
        }
        return hasSameName;
    }
*/

    private boolean isEmptyOrEquivalent( SegmentObject connectorFlow ) {
        return getFlow().getName().isEmpty()
                || Matcher.getInstance().same( getFlow().getName(), connectorFlow.getName() );
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
            change = doCommand( new SatisfyNeed(
                    need,
                    capability,
                    !SatisfyNeed.KEEP_CAPABILITY,
                    SatisfyNeed.KEEP_NEED ) );
        } else {
            change = doCommand( new RedirectFlow( getFlow(), other, isSend() ) );
        }
        Flow newFlow = (Flow) change.getSubject( getQueryService() );
        // requestLockOn( newFlow );
        setFlow( newFlow );
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

    /**
     * {@inheritDoc}
     */
    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    /**
     * {@inheritDoc}
     */
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
        doCommand( new UpdateSegmentObject( getFlow(), "name", name ) );
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
        doCommand( new UpdateSegmentObject( getFlow(), "description", val ) );
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
        doCommand( new UpdateSegmentObject( getFlow(), "askedFor", val ) );
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
        doCommand( new UpdateSegmentObject( getFlow(), "all", value ) );
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
        doCommand( new UpdateSegmentObject( getFlow(), "significanceToTarget", val ) );
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
        doCommand( new UpdateSegmentObject( getFlow(), "significanceToSource", val ) );
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

    public void changed( Change change ) {
        // ignore selection of other node - don't propagate selection
        if ( !( change.isSelected()
                && change.isForInstanceOf( Node.class )
                && change.isForProperty( "other" ) ) ) {
            super.changed( change );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updated ) {
        if ( change.isSelected()
                && change.isForInstanceOf( Node.class )
                && change.isForProperty( "other" ) ) {
            Flow oldFlow = getFlow();
            setOther( (Node) change.getSubject( getQueryService() ) );
            adjustFields( getFlow() );
            update( target, new Change( Change.Type.Updated, getNode() ) );
            if ( !getFlow().equals( oldFlow ) ) {
                update( target, new Change( Change.Type.Collapsed, oldFlow ) );
                update( target, new Change( Change.Type.Expanded, getFlow() ) );
            }
        } else {
            if ( change.isUpdated() ) {
                adjustFields( getFlow() );
                target.addComponent( this );
            }
            super.updateWith( target, change, updated );
        }
    }

}
