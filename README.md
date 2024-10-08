[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.10582052.svg)](https://doi.org/10.5281/zenodo.10582052)

KGCL Java - KGCL library for Java and ROBOT
===========================================

KGCL-Java is an implementation of the [Knowledge Graph Change Language
(KGCL)](https://github.com/INCATools/kgcl) specification for the Java
language – just a hobby, won’t be big and professional like the
INCATools implementation.

Design
------
The code is organized in four distinct packages.

The `org.incenp.obofoundry.kgcl` package is intended to provide a
reasonably high-level API to serialise, deserialise, and manipulate KGCL
objects.

The `org.incenp.obofoundry.kgcl.model` package provides the classes that
make up the object model for KGCL. Those classes are directly derived
from the LinkML schema that formally describes KGCL (they are generated
from a [custom LinkML-based generator](linkml/custom-javagen.py)). Those
classes may be used by those who want to manipulate KGCL objects.

The `org.incenp.obofoundry.kgcl.owl` package provides classes to apply
KGCL-described changes to a OWL ontology using the OWLAPI.

The `org.incenp.obofoundry.kgcl.parser` package contains an
[ANTLR](https://www.antlr.org/)-generated parser to convert KGCL
instructions into a corresponding object representation. The classes
there may be used by those who want to parse KGCL and manipulate the
parse tree themselves (knowledge of the ANTLR runtime library is
needed).

Library API
-----------

### Parsing a KGCL file
Use the `org.incenp.obofoundry.kgcl.KGCLReader` to parse a KGCL file:

```java
import org.incenp.obofoundry.kgcl.KGCLReader;
import org.incenp.obofoundry.kgcl.model.Change;
import java.util.List;

KGCLReader reader = new KGCLReader([filename, file, InputStream, or Reader object]);
if ( reader.read() ) {
    // File successfully parsed
    List<Change> changes = read.getChangeset();
} else {
    // Syntax errors found
    for ( KGCLSyntaxError error : reader.getErrors() ) {
        System.err.printf("syntax error %s\n", error.toString());
    }
}
```

The reader will throw the standard exceptions (`FileNotFoundException`,
`IOException`) if some non-KGCL-related I/O errors occur.

Before attempting to read, you can pass a OWLAPI `PrefixManager` to the
reader, which will use it to expand CURIEs in the KGCL file. For
example, assuming you have an ontology in a file format that supports
prefixes:

```java
OWLOntologyManager mgr = OWLManager.createOWLOntologyManager();
OWLOntology o = mgr.loadOntologyFrom...;
reader.setPrefixManager(mgr.getOntologyFormat(o).asPrefixOWLOntologyFormat());
```

### Writing a KGCL file
Use the `org.incenp.obofoundry.kgcl.KGCLWriter` to serialise KGCL
change objects into the KGCL language:

```java
import org.incenp.obofoundry.kgcl.KGCLWriter
import org.incenp.obofoundry.kgcl.model.Change;

KGCLWriter writer = new KGCLWriter([filename, file, or OutputStream object]);
writer.write(change); // write an individual change;
writer.write(changes); // write several changes in one step,
                       // changes is a List<Change>
writer.close();
```

The writer will throw standard I/O exceptions upon I/O errors.

As for parsing, you can pass a OWL API `PrefixManager` to the writer to
condense identifiers into short, “CURIEfied” identifiers.

### Applying the changes to a OWL ontology
Use the `org.incenp.obofoundry.kgcl.owl.OntologyPatcher` to apply changes to
an ontology:

```java
import org.incenp.obofoundry.kgcl.RejectedChange;
import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.owl.OntologyPatcher;
import org.semanticweb.owlapi.model.OWLOntology;
import java.util.List;

OWLOntology o = ...;
List<Change> changes = ...;
OWLReasoner reasoner = ...;
OntologyPatcher patcher = new OntologyPatcher(o, reasoner);
if ( patcher.apply(changes, false) ) {
    // All changes were applied
    o.saveOntology(...);
} else {
    // Some changes could not be applied
    for ( RejectedChange rc : patcher.getRejectedChanges() ) {
        System.err.printf("Change rejected: %s\n", rc.getReason());
    }
}
```

ROBOT command
-------------
KGCL-Java provides a [ROBOT](http://robot.obolibrary.org/) command
`apply` that is intended to allow the injection of KGCL instructions as
part of a ROBOT pipeline.

To enable it, install the release artefact `kcgl-robot-plugin-X.Y.Z.jar`
in a directory where ROBOT (1.9.5 minimum) searches for plugins, and
rename it to a shorter name such as `kgcl.jar`.

Building KGCL with the standard Maven goal `package` will also produce a
Jar file that includes ROBOT and that may be used as a standalone
version of ROBOT, where the `apply` command is available as a built-in
command (in addition to all the standard commands).

The `apply` command takes a single KGCL instruction in its `-k` (or
`--kgcl`) option, or a KGCL file (with one instruction per line) in its
`-K` (or `--kgcl-file`) option, and apply the requested changes to the
current ontology. Both options can be used repeatedly.

```sh
robot apply -i input.ofn -K changes.kgcl -o output.ofn
```

If some changes cannot be applied because they don’t match the contents
of the ontology (for example, trying to add a label to a class that does
not exist), they will be written back to a file with the same name as
the original KGCL file, with a ".rej" extension appended.

If the `--no-partial-apply` option is used, then the command will refuse
to apply any changes if at least one change cannot be applied.

Todo
----
KGCL implementation chart (_parsed_ means the library can recognize the
instruction and convert it to its object representation; _applied_ means
the library can apply the change to an ontology):

| Change type         | Parsed | Applied |
| ------------------- | -------| ------- |
| rename              | yes    | yes     |
| create class        | yes    | yes     |
| obsolete            | yes    | yes     |
| delete              | yes    | yes     |
| move                | yes    | yes     |
| unobsolete          | yes    | yes     |
| deepen              | yes    | yes     |
| shallow             | yes    | yes     |
| change relationship | yes    | yes     |
| change annotation   | yes    | yes     |
| create edge         | yes    | yes     |
| delete edge         | yes    | yes     |
| create synonym      | yes    | yes     |
| remove synonym      | yes    | yes     |
| change synonym      | yes    | yes     |
| remove from subset  | yes    | yes     |
| add to subset       | yes    | yes     |
| add definition      | yes    | yes     |
| change definition   | yes    | yes     |
| remove definition   | yes    | yes     |

Homepage and repository
-----------------------
The project is located at <https://incenp.org/dvlpt/kgcl-java/>. The
source code is available in a Git repository at
<https://github.com/gouttegd/kgcl-java>.

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
