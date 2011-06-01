package net.praqma.monkit;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MonKit {
    private File destination = null;
    
    private Element root = null;
    private Document doc = null;
    
    public MonKit( ) {
	destination = new File( "monkit.xml" );
	initialize();
    }
    
    public MonKit( File destination ) {
	initialize();
    }
    
    public MonKit( Document doc ) throws MonKitException {
	this.doc = doc;
	Node node = doc.getFirstChild();
	
	if( node == null ) {
	    throw new MonKitException( "Empty MonKit file" );
	}
	
	if( !node.getNodeName().equals("observations") ) {
	    throw new MonKitException( "Not a valid MonKit format" );
	}
	
	root = (Element) node;
    }
    
    private void initialize() {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	factory.setNamespaceAware(true);
	DocumentBuilder builder;
	try {
	    builder = factory.newDocumentBuilder();
	    doc = builder.newDocument();
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	}

	/* Preparing the root note */
	root = (Element) doc.appendChild(doc.createElement("observations"));
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
	    throw new MonKitException( "Coult not parse the file" );
	}
	
	return new MonKit( doc );
    }
    
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
     * Add an observation given the three arguments
     * @param name Name of the observation
     * @param scale The scale of the observation
     * @param value Value of the observation
     * @throws MonKitException
     */
    public void add(String name, String scale, String value, String category) {
	Element o = doc.createElement("observation");
	o.setAttribute("name", name);
	o.setAttribute("scale", scale);
	o.setAttribute("category", category);
	o.setTextContent(value);
	root.appendChild(o);
    }
    
    /**
     * Add an observation given a {@link MonKitObservation}
     * @param mke A MonKitElement
     */
    public void add( MonKitObservation mke ) {
	Element o = doc.createElement("observation");
	o.setAttribute("name", mke.getName());
	o.setAttribute("scale", mke.getScale());
	o.setAttribute("category", mke.getCategory());
	o.setTextContent(mke.getValue());
	root.appendChild(o);
    }
    
    /**
     * Add a series of observations
     * @param elements A list of {@link MonKitObservation}s
     */
    public void add( List<MonKitObservation> elements ) {
	for( MonKitObservation mke : elements ) {
	    add(mke);
	}
    }
    
    /**
     * Retrieve the observations
     * @return A list of {@link MonKitObservation}s
     */
    public List<MonKitObservation> getObservations() {
	NodeList nodes = root.getElementsByTagName("observation");
	
	List<MonKitObservation> elements = new ArrayList<MonKitObservation>();
	
	for( int i = 0, len = nodes.getLength() ; i < len ; ++i ) {
	    Node node = nodes.item(i);
	    if( node.getNodeType( ) == Node.ELEMENT_NODE ) {
		Element e = (Element)node;
		elements.add( new MonKitObservation(e.getAttribute("name"), e.getAttribute("scale"), e.getTextContent(), e.getAttribute("category")) );
	    }
	}
	
	return elements;
    }
   
    /**
     * Merge two or more {@link MonKit}s 
     * @param mks An array of MonKits
     * @return A new instantiation of MonKit
     */
    public static MonKit merge( MonKit ... mks ) {
	MonKit nmk = new MonKit();
	for( MonKit mk : mks ) {
	    nmk.add( mk.getObservations() );
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
	    nmk.add( mk.getObservations() );
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
    
    public List<MonKitObservation> toList() {
	return getObservations();
    }
    
    /*
    public Map<MonKitObservation> toMap() {
	Map<MonKitObservation> map = new HashMap<MonKitObservation>();
	
	return map;
    }*/
    
    public void save() throws IOException {
	save( destination );
    }
    
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
