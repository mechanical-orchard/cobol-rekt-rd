#!/bin/bash

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Install openjdk@21 if not already installed
if ! brew list openjdk@21 &>/dev/null; then
    echo "Installing openjdk@21..."
    brew install openjdk@21
    echo "✅ openjdk@21 installed"
else
    echo "✅ openjdk@21 is already installed."
fi

# Install maven if not already installed
if ! brew list maven &>/dev/null; then
    echo "Installing maven..."
    brew install maven
    echo "✅ maven installed."
else
    echo "✅ maven is already installed."
fi

# Install graphviz if not already installed
if ! brew list graphviz &>/dev/null; then
    echo "Installing graphviz..."
    brew install graphviz
    echo "✅ graphviz installed"
else
    echo "✅ graphviz is already installed."
fi

echo "creating symbolic link for openjdk@21"
# Check if the symbolic link for openjdk@21 exists
if [ ! -L /Library/Java/JavaVirtualMachines/openjdk-21.jdk ]; then
    echo "Creating symbolic link for openjdk@21..."
    sudo ln -sfn /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-21.jdk
    echo "✅ Symbolic link created."
else
    echo "✅ Symbolic link for openjdk@21 already exists."
fi


echo "updating git repo..."
git pull -r

echo "updating submodule..."
git submodule update --init

# Check if Neo4j is running locally
if lsof -i:7474; then
    echo "Neo4j is already running on port 7474."
else
    echo "Starting Neo4j using Docker Compose..."
    docker-compose up -d
fi

echo "creating jars skipping tests and eclipse checkstyles..."
mvn clean verify package -Dcheckstyle.skip=true -Dmaven.test.skip=true

echo "Setup completed successfully."