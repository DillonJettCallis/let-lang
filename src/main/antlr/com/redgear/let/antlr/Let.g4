

grammar Let;

@header {
package com.redgear.let.antlr;
}

module
 : statement+ EOF
 ;

statement
 : portIn # ImportStatement
 | exported=Export? 'fun' id=LocalIdentifier '(' (args+=LocalIdentifier ':' argTypes+=typeExpression (',' args+=LocalIdentifier ':' argTypes+=typeExpression )* )? ')' ':' resultType=typeExpression
    '{' body=expression+ '}' # FunctionStatement
 ;

portIn
 : Import path+=ModuleIdentifier ( '.' path+=ModuleIdentifier)* ('as' alias=ModuleIdentifier)?
 ;

expression
 : Let LocalIdentifier '=' expression ';'? # AssignmentExpression
 | 'if' condition=expression '{' thenExpressions+=expression '}' ('else' '{' elseExpressions+=expression '}')? # IfExpression
 | 'for' '(' local=LocalIdentifier 'in' collection=expression ')' '{' body=expression+ '}' # ForExpression
 | '{' (LocalIdentifier ':' argTypes+=typeExpression (',' LocalIdentifier ':' argTypes+=typeExpression)* )? '=>' expression+ '}' # FunctionExpression
 | ModuleIdentifier '.' LocalIdentifier  # ModuleAccessExpression
 | expression op=('|' | '|?' | '|/' | '|!' | '|&') expression # BinaryOpExpression
 | method=expression '(' (args+=expression (',' args+=expression)*)? ')' # CallExpression
 | args+=expression '::' method=expression '(' (args+=expression (',' args+=expression)*)? ')' # CallExpression
 | op='-' expression # UnaryNegOpExpression
 | op='?' expression # UnaryOpExpression
 | op='!' expression # UnaryOpExpression
 | expression op=('*' | '/' | '**') expression # BinaryOpExpression
 | expression op=('+' | '-') expression # BinaryOpExpression
 | expression op=('<' | '>' | '>=' | '=<') expression # BinaryOpExpression
 | expression op=('==' | '!=') expression # BinaryOpExpression
 | expression op='&&' expression # BinaryOpExpression
 | expression op='||' expression # BinaryOpExpression
 | LocalIdentifier # LocalIdentifierExpression
 | IntLiteral # IntLiteralExpression
 | FloatLiteral # FloatLiteralExpression
 | StringLiteral # StringLiteralExpression
 | '[' (expression '->' expression (',' expression'->' expression)*)? ']' # MapLiteralExpression
 | '[' (expression (',' expression)*)? ']' # ListLiteralExpression
 | '(' expression+ ')' # ParenthesizedExpression
 ;

typeExpression
 : ModuleIdentifier # TypeIdentifier
 | type=typeExpression '<' typeParams+=typeExpression (',' typeParams+=typeExpression)* '>' # TypeGenericIdentifier
 | '{' (argTypes+=typeExpression (',' argTypes+=typeExpression)*)? '=>' resultType=typeExpression '}' # TypeFunctionIdentifier
 ;

BinaryOp
 : '+'
 | '-'
 | '*'
 | '/'
 | '**'
 | '<'
 | '>'
 | '>='
 | '<='
 | '&&'
 | '||'
 | '=='
 | '!='
 ;

UnaryOp
 : '-'
 | '!'
 ;

Let : 'let' ;
Import : 'import' ;
From : 'from' ;
Export : 'export' ;

//TypeIdentifier
// : TypeIdentifierStart IdentifierEnd*
// ;

ModuleIdentifier
 : ModuleIdentifierStart IdentifierEnd*
 ;

LocalIdentifier
 : LocalIdentifierStart IdentifierEnd*
 | '_'
 ;

//fragment TypeIdentifierStart
// : [A-Z]
// ;

fragment ModuleIdentifierStart
 : [A-Z]
 ;

fragment LocalIdentifierStart
 : [a-z]
 ;

fragment IdentifierEnd
 : [a-zA-Z_0-9]
 ;

IntLiteral
 : [0-9]+
 ;

FloatLiteral
 : IntLiteral '.' IntLiteral
 ;

StringLiteral
 : '\'' ~[']* '\''
 | '"' ~["]* '"'
 ;

LineComment
 : '#' ~[\r\n\u2028\u2029]* -> channel(HIDDEN)
 ;

WhiteSpaces
 : [ \t\r\n]+ -> channel(HIDDEN)
 ;