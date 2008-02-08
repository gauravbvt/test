{bean, args, builder ->
    def excluded = [bean.id]
    excluded.addAll(bean.trans('parent').collect{it.id})
    println "excluded ancestors=$excluded"
    excluded.addAll(bean.subTests.collect{it.id})
    println "with excluded subTests=$excluded"
    // all tests that are not ancestors, or not immediate sub tests
    def candidates = beanAt('TestEnvironment', bean.db).tests.findAll {!excluded.contains(it.id)}
    println "candidates=$candidates"
    builder.beans() {
        candidates.each {test ->
            builder.bean(id: test.id, db: test.db, test.name)
        }
    }
}
