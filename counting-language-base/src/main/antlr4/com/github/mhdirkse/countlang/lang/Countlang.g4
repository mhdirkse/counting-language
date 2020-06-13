grammar Countlang;

prog : statements EOF ;

statements : statement (';' statement)* ';'? ;

statement
  : ID '=' expr # assignmentStatement
  | 'function' ID '(' varDecls? ')' '{' statements '}' # functionDefinitionStatement
  | 'print' expr # printStatement
  | 'return' expr # returnStatement
  | 'markUsed' expr # markUsedStatement
  | '{' statements '}' # compoundStatement
  ;

varDecls : varDecl (',' varDecl)* ;

varDecl : typeId ID ;

typeId : INTTYPE | BOOLTYPE ;

expr
  : '(' expr ')' # bracketExpression
  | ID '(' (expr (',' expr)*)? ')' # functionCallExpression
  | '-' expr # unaryMinusExpression
  | expr ( '*' | '/' ) expr # multDifExpression
  | expr ( '+' | '-' ) expr # plusMinusExpression
  | expr ( '<' | '<=' | '>' | '>=' | '==' | '!=' ) expr # compExpression
  | 'not' expr # notExpression
  | expr 'and' expr # andExpression
  | expr 'or' expr # orExpression
  | ID # symbolReferenceExpression
  | (INT | BOOL) # valueExpression
  ;

BOOLTYPE: 'bool' ;
INTTYPE: 'int' ;
INT : '-'? [0-9]+ ;
BOOL : 'true' | 'false' ;
ID : [a-zA-Z] [a-zA-Z0-9]* ;
WS : [ \t\r\n] -> skip ;
