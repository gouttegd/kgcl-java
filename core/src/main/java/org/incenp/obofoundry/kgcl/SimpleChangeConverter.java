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
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.incenp.obofoundry.kgcl;

import java.util.Map;

import org.incenp.linkml.core.ConverterContext;
import org.incenp.linkml.core.LinkMLRuntimeException;
import org.incenp.linkml.core.ObjectConverter;
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
