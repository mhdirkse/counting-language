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
    (INTTYPE | BOOLTYPE | FRACTYPE ) # simpleType
    | (DISTRIBUTIONTYPE '<' typeId '>') # distributionType
    | (ARRAYTYPE '<' typeId '>' ) # arrayType
    // A tuple has at least two subtypes, but we do not enforce this in the grammar.
    // We can produce a better error message when we check this later.
    | (TUPLETYPE '<' typeId (',' typeId)* '>') # tupleType
    ;

expr
  : '(' expr ')' # bracketExpression
  | expr '.' ID '(' (expr (',' expr)*)? ')' # memberCallExpression
  | ID '(' (expr (',' expr)*)? ')' # functionCallExpression
  | '-' expr # unaryMinusExpression
  | expr ( '*' | 'div' | '/') expr # multDifExpression
  | expr ( '+' | '-' ) expr # plusMinusExpression
  | expr ( '<' | '<=' | '>' | '>=' | '==' | '!=' ) expr # compExpression
  | 'not' expr # notExpression
  | expr 'and' expr # andExpression
  | expr 'or' expr # orExpression
  | 'distribution' ( '<' typeId '>' )? (distItem (',' distItem)* )? ( (TOTAL | UNKNOWN) expr)? # distributionExpression
  | expr '[' expr ']' # dereferenceExpression
  | '[' expr ( ',' expr )* ']' # arrayExpression
  | typeId '[' ']' # emptyArrayExpression
  | 'known of' expr # distributionKnownExpression
  // A tuple expression has at least two sub-expressions, but we do not check this in the grammar.
  // We can produce a better error message when we check this later.
  | 'tuple' expr (',' expr)* # tupleExpression
  | ID # symbolReferenceExpression
  | (INT | BOOL ) # valueExpression
  ;

distItem
  : expr 'of' expr # distItemCount
  | expr # distItemSimple
  ;

BOOLTYPE: 'bool' ;
INTTYPE: 'int' ;
FRACTYPE: 'fraction';
DISTRIBUTIONTYPE: 'distribution' ;
ARRAYTYPE: 'array' ;
TUPLETYPE: 'tuple' ;
COUNTING: 'possibility' WS+ 'counting' ; 

TOTAL: 'total' ;
UNKNOWN: 'unknown' ;

INT : '-'? [0-9]+ ;
BOOL : 'true' | 'false' ;
ID : [a-zA-Z] [a-zA-Z0-9]* ;
WS : [ \t\r\n] -> skip ;
