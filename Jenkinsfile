pipeline {
    agent any // Or a specific agent with Docker and Docker Compose

    environment {
        DOCKER_REGISTRY = "" // Your Docker registry (e.g., "myregistry.com" or leave empty for Docker Hub)
        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials' // Credentials ID for your Docker registry (if needed)
        APP_NAME = "my-microservices-app" // A name for your application
        TARGET_SERVER = "" // SSH connection string (user@host) for remote deployment (if needed)
    }

    stages {
        stage('Build and Push') {
            when {
                branch 'main' // Trigger on pushes to the main branch (adjust as needed)
            }
            steps {
                script {
                    // Build images using Docker Compose
                    sh "docker-compose build gateway-service client-service car-service"

                    // Tag images (if using a registry)
                    if (env.DOCKER_REGISTRY) {
                        sh "docker tag gateway-service:latest ${DOCKER_REGISTRY}/${APP_NAME}-gateway:latest"
                        sh "docker tag client-service:latest ${DOCKER_REGISTRY}/${APP_NAME}-client:latest"
                        sh "docker tag car-service:latest ${DOCKER_REGISTRY}/${APP_NAME}-car:latest"
                    }

                    // Authenticate with the registry (if needed)
                    if (env.DOCKER_REGISTRY) {
                        docker.withRegistry("https://${DOCKER_REGISTRY}", DOCKER_CREDENTIALS_ID) {
                            // Push images
                            sh "docker push ${DOCKER_REGISTRY}/${APP_NAME}-gateway:latest"
                            sh "docker push ${DOCKER_REGISTRY}/${APP_NAME}-client:latest"
                            sh "docker push ${DOCKER_REGISTRY}/${APP_NAME}-car:latest"
                        }
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Deploy using Docker Compose (either locally or remotely)
                    if (env.TARGET_SERVER) {
                        // Remote deployment via SSH
                        sshagent(['your-ssh-credentials']) { // Use your SSH credentials ID
                            sh """
                                ssh ${TARGET_SERVER} '
                                    cd /path/to/your/app/on/target/server
                                    docker-compose pull
                                    docker-compose up -d --force-recreate
                                '
                            """
                        }
                    } else {
                        // Local deployment (on the same server as Jenkins)
                        sh """
                            docker-compose pull
                            docker-compose up -d --force-recreate
                        """
                    }
                }
            }
        }
    }
}