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
package be.fedict.lod.shacl.constraints;

import be.fedict.lod.shacl.ShaclViolation;
import be.fedict.lod.shacl.shapes.ShaclPropertyShape;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract SHACL constraint.
 * 
 * @author Bart Hanssens
 */
public abstract class ShaclConstraint {
	private final static Logger LOG = LoggerFactory.getLogger(ShaclConstraint.class);
	
	private ShaclPropertyShape shape;
	private int errors = 0;
	
	/**
	 * Validate rule
	 * 
	 * @param m triples to validate
	 * @return false if validation failed 
	 */
	public abstract boolean validate(Model m);

	public ShaclPropertyShape getShape() {
		return this.shape;
	}
	
	public void setShape(ShaclPropertyShape shape) {
		this.shape = shape;
	}

	/**
	 * Clear the violations counter
	 */
	public void clearViolations() {
		this.errors = 0;
	}
	
	/**
	 * Check the violations counter
	 * 
	 * @return true when violations occurred
	 */
	public boolean hasViolations() {
		return (this.errors > 0);
	}
	
	/**
	 * Add a violation
	 * 
	 * @param violation 
	 */
	protected void addViolation(ShaclViolation violation) {
		errors++;
		LOG.error("Violation {}", violation);
	}	

	/**
	 * Add a violation.
	 * 
	 * @param constraint
	 * @param s subject causing the violation
	 */
	protected void addViolation(ShaclConstraint constraint, IRI s) {
		addViolation(new ShaclViolation(constraint, s, null, null));		
	}
	
	/**
	 * Add a violation.
	 * 
	 * @param constraint
	 * @param s statement causing the violation
	 */
	protected void addViolation(ShaclConstraint constraint, Statement s) {
		addViolation(new ShaclViolation(constraint, s));		
	}

	/**
	 * Add a violation.
	 * 
	 * @param constraint constraint
	 * @param s subject
	 * @param p predicate
	 */
	protected void addViolation(ShaclConstraint constraint, Resource s, IRI p) {
		addViolation(new ShaclViolation(constraint, s, p, null));
	}
}
