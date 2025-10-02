pipeline {

    agent {
        docker {
            image 'hdxt25/agent:v1'
            args '--user root -v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    stages {
        stage("build & test") {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Dependency-Check') {
            steps {
                sh 'mvn org.owasp:dependency-check-maven:check -DskipTests'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'target/dependency-check-report.html', fingerprint: true
                }
            }
        }
        stage('static code analysis') {
            environment {
                SONAR_URL = "http://3.144.131.253:9000"
            }
            steps {
                withCredentials([string(credentialsId: 'sonarqube', variable: 'SONAR_AUTH_TOKEN')]) {
                    sh 'mvn sonar:sonar -Dsonar.login=$SONAR_LOGIN -Dsonar.host.url=$SONAR_URL'
                }
            }
        }
        stage('Docker Build (Local Only)') {
            steps {
                sh """
                    docker buildx create --name mybuilder --use || true
                    docker buildx inspect --bootstrap

                    # Build single arch and load locally for scanning
                    docker buildx build \
                    --platform linux/amd64 \
                    -t ${DOCKER_IMAGE}:${GIT_COMMIT} \
                    --load .
                """
            }
        }
        stage('Run Trivy vulnerability scanner') {
            steps {
                sh """
                
                    # Install dependencies and Trivy in one go
                    apt-get update -y && \
                    apt-get install -y wget apt-transport-https gnupg lsb-release && \
                
                    # Add Trivy repo
                    wget -qO - https://aquasecurity.github.io/trivy-repo/deb/public.key | gpg --dearmor -o /usr/share/keyrings/trivy.gpg
                    echo "deb [signed-by=/usr/share/keyrings/trivy.gpg] https://aquasecurity.github.io/trivy-repo/deb $(lsb_release -sc) main" \
                        | tee /etc/apt/sources.list.d/trivy.list

                    # Update package index once more to include Trivy repo and install Trivy
                    apt-get update -y && apt-get install -y trivy

                    # Run Trivy scan on the staging Docker image
                    trivy image \
                        --exit-code 1 \
                        --severity HIGH,CRITICAL \
                        ${DOCKER_IMAGE}:${GIT_COMMIT}-scan
            
                """
            }
        }
        stage(build & push final docker image) {
            environment {
                DOCKER_IMAGE = "hdxt25/web-app-1:${GIT_COMMIT}"
            }
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId:'docker-cred',
                                                        usernameVariable: $DOCKER_USER,
                                                        passwordVariable: $DOCKER_PASS)]) {
                        sh """
                            
                            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                            # Create and use a new builder (if not exists)
                            docker buildx create --name mybuilder --use || true
                            docker buildx inspect --bootstrap

                            # Build and push multi-arch image
                            docker buildx build \
                                --platform linux/amd64,linux/arm64,linux/arm/v7 \
                                -t ${DOCKER_IMAGE}:${GIT_COMMIT} --push .
                            
                            docker logout
                                
                                

                            
                        """
                    }                                   
                }
            }
        }
        stage('Update Deployment File') {
            environment {
                GIT_REPO_NAME = "spring-boot-app"
                GIT_USER_NAME = "hdxt25"
            }
            steps {
                withCredentials([string(credentialsId: 'github', variable: 'GITHUB_TOKEN')]) {
                    sh """
                        # Configure Git
                        git config user.email "hdxt25@gmail.com"
                        git config user.name "Himanshu"
                        git config --global --add safe.directory $WORKSPACE

                        # Update deployment manifest with current build number
                        sed -i "s/replaceImageTag/${BUILD_NUMBER}/g" spring-boot-app-manifests/deployment.yml

                        # Commit & push changes
                        git add spring-boot-app-manifests/deployment.yml
                        git commit -m "Update deployment image to version ${BUILD_NUMBER}" || echo "No changes to commit"
                
                        # Push to private GitHub repo using token authentication
                        git push https://${GITHUB_TOKEN}@github.com/${GIT_USER_NAME}/${GIT_REPO_NAME}.git HEAD:main
                    """
                }    
            }
        }
    }
}

        



