pipeline {
    agent {
        kubernetes {
            label 'maven'
            defaultContainer 'maven'

        }
    }
    stages {
        stage ('git checkout') {
            steps {
                script {
                    checkout scmGit(branches: [[name: '*/release']], extensions: [], userRemoteConfigs: [[credentialsId: 'github-access', url: 'https://github.com/suryateja1401/javaspringboot.git']])

            }
        }
    }
        stage ('code build verification') {
            steps {
                script {
                    sh """
                    mvn clean install
                    cp -rf ./target /tmp/mydata
                    ls -l /tmp/mydata
                    """
            }
        }
    }
        stage('code quality checks') {
            steps {
                container('sonar-cli') {
                    script {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh """
                            sonar-scanner \
                            -Dsonar.projectKey="vtalent-devops_vtalent-devops-java"  \
                            -Dsonar.sources="/tmp/mydata/target/"   \
                            -Dsonar.host.url="https://sonarcloud.io" \
                            -Dsonar.branch.target="master" \
                            -Dsonar.branch.name="release" \
                            -Dsonar.login="$SONAR_TOKEN" \
                            -Dsonar.organization="vtalent-devops"
                            """
                            
                }
            }
        }
    }
}


}
}
