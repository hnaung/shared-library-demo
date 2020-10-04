#!/bin/sh


# Checks that the given error code is zero. If not, exits the script after printing the given error message.
# @param 1    the return code to check.
# @param 2    the error message to display in case the given error code is non-zero.
# @stdout     nothing in case of success, the given error message otherwise.
ensureSuccess() {
	if [[ $1 -ne 0 ]]; then
		echo
		echo "Error: $2"
		echo "ABORTING!"
		exit 1
	fi
}


# We assume registry credentials are available as environments variables
# (but you can find other solutions).
echo "Pushing the image ${config.imageName}:${buildId}..."
docker login -u=${DOCKER_REGISTRY_USER} -p=${DOCKER_REGISTRY_PWD}
docker push "${config.imageName}:${buildId}"
docker logout

ensureSuccess $? "Failed to push the Docker image."

# FIXME: tag the Git repository?

# Delete all the local images then.
# For performances reasons, it may help to have a proxy registry
# between this node and external registries.
docker rmi $(docker images -q)
ensureSuccess $? "Failed to delete the local images."

