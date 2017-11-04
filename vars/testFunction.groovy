def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    print "This is a test of a global DSL"

    stage name: 'Stage', concurrency: 1 {
        print "This is a test of a global DSL"
        print "Output: ${config.testoutput}"
    }
}