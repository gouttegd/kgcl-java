KGCL Java - KGCL library for Java and ROBOT
===========================================

KGCL-Java is an implementation of the [Knowledge Graph Change Language
(KGCL)](https://github.com/INCATools/kgcl) specification for the Java
language – just a hobby, won’t be big and professional like the
INCATools implementation.

Design
------
The code is organized in three distinct packages.

The `org.incenp.obofoundry.kgcl` package is intended to provide a
reasonably high-level API to parse KGCL instructions and apply the
changes to an OWL ontology. The main interface, at least for now, is the
`KGCLHelper` class.

The `org.incenp.obofoundry.kgcl.model` package provides the classes that
make up the object model for KGCL. Those classes are directly derived
from the LinkML schema that formally describes KGCL (they are generated
from a [custom LinkML-based generator](linkml/custom-javagen.py)). Those
classes may be used by those who want to manipulate KGCL objects.

The `org.incenp.obofoundry.kgcl.parser` package contains an
[ANTLR](https://www.antlr.org/)-generated parser to convert KGCL
instructions into a corresponding object representation. The classes
there may be used by those who want to parse KGCL and manipulate the
parse tree themselves (knowledge of the ANTLR runtime library is
needed).

ROBOT command
-------------
KGCL-Java provides a [ROBOT](http://robot.obolibrary.org/) command
`apply` that is intended to allow the injection of KGCL instructions as
part of a ROBOT pipeline.

Ultimately, the goal is to provide that command under the form of a
plugin that could be loaded dynamically by the standard distribution of
ROBOT, if/when ROBOT supports the use of such plugins.

In the meantime, building KGCL with the standard Maven goal `package`
will result in a Jar file that includes ROBOT and that may be used as a
standalone version of ROBOT, where the `apply` command is available (in
addition to all the standard commands).

The `apply` command takes a single KGCL instruction in its `-k` (or
`--kgcl`) option, or a KGCL file (with one instruction per line) in its
`-K` (or `--kgcl-file`) option, and apply the requested changes to the
current ontology.

Todo
----
Everything! Most types of change are not implemented yet, and there is
no handling of errors at all.

KGCL implementation chart (_parsed_ means the library can recognize the
instruction and convert it to its object representation; _applied_ means
the library can apply the change to an ontology):

| Change type         | Parsed | Applied |
| ------------------- | -------| ------- |
| rename              | yes    | yes     |
| create class        | yes    | yes     |
| obsolete            | yes    | yes     |
| delete              | no     | no      |
| move                | no     | no      |
| unobsolete          | no     | no      |
| deepen              | no     | no      |
| shallow             | no     | no      |
| change relationship | no     | no      |
| change annotation   | no     | no      |
| create edge         | yes    | yes     |
| delete edge         | no     | no      |
| create synonym      | yes    | yes     |
| remove synonym      | yes    | yes     |
| change synonym      | yes    | yes     |
| remove from subset  | no     | no      |
| add to subset       | no     | no      |
| add definition      | yes    | yes     |
| change definition   | yes    | yes     |
| remove definition   | yes    | yes     |

Copying
-------
KGCL-Java is distributed under the terms of the GNU General Public
License, version 3 or higher. The full license is included in the
[COPYING file](COPYING) of the source distribution.

KGCL-Java includes code that is automatically derived from the [KGCL
schema](https://github.com/INCATools/kgcl) (all classes under the
`org.incenp.obofoundry.kgcl.model` namespace). That code is distributed
under the same terms as the schema itself:

> Copyright (c) 2022 Mark A. Miller
>
> Permission is hereby granted, free of charge, to any person obtaining a copy
> of this software and associated documentation files (the "Software"), to deal
> in the Software without restriction, including without limitation the rights
> to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
> copies of the Software, and to permit persons to whom the Software is
> furnished to do so, subject to the following conditions:
>
> The above copyright notice and this permission notice shall be included in all
> copies or substantial portions of the Software.
>
> THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
> IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
> FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
> AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
> LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
> OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
> SOFTWARE.
