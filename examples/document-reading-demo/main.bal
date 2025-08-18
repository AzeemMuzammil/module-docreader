// Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com) All Rights Reserved.
//
// WSO2 LLC. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/io;

import xlibb/docreader;

// Sample documents for testing different formats
final string[] SAMPLE_DOCUMENTS = [
    "./resources/sample.txt",
    "./resources/sample.docx",
    "./resources/sample.pdf",
    "./resources/sample.xls",
    "./resources/sample.pptx",
    "./resources/sample.html",
    "./resources/sample.csv",
    "./resources/sample.xml",
    "./resources/sample.json",
    "./resources/sample.rtf",
    "./resources/sample.epub"
];

public function main() returns error? {
    io:println("🚀 DocReader Library - Comprehensive Document Reading Demo");
    io:println("===========================================================\n");

    // Demo 1: Parse multiple document formats
    io:println("📄 Demo 1: Multi-Format Document Parsing");
    io:println("-----------------------------------------");

    foreach string filePath in SAMPLE_DOCUMENTS {
        check parseAndDisplayDocument(filePath);
        io:println(); // Add spacing between documents
    }

    // Demo 2: Error handling examples
    io:println("\n� Demo 2: Error Handling Examples");
    io:println("----------------------------------");
    check demonstrateErrorHandling();

    // Demo 3: Content analysis
    io:println("\n📊 Demo 3: Content Analysis");
    io:println("---------------------------");
    check performContentAnalysis();

    io:println("\n✅ DocReader Demo Completed Successfully!");
    io:println("=========================================");
}

// Read and display information about a document
function parseAndDisplayDocument(string filePath) returns error? {
    io:println(string `📖 Processing: ${filePath}`);

    docreader:DocumentInfo|docreader:Error result = docreader:readDocument(filePath);

    if result is docreader:DocumentInfo {
        io:println("   ✅ Successfully parsed!");
        io:println(string `   📋 MIME Type: ${result.mimeType}`);
        io:println(string `   🏷️  Extension: ${result.extension}`);
        io:println(string `   📏 Content Length: ${result.content.length()} characters`);
        
        // Display metadata
        io:println("   🔍 Metadata:");
        if result.metadata.length() > 0 {
            foreach string key in result.metadata.keys() {
                string value = result.metadata[key] ?: "";
                if value.length() > 100 {
                    value = value.substring(0, 100) + "...";
                }
                io:println(string `      ${key}: ${value}`);
            }
        } else {
            io:println("      No metadata available");
        }

        // Show content preview (first 200 characters)
        string preview = result.content.length() > 200 ?
            result.content.substring(0, 200) + "..." :
            result.content;
        io:println("   📄 Content Preview:");
        io:println(string `   "${preview.trim()}"`);

    } else {
        io:println(string `   ❌ Failed to parse: ${result.message()}`);
    }
}

// Demonstrate error handling scenarios
function demonstrateErrorHandling() returns error? {
    io:println("Testing error scenarios...\n");

    // Test 1: Non-existent file
    io:println("🔍 Test 1: Non-existent file");
    docreader:DocumentInfo|docreader:Error result = docreader:readDocument("./resources/non-existent-file.txt");
    if result is docreader:Error {
        io:println(string `   ✅ Expected error caught: ${result.message()}`);
    } else {
        io:println("   ❌ Expected error but parsing succeeded");
    }

    // Test 2: Empty file path
    io:println("\n🔍 Test 2: Empty file path");
    result = docreader:readDocument("");
    if result is docreader:Error {
        io:println(string `   ✅ Expected error caught: ${result.message()}`);
    } else {
        io:println("   ❌ Expected error but parsing succeeded");
    }

    // Test 3: Directory instead of file
    io:println("\n🔍 Test 3: Directory path");
    result = docreader:readDocument("./resources");
    if result is docreader:Error {
        io:println(string `   ✅ Expected error caught: ${result.message()}`);
    } else {
        io:println("   ❌ Expected error but parsing succeeded");
    }
}

// Perform content analysis on parsed documents
function performContentAnalysis() returns error? {
    io:println("Analyzing document characteristics...\n");

    map<int> formatCounts = {};
    map<int> contentLengths = {};
    int totalDocuments = 0;
    int successfulParses = 0;

    foreach string filePath in SAMPLE_DOCUMENTS {
        totalDocuments += 1;
        docreader:DocumentInfo|docreader:Error result = docreader:readDocument(filePath);

        if result is docreader:DocumentInfo {
            successfulParses += 1;

            // Count by file extension
            string ext = result.extension == "" ? "no-extension" : result.extension;
            formatCounts[ext] = (formatCounts[ext] ?: 0) + 1;

            // Track content lengths
            string lengthCategory = categorizeContentLength(result.content.length());
            contentLengths[lengthCategory] = (contentLengths[lengthCategory] ?: 0) + 1;
        }
    }

    // Display analysis results
    io:println("📈 Analysis Results:");
    io:println(string `   📊 Total Documents: ${totalDocuments}`);
    io:println(string `   ✅ Successfully Parsed: ${successfulParses}`);
    io:println(string `   📉 Parse Success Rate: ${(successfulParses * 100 / totalDocuments)}%`);

    io:println("\n📋 Format Distribution:");
    foreach string format in formatCounts.keys() {
        int count = formatCounts[format] ?: 0;
        io:println(string `   ${format.toUpperAscii()}: ${count} document(s)`);
    }

    io:println("\n📏 Content Length Distribution:");
    foreach string category in contentLengths.keys() {
        int count = contentLengths[category] ?: 0;
        io:println(string `   ${category}: ${count} document(s)`);
    }
}

// Categorize content by length
function categorizeContentLength(int length) returns string {
    if length == 0 {
        return "Empty";
    } else if length < 100 {
        return "Short (< 100 chars)";
    } else if length < 500 {
        return "Medium (100-500 chars)";
    } else if length < 1000 {
        return "Long (500-1000 chars)";
    } else {
        return "Very Long (> 1000 chars)";
    }
}
