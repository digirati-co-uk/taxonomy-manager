pipeline {
    agent {
        dockerfile {
            dir 'dockerfiles'
            filename 'Dockerfile.build'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    environment {
        IMAGE_CREDS_JENKINS_ID = 'aks-taxman'
        IMAGE_REGISTRY = 'taxman.azurecr.io'
        IMAGE_REPOSITORY = 'backend'
        DEPLOYMENT_JOB = '../digirati-taxonomy-manager-infra/master'
        DEPLOYMENT_ENV = 'dev'
    }

    options {
        ansiColor('xterm')
        timestamps()
    }

    stages {
        stage('general linting') {
            steps {
                sh 'pre-commit install'
                sh 'pre-commit run --all-files --verbose'
            }
        }

        stage('dockerfile linting') {
            agent {
                docker {
                    image 'hadolint/hadolint:latest-debian'
                }
            }
            steps {
                sh 'hadolint dockerfiles/* | tee -a hadolint_lint.txt'
            }
            post {
                always {
                    archiveArtifacts 'hadolint_lint.txt'
                }
            }
        }

        stage('build modules') {
            steps {
                script {
                    def workspace = env.WORKSPACE
                    sh "$workspace/gradlew -Pci=true clean generateTestKeyPair assemble check"
                }
            }
        }

        stage("generic code analysis") {
            steps {
                script {
                    def spotbugs = scanForIssues tool: [$class: 'SpotBugs', pattern: '**/build/reports/spotbugs/*.xml']
                    def checkstyle = scanForIssues tool: [$class: 'CheckStyle', pattern: '**/build/reports/checkstyle/*.xml']
                    def javac = scanForIssues tool: [$class: 'Java']

                    publishIssues issues: [javac]
                    publishIssues issues: [spotbugs]
                    publishIssues issues: [checkstyle]
                }
            }
        }

        stage('pull request code analysis') {
            when {
                expression { env.CHANGE_ID != null }
            }

            steps {
                withSonarQubeEnv('default') {
                    script {
                        def branchName = env.GIT_BRANCH.replaceAll("origin/", "")
                        def changeId = env.CHANGE_ID
                        def workspace = env.WORKSPACE

                        sh "$workspace/gradlew -Pci=true sonarqube -Dsonar.pullrequest.branch=$branchName -Dsonar.pullrequest.key=$changeId"
                    }
                }
            }
        }

        stage ('mainline code analysis') {
            when {
                branch "master"
            }

            steps {
                withSonarQubeEnv('default') {
                    script {
                        def workspace = env.WORKSPACE
                        sh "$workspace/gradlew -Pci=true sonarqube"
                    }
                }
            }
        }

        stage('build image') {
            steps {
                sh 'docker build -t $IMAGE_REPOSITORY:latest -f dockerfiles/Dockerfile.jvm .'
            }
        }

        stage('push image') {
            when {
                branch "master"
            }

            steps {
                withCredentials([usernamePassword(credentialsId: "$IMAGE_CREDS_JENKINS_ID", usernameVariable: 'IMAGE_REGISTRY_USERNAME', passwordVariable: 'IMAGE_REGISTRY_PASSWORD')]) {
                    sh 'docker login $IMAGE_REGISTRY --username $IMAGE_REGISTRY_USERNAME --password $IMAGE_REGISTRY_PASSWORD'
                }

                script {
                    def properties = readProperties(file: 'version.properties')
                    version = "${properties.version}-${currentBuild.startTimeInMillis}.${currentBuild.number}"
                    def images = [
                        "\$IMAGE_REGISTRY/\$IMAGE_REPOSITORY:$version",
                        "\$IMAGE_REGISTRY/\$IMAGE_REPOSITORY:latest"
                    ]

                    for (String image : images) {
                        sh "docker tag \$IMAGE_REPOSITORY:latest $image"
                        sh "docker push $image"
                    }
                }
            }
        }

        stage('deploy image') {
            when {
                branch "master"
            }

            steps {
                build job: '$DEPLOYMENT_JOB',
                      parameters:  [
                          [$class: 'StringParameterValue', name: 'ENVIRONMENT', value: '$DEPLOYMENT_ENV'],
                          [$class: 'StringParameterValue', name: 'BACKEND_IMAGE_TAG', value: "${version}"]
                      ],
                      propagate: true
            }
        }
    }

    post {
        always {
            junit '**/build/test-results/test/*.xml'
        }
    }
}
