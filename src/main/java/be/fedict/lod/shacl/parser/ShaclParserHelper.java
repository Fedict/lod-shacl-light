/*
 * Copyright (c) 2017, Bart Hanssens <bart.hanssens@fedict.be>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package be.fedict.lod.shacl.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import be.fedict.lod.shacl.shapes.ShaclNodeShape;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Bart Hanssens
 */
public class ShaclParserHelper {		
	/**
	 * Filter model for a specific property
	 * 
	 * @param m model
	 * @param p predicate of the property
	 * @return object IRI or null
	 */
	public static IRI asIRI(Model m, IRI p) {
		return (m == null || m.isEmpty() || p == null) 
				? null 
				: Models.getPropertyIRI(m, Models.subject(m).get(), p).orElse(null);
	}
	/**
	 * Filter model for a specific property
	 * 
	 * @param m model
	 * @param p predicate of the property
	 * @return object value or null
	 */
	public static Value asValue(Model m, IRI p) {
		return (m == null || m.isEmpty() || p == null) 
				? null 
				: Models.getProperty(m, Models.subject(m).get(), p).orElse(null);
	}
	
	/**
	 * Filter model for a specific property
	 * 
	 * @param m model
	 * @param p predicate of the property
	 * @return set of IRIs or null
	 */
	public static Set<IRI> asIRIs(Model m, IRI p){
		return (m == null || m.isEmpty() || p == null) 
				? null
				: Models.getPropertyIRIs(m, Models.subject(m).get() , p);
	}
	
	/**
	 * Filter model for a specific property.
	 * 
	 * @param m model
	 * @param p predicate of the property
	 * @return 
	 */
	public static String asString(Model m, IRI p) {
		return (m == null || m.isEmpty() || p == null) 
				? null
				: Models.getPropertyString(m, Models.subject(m).get() , p).orElse(null);
	}
	
	/**
	 * 
	 * @param m
	 * @return 
	 */
	public static Set<String> collectionAsStrings(Model m, Resource head) {
		if (m == null || m.isEmpty() || head == null) {
			return null;
		}
		
		Set<String> vals = new HashSet<>();
		for (Value v: RDFCollections.asValues(m, head, new ArrayList<>())) {
			vals.add(((Literal) v).stringValue());
		}
		return vals;
	}
	
	/**
	 * Filter model for a specific property.
	 * 
	 * @param m model
	 * @param p predicate of the property
	 * @param value default value
	 * @return integer value
	 * @throws ShaclParserException 
	 */
	public static int asInt(Model m, IRI p, int value) throws ShaclParserException {
		if (m == null || m.isEmpty() || p == null) {
			return value;
		}
		Literal l = Models.getPropertyLiteral(m, Models.subject(m).get(), p).orElse(null);
		return (l == null) ? value : l.intValue();
	}
	
	/**
	 * Filter model for a specific property.
	 * 
	 * @param m model
	 * @param p predicate of the property
	 * @return boolean value
	 * @throws ShaclParserException 
	 */
	public static boolean asBool(Model m, IRI p) throws ShaclParserException {
		if (m == null || m.isEmpty() || p == null) {
			return false;
		}
		Literal l = Models.getPropertyLiteral(m, Models.subject(m).get(), p).orElse(null);
		return (l == null) ? false : l.booleanValue();
	}
	
	
	/**
	 * Get subject IRIs by SHACL targetNode
	 * 
	 * @param m model
	 * @param nodes target nodes
	 * @return set of subjects
	 */
	public static Set<Resource> getFilteredNodes(Model m, Set<Resource> nodes) {
		Set<Resource> subjs = new HashSet<>();
		for(Resource node: nodes) {
			if (node != null) {
				subjs.addAll(m.filter(node, null, null).subjects());
			}
		}
		return subjs;
	}
	
	/**
	 * Get subject IRIs by SHACL targetClass
	 * 
	 * @param m model
	 * @param classes target classes
	 * @return set of subjects
	 */
	public static Set<Resource> getFilteredClasses(Model m, Set<IRI> classes) {
		Set<Resource> subjs = new HashSet<>();
		for(Resource cl: classes) {
			if (cl != null) {
				subjs.addAll(m.filter(null, RDF.TYPE, cl).subjects());
			}
		}
		return subjs;
	}
	
	/**
	 * Get subject IRIs of the targets of a shape
	 * 
	 * @param m model
	 * @param shape shape
	 * @return set of subjects 
	 */
	public static Set<Resource> getTargets(Model m, ShaclNodeShape shape) {
		Set<IRI> classes  = shape.getTargetClasses();
		
		if (classes == null || classes.isEmpty()) {
			return m.subjects();
		}
		Set<Resource> iris = new HashSet<>();
		
		iris.addAll(getFilteredClasses(m, classes));
		
		return iris;
	}
}
