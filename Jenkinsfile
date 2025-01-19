pipeline {
    agent any

    environment {
        APP_NAME = "ms_rest_template"  // Matching your current image prefix
    }

    stages {
        stage('Build and Deploy') {
            steps {
                script {
                    // Using PowerShell to execute Docker commands
                    powershell '''
                        # Build services
                        docker-compose build gateway-service client-service car-service

                        # Stop and remove existing containers (if any)
                        docker-compose down

                        # Start services
                        docker-compose up -d --force-recreate
                    '''
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                script {
                    powershell '''
                        # Check if containers are running
                        docker ps --format "table {{.Names}}\t{{.Status}}"
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed. Please check the logs.'
        }
    }
}