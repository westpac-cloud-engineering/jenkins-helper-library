def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    stage ('Close Change') {
        print "Change ${config.change["id"]}: Successfully Closed"
        currentBuild.result = 'SUCCESS'

    }

}