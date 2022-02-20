grammar Countlang;

prog : statements EOF ;

statements : ( statement (';' statement)* )? ';'? ;

statement
  : ID '=' expr # assignmentStatement
  | lhsItem (',' lhsItem)+ '=' expr # tupleDealingAssignmentStatement
  | 'function' commonFunctionDefinitionSyntax # functionDefinitionStatement
  | 'procedure' commonFunctionDefinitionSyntax # procedureDefinitionStatement
  | COUNTING? 'experiment' commonFunctionDefinitionSyntax # experimentDefinitionStatement
  | 'print' FORMAT? expr # printStatement
  | 'return' expr # singleValueReturnStatement
  | 'return' expr (',' expr)+ # tupleCreatingReturnStatement
  | 'return' # nonValueReturnStatement
  | 'markUsed' expr # markUsedStatement
  | 'sample' ID 'from' expr # sampleStatement
  | 'sample' lhsItem (',' lhsItem)+ 'from' expr # tupleDealingSampleStatement
  | 'sample' ID 'as' expr 'from' expr # sampleMultipleStatement
  | 'if' '(' expr ')' '{' statements '}' ('else' '{' statements '}')? # ifStatement
  | 'while' '(' expr ')' '{' statements '}' # whileStatement
  | 'repeat' '(' expr ')' '{' statements '}' # repeatStatement
  | 'for' ID 'in' expr '{' statements '}' # forInRepetitionStatement
  | 'for' lhsItem (',' lhsItem)+ 'in' expr '{' statements '}' # tupleDealingForInRepetitionStatement
  | call # procedureCallStatement
  | '{' statements '}' # compoundStatement
  ;

commonFunctionDefinitionSyntax : ID '(' varDecls? ')' '{' statements '}' ;

varDecls : varDecl (',' varDecl)* ;

varDecl : typeId ID ;

typeId : 
    (INTTYPE | BOOLTYPE | FRACTYPE ) # simpleType
    | (DISTRIBUTIONTYPE '<' typeId '>') # distributionType
    | (ARRAYTYPE '<' typeId '>' ) # arrayType
    // A tuple has at least two subtypes, but we do not enforce this in the grammar.
    // We can produce a better error message when we check this later.
    | (TUPLETYPE '<' typeId (',' typeId)* '>') # tupleType
    // We do not allow range types here. There can be no variables of a range type.
    ;

expr
  : '(' expr ')' # bracketExpression
  | expr '.' ID '(' (expr (',' expr)*)? ')' # memberCallExpression
  | expr '[' expr (',' expr)* ']' # dereferenceExpression
  | call # functionCallExpression
  | '-' expr # unaryMinusExpression
  | expr ( '*' | 'div' | '/') expr # multDifExpression
  | expr ( '+' | '-' ) expr # plusMinusExpression
  | expr ( '<' | '<=' | '>' | '>=' | '==' | '!=' ) expr # compExpression
  | 'not' expr # notExpression
  | expr 'and' expr # andExpression
  | expr 'or' expr # orExpression
  | 'distribution' ( '<' typeId '>' )? (distItem (',' distItem)* )? ( (TOTAL | UNKNOWN) expr)? # distributionExpression
  | '[' expr ( ',' expr )* ']' # arrayExpression
  | typeId '[' ']' # emptyArrayExpression
  | 'known of' expr # distributionKnownExpression
  // A tuple expression has at least two sub-expressions, but we do not check this in the grammar.
  // We can produce a better error message when we check this later.
  | 'tuple' expr (',' expr)* # tupleExpression
  // If we would write this like "expr ':' expr (':" expr)?", ANTLR would parse it like "((expr ':' expr) ':' expr)" which is not what we need.
  | expr ':' expr ':' expr # rangeExpression
  | expr ':' expr # rangeExpression
  | ID # symbolReferenceExpression
  | (INT | BOOL ) # valueExpression
  ;

call: ID '(' (expr (',' expr)*)? ')' ;

distItem
  : expr 'of' expr # distItemCount
  | expr # distItemSimple
  ;

lhsItem : ID | LHS_PLACEHOLDER # lhsItem
  ;

COMMENT: '#' ~[\n]* '\n' -> skip ;

BOOLTYPE: 'bool' ;
INTTYPE: 'int' ;
FRACTYPE: 'fraction';
DISTRIBUTIONTYPE: 'distribution' ;
ARRAYTYPE: 'array' ;
TUPLETYPE: 'tuple' ;
COUNTING: 'possibility' WS+ 'counting' ; 
FORMAT: 'exact' | 'approx' ;
TOTAL: 'total' ;
UNKNOWN: 'unknown' ;

INT : '-'? [0-9]+ ;
BOOL : 'true' | 'false' ;
ID : [a-zA-Z] [a-zA-Z0-9]* ;
LHS_PLACEHOLDER : '_' ;
WS : [ \t\r\n] -> skip ;
