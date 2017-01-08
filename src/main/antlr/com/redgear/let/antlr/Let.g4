

grammar Let;

@header {
package com.redgear.let.antlr;
}

module
 : statement+ EOF
 ;

statement
 : portIn # ImportStatement
 | portOut # ExportStatement
 | expression # ExpressionStatement
 ;

portIn
 : Import ModuleIdentifier From StringLiteral
 ;

portOut
 : Export LocalIdentifier
 | Export LocalIdentifier '=' expression
 ;

expression
 : Let LocalIdentifier '=' expression ';'? # AssignmentExpression
 | '{' (LocalIdentifier (',' LocalIdentifier)* )? '=>' expression+ '}' # FunctionExpression
 | expression '(' (expression (',' expression)*)* ')' # CallExpression
 | expression '.' LocalIdentifier  # ModuleAccessExpression
 | op='-' expression # UnaryOpExpression
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
 : '"' ~["]* '"'
 | '\'' ~[']* '\''
 ;

LineComment
 : '#' ~[\r\n\u2028\u2029]* -> channel(HIDDEN)
 ;

WhiteSpaces
 : [ \t\r\n]+ -> channel(HIDDEN)
 ;