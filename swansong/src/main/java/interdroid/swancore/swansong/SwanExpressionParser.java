// $ANTLR 3.5.2 SwanExpression.g 2016-08-23 14:51:21

package interdroid.swancore.swansong;

import android.os.Bundle;
import java.util.Stack;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class SwanExpressionParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALL", "AND", "ANY", "CONFIG_HASH", 
		"CONFIG_IS", "CONFIG_TILT", "CONFIG_VAL", "CONTAINS", "DIV", "EQUALS", 
		"ESC_SEQ", "EXPONENT", "FLOAT", "GT", "GTEQ", "HEX_DIGIT", "HTTP", "HTTPS", 
		"ID", "INT", "LT", "LTEQ", "MAX", "MEAN", "MEDIAN", "MIN", "MINUS", "MOD", 
		"MULT", "NONE", "NOT", "NOTEQUALS", "OR", "PLUS", "REGEX", "STRING", "TIME_UNIT", 
		"WS", "'$'", "'('", "')'", "','", "'.'", "':'", "'?'", "'@'", "'{'", "'}'"
	};
	public static final int EOF=-1;
	public static final int T__42=42;
	public static final int T__43=43;
	public static final int T__44=44;
	public static final int T__45=45;
	public static final int T__46=46;
	public static final int T__47=47;
	public static final int T__48=48;
	public static final int T__49=49;
	public static final int T__50=50;
	public static final int T__51=51;
	public static final int ALL=4;
	public static final int AND=5;
	public static final int ANY=6;
	public static final int CONFIG_HASH=7;
	public static final int CONFIG_IS=8;
	public static final int CONFIG_TILT=9;
	public static final int CONFIG_VAL=10;
	public static final int CONTAINS=11;
	public static final int DIV=12;
	public static final int EQUALS=13;
	public static final int ESC_SEQ=14;
	public static final int EXPONENT=15;
	public static final int FLOAT=16;
	public static final int GT=17;
	public static final int GTEQ=18;
	public static final int HEX_DIGIT=19;
	public static final int HTTP=20;
	public static final int HTTPS=21;
	public static final int ID=22;
	public static final int INT=23;
	public static final int LT=24;
	public static final int LTEQ=25;
	public static final int MAX=26;
	public static final int MEAN=27;
	public static final int MEDIAN=28;
	public static final int MIN=29;
	public static final int MINUS=30;
	public static final int MOD=31;
	public static final int MULT=32;
	public static final int NONE=33;
	public static final int NOT=34;
	public static final int NOTEQUALS=35;
	public static final int OR=36;
	public static final int PLUS=37;
	public static final int REGEX=38;
	public static final int STRING=39;
	public static final int TIME_UNIT=40;
	public static final int WS=41;

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

	@Override public String[] getTokenNames() { return SwanExpressionParser.tokenNames; }
	@Override public String getGrammarFileName() { return "SwanExpression.g"; }


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




	// $ANTLR start "http_configuration_options"
	// SwanExpression.g:53:1: http_configuration_options returns [Bundle http_configuration] : (hid= ID hval= CONFIG_VAL ) ( CONFIG_TILT more_hid= ID more_hval= CONFIG_VAL )* ;
	public final Bundle http_configuration_options() throws RecognitionException {
		Bundle http_configuration = null;


		Token hid=null;
		Token hval=null;
		Token more_hid=null;
		Token more_hval=null;


			Bundle http_config = new Bundle();

		try {
			// SwanExpression.g:57:2: ( (hid= ID hval= CONFIG_VAL ) ( CONFIG_TILT more_hid= ID more_hval= CONFIG_VAL )* )
			// SwanExpression.g:58:2: (hid= ID hval= CONFIG_VAL ) ( CONFIG_TILT more_hid= ID more_hval= CONFIG_VAL )*
			{
			// SwanExpression.g:58:2: (hid= ID hval= CONFIG_VAL )
			// SwanExpression.g:58:3: hid= ID hval= CONFIG_VAL
			{
			hid=(Token)match(input,ID,FOLLOW_ID_in_http_configuration_options62); 
			hval=(Token)match(input,CONFIG_VAL,FOLLOW_CONFIG_VAL_in_http_configuration_options66); 
			}

			http_config.putString(hid.getText(), hval.getText().substring(1));
			// SwanExpression.g:60:2: ( CONFIG_TILT more_hid= ID more_hval= CONFIG_VAL )*
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0==CONFIG_TILT) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// SwanExpression.g:60:3: CONFIG_TILT more_hid= ID more_hval= CONFIG_VAL
					{
					match(input,CONFIG_TILT,FOLLOW_CONFIG_TILT_in_http_configuration_options76); 
					more_hid=(Token)match(input,ID,FOLLOW_ID_in_http_configuration_options80); 
					more_hval=(Token)match(input,CONFIG_VAL,FOLLOW_CONFIG_VAL_in_http_configuration_options84); 
					System.out.println(more_hval.getText().substring(1));
							http_config.putString(more_hid.getText(), more_hval.getText().substring(1));
					}
					break;

				default :
					break loop1;
				}
			}

			http_configuration = http_config;
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return http_configuration;
	}
	// $ANTLR end "http_configuration_options"



	// $ANTLR start "configuration_options"
	// SwanExpression.g:68:1: configuration_options returns [Bundle configuration] : (id= ID val= CONFIG_VAL ) ( CONFIG_HASH more_id= ID more_val= CONFIG_VAL )* ;
	public final Bundle configuration_options() throws RecognitionException {
		Bundle configuration = null;


		Token id=null;
		Token val=null;
		Token more_id=null;
		Token more_val=null;


			Bundle config = new Bundle();

		try {
			// SwanExpression.g:72:2: ( (id= ID val= CONFIG_VAL ) ( CONFIG_HASH more_id= ID more_val= CONFIG_VAL )* )
			// SwanExpression.g:73:2: (id= ID val= CONFIG_VAL ) ( CONFIG_HASH more_id= ID more_val= CONFIG_VAL )*
			{
			// SwanExpression.g:73:2: (id= ID val= CONFIG_VAL )
			// SwanExpression.g:73:3: id= ID val= CONFIG_VAL
			{
			id=(Token)match(input,ID,FOLLOW_ID_in_configuration_options123); 
			val=(Token)match(input,CONFIG_VAL,FOLLOW_CONFIG_VAL_in_configuration_options127); 
			}

			config.putString(id.getText(), val.getText().substring(1));
			// SwanExpression.g:75:2: ( CONFIG_HASH more_id= ID more_val= CONFIG_VAL )*
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( (LA2_0==CONFIG_HASH) ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// SwanExpression.g:75:3: CONFIG_HASH more_id= ID more_val= CONFIG_VAL
					{
					match(input,CONFIG_HASH,FOLLOW_CONFIG_HASH_in_configuration_options137); 
					more_id=(Token)match(input,ID,FOLLOW_ID_in_configuration_options141); 
					more_val=(Token)match(input,CONFIG_VAL,FOLLOW_CONFIG_VAL_in_configuration_options145); 
					config.putString(more_id.getText(), more_val.getText().substring(1));
					}
					break;

				default :
					break loop2;
				}
			}

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
	// SwanExpression.g:81:1: value_path returns [String value_path] : vp= v_p ( '.' more_id= v_p )* ;
	public final String value_path() throws RecognitionException {
		String value_path = null;


		String vp =null;
		String more_id =null;


			StringBuffer buf = new StringBuffer();

		try {
			// SwanExpression.g:85:2: (vp= v_p ( '.' more_id= v_p )* )
			// SwanExpression.g:86:2: vp= v_p ( '.' more_id= v_p )*
			{
			pushFollow(FOLLOW_v_p_in_value_path181);
			vp=v_p();
			state._fsp--;

			buf.append(vp);
			// SwanExpression.g:88:2: ( '.' more_id= v_p )*
			loop3:
			while (true) {
				int alt3=2;
				int LA3_0 = input.LA(1);
				if ( (LA3_0==46) ) {
					alt3=1;
				}

				switch (alt3) {
				case 1 :
					// SwanExpression.g:88:3: '.' more_id= v_p
					{
					match(input,46,FOLLOW_46_in_value_path190); 
					pushFollow(FOLLOW_v_p_in_value_path194);
					more_id=v_p();
					state._fsp--;

					buf.append('.'); buf.append(vp);
					}
					break;

				default :
					break loop3;
				}
			}

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
	// SwanExpression.g:94:1: v_p returns [String vp] : (id= ID |str= STRING );
	public final String v_p() throws RecognitionException {
		String vp = null;


		Token id=null;
		Token str=null;

		try {
			// SwanExpression.g:95:2: (id= ID |str= STRING )
			int alt4=2;
			int LA4_0 = input.LA(1);
			if ( (LA4_0==ID) ) {
				alt4=1;
			}
			else if ( (LA4_0==STRING) ) {
				alt4=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 4, 0, input);
				throw nvae;
			}

			switch (alt4) {
				case 1 :
					// SwanExpression.g:95:4: id= ID
					{
					id=(Token)match(input,ID,FOLLOW_ID_in_v_p226); 
					 vp = id.getText(); 
					}
					break;
				case 2 :
					// SwanExpression.g:96:4: str= STRING
					{
					str=(Token)match(input,STRING,FOLLOW_STRING_in_v_p235); 
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
	// SwanExpression.g:99:1: comparator returns [Comparator comparator] : ( GT | LT | GTEQ | LTEQ | EQUALS | NOTEQUALS | REGEX | CONTAINS );
	public final Comparator comparator() throws RecognitionException {
		Comparator comparator = null;


		try {
			// SwanExpression.g:100:2: ( GT | LT | GTEQ | LTEQ | EQUALS | NOTEQUALS | REGEX | CONTAINS )
			int alt5=8;
			switch ( input.LA(1) ) {
			case GT:
				{
				alt5=1;
				}
				break;
			case LT:
				{
				alt5=2;
				}
				break;
			case GTEQ:
				{
				alt5=3;
				}
				break;
			case LTEQ:
				{
				alt5=4;
				}
				break;
			case EQUALS:
				{
				alt5=5;
				}
				break;
			case NOTEQUALS:
				{
				alt5=6;
				}
				break;
			case REGEX:
				{
				alt5=7;
				}
				break;
			case CONTAINS:
				{
				alt5=8;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 5, 0, input);
				throw nvae;
			}
			switch (alt5) {
				case 1 :
					// SwanExpression.g:100:4: GT
					{
					match(input,GT,FOLLOW_GT_in_comparator252); 
					comparator = Comparator.GREATER_THAN;
					}
					break;
				case 2 :
					// SwanExpression.g:101:4: LT
					{
					match(input,LT,FOLLOW_LT_in_comparator259); 
					comparator = Comparator.LESS_THAN;
					}
					break;
				case 3 :
					// SwanExpression.g:102:4: GTEQ
					{
					match(input,GTEQ,FOLLOW_GTEQ_in_comparator266); 
					comparator = Comparator.GREATER_THAN_OR_EQUALS;
					}
					break;
				case 4 :
					// SwanExpression.g:103:4: LTEQ
					{
					match(input,LTEQ,FOLLOW_LTEQ_in_comparator273); 
					comparator = Comparator.LESS_THAN_OR_EQUALS;
					}
					break;
				case 5 :
					// SwanExpression.g:104:4: EQUALS
					{
					match(input,EQUALS,FOLLOW_EQUALS_in_comparator280); 
					comparator = Comparator.EQUALS;
					}
					break;
				case 6 :
					// SwanExpression.g:105:4: NOTEQUALS
					{
					match(input,NOTEQUALS,FOLLOW_NOTEQUALS_in_comparator287); 
					comparator = Comparator.NOT_EQUALS;
					}
					break;
				case 7 :
					// SwanExpression.g:106:4: REGEX
					{
					match(input,REGEX,FOLLOW_REGEX_in_comparator294); 
					comparator = Comparator.REGEX_MATCH;
					}
					break;
				case 8 :
					// SwanExpression.g:107:4: CONTAINS
					{
					match(input,CONTAINS,FOLLOW_CONTAINS_in_comparator301); 
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
	// SwanExpression.g:110:1: logic_operator returns [LogicOperator logic_operator] : (binary= binary_logic_operator |unary= unary_logic_operator );
	public final LogicOperator logic_operator() throws RecognitionException {
		LogicOperator logic_operator = null;


		BinaryLogicOperator binary =null;
		UnaryLogicOperator unary =null;

		try {
			// SwanExpression.g:111:2: (binary= binary_logic_operator |unary= unary_logic_operator )
			int alt6=2;
			int LA6_0 = input.LA(1);
			if ( (LA6_0==AND||LA6_0==OR) ) {
				alt6=1;
			}
			else if ( (LA6_0==NOT) ) {
				alt6=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 6, 0, input);
				throw nvae;
			}

			switch (alt6) {
				case 1 :
					// SwanExpression.g:112:3: binary= binary_logic_operator
					{
					pushFollow(FOLLOW_binary_logic_operator_in_logic_operator323);
					binary=binary_logic_operator();
					state._fsp--;

					logic_operator = binary /* .logic_operator */ ;
					}
					break;
				case 2 :
					// SwanExpression.g:114:4: unary= unary_logic_operator
					{
					pushFollow(FOLLOW_unary_logic_operator_in_logic_operator336);
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
	// SwanExpression.g:118:1: binary_logic_operator returns [BinaryLogicOperator logic_operator] : ( AND | OR );
	public final BinaryLogicOperator binary_logic_operator() throws RecognitionException {
		BinaryLogicOperator logic_operator = null;


		try {
			// SwanExpression.g:119:2: ( AND | OR )
			int alt7=2;
			int LA7_0 = input.LA(1);
			if ( (LA7_0==AND) ) {
				alt7=1;
			}
			else if ( (LA7_0==OR) ) {
				alt7=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 7, 0, input);
				throw nvae;
			}

			switch (alt7) {
				case 1 :
					// SwanExpression.g:119:4: AND
					{
					match(input,AND,FOLLOW_AND_in_binary_logic_operator357); 
					logic_operator = BinaryLogicOperator.AND;
					}
					break;
				case 2 :
					// SwanExpression.g:120:4: OR
					{
					match(input,OR,FOLLOW_OR_in_binary_logic_operator364); 
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
	// SwanExpression.g:123:1: unary_logic_operator returns [UnaryLogicOperator logic_operator] : NOT ;
	public final UnaryLogicOperator unary_logic_operator() throws RecognitionException {
		UnaryLogicOperator logic_operator = null;


		try {
			// SwanExpression.g:124:2: ( NOT )
			// SwanExpression.g:124:4: NOT
			{
			match(input,NOT,FOLLOW_NOT_in_unary_logic_operator381); 
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
	// SwanExpression.g:127:1: math_operator returns [MathOperator math_operator] : (add= additive_math_operator |mult= multiplicative_math_operator );
	public final MathOperator math_operator() throws RecognitionException {
		MathOperator math_operator = null;


		MathOperator add =null;
		MathOperator mult =null;

		try {
			// SwanExpression.g:128:2: (add= additive_math_operator |mult= multiplicative_math_operator )
			int alt8=2;
			int LA8_0 = input.LA(1);
			if ( (LA8_0==MINUS||LA8_0==PLUS) ) {
				alt8=1;
			}
			else if ( (LA8_0==DIV||(LA8_0 >= MOD && LA8_0 <= MULT)) ) {
				alt8=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 8, 0, input);
				throw nvae;
			}

			switch (alt8) {
				case 1 :
					// SwanExpression.g:128:4: add= additive_math_operator
					{
					pushFollow(FOLLOW_additive_math_operator_in_math_operator400);
					add=additive_math_operator();
					state._fsp--;

					math_operator =add /* .math_operator */ ;
					}
					break;
				case 2 :
					// SwanExpression.g:129:4: mult= multiplicative_math_operator
					{
					pushFollow(FOLLOW_multiplicative_math_operator_in_math_operator409);
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
	// SwanExpression.g:132:1: additive_math_operator returns [MathOperator math_operator] : ( PLUS | MINUS );
	public final MathOperator additive_math_operator() throws RecognitionException {
		MathOperator math_operator = null;


		try {
			// SwanExpression.g:133:2: ( PLUS | MINUS )
			int alt9=2;
			int LA9_0 = input.LA(1);
			if ( (LA9_0==PLUS) ) {
				alt9=1;
			}
			else if ( (LA9_0==MINUS) ) {
				alt9=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 9, 0, input);
				throw nvae;
			}

			switch (alt9) {
				case 1 :
					// SwanExpression.g:133:4: PLUS
					{
					match(input,PLUS,FOLLOW_PLUS_in_additive_math_operator426); 
					math_operator = MathOperator.PLUS;
					}
					break;
				case 2 :
					// SwanExpression.g:134:4: MINUS
					{
					match(input,MINUS,FOLLOW_MINUS_in_additive_math_operator433); 
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
	// SwanExpression.g:137:1: multiplicative_math_operator returns [MathOperator math_operator] : ( MULT | DIV | MOD );
	public final MathOperator multiplicative_math_operator() throws RecognitionException {
		MathOperator math_operator = null;


		try {
			// SwanExpression.g:138:2: ( MULT | DIV | MOD )
			int alt10=3;
			switch ( input.LA(1) ) {
			case MULT:
				{
				alt10=1;
				}
				break;
			case DIV:
				{
				alt10=2;
				}
				break;
			case MOD:
				{
				alt10=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 10, 0, input);
				throw nvae;
			}
			switch (alt10) {
				case 1 :
					// SwanExpression.g:138:4: MULT
					{
					match(input,MULT,FOLLOW_MULT_in_multiplicative_math_operator451); 
					math_operator = MathOperator.TIMES;
					}
					break;
				case 2 :
					// SwanExpression.g:139:4: DIV
					{
					match(input,DIV,FOLLOW_DIV_in_multiplicative_math_operator458); 
					math_operator = MathOperator.DIVIDE;
					}
					break;
				case 3 :
					// SwanExpression.g:140:4: MOD
					{
					match(input,MOD,FOLLOW_MOD_in_multiplicative_math_operator465); 
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
	// SwanExpression.g:143:1: history_mode returns [HistoryReductionMode history_mode] : ( ALL | MAX | MIN | MEAN | MEDIAN | ANY );
	public final HistoryReductionMode history_mode() throws RecognitionException {
		HistoryReductionMode history_mode = null;


		try {
			// SwanExpression.g:144:2: ( ALL | MAX | MIN | MEAN | MEDIAN | ANY )
			int alt11=6;
			switch ( input.LA(1) ) {
			case ALL:
				{
				alt11=1;
				}
				break;
			case MAX:
				{
				alt11=2;
				}
				break;
			case MIN:
				{
				alt11=3;
				}
				break;
			case MEAN:
				{
				alt11=4;
				}
				break;
			case MEDIAN:
				{
				alt11=5;
				}
				break;
			case ANY:
				{
				alt11=6;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 11, 0, input);
				throw nvae;
			}
			switch (alt11) {
				case 1 :
					// SwanExpression.g:144:4: ALL
					{
					match(input,ALL,FOLLOW_ALL_in_history_mode482); 
					history_mode = HistoryReductionMode.ALL;
					}
					break;
				case 2 :
					// SwanExpression.g:145:4: MAX
					{
					match(input,MAX,FOLLOW_MAX_in_history_mode489); 
					history_mode = HistoryReductionMode.MAX;
					}
					break;
				case 3 :
					// SwanExpression.g:146:4: MIN
					{
					match(input,MIN,FOLLOW_MIN_in_history_mode496); 
					history_mode = HistoryReductionMode.MIN;
					}
					break;
				case 4 :
					// SwanExpression.g:147:4: MEAN
					{
					match(input,MEAN,FOLLOW_MEAN_in_history_mode503); 
					history_mode = HistoryReductionMode.MEAN;
					}
					break;
				case 5 :
					// SwanExpression.g:148:4: MEDIAN
					{
					match(input,MEDIAN,FOLLOW_MEDIAN_in_history_mode510); 
					history_mode = HistoryReductionMode.MEDIAN;
					}
					break;
				case 6 :
					// SwanExpression.g:149:4: ANY
					{
					match(input,ANY,FOLLOW_ANY_in_history_mode517); 
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
	// SwanExpression.g:154:1: sensor_value_expression returns [SensorValueExpression value_expression] : (location= ID '@' entity= ID ':' path= value_path |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options |location= ID '@' entity= ID ':' path= value_path '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' |location= ID '@' entity= ID ':' path= value_path '$' http_config= http_configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options '$' http_config= http_configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' );
	public final SensorValueExpression sensor_value_expression() throws RecognitionException {
		SensorValueExpression value_expression = null;


		Token location=null;
		Token entity=null;
		String path =null;
		Bundle config =null;
		HistoryReductionMode mode =null;
		Long time =null;
		Bundle http_config =null;

		try {
			// SwanExpression.g:155:2: (location= ID '@' entity= ID ':' path= value_path |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options |location= ID '@' entity= ID ':' path= value_path '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' |location= ID '@' entity= ID ':' path= value_path '$' http_config= http_configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options '$' http_config= http_configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' )
			int alt16=6;
			alt16 = dfa16.predict(input);
			switch (alt16) {
				case 1 :
					// SwanExpression.g:155:4: location= ID '@' entity= ID ':' path= value_path
					{
					location=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression538); 
					match(input,49,FOLLOW_49_in_sensor_value_expression540); 
					entity=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression544); 
					match(input,47,FOLLOW_47_in_sensor_value_expression546); 
					pushFollow(FOLLOW_value_path_in_sensor_value_expression550);
					path=value_path();
					state._fsp--;

					value_expression = new SensorValueExpression(location.getText(), entity.getText(), path /*.value_path */, null, HistoryReductionMode.ANY, 0,null);
					}
					break;
				case 2 :
					// SwanExpression.g:157:4: location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options
					{
					location=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression562); 
					match(input,49,FOLLOW_49_in_sensor_value_expression564); 
					entity=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression568); 
					match(input,47,FOLLOW_47_in_sensor_value_expression570); 
					pushFollow(FOLLOW_value_path_in_sensor_value_expression574);
					path=value_path();
					state._fsp--;

					match(input,48,FOLLOW_48_in_sensor_value_expression576); 
					pushFollow(FOLLOW_configuration_options_in_sensor_value_expression580);
					config=configuration_options();
					state._fsp--;

					value_expression = new SensorValueExpression(location.getText(), entity.getText(), path /*.value_path */, config /*.configuration */, HistoryReductionMode.ANY, 0,null);
					}
					break;
				case 3 :
					// SwanExpression.g:159:4: location= ID '@' entity= ID ':' path= value_path '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}'
					{
					location=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression592); 
					match(input,49,FOLLOW_49_in_sensor_value_expression594); 
					entity=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression598); 
					match(input,47,FOLLOW_47_in_sensor_value_expression600); 
					pushFollow(FOLLOW_value_path_in_sensor_value_expression604);
					path=value_path();
					state._fsp--;

					match(input,50,FOLLOW_50_in_sensor_value_expression606); 
					// SwanExpression.g:159:54: ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value )
					int alt12=3;
					switch ( input.LA(1) ) {
					case ALL:
						{
						int LA12_1 = input.LA(2);
						if ( (LA12_1==45) ) {
							alt12=1;
						}
						else if ( (LA12_1==51) ) {
							alt12=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 12, 1, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case MAX:
						{
						int LA12_2 = input.LA(2);
						if ( (LA12_2==45) ) {
							alt12=1;
						}
						else if ( (LA12_2==51) ) {
							alt12=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 12, 2, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case MIN:
						{
						int LA12_3 = input.LA(2);
						if ( (LA12_3==45) ) {
							alt12=1;
						}
						else if ( (LA12_3==51) ) {
							alt12=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 12, 3, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case MEAN:
						{
						int LA12_4 = input.LA(2);
						if ( (LA12_4==45) ) {
							alt12=1;
						}
						else if ( (LA12_4==51) ) {
							alt12=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 12, 4, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case MEDIAN:
						{
						int LA12_5 = input.LA(2);
						if ( (LA12_5==45) ) {
							alt12=1;
						}
						else if ( (LA12_5==51) ) {
							alt12=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 12, 5, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case ANY:
						{
						int LA12_6 = input.LA(2);
						if ( (LA12_6==45) ) {
							alt12=1;
						}
						else if ( (LA12_6==51) ) {
							alt12=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 12, 6, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
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
							// SwanExpression.g:159:55: (mode= history_mode ',' time= time_value )
							{
							// SwanExpression.g:159:55: (mode= history_mode ',' time= time_value )
							// SwanExpression.g:159:56: mode= history_mode ',' time= time_value
							{
							pushFollow(FOLLOW_history_mode_in_sensor_value_expression612);
							mode=history_mode();
							state._fsp--;

							match(input,45,FOLLOW_45_in_sensor_value_expression614); 
							pushFollow(FOLLOW_time_value_in_sensor_value_expression618);
							time=time_value();
							state._fsp--;

							}

							}
							break;
						case 2 :
							// SwanExpression.g:159:97: mode= history_mode
							{
							pushFollow(FOLLOW_history_mode_in_sensor_value_expression625);
							mode=history_mode();
							state._fsp--;

							}
							break;
						case 3 :
							// SwanExpression.g:159:117: time= time_value
							{
							pushFollow(FOLLOW_time_value_in_sensor_value_expression631);
							time=time_value();
							state._fsp--;

							}
							break;

					}

					match(input,51,FOLLOW_51_in_sensor_value_expression634); 
					if (time == null) {
									value_expression = new SensorValueExpression(location.getText(), entity.getText(), path /* .value_path */, null, mode /*.history_mode */, 0,null);
								} else {
									value_expression = new SensorValueExpression(location.getText(), entity.getText(), path /* .value_path */, null, mode /*.history_mode */, time,null);
								}
					}
					break;
				case 4 :
					// SwanExpression.g:165:4: location= ID '@' entity= ID ':' path= value_path '$' http_config= http_configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}'
					{
					location=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression647); 
					match(input,49,FOLLOW_49_in_sensor_value_expression649); 
					entity=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression653); 
					match(input,47,FOLLOW_47_in_sensor_value_expression655); 
					pushFollow(FOLLOW_value_path_in_sensor_value_expression659);
					path=value_path();
					state._fsp--;

					match(input,42,FOLLOW_42_in_sensor_value_expression661); 
					pushFollow(FOLLOW_http_configuration_options_in_sensor_value_expression665);
					http_config=http_configuration_options();
					state._fsp--;

					match(input,50,FOLLOW_50_in_sensor_value_expression667); 
					// SwanExpression.g:165:97: ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value )
					int alt13=3;
					switch ( input.LA(1) ) {
					case ALL:
						{
						int LA13_1 = input.LA(2);
						if ( (LA13_1==45) ) {
							alt13=1;
						}
						else if ( (LA13_1==51) ) {
							alt13=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 13, 1, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case MAX:
						{
						int LA13_2 = input.LA(2);
						if ( (LA13_2==45) ) {
							alt13=1;
						}
						else if ( (LA13_2==51) ) {
							alt13=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 13, 2, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case MIN:
						{
						int LA13_3 = input.LA(2);
						if ( (LA13_3==45) ) {
							alt13=1;
						}
						else if ( (LA13_3==51) ) {
							alt13=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 13, 3, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case MEAN:
						{
						int LA13_4 = input.LA(2);
						if ( (LA13_4==45) ) {
							alt13=1;
						}
						else if ( (LA13_4==51) ) {
							alt13=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 13, 4, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case MEDIAN:
						{
						int LA13_5 = input.LA(2);
						if ( (LA13_5==45) ) {
							alt13=1;
						}
						else if ( (LA13_5==51) ) {
							alt13=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 13, 5, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case ANY:
						{
						int LA13_6 = input.LA(2);
						if ( (LA13_6==45) ) {
							alt13=1;
						}
						else if ( (LA13_6==51) ) {
							alt13=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 13, 6, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case INT:
						{
						alt13=3;
						}
						break;
					default:
						NoViableAltException nvae =
							new NoViableAltException("", 13, 0, input);
						throw nvae;
					}
					switch (alt13) {
						case 1 :
							// SwanExpression.g:165:98: (mode= history_mode ',' time= time_value )
							{
							// SwanExpression.g:165:98: (mode= history_mode ',' time= time_value )
							// SwanExpression.g:165:99: mode= history_mode ',' time= time_value
							{
							pushFollow(FOLLOW_history_mode_in_sensor_value_expression673);
							mode=history_mode();
							state._fsp--;

							match(input,45,FOLLOW_45_in_sensor_value_expression675); 
							pushFollow(FOLLOW_time_value_in_sensor_value_expression679);
							time=time_value();
							state._fsp--;

							}

							}
							break;
						case 2 :
							// SwanExpression.g:165:140: mode= history_mode
							{
							pushFollow(FOLLOW_history_mode_in_sensor_value_expression686);
							mode=history_mode();
							state._fsp--;

							}
							break;
						case 3 :
							// SwanExpression.g:165:160: time= time_value
							{
							pushFollow(FOLLOW_time_value_in_sensor_value_expression692);
							time=time_value();
							state._fsp--;

							}
							break;

					}

					match(input,51,FOLLOW_51_in_sensor_value_expression695); 
					if (time == null) {
									value_expression = new SensorValueExpression(location.getText(), entity.getText(), path /* .value_path */, null, mode /*.history_mode */, 0,http_config);
								} else {
									value_expression = new SensorValueExpression(location.getText(), entity.getText(), path /* .value_path */, null, mode /*.history_mode */, time,http_config);
								}
					}
					break;
				case 5 :
					// SwanExpression.g:171:4: location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}'
					{
					location=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression707); 
					match(input,49,FOLLOW_49_in_sensor_value_expression709); 
					entity=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression713); 
					match(input,47,FOLLOW_47_in_sensor_value_expression715); 
					pushFollow(FOLLOW_value_path_in_sensor_value_expression719);
					path=value_path();
					state._fsp--;

					match(input,48,FOLLOW_48_in_sensor_value_expression721); 
					pushFollow(FOLLOW_configuration_options_in_sensor_value_expression725);
					config=configuration_options();
					state._fsp--;

					match(input,50,FOLLOW_50_in_sensor_value_expression727); 
					// SwanExpression.g:171:87: ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value )
					int alt14=3;
					switch ( input.LA(1) ) {
					case ALL:
						{
						int LA14_1 = input.LA(2);
						if ( (LA14_1==45) ) {
							alt14=1;
						}
						else if ( (LA14_1==51) ) {
							alt14=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 14, 1, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case MAX:
						{
						int LA14_2 = input.LA(2);
						if ( (LA14_2==45) ) {
							alt14=1;
						}
						else if ( (LA14_2==51) ) {
							alt14=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 14, 2, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case MIN:
						{
						int LA14_3 = input.LA(2);
						if ( (LA14_3==45) ) {
							alt14=1;
						}
						else if ( (LA14_3==51) ) {
							alt14=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 14, 3, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case MEAN:
						{
						int LA14_4 = input.LA(2);
						if ( (LA14_4==45) ) {
							alt14=1;
						}
						else if ( (LA14_4==51) ) {
							alt14=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 14, 4, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case MEDIAN:
						{
						int LA14_5 = input.LA(2);
						if ( (LA14_5==45) ) {
							alt14=1;
						}
						else if ( (LA14_5==51) ) {
							alt14=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 14, 5, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case ANY:
						{
						int LA14_6 = input.LA(2);
						if ( (LA14_6==45) ) {
							alt14=1;
						}
						else if ( (LA14_6==51) ) {
							alt14=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 14, 6, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case INT:
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
							// SwanExpression.g:171:88: (mode= history_mode ',' time= time_value )
							{
							// SwanExpression.g:171:88: (mode= history_mode ',' time= time_value )
							// SwanExpression.g:171:89: mode= history_mode ',' time= time_value
							{
							pushFollow(FOLLOW_history_mode_in_sensor_value_expression733);
							mode=history_mode();
							state._fsp--;

							match(input,45,FOLLOW_45_in_sensor_value_expression735); 
							pushFollow(FOLLOW_time_value_in_sensor_value_expression739);
							time=time_value();
							state._fsp--;

							}

							}
							break;
						case 2 :
							// SwanExpression.g:171:130: mode= history_mode
							{
							pushFollow(FOLLOW_history_mode_in_sensor_value_expression746);
							mode=history_mode();
							state._fsp--;

							}
							break;
						case 3 :
							// SwanExpression.g:171:150: time= time_value
							{
							pushFollow(FOLLOW_time_value_in_sensor_value_expression752);
							time=time_value();
							state._fsp--;

							}
							break;

					}

					match(input,51,FOLLOW_51_in_sensor_value_expression755); 
					if (time == null) {
									value_expression = new SensorValueExpression(location.getText(), entity.getText(), path /* .value_path */ , config /*.configuration */ , mode /* .history_mode */, 0,null);
								} else {
									value_expression = new SensorValueExpression(location.getText(), entity.getText(), path /* .value_path */ , config /*.configuration */ , mode /* .history_mode */ , time,null);
								}
					}
					break;
				case 6 :
					// SwanExpression.g:177:4: location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options '$' http_config= http_configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}'
					{
					location=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression767); 
					match(input,49,FOLLOW_49_in_sensor_value_expression769); 
					entity=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression773); 
					match(input,47,FOLLOW_47_in_sensor_value_expression775); 
					pushFollow(FOLLOW_value_path_in_sensor_value_expression779);
					path=value_path();
					state._fsp--;

					match(input,48,FOLLOW_48_in_sensor_value_expression781); 
					pushFollow(FOLLOW_configuration_options_in_sensor_value_expression785);
					config=configuration_options();
					state._fsp--;

					match(input,42,FOLLOW_42_in_sensor_value_expression787); 
					pushFollow(FOLLOW_http_configuration_options_in_sensor_value_expression791);
					http_config=http_configuration_options();
					state._fsp--;

					match(input,50,FOLLOW_50_in_sensor_value_expression793); 
					// SwanExpression.g:177:130: ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value )
					int alt15=3;
					switch ( input.LA(1) ) {
					case ALL:
						{
						int LA15_1 = input.LA(2);
						if ( (LA15_1==45) ) {
							alt15=1;
						}
						else if ( (LA15_1==51) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 15, 1, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case MAX:
						{
						int LA15_2 = input.LA(2);
						if ( (LA15_2==45) ) {
							alt15=1;
						}
						else if ( (LA15_2==51) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 15, 2, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case MIN:
						{
						int LA15_3 = input.LA(2);
						if ( (LA15_3==45) ) {
							alt15=1;
						}
						else if ( (LA15_3==51) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 15, 3, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case MEAN:
						{
						int LA15_4 = input.LA(2);
						if ( (LA15_4==45) ) {
							alt15=1;
						}
						else if ( (LA15_4==51) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 15, 4, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case MEDIAN:
						{
						int LA15_5 = input.LA(2);
						if ( (LA15_5==45) ) {
							alt15=1;
						}
						else if ( (LA15_5==51) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 15, 5, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case ANY:
						{
						int LA15_6 = input.LA(2);
						if ( (LA15_6==45) ) {
							alt15=1;
						}
						else if ( (LA15_6==51) ) {
							alt15=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 15, 6, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case INT:
						{
						alt15=3;
						}
						break;
					default:
						NoViableAltException nvae =
							new NoViableAltException("", 15, 0, input);
						throw nvae;
					}
					switch (alt15) {
						case 1 :
							// SwanExpression.g:177:131: (mode= history_mode ',' time= time_value )
							{
							// SwanExpression.g:177:131: (mode= history_mode ',' time= time_value )
							// SwanExpression.g:177:132: mode= history_mode ',' time= time_value
							{
							pushFollow(FOLLOW_history_mode_in_sensor_value_expression799);
							mode=history_mode();
							state._fsp--;

							match(input,45,FOLLOW_45_in_sensor_value_expression801); 
							pushFollow(FOLLOW_time_value_in_sensor_value_expression805);
							time=time_value();
							state._fsp--;

							}

							}
							break;
						case 2 :
							// SwanExpression.g:177:173: mode= history_mode
							{
							pushFollow(FOLLOW_history_mode_in_sensor_value_expression812);
							mode=history_mode();
							state._fsp--;

							}
							break;
						case 3 :
							// SwanExpression.g:177:193: time= time_value
							{
							pushFollow(FOLLOW_time_value_in_sensor_value_expression818);
							time=time_value();
							state._fsp--;

							}
							break;

					}

					match(input,51,FOLLOW_51_in_sensor_value_expression821); 
					if (time == null) {
									value_expression = new SensorValueExpression(location.getText(), entity.getText(), path /* .value_path */ , config /*.configuration */ , mode /* .history_mode */, 0,http_config);
								} else {
									value_expression = new SensorValueExpression(location.getText(), entity.getText(), path /* .value_path */ , config /*.configuration */ , mode /* .history_mode */ , time,http_config);
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
	// SwanExpression.g:185:1: constant_value_expression returns [ConstantValueExpression value_expression] : (i= INT |f= FLOAT |raw= STRING );
	public final ConstantValueExpression constant_value_expression() throws RecognitionException {
		ConstantValueExpression value_expression = null;


		Token i=null;
		Token f=null;
		Token raw=null;

		try {
			// SwanExpression.g:186:2: (i= INT |f= FLOAT |raw= STRING )
			int alt17=3;
			switch ( input.LA(1) ) {
			case INT:
				{
				alt17=1;
				}
				break;
			case FLOAT:
				{
				alt17=2;
				}
				break;
			case STRING:
				{
				alt17=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 17, 0, input);
				throw nvae;
			}
			switch (alt17) {
				case 1 :
					// SwanExpression.g:186:4: i= INT
					{
					i=(Token)match(input,INT,FOLLOW_INT_in_constant_value_expression843); 
					value_expression = new ConstantValueExpression(Long.parseLong(i.getText()));
					}
					break;
				case 2 :
					// SwanExpression.g:188:4: f= FLOAT
					{
					f=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_constant_value_expression856); 
					value_expression = new ConstantValueExpression(Double.parseDouble(f.getText()));
					}
					break;
				case 3 :
					// SwanExpression.g:190:5: raw= STRING
					{
					raw=(Token)match(input,STRING,FOLLOW_STRING_in_constant_value_expression870); 
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
	// SwanExpression.g:194:1: value_expression returns [ValueExpression value_expression] : (constant= constant_value_expression |sensor= sensor_value_expression );
	public final ValueExpression value_expression() throws RecognitionException {
		ValueExpression value_expression = null;


		ConstantValueExpression constant =null;
		SensorValueExpression sensor =null;

		try {
			// SwanExpression.g:195:2: (constant= constant_value_expression |sensor= sensor_value_expression )
			int alt18=2;
			int LA18_0 = input.LA(1);
			if ( (LA18_0==FLOAT||LA18_0==INT||LA18_0==STRING) ) {
				alt18=1;
			}
			else if ( (LA18_0==ID) ) {
				alt18=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 18, 0, input);
				throw nvae;
			}

			switch (alt18) {
				case 1 :
					// SwanExpression.g:195:4: constant= constant_value_expression
					{
					pushFollow(FOLLOW_constant_value_expression_in_value_expression893);
					constant=constant_value_expression();
					state._fsp--;

					value_expression = constant /* value expression */ ;
					}
					break;
				case 2 :
					// SwanExpression.g:197:5: sensor= sensor_value_expression
					{
					pushFollow(FOLLOW_sensor_value_expression_in_value_expression907);
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
	// SwanExpression.g:201:1: parentheticalExpression returns [Expression expression] : (val= value_expression | '(' ( WS )* exp= orExpression ( WS )* ')' );
	public final Expression parentheticalExpression() throws RecognitionException {
		Expression expression = null;


		ValueExpression val =null;
		Expression exp =null;

		try {
			// SwanExpression.g:202:5: (val= value_expression | '(' ( WS )* exp= orExpression ( WS )* ')' )
			int alt21=2;
			int LA21_0 = input.LA(1);
			if ( (LA21_0==FLOAT||(LA21_0 >= ID && LA21_0 <= INT)||LA21_0==STRING) ) {
				alt21=1;
			}
			else if ( (LA21_0==43) ) {
				alt21=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 21, 0, input);
				throw nvae;
			}

			switch (alt21) {
				case 1 :
					// SwanExpression.g:202:9: val= value_expression
					{
					pushFollow(FOLLOW_value_expression_in_parentheticalExpression936);
					val=value_expression();
					state._fsp--;

					expression = val;
					}
					break;
				case 2 :
					// SwanExpression.g:204:9: '(' ( WS )* exp= orExpression ( WS )* ')'
					{
					match(input,43,FOLLOW_43_in_parentheticalExpression961); 
					// SwanExpression.g:204:13: ( WS )*
					loop19:
					while (true) {
						int alt19=2;
						int LA19_0 = input.LA(1);
						if ( (LA19_0==WS) ) {
							alt19=1;
						}

						switch (alt19) {
						case 1 :
							// SwanExpression.g:204:13: WS
							{
							match(input,WS,FOLLOW_WS_in_parentheticalExpression963); 
							}
							break;

						default :
							break loop19;
						}
					}

					pushFollow(FOLLOW_orExpression_in_parentheticalExpression968);
					exp=orExpression();
					state._fsp--;

					// SwanExpression.g:204:34: ( WS )*
					loop20:
					while (true) {
						int alt20=2;
						int LA20_0 = input.LA(1);
						if ( (LA20_0==WS) ) {
							alt20=1;
						}

						switch (alt20) {
						case 1 :
							// SwanExpression.g:204:34: WS
							{
							match(input,WS,FOLLOW_WS_in_parentheticalExpression970); 
							}
							break;

						default :
							break loop20;
						}
					}

					match(input,44,FOLLOW_44_in_parentheticalExpression973); 
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
	// SwanExpression.g:208:1: multiplicativeExpression returns [Expression expression] : left= parentheticalExpression ( ( WS )* (location= ID '@' )? op= multiplicative_math_operator ( WS )* right= parentheticalExpression )* ;
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
			// SwanExpression.g:214:5: (left= parentheticalExpression ( ( WS )* (location= ID '@' )? op= multiplicative_math_operator ( WS )* right= parentheticalExpression )* )
			// SwanExpression.g:214:7: left= parentheticalExpression ( ( WS )* (location= ID '@' )? op= multiplicative_math_operator ( WS )* right= parentheticalExpression )*
			{
			pushFollow(FOLLOW_parentheticalExpression_in_multiplicativeExpression1020);
			left=parentheticalExpression();
			state._fsp--;

			// SwanExpression.g:215:5: ( ( WS )* (location= ID '@' )? op= multiplicative_math_operator ( WS )* right= parentheticalExpression )*
			loop25:
			while (true) {
				int alt25=2;
				alt25 = dfa25.predict(input);
				switch (alt25) {
				case 1 :
					// SwanExpression.g:215:6: ( WS )* (location= ID '@' )? op= multiplicative_math_operator ( WS )* right= parentheticalExpression
					{
					// SwanExpression.g:215:6: ( WS )*
					loop22:
					while (true) {
						int alt22=2;
						int LA22_0 = input.LA(1);
						if ( (LA22_0==WS) ) {
							alt22=1;
						}

						switch (alt22) {
						case 1 :
							// SwanExpression.g:215:6: WS
							{
							match(input,WS,FOLLOW_WS_in_multiplicativeExpression1027); 
							}
							break;

						default :
							break loop22;
						}
					}

					// SwanExpression.g:215:10: (location= ID '@' )?
					int alt23=2;
					int LA23_0 = input.LA(1);
					if ( (LA23_0==ID) ) {
						alt23=1;
					}
					switch (alt23) {
						case 1 :
							// SwanExpression.g:215:11: location= ID '@'
							{
							location=(Token)match(input,ID,FOLLOW_ID_in_multiplicativeExpression1033); 
							match(input,49,FOLLOW_49_in_multiplicativeExpression1035); 
							}
							break;

					}

					pushFollow(FOLLOW_multiplicative_math_operator_in_multiplicativeExpression1041);
					op=multiplicative_math_operator();
					state._fsp--;

					// SwanExpression.g:215:61: ( WS )*
					loop24:
					while (true) {
						int alt24=2;
						int LA24_0 = input.LA(1);
						if ( (LA24_0==WS) ) {
							alt24=1;
						}

						switch (alt24) {
						case 1 :
							// SwanExpression.g:215:61: WS
							{
							match(input,WS,FOLLOW_WS_in_multiplicativeExpression1043); 
							}
							break;

						default :
							break loop24;
						}
					}

					pushFollow(FOLLOW_parentheticalExpression_in_multiplicativeExpression1048);
					right=parentheticalExpression();
					state._fsp--;

					locationStack.push(location == null ? Expression.LOCATION_INFER : location.getText()); opStack.push(op /* .math_operator */ ); rightStack.push((ValueExpression) right /* .expression */ );
					}
					break;

				default :
					break loop25;
				}
			}


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
	// SwanExpression.g:231:1: additiveExpression returns [Expression expression] : left= multiplicativeExpression ( (location= ID '@' )? op= additive_math_operator right= multiplicativeExpression )* ;
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
			// SwanExpression.g:237:5: (left= multiplicativeExpression ( (location= ID '@' )? op= additive_math_operator right= multiplicativeExpression )* )
			// SwanExpression.g:237:7: left= multiplicativeExpression ( (location= ID '@' )? op= additive_math_operator right= multiplicativeExpression )*
			{
			pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression1096);
			left=multiplicativeExpression();
			state._fsp--;

			// SwanExpression.g:238:5: ( (location= ID '@' )? op= additive_math_operator right= multiplicativeExpression )*
			loop27:
			while (true) {
				int alt27=2;
				int LA27_0 = input.LA(1);
				if ( (LA27_0==ID) ) {
					int LA27_2 = input.LA(2);
					if ( (LA27_2==49) ) {
						int LA27_4 = input.LA(3);
						if ( (LA27_4==MINUS||LA27_4==PLUS) ) {
							alt27=1;
						}

					}

				}
				else if ( (LA27_0==MINUS||LA27_0==PLUS) ) {
					alt27=1;
				}

				switch (alt27) {
				case 1 :
					// SwanExpression.g:238:6: (location= ID '@' )? op= additive_math_operator right= multiplicativeExpression
					{
					// SwanExpression.g:238:6: (location= ID '@' )?
					int alt26=2;
					int LA26_0 = input.LA(1);
					if ( (LA26_0==ID) ) {
						alt26=1;
					}
					switch (alt26) {
						case 1 :
							// SwanExpression.g:238:7: location= ID '@'
							{
							location=(Token)match(input,ID,FOLLOW_ID_in_additiveExpression1106); 
							match(input,49,FOLLOW_49_in_additiveExpression1108); 
							}
							break;

					}

					pushFollow(FOLLOW_additive_math_operator_in_additiveExpression1114);
					op=additive_math_operator();
					state._fsp--;

					pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression1118);
					right=multiplicativeExpression();
					state._fsp--;

					locationStack.push(location == null ? Expression.LOCATION_INFER : location.getText()); opStack.push(op /* .math_operator */ ); rightStack.push((ValueExpression) right /* .expression */ );
					}
					break;

				default :
					break loop27;
				}
			}


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
	// SwanExpression.g:254:1: comparativeExpression returns [Expression expression] : left= additiveExpression ( ( WS )* ( (location= ID )? c= comparator ) ( WS )* right= additiveExpression )? ;
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
			// SwanExpression.g:260:5: (left= additiveExpression ( ( WS )* ( (location= ID )? c= comparator ) ( WS )* right= additiveExpression )? )
			// SwanExpression.g:260:7: left= additiveExpression ( ( WS )* ( (location= ID )? c= comparator ) ( WS )* right= additiveExpression )?
			{
			pushFollow(FOLLOW_additiveExpression_in_comparativeExpression1166);
			left=additiveExpression();
			state._fsp--;

			// SwanExpression.g:261:5: ( ( WS )* ( (location= ID )? c= comparator ) ( WS )* right= additiveExpression )?
			int alt31=2;
			alt31 = dfa31.predict(input);
			switch (alt31) {
				case 1 :
					// SwanExpression.g:261:6: ( WS )* ( (location= ID )? c= comparator ) ( WS )* right= additiveExpression
					{
					// SwanExpression.g:261:6: ( WS )*
					loop28:
					while (true) {
						int alt28=2;
						int LA28_0 = input.LA(1);
						if ( (LA28_0==WS) ) {
							alt28=1;
						}

						switch (alt28) {
						case 1 :
							// SwanExpression.g:261:6: WS
							{
							match(input,WS,FOLLOW_WS_in_comparativeExpression1173); 
							}
							break;

						default :
							break loop28;
						}
					}

					// SwanExpression.g:261:10: ( (location= ID )? c= comparator )
					// SwanExpression.g:261:11: (location= ID )? c= comparator
					{
					// SwanExpression.g:261:11: (location= ID )?
					int alt29=2;
					int LA29_0 = input.LA(1);
					if ( (LA29_0==ID) ) {
						alt29=1;
					}
					switch (alt29) {
						case 1 :
							// SwanExpression.g:261:12: location= ID
							{
							location=(Token)match(input,ID,FOLLOW_ID_in_comparativeExpression1180); 
							}
							break;

					}

					pushFollow(FOLLOW_comparator_in_comparativeExpression1186);
					c=comparator();
					state._fsp--;

					}

					// SwanExpression.g:261:40: ( WS )*
					loop30:
					while (true) {
						int alt30=2;
						int LA30_0 = input.LA(1);
						if ( (LA30_0==WS) ) {
							alt30=1;
						}

						switch (alt30) {
						case 1 :
							// SwanExpression.g:261:40: WS
							{
							match(input,WS,FOLLOW_WS_in_comparativeExpression1189); 
							}
							break;

						default :
							break loop30;
						}
					}

					pushFollow(FOLLOW_additiveExpression_in_comparativeExpression1194);
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
	// SwanExpression.g:277:1: unaryExpression returns [Expression expression] : ( (location= ID )? NOT exp= comparativeExpression |exp= comparativeExpression );
	public final Expression unaryExpression() throws RecognitionException {
		Expression expression = null;


		Token location=null;
		Expression exp =null;

		try {
			// SwanExpression.g:278:5: ( (location= ID )? NOT exp= comparativeExpression |exp= comparativeExpression )
			int alt33=2;
			switch ( input.LA(1) ) {
			case ID:
				{
				int LA33_1 = input.LA(2);
				if ( (LA33_1==49) ) {
					alt33=2;
				}
				else if ( (LA33_1==NOT) ) {
					alt33=1;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 33, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case NOT:
				{
				alt33=1;
				}
				break;
			case FLOAT:
			case INT:
			case STRING:
			case 43:
				{
				alt33=2;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 33, 0, input);
				throw nvae;
			}
			switch (alt33) {
				case 1 :
					// SwanExpression.g:278:7: (location= ID )? NOT exp= comparativeExpression
					{
					// SwanExpression.g:278:7: (location= ID )?
					int alt32=2;
					int LA32_0 = input.LA(1);
					if ( (LA32_0==ID) ) {
						alt32=1;
					}
					switch (alt32) {
						case 1 :
							// SwanExpression.g:278:8: location= ID
							{
							location=(Token)match(input,ID,FOLLOW_ID_in_unaryExpression1238); 
							}
							break;

					}

					match(input,NOT,FOLLOW_NOT_in_unaryExpression1242); 
					pushFollow(FOLLOW_comparativeExpression_in_unaryExpression1246);
					exp=comparativeExpression();
					state._fsp--;

					expression = new LogicExpression(location == null ? Expression.LOCATION_INFER : location.getText(), UnaryLogicOperator.NOT /* .logic_operator */ , (TriStateExpression) exp /* .expression */ );
					}
					break;
				case 2 :
					// SwanExpression.g:280:7: exp= comparativeExpression
					{
					pushFollow(FOLLOW_comparativeExpression_in_unaryExpression1267);
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
	// SwanExpression.g:284:1: andExpression returns [Expression expression] : left= unaryExpression ( (location= ID '@' )? AND right= unaryExpression )* ;
	public final Expression andExpression() throws RecognitionException {
		Expression expression = null;


		Token location=null;
		Expression left =null;
		Expression right =null;


		    Stack<Expression> rightStack = new Stack<Expression>();
		    Stack<String> locationStack = new Stack<String>();

		try {
			// SwanExpression.g:289:5: (left= unaryExpression ( (location= ID '@' )? AND right= unaryExpression )* )
			// SwanExpression.g:289:7: left= unaryExpression ( (location= ID '@' )? AND right= unaryExpression )*
			{
			pushFollow(FOLLOW_unaryExpression_in_andExpression1306);
			left=unaryExpression();
			state._fsp--;

			// SwanExpression.g:290:5: ( (location= ID '@' )? AND right= unaryExpression )*
			loop35:
			while (true) {
				int alt35=2;
				int LA35_0 = input.LA(1);
				if ( (LA35_0==ID) ) {
					int LA35_1 = input.LA(2);
					if ( (LA35_1==49) ) {
						int LA35_4 = input.LA(3);
						if ( (LA35_4==AND) ) {
							alt35=1;
						}

					}

				}
				else if ( (LA35_0==AND) ) {
					alt35=1;
				}

				switch (alt35) {
				case 1 :
					// SwanExpression.g:290:6: (location= ID '@' )? AND right= unaryExpression
					{
					// SwanExpression.g:290:6: (location= ID '@' )?
					int alt34=2;
					int LA34_0 = input.LA(1);
					if ( (LA34_0==ID) ) {
						alt34=1;
					}
					switch (alt34) {
						case 1 :
							// SwanExpression.g:290:7: location= ID '@'
							{
							location=(Token)match(input,ID,FOLLOW_ID_in_andExpression1316); 
							match(input,49,FOLLOW_49_in_andExpression1318); 
							}
							break;

					}

					match(input,AND,FOLLOW_AND_in_andExpression1322); 
					pushFollow(FOLLOW_unaryExpression_in_andExpression1326);
					right=unaryExpression();
					state._fsp--;

					locationStack.push(location == null ? Expression.LOCATION_INFER : location.getText()); rightStack.push(right /* .expression */ );
					}
					break;

				default :
					break loop35;
				}
			}


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
	// SwanExpression.g:306:1: orExpression returns [Expression expression] : left= andExpression ( (location= ID '@' )? OR right= andExpression )* ;
	public final Expression orExpression() throws RecognitionException {
		Expression expression = null;


		Token location=null;
		Expression left =null;
		Expression right =null;


		    Stack<Expression> rightStack = new Stack<Expression>();
		    Stack<String> locationStack = new Stack<String>();

		try {
			// SwanExpression.g:311:5: (left= andExpression ( (location= ID '@' )? OR right= andExpression )* )
			// SwanExpression.g:311:7: left= andExpression ( (location= ID '@' )? OR right= andExpression )*
			{
			pushFollow(FOLLOW_andExpression_in_orExpression1374);
			left=andExpression();
			state._fsp--;

			// SwanExpression.g:312:5: ( (location= ID '@' )? OR right= andExpression )*
			loop37:
			while (true) {
				int alt37=2;
				int LA37_0 = input.LA(1);
				if ( (LA37_0==ID||LA37_0==OR) ) {
					alt37=1;
				}

				switch (alt37) {
				case 1 :
					// SwanExpression.g:312:6: (location= ID '@' )? OR right= andExpression
					{
					// SwanExpression.g:312:6: (location= ID '@' )?
					int alt36=2;
					int LA36_0 = input.LA(1);
					if ( (LA36_0==ID) ) {
						alt36=1;
					}
					switch (alt36) {
						case 1 :
							// SwanExpression.g:312:7: location= ID '@'
							{
							location=(Token)match(input,ID,FOLLOW_ID_in_orExpression1384); 
							match(input,49,FOLLOW_49_in_orExpression1386); 
							}
							break;

					}

					match(input,OR,FOLLOW_OR_in_orExpression1390); 
					pushFollow(FOLLOW_andExpression_in_orExpression1394);
					right=andExpression();
					state._fsp--;

					locationStack.push(location == null ? Expression.LOCATION_INFER : location.getText()); rightStack.push(right /* .expression */ );
					}
					break;

				default :
					break loop37;
				}
			}


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
	// SwanExpression.g:328:1: expression returns [Expression expression] : logic= orExpression EOF ;
	public final Expression expression() throws RecognitionException {
		Expression expression = null;


		Expression logic =null;

		try {
			// SwanExpression.g:329:5: (logic= orExpression EOF )
			// SwanExpression.g:330:5: logic= orExpression EOF
			{
			pushFollow(FOLLOW_orExpression_in_expression1444);
			logic=orExpression();
			state._fsp--;

			match(input,EOF,FOLLOW_EOF_in_expression1446); 
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
	// SwanExpression.g:335:1: time_value returns [Long time] : val= INT (unit= TIME_UNIT )? (rep_val= INT (rep_unit= TIME_UNIT )? )* ;
	public final Long time_value() throws RecognitionException {
		Long time = null;


		Token val=null;
		Token unit=null;
		Token rep_val=null;
		Token rep_unit=null;

		try {
			// SwanExpression.g:336:5: (val= INT (unit= TIME_UNIT )? (rep_val= INT (rep_unit= TIME_UNIT )? )* )
			// SwanExpression.g:337:5: val= INT (unit= TIME_UNIT )? (rep_val= INT (rep_unit= TIME_UNIT )? )*
			{
			val=(Token)match(input,INT,FOLLOW_INT_in_time_value1484); 
			// SwanExpression.g:337:17: (unit= TIME_UNIT )?
			int alt38=2;
			int LA38_0 = input.LA(1);
			if ( (LA38_0==TIME_UNIT) ) {
				alt38=1;
			}
			switch (alt38) {
				case 1 :
					// SwanExpression.g:337:17: unit= TIME_UNIT
					{
					unit=(Token)match(input,TIME_UNIT,FOLLOW_TIME_UNIT_in_time_value1488); 
					}
					break;

			}

			 long theTime = convertTime(val, unit); 
			// SwanExpression.g:339:5: (rep_val= INT (rep_unit= TIME_UNIT )? )*
			loop40:
			while (true) {
				int alt40=2;
				int LA40_0 = input.LA(1);
				if ( (LA40_0==INT) ) {
					alt40=1;
				}

				switch (alt40) {
				case 1 :
					// SwanExpression.g:339:6: rep_val= INT (rep_unit= TIME_UNIT )?
					{
					rep_val=(Token)match(input,INT,FOLLOW_INT_in_time_value1505); 
					// SwanExpression.g:339:26: (rep_unit= TIME_UNIT )?
					int alt39=2;
					int LA39_0 = input.LA(1);
					if ( (LA39_0==TIME_UNIT) ) {
						alt39=1;
					}
					switch (alt39) {
						case 1 :
							// SwanExpression.g:339:26: rep_unit= TIME_UNIT
							{
							rep_unit=(Token)match(input,TIME_UNIT,FOLLOW_TIME_UNIT_in_time_value1509); 
							}
							break;

					}

					 theTime += convertTime(rep_val, rep_unit);
					}
					break;

				default :
					break loop40;
				}
			}

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


	protected DFA16 dfa16 = new DFA16(this);
	protected DFA25 dfa25 = new DFA25(this);
	protected DFA31 dfa31 = new DFA31(this);
	static final String DFA16_eotS =
		"\26\uffff";
	static final String DFA16_eofS =
		"\5\uffff\2\10\5\uffff\2\10\1\uffff\1\21\5\uffff\1\21";
	static final String DFA16_minS =
		"\1\26\1\61\1\26\1\57\1\26\2\5\1\26\1\uffff\1\26\2\uffff\2\5\1\12\1\5\1"+
		"\26\3\uffff\1\12\1\5";
	static final String DFA16_maxS =
		"\1\26\1\61\1\26\1\57\1\47\2\62\1\47\1\uffff\1\26\2\uffff\2\62\1\12\1\62"+
		"\1\26\3\uffff\1\12\1\62";
	static final String DFA16_acceptS =
		"\10\uffff\1\1\1\uffff\1\3\1\4\5\uffff\1\2\1\5\1\6\2\uffff";
	static final String DFA16_specialS =
		"\26\uffff}>";
	static final String[] DFA16_transitionS = {
			"\1\1",
			"\1\2",
			"\1\3",
			"\1\4",
			"\1\5\20\uffff\1\6",
			"\1\10\5\uffff\3\10\3\uffff\2\10\3\uffff\1\10\1\uffff\2\10\4\uffff\3"+
			"\10\2\uffff\4\10\2\uffff\1\10\1\13\1\uffff\1\10\1\uffff\1\7\1\uffff\1"+
			"\11\1\uffff\1\12",
			"\1\10\5\uffff\3\10\3\uffff\2\10\3\uffff\1\10\1\uffff\2\10\4\uffff\3"+
			"\10\2\uffff\4\10\2\uffff\1\10\1\13\1\uffff\1\10\1\uffff\1\7\1\uffff\1"+
			"\11\1\uffff\1\12",
			"\1\14\20\uffff\1\15",
			"",
			"\1\16",
			"",
			"",
			"\1\10\5\uffff\3\10\3\uffff\2\10\3\uffff\1\10\1\uffff\2\10\4\uffff\3"+
			"\10\2\uffff\4\10\2\uffff\1\10\1\13\1\uffff\1\10\1\uffff\1\7\1\uffff\1"+
			"\11\1\uffff\1\12",
			"\1\10\5\uffff\3\10\3\uffff\2\10\3\uffff\1\10\1\uffff\2\10\4\uffff\3"+
			"\10\2\uffff\4\10\2\uffff\1\10\1\13\1\uffff\1\10\1\uffff\1\7\1\uffff\1"+
			"\11\1\uffff\1\12",
			"\1\17",
			"\1\21\1\uffff\1\20\3\uffff\3\21\3\uffff\2\21\3\uffff\1\21\1\uffff\2"+
			"\21\4\uffff\3\21\2\uffff\4\21\2\uffff\1\21\1\23\1\uffff\1\21\5\uffff"+
			"\1\22",
			"\1\24",
			"",
			"",
			"",
			"\1\25",
			"\1\21\1\uffff\1\20\3\uffff\3\21\3\uffff\2\21\3\uffff\1\21\1\uffff\2"+
			"\21\4\uffff\3\21\2\uffff\4\21\2\uffff\1\21\1\23\1\uffff\1\21\5\uffff"+
			"\1\22"
	};

	static final short[] DFA16_eot = DFA.unpackEncodedString(DFA16_eotS);
	static final short[] DFA16_eof = DFA.unpackEncodedString(DFA16_eofS);
	static final char[] DFA16_min = DFA.unpackEncodedStringToUnsignedChars(DFA16_minS);
	static final char[] DFA16_max = DFA.unpackEncodedStringToUnsignedChars(DFA16_maxS);
	static final short[] DFA16_accept = DFA.unpackEncodedString(DFA16_acceptS);
	static final short[] DFA16_special = DFA.unpackEncodedString(DFA16_specialS);
	static final short[][] DFA16_transition;

	static {
		int numStates = DFA16_transitionS.length;
		DFA16_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA16_transition[i] = DFA.unpackEncodedString(DFA16_transitionS[i]);
		}
	}

	protected class DFA16 extends DFA {

		public DFA16(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 16;
			this.eot = DFA16_eot;
			this.eof = DFA16_eof;
			this.min = DFA16_min;
			this.max = DFA16_max;
			this.accept = DFA16_accept;
			this.special = DFA16_special;
			this.transition = DFA16_transition;
		}
		@Override
		public String getDescription() {
			return "154:1: sensor_value_expression returns [SensorValueExpression value_expression] : (location= ID '@' entity= ID ':' path= value_path |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options |location= ID '@' entity= ID ':' path= value_path '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' |location= ID '@' entity= ID ':' path= value_path '$' http_config= http_configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options '$' http_config= http_configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' );";
		}
	}

	static final String DFA25_eotS =
		"\7\uffff";
	static final String DFA25_eofS =
		"\1\2\6\uffff";
	static final String DFA25_minS =
		"\1\5\1\13\1\uffff\1\13\1\uffff\1\5\1\13";
	static final String DFA25_maxS =
		"\1\54\1\61\1\uffff\1\54\1\uffff\1\45\1\61";
	static final String DFA25_acceptS =
		"\2\uffff\1\2\1\uffff\1\1\2\uffff";
	static final String DFA25_specialS =
		"\7\uffff}>";
	static final String[] DFA25_transitionS = {
			"\1\2\5\uffff\1\2\1\4\1\2\3\uffff\2\2\3\uffff\1\1\1\uffff\2\2\4\uffff"+
			"\1\2\2\4\2\uffff\4\2\2\uffff\1\3\2\uffff\1\2",
			"\1\2\1\uffff\1\2\3\uffff\2\2\5\uffff\2\2\11\uffff\1\2\2\uffff\1\2\12"+
			"\uffff\1\5",
			"",
			"\1\2\1\4\1\2\3\uffff\2\2\3\uffff\1\6\1\uffff\2\2\5\uffff\2\4\2\uffff"+
			"\1\2\2\uffff\1\2\2\uffff\1\3\2\uffff\1\2",
			"",
			"\1\2\6\uffff\1\4\21\uffff\1\2\2\4\3\uffff\2\2",
			"\1\2\1\uffff\1\2\3\uffff\2\2\5\uffff\2\2\11\uffff\1\2\2\uffff\1\2\12"+
			"\uffff\1\4"
	};

	static final short[] DFA25_eot = DFA.unpackEncodedString(DFA25_eotS);
	static final short[] DFA25_eof = DFA.unpackEncodedString(DFA25_eofS);
	static final char[] DFA25_min = DFA.unpackEncodedStringToUnsignedChars(DFA25_minS);
	static final char[] DFA25_max = DFA.unpackEncodedStringToUnsignedChars(DFA25_maxS);
	static final short[] DFA25_accept = DFA.unpackEncodedString(DFA25_acceptS);
	static final short[] DFA25_special = DFA.unpackEncodedString(DFA25_specialS);
	static final short[][] DFA25_transition;

	static {
		int numStates = DFA25_transitionS.length;
		DFA25_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA25_transition[i] = DFA.unpackEncodedString(DFA25_transitionS[i]);
		}
	}

	protected class DFA25 extends DFA {

		public DFA25(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 25;
			this.eot = DFA25_eot;
			this.eof = DFA25_eof;
			this.min = DFA25_min;
			this.max = DFA25_max;
			this.accept = DFA25_accept;
			this.special = DFA25_special;
			this.transition = DFA25_transition;
		}
		@Override
		public String getDescription() {
			return "()* loopback of 215:5: ( ( WS )* (location= ID '@' )? op= multiplicative_math_operator ( WS )* right= parentheticalExpression )*";
		}
	}

	static final String DFA31_eotS =
		"\5\uffff";
	static final String DFA31_eofS =
		"\1\4\4\uffff";
	static final String DFA31_minS =
		"\1\5\2\13\2\uffff";
	static final String DFA31_maxS =
		"\2\54\1\61\2\uffff";
	static final String DFA31_acceptS =
		"\3\uffff\1\1\1\2";
	static final String DFA31_specialS =
		"\5\uffff}>";
	static final String[] DFA31_transitionS = {
			"\1\4\5\uffff\1\3\1\uffff\1\3\3\uffff\2\3\3\uffff\1\2\1\uffff\2\3\11\uffff"+
			"\1\3\1\4\1\uffff\1\3\2\uffff\1\1\2\uffff\1\4",
			"\1\3\1\uffff\1\3\3\uffff\2\3\3\uffff\1\3\1\uffff\2\3\11\uffff\1\3\2"+
			"\uffff\1\3\2\uffff\1\1\2\uffff\1\4",
			"\1\3\1\uffff\1\3\3\uffff\2\3\5\uffff\2\3\11\uffff\1\3\2\uffff\1\3\12"+
			"\uffff\1\4",
			"",
			""
	};

	static final short[] DFA31_eot = DFA.unpackEncodedString(DFA31_eotS);
	static final short[] DFA31_eof = DFA.unpackEncodedString(DFA31_eofS);
	static final char[] DFA31_min = DFA.unpackEncodedStringToUnsignedChars(DFA31_minS);
	static final char[] DFA31_max = DFA.unpackEncodedStringToUnsignedChars(DFA31_maxS);
	static final short[] DFA31_accept = DFA.unpackEncodedString(DFA31_acceptS);
	static final short[] DFA31_special = DFA.unpackEncodedString(DFA31_specialS);
	static final short[][] DFA31_transition;

	static {
		int numStates = DFA31_transitionS.length;
		DFA31_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA31_transition[i] = DFA.unpackEncodedString(DFA31_transitionS[i]);
		}
	}

	protected class DFA31 extends DFA {

		public DFA31(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 31;
			this.eot = DFA31_eot;
			this.eof = DFA31_eof;
			this.min = DFA31_min;
			this.max = DFA31_max;
			this.accept = DFA31_accept;
			this.special = DFA31_special;
			this.transition = DFA31_transition;
		}
		@Override
		public String getDescription() {
			return "261:5: ( ( WS )* ( (location= ID )? c= comparator ) ( WS )* right= additiveExpression )?";
		}
	}

	public static final BitSet FOLLOW_ID_in_http_configuration_options62 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_CONFIG_VAL_in_http_configuration_options66 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_CONFIG_TILT_in_http_configuration_options76 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_ID_in_http_configuration_options80 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_CONFIG_VAL_in_http_configuration_options84 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_ID_in_configuration_options123 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_CONFIG_VAL_in_configuration_options127 = new BitSet(new long[]{0x0000000000000082L});
	public static final BitSet FOLLOW_CONFIG_HASH_in_configuration_options137 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_ID_in_configuration_options141 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_CONFIG_VAL_in_configuration_options145 = new BitSet(new long[]{0x0000000000000082L});
	public static final BitSet FOLLOW_v_p_in_value_path181 = new BitSet(new long[]{0x0000400000000002L});
	public static final BitSet FOLLOW_46_in_value_path190 = new BitSet(new long[]{0x0000008000400000L});
	public static final BitSet FOLLOW_v_p_in_value_path194 = new BitSet(new long[]{0x0000400000000002L});
	public static final BitSet FOLLOW_ID_in_v_p226 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_STRING_in_v_p235 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GT_in_comparator252 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LT_in_comparator259 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GTEQ_in_comparator266 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LTEQ_in_comparator273 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EQUALS_in_comparator280 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NOTEQUALS_in_comparator287 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_REGEX_in_comparator294 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CONTAINS_in_comparator301 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_binary_logic_operator_in_logic_operator323 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_unary_logic_operator_in_logic_operator336 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AND_in_binary_logic_operator357 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OR_in_binary_logic_operator364 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NOT_in_unary_logic_operator381 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_additive_math_operator_in_math_operator400 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_multiplicative_math_operator_in_math_operator409 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PLUS_in_additive_math_operator426 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MINUS_in_additive_math_operator433 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MULT_in_multiplicative_math_operator451 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DIV_in_multiplicative_math_operator458 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MOD_in_multiplicative_math_operator465 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ALL_in_history_mode482 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MAX_in_history_mode489 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MIN_in_history_mode496 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MEAN_in_history_mode503 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MEDIAN_in_history_mode510 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ANY_in_history_mode517 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_sensor_value_expression538 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_49_in_sensor_value_expression540 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_ID_in_sensor_value_expression544 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_47_in_sensor_value_expression546 = new BitSet(new long[]{0x0000008000400000L});
	public static final BitSet FOLLOW_value_path_in_sensor_value_expression550 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_sensor_value_expression562 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_49_in_sensor_value_expression564 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_ID_in_sensor_value_expression568 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_47_in_sensor_value_expression570 = new BitSet(new long[]{0x0000008000400000L});
	public static final BitSet FOLLOW_value_path_in_sensor_value_expression574 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_48_in_sensor_value_expression576 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_configuration_options_in_sensor_value_expression580 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_sensor_value_expression592 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_49_in_sensor_value_expression594 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_ID_in_sensor_value_expression598 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_47_in_sensor_value_expression600 = new BitSet(new long[]{0x0000008000400000L});
	public static final BitSet FOLLOW_value_path_in_sensor_value_expression604 = new BitSet(new long[]{0x0004000000000000L});
	public static final BitSet FOLLOW_50_in_sensor_value_expression606 = new BitSet(new long[]{0x000000003C800050L});
	public static final BitSet FOLLOW_history_mode_in_sensor_value_expression612 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_45_in_sensor_value_expression614 = new BitSet(new long[]{0x0000000000800000L});
	public static final BitSet FOLLOW_time_value_in_sensor_value_expression618 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_history_mode_in_sensor_value_expression625 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_time_value_in_sensor_value_expression631 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_51_in_sensor_value_expression634 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_sensor_value_expression647 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_49_in_sensor_value_expression649 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_ID_in_sensor_value_expression653 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_47_in_sensor_value_expression655 = new BitSet(new long[]{0x0000008000400000L});
	public static final BitSet FOLLOW_value_path_in_sensor_value_expression659 = new BitSet(new long[]{0x0000040000000000L});
	public static final BitSet FOLLOW_42_in_sensor_value_expression661 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_http_configuration_options_in_sensor_value_expression665 = new BitSet(new long[]{0x0004000000000000L});
	public static final BitSet FOLLOW_50_in_sensor_value_expression667 = new BitSet(new long[]{0x000000003C800050L});
	public static final BitSet FOLLOW_history_mode_in_sensor_value_expression673 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_45_in_sensor_value_expression675 = new BitSet(new long[]{0x0000000000800000L});
	public static final BitSet FOLLOW_time_value_in_sensor_value_expression679 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_history_mode_in_sensor_value_expression686 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_time_value_in_sensor_value_expression692 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_51_in_sensor_value_expression695 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_sensor_value_expression707 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_49_in_sensor_value_expression709 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_ID_in_sensor_value_expression713 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_47_in_sensor_value_expression715 = new BitSet(new long[]{0x0000008000400000L});
	public static final BitSet FOLLOW_value_path_in_sensor_value_expression719 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_48_in_sensor_value_expression721 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_configuration_options_in_sensor_value_expression725 = new BitSet(new long[]{0x0004000000000000L});
	public static final BitSet FOLLOW_50_in_sensor_value_expression727 = new BitSet(new long[]{0x000000003C800050L});
	public static final BitSet FOLLOW_history_mode_in_sensor_value_expression733 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_45_in_sensor_value_expression735 = new BitSet(new long[]{0x0000000000800000L});
	public static final BitSet FOLLOW_time_value_in_sensor_value_expression739 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_history_mode_in_sensor_value_expression746 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_time_value_in_sensor_value_expression752 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_51_in_sensor_value_expression755 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_sensor_value_expression767 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_49_in_sensor_value_expression769 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_ID_in_sensor_value_expression773 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_47_in_sensor_value_expression775 = new BitSet(new long[]{0x0000008000400000L});
	public static final BitSet FOLLOW_value_path_in_sensor_value_expression779 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_48_in_sensor_value_expression781 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_configuration_options_in_sensor_value_expression785 = new BitSet(new long[]{0x0000040000000000L});
	public static final BitSet FOLLOW_42_in_sensor_value_expression787 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_http_configuration_options_in_sensor_value_expression791 = new BitSet(new long[]{0x0004000000000000L});
	public static final BitSet FOLLOW_50_in_sensor_value_expression793 = new BitSet(new long[]{0x000000003C800050L});
	public static final BitSet FOLLOW_history_mode_in_sensor_value_expression799 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_45_in_sensor_value_expression801 = new BitSet(new long[]{0x0000000000800000L});
	public static final BitSet FOLLOW_time_value_in_sensor_value_expression805 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_history_mode_in_sensor_value_expression812 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_time_value_in_sensor_value_expression818 = new BitSet(new long[]{0x0008000000000000L});
	public static final BitSet FOLLOW_51_in_sensor_value_expression821 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INT_in_constant_value_expression843 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FLOAT_in_constant_value_expression856 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_STRING_in_constant_value_expression870 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_constant_value_expression_in_value_expression893 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_sensor_value_expression_in_value_expression907 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_value_expression_in_parentheticalExpression936 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_43_in_parentheticalExpression961 = new BitSet(new long[]{0x00000A8400C10000L});
	public static final BitSet FOLLOW_WS_in_parentheticalExpression963 = new BitSet(new long[]{0x00000A8400C10000L});
	public static final BitSet FOLLOW_orExpression_in_parentheticalExpression968 = new BitSet(new long[]{0x0000120000000000L});
	public static final BitSet FOLLOW_WS_in_parentheticalExpression970 = new BitSet(new long[]{0x0000120000000000L});
	public static final BitSet FOLLOW_44_in_parentheticalExpression973 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_parentheticalExpression_in_multiplicativeExpression1020 = new BitSet(new long[]{0x0000020180401002L});
	public static final BitSet FOLLOW_WS_in_multiplicativeExpression1027 = new BitSet(new long[]{0x0000020180401000L});
	public static final BitSet FOLLOW_ID_in_multiplicativeExpression1033 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_49_in_multiplicativeExpression1035 = new BitSet(new long[]{0x0000000180001000L});
	public static final BitSet FOLLOW_multiplicative_math_operator_in_multiplicativeExpression1041 = new BitSet(new long[]{0x00000A8000C10000L});
	public static final BitSet FOLLOW_WS_in_multiplicativeExpression1043 = new BitSet(new long[]{0x00000A8000C10000L});
	public static final BitSet FOLLOW_parentheticalExpression_in_multiplicativeExpression1048 = new BitSet(new long[]{0x0000020180401002L});
	public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression1096 = new BitSet(new long[]{0x0000002040400002L});
	public static final BitSet FOLLOW_ID_in_additiveExpression1106 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_49_in_additiveExpression1108 = new BitSet(new long[]{0x0000002040000000L});
	public static final BitSet FOLLOW_additive_math_operator_in_additiveExpression1114 = new BitSet(new long[]{0x0000088000C10000L});
	public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression1118 = new BitSet(new long[]{0x0000002040400002L});
	public static final BitSet FOLLOW_additiveExpression_in_comparativeExpression1166 = new BitSet(new long[]{0x0000024803462802L});
	public static final BitSet FOLLOW_WS_in_comparativeExpression1173 = new BitSet(new long[]{0x0000024803462800L});
	public static final BitSet FOLLOW_ID_in_comparativeExpression1180 = new BitSet(new long[]{0x0000004803062800L});
	public static final BitSet FOLLOW_comparator_in_comparativeExpression1186 = new BitSet(new long[]{0x00000A8000C10000L});
	public static final BitSet FOLLOW_WS_in_comparativeExpression1189 = new BitSet(new long[]{0x00000A8000C10000L});
	public static final BitSet FOLLOW_additiveExpression_in_comparativeExpression1194 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_unaryExpression1238 = new BitSet(new long[]{0x0000000400000000L});
	public static final BitSet FOLLOW_NOT_in_unaryExpression1242 = new BitSet(new long[]{0x0000088000C10000L});
	public static final BitSet FOLLOW_comparativeExpression_in_unaryExpression1246 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_comparativeExpression_in_unaryExpression1267 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_unaryExpression_in_andExpression1306 = new BitSet(new long[]{0x0000000000400022L});
	public static final BitSet FOLLOW_ID_in_andExpression1316 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_49_in_andExpression1318 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_AND_in_andExpression1322 = new BitSet(new long[]{0x0000088400C10000L});
	public static final BitSet FOLLOW_unaryExpression_in_andExpression1326 = new BitSet(new long[]{0x0000000000400022L});
	public static final BitSet FOLLOW_andExpression_in_orExpression1374 = new BitSet(new long[]{0x0000001000400002L});
	public static final BitSet FOLLOW_ID_in_orExpression1384 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_49_in_orExpression1386 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_OR_in_orExpression1390 = new BitSet(new long[]{0x0000088400C10000L});
	public static final BitSet FOLLOW_andExpression_in_orExpression1394 = new BitSet(new long[]{0x0000001000400002L});
	public static final BitSet FOLLOW_orExpression_in_expression1444 = new BitSet(new long[]{0x0000000000000000L});
	public static final BitSet FOLLOW_EOF_in_expression1446 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INT_in_time_value1484 = new BitSet(new long[]{0x0000010000800002L});
	public static final BitSet FOLLOW_TIME_UNIT_in_time_value1488 = new BitSet(new long[]{0x0000000000800002L});
	public static final BitSet FOLLOW_INT_in_time_value1505 = new BitSet(new long[]{0x0000010000800002L});
	public static final BitSet FOLLOW_TIME_UNIT_in_time_value1509 = new BitSet(new long[]{0x0000000000800002L});
}
