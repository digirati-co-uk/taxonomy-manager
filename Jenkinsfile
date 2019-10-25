node {

    def config = [
        tagCommit: isMasterBuild(),
        deployImage: isMasterBuild() || env.TAG_NAME,
        registryUrl: "taxman.azurecr.io",
        repositoryName: "backend",
        deploymentJob: '../digirati-taxonomy-manager-infra/master',
        deploymentEnv: 'dev',
        gitCommiterEmail: "digirati-ci@digirati.com",
        gitCommiterUsername: "digirati-ci",
    ]

    stage('checkout scm') {
        checkoutScm()
    }

    def buildImage

    stage('build build image') {
        buildImage = buildBuildImage()
    }

    stage("initialise git config") {
        buildImage.inside("-v /var/run/docker.sock:/var/run/docker.sock") {
          initialiseGitConfig(config.gitCommiterEmail, config.gitCommiterUsername)
        }
    }

    stage('general linting') {
        buildImage.inside("-v /var/run/docker.sock:/var/run/docker.sock") {
            generalLinting()
        }
    }

    stage('dockerfile linting') {
        docker.image('hadolint/hadolint:latest-debian').inside {
            dockerfileLinting()
        }
    }

    stage('build modules') {
        buildImage.inside("-v /var/run/docker.sock:/var/run/docker.sock") {
            buildModules()
        }
    }

    stage('generic code analysis') {
        buildImage.inside("-v /var/run/docker.sock:/var/run/docker.sock") {
            genericCodeAnalysis()
        }
    }

    if (env.CHANGE_ID != null) {
        stage('pull request code analysis') {
            withSonarQubeEnv('default') {
                buildImage.inside("-v /var/run/docker.sock:/var/run/docker.sock") {
                    pullRequestCodeAnalysis()
                }
            }
        }
    }

    if (isMasterBuild()) {
        stage('mainline code analysis') {
            withSonarQubeEnv('default') {
                buildImage.inside("-v /var/run/docker.sock:/var/run/docker.sock") {
                    mainlineCodeAnalysis()
                }
            }
        }
    }

    stage('build backend image') {
        buildImage.inside("-v /var/run/docker.sock:/var/run/docker.sock") {
            buildBackendImage(config.repositoryName)
        }
    }

    def tagVersion = fetchTagVersion()

    if (config.tagCommit) {
        stage('create git tag') {
            buildImage.inside("-v /var/run/docker.sock:/var/run/docker.sock") {
                createGitTag(tagVersion)
            }
        }
    }

    stage('push image') {
        withCredentials([usernamePassword(credentialsId: "aks-taxman", usernameVariable: 'registryUsername', passwordVariable: 'registryPassword')]) {
            buildImage.inside("-v /var/run/docker.sock:/var/run/docker.sock") {
                pushImage(config.registryUrl, registryUsername, registryPassword, config.repositoryName, tagVersion)
            }
        }
    }

    if (config.deployImage) {
        stage('deploy image') {
            buildImage.inside("-v /var/run/docker.sock:/var/run/docker.sock") {
                deployImage(config.deploymentJob, config.deploymentEnv, tagVersion)
            }
        }
    }
}

def initialiseGitConfig(def commiterEmail, def commiterUsername) {
  sh "git config --global user.email ${commiterEmail}"
  sh "git config --global user.name ${commiterUsername}"
}

def checkoutScm() {
    checkout scm
}

def buildBuildImage() {
    return docker.build("taxonomy-manager-infra-build", "-f dockerfiles/Dockerfile.build .")
}

def generalLinting() {
    sh 'pre-commit install'
    sh 'pre-commit run --all-files --verbose'
}

def dockerfileLinting() {
    try {
        sh 'hadolint dockerfiles/* | tee -a hadolint_lint.txt'
    }
    finally {
        archiveArtifacts 'hadolint_lint.txt'
    }
}

def buildModules() {
    def workspace = env.WORKSPACE
    sh "$workspace/gradlew -Pci=true clean generateTestKeyPair build"
}

def genericCodeAnalysis() {
    def spotbugs = scanForIssues tool: [$class: 'SpotBugs', pattern: '**/build/reports/spotbugs/*.xml']
    def checkstyle = scanForIssues tool: [$class: 'CheckStyle', pattern: '**/build/reports/checkstyle/*.xml']
    def javac = scanForIssues tool: [$class: 'Java']

    publishIssues issues: [javac]
    publishIssues issues: [spotbugs]
    publishIssues issues: [checkstyle]
}

def pullRequestCodeAnalysis() {
    def branchName = env.BRANCH_NAME
    def changeId = env.CHANGE_ID
    def workspace = env.WORKSPACE

    sh "$workspace/gradlew -Pci=true sonarqube -Dsonar.pullrequest.branch=${branchName} -Dsonar.pullrequest.key=${changeId}"
}

def mainlineCodeAnalysis() {
    def workspace = env.WORKSPACE
    sh "$workspace/gradlew -Pci=true sonarqube"
}

def buildBackendImage(def repositoryName) {
    sh "docker build -t \"${repositoryName}:latest\" -f \"dockerfiles/Dockerfile.jvm\" ."
}

def isMasterBuild() {
    return env.BRANCH_NAME == 'master'
}

def fetchTagVersion() {
    if (isMasterBuild()) {
        def properties = readProperties(file: 'version.properties')
        return "${properties.version}-${currentBuild.startTimeInMillis}.${currentBuild.number}"
    }

    if (env.TAG_NAME) {
        return env.TAG_NAME
    }

    return sh(returnStdout: true, script: "echo ${env.BRANCH_NAME} | sed -e \"s/\\//-/g\"").trim()
}

def createGitTag(def tagVersion) {
    sh "mkdir -p  ~/.ssh/ && ssh-keyscan github.com | tee -a ~/.ssh/known_hosts"
    sshagent(['github-ssh']) {
        sh "git tag -a ${tagVersion} -m 'Automatic RC tag ${tagVersion}'"
        sh "git push origin ${tagVersion}"
    }
}

def pushImage(def registryUrl, def registryUsername, def registryPassword, def repositoryName, def tagVersion) {
    sh "docker login \"https://${registryUrl}\" --username \"${registryUsername}\" --password \"${registryPassword}\""
    sh "docker tag \"${repositoryName}:latest\" \"${registryUrl}/${repositoryName}:${tagVersion}\""
    sh "docker push \"${registryUrl}/${repositoryName}:${tagVersion}\""
}

def deployImage(def deploymentJob, def deploymentEnv, def tagVersion) {
    build job: deploymentJob,
        parameters:  [
            booleanParam(name: 'DEPLOY', value: true),
            stringParam(name: 'ENVIRONMENT', value: deploymentEnv),
            stringParam(name: 'BACKEND_IMAGE_TAG', value: tagVersion)
        ],
        propagate: true
}
