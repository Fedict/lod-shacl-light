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
import java.util.regex.Pattern;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;

/**
 * Check if object value follows language count and pattern.
 * 
 * @author Bart.Hanssens
 */
public class ShaclConstraintPropertyString extends ShaclConstraintProperty {
	private final int min;
	private final int max;
	private final Pattern pattern;

	@Override
	public String toString() {
		return String.format("%s [path=%s, min=%d, max=%d, pattern=%s]",
			this.getClass().getSimpleName(), getPathStr(), min, max, 
			(pattern != null) ? pattern : "<none>");
	}

	@Override
	protected void validate(Model m, Set<Resource> targets) {
		for (Statement s: m) {
			Value v = s.getObject();
			if (! (v instanceof Literal)) {
				addViolation(this, s);
				continue;
			}
			Literal l = (Literal) v;
			if (! l.getDatatype().equals(XMLSchema.STRING)) {
				addViolation(this, s);
			}
			
			String str = l.getLabel();
			if (str.length() < min || str.length() > max) {
				addViolation(this, s);
				continue;
			}
				
			if (pattern != null && !pattern.matcher(str).matches()) {
				addViolation(this, s);
			}
		}
	}
	
	/**
	 * Constructor
	 * 
	 * @param min minium count
	 * @param max maximum count
	 * @param pattern string pattern
	 */
	public ShaclConstraintPropertyString(int min, int max, Pattern pattern) {
		this.min = min;
		this.max = max;
		this.pattern = pattern;
	}
}
