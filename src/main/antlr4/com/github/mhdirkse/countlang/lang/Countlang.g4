grammar Countlang;

prog : statement (';' statement)* EOF ;

statement
  : ID '=' expr # assignmentStatement
  | 'print' expr # printStatement
  ;

expr
  : expr ( '*' | '/' ) expr # multDifExpression
  | expr ( '+' | '-' ) expr # plusMinusExpression
  | ID # symbolReferenceExpression
  | INT # valueExpression
  ;

ID : [a-zA-Z] [a-zA-Z0-9]* ;
INT : '-'? [0-9]+ ;
WS : [ \t\r\n] -> skip ;
