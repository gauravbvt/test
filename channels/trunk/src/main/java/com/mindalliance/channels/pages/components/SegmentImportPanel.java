package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.pages.PlanPage;
import com.mindalliance.channels.core.dao.User;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
     * The file upload.
     */
    private FileUpload upload;

    public SegmentImportPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        importDialog = new WebMarkupContainer("import");
        importDialog.setOutputMarkupId( true );
        makeVisible(importDialog, false);
        add(importDialog);
        addUploadField();
        addCancel();
        addSubmit();
        adjustFields();
    }

    private void addCancel() {
        AjaxFallbackLink closeLink = new AjaxFallbackLink( "cancel" ) {
            public void onClick( AjaxRequestTarget target ) {
                close(target);
            }
        };
        importDialog.add( closeLink );
    }

    public void open( AjaxRequestTarget target) {
        makeVisible( importDialog, true );
        target.addComponent( importDialog );
    }

    public void close(AjaxRequestTarget target) {
        makeVisible( importDialog, false );
        target.addComponent( importDialog );
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
        target.addComponent( submitLink );
    }

    private void addUploadField() {
        FileUploadField segmentImportField = new FileUploadField(
                "import", new PropertyModel<FileUpload>( this, "upload" ) );
        segmentImportField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                submitLink.setEnabled( true );
                target.addComponent( submitLink );
            }
        } );
        importDialog.add( segmentImportField );
    }

    /**
     * Set an upload. Called when user attached a file and then submitted.
     *
     * @param upload the uploaded file info
     */
    public void setUpload( FileUpload upload ) {
        this.upload = upload;
        if ( upload != null ) {
            try {
                getPlanManager().importSegment( User.plan(), upload.getInputStream() );
            } catch ( IOException e ) {
                LoggerFactory.getLogger( getClass() ).warn( "Unable to get upload stream", e );
            }
            ( (PlanPage) getPage() ).redirectToPlan();
        }
    }

    public FileUpload getUpload() {
        return upload;
    }

}
