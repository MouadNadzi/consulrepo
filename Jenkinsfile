pipeline {
    agent any

    environment {
        APP_NAME = "ms_rest_template"
    }

    stages {

        stage('Test Docker') {
                    steps {
                        script {
                            sh '''
                                # Test Docker installation
                                echo "Testing Docker installation..."
                                docker --version

                                # List current Docker images
                                echo "\nListing Docker images..."
                                docker images

                                # List running containers
                                echo "\nListing running containers..."
                                docker ps
                            '''
                        }
                    }
                }
        stage('Build and Deploy') {
            steps {
                script {
                    // Using sh for Linux-based Jenkins container
                    sh '''
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
                    sh '''
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