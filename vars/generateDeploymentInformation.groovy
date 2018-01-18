import groovy.json.JsonOutput

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    stage ('Setting Up Deployment File') {
        
        def deployment_map = withCredentials(
            [
                [ // Azure Credentials  - Hard Coded for POC
                    $class: 'AzureCredentialsBinding', 
                    credentialsId: 'NProd-POC-Tenant',
                    clientIdVariable: "AZURE_CLIENT_ID", 
                    clientSecretVariable: 'AZURE_CLIENT_SECRET', 
                    subscriptionIdVariable: 'AZURE_SUBSCRIPTION_ID', 
                    tenantIdVariable: 'AZURE_TENANT_ID'
                ],
                [ // Terraform Enterprise 2 Credentials
                    $class: 'StringBinding', 
                    credentialsId: 'Terraform_Enterprise_POC_Token',
                    variable: 'TERRAFORM_SECRET'
                ]
                [ // Terraform Enterprise 2 Credentials
                    $class: 'StringBinding', 
                    credentialsId: 'Terraform_Enterprise_POC_Token',
                    variable: 'TERRAFORM_SECRET'
                ]
                [ // Terraform Enterprise 2 Credentials
                    $class: 'UsernamePasswordMultiBinding', 
                    credentialsId: 'jenkins_servicenow',
                    usernameVariable: 'JENKINS_SNOW_USERNAME',
                    passwordVariable: 'JENKINS_SNOW_PASSWORD'
                ]
            ]
        ) {
            def credentialMap = [:]
            
            credentialMap['deployment']['id'] = config.app_id
            credentialMap['deployment']['component_name'] = config.app_component_name
            credentialMap['deployment']['environment'] = config.app_environment

            credentialMap['consul']['consul'] = "consul.australiaeast.cloudapp.azure.com"
            credentialMap['consul']['port'] = "8500"
            credentialMap['consul']['dc'] = "australiaeast"
            credentialMap['consul']['token'] = ""

            credentialMap['service_now']['url'] = "https://wbchpaaspoc.service-now.com/api/now/table/change_request"
            credentialMap['service_now']['username'] = env.JENKINS_SNOW_USERNAME
            credentialMap['service_now']['password'] = env.JENKINS_SNOW_PASSWORD

            credentialMap['azure_secret'] = env.AZURE_CLIENT_SECRET
            credentialMap['atlas_secret'] = env.TERRAFORM_SECRET
        
            return deployment_map
        }
        def jsonMap = readJSON text: JsonOutput.toJson(deployment_map)
        return jsonMap
    }
}
