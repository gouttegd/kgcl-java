#!/usr/bin/env python3

import click
from pathlib import Path
from linkml.generators.javagen import cli


ctx = click.Context(cli)
ctx.invoke(cli,
           yamlfile=Path("core/src/main/linkml/schemas"),
           output_directory=Path("core/src/main/java"),
           true_enums=True,
           use_aliases=False,
           template_variant="org.incenp.linkml",
           template_dir=Path("core/src/main/linkml/templates"),
           visitor=["Change"])
