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

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;

/**
 * Helper visitor to rewrite a class expression to change any reference to a
 * given class into a reference to another class.
 */
public class ClassRewritingVisitor extends RecursiveClassExpressionVisitorBase {

    private IRI oldObject;
    private IRI newObject;

    /**
     * Creates a new instance.
     * 
     * @param factory   The factory used to create the rewritten axioms.
     * @param oldObject The IRI of the class to rewrite from.
     * @param newObject The IRI of the class to rewrite to.
     */
    public ClassRewritingVisitor(OWLDataFactory factory, IRI oldObject, IRI newObject) {
        super(factory);
        this.oldObject = oldObject;
        this.newObject = newObject;
    }

    @Override
    public OWLClassExpression visit(OWLClass ce) {
        if ( ce.getIRI().equals(oldObject) ) {
            return factory.getOWLClass(newObject);
        } else {
            return factory.getOWLClass(ce.getIRI());
        }
    }
}
