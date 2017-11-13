// TEMPORARY CREDENTIAL GATHERER FOR THE TERRAFORM ENTERPRISE 2 POC
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
            credentialMap['environment_variables'] = [:]
            credentialMap['environment_variables']['ARM_CLIENT_ID'] = env.AZURE_CLIENT_ID
            credentialMap['environment_variables']['ARM_CLIENT_SECRET'] = env.AZURE_CLIENT_SECRET
            credentialMap['environment_variables']['ARM_SUBSCRIPTION_ID'] = env.AZURE_SUBSCRIPTION_ID
            credentialMap['environment_variables']['ARM_TENANT_ID'] = env.AZURE_TENANT_ID
            credentialMap['atlas_token'] = env.TERRAFORM_SECRET
            
            return credentialMap
        }
        print(credentialMap)
        return credentialMap
    }
}
