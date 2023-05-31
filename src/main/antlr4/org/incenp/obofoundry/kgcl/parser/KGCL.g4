grammar KGCL;

changeset : change (NL change)* EOF;

change    : rename
          | obsolete
          ;
      
rename    : 'rename' id 'from' old_label=text 'to' new_label=text;

obsolete  : 'obsolete' old_id=id                               #ObsoleteNoReplacement
          | 'obsolete' old_id=id 'with replacement' new_id=id  #ObsoleteWithReplacement
          ;

id        : IRI    #IdAsIRI
          | CURIE  #IdAsCURIE
          ;
          
text      : string lang=LANGTAG?;

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
