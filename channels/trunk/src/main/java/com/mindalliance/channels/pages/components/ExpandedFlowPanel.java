package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Channelable;
import com.mindalliance.channels.Connector;
import com.mindalliance.channels.Delay;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Service;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.analysis.Analyst;
import com.mindalliance.channels.pages.Project;
import com.mindalliance.channels.pages.ScenarioPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
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
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Details of an expanded flow.
 */
public abstract class ExpandedFlowPanel extends Panel implements DeletableFlow {

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
     * The channel field.
     */
    private WebMarkupContainer channelRow;

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
     * The significance dropdown choice.
     */
    private DropDownChoice significanceChoice;

    /**
     * The channels field
     */
    private ChannelListPanel channelListPanel;

    /**
     * The "all" field
     */
    private FormComponentLabel allField;

    protected ExpandedFlowPanel( String id, Flow flow, boolean outcome, Set<Long> expansions ) {
        super( id );
        setDefaultModel( new CompoundPropertyModel<Flow>(
                new PropertyModel<Flow>( this, "flow" ) ) );                              // NON-NLS

        setOutputMarkupId( true );
        setFlow( flow );
        setOutcome( outcome );

        addHeader();
        nameField = new TextField<String>( "name" );                                      // NON-NLS
        addLabeled( "name-label", nameField );                                            // NON-NLS
        descriptionField = new TextArea<String>( "description" );                         // NON-NLS
        addLabeled( "description-label", descriptionField );                              // NON-NLS
        addChecks();

        addOtherField();
        addAllField();

        Node node = getOther();
        if ( node.isConnector() && node.getScenario().equals( getNode().getScenario() ) ) {
            add( new ConnectedFlowList( "others", (Connector) node ) );                   // NON-NLS
        } else {
            add( new Label( "others", "" ) );                                             // NON-NLS
        }

        addChannelRow();
        add( new DelayPanel( "max-delay", new Model<Delay>( flow.getMaxDelay() ) ) );
        // addLabeled( "maxDelay-label", new TextField<String>( "maxDelay" ) );           // NON-NLS
        add( new AttachmentPanel( "attachments", flow ) );                                // NON-NLS
        add( new IssuesPanel( "issues", new Model<ModelObject>( flow ), expansions ) );   // NON-NLS
        adjustFields( flow );
    }

    /**
     * Show/hide/enable/disable parts of the panel given the state of the flow.
     *
     * @param f the flow
     */
    private void adjustFields( Flow f ) {
        nameField.setEnabled( f.isInternal() );
        descriptionField.setEnabled( f.isInternal() );
        askedForButtons.setEnabled( f.isInternal() );
        significanceChoice.setEnabled( f.isInternal() );
        channelListPanel.setEnabled( isChannelEditable( f ) );

        channelRow.setVisible( isChannelRelevant( f ) );
        allField.setVisible( getOther().isPart() && ( (Part) getOther() ).isOnlyRole() );
    }

    private void addChecks() {
        askedForButtons = new RadioGroup<Boolean>( "askedFor" );                          // NON-NLS
        askedForButtons.add( new Radio<Boolean>( "askedForTrue",                          // NON-NLS
                new Model<Boolean>( true ) ) );
        askedForButtons.add( new Radio<Boolean>( "askedForFalse",                         // NON-NLS
                new Model<Boolean>( false ) ) );
        askedForButtons.add( new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                channelRow.setVisible( isChannelRelevant( flow ) );
                adjustFields( flow );
                target.addComponent( ExpandedFlowPanel.this );
                target.addComponent( ( (ScenarioPage) getPage() ).getGraph() );
            }
        } );

        add( askedForButtons );
        significanceChoice = new DropDownChoice<Flow.Significance>(
                "significance",
                new PropertyModel<Flow.Significance>(flow,"significance"),
                new PropertyModel<List<? extends Flow.Significance>>(flow, "significance.choices"),
                new IChoiceRenderer<Flow.Significance>() {

                    public Object getDisplayValue( Flow.Significance significance ) {
                        return significance.getName(outcome) ;
                    }

                    public String getIdValue( Flow.Significance significance, int i ) {
                        return significance.toString();
                    }
                }
        );
        add(significanceChoice);
    }

    private void addHeader() {
        add( new Label( "title",                                                          // NON-NLS
                new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        return outcome ? getFlow().getOutcomeTitle()
                                : getFlow().getRequirementTitle();
                    }
                } ) );

        // TODO don't collapse everything on hide
        add( new ScenarioLink( "hide", new PropertyModel<Node>( this, "node" ) ) );       // NON-NLS
        add( new Link( "add-issue" ) {                                                    // NON-NLS

            @Override
            public void onClick() {
                UserIssue newIssue = new UserIssue( flow );
                getService().add( newIssue );
                PageParameters parameters = getWebPage().getPageParameters();
                parameters.add( Project.EXPAND_PARM, String.valueOf( newIssue.getId() ) );
                setResponsePage( getWebPage().getClass(), parameters );
            }
        } );
        add( new CheckBox( "delete",                                                      // NON-NLS
                new PropertyModel<Boolean>( this, "markedForDeletion" ) ) );              // NON-NLS
    }

    private void addAllField() {
        CheckBox checkBox = new CheckBox( "all" );                                        // NON-NLS
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
        FormComponentLabel result = new FormComponentLabel( id, component );
        add( result );
        add( component );
        addIssues( component, getFlow(), component.getId() );
        return result;
    }

    /**
     * Add issues annotations to a component.
     *
     * @param component the component
     * @param object    the object of the issues
     * @param property  the property of concern. If null, get issues of object
     */
    protected void addIssues( FormComponent<?> component, ModelObject object, String property ) {
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
        DropDownChoice<Node> other = new DropDownChoice<Node>(
                "other",                                                                  // NON-NLS
                new PropertyModel<Node>( this, "other" ),                                 // NON-NLS
                new PropertyModel<List<? extends Node>>( this, "otherNodes" ),            // NON-NLS
                new IChoiceRenderer<Node>() {
                    public Object getDisplayValue( Node object ) {
                        Node o = getOther();
                        boolean outside =
                                object.equals( o ) && o.isConnector() && o.getScenario().equals(
                                        getNode().getScenario() );
                        return outside ? "* outside scenario *" : object.toString();
                    }

                    public String getIdValue( Node object, int index ) {
                        return Long.toString( object.getId() );
                    }
                } );

        other.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {                  // NON-NLS

            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                adjustFields( flow );
                target.addComponent( ExpandedFlowPanel.this );
                target.addComponent( ( (ScenarioPage) getPage() ).getGraph() );
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
                new FormComponentLabel( "other-label", other );                           // NON-NLS
        otherLabel.add( details );
        add( otherLabel );
        add( other );
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
        Flow oldFlow = getFlow();
        Flow newFlow = isOutcome() ?
                getService().connect( oldFlow.getSource(), other, oldFlow.getName() ) :
                getService().connect( other, oldFlow.getTarget(), oldFlow.getName() );

        newFlow.initFrom( oldFlow );
        oldFlow.disconnect();
        setFlow( newFlow );
    }

    private void addChannelRow() {
        channelRow = new WebMarkupContainer( "channel-row" );                             // NON-NLS
        channelRow.setOutputMarkupPlaceholderTag( true );
        channelRow.add( new Label( "channel-title", new AbstractReadOnlyModel<String>() { // NON-NLS

            @Override
            public String getObject() {
                return getFlow().isAskedFor() ? "Sender channels:" : "Receiver channels:";
            }
        } ) );

        channelListPanel = new ChannelListPanel( "channels", new Model<Channelable>( getFlow() ) );                                // NON-NLS
        channelRow.add( channelListPanel );
        add( channelRow );
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
     * Get list of potential source/targets for this flow.
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
            if ( !node.equals( n ) && (
                    other.equals( n )
                            || !n.isConnector() && !node.isConnectedTo( outcome, n, flow.getName() ) ) )
                result.add( n );
        }

        // Add inputs/outputs of other scenarios
        Service service = ( (Project) getApplication() ).getService();
        for ( Scenario s : service.list( Scenario.class ) ) {
            if ( !scenario.equals( s ) ) {
                Iterator<Connector> c = isOutcome() ? s.inputs() : s.outputs();
                while ( c.hasNext() ) {
                    Connector connector = c.next();
                    if ( other.equals( connector ) || !node.isConnectedTo(
                            outcome, connector, flow.getName() ) )
                        result.add( connector );
                }
            }
        }
        return new ArrayList<Node>( result );
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
