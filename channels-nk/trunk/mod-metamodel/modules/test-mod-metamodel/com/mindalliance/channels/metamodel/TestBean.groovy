package com.mindalliance.channels.metamodel

import com.mindalliance.channels.nk.bean.BeanReference
import com.mindalliance.channels.nk.bean.BeanList
import com.mindalliance.channels.nk.bean.AbstractPersistentBean
import com.mindalliance.channels.nk.bean.SimpleData

/**
* Created by IntelliJ IDEA.
* User: jf
* Date: Jan 19, 2008
* Time: 11:55:22 AM
*/
class TestBean extends AbstractPersistentBean {

    def name = new SimpleData(dataClass: String.class)
    def description = new SimpleData(dataClass: String.class, calculate: 'calculate_description')
    def kind = new SimpleData(dataClass: String.class)
    def successful = new SimpleData(dataClass: Boolean.class)
    def score = new SimpleData(dataClass: BigDecimal.class, calculate: 'calculate_score') // derived SimpleData property
    def cost = new SimpleData(dataClass: BigDecimal.class)
    def parent = new BeanReference(beanClass: TestBean.class.name, domain: 'parent_domain') // parent bean domain = all TestBeans that are not among this one's subTests (transitively)
    def runs = new BeanList(itemPrototype: new TestRunComponent(), itemName: 'run')
    def subTests = new BeanList(itemPrototype: new BeanReference(beanClass: TestBean.class.name, domain: 'subTests_domain'), itemName: 'test') // subtest bean domain = all TestBeans that are not an ancestor (no circularity) or already a direct subtest (no redundancy)
    def successfulTests = new BeanList(calculate: 'calculate_successful_tests', itemName: 'successfulTest') // derived BeanList property
    def mostExpensiveSubTest = new BeanReference(beanClass: TestBean.class.name, calculate: 'most_expensive_subtest')
    def confirmation = new BeanReference(beanClass: TestBean.class.name) // owned BeanReference

    Map getBeanProperties() {
        return [name: name, description: description, kind: kind, successful: successful, score: score, cost: cost, parent: parent,
                runs: runs, subTests: subTests, successfulTests: successfulTests, mostExpensiveSubTest: mostExpensiveSubTest,
                confirmation: confirmation]
    }
    /* Metadata keys:
        label, hint, required, readonly, appearance, anyAttribute (all)
        range, step (Numerical SimpleData)
        choices (SimpleData)
        number (BeanList) - how many to display at once
    */
    void initialize() {
        defaultMetadata = [
                name: [required: true, hint: 'A name for this test'],
                kind: [required: true, hint: 'What kind of test was this?', choices: ['Integration', 'Unit', 'Performance', 'Usability']],
                successful: [required: true, hint: 'Whether this test was successul'],
                cost: [hint: 'Between 0 (no cost) and 100 (prohibitive)', range: 0..100, step: 1],
                parent: [label: '', required: false, hint: 'The integrating test'],
                runs: [label: 'Test runs', number: 4],
                subTests: [label: 'Unit tests'],
                confirmation: [label: 'Confirmation test', hint: 'Backup test that confirms this one']
        ]
    }


    // CALCULATIONS

    def calculate_description() {
        return "A ${successful.value ? 'successful' : 'failed'} ${kind.value} test "
    }

    // Calculates a score
    def calculate_score() {
        int count = subTests.size()
        if (count == 0) {
            def val = successful.value ? 1.0 : 0.0
            return val
        }
        else {
            def scores = subTests.inject(0) {i, test ->
                i += test.score.value
            }
            return scores / count
        }
    }

    // Calculates a list of bean references
    def calculate_successful_tests() {
        List testsFound = subTests.findAll {test -> test.successful.value}
        return testsFound
    }

    // Calculates an id (db must always be that of the property's context bean)
    def most_expensive_subtest() {
        String beanId
        def maxCost = -1
        subTests.each {test ->
            if (test.cost.value > maxCost) {
                maxCost = test.cost.value
                beanId = test.id
            }
        }
        return beanId
    }

    // DOMAINS

    // Called within NetKernelCategory and PersistentBeanCategory
    void parent_domain(def builder) {
        def excluded = [this.id]
        trans('subTests').each {list -> list.each {beanRef -> excluded.add(beanRef.id)}}
        def candidates = []
        def testEnvironment = beanAt('TestEnvironment')
        testEnvironment.tests.each {beanRef ->
            if (!excluded.contains(beanRef.id)) {candidates.add(beanRef)}
        }
        builder.items(xmlns: '') {
            candidates.each {
                def test = it
                builder.item(label: test.name) {
                    parent() {
                        builder.ref {
                            builder.id(test.id)
                            builder.db(test.db)
                        }
                    }
                }
            }
        }
    }

    void subTests_domain(def builder) {
        def excluded = [this.id]
        excluded.addAll(trans('parent').collect {it.id})
        excluded.addAll(this.subTests.collect {it.id})
        println "with excluded subTests=$excluded"
        // all tests that are not ancestors, or not immediate sub tests
        def candidates = beanAt('TestEnvironment').tests.findAll {!excluded.contains(it.id)}
        builder.items() {
            candidates.each {
                def test = it
                builder.item(label: test.name) {
                    builder.ref {
                        builder.id(test.id)
                        builder.db(test.db)
                    }
                }
            }
        }
    }
}