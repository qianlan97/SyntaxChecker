import java.util.List;
import java.util.ArrayList;

public class Parser
{
    public static final int ENDMARKER   =  0;
    public static final int LEXERROR    =  1;

    public static final int BOOL        = 10;
    public static final int INT         = 11;
    public static final int NEW         = 12;
    public static final int PTR         = 13;
    public static final int DO          = 14;
    public static final int WHILE       = 15;
    public static final int IF          = 16;
    public static final int THEN        = 17;
    public static final int ELSE        = 18;
    public static final int ENDIF       = 19;
    public static final int BEGIN       = 20;
    public static final int END         = 21;
    public static final int LPAREN      = 22;
    public static final int RPAREN      = 23;
    public static final int LBRACKET    = 24;
    public static final int RBRACKET    = 25;
    public static final int ASSIGN      = 26;
    public static final int RELOP       = 27;
    public static final int EXPROP      = 28;
    public static final int TERMOP      = 29;
    public static final int SEMI        = 30;
    public static final int COMMA       = 31;
    public static final int BOOL_LIT    = 32;
    public static final int INT_LIT     = 33;
    public static final int IDENT       = 34;

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

        if(match == false)
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
    public List<String> compound_stmt() throws Exception
    {
        throw new Exception("not implemented");
    }
}
