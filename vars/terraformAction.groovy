def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    node {

        stage ('Terraform Action') {

            print "Begin Terraform: ${config.request_type}" 

            // Remove all old log files 
            sh "rm -f *.log"
            sh "rm -f *json"

            // Generate Deployment File
            writeJSON file: 'deployment_configuration.json', json: deploy_info

            // Download Terraform Helper Scripts
            print 'echo "Getting Terraform Scripts"'
            dir('helper_scripts') {
                    git url: 'https://github.com/westpac-cloud-engineering/terraform-pipeline-wrapper'
            }

            // Trigger Run
            sh 'pip3 install -r helper_scripts/requirements.txt'
            sh "set +e; python3 helper_scripts/tfe2_pipeline_wrapper/terraform_job.py \
            \ --request_type '${config.request_type}\' \
            \ --configuration_file deployment_configuration.json " 

            // Upload Outputs
            archiveArtifacts artifacts: '*.log', fingerprint: true
            archiveArtifacts artifacts: '*.json', fingerprint: true

            // Interperate Results
            results = readJSON file: 'data.json'
            if (results['attributes']['status'] == "applied") {
                print "Successfully Applied!"
                currentBuild.result = 'SUCCESS'
            } else if (results['attributes']['status'] == "planned") {
                print "Successfully Planned!"
                currentBuild.result = 'SUCCESS'
            } else {
                error("Run Failed. See Terraform Log for details")
            }

            return results
        }
    }

}