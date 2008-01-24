{bean, builder ->
    builder.tests {
        // iterating on each BeanTest in each BeanList
        bean.trans('subTests').each{list -> list.findAll{ test -> test.successful }.each{ builder.test(it.name) } }
    }
}
