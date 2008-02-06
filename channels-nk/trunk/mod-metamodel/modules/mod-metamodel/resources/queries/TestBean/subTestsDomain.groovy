/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Feb 5, 2008
 * Time: 4:30:56 PM
 * To change this template use File | Settings | File Templates.
 */
{bean, args, builder ->
    def ancestors = bean.trans('parent')
    def allTests = beanAt('TestEnvironment', bean.db).tests
    def subs = bean.subTests
    builder.beans() {
        allTests.findAll {!subs.contains(it) && !ancestors.contains(it)}.each {test ->
            builder.bean(id: test.id, db: test.db, test.name)
        }
    }
}
