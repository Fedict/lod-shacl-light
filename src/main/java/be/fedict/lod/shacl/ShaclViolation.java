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
	private final ShaclConstraint constraint;
	private final Resource subj;
	private final IRI pred;
	private final Value obj;
	
	private final ValueFactory F = SimpleValueFactory.getInstance();
	
	@Override
	public String toString() {
		return String.format("%s (%s) [%s %s %s]", 
			constraint.getClass().getSimpleName(), constraint.getShape().getID(), 
			(subj != null) ? subj : "", (pred != null) ? pred : "", (obj != null) ? obj : "");
	}
	
	/**
	 * Get statement that generated a violation.
	 * 
	 * @return triple statement 
	 */
	public Statement getStatement() {
		return F.createStatement(subj, pred, obj);
	}

	/**
	 * Constructor
	 * 
	 * @param constraint
	 * @param stmt RDF triple
	 */
	public ShaclViolation(ShaclConstraint constraint, Statement stmt) {
		this(constraint, stmt.getSubject(), stmt.getPredicate(), stmt.getObject());
	}
	
	/**
	 * Constructor
	 * 
	 * @param constraint
	 * @param s subject
	 * @param p object
	 * @param o predicate
	 */
	public ShaclViolation(ShaclConstraint constraint, Resource s, IRI p, Value o) {
		this.constraint = constraint;
		this.subj = s;
		this.pred = p;
		this.obj = o;
	}
}
