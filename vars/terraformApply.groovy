def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    node {

        stage ('Apply') {

            // Download Terraform Helper Scripts
            sh 'echo "Getting Terraform Scripts"'
            git url: 'https://github.com/Westpac/cloud_deployment_helpers'

            // Run Apply
            sh 'pip3 install -r terraform_enterprise_2/run_jobs/requirements.txt'
            sh "set +e; python3 terraform_enterprise_2/run_jobs/run_terraform_apply.py \'${config.org}\' \'${config.app_id}\' \'${config.workspace}\' \'${plan_info.props['run_id']}\' \'${config.destroy_flag}\' \'${config.tf_token}\'"

            results = readJSON file: 'data.json'

            if (results['status'] == "applied") {
                currentBuild.result = 'SUCCESS'
                sh 'echo "Successfully Applied!"'
                results.changes = False
            } else if results['status'] == "errored") {
                currentBuild.result = 'FAILURE'
                sh 'echo "Apply Failed!"'
                results.changes = False
            } else {
                sh 'echo "Changes Found!"'
                results.changes = True
            }

            sh 'echo "LOGS WILL APPEAR HERE PENDING TF 2 API FIX"' // REPLACE WHEN LOGGING API IS AVAILABLE
        }
    }

}