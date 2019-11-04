build {
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
    sh(label: 'Gradle build', script: './gradlew -Pci=true assemble')
  }

  stage('Testing') {
    sh(label: 'Gradle assemble', script: './gradlew -Pci=true check')
  }

  stage('Analysis') {
    def spotbugs = scanForIssues tool: spotBugs(pattern: '**/build/reports/spotbugs/*.xml')
    def checkstyle = scanForIssues tool: checkstyle(pattern: '**/build/reports/checkstyle/*.xml')
    def javac = scanForIssues tool: [$class: 'Java']

    publishIssues issues: [javac, spotbugs, checkstyle]
  }

  stage('Deployment') {

  }
}

void build(Closure pipeline) {
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
