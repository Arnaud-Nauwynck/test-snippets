grammar FXON;

topLevel : NUMBER;

topLevel2
   : object
   | array
   ;

   
object 
   : objectData
   | typeName objectData
   ;

objectData
   : '{' objectDataElt (',' objectDataElt)* '}'
   | '{' '}'
   ;


objectDataElt
   : identifierOrString ':' expression
   // 'override' identifierOrString assignmentOperator expression 
   | 'import' name
   ;

assignmentOperator
	:	'='
	|	'*='
	|	'/='
	|	'%='
	|	'+='
	|	'-='
	|	'<<='
	|	'>>='
	|	'>>>='
	|	'&='
	|	'^='
	|	'|='
	;



   
typeName
	:	primitiveType
	|	referenceType
	;

primitiveType
    : 'boolean' | 'char' | 'int' | 'float' | 'string' | 'ref';	

referenceType
   : ( name ':')? name;


Identifier
	:	Letter LetterOrDigit*
	;

Letter
	:	[a-zA-Z$_] // these are the "java letters" below 0xFF
	|	// covers all characters above 0xFF which are not a surrogate
		~[\u0000-\u00FF\uD800-\uDBFF]
	|	// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
		[\uD800-\uDBFF] [\uDC00-\uDFFF]
	;

LetterOrDigit
	:	[a-zA-Z0-9$_] // these are the "java letters or digits" below 0xFF
	|	// covers all characters above 0xFF which are not a surrogate
		~[\u0000-\u00FF\uD800-\uDBFF]
	|	// covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
		[\uD800-\uDBFF] [\uDC00-\uDFFF]
	;
   

name
   : Identifier ('.' Identifier)*
   ;


identifierOrString : Identifier | STRING; 

array
   : '[' expression (',' expression)* ']'
   | '[' ']'
   ;

literal
	: NUMBER
	| STRING
    | 'true'
    | 'false'
    | 'null'
	;
	
expression
	:	literal
	// |	typeName ('[' ']')* '.' 'class'
	|	'(' expression ')'
	| object
	| array
	| methodInvocation
	// | lambdaExpression
	| conditionalExpression
   ;

methodInvocation
	: Identifier '(' expression (',' expression )* 
   		// (',' namedParam (',' namedParam)* )?
   		')'
   		;



lambdaExpression
	:	lambdaParameters '->' lambdaBody
	;

lambdaParameters
	:	Identifier
	|	'(' formalParameters? ')'
	|	'(' inferredFormalParameterList ')'
	;

formalParameters
	:	formalParameter (',' formalParameter)*
	;

formalParameter
	:	Identifier unannType
	;

unannType
    : typeName
    ;
		
inferredFormalParameterList
	:	Identifier (',' Identifier)*
	;

lambdaBody
	:	expression
	// |	block
	;


expressionName
	:	Identifier ('.' Identifier)*
	;
	

conditionalExpression
	:	conditionalOrExpression
	|	conditionalOrExpression '?' expression ':' conditionalExpression
	;

conditionalOrExpression
	:	conditionalAndExpression
	|	conditionalOrExpression '||' conditionalAndExpression
	;

conditionalAndExpression
	:	inclusiveOrExpression
	|	conditionalAndExpression '&&' inclusiveOrExpression
	;

inclusiveOrExpression
	:	exclusiveOrExpression
	|	inclusiveOrExpression '|' exclusiveOrExpression
	;

exclusiveOrExpression
	:	andExpression
	|	exclusiveOrExpression '^' andExpression
	;

andExpression
	:	equalityExpression
	|	andExpression '&' equalityExpression
	;

equalityExpression
	:	relationalExpression
	|	equalityExpression '==' relationalExpression
	|	equalityExpression '!=' relationalExpression
	;

relationalExpression
	:	shiftExpression
	|	relationalExpression '<' shiftExpression
	|	relationalExpression '>' shiftExpression
	|	relationalExpression '<=' shiftExpression
	|	relationalExpression '>=' shiftExpression
	|	relationalExpression 'instanceof' referenceType
	;

shiftExpression
	:	additiveExpression
	|	shiftExpression '<' '<' additiveExpression
	|	shiftExpression '>' '>' additiveExpression
	|	shiftExpression '>' '>' '>' additiveExpression
	;

additiveExpression
	:	multiplicativeExpression
	|	additiveExpression '+' multiplicativeExpression
	|	additiveExpression '-' multiplicativeExpression
	;

multiplicativeExpression
	:	unaryExpression
	|	multiplicativeExpression '*' unaryExpression
	|	multiplicativeExpression '/' unaryExpression
	|	multiplicativeExpression '%' unaryExpression
	;

unaryExpression
	:	preIncrementExpression
	|	preDecrementExpression
	|	'+' unaryExpression
	|	'-' unaryExpression
	|	unaryExpressionNotPlusMinus
	;

preIncrementExpression
	:	'++' unaryExpression
	;

preDecrementExpression
	:	'--' unaryExpression
	;

unaryExpressionNotPlusMinus
	:	'~' unaryExpression
	|	'!' unaryExpression
	;







STRING
   : '"' (ESC | ~ ["\\])* '"'
   ;

fragment ESC
   : '\\' (["\\/bfnrt] | UNICODE)
   ;


fragment UNICODE
   : 'u' HEX HEX HEX HEX
   ;


fragment HEX
   : [0-9a-fA-F]
   ;


NUMBER
   : '-'? INT '.' [0-9] + EXP? | '-'? INT EXP | '-'? INT
   ;


fragment INT
   : '0' | [1-9] [0-9]*
   ;

// no leading zeros

fragment EXP
   : [Ee] [+\-]? INT
   ;

WS
   : [ \t\n\r] + -> skip
   ;
