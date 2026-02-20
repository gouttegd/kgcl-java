#!/usr/bin/env python3

import os
import click
from linkml.generators.javagen import JavaGenerator
from linkml.utils.generator import shared_arguments
from jinja2 import Template

class_template = """\
package org.incenp.obofoundry.kgcl.model;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.incenp.linkml.core.annotations.Converter;
import org.incenp.linkml.core.annotations.Identifier;
import org.incenp.linkml.core.annotations.Inlining;
import org.incenp.linkml.core.annotations.SlotName;
import org.incenp.linkml.core.annotations.TypeDesignator;
import org.incenp.linkml.core.CurieConverter;
import org.incenp.linkml.core.InliningMode;

/**
 * {{ cls.description|e }}
 */
@Data
@EqualsAndHashCode(callSuper={% if gen.parent_has_slots(cls) %}true{% else %}false{% endif %})
public class {{ cls.name }} {% if cls.is_a -%} extends {{ cls.is_a }} {%- endif %} {
{%- for f in cls.fields %}
    {%- if f.source_slot.identifier %}
    @Identifier
    {%- endif %}
    {%- if f.source_slot.designates_type %}
    @TypeDesignator
    {%- endif %}
    {%- if f.source_slot.name != f.name %}
    @SlotName("{{ f.source_slot.name }}")
    {%- endif %}
    {%- if f.source_slot.inlined %}
    {%- if f.source_slot.inlined_as_list %}
    @Inlining(InliningMode.LIST)
    {%- else %}
    @Inlining(InliningMode.DICT)
    {%- endif %}
    {%- endif %}
    {%- if f.source_slot.range == "uriorcurie" %}
    @Converter(CurieConverter.class)
    {%- endif %}
    private {{ f.range }} {{ f.name }};
{%- endfor -%}

{% if cls.name == "Change" or gen.has_ancestor(cls, "Change") %}
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
{% endif -%}
}
"""

visitor_template = """\
package org.incenp.obofoundry.kgcl.model;

/**
 * A visitor interface for {@link Change} objects. Implement that
 * interface to apply arbitrary processing to all types of KGCL changes.
 */
public interface IChangeVisitor<T> {
    T visit(Change v);
{%- for descendant in gen.get_descendants("Change") %}
    T visit({{ descendant }} v);
{%- endfor %}
}
"""

class CustomJavaGenerator(JavaGenerator):
    """
    A custom Java code generator that provides helper methods
    to templates and allows to generate supplementary files
    in addition to the model-derived classes.
    """

    def serialize(self, directory, extras=[]):
        """Generate the Java code.

        :param directory: output directory where Java files are to be written
        :param extras: a list of tuples (f,t) representing supplementary
            files to generate, where f is the filename and t a custom template
        """

        os.makedirs(directory, exist_ok=True)

        template_obj = Template(class_template)
        oodocs = self.create_documents()
        for oodoc in oodocs:
            if oodoc.classes:
                cls = oodoc.classes[0]
                if cls.mixin:
                    continue
                code = template_obj.render(doc=oodoc, cls=cls, gen=self)
            else:
                # Use default enum template
                enum_templ = self.template_cache.get_template(oodoc.name, "enum")
                code = enum_templ.render(doc=oodoc, enum=oodoc.enums[0], gen=self)

            path = os.path.join(directory, f"{oodoc.name}.java")
            with open(path, "w", encoding="UTF-8") as stream:
                stream.write(code)

        for filename, template in extras:
            template_obj = Template(template)
            code = template_obj.render(gen=self)
            path = os.path.join(directory, filename)
            with open(path, "w", encoding="UTF-8") as stream:
                stream.write(code)

    def has_ancestor(self, cls, name):
        """Check for a ancestor in a class inheritance tree.

        :param cls: a class object
        :param name: a class name
        :return: True if cls as any ancestor with the indicated name
        """

        if cls.is_a is None:
            return False
        elif cls.is_a == name:
            return True
        else:
            return self.has_ancestor(self.schemaview.get_class(cls.is_a), name)

    def parent_has_slots(self, cls):
        """Check if the parent of the class has slots of its own.

        :param cls: a class object
        :return: True if cls has a parent with its own slots
        """

        if cls.is_a is None:
            return False
        else:
            parent_slots = self.schemaview.class_slots(cls.is_a)
            return len(parent_slots) > 0

    def get_descendants(self, name, _descendants=[]):
        """Get all the descendants of a class.

        :param name: a class name
        :return: a flat list of the names of all classes that inherit
            from the named class
        """

        for child in self.schemaview.class_children(name):
            _descendants.append(child)
            self.get_descendants(child, _descendants)
        return _descendants


@click.option("--output-directory", default="output", show_default=True)
@click.command()
def cli(output_directory=None):
    for schema in ["basics", "ontology_model", "prov", "kgcl"]:
        gen = CustomJavaGenerator(schema + ".yaml", true_enums=True, package="org.incenp.obofoundry.kgcl.model")
        extra_templates = []
        if schema == "kgcl":
            extra_templates.append(("IChangeVisitor.java", visitor_template))
        gen.serialize(output_directory, extra_templates)

if __name__ == "__main__":
    cli()
