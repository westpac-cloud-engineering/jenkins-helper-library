def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    node {

        stage ('Apply') {

            // Run Apply
            sh 'pip3 install -r terraform/requirements.txt'
            sh "set +e; python3 terraform/run_apply.py \'${config.organisation}\' \'${config.app_id}\' \'${config.workspace_name}\' \'${plan_info.props['run_id']}\' \'${config.destroy_infrastructure}\' \'${config.atlas_token}\'"

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