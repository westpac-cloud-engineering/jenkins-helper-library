def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    stage ('Close Change') {
        print "Change ${config.change_details["id"]}: Successfully Closed"
    }

}