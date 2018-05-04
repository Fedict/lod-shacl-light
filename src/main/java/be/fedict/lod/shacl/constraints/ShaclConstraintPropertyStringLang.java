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

import be.fedict.lod.shacl.shapes.ShaclShape;
import java.util.Set;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;

/**
 * Check if object value follows language rules.
 * 
 * @author Bart Hanssens
 */
public class ShaclConstraintPropertyStringLang extends ShaclConstraintProperty {
	private final Set<String> langs;
	private final boolean unique;

	@Override
	public boolean validate(Model m) {
		for (Statement s: m) {
			Value v = s.getObject();
			if (! (v instanceof Literal)) {
				addViolation(getShape(), s);
				continue;
			}
			
			Literal l = (Literal) v;
			if (! l.getDatatype().equals(XMLSchema.STRING)) {
				addViolation(getShape(), s);
				continue;
			}
			if (! langs.contains(l.getLanguage().orElse(""))) {
				addViolation(getShape(), s);
			}
		}	
		return getViolations().isEmpty();
	}
	
	/**
	 * Constructor
	 * 
	 * @param langs set language codes
	 * @param unique only one instance per language
	 */
	public ShaclConstraintPropertyStringLang(Set<String> langs, boolean unique) {
		this.langs = langs;
		this.unique = unique;
	}
}
