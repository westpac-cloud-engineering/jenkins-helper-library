def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    stage ('Raise Change') {

        print "Change against ${config.app_id} being raised"

        def change_details = [:]
        change_details.id = "CRQ000000001"

        // RAISE CHANGE HERE
        print "Change ${change_details['id']}: Successfully Raised"

        // Only request approval on production environments - This logic will move to SNow
        if (config.production) {
            input message:'Approve deployment?'
        }

        return change_details
    }
}