package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.ExternalFlow;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.ModelObject;
import com.mindalliance.channels.Node;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.analysis.ScenarioAnalyst;
import com.mindalliance.channels.pages.Project;
import org.apache.wicket.AttributeModifier;
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
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

/**
 * Details of an expanded flow.
 */
public abstract class ExpandedFlowPanel extends Panel implements Deletable {

    /** The flow edited by this panel. */
    private Flow flow;

    /** True if outcome, otherwise a requirement. */
    private boolean outcome;

    /** True if this flow is marked for deletion. */
    private boolean markedForDeletion;

    protected ExpandedFlowPanel( String id, Flow flow, boolean outcome ) {
        super( id );
        setFlow( flow );
        setOutcome( outcome );
        setDefaultModel( new CompoundPropertyModel<Flow>( flow ) );

        // TODO add any/all depending on actor

        add( new Label( "title", new PropertyModel( flow,                                 // NON-NLS
                            outcome ? "outcomeTitle" : "requirementTitle" ) ) );          // NON-NLS

        // TODO don't collapse everything on hide
        add( new ScenarioLink( "hide", getNode() ) );                                     // NON-NLS
        add( new CheckBox( "delete",                                                      // NON-NLS
                           new PropertyModel<Boolean>( this, "markedForDeletion" ) ) );   // NON-NLS

        addLabeled( "name-label", new TextField<String>( "name" ) );                      // NON-NLS
        addLabeled( "description-label", new TextArea<String>( "description" ) );         // NON-NLS

        add( new CheckBox( "critical" ) );                                                // NON-NLS
        final RadioGroup<Boolean> rg = new RadioGroup<Boolean>( "askedFor" );             // NON-NLS
        rg.add( new Radio<Boolean>( "askedForTrue", new Model<Boolean>( true ) ) );       // NON-NLS
        rg.add( new Radio<Boolean>( "askedForFalse", new Model<Boolean>( false ) ) );     // NON-NLS
        add( rg );

        addOtherField();
        addAllField();
        addLabeled( "maxDelay-label", new TextField<String>( "maxDelay" ) );              // NON-NLS
        add( new AttachmentPanel( "attachments", flow ) );                                // NON-NLS
    }

    private void addAllField() {
        final CheckBox checkBox = new CheckBox( "all" );
        final FormComponentLabel label = new FormComponentLabel( "all-label", checkBox );
        label.add( checkBox );
        add( label );
        label.setVisible( getOther().isPart() && ( (Part) getOther() ).isRole() );
    }

    /**
     * Add a component with an attached label.
     * @param id the id. Label is "id-label".
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
     * @param component the component
     * @param object the object of the issues
     * @param property the property of concern. If null, get issues of object
     */
    protected void addIssues( FormComponent<?> component, ModelObject object, String property ) {

        final ScenarioAnalyst analyst = ( (Project) getApplication() ).getScenarioAnalyst();
        final String issue = property == null ? analyst.getIssuesSummary( object, false )
                                              : analyst.getIssuesSummary( object, property );
        if ( !issue.isEmpty() ) {
            component.add(
                new AttributeModifier( "class", true, new Model<String>( "error" ) ) );   // NON-NLS
            component.add(
                new AttributeModifier( "title", true, new Model<String>( issue ) ) );     // NON-NLS
        }
    }

    /**
     * Add the target/source dropdown. Fill with getOtherNodes(); select with getOther().
     */
    protected final void addOtherField() {
        final DropDownChoice<Node> other = new DropDownChoice<Node>( "other",             // NON-NLS
            new PropertyModel<Node>( this, "other" ),                                     // NON-NLS
            new PropertyModel<List<? extends Node>>( this, "otherNodes" ),                // NON-NLS
            new IChoiceRenderer<Node>() {
                public Object getDisplayValue( Node object ) {
                    final Node o = getOther();
                    final boolean outside = object.equals( o )
                                            && o.isConnector()
                                            && o.getScenario().equals( getNode().getScenario() );
                    return outside ? "* outside scenario *"
                                            : object.toString();
                }

                public String getIdValue( Node object, int index ) {
                    return Long.toString( object.getId() );
                }
            } );

        final Node otherNode = getOther();
        final ScenarioLink details =
                new ScenarioLink( "other-details", otherNode, getFlow() );                // NON-NLS
        details.add(
            new Label( "type",                                                            // NON-NLS
                       new Model<String>( isOutcome() ? "Target" : "Source" ) ) );

        final FormComponentLabel otherLabel =
            new FormComponentLabel( "other-label", other );                               // NON-NLS
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
        return isOutcome() ? getFlow().getSource()
                           : getFlow().getTarget();
    }

    /**
     * Get the node at the other side of this flow: the source if requirement, the target if
     * outcome.
     * @return the other side of this flow.
     */
    public final Node getOther() {
        final Flow f = getFlow();
        return f.isInternal() ? isOutcome() ? f.getTarget()
                                            : f.getSource()
                              : ( (ExternalFlow) f ).getConnector();
    }

    /**
     * Set the node at the other side of this flow
     * @param other the new source or target
     */
    public void setOther( Node other ) {
        final Flow f = getFlow();
        final Scenario s = f.getSource().getScenario();
        if ( isOutcome() ) {
            final Node source = f.getSource();
            f.disconnect();
            setFlow( s.connect( source, other ) );

        } else {
            final Node target = f.getTarget();
            f.disconnect();
            setFlow( s.connect( other, target ) );
        }
    }

    /**
     * @return list of nodes that can be potential targets of the underlying flow.
     */
    public abstract List<? extends Node> getOtherNodes();

    /** {@inheritDoc} */
    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    /** {@inheritDoc} */
    public void setMarkedForDeletion( boolean delete ) {
        markedForDeletion = delete;
    }


}
