package net.praqma.monkit;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MonKit {
    
    public static final String __ROOT_TAG = "categories";
    public static final String __SCHEMA = "http://code.praqma.net/schemas/monkit/1.0.1/monkit.xsd";
    
    private File destination = null;
    
    private Element root = null;
    private Document doc = null;
    
    /**
     * Constructor initializing an empty MonKit
     */
    public MonKit( ) {
	destination = new File( "monkit.xml" );
	initialize();
    }
    
    /**
     * Constructor initializing an empty MonKit with a predefined output destination
     * @param destination A File representing the output file
     */
    public MonKit( File destination ) {
	initialize();
    }
    
    /**
     * Constructor initializing a MonKit given a Document
     * @param doc A valid MonKit Document
     * @throws MonKitException
     */
    public MonKit( Document doc ) throws MonKitException {
	this.doc = doc;
	Node node = doc.getFirstChild();
	
	if( node == null ) {
	    throw new MonKitException( "Empty MonKit file" );
	}
	
	if( !node.getNodeName().equals(MonKit.__ROOT_TAG) ) {
	    throw new MonKitException( "Not a valid MonKit format" );
	}
	
	root = (Element) node;
    }
    
    private void initialize() {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	factory.setNamespaceAware(true);
	factory.setSchema(MonKit.getSchema());
	DocumentBuilder builder;
	try {
	    builder = factory.newDocumentBuilder();
	    doc = builder.newDocument();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	}

	/* Preparing the root note */
	root = (Element) doc.appendChild(doc.createElement(MonKit.__ROOT_TAG));
	root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", __SCHEMA);
    }
    
    /**
     * Create a MonKit object from an xml file
     * @param xml A File with the MonKit obersations
     * @return A new MonKit object
     * @throws MonKitException
     */
    public static MonKit fromXML( File xml ) throws MonKitException {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	factory.setNamespaceAware( true );
	
	DocumentBuilder builder;
	Document doc = null;
	try {
	    builder = factory.newDocumentBuilder();

	    doc = builder.parse(xml);
	} catch (Exception e) {
	    throw new MonKitException( "Could not parse the file" );
	}
	
	return new MonKit( doc );
    }
    
    /**
     * Given a String, a MonKit is initialized
     * @param str A valid MonKit
     * @return A MonKit
     * @throws MonKitException
     */
    public static MonKit fromString( String str ) throws MonKitException {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	factory.setNamespaceAware( true );
	
	DocumentBuilder builder;
	Document doc = null;
	try {
	    builder = factory.newDocumentBuilder();

	    doc = builder.parse(new ByteArrayInputStream(str.getBytes("UTF-8")));
	} catch (Exception e) {
	    throw new MonKitException( "Coult not parse the file" );
	}
	
	return new MonKit( doc );
    }
    
    /**
     * Add a category to the MonKit
     * @param name The name of the category
     * @param scale The scale of the category
     */
    public void addCategory( String name, String scale ) {
	if( getCategory(name) != null ) {
	    return;
	}
	
	Element c = doc.createElement("category");
	c.setAttribute("name", name);
	c.setAttribute("scale", scale);
	c.appendChild(doc.createElement("observations"));
	root.appendChild(c);
    }
    
    /**
     * Add an observation given the three arguments
     * @param name Name of the observation
     * @param scale The scale of the observation
     * @param value Value of the observation
     * @throws MonKitException
     */
    public void add(String name, String value, String category) {
	_add(name, value, category);
    }
    
    /**
     * Add an observation given a {@link MonKitObservation}
     * @param mke A MonKitElement
     */
    public void add( MonKitObservation mke, String category ) {
	_add(mke.getName(), mke.getValue(), category);
    }
    
    private void _add(String name, String value, String category) {
	Element c = getCategory(category);
	if( c == null ) {
	    return;
	}
	
	Element e = getObservation( name, category );
	
	if( e != null ) {
	    e.setTextContent(value);
	    e.setAttribute("name", name);
	} else {
	    Element o = doc.createElement("observation");
	    o.setAttribute("name", name);
	    o.setTextContent(value);
	    Element obs = (Element) c.getElementsByTagName("observations").item(0);
	    if( obs != null ) {
		obs.appendChild(o);
	    }
	}
    }
    
    /**
     * Add a List of categories to the MonKit
     * @param categories The list of MonKitCategories
     */
    public void add( List<MonKitCategory> categories ) {
	for( MonKitCategory mkc : categories ) {
	    add(mkc);
	}
    }
    
    /**
     * Add another MonKitCategory to the MonKit
     * @param mkc
     */
    public void add( MonKitCategory mkc ) {
	addCategory(mkc.getName(), mkc.getScale());
	add( mkc, mkc.getName() );
    }
    
    /**
     * Add a series of observations
     * @param elements A list of {@link MonKitObservation}s
     */
    public void add( List<MonKitObservation> elements, String category ) {
	for( MonKitObservation mke : elements ) {
	    add(mke, category);
	}
    }
    
    /**
     * Get a List of the available categories
     * @return
     */
    public List<MonKitCategory> getCategories() {
	NodeList nodes = root.getElementsByTagName("category");
	
	List<MonKitCategory> elements = new ArrayList<MonKitCategory>();
	
	for( int i = 0, len = nodes.getLength() ; i < len ; ++i ) {
	    Node node = nodes.item(i);
	    if( node.getNodeType( ) == Node.ELEMENT_NODE ) {
		Element e = (Element)node;
		String name = e.getAttribute("name");
		String scale = e.getAttribute("scale");
		elements.add( new MonKitCategory(name, scale,getObservations(name)) );
	    }
	}
	
	return elements;
    }
    
    private Element getCategory( String name ) {
	NodeList nodes = root.getElementsByTagName("category");
	
	for( int i = 0, len = nodes.getLength() ; i < len ; ++i ) {
	    Node node = nodes.item(i);
	    if( node.getNodeType( ) == Node.ELEMENT_NODE ) {
		Element e = (Element)node;
		if( e.getAttribute("name").equalsIgnoreCase(name) ) {
		    return e;
		}
	    }
	}
	
	return null;
    }
    
    private Element getObservationsElement( Element category ) {
	NodeList nodes = category.getElementsByTagName("observations");
	for( int i = 0, len = nodes.getLength() ; i < len ; ++i ) {
	    
	    Node node = nodes.item(i);
	    if( node.getNodeType( ) == Node.ELEMENT_NODE ) {
		return (Element)node;
	    }
	}
	
	return null;
    }
    
    /**
     * Retrieve the observations
     * @return A list of {@link MonKitObservation}s
     */
    public List<MonKitObservation> getObservations( String name ) {
	Element category = getCategory(name);
	if( category == null ) {
	    return null;
	}
	
	NodeList nodes = category.getElementsByTagName("observation");
	
	List<MonKitObservation> elements = new ArrayList<MonKitObservation>();
	
	for( int i = 0, len = nodes.getLength() ; i < len ; ++i ) {
	    Node node = nodes.item(i);
	    if( node.getNodeType( ) == Node.ELEMENT_NODE ) {
		Element e = (Element)node;
		elements.add( new MonKitObservation(e.getAttribute("name"), e.getTextContent()) );
	    }
	}
	
	return elements;
    }
   
    private Element getObservation( String name, String category ) {
	Element c = getCategory(category);
	if( c == null ) {
	    return null;
	}
	
	Element obs = (Element) c.getElementsByTagName("observations").item(0);
	NodeList nodes = obs.getElementsByTagName("observation");
	
	for( int i = 0, len = nodes.getLength() ; i < len ; ++i ) {
	    Node node = nodes.item(i);
	    if( node.getNodeType( ) == Node.ELEMENT_NODE ) {
		Element e = (Element)node;
		if( e.getAttribute("name").equalsIgnoreCase(name) ) {
		    return e;
		}
	    }
	}
	
	return null;
    }
    
    /**
     * Merge two or more {@link MonKit}s 
     * @param mks An array of MonKits
     * @return A new instantiation of MonKit
     */
    public static MonKit merge( MonKit ... mks ) {
	MonKit nmk = new MonKit();
	for( MonKit mk : mks ) {
	    nmk.add(mk.getCategories());
	}
	
	return nmk;
    }
    
    /**
     * Merge two or more {@link MonKit}s 
     * @param mks A List of MonKits
     * @return A new instantiation of MonKit
     */
    public static MonKit merge( List<MonKit> mks ) {
	MonKit nmk = new MonKit();
	for( MonKit mk : mks ) {
	    nmk.add(mk.getCategories());
	}
	
	return nmk;
    }
    
    /**
     * Get the observations as String
     * @return
     */
    public String getXML() {
	StringWriter out = new StringWriter();
	
	try {
	    TransformerFactory factory = TransformerFactory.newInstance();

	    Transformer transformer = factory.newTransformer();

	    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");

	    transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "4" );

	    Source src = new DOMSource( doc );

	    Result dest = new StreamResult(out);
	    transformer.transform(src, dest);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return out.toString();
    }
    
    public String toString() {
	return getXML();
    }
    
    public static Schema getSchema() {
	SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
	try {
	    return schemaFactory.newSchema(new URL( "http://code.praqma.net/schemas/monkit/1.0.0/monkit.xsd" ));
	}
	catch( Exception e ) {
	    e.printStackTrace();
	}
	
	return null;
    }
    
    /**
     * Validate the MonKit xml file
     * @return True or false whether the xml is valid or not
     */
    public boolean validate() {
	SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
	try {
	    Schema schema = schemaFactory.newSchema(new URL( __SCHEMA ));
	    Validator validator = schema.newValidator();
	    
	    validator.validate(new DOMSource(doc));
	    return true;
	    
	} catch (MalformedURLException e) {
	    System.err.println( "Whoops... Not good: " + e );
	} catch (SAXException e) {
	    System.err.println( "Not valid: " + e );
	} catch (IOException e) {
	    System.err.println( "Whoops... Not good: " + e );
	}

	return false;
    }
    
    public List<MonKitObservation> toList( String name ) {
	return getObservations(name);
    }
    
    /**
     * Save the MonKit
     * @throws IOException
     */
    public void save() throws IOException {
	save( destination );
    }
    
    /**
     * Save the MonKit to a given destination
     * @param filename
     * @throws IOException
     */
    public void save(File filename) throws IOException {
	String xml = getXML();
	try {
	    BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
	    bw.append(xml);
	    bw.close();
	} catch (IOException e) {
	    throw e;
	}
    }
}
