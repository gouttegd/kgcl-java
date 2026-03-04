#!/usr/bin/env python3

import click
from pathlib import Path
from linkml.generators.javagen import JavaGenerator


@click.option("--output-directory", default="output", show_default=True)
@click.command()
def cli(output_directory=None):
    for schema in ["basics", "ontology_model", "prov", "kgcl"]:
        gen = JavaGenerator(schema + ".yaml",
                            true_enums=True,
                            package="org.incenp.obofoundry.kgcl.model",
                            template_dir=Path("templates"))
        visitors = []
        if schema == "kgcl":
            visitors = ["Change"]
        gen.serialize(output_directory, visitors=visitors, template_variant="kgcl")

if __name__ == "__main__":
    cli()
