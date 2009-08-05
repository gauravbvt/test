package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Analyst;
import com.mindalliance.channels.Channels;
import com.mindalliance.channels.QueryService;
import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.command.commands.RedirectFlow;
import com.mindalliance.channels.command.commands.SatisfyNeed;
import com.mindalliance.channels.command.commands.UpdateScenarioObject;
import com.mindalliance.channels.model.Connector;
import com.mindalliance.channels.model.ExternalFlow;
import com.mindalliance.channels.model.Flow;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Scenario;
import com.mindalliance.channels.model.ScenarioObject;
import com.mindalliance.channels.util.Matcher;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
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
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
     * The description field.
     */
    private TextArea<String> descriptionField;

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
    private DropDownChoice<Node> otherChoice;
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
     * Issues panel.
     */
    private IssuesPanel issuesPanel;

    protected ExpandedFlowPanel(
            String id,
            IModel<Flow> model,
            boolean outcome,
            Set<Long> expansions ) {
        super( id, model, outcome, false, expansions );
        init();
    }

    private void init() {
        setOutputMarkupId( true );
        addHeader();
        addNameField();
        addLabeled( "name-label", nameField );                                            // NON-NLS
        descriptionField = new TextArea<String>(
                "description",
                new PropertyModel<String>( this, "description" ) );
        descriptionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.Updated, getFlow(), "description" ) );
            }
        } );
        addLabeled( "description-label", descriptionField );                              // NON-NLS
        addAskedForRadios();
        addSignificanceToTarget();
        addOtherField();
        addAllField();

        Node node = getOther();
        if ( node.isConnector() && node.getScenario().equals( getNode().getScenario() ) ) {
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
        add( new AttachmentPanel( "attachments", new PropertyModel<Flow>( this, "flow" ) ) );
        issuesPanel = new IssuesPanel(
                "issues",
                new PropertyModel<ModelObject>( this, "flow" ),
                getExpansions() );
        issuesPanel.setOutputMarkupId( true );
        add( issuesPanel );
        adjustFields( getFlow() );
    }

    /**
     * Show/hide/enable/disable parts of the panel given the state of the flow.
     *
     * @param f the flow
     */
    private void adjustFields( Flow f ) {
        // TODO exception wnem f just got disconnected on undo
        boolean lockedByUser = isLockedByUser( f );

        nameField.setEnabled( lockedByUser && f.canSetNameAndDescription() );
        descriptionField.setEnabled( lockedByUser && f.canSetNameAndDescription() );
        askedForButtons.setEnabled( lockedByUser && f.canSetAskedFor() );
        allField.setVisible( isOutcome() && f.canGetAll() );
        allField.setEnabled( lockedByUser && isOutcome() && f.canSetAll() );
        significanceToTargetLabel.setVisible( f.canGetSignificanceToTarget() );
        significanceToTargetChoice.setEnabled(
                lockedByUser && f.canSetSignificanceToTarget() );
        channelRow.setVisible( f.canGetChannels() );
        maxDelayRow.setVisible( f.canGetMaxDelay() );
        delayPanel.enable( lockedByUser && f.canSetMaxDelay() );
        significanceToSourceRow.setVisible( f.canGetSignificanceToSource() );
        triggersSourceContainer.setVisible(
                ( !isOutcome() || f.isAskedFor() ) && f.canGetTriggersSource() );
        triggersSourceCheckBox.setEnabled( lockedByUser && f.canSetTriggersSource() );
        terminatesSourceContainer.setVisible( f.canGetTerminatesSource() );
        terminatesSourceCheckBox.setEnabled( lockedByUser && f.canSetTerminatesSource() );
        otherChoice.setEnabled( lockedByUser );
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
            Iterator<Flow> nodeFlows = node.outcomes();
            while ( nodeFlows.hasNext() ) {
                String name = nodeFlows.next().getName();
                if ( queryService.likelyRelated( s, name ) )
                    choices.add( name );
            }
            nodeFlows = node.requirements();
            while ( nodeFlows.hasNext() ) {
                String name = nodeFlows.next().getName();
                if ( getQueryService().likelyRelated( s, name ) )
                    choices.add( name );
            }
            // all name-matching in-scenario flows of the right polarity
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
                new Label( "target-label", isOutcome() ? "the recipient's task" : "this task" ) );
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
        significanceToSourceRow.add(
                new Label( "source-task", isOutcome() ? "This task" : "Sender's task" ) );
        triggersSourceContainer = new WebMarkupContainer( "triggers-source-container" );
        significanceToSourceRow.add( triggersSourceContainer );
        triggersSourceCheckBox = new CheckBox(
                "triggers-source",
                new PropertyModel<Boolean>( this, "triggeringToSource" ) );
        triggersSourceCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
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
        terminatesSourceCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
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
        Label titleLabel = new Label( "title",
                new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        return isOutcome() ? getFlow().getOutcomeTitle()
                                : getFlow().getRequirementTitle();
                    }
                } );
        titleLabel.setOutputMarkupId( true );
        add( titleLabel );
        addFlowActionMenu( );
    }

    private void addAllField() {
        CheckBox checkBox = new CheckBox(
                "all",
                new PropertyModel<Boolean>( this, "all" ) );
        checkBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {

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
    protected final FormComponentLabel addLabeled( String id, FormComponent<?> component ) {
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
    protected void addIssuesAnnotation(
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

    /**
     * Add the target/source dropdown. Fill with getOtherNodes(); select with getOther().
     */
    protected final void addOtherField() {
        otherChoice = new DropDownChoice<Node>(
                "other",                                                                  // NON-NLS
                new PropertyModel<Node>( this, "other" ),                                 // NON-NLS
                new PropertyModel<List<? extends Node>>( this, "otherNodes" ),            // NON-NLS
                new IChoiceRenderer<Node>() {
                    public Object getDisplayValue( Node object ) {
                        Node node = getOther();
                        boolean tbd =
                                object.equals( node ) && node.isConnector() && node.getScenario().equals(
                                        getNode().getScenario() );
                        return tbd
                                ? "* to be determined *"
                                : object.isConnector() && object.getScenario().equals( getFlow().getScenario() )
                                    ? ( (Connector) object ).getInnerFlow().getLocalPart().toString()
                                    : object.toString();
                    }

                    public String getIdValue( Node object, int index ) {
                        return Long.toString( object.getId() );
                    }
                } );

        otherChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {

            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields( getFlow() );
                update( target, new Change( Change.Type.Updated, getFlow(), "other" ) );
            }
        } );

        // TODO fix flow expansion of target when other has changed (fixed?)
        ScenarioLink details = new ScenarioLink( "other-details",
                new PropertyModel<Node>( this, "other" ),
                getFlow() );
        details.add(
                new Label( "type",
                        new Model<String>( isOutcome() ? "To" : "From" ) ) );
        FormComponentLabel otherLabel =
                new FormComponentLabel( "other-label", otherChoice );
        otherLabel.add( details );
        add( otherLabel );
        add( otherChoice );
    }



    /**
     * @return the node on this side of the flow
     */
    public final Node getNode() {
        return isOutcome() ? getFlow().getSource() : getFlow().getTarget();
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
        Scenario scenario = node.getScenario();
        Set<Node> result = new TreeSet<Node>();

        // Add other parts of this scenario
        Iterator<Node> nodes = scenario.nodes();
        while ( nodes.hasNext() ) {
            Node n = nodes.next();
            if ( !node.equals( n ) ) {
                if ( n.equals( other ) ) {
                    result.add( n );
                } else if ( n.isConnector() ) {
                    Connector connector = (Connector) n;
                    Flow connectorFlow = connector.getInnerFlow();
                    if ( isEmptyOrEquivalent( connectorFlow ) ) {
                        if ( isOutcome() ) {
                            if ( connector.isSource() && !connectorFlow.getTarget().equals( node ) )
                                result.add( connector );
                        } else {
                            if ( connector.isTarget() && !connectorFlow.getSource().equals( node ) )
                                result.add( connector );
                        }
                    }
                } else {
                    // a part in scenario with same flow to/from part
                    if ( hasPartFlowWithSameName( n ) ) result.add( n );
                }
            }
        }
        // Add inputs/outputs of other scenarios
        QueryService queryService = getQueryService();
        for ( Scenario s : queryService.list( Scenario.class ) ) {
            if ( !scenario.equals( s ) ) {
                Iterator<Connector> c = isOutcome() ? s.inputs() : s.outputs();
                while ( c.hasNext() ) {
                    Connector connector = c.next();
                    Flow connectorFlow = connector.getInnerFlow();
                    if ( isEmptyOrEquivalent( connectorFlow ) ) {
                        if ( other.equals( connector ) || !node.isConnectedTo(
                                isOutcome(), connector, getFlow().getName() ) )
                            result.add( connector );
                    }
                }
            }
        }
        return new ArrayList<Node>( result );
    }

    private boolean hasPartFlowWithSameName( Node n ) {
        String name = getFlow().getName();
        Iterator<Flow> flows = isOutcome() ? n.requirements() : n.outcomes();
        boolean hasSameName = false;
        while ( !hasSameName && flows.hasNext() ) {
            Flow otherFlow = flows.next();
            hasSameName = !otherFlow.hasConnector() && Matcher.same( otherFlow.getName(), name );
        }
        return hasSameName;
    }

    private boolean isEmptyOrEquivalent( ScenarioObject connectorFlow ) {
        return getFlow().getName().isEmpty()
                || Matcher.matches( getFlow().getName(), connectorFlow.getName() );
    }

    private List<Flow> findRelevantInternalFlows() {
        List<Flow> result = new ArrayList<Flow>();
        Iterator<Part> parts = getFlow().getScenario().parts();
        while ( parts.hasNext() ) {
            Iterator<Flow> flows = isOutcome()
                    ? parts.next().requirements()
                    : parts.next().outcomes();
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
        for ( Scenario s : queryService.list( Scenario.class ) ) {
            Iterator<Connector> connectorIterator = isOutcome()
                    ? s.inputs()
                    : s.outputs();
            while ( connectorIterator.hasNext() )
                result.add( connectorIterator.next() );
        }
        return result;
    }

    /**
     * Get the node at the other side of this flow: the source if requirement, the target if
     * outcome.
     *
     * @return the other side of this flow.
     */
    public final Node getOther() {
        Flow f = getFlow();
        return f.isInternal() ?
                isOutcome() ? f.getTarget() : f.getSource() :
                ( (ExternalFlow) f ).getConnector();
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
            Flow need = isOutcome() ? connector.getInnerFlow() : getFlow();
            Flow capability = isOutcome() ? getFlow() : connector.getInnerFlow();
            change = doCommand( new SatisfyNeed( need, capability, getFlow().getScenario() ) );
        } else {
            change = doCommand( new RedirectFlow( getFlow(), other, isOutcome() ) );
        }
        Flow newFlow = (Flow) change.getSubject();
        // requestLockOn( newFlow );
        setFlow( newFlow );
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
        doCommand( new UpdateScenarioObject( getFlow(), "name", name ) );
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
     * Set flow name via command.
     *
     * @param desc a string
     */
    public void setDescription( String desc ) {
        doCommand( new UpdateScenarioObject( getFlow(), "description", desc ) );
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
        doCommand( new UpdateScenarioObject( getFlow(), "askedFor", val ) );
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
        doCommand( new UpdateScenarioObject( getFlow(), "all", value ) );
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
        doCommand( new UpdateScenarioObject( getFlow(), "significanceToTarget", val ) );
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
        doCommand( new UpdateScenarioObject( getFlow(), "significanceToSource", val ) );
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
     * {@inheritDoc}
     */
    public void updateWith( AjaxRequestTarget target, Change change ) {
        if ( change.isUpdated() ) {
            adjustFields( getFlow() );
            target.addComponent( this );
        }
        super.updateWith( target, change );
    }

}
