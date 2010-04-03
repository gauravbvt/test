package com.mindalliance.channels.pages.components.plan;

import com.mindalliance.channels.attachments.Attachment;
import com.mindalliance.channels.model.Identifiable;
import com.mindalliance.channels.model.ModelObject;
import com.mindalliance.channels.model.Part;
import com.mindalliance.channels.model.Segment;
import com.mindalliance.channels.model.SegmentObject;
import com.mindalliance.channels.pages.components.AbstractCommandablePanel;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.Filterable;
import com.mindalliance.channels.util.SortableBeanProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Plan bibliography panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jun 13, 2009
 * Time: 1:15:43 PM
 */
public class PlanBibliographyPanel extends AbstractCommandablePanel implements Filterable {

    /**
     * Maximum number of rows of attachments to show at a time.
     */
    private static final int MAX_ROWS = 20;
    /**
     * All attachment types.
     */
    private static final String ALL = "All";
    /**
     * Selected attachment type.
     */
    private Attachment.Type selectedType = null;
    /**
     * Filters mapped by property.
     */
    private Map<String, ModelObject> filters = new HashMap<String, ModelObject>();
    /**
     * Choice of attachment types.
     */
    private static List<String> TypeNames;
    /**
     * Filterable table of attached documents.
     */
    private AttachmentTable attachmentTable;

    static {
        TypeNames = new ArrayList<String>();
        TypeNames.add( ALL );
        for ( Attachment.Type type : Attachment.Type.values() ) {
            TypeNames.add( type.name() );
        }
    }

    public PlanBibliographyPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        addAttachmentTypeChoice();
        addAttachmentTable();
    }

    private void addAttachmentTypeChoice() {
        DropDownChoice<String> typeChoice = new DropDownChoice<String>(
                "types",
                new PropertyModel<String>( this, "typeName" ),
                TypeNames,
                new IChoiceRenderer<String>() {
                    public Object getDisplayValue( String id ) {
                        return id.equals( ALL ) ? ALL : Attachment.Type.valueOf( id ).getLabel();
                    }

                    public String getIdValue( String id, int i ) {
                        return id;
                    }
                }
        );
        typeChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            protected void onUpdate( AjaxRequestTarget target ) {
                addAttachmentTable();
                target.addComponent( attachmentTable );
            }
        } );
        add( typeChoice );
    }

    private void addAttachmentTable() {
        attachmentTable = new AttachmentTable(
                "attachmentTable",
                new PropertyModel<List<AttachmentRelationship>>( this, "attachments" ) );
        attachmentTable.setOutputMarkupId( true );
        addOrReplace( attachmentTable );
    }

    /**
     * {@inheritDoc}
     */
    public void toggleFilter( Identifiable identifiable, String property, AjaxRequestTarget target ) {
        if ( isFiltered( identifiable, property ) ) {
            filters.remove( property );
        } else {
            filters.put( property, (ModelObject) identifiable );
        }
        addAttachmentTable();
        target.addComponent( attachmentTable );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered( Identifiable identifiable, String property ) {
        ModelObject filtered = filters.get( property );
        return filtered != null && filtered.equals( identifiable );
    }

    public String getTypeName() {
        if ( selectedType == null )
            return ALL;
        else
            return selectedType.name();
    }

    public void setTypeName( String val ) {
        if ( val.equals( ALL ) )
            selectedType = null;
        else
            selectedType = Attachment.Type.valueOf( val );
    }

    /**
     * Find all unfiltered attachment relationships.
     *
     * @return a list of attachment realtionships
     */
    @SuppressWarnings( "unchecked" )
    public List<AttachmentRelationship> getAttachments() {
        List<AttachmentRelationship> attachmentRels = new ArrayList<AttachmentRelationship>();
        for ( ModelObject mo : getQueryService().findAllModelObjects() ) {
            for ( Attachment attachment : mo.getAttachments() ) {
                attachmentRels.add( new AttachmentRelationship(
                        mo,
                        attachment ) );
            }
        }
        return (List<AttachmentRelationship>) CollectionUtils.select(
                attachmentRels,
                new Predicate() {
                    public boolean evaluate( Object obj ) {
                        return !isFilteredOut( (AttachmentRelationship) obj );
                    }
                } );
    }

    private boolean isFilteredOut( AttachmentRelationship attRel ) {
        if ( selectedType != null && selectedType != attRel.getAttachment().getType() )
            return true;
        ModelObject mo = filters.get( "modelObject" );
        if ( mo != null && !mo.equals( attRel.getModelObject() ) )
            return true;
        Segment segment = (Segment) filters.get( "segment" );
        ModelObject attachee = attRel.getModelObject();
        return segment != null
                && ( !( attachee instanceof SegmentObject )
                || !( (SegmentObject) attachee ).getSegment().equals( segment ) );
    }

    /**
     * An attachment to a model object.
     */
    public class AttachmentRelationship implements Serializable {
        /**
         * A model object.
         */
        private ModelObject modelObject;
        /**
         * An attachment of the model object.
         */
        private Attachment attachment;

        public AttachmentRelationship(
                ModelObject modelObject,
                Attachment attachment ) {
            this.modelObject = modelObject;
            this.attachment = attachment;
        }

        public ModelObject getModelObject() {
            return modelObject;
        }

        public Attachment getAttachment() {
            return attachment;
        }

        /**
         * Get label for attachment.
         *
         * @return a string
         */
        public String getUrlLabel() {
            return getQueryService().getAttachmentManager().getLabel( getPlan(), attachment );
        }

        /**
         * Get model object's segment if applicable.
         *
         * @return a sceanrio or null
         */
        public Segment getSegment() {
            if ( modelObject instanceof SegmentObject ) {
                return ( (SegmentObject) modelObject ).getSegment();
            } else {
                return null;
            }
        }

        /**
         * Label for attachee.
         *
         * @return a string
         */
        public String getAttacheeLabel() {
            return ( modelObject instanceof Part )
                    ? ( (Part) modelObject ).getTitle()
                    : modelObject.getName();
        }
    }

    /**
     * Attachment table.
     */
    public class AttachmentTable extends AbstractTablePanel<AttachmentRelationship> {
        /**
         * Attachment relationships model.
         */
        private IModel<List<AttachmentRelationship>> attachmentRelsModel;

        public AttachmentTable(
                String id,
                IModel<List<AttachmentRelationship>> attachmentRelsModel ) {
            super( id, null, MAX_ROWS, null );
            this.attachmentRelsModel = attachmentRelsModel;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            final List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // columns
            columns.add( makeColumn(
                    "Type",
                    "attachment.type.label",
                    EMPTY ) );
            columns.add( makeExternalLinkColumn(
                    "Document",
                    "attachment.url",
                    "urlLabel",
                    EMPTY ) );
            columns.add( makeFilterableLinkColumn(
                    "Attached to",
                    "modelObject",
                    "attacheeLabel",
                    EMPTY,
                    PlanBibliographyPanel.this ) );
            columns.add( makeFilterableLinkColumn(
                    "Plan segment",
                    "segment",
                    "segment.name",
                    EMPTY,
                    PlanBibliographyPanel.this ) );
            // provider and table
            add( new AjaxFallbackDefaultDataTable(
                    "attachments",
                    columns,
                    new SortableBeanProvider<AttachmentRelationship>(
                            attachmentRelsModel.getObject(),
                            "attachment.type.label" ),
                    getPageSize() ) );
        }
    }

}
