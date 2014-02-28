package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.pages.ModelPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Segment import panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 7, 2009
 * Time: 3:04:36 PM
 */
public class SegmentImportPanel extends AbstractCommandablePanel {

    private WebMarkupContainer importDialog;
    /**
     * Submit link.
     */
    private SubmitLink submitLink;

    /**
     * The file uploads.
     */
    private List<FileUpload> uploads;

    public SegmentImportPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        importDialog = new WebMarkupContainer( "import" );
        importDialog.setOutputMarkupId( true );
        makeVisible( importDialog, false );
        add( importDialog );
        addUploadField();
        addCancel();
        addSubmit();
        adjustFields();
    }

    private void addCancel() {
        AjaxLink closeLink = new AjaxLink( "cancel" ) {
            public void onClick( AjaxRequestTarget target ) {
                close( target );
            }
        };
        importDialog.add( closeLink );
    }

    public void open( AjaxRequestTarget target ) {
        makeVisible( importDialog, true );
        target.add( importDialog );
    }

    public void close( AjaxRequestTarget target ) {
        makeVisible( importDialog, false );
        target.add( importDialog );
    }

    /**
     * {@inheritDoc}
     */
    protected void onBeforeRender() {
        super.onBeforeRender();
        adjustFields();
    }

    private void addSubmit() {
        submitLink = new SubmitLink( "submit" );
        submitLink.setOutputMarkupId( true );
        importDialog.add( submitLink );
    }

    private void adjustFields() {
        submitLink.setEnabled( false );
    }

    public void refresh( AjaxRequestTarget target ) {
        adjustFields();
        target.add( submitLink );
    }

    private void addUploadField() {
        FileUploadField segmentImportField = new FileUploadField(
                "import", new PropertyModel<List<FileUpload>>( this, "uploads" ) );
        segmentImportField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                submitLink.setEnabled( true );
                target.add( submitLink );
            }
        } );
        importDialog.add( segmentImportField );
    }

    /**
     * Set an upload. Called when user attached a file and then submitted.
     *
     * @param uploads the uploaded files info
     */
    public void setUploads( List<FileUpload> uploads ) {
        this.uploads = uploads;
        if ( uploads != null ) {
            for ( FileUpload upload : uploads ) {
                try {
                    getModelManager().importSegment( getUser().getUsername(), getCollaborationModel(), upload.getInputStream() );
                } catch ( IOException e ) {
                    LoggerFactory.getLogger( getClass() ).warn( "Unable to get upload stream", e );
                }
                ( (ModelPage) getPage() ).redirectToPlan();
            }
        }
    }

    public List<FileUpload> getUploads() {
        return uploads;
    }

}
