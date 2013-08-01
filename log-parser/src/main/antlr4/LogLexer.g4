// @header {
// package fr.an.logparser.lexer; ... obsolete in antlr4 ... cf "-package" in generator 
// }

lexer grammar LogLexer;


IDENT : ([A-Za-z_][A-Za-z0-9_]*); // TODO... also accept all accents
TEXT : (IDENT | WS | PUNCTUATION)+;

NL : '\r'? '\n';
WS : (' ' | '\t' ) -> skip;

STRING_LITERAL1 : '\'' (~('\'' | '\\' | '\n' | '\r') | ECHAR)* '\'';
STRING_LITERAL2 : '"' (~('"' | '\\' | '\n' | '\r') | ECHAR)* '"';
STRING_LITERAL_LONG1 : '\'\'\'' (('\'' | '\'\'')? (~('\''|'\\') | ECHAR))* '\'\'\'';
STRING_LITERAL_LONG2 : '"""' (('"' | '""')? (~('"'|'\\') | ECHAR))* '"""';

fragment ECHAR : '\\' ('t' | 'b' | 'n' | 'r' | 'f' | '\\' | '"' | '\'');



PUNCTUATION : ( '.' | ',' | ';' | ':' | '!' | '?');
ASSIGN_OP : (':=' | '=');

fragment DIGIT : '0'..'9';
fragment EXPONENT : ('e'|'E') SIGN? DIGIT+;
fragment SIGN : ('+'|'-');
fragment DOT : '.';

INT :   SIGN? DIGIT+ ;
DECIMAL : SIGN? DIGIT* DOT DIGIT*;
DOUBLE : SIGN? DIGIT+ DOT DIGIT* EXPONENT?;

fragment DATE_YMD: ( DIGIT+ ) ('/' | '-') (DIGIT+ | MONTH) ('/' | '-') DIGIT+; // TODO month name
fragment DATE_HMS: DIGIT+ ':' DIGIT+ ':' DIGIT+; // TODO... millis, Timezone... 
DATE: (DATE_YMD DATE_HMS) | DATE_YMD | DATE_HMS;
fragment MONTH: ('Jan' | 'Feb' | 'Mar' | 'Apr' | 'May' | 'Jun' | 'Jul' | 'Aug' | 'Sep' | 'Oct' | 'Nov' | 'Dec');

OPEN_BRACE : '(';
CLOSE_BRACE : ')';
OPEN_CURLY_BRACE : '{';
CLOSE_CURLY_BRACE : '}';
OPEN_SQUARE_BRACKET : '[';
CLOSE_SQUARE_BRACKET : ']';

OTHER: .;
