/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2023 Damien Goutte-Gattat
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
