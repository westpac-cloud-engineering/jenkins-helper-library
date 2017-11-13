def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    stage ('Get Keys'){
        wrap(
            [
                $class: 'ConsulKVReadWrapper', reads: [
                    [ aclToken: '', apiUri: '', debugMode: 'DISABLED', envKey: "app_name", hostURL: "${config.consulUri}", ingoreGlobalSettings: true, key: "apps/${config.app_id}/app_name"],
                    [ aclToken: '', apiUri: '', debugMode: 'DISABLED', envKey: "bcrm", hostURL: "${config.consulUri}", ingoreGlobalSettings: true, key: "apps/${config.app_id}/bcrm"],         
                ]
            ]
        ) {
            print env.TEST_KEY
            print env.TEST_KEY2
        }
        
        wrap(
            [
                $class: 'ConsulKVReadWrapper', reads: [
                    [ aclToken: '', apiUri: '', debugMode: 'DISABLED', envKey: "cost_centre", hostURL: "${config.consulUri}", ingoreGlobalSettings: true, key: "shared_services/terraform/poc/organisation"],
                ]
            ]
        ) {
            print env.TEST_KEY
            print env.TEST_KEY2
        }
    }

}