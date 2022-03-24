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
            //

        }
        throw new Exception();
    }
    public String return_stmt() throws Exception
    {
        switch (_token.type)
        {
            //

        }
        throw new Exception();
    }
    public String if_stmt() throws Exception
    {
        switch (_token.type)
        {
            //

        }
        throw new Exception();
    }
    public String while_stmt() throws Exception
    {
        switch (_token.type)
        {
            //

        }
        throw new Exception();
    }
    public List<String> compound_stmt() throws Exception
    {
        switch (_token.type)
        {
            //

        }
        throw new Exception();
    }
    public String args() throws Exception
    {
        switch (_token.type)
        {
            //

        }
        throw new Exception();
    }
    public String arg_list() throws Exception
    {
        switch (_token.type)
        {
            //

        }
        throw new Exception();
    }
    public String arg_list_() throws Exception
    {
        switch (_token.type)
        {
            //

        }
        throw new Exception();
    }
    public String expr() throws Exception
    {
        switch (_token.type)
        {
            //

        }
        throw new Exception();
    }
    public String expr_() throws Exception
    {
        switch (_token.type)
        {
            //

        }
        throw new Exception();
    }
    public String term() throws Exception
    {
        switch (_token.type)
        {
            //

        }
        throw new Exception();
    }
    public String term_() throws Exception
    {
        switch (_token.type)
        {
            //

        }
        throw new Exception();
    }
    public String factor() throws Exception
    {
        switch (_token.type)
        {
            //

        }
        throw new Exception();
    }
    public String factor_() throws Exception
    {
        switch (_token.type)
        {
            //

        }
        throw new Exception();
    }
}
