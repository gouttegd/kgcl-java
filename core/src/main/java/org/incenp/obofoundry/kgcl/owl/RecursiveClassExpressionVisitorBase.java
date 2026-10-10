/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2023 Damien Goutte-Gattat
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

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

/**
 * A helper class to apply arbitrary transformations to class expressions.
 * <p>
 * This class walks recursively over the components of a class expression. Each
 * method returns an exact copy of the original expression. Extend this class
 * and override methods as needed to apply transformations.
 */
public class RecursiveClassExpressionVisitorBase implements OWLClassExpressionVisitorEx<OWLClassExpression> {

    protected OWLDataFactory factory;

    protected RecursiveClassExpressionVisitorBase(OWLDataFactory factory) {
        this.factory = factory;
    }

    protected OWLObjectPropertyExpression visit(OWLObjectPropertyExpression pe) {
        return pe;
    }

    @Override
    public OWLClassExpression visit(OWLClass ce) {
        return factory.getOWLClass(ce.getIRI());
    }

    @Override
    public OWLClassExpression visit(OWLObjectIntersectionOf ce) {
        HashSet<OWLClassExpression> operands = new HashSet<OWLClassExpression>();
        for ( OWLClassExpression operand : ce.getOperands() ) {
            operands.add(operand.accept(this));
        }
        return factory.getOWLObjectIntersectionOf(operands);
    }

    @Override
    public OWLClassExpression visit(OWLObjectUnionOf ce) {
        HashSet<OWLClassExpression> operands = new HashSet<OWLClassExpression>();
        for ( OWLClassExpression operand : ce.getOperands() ) {
            operands.add(operand.accept(this));
        }
        return factory.getOWLObjectUnionOf(operands);
    }

    @Override
    public OWLClassExpression visit(OWLObjectComplementOf ce) {
        return factory.getOWLObjectComplementOf(ce.getOperand().accept(this));
    }

    @Override
    public OWLClassExpression visit(OWLObjectSomeValuesFrom ce) {
        return factory.getOWLObjectSomeValuesFrom(visit(ce.getProperty()), ce.getFiller().accept(this));
    }

    @Override
    public OWLClassExpression visit(OWLObjectAllValuesFrom ce) {
        return factory.getOWLObjectAllValuesFrom(visit(ce.getProperty()), ce.getFiller().accept(this));
    }

    @Override
    public OWLClassExpression visit(OWLObjectHasValue ce) {
        return factory.getOWLObjectHasValue(visit(ce.getProperty()), ce.getFiller());
    }

    @Override
    public OWLClassExpression visit(OWLObjectMinCardinality ce) {
        return factory.getOWLObjectMinCardinality(ce.getCardinality(), visit(ce.getProperty()),
                ce.getFiller().accept(this));
    }

    @Override
    public OWLClassExpression visit(OWLObjectExactCardinality ce) {
        return factory.getOWLObjectExactCardinality(ce.getCardinality(), visit(ce.getProperty()),
                ce.getFiller().accept(this));
    }

    @Override
    public OWLClassExpression visit(OWLObjectMaxCardinality ce) {
        return factory.getOWLObjectMaxCardinality(ce.getCardinality(), visit(ce.getProperty()),
                ce.getFiller().accept(this));
    }

    @Override
    public OWLClassExpression visit(OWLObjectHasSelf ce) {
        return factory.getOWLObjectHasSelf(visit(ce.getProperty()));
    }

    @Override
    public OWLClassExpression visit(OWLObjectOneOf ce) {
        return factory.getOWLObjectOneOf(ce.getIndividuals());
    }

    @Override
    public OWLClassExpression visit(OWLDataSomeValuesFrom ce) {
        return factory.getOWLDataSomeValuesFrom(ce.getProperty(), ce.getFiller());
    }

    @Override
    public OWLClassExpression visit(OWLDataAllValuesFrom ce) {
        return factory.getOWLDataAllValuesFrom(ce.getProperty(), ce.getFiller());
    }

    @Override
    public OWLClassExpression visit(OWLDataHasValue ce) {
        return factory.getOWLDataHasValue(ce.getProperty(), ce.getFiller());
    }

    @Override
    public OWLClassExpression visit(OWLDataMinCardinality ce) {
        return factory.getOWLDataMinCardinality(ce.getCardinality(), ce.getProperty(), ce.getFiller());
    }

    @Override
    public OWLClassExpression visit(OWLDataExactCardinality ce) {
        return factory.getOWLDataExactCardinality(ce.getCardinality(), ce.getProperty(), ce.getFiller());
    }

    @Override
    public OWLClassExpression visit(OWLDataMaxCardinality ce) {
        return factory.getOWLDataMaxCardinality(ce.getCardinality(), ce.getProperty(), ce.getFiller());
    }

}
