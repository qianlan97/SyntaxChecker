public class SyntaxChecker {
	public static void main(String[] args) throws Exception
    {
        //java.io.Reader r = new java.io.StringReader
        //("function main(a::int)::int\n"
        //+"begin\n"
        //+"    var x::int;\n"
        //+"    x == x + 1;  % There is a syntax error here.\n"
        //+"end\n"
        //);

        if(args.length <= 0)
            return;
        //args = new String[]
        //    {
        //          "D:\\CMPSC 470\\proj4-minc-RecPredict\\samples\\succ_01.minc",
        //    };

        java.io.Reader r = new java.io.FileReader(args[0]);

        Parser p = new Parser(r);
        p.yyparse();
	}
}
