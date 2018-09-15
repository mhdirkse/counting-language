grammar Countlang;

prog : statements EOF ;

statements : statement (';' statement)* ;

statement
  : ID '=' expr # assignmentStatement
  | 'function' ID '(' varDecls? ')' '{' statements '}' # functionDefinitionStatement
  | 'print' expr # printStatement
  | 'return' expr # returnStatement
  ;

varDecls : ID (',' ID)* ;

expr
  : '(' expr ')' # bracketExpression
  | ID '(' (expr (',' expr)*)? ')' # functionCallExpression
  | expr ( '*' | '/' ) expr # multDifExpression
  | expr ( '+' | '-' ) expr # plusMinusExpression
  | ID # symbolReferenceExpression
  | INT # valueExpression
  ;

ID : [a-zA-Z] [a-zA-Z0-9]* ;
INT : '-'? [0-9]+ ;
WS : [ \t\r\n] -> skip ;
