/**
 * Created by IntelliJ IDEA.
 * User: jf
 * Date: Feb 5, 2008
 * Time: 4:30:56 PM
 * To change this template use File | Settings | File Templates.
 */
{bean, args, builder ->
    def allSubs = bean.trans('subTests')
    def allTests = beanAt('TestEnvironment', bean.db).tests
    builder.beans() {
        allTests.findAll {!allSubs.contains(it)}.each {test ->
            builder.bean(id: test.id, db: test.db, test.name)
        }
    }
}