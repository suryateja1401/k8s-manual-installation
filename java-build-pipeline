pipeline {
    agent {
        kubernetes {
            inheritFrom 'maven'
            defaultContainer 'kaniko'
        }
    }
    stages {
        stage('git check out') {
            steps {
                script {
                    checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: 'github-access', url: 'https://github.com/suryateja1401/javaspringboot.git']])
                }
            }
        }

        stage('read version') {
            steps {
                script {
                    pom = readMavenPom file: 'pom.xml'
                    image_version = pom.version
                    println "my pom version is ${image_version}"
                }
            }
        }

        stage('build and push image') {
            environment {
                PATH = "/busybox:$PATH"
                REGISTRY = 'index.docker.io'
                REPOSITORY = 'suryateja1401'
                IMAGE = 'dvsbatch4'
                TAG = "${image_version}"
            }
            steps {
                script {
                    sh """
                    /kaniko/executor -f `pwd`/Dockerfile -c `pwd` --cache=true --destination=${REGISTRY}/${REPOSITORY}/${IMAGE}:${TAG}
                    """
                }
            }
        }
    }
}
