#!/bin/sh

# If there is a script file named "tests/execute-tests.sh",
# we execute it. Otherwise, we do nothing.

# For the record, the shared library is executed inside the directory
# where the project sources are checked out. But it is located somewhere else.
# "execute-tests.sh" must be provided by the project team. See the "master" branch.

if [[ -f "tests/execute-tests.sh" ]]; then
	echo "Executing project tests..."
	/bin/sh tests/execute-tests.sh
else
	echo "No test was found to execute."
fi

