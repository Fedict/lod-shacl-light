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
import be.fedict.lod.shacl.shapes.ShaclShape;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;

/**
 * Abstract SHACL constraint.
 * 
 * @author Bart.Hanssens
 */
public abstract class ShaclConstraint {
	private ShaclShape shape = null;
	private final List<ShaclViolation> violations = new ArrayList<>();
	
	@Override
	public String toString() {
		return "Shacl Constraint " + this.getClass().getSimpleName();
	}
	
	/**
	 * Validate rule
	 * 
	 * @param m triples to validate
	 * @return false if validation failed 
	 */
	public abstract boolean validate(Model m);
	
	/**
	 * Get all violations.
	 * 
	 * @return list of violations 
	 */
	public List<ShaclViolation> getViolations() {
		return this.violations;
	}

	
	public ShaclShape getShape() {
		return this.shape;
	}
	
	public void setShape(ShaclShape shape) {
		this.shape = shape;
	}
	
	/**
	 * Add a violation
	 * 
	 * @param violation 
	 */
	protected void addViolation(ShaclViolation violation) {
		violations.add(violation);
	}	
	
	/**
	 * Add a violation.
	 * 
	 * @param shape shape
	 * @param s statement causing the violation
	 */
	protected void addViolation(ShaclShape shape, Statement s) {
		addViolation(new ShaclViolation(shape, s));		
	}

	/**
	 * Add a violation.
	 * 
	 * @param shape shape
	 * @param s subject
	 * @param p predicate
	 */
	protected void addViolation(ShaclShape shape, Resource s, IRI p) {
		addViolation(new ShaclViolation(shape, s, p, null));
	}
}
