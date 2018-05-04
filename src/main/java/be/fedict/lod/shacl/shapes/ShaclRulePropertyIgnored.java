/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.fedict.lod.shacl.shapes;

import be.fedict.lod.shacl.constraints.ShaclConstraintProperty;
import java.util.List;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;

/**
 *
 * @author Bart.Hanssens
 */
public class ShaclRulePropertyIgnored extends ShaclConstraintProperty {
	private final List<IRI> ignored; 
	
	@Override
	public boolean validate(Model m) {
		return true;
	}

	
	public ShaclRulePropertyIgnored(List<IRI> ignored) {
		super();
		this.ignored = ignored;
	}
}
