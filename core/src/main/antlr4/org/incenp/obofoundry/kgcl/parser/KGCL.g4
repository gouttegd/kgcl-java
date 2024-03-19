grammar KGCL;

changeset : NL* change EOF
          | NL* (change NL+)* EOF;

change    : rename
          | obsolete
          | unobsolete
          | delete
          | newSynonym
          | removeSynonym
          | changeSynonym
          | newDefinition
          | removeDefinition
          | changeDefinition
          | newNode
          | newEdge
          | deleteEdge
          | changePredicate
          | changeAnnotation
          | move
          | addSubset
          | removeSubset
          ;
      
rename    : 'rename' id 'from' old_label=text 'to' new_label=text;

obsolete  : 'obsolete' old_id=id                                   #ObsoleteNoReplacement
          | 'obsolete' old_id=id 'with replacement' new_id=id      #ObsoleteWithReplacement
          | 'obsolete' old_id=id 'with alternative' alt_id=idlist  #ObsoleteWithAlternative
          ;

unobsolete: 'unobsolete' id;

delete    : 'delete' id;

newSynonym: 'create' qualifier? 'synonym' synonym=text 'for' id;

removeSynonym: 'remove' 'synonym' synonym=text 'for' id;

changeSynonym: 'change' 'synonym' 'from' old_synonym=text 'to' new_synonym=text 'for' id;

newDefinition: 'add' 'definition' new_definition=text 'to' id;

removeDefinition: 'remove' 'definition' 'for' id;

changeDefinition: 'change' 'definition' 'of' id ('from' old_definition=text)? 'to' new_definition=text;

newNode   : 'create' nodeType id label=text;

newEdge   : 'create' 'edge' subject_id=id predicate_id=id object_id=id;

deleteEdge: 'delete' 'edge' subject_id=id predicate_id=id object_id=id;

changePredicate: 'change' 'relationship' 'between' subject_id=id 'and' object_id=id 'from' old_predicate_id=id 'to' new_predicate_id=id;

changeAnnotation: 'change' 'annotation' 'of' subject_id=id 'with' predicate_id=id 'from' old_annotation=text 'to' new_annotation=text;

move      : 'move' subject_id=id 'from' old_parent=id 'to' new_parent=id
          | 'deepen' subject_id=id 'from' old_parent=id 'to' new_parent=id
          | 'shallow' subject_id=id 'from' old_parent=id 'to' new_parent=id
          ;

addSubset : 'add' node_id=id 'to' 'subset' subset_id=id;

removeSubset : 'remove' node_id=id 'from' 'subset' subset_id=id;

idlist    : id (',' id)*;

id        : IRI    #IdAsIRI
          | CURIE  #IdAsCURIE
          ;

nodeType  : 'class' | 'relation' | 'instance' | 'annotation property';
          
text      : string lang=LANGTAG?
          | string type=typetag?
          ;

string    : SQ_STRING
          | DQ_STRING
          ;
          
qualifier : 'exact'
          | 'narrow'
          | 'broad'
          | 'related'
          ;

typetag   : '^^' datatype=CURIE;

IRI       : '<' ~[\p{Z}>]+ '>';

CURIE     : [a-zA-Z0-9_]+ ':' [a-zA-Z0-9_]+;

SQ_STRING : '\'' (SQ_ESCAPE|.)*? '\'';
DQ_STRING : '"' (DQ_ESCAPE|.)*? '"';

LANGTAG   : '@' [a-zA-Z][a-zA-Z][a-zA-Z]?('-'[a-zA-Z]+)?;

WS        : (' ' | '\t') -> skip;
COMMENT   : '#' .*? NL -> skip;
NL        : '\r'? '\n';

fragment SQ_ESCAPE: '\\\'' | '\\\\';
fragment DQ_ESCAPE: '\\"' | '\\\\';
