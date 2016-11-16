grammar countlang;

prog : expr (';' expr)* EOF ;

expr
  : expr ( '*' | '/' ) expr
  | expr ( '+' | '-' ) expr
  | ID
  | INT;

ID : [a-zA-Z] [a-zA-Z0-9]* ;
INT : '-'? [0-9]+ ;
WS : [ \t\r\n] -> skip ;

