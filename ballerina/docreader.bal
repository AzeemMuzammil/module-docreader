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

import ballerina/jballerina.java;

# Reads a document file and extracts its MIME type, extension, metadata, and content.
#
# + filePath - The absolute or relative path to the document file to be read
# + return - `DocumentInfo` record containing the MIME type, extension, metadata, and content, or `Error` on failure
public isolated function readDocument(@display {label: "File Path"} string filePath)
        returns DocumentInfo|Error = @java:Method {
    'class: "io.xlibb.docreader.DocReader"
} external;
