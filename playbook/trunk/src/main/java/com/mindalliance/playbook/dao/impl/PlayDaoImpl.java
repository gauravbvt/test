package com.mindalliance.playbook.dao.impl;

import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.model.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Hibernate implementation.
 */
@Repository
public class PlayDaoImpl extends IndexedHibernateDao<Play,Long> implements PlayDao {

    @Autowired
    private StepDao stepDao;

    @Override
    public void delete( Play entity ) {
        for ( Step step : entity.getSteps() )
            stepDao.delete( step );

        super.delete( entity );
    }
}
