
def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    stage name: 'Stage', concurrency: 1 {
        print "Output: ${config.testoutput}"
    }
}