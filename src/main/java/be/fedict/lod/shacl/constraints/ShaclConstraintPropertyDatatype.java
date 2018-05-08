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

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.datatypes.XMLDatatypeUtil;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;

/**
 *
 * @author Bart.Hanssens
 */
public class ShaclConstraintPropertyDatatype extends ShaclConstraintProperty {
	private final IRI datatype;
	
	@Override
	public String toString() {
		return String.format("%s [path=%s, type=%s]",
							this.getClass().getSimpleName(), getPath(), datatype);
	}
	
	public IRI getDataType() {
		return this.datatype;
	}
	
	private boolean validateDataType(Value v, IRI datatype) {
		if (v == null) {
			return false;
		}
		if (! (v instanceof Literal)) {
			return false;
		}
		
		IRI t = ((Literal) v).getDatatype();
		if (! datatype.equals(t)) {
			return false;
		}
		
		String s = v.stringValue();

		if (datatype.equals(XMLSchema.BOOLEAN)) {
			return XMLDatatypeUtil.isValidBoolean(s);
		}
		if (datatype.equals(XMLSchema.INT)) {
			return XMLDatatypeUtil.isValidInt(s);
		}
		if (datatype.equals(XMLSchema.INTEGER)) {
			return XMLDatatypeUtil.isValidInteger(s);
		}
		if (datatype.equals(XMLSchema.DATE)) {
			return XMLDatatypeUtil.isValidDate(s);
		}
		if (datatype.equals(XMLSchema.DATETIME)) {
			return XMLDatatypeUtil.isValidDateTime(s);
		}
		if (datatype.equals(XMLSchema.ANYURI)) {
			return XMLDatatypeUtil.isValidAnyURI(s);
		}
		return true;
	}
	
	@Override
	public boolean validate(Model m) {
		for(Statement s: m) {
			if (! validateDataType(s.getObject(), this.datatype)) {
				addViolation(getShape(), s);
			}
		}
		return getViolations().isEmpty();
	}
	
	/**
	 * Constructor
	 * 
	 * @param datatype 
	 */
	public ShaclConstraintPropertyDatatype(IRI datatype) {
		this.datatype = datatype;
	}
}
