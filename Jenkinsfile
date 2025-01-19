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
                                        # Test Docker installation using full path (if needed)
                                        #ls -l /usr/bin/docker
                                        whoami
                                        ps aux | grep jenkins
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

        stage('Deploy Services') {
            steps {
                script {
                    // Using sh for Linux-based Jenkins container
                    sh '''
                        # Go to the directory containing docker-compose.yml (if needed)
                        # cd your-project-directory

                        # Stop and remove existing containers (if any)
                        docker-compose down

                        # Start services using pre-built images defined in docker-compose.yml
                        docker-compose up -d
                    '''
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                script {
                    sh '''
                        # Check if containers are running
                        echo "Containers currently running:"
                        docker ps --format "table {{.Names}}\t{{.Status}}"

                        # Optional: Add more specific checks here, such as:

                        # Check if a specific service is running:
                        # if ! docker ps -f name=client-service --format "{{.Names}}" | grep -q client-service; then
                        #     echo "Error: client-service is not running!"
                        #     exit 1
                        # fi

                        # Check health endpoints (if you have Spring Actuator or other health checks):
                        # if ! curl -s http://localhost:8088/actuator/health | grep -q '"status":"UP"'; then
                        #     echo "Error: client-service health check failed!"
                        #     exit 1
                        # fi
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