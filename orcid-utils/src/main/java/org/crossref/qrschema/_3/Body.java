//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.08.17 at 03:19:13 PM BST 
//


package org.crossref.qrschema._3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.crossref.org/qrschema/3.0}query" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.crossref.org/qrschema/3.0}forward_link" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.crossref.org/qrschema/3.0}crossref_metadata" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "query",
    "forwardLink",
    "crossrefMetadata"
})
@XmlRootElement(name = "body")
public class Body {

    protected List<Query> query;
    @XmlElement(name = "forward_link")
    protected List<ForwardLink> forwardLink;
    @XmlElement(name = "crossref_metadata")
    protected List<CrossrefMetadata> crossrefMetadata;

    /**
     * Gets the value of the query property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the query property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQuery().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Query }
     * 
     * 
     */
    public List<Query> getQuery() {
        if (query == null) {
            query = new ArrayList<Query>();
        }
        return this.query;
    }

    /**
     * Gets the value of the forwardLink property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the forwardLink property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getForwardLink().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ForwardLink }
     * 
     * 
     */
    public List<ForwardLink> getForwardLink() {
        if (forwardLink == null) {
            forwardLink = new ArrayList<ForwardLink>();
        }
        return this.forwardLink;
    }

    /**
     * Gets the value of the crossrefMetadata property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the crossrefMetadata property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCrossrefMetadata().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CrossrefMetadata }
     * 
     * 
     */
    public List<CrossrefMetadata> getCrossrefMetadata() {
        if (crossrefMetadata == null) {
            crossrefMetadata = new ArrayList<CrossrefMetadata>();
        }
        return this.crossrefMetadata;
    }

}
