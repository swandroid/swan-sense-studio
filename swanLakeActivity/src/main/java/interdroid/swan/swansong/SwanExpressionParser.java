// $ANTLR 3.5.2 SwanExpression.g 2015-12-24 10:51:10

package interdroid.swan.swansong;

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
		"ESC_SEQ", "EXPONENT", "FLOAT", "GT", "GTEQ", "HEX_DIGIT", "ID", "INT", 
		"LT", "LTEQ", "MAX", "MEAN", "MEDIAN", "MIN", "MINUS", "MOD", "MULT", 
		"NONE", "NOT", "NOTEQUALS", "OR", "PLUS", "REGEX", "STRING", "TIME_UNIT", 
		"WS", "'$'", "'('", "')'", "','", "'.'", "':'", "'?'", "'@'", "'{'", "'}'"
	};
	public static final int EOF=-1;
	public static final int T__40=40;
	public static final int T__41=41;
	public static final int T__42=42;
	public static final int T__43=43;
	public static final int T__44=44;
	public static final int T__45=45;
	public static final int T__46=46;
	public static final int T__47=47;
	public static final int T__48=48;
	public static final int T__49=49;
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
	public static final int ID=20;
	public static final int INT=21;
	public static final int LT=22;
	public static final int LTEQ=23;
	public static final int MAX=24;
	public static final int MEAN=25;
	public static final int MEDIAN=26;
	public static final int MIN=27;
	public static final int MINUS=28;
	public static final int MOD=29;
	public static final int MULT=30;
	public static final int NONE=31;
	public static final int NOT=32;
	public static final int NOTEQUALS=33;
	public static final int OR=34;
	public static final int PLUS=35;
	public static final int REGEX=36;
	public static final int STRING=37;
	public static final int TIME_UNIT=38;
	public static final int WS=39;

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
				if ( (LA3_0==44) ) {
					alt3=1;
				}

				switch (alt3) {
				case 1 :
					// SwanExpression.g:88:3: '.' more_id= v_p
					{
					match(input,44,FOLLOW_44_in_value_path190); 
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
	// SwanExpression.g:154:1: sensor_value_expression returns [SensorValueExpression value_expression] : (location= ID '@' entity= ID ':' path= value_path |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options |location= ID '@' entity= ID ':' path= value_path '$' http_config= http_configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options '$' http_config= http_configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' );
	public final SensorValueExpression sensor_value_expression() throws RecognitionException {
		SensorValueExpression value_expression = null;


		Token location=null;
		Token entity=null;
		String path =null;
		Bundle config =null;
		Bundle http_config =null;
		HistoryReductionMode mode =null;
		Long time =null;

		try {
			// SwanExpression.g:155:2: (location= ID '@' entity= ID ':' path= value_path |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options |location= ID '@' entity= ID ':' path= value_path '$' http_config= http_configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options '$' http_config= http_configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' )
			int alt14=4;
			alt14 = dfa14.predict(input);
			switch (alt14) {
				case 1 :
					// SwanExpression.g:155:4: location= ID '@' entity= ID ':' path= value_path
					{
					location=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression538); 
					match(input,47,FOLLOW_47_in_sensor_value_expression540); 
					entity=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression544); 
					match(input,45,FOLLOW_45_in_sensor_value_expression546); 
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
					match(input,47,FOLLOW_47_in_sensor_value_expression564); 
					entity=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression568); 
					match(input,45,FOLLOW_45_in_sensor_value_expression570); 
					pushFollow(FOLLOW_value_path_in_sensor_value_expression574);
					path=value_path();
					state._fsp--;

					match(input,46,FOLLOW_46_in_sensor_value_expression576); 
					pushFollow(FOLLOW_configuration_options_in_sensor_value_expression580);
					config=configuration_options();
					state._fsp--;

					value_expression = new SensorValueExpression(location.getText(), entity.getText(), path /*.value_path */, config /*.configuration */, HistoryReductionMode.ANY, 0,null);
					}
					break;
				case 3 :
					// SwanExpression.g:159:4: location= ID '@' entity= ID ':' path= value_path '$' http_config= http_configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}'
					{
					location=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression592); 
					match(input,47,FOLLOW_47_in_sensor_value_expression594); 
					entity=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression598); 
					match(input,45,FOLLOW_45_in_sensor_value_expression600); 
					pushFollow(FOLLOW_value_path_in_sensor_value_expression604);
					path=value_path();
					state._fsp--;

					match(input,40,FOLLOW_40_in_sensor_value_expression606); 
					pushFollow(FOLLOW_http_configuration_options_in_sensor_value_expression610);
					http_config=http_configuration_options();
					state._fsp--;

					match(input,48,FOLLOW_48_in_sensor_value_expression612); 
					// SwanExpression.g:159:97: ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value )
					int alt12=3;
					switch ( input.LA(1) ) {
					case ALL:
						{
						int LA12_1 = input.LA(2);
						if ( (LA12_1==43) ) {
							alt12=1;
						}
						else if ( (LA12_1==49) ) {
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
						if ( (LA12_2==43) ) {
							alt12=1;
						}
						else if ( (LA12_2==49) ) {
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
						if ( (LA12_3==43) ) {
							alt12=1;
						}
						else if ( (LA12_3==49) ) {
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
						if ( (LA12_4==43) ) {
							alt12=1;
						}
						else if ( (LA12_4==49) ) {
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
						if ( (LA12_5==43) ) {
							alt12=1;
						}
						else if ( (LA12_5==49) ) {
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
						if ( (LA12_6==43) ) {
							alt12=1;
						}
						else if ( (LA12_6==49) ) {
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
							// SwanExpression.g:159:98: (mode= history_mode ',' time= time_value )
							{
							// SwanExpression.g:159:98: (mode= history_mode ',' time= time_value )
							// SwanExpression.g:159:99: mode= history_mode ',' time= time_value
							{
							pushFollow(FOLLOW_history_mode_in_sensor_value_expression618);
							mode=history_mode();
							state._fsp--;

							match(input,43,FOLLOW_43_in_sensor_value_expression620); 
							pushFollow(FOLLOW_time_value_in_sensor_value_expression624);
							time=time_value();
							state._fsp--;

							}

							}
							break;
						case 2 :
							// SwanExpression.g:159:140: mode= history_mode
							{
							pushFollow(FOLLOW_history_mode_in_sensor_value_expression631);
							mode=history_mode();
							state._fsp--;

							}
							break;
						case 3 :
							// SwanExpression.g:159:160: time= time_value
							{
							pushFollow(FOLLOW_time_value_in_sensor_value_expression637);
							time=time_value();
							state._fsp--;

							}
							break;

					}

					match(input,49,FOLLOW_49_in_sensor_value_expression640); 
					if (time == null) {
									value_expression = new SensorValueExpression(location.getText(), entity.getText(), path /* .value_path */, null, mode /*.history_mode */, 0,http_config);
								} else {
									value_expression = new SensorValueExpression(location.getText(), entity.getText(), path /* .value_path */, null, mode /*.history_mode */, time,http_config);
								}
					}
					break;
				case 4 :
					// SwanExpression.g:165:4: location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options '$' http_config= http_configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}'
					{
					location=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression652); 
					match(input,47,FOLLOW_47_in_sensor_value_expression654); 
					entity=(Token)match(input,ID,FOLLOW_ID_in_sensor_value_expression658); 
					match(input,45,FOLLOW_45_in_sensor_value_expression660); 
					pushFollow(FOLLOW_value_path_in_sensor_value_expression664);
					path=value_path();
					state._fsp--;

					match(input,46,FOLLOW_46_in_sensor_value_expression666); 
					pushFollow(FOLLOW_configuration_options_in_sensor_value_expression670);
					config=configuration_options();
					state._fsp--;

					match(input,40,FOLLOW_40_in_sensor_value_expression672); 
					pushFollow(FOLLOW_http_configuration_options_in_sensor_value_expression676);
					http_config=http_configuration_options();
					state._fsp--;

					match(input,48,FOLLOW_48_in_sensor_value_expression678); 
					// SwanExpression.g:165:130: ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value )
					int alt13=3;
					switch ( input.LA(1) ) {
					case ALL:
						{
						int LA13_1 = input.LA(2);
						if ( (LA13_1==43) ) {
							alt13=1;
						}
						else if ( (LA13_1==49) ) {
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
						if ( (LA13_2==43) ) {
							alt13=1;
						}
						else if ( (LA13_2==49) ) {
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
						if ( (LA13_3==43) ) {
							alt13=1;
						}
						else if ( (LA13_3==49) ) {
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
						if ( (LA13_4==43) ) {
							alt13=1;
						}
						else if ( (LA13_4==49) ) {
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
						if ( (LA13_5==43) ) {
							alt13=1;
						}
						else if ( (LA13_5==49) ) {
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
						if ( (LA13_6==43) ) {
							alt13=1;
						}
						else if ( (LA13_6==49) ) {
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
							// SwanExpression.g:165:131: (mode= history_mode ',' time= time_value )
							{
							// SwanExpression.g:165:131: (mode= history_mode ',' time= time_value )
							// SwanExpression.g:165:132: mode= history_mode ',' time= time_value
							{
							pushFollow(FOLLOW_history_mode_in_sensor_value_expression684);
							mode=history_mode();
							state._fsp--;

							match(input,43,FOLLOW_43_in_sensor_value_expression686); 
							pushFollow(FOLLOW_time_value_in_sensor_value_expression690);
							time=time_value();
							state._fsp--;

							}

							}
							break;
						case 2 :
							// SwanExpression.g:165:173: mode= history_mode
							{
							pushFollow(FOLLOW_history_mode_in_sensor_value_expression697);
							mode=history_mode();
							state._fsp--;

							}
							break;
						case 3 :
							// SwanExpression.g:165:193: time= time_value
							{
							pushFollow(FOLLOW_time_value_in_sensor_value_expression703);
							time=time_value();
							state._fsp--;

							}
							break;

					}

					match(input,49,FOLLOW_49_in_sensor_value_expression706); 
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
	// SwanExpression.g:173:1: constant_value_expression returns [ConstantValueExpression value_expression] : (i= INT |f= FLOAT |raw= STRING );
	public final ConstantValueExpression constant_value_expression() throws RecognitionException {
		ConstantValueExpression value_expression = null;


		Token i=null;
		Token f=null;
		Token raw=null;

		try {
			// SwanExpression.g:174:2: (i= INT |f= FLOAT |raw= STRING )
			int alt15=3;
			switch ( input.LA(1) ) {
			case INT:
				{
				alt15=1;
				}
				break;
			case FLOAT:
				{
				alt15=2;
				}
				break;
			case STRING:
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
					// SwanExpression.g:174:4: i= INT
					{
					i=(Token)match(input,INT,FOLLOW_INT_in_constant_value_expression728); 
					value_expression = new ConstantValueExpression(Long.parseLong(i.getText()));
					}
					break;
				case 2 :
					// SwanExpression.g:176:4: f= FLOAT
					{
					f=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_constant_value_expression741); 
					value_expression = new ConstantValueExpression(Double.parseDouble(f.getText()));
					}
					break;
				case 3 :
					// SwanExpression.g:178:5: raw= STRING
					{
					raw=(Token)match(input,STRING,FOLLOW_STRING_in_constant_value_expression755); 
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
	// SwanExpression.g:182:1: value_expression returns [ValueExpression value_expression] : (constant= constant_value_expression |sensor= sensor_value_expression );
	public final ValueExpression value_expression() throws RecognitionException {
		ValueExpression value_expression = null;


		ConstantValueExpression constant =null;
		SensorValueExpression sensor =null;

		try {
			// SwanExpression.g:183:2: (constant= constant_value_expression |sensor= sensor_value_expression )
			int alt16=2;
			int LA16_0 = input.LA(1);
			if ( (LA16_0==FLOAT||LA16_0==INT||LA16_0==STRING) ) {
				alt16=1;
			}
			else if ( (LA16_0==ID) ) {
				alt16=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 16, 0, input);
				throw nvae;
			}

			switch (alt16) {
				case 1 :
					// SwanExpression.g:183:4: constant= constant_value_expression
					{
					pushFollow(FOLLOW_constant_value_expression_in_value_expression778);
					constant=constant_value_expression();
					state._fsp--;

					value_expression = constant /* value expression */ ;
					}
					break;
				case 2 :
					// SwanExpression.g:185:5: sensor= sensor_value_expression
					{
					pushFollow(FOLLOW_sensor_value_expression_in_value_expression792);
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
	// SwanExpression.g:189:1: parentheticalExpression returns [Expression expression] : (val= value_expression | '(' ( WS )* exp= orExpression ( WS )* ')' );
	public final Expression parentheticalExpression() throws RecognitionException {
		Expression expression = null;


		ValueExpression val =null;
		Expression exp =null;

		try {
			// SwanExpression.g:190:5: (val= value_expression | '(' ( WS )* exp= orExpression ( WS )* ')' )
			int alt19=2;
			int LA19_0 = input.LA(1);
			if ( (LA19_0==FLOAT||(LA19_0 >= ID && LA19_0 <= INT)||LA19_0==STRING) ) {
				alt19=1;
			}
			else if ( (LA19_0==41) ) {
				alt19=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 19, 0, input);
				throw nvae;
			}

			switch (alt19) {
				case 1 :
					// SwanExpression.g:190:9: val= value_expression
					{
					pushFollow(FOLLOW_value_expression_in_parentheticalExpression821);
					val=value_expression();
					state._fsp--;

					expression = val;
					}
					break;
				case 2 :
					// SwanExpression.g:192:9: '(' ( WS )* exp= orExpression ( WS )* ')'
					{
					match(input,41,FOLLOW_41_in_parentheticalExpression846); 
					// SwanExpression.g:192:13: ( WS )*
					loop17:
					while (true) {
						int alt17=2;
						int LA17_0 = input.LA(1);
						if ( (LA17_0==WS) ) {
							alt17=1;
						}

						switch (alt17) {
						case 1 :
							// SwanExpression.g:192:13: WS
							{
							match(input,WS,FOLLOW_WS_in_parentheticalExpression848); 
							}
							break;

						default :
							break loop17;
						}
					}

					pushFollow(FOLLOW_orExpression_in_parentheticalExpression853);
					exp=orExpression();
					state._fsp--;

					// SwanExpression.g:192:34: ( WS )*
					loop18:
					while (true) {
						int alt18=2;
						int LA18_0 = input.LA(1);
						if ( (LA18_0==WS) ) {
							alt18=1;
						}

						switch (alt18) {
						case 1 :
							// SwanExpression.g:192:34: WS
							{
							match(input,WS,FOLLOW_WS_in_parentheticalExpression855); 
							}
							break;

						default :
							break loop18;
						}
					}

					match(input,42,FOLLOW_42_in_parentheticalExpression858); 
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
	// SwanExpression.g:196:1: multiplicativeExpression returns [Expression expression] : left= parentheticalExpression ( ( WS )* (location= ID '@' )? op= multiplicative_math_operator ( WS )* right= parentheticalExpression )* ;
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
			// SwanExpression.g:202:5: (left= parentheticalExpression ( ( WS )* (location= ID '@' )? op= multiplicative_math_operator ( WS )* right= parentheticalExpression )* )
			// SwanExpression.g:202:7: left= parentheticalExpression ( ( WS )* (location= ID '@' )? op= multiplicative_math_operator ( WS )* right= parentheticalExpression )*
			{
			pushFollow(FOLLOW_parentheticalExpression_in_multiplicativeExpression905);
			left=parentheticalExpression();
			state._fsp--;

			// SwanExpression.g:203:5: ( ( WS )* (location= ID '@' )? op= multiplicative_math_operator ( WS )* right= parentheticalExpression )*
			loop23:
			while (true) {
				int alt23=2;
				alt23 = dfa23.predict(input);
				switch (alt23) {
				case 1 :
					// SwanExpression.g:203:6: ( WS )* (location= ID '@' )? op= multiplicative_math_operator ( WS )* right= parentheticalExpression
					{
					// SwanExpression.g:203:6: ( WS )*
					loop20:
					while (true) {
						int alt20=2;
						int LA20_0 = input.LA(1);
						if ( (LA20_0==WS) ) {
							alt20=1;
						}

						switch (alt20) {
						case 1 :
							// SwanExpression.g:203:6: WS
							{
							match(input,WS,FOLLOW_WS_in_multiplicativeExpression912); 
							}
							break;

						default :
							break loop20;
						}
					}

					// SwanExpression.g:203:10: (location= ID '@' )?
					int alt21=2;
					int LA21_0 = input.LA(1);
					if ( (LA21_0==ID) ) {
						alt21=1;
					}
					switch (alt21) {
						case 1 :
							// SwanExpression.g:203:11: location= ID '@'
							{
							location=(Token)match(input,ID,FOLLOW_ID_in_multiplicativeExpression918); 
							match(input,47,FOLLOW_47_in_multiplicativeExpression920); 
							}
							break;

					}

					pushFollow(FOLLOW_multiplicative_math_operator_in_multiplicativeExpression926);
					op=multiplicative_math_operator();
					state._fsp--;

					// SwanExpression.g:203:61: ( WS )*
					loop22:
					while (true) {
						int alt22=2;
						int LA22_0 = input.LA(1);
						if ( (LA22_0==WS) ) {
							alt22=1;
						}

						switch (alt22) {
						case 1 :
							// SwanExpression.g:203:61: WS
							{
							match(input,WS,FOLLOW_WS_in_multiplicativeExpression928); 
							}
							break;

						default :
							break loop22;
						}
					}

					pushFollow(FOLLOW_parentheticalExpression_in_multiplicativeExpression933);
					right=parentheticalExpression();
					state._fsp--;

					locationStack.push(location == null ? Expression.LOCATION_INFER : location.getText()); opStack.push(op /* .math_operator */ ); rightStack.push((ValueExpression) right /* .expression */ );
					}
					break;

				default :
					break loop23;
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
	// SwanExpression.g:219:1: additiveExpression returns [Expression expression] : left= multiplicativeExpression ( (location= ID '@' )? op= additive_math_operator right= multiplicativeExpression )* ;
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
			// SwanExpression.g:225:5: (left= multiplicativeExpression ( (location= ID '@' )? op= additive_math_operator right= multiplicativeExpression )* )
			// SwanExpression.g:225:7: left= multiplicativeExpression ( (location= ID '@' )? op= additive_math_operator right= multiplicativeExpression )*
			{
			pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression981);
			left=multiplicativeExpression();
			state._fsp--;

			// SwanExpression.g:226:5: ( (location= ID '@' )? op= additive_math_operator right= multiplicativeExpression )*
			loop25:
			while (true) {
				int alt25=2;
				int LA25_0 = input.LA(1);
				if ( (LA25_0==ID) ) {
					int LA25_2 = input.LA(2);
					if ( (LA25_2==47) ) {
						int LA25_4 = input.LA(3);
						if ( (LA25_4==MINUS||LA25_4==PLUS) ) {
							alt25=1;
						}

					}

				}
				else if ( (LA25_0==MINUS||LA25_0==PLUS) ) {
					alt25=1;
				}

				switch (alt25) {
				case 1 :
					// SwanExpression.g:226:6: (location= ID '@' )? op= additive_math_operator right= multiplicativeExpression
					{
					// SwanExpression.g:226:6: (location= ID '@' )?
					int alt24=2;
					int LA24_0 = input.LA(1);
					if ( (LA24_0==ID) ) {
						alt24=1;
					}
					switch (alt24) {
						case 1 :
							// SwanExpression.g:226:7: location= ID '@'
							{
							location=(Token)match(input,ID,FOLLOW_ID_in_additiveExpression991); 
							match(input,47,FOLLOW_47_in_additiveExpression993); 
							}
							break;

					}

					pushFollow(FOLLOW_additive_math_operator_in_additiveExpression999);
					op=additive_math_operator();
					state._fsp--;

					pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression1003);
					right=multiplicativeExpression();
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
	// $ANTLR end "additiveExpression"



	// $ANTLR start "comparativeExpression"
	// SwanExpression.g:242:1: comparativeExpression returns [Expression expression] : left= additiveExpression ( ( WS )* ( (location= ID )? c= comparator ) ( WS )* right= additiveExpression )? ;
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
			// SwanExpression.g:248:5: (left= additiveExpression ( ( WS )* ( (location= ID )? c= comparator ) ( WS )* right= additiveExpression )? )
			// SwanExpression.g:248:7: left= additiveExpression ( ( WS )* ( (location= ID )? c= comparator ) ( WS )* right= additiveExpression )?
			{
			pushFollow(FOLLOW_additiveExpression_in_comparativeExpression1051);
			left=additiveExpression();
			state._fsp--;

			// SwanExpression.g:249:5: ( ( WS )* ( (location= ID )? c= comparator ) ( WS )* right= additiveExpression )?
			int alt29=2;
			alt29 = dfa29.predict(input);
			switch (alt29) {
				case 1 :
					// SwanExpression.g:249:6: ( WS )* ( (location= ID )? c= comparator ) ( WS )* right= additiveExpression
					{
					// SwanExpression.g:249:6: ( WS )*
					loop26:
					while (true) {
						int alt26=2;
						int LA26_0 = input.LA(1);
						if ( (LA26_0==WS) ) {
							alt26=1;
						}

						switch (alt26) {
						case 1 :
							// SwanExpression.g:249:6: WS
							{
							match(input,WS,FOLLOW_WS_in_comparativeExpression1058); 
							}
							break;

						default :
							break loop26;
						}
					}

					// SwanExpression.g:249:10: ( (location= ID )? c= comparator )
					// SwanExpression.g:249:11: (location= ID )? c= comparator
					{
					// SwanExpression.g:249:11: (location= ID )?
					int alt27=2;
					int LA27_0 = input.LA(1);
					if ( (LA27_0==ID) ) {
						alt27=1;
					}
					switch (alt27) {
						case 1 :
							// SwanExpression.g:249:12: location= ID
							{
							location=(Token)match(input,ID,FOLLOW_ID_in_comparativeExpression1065); 
							}
							break;

					}

					pushFollow(FOLLOW_comparator_in_comparativeExpression1071);
					c=comparator();
					state._fsp--;

					}

					// SwanExpression.g:249:40: ( WS )*
					loop28:
					while (true) {
						int alt28=2;
						int LA28_0 = input.LA(1);
						if ( (LA28_0==WS) ) {
							alt28=1;
						}

						switch (alt28) {
						case 1 :
							// SwanExpression.g:249:40: WS
							{
							match(input,WS,FOLLOW_WS_in_comparativeExpression1074); 
							}
							break;

						default :
							break loop28;
						}
					}

					pushFollow(FOLLOW_additiveExpression_in_comparativeExpression1079);
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
	// SwanExpression.g:265:1: unaryExpression returns [Expression expression] : ( (location= ID )? NOT exp= comparativeExpression |exp= comparativeExpression );
	public final Expression unaryExpression() throws RecognitionException {
		Expression expression = null;


		Token location=null;
		Expression exp =null;

		try {
			// SwanExpression.g:266:5: ( (location= ID )? NOT exp= comparativeExpression |exp= comparativeExpression )
			int alt31=2;
			switch ( input.LA(1) ) {
			case ID:
				{
				int LA31_1 = input.LA(2);
				if ( (LA31_1==47) ) {
					alt31=2;
				}
				else if ( (LA31_1==NOT) ) {
					alt31=1;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 31, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case NOT:
				{
				alt31=1;
				}
				break;
			case FLOAT:
			case INT:
			case STRING:
			case 41:
				{
				alt31=2;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 31, 0, input);
				throw nvae;
			}
			switch (alt31) {
				case 1 :
					// SwanExpression.g:266:7: (location= ID )? NOT exp= comparativeExpression
					{
					// SwanExpression.g:266:7: (location= ID )?
					int alt30=2;
					int LA30_0 = input.LA(1);
					if ( (LA30_0==ID) ) {
						alt30=1;
					}
					switch (alt30) {
						case 1 :
							// SwanExpression.g:266:8: location= ID
							{
							location=(Token)match(input,ID,FOLLOW_ID_in_unaryExpression1123); 
							}
							break;

					}

					match(input,NOT,FOLLOW_NOT_in_unaryExpression1127); 
					pushFollow(FOLLOW_comparativeExpression_in_unaryExpression1131);
					exp=comparativeExpression();
					state._fsp--;

					expression = new LogicExpression(location == null ? Expression.LOCATION_INFER : location.getText(), UnaryLogicOperator.NOT /* .logic_operator */ , (TriStateExpression) exp /* .expression */ );
					}
					break;
				case 2 :
					// SwanExpression.g:268:7: exp= comparativeExpression
					{
					pushFollow(FOLLOW_comparativeExpression_in_unaryExpression1152);
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
	// SwanExpression.g:272:1: andExpression returns [Expression expression] : left= unaryExpression ( (location= ID '@' )? AND right= unaryExpression )* ;
	public final Expression andExpression() throws RecognitionException {
		Expression expression = null;


		Token location=null;
		Expression left =null;
		Expression right =null;


		    Stack<Expression> rightStack = new Stack<Expression>();
		    Stack<String> locationStack = new Stack<String>();

		try {
			// SwanExpression.g:277:5: (left= unaryExpression ( (location= ID '@' )? AND right= unaryExpression )* )
			// SwanExpression.g:277:7: left= unaryExpression ( (location= ID '@' )? AND right= unaryExpression )*
			{
			pushFollow(FOLLOW_unaryExpression_in_andExpression1191);
			left=unaryExpression();
			state._fsp--;

			// SwanExpression.g:278:5: ( (location= ID '@' )? AND right= unaryExpression )*
			loop33:
			while (true) {
				int alt33=2;
				int LA33_0 = input.LA(1);
				if ( (LA33_0==ID) ) {
					int LA33_1 = input.LA(2);
					if ( (LA33_1==47) ) {
						int LA33_4 = input.LA(3);
						if ( (LA33_4==AND) ) {
							alt33=1;
						}

					}

				}
				else if ( (LA33_0==AND) ) {
					alt33=1;
				}

				switch (alt33) {
				case 1 :
					// SwanExpression.g:278:6: (location= ID '@' )? AND right= unaryExpression
					{
					// SwanExpression.g:278:6: (location= ID '@' )?
					int alt32=2;
					int LA32_0 = input.LA(1);
					if ( (LA32_0==ID) ) {
						alt32=1;
					}
					switch (alt32) {
						case 1 :
							// SwanExpression.g:278:7: location= ID '@'
							{
							location=(Token)match(input,ID,FOLLOW_ID_in_andExpression1201); 
							match(input,47,FOLLOW_47_in_andExpression1203); 
							}
							break;

					}

					match(input,AND,FOLLOW_AND_in_andExpression1207); 
					pushFollow(FOLLOW_unaryExpression_in_andExpression1211);
					right=unaryExpression();
					state._fsp--;

					locationStack.push(location == null ? Expression.LOCATION_INFER : location.getText()); rightStack.push(right /* .expression */ );
					}
					break;

				default :
					break loop33;
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
	// SwanExpression.g:294:1: orExpression returns [Expression expression] : left= andExpression ( (location= ID '@' )? OR right= andExpression )* ;
	public final Expression orExpression() throws RecognitionException {
		Expression expression = null;


		Token location=null;
		Expression left =null;
		Expression right =null;


		    Stack<Expression> rightStack = new Stack<Expression>();
		    Stack<String> locationStack = new Stack<String>();

		try {
			// SwanExpression.g:299:5: (left= andExpression ( (location= ID '@' )? OR right= andExpression )* )
			// SwanExpression.g:299:7: left= andExpression ( (location= ID '@' )? OR right= andExpression )*
			{
			pushFollow(FOLLOW_andExpression_in_orExpression1259);
			left=andExpression();
			state._fsp--;

			// SwanExpression.g:300:5: ( (location= ID '@' )? OR right= andExpression )*
			loop35:
			while (true) {
				int alt35=2;
				int LA35_0 = input.LA(1);
				if ( (LA35_0==ID||LA35_0==OR) ) {
					alt35=1;
				}

				switch (alt35) {
				case 1 :
					// SwanExpression.g:300:6: (location= ID '@' )? OR right= andExpression
					{
					// SwanExpression.g:300:6: (location= ID '@' )?
					int alt34=2;
					int LA34_0 = input.LA(1);
					if ( (LA34_0==ID) ) {
						alt34=1;
					}
					switch (alt34) {
						case 1 :
							// SwanExpression.g:300:7: location= ID '@'
							{
							location=(Token)match(input,ID,FOLLOW_ID_in_orExpression1269); 
							match(input,47,FOLLOW_47_in_orExpression1271); 
							}
							break;

					}

					match(input,OR,FOLLOW_OR_in_orExpression1275); 
					pushFollow(FOLLOW_andExpression_in_orExpression1279);
					right=andExpression();
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
	// SwanExpression.g:316:1: expression returns [Expression expression] : logic= orExpression EOF ;
	public final Expression expression() throws RecognitionException {
		Expression expression = null;


		Expression logic =null;

		try {
			// SwanExpression.g:317:5: (logic= orExpression EOF )
			// SwanExpression.g:318:5: logic= orExpression EOF
			{
			pushFollow(FOLLOW_orExpression_in_expression1329);
			logic=orExpression();
			state._fsp--;

			match(input,EOF,FOLLOW_EOF_in_expression1331); 
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
	// SwanExpression.g:323:1: time_value returns [Long time] : val= INT (unit= TIME_UNIT )? (rep_val= INT (rep_unit= TIME_UNIT )? )* ;
	public final Long time_value() throws RecognitionException {
		Long time = null;


		Token val=null;
		Token unit=null;
		Token rep_val=null;
		Token rep_unit=null;

		try {
			// SwanExpression.g:324:5: (val= INT (unit= TIME_UNIT )? (rep_val= INT (rep_unit= TIME_UNIT )? )* )
			// SwanExpression.g:325:5: val= INT (unit= TIME_UNIT )? (rep_val= INT (rep_unit= TIME_UNIT )? )*
			{
			val=(Token)match(input,INT,FOLLOW_INT_in_time_value1369); 
			// SwanExpression.g:325:17: (unit= TIME_UNIT )?
			int alt36=2;
			int LA36_0 = input.LA(1);
			if ( (LA36_0==TIME_UNIT) ) {
				alt36=1;
			}
			switch (alt36) {
				case 1 :
					// SwanExpression.g:325:17: unit= TIME_UNIT
					{
					unit=(Token)match(input,TIME_UNIT,FOLLOW_TIME_UNIT_in_time_value1373); 
					}
					break;

			}

			 long theTime = convertTime(val, unit); 
			// SwanExpression.g:327:5: (rep_val= INT (rep_unit= TIME_UNIT )? )*
			loop38:
			while (true) {
				int alt38=2;
				int LA38_0 = input.LA(1);
				if ( (LA38_0==INT) ) {
					alt38=1;
				}

				switch (alt38) {
				case 1 :
					// SwanExpression.g:327:6: rep_val= INT (rep_unit= TIME_UNIT )?
					{
					rep_val=(Token)match(input,INT,FOLLOW_INT_in_time_value1390); 
					// SwanExpression.g:327:26: (rep_unit= TIME_UNIT )?
					int alt37=2;
					int LA37_0 = input.LA(1);
					if ( (LA37_0==TIME_UNIT) ) {
						alt37=1;
					}
					switch (alt37) {
						case 1 :
							// SwanExpression.g:327:26: rep_unit= TIME_UNIT
							{
							rep_unit=(Token)match(input,TIME_UNIT,FOLLOW_TIME_UNIT_in_time_value1394); 
							}
							break;

					}

					 theTime += convertTime(rep_val, rep_unit);
					}
					break;

				default :
					break loop38;
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


	protected DFA14 dfa14 = new DFA14(this);
	protected DFA23 dfa23 = new DFA23(this);
	protected DFA29 dfa29 = new DFA29(this);
	static final String DFA14_eotS =
		"\24\uffff";
	static final String DFA14_eofS =
		"\5\uffff\2\10\4\uffff\2\10\1\uffff\1\20\4\uffff\1\20";
	static final String DFA14_minS =
		"\1\24\1\57\1\24\1\55\1\24\2\5\1\24\1\uffff\1\24\1\uffff\2\5\1\12\1\5\1"+
		"\24\2\uffff\1\12\1\5";
	static final String DFA14_maxS =
		"\1\24\1\57\1\24\1\55\1\45\2\56\1\45\1\uffff\1\24\1\uffff\2\56\1\12\1\52"+
		"\1\24\2\uffff\1\12\1\52";
	static final String DFA14_acceptS =
		"\10\uffff\1\1\1\uffff\1\3\5\uffff\1\2\1\4\2\uffff";
	static final String DFA14_specialS =
		"\24\uffff}>";
	static final String[] DFA14_transitionS = {
			"\1\1",
			"\1\2",
			"\1\3",
			"\1\4",
			"\1\5\20\uffff\1\6",
			"\1\10\5\uffff\3\10\3\uffff\2\10\1\uffff\1\10\1\uffff\2\10\4\uffff\3"+
			"\10\2\uffff\4\10\2\uffff\1\10\1\12\1\uffff\1\10\1\uffff\1\7\1\uffff\1"+
			"\11",
			"\1\10\5\uffff\3\10\3\uffff\2\10\1\uffff\1\10\1\uffff\2\10\4\uffff\3"+
			"\10\2\uffff\4\10\2\uffff\1\10\1\12\1\uffff\1\10\1\uffff\1\7\1\uffff\1"+
			"\11",
			"\1\13\20\uffff\1\14",
			"",
			"\1\15",
			"",
			"\1\10\5\uffff\3\10\3\uffff\2\10\1\uffff\1\10\1\uffff\2\10\4\uffff\3"+
			"\10\2\uffff\4\10\2\uffff\1\10\1\12\1\uffff\1\10\1\uffff\1\7\1\uffff\1"+
			"\11",
			"\1\10\5\uffff\3\10\3\uffff\2\10\1\uffff\1\10\1\uffff\2\10\4\uffff\3"+
			"\10\2\uffff\4\10\2\uffff\1\10\1\12\1\uffff\1\10\1\uffff\1\7\1\uffff\1"+
			"\11",
			"\1\16",
			"\1\20\1\uffff\1\17\3\uffff\3\20\3\uffff\2\20\1\uffff\1\20\1\uffff\2"+
			"\20\4\uffff\3\20\2\uffff\4\20\2\uffff\1\20\1\21\1\uffff\1\20",
			"\1\22",
			"",
			"",
			"\1\23",
			"\1\20\1\uffff\1\17\3\uffff\3\20\3\uffff\2\20\1\uffff\1\20\1\uffff\2"+
			"\20\4\uffff\3\20\2\uffff\4\20\2\uffff\1\20\1\21\1\uffff\1\20"
	};

	static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
	static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
	static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
	static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
	static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
	static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
	static final short[][] DFA14_transition;

	static {
		int numStates = DFA14_transitionS.length;
		DFA14_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
		}
	}

	protected class DFA14 extends DFA {

		public DFA14(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 14;
			this.eot = DFA14_eot;
			this.eof = DFA14_eof;
			this.min = DFA14_min;
			this.max = DFA14_max;
			this.accept = DFA14_accept;
			this.special = DFA14_special;
			this.transition = DFA14_transition;
		}
		@Override
		public String getDescription() {
			return "154:1: sensor_value_expression returns [SensorValueExpression value_expression] : (location= ID '@' entity= ID ':' path= value_path |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options |location= ID '@' entity= ID ':' path= value_path '$' http_config= http_configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' |location= ID '@' entity= ID ':' path= value_path '?' config= configuration_options '$' http_config= http_configuration_options '{' ( (mode= history_mode ',' time= time_value ) |mode= history_mode |time= time_value ) '}' );";
		}
	}

	static final String DFA23_eotS =
		"\7\uffff";
	static final String DFA23_eofS =
		"\1\2\6\uffff";
	static final String DFA23_minS =
		"\1\5\1\13\1\uffff\1\13\1\uffff\1\5\1\13";
	static final String DFA23_maxS =
		"\1\52\1\57\1\uffff\1\52\1\uffff\1\43\1\57";
	static final String DFA23_acceptS =
		"\2\uffff\1\2\1\uffff\1\1\2\uffff";
	static final String DFA23_specialS =
		"\7\uffff}>";
	static final String[] DFA23_transitionS = {
			"\1\2\5\uffff\1\2\1\4\1\2\3\uffff\2\2\1\uffff\1\1\1\uffff\2\2\4\uffff"+
			"\1\2\2\4\2\uffff\4\2\2\uffff\1\3\2\uffff\1\2",
			"\1\2\1\uffff\1\2\3\uffff\2\2\3\uffff\2\2\11\uffff\1\2\2\uffff\1\2\12"+
			"\uffff\1\5",
			"",
			"\1\2\1\4\1\2\3\uffff\2\2\1\uffff\1\6\1\uffff\2\2\5\uffff\2\4\2\uffff"+
			"\1\2\2\uffff\1\2\2\uffff\1\3\2\uffff\1\2",
			"",
			"\1\2\6\uffff\1\4\17\uffff\1\2\2\4\3\uffff\2\2",
			"\1\2\1\uffff\1\2\3\uffff\2\2\3\uffff\2\2\11\uffff\1\2\2\uffff\1\2\12"+
			"\uffff\1\4"
	};

	static final short[] DFA23_eot = DFA.unpackEncodedString(DFA23_eotS);
	static final short[] DFA23_eof = DFA.unpackEncodedString(DFA23_eofS);
	static final char[] DFA23_min = DFA.unpackEncodedStringToUnsignedChars(DFA23_minS);
	static final char[] DFA23_max = DFA.unpackEncodedStringToUnsignedChars(DFA23_maxS);
	static final short[] DFA23_accept = DFA.unpackEncodedString(DFA23_acceptS);
	static final short[] DFA23_special = DFA.unpackEncodedString(DFA23_specialS);
	static final short[][] DFA23_transition;

	static {
		int numStates = DFA23_transitionS.length;
		DFA23_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA23_transition[i] = DFA.unpackEncodedString(DFA23_transitionS[i]);
		}
	}

	protected class DFA23 extends DFA {

		public DFA23(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 23;
			this.eot = DFA23_eot;
			this.eof = DFA23_eof;
			this.min = DFA23_min;
			this.max = DFA23_max;
			this.accept = DFA23_accept;
			this.special = DFA23_special;
			this.transition = DFA23_transition;
		}
		@Override
		public String getDescription() {
			return "()* loopback of 203:5: ( ( WS )* (location= ID '@' )? op= multiplicative_math_operator ( WS )* right= parentheticalExpression )*";
		}
	}

	static final String DFA29_eotS =
		"\5\uffff";
	static final String DFA29_eofS =
		"\1\4\4\uffff";
	static final String DFA29_minS =
		"\1\5\2\13\2\uffff";
	static final String DFA29_maxS =
		"\2\52\1\57\2\uffff";
	static final String DFA29_acceptS =
		"\3\uffff\1\1\1\2";
	static final String DFA29_specialS =
		"\5\uffff}>";
	static final String[] DFA29_transitionS = {
			"\1\4\5\uffff\1\3\1\uffff\1\3\3\uffff\2\3\1\uffff\1\2\1\uffff\2\3\11\uffff"+
			"\1\3\1\4\1\uffff\1\3\2\uffff\1\1\2\uffff\1\4",
			"\1\3\1\uffff\1\3\3\uffff\2\3\1\uffff\1\3\1\uffff\2\3\11\uffff\1\3\2"+
			"\uffff\1\3\2\uffff\1\1\2\uffff\1\4",
			"\1\3\1\uffff\1\3\3\uffff\2\3\3\uffff\2\3\11\uffff\1\3\2\uffff\1\3\12"+
			"\uffff\1\4",
			"",
			""
	};

	static final short[] DFA29_eot = DFA.unpackEncodedString(DFA29_eotS);
	static final short[] DFA29_eof = DFA.unpackEncodedString(DFA29_eofS);
	static final char[] DFA29_min = DFA.unpackEncodedStringToUnsignedChars(DFA29_minS);
	static final char[] DFA29_max = DFA.unpackEncodedStringToUnsignedChars(DFA29_maxS);
	static final short[] DFA29_accept = DFA.unpackEncodedString(DFA29_acceptS);
	static final short[] DFA29_special = DFA.unpackEncodedString(DFA29_specialS);
	static final short[][] DFA29_transition;

	static {
		int numStates = DFA29_transitionS.length;
		DFA29_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA29_transition[i] = DFA.unpackEncodedString(DFA29_transitionS[i]);
		}
	}

	protected class DFA29 extends DFA {

		public DFA29(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 29;
			this.eot = DFA29_eot;
			this.eof = DFA29_eof;
			this.min = DFA29_min;
			this.max = DFA29_max;
			this.accept = DFA29_accept;
			this.special = DFA29_special;
			this.transition = DFA29_transition;
		}
		@Override
		public String getDescription() {
			return "249:5: ( ( WS )* ( (location= ID )? c= comparator ) ( WS )* right= additiveExpression )?";
		}
	}

	public static final BitSet FOLLOW_ID_in_http_configuration_options62 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_CONFIG_VAL_in_http_configuration_options66 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_CONFIG_TILT_in_http_configuration_options76 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_ID_in_http_configuration_options80 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_CONFIG_VAL_in_http_configuration_options84 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_ID_in_configuration_options123 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_CONFIG_VAL_in_configuration_options127 = new BitSet(new long[]{0x0000000000000082L});
	public static final BitSet FOLLOW_CONFIG_HASH_in_configuration_options137 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_ID_in_configuration_options141 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_CONFIG_VAL_in_configuration_options145 = new BitSet(new long[]{0x0000000000000082L});
	public static final BitSet FOLLOW_v_p_in_value_path181 = new BitSet(new long[]{0x0000100000000002L});
	public static final BitSet FOLLOW_44_in_value_path190 = new BitSet(new long[]{0x0000002000100000L});
	public static final BitSet FOLLOW_v_p_in_value_path194 = new BitSet(new long[]{0x0000100000000002L});
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
	public static final BitSet FOLLOW_ID_in_sensor_value_expression538 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_47_in_sensor_value_expression540 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_ID_in_sensor_value_expression544 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_45_in_sensor_value_expression546 = new BitSet(new long[]{0x0000002000100000L});
	public static final BitSet FOLLOW_value_path_in_sensor_value_expression550 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_sensor_value_expression562 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_47_in_sensor_value_expression564 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_ID_in_sensor_value_expression568 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_45_in_sensor_value_expression570 = new BitSet(new long[]{0x0000002000100000L});
	public static final BitSet FOLLOW_value_path_in_sensor_value_expression574 = new BitSet(new long[]{0x0000400000000000L});
	public static final BitSet FOLLOW_46_in_sensor_value_expression576 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_configuration_options_in_sensor_value_expression580 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_sensor_value_expression592 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_47_in_sensor_value_expression594 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_ID_in_sensor_value_expression598 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_45_in_sensor_value_expression600 = new BitSet(new long[]{0x0000002000100000L});
	public static final BitSet FOLLOW_value_path_in_sensor_value_expression604 = new BitSet(new long[]{0x0000010000000000L});
	public static final BitSet FOLLOW_40_in_sensor_value_expression606 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_http_configuration_options_in_sensor_value_expression610 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_48_in_sensor_value_expression612 = new BitSet(new long[]{0x000000000F200050L});
	public static final BitSet FOLLOW_history_mode_in_sensor_value_expression618 = new BitSet(new long[]{0x0000080000000000L});
	public static final BitSet FOLLOW_43_in_sensor_value_expression620 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_time_value_in_sensor_value_expression624 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_history_mode_in_sensor_value_expression631 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_time_value_in_sensor_value_expression637 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_49_in_sensor_value_expression640 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_sensor_value_expression652 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_47_in_sensor_value_expression654 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_ID_in_sensor_value_expression658 = new BitSet(new long[]{0x0000200000000000L});
	public static final BitSet FOLLOW_45_in_sensor_value_expression660 = new BitSet(new long[]{0x0000002000100000L});
	public static final BitSet FOLLOW_value_path_in_sensor_value_expression664 = new BitSet(new long[]{0x0000400000000000L});
	public static final BitSet FOLLOW_46_in_sensor_value_expression666 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_configuration_options_in_sensor_value_expression670 = new BitSet(new long[]{0x0000010000000000L});
	public static final BitSet FOLLOW_40_in_sensor_value_expression672 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_http_configuration_options_in_sensor_value_expression676 = new BitSet(new long[]{0x0001000000000000L});
	public static final BitSet FOLLOW_48_in_sensor_value_expression678 = new BitSet(new long[]{0x000000000F200050L});
	public static final BitSet FOLLOW_history_mode_in_sensor_value_expression684 = new BitSet(new long[]{0x0000080000000000L});
	public static final BitSet FOLLOW_43_in_sensor_value_expression686 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_time_value_in_sensor_value_expression690 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_history_mode_in_sensor_value_expression697 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_time_value_in_sensor_value_expression703 = new BitSet(new long[]{0x0002000000000000L});
	public static final BitSet FOLLOW_49_in_sensor_value_expression706 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INT_in_constant_value_expression728 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FLOAT_in_constant_value_expression741 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_STRING_in_constant_value_expression755 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_constant_value_expression_in_value_expression778 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_sensor_value_expression_in_value_expression792 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_value_expression_in_parentheticalExpression821 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_41_in_parentheticalExpression846 = new BitSet(new long[]{0x000002A100310000L});
	public static final BitSet FOLLOW_WS_in_parentheticalExpression848 = new BitSet(new long[]{0x000002A100310000L});
	public static final BitSet FOLLOW_orExpression_in_parentheticalExpression853 = new BitSet(new long[]{0x0000048000000000L});
	public static final BitSet FOLLOW_WS_in_parentheticalExpression855 = new BitSet(new long[]{0x0000048000000000L});
	public static final BitSet FOLLOW_42_in_parentheticalExpression858 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_parentheticalExpression_in_multiplicativeExpression905 = new BitSet(new long[]{0x0000008060101002L});
	public static final BitSet FOLLOW_WS_in_multiplicativeExpression912 = new BitSet(new long[]{0x0000008060101000L});
	public static final BitSet FOLLOW_ID_in_multiplicativeExpression918 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_47_in_multiplicativeExpression920 = new BitSet(new long[]{0x0000000060001000L});
	public static final BitSet FOLLOW_multiplicative_math_operator_in_multiplicativeExpression926 = new BitSet(new long[]{0x000002A000310000L});
	public static final BitSet FOLLOW_WS_in_multiplicativeExpression928 = new BitSet(new long[]{0x000002A000310000L});
	public static final BitSet FOLLOW_parentheticalExpression_in_multiplicativeExpression933 = new BitSet(new long[]{0x0000008060101002L});
	public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression981 = new BitSet(new long[]{0x0000000810100002L});
	public static final BitSet FOLLOW_ID_in_additiveExpression991 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_47_in_additiveExpression993 = new BitSet(new long[]{0x0000000810000000L});
	public static final BitSet FOLLOW_additive_math_operator_in_additiveExpression999 = new BitSet(new long[]{0x0000022000310000L});
	public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression1003 = new BitSet(new long[]{0x0000000810100002L});
	public static final BitSet FOLLOW_additiveExpression_in_comparativeExpression1051 = new BitSet(new long[]{0x0000009200D62802L});
	public static final BitSet FOLLOW_WS_in_comparativeExpression1058 = new BitSet(new long[]{0x0000009200D62800L});
	public static final BitSet FOLLOW_ID_in_comparativeExpression1065 = new BitSet(new long[]{0x0000001200C62800L});
	public static final BitSet FOLLOW_comparator_in_comparativeExpression1071 = new BitSet(new long[]{0x000002A000310000L});
	public static final BitSet FOLLOW_WS_in_comparativeExpression1074 = new BitSet(new long[]{0x000002A000310000L});
	public static final BitSet FOLLOW_additiveExpression_in_comparativeExpression1079 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_unaryExpression1123 = new BitSet(new long[]{0x0000000100000000L});
	public static final BitSet FOLLOW_NOT_in_unaryExpression1127 = new BitSet(new long[]{0x0000022000310000L});
	public static final BitSet FOLLOW_comparativeExpression_in_unaryExpression1131 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_comparativeExpression_in_unaryExpression1152 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_unaryExpression_in_andExpression1191 = new BitSet(new long[]{0x0000000000100022L});
	public static final BitSet FOLLOW_ID_in_andExpression1201 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_47_in_andExpression1203 = new BitSet(new long[]{0x0000000000000020L});
	public static final BitSet FOLLOW_AND_in_andExpression1207 = new BitSet(new long[]{0x0000022100310000L});
	public static final BitSet FOLLOW_unaryExpression_in_andExpression1211 = new BitSet(new long[]{0x0000000000100022L});
	public static final BitSet FOLLOW_andExpression_in_orExpression1259 = new BitSet(new long[]{0x0000000400100002L});
	public static final BitSet FOLLOW_ID_in_orExpression1269 = new BitSet(new long[]{0x0000800000000000L});
	public static final BitSet FOLLOW_47_in_orExpression1271 = new BitSet(new long[]{0x0000000400000000L});
	public static final BitSet FOLLOW_OR_in_orExpression1275 = new BitSet(new long[]{0x0000022100310000L});
	public static final BitSet FOLLOW_andExpression_in_orExpression1279 = new BitSet(new long[]{0x0000000400100002L});
	public static final BitSet FOLLOW_orExpression_in_expression1329 = new BitSet(new long[]{0x0000000000000000L});
	public static final BitSet FOLLOW_EOF_in_expression1331 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INT_in_time_value1369 = new BitSet(new long[]{0x0000004000200002L});
	public static final BitSet FOLLOW_TIME_UNIT_in_time_value1373 = new BitSet(new long[]{0x0000000000200002L});
	public static final BitSet FOLLOW_INT_in_time_value1390 = new BitSet(new long[]{0x0000004000200002L});
	public static final BitSet FOLLOW_TIME_UNIT_in_time_value1394 = new BitSet(new long[]{0x0000000000200002L});
}
