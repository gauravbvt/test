package com.mindalliance.channels.pages.profiles;

import com.mindalliance.channels.Organization;
import com.mindalliance.channels.Role;
import com.mindalliance.channels.Scenario;
import com.mindalliance.channels.Part;
import com.mindalliance.channels.Flow;
import com.mindalliance.channels.pages.Project;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2008 Mind-Alliance Systems. All Rights Reserved.
 * Proprietary and Confidential.
 * User: jf
 * Date: Jan 8, 2009
 * Time: 1:16:32 PM
 */
public class SortableJobsProvider extends SortableDataProvider<Job> {

    private List<Job> jobs;

    public SortableJobsProvider( Role role ) {
        jobs = findAllJobsForRole( role );
        setSort( "name", true );
    }

    public SortableJobsProvider( Organization organization ) {
        // TODO
    }


    public Iterator<Job> iterator( int first, int count ) {
        final SortParam sortParam = getSort();
        List<Job> sortedJobs = new ArrayList<Job>();
        Collections.copy( sortedJobs, jobs );
        Collections.sort( sortedJobs, new Comparator<Job>() {
            /**
             * @param job the first object to be compared.
             * @param otherJob the second object to be compared.
             * @return a negative integer, zero, or a positive integer as the
             *         first argument is less than, equal to, or greater than the
             *         second.
             * @throws ClassCastException if the arguments' types prevent them from
             *                            being compared by this comparator.
             */
            public int compare( Job job, Job otherJob ) {
                int comp = 0;
                String sortProperty = sortParam.getProperty();
                try {
                    String value = PropertyUtils.getProperty( job, sortProperty ).toString();
                    String otherValue = PropertyUtils.getProperty( otherJob, sortProperty ).toString();
                    comp = value.compareTo( otherValue );
                }
                catch ( Exception e ) {
                    e.printStackTrace();
                }
                return sortParam.isAscending() ? comp : comp * -1;
            }
        } );
        return sortedJobs.subList( first, first + count ).iterator();
    }

    public int size() {
        return jobs.size();
    }

    public IModel<Job> model( Job job ) {
        return new Model<Job>( job );
    }

    private List<Job> findAllJobsForRole( Role role ) {
        HashSet<Job> set = new HashSet<Job>();
        Iterator<Scenario> scenarios = Project.getProject().getDao().scenarios();
        while ( scenarios.hasNext() ) {
            Scenario scenario = scenarios.next();
            Iterator<Part> parts = scenario.parts();
            while ( parts.hasNext() ) {
                Part part = parts.next();
                if ( part.getRole() == role && part.getActor() != null ) {
                    Job job = new Job(part);
                    // Find all channels used to communicate with this part
                    Iterator<Flow> flows = scenario.flows();
                    while (flows.hasNext()) {
                        Flow flow = flows.next();
                        if (flow.getTarget() == part && !flow.isAskedFor()) {
                            job.addChannel(flow.getChannel());
                        }
                        if (flow.getSource() == part && flow.isAskedFor()) {
                            job.addChannel(flow.getChannel());
                        }
                    }
                    set.add( job );
                }
            }
        }
        List<Job> list = new ArrayList<Job>();
        list.addAll( set );
        return list;
    }
}
