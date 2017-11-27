def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    node {

        stage ('Terraform Action') {

            print "Begin Terraform: ${config.request_type}" 

            // Download Terraform Helper Scripts
            print 'echo "Getting Terraform Scripts"'
            dir('helper_scripts') {
                    git url: 'https://github.com/westpac-cloud-engineering/terraform-pipeline-wrapper'
            }

            // Trigger Run
            sh 'pip3 install -r helper_scripts/requirements.txt'
            sh "set +e; python3 helper_scripts/run_terraform_apply.py \
            \'${config.request_type}\' \
            \'${config.app_id}\' \
            \'${config.component_name}\' \
            \'${config.environment}\' \
            \'${config.atlas_token}\' \
            \'${config.destroy}\' \
            \'${config.run_id}\' " 

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
                print "Failed Run!"
                currentBuild.result = 'FAILURE'
            }

            return results
        }
    }

}