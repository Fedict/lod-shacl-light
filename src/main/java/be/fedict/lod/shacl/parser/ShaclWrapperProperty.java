/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fedict.lod.shacl.parser;

import be.fedict.lod.shacl.shapes.ShaclShape;
import be.fedict.lod.shacl.constraints.ShaclConstraintProperty;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.RDFS;

/**
 *
 * @author Bart.Hanssens
 */
public class ShaclWrapperProperty {
	private final ShaclShape shape;
	private final IRI path;
	
	public ShaclConstraintProperty wrap(ShaclConstraintProperty rule) {
		if (rule == null) {
			return rule;
		}
		//rule.setShape(shape);
		rule.setPath(path);
		return rule;
	}
	
	public ShaclWrapperProperty(ShaclShape shape, IRI path) {
		this.shape = shape;
		this.path = path;
	}
}
