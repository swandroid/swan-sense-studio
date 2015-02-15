// $ANTLR 3.4 /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g 2015-02-11 20:36:26

package interdroid.swan.swansong;

import android.os.Bundle;
import java.util.Stack;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class SwanExpressionParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALL", "AND", "ANY", "CONFIG_AND", "CONFIG_IS", "CONFIG_VAL", "CONTAINS", "DIV", "EQUALS", "ESC_SEQ", "EXPONENT", "FLOAT", "GT", "GTEQ", "HEX_DIGIT", "ID", "INT", "LT", "LTEQ", "MAX", "MEAN", "MEDIAN", "MIN", "MINUS", "MOD", "MULT", "NONE", "NOT", "NOTEQUALS", "OR", "PLUS", "REGEX", "STRING", "TIME_UNIT", "WS", "'('", "')'", "','", "'.'", "':'", "'?'", "'@'", "'{'", "'}'"
    };

    public static final int EOF=-1;
    public static final int T__39=39;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__42=42;
    public static final int T__43=43;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int T__46=46;
    public static final int T__47=47;
    public static final int ALL=4;
    public static final int AND=5;
    public static final int ANY=6;
    public static final int CONFIG_AND=7;
    public static final int CONFIG_IS=8;
    public static final int CONFIG_VAL=9;
    public static final int CONTAINS=10;
    public static final int DIV=11;
    public static final int EQUALS=12;
    public static final int ESC_SEQ=13;
    public static final int EXPONENT=14;
    public static final int FLOAT=15;
    public static final int GT=16;
    public static final int GTEQ=17;
    public static final int HEX_DIGIT=18;
    public static final int ID=19;
    public static final int INT=20;
    public static final int LT=21;
    public static final int LTEQ=22;
    public static final int MAX=23;
    public static final int MEAN=24;
    public static final int MEDIAN=25;
    public static final int MIN=26;
    public static final int MINUS=27;
    public static final int MOD=28;
    public static final int MULT=29;
    public static final int NONE=30;
    public static final int NOT=31;
    public static final int NOTEQUALS=32;
    public static final int OR=33;
    public static final int PLUS=34;
    public static final int REGEX=35;
    public static final int STRING=36;
    public static final int TIME_UNIT=37;
    public static final int WS=38;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public SwanExpressionParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public SwanExpressionParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return SwanExpressionParser.tokenNames; }
    public String getGrammarFileName() { return "/Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g"; }


    public static final Expression parseExpression(final String expression) throws ExpressionParseException {
            if (expression == null || expression.trim().length() == 0)
                return null;

            CharStream stream = new ANTLRStringStream(expression);
            SwanExpressionLexer lexer = new SwanExpressionLexer(stream);
            TokenStream tokenStream = new CommonTokenStream(lexer);
            SwanExpressionParser parser = new SwanExpressionParser(tokenStream);
            try {
                return parser.expression() /* .expression */ ;
            } catch (RecognitionException e) {
                throw new ExpressionParseException(e);
            }
    }

    public static final long convertTime(Token time, Token unit) {
    	long unitFactor = 1;
    	if (unit != null) {
    		String unitText = unit.getText();
    		if (unitText.equals("h") || unitText.equals("H")) {
    			unitFactor = 60 * 60 * 1000;
    		} else if (unitText.equals("m") || unitText.equals("M")) {
    			unitFactor = 60 * 1000;
    		} else if (unitText.equals("s") || unitText.equals("S")) {
    			unitFactor = 1000;
    		}
    	}
    	return Long.parseLong(time.getText()) * unitFactor;
    }




    // $ANTLR start "configuration_options"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:53:1: configuration_options returns [Bundle configuration] : (id= ID val= CONFIG_VAL ) ( CONFIG_AND more_id= ID more_val= CONFIG_VAL )* ;
    public final Bundle configuration_options() throws RecognitionException {
        Bundle configuration = null;


        Token id=null;
        Token val=null;
        Token more_id=null;
        Token more_val=null;


        	Bundle config = new Bundle();

        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:57:2: ( (id= ID val= CONFIG_VAL ) ( CONFIG_AND more_id= ID more_val= CONFIG_VAL )* )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:58:2: (id= ID val= CONFIG_VAL ) ( CONFIG_AND more_id= ID more_val= CONFIG_VAL )*
            {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:58:2: (id= ID val= CONFIG_VAL )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:58:3: id= ID val= CONFIG_VAL
            {
            id=(Token)match(input,ID,FOLLOW_ID_in_configuration_options62); 

            val=(Token)match(input,CONFIG_VAL,FOLLOW_CONFIG_VAL_in_configuration_options66); 

            }


            config.putString(id.getText(), val.getText().substring(1));

            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:60:2: ( CONFIG_AND more_id= ID more_val= CONFIG_VAL )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==CONFIG_AND) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:60:3: CONFIG_AND more_id= ID more_val= CONFIG_VAL
            	    {
            	    match(input,CONFIG_AND,FOLLOW_CONFIG_AND_in_configuration_options76); 

            	    more_id=(Token)match(input,ID,FOLLOW_ID_in_configuration_options80); 

            	    more_val=(Token)match(input,CONFIG_VAL,FOLLOW_CONFIG_VAL_in_configuration_options84); 

            	    config.putString(more_id.getText(), more_val.getText().substring(1));

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            configuration = config;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return configuration;
    }
    // $ANTLR end "configuration_options"



    // $ANTLR start "value_path"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:66:1: value_path returns [String value_path] : vp= v_p ( '.' more_id= v_p )* ;
    public final String value_path() throws RecognitionException {
        String value_path = null;


        String vp =null;

        String more_id =null;



        	StringBuffer buf = new StringBuffer();

        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:70:2: (vp= v_p ( '.' more_id= v_p )* )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:71:2: vp= v_p ( '.' more_id= v_p )*
            {
            pushFollow(FOLLOW_v_p_in_value_path120);
            vp=v_p();

            state._fsp--;


            buf.append(vp);

            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:73:2: ( '.' more_id= v_p )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==42) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:73:3: '.' more_id= v_p
            	    {
            	    match(input,42,FOLLOW_42_in_value_path129); 

            	    pushFollow(FOLLOW_v_p_in_value_path133);
            	    more_id=v_p();

            	    state._fsp--;


            	    buf.append('.'); buf.append(vp);

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            value_path = buf.toString();

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return value_path;
    }
    // $ANTLR end "value_path"



    // $ANTLR start "v_p"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:79:1: v_p returns [String vp] : (id= ID |str= STRING );
    public final String v_p() throws RecognitionException {
        String vp = null;


        Token id=null;
        Token str=null;

        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:80:2: (id= ID |str= STRING )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==ID) ) {
                alt3=1;
            }
            else if ( (LA3_0==STRING) ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;

            }
            switch (alt3) {
                case 1 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:80:4: id= ID
                    {
                    id=(Token)match(input,ID,FOLLOW_ID_in_v_p165); 

                     vp = id.getText(); 

                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:81:4: str= STRING
                    {
                    str=(Token)match(input,STRING,FOLLOW_STRING_in_v_p174); 

                     vp = str.getText(); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return vp;
    }
    // $ANTLR end "v_p"



    // $ANTLR start "comparator"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:84:1: comparator returns [Comparator comparator] : ( GT | LT | GTEQ | LTEQ | EQUALS | NOTEQUALS | REGEX | CONTAINS );
    public final Comparator comparator() throws RecognitionException {
        Comparator comparator = null;


        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:85:2: ( GT | LT | GTEQ | LTEQ | EQUALS | NOTEQUALS | REGEX | CONTAINS )
            int alt4=8;
            switch ( input.LA(1) ) {
            case GT:
                {
                alt4=1;
                }
                break;
            case LT:
                {
                alt4=2;
                }
                break;
            case GTEQ:
                {
                alt4=3;
                }
                break;
            case LTEQ:
                {
                alt4=4;
                }
                break;
            case EQUALS:
                {
                alt4=5;
                }
                break;
            case NOTEQUALS:
                {
                alt4=6;
                }
                break;
            case REGEX:
                {
                alt4=7;
                }
                break;
            case CONTAINS:
                {
                alt4=8;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;

            }

            switch (alt4) {
                case 1 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:85:4: GT
                    {
                    match(input,GT,FOLLOW_GT_in_comparator191); 

                    comparator = Comparator.GREATER_THAN;

                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:86:4: LT
                    {
                    match(input,LT,FOLLOW_LT_in_comparator198); 

                    comparator = Comparator.LESS_THAN;

                    }
                    break;
                case 3 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:87:4: GTEQ
                    {
                    match(input,GTEQ,FOLLOW_GTEQ_in_comparator205); 

                    comparator = Comparator.GREATER_THAN_OR_EQUALS;

                    }
                    break;
                case 4 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:88:4: LTEQ
                    {
                    match(input,LTEQ,FOLLOW_LTEQ_in_comparator212); 

                    comparator = Comparator.LESS_THAN_OR_EQUALS;

                    }
                    break;
                case 5 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:89:4: EQUALS
                    {
                    match(input,EQUALS,FOLLOW_EQUALS_in_comparator219); 

                    comparator = Comparator.EQUALS;

                    }
                    break;
                case 6 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:90:4: NOTEQUALS
                    {
                    match(input,NOTEQUALS,FOLLOW_NOTEQUALS_in_comparator226); 

                    comparator = Comparator.NOT_EQUALS;

                    }
                    break;
                case 7 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:91:4: REGEX
                    {
                    match(input,REGEX,FOLLOW_REGEX_in_comparator233); 

                    comparator = Comparator.REGEX_MATCH;

                    }
                    break;
                case 8 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:92:4: CONTAINS
                    {
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_comparator240); 

                    comparator = Comparator.STRING_CONTAINS;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return comparator;
    }
    // $ANTLR end "comparator"



    // $ANTLR start "logic_operator"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:95:1: logic_operator returns [LogicOperator logic_operator] : (binary= binary_logic_operator |unary= unary_logic_operator );
    public final LogicOperator logic_operator() throws RecognitionException {
        LogicOperator logic_operator = null;


        BinaryLogicOperator binary =null;

        UnaryLogicOperator unary =null;


        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:96:2: (binary= binary_logic_operator |unary= unary_logic_operator )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==AND||LA5_0==OR) ) {
                alt5=1;
            }
            else if ( (LA5_0==NOT) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;

            }
            switch (alt5) {
                case 1 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:97:3: binary= binary_logic_operator
                    {
                    pushFollow(FOLLOW_binary_logic_operator_in_logic_operator262);
                    binary=binary_logic_operator();

                    state._fsp--;


                    logic_operator = binary /* .logic_operator */ ;

                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:99:4: unary= unary_logic_operator
                    {
                    pushFollow(FOLLOW_unary_logic_operator_in_logic_operator275);
                    unary=unary_logic_operator();

                    state._fsp--;


                    logic_operator = unary /* .logic_operator */ ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return logic_operator;
    }
    // $ANTLR end "logic_operator"



    // $ANTLR start "binary_logic_operator"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:103:1: binary_logic_operator returns [BinaryLogicOperator logic_operator] : ( AND | OR );
    public final BinaryLogicOperator binary_logic_operator() throws RecognitionException {
        BinaryLogicOperator logic_operator = null;


        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:104:2: ( AND | OR )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==AND) ) {
                alt6=1;
            }
            else if ( (LA6_0==OR) ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;

            }
            switch (alt6) {
                case 1 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:104:4: AND
                    {
                    match(input,AND,FOLLOW_AND_in_binary_logic_operator296); 

                    logic_operator = BinaryLogicOperator.AND;

                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:105:4: OR
                    {
                    match(input,OR,FOLLOW_OR_in_binary_logic_operator303); 

                    logic_operator = BinaryLogicOperator.OR;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return logic_operator;
    }
    // $ANTLR end "binary_logic_operator"



    // $ANTLR start "unary_logic_operator"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:108:1: unary_logic_operator returns [UnaryLogicOperator logic_operator] : NOT ;
    public final UnaryLogicOperator unary_logic_operator() throws RecognitionException {
        UnaryLogicOperator logic_operator = null;


        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:109:2: ( NOT )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:109:4: NOT
            {
            match(input,NOT,FOLLOW_NOT_in_unary_logic_operator320); 

            logic_operator = UnaryLogicOperator.NOT;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return logic_operator;
    }
    // $ANTLR end "unary_logic_operator"



    // $ANTLR start "math_operator"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:112:1: math_operator returns [MathOperator math_operator] : (add= additive_math_operator |mult= multiplicative_math_operator );
    public final MathOperator math_operator() throws RecognitionException {
        MathOperator math_operator = null;


        MathOperator add =null;

        MathOperator mult =null;


        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:113:2: (add= additive_math_operator |mult= multiplicative_math_operator )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==MINUS||LA7_0==PLUS) ) {
                alt7=1;
            }
            else if ( (LA7_0==DIV||(LA7_0 >= MOD && LA7_0 <= MULT)) ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;

            }
            switch (alt7) {
                case 1 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:113:4: add= additive_math_operator
                    {
                    pushFollow(FOLLOW_additive_math_operator_in_math_operator339);
                    add=additive_math_operator();

                    state._fsp--;


                    math_operator =add /* .math_operator */ ;

                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:114:4: mult= multiplicative_math_operator
                    {
                    pushFollow(FOLLOW_multiplicative_math_operator_in_math_operator348);
                    mult=multiplicative_math_operator();

                    state._fsp--;


                    math_operator =mult /* .math_operator */ ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return math_operator;
    }
    // $ANTLR end "math_operator"



    // $ANTLR start "additive_math_operator"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:117:1: additive_math_operator returns [MathOperator math_operator] : ( PLUS | MINUS );
    public final MathOperator additive_math_operator() throws RecognitionException {
        MathOperator math_operator = null;


        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:118:2: ( PLUS | MINUS )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==PLUS) ) {
                alt8=1;
            }
            else if ( (LA8_0==MINUS) ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;

            }
            switch (alt8) {
                case 1 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:118:4: PLUS
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_additive_math_operator365); 

                    math_operator = MathOperator.PLUS;

                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:119:4: MINUS
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_additive_math_operator372); 

                    math_operator = MathOperator.MINUS;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return math_operator;
    }
    // $ANTLR end "additive_math_operator"



    // $ANTLR start "multiplicative_math_operator"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:122:1: multiplicative_math_operator returns [MathOperator math_operator] : ( MULT | DIV | MOD );
    public final MathOperator multiplicative_math_operator() throws RecognitionException {
        MathOperator math_operator = null;


        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:123:2: ( MULT | DIV | MOD )
            int alt9=3;
            switch ( input.LA(1) ) {
            case MULT:
                {
                alt9=1;
                }
                break;
            case DIV:
                {
                alt9=2;
                }
                break;
            case MOD:
                {
                alt9=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;

            }

            switch (alt9) {
                case 1 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:123:4: MULT
                    {
                    match(input,MULT,FOLLOW_MULT_in_multiplicative_math_operator390); 

                    math_operator = MathOperator.TIMES;

                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:124:4: DIV
                    {
                    match(input,DIV,FOLLOW_DIV_in_multiplicative_math_operator397); 

                    math_operator = MathOperator.DIVIDE;

                    }
                    break;
                case 3 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:125:4: MOD
                    {
                    match(input,MOD,FOLLOW_MOD_in_multiplicative_math_operator404); 

                    math_operator = MathOperator.MOD;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return math_operator;
    }
    // $ANTLR end "multiplicative_math_operator"



    // $ANTLR start "history_mode"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:128:1: history_mode returns [HistoryReductionMode history_mode] : ( ALL | MAX | MIN | MEAN | MEDIAN | ANY );
    public final HistoryReductionMode history_mode() throws RecognitionException {
        HistoryReductionMode history_mode = null;


        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:129:2: ( ALL | MAX | MIN | MEAN | MEDIAN | ANY )
            int alt10=6;
            switch ( input.LA(1) ) {
            case ALL:
                {
                alt10=1;
                }
                break;
            case MAX:
                {
                alt10=2;
                }
                break;
            case MIN:
                {
                alt10=3;
                }
                break;
            case MEAN:
                {
                alt10=4;
                }
                break;
            case MEDIAN:
                {
                alt10=5;
                }
                break;
            case ANY:
                {
                alt10=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;

            }

            switch (alt10) {
                case 1 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:129:4: ALL
                    {
                    match(input,ALL,FOLLOW_ALL_in_history_mode421); 

                    history_mode = HistoryReductionMode.ALL;

                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:130:4: MAX
                    {
                    match(input,MAX,FOLLOW_MAX_in_history_mode428); 

                    history_mode = HistoryReductionMode.MAX;

                    }
                    break;
                case 3 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:131:4: MIN
                    {
                    match(input,MIN,FOLLOW_MIN_in_history_mode435); 

                    history_mode = HistoryReductionMode.MIN;

                    }
                    break;
                case 4 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:132:4: MEAN
                    {
                    match(input,MEAN,FOLLOW_MEAN_in_history_mode442); 

                    history_mode = HistoryReductionMode.MEAN;

                    }
                    break;
                case 5 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:133:4: MEDIAN
                    {
                    match(input,MEDIAN,FOLLOW_MEDIAN_in_history_mode449); 

                    history_mode = HistoryReductionMode.MEDIAN;

                    }
                    break;
                case 6 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:134:4: ANY
                    {
                    match(input,ANY,FOLLOW_ANY_in_history_mode456); 

                    history_mode = HistoryReductionMode.ANY;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return history_mode;
    }
    // $ANTLR end "history_mode"



    // $ANTLR start "sensor_value_expression"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:139:1: sensor_value_expression returns [SensorValueExpression value_expression] : (location= ID '@' entity= ID ':' path= value_path |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options |location= ID '@' entity= ID ':' path= value_path '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' );
    public final SensorValueExpression sensor_value_expression() throws RecognitionException {
        SensorValueExpression value_expression = null;


        Token location=null;
        Token entity=null;
        String path =null;

        Bundle config =null;

        HistoryReductionMode mode =null;

        Long time =null;


        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:140:2: (location= ID '@' entity= ID ':' path= value_path |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options |location= ID '@' entity= ID ':' path= value_path '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' )
            int alt13=4;
            alt13 = dfa13.predict(input);
            switch (alt13) {
                case 1 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:140:4: location= ID '@' entity= ID ':' path= value_path
                    {
                    location=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression477); 

                    match(input,45,FOLLOW_45_in_sensor_value_expression479); 

                    entity=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression483); 

                    match(input,43,FOLLOW_43_in_sensor_value_expression485); 

                    pushFollow(FOLLOW_value_path_in_sensor_value_expression489);
                    path=value_path();

                    state._fsp--;


                    value_expression = new SensorValueExpression(location.getText(), entity.getText(), path /*.value_path */, null, HistoryReductionMode.ANY, 0);

                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:142:4: location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options
                    {
                    location=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression501); 

                    match(input,45,FOLLOW_45_in_sensor_value_expression503); 

                    entity=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression507); 

                    match(input,43,FOLLOW_43_in_sensor_value_expression509); 

                    pushFollow(FOLLOW_value_path_in_sensor_value_expression513);
                    path=value_path();

                    state._fsp--;


                    match(input,44,FOLLOW_44_in_sensor_value_expression515); 

                    pushFollow(FOLLOW_configuration_options_in_sensor_value_expression519);
                    config=configuration_options();

                    state._fsp--;


                    value_expression = new SensorValueExpression(location.getText(), entity.getText(), path /*.value_path */, config /*.configuration */, HistoryReductionMode.ANY, 0);

                    }
                    break;
                case 3 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:144:4: location= ID '@' entity= ID ':' path= value_path '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}'
                    {
                    location=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression531); 

                    match(input,45,FOLLOW_45_in_sensor_value_expression533); 

                    entity=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression537); 

                    match(input,43,FOLLOW_43_in_sensor_value_expression539); 

                    pushFollow(FOLLOW_value_path_in_sensor_value_expression543);
                    path=value_path();

                    state._fsp--;


                    match(input,46,FOLLOW_46_in_sensor_value_expression545); 

                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:144:54: ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value )
                    int alt11=3;
                    switch ( input.LA(1) ) {
                    case ALL:
                        {
                        int LA11_1 = input.LA(2);

                        if ( (LA11_1==41) ) {
                            alt11=1;
                        }
                        else if ( (LA11_1==47) ) {
                            alt11=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 11, 1, input);

                            throw nvae;

                        }
                        }
                        break;
                    case MAX:
                        {
                        int LA11_2 = input.LA(2);

                        if ( (LA11_2==41) ) {
                            alt11=1;
                        }
                        else if ( (LA11_2==47) ) {
                            alt11=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 11, 2, input);

                            throw nvae;

                        }
                        }
                        break;
                    case MIN:
                        {
                        int LA11_3 = input.LA(2);

                        if ( (LA11_3==41) ) {
                            alt11=1;
                        }
                        else if ( (LA11_3==47) ) {
                            alt11=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 11, 3, input);

                            throw nvae;

                        }
                        }
                        break;
                    case MEAN:
                        {
                        int LA11_4 = input.LA(2);

                        if ( (LA11_4==41) ) {
                            alt11=1;
                        }
                        else if ( (LA11_4==47) ) {
                            alt11=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 11, 4, input);

                            throw nvae;

                        }
                        }
                        break;
                    case MEDIAN:
                        {
                        int LA11_5 = input.LA(2);

                        if ( (LA11_5==41) ) {
                            alt11=1;
                        }
                        else if ( (LA11_5==47) ) {
                            alt11=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 11, 5, input);

                            throw nvae;

                        }
                        }
                        break;
                    case ANY:
                        {
                        int LA11_6 = input.LA(2);

                        if ( (LA11_6==41) ) {
                            alt11=1;
                        }
                        else if ( (LA11_6==47) ) {
                            alt11=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 11, 6, input);

                            throw nvae;

                        }
                        }
                        break;
                    case INT:
                        {
                        alt11=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 11, 0, input);

                        throw nvae;

                    }

                    switch (alt11) {
                        case 1 :
                            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:144:55: (mode= history_mode ',' time= time_value )
                            {
                            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:144:55: (mode= history_mode ',' time= time_value )
                            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:144:56: mode= history_mode ',' time= time_value
                            {
                            pushFollow(FOLLOW_history_mode_in_sensor_value_expression551);
                            mode=history_mode();

                            state._fsp--;


                            match(input,41,FOLLOW_41_in_sensor_value_expression553); 

                            pushFollow(FOLLOW_time_value_in_sensor_value_expression557);
                            time=time_value();

                            state._fsp--;


                            }


                            }
                            break;
                        case 2 :
                            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:144:97: mode= history_mode
                            {
                            pushFollow(FOLLOW_history_mode_in_sensor_value_expression564);
                            mode=history_mode();

                            state._fsp--;


                            }
                            break;
                        case 3 :
                            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:144:117: time= time_value
                            {
                            pushFollow(FOLLOW_time_value_in_sensor_value_expression570);
                            time=time_value();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input,47,FOLLOW_47_in_sensor_value_expression573); 

                    if (time == null) {
                    				value_expression = new SensorValueExpression(location.getText(), entity.getText(), path /* .value_path */, null, mode /*.history_mode */, 0);
                    			} else {
                    				value_expression = new SensorValueExpression(location.getText(), entity.getText(), path /* .value_path */, null, mode /*.history_mode */, time);
                    			}

                    }
                    break;
                case 4 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:150:4: location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}'
                    {
                    location=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression585); 

                    match(input,45,FOLLOW_45_in_sensor_value_expression587); 

                    entity=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression591); 

                    match(input,43,FOLLOW_43_in_sensor_value_expression593); 

                    pushFollow(FOLLOW_value_path_in_sensor_value_expression597);
                    path=value_path();

                    state._fsp--;


                    match(input,44,FOLLOW_44_in_sensor_value_expression599); 

                    pushFollow(FOLLOW_configuration_options_in_sensor_value_expression603);
                    config=configuration_options();

                    state._fsp--;


                    match(input,46,FOLLOW_46_in_sensor_value_expression605); 

                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:150:87: ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value )
                    int alt12=3;
                    switch ( input.LA(1) ) {
                    case ALL:
                        {
                        int LA12_1 = input.LA(2);

                        if ( (LA12_1==41) ) {
                            alt12=1;
                        }
                        else if ( (LA12_1==47) ) {
                            alt12=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 12, 1, input);

                            throw nvae;

                        }
                        }
                        break;
                    case MAX:
                        {
                        int LA12_2 = input.LA(2);

                        if ( (LA12_2==41) ) {
                            alt12=1;
                        }
                        else if ( (LA12_2==47) ) {
                            alt12=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 12, 2, input);

                            throw nvae;

                        }
                        }
                        break;
                    case MIN:
                        {
                        int LA12_3 = input.LA(2);

                        if ( (LA12_3==41) ) {
                            alt12=1;
                        }
                        else if ( (LA12_3==47) ) {
                            alt12=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 12, 3, input);

                            throw nvae;

                        }
                        }
                        break;
                    case MEAN:
                        {
                        int LA12_4 = input.LA(2);

                        if ( (LA12_4==41) ) {
                            alt12=1;
                        }
                        else if ( (LA12_4==47) ) {
                            alt12=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 12, 4, input);

                            throw nvae;

                        }
                        }
                        break;
                    case MEDIAN:
                        {
                        int LA12_5 = input.LA(2);

                        if ( (LA12_5==41) ) {
                            alt12=1;
                        }
                        else if ( (LA12_5==47) ) {
                            alt12=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 12, 5, input);

                            throw nvae;

                        }
                        }
                        break;
                    case ANY:
                        {
                        int LA12_6 = input.LA(2);

                        if ( (LA12_6==41) ) {
                            alt12=1;
                        }
                        else if ( (LA12_6==47) ) {
                            alt12=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 12, 6, input);

                            throw nvae;

                        }
                        }
                        break;
                    case INT:
                        {
                        alt12=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 12, 0, input);

                        throw nvae;

                    }

                    switch (alt12) {
                        case 1 :
                            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:150:88: (mode= history_mode ',' time= time_value )
                            {
                            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:150:88: (mode= history_mode ',' time= time_value )
                            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:150:89: mode= history_mode ',' time= time_value
                            {
                            pushFollow(FOLLOW_history_mode_in_sensor_value_expression611);
                            mode=history_mode();

                            state._fsp--;


                            match(input,41,FOLLOW_41_in_sensor_value_expression613); 

                            pushFollow(FOLLOW_time_value_in_sensor_value_expression617);
                            time=time_value();

                            state._fsp--;


                            }


                            }
                            break;
                        case 2 :
                            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:150:130: mode= history_mode
                            {
                            pushFollow(FOLLOW_history_mode_in_sensor_value_expression624);
                            mode=history_mode();

                            state._fsp--;


                            }
                            break;
                        case 3 :
                            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:150:150: time= time_value
                            {
                            pushFollow(FOLLOW_time_value_in_sensor_value_expression630);
                            time=time_value();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input,47,FOLLOW_47_in_sensor_value_expression633); 

                    if (time == null) {
                    				value_expression = new SensorValueExpression(location.getText(), entity.getText(), path /* .value_path */ , config /*.configuration */ , mode /* .history_mode */, 0);
                    			} else {
                    				value_expression = new SensorValueExpression(location.getText(), entity.getText(), path /* .value_path */ , config /*.configuration */ , mode /* .history_mode */ , time);
                    			}

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return value_expression;
    }
    // $ANTLR end "sensor_value_expression"



    // $ANTLR start "constant_value_expression"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:158:1: constant_value_expression returns [ConstantValueExpression value_expression] : (i= INT |f= FLOAT |raw= STRING );
    public final ConstantValueExpression constant_value_expression() throws RecognitionException {
        ConstantValueExpression value_expression = null;


        Token i=null;
        Token f=null;
        Token raw=null;

        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:159:2: (i= INT |f= FLOAT |raw= STRING )
            int alt14=3;
            switch ( input.LA(1) ) {
            case INT:
                {
                alt14=1;
                }
                break;
            case FLOAT:
                {
                alt14=2;
                }
                break;
            case STRING:
                {
                alt14=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;

            }

            switch (alt14) {
                case 1 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:159:4: i= INT
                    {
                    i=(Token)match(input,INT,FOLLOW_INT_in_constant_value_expression655); 

                    value_expression = new ConstantValueExpression(Long.parseLong(i.getText()));

                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:161:4: f= FLOAT
                    {
                    f=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_constant_value_expression668); 

                    value_expression = new ConstantValueExpression(Double.parseDouble(f.getText()));

                    }
                    break;
                case 3 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:163:5: raw= STRING
                    {
                    raw=(Token)match(input,STRING,FOLLOW_STRING_in_constant_value_expression682); 

                    value_expression = new ConstantValueExpression(raw.getText());

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return value_expression;
    }
    // $ANTLR end "constant_value_expression"



    // $ANTLR start "value_expression"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:167:1: value_expression returns [ValueExpression value_expression] : (constant= constant_value_expression |sensor= sensor_value_expression );
    public final ValueExpression value_expression() throws RecognitionException {
        ValueExpression value_expression = null;


        ConstantValueExpression constant =null;

        SensorValueExpression sensor =null;


        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:168:2: (constant= constant_value_expression |sensor= sensor_value_expression )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==FLOAT||LA15_0==INT||LA15_0==STRING) ) {
                alt15=1;
            }
            else if ( (LA15_0==ID) ) {
                alt15=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;

            }
            switch (alt15) {
                case 1 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:168:4: constant= constant_value_expression
                    {
                    pushFollow(FOLLOW_constant_value_expression_in_value_expression705);
                    constant=constant_value_expression();

                    state._fsp--;


                    value_expression = constant /* value expression */ ;

                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:170:5: sensor= sensor_value_expression
                    {
                    pushFollow(FOLLOW_sensor_value_expression_in_value_expression719);
                    sensor=sensor_value_expression();

                    state._fsp--;


                    value_expression = sensor /* value expression */ ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return value_expression;
    }
    // $ANTLR end "value_expression"



    // $ANTLR start "parentheticalExpression"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:174:1: parentheticalExpression returns [Expression expression] : (val= value_expression | '(' ( WS )* exp= orExpression ( WS )* ')' );
    public final Expression parentheticalExpression() throws RecognitionException {
        Expression expression = null;


        ValueExpression val =null;

        Expression exp =null;


        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:175:5: (val= value_expression | '(' ( WS )* exp= orExpression ( WS )* ')' )
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==FLOAT||(LA18_0 >= ID && LA18_0 <= INT)||LA18_0==STRING) ) {
                alt18=1;
            }
            else if ( (LA18_0==39) ) {
                alt18=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;

            }
            switch (alt18) {
                case 1 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:175:9: val= value_expression
                    {
                    pushFollow(FOLLOW_value_expression_in_parentheticalExpression748);
                    val=value_expression();

                    state._fsp--;


                    expression = val;

                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:177:9: '(' ( WS )* exp= orExpression ( WS )* ')'
                    {
                    match(input,39,FOLLOW_39_in_parentheticalExpression773); 

                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:177:13: ( WS )*
                    loop16:
                    do {
                        int alt16=2;
                        int LA16_0 = input.LA(1);

                        if ( (LA16_0==WS) ) {
                            alt16=1;
                        }


                        switch (alt16) {
                    	case 1 :
                    	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:177:13: WS
                    	    {
                    	    match(input,WS,FOLLOW_WS_in_parentheticalExpression775); 

                    	    }
                    	    break;

                    	default :
                    	    break loop16;
                        }
                    } while (true);


                    pushFollow(FOLLOW_orExpression_in_parentheticalExpression780);
                    exp=orExpression();

                    state._fsp--;


                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:177:34: ( WS )*
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( (LA17_0==WS) ) {
                            alt17=1;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:177:34: WS
                    	    {
                    	    match(input,WS,FOLLOW_WS_in_parentheticalExpression782); 

                    	    }
                    	    break;

                    	default :
                    	    break loop17;
                        }
                    } while (true);


                    match(input,40,FOLLOW_40_in_parentheticalExpression785); 

                    expression = exp /* .expression */ ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return expression;
    }
    // $ANTLR end "parentheticalExpression"



    // $ANTLR start "multiplicativeExpression"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:181:1: multiplicativeExpression returns [Expression expression] : left= parentheticalExpression ( ( WS )* (location= ID '@' )? op= multiplicative_math_operator ( WS )* right= parentheticalExpression )* ;
    public final Expression multiplicativeExpression() throws RecognitionException {
        Expression expression = null;


        Token location=null;
        Expression left =null;

        MathOperator op =null;

        Expression right =null;



            Stack<ValueExpression> rightStack = new Stack<ValueExpression>();
            Stack<MathOperator> opStack = new Stack<MathOperator>();
            Stack<String> locationStack = new Stack<String>();

        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:187:5: (left= parentheticalExpression ( ( WS )* (location= ID '@' )? op= multiplicative_math_operator ( WS )* right= parentheticalExpression )* )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:187:7: left= parentheticalExpression ( ( WS )* (location= ID '@' )? op= multiplicative_math_operator ( WS )* right= parentheticalExpression )*
            {
            pushFollow(FOLLOW_parentheticalExpression_in_multiplicativeExpression832);
            left=parentheticalExpression();

            state._fsp--;


            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:188:5: ( ( WS )* (location= ID '@' )? op= multiplicative_math_operator ( WS )* right= parentheticalExpression )*
            loop22:
            do {
                int alt22=2;
                alt22 = dfa22.predict(input);
                switch (alt22) {
            	case 1 :
            	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:188:6: ( WS )* (location= ID '@' )? op= multiplicative_math_operator ( WS )* right= parentheticalExpression
            	    {
            	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:188:6: ( WS )*
            	    loop19:
            	    do {
            	        int alt19=2;
            	        int LA19_0 = input.LA(1);

            	        if ( (LA19_0==WS) ) {
            	            alt19=1;
            	        }


            	        switch (alt19) {
            	    	case 1 :
            	    	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:188:6: WS
            	    	    {
            	    	    match(input,WS,FOLLOW_WS_in_multiplicativeExpression839); 

            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop19;
            	        }
            	    } while (true);


            	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:188:10: (location= ID '@' )?
            	    int alt20=2;
            	    int LA20_0 = input.LA(1);

            	    if ( (LA20_0==ID) ) {
            	        alt20=1;
            	    }
            	    switch (alt20) {
            	        case 1 :
            	            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:188:11: location= ID '@'
            	            {
            	            location=(Token)match(input,ID,FOLLOW_ID_in_multiplicativeExpression845); 

            	            match(input,45,FOLLOW_45_in_multiplicativeExpression847); 

            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_multiplicative_math_operator_in_multiplicativeExpression853);
            	    op=multiplicative_math_operator();

            	    state._fsp--;


            	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:188:61: ( WS )*
            	    loop21:
            	    do {
            	        int alt21=2;
            	        int LA21_0 = input.LA(1);

            	        if ( (LA21_0==WS) ) {
            	            alt21=1;
            	        }


            	        switch (alt21) {
            	    	case 1 :
            	    	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:188:61: WS
            	    	    {
            	    	    match(input,WS,FOLLOW_WS_in_multiplicativeExpression855); 

            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop21;
            	        }
            	    } while (true);


            	    pushFollow(FOLLOW_parentheticalExpression_in_multiplicativeExpression860);
            	    right=parentheticalExpression();

            	    state._fsp--;


            	    locationStack.push(location == null ? Expression.LOCATION_INFER : location.getText()); opStack.push(op /* .math_operator */ ); rightStack.push((ValueExpression) right /* .expression */ );

            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);



                while(rightStack.size() > 1) {
                    ValueExpression temp = rightStack.pop();
                    rightStack.push(new MathValueExpression(locationStack.pop(), rightStack.pop(), opStack.pop(), temp, HistoryReductionMode.DEFAULT_MODE));
                }
                if (rightStack.size() > 0) {
                    expression = new MathValueExpression(locationStack.pop(), (ValueExpression) left /* .expression */ , opStack.pop(), rightStack.pop(), HistoryReductionMode.DEFAULT_MODE);
                } else {
                    expression = left /* .expression */ ;
                }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return expression;
    }
    // $ANTLR end "multiplicativeExpression"



    // $ANTLR start "additiveExpression"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:204:1: additiveExpression returns [Expression expression] : left= multiplicativeExpression ( (location= ID '@' )? op= additive_math_operator right= multiplicativeExpression )* ;
    public final Expression additiveExpression() throws RecognitionException {
        Expression expression = null;


        Token location=null;
        Expression left =null;

        MathOperator op =null;

        Expression right =null;



            Stack<ValueExpression> rightStack = new Stack<ValueExpression>();
            Stack<MathOperator> opStack = new Stack<MathOperator>();
            Stack<String> locationStack = new Stack<String>();

        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:210:5: (left= multiplicativeExpression ( (location= ID '@' )? op= additive_math_operator right= multiplicativeExpression )* )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:210:7: left= multiplicativeExpression ( (location= ID '@' )? op= additive_math_operator right= multiplicativeExpression )*
            {
            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression908);
            left=multiplicativeExpression();

            state._fsp--;


            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:211:5: ( (location= ID '@' )? op= additive_math_operator right= multiplicativeExpression )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==ID) ) {
                    int LA24_2 = input.LA(2);

                    if ( (LA24_2==45) ) {
                        int LA24_4 = input.LA(3);

                        if ( (LA24_4==MINUS||LA24_4==PLUS) ) {
                            alt24=1;
                        }


                    }


                }
                else if ( (LA24_0==MINUS||LA24_0==PLUS) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:211:6: (location= ID '@' )? op= additive_math_operator right= multiplicativeExpression
            	    {
            	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:211:6: (location= ID '@' )?
            	    int alt23=2;
            	    int LA23_0 = input.LA(1);

            	    if ( (LA23_0==ID) ) {
            	        alt23=1;
            	    }
            	    switch (alt23) {
            	        case 1 :
            	            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:211:7: location= ID '@'
            	            {
            	            location=(Token)match(input,ID,FOLLOW_ID_in_additiveExpression918); 

            	            match(input,45,FOLLOW_45_in_additiveExpression920); 

            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_additive_math_operator_in_additiveExpression926);
            	    op=additive_math_operator();

            	    state._fsp--;


            	    pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression930);
            	    right=multiplicativeExpression();

            	    state._fsp--;


            	    locationStack.push(location == null ? Expression.LOCATION_INFER : location.getText()); opStack.push(op /* .math_operator */ ); rightStack.push((ValueExpression) right /* .expression */ );

            	    }
            	    break;

            	default :
            	    break loop24;
                }
            } while (true);



                while(rightStack.size() > 1) {
                    ValueExpression temp = rightStack.pop();
                    rightStack.push(new MathValueExpression(locationStack.pop(), rightStack.pop(), opStack.pop(), temp, HistoryReductionMode.DEFAULT_MODE));
                }
                if (rightStack.size() > 0) {
                    expression = new MathValueExpression(locationStack.pop(), (ValueExpression) left /* .expression */ , opStack.pop(), rightStack.pop(), HistoryReductionMode.DEFAULT_MODE);
                } else {
                    expression = left /* .expression */ ;
                }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return expression;
    }
    // $ANTLR end "additiveExpression"



    // $ANTLR start "comparativeExpression"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:227:1: comparativeExpression returns [Expression expression] : left= additiveExpression ( ( WS )* ( (location= ID )? c= comparator ) ( WS )* right= additiveExpression )? ;
    public final Expression comparativeExpression() throws RecognitionException {
        Expression expression = null;


        Token location=null;
        Expression left =null;

        Comparator c =null;

        Expression right =null;



            Stack<Expression> rightStack = new Stack<Expression>();
            Stack<Comparator> compareStack = new Stack<Comparator>();
            Stack<String> locationStack = new Stack<String>();

        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:233:5: (left= additiveExpression ( ( WS )* ( (location= ID )? c= comparator ) ( WS )* right= additiveExpression )? )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:233:7: left= additiveExpression ( ( WS )* ( (location= ID )? c= comparator ) ( WS )* right= additiveExpression )?
            {
            pushFollow(FOLLOW_additiveExpression_in_comparativeExpression978);
            left=additiveExpression();

            state._fsp--;


            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:234:5: ( ( WS )* ( (location= ID )? c= comparator ) ( WS )* right= additiveExpression )?
            int alt28=2;
            alt28 = dfa28.predict(input);
            switch (alt28) {
                case 1 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:234:6: ( WS )* ( (location= ID )? c= comparator ) ( WS )* right= additiveExpression
                    {
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:234:6: ( WS )*
                    loop25:
                    do {
                        int alt25=2;
                        int LA25_0 = input.LA(1);

                        if ( (LA25_0==WS) ) {
                            alt25=1;
                        }


                        switch (alt25) {
                    	case 1 :
                    	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:234:6: WS
                    	    {
                    	    match(input,WS,FOLLOW_WS_in_comparativeExpression985); 

                    	    }
                    	    break;

                    	default :
                    	    break loop25;
                        }
                    } while (true);


                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:234:10: ( (location= ID )? c= comparator )
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:234:11: (location= ID )? c= comparator
                    {
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:234:11: (location= ID )?
                    int alt26=2;
                    int LA26_0 = input.LA(1);

                    if ( (LA26_0==ID) ) {
                        alt26=1;
                    }
                    switch (alt26) {
                        case 1 :
                            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:234:12: location= ID
                            {
                            location=(Token)match(input,ID,FOLLOW_ID_in_comparativeExpression992); 

                            }
                            break;

                    }


                    pushFollow(FOLLOW_comparator_in_comparativeExpression998);
                    c=comparator();

                    state._fsp--;


                    }


                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:234:40: ( WS )*
                    loop27:
                    do {
                        int alt27=2;
                        int LA27_0 = input.LA(1);

                        if ( (LA27_0==WS) ) {
                            alt27=1;
                        }


                        switch (alt27) {
                    	case 1 :
                    	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:234:40: WS
                    	    {
                    	    match(input,WS,FOLLOW_WS_in_comparativeExpression1001); 

                    	    }
                    	    break;

                    	default :
                    	    break loop27;
                        }
                    } while (true);


                    pushFollow(FOLLOW_additiveExpression_in_comparativeExpression1006);
                    right=additiveExpression();

                    state._fsp--;


                    locationStack.push(location == null ? Expression.LOCATION_INFER : location.getText()); compareStack.push(c /* .comparator */ ); rightStack.push(right /* .expression */ );

                    }
                    break;

            }



                while(rightStack.size() > 1) {
                    Expression temp = rightStack.pop();
                    rightStack.push(new ComparisonExpression(locationStack.pop(), (ValueExpression) rightStack.pop(), compareStack.pop(), (ValueExpression) temp));
                }
                if (rightStack.size() > 0) {
                    expression = new ComparisonExpression(locationStack.pop(), (ValueExpression) left /* .expression */ , compareStack.pop(), (ValueExpression) rightStack.pop());
                } else {
                    expression = left /* .expression */ ;
                }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return expression;
    }
    // $ANTLR end "comparativeExpression"



    // $ANTLR start "unaryExpression"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:250:1: unaryExpression returns [Expression expression] : ( (location= ID )? NOT exp= comparativeExpression |exp= comparativeExpression );
    public final Expression unaryExpression() throws RecognitionException {
        Expression expression = null;


        Token location=null;
        Expression exp =null;


        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:251:5: ( (location= ID )? NOT exp= comparativeExpression |exp= comparativeExpression )
            int alt30=2;
            switch ( input.LA(1) ) {
            case ID:
                {
                int LA30_1 = input.LA(2);

                if ( (LA30_1==45) ) {
                    alt30=2;
                }
                else if ( (LA30_1==NOT) ) {
                    alt30=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 1, input);

                    throw nvae;

                }
                }
                break;
            case NOT:
                {
                alt30=1;
                }
                break;
            case FLOAT:
            case INT:
            case STRING:
            case 39:
                {
                alt30=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;

            }

            switch (alt30) {
                case 1 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:251:7: (location= ID )? NOT exp= comparativeExpression
                    {
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:251:7: (location= ID )?
                    int alt29=2;
                    int LA29_0 = input.LA(1);

                    if ( (LA29_0==ID) ) {
                        alt29=1;
                    }
                    switch (alt29) {
                        case 1 :
                            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:251:8: location= ID
                            {
                            location=(Token)match(input,ID,FOLLOW_ID_in_unaryExpression1050); 

                            }
                            break;

                    }


                    match(input,NOT,FOLLOW_NOT_in_unaryExpression1054); 

                    pushFollow(FOLLOW_comparativeExpression_in_unaryExpression1058);
                    exp=comparativeExpression();

                    state._fsp--;


                    expression = new LogicExpression(location == null ? Expression.LOCATION_INFER : location.getText(), UnaryLogicOperator.NOT /* .logic_operator */ , (TriStateExpression) exp /* .expression */ );

                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:253:7: exp= comparativeExpression
                    {
                    pushFollow(FOLLOW_comparativeExpression_in_unaryExpression1079);
                    exp=comparativeExpression();

                    state._fsp--;


                    expression = exp /* .expression */ ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return expression;
    }
    // $ANTLR end "unaryExpression"



    // $ANTLR start "andExpression"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:257:1: andExpression returns [Expression expression] : left= unaryExpression ( (location= ID '@' )? AND right= unaryExpression )* ;
    public final Expression andExpression() throws RecognitionException {
        Expression expression = null;


        Token location=null;
        Expression left =null;

        Expression right =null;



            Stack<Expression> rightStack = new Stack<Expression>();
            Stack<String> locationStack = new Stack<String>();

        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:262:5: (left= unaryExpression ( (location= ID '@' )? AND right= unaryExpression )* )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:262:7: left= unaryExpression ( (location= ID '@' )? AND right= unaryExpression )*
            {
            pushFollow(FOLLOW_unaryExpression_in_andExpression1118);
            left=unaryExpression();

            state._fsp--;


            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:263:5: ( (location= ID '@' )? AND right= unaryExpression )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( (LA32_0==ID) ) {
                    int LA32_1 = input.LA(2);

                    if ( (LA32_1==45) ) {
                        int LA32_4 = input.LA(3);

                        if ( (LA32_4==AND) ) {
                            alt32=1;
                        }


                    }


                }
                else if ( (LA32_0==AND) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:263:6: (location= ID '@' )? AND right= unaryExpression
            	    {
            	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:263:6: (location= ID '@' )?
            	    int alt31=2;
            	    int LA31_0 = input.LA(1);

            	    if ( (LA31_0==ID) ) {
            	        alt31=1;
            	    }
            	    switch (alt31) {
            	        case 1 :
            	            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:263:7: location= ID '@'
            	            {
            	            location=(Token)match(input,ID,FOLLOW_ID_in_andExpression1128); 

            	            match(input,45,FOLLOW_45_in_andExpression1130); 

            	            }
            	            break;

            	    }


            	    match(input,AND,FOLLOW_AND_in_andExpression1134); 

            	    pushFollow(FOLLOW_unaryExpression_in_andExpression1138);
            	    right=unaryExpression();

            	    state._fsp--;


            	    locationStack.push(location == null ? Expression.LOCATION_INFER : location.getText()); rightStack.push(right /* .expression */ );

            	    }
            	    break;

            	default :
            	    break loop32;
                }
            } while (true);



                while(rightStack.size() > 1) {
                    Expression temp = rightStack.pop();
                    rightStack.push(new LogicExpression(locationStack.pop(), (TriStateExpression) rightStack.pop(), BinaryLogicOperator.AND, (TriStateExpression) temp));
                }
                if (rightStack.size() > 0) {
                    expression = new LogicExpression(locationStack.pop(), (TriStateExpression) left /* .expression */ , BinaryLogicOperator.AND, (TriStateExpression) rightStack.pop());
                } else {
                    expression = left /* .expression */ ;
                }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return expression;
    }
    // $ANTLR end "andExpression"



    // $ANTLR start "orExpression"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:279:1: orExpression returns [Expression expression] : left= andExpression ( (location= ID '@' )? OR right= andExpression )* ;
    public final Expression orExpression() throws RecognitionException {
        Expression expression = null;


        Token location=null;
        Expression left =null;

        Expression right =null;



            Stack<Expression> rightStack = new Stack<Expression>();
            Stack<String> locationStack = new Stack<String>();

        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:284:5: (left= andExpression ( (location= ID '@' )? OR right= andExpression )* )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:284:7: left= andExpression ( (location= ID '@' )? OR right= andExpression )*
            {
            pushFollow(FOLLOW_andExpression_in_orExpression1186);
            left=andExpression();

            state._fsp--;


            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:285:5: ( (location= ID '@' )? OR right= andExpression )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0==ID||LA34_0==OR) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:285:6: (location= ID '@' )? OR right= andExpression
            	    {
            	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:285:6: (location= ID '@' )?
            	    int alt33=2;
            	    int LA33_0 = input.LA(1);

            	    if ( (LA33_0==ID) ) {
            	        alt33=1;
            	    }
            	    switch (alt33) {
            	        case 1 :
            	            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:285:7: location= ID '@'
            	            {
            	            location=(Token)match(input,ID,FOLLOW_ID_in_orExpression1196); 

            	            match(input,45,FOLLOW_45_in_orExpression1198); 

            	            }
            	            break;

            	    }


            	    match(input,OR,FOLLOW_OR_in_orExpression1202); 

            	    pushFollow(FOLLOW_andExpression_in_orExpression1206);
            	    right=andExpression();

            	    state._fsp--;


            	    locationStack.push(location == null ? Expression.LOCATION_INFER : location.getText()); rightStack.push(right /* .expression */ );

            	    }
            	    break;

            	default :
            	    break loop34;
                }
            } while (true);



                while(rightStack.size() > 1) {
                    Expression temp = rightStack.pop();
                    rightStack.push(new LogicExpression(locationStack.pop(), (TriStateExpression) rightStack.pop(), BinaryLogicOperator.OR, (TriStateExpression) temp));
                }
                if (rightStack.size() > 0) {
                    expression = new LogicExpression(locationStack.pop(), (TriStateExpression) left /* .expression */ , BinaryLogicOperator.OR, (TriStateExpression) rightStack.pop());
                } else {
                    expression = left /* .expression */ ;
                }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return expression;
    }
    // $ANTLR end "orExpression"



    // $ANTLR start "expression"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:301:1: expression returns [Expression expression] : logic= orExpression EOF ;
    public final Expression expression() throws RecognitionException {
        Expression expression = null;


        Expression logic =null;


        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:302:5: (logic= orExpression EOF )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:303:5: logic= orExpression EOF
            {
            pushFollow(FOLLOW_orExpression_in_expression1256);
            logic=orExpression();

            state._fsp--;


            match(input,EOF,FOLLOW_EOF_in_expression1258); 

            expression = logic /* .expression */ ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return expression;
    }
    // $ANTLR end "expression"



    // $ANTLR start "time_value"
    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:308:1: time_value returns [Long time] : val= INT (unit= TIME_UNIT )? (rep_val= INT (rep_unit= TIME_UNIT )? )* ;
    public final Long time_value() throws RecognitionException {
        Long time = null;


        Token val=null;
        Token unit=null;
        Token rep_val=null;
        Token rep_unit=null;

        try {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:309:5: (val= INT (unit= TIME_UNIT )? (rep_val= INT (rep_unit= TIME_UNIT )? )* )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:310:5: val= INT (unit= TIME_UNIT )? (rep_val= INT (rep_unit= TIME_UNIT )? )*
            {
            val=(Token)match(input,INT,FOLLOW_INT_in_time_value1296); 

            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:310:17: (unit= TIME_UNIT )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==TIME_UNIT) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:310:17: unit= TIME_UNIT
                    {
                    unit=(Token)match(input,TIME_UNIT,FOLLOW_TIME_UNIT_in_time_value1300); 

                    }
                    break;

            }


             long theTime = convertTime(val, unit); 

            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:312:5: (rep_val= INT (rep_unit= TIME_UNIT )? )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( (LA37_0==INT) ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:312:6: rep_val= INT (rep_unit= TIME_UNIT )?
            	    {
            	    rep_val=(Token)match(input,INT,FOLLOW_INT_in_time_value1317); 

            	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:312:26: (rep_unit= TIME_UNIT )?
            	    int alt36=2;
            	    int LA36_0 = input.LA(1);

            	    if ( (LA36_0==TIME_UNIT) ) {
            	        alt36=1;
            	    }
            	    switch (alt36) {
            	        case 1 :
            	            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:312:26: rep_unit= TIME_UNIT
            	            {
            	            rep_unit=(Token)match(input,TIME_UNIT,FOLLOW_TIME_UNIT_in_time_value1321); 

            	            }
            	            break;

            	    }


            	     theTime += convertTime(rep_val, rep_unit);

            	    }
            	    break;

            	default :
            	    break loop37;
                }
            } while (true);


            time = new Long(theTime);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return time;
    }
    // $ANTLR end "time_value"

    // Delegated rules


    protected DFA13 dfa13 = new DFA13(this);
    protected DFA22 dfa22 = new DFA22(this);
    protected DFA28 dfa28 = new DFA28(this);
    static final String DFA13_eotS =
        "\24\uffff";
    static final String DFA13_eofS =
        "\5\uffff\2\10\4\uffff\2\10\1\uffff\1\20\4\uffff\1\20";
    static final String DFA13_minS =
        "\1\23\1\55\1\23\1\53\1\23\2\5\1\23\1\uffff\1\23\1\uffff\2\5\1\11"+
        "\1\5\1\23\2\uffff\1\11\1\5";
    static final String DFA13_maxS =
        "\1\23\1\55\1\23\1\53\1\44\2\56\1\44\1\uffff\1\23\1\uffff\2\56\1"+
        "\11\1\56\1\23\2\uffff\1\11\1\56";
    static final String DFA13_acceptS =
        "\10\uffff\1\1\1\uffff\1\3\5\uffff\1\2\1\4\2\uffff";
    static final String DFA13_specialS =
        "\24\uffff}>";
    static final String[] DFA13_transitionS = {
            "\1\1",
            "\1\2",
            "\1\3",
            "\1\4",
            "\1\5\20\uffff\1\6",
            "\1\10\4\uffff\3\10\3\uffff\2\10\1\uffff\1\10\1\uffff\2\10\4"+
            "\uffff\3\10\2\uffff\4\10\2\uffff\1\10\1\uffff\1\10\1\uffff\1"+
            "\7\1\uffff\1\11\1\uffff\1\12",
            "\1\10\4\uffff\3\10\3\uffff\2\10\1\uffff\1\10\1\uffff\2\10\4"+
            "\uffff\3\10\2\uffff\4\10\2\uffff\1\10\1\uffff\1\10\1\uffff\1"+
            "\7\1\uffff\1\11\1\uffff\1\12",
            "\1\13\20\uffff\1\14",
            "",
            "\1\15",
            "",
            "\1\10\4\uffff\3\10\3\uffff\2\10\1\uffff\1\10\1\uffff\2\10\4"+
            "\uffff\3\10\2\uffff\4\10\2\uffff\1\10\1\uffff\1\10\1\uffff\1"+
            "\7\1\uffff\1\11\1\uffff\1\12",
            "\1\10\4\uffff\3\10\3\uffff\2\10\1\uffff\1\10\1\uffff\2\10\4"+
            "\uffff\3\10\2\uffff\4\10\2\uffff\1\10\1\uffff\1\10\1\uffff\1"+
            "\7\1\uffff\1\11\1\uffff\1\12",
            "\1\16",
            "\1\20\1\uffff\1\17\2\uffff\3\20\3\uffff\2\20\1\uffff\1\20\1"+
            "\uffff\2\20\4\uffff\3\20\2\uffff\4\20\2\uffff\1\20\1\uffff\1"+
            "\20\5\uffff\1\21",
            "\1\22",
            "",
            "",
            "\1\23",
            "\1\20\1\uffff\1\17\2\uffff\3\20\3\uffff\2\20\1\uffff\1\20\1"+
            "\uffff\2\20\4\uffff\3\20\2\uffff\4\20\2\uffff\1\20\1\uffff\1"+
            "\20\5\uffff\1\21"
    };

    static final short[] DFA13_eot = DFA.unpackEncodedString(DFA13_eotS);
    static final short[] DFA13_eof = DFA.unpackEncodedString(DFA13_eofS);
    static final char[] DFA13_min = DFA.unpackEncodedStringToUnsignedChars(DFA13_minS);
    static final char[] DFA13_max = DFA.unpackEncodedStringToUnsignedChars(DFA13_maxS);
    static final short[] DFA13_accept = DFA.unpackEncodedString(DFA13_acceptS);
    static final short[] DFA13_special = DFA.unpackEncodedString(DFA13_specialS);
    static final short[][] DFA13_transition;

    static {
        int numStates = DFA13_transitionS.length;
        DFA13_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA13_transition[i] = DFA.unpackEncodedString(DFA13_transitionS[i]);
        }
    }

    class DFA13 extends DFA {

        public DFA13(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 13;
            this.eot = DFA13_eot;
            this.eof = DFA13_eof;
            this.min = DFA13_min;
            this.max = DFA13_max;
            this.accept = DFA13_accept;
            this.special = DFA13_special;
            this.transition = DFA13_transition;
        }
        public String getDescription() {
            return "139:1: sensor_value_expression returns [SensorValueExpression value_expression] : (location= ID '@' entity= ID ':' path= value_path |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options |location= ID '@' entity= ID ':' path= value_path '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' );";
        }
    }
    static final String DFA22_eotS =
        "\7\uffff";
    static final String DFA22_eofS =
        "\1\2\6\uffff";
    static final String DFA22_minS =
        "\1\5\1\12\1\uffff\1\12\1\uffff\1\5\1\12";
    static final String DFA22_maxS =
        "\1\50\1\55\1\uffff\1\50\1\uffff\1\42\1\55";
    static final String DFA22_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\2\uffff";
    static final String DFA22_specialS =
        "\7\uffff}>";
    static final String[] DFA22_transitionS = {
            "\1\2\4\uffff\1\2\1\4\1\2\3\uffff\2\2\1\uffff\1\1\1\uffff\2\2"+
            "\4\uffff\1\2\2\4\2\uffff\4\2\2\uffff\1\3\1\uffff\1\2",
            "\1\2\1\uffff\1\2\3\uffff\2\2\3\uffff\2\2\11\uffff\1\2\2\uffff"+
            "\1\2\11\uffff\1\5",
            "",
            "\1\2\1\4\1\2\3\uffff\2\2\1\uffff\1\6\1\uffff\2\2\5\uffff\2"+
            "\4\2\uffff\1\2\2\uffff\1\2\2\uffff\1\3\1\uffff\1\2",
            "",
            "\1\2\5\uffff\1\4\17\uffff\1\2\2\4\3\uffff\2\2",
            "\1\2\1\uffff\1\2\3\uffff\2\2\3\uffff\2\2\11\uffff\1\2\2\uffff"+
            "\1\2\11\uffff\1\4"
    };

    static final short[] DFA22_eot = DFA.unpackEncodedString(DFA22_eotS);
    static final short[] DFA22_eof = DFA.unpackEncodedString(DFA22_eofS);
    static final char[] DFA22_min = DFA.unpackEncodedStringToUnsignedChars(DFA22_minS);
    static final char[] DFA22_max = DFA.unpackEncodedStringToUnsignedChars(DFA22_maxS);
    static final short[] DFA22_accept = DFA.unpackEncodedString(DFA22_acceptS);
    static final short[] DFA22_special = DFA.unpackEncodedString(DFA22_specialS);
    static final short[][] DFA22_transition;

    static {
        int numStates = DFA22_transitionS.length;
        DFA22_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA22_transition[i] = DFA.unpackEncodedString(DFA22_transitionS[i]);
        }
    }

    class DFA22 extends DFA {

        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = DFA22_eot;
            this.eof = DFA22_eof;
            this.min = DFA22_min;
            this.max = DFA22_max;
            this.accept = DFA22_accept;
            this.special = DFA22_special;
            this.transition = DFA22_transition;
        }
        public String getDescription() {
            return "()* loopback of 188:5: ( ( WS )* (location= ID '@' )? op= multiplicative_math_operator ( WS )* right= parentheticalExpression )*";
        }
    }
    static final String DFA28_eotS =
        "\5\uffff";
    static final String DFA28_eofS =
        "\1\4\4\uffff";
    static final String DFA28_minS =
        "\1\5\2\12\2\uffff";
    static final String DFA28_maxS =
        "\2\50\1\55\2\uffff";
    static final String DFA28_acceptS =
        "\3\uffff\1\1\1\2";
    static final String DFA28_specialS =
        "\5\uffff}>";
    static final String[] DFA28_transitionS = {
            "\1\4\4\uffff\1\3\1\uffff\1\3\3\uffff\2\3\1\uffff\1\2\1\uffff"+
            "\2\3\11\uffff\1\3\1\4\1\uffff\1\3\2\uffff\1\1\1\uffff\1\4",
            "\1\3\1\uffff\1\3\3\uffff\2\3\1\uffff\1\3\1\uffff\2\3\11\uffff"+
            "\1\3\2\uffff\1\3\2\uffff\1\1\1\uffff\1\4",
            "\1\3\1\uffff\1\3\3\uffff\2\3\3\uffff\2\3\11\uffff\1\3\2\uffff"+
            "\1\3\11\uffff\1\4",
            "",
            ""
    };

    static final short[] DFA28_eot = DFA.unpackEncodedString(DFA28_eotS);
    static final short[] DFA28_eof = DFA.unpackEncodedString(DFA28_eofS);
    static final char[] DFA28_min = DFA.unpackEncodedStringToUnsignedChars(DFA28_minS);
    static final char[] DFA28_max = DFA.unpackEncodedStringToUnsignedChars(DFA28_maxS);
    static final short[] DFA28_accept = DFA.unpackEncodedString(DFA28_acceptS);
    static final short[] DFA28_special = DFA.unpackEncodedString(DFA28_specialS);
    static final short[][] DFA28_transition;

    static {
        int numStates = DFA28_transitionS.length;
        DFA28_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA28_transition[i] = DFA.unpackEncodedString(DFA28_transitionS[i]);
        }
    }

    class DFA28 extends DFA {

        public DFA28(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 28;
            this.eot = DFA28_eot;
            this.eof = DFA28_eof;
            this.min = DFA28_min;
            this.max = DFA28_max;
            this.accept = DFA28_accept;
            this.special = DFA28_special;
            this.transition = DFA28_transition;
        }
        public String getDescription() {
            return "234:5: ( ( WS )* ( (location= ID )? c= comparator ) ( WS )* right= additiveExpression )?";
        }
    }
 

    public static final BitSet FOLLOW_ID_in_configuration_options62 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_CONFIG_VAL_in_configuration_options66 = new BitSet(new long[]{0x0000000000000082L});
    public static final BitSet FOLLOW_CONFIG_AND_in_configuration_options76 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_configuration_options80 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_CONFIG_VAL_in_configuration_options84 = new BitSet(new long[]{0x0000000000000082L});
    public static final BitSet FOLLOW_v_p_in_value_path120 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_42_in_value_path129 = new BitSet(new long[]{0x0000001000080000L});
    public static final BitSet FOLLOW_v_p_in_value_path133 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_ID_in_v_p165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_v_p174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_comparator191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_comparator198 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GTEQ_in_comparator205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LTEQ_in_comparator212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_comparator219 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOTEQUALS_in_comparator226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REGEX_in_comparator233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONTAINS_in_comparator240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_binary_logic_operator_in_logic_operator262 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unary_logic_operator_in_logic_operator275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_in_binary_logic_operator296 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_binary_logic_operator303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_unary_logic_operator320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_additive_math_operator_in_math_operator339 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiplicative_math_operator_in_math_operator348 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_additive_math_operator365 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_additive_math_operator372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MULT_in_multiplicative_math_operator390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIV_in_multiplicative_math_operator397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_in_multiplicative_math_operator404 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALL_in_history_mode421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MAX_in_history_mode428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MIN_in_history_mode435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MEAN_in_history_mode442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MEDIAN_in_history_mode449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_history_mode456 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_sensor_value_expression477 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_sensor_value_expression479 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_sensor_value_expression483 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_sensor_value_expression485 = new BitSet(new long[]{0x0000001000080000L});
    public static final BitSet FOLLOW_value_path_in_sensor_value_expression489 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_sensor_value_expression501 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_sensor_value_expression503 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_sensor_value_expression507 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_sensor_value_expression509 = new BitSet(new long[]{0x0000001000080000L});
    public static final BitSet FOLLOW_value_path_in_sensor_value_expression513 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_sensor_value_expression515 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_configuration_options_in_sensor_value_expression519 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_sensor_value_expression531 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_sensor_value_expression533 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_sensor_value_expression537 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_sensor_value_expression539 = new BitSet(new long[]{0x0000001000080000L});
    public static final BitSet FOLLOW_value_path_in_sensor_value_expression543 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_46_in_sensor_value_expression545 = new BitSet(new long[]{0x0000000007900050L});
    public static final BitSet FOLLOW_history_mode_in_sensor_value_expression551 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_sensor_value_expression553 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_time_value_in_sensor_value_expression557 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_history_mode_in_sensor_value_expression564 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_time_value_in_sensor_value_expression570 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_47_in_sensor_value_expression573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_sensor_value_expression585 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_sensor_value_expression587 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_ID_in_sensor_value_expression591 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_sensor_value_expression593 = new BitSet(new long[]{0x0000001000080000L});
    public static final BitSet FOLLOW_value_path_in_sensor_value_expression597 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_sensor_value_expression599 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_configuration_options_in_sensor_value_expression603 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_46_in_sensor_value_expression605 = new BitSet(new long[]{0x0000000007900050L});
    public static final BitSet FOLLOW_history_mode_in_sensor_value_expression611 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_41_in_sensor_value_expression613 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_time_value_in_sensor_value_expression617 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_history_mode_in_sensor_value_expression624 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_time_value_in_sensor_value_expression630 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_47_in_sensor_value_expression633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_constant_value_expression655 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_constant_value_expression668 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_constant_value_expression682 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constant_value_expression_in_value_expression705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sensor_value_expression_in_value_expression719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_value_expression_in_parentheticalExpression748 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_parentheticalExpression773 = new BitSet(new long[]{0x000000D080188000L});
    public static final BitSet FOLLOW_WS_in_parentheticalExpression775 = new BitSet(new long[]{0x000000D080188000L});
    public static final BitSet FOLLOW_orExpression_in_parentheticalExpression780 = new BitSet(new long[]{0x0000014000000000L});
    public static final BitSet FOLLOW_WS_in_parentheticalExpression782 = new BitSet(new long[]{0x0000014000000000L});
    public static final BitSet FOLLOW_40_in_parentheticalExpression785 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parentheticalExpression_in_multiplicativeExpression832 = new BitSet(new long[]{0x0000004030080802L});
    public static final BitSet FOLLOW_WS_in_multiplicativeExpression839 = new BitSet(new long[]{0x0000004030080800L});
    public static final BitSet FOLLOW_ID_in_multiplicativeExpression845 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_multiplicativeExpression847 = new BitSet(new long[]{0x0000000030000800L});
    public static final BitSet FOLLOW_multiplicative_math_operator_in_multiplicativeExpression853 = new BitSet(new long[]{0x000000D000188000L});
    public static final BitSet FOLLOW_WS_in_multiplicativeExpression855 = new BitSet(new long[]{0x000000D000188000L});
    public static final BitSet FOLLOW_parentheticalExpression_in_multiplicativeExpression860 = new BitSet(new long[]{0x0000004030080802L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression908 = new BitSet(new long[]{0x0000000408080002L});
    public static final BitSet FOLLOW_ID_in_additiveExpression918 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_additiveExpression920 = new BitSet(new long[]{0x0000000408000000L});
    public static final BitSet FOLLOW_additive_math_operator_in_additiveExpression926 = new BitSet(new long[]{0x0000009000188000L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression930 = new BitSet(new long[]{0x0000000408080002L});
    public static final BitSet FOLLOW_additiveExpression_in_comparativeExpression978 = new BitSet(new long[]{0x00000049006B1402L});
    public static final BitSet FOLLOW_WS_in_comparativeExpression985 = new BitSet(new long[]{0x00000049006B1400L});
    public static final BitSet FOLLOW_ID_in_comparativeExpression992 = new BitSet(new long[]{0x0000000900631400L});
    public static final BitSet FOLLOW_comparator_in_comparativeExpression998 = new BitSet(new long[]{0x000000D000188000L});
    public static final BitSet FOLLOW_WS_in_comparativeExpression1001 = new BitSet(new long[]{0x000000D000188000L});
    public static final BitSet FOLLOW_additiveExpression_in_comparativeExpression1006 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_unaryExpression1050 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_NOT_in_unaryExpression1054 = new BitSet(new long[]{0x0000009000188000L});
    public static final BitSet FOLLOW_comparativeExpression_in_unaryExpression1058 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comparativeExpression_in_unaryExpression1079 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unaryExpression_in_andExpression1118 = new BitSet(new long[]{0x0000000000080022L});
    public static final BitSet FOLLOW_ID_in_andExpression1128 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_andExpression1130 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_AND_in_andExpression1134 = new BitSet(new long[]{0x0000009080188000L});
    public static final BitSet FOLLOW_unaryExpression_in_andExpression1138 = new BitSet(new long[]{0x0000000000080022L});
    public static final BitSet FOLLOW_andExpression_in_orExpression1186 = new BitSet(new long[]{0x0000000200080002L});
    public static final BitSet FOLLOW_ID_in_orExpression1196 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_orExpression1198 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_OR_in_orExpression1202 = new BitSet(new long[]{0x0000009080188000L});
    public static final BitSet FOLLOW_andExpression_in_orExpression1206 = new BitSet(new long[]{0x0000000200080002L});
    public static final BitSet FOLLOW_orExpression_in_expression1256 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_expression1258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_time_value1296 = new BitSet(new long[]{0x0000002000100002L});
    public static final BitSet FOLLOW_TIME_UNIT_in_time_value1300 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_INT_in_time_value1317 = new BitSet(new long[]{0x0000002000100002L});
    public static final BitSet FOLLOW_TIME_UNIT_in_time_value1321 = new BitSet(new long[]{0x0000000000100002L});

}