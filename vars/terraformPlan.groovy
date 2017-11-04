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
            sh "set +e; python3 terraform_enterprise_2/run_jobs/run_plan.py \'${config.organisation}\' \'${config.app_id}\' \'${config.workspace_name}\' \'${config.destroy_infrastructure}\' \'${config.atlas_token}\'"

            def plan_info = readJSON file: 'data.json'

            if (props['status'] == "unchanged") {
                sh 'echo "Plan Succeeded: No Changes"'
                currentBuild.result = 'SUCCESS'
            }
            else if (props['status'] == "failed") {
                sh 'echo "Plan Failed: Logs Below"'
                currentBuild.result = 'FAILURE'
            }

            sh 'echo "LOGS WILL APPEAR HERE PENDING TF 2 API FIX"' // REPLACE WHEN LOGGING API IS AVAILABLE

            return plan_info
        }
    }
}