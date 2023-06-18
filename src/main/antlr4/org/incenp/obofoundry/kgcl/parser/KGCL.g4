grammar KGCL;

changeset : change (NL change)* NL? EOF;

change    : rename
          | obsolete
          | newSynonym
          | removeSynonym
          | changeSynonym
          | newDefinition
          | removeDefinition
          | changeDefinition
          ;
      
rename    : 'rename' id 'from' old_label=text 'to' new_label=text;

obsolete  : 'obsolete' old_id=id                                   #ObsoleteNoReplacement
          | 'obsolete' old_id=id 'with replacement' new_id=id      #ObsoleteWithReplacement
          | 'obsolete' old_id=id 'with alternative' alt_id=idlist  #ObsoleteWithAlternative
          ;

newSynonym: 'create' qualifier? 'synonym' synonym=text 'for' id;

removeSynonym: 'remove' 'synonym' synonym=text 'for' id;

changeSynonym: 'change' 'synonym' 'from' old_synonym=text 'to' new_synonym=text 'for' id;

newDefinition: 'add' 'definition' new_definition=text 'to' id;

removeDefinition: 'remove' 'definition' 'for' id;

changeDefinition: 'change' 'definition' 'of' id ('from' old_definition=text)? 'to' new_definition=text;

idlist    : id (',' id)*;

id        : IRI    #IdAsIRI
          | CURIE  #IdAsCURIE
          ;
          
text      : string lang=LANGTAG?;

string    : SQ_STRING
          | DQ_STRING
          ;
          
qualifier : 'exact'
          | 'narrow'
          | 'broad'
          | 'related'
          ;

IRI       : '<' ~[\p{Z}>]+ '>';

CURIE     : [a-zA-Z0-9_]+ ':' [a-zA-Z0-9_]+;

SQ_STRING : '\'' (SQ_ESCAPE|.)*? '\'';
DQ_STRING : '"' (DQ_ESCAPE|.)*? '"';

LANGTAG   : '@' [a-zA-Z][a-zA-Z][a-zA-Z]?('-'[a-zA-Z]+)?;

WS        : (' ' | '\t') -> skip;
NL        : '\r'? '\n';

fragment SQ_ESCAPE: '\\\'' | '\\\\';
fragment DQ_ESCAPE: '\\"' | '\\\\';
