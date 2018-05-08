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
package be.fedict.lod.shacl.shapes;

import be.fedict.lod.shacl.constraints.ShaclConstraint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Bart.Hanssens
 */
public abstract class ShaclShape {
	private final static Logger LOG = LoggerFactory.getLogger(ShaclShape.class);
	
	private final Resource id;
	private final Map<String,String> messages = new HashMap<>();
	private final List<ShaclConstraint> constraints = new ArrayList<>();

	public Map<String,String> getMessages() {
		return this.messages;
	}
	
	public String getMessage(String lang) {
		return messages.get(lang);
	}
	
	public void setMessage(String lang, String message) {
		if (message == null) {
			messages.remove(lang);
		}
		messages.put(lang, message);
	}
	
	public void addConstraint(ShaclConstraint constraint) {
		if (constraint != null) {
			LOG.info("Adding constraint {}", constraint);
			constraint.setShape(this);
			this.constraints.add(constraint);
		}
	}
	
	public List<ShaclConstraint> getConstraints() {
		return this.constraints;
	}
	
	public Resource getID() {
		return this.id;
	}
	
	public ShaclShape(Resource id) {
		this.id = id;
	}
}
