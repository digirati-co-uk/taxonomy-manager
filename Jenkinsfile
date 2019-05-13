pipeline {
    agent {
        docker {
            image 'gradle:5.4.1-jdk11'
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
                sh 'gradle clean build'
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

                        sh "gradle sonarqube -Dsonar.pullrequest.branch=$branchName -Dsonar.pullrequest.key=$changeId"
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
                    sh "gradle sonarqube"
                }
            }
        }
    }
}
