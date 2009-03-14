package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.Connector;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.command.commands.RedirectFlow;
import com.mindalliance.channels.command.commands.UpdateScenarioObject;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.components.menus.FlowActionsMenuPanel;
import com.mindalliance.channels.util.SemMatch;
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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Details of an expanded flow.
 */
public abstract class ExpandedFlowPanel extends AbstractCommandablePanel {

    /**
     * The flow edited by this panel.
     */
    private IModel<Flow> model;

    /**
     * True if outcome, otherwise a requirement.
     */
    private boolean outcome;

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
    DropDownChoice<Node> otherChoice;
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
    IssuesPanel issuesPanel;
    /**
     * Expansions.
     */
    private Set<Long> expansions;

    protected ExpandedFlowPanel( String id, IModel<Flow> model, boolean outcome, Set<Long> expansions ) {
        super( id, model );
        this.model = model;
        this.expansions = expansions;
        setOutcome( outcome );
        init();
    }

    private void init() {
        setOutputMarkupId( true );
        addHeader();
        addNameField();
        addLabeled( "name-label", nameField );                                            // NON-NLS
        descriptionField = new TextArea<String>(
                "description",
                new PropertyModel<String>( this, "description" ) );                         // NON-NLS
        descriptionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                // target.addComponent( ExpandedFlowPanel.this );
                updateWith( target, getFlow() );
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
        addChannelRow();
        addMaxDelayRow();
        addSignificanceToSource();
        add( new AttachmentPanel( "attachments", new PropertyModel<Flow>( this, "flow" ) ) );
        issuesPanel = new IssuesPanel(
                "issues",
                new PropertyModel<ModelObject>( this, "flow" ),
                expansions );
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
        nameField.setEnabled( isLockedByUser( f ) && f.canSetNameAndDescription() );
        descriptionField.setEnabled( isLockedByUser( f ) &&  f.canSetNameAndDescription() );
        askedForButtons.setEnabled( isLockedByUser( f ) &&  f.canSetAskedFor() );
        allField.setVisible( outcome && f.canGetAll() );
        allField.setEnabled( isLockedByUser( f ) &&  outcome && f.canSetAll() );
        significanceToTargetLabel.setVisible( f.canGetSignificanceToTarget() );
        significanceToTargetChoice.setEnabled( isLockedByUser( f ) &&  f.canSetSignificanceToTarget() );
        channelRow.setVisible( f.canGetChannels() );
        maxDelayRow.setVisible( f.canGetMaxDelay() );
        delayPanel.enable( isLockedByUser( f ) &&  f.canSetMaxDelay() );
        significanceToSourceRow.setVisible( f.canGetSignificanceToSource() );
        triggersSourceContainer.setVisible( ( !outcome || f.isAskedFor() ) && f.canGetTriggersSource() );
        triggersSourceCheckBox.setEnabled( isLockedByUser( f ) &&  f.canSetTriggersSource() );
        terminatesSourceContainer.setVisible( f.canGetTerminatesSource() );
        terminatesSourceCheckBox.setEnabled( isLockedByUser( f ) &&  f.canSetTerminatesSource() );
        otherChoice.setEnabled(isLockedByUser( f ));
        issuesPanel.setVisible( Project.analyst().hasIssues( model.getObject(), false ) );
    }

    public void updateWith( AjaxRequestTarget target, Object context ) {
        adjustFields( getFlow() );
        target.addComponent( this );
        super.updateWith( target, context );
    }

    private void addNameField() {
        nameField = new AutoCompleteTextField<String>(
                "name",
                new PropertyModel<String>( this, "name" ) ) {
            protected Iterator<String> getChoices( String s ) {
                return getFlowNameChoices( s );
            }
        };
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {                // NON-NLS

            protected void onUpdate( AjaxRequestTarget target ) {
                addIssuesAnnotation( nameField, getFlow(), "name" );
                target.addComponent( nameField );
                updateWith( target, getFlow() );
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
        Set<String> choices = new HashSet<String>();
        if ( s.length() > 1 ) {
            Node node = getNode();
            Iterator<Flow> nodeFlows = node.outcomes();
            while ( nodeFlows.hasNext() ) {
                String name = nodeFlows.next().getName();
                if ( SemMatch.matches( s, name ) )
                    choices.add( name );
            }
            nodeFlows = node.requirements();
            while ( nodeFlows.hasNext() ) {
                String name = nodeFlows.next().getName();
                if ( SemMatch.matches( s, name ) )
                    choices.add( name );
            }
            // all connectors of the right "polarity"
            for ( Connector connector : findAllRelevantConnectors() ) {
                String name = connector.getInnerFlow().getName();
                if ( SemMatch.matches( s, name ) )
                    choices.add( name );
            }
        }
        return choices.iterator();
    }

    private void addAskedForRadios() {
        askedForButtons = new RadioGroup<Boolean>(
                "askedFor",
                new PropertyModel<Boolean>( this, "askedFor" ) );                          // NON-NLS
        askedForButtons.add( new Radio<Boolean>( "askedForTrue",                          // NON-NLS
                new Model<Boolean>( true ) ) );
        askedForButtons.add( new Radio<Boolean>( "askedForFalse",                         // NON-NLS
                new Model<Boolean>( false ) ) );
        askedForButtons.add( new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                channelRow.setVisible( isChannelRelevant( getFlow() ) );
                updateWith( target, getFlow() );
            }
        } );

        add( askedForButtons );
    }

    private void addSignificanceToTarget() {
        significanceToTargetLabel = new WebMarkupContainer( "target-significance-label" );
        add( significanceToTargetLabel );
        significanceToTargetLabel.add( new Label( "target-label", outcome ? "the recipient's task" : "this task" ) );
        significanceToTargetChoice = new DropDownChoice<Flow.Significance>(
                "significance-to-target",
                new PropertyModel<Flow.Significance>( this, "significanceToTarget" ),
                new PropertyModel<List<? extends Flow.Significance>>( this, "significanceToTargetChoices" ),
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
            protected void onUpdate( AjaxRequestTarget target ) {
                updateWith( target, getFlow() );
            }
        } );
        significanceToTargetLabel.add( significanceToTargetChoice );
    }

    private void addSignificanceToSource() {
        significanceToSourceRow = new WebMarkupContainer( "significance-to-source" );
        add( significanceToSourceRow );
        significanceToSourceRow.add( new Label( "source-task", outcome ? "This task" : "Sender's task" ) );
        triggersSourceContainer = new WebMarkupContainer( "triggers-source-container" );
        significanceToSourceRow.add( triggersSourceContainer );
        triggersSourceCheckBox = new CheckBox(
                "triggers-source",
                new PropertyModel<Boolean>( this, "triggeringToSource" ) );
        triggersSourceCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                updateWith( target, getFlow() );
            }
        } );
        triggersSourceContainer.add( triggersSourceCheckBox );
        terminatesSourceContainer = new WebMarkupContainer( "terminates-source-container" );
        significanceToSourceRow.add( terminatesSourceContainer );
        terminatesSourceCheckBox = new CheckBox(
                "terminates-source",
                new PropertyModel<Boolean>( this, "terminatingToSource" ) );
        terminatesSourceCheckBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                updateWith( target, getFlow() );
            }
        } );
        terminatesSourceContainer.add( terminatesSourceCheckBox );
        terminatesSourceContainer.add(
                new Label( "notifying-or-replying", new PropertyModel<String>( this, "replyingOrNotifying" ) ) );
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
        Label titleLabel = new Label( "title",                                                          // NON-NLS
                new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        return outcome ? getFlow().getOutcomeTitle()
                                : getFlow().getRequirementTitle();
                    }
                } );
        titleLabel.setOutputMarkupId( true );
        add( titleLabel );
        addFlowActionMenu( outcome );
    }

    private void addFlowActionMenu( boolean isOutcome ) {
        FlowActionsMenuPanel flowActionsMenu = new FlowActionsMenuPanel(
                "flowActionsMenu",
                new PropertyModel<Flow>( this, "flow" ),
                isOutcome,
                false );
        flowActionsMenu.setOutputMarkupId( true );
        add( flowActionsMenu );
    }


    private void addAllField() {
        CheckBox checkBox = new CheckBox(
                "all",
                new PropertyModel<Boolean>( this, "all" ) );                                        // NON-NLS
        checkBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {                // NON-NLS

            protected void onUpdate( AjaxRequestTarget target ) {
                updateWith( target, getFlow() );
            }
        } );
        allField = new FormComponentLabel( "all-label", checkBox );                       // NON-NLS
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
    protected void addIssuesAnnotation( FormComponent<?> component, ModelObject object, String property ) {
        Analyst analyst = ( (Project) getApplication() ).getAnalyst();
        String issue = property == null ?
                analyst.getIssuesSummary( object, false ) :
                analyst.getIssuesSummary( object, property );
        if ( !issue.isEmpty() ) {
            component.add(
                    new AttributeModifier(
                            "class", true, new Model<String>( "error" ) ) );              // NON-NLS
            component.add(
                    new AttributeModifier(
                            "title", true, new Model<String>( issue ) ) );                // NON-NLS
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
                        Node o = getOther();
                        boolean tbd =
                                object.equals( o ) && o.isConnector() && o.getScenario().equals(
                                        getNode().getScenario() );
                        return tbd ? "* to be determined *" : object.toString();
                    }

                    public String getIdValue( Node object, int index ) {
                        return Long.toString( object.getId() );
                    }
                } );

        otherChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {                  // NON-NLS
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields( getFlow() );
                updateWith( target, getFlow() );
                // PageParameters parameters = getWebPage().getPageParameters();
                // TODO - Denis : fix bug and remove patch
                /*PageParameters parameters = ( (ScenarioPage) getWebPage() )
                        .getParametersCollapsing( getFlow().getScenario().getId() );
                setResponsePage( getWebPage().getClass(), parameters );*/
            }
        } );

        // TODO fix flow expansion of target when other has changed
        ScenarioLink details = new ScenarioLink( "other-details",                         // NON-NLS
                new PropertyModel<Node>( this, "other" ),                                 // NON-NLS
                getFlow() );
        details.add(
                new Label( "type",                                                        // NON-NLS
                        new Model<String>( isOutcome() ? "To" : "From" ) ) );
        FormComponentLabel otherLabel =
                new FormComponentLabel( "other-label", otherChoice );                           // NON-NLS
        otherLabel.add( details );
        add( otherLabel );
        add( otherChoice );
    }

    public final Flow getFlow() {
        return model.getObject();
    }

    public final void setFlow( Flow flow ) {
        model.setObject( flow );
    }

    public final boolean isOutcome() {
        return outcome;
    }

    public final void setOutcome( boolean outcome ) {
        this.outcome = outcome;
    }

    /**
     * @return the node on this side of the flow
     */
    public final Node getNode() {
        return isOutcome() ? getFlow().getSource() : getFlow().getTarget();
    }

    private void addChannelRow() {
        channelRow = new WebMarkupContainer( "channel-row" );                             // NON-NLS
        channelRow.setOutputMarkupPlaceholderTag( true );
        channelRow.add( new Label( "channel-title", new AbstractReadOnlyModel<String>() { // NON-NLS

            @Override
            public String getObject() {
                return getFlow().isAskedFor() ? "Sender's channels:" : "Receiver's channels:";
            }
        } ) );

        ChannelListPanel channelListPanel = new ChannelListPanel(
                "channels",
                new PropertyModel<Channelable>( this, "flow" ) );
        channelRow.add( channelListPanel );
        add( channelRow );
    }

    private void addMaxDelayRow() {
        maxDelayRow = new WebMarkupContainer( "max-delay-row" );
        add( maxDelayRow );
        delayPanel = new DelayPanel(
                "max-delay",
                new PropertyModel<ModelObject>( this, "flow") ,
                "maxDelay" ) ;
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
                    if ( getFlow().getName().isEmpty()
                            || SemMatch.matches( getFlow().getName(), connectorFlow.getName() ) ) {
                        if ( isOutcome() ) {
                            if ( connector.isSource() && !connectorFlow.getTarget().equals( node ) )
                                result.add( connector );
                        } else {
                            if ( connector.isTarget() && !connectorFlow.getSource().equals( node ) )
                                result.add( connector );
                        }
                    }
                }
            }
        }
        // Add inputs/outputs of other scenarios
        Service service = ( (Project) getApplication() ).getService();
        for ( Scenario s : service.list( Scenario.class ) ) {
            if ( !scenario.equals( s ) ) {
                Iterator<Connector> c = isOutcome() ? s.inputs() : s.outputs();
                while ( c.hasNext() ) {
                    Connector connector = c.next();
                    Flow connectorFlow = connector.getInnerFlow();
                    if ( getFlow().getName().isEmpty()
                            || SemMatch.matches( getFlow().getName(), connectorFlow.getName() ) ) {
                        if ( other.equals( connector ) || !node.isConnectedTo(
                                outcome, connector, getFlow().getName() ) )
                            result.add( connector );
                    }
                }
            }
        }
        return new ArrayList<Node>( result );
    }

    private List<Connector> findAllRelevantConnectors() {
        List<Connector> result = new ArrayList<Connector>();
        Service service = ( (Project) getApplication() ).getService();
        for ( Scenario s : service.list( Scenario.class ) ) {
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
     * Set the node at the other side of this flow
     *
     * @param other the new source or target
     */
    public void setOther( Node other ) {
        if ( other.isConnector() ) {
            Flow newFlow = (Flow) doCommand( new RedirectFlow( getFlow(), (Connector) other, isOutcome() ) );
            requestLockOn( newFlow );
            setFlow( newFlow );
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

    public void setAskedFor( boolean val ) {
        doCommand( new UpdateScenarioObject( getFlow(), "askedFor", val ) );
    }

    public boolean isAll() {
        return getFlow().isAll();
    }

    public void setAll( boolean value ) {
        doCommand( new UpdateScenarioObject( getFlow(), "all", value ) );
    }

    public Flow.Significance getSignificanceToTarget() {
        return getFlow().getSignificanceToTarget();
    }

    public void setSignificanceToTarget( Flow.Significance val ) {
        doCommand( new UpdateScenarioObject( getFlow(), "significanceToTarget", val ) );
    }

    public Flow.Significance getSignificanceToSource() {
        return getFlow().getSignificanceToSource();
    }

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


}
