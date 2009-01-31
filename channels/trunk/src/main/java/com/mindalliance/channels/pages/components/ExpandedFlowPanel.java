package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.Connector;
import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.UserIssue;
import com.mindalliance.channels.Delay;
import com.mindalliance.channels.Service;
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
     * The critical checkbox.
     */
    private CheckBox criticalCheck;
    /**
     * The channels field
     */
    private FormComponent<?> channelField;
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

        final Node node = getOther();
        if ( node.isConnector() && node.getScenario().equals( getNode().getScenario() ) ) {
            add( new ConnectedFlowList( "others", (Connector) node ) );                   // NON-NLS
        } else {
            add( new Label( "others", "" ) );                                             // NON-NLS
        }

        addChannelRow();
        addMaxDelayFields();
        // addLabeled( "maxDelay-label", new TextField<String>( "maxDelay" ) );              // NON-NLS
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
        criticalCheck.setEnabled( f.isInternal() );
        channelField.setEnabled( isChannelEditable( f ) );

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
        criticalCheck = new CheckBox( "critical" );                                       // NON-NLS
        add( criticalCheck );
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
                final UserIssue newIssue = new UserIssue( flow );
                final Service service = ( (Project) getApplication() ).getService();                
                service.add( newIssue );
                final PageParameters parameters = getWebPage().getPageParameters();
                parameters.add( Project.EXPAND_PARM, String.valueOf( newIssue.getId() ) );
                setResponsePage( getWebPage().getClass(), parameters );
            }
        } );
        add( new CheckBox( "delete",                                                      // NON-NLS
                new PropertyModel<Boolean>( this, "markedForDeletion" ) ) );              // NON-NLS
    }

    private void addAllField() {
        final CheckBox checkBox = new CheckBox( "all" );                                  // NON-NLS
        allField = new FormComponentLabel( "all-label", checkBox );
        allField.add( checkBox );
        add( allField );                                                                  // NON-NLS
    }

    /**
     * Add a component with an attached label.
     *
     * @param id        the id. Label is "id-label".
     * @param component the component
     * @return the label component
     */
    protected final FormComponentLabel addLabeled( String id, FormComponent<?> component ) {
        final FormComponentLabel result = new FormComponentLabel( id, component );
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
        final Analyst analyst = ( (Project) getApplication() ).getAnalyst();
        final String issue = property == null ?
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
        final DropDownChoice<Node> other = new DropDownChoice<Node>(
                "other",                                                                  // NON-NLS
                new PropertyModel<Node>( this, "other" ),                                 // NON-NLS
                new PropertyModel<List<? extends Node>>( this, "otherNodes" ),            // NON-NLS
                new IChoiceRenderer<Node>() {
                    public Object getDisplayValue( Node object ) {
                        final Node o = getOther();
                        final boolean outside =
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
        final ScenarioLink details = new ScenarioLink( "other-details",                   // NON-NLS
                new PropertyModel<Node>( this, "other" ),                                 // NON-NLS
                getFlow() );
        details.add(
                new Label( "type",                                                        // NON-NLS
                        new Model<String>( isOutcome() ? "To" : "From" ) ) );
        final FormComponentLabel otherLabel =
                new FormComponentLabel( "other-label", other );                           // NON-NLS
        otherLabel.add( details );
        add( otherLabel );
        add( other );
    }

    /**
     * Add input fields for max delay
     */
    protected final void addMaxDelayFields() {
        add( new TextField<String>( "delay-amount", new PropertyModel<String>( flow, "maxDelay.amountString" ) ) );
        add( new DropDownChoice<Delay.Unit>(
                "delay-unit",
                new PropertyModel<Delay.Unit>( flow, "maxDelay.unit" ),
                new PropertyModel<List<? extends Delay.Unit>>( flow, "maxDelay.units" ),
                new IChoiceRenderer<Delay.Unit>() {
                    public Object getDisplayValue( Delay.Unit unit ) {
                        return unit.toString();
                    }

                    public String getIdValue( Delay.Unit unit, int i ) {
                        return unit.toString();
                    }
                }
        ) {
        } );
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
        final Flow f = getFlow();
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
        final Flow oldFlow = getFlow();
        final Scenario s = other.getScenario();
        final Flow newFlow = isOutcome() ?
                s.connect( oldFlow.getSource(), other ) :
                s.connect( other, oldFlow.getTarget() );
        newFlow.initFrom( oldFlow );
        oldFlow.disconnect();
        setFlow( newFlow );
    }

    private void addChannelRow() {
        channelRow = new WebMarkupContainer( "channel-row" );                             // NON-NLS
        channelRow.setOutputMarkupPlaceholderTag( true );
        channelField = new TextField<String>( "channel" );                                // NON-NLS
        final FormComponentLabel label =
                new FormComponentLabel( "channel-label", channelField );                  // NON-NLS
        channelRow.add( label );
        channelRow.add( channelField );
        addIssues( channelField, getFlow(), channelField.getId() );
        label.add( new Label( "channel-title", new AbstractReadOnlyModel<String>() {      // NON-NLS

            @Override
            public String getObject() {
                return getFlow().isAskedFor() ? "Sender channel:" : "Receiver channel:";
            }
        } ) );

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
        final Node node = getNode();
        final Node other = getOther();
        final Scenario scenario = node.getScenario();
        final Set<Node> result = new TreeSet<Node>();

        // Add other parts of this scenario
        final Iterator<Node> nodes = scenario.nodes();
        while ( nodes.hasNext() ) {
            final Node n = nodes.next();
            if ( !node.equals( n ) && (
                    other.equals( n )
                            || !n.isConnector() && !node.isConnectedTo( outcome, n ) ) )
                result.add( n );
        }

        // Add inputs/outputs of other scenarios
        final Service service = ( (Project) getApplication() ).getService();
        final Iterator<Scenario> scenarios = service.iterate( Scenario.class );
        while ( scenarios.hasNext() ) {
            final Scenario s = scenarios.next();
            if ( !scenario.equals( s ) ) {
                final Iterator<Connector> c = isOutcome() ? s.inputs() : s.outputs();
                while ( c.hasNext() ) {
                    final Connector connector = c.next();
                    if ( other.equals( connector ) || !node.isConnectedTo( outcome, connector ) )
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
