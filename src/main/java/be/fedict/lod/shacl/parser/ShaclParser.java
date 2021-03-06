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
package be.fedict.lod.shacl.parser;

import be.fedict.lod.shacl.constraints.ShaclConstraintPropertyClass;
import be.fedict.lod.shacl.shapes.ShaclNodeShape;
import be.fedict.lod.shacl.constraints.ShaclConstraintPropertyCount;
import be.fedict.lod.shacl.constraints.ShaclConstraintPropertyDatatype;
import be.fedict.lod.shacl.constraints.ShaclConstraintPropertyNode;
import be.fedict.lod.shacl.constraints.ShaclConstraintPropertyNodekind;
import be.fedict.lod.shacl.constraints.ShaclConstraintPropertyString;
import be.fedict.lod.shacl.constraints.ShaclConstraintPropertyStringLang;
import be.fedict.lod.shacl.constraints.ShaclConstraintPropertyValue;
import be.fedict.lod.shacl.shapes.ShaclPropertyShape;
import be.fedict.lod.shacl.targets.ShaclTarget;
import be.fedict.lod.shacl.targets.ShaclTargetClass;
import be.fedict.lod.shacl.targets.ShaclTargetNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.util.Models;

import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.SHACL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Parse SHACL
 * 
 * @author Bart.Hanssens
 */
public class ShaclParser {	
	private final static Logger LOG = LoggerFactory.getLogger(ShaclParser.class);
	
	/**
	 * Parse to ShaclConstraint
	 * 
	 * @param m
	 * @return constraint or null
	 * @throws ShaclParserException 
	 */
	private static ShaclConstraintPropertyCount parseCount(Model m) 
												throws ShaclParserException {
		int min = ShaclParserHelper.asInt(m, SHACL.MIN_COUNT, 0);
		int max = ShaclParserHelper.asInt(m, SHACL.MAX_COUNT, Integer.MAX_VALUE);
		
		return (min == 0 && max == Integer.MAX_VALUE) ? null
				: new ShaclConstraintPropertyCount(min, max);
	}
	
	/**
	 * Parse to ShaclConstraint
	 * 
	 * @param m
	 * @return constraint or null
	 * @throws ShaclParserException 
	 */
	private static ShaclConstraintPropertyDatatype parseType(Model m) 
												throws ShaclParserException {
		IRI type = ShaclParserHelper.asIRI(m, SHACL.DATATYPE);
		
		return (type == null) ? null 
				: new ShaclConstraintPropertyDatatype(type);
	}
	
	/**
	 * Parse to ShaclConstraint
	 * 
	 * @param m
	 * @return constraint or null
	 * @throws ShaclParserException 
	 */
	private static ShaclConstraintPropertyClass parseClass(Model m) 
												throws ShaclParserException {
		IRI cl = ShaclParserHelper.asIRI(m, SHACL.CLASS);
		
		return (cl == null) ? null 
				: new ShaclConstraintPropertyClass(cl);
	}
	
	/**
	 * Parse to ShaclConstraint
	 * 
	 * @param m
	 * @return constraint or null
	 * @throws ShaclParserException 
	 */
	private static ShaclConstraintPropertyNodekind parseKind(Model m) 
												throws ShaclParserException {
		IRI kind = ShaclParserHelper.asIRI(m, SHACL.NODE_KIND_PROP);
		
		return (kind == null) ? null 
				: new ShaclConstraintPropertyNodekind(kind);
	}
	
	private static ShaclConstraintPropertyNode parseNode(Model m) 
											throws ShaclParserException {
		Resource node = ShaclParserHelper.asResource(m, SHACL.NODE);
		
		return (node == null) ? null 
				: new ShaclConstraintPropertyNode(node);
	}

	/**
	 * Parse to ShaclConstraint
	 * 
	 * @param m
	 * @return constraint or null
	 * @throws ShaclParserException 
	 */
	private static ShaclConstraintPropertyString parseString(Model m)
												throws ShaclParserException {
		int min = ShaclParserHelper.asInt(m, SHACL.MIN_LENGTH, 0);
		int max = ShaclParserHelper.asInt(m, SHACL.MAX_LENGTH, Integer.MAX_VALUE);
		
		String str = ShaclParserHelper.asString(m, SHACL.PATTERN);
		Pattern pattern = (str == null) ? null : Pattern.compile(str);
		
		return (min == 0 && max == Integer.MAX_VALUE && pattern == null) ? null
				: new ShaclConstraintPropertyString(min, max, pattern);
	}

	/**
	 * Parse to ShaclConstraint
	 * 
	 * @param m
	 * @param m2
	 * @return constraint or null
	 * @throws ShaclParserException 
	 */
	private static ShaclConstraintPropertyStringLang parseStringLang(Model m, Model m2)
												throws ShaclParserException {
		Resource head = Models.getPropertyResource(m, Models.subject(m).get(), 
												SHACL.LANGUAGE_IN).orElse(null);
		Set<String> langs = ShaclParserHelper.collectionAsStrings(m2, head);
		if (langs == null) {
			langs = new HashSet<>();
		}
		boolean unique = ShaclParserHelper.asBool(m, SHACL.UNIQUE_LANG);

		return (unique == false && langs.isEmpty()) ? null
				: new ShaclConstraintPropertyStringLang(langs, unique);
	}
	
	/**
	 * Parse to ShaclConstraint
	 * 
	 * @param m
	 * @return constraint or null
	 * @throws ShaclParserException 
	 */
	private static ShaclConstraintPropertyValue parseValue(Model m) 
												throws ShaclParserException {
		Value value = ShaclParserHelper.asValue(m, SHACL.HAS_VALUE);
		
		return (value == null) ? null 
				: new ShaclConstraintPropertyValue(value);
	}
	
	
	public static Set<ShaclTarget> getTargetClasses(Model m, Resource subj) {
		Model t = m.filter(subj, SHACL.TARGET_CLASS, null);
		if (t != null && !t.isEmpty()) {
			return t.objects().stream().map(v -> new ShaclTargetClass((IRI) v))
										.collect(Collectors.toSet());
		}
		return Collections.EMPTY_SET;
	}
	
	public static Set<ShaclTarget> getTargetNodes(Model m, Resource subj) {
		Model t = m.filter(subj, SHACL.TARGET_NODE, null);
		if (t != null && !t.isEmpty()) {
			return t.objects().stream().map(v -> new ShaclTargetNode((Resource) v))
										.collect(Collectors.toSet());
		}
		return Collections.EMPTY_SET;
	}

	/**
	 * Parse SHACL model into a list of rules
	 * 
	 * @param shacl model
	 * @return list of rules
	 * @throws ShaclParserException
	 */
	public static Map<Resource,ShaclNodeShape> parse(Model shacl) throws ShaclParserException {
		Map<Resource,ShaclNodeShape> shapes = new HashMap<>();
		
		// Node shapes
		Model ids = shacl.filter(null, RDF.TYPE, SHACL.NODE_SHAPE);
		
		for(Statement id: ids) {
			LOG.info("Parsing node shape {}", id);
			Resource subj = id.getSubject();
			ShaclNodeShape nodeShape = new ShaclNodeShape(subj);

			// Set class targets or node targets
			Set<ShaclTarget> tcls = getTargetClasses(shacl, subj);
			if (! tcls.isEmpty()) {
				nodeShape.setTargets(tcls);
			}
			
			Set<ShaclTarget> tns = getTargetNodes(shacl, subj);
			if (! tns.isEmpty()) {
				nodeShape.setTargets(tns);
			}
			
			// Property shapes			
			Model props = shacl.filter(subj, SHACL.PROPERTY, null);

			for(Value prop: props.objects()) {
				LOG.info("Parsing property shape {}", prop);
				Resource propId = (Resource) prop;

				// Constraints
				Model constraints = shacl.filter(propId, null, null);
				
				boolean disabled = ShaclParserHelper.asBool(constraints, SHACL.DEACTIVATED);
				if (disabled) {
					LOG.info("Skipping, deactivated shape {}", propId);
					continue;
				}
				
				IRI path = ShaclParserHelper.asIRI(constraints, SHACL.PATH);
				if (path == null) {
					LOG.info("Skipping, path not set for {}", propId);
					continue;
				}
				
				ShaclPropertyShape propShape = new ShaclPropertyShape(propId);
				propShape.setPath(path);
		
				propShape.addConstraint(parseClass(constraints));	
				propShape.addConstraint(parseCount(constraints));
				propShape.addConstraint(parseKind(constraints));
				propShape.addConstraint(parseNode(constraints));
				propShape.addConstraint(parseString(constraints));
				propShape.addConstraint(parseStringLang(constraints, shacl));
				propShape.addConstraint(parseType(constraints));
				propShape.addConstraint(parseValue(constraints));
				
				nodeShape.addPropertyShape(propShape);
			}
			shapes.put(subj,nodeShape);
		}
		LOG.info("Added {} node shapes", shapes.size());
		return shapes;
	}
}
