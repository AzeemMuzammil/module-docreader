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

import io.ballerina.runtime.api.creators.TypeCreator;
import io.ballerina.runtime.api.creators.ValueCreator;
import io.ballerina.runtime.api.types.RecordType;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BString;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.OfficeParserConfig;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import static io.xlibb.docreader.ModuleUtils.createError;

/**
 * Document reader implementation using Apache Tika.
 */
public class DocReader {
    private static final String MIME_TYPE_FIELD = "mimeType";
    private static final String EXTENSION_FIELD = "extension";
    private static final String METADATA_FIELD = "metadata";
    private static final String CONTENT_FIELD = "content";
    private static final String DEFAULT_MIME_TYPE = "application/octet-stream";
    private static final String DOCUMENT_INFO_RECORD = "DocumentInfo";
    private static final String X_TIKA_PREFIX = "x-tika";

    private static final Tika tika = new Tika();

    /**
     * Reads a document file and extracts its MIME type, extension, metadata, and content.
     *
     * @param filePath the absolute or relative path to the document file to be read
     * @return DocumentInfo record containing the MIME type, extension, metadata, and content, or Error on failure
     */
    public static Object readDocument(BString filePath) {
        try {
            File file = validateAndGetFile(filePath.getValue());
            String extension = extractFileExtension(file);
            DocumentInfo docInfo = extractDocumentInfo(file, extension);
            return createDocumentInfo(docInfo);
        } catch (IllegalArgumentException e) {
            return createError(e.getMessage());
        } catch (IOException | TikaException | SAXException e) {
            return createError("Error reading document: " + e.getMessage());
        } catch (RuntimeException e) {
            return createError("Unexpected error: " + e.getMessage());
        }
    }

    private static File validateAndGetFile(String filePathStr) {
        File file = new File(filePathStr);

        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + filePathStr);
        }

        if (!file.isFile()) {
            throw new IllegalArgumentException("Path is not a file: " + filePathStr);
        }

        return file;
    }

    private static String extractFileExtension(File file) {
        Path path = Paths.get(file.getAbsolutePath());
        Path fileNamePath = path.getFileName();

        if (fileNamePath != null) {
            String fileName = fileNamePath.toString();
            int lastDotIndex = fileName.lastIndexOf('.');
            if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
                return fileName.substring(lastDotIndex + 1).toLowerCase(Locale.ENGLISH);
            }
        }
        return "";
    }

    private static DocumentInfo extractDocumentInfo(File file, String extension)
            throws IOException, TikaException, SAXException {
        BodyContentHandler handler = new BodyContentHandler(-1);
        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        ParseContext context = configureParseContext();

        try (FileInputStream inputStream = new FileInputStream(file)) {
            parser.parse(inputStream, handler, metadata, context);
            String mimeType = tika.detect(file);
            String content = handler.toString().trim();
            return new DocumentInfo(mimeType, extension, metadata, content);
        }
    }

    private static ParseContext configureParseContext() {
        ParseContext context = new ParseContext();

        PDFParserConfig pdfConfig = new PDFParserConfig();
        pdfConfig.setExtractInlineImages(false);
        pdfConfig.setExtractUniqueInlineImagesOnly(true);
        context.set(PDFParserConfig.class, pdfConfig);

        OfficeParserConfig officeConfig = new OfficeParserConfig();
        officeConfig.setUseSAXDocxExtractor(true);
        officeConfig.setUseSAXPptxExtractor(true);
        officeConfig.setIncludeDeletedContent(false);
        officeConfig.setIncludeMoveFromContent(false);
        officeConfig.setExtractMacros(false);
        context.set(OfficeParserConfig.class, officeConfig);

        return context;
    }

    private static BMap<BString, Object> createDocumentInfo(DocumentInfo docInfo) {
        RecordType resultRecordType =
                TypeCreator.createRecordType(DOCUMENT_INFO_RECORD, ModuleUtils.getModule(), 0, false, 0);
        BMap<BString, Object> documentInfo = ValueCreator.createRecordValue(resultRecordType);

        documentInfo.put(StringUtils.fromString(MIME_TYPE_FIELD),
                StringUtils.fromString(docInfo.mimeType() != null ? docInfo.mimeType() : DEFAULT_MIME_TYPE));
        documentInfo.put(StringUtils.fromString(EXTENSION_FIELD), StringUtils.fromString(docInfo.extension()));

        BMap<BString, Object> metadataMap = createMetadataMap(docInfo.metadata());
        documentInfo.put(StringUtils.fromString(METADATA_FIELD), metadataMap);
        documentInfo.put(StringUtils.fromString(CONTENT_FIELD), StringUtils.fromString(docInfo.content()));

        return documentInfo;
    }

    private static BMap<BString, Object> createMetadataMap(Metadata metadata) {
        BMap<BString, Object> metadataMap = ValueCreator.createMapValue();

        for (String name : metadata.names()) {
            if (name != null && name.toLowerCase(Locale.ENGLISH).startsWith(X_TIKA_PREFIX)) {
                continue;
            }

            String[] values = metadata.getValues(name);
            if (values != null && values.length > 0) {
                String value = values.length == 1 ? values[0] : String.join("; ", values);
                metadataMap.put(StringUtils.fromString(name), StringUtils.fromString(value));
            }
        }

        return metadataMap;
    }
}
