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

import be.fedict.lod.shacl.constraints.ShaclConstraint;
import be.fedict.lod.shacl.constraints.ShaclConstraintPropertyCount;
import be.fedict.lod.shacl.parser.ShaclParser;
import be.fedict.lod.shacl.parser.ShaclParserHelper;
import be.fedict.lod.shacl.shapes.ShaclNodeShape;
import be.fedict.lod.shacl.shapes.ShaclPropertyShape;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;

import java.util.List;
import java.util.Set;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;


/**
 * SHACL light validation engine.
 * 
 * @author Bart.Hanssens
 */
public class ShaclValidator {
	private final Set<ShaclNodeShape> shapes; 
	
	/**
	 * Select statements from model 
	 * 
	 * @param m model
	 * @param s set of subjects
	 * @param p predicate
	 * @return filtered model
	 */
	public static Model select(Model m, Set<Resource> s, IRI p) {
		Model m2 = new LinkedHashModel();
		for (Statement st: m) {
			if (s.contains(st.getSubject()) && (p == null || st.getPredicate().equals(p))) {
				m2.add(st);
			}
		}
		return m2;
	}
	
	/**
	 * Validate an RDF triple model.
	 * 
	 * @param m triples
	 * @return false in case of violations
	 */
	public boolean validate(Model m) {
		int errors = 0;
		
		for (ShaclNodeShape n: shapes) {
			Set<Resource> targets = ShaclParserHelper.getTargets(m, n);
			
			List<ShaclPropertyShape> properties = n.getProperties();
			
			for (ShaclPropertyShape p: properties) {
				List<ShaclConstraint> constraints = p.getConstraints();
				
				for (ShaclConstraint c: constraints) {
					IRI path = p.getPath();
					
					// Exception: min property count might need more triples,
					// otherwise it is not possible to check for missing properties
					if (c instanceof ShaclConstraintPropertyCount) {
						if (((ShaclConstraintPropertyCount) c).getMin() > 0) {
							path = null;
						}
					}
					
					Model m2 = select(m, targets, path);
					if (! c.validate(m2)) {
						errors++;
					}
				}
			}
		}
		return (errors == 0);
	}

	/**
	 * Validate a file
	 * 
	 * @param f
	 * @return false in case of violations
	 * @throws IOException 
	 */
	public boolean validate(File f) throws IOException {
		RDFFormat fmt = Rio.getParserFormatForFileName(f.getName())
							.orElseThrow(() -> new IOException("File type not supported"));
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f))) {
			return validate(bis, fmt);
		}
	}
	
	/**
	 * Validate an RDF inputstream
	 * 
	 * @param is inputstream
	 * @param fmt format
	 * @return false in case of violations
	 * @throws IOException
	 */
	public boolean validate(InputStream is, RDFFormat fmt) throws IOException {
		Model m = Rio.parse(is, "http://localhost", fmt);
		return validate(m);
	}

	/**
	 * Constructor
	 * 
	 * @param f SHACL file
	 * @throws java.io.IOException
	 */
	public ShaclValidator(File f) throws IOException {
		RDFFormat fmt = Rio.getParserFormatForFileName(f.getName())
							.orElseThrow(() -> new IOException("File type not supported"));
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f))) {
			Model m = Rio.parse(bis, "http://localhost", fmt);
			this.shapes = ShaclParser.parse(m);
		}
	}
	
	/**
	 * Constructor
	 * 
	 * @param is SHACL inputstream
	 * @param fmt format
	 * @throws IOException 
	 */
	public ShaclValidator(InputStream is, RDFFormat fmt) throws IOException {
		Model m = Rio.parse(is, "http://localhost", fmt);
		this.shapes = ShaclParser.parse(m);
	}

	/**
	 * Constructor
	 * 
	 * @param shapes set of node shapes
	 */
	public ShaclValidator(Set<ShaclNodeShape> shapes) {
		this.shapes = shapes;
	}
}
