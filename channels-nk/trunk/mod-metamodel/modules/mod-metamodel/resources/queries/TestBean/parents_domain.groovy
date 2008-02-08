{bean, args, builder ->
    def excluded = [bean.id]
    bean.trans('subTests').each {list -> list.each {beanRef -> excluded.add(beanRef.id)}}
    println "excluded=$excluded"
    def candidates = []
    beanAt('TestEnvironment', bean.db).tests.each {beanRef ->
        if (!excluded.contains(beanRef.id)) {candidates.add(beanRef)}
    }
    println "candidates=$candidates"
    builder.beans() {
        candidates.each {
            // def pb = beanRef.dereference()
            builder.bean(id: it.id, db: it.db, it.name)
        }
    }
}