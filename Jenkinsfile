pipeline {
    agent {
        dockerfile {
            dir 'dockerfiles'
            filename 'Dockerfile.build'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    environment {
        IMAGE_AKS_REGISTRY = 'taxman.azurecr.io'
        IMAGE_AKS_REPOSITORY = 'backend'
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
                sh 'docker build -t $IMAGE_AKS_REPOSITORY:latest -f dockerfiles/Dockerfile.jvm .'
            }
        }

        stage('push image') {
            //when {
            //    branch "master"
            //}

            steps {
                withCredentials([usernamePassword(credentialsId: 'aks-taxman', usernameVariable: 'AKS_USERNAME', passwordVariable: 'AKS_PASSWORD')]) {
                    sh 'docker login $IMAGE_AKS_REGISTRY --username $AKS_USERNAME --password $AKS_PASSWORD'
                }

                script {
                    def properties = readProperties(file: 'version.properties')
                    version = "${properties.version}-${currentBuild.startTimeInMillis}.${currentBuild.number}"
                    def images = [
                        "\$IMAGE_AKS_REGISTRY/\$IMAGE_AKS_REPOSITORY:$version",
                        "\$IMAGE_AKS_REGISTRY/\$IMAGE_AKS_REPOSITORY:latest"
                    ]

                    for (String image : images) {
                        sh "docker tag \$IMAGE_AKS_REPOSITORY:latest $image"
                        sh "docker push $image"
                    }
                }
            }
        }

        stage('deploy image') {
            steps {
                build job: '../digirati-taxonomy-manager-infra/PR-3',
                      parameters:  [
                          [$class: 'StringParameterValue', name: 'ENVIRONMENT', value: 'dev'],
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
