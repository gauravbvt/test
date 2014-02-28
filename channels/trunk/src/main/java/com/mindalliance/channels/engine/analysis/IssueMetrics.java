package com.mindalliance.channels.engine.analysis;

import com.mindalliance.channels.core.community.CommunityService;
import com.mindalliance.channels.core.model.Issue;
import com.mindalliance.channels.core.model.Level;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright (C) 2008-2013 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: 3/12/13
 * Time: 2:20 PM
 */
public class IssueMetrics implements Serializable {

    private List<Issue> allUnwaivedIssues = new ArrayList<Issue>(  );
    private List<Issue> allWaivedIssues = new ArrayList<Issue>(  );
    private Map<String, IssueTypeMetrics> issueTypeMetrics; // type  -> IssueTypeMetrics - type in {VALIDITY, COMPLETENESS, ROBUSTNESS}

    public IssueMetrics( CommunityService communityService ) {
        computeMetrics( communityService, communityService.getAnalyst() );
    }

    private void computeMetrics( CommunityService communityService, Analyst analyst ) {
        Doctor doctor = communityService.getDoctor();
        allWaivedIssues = doctor.findAllWaivedIssues( communityService );
        allUnwaivedIssues = doctor.findAllUnwaivedIssues( communityService );
        issueTypeMetrics = new HashMap<String, IssueTypeMetrics>(  );
        for ( String type : Issue.TYPES ) {
            issueTypeMetrics.put( type, new IssueTypeMetrics( type, communityService, analyst ) );
        }
    }

    public int getAllIssuesCount() {
        return allUnwaivedIssues.size() + allWaivedIssues.size();
    }

    public int getAllWaivedIssuesCount() {
        return allWaivedIssues.size();
    }

    public int getAllUnwaivedIssuesCount() {
        return allUnwaivedIssues.size();
    }

    public List<Issue> getAllUnwaivedIssues() {
        return allUnwaivedIssues;
    }

    public List<Issue> getAllWaivedIssues() {
        return allWaivedIssues;
    }

    public Metrics getIssueSummaryMetrics( String type, boolean waived ) {
        List<Issue> issues = getAllIssues( type, waived );
        Set<String> kinds = new HashSet<String>();
        for ( Issue issue : issues ) {
            kinds.add( issue.getKind() );
        }
        double n = issues.size();
        double total = getAllIssuesCount();
        double percent = total == 0 ? 0.0 : n / total;
        return new Metrics( issues.size(), percent, kinds.size(), waived );
    }

    public IssueTypeMetrics getIssueTypeMetrics( String type ) {
        return issueTypeMetrics.get( type );
    }

    @SuppressWarnings( "unchecked" )
    public Metrics getSeverityMetrics( final Level severity, String type, boolean waived ) {
        List<Issue> issues = (List<Issue>) CollectionUtils.select(
                getAllIssues( type, waived ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Issue) object ).getSeverity().equals( severity );
                    }
                }
        );
        Set<String> kinds = new HashSet<String>();
        for ( Issue issue : issues ) {
            kinds.add( issue.getKind() );
        }
        double n = issues.size();
        double total = getAllIssuesCount();
        double percent = total == 0 ? 0.0 : n / total;
        return new Metrics( issues.size(), percent, kinds.size(), waived );
    }


    @SuppressWarnings( "unchecked" )
    private List<Issue> getAllIssues( final String type, boolean waived ) {
        return (List<Issue>) CollectionUtils.select(
                getAllIssues( waived ),
                new Predicate() {
                    @Override
                    public boolean evaluate( Object object ) {
                        return ( (Issue) object ).getType().equals( type );
                    }
                }
        );
    }

    private List<Issue> getAllIssues( boolean waived ) {
        return waived
                ? allWaivedIssues
                : allUnwaivedIssues;
    }

    public class IssueTypeMetrics implements Serializable {

        private String issueType;
        private Map<String, List<Issue>> issuesOfTypeByKind = new HashMap<String, List<Issue>>();
        private Map<String, String> issueKindLabels = new HashMap<String, String>();
        private List<Issue> issuesOfType;


        public IssueTypeMetrics( String issueType, CommunityService communityService, Analyst analyst ) {
            this.issueType = issueType;
            computeTypeMetrics(  );
        }

        @SuppressWarnings( "unchecked" )
        private void computeTypeMetrics(  ) {
            issuesOfType =
                    (List<Issue>) CollectionUtils.select( getAllUnwaivedIssues(),
                            new Predicate() {
                                @Override
                                public boolean evaluate( Object object ) {
                                    return ( (Issue) object ).getType().equals( issueType );
                                }
                            } );
            for ( Issue issue : issuesOfType ) {
                String kind = issue.getKind();
                List<Issue> issuesOfKind = issuesOfTypeByKind.get( kind );
                if ( issuesOfKind == null ) {
                    issueKindLabels.put( kind, issue.getDetectorLabel() );
                    issuesOfKind = new ArrayList<Issue>();
                    issuesOfTypeByKind.put( kind, issuesOfKind );
                }
                issuesOfKind.add( issue );
            }
        }

        public boolean isEmpty() {
            return issuesOfType.isEmpty();
        }

        public int getSeverityCount( String kind, final Level severity ) {
            return CollectionUtils.select( issuesOfTypeByKind.get( kind ), new Predicate() {
                @Override
                public boolean evaluate( Object object ) {
                    return ( (Issue) object ).getSeverity().equals( severity );
                }
            } ).size();
        }

        public String getIssueLabel( String kind ) {
            return issueKindLabels.get( kind );
        }

        public List<String> getIssueKinds() {
            List<String> issueKinds = new ArrayList<String>( issuesOfTypeByKind.keySet() );
            Collections.sort( issueKinds, new Comparator<String>() {
                @Override
                public int compare( String k1, String k2 ) {
                    int k1Count = issuesOfTypeByKind.get( k1 ).size();
                    int k2Count = issuesOfTypeByKind.get( k2 ).size();
                    return k1Count > k2Count ? -1 : k1Count < k2Count ? 1 : 0;
                }
            } );
            return issueKinds;
        }

        public List<Issue> getIssuesOfKind( String kind ) {
            return issuesOfTypeByKind.get( kind );
        }

        public Metrics getIssueTypeMetrics( String kind ) {
            int count = issuesOfTypeByKind.get( kind ).size();
            double total = getAllUnwaivedIssuesCount();
            double percent = total == 0 ? 0.0 : (double)count / total;
            int kinds = 1;
            return new Metrics( count, percent, kinds, true );
        }


    }

    public class Metrics implements Serializable {

        int count;
        double percent;
        int kindsCount;
        private boolean waived;

        Metrics( int count, double percent, int kindsCount, boolean waived ) {
            this.count = count;
            this.percent = percent;
            this.kindsCount = kindsCount;
            this.waived = waived;
        }

        public int getCount() {
            return count;
        }

        public double getPercent() {
            return percent;
        }

        public int getKindsCount() {
            return kindsCount;
        }

        public boolean isWaived() {
            return waived;
        }
    }
}
