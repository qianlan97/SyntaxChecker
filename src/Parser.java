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
    // Right now I'm not sure if those functions given are correct
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
            case INT:
            case ENDMARKER:
                decl_list();
                Match(ENDMARKER);
                return null;
        }
        throw new Exception();
    }
    public List<String> decl_list() throws Exception
    {
        switch(_token.type)
        {
            // decl_list -> decl_list'
            case INT:
            case ENDMARKER:
                decl_list_();
                return null;
        }
        throw new Exception();
    }
    public List<String> decl_list_() throws Exception
    {
        switch(_token.type)
        {
            // decl_list'	-> fun_decl decl_list'
            case INT:
                fun_decl()  ;
                decl_list_();
                return null;
            case ENDMARKER:
                return null;
        }
        throw new Exception();
    }
    public List<String> fun_decl() throws Exception
    {
        switch(_token.type)
        {
            // fun_decl	-> type_spec IDENT "(" params ")" compound_stmt
            case INT:
                type_spec()    ;
                Match(IDENT)   ;
                Match(LPAREN)  ;
                params()       ;
                Match(RPAREN)  ;
                Match(BEGIN)   ;
                compound_stmt();
                Match(END)     ;
                return null;
        }
        throw new Exception();
    }
    public String params() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String type_spec() throws Exception
    {
        switch(_token.type)
        {
            // type_spec	-> "int"
            case INT:
                Match(INT);
                return null;
        }
        throw new Exception();
    }
    public String type_spec_() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String prim_type() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String local_decls() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String local_decls_() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String local_decl() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String stmt_list() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String stmt_list_() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String stmt() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String expr_stmt() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String print_stmt() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String return_stmt() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String if_stmt() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String while_stmt() throws Exception
    {
        throw new Exception("not implemented");
    }
    public List<String> compound_stmt() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String args() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String arg_list() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String arg_list_() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String expr() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String expr_() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String term() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String term_() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String factor() throws Exception
    {
        throw new Exception("not implemented");
    }
    public String factor_() throws Exception
    {
        throw new Exception("not implemented");
    }
}
