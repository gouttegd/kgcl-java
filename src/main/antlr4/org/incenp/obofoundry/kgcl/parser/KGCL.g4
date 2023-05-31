grammar KGCL;

changeset : change (NL change)* EOF;

change    : rename
          | obsolete
          ;
      
rename    : 'rename' id 'from' old_label=label 'to' new_label=label;

obsolete  : 'obsolete' old_id=id                               #ObsoleteNoReplacement
          | 'obsolete' old_id=id 'with replacement' new_id=id  #ObsoleteWithReplacement
          ;

id        : IRI    #IdAsIRI
          | CURIE  #IdAsCURIE
          ;
          
label     : string lang=LANGTAG?;

string    : SQ_STRING
          | DQ_STRING
          ;
          
IRI       : '<' ~[\p{Z}>]+ '>';

CURIE     : [a-zA-Z0-9_]+ ':' [a-zA-Z0-9_]+;

SQ_STRING : '\'' ~[']*? '\'';
DQ_STRING : '"' ~["]*? '"';

LANGTAG   : '@' [a-zA-Z][a-zA-Z][a-zA-Z]?('-'[a-zA-Z]+)?;

WS        : (' ' | '\t') -> skip;
NL        : '\r'? '\n';
