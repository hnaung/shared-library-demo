// In vars/allInOne.groovy (shared library that defines the generic pipeline, upgraded to support existing images)
def call(Map config) { 

	node { 
     		def timeStamp = Calendar.getInstance().getTime().format('YYYYMMdd-hhmmss', TimeZone.getTimeZone('Europe/Paris'))
     		def buildId = "${config.imageVersion}-${timeStamp}"
		
		// Alway checkout the sources, as they may include tests
		stage('Checkout') {
			echo "Checking out the sources..." 
			checkout scm
		}

		if (config.existing == true) {
			stage('Docker pull') {
//				def buildId = "${config.imageVersion}"
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
