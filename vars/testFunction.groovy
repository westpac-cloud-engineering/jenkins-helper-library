def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    node {
        stage ('Get Keys'){
            wrap(
                [
                    $class: 'ConsulKVReadWrapper', reads: [
                        [ aclToken: '', apiUri: '', debugMode: 'DISABLED', envKey: "TEST_KEY", hostURL: 'http://consul.australiaeast.cloudapp.azure.com:8500', ingoreGlobalSettings: true, key: 'apps/A00001/cost_centre'],
                        [ aclToken: '', apiUri: '', debugMode: 'DISABLED', envKey: "TEST_KEY2", hostURL: 'http://consul.australiaeast.cloudapp.azure.com:8500', ingoreGlobalSettings: true, key: 'apps/A00001/app_name'],
                    ]
                ]
            ) {
                print env.TEST_KEY
                print env.TEST_KEY2
            }
        }
    }

}