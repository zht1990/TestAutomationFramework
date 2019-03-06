package se.claremont.autotest.soapsupport;

import se.claremont.autotest.common.logging.LogLevel;
import se.claremont.autotest.common.testcase.TestCase;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Used to interact with SOAP services.
 * Pre-requisite is using maven plugin for Apache CFX wsdl2java to generate object model for ease of maintenance.
 */
public class SoapInteraction {

    private Integer serverConnectionPort = null;
    private String serverConnectionHost = null;
    private Protocol serverConnectionProtocol = null;
    private TestCase testCase;
    private String cfxWsdl2javaGeneratedDatamodelPackageName = null;

    private String authorizationUserName = null;
    private String getAuthorizationPassword = null;
    private String standardDatamodelNamespaceUrl = null;

    /**
     * Used to interact with SOAP services.
     *
     * @param testCase                                  The TestCase object to log to.
     * @param cfxWsdl2javaGeneratedDatamodelPackageName The package name of the datamodel package generated by the wsdl2java maven plugin. Usually in the target folder.
     */
    public SoapInteraction(TestCase testCase, String cfxWsdl2javaGeneratedDatamodelPackageName) {
        if (testCase == null) testCase = new TestCase();
        this.testCase = testCase;
        this.cfxWsdl2javaGeneratedDatamodelPackageName = cfxWsdl2javaGeneratedDatamodelPackageName;
    }

    /**
     * Used to interact with SOAP services.
     *
     * @param testCase                                  The TestCase object to log to.
     * @param cfxWsdl2javaGeneratedDatamodelPackageName The package name of the datamodel package generated by the wsdl2java maven plugin. Usually in the target folder.
     * @param standardDatamodelNamespaceUrl             The URL to the datamodel namespace.
     */
    public SoapInteraction(TestCase testCase, String standardDatamodelNamespaceUrl, String cfxWsdl2javaGeneratedDatamodelPackageName) {
        if (testCase == null) testCase = new TestCase();
        this.standardDatamodelNamespaceUrl = standardDatamodelNamespaceUrl;
        this.testCase = testCase;
        this.cfxWsdl2javaGeneratedDatamodelPackageName = cfxWsdl2javaGeneratedDatamodelPackageName;
    }

    /**
     * Sets the server host name for the interaction.
     * The separate setter enables same tests towards multiple test environments.
     * Connection port or authentication is set with separate methods.
     *
     * @param protocol The access protocol to use.
     * @param host     The host name.
     */
    public void setServer(Protocol protocol, String host) {
        setServer(protocol, host, null);
    }

    /**
     * Sets the server host name for the interaction. Uses HTTP for access.
     * For other protocols, use other setter.
     * The separate setter enables same tests towards multiple test environments.
     * Connection port or authentication is set with separate methods.
     *
     * @param host The host name.
     */
    public void setServer(String host) {
        if (serverConnectionProtocol == null)
            serverConnectionProtocol = Protocol.HTTP;
        setServer(null, host, null);
    }

    /**
     * For convenience the namespace URL can be set separately using this method.
     *
     * @param nameSpace SOAP namespace URL.
     */
    public void setStandardDatamodelNamespaceUrl(String nameSpace) {
        standardDatamodelNamespaceUrl = nameSpace;
    }


    /**
     * Sets the server host name for the interaction.
     * The separate setter enables same tests towards multiple test environments.
     * Connection port or authentication is set with separate methods.
     *
     * @param protocol The access protocol to use.
     * @param host     The host name.
     * @param port     Port number for SOAP endpoint access.
     */
    public void setServer(Protocol protocol, String host, Integer port) {
        if (host != null)
            serverConnectionHost = host;

        if (port != null)
            serverConnectionPort = port;

        if (protocol != null)
            serverConnectionProtocol = protocol;
    }

    /**
     * Concaternates user name and password to endpoint URL.
     *
     * @param userName User name
     * @param password Password
     */
    public void setLoginCredentials(String userName, String password) {
        if (userName == null && password != null) userName = "";
        if (userName != null && password == null) password = "";
        authorizationUserName = userName;
        getAuthorizationPassword = password;

    }

    private String baseUrl() {
        if (serverConnectionProtocol == null) serverConnectionProtocol = Protocol.HTTP;
        StringBuilder sb = new StringBuilder();
        sb.append(serverConnectionProtocol.toString().toLowerCase()).append("://");
        if (authorizationUserName != null && getAuthorizationPassword != null)
            sb.append(authorizationUserName).append(":").append(getAuthorizationPassword).append("@");
        sb.append(serverConnectionHost);
        if (serverConnectionPort != null)
            sb.append(":").append(serverConnectionPort);
        return sb.toString();
    }

    private JAXBElement<? extends Object> object2Xml(Object obj) {
        Object objectFactory = null;

        try {
            Class<?> clazz = Class.forName(cfxWsdl2javaGeneratedDatamodelPackageName + ".ObjectFactory");
            Constructor<?> ctor = clazz.getConstructor();
            objectFactory = ctor.newInstance();
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            testCase.log(LogLevel.EXECUTION_PROBLEM, "Cannot create JAXBElement from object. " + e.toString());
        }
        if (objectFactory == null) {
            return null;
        }
        Object[] params = {obj};
        for (Method method : objectFactory.getClass().getMethods()) {
            if (!method.getName().equals("create" + obj.getClass().getSimpleName())) continue;

            try {
                return (JAXBElement<? extends Object>) method.invoke(objectFactory, new Object[]{obj});
            } catch (IllegalAccessException | InvocationTargetException e) {
                testCase.log(LogLevel.EXECUTION_PROBLEM, "Cannot create JAXBElement from object. " + e.toString());
            }
        }
        return null;
    }

    public TafSoapResponse sendSoapRequest(String endpointExtension, String actionName, String verbString, String nameSpaceName, Object myCustomObject) {
        JAXBElement<? extends Object> objectXml = object2Xml(myCustomObject);
        if (objectXml == null) {
            testCase.log(LogLevel.EXECUTION_PROBLEM, "Could not convert object to XML. Try using the sendSoapRequest overlay using JAXBElement<? extends Object> rather than the one using the object directly.");
            testCase.report();
        }
        ;
        return sendSoapRequest(endpointExtension, actionName, verbString, nameSpaceName, objectXml);
    }

    public TafSoapResponse sendSoapRequest(String endpointExtension, String actionName, String verbString, String nameSpaceName, JAXBElement<? extends Object> myCustomObjectElement) { //obj,
        SOAPConnection soapConnection = null;
        SOAPMessage requestMessage = null;
        SOAPConnectionFactory soapConnectionFactory = null;
        if (!endpointExtension.startsWith("/") && !baseUrl().endsWith("/"))
            endpointExtension = "/" + endpointExtension;
        String endpoint = baseUrl() + endpointExtension;

        testCase.log(LogLevel.DEBUG, "Sending SOAP request to endpoint '" + endpoint + "'.");
        try {
            //Creating message
            soapConnectionFactory = SOAPConnectionFactory.newInstance();
            soapConnection = soapConnectionFactory.createConnection();
            MessageFactory messageFactory = MessageFactory.newInstance();
            requestMessage = messageFactory.createMessage();
            SOAPPart soapPart = requestMessage.getSOAPPart();

            //Preparing message envelope
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration(nameSpaceName, standardDatamodelNamespaceUrl);
            //envelope.addNamespaceDeclaration(nameSpaceName, "http://schemas.itello.se/Inca/datamodel");
            MimeHeaders headers = requestMessage.getMimeHeaders();
            headers.addHeader("SOAPAction", actionName);
            requestMessage.saveChanges();

            SOAPElement verb = requestMessage.getSOAPBody().addChildElement(verbString, nameSpaceName);
            JAXBContext jaxbContext = JAXBContext.newInstance(cfxWsdl2javaGeneratedDatamodelPackageName);
            //JAXBContext jaxbContext = JAXBContext.newInstance("se.itello.schemas.inca.datamodel");
            jaxbContext.createMarshaller().marshal(myCustomObjectElement, verb);
            requestMessage.saveChanges();
            return getResponse(soapConnection, requestMessage, endpoint);
        } catch (SOAPException | JAXBException e) {
            testCase.log(LogLevel.EXECUTION_PROBLEM, e.toString());
        }

        return null;
    }

    private TafSoapResponse getResponse(SOAPConnection soapConnection, SOAPMessage requestMessage, Object endpoint) {
        TafSoapResponse response = new TafSoapResponse(testCase);
        response.setRequestSoapMessage(requestMessage);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            requestMessage.writeTo(out);
            String strMsg = new String(out.toByteArray());
            response.setRequestBody(strMsg);
            System.out.println("SOAP request: " + strMsg);
            testCase.log(LogLevel.DEBUG, "SOAP request: " + strMsg);

            SOAPMessage soapResponse = soapConnection.call(requestMessage, endpoint);
            response.setResponseSoapMessage(soapResponse);

            ByteArrayOutputStream newOut = new ByteArrayOutputStream();
            response.responseSoapMessage.writeTo(newOut);
            String responseStrMessage = new String(newOut.toByteArray());
            response.setResponseBody(responseStrMessage);
            System.out.println("SOAP response: " + responseStrMessage);
            testCase.log(LogLevel.DEBUG, "SOAP request to '" + endpoint + "' returned response:" +
                    System.lineSeparator() + responseStrMessage);
        } catch (SOAPException | IOException e) {
            testCase.log(LogLevel.EXECUTION_PROBLEM, e.toString());
        }
        return response;
    }

    /**
     * Endpoint access protocol
     */
    public enum Protocol {
        HTTP,
        HTTPS,
        FILE
    }

}