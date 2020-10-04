import groovy.json.JsonBuilder

def call(Map config) { 
    node {
	    // Clean workspace before doing anything
	    deleteDir()

	    try {
	        stage ('Clone') {
	        	checkout scm
	        }
			stage('Build Image') {
				// Enforce the shape of the repository and assume it is always under image/      

				sh 'docker build -t "${config.imageName}:${buildId}" image/'
			}
	        stage ('Tests') {
		        parallel 'static': {
		            sh "echo 'shell scripts to run static tests...'"
		        },
		        'unit': {
		            sh "echo 'shell scripts to run unit tests...'"
		        },
		        'integration': {
		            sh "echo 'shell scripts to run integration tests...'"
		        }
	        }
	      	stage ('Deploy') {
	            sh "echo 'deploying to server ${config.serverDomain}...'"
	      	}
	    } catch (err) {
	        currentBuild.result = 'FAILED'
	        throw err
	    }
    }
}