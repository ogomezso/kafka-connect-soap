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

package com.github.ogomezso.kafka.connect.soap;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import java.io.FileInputStream;
import java.util.Optional;

public class SoapClient {


  public SoapEvent start(SoapSourceConnectorConfig config) throws Exception {

    QName serviceName = new QName(config.getString(SoapSourceConnectorConfig.TARGET_NAMESPACE),
        config.getString(SoapSourceConnectorConfig.SERVICE_NAME));
    QName portName = new QName(config.getString(SoapSourceConnectorConfig.TARGET_NAMESPACE),
        config.getString(SoapSourceConnectorConfig.PORT_NAME));
    String endpointUrl = config.getString(SoapSourceConnectorConfig.ENDPOINT_URL);
    String actionUrl = Optional.ofNullable(SoapSourceConnectorConfig.SOAP_ACTION).orElse("");

    SOAPMessage response = invoke(serviceName, portName, endpointUrl, actionUrl);
    return null;
  }

  public SOAPMessage invoke(QName serviceName, QName portName, String endpointUrl,
      String soapActionUri) throws Exception {
    /** Create a service and add at least one port to it. **/
    Service service = Service.create(serviceName);
    service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, endpointUrl);

    /** Create a Dispatch instance from a service.**/
    Dispatch<SOAPMessage> dispatch = service.createDispatch(portName,
        SOAPMessage.class, Service.Mode.MESSAGE);

    // The soapActionUri is set here. otherwise we get a error on .net based services.
    dispatch.getRequestContext().put(Dispatch.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
    dispatch.getRequestContext().put(Dispatch.SOAPACTION_URI_PROPERTY, soapActionUri);

    /** Create SOAPMessage request. **/
    // compose a request message
    MessageFactory messageFactory = MessageFactory.newInstance();
    SOAPMessage message = messageFactory.createMessage();

    //Create objects for the message parts
    SOAPPart soapPart = message.getSOAPPart();
    SOAPEnvelope envelope = soapPart.getEnvelope();
    SOAPBody body = envelope.getBody();

    //Populate the Message.  In here, I populate the message from a xml file
    StreamSource preppedMsgSrc = new StreamSource(new FileInputStream(
        "/Users/ogomezsoriano/projects/confluent/kafka-connect-soap/src/main/resources/req.xml"));
    soapPart.setContent(preppedMsgSrc);

    //Save the message
    message.saveChanges();

    System.out.println(message.getSOAPBody().getFirstChild().getTextContent());

    SOAPMessage response = (SOAPMessage) dispatch.invoke(message);

    return response;
  }
}
