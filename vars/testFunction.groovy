def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    // now build, based on the configuration provided
    print "This is a test of a global DSL"

    stage name: 'Stage', concurrency: 1 {
        print "This is a test of a global DSL"
        print "Output: ${config.testOutput}"
    }
}