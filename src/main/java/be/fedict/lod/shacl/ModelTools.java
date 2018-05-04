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
package be.fedict.lod.shacl;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

/**
 *
 * @author Bart.Hanssens
 */
public class ModelTools {
	private final static ValueFactory F = SimpleValueFactory.getInstance();

	
	/**
	 * Remove the context / named graph info from all statements, 
	 * effectively turning all RDF quads into triples.
	 * In other words: copy all triples from named graphs to one default graph.
	 * 
	 * @param m1 model with triples in various graphs
	 * @return model
	 */
	public static Model removeContexts(Model m1) {
		Model m = new LinkedHashModel();
		m1.forEach(s -> 
			m.add(F.createStatement(s.getSubject(), s.getPredicate(), s.getObject())));
		return m;
	} 
	
	/**
	 * Add or update the context / named graph info from all statements, 
	 * effectively turning all RDF triples into quads.
	 * In other words: copy all triples into a specific named graph.
	 * 
	 * @param m1 model
	 * @param ctx named graph IRI
	 * @return model
	 */
	public static Model addContext(Model m1, IRI ctx) {
		Model m = new LinkedHashModel();
		m1.forEach(s -> 
			m.add(F.createStatement(s.getSubject(), s.getPredicate(), s.getObject(), ctx)));
		return m;
	}
	
	/**
	 * Get a model containing all statements from m1 that are not in m2,
	 * ignoring named graph info
	 * <p>
	 * To get the difference between an 'original' and a 'modified' model, 
	 * apply this function twice:
	 * <ul>
	 * <li>added statements: minus(modif, orig)
	 * <li>deleted statements: minus(orig, modif)
	 * </ul>
	 * 
	 * @param m1 first model
	 * @param m2 second model
	 * @return
	 */
	public static Model minus(Model m1, Model m2) {
		Model copyM1 = new LinkedHashModel();
		copyM1.addAll(m1);
		copyM1.removeAll(m2);
		
		return copyM1;
	}
	
	/**
	 * Get 'root' IRIs,triple subject IRIs that are not linked to by object IRIs. 
	 * <p>
	 * An RDF model can contain circular references (inverse properties like 
	 * hasVersion / isVersionOf), so this method optionally takes a set 
	 * of inverse properties that should be ignored.
	 * 
	 * @param m model
	 * @param ignorePred predicate(s) to be ignored
	 * @return 'root' subjects
	 */
	public static Set<Resource> rootSubjects(Model m, Set<IRI> ignorePred) {
		Set<Resource> subjs = new HashSet<>();
		Set<Resource> objs = new HashSet<>();
		
		for(Statement s : m) {
			// check for inverse properties that should be ignored
			if (! ignorePred.contains(s.getPredicate())) {
				subjs.add(s.getSubject());
				// only IRIs, ignore literals
				if (s.getObject() instanceof Resource) {
					objs.add((Resource) s.getObject());
				}
			}
		}
		subjs.removeAll(objs);
		
		return subjs;
	}
	
	/**
	 * Select statements from model 
	 * 
	 * @param m model
	 * @param s subject
	 * @param p predicate
	 * @return filtered model
	 */
	public static Model select(Model m, Set<Resource> s, IRI p) {
		Model m2 = new LinkedHashModel();
		for (Statement st: m2) {
			if (s.contains(st.getSubject()) && 
					(p == null || st.getPredicate().equals(p))) {
				m2.add(st);
			}
		}
		return m2;
	}
	
	/**
	 * Create statement with null object
	 * 
	 * @param s subject as string
	 * @param p predicate as string
	 * @return 
	 */
	public static Statement createStatement(String s, String p) {
		return F.createStatement(F.createIRI(s), F.createIRI(p), null);
	}
}
