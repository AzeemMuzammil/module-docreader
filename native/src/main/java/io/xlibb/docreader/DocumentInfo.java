/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.xlibb.docreader;

import org.apache.tika.metadata.Metadata;

/**
 * Record containing extracted document information.
 *
 * @param mimeType the MIME type of the document
 * @param extension the file extension without the dot
 * @param metadata the extracted metadata from the document
 * @param content the extracted text content from the document
 */
public record DocumentInfo(
    String mimeType,
    String extension,
    Metadata metadata,
    String content
) {
    public DocumentInfo(String mimeType, String extension, Metadata metadata, String content) {
        this.mimeType = mimeType;
        this.extension = extension;
        this.metadata = copyMetadata(metadata);
        this.content = content;
    }

    @Override
    public Metadata metadata() {
        return copyMetadata(metadata);
    }

    private static Metadata copyMetadata(Metadata original) {
        if (original == null) {
            return null;
        }
        Metadata copy = new Metadata();
        for (String name : original.names()) {
            String[] values = original.getValues(name);
            if (values != null) {
                for (String value : values) {
                    copy.add(name, value);
                }
            }
        }
        return copy;
    }
}
