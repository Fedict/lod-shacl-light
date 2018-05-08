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
import be.fedict.lod.shacl.parser.ShaclParser;
import be.fedict.lod.shacl.parser.ShaclParserHelper;
import be.fedict.lod.shacl.shapes.ShaclNodeShape;
import be.fedict.lod.shacl.shapes.ShaclPropertyShape;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SHACL light validation engine.
 * 
 * @author Bart.Hanssens
 */
public class ShaclValidator {
	private final static Logger LOG = LoggerFactory.getLogger(ShaclValidator.class);
	
	private final Set<ShaclNodeShape> shapes; 
	private final List<ShaclViolation> violations = new ArrayList<>();
	
	/**
	 * Get the list of violations (empty list when there are no violations)
	 * 
	 * @return list of violations
	 */
	public List<ShaclViolation> getViolations() {
		return this.violations;
	}

	/**
	 * Validate an RDF file.
	 * 
	 * @param is inputstream
	 * @param fmt format
	 * @return false in case of violations
	 */
	public boolean validate(InputStream is, RDFFormat fmt) throws IOException {
		Model m = Rio.parse(is, "http://localhost", fmt);
		if (m == null) { throw new IOException(); }
		return validate(m);
	}
	
	/**
	 * Validate an RDF triple model.
	 * 
	 * @param m triples
	 * @return false in case of violations
	 */
	public boolean validate(Model m) {
		violations.clear();
		
		for (ShaclNodeShape n: shapes) {
			Set<Resource> targets = ShaclParserHelper.getTargets(m, n);
			if (targets == null || targets.isEmpty()) {
				LOG.warn("Skipping validation, no targets for node shape {}", n.getID());
				continue;
			}
			
			List<ShaclPropertyShape> properties = n.getProperties();
			if (properties == null || properties.isEmpty()) {
				LOG.warn("Skipping validation, no property shapes for node shape {}", n.getID());
				continue;
			}
			
			for (ShaclPropertyShape p: properties) {
				List<ShaclConstraint> constraints = p.getConstraints();
				if (constraints == null || constraints.isEmpty()) {
					LOG.warn("Skipping validation, no constraints for property shape {}", p.getID());
					continue;
				}
				
				for (ShaclConstraint c:  constraints) {
					Model m2 = ModelTools.select(m, targets, p.getPath());
					if (! c.validate(m2)) {
						violations.addAll(c.getViolations());
						c.getViolations().clear();
					}
				}
			}
		}
		return getViolations().isEmpty();
	}

	/**
	 * Constructor
	 * 
	 * @param is inputstream
	 * @param fmt format
	 * @throws IOException 
	 */
	public ShaclValidator(InputStream is, RDFFormat fmt) throws IOException {
		Model m = Rio.parse(is, "http://localhost", fmt);
				if (m == null) { throw new IOException(); }
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
