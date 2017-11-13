def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    node {

        stage ('Apply') {

            // Download Terraform Helper Scripts
            print 'echo "Getting Terraform Scripts"'
            git url: 'https://github.com/Westpac/cloud_deployment_helpers'
            writeJSON file: 'environment_variables.json', json: config.credentials

            // Run Apply
            sh 'pip3 install -r terraform_enterprise_2/run_jobs/requirements.txt'
            
            sh "set +e; python3 terraform_enterprise_2/run_jobs/run_terraform_apply.py \'${config.app_id}\' \'${config.component_name}\' \'${config.environment}\' \'${config.destroy_flag}\' \'${config.plan_info.run_id}\' "

            results = readJSON file: 'data.json'

            if (results['status'] == "applied") {
                print "Successfully Applied!"
                currentBuild.result = 'SUCCESS'
            } else {
                print "Apply Failed!"
                currentBuild.result = 'FAILURE'
            } 
            sh "LOGS WILL APPEAR HERE PENDING TF 2 API FIX" // REPLACE WHEN LOGGING API IS AVAILABLE
        }
    }

}