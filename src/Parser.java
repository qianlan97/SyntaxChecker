import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class Parser
{
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

    public String Match(int token_type) throws Exception
    {
        boolean match = (token_type == _token.type);
        String lexeme = "";

        if(!match)
            throw new Exception();

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
        }
        catch(Exception e)
        {
            System.out.println("Error: there exists syntax error(s).");
            //System.out.println(e.getMessage());
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

//    template:
//    public List<String> program() throws Exception
//    {
//        switch(_token.type)
//        {
//            // program -> decl_list
//            case INT:
//            case ENDMARKER:
//                decl_list();
//                Match(ENDMARKER);
//                return null;
//        }
//        throw new Exception();
//    }

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
        throw new Exception("error in program");
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
        throw new Exception("error in decl_list");
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
        throw new Exception("error in decl_list_");
    }
    public List<String> fun_decl() throws Exception
    {
        switch(_token.type)
        {
            // fun_decl -> FUNC IDENT TYPEOF LPAREN params RPAREN FUNCRET prim_type BEGIN local_decls stmt_list END
            case FUNC:
                Match(FUNC);
                Match(IDENT);
                Match(TYPEOF);
                Match(LPAREN);
                params();
                Match(RPAREN);
                Match(FUNCRET);
                prim_type();
                Match(BEGIN);
                local_decls();
                stmt_list();
                Match(END);
                return null;
        }
        throw new Exception("errpr in fun_decl");
    }
    public String params() throws Exception
    {
        switch (_token.type)
        {
            // params -> param_list
            case IDENT:
                param_list()    ;
                return null;
            // params -> ϵ
            case RPAREN:
                return null;
        }
        throw new Exception("error in params");
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
        throw new Exception("error in param_list");
    }
    public String param_list_() throws Exception
    {
        switch (_token.type)
        {
            // param_list' -> COMMA param param_list'
            case COMMA:
                Match(COMMA);
                param();
                param_list_();
                return null;
            // param_list' -> ϵ
            case RPAREN:
                return null;
        }
        throw new Exception("error in param_list_");
    }
    public String param() throws Exception
    {
        switch (_token.type)
        {
            // param -> IDENT TYPEOF type_spec
            case IDENT:
                Match(IDENT);
                Match(TYPEOF);
                type_spec();
                return null;
        }
        throw new Exception("error in param");
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
        throw new Exception("error in type_spec");
    }
    public String type_spec_() throws Exception
    {
        switch (_token.type)
        {
            // type_spec' -> LBRACKET RBRACKET
            case LBRACKET:
                Match(LBRACKET);
                Match(RBRACKET);
                return null;
            // type_spec' -> ϵ
            case RPAREN:
            case SEMI:
            case COMMA:
                return null;
        }
        throw new Exception("error in type_spec_");
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
        throw new Exception("error in prim_type");
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
        throw new Exception("error in local_decls");
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
        throw new Exception("error in local_decls_");
    }
    public String local_decl() throws Exception
    {
        switch (_token.type)
        {
            // local_decl -> VAR IDENT TYPEOF type_spec SEMI
            case VAR:
                Match(VAR);
                Match(IDENT);
                Match(TYPEOF);
                type_spec();
                Match(SEMI);
                return null;
        }
        throw new Exception("error in local_decl");
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
        throw new Exception("error in stmt_list");
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
        throw new Exception("error in stmt_list_");
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
        throw new Exception("error in stmt");
    }
    public String expr_stmt() throws Exception
    {
        switch (_token.type)
        {
            // expr_stmt -> IDENT ASSIGN expr SEMI
            case IDENT:
                Match(IDENT);
                Match(ASSIGN);
                expr();
                Match(SEMI);
                return null;
        }
        throw new Exception("error in expr_stmt");
    }
    public String print_stmt() throws Exception
    {
        switch (_token.type)
        {
            // print_stmt -> PRINT expr SEMI
            case PRINT:
                Match(PRINT);
                expr();
                Match(SEMI);
                return null;
        }
        throw new Exception("error in print_stmt");
    }
    public String return_stmt() throws Exception
    {
        switch (_token.type)
        {
            // return_stmt -> RETURN expr SEMI
            case RETURN:
                Match(RETURN);
                expr();
                Match(SEMI);
                return null;
        }
        throw new Exception("error in return_stmt");
    }
    public String if_stmt() throws Exception
    {
        switch (_token.type)
        {
            // if_stmt -> IF expr THEN stmt_list ELSE stmt_list END
            case IF:
                Match(IF);
                expr();
                Match(THEN);
                stmt_list();
                Match(ELSE);
                stmt_list();
                Match(END);
                return null;
        }
        throw new Exception("error in if_stmt");
    }
    public String while_stmt() throws Exception
    {
        switch (_token.type)
        {
            // while_stmt -> WHILE expr BEGIN stmt_list END
            case WHILE:
                Match(WHILE);
                expr();
                Match(BEGIN);
                stmt_list();
                Match(END);
                return null;
        }
        throw new Exception("error in while_stmt");
    }
    public List<String> compound_stmt() throws Exception
    {
        switch (_token.type)
        {
            // compound_stmt -> BEGIN local_decls stmt_list END
            case BEGIN:
                Match(BEGIN);
                local_decls();
                stmt_list();
                Match(END);
                return null;
        }
        throw new Exception("error in compound_stmt");
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
        throw new Exception("error in args");
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
                arg_list_();
                return null;
        }
        throw new Exception("error in arg_list");
    }
    public String arg_list_() throws Exception
    {
        switch (_token.type)
        {
            // arg_list' -> COMMA expr arg_list'
            case COMMA:
                Match(COMMA);
                expr();
                arg_list_();
                return null;
            // arg_list' -> ϵ
            case RPAREN:
                return null;
        }
        throw new Exception("error in arg_list_");
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
        throw new Exception("error in expr");
    }
    public String expr_() throws Exception
    {
        switch (_token.type)
        {
            // expr' -> RELOP term expr'
            case RELOP:
                Match(RELOP);
                term();
                expr_();
                return null;
            //  expr' -> EXPROP term expr'
            case EXPROP:
                Match(EXPROP);
                term();
                expr_();
                return null;
            // expr' -> ϵ
            case BEGIN:
            case THEN:
            case RPAREN:
            case RBRACKET:
            case SEMI:
            case COMMA:
                return null;
        }
        throw new Exception("error in expr_");
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
                term_();
                return null;
        }
        throw new Exception("error in term");
    }
    public String term_() throws Exception
    {
        switch (_token.type)
        {
            // term' -> TERMOP factor term'
            case TERMOP:
                Match(TERMOP);
                factor();
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
        throw new Exception("error in term_");
    }
    public String factor() throws Exception
    {
        switch (_token.type)
        {
            // factor -> LPAREN expr RPAREN
            case LPAREN:
                Match(LPAREN);
                expr();
                Match(RPAREN);
                return null;
            // factor -> NEW prim_type LBRACKET expr RBRACKET
            case NEW:
                Match(NEW);
                prim_type();
                Match(LBRACKET);
                expr();
                Match(RBRACKET);
                return null;
            // factor -> BOOL_LIT
            case BOOL_LIT:
                Match(BOOL_LIT);
                return null;
            // factor -> INT_LIT
            case INT_LIT:
                Match(INT_LIT);
                return null;
            // factor -> IDENT factor'
            case IDENT:
                Match(IDENT);
                factor_();
                return null;
        }
        throw new Exception("error in factor");
    }
    public String factor_() throws Exception
    {
        switch (_token.type)
        {
            // factor' -> LPAREN args RPAREN
            case LPAREN:
                Match(LPAREN);
                args();
                Match(RPAREN);
                return null;
            // factor' -> LBRACKET expr RBRACKET
            case LBRACKET:
                Match(LBRACKET);
                expr();
                Match(RBRACKET);
                return null;
            // factor' -> DOT SIZE
            case DOT:
                Match(DOT);
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
        throw new Exception("error in factor_");
    }
}
