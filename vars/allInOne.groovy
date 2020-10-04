def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    node {
	    // Clean workspace before doing anything
	    deleteDir()
	    try {
		if (config.existing == true) {
			stage('Docker pull') {
//				def buildId = "${config.imageVersion}-${timeStamp}"
				sh 'docker pull "${config.imageName}:${buildId}"'
			}
		}

		if (config.existing != true) {
			stage('Build Image') {
				// Enforce the shape of the repository and assume it is always under image/
				sh 'docker build -t "${config.imageName}:${buildId}" image/'
			}
		}

		stage('Project tests') {
			def scriptFileContent = libraryResource( 'execute-project-tests.sh' )
			sh scriptFileContent
		}

		stage('Security checks') {
			echo "Checking security..."
			securityInspection( "${config.imageName}", "${buildId}" )
		}

		stage('Software Governance') {
			echo "Handling Software checks..."
			softwareCheck( "${config.imageName}", "${buildId}" )
		}

		stage('Promotion') {
			echo "Promoting the local image to a trusted repository..."
			def scriptFileContent = libraryResource( 'promote-image.sh' )
			sh scriptFileContent
		}
	} 
}
