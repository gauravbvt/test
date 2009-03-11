package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Delay;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.util.SemMatch;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.ScenarioPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
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
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;

/**
 * Details of an expanded flow.
 */
public abstract class ExpandedFlowPanel extends AbstractUpdatablePanel implements DeletableFlow {

    /**
     * The flow edited by this panel.
     */
    private Flow flow;

    /**
     * True if outcome, otherwise a requirement.
     */
    private boolean outcome;

    /**
     * True if this flow is marked for deletion.
     */
    private boolean markedForDeletion;
    /**
     * Flow's title label
     */
    private Label titleLabel;
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
     * Drop down of choice for the other end of the flow
     */
    private DropDownChoice<Node> otherChoice;
    /**
     * A panel with issues on the flow.
     */
    private IssuesPanel flowIssuesPanel;

    protected ExpandedFlowPanel( String id, Flow flow, boolean outcome ) {
        super( id );
        setDefaultModel( new CompoundPropertyModel<Flow>(
                new PropertyModel<Flow>( this, "flow" ) ) );                              // NON-NLS

        setOutputMarkupId( true );
        setFlow( flow );
        setOutcome( outcome );

        addHeader();
        addNameField();
        addLabeled( "name-label", nameField );                                            // NON-NLS
        descriptionField = new TextArea<String>( "description" );                         // NON-NLS
        descriptionField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                target.addComponent( ExpandedFlowPanel.this );
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
        flowIssuesPanel = new IssuesPanel( "issues", new PropertyModel<ModelObject>( this, "flow" ) );
        flowIssuesPanel.setOutputMarkupId( true );
        add( flowIssuesPanel );
        adjustFields( getFlow() );
    }

    /**
     * Show/hide/enable/disable parts of the panel given the state of the flow.
     *
     * @param f the flow
     */
    private void adjustFields( Flow f ) {
        nameField.setEnabled( f.canSetNameAndDescription() );
        descriptionField.setEnabled( f.canSetNameAndDescription() );
        askedForButtons.setEnabled( f.canSetAskedFor() );
        allField.setVisible( outcome && f.canGetAll() );
        allField.setEnabled( outcome && f.canSetAll() );
        significanceToTargetLabel.setVisible( f.canGetSignificanceToTarget() );
        significanceToTargetChoice.setEnabled( f.canSetSignificanceToTarget() );
        channelRow.setVisible( f.canGetChannels() );
        maxDelayRow.setVisible( f.canGetMaxDelay() );
        delayPanel.enable( f.canSetMaxDelay() );
        significanceToSourceRow.setVisible( f.canGetSignificanceToSource() );
        triggersSourceContainer.setVisible( ( !outcome || f.isAskedFor() ) && f.canGetTriggersSource() );
        triggersSourceCheckBox.setEnabled( f.canSetTriggersSource() );
        terminatesSourceContainer.setVisible( f.canGetTerminatesSource() );
        terminatesSourceCheckBox.setEnabled( f.canSetTerminatesSource() );
    }

    public void update( AjaxRequestTarget target ) {
        super.update( target );
        target.addComponent( flowIssuesPanel );
    }

    private void addNameField() {
        nameField = new AutoCompleteTextField<String>( "name" ) {
            protected Iterator<String> getChoices( String s ) {
                return getFlowNameChoices( s );
            }
        };
        nameField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {                // NON-NLS

            protected void onUpdate( AjaxRequestTarget target ) {
                addIssuesAnnotation( nameField, getFlow(), "name" );
                target.addComponent( nameField );
                target.addComponent( titleLabel );
                target.addComponent( otherChoice );
                updateWith( target );
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
        askedForButtons = new RadioGroup<Boolean>( "askedFor" );                          // NON-NLS
        askedForButtons.add( new Radio<Boolean>( "askedForTrue",                          // NON-NLS
                new Model<Boolean>( true ) ) );
        askedForButtons.add( new Radio<Boolean>( "askedForFalse",                         // NON-NLS
                new Model<Boolean>( false ) ) );
        askedForButtons.add( new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                channelRow.setVisible( isChannelRelevant( getFlow() ) );
                adjustFields( getFlow() );
                target.addComponent( ExpandedFlowPanel.this );
                updateWith( target );
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
                new PropertyModel<Flow.Significance>( getFlow(), "significanceToTarget" ),
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
                target.addComponent( titleLabel );
                updateWith( target );
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
                target.addComponent( titleLabel );
                updateWith( target );
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
                updateWith( target );
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
            getFlow().becomeTriggeringToSource();
        } else {
            getFlow().setSignificanceToSource( Flow.Significance.None );
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
            getFlow().becomeTerminatingToSource();
        } else {
            getFlow().setSignificanceToSource( Flow.Significance.None );
        }
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
        titleLabel = new Label( "title",                                                          // NON-NLS
                new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        return outcome ? getFlow().getOutcomeTitle()
                                : getFlow().getRequirementTitle();
                    }
                } );
        titleLabel.setOutputMarkupId( true );
        add( titleLabel );
        WebMarkupContainer replicateItem = new WebMarkupContainer( "replicate-item" );
        add( replicateItem );
        replicateItem.setVisible(
                ( outcome && getFlow().getTarget().isPart() )
                        || ( !outcome && getFlow().getSource().isPart() ) );
        replicateItem.add( new Link( "replicate" ) {
            @Override
            public void onClick() {
                Flow replica = getFlow().replicate( outcome );
                // PageParameters parameters = getWebPage().getPageParameters();
                // TODO - Denis: Fix problem and remove patch
                PageParameters parameters = ( (ScenarioPage) getWebPage() )
                        .getParametersCollapsing( getFlow().getScenario().getId() );
                parameters.add( ScenarioPage.EXPAND_PARM, String.valueOf( replica.getId() ) );
                this.setResponsePage( getWebPage().getClass(), parameters );
            }
        } );
        // add( new ScenarioLink( "hide", new PropertyModel<Node>( this, "node" ) ) );       // NON-NLS
        // TODO - hack - adjust for Bookmarkable link
        String url = getRequest().getURL().
                replaceAll( "&" + ScenarioPage.EXPAND_PARM + "=" + getFlow().getId(), "" );
        add( new ExternalLink( "hide", url ) );                                  // NON-NLS
        add( new Link( "add-issue" ) {                                                    // NON-NLS

            @Override
            public void onClick() {
                UserIssue newIssue = new UserIssue( getFlow() );
                getService().add( newIssue );
                // PageParameters parameters = getWebPage().getPageParameters();
                // TODO - Denis: Fix probelm and remove patch
                PageParameters parameters = ( (ScenarioPage) getWebPage() )
                        .getParametersCollapsing( getFlow().getScenario().getId() );
                parameters.add( ScenarioPage.EXPAND_PARM, String.valueOf( newIssue.getId() ) );
                setResponsePage( getWebPage().getClass(), parameters );
            }
        } );
        add( new CheckBox( "delete",                                                      // NON-NLS
                new PropertyModel<Boolean>( this, "markedForDeletion" ) ) );              // NON-NLS
    }

    private void addAllField() {
        CheckBox checkBox = new CheckBox( "all" );                                        // NON-NLS
        checkBox.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {                // NON-NLS

            protected void onUpdate( AjaxRequestTarget target ) {
                target.addComponent( titleLabel );
                updateWith( target );
            }
        } );
        allField = new FormComponentLabel( "all-label", checkBox );                       // NON-NLS
        allField.add( checkBox );
        add( allField );
    }

    private Service getService() {
        return ( (Project) getApplication() ).getService();
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
                target.addComponent( ExpandedFlowPanel.this );
                updateWith( target );
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
        return flow;
    }

    public final void setFlow( Flow flow ) {
        this.flow = flow;
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
        delayPanel = new DelayPanel( "max-delay", new PropertyModel<Delay>( this, "flow.maxDelay" ) );
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
            // different other
            Connector connector = (Connector) other;
            Flow connectorFlow = connector.getInnerFlow();
            Flow oldFlow = getFlow();
            Flow newFlow;
            if ( isOutcome() ) {
                if ( connector.getScenario() != oldFlow.getSource().getScenario() ) {
                    newFlow = getService().connect(
                            oldFlow.getSource(),
                            connector,
                            connectorFlow.getName() );
                    newFlow.initFrom( oldFlow );
                } else {
                    newFlow = getService().connect(
                            oldFlow.getSource(),
                            connectorFlow.getTarget(),
                            connectorFlow.getName() );
                    newFlow.initFrom( connectorFlow );
                }
            } else {
                if ( connector.getScenario() != oldFlow.getTarget().getScenario() ) {
                    newFlow = getService().connect(
                            connector,
                            oldFlow.getTarget(),
                            connectorFlow.getName() );
                    newFlow.initFrom( oldFlow );
                } else {
                    newFlow = getService().connect(
                            connectorFlow.getSource(),
                            oldFlow.getTarget(),
                            connectorFlow.getName() );
                    newFlow.initFrom( connectorFlow );
                }
            }
            oldFlow.disconnect();
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

}
