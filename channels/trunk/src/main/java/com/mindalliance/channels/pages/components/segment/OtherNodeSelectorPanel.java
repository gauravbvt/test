package com.mindalliance.channels.pages.components.segment;

import com.mindalliance.channels.command.Change;
import com.mindalliance.channels.model.Node;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.util.Matcher;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Node selector.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Apr 28, 2010
 * Time: 1:15:10 PM
 */
public class OtherNodeSelectorPanel extends AbstractUpdatablePanel {

    /**
     *   Maximum display length for task name.
     */
    static final private int MAX_ITEM_DISPLAY_LENGTH = 35;
    /**
     * To indicate other than first choice part.
     */
    static final private String OTHER = "Other...";
    /**
     * To indicate a tbd other.
     */
    static final private String TBD = "*To be determined*";
    /**
     * Selecting the unknown node from the first choices drop-down indicates the desire to select from the second choices.
     */
    private Node unknownOtherNode;
    /**
     * Node associated to the other node to be selected.
     */
    private IModel<Node> nodeModel;
    /**
     * The "other" node previously selected.
     */
    private IModel<Node> otherNodeModel;
    /**
     * Parts to be shown in a drop down.
     */
    private IModel<List<Node>> firstChoiceNodes;
    /**                                                                                                                                                                                       acced
     * Parts to be offerred as auto-complete candidates.
     */
    private IModel<List<Node>> secondChoiceNodes;
    /**
     * Drop down from which to select node from first choices.
     */
    private DropDownChoice<Node> firstChoiceInput;
    /**
     * Autocomplete text field from which to select node from second choices.
     */
    private WebMarkupContainer secondChoice;
    /**
     * Currently selected other node.
     */
    private Node selectedOtherNode;

    public OtherNodeSelectorPanel( String id,
                                   IModel<Node> nodeModel,
                                   IModel<Node> otherNodeModel,
                                   IModel<List<Node>> firstChoiceNodes,
                                   IModel<List<Node>> secondChoiceNodes) {
        super( id );
        this.nodeModel = nodeModel;
        this.otherNodeModel = otherNodeModel;
        this.firstChoiceNodes = firstChoiceNodes;
        this.secondChoiceNodes = secondChoiceNodes;
        init();
    }

    private void init() {
        unknownOtherNode = new Part() {
            /** {@inheritDoc} */
            public String toString() {
                return "Enter a task name";
            }
            /** {@inheritDoc} */
            public String displayString() {
                return toString();
            }
            /** {@inheritDoc} */
            public String displayString( int maxItemLength ) {
                return displayString();
            }
        };
        selectedOtherNode = otherNodeModel.getObject();
        addFirstChoiceDropDown();
        addSecondChoiceInput();
    }

    private void addFirstChoiceDropDown() {
        firstChoiceInput = new DropDownChoice<Node>(
                "firstChoice",
                new PropertyModel<Node>( this, "selectedOtherNode" ),
                new PropertyModel<List<Node>>( this, "firstChoiceNodes" ),
                new IChoiceRenderer<Node>() {
                    public Object getDisplayValue( Node otherNode ) {
                        return shortDisplayString( otherNode );
                    }

                    public String getIdValue( Node object, int index ) {
                        return Integer.toString( index );
                    }
                }
        );
        firstChoiceInput.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                if ( isUnknownOtherSelected() ) {
                    makeVisible( secondChoice, true );
                    target.addComponent( secondChoice );
                } else if ( isOtherNodeChanged() )
                    update( target, new Change( Change.Type.Selected, selectedOtherNode, "other" ) );
            }
        } );
        firstChoiceInput.setOutputMarkupId( true );
        add( firstChoiceInput );
    }

    private boolean isUnknownOtherSelected() {
        return selectedOtherNode.equals( unknownOtherNode );
    }

    private String displayString( Node node ) {
        return node.equals( unknownOtherNode )
                ? OTHER
                : isTBD( node )
                ? TBD
                : node.displayString();
    }

    private String shortDisplayString( Node node ) {
        return node.equals( unknownOtherNode )
                ? OTHER
                : isTBD( node )
                ? TBD
                : node.displayString( MAX_ITEM_DISPLAY_LENGTH );
    }

    private boolean isTBD( Node node ) {
        return node.isConnector() && node.getSegment().equals( nodeModel.getObject().getSegment());
    }

    @SuppressWarnings("unchecked")
    private void addSecondChoiceInput() {
        secondChoice = new WebMarkupContainer( "secondChoice" );
        secondChoice.setOutputMarkupId( true );
        makeVisible( secondChoice, false );
        add( secondChoice );
        final List<String> choices = (List<String>) CollectionUtils.collect(
                getSecondChoices(),
                new Transformer() {
                    public Object transform( Object input ) {
                        return displayString( ( (Node) input ) );
                    }
                }
        );
        AutoCompleteTextField<String> secondChoiceInput = new AutoCompleteTextField<String>(
                "secondChoice-input",
                new PropertyModel<String>( this, "selectedOtherNodeName" ) ) {
            protected Iterator<String> getChoices( String nodeString ) {
                List<String> candidates = new ArrayList<String>();
                for ( String choice : choices ) {
                    if ( Matcher.matches( nodeString, choice ) )
                        candidates.add( choice );
                }
                return candidates.iterator();
            }
        };
        secondChoiceInput.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                makeVisible( secondChoice, false );
                if ( isUnknownOtherSelected() ) {
                    selectedOtherNode = otherNodeModel.getObject();
                }
                if ( isOtherNodeChanged() )
                    update( target, new Change( Change.Type.Selected, selectedOtherNode, "other" ) );
                target.addComponent( firstChoiceInput );
                target.addComponent( secondChoice );
            }
        } );
        secondChoice.add( secondChoiceInput );
    }

    private boolean isOtherNodeChanged() {
        return !isUnknownOtherSelected() && !selectedOtherNode.equals( otherNodeModel.getObject() );
    }

    public List<Node> getFirstChoiceNodes() {
        List<Node> firstChoices = new ArrayList<Node>();
        firstChoices.addAll( firstChoiceNodes.getObject() );
        firstChoices.remove( selectedOtherNode );
        Collections.sort( firstChoices, new Comparator<Node>() {
            public int compare( Node n1, Node n2 ) {
                return Collator.getInstance().compare( displayString( n1 ), displayString( n2 ) );
            }
        } );
        List<Node> results = new ArrayList<Node>();
        results.add( selectedOtherNode );
        results.addAll( firstChoices );
        if ( !getSecondChoices().isEmpty() ) {
            results.add( unknownOtherNode );
        }   // TODO - second choices recomputed
        return results;
    }

    public List<Node> getSecondChoices() {
        List<Node> nodes = new ArrayList<Node>( secondChoiceNodes.getObject() );
        if ( selectedOtherNode != null ) nodes.remove( selectedOtherNode );
        return nodes;
    }

    public String getSelectedOtherNodeName() {
        return selectedOtherNode.toString();
    }

    public void setSelectedOtherNodeName( final String nodeName ) {
        Node node = null;
        if ( nodeName != null ) {
            node = (Node) CollectionUtils.find(
                    getSecondChoices(),
                    new Predicate() {
                        public boolean evaluate( Object object ) {
                            return Matcher.same( displayString( (Node) object ), nodeName );
                        }
                    }
            );
        }
        if ( node != null ) selectedOtherNode = node;
    }
}
