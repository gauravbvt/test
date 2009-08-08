package com.mindalliance.channels.pages.components;

import com.mindalliance.channels.dao.PlanManager;
import com.mindalliance.channels.pages.PlanPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.PropertyModel;

/**
 * Scenario import panel.
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Aug 7, 2009
 * Time: 3:04:36 PM
 */
public class ScenarioImportPanel extends AbstractCommandablePanel {

    /**
     * Submit link.
     */
    private SubmitLink submitLink;

    /**
     * The file upload.
     */
    private FileUpload upload;

    public ScenarioImportPanel( String id ) {
        super( id );
        addUploadField();
        addSubmit();
    }

    /**
     * {@inheritDoc}
     */
    protected void onBeforeRender() {
        super.onBeforeRender();
        adjustFields();
        makeVisible( submitLink, false );
    }

    private void addSubmit() {
        submitLink = new SubmitLink( "submit" );
        submitLink.setOutputMarkupId( true );
        submitLink.setEnabled( false );
        add( submitLink );
    }

    private void adjustFields() {
        makeVisible( submitLink, false );
    }

    public void refresh( AjaxRequestTarget target ) {
        adjustFields();
        target.addComponent( submitLink );
    }

    private void addUploadField() {
        FileUploadField scenarioImportField = new FileUploadField(
                "import", new PropertyModel<FileUpload>( this, "upload" ) );
        scenarioImportField.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                makeVisible( submitLink, true );
                submitLink.setEnabled( true );
                target.addComponent( submitLink );
            }
        } );
        add( scenarioImportField );
    }

    /**
     * Set an upload. Called when user attached a file and then submitted.
     *
     * @param upload the uploaded file info
     */
    public void setUpload( FileUpload upload ) {
        this.upload = upload;
        if ( upload != null ) {
            getPlanManager().importScenario( upload, getQueryService() );
            ( (PlanPage) getPage() ).redirectToPlan();
        }
    }

    public FileUpload getUpload() {
        return upload;
    }

    private PlanManager getPlanManager() {
       return getQueryService().getPlanManager();
    }
}
