/*
 * Copyright (c) 2018, Bart Hanssens <bart.hanssens@fedict.be>
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

import be.fedict.lod.shacl.ShaclValidator;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rdf4j.rio.RDFFormat;

import org.junit.jupiter.api.BeforeAll;

/**
 *
 * @author Bart Hanssens
 */
public abstract class ShaclConstraintTest {
	public ShaclValidator validator;
	
	public boolean validate(String f) throws IOException {
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(f);
		return validator.validate(is, RDFFormat.TURTLE);
	}
	
	@BeforeAll
	public void init() throws IOException {
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("shacl.ttl");
		validator = new ShaclValidator(is, RDFFormat.TURTLE);
	}
}
