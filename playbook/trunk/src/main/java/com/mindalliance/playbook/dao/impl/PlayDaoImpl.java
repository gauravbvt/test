package com.mindalliance.playbook.dao.impl;

import com.mindalliance.playbook.dao.PlayDao;
import com.mindalliance.playbook.dao.StepDao;
import com.mindalliance.playbook.model.Play;
import com.mindalliance.playbook.model.Step;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Hibernate implementation.
 */
@Repository
public class PlayDaoImpl extends IndexedHibernateDao<Play,Long> implements PlayDao {

    @Autowired
    private StepDao stepDao;

    private final Analyzer analyzer = new StandardAnalyzer( LUCENE_VERSION );
    
    @Override
    public void delete( Play entity ) {
        for ( Step step : entity.getSteps() )
            stepDao.delete( step );

        super.delete( entity );
    }

    @Override
    protected Analyzer getAnalyzer() {
        return analyzer;
    }
}
