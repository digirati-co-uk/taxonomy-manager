pipeline {
    agent {
        dockerfile {
            filename 'Dockerfile.build'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    stages {
        stage('linting') {
            steps {
                sh 'pre-commit install'
                sh 'pre-commit run --all-files --verbose'
            }
        }

        stage('build modules') {
            steps {
                script {
                    def workspace = env.WORKSPACE
                    sh "$workspace/gradlew clean build"
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

                        sh "$workspace/gradlew sonarqube -Dsonar.pullrequest.branch=$branchName -Dsonar.pullrequest.key=$changeId"
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
                        sh "$workspace/gradlew sonarqube"
                    }
                }
            }
        }
    }
}
