/**
 * Copyright © 2021 Oscar Gómez (ogomezso0@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.ogomezso.kafka.connect.soap.model;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

import org.apache.kafka.connect.source.SourceRecord;
import org.w3c.dom.Document;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SourceRecordMapper {

  private final XmlMapper xmlMapper = new XmlMapper();
  private final ObjectMapper jsonMapper = new ObjectMapper();

  public SourceRecord getSourceRecordFromSoapMessage(RecordKey recordKey, SOAPMessage message,
      String topic)
      throws SOAPException, IOException, TransformerException {

    log.debug("Record key: {}", recordKey);
    String keyAsJsonString = jsonMapper.writeValueAsString(recordKey);
    log.debug("Record key string: {}", keyAsJsonString);
    String valueAsJsonString = valueToJsonString(message);
    log.debug("Value string: {}", valueAsJsonString);
    return createSourceRecordFromSoapEvent(new RecordKeyStruct(keyAsJsonString),
        new RecordValueStruct(valueAsJsonString), topic);
  }

  private String valueToJsonString(SOAPMessage message)
      throws TransformerException, SOAPException, IOException {
    log.info("mapping");
    Document doc = message.getSOAPBody().extractContentAsDocument();
    StringWriter sw = new StringWriter();
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.transform(new DOMSource(doc), new StreamResult(sw));
    JsonNode node = xmlMapper.readTree(sw.toString().getBytes());
    return jsonMapper.writeValueAsString(node);
  }

  private SourceRecord createSourceRecordFromSoapEvent(
      RecordKeyStruct key,
      RecordValueStruct value, String topic) {
// TODO Add Support to full qualified AVRO Schemas
    Map<String, ?> srcOffset = Collections.emptyMap();
    Map<String, ?> srcPartition = Collections.emptyMap();

    log.debug("Event " + value.toString());
    return new SourceRecord(
        srcPartition,
        srcOffset,
        topic,
        RecordKeyStruct.SCHEMA,
        key,
        RecordValueStruct.SCHEMA,
        value
    );

  }
}
