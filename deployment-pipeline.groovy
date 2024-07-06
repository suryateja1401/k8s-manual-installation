def imagename = "suryateja1401/dvsbatch4"
def deployment = "javaapp"
def namespace = "javaapp"
def ContainerName = "javaapp"

pipeline {
    agent {
        kubernetes {
            cloud "Kubernetes"
            defaultContainer "kubectl"
            label "maven"
        }
    }
    parameters {
        choice(name: 'ENV', choices: ['dev', 'prod'], description: 'Environment to deploy')
        string(name: 'VERSION', defaultValue: '0.0.0', description: 'Version to deploy')
    }
    stages {
        stage('Printing Inputs') {
            steps {
                script {
                    println "Env is ${params.ENV}, version selected is ${params.VERSION}"
                    println "Selected namespace is ${namespace}-${params.ENV}"
                }
            }
        }
        stage('Patch Deployment') {
            steps {
                script {
                    withKubeConfig([credentialsId: 'k8s-access', serverUrl: 'https://172.31.56.153:6443']) {
                        sh """
                        kubectl set image deployment/${deployment} ${ContainerName}=${imagename}:${params.VERSION} -n ${namespace}-${params.ENV}
                        """
                    }
                }
            }
        }
        stage('Restart Deployment') {
            steps {
                script {
                    withKubeConfig([credentialsId: 'k8s-access', serverUrl: 'https://172.31.56.153:6443']) {
                        sh """
                        kubectl rollout restart deployment ${deployment} -n ${namespace}-${params.ENV}
                        """
                    }
                }
            }
        }
    }
}
