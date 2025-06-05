pipeline {
    agent {
        label 'agent01'
    }
    stages {
        stage('Source') {
            steps { 
                echo 'git jacobovirtual/unir-cicd.git'
                git 'https://github.com/jacobovirtual/unir-cicd.git'
            }
        }
        stage('Build') {
            steps {
                echo 'Building!'
                sh 'make build'
            }
        }
        stage('Unit tests') {
            steps { 
                sh 'make test-unit'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('Api tests') {
            steps { 
                sh 'make test-api'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
        stage('E2E tests') {
            steps { 
                sh 'make test-e2e'
                archiveArtifacts artifacts: 'results/*.xml'
            }
        }
    }
    post {
        always {
            junit 'results/*_result.xml'
        }
        failure {
            emailext (
                to: 'jacobo.garcia654@comunidadunir.net', 
                replyTo: 'jacobo.garcia654@comunidadunir.net', 
                subject: "Error pipeline", 
                body:  "Job '${JOB_NAME}' (${BUILD_NUMBER}) is waiting for input", 
                mimeType: 'text/html'
            )
            echo  "Job '${JOB_NAME}' (${BUILD_NUMBER}) is waiting for input"
            cleanWs()
        }
    }
}