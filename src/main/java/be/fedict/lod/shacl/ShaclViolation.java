/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fedict.lod.shacl;

import be.fedict.lod.shacl.shapes.ShaclShape;

import java.util.Map;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

/**
 *
 * @author Bart.Hanssens
 */
public class ShaclViolation {
	private final ShaclShape shape;
	private final Statement statement;
	private final ValueFactory F = SimpleValueFactory.getInstance();
	
	/**
	 * Get statement that generated a violation.
	 * 
	 * @return triple statement 
	 */
	public Statement getStatement() {
		return this.statement;
	}
	
	/**
	 * Get messages from the SHACL Shape.
	 * 
	 * @return map of messages (different languages)
	 */
	public Map<String,String> getMessages() {
		return shape.getMessages();
	}
			
	/**
	 * Constructor
	 * 
	 * @param shape shape
	 * @param statement RDF triple
	 */
	public ShaclViolation(ShaclShape shape, Statement statement) {
		this.shape = shape;
		this.statement = statement;
	}
	
	/**
	 * Constructor
	 * 
	 * @param shape
	 * @param s subject
	 * @param p object
	 * @param o predicate
	 */
	public ShaclViolation(ShaclShape shape, Resource s, IRI p, Value o) {
		this.shape = shape;
		this.statement = F.createStatement(s, p, o);
	}
}
