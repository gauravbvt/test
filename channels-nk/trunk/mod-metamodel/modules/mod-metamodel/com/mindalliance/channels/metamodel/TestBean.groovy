package com.mindalliance.channels.metamodel

import com.mindalliance.channels.nk.bean.BeanReference
import com.mindalliance.channels.nk.bean.BeanList
import com.mindalliance.channels.nk.bean.AbstractPersistentBean
import com.mindalliance.channels.nk.bean.SimpleData
import com.mindalliance.channels.nk.bean.BeanDomain
import com.mindalliance.channels.nk.bean.IBeanDomain

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 19, 2008
* Time: 11:55:22 AM
*/
class TestBean extends AbstractPersistentBean {    // TODO - move to com.mindalliance.channels.metamodel.test

    def name = new SimpleData(String.class)
    def kind = new SimpleData(String.class)
    def successful = new SimpleData(Boolean.class)
    def score = new SimpleData(Double.class)
    def parent  = new BeanReference(beanClass: TestBean.class.name, domain: getParentDomain())
    def runs = new BeanList(itemPrototype: new TestRunComponent(), itemName:'run')
    def subTests = new BeanList(itemPrototype: new BeanReference(beanClass: TestBean.class.name, domain: getSubTestsDomain()) , itemName:'test')

    Map getBeanProperties() {
        return [name:name, kind:kind, successful:successful, score:score, parent:parent, runs:runs, subTests:subTests]
    }
    /* Metadata keys:
        label, hint, required, readonly, appearance, anyAttribute (all)
        range, step (Numerical SimpleData)
        choices (SimpleData)
        number (BeanList) - how many to display at once
    */
    void initialize() {
        defaultMetadata =   [
                name: [hint: 'A name for this test' ],
                kind: [hint: 'What kind of test was this?', choices:['Integration', 'Unit', 'Performance', 'Usability']],
                successful: [hint: 'Whether this test was successul' ],
                score: [hint: 'Between 0 (total failure) and 1 (total success)', range: [0..100], step: 1 ],
                parent: [label: '', required: false, hint: 'The integrating test' ],
                runs: [label: 'Test runs', number: 4 ],
                subTests: [label: 'Unit tests' ]
            ]
    }

    // parent bean domain = all TestBeans that are not among this one's subTests (transitively)
    private IBeanDomain getParentDomain() {
        IBeanDomain domain = new BeanDomain()
        domain.query = 'parents_domain.groovy'
        return domain
    }

    // subtest bean domain = all TestBeans that are not an ancestor (no circularity) or already a direct subtest (no redundancy)
    private IBeanDomain getSubTestsDomain() {
        IBeanDomain domain = new BeanDomain()
        domain.query = 'subTests_domain.groovy' 
        return domain
     }

}