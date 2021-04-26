grammar Countlang;

prog : statements EOF ;

statements : ( statement (';' statement)* )? ';'? ;

statement
  : ID '=' expr # assignmentStatement
  | 'function' ID '(' varDecls? ')' '{' statements '}' # functionDefinitionStatement
  | COUNTING? 'experiment' ID '(' varDecls? ')' '{' statements '}' # experimentDefinitionStatement
  | 'print' expr # printStatement
  | 'return' expr # returnStatement
  | 'markUsed' expr # markUsedStatement
  | 'sample' ID 'from' expr # sampleStatement
  | 'if' '(' expr ')' '{' statements '}' ('else' '{' statements '}')? # ifStatement
  | 'while' '(' expr ')' '{' statements '}' # whileStatement
  | '{' statements '}' # compoundStatement
  ;

varDecls : varDecl (',' varDecl)* ;

varDecl : typeId ID ;

typeId : 
    (INTTYPE | BOOLTYPE) # simpleType
    | (DISTRIBUTIONTYPE '<' typeId '>') # distributionType
    ;

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
  | 'distribution' ( '<' typeId '>' )? (distItem (',' distItem)* )? ( (TOTAL | UNKNOWN) expr)? # distributionExpression 
  | 'known of' expr # distributionKnownExpression
  | ID # symbolReferenceExpression
  | (INT | BOOL ) # valueExpression
  ;

distItem
  : expr 'of' expr # distItemCount
  | expr # distItemSimple
  ;

BOOLTYPE: 'bool' ;
INTTYPE: 'int' ;
DISTRIBUTIONTYPE: 'distribution' ;
COUNTING: 'possibility' WS+ 'counting' ; 

TOTAL: 'total' ;
UNKNOWN: 'unknown' ;

INT : '-'? [0-9]+ ;
BOOL : 'true' | 'false' ;
ID : [a-zA-Z] [a-zA-Z0-9]* ;
WS : [ \t\r\n] -> skip ;
