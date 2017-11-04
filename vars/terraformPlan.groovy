def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    node {

        stage ('Plan') {

            // Download Terraform Helper Scripts
            sh 'echo "Getting Terraform Scripts"'
            git url: 'https://github.com/Westpac/cloud_deployment_helpers'

            // Creating Plan
            sh 'pip3 install -r terraform_enterprise_2/run_jobs/requirements.txt'
            sh "set +e; python3 terraform_enterprise_2/run_jobs/run_terraform_plan.py \'${config.org}\' \'${config.app_id}\' \'${config.workspace}\' \'${config.destroy_flag}\' \'${config.tf_token}\'"

            def plan_info = readJSON file: 'data.json'

            if (plan_info['status'] == "unchanged") {
                sh 'echo "Plan Succeeded: No Changes"'
                currentBuild.result = 'SUCCESS'
            }
            else if (plan_info['status'] == "failed") {
                sh 'echo "Plan Failed: Logs Below"'
                currentBuild.result = 'FAILURE'
            }

            sh 'echo "LOGS WILL APPEAR HERE PENDING TF 2 API FIX"' // REPLACE WHEN LOGGING API IS AVAILABLE

            return plan_info
        }
    }
}