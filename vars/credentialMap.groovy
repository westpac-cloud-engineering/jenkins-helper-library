import groovy.json.JsonOutput

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    stage ('Get Credentials') {
        
        def credentialMap = withCredentials(
            [
                [ // Azure Credentials
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
            ]
        ) {
            def credentialMap = [:]
            credentialMap['azure_client_secret'] = env.AZURE_CLIENT_SECRET
            credentialMap['atlas_token'] = env.TERRAFORM_SECRET
            
            return credentialMap
        }
        def jsonMap = readJSON text: JsonOutput.toJson(credentialMap)
        return jsonMap
    }
}
