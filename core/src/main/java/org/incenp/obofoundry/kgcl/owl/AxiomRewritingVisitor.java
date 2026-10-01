/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2026 Damien Goutte-Gattat
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the Gnu General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.incenp.obofoundry.kgcl.owl;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.OWLAxiomVisitorExAdapter;

/**
 * Helper visitor to rewrite all logical axioms to change any reference to a
 * given class into a reference to another class.
 */
public class AxiomRewritingVisitor extends OWLAxiomVisitorExAdapter<OWLAxiom> {

    private ClassRewritingVisitor rewriter;
    private OWLDataFactory factory;

    /**
     * Creates a new instance.
     * 
     * @param factory  The factory used to create the rewritten axioms.
     * @param oldClass The IRI of the class to rewrite from.
     * @param newClass The IRI of the class to rewrite to.
     */
    public AxiomRewritingVisitor(OWLDataFactory factory, IRI oldClass, IRI newClass) {
        super(null);
        rewriter = new ClassRewritingVisitor(factory, oldClass, newClass);
        this.factory = factory;
    }

    @Override
    public OWLAxiom doDefault(OWLAxiom axiom) {
        return null;
    }

    @Override
    public OWLAxiom visit(OWLSubClassOfAxiom axiom) {
        return factory.getOWLSubClassOfAxiom(axiom.getSubClass().accept(rewriter),
                axiom.getSuperClass().accept(rewriter), axiom.getAnnotations());
    }

    @Override
    public OWLAxiom visit(OWLEquivalentClassesAxiom axiom) {
        Set<OWLClassExpression> equivs = new HashSet<>();
        for ( OWLClassExpression ce : axiom.getClassExpressions() ) {
            equivs.add(ce.accept(rewriter));
        }
        return factory.getOWLEquivalentClassesAxiom(equivs, axiom.getAnnotations());
    }

    @Override
    public OWLAxiom visit(OWLDisjointClassesAxiom axiom) {
        Set<OWLClassExpression> disjoints = new HashSet<>();
        for ( OWLClassExpression ce : axiom.getClassExpressions() ) {
            disjoints.add(ce.accept(rewriter));
        }
        return factory.getOWLDisjointClassesAxiom(disjoints, axiom.getAnnotations());
    }

    @Override
    public OWLAxiom visit(OWLDisjointUnionAxiom axiom) {
        Set<OWLClassExpression> union = new HashSet<>();
        for ( OWLClassExpression ce : axiom.getClassExpressions() ) {
            union.add(ce.accept(rewriter));
        }
        return factory.getOWLDisjointUnionAxiom(axiom.getOWLClass().accept(rewriter).asOWLClass(), union,
                axiom.getAnnotations());
    }
}
