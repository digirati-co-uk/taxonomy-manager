runBuild {
  stage('Linting') {
    parallel(
      precommit: {
        sh('pre-commit run --all-files --verbose')
      },
      hadolint: {
        docker.image('hadolint/hadolint:latest-debian').inside {
          sh('hadolint dockerfiles/Dockerfile.jvm dockerfiles/Dockerfile.build')
        }
      },
      failFast: true
    )
  }

  stage('Build') {
    gradle('assemble')
  }

  stage('Testing') {
    gradle('check')
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

    gradle("sonarqube ${sonarArgs}")
  }

  stage("Publishing") {
    sh(label: 'Build container image', """
       docker build -t "${repositoryName}:latest" -f "dockerfiles/Dockerfile.jvm" .
    """)
  }

  stage('Deployment') {
    if (deploy()) {
      /*
       Prevent older, slower builds from deploying after the latest build.
       */
      milestone(label: "DEPLOYMENT_MILESTONE")

// @todo - save tag version
//      build(job: deploymentJob,
//        parameters: [
//          booleanParam(name: 'DEPLOY', value: true),
//          stringParam(name: 'ENVIRONMENT', value: deploymentEnv),
//          stringParam(name: 'BACKEND_IMAGE_TAG', value: tagVersion)
//        ],
//        propagate: false,
//        wait: false
//      )
    }
  }
}

boolean deploy() {
  return env.BRANCH_NAME == 'master' || env.TAG_NAME
}

void gradle(String args) {
  sh(label: "Gradle ${args}", script: "./gradlew -Pci=true $args")
}

void runBuild(Closure pipeline) {
  node {
    checkout(scm)

    def image = docker.build("taxonomy-manager-infra-build", "-f dockerfiles/Dockerfile.build .")
    def args = [
      '-v', '/var/run/docker.sock:/var/run/docker.sock',
      '-v', '$HOME/.m2:/root/.m2',
      '-v', '$HOME/.gradle:/root/.gradle'
    ]

    image.inside(args.join(" ")) {
      stage("Setup") {
        sh """
          git config --global user.email "digirati-ci@digirati.com"; git config --global user.name "digirati-ci";
        """
      }

      pipeline()
    }
  }
}
