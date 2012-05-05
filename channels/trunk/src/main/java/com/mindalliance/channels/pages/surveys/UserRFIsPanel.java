package com.mindalliance.channels.pages.surveys;

import com.mindalliance.channels.core.command.Change;
import com.mindalliance.channels.core.model.Identifiable;
import com.mindalliance.channels.core.model.NotFoundException;
import com.mindalliance.channels.core.model.Organization;
import com.mindalliance.channels.core.util.ChannelsUtils;
import com.mindalliance.channels.core.util.SortableBeanProvider;
import com.mindalliance.channels.pages.components.AbstractTablePanel;
import com.mindalliance.channels.pages.components.AbstractUpdatablePanel;
import com.mindalliance.channels.social.model.rfi.RFI;
import com.mindalliance.channels.social.services.RFIForwardService;
import com.mindalliance.channels.social.services.SurveysDAO;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Copyright (C) 2008-2012 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 4/24/12
 * Time: 3:20 PM
 */
public class UserRFIsPanel extends AbstractUpdatablePanel {

    private static final int MAX_ROWS = 5;

    @SpringBean
    private SurveysDAO surveysDAO;

    @SpringBean
    private RFIForwardService rfiForwardService;

    private List<RFI> doneRFIs;
    private List<RFI> todoRFIs;
    private List<RFI> declinedRFIs;
    private AjaxTabbedPanel tabbedPanel;

    public UserRFIsPanel( String id ) {
        super( id );
        init();
    }

    private void init() {
        addTabbedPanel();
    }

    private void addTabbedPanel() {
        reset();
        tabbedPanel = new AjaxTabbedPanel( "tabs", getTabs() ) {
            @Override
            protected void onAjaxUpdate( AjaxRequestTarget target ) {
                update( target, new Change( Change.Type.NeedsRefresh ) );
            }
        };
        tabbedPanel.setOutputMarkupId( true );
        addOrReplace( tabbedPanel );
    }

    private void reset() {
        doneRFIs = null;
        todoRFIs = null;
        declinedRFIs = null;
    }

    private List<ITab> getTabs() {
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add( new AbstractTab( new Model<String>( getTodoLabel() ) ) {
            public Panel getPanel( String id ) {
                return new RFIsTable( id, getTodoRFIs() );
            }
        } );
        tabs.add( new AbstractTab( new Model<String>( getDoneLabel() ) ) {
            public Panel getPanel( String id ) {
                return new RFIsTable( id, getDoneRFIs() );
            }
        } );
        tabs.add( new AbstractTab( new Model<String>( getDeclinedLabel() ) ) {
            public Panel getPanel( String id ) {
                return new RFIsTable( id, getDeclinedRFIs() );
            }
        } );
        return tabs;
    }

    private String getTodoLabel() {
        int count = getTodoRFIs().size();
        return "Todo" + " (" + count + ")";
    }


    private String getDoneLabel() {
        int count = getDoneRFIs().size();
        return "Done" + " (" + count + ")";
    }

    private String getDeclinedLabel() {
        int count = getDeclinedRFIs().size();
        return "Declined" + " (" + count + ")";
    }

    private List<RFIWrapper> getTodoRFIs() {
        if ( todoRFIs == null ) {
            todoRFIs = surveysDAO.findIncompleteRFIs( getPlan(), getUser(), getQueryService(), getAnalyst() );
        }
        return wrapAll( todoRFIs );
    }

    private List<RFIWrapper> getDoneRFIs() {
        if ( doneRFIs == null ) {
            doneRFIs = surveysDAO.findCompletedRFIs( getPlan(), getUser(), getQueryService(), getAnalyst() );
        }
        return wrapAll( doneRFIs );
    }

    private List<RFIWrapper> getDeclinedRFIs() {
        if ( declinedRFIs == null ) {
            declinedRFIs = surveysDAO.findDeclinedRFIs( getPlan(), getUser(), getQueryService(), getAnalyst() );
        }
        return wrapAll( declinedRFIs );
    }

    private List<RFIWrapper> wrapAll( List<RFI> rfis ) {
        List<RFIWrapper> wrappers = new ArrayList<RFIWrapper>();
        for ( RFI rfi : rfis ) {
            wrappers.add( new RFIWrapper( rfi ) );
        }
        return wrappers;
    }

    public void refresh( AjaxRequestTarget target, Change change ) {
        int tabIndex = tabbedPanel.getSelectedTab();
        addTabbedPanel();
        tabbedPanel.setSelectedTab( tabIndex );
        target.add( tabbedPanel );
    }

    @Override
    public void changed( Change change ) {
        if ( change.isForInstanceOf( RFIWrapper.class ) && change.isExpanded() ) {
            RFIWrapper rfiWrapper = (RFIWrapper) change.getSubject( getQueryService() );
            change.setSubject( rfiWrapper.getRFI() );
        }
        super.changed( change );
    }


    public class RFIWrapper implements Identifiable {

        private RFI rfi;

        private RFIWrapper( RFI rfi ) {
            this.rfi = rfi;
        }

        public String getSurveyLabel() {
            return rfi.getLabel( getQueryService() );
        }

        public String getJob() {
            StringBuilder sb = new StringBuilder();
            Organization org = getOrganization();
            String title = rfi.getTitle();
            sb.append( title == null ? "" : title + " " );
            sb.append( org == null ? "" : "at " + org.getName() );
            String job = sb.toString();
            return job.isEmpty() ? null : job;
        }

        private Organization getOrganization() {
            Organization org = null;
            Long id = rfi.getOrganizationId();
            if ( id != null ) {
                try {
                    org = getQueryService().find( Organization.class, rfi.getOrganizationId() );
                } catch ( NotFoundException e ) {
                    // do nothing
                }
            }
            return org;
        }

        public String getSentBy() {
            return getUserFullName( rfi.getUsername() );
        }

        public Date getCreatedOn() {
            return rfi.getCreated();
        }

        public String getShortSentOn() {
            return getShortDateFormat().format( rfi.getCreated() );
        }

        public String getSentOn() {
            return getDateFormat().format( rfi.getCreated() );
        }

        public long getTimeLeft() {
            return rfi.getTimeLeft();
        }

        public String getShortTimeLeft() {
            return rfi.getShortTimeLeft();
        }

        public String getLongTimeLeft() {
            return rfi.getLongTimeLeft();
        }

        public String getTimeLeftStyle() {
            Date deadline = rfi.getDeadline();
            if ( deadline == null ) {
                return null;
            } else {
                long delta = deadline.getTime() - new Date().getTime();
                boolean overdue = delta < 0;
                return overdue ? "late" : null;
            }
        }

        public int getCompletionPercent() {
            return surveysDAO.getPercentCompletion( rfi );
        }

        public String getAnswersRequired() {
            int requiredQuestionsCount = surveysDAO.getRequiredQuestionCount( rfi );
            int requiredAnswersCount = surveysDAO.getRequiredAnswersCount( rfi );
            int left = requiredQuestionsCount - requiredAnswersCount;
            if ( left > 0 ) {
                return left
                        + " more "
                        + ( left > 1 ? "answers" : "answer")
                        + " required";
            } else {
                return null;
            }
        }

        public String getForwardedTo() {
            List<String> forwardedTo = rfiForwardService.findForwardedTo( rfi );
            return ChannelsUtils.listToString( new ArrayList<String>( forwardedTo ), ", " );
        }

        public int getForwardedToCount() {
            return rfiForwardService.findForwardedTo( rfi ).size();
        }

        public RFI getRFI() {
            return rfi;
        }

        // Identifiable


        @Override
        public long getId() {
            return rfi.getId();
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public String getTypeName() {
            return RFI.class.getSimpleName();
        }

        @Override
        public boolean isModifiableInProduction() {
            return true;
        }

        @Override
        public String getClassLabel() {
            return getTypeName();
        }

        @Override
        public String getName() {
            return rfi.getName();
        }
    }

    private class RFIsTable extends AbstractTablePanel<RFIWrapper> {
        private List<RFIWrapper> rfis;

        public RFIsTable( String id, List<RFIWrapper> rfis ) {
            super( id );
            this.rfis = rfis;
            initialize();
        }

        @SuppressWarnings( "unchecked" )
        private void initialize() {
            List<IColumn<?>> columns = new ArrayList<IColumn<?>>();
            // Columns
            columns.add( makeColumn( "Survey", "surveyLabel", EMPTY ) );
            columns.add( makeColumn( "Sent by", "sentBy", EMPTY ) );
            columns.add( makeColumn( "On", "shortSentOn", null, EMPTY, "sentOn" ) );
            columns.add( makeColumn( "To you as", "job", EMPTY ) );
            columns.add( makeColumn( "Deadline", "shortTimeLeft", "@timeLeftStyle", EMPTY, "longTimeLeft" ) );
            columns.add( makeColumn( "Progress", "completionPercent", "percent", EMPTY, "answersRequired" ) );
            columns.add( makeColumn( "Forwarded to", "forwardedToCount", null, EMPTY, "forwardedTo" ) );
            columns.add( makeExpandLinkColumn( "", "", "open..." ) );
            // Provider and table
            add( new AjaxFallbackDefaultDataTable( "rfis",
                    columns,
                    new SortableBeanProvider<RFIWrapper>( rfis,
                            "timeLeft" ),
                    MAX_ROWS ) );

        }
    }
}
