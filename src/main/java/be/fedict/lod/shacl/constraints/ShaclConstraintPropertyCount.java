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

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;

/**
 * Check if a property occurs between min and max times.
 * 
 * @author Bart Hanssens
 */
public class ShaclConstraintPropertyCount extends ShaclConstraintProperty {	
	private final int min;
	private final int max;
	
	@Override
	public String toString() {
		return String.format("%s [path=%s, min=%d, max=%d]",
							this.getClass().getSimpleName(), getPathStr(), min, max);
	}
	
	/**
	 * Get minimum number of occurrences
	 * 
	 * @return 0 or more
	 */
	public int getMin() {
		return this.min;
	}
	
	/**
	 * Get maximum number of occurrences
	 * 
	 * @return max(int) or less
	 */
	public int getMax() {
		return this.max;
	}
	
	@Override
	protected void validate(Model m, Set<Resource> targets) {
		for (Resource subj: targets) {
			int cnt = m.filter(subj, getPath(), null).size();
			if (cnt < min || cnt > max) {			
				addViolation(this, subj, getPath());
			}
		}
	}
	
	/**
	 * Constructor
	 * 
	 * @param min minimum count
	 * @param max maximum count
	 */
	public ShaclConstraintPropertyCount(int min, int max) {
		this.min = min;
		this.max = max;
	}
}