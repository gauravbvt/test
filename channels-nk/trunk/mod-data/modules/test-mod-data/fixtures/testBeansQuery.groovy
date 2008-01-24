{bean, builder ->
    builder.tests {
        // Find all successful sub-tests transitively reacheable from a test
        bean.trans('subTests').each {list ->
            list.findAll {test -> test.successful}.each {
               builder.test(it.name)
            }
        }
    }
}
