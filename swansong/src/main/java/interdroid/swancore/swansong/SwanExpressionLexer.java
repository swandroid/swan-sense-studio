// $ANTLR 3.5.2 SwanExpression.g 2016-08-23 14:51:21

package interdroid.swancore.swansong;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class SwanExpressionLexer extends Lexer {
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
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public SwanExpressionLexer() {} 
	public SwanExpressionLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}
	public SwanExpressionLexer(CharStream input, RecognizerSharedState state) {
		super(input,state);
	}
	@Override public String getGrammarFileName() { return "SwanExpression.g"; }

	// $ANTLR start "T__42"
	public final void mT__42() throws RecognitionException {
		try {
			int _type = T__42;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:11:7: ( '$' )
			// SwanExpression.g:11:9: '$'
			{
			match('$'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__42"

	// $ANTLR start "T__43"
	public final void mT__43() throws RecognitionException {
		try {
			int _type = T__43;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:12:7: ( '(' )
			// SwanExpression.g:12:9: '('
			{
			match('('); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__43"

	// $ANTLR start "T__44"
	public final void mT__44() throws RecognitionException {
		try {
			int _type = T__44;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:13:7: ( ')' )
			// SwanExpression.g:13:9: ')'
			{
			match(')'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__44"

	// $ANTLR start "T__45"
	public final void mT__45() throws RecognitionException {
		try {
			int _type = T__45;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:14:7: ( ',' )
			// SwanExpression.g:14:9: ','
			{
			match(','); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__45"

	// $ANTLR start "T__46"
	public final void mT__46() throws RecognitionException {
		try {
			int _type = T__46;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:15:7: ( '.' )
			// SwanExpression.g:15:9: '.'
			{
			match('.'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__46"

	// $ANTLR start "T__47"
	public final void mT__47() throws RecognitionException {
		try {
			int _type = T__47;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:16:7: ( ':' )
			// SwanExpression.g:16:9: ':'
			{
			match(':'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__47"

	// $ANTLR start "T__48"
	public final void mT__48() throws RecognitionException {
		try {
			int _type = T__48;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:17:7: ( '?' )
			// SwanExpression.g:17:9: '?'
			{
			match('?'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__48"

	// $ANTLR start "T__49"
	public final void mT__49() throws RecognitionException {
		try {
			int _type = T__49;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:18:7: ( '@' )
			// SwanExpression.g:18:9: '@'
			{
			match('@'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__49"

	// $ANTLR start "T__50"
	public final void mT__50() throws RecognitionException {
		try {
			int _type = T__50;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:19:7: ( '{' )
			// SwanExpression.g:19:9: '{'
			{
			match('{'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__50"

	// $ANTLR start "T__51"
	public final void mT__51() throws RecognitionException {
		try {
			int _type = T__51;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:20:7: ( '}' )
			// SwanExpression.g:20:9: '}'
			{
			match('}'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "T__51"

	// $ANTLR start "TIME_UNIT"
	public final void mTIME_UNIT() throws RecognitionException {
		try {
			int _type = TIME_UNIT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:348:5: ( 'h' | 'H' | 'm' | 'M' | 's' | 'S' | 'ms' )
			int alt1=7;
			switch ( input.LA(1) ) {
			case 'h':
				{
				alt1=1;
				}
				break;
			case 'H':
				{
				alt1=2;
				}
				break;
			case 'm':
				{
				int LA1_3 = input.LA(2);
				if ( (LA1_3=='s') ) {
					alt1=7;
				}

				else {
					alt1=3;
				}

				}
				break;
			case 'M':
				{
				alt1=4;
				}
				break;
			case 's':
				{
				alt1=5;
				}
				break;
			case 'S':
				{
				alt1=6;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 1, 0, input);
				throw nvae;
			}
			switch (alt1) {
				case 1 :
					// SwanExpression.g:348:9: 'h'
					{
					match('h'); 
					}
					break;
				case 2 :
					// SwanExpression.g:348:13: 'H'
					{
					match('H'); 
					}
					break;
				case 3 :
					// SwanExpression.g:348:17: 'm'
					{
					match('m'); 
					}
					break;
				case 4 :
					// SwanExpression.g:348:21: 'M'
					{
					match('M'); 
					}
					break;
				case 5 :
					// SwanExpression.g:348:25: 's'
					{
					match('s'); 
					}
					break;
				case 6 :
					// SwanExpression.g:348:29: 'S'
					{
					match('S'); 
					}
					break;
				case 7 :
					// SwanExpression.g:348:33: 'ms'
					{
					match("ms"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "TIME_UNIT"

	// $ANTLR start "OR"
	public final void mOR() throws RecognitionException {
		try {
			int _type = OR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:351:7: ( '||' | 'or' | 'OR' )
			int alt2=3;
			switch ( input.LA(1) ) {
			case '|':
				{
				alt2=1;
				}
				break;
			case 'o':
				{
				alt2=2;
				}
				break;
			case 'O':
				{
				alt2=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 2, 0, input);
				throw nvae;
			}
			switch (alt2) {
				case 1 :
					// SwanExpression.g:351:13: '||'
					{
					match("||"); 

					}
					break;
				case 2 :
					// SwanExpression.g:351:20: 'or'
					{
					match("or"); 

					}
					break;
				case 3 :
					// SwanExpression.g:351:27: 'OR'
					{
					match("OR"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OR"

	// $ANTLR start "AND"
	public final void mAND() throws RecognitionException {
		try {
			int _type = AND;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:352:7: ( '&&' | 'and' | 'AND' )
			int alt3=3;
			switch ( input.LA(1) ) {
			case '&':
				{
				alt3=1;
				}
				break;
			case 'a':
				{
				alt3=2;
				}
				break;
			case 'A':
				{
				alt3=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 3, 0, input);
				throw nvae;
			}
			switch (alt3) {
				case 1 :
					// SwanExpression.g:352:13: '&&'
					{
					match("&&"); 

					}
					break;
				case 2 :
					// SwanExpression.g:352:20: 'and'
					{
					match("and"); 

					}
					break;
				case 3 :
					// SwanExpression.g:352:28: 'AND'
					{
					match("AND"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "AND"

	// $ANTLR start "NOT"
	public final void mNOT() throws RecognitionException {
		try {
			int _type = NOT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:354:7: ( '!' | 'not' | 'NOT' )
			int alt4=3;
			switch ( input.LA(1) ) {
			case '!':
				{
				alt4=1;
				}
				break;
			case 'n':
				{
				alt4=2;
				}
				break;
			case 'N':
				{
				alt4=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 4, 0, input);
				throw nvae;
			}
			switch (alt4) {
				case 1 :
					// SwanExpression.g:354:12: '!'
					{
					match('!'); 
					}
					break;
				case 2 :
					// SwanExpression.g:354:18: 'not'
					{
					match("not"); 

					}
					break;
				case 3 :
					// SwanExpression.g:354:26: 'NOT'
					{
					match("NOT"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NOT"

	// $ANTLR start "CONFIG_IS"
	public final void mCONFIG_IS() throws RecognitionException {
		try {
			int _type = CONFIG_IS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:358:5: ( '=' )
			// SwanExpression.g:358:9: '='
			{
			match('='); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "CONFIG_IS"

	// $ANTLR start "CONFIG_HASH"
	public final void mCONFIG_HASH() throws RecognitionException {
		try {
			int _type = CONFIG_HASH;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:360:5: ( '#' )
			// SwanExpression.g:360:9: '#'
			{
			match('#'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "CONFIG_HASH"

	// $ANTLR start "CONFIG_TILT"
	public final void mCONFIG_TILT() throws RecognitionException {
		try {
			int _type = CONFIG_TILT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:362:5: ( '~' )
			// SwanExpression.g:362:7: '~'
			{
			match('~'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "CONFIG_TILT"

	// $ANTLR start "EQUALS"
	public final void mEQUALS() throws RecognitionException {
		try {
			int _type = EQUALS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:365:7: ( '==' | '=' )
			int alt5=2;
			int LA5_0 = input.LA(1);
			if ( (LA5_0=='=') ) {
				int LA5_1 = input.LA(2);
				if ( (LA5_1=='=') ) {
					alt5=1;
				}

				else {
					alt5=2;
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 5, 0, input);
				throw nvae;
			}

			switch (alt5) {
				case 1 :
					// SwanExpression.g:365:12: '=='
					{
					match("=="); 

					}
					break;
				case 2 :
					// SwanExpression.g:365:19: '='
					{
					match('='); 
					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "EQUALS"

	// $ANTLR start "NOTEQUALS"
	public final void mNOTEQUALS() throws RecognitionException {
		try {
			int _type = NOTEQUALS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:367:7: ( '!=' | '<>' )
			int alt6=2;
			int LA6_0 = input.LA(1);
			if ( (LA6_0=='!') ) {
				alt6=1;
			}
			else if ( (LA6_0=='<') ) {
				alt6=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 6, 0, input);
				throw nvae;
			}

			switch (alt6) {
				case 1 :
					// SwanExpression.g:367:12: '!='
					{
					match("!="); 

					}
					break;
				case 2 :
					// SwanExpression.g:367:19: '<>'
					{
					match("<>"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NOTEQUALS"

	// $ANTLR start "LT"
	public final void mLT() throws RecognitionException {
		try {
			int _type = LT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:368:7: ( '<' )
			// SwanExpression.g:368:12: '<'
			{
			match('<'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LT"

	// $ANTLR start "LTEQ"
	public final void mLTEQ() throws RecognitionException {
		try {
			int _type = LTEQ;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:369:7: ( '<=' )
			// SwanExpression.g:369:12: '<='
			{
			match("<="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LTEQ"

	// $ANTLR start "GT"
	public final void mGT() throws RecognitionException {
		try {
			int _type = GT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:370:7: ( '>' )
			// SwanExpression.g:370:12: '>'
			{
			match('>'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "GT"

	// $ANTLR start "GTEQ"
	public final void mGTEQ() throws RecognitionException {
		try {
			int _type = GTEQ;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:371:7: ( '>=' )
			// SwanExpression.g:371:12: '>='
			{
			match(">="); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "GTEQ"

	// $ANTLR start "PLUS"
	public final void mPLUS() throws RecognitionException {
		try {
			int _type = PLUS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:372:7: ( '+' )
			// SwanExpression.g:372:12: '+'
			{
			match('+'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "PLUS"

	// $ANTLR start "MINUS"
	public final void mMINUS() throws RecognitionException {
		try {
			int _type = MINUS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:373:7: ( '-' )
			// SwanExpression.g:373:12: '-'
			{
			match('-'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "MINUS"

	// $ANTLR start "MULT"
	public final void mMULT() throws RecognitionException {
		try {
			int _type = MULT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:374:7: ( '*' )
			// SwanExpression.g:374:12: '*'
			{
			match('*'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "MULT"

	// $ANTLR start "DIV"
	public final void mDIV() throws RecognitionException {
		try {
			int _type = DIV;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:375:7: ( '/' )
			// SwanExpression.g:375:12: '/'
			{
			match('/'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DIV"

	// $ANTLR start "MOD"
	public final void mMOD() throws RecognitionException {
		try {
			int _type = MOD;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:376:7: ( '%' )
			// SwanExpression.g:376:12: '%'
			{
			match('%'); 
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "MOD"

	// $ANTLR start "REGEX"
	public final void mREGEX() throws RecognitionException {
		try {
			int _type = REGEX;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:377:7: ( 'regex' | 'REGEX' )
			int alt7=2;
			int LA7_0 = input.LA(1);
			if ( (LA7_0=='r') ) {
				alt7=1;
			}
			else if ( (LA7_0=='R') ) {
				alt7=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 7, 0, input);
				throw nvae;
			}

			switch (alt7) {
				case 1 :
					// SwanExpression.g:378:9: 'regex'
					{
					match("regex"); 

					}
					break;
				case 2 :
					// SwanExpression.g:378:19: 'REGEX'
					{
					match("REGEX"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "REGEX"

	// $ANTLR start "CONTAINS"
	public final void mCONTAINS() throws RecognitionException {
		try {
			int _type = CONTAINS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:380:5: ( 'contains' | 'CONTAINS' )
			int alt8=2;
			int LA8_0 = input.LA(1);
			if ( (LA8_0=='c') ) {
				alt8=1;
			}
			else if ( (LA8_0=='C') ) {
				alt8=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 8, 0, input);
				throw nvae;
			}

			switch (alt8) {
				case 1 :
					// SwanExpression.g:380:10: 'contains'
					{
					match("contains"); 

					}
					break;
				case 2 :
					// SwanExpression.g:380:23: 'CONTAINS'
					{
					match("CONTAINS"); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "CONTAINS"

	// $ANTLR start "ALL"
	public final void mALL() throws RecognitionException {
		try {
			int _type = ALL;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:383:9: ( ( 'ALL' | 'all' ) )
			// SwanExpression.g:383:13: ( 'ALL' | 'all' )
			{
			// SwanExpression.g:383:13: ( 'ALL' | 'all' )
			int alt9=2;
			int LA9_0 = input.LA(1);
			if ( (LA9_0=='A') ) {
				alt9=1;
			}
			else if ( (LA9_0=='a') ) {
				alt9=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 9, 0, input);
				throw nvae;
			}

			switch (alt9) {
				case 1 :
					// SwanExpression.g:383:14: 'ALL'
					{
					match("ALL"); 

					}
					break;
				case 2 :
					// SwanExpression.g:383:20: 'all'
					{
					match("all"); 

					}
					break;

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ALL"

	// $ANTLR start "ANY"
	public final void mANY() throws RecognitionException {
		try {
			int _type = ANY;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:384:5: ( ( 'ANY' | 'any' ) )
			// SwanExpression.g:384:9: ( 'ANY' | 'any' )
			{
			// SwanExpression.g:384:9: ( 'ANY' | 'any' )
			int alt10=2;
			int LA10_0 = input.LA(1);
			if ( (LA10_0=='A') ) {
				alt10=1;
			}
			else if ( (LA10_0=='a') ) {
				alt10=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 10, 0, input);
				throw nvae;
			}

			switch (alt10) {
				case 1 :
					// SwanExpression.g:384:10: 'ANY'
					{
					match("ANY"); 

					}
					break;
				case 2 :
					// SwanExpression.g:384:16: 'any'
					{
					match("any"); 

					}
					break;

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ANY"

	// $ANTLR start "NONE"
	public final void mNONE() throws RecognitionException {
		try {
			int _type = NONE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:387:9: ( ( 'NONE' | 'none' ) )
			// SwanExpression.g:387:13: ( 'NONE' | 'none' )
			{
			// SwanExpression.g:387:13: ( 'NONE' | 'none' )
			int alt11=2;
			int LA11_0 = input.LA(1);
			if ( (LA11_0=='N') ) {
				alt11=1;
			}
			else if ( (LA11_0=='n') ) {
				alt11=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 11, 0, input);
				throw nvae;
			}

			switch (alt11) {
				case 1 :
					// SwanExpression.g:387:14: 'NONE'
					{
					match("NONE"); 

					}
					break;
				case 2 :
					// SwanExpression.g:387:21: 'none'
					{
					match("none"); 

					}
					break;

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NONE"

	// $ANTLR start "MAX"
	public final void mMAX() throws RecognitionException {
		try {
			int _type = MAX;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:388:5: ( ( 'MAX' | 'max' ) )
			// SwanExpression.g:388:9: ( 'MAX' | 'max' )
			{
			// SwanExpression.g:388:9: ( 'MAX' | 'max' )
			int alt12=2;
			int LA12_0 = input.LA(1);
			if ( (LA12_0=='M') ) {
				alt12=1;
			}
			else if ( (LA12_0=='m') ) {
				alt12=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 12, 0, input);
				throw nvae;
			}

			switch (alt12) {
				case 1 :
					// SwanExpression.g:388:10: 'MAX'
					{
					match("MAX"); 

					}
					break;
				case 2 :
					// SwanExpression.g:388:16: 'max'
					{
					match("max"); 

					}
					break;

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "MAX"

	// $ANTLR start "MIN"
	public final void mMIN() throws RecognitionException {
		try {
			int _type = MIN;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:389:5: ( ( 'MIN' | 'min' ) )
			// SwanExpression.g:389:9: ( 'MIN' | 'min' )
			{
			// SwanExpression.g:389:9: ( 'MIN' | 'min' )
			int alt13=2;
			int LA13_0 = input.LA(1);
			if ( (LA13_0=='M') ) {
				alt13=1;
			}
			else if ( (LA13_0=='m') ) {
				alt13=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 13, 0, input);
				throw nvae;
			}

			switch (alt13) {
				case 1 :
					// SwanExpression.g:389:10: 'MIN'
					{
					match("MIN"); 

					}
					break;
				case 2 :
					// SwanExpression.g:389:16: 'min'
					{
					match("min"); 

					}
					break;

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "MIN"

	// $ANTLR start "MEAN"
	public final void mMEAN() throws RecognitionException {
		try {
			int _type = MEAN;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:390:9: ( ( 'MEAN' | 'mean' ) )
			// SwanExpression.g:390:13: ( 'MEAN' | 'mean' )
			{
			// SwanExpression.g:390:13: ( 'MEAN' | 'mean' )
			int alt14=2;
			int LA14_0 = input.LA(1);
			if ( (LA14_0=='M') ) {
				alt14=1;
			}
			else if ( (LA14_0=='m') ) {
				alt14=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 14, 0, input);
				throw nvae;
			}

			switch (alt14) {
				case 1 :
					// SwanExpression.g:390:14: 'MEAN'
					{
					match("MEAN"); 

					}
					break;
				case 2 :
					// SwanExpression.g:390:21: 'mean'
					{
					match("mean"); 

					}
					break;

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "MEAN"

	// $ANTLR start "MEDIAN"
	public final void mMEDIAN() throws RecognitionException {
		try {
			int _type = MEDIAN;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:391:9: ( ( 'MEDIAN' | 'median' ) )
			// SwanExpression.g:391:13: ( 'MEDIAN' | 'median' )
			{
			// SwanExpression.g:391:13: ( 'MEDIAN' | 'median' )
			int alt15=2;
			int LA15_0 = input.LA(1);
			if ( (LA15_0=='M') ) {
				alt15=1;
			}
			else if ( (LA15_0=='m') ) {
				alt15=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 15, 0, input);
				throw nvae;
			}

			switch (alt15) {
				case 1 :
					// SwanExpression.g:391:14: 'MEDIAN'
					{
					match("MEDIAN"); 

					}
					break;
				case 2 :
					// SwanExpression.g:391:23: 'median'
					{
					match("median"); 

					}
					break;

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "MEDIAN"

	// $ANTLR start "HTTP"
	public final void mHTTP() throws RecognitionException {
		try {
			int _type = HTTP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:393:7: ( ( 'http://' ) )
			// SwanExpression.g:393:9: ( 'http://' )
			{
			// SwanExpression.g:393:9: ( 'http://' )
			// SwanExpression.g:393:10: 'http://'
			{
			match("http://"); 

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "HTTP"

	// $ANTLR start "HTTPS"
	public final void mHTTPS() throws RecognitionException {
		try {
			int _type = HTTPS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:394:8: ( ( 'https://' ) )
			// SwanExpression.g:394:11: ( 'https://' )
			{
			// SwanExpression.g:394:11: ( 'https://' )
			// SwanExpression.g:394:12: 'https://'
			{
			match("https://"); 

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "HTTPS"

	// $ANTLR start "ID"
	public final void mID() throws RecognitionException {
		try {
			int _type = ID;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:396:5: ( ( HTTP | HTTPS )? ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '/' | '-' | '.' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '/' | '-' | '.' )* ) )
			// SwanExpression.g:396:9: ( HTTP | HTTPS )? ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '/' | '-' | '.' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '/' | '-' | '.' )* )
			{
			// SwanExpression.g:396:9: ( HTTP | HTTPS )?
			int alt16=3;
			int LA16_0 = input.LA(1);
			if ( (LA16_0=='h') ) {
				int LA16_1 = input.LA(2);
				if ( (LA16_1=='t') ) {
					int LA16_3 = input.LA(3);
					if ( (LA16_3=='t') ) {
						int LA16_4 = input.LA(4);
						if ( (LA16_4=='p') ) {
							int LA16_5 = input.LA(5);
							if ( (LA16_5==':') ) {
								alt16=1;
							}
							else if ( (LA16_5=='s') ) {
								int LA16_7 = input.LA(6);
								if ( (LA16_7==':') ) {
									alt16=2;
								}
							}
						}
					}
				}
			}
			switch (alt16) {
				case 1 :
					// SwanExpression.g:396:10: HTTP
					{
					mHTTP(); 

					}
					break;
				case 2 :
					// SwanExpression.g:396:17: HTTPS
					{
					mHTTPS(); 

					}
					break;

			}

			// SwanExpression.g:396:24: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '/' | '-' | '.' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '/' | '-' | '.' )* )
			// SwanExpression.g:396:25: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '/' | '-' | '.' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '/' | '-' | '.' )*
			{
			if ( (input.LA(1) >= '-' && input.LA(1) <= '/')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			// SwanExpression.g:396:61: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '/' | '-' | '.' )*
			loop17:
			while (true) {
				int alt17=2;
				int LA17_0 = input.LA(1);
				if ( ((LA17_0 >= '-' && LA17_0 <= '9')||(LA17_0 >= 'A' && LA17_0 <= 'Z')||LA17_0=='_'||(LA17_0 >= 'a' && LA17_0 <= 'z')) ) {
					alt17=1;
				}

				switch (alt17) {
				case 1 :
					// SwanExpression.g:
					{
					if ( (input.LA(1) >= '-' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop17;
				}
			}

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ID"

	// $ANTLR start "INT"
	public final void mINT() throws RecognitionException {
		try {
			int _type = INT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:400:5: ( ( '-' )? ( '0' .. '9' )+ )
			// SwanExpression.g:400:9: ( '-' )? ( '0' .. '9' )+
			{
			// SwanExpression.g:400:9: ( '-' )?
			int alt18=2;
			int LA18_0 = input.LA(1);
			if ( (LA18_0=='-') ) {
				alt18=1;
			}
			switch (alt18) {
				case 1 :
					// SwanExpression.g:400:10: '-'
					{
					match('-'); 
					}
					break;

			}

			// SwanExpression.g:400:16: ( '0' .. '9' )+
			int cnt19=0;
			loop19:
			while (true) {
				int alt19=2;
				int LA19_0 = input.LA(1);
				if ( ((LA19_0 >= '0' && LA19_0 <= '9')) ) {
					alt19=1;
				}

				switch (alt19) {
				case 1 :
					// SwanExpression.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt19 >= 1 ) break loop19;
					EarlyExitException eee = new EarlyExitException(19, input);
					throw eee;
				}
				cnt19++;
			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "INT"

	// $ANTLR start "FLOAT"
	public final void mFLOAT() throws RecognitionException {
		try {
			int _type = FLOAT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:404:5: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | ( '-' )? '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '-' )? ( '0' .. '9' )+ EXPONENT )
			int alt29=3;
			alt29 = dfa29.predict(input);
			switch (alt29) {
				case 1 :
					// SwanExpression.g:404:9: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )?
					{
					// SwanExpression.g:404:9: ( '-' )?
					int alt20=2;
					int LA20_0 = input.LA(1);
					if ( (LA20_0=='-') ) {
						alt20=1;
					}
					switch (alt20) {
						case 1 :
							// SwanExpression.g:404:10: '-'
							{
							match('-'); 
							}
							break;

					}

					// SwanExpression.g:404:16: ( '0' .. '9' )+
					int cnt21=0;
					loop21:
					while (true) {
						int alt21=2;
						int LA21_0 = input.LA(1);
						if ( ((LA21_0 >= '0' && LA21_0 <= '9')) ) {
							alt21=1;
						}

						switch (alt21) {
						case 1 :
							// SwanExpression.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt21 >= 1 ) break loop21;
							EarlyExitException eee = new EarlyExitException(21, input);
							throw eee;
						}
						cnt21++;
					}

					match('.'); 
					// SwanExpression.g:404:32: ( '0' .. '9' )*
					loop22:
					while (true) {
						int alt22=2;
						int LA22_0 = input.LA(1);
						if ( ((LA22_0 >= '0' && LA22_0 <= '9')) ) {
							alt22=1;
						}

						switch (alt22) {
						case 1 :
							// SwanExpression.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							break loop22;
						}
					}

					// SwanExpression.g:404:44: ( EXPONENT )?
					int alt23=2;
					int LA23_0 = input.LA(1);
					if ( (LA23_0=='E'||LA23_0=='e') ) {
						alt23=1;
					}
					switch (alt23) {
						case 1 :
							// SwanExpression.g:404:44: EXPONENT
							{
							mEXPONENT(); 

							}
							break;

					}

					}
					break;
				case 2 :
					// SwanExpression.g:405:9: ( '-' )? '.' ( '0' .. '9' )+ ( EXPONENT )?
					{
					// SwanExpression.g:405:9: ( '-' )?
					int alt24=2;
					int LA24_0 = input.LA(1);
					if ( (LA24_0=='-') ) {
						alt24=1;
					}
					switch (alt24) {
						case 1 :
							// SwanExpression.g:405:10: '-'
							{
							match('-'); 
							}
							break;

					}

					match('.'); 
					// SwanExpression.g:405:20: ( '0' .. '9' )+
					int cnt25=0;
					loop25:
					while (true) {
						int alt25=2;
						int LA25_0 = input.LA(1);
						if ( ((LA25_0 >= '0' && LA25_0 <= '9')) ) {
							alt25=1;
						}

						switch (alt25) {
						case 1 :
							// SwanExpression.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt25 >= 1 ) break loop25;
							EarlyExitException eee = new EarlyExitException(25, input);
							throw eee;
						}
						cnt25++;
					}

					// SwanExpression.g:405:32: ( EXPONENT )?
					int alt26=2;
					int LA26_0 = input.LA(1);
					if ( (LA26_0=='E'||LA26_0=='e') ) {
						alt26=1;
					}
					switch (alt26) {
						case 1 :
							// SwanExpression.g:405:32: EXPONENT
							{
							mEXPONENT(); 

							}
							break;

					}

					}
					break;
				case 3 :
					// SwanExpression.g:406:9: ( '-' )? ( '0' .. '9' )+ EXPONENT
					{
					// SwanExpression.g:406:9: ( '-' )?
					int alt27=2;
					int LA27_0 = input.LA(1);
					if ( (LA27_0=='-') ) {
						alt27=1;
					}
					switch (alt27) {
						case 1 :
							// SwanExpression.g:406:10: '-'
							{
							match('-'); 
							}
							break;

					}

					// SwanExpression.g:406:16: ( '0' .. '9' )+
					int cnt28=0;
					loop28:
					while (true) {
						int alt28=2;
						int LA28_0 = input.LA(1);
						if ( ((LA28_0 >= '0' && LA28_0 <= '9')) ) {
							alt28=1;
						}

						switch (alt28) {
						case 1 :
							// SwanExpression.g:
							{
							if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							if ( cnt28 >= 1 ) break loop28;
							EarlyExitException eee = new EarlyExitException(28, input);
							throw eee;
						}
						cnt28++;
					}

					mEXPONENT(); 

					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "FLOAT"

	// $ANTLR start "WS"
	public final void mWS() throws RecognitionException {
		try {
			int _type = WS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:409:5: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
			// SwanExpression.g:409:9: ( ' ' | '\\t' | '\\r' | '\\n' )
			{
			if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			_channel=HIDDEN;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WS"

	// $ANTLR start "STRING"
	public final void mSTRING() throws RecognitionException {
		try {
			int _type = STRING;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			int normal;

			StringBuilder lBuf = new StringBuilder();
			// SwanExpression.g:418:5: ( '\\'' ( ESC_SEQ |normal=~ ( '\\'' | '\\\\' ) )* '\\'' )
			// SwanExpression.g:419:5: '\\'' ( ESC_SEQ |normal=~ ( '\\'' | '\\\\' ) )* '\\''
			{
			match('\''); 
			// SwanExpression.g:420:5: ( ESC_SEQ |normal=~ ( '\\'' | '\\\\' ) )*
			loop30:
			while (true) {
				int alt30=3;
				int LA30_0 = input.LA(1);
				if ( (LA30_0=='\\') ) {
					alt30=1;
				}
				else if ( ((LA30_0 >= '\u0000' && LA30_0 <= '&')||(LA30_0 >= '(' && LA30_0 <= '[')||(LA30_0 >= ']' && LA30_0 <= '\uFFFF')) ) {
					alt30=2;
				}

				switch (alt30) {
				case 1 :
					// SwanExpression.g:420:7: ESC_SEQ
					{
					mESC_SEQ(); 

					lBuf.append(getText());
					}
					break;
				case 2 :
					// SwanExpression.g:422:7: normal=~ ( '\\'' | '\\\\' )
					{
					normal= input.LA(1);
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '&')||(input.LA(1) >= '(' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					lBuf.appendCodePoint(normal);
					}
					break;

				default :
					break loop30;
				}
			}

			match('\''); 
			setText(lBuf.toString());
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "STRING"

	// $ANTLR start "ESC_SEQ"
	public final void mESC_SEQ() throws RecognitionException {
		try {
			CommonToken i=null;
			CommonToken j=null;
			CommonToken k=null;
			CommonToken l=null;

			// SwanExpression.g:432:5: ( '\\\\' ( 'n' | 'r' | 't' | 'b' | 'f' | '\"' | '\\'' | '/' | '\\\\' | ( 'u' )+ i= HEX_DIGIT j= HEX_DIGIT k= HEX_DIGIT l= HEX_DIGIT ) )
			// SwanExpression.g:432:9: '\\\\' ( 'n' | 'r' | 't' | 'b' | 'f' | '\"' | '\\'' | '/' | '\\\\' | ( 'u' )+ i= HEX_DIGIT j= HEX_DIGIT k= HEX_DIGIT l= HEX_DIGIT )
			{
			match('\\'); 
			// SwanExpression.g:433:9: ( 'n' | 'r' | 't' | 'b' | 'f' | '\"' | '\\'' | '/' | '\\\\' | ( 'u' )+ i= HEX_DIGIT j= HEX_DIGIT k= HEX_DIGIT l= HEX_DIGIT )
			int alt32=10;
			switch ( input.LA(1) ) {
			case 'n':
				{
				alt32=1;
				}
				break;
			case 'r':
				{
				alt32=2;
				}
				break;
			case 't':
				{
				alt32=3;
				}
				break;
			case 'b':
				{
				alt32=4;
				}
				break;
			case 'f':
				{
				alt32=5;
				}
				break;
			case '\"':
				{
				alt32=6;
				}
				break;
			case '\'':
				{
				alt32=7;
				}
				break;
			case '/':
				{
				alt32=8;
				}
				break;
			case '\\':
				{
				alt32=9;
				}
				break;
			case 'u':
				{
				alt32=10;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 32, 0, input);
				throw nvae;
			}
			switch (alt32) {
				case 1 :
					// SwanExpression.g:433:17: 'n'
					{
					match('n'); 
					setText("\n");
					}
					break;
				case 2 :
					// SwanExpression.g:434:17: 'r'
					{
					match('r'); 
					setText("\r");
					}
					break;
				case 3 :
					// SwanExpression.g:435:17: 't'
					{
					match('t'); 
					setText("\t");
					}
					break;
				case 4 :
					// SwanExpression.g:436:17: 'b'
					{
					match('b'); 
					setText("\b");
					}
					break;
				case 5 :
					// SwanExpression.g:437:17: 'f'
					{
					match('f'); 
					setText("\f");
					}
					break;
				case 6 :
					// SwanExpression.g:438:17: '\"'
					{
					match('\"'); 
					setText("\"");
					}
					break;
				case 7 :
					// SwanExpression.g:439:17: '\\''
					{
					match('\''); 
					setText("\'");
					}
					break;
				case 8 :
					// SwanExpression.g:440:17: '/'
					{
					match('/'); 
					setText("/");
					}
					break;
				case 9 :
					// SwanExpression.g:441:17: '\\\\'
					{
					match('\\'); 
					setText("\\");
					}
					break;
				case 10 :
					// SwanExpression.g:442:17: ( 'u' )+ i= HEX_DIGIT j= HEX_DIGIT k= HEX_DIGIT l= HEX_DIGIT
					{
					// SwanExpression.g:442:17: ( 'u' )+
					int cnt31=0;
					loop31:
					while (true) {
						int alt31=2;
						int LA31_0 = input.LA(1);
						if ( (LA31_0=='u') ) {
							alt31=1;
						}

						switch (alt31) {
						case 1 :
							// SwanExpression.g:442:18: 'u'
							{
							match('u'); 
							}
							break;

						default :
							if ( cnt31 >= 1 ) break loop31;
							EarlyExitException eee = new EarlyExitException(31, input);
							throw eee;
						}
						cnt31++;
					}

					int iStart1128 = getCharIndex();
					int iStartLine1128 = getLine();
					int iStartCharPos1128 = getCharPositionInLine();
					mHEX_DIGIT(); 
					i = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, iStart1128, getCharIndex()-1);
					i.setLine(iStartLine1128);
					i.setCharPositionInLine(iStartCharPos1128);

					int jStart1132 = getCharIndex();
					int jStartLine1132 = getLine();
					int jStartCharPos1132 = getCharPositionInLine();
					mHEX_DIGIT(); 
					j = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, jStart1132, getCharIndex()-1);
					j.setLine(jStartLine1132);
					j.setCharPositionInLine(jStartCharPos1132);

					int kStart1136 = getCharIndex();
					int kStartLine1136 = getLine();
					int kStartCharPos1136 = getCharPositionInLine();
					mHEX_DIGIT(); 
					k = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, kStart1136, getCharIndex()-1);
					k.setLine(kStartLine1136);
					k.setCharPositionInLine(kStartCharPos1136);

					int lStart1140 = getCharIndex();
					int lStartLine1140 = getLine();
					int lStartCharPos1140 = getCharPositionInLine();
					mHEX_DIGIT(); 
					l = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, lStart1140, getCharIndex()-1);
					l.setLine(lStartLine1140);
					l.setCharPositionInLine(lStartCharPos1140);

					setText(String.valueOf((char) Integer.parseInt(i.getText() + j.getText() + k.getText() + l.getText(), 16)));
					}
					break;

			}

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ESC_SEQ"

	// $ANTLR start "CONFIG_VAL"
	public final void mCONFIG_VAL() throws RecognitionException {
		try {
			int _type = CONFIG_VAL;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// SwanExpression.g:446:5: ( '=' ( STRING | ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '/' | ':' | '=' | '&' | '_' | '\"' )* ) )
			// SwanExpression.g:446:9: '=' ( STRING | ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '/' | ':' | '=' | '&' | '_' | '\"' )* )
			{
			match('='); 
			// SwanExpression.g:446:13: ( STRING | ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '/' | ':' | '=' | '&' | '_' | '\"' )* )
			int alt34=2;
			int LA34_0 = input.LA(1);
			if ( (LA34_0=='\'') ) {
				alt34=1;
			}

			else {
				alt34=2;
			}

			switch (alt34) {
				case 1 :
					// SwanExpression.g:446:14: STRING
					{
					mSTRING(); 


					            /* String uses setText which drops the '='. Put it back so it is the same as the other branch. */ 
					            setText("=" + getText());
					            
					}
					break;
				case 2 :
					// SwanExpression.g:451:11: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '/' | ':' | '=' | '&' | '_' | '\"' )*
					{
					// SwanExpression.g:451:11: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '/' | ':' | '=' | '&' | '_' | '\"' )*
					loop33:
					while (true) {
						int alt33=2;
						int LA33_0 = input.LA(1);
						if ( (LA33_0=='\"'||LA33_0=='&'||(LA33_0 >= '.' && LA33_0 <= ':')||LA33_0=='='||(LA33_0 >= 'A' && LA33_0 <= 'Z')||LA33_0=='_'||(LA33_0 >= 'a' && LA33_0 <= 'z')) ) {
							alt33=1;
						}

						switch (alt33) {
						case 1 :
							// SwanExpression.g:
							{
							if ( input.LA(1)=='\"'||input.LA(1)=='&'||(input.LA(1) >= '.' && input.LA(1) <= ':')||input.LA(1)=='='||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

						default :
							break loop33;
						}
					}

					}
					break;

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "CONFIG_VAL"

	// $ANTLR start "EXPONENT"
	public final void mEXPONENT() throws RecognitionException {
		try {
			// SwanExpression.g:457:10: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
			// SwanExpression.g:457:12: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
			{
			if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			// SwanExpression.g:457:22: ( '+' | '-' )?
			int alt35=2;
			int LA35_0 = input.LA(1);
			if ( (LA35_0=='+'||LA35_0=='-') ) {
				alt35=1;
			}
			switch (alt35) {
				case 1 :
					// SwanExpression.g:
					{
					if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

			}

			// SwanExpression.g:457:33: ( '0' .. '9' )+
			int cnt36=0;
			loop36:
			while (true) {
				int alt36=2;
				int LA36_0 = input.LA(1);
				if ( ((LA36_0 >= '0' && LA36_0 <= '9')) ) {
					alt36=1;
				}

				switch (alt36) {
				case 1 :
					// SwanExpression.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
						input.consume();
					}
					else {
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt36 >= 1 ) break loop36;
					EarlyExitException eee = new EarlyExitException(36, input);
					throw eee;
				}
				cnt36++;
			}

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "EXPONENT"

	// $ANTLR start "HEX_DIGIT"
	public final void mHEX_DIGIT() throws RecognitionException {
		try {
			// SwanExpression.g:460:11: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
			// SwanExpression.g:
			{
			if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'F')||(input.LA(1) >= 'a' && input.LA(1) <= 'f') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "HEX_DIGIT"

	@Override
	public void mTokens() throws RecognitionException {
		// SwanExpression.g:1:8: ( T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | T__50 | T__51 | TIME_UNIT | OR | AND | NOT | CONFIG_IS | CONFIG_HASH | CONFIG_TILT | EQUALS | NOTEQUALS | LT | LTEQ | GT | GTEQ | PLUS | MINUS | MULT | DIV | MOD | REGEX | CONTAINS | ALL | ANY | NONE | MAX | MIN | MEAN | MEDIAN | HTTP | HTTPS | ID | INT | FLOAT | WS | STRING | CONFIG_VAL )
		int alt37=45;
		alt37 = dfa37.predict(input);
		switch (alt37) {
			case 1 :
				// SwanExpression.g:1:10: T__42
				{
				mT__42(); 

				}
				break;
			case 2 :
				// SwanExpression.g:1:16: T__43
				{
				mT__43(); 

				}
				break;
			case 3 :
				// SwanExpression.g:1:22: T__44
				{
				mT__44(); 

				}
				break;
			case 4 :
				// SwanExpression.g:1:28: T__45
				{
				mT__45(); 

				}
				break;
			case 5 :
				// SwanExpression.g:1:34: T__46
				{
				mT__46(); 

				}
				break;
			case 6 :
				// SwanExpression.g:1:40: T__47
				{
				mT__47(); 

				}
				break;
			case 7 :
				// SwanExpression.g:1:46: T__48
				{
				mT__48(); 

				}
				break;
			case 8 :
				// SwanExpression.g:1:52: T__49
				{
				mT__49(); 

				}
				break;
			case 9 :
				// SwanExpression.g:1:58: T__50
				{
				mT__50(); 

				}
				break;
			case 10 :
				// SwanExpression.g:1:64: T__51
				{
				mT__51(); 

				}
				break;
			case 11 :
				// SwanExpression.g:1:70: TIME_UNIT
				{
				mTIME_UNIT(); 

				}
				break;
			case 12 :
				// SwanExpression.g:1:80: OR
				{
				mOR(); 

				}
				break;
			case 13 :
				// SwanExpression.g:1:83: AND
				{
				mAND(); 

				}
				break;
			case 14 :
				// SwanExpression.g:1:87: NOT
				{
				mNOT(); 

				}
				break;
			case 15 :
				// SwanExpression.g:1:91: CONFIG_IS
				{
				mCONFIG_IS(); 

				}
				break;
			case 16 :
				// SwanExpression.g:1:101: CONFIG_HASH
				{
				mCONFIG_HASH(); 

				}
				break;
			case 17 :
				// SwanExpression.g:1:113: CONFIG_TILT
				{
				mCONFIG_TILT(); 

				}
				break;
			case 18 :
				// SwanExpression.g:1:125: EQUALS
				{
				mEQUALS(); 

				}
				break;
			case 19 :
				// SwanExpression.g:1:132: NOTEQUALS
				{
				mNOTEQUALS(); 

				}
				break;
			case 20 :
				// SwanExpression.g:1:142: LT
				{
				mLT(); 

				}
				break;
			case 21 :
				// SwanExpression.g:1:145: LTEQ
				{
				mLTEQ(); 

				}
				break;
			case 22 :
				// SwanExpression.g:1:150: GT
				{
				mGT(); 

				}
				break;
			case 23 :
				// SwanExpression.g:1:153: GTEQ
				{
				mGTEQ(); 

				}
				break;
			case 24 :
				// SwanExpression.g:1:158: PLUS
				{
				mPLUS(); 

				}
				break;
			case 25 :
				// SwanExpression.g:1:163: MINUS
				{
				mMINUS(); 

				}
				break;
			case 26 :
				// SwanExpression.g:1:169: MULT
				{
				mMULT(); 

				}
				break;
			case 27 :
				// SwanExpression.g:1:174: DIV
				{
				mDIV(); 

				}
				break;
			case 28 :
				// SwanExpression.g:1:178: MOD
				{
				mMOD(); 

				}
				break;
			case 29 :
				// SwanExpression.g:1:182: REGEX
				{
				mREGEX(); 

				}
				break;
			case 30 :
				// SwanExpression.g:1:188: CONTAINS
				{
				mCONTAINS(); 

				}
				break;
			case 31 :
				// SwanExpression.g:1:197: ALL
				{
				mALL(); 

				}
				break;
			case 32 :
				// SwanExpression.g:1:201: ANY
				{
				mANY(); 

				}
				break;
			case 33 :
				// SwanExpression.g:1:205: NONE
				{
				mNONE(); 

				}
				break;
			case 34 :
				// SwanExpression.g:1:210: MAX
				{
				mMAX(); 

				}
				break;
			case 35 :
				// SwanExpression.g:1:214: MIN
				{
				mMIN(); 

				}
				break;
			case 36 :
				// SwanExpression.g:1:218: MEAN
				{
				mMEAN(); 

				}
				break;
			case 37 :
				// SwanExpression.g:1:223: MEDIAN
				{
				mMEDIAN(); 

				}
				break;
			case 38 :
				// SwanExpression.g:1:230: HTTP
				{
				mHTTP(); 

				}
				break;
			case 39 :
				// SwanExpression.g:1:235: HTTPS
				{
				mHTTPS(); 

				}
				break;
			case 40 :
				// SwanExpression.g:1:241: ID
				{
				mID(); 

				}
				break;
			case 41 :
				// SwanExpression.g:1:244: INT
				{
				mINT(); 

				}
				break;
			case 42 :
				// SwanExpression.g:1:248: FLOAT
				{
				mFLOAT(); 

				}
				break;
			case 43 :
				// SwanExpression.g:1:254: WS
				{
				mWS(); 

				}
				break;
			case 44 :
				// SwanExpression.g:1:257: STRING
				{
				mSTRING(); 

				}
				break;
			case 45 :
				// SwanExpression.g:1:264: CONFIG_VAL
				{
				mCONFIG_VAL(); 

				}
				break;

		}
	}


	protected DFA29 dfa29 = new DFA29(this);
	protected DFA37 dfa37 = new DFA37(this);
	static final String DFA29_eotS =
		"\6\uffff";
	static final String DFA29_eofS =
		"\6\uffff";
	static final String DFA29_minS =
		"\1\55\2\56\3\uffff";
	static final String DFA29_maxS =
		"\2\71\1\145\3\uffff";
	static final String DFA29_acceptS =
		"\3\uffff\1\2\1\1\1\3";
	static final String DFA29_specialS =
		"\6\uffff}>";
	static final String[] DFA29_transitionS = {
			"\1\1\1\3\1\uffff\12\2",
			"\1\3\1\uffff\12\2",
			"\1\4\1\uffff\12\2\13\uffff\1\5\37\uffff\1\5",
			"",
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
			return "403:1: FLOAT : ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | ( '-' )? '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '-' )? ( '0' .. '9' )+ EXPONENT );";
		}
	}

	static final String DFA37_eotS =
		"\5\uffff\1\54\5\uffff\6\57\1\uffff\2\50\1\uffff\2\50\1\76\2\50\1\102\2"+
		"\uffff\1\105\1\107\1\uffff\1\110\1\uffff\1\113\1\uffff\4\50\1\uffff\1"+
		"\120\3\uffff\2\50\1\uffff\1\57\6\50\2\21\4\50\2\uffff\2\50\1\146\7\uffff"+
		"\2\50\1\uffff\4\50\2\uffff\2\50\1\160\1\161\2\50\1\160\1\161\2\50\1\24"+
		"\1\166\1\167\1\24\1\166\1\167\1\76\1\50\1\76\1\50\1\uffff\11\50\2\uffff"+
		"\1\u0084\1\50\1\u0084\1\50\2\uffff\2\u0087\10\50\1\uffff\1\50\1\uffff"+
		"\2\50\1\uffff\2\50\2\u0092\2\50\2\uffff\2\u0097\1\uffff\2\50\1\u009a\2"+
		"\uffff\2\50\1\uffff\1\u009e\2\u009f\2\uffff";
	static final String DFA37_eofS =
		"\u00a0\uffff";
	static final String DFA37_minS =
		"\1\11\4\uffff\1\55\5\uffff\6\55\1\uffff\1\162\1\122\1\uffff\1\154\1\114"+
		"\1\75\1\157\1\117\1\42\2\uffff\2\75\1\uffff\1\55\1\uffff\1\55\1\uffff"+
		"\1\145\1\105\1\157\1\117\1\uffff\1\56\3\uffff\1\60\1\164\1\uffff\1\55"+
		"\1\170\1\156\1\141\1\130\1\116\1\101\2\55\1\144\1\154\1\104\1\114\2\uffff"+
		"\1\156\1\116\1\42\7\uffff\1\56\1\60\1\uffff\1\147\1\107\1\156\1\116\2"+
		"\uffff\1\53\1\160\2\55\1\156\1\151\2\55\1\116\1\111\7\55\1\145\1\55\1"+
		"\105\1\uffff\1\60\1\53\1\145\1\105\1\164\1\124\2\60\1\72\2\uffff\1\55"+
		"\1\141\1\55\1\101\2\uffff\2\55\1\60\1\53\2\60\1\170\1\130\1\141\1\101"+
		"\1\57\1\72\1\uffff\1\156\1\116\1\uffff\2\60\2\55\1\151\1\111\2\57\2\55"+
		"\1\uffff\1\156\1\116\1\55\1\57\1\uffff\1\163\1\123\1\uffff\3\55\2\uffff";
	static final String DFA37_maxS =
		"\1\176\4\uffff\1\172\5\uffff\6\172\1\uffff\1\162\1\122\1\uffff\1\156\1"+
		"\116\1\75\1\157\1\117\1\172\2\uffff\1\76\1\75\1\uffff\1\172\1\uffff\1"+
		"\172\1\uffff\1\145\1\105\1\157\1\117\1\uffff\1\145\3\uffff\1\145\1\164"+
		"\1\uffff\1\172\1\170\1\156\1\144\1\130\1\116\1\104\2\172\1\171\1\154\1"+
		"\131\1\114\2\uffff\1\164\1\124\1\172\7\uffff\1\145\1\71\1\uffff\1\147"+
		"\1\107\1\156\1\116\2\uffff\1\71\1\160\2\172\1\156\1\151\2\172\1\116\1"+
		"\111\7\172\1\145\1\172\1\105\1\uffff\1\145\1\71\1\145\1\105\1\164\1\124"+
		"\2\71\1\163\2\uffff\1\172\1\141\1\172\1\101\2\uffff\2\172\1\145\3\71\1"+
		"\170\1\130\1\141\1\101\1\57\1\72\1\uffff\1\156\1\116\1\uffff\2\71\2\172"+
		"\1\151\1\111\2\57\2\172\1\uffff\1\156\1\116\1\172\1\57\1\uffff\1\163\1"+
		"\123\1\uffff\3\172\2\uffff";
	static final String DFA37_acceptS =
		"\1\uffff\1\1\1\2\1\3\1\4\1\uffff\1\6\1\7\1\10\1\11\1\12\6\uffff\1\14\2"+
		"\uffff\1\15\6\uffff\1\20\1\21\2\uffff\1\30\1\uffff\1\32\1\uffff\1\34\4"+
		"\uffff\1\50\1\uffff\1\53\1\54\1\5\2\uffff\1\13\15\uffff\1\23\1\16\3\uffff"+
		"\1\17\1\55\1\25\1\24\1\27\1\26\1\31\2\uffff\1\33\4\uffff\1\51\1\52\24"+
		"\uffff\1\22\11\uffff\1\42\1\43\4\uffff\1\40\1\37\14\uffff\1\44\2\uffff"+
		"\1\41\12\uffff\1\35\4\uffff\1\45\2\uffff\1\46\3\uffff\1\47\1\36";
	static final String DFA37_specialS =
		"\u00a0\uffff}>";
	static final String[] DFA37_transitionS = {
			"\2\52\2\uffff\1\52\22\uffff\1\52\1\27\1\uffff\1\33\1\1\1\43\1\24\1\53"+
			"\1\2\1\3\1\41\1\37\1\4\1\40\1\5\1\42\12\51\1\6\1\uffff\1\35\1\32\1\36"+
			"\1\7\1\10\1\26\1\50\1\47\4\50\1\14\4\50\1\16\1\31\1\23\2\50\1\45\1\20"+
			"\7\50\4\uffff\1\50\1\uffff\1\25\1\50\1\46\4\50\1\13\4\50\1\15\1\30\1"+
			"\22\2\50\1\44\1\17\7\50\1\11\1\21\1\12\1\34",
			"",
			"",
			"",
			"",
			"\3\50\12\55\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"",
			"",
			"",
			"",
			"",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\23\50\1\56\6\50",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\1\61\3\50\1\63\3\50\1\62\11"+
			"\50\1\60\7\50",
			"\15\50\7\uffff\1\64\3\50\1\66\3\50\1\65\21\50\4\uffff\1\50\1\uffff\32"+
			"\50",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"",
			"\1\67",
			"\1\70",
			"",
			"\1\72\1\uffff\1\71",
			"\1\74\1\uffff\1\73",
			"\1\75",
			"\1\77",
			"\1\100",
			"\1\103\3\uffff\2\103\6\uffff\15\103\2\uffff\1\101\3\uffff\32\103\4\uffff"+
			"\1\103\1\uffff\32\103",
			"",
			"",
			"\1\104\1\75",
			"\1\106",
			"",
			"\1\50\1\112\1\50\12\111\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"",
			"\1\114",
			"\1\115",
			"\1\116",
			"\1\117",
			"",
			"\1\121\1\uffff\12\51\13\uffff\1\121\37\uffff\1\121",
			"",
			"",
			"",
			"\12\55\13\uffff\1\122\37\uffff\1\122",
			"\1\123",
			"",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\1\124",
			"\1\125",
			"\1\126\2\uffff\1\127",
			"\1\130",
			"\1\131",
			"\1\132\2\uffff\1\133",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\1\134\24\uffff\1\135",
			"\1\136",
			"\1\137\24\uffff\1\140",
			"\1\141",
			"",
			"",
			"\1\143\5\uffff\1\142",
			"\1\145\5\uffff\1\144",
			"\1\103\3\uffff\1\103\7\uffff\15\103\2\uffff\1\103\3\uffff\32\103\4\uffff"+
			"\1\103\1\uffff\32\103",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\147\1\uffff\12\111\13\uffff\1\150\37\uffff\1\150",
			"\12\55",
			"",
			"\1\151",
			"\1\152",
			"\1\153",
			"\1\154",
			"",
			"",
			"\1\121\1\uffff\1\155\2\uffff\12\156",
			"\1\157",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\1\162",
			"\1\163",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\1\164",
			"\1\165",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\1\170",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\1\171",
			"",
			"\12\172\13\uffff\1\173\37\uffff\1\173",
			"\1\121\1\uffff\1\174\2\uffff\12\175",
			"\1\176",
			"\1\177",
			"\1\u0080",
			"\1\u0081",
			"\12\156",
			"\12\156",
			"\1\u0082\70\uffff\1\u0083",
			"",
			"",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\1\u0085",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\1\u0086",
			"",
			"",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\12\172\13\uffff\1\173\37\uffff\1\173",
			"\1\121\1\uffff\1\u0088\2\uffff\12\u0089",
			"\12\175",
			"\12\175",
			"\1\u008a",
			"\1\u008b",
			"\1\u008c",
			"\1\u008d",
			"\1\u008e",
			"\1\u008f",
			"",
			"\1\u0090",
			"\1\u0091",
			"",
			"\12\u0089",
			"\12\u0089",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\1\u0093",
			"\1\u0094",
			"\1\u0095",
			"\1\u0096",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"",
			"\1\u0098",
			"\1\u0099",
			"\3\50\21\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\1\u009b",
			"",
			"\1\u009c",
			"\1\u009d",
			"",
			"\3\50\21\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"\15\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
			"",
			""
	};

	static final short[] DFA37_eot = DFA.unpackEncodedString(DFA37_eotS);
	static final short[] DFA37_eof = DFA.unpackEncodedString(DFA37_eofS);
	static final char[] DFA37_min = DFA.unpackEncodedStringToUnsignedChars(DFA37_minS);
	static final char[] DFA37_max = DFA.unpackEncodedStringToUnsignedChars(DFA37_maxS);
	static final short[] DFA37_accept = DFA.unpackEncodedString(DFA37_acceptS);
	static final short[] DFA37_special = DFA.unpackEncodedString(DFA37_specialS);
	static final short[][] DFA37_transition;

	static {
		int numStates = DFA37_transitionS.length;
		DFA37_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA37_transition[i] = DFA.unpackEncodedString(DFA37_transitionS[i]);
		}
	}

	protected class DFA37 extends DFA {

		public DFA37(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 37;
			this.eot = DFA37_eot;
			this.eof = DFA37_eof;
			this.min = DFA37_min;
			this.max = DFA37_max;
			this.accept = DFA37_accept;
			this.special = DFA37_special;
			this.transition = DFA37_transition;
		}
		@Override
		public String getDescription() {
			return "1:1: Tokens : ( T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | T__50 | T__51 | TIME_UNIT | OR | AND | NOT | CONFIG_IS | CONFIG_HASH | CONFIG_TILT | EQUALS | NOTEQUALS | LT | LTEQ | GT | GTEQ | PLUS | MINUS | MULT | DIV | MOD | REGEX | CONTAINS | ALL | ANY | NONE | MAX | MIN | MEAN | MEDIAN | HTTP | HTTPS | ID | INT | FLOAT | WS | STRING | CONFIG_VAL );";
		}
	}

}
