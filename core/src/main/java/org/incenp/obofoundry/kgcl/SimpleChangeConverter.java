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

package org.incenp.obofoundry.kgcl;

import java.util.Map;

import org.incenp.linkml.core.ConverterContext;
import org.incenp.linkml.core.LinkMLRuntimeException;
import org.incenp.linkml.core.converters.ObjectConverter;
import org.incenp.obofoundry.kgcl.model.SimpleChange;

/**
 * A custom LinkML converter for SimpleChange objects.
 * <p>
 * A custom converter is needed here because, even though the
 * <code>new_value</code> and <code>old_value</code> slots of the
 * {@link SimpleChange} class are typed as strings, they are sometimes expected
 * to contain CURIEs, when the value that is being affected by the change is
 * itself an IRI (for example, when the change is a
 * <code>PredicateChange</code>).
 * <p>
 * When that happens, the fact that <code>old_value</code> (resp.
 * <code>new_value</code>) is a CURIE is indicated by the
 * <code>old_value_type</code> (resp. <code>new_value_type</code>) slot, which
 * is set to <code>curie</code>. This is a KGCL-specific mechanism that cannot
 * be handled generically at the level of the LinkML runtime, so we must deal
 * with it here.
 * <p>
 * An alternative option would be to deal with those values in a post-parsing
 * step, in which we iterate over SimpleChange-typed changes and expand their
 * <code>old_value</code> / <code>new_value</code> slots as needed. But custom
 * converters offer a nice way of ensuring that expansion is done for us during
 * the parsing phase.
 */
public class SimpleChangeConverter extends ObjectConverter {

    public SimpleChangeConverter(Class<SimpleChange> type) {
        super(type);
    }

    @Override
    public void convertTo(Map<String, Object> raw, Object dest, ConverterContext ctx) throws LinkMLRuntimeException {
        // Let the default converter do most of the job
        super.convertTo(raw, dest, ctx);

        // Then expand the old_value/new_value slots if needed
        SimpleChange c = (SimpleChange) dest;
        if ( c.getOldValueType() != null && c.getOldValueType().equals("curie") && c.getOldValue() != null ) {
            c.setOldValue(ctx.resolve(c.getOldValue()));
        }
        if ( c.getNewValueType() != null && c.getNewValueType().equals("curie") && c.getNewValue() != null ) {
            c.setNewValue(ctx.resolve(c.getNewValue()));
        }
    }

    @Override
    public Map<String, Object> serialise(Object object, boolean withIdentifier, ConverterContext ctx)
            throws LinkMLRuntimeException {
        // Same approach as above: we let the default converter do its job
        Map<String, Object> serialised = super.serialise(object, withIdentifier, ctx);

        // Then we overwrite the new/old_value slots if needed
        SimpleChange c = (SimpleChange) object;
        if ( c.getOldValueType() != null && c.getOldValueType().equals("curie") && c.getOldValue() != null ) {
            serialised.put("old_value", ctx.compact(c.getOldValue()));
        }
        if ( c.getNewValueType() != null && c.getNewValueType().equals("curie") && c.getNewValue() != null ) {
            serialised.put("new_value", ctx.compact(c.getNewValue()));
        }

        return serialised;
    }
}
