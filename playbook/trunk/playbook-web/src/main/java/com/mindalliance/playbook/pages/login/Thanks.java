package com.mindalliance.playbook.pages.login;

import com.mindalliance.playbook.pages.MobilePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * An empty thank you page, for now.
 */
public class Thanks extends MobilePage {

    public Thanks( PageParameters parameters ) {
        super( parameters );
        
        setStatelessHint( true );
    }

    @Override
    public String getPageTitle() {
        return "Thank you";
    }
}
