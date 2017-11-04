def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    node {
        sh "echo \"Change ${config.change_details["id"]}: Successfully Closed\""
    }

}