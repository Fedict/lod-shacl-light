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

import static be.fedict.lod.shacl.ShaclValidator.select;
import be.fedict.lod.shacl.constraints.ShaclConstraint;
import be.fedict.lod.shacl.constraints.ShaclConstraintPropertyClass;
import be.fedict.lod.shacl.targets.ShaclTarget;
import be.fedict.lod.shacl.targets.ShaclTargetClass;
import be.fedict.lod.shacl.targets.ShaclTargetNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.vocabulary.RDF;

/**
 *
 * @author Bart.Hanssens
 */
public class ShaclNodeShape extends ShaclShape {
	private final List<ShaclPropertyShape> properties = new ArrayList<>();
	private Set<ShaclTarget> targets = new HashSet<>();
	
	@Override
	public String toString() {
		return "node=" + getID() + ", targets=" + targets;
	}
	
	public Set<ShaclTarget> getTargets() {
		return this.targets;
	}
	
	public void setTargets(Set<ShaclTarget> targets) {
		this.targets = targets;
	}
	
	/**
	 * Get subject IRIs by SHACL targetClass
	 * 
	 * @param m model
	 * @param classes target classes
	 * @return set of subjects
	 */
	public static Set<Resource> getSubjectPerClass(Model m, Set<IRI> classes) {
		Set<Resource> subjs = new HashSet<>();
		for(Resource cl: classes) {
			subjs.addAll(m.filter(null, RDF.TYPE, cl).subjects());
		}
		return subjs;
	}
	
	/**
	 * Get subject IRIs of the targets of a shape
	 * 
	 * @param m model
	 * @return set of subjects 
	 */
	public Set<Resource> getTargetIDs(Model m) {
		if (targets == null) {
			return m.subjects();
		}

		Set<Resource> iris = new HashSet<>();
		
		Set<IRI> classes = targets.stream()
									.filter(t -> t instanceof ShaclTargetClass)
									.map(t -> ((ShaclTargetClass) t).getTargetClass())
									.collect(Collectors.toSet());
		if (!classes.isEmpty()) {
			iris.addAll(getSubjectPerClass(m, classes));
		}
		
		Set<Resource> nodes = targets.stream()
									.filter(t -> t instanceof ShaclTargetNode)
									.map(t -> ((ShaclTargetNode) t).getTargetNode())
									.collect(Collectors.toSet());
		iris.addAll(nodes);
		
		return iris;
	}
	
	/**
	 * Add property shape
	 * 
	 * @param shape 
	 */
	public void addPropertyShape(ShaclPropertyShape shape) {
		properties.add(shape);
		shape.setNodeShape(this);
	}

	public int validate(Model m) {
		int errors = 0;
		
		Set<Resource> subjs = getTargetIDs(m);
		
		for (ShaclPropertyShape p: properties) {
			List<ShaclConstraint> constraints = p.getConstraints();
			Model filtered = select(m, subjs, p.getPath());
				
			for (ShaclConstraint c: constraints) {
				Model model = (c instanceof ShaclConstraintPropertyClass) ? m : filtered;
	
				if (! c.isValid(model, subjs)) {
					errors++;
				}
			}
		}
		return errors;
	}
	/**
	 * Get property shapes
	 * 
	 * @return 
	 */
	public List<ShaclPropertyShape> getPropertyShapes() {
		return this.properties;
	}
	
	/**
	 * Constructor
	 * 
	 * @param id 
	 */
	public ShaclNodeShape(Resource id) {
		super(id);
	}
}
