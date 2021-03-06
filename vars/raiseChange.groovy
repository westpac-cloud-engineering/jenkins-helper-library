def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    node {

        stage ('Raise Change') {

            // Remove all old log files 
            sh "rm -f *.log"
            sh "rm -f *json"

            // Generate Deployment File
            writeJSON file: 'deployment_configuration.json', json: config.deploy_info

            // Download Terraform Helper Scripts #
            print 'echo "Loading ServiceNow Scripts"'
            dir('helper_scripts') {
                    git url: 'https://github.com/westpac-cloud-engineering/terraform-pipeline-wrapper'
            }

            unstash 'logs'

            // Trigger Run
            withEnv(['PYTHONPATH=PYTHONPATH:helper_scripts/']){
                sh 'pip3 install -r helper_scripts/requirements.txt'
                sh "set +e; python3 helper_scripts/tfe2_pipeline_wrapper/servicenow_change.py \
                --action_type plan \
                --configuration_file deployment_configuration.json \
                --log_file deployment_plan.log \
                --sys_id None" 

                // Upload Outputs
                results = readJSON file: 'change_results.json'

                return results['result']['sys_id']
            }
        }
    }
}