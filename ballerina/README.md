# Ballerina DocReader Library

A Ballerina library that makes it easy to parse and extract content from documents of many formats — similar to Apache Tika, but designed for Ballerina.

## Overview

The DocReader library provides powerful document parsing capabilities for various formats including TXT, DOCX, PDF, XLS, PPT, HTML, CSV, XML, JSON, and RTF.

## Usage

```ballerina
import xlibb/docreader;
import ballerina/io;

public function main() returns error? {
    // Read a document file and extract its content
    docreader:DocumentInfo|docreader:Error result = docreader:readDocument("./document.pdf");
    
    if result is docreader:DocumentInfo {
        io:println("MIME Type: ", result.mimeType);
        io:println("Extension: ", result.extension);
        
        // Access document metadata
        foreach string key in result.metadata.keys() {
            io:println(string `${key}: ${result.metadata[key] ?: ""}`);
        }
        
        io:println("Content: ", result.content);
    } else {
        io:println("Error: ", result.message());
    }
}
```

### API

#### `readDocument(string filePath) returns DocumentInfo|Error`

Reads a document file and extracts its MIME type, extension, metadata, and content.

**Parameters:**
- `filePath` - The absolute or relative path to the document file

**Returns:**
- `DocumentInfo` record with `mimeType`, `extension`, `metadata`, and `content` fields
- `Error` if the file cannot be read or parsed

The `metadata` field contains document properties like author, title, creation date, etc., excluding internal Tika processing fields.

## Building from Source

```bash
git clone https://github.com/xlibb/module-docreader.git
cd module-docreader
./gradlew build
```

## Issues and Contributing

Report issues and contribute at: https://github.com/xlibb/module-docreader

## License

This project is licensed under the Apache License 2.0.
