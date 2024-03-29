import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class Parser
{
    public static int indentation_order   = 0;
    public static final String indentation_atomic = "    ";
    public static String indentation = indentation_atomic.repeat(indentation_order);

    public static final int ENDMARKER   =  0;
    public static final int LEXERROR    =  1;

    public static final int FUNC        = 10;
    public static final int RETURN      = 11;
    public static final int VAR         = 12;
    public static final int IF          = 13;
    public static final int THEN        = 14;
    public static final int ELSE        = 15;
    public static final int BEGIN       = 16;
    public static final int END         = 17;
    public static final int WHILE       = 18;
    public static final int LPAREN      = 19;
    public static final int RPAREN      = 20;
    public static final int LBRACKET    = 21;
    public static final int RBRACKET    = 22;
    public static final int VOID        = 23;
    public static final int INT         = 24;
    public static final int BOOL        = 25;
    public static final int NEW         = 26;
    public static final int SIZE        = 27;
    public static final int PRINT       = 28;
    public static final int ASSIGN      = 29;
    public static final int FUNCRET     = 30;
    public static final int TYPEOF      = 31;
    public static final int RELOP       = 32;
    public static final int EXPROP      = 33;
    public static final int TERMOP      = 34;
    public static final int SEMI        = 35;
    public static final int COMMA       = 36;
    public static final int DOT         = 37;
    public static final int BOOL_LIT    = 38;
    public static final int INT_LIT     = 39;
    public static final int IDENT       = 40;

    public class Token
    {
        public int       type;
        public ParserVal attr;
        public Token(int type, ParserVal attr) {
            this.type   = type;
            this.attr   = attr;
        }
    }

    public ParserVal yylval;
    Token _token;
    Lexer _lexer;
    public Parser(java.io.Reader r) throws Exception
    {
        _lexer = new Lexer(r, this);
        _token = null;
        Advance();
    }

    public void Advance() throws Exception
    {
        int token_type = _lexer.yylex();
        if(token_type ==  0)      _token = new Token(ENDMARKER , null  );
        else if(token_type == -1) _token = new Token(LEXERROR  , yylval);
        else                      _token = new Token(token_type, yylval);
    }
    public void updateIndentation(int step)
    {
        indentation_order = indentation_order + step;
        indentation = indentation_atomic.repeat(indentation_order);
    }
    public void resetIndentation()
    {
        indentation_order = 0;
        indentation = indentation_atomic.repeat(indentation_order);
    }

    public String Match(int token_type) throws Exception
    {
        boolean match = (token_type == _token.type);
        String lexeme = "";
        String errorMsg = "";

        if(!match) {
//            System.out.println(token_type);
            switch (token_type) {
                case 10:
                    errorMsg = "\"func\"";
                    break;
                case 11:
                    errorMsg = "\"return\"";
                    break;
                case 12:
                    errorMsg = "\"var\"";
                    break;
                case 13:
                    errorMsg = "\"if\"";
                    break;
                case 14:
                    errorMsg = "\"then\"";
                    break;
                case 15:
                    errorMsg = "\"else\"";
                    break;
                case 16:
                    errorMsg = "\"begin\"";
                    break;
                case 17:
                    errorMsg = "\"end\"";
                    break;
                case 18:
                    errorMsg = "\"while\"";
                    break;
                case 19:
                    errorMsg = "\"(\"";
                    break;
                case 20:
                    errorMsg = "\")\"";
                    break;
                case 21:
                    errorMsg = "\"[\"";
                    break;
                case 22:
                    errorMsg = "\"]\"";
                    break;
                case 23:
                    errorMsg = "\"void\"";
                    break;
                case 24:
                    errorMsg = "\"int\"";
                    break;
                case 25:
                    errorMsg = "\"bool\"";
                    break;
                case 26:
                    errorMsg = "\"new\"";
                    break;
                case 27:
                    errorMsg = "\"size\"";
                    break;
                case 28:
                    errorMsg = "\"print\"";
                    break;
                case 29:
                    errorMsg = "\"<-\"";
                    break;
                case 30:
                    errorMsg = "\"->\"";
                    break;
                case 31:
                    errorMsg = "\"::\"";
                    break;
                case 32:
                    errorMsg = "\"<\", \">\",\"<=\", \"=\" or \"!=\"";
                    break;
                case 33:
                    errorMsg = "\"+\", \"-\" or \"or\"";
                    break;
                case 34:
                    errorMsg = "\"*\", \"/\" or \"and\"";
                    break;
                case 35:
                    errorMsg = "\";\"";
                    break;
                case 36:
                    errorMsg = "\",\"";
                    break;
                case 37:
                    errorMsg = "\".\"";
                    break;
                case 38:
                    errorMsg = "a boolean value";
                    break;
                case 39:
                    errorMsg = "an integer";
                    break;
                case 40:
                    errorMsg = "an identifier";
                    break;
            }
            throw new Exception(errorMsg + " is expected instead of \"" + _token.attr.obj + "\" at " + (Lexer.yyline+1) + ":" + (Lexer.yycolumn+1) + ".");
        }
        if(_token.type != ENDMARKER)
            Advance();

        return lexeme;
    }

    public int yyparse() throws Exception
    {
        try
        {
            parse();
            System.out.println("Success: no syntax error is found.");
            System.out.println();
            System.out.println("Following is the indentation-updated source code:\n");
            System.out.println(_lexer.indented_script);
        }
        catch(Exception e)
        {
            System.out.println("Error: There is syntax error(s).");
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public List<String> parse() throws Exception
    {
        return program();
    }

    // **************************************************************************************
    // From here down, implement all the functions for each non-terminal from the parse table
    // **************************************************************************************

    public List<String> program() throws Exception
    {
        switch(_token.type)
        {
            // program -> decl_list
            case FUNC:
            case ENDMARKER:
                decl_list();
                return null;
        }
        throw new Exception("error in program at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public List<String> decl_list() throws Exception
    {
        switch(_token.type)
        {
            // decl_list -> decl_list'
            case FUNC:
            case ENDMARKER:
                decl_list_();
                return null;
        }
        throw new Exception("error in decl_list at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public List<String> decl_list_() throws Exception
    {
        switch(_token.type)
        {
            // decl_list'	-> fun_decl decl_list'
            case FUNC:
                fun_decl();
                decl_list_();
                return null;
            case ENDMARKER:
                return null;
        }
        throw new Exception("error in decl_list_ at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public List<String> fun_decl() throws Exception
    {
        switch(_token.type)
        {
            // fun_decl -> FUNC IDENT TYPEOF LPAREN params RPAREN FUNCRET prim_type BEGIN local_decls stmt_list END
            case FUNC:
                resetIndentation();
                _lexer.appendToScript(_lexer.yytext() + " ");
                Match(FUNC);
                _lexer.appendToScript(_lexer.yytext());
                Match(IDENT);
                _lexer.appendToScript(_lexer.yytext());
                Match(TYPEOF);
                _lexer.appendToScript(_lexer.yytext());
                Match(LPAREN);
                params();
                _lexer.appendToScript(_lexer.yytext());
                Match(RPAREN);
                _lexer.appendToScript(_lexer.yytext());
                Match(FUNCRET);
                _lexer.appendToScript(_lexer.yytext());
                prim_type();
                _lexer.appendToScript("\n"+ _lexer.yytext() +"\n");
                Match(BEGIN);
                updateIndentation(1);
                local_decls();
                stmt_list();
                _lexer.appendToScript(_lexer.yytext()+ "\n\n");
                Match(END);
                return null;
        }
        throw new Exception("errpr in fun_decl at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String params() throws Exception
    {
        switch (_token.type)
        {
            // params -> param_list
            case IDENT:
                param_list();
                return null;
            // params -> ϵ
            case RPAREN:
                return null;
        }
        throw new Exception("error in params at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String param_list() throws Exception
    {
        switch (_token.type)
        {
            // param_list -> param param_list'
            case IDENT:
                param();
                param_list_();
                return null;
        }
        throw new Exception("error in param_list at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String param_list_() throws Exception
    {
        switch (_token.type)
        {
            // param_list' -> COMMA param param_list'
            case COMMA:
                _lexer.appendToScript(_lexer.yytext() + " ");
                Match(COMMA);
                param();
                param_list_();
                return null;
            // param_list' -> ϵ
            case RPAREN:
                return null;
        }
        throw new Exception("error in param_list_ at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String param() throws Exception
    {
        switch (_token.type)
        {
            // param -> IDENT TYPEOF type_spec
            case IDENT:
                _lexer.appendToScript(_lexer.yytext());
                Match(IDENT);
                _lexer.appendToScript(_lexer.yytext());
                Match(TYPEOF);
                _lexer.appendToScript(_lexer.yytext());
                type_spec();
                return null;
        }
        throw new Exception("error in param at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String type_spec() throws Exception
    {
        switch(_token.type)
        {
            // type_spec -> prim_type type_spec'
            case INT:
            case BOOL:
                prim_type();
                type_spec_();
                return null;
        }
        throw new Exception("error in type_spec at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String type_spec_() throws Exception
    {
        switch (_token.type)
        {
            // type_spec' -> LBRACKET RBRACKET
            case LBRACKET:
                _lexer.appendToScript(_lexer.yytext());
                Match(LBRACKET);
                _lexer.appendToScript(_lexer.yytext());
                Match(RBRACKET);
                return null;
            // type_spec' -> ϵ
            case RPAREN:
            case SEMI:
            case COMMA:
                return null;
        }
        throw new Exception("Incorrect type specification at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String prim_type() throws Exception
    {
        switch (_token.type)
        {
            // prim_type -> INT
            case INT:
                Match(INT);
                return null;
            // prim_type  -> BOOL
            case BOOL:
                Match(BOOL);
                return null;
        }
        throw new Exception("error in prim_type at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String local_decls() throws Exception
    {
        switch (_token.type)
        {
            // local_decls -> local_decls'
            case RETURN:
            case VAR:
            case IF:
            case BEGIN:
            case END:
            case WHILE:
            case PRINT:
            case IDENT:
                local_decls_();
                return null;
        }
        throw new Exception("Incorrect declaration of a local variable at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String local_decls_() throws Exception
    {
        switch (_token.type)
        {
            // local_decls' -> local_decl local_decls'
            case VAR:
                local_decl();
                local_decls_();
                return null;
            // local_decls' -> ϵ
            case RETURN:
            case IF:
            case BEGIN:
            case END:
            case WHILE:
            case PRINT:
            case IDENT:
                return null;

        }
        throw new Exception("error in local_decls_ at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String local_decl() throws Exception
    {
        switch (_token.type)
        {
            // local_decl -> VAR IDENT TYPEOF type_spec SEMI
            case VAR:
                _lexer.appendToScript(indentation + _lexer.yytext()+" ");
                Match(VAR);
                _lexer.appendToScript(_lexer.yytext());
                Match(IDENT);
                _lexer.appendToScript(_lexer.yytext());
                Match(TYPEOF);
                _lexer.appendToScript(_lexer.yytext() );
                type_spec();
                _lexer.appendToScript(_lexer.yytext() + "\n");
                Match(SEMI);
                return null;
        }
        throw new Exception("error in local_decl at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String stmt_list() throws Exception
    {
        switch (_token.type)
        {
            // stmt_list -> stmt_list'
            case RETURN:
            case IF:
            case ELSE:
            case BEGIN:
            case END:
            case WHILE:
            case PRINT:
            case IDENT:
                stmt_list_();
                return null;
        }
        throw new Exception("error in stmt_list at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String stmt_list_() throws Exception
    {
        switch (_token.type)
        {
            // stmt_list' -> stmt stmt_list'
            case RETURN:
            case IF:
            case BEGIN:
            case WHILE:
            case PRINT:
            case IDENT:
                stmt();
                stmt_list_();
                return null;
            // stmt_list' -> ϵ
            case ELSE:
            case END:
                return null;
        }
        throw new Exception("Incorrect statement at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String stmt() throws Exception
    {
        switch (_token.type)
        {
            // stmt -> return_stmt
            case RETURN:
                return_stmt();
                return null;
            // stmt -> if_stmt
            case IF:
                if_stmt();
                return null;
            // stmt -> compound_stmt
            case BEGIN:
                compound_stmt();
                return null;
            // stmt -> while_stmt
            case WHILE:
                while_stmt();
                return null;
            // stmt -> print_stmt
            case PRINT:
                print_stmt();
                return null;
            // stmt -> expr_stmt
            case IDENT:
                expr_stmt();
                return null;
        }
        throw new Exception("error in stmt at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String expr_stmt() throws Exception
    {
        switch (_token.type)
        {
            // expr_stmt -> IDENT ASSIGN expr SEMI
            case IDENT:
                _lexer.appendToScript(indentation+ _lexer.yytext() + " ");
                Match(IDENT);
                _lexer.appendToScript(_lexer.yytext() + " ");
                Match(ASSIGN);
                expr();
                _lexer.removeLastFromScript();
                _lexer.appendToScript(_lexer.yytext() + "\n");
                Match(SEMI);
                return null;
        }
        throw new Exception("error in expr_stmt at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String print_stmt() throws Exception
    {
        switch (_token.type)
        {
            // print_stmt -> PRINT expr SEMI
            case PRINT:
                _lexer.appendToScript(indentation+ _lexer.yytext() + " ");
                Match(PRINT);
                expr();
                _lexer.removeLastFromScript();
                _lexer.appendToScript(_lexer.yytext() + "\n");
                Match(SEMI);
                return null;
        }
        throw new Exception("error in print_stmt at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String return_stmt() throws Exception
    {
        switch (_token.type)
        {
            // return_stmt -> RETURN expr SEMI
            case RETURN:
                _lexer.appendToScript(_lexer.yytext() + " ");
                Match(RETURN);
                expr();
                _lexer.appendToScript(_lexer.yytext() + " ");
                Match(SEMI);
                return null;
        }
        throw new Exception("error in return_stmt at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String if_stmt() throws Exception
    {
        switch (_token.type)
        {
            // if_stmt -> IF expr THEN stmt_list ELSE stmt_list END
            case IF:
                _lexer.appendToScript( indentation + _lexer.yytext() + " ");
                Match(IF);
                expr();
                _lexer.appendToScript(_lexer.yytext() + "\n");
                Match(THEN);
                updateIndentation(1);
                stmt_list();
                updateIndentation(-1);
                _lexer.appendToScript(indentation+_lexer.yytext() + "\n");
                Match(ELSE);
                updateIndentation(1);
                stmt_list();
                updateIndentation(-1);
                _lexer.appendToScript(indentation+_lexer.yytext() + "\n");
                Match(END);
                return null;
        }
        throw new Exception("error in if_stmt at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String while_stmt() throws Exception
    {
        switch (_token.type)
        {
            // while_stmt -> WHILE expr BEGIN stmt_list END
            case WHILE:
                _lexer.appendToScript(indentation +  _lexer.yytext() + " ");
                Match(WHILE);
                expr();
                _lexer.appendToScript( "\n"+ indentation +  _lexer.yytext() + "\n");
                Match(BEGIN);
                updateIndentation(1);
                stmt_list();
                updateIndentation(-1);
                _lexer.appendToScript(indentation + _lexer.yytext() + " \n");
                Match(END);
                return null;
        }
        throw new Exception("error in while_stmt at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public List<String> compound_stmt() throws Exception
    {
        switch (_token.type)
        {
            // compound_stmt -> BEGIN local_decls stmt_list END
            case BEGIN:
                _lexer.appendToScript(indentation + _lexer.yytext() + "\n");
                Match(BEGIN);
                updateIndentation(1);
                local_decls();
                stmt_list();
                updateIndentation(-1);
                _lexer.appendToScript(indentation + _lexer.yytext() + "\n");
                Match(END);
                return null;
        }
        throw new Exception("error in compound_stmt at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String args() throws Exception
    {
        switch (_token.type)
        {
            // args -> arg_list
            case LPAREN:
            case NEW:
            case BOOL_LIT:
            case INT_LIT:
            case IDENT:
                arg_list();
                return null;
            //  args -> ϵ
            case RPAREN:
                return null;
        }
        throw new Exception("error in args at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String arg_list() throws Exception
    {
        switch (_token.type)
        {
            // arg_list -> expr arg_list'
            case LPAREN:
            case NEW:
            case BOOL_LIT:
            case INT_LIT:
            case IDENT:

                expr();
                _lexer.removeLastFromScript();
                arg_list_();
                return null;
        }
        throw new Exception("error in arg_list at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String arg_list_() throws Exception
    {
        switch (_token.type)
        {
            // arg_list' -> COMMA expr arg_list'
            case COMMA:
                _lexer.appendToScript(_lexer.yytext() + " ");
                Match(COMMA);
                expr();
                _lexer.removeLastFromScript();
                arg_list_();
                return null;
            // arg_list' -> ϵ
            case RPAREN:
                return null;
        }
        throw new Exception("Incorrect argument format at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String expr() throws Exception
    {
        switch (_token.type)
        {
            // expr -> term expr'
            case LPAREN:
            case NEW:
            case BOOL_LIT:
            case INT_LIT:
            case IDENT:
                term();
                expr_();
                return null;
        }
        throw new Exception("Incorrect expression at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String expr_() throws Exception
    {
        switch (_token.type)
        {
            // expr' -> RELOP term expr'
            case RELOP:
                _lexer.appendToScript(_lexer.yytext() +" ");
                Match(RELOP);
                term();
                expr_();
                return null;
            //  expr' -> EXPROP term expr'
            case EXPROP:
                _lexer.appendToScript(_lexer.yytext() + " ");
                Match(EXPROP);
                term();
                expr_();
                return null;

            // expr' -> ϵ
            case RPAREN:
            case RBRACKET:
            case BEGIN:
            case THEN:
            case SEMI:
            case COMMA:
                return null;
        }
        throw new Exception("error in expr_ at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String term() throws Exception
    {
        switch (_token.type)
        {
            // term -> factor term'
            case LPAREN:
            case NEW:
            case BOOL_LIT:
            case INT_LIT:
            case IDENT:
                factor();
                _lexer.appendToScript(" ");
                term_();
                return null;
        }
        throw new Exception("Incorrect expression at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String term_() throws Exception
    {
        switch (_token.type)
        {
            // term' -> TERMOP factor term'
            case TERMOP:
                _lexer.appendToScript(_lexer.yytext() + " ");
                Match(TERMOP);
                factor();
                _lexer.appendToScript(" ");
                term_();
                return null;
            // term' -> ϵ
            case THEN:
            case BEGIN:
            case RPAREN:
            case RBRACKET:
            case RELOP:
            case EXPROP:
            case SEMI:
            case COMMA:
                return null;
        }
        throw new Exception("Incorrect expression at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String factor() throws Exception
    {
        switch (_token.type)
        {
            // factor -> LPAREN expr RPAREN
            case LPAREN:
                _lexer.appendToScript(_lexer.yytext());
                Match(LPAREN);
                expr();
                _lexer.removeLastFromScript();
                _lexer.appendToScript(_lexer.yytext());
                Match(RPAREN);
                return null;
            // factor -> NEW prim_type LBRACKET expr RBRACKET
            case NEW:
                _lexer.appendToScript(_lexer.yytext() +" ");
                Match(NEW);
                _lexer.appendToScript(_lexer.yytext());
                prim_type();
                _lexer.appendToScript(_lexer.yytext());
                Match(LBRACKET);
                expr();
                _lexer.removeLastFromScript();
                _lexer.appendToScript(_lexer.yytext());
                Match(RBRACKET);
                return null;
            // factor -> BOOL_LIT
            case BOOL_LIT:
                _lexer.appendToScript(_lexer.yytext());
                Match(BOOL_LIT);
                return null;
            // factor -> INT_LIT
            case INT_LIT:
                _lexer.appendToScript(_lexer.yytext());
                Match(INT_LIT);
                return null;
            // factor -> IDENT factor'
            case IDENT:
                _lexer.appendToScript(_lexer.yytext());
                Match(IDENT);
                factor_();
                return null;
        }
        throw new Exception("Incorrect expression at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
    public String factor_() throws Exception
    {
        switch (_token.type)
        {
            // factor' -> LPAREN args RPAREN
            case LPAREN:
                _lexer.appendToScript(_lexer.yytext());
                Match(LPAREN);
                args();
                _lexer.appendToScript(_lexer.yytext());
                Match(RPAREN);
                return null;
            // factor' -> LBRACKET expr RBRACKET
            case LBRACKET:
                _lexer.appendToScript(_lexer.yytext());
                Match(LBRACKET);
                expr();
                _lexer.removeLastFromScript();
                _lexer.appendToScript(_lexer.yytext());
                Match(RBRACKET);
                return null;
            // factor' -> DOT SIZE
            case DOT:
                _lexer.appendToScript(_lexer.yytext());
                Match(DOT);
                _lexer.appendToScript(_lexer.yytext());
                Match(SIZE);
                return null;
            // factor' -> ϵ
            case THEN:
            case BEGIN:
            case RPAREN:
            case RBRACKET:
            case RELOP:
            case EXPROP:
            case TERMOP:
            case SEMI:
            case COMMA:
                return null;
        }
        throw new Exception("Incorrect expression at " + (Lexer.yyline+1) +":" + (Lexer.yycolumn+1) + ".");
    }
}
