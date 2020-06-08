runBuild {
  def repositoryName = "backend"
  def registryUrl = "taxman.azurecr.io"
  def deploymentJob = "../deploy/master"
  def deploymentEnv = 'dev'
  def deploymentCustomer = 'digirati'
  def tagVersion = fetchTagVersion()

  stage('Linting') {
    parallel(
      precommit: {
        sh('pre-commit run --all-files --verbose')
      },
      hadolint: {
        sh('hadolint dockerfiles/Dockerfile.jvm dockerfiles/Dockerfile.build')
      },
      failFast: true
    )
  }

  stage('Build') {
    gradle('assemble')
  }

  stage('Analysis') {
    def spotbugs = scanForIssues tool: spotBugs(pattern: '**/build/reports/spotbugs/*.xml')
    def checkstyle = scanForIssues tool: checkStyle(pattern: '**/build/reports/checkstyle/*.xml')
    def javac = scanForIssues tool: java()

    publishIssues issues: [javac, spotbugs, checkstyle]

    def sonarArgs = ""
    if (env.CHANGE_ID != null) {
      sonarArgs += "-Dsonar.pullrequest.branch=${env.BRANCH_NAME} -Dsonar.pullrequest.key=${env.CHANGE_ID}"
    }
  }

  stage("Publishing") {
    withCredentials([usernamePassword(credentialsId: "jenkins-taxman-acr", usernameVariable: 'registryUsername', passwordVariable: 'registryPassword')]) {
      sh(label: 'Build container image', script: """
       docker build -t "${repositoryName}:latest" -f "dockerfiles/Dockerfile.jvm" .
       docker tag "$repositoryName:latest" "$registryUrl/$repositoryName:$tagVersion"
       docker login "https://${registryUrl}" --username "${registryUsername}" --password "${registryPassword}"
       docker push "$registryUrl/$repositoryName:$tagVersion"
    """)
    }
  }

  stage('Deployment') {
    if (deploy()) {
      /*
       Prevent older, slower builds from deploying after the latest build.
       */
      milestone(label: "DEPLOYMENT_MILESTONE")

      build(job: deploymentJob,
        parameters: [
          booleanParam(name: 'DEPLOY', value: true),
          stringParam(name: 'ENVIRONMENT', value: deploymentEnv),
          stringParam(name: 'CUSTOMER', value: deploymentCustomer),
          stringParam(name: 'BACKEND_IMAGE_TAG', value: tagVersion)
        ],
        propagate: false,
        wait: false
      )
    }
  }
}

def fetchTagVersion() {
  if (env.BRANCH_NAME == 'master') {
    def properties = readProperties(file: 'version.properties')
    return "${properties.version}-${currentBuild.startTimeInMillis}.${currentBuild.number}"
  }

  if (env.TAG_NAME) {
    return env.TAG_NAME
  }

  return sh(returnStdout: true, script: "echo ${env.BRANCH_NAME} | sed -e \"s/\\//-/g\"").trim()
}

boolean deploy() {
  return env.BRANCH_NAME == 'master' || env.TAG_NAME
}

void gradle(String args) {
  sh(label: "Gradle ${args}", script: "./gradlew -Pci=true $args")
}

void runBuild(Closure pipeline) {
  node('linux') {
    container('buildkit') {
      checkout(scm)

      stage("Setup") {
        sh """
            git config --global user.email "digirati-ci@digirati.com"; git config --global user.name "digirati-ci";
          """
      }

      pipeline()
    }

  }
}
