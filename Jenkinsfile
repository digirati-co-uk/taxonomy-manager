pipeline {
    agent {
        dockerfile {
            dir 'dockerfiles'
            filename 'Dockerfile.build'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    environment {
        RELEASE_TAG_REGEX = /^\d+\.\d+\.\d+$/
        RC_TAG_REGEX = /^\d+\.\d+\.\d+-.+$/
        GIT_COMMITER_EMAIL = 'digirati-ci@digirati.com'
        GIT_COMMITER_USERNAME = 'digirati-ci'
        GITHUB_REPO_PATH = 'digirati-co-uk/taxonomy-manager'
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
        stage('abort if release candidate tag') {
            when {
                tag pattern: RC_TAG_REGEX, comparator: 'REGEXP'
            }

            steps {
                script {
                    echo 'Release candidate build detected, quietly aborting...'
                    currentBuild.result = 'SUCCESS'
                    return
                }
            }
        }

        stage('initialise git config') {
            steps {
                sh("git config user.email '${GIT_COMMITER_EMAIL}'")
                sh("git config user.name '${GIT_COMMITER_USERNAME}'")
            }
        }

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

        stage('determine release candidate tag') {
            when {
                branch 'master'
            }

            steps {
                script {
                    def properties = readProperties(file: 'version.properties')
                    tagVersion = "${properties.version}-${currentBuild.startTimeInMillis}.${currentBuild.number}"
                }
            }
        }

        stage('determine release tag') {
            when {
                tag pattern: RELEASE_TAG_REGEX, comparator: 'REGEXP'
            }

            steps {
                script {
                    tagVersion = sh (returnStdout: true, script: "git tag --points-at HEAD").trim()
                }
            }
        }

        stage('create github release') {
            when {
                anyOf {
                    branch 'master'
                    tag pattern: RELEASE_TAG_REGEX, comparator: 'REGEXP'
                }
            }

            steps {
                withCredentials([usernamePassword(credentialsId: 'github-token', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                    script {
                        def createReleaseResponse = sh (
                                returnStdout: true,
                                script:
                                        """
                            curl -u '${GIT_USERNAME}:${GIT_PASSWORD}' \
                                 -H 'Content-Type:application/json' \
                                 'https://api.github.com/repos/${GITHUB_REPO_PATH}/releases' \
                                 -d '{
                                         "tag_name": "${tagVersion}",
                                         "target_commitish": "$GIT_COMMIT",
                                         "name": "${tagVersion}",
                                         "prerelease": ${tagVersion ==~ RC_TAG_REGEX}
                                     }'
                            """
                        )
                    }
                }
            }
        }

        stage('push image') {
            when {
                anyOf {
                    branch 'master'
                    tag pattern: RELEASE_TAG_REGEX, comparator: 'REGEXP'
                }
            }

            steps {
                withCredentials([usernamePassword(credentialsId: "$IMAGE_CREDS_JENKINS_ID", usernameVariable: 'IMAGE_REGISTRY_USERNAME', passwordVariable: 'IMAGE_REGISTRY_PASSWORD')]) {
                    sh 'docker login $IMAGE_REGISTRY --username $IMAGE_REGISTRY_USERNAME --password $IMAGE_REGISTRY_PASSWORD'
                    sh "docker tag \$IMAGE_REPOSITORY:latest \$IMAGE_REGISTRY/\$IMAGE_REPOSITORY:${tagVersion}"
                    sh "docker push \$IMAGE_REGISTRY/\$IMAGE_REPOSITORY:${tagVersion}"
                }
            }
        }

        stage('deploy image') {
            when {
                anyOf {
                    branch 'master'
                    tag pattern: RELEASE_TAG_REGEX, comparator: 'REGEXP'
                }
            }

            steps {
                build job: DEPLOYMENT_JOB,
                    parameters:  [
                        stringParam(name: 'ENVIRONMENT', value: DEPLOYMENT_ENV),
                        stringParam(name: 'FRONTEND_VERSION', value: tagVersion)
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
