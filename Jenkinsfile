#!groovy

def buildImage

// Set build properties
properties([[$class: 'GithubProjectProperty',
               displayName: 'Ciste',
               projectUrlStr: 'https://github.com/duck1123/ciste/']]);

stage('Prepare environment') {
    node('docker') {
        buildImage = docker.image('clojure')
        buildImage.pull()

        sh 'env | sort'
    }
}

stage('Unit Tests') {
    node('docker') {
        buildImage.inside {
            checkout scm

            wrap([$class: 'AnsiColorBuildWrapper']) {
                sh 'lein midje'
            }

            junit 'target/surefire-reports/TEST-*.xml'
        }
    }
}

stage('Generate Reports') {
    node('docker') {
        buildImage.inside {
            checkout scm
            sh 'lein doc'
            step([$class: 'JavadocArchiver', javadocDir: 'doc', keepAll: true])
            step([$class: 'TasksPublisher', high: 'FIXME', normal: 'TODO', pattern: '**/*.clj,**/*.cljs'])
        }
    }
}

// TODO: Skip for features and PRs
stage('Deploy Artifacts') {
    node('docker') {
        buildImage.inside {
            withCredentials([[$class: 'UsernamePasswordMultiBinding',
                                credentialsId: 'repo-creds',
                                usernameVariable: 'REPO_USERNAME', passwordVariable: 'REPO_PASSWORD']]) {
                sh 'lein deploy'
            }
        }
    }
}
