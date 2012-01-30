package com.mindalliance.playbook.pages;

import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Contact upload page.
 */
public class UploadContacts extends MobilePage {

    public UploadContacts( PageParameters parameters ) {
        super( parameters );
    }

    @Override
    public String getPageTitle() {
        return "Upload contacts";
    }
}
