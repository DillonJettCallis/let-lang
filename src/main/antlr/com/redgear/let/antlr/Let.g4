

grammar Let;

@header {
package com.redgear.let.antlr;
}

module
 : statement+ EOF
 ;

statement
 : portIn # ImportStatement
 | exported=Export? 'fun' id=LocalIdentifier '(' (args+=LocalIdentifier (',' args+=LocalIdentifier)* )? ')' '{' body=expression+ '}' # FunctionStatement
 ;

portIn
 : Import path+=ModuleIdentifier ( '.' path+=ModuleIdentifier)* ('as' alias=ModuleIdentifier)?
 ;

expression
 : Let LocalIdentifier '=' expression ';'? # AssignmentExpression
 | 'if' condition=expression '{' thenExpressions+=expression '}' ('else' '{' elseExpressions+=expression '}')? # IfExpression
 | 'for' '(' local=LocalIdentifier 'in' collection=expression ')' '{' body=expression+ '}' # ForExpression
 | '{' (LocalIdentifier (',' LocalIdentifier)* )? '=>' expression+ '}' # FunctionExpression
 | expression '.' LocalIdentifier  # ModuleAccessExpression
 | expression op=('|' | '|?' | '|/' | '|!' | '|&') expression # BinaryOpExpression
 | method=expression '(' (args+=expression (',' args+=expression)*)? ')' # CallExpression
 | args+=expression '::' method=expression '(' (args+=expression (',' args+=expression)*)? ')' # CallExpression
 | op='-' expression # UnaryOpExpression
 | op='?' expression # UnaryOpExpression
 | op='!' expression # UnaryOpExpression
 | expression op=('*' | '/' | '**') expression # BinaryOpExpression
 | expression op=('+' | '-') expression # BinaryOpExpression
 | expression op=('<' | '>' | '>=' | '<=') expression # BinaryOpExpression
 | expression op=('==' | '!=') expression # BinaryOpExpression
 | expression op='&&' expression # BinaryOpExpression
 | expression op='||' expression # BinaryOpExpression
 | ModuleIdentifier # ModuleIdentifierExpression
 | LocalIdentifier # LocalIdentifierExpression
 | IntLiteral # IntLiteralExpression
 | FloatLiteral # FloatLiteralExpression
 | StringLiteral # StringLiteralExpression
 | '[' (expression '->' expression (',' expression'->' expression)*)? ']' # MapLiteralExpression
 | '[' (expression (',' expression)*)? ']' # ListLiteralExpression
 | '(' expression+ ')' # ParenthesizedExpression
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

ModuleIdentifier
 : ModuleIdentifierStart IdentifierEnd*
 ;

LocalIdentifier
 : LocalIdentifierStart IdentifierEnd*
 | '_'
 ;

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