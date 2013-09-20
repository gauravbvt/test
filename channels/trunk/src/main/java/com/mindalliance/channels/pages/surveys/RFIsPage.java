package com.mindalliance.channels.pages.surveys;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.db.data.messages.Feedback;
import com.mindalliance.channels.db.data.surveys.RFI;
import com.mindalliance.channels.db.services.surveys.RFIService;
import com.mindalliance.channels.pages.AbstractChannelsBasicPage;
import com.mindalliance.channels.pages.Modalable;
import com.mindalliance.channels.pages.Updatable;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * My surveys page.
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/23/12
 * Time: 1:31 PM
 */
public class RFIsPage extends AbstractChannelsBasicPage implements Modalable {

    public static final String SURVEYS = "surveys";
    public static final String RFI_PARM = "rfi";

    private RFI selectedRFI;
    private Component rfiPanel;
    private UserRFIsPanel userRFIsPanel;
    @SpringBean
    private RFIService rfiService;

    public RFIsPage() {
        this( new PageParameters() );
    }

    public RFIsPage( PageParameters parameters ) {
        super( parameters );
    }


    @Override
    protected String getHelpSectionId() {
        return "surveys-page";
    }

    @Override
    protected String getHelpTopicId() {
        return "about-surveys-page";
    }


    private void processParameters( PageParameters parameters ) {
        String rfiUid = parameters.get( RFI_PARM ).toString();
        if ( rfiUid != null ) {
            selectedRFI = rfiService.load( rfiUid );
        }
    }

    protected void addContent() {
        processParameters( getParameters() );
        addModalDialog( "dialog", null, getContainer() );
        getContainer().add( new Label( "templateName", getPlan().getName() ) );
        addUserRFIsPanel();
        addRFIPanel();
    }

    @Override
    protected String getContentsCssClass() {
        return "surveys-contents";
    }

    @Override
    public String getPageName() {
        return "Surveys";
    }

    @Override
    protected String getFeedbackType() {
        return Feedback.SURVEYS;
    }

    @Override
    protected void updateContent( AjaxRequestTarget target ) {
        userRFIsPanel.updateContent( target );
    }

    @Override
    protected String getDefaultUserRoleId() {
        return "user";
    }


    private void addUserRFIsPanel() {
        userRFIsPanel = new UserRFIsPanel( "rfis", new PropertyModel<RFI>( this, "selectedRFI" ) );
        getContainer().addOrReplace( userRFIsPanel );
    }

    private void addRFIPanel() {
        if ( selectedRFI == null ) {
            rfiPanel = new Label( "rfi", "" );
        } else {
            rfiPanel = new RFIPanel( "rfi", new Model<RFI>( selectedRFI ) );
        }
        rfiPanel.setOutputMarkupId( true );
        makeVisible( rfiPanel, selectedRFI != null );
        getContainer().addOrReplace( rfiPanel );
    }

    @Override
    public void changed( Change change ) {
        if ( change.isForInstanceOf( RFI.class ) ) {
            if ( change.isExpanded() ) {
                RFI rfi = (RFI) change.getSubject( getCommunityService() );
                if ( selectedRFI != null && rfi.equals( selectedRFI ) ) {
                    selectedRFI = null;
                } else {
                    selectedRFI = rfi;
                }
            } else if ( change.isCollapsed() ) {
                selectedRFI = null;
            }
        } else if ( change.isRefreshNeeded() ) {
            selectedRFI = null;
        } else {
            super.changed( change );
        }
    }


    public void updateWith( AjaxRequestTarget target, Change change, List<Updatable> updatables ) {
        String message = change.getMessage();
        if ( message != null ) {
            getNotifier().create( target,
                    "Notification",
                    message );
        }
        if ( change.isRefreshNeeded() ) {
            addRFIPanel();
            target.add( rfiPanel );
        } else if ( change.isForInstanceOf( RFI.class ) ) {
            if ( change.isExpanded() || change.isCollapsed() ) {
                addRFIPanel();
                target.add( rfiPanel );
                if ( change.isCollapsed() ) {
                    userRFIsPanel.refresh( target, change );
                }
            } else if ( change.isUpdated() ) {
                userRFIsPanel.refresh( target, change );
                if ( change.isForProperty( "declined" )
                        || change.isForProperty( "accepted" )
                        || change.isForProperty( "submitted" ) ) {
                    selectedRFI = null;
                    addRFIPanel();
                    target.add( rfiPanel );
                }
            } else {
                super.updateWith( target, change, updatables );
            }
        } else {
            super.updateWith( target, change, updatables );
        }
    }

    public RFI getSelectedRFI() {
        return selectedRFI;
    }

}
