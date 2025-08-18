# Ballerina DocReader Library

[![Build](https://github.com/xlibb/module-docreader/actions/workflows/build-timestamped-master.yml/badge.svg)](https://github.com/xlibb/module-docreader/actions/workflows/build-timestamped-master.yml)
[![codecov](https://codecov.io/gh/xlibb/module-docreader/branch/main/graph/badge.svg)](https://codecov.io/gh/xlibb/module-docreader)
[![GitHub Last Commit](https://img.shields.io/github/last-commit/xlibb/module-docreader.svg)](https://github.com/xlibb/module-docreader/commits/main)
[![Github issues](https://img.shields.io/github/issues/xlibb/module-docreader/module/docreader.svg?label=Open%20Issues)](https://github.com/xlibb/module-docreader/labels/module%2Fdocreader)
[![GraalVM Check](https://github.com/xlibb/module-docreader/actions/workflows/build-with-bal-test-graalvm.yml/badge.svg)](https://github.com/xlibb/module-docreader/actions/workflows/build-with-bal-test-graalvm.yml)

## Overview

A Ballerina library that makes it easy to parse and extract content from documents of many formats — similar to Apache Tika, but designed for Ballerina.

## Usage

```ballerina
import xlibb/docreader;
import ballerina/io;

public function main() returns error? {
    // Read a document file
    docreader:DocumentInfo|docreader:Error result = docreader:readDocument("./sample.pdf");
    
    if result is docreader:DocumentInfo {
        io:println("MIME Type: ", result.mimeType);
        io:println("Extension: ", result.extension);
        
        // Access document metadata
        io:println("Metadata:");
        foreach string key in result.metadata.keys() {
            io:println(string `  ${key}: ${result.metadata[key] ?: ""}`);
        }
        
        io:println("Content: ", result.content);
    } else {
        io:println("Error: ", result.message());
    }
}
```

### Supported Document Formats

The library supports various file types including TXT, DOCX, PDF, XLS, PPT, HTML, CSV, XML, JSON, and RTF.

### API Reference

#### `readDocument(string filePath) returns DocumentInfo|Error`

Reads a document file and extracts its metadata and content.

**Parameters:**
- `filePath` - The absolute or relative path to the document file

**Returns:**
- `DocumentInfo` - A record containing:
  - `mimeType` - The MIME type of the document
  - `extension` - The file extension without the dot
  - `metadata` - A map containing document metadata (author, title, creation date, etc.)
  - `content` - The extracted text content
- `Error` - If the file cannot be read or parsed

## Build from the source

### Set up the prerequisites

1.  Download and install Java SE Development Kit (JDK) version 21 (from one of the following locations).

    - [Oracle](https://www.oracle.com/java/technologies/javase-jdk21-downloads.html)

    - [OpenJDK](https://adoptopenjdk.net/)

      > **Note:** Set the JAVA_HOME environment variable to the path name of the directory into which you installed JDK.

2.  Export your Github Personal access token with the read package permissions as follows.

              export packageUser=<Username>
              export packagePAT=<Personal access token>

### Build the source

Execute the commands below to build from the source.

1. To build the library:

   ```
   ./gradlew clean build
   ```

2. To run the integration tests:
   ```
   ./gradlew clean test
   ```
3. To build the module without the tests:
   ```
   ./gradlew clean build -x test
   ```
4. To debug module implementation:
   ```
   ./gradlew clean build -Pdebug=<port>
   ./gradlew clean test -Pdebug=<port>
   ```
5. To debug the module with Ballerina language:
   ```
   ./gradlew clean build -PbalJavaDebug=<port>
   ./gradlew clean test -PbalJavaDebug=<port>
   ```
6. Publish ZIP artifact to the local `.m2` repository:
   ```
   ./gradlew clean build publishToMavenLocal
   ```
7. Publish the generated artifacts to the local Ballerina central repository:
   ```
   ./gradlew clean build -PpublishToLocalCentral=true
   ```
8. Publish the generated artifacts to the Ballerina central repository:
   ```
   ./gradlew clean build -PpublishToCentral=true
   ```

## Contributing

We welcome contributions! Please feel free to submit a Pull Request.

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.
