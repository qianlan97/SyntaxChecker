/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (C) 2000 Gerwin Klein <lsf@jflex.de>                          *
 * All rights reserved.                                                    *
 *                                                                         *
 * Thanks to Larry Bell and Bob Jamison for suggestions and comments.      *
 *                                                                         *
 * License: BSD                                                            *
 *                                                                         *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

%%

%class Lexer
%byaccj

%{

  public Parser   parser;
  public int      lineno;
  public int      column;

  public Lexer(java.io.Reader r, Parser parser) {
    this(r);
    this.parser = parser;
    this.lineno = 1;
    this.column = 1;
  }
%}

int        = [0-9]+
float      = [0-9]+"."[0-9]+(E[+-]?[0-9]+)?
identifier = [a-zA-Z_][a-zA-Z0-9_]*
newline    = \n
whitespace = [ \t\r]+
comment    = "//".*
blkcomment = "/*"(.|\n)*"*/"

%%

"int"                               { parser.yylval = new ParserVal((Object)yytext()); return Parser.INT    ; }
"@"                                 { parser.yylval = new ParserVal((Object)yytext()); return Parser.PTR    ; }
"{"                                 { parser.yylval = new ParserVal((Object)yytext()); return Parser.BEGIN  ; }
"}"                                 { parser.yylval = new ParserVal((Object)yytext()); return Parser.END    ; }
"("                                 { parser.yylval = new ParserVal((Object)yytext()); return Parser.LPAREN ; }
")"                                 { parser.yylval = new ParserVal((Object)yytext()); return Parser.RPAREN ; }
"="                                 { parser.yylval = new ParserVal((Object)yytext()); return Parser.ASSIGN ; }
"+"                                 { parser.yylval = new ParserVal((Object)yytext()); return Parser.EXPROP ; }
"*"                                 { parser.yylval = new ParserVal((Object)yytext()); return Parser.TERMOP ; }
";"                                 { parser.yylval = new ParserVal((Object)yytext()); return Parser.SEMI   ; }
{int}                               { parser.yylval = new ParserVal((Object)yytext()); return Parser.INT_LIT; }
{identifier}                        { parser.yylval = new ParserVal((Object)yytext()); return Parser.IDENT  ; }
{comment}                           { /* skip */ }
{newline}                           { /* skip */ }
{whitespace}                        { /* skip */ }
{blkcomment}                        { /* skip */ }


\b     { System.err.println("Sorry, backspace doesn't work"); }

/* error fallback */
[^]    { System.err.println("Error: unexpected character '"+yytext()+"'"); return -1; }
