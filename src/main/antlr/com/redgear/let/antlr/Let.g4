

grammar Let;

@header {
package com.redgear.let.antlr;
}

module
 : statement+ EOF
 ;

statement
 : portIn # ImportStatement
 | exported=Export? 'fun' id=LocalIdentifier functionDeclaration # FunctionStatement
 ;

portIn
 : Import path+=ModuleIdentifier ( '.' path+=ModuleIdentifier)* ('as' alias=ModuleIdentifier)?
 ;

expression
 : Let LocalIdentifier '=' expression ';'? # AssignmentExpression
 | 'if' condition=expression '{' thenExpressions+=expression '}' ('else' '{' elseExpressions+=expression '}')? # IfExpression
 | 'for' '(' parent=LocalIdentifier 'in' collection=expression ')' '{' body=expression+ '}' # ForExpression
 | 'fun' id=LocalIdentifier functionDeclaration # FunctionExpression
 | '{' (maybeQualifiedVariable (',' maybeQualifiedVariable)* )? '=>' expression+ '}' # LambdaExpression
 | ModuleIdentifier '.' LocalIdentifier  # ModuleAccessExpression
 | expression op=('|' | '|?' | '|/' | '|!' | '|&') expression # BinaryOpExpression
 | method=expression '(' (args+=expression (',' args+=expression)*)? ')' # CallExpression
 | args+=expression '::' method=expression '(' (args+=expression (',' args+=expression)*)? ')' # CallExpression
 | expression op='&' expression # BinaryOpExpression
 | op='-' expression # UnaryNegOpExpression
 | op='?' expression # UnaryOpExpression
 | op='!' expression # UnaryOpExpression
 | expression op=('*' | '/' | '**') expression # BinaryOpExpression
 | expression op=('+' | '-') expression # BinaryOpExpression
 | expression op=('<' | '>' | '>=' | '=<') expression # BinaryOpExpression
 | expression op=('==' | '!=') expression # BinaryOpExpression
 | expression op='&&' expression # BinaryAndOpExpression
 | expression op='||' expression # BinaryOrOpExpression
 | LocalIdentifier # LocalIdentifierExpression
 | IntLiteral # IntLiteralExpression
 | FloatLiteral # FloatLiteralExpression
 | StringLiteral # StringLiteralExpression
 | '[' (keys+=expression '->' values+=expression (',' keys+=expression'->' values+=expression)*)? ']' # MapLiteralExpression
 | '[' (expression (',' expression)*)? ']' # ListLiteralExpression
 | '(' expression (',' expression)+ ')' # TupleLiteralExpression
 | '(' expression+ ')' # ParenthesizedExpression
 ;

functionDeclaration
    : singleFunctionDeclaration # SingleFunctionExpression
    | '{' singleFunctionDeclaration+ '}' # OverloadedFunctionExpression
    ;

singleFunctionDeclaration
    : '(' (args+=qualifiedVariable (',' args+=qualifiedVariable )* )? ')' ':' resultType=typeExpression '{' body=expression+ '}' # SimpleFunctionExpression
    | '<' typeParam+=typeExpression (',' typeParam+=typeExpression)*  '>' '(' (args+=qualifiedVariable (',' args+=qualifiedVariable )* )? ')' ':' resultType=typeExpression '{' body=expression+ '}' # GenericFunctionExpression
    ;

typeExpression
 : ModuleIdentifier # TypeIdentifier
 | '(' typeExpression (',' typeExpression)+ ')' #TypeTupleIdentifier
 | type=typeExpression '<' typeParams+=typeExpression (',' typeParams+=typeExpression)* '>' # TypeGenericIdentifier
 | '{' (argTypes+=typeExpression (',' argTypes+=typeExpression)*)? '=>' resultType=typeExpression '}' # TypeFunctionIdentifier
 ;

qualifiedVariable
 : arg=LocalIdentifier ':' argTypes=typeExpression
 ;

maybeQualifiedVariable
 : arg=LocalIdentifier (':' argTypes=typeExpression)?
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