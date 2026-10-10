/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2026 Damien Goutte-Gattat
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   (1) Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 *   (2) Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 *   (3) Neither the name of the copyright holder nor the names its
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
