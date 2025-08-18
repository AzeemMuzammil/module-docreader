# DocReader Library - Document Reading Demo

A demonstration of the Ballerina DocReader library for parsing various document formats.

## Project Structure

```
document-reading-demo/
├── main.bal         # Main demonstration program
├── Ballerina.toml   # Project configuration
└── resources/       # Sample documents (11 formats)
```

## Running the Demo

```bash
cd examples/document-reading-demo
bal run
```

## Features

- Multi-format document parsing (TXT, DOCX, PDF, XLS, PPT, HTML, CSV, XML, JSON, RTF, EPUB)
- Error handling examples
- Content analysis

## Usage Examples

```ballerina
import xlibb/docreader;
import ballerina/io;

public function main() returns error? {
    // Read document
    docreader:DocumentInfo|docreader:Error result = docreader:readDocument("./resources/sample.pdf");

    if result is docreader:DocumentInfo {
        io:println("MIME Type: ", result.mimeType);
        io:println("Extension: ", result.extension);
        
        // Access metadata
        string author = result.metadata["Author"] ?: "Unknown";
        string title = result.metadata["Title"] ?: "Untitled";
        io:println(string `Document: ${title} by ${author}`);
        
        io:println("Content Length: ", result.content.length());
    } else {
        io:println("Error: ", result.message());
    }
}
```

### Error Handling

```ballerina
docreader:DocumentInfo|docreader:Error result = docreader:readDocument("./non-existent.txt");
if result is docreader:Error {
    io:println("Failed to read document: ", result.message());
}
```
