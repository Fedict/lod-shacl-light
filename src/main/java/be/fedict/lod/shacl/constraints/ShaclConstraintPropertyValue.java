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

import java.util.Set;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.util.Models;

/**
 * Check if object value follows language count and pattern.
 * 
 * @author Bart.Hanssens
 */
public class ShaclConstraintPropertyValue extends ShaclConstraintProperty {
	private final Value value;
	
	@Override
	public String toString() {
		return String.format("%s [path=%s, value=%s]",
			this.getClass().getSimpleName(), getPath(), value);
	}

	@Override
	public boolean validate(Model m) {
		clearViolations();
		
		Set<IRI> subjs = Models.subjectIRIs(m);
		
		for(IRI subj: subjs) {
			// check if at least one triple has the specified value 
			boolean hasval = false;
			for (Statement s: m.filter(subj, null, null)) {
				Value v = s.getObject();
				if (value.equals(v)) {
					hasval = true;
					break;
				}
			}
			if (hasval == false) {
				addViolation(this, subj);
			}
		}
		return !hasViolations();
	}
	
	/**
	 * Constructor
	 * 
	 * @param value value
	 */
	public ShaclConstraintPropertyValue(Value value) {
		this.value = value;
	}
}
