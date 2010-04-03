// Copyright (C) 2010 Mind-Alliance Systems LLC.
// All rights reserved.

package com.mindalliance.channels.query;

import com.mindalliance.channels.command.DefaultCommander;
import com.mindalliance.channels.dao.PlanDao;
import com.mindalliance.channels.model.Plan;

/**
 * Available functionality on a given plan.
 */
public class PlanningService {

    private final DefaultCommander commander;

    private final PlanDao planDao;

    private final Plan plan;

    public PlanningService( DefaultCommander commander ) {
        this.commander = commander;
        planDao = commander.getPlanDao();
        plan = planDao.getPlan();
    }

    public DefaultCommander getCommander() {
        return commander;
    }

    public PlanDao getPlanDao() {
        return planDao;
    }

    public Plan getPlan() {
        return plan;
    }
}
