#!/usr/bin/env python3

import click
from pathlib import Path
from linkml.generators.javagen import JavaGenerator


@click.option("--output-directory", show_default=True,
              default=Path("core/src/main/java/org/incenp/obofoundry/kgcl/model"))
@click.option("--linkml-directory", show_default=True,
              type=click.Path(exists=True, dir_okay=True, file_okay=False),
              default=Path("core/src/main/linkml"))
@click.command()
def cli(output_directory, linkml_directory):
    for schema in ["basics", "ontology_model", "prov", "kgcl"]:
        gen = JavaGenerator(linkml_directory / "schemas" / (schema + ".yaml"),
                            true_enums=True,
                            package="org.incenp.obofoundry.kgcl.model",
                            template_dir=linkml_directory / "templates")
        visitors = []
        if schema == "kgcl":
            visitors = ["Change"]
        gen.serialize(output_directory, visitors=visitors, template_variant="org.incenp.linkml")

if __name__ == "__main__":
    cli()
