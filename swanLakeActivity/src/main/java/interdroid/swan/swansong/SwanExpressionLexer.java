// $ANTLR 3.4 /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g 2015-02-11 20:36:26

package interdroid.swan.swansong;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class SwanExpressionLexer extends Lexer {
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
    public String getGrammarFileName() { return "/Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g"; }

    // $ANTLR start "T__39"
    public final void mT__39() throws RecognitionException {
        try {
            int _type = T__39;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:11:7: ( '(' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:11:9: '('
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
    // $ANTLR end "T__39"

    // $ANTLR start "T__40"
    public final void mT__40() throws RecognitionException {
        try {
            int _type = T__40;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:12:7: ( ')' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:12:9: ')'
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
    // $ANTLR end "T__40"

    // $ANTLR start "T__41"
    public final void mT__41() throws RecognitionException {
        try {
            int _type = T__41;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:13:7: ( ',' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:13:9: ','
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
    // $ANTLR end "T__41"

    // $ANTLR start "T__42"
    public final void mT__42() throws RecognitionException {
        try {
            int _type = T__42;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:14:7: ( '.' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:14:9: '.'
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
    // $ANTLR end "T__42"

    // $ANTLR start "T__43"
    public final void mT__43() throws RecognitionException {
        try {
            int _type = T__43;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:15:7: ( ':' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:15:9: ':'
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
    // $ANTLR end "T__43"

    // $ANTLR start "T__44"
    public final void mT__44() throws RecognitionException {
        try {
            int _type = T__44;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:16:7: ( '?' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:16:9: '?'
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
    // $ANTLR end "T__44"

    // $ANTLR start "T__45"
    public final void mT__45() throws RecognitionException {
        try {
            int _type = T__45;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:17:7: ( '@' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:17:9: '@'
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
    // $ANTLR end "T__45"

    // $ANTLR start "T__46"
    public final void mT__46() throws RecognitionException {
        try {
            int _type = T__46;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:18:7: ( '{' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:18:9: '{'
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
    // $ANTLR end "T__46"

    // $ANTLR start "T__47"
    public final void mT__47() throws RecognitionException {
        try {
            int _type = T__47;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:19:7: ( '}' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:19:9: '}'
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
    // $ANTLR end "T__47"

    // $ANTLR start "TIME_UNIT"
    public final void mTIME_UNIT() throws RecognitionException {
        try {
            int _type = TIME_UNIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:321:5: ( 'h' | 'H' | 'm' | 'M' | 's' | 'S' | 'ms' )
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
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:321:9: 'h'
                    {
                    match('h'); 

                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:321:13: 'H'
                    {
                    match('H'); 

                    }
                    break;
                case 3 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:321:17: 'm'
                    {
                    match('m'); 

                    }
                    break;
                case 4 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:321:21: 'M'
                    {
                    match('M'); 

                    }
                    break;
                case 5 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:321:25: 's'
                    {
                    match('s'); 

                    }
                    break;
                case 6 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:321:29: 'S'
                    {
                    match('S'); 

                    }
                    break;
                case 7 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:321:33: 'ms'
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:324:7: ( '||' | 'or' | 'OR' )
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
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:324:13: '||'
                    {
                    match("||"); 



                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:324:20: 'or'
                    {
                    match("or"); 



                    }
                    break;
                case 3 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:324:27: 'OR'
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:325:7: ( '&&' | 'and' | 'AND' )
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
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:325:13: '&&'
                    {
                    match("&&"); 



                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:325:20: 'and'
                    {
                    match("and"); 



                    }
                    break;
                case 3 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:325:28: 'AND'
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:327:7: ( '!' | 'not' | 'NOT' )
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
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:327:12: '!'
                    {
                    match('!'); 

                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:327:18: 'not'
                    {
                    match("not"); 



                    }
                    break;
                case 3 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:327:26: 'NOT'
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:331:5: ( '=' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:331:9: '='
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

    // $ANTLR start "CONFIG_AND"
    public final void mCONFIG_AND() throws RecognitionException {
        try {
            int _type = CONFIG_AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:333:5: ( '&' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:333:9: '&'
            {
            match('&'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CONFIG_AND"

    // $ANTLR start "EQUALS"
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:337:7: ( '==' | '=' )
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
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:337:12: '=='
                    {
                    match("=="); 



                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:337:19: '='
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:339:7: ( '!=' | '<>' )
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
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:339:12: '!='
                    {
                    match("!="); 



                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:339:19: '<>'
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:340:7: ( '<' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:340:12: '<'
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:341:7: ( '<=' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:341:12: '<='
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:342:7: ( '>' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:342:12: '>'
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:343:7: ( '>=' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:343:12: '>='
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:344:7: ( '+' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:344:12: '+'
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:345:7: ( '-' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:345:12: '-'
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:346:7: ( '*' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:346:12: '*'
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:347:7: ( '/' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:347:12: '/'
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:348:7: ( '%' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:348:12: '%'
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:349:7: ( 'regex' | 'REGEX' )
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
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:350:9: 'regex'
                    {
                    match("regex"); 



                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:350:19: 'REGEX'
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:352:5: ( 'contains' | 'CONTAINS' )
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
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:352:10: 'contains'
                    {
                    match("contains"); 



                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:352:23: 'CONTAINS'
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:355:9: ( ( 'ALL' | 'all' ) )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:355:13: ( 'ALL' | 'all' )
            {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:355:13: ( 'ALL' | 'all' )
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
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:355:14: 'ALL'
                    {
                    match("ALL"); 



                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:355:20: 'all'
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:356:5: ( ( 'ANY' | 'any' ) )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:356:9: ( 'ANY' | 'any' )
            {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:356:9: ( 'ANY' | 'any' )
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
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:356:10: 'ANY'
                    {
                    match("ANY"); 



                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:356:16: 'any'
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:359:9: ( ( 'NONE' | 'none' ) )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:359:13: ( 'NONE' | 'none' )
            {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:359:13: ( 'NONE' | 'none' )
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
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:359:14: 'NONE'
                    {
                    match("NONE"); 



                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:359:21: 'none'
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:360:5: ( ( 'MAX' | 'max' ) )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:360:9: ( 'MAX' | 'max' )
            {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:360:9: ( 'MAX' | 'max' )
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
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:360:10: 'MAX'
                    {
                    match("MAX"); 



                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:360:16: 'max'
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:361:5: ( ( 'MIN' | 'min' ) )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:361:9: ( 'MIN' | 'min' )
            {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:361:9: ( 'MIN' | 'min' )
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
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:361:10: 'MIN'
                    {
                    match("MIN"); 



                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:361:16: 'min'
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:362:9: ( ( 'MEAN' | 'mean' ) )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:362:13: ( 'MEAN' | 'mean' )
            {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:362:13: ( 'MEAN' | 'mean' )
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
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:362:14: 'MEAN'
                    {
                    match("MEAN"); 



                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:362:21: 'mean'
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:363:9: ( ( 'MEDIAN' | 'median' ) )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:363:13: ( 'MEDIAN' | 'median' )
            {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:363:13: ( 'MEDIAN' | 'median' )
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
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:363:14: 'MEDIAN'
                    {
                    match("MEDIAN"); 



                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:363:23: 'median'
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

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:365:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:365:9: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:365:33: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( ((LA16_0 >= '0' && LA16_0 <= '9')||(LA16_0 >= 'A' && LA16_0 <= 'Z')||LA16_0=='_'||(LA16_0 >= 'a' && LA16_0 <= 'z')) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
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
            	    break loop16;
                }
            } while (true);


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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:368:5: ( ( '-' )? ( '0' .. '9' )+ )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:368:9: ( '-' )? ( '0' .. '9' )+
            {
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:368:9: ( '-' )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0=='-') ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:368:10: '-'
                    {
                    match('-'); 

                    }
                    break;

            }


            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:368:16: ( '0' .. '9' )+
            int cnt18=0;
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( ((LA18_0 >= '0' && LA18_0 <= '9')) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:
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
            	    if ( cnt18 >= 1 ) break loop18;
                        EarlyExitException eee =
                            new EarlyExitException(18, input);
                        throw eee;
                }
                cnt18++;
            } while (true);


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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:372:5: ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | ( '-' )? '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '-' )? ( '0' .. '9' )+ EXPONENT )
            int alt28=3;
            alt28 = dfa28.predict(input);
            switch (alt28) {
                case 1 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:372:9: ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )?
                    {
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:372:9: ( '-' )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);

                    if ( (LA19_0=='-') ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:372:10: '-'
                            {
                            match('-'); 

                            }
                            break;

                    }


                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:372:16: ( '0' .. '9' )+
                    int cnt20=0;
                    loop20:
                    do {
                        int alt20=2;
                        int LA20_0 = input.LA(1);

                        if ( ((LA20_0 >= '0' && LA20_0 <= '9')) ) {
                            alt20=1;
                        }


                        switch (alt20) {
                    	case 1 :
                    	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:
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
                    	    if ( cnt20 >= 1 ) break loop20;
                                EarlyExitException eee =
                                    new EarlyExitException(20, input);
                                throw eee;
                        }
                        cnt20++;
                    } while (true);


                    match('.'); 

                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:372:32: ( '0' .. '9' )*
                    loop21:
                    do {
                        int alt21=2;
                        int LA21_0 = input.LA(1);

                        if ( ((LA21_0 >= '0' && LA21_0 <= '9')) ) {
                            alt21=1;
                        }


                        switch (alt21) {
                    	case 1 :
                    	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:
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
                    	    break loop21;
                        }
                    } while (true);


                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:372:44: ( EXPONENT )?
                    int alt22=2;
                    int LA22_0 = input.LA(1);

                    if ( (LA22_0=='E'||LA22_0=='e') ) {
                        alt22=1;
                    }
                    switch (alt22) {
                        case 1 :
                            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:372:44: EXPONENT
                            {
                            mEXPONENT(); 


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:373:9: ( '-' )? '.' ( '0' .. '9' )+ ( EXPONENT )?
                    {
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:373:9: ( '-' )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);

                    if ( (LA23_0=='-') ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:373:10: '-'
                            {
                            match('-'); 

                            }
                            break;

                    }


                    match('.'); 

                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:373:20: ( '0' .. '9' )+
                    int cnt24=0;
                    loop24:
                    do {
                        int alt24=2;
                        int LA24_0 = input.LA(1);

                        if ( ((LA24_0 >= '0' && LA24_0 <= '9')) ) {
                            alt24=1;
                        }


                        switch (alt24) {
                    	case 1 :
                    	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:
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
                    	    if ( cnt24 >= 1 ) break loop24;
                                EarlyExitException eee =
                                    new EarlyExitException(24, input);
                                throw eee;
                        }
                        cnt24++;
                    } while (true);


                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:373:32: ( EXPONENT )?
                    int alt25=2;
                    int LA25_0 = input.LA(1);

                    if ( (LA25_0=='E'||LA25_0=='e') ) {
                        alt25=1;
                    }
                    switch (alt25) {
                        case 1 :
                            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:373:32: EXPONENT
                            {
                            mEXPONENT(); 


                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:374:9: ( '-' )? ( '0' .. '9' )+ EXPONENT
                    {
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:374:9: ( '-' )?
                    int alt26=2;
                    int LA26_0 = input.LA(1);

                    if ( (LA26_0=='-') ) {
                        alt26=1;
                    }
                    switch (alt26) {
                        case 1 :
                            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:374:10: '-'
                            {
                            match('-'); 

                            }
                            break;

                    }


                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:374:16: ( '0' .. '9' )+
                    int cnt27=0;
                    loop27:
                    do {
                        int alt27=2;
                        int LA27_0 = input.LA(1);

                        if ( ((LA27_0 >= '0' && LA27_0 <= '9')) ) {
                            alt27=1;
                        }


                        switch (alt27) {
                    	case 1 :
                    	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:
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
                    	    if ( cnt27 >= 1 ) break loop27;
                                EarlyExitException eee =
                                    new EarlyExitException(27, input);
                                throw eee;
                        }
                        cnt27++;
                    } while (true);


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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:377:5: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:377:9: ( ' ' | '\\t' | '\\r' | '\\n' )
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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:386:5: ( '\\'' ( ESC_SEQ |normal=~ ( '\\'' | '\\\\' ) )* '\\'' )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:387:5: '\\'' ( ESC_SEQ |normal=~ ( '\\'' | '\\\\' ) )* '\\''
            {
            match('\''); 

            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:388:5: ( ESC_SEQ |normal=~ ( '\\'' | '\\\\' ) )*
            loop29:
            do {
                int alt29=3;
                int LA29_0 = input.LA(1);

                if ( (LA29_0=='\\') ) {
                    alt29=1;
                }
                else if ( ((LA29_0 >= '\u0000' && LA29_0 <= '&')||(LA29_0 >= '(' && LA29_0 <= '[')||(LA29_0 >= ']' && LA29_0 <= '\uFFFF')) ) {
                    alt29=2;
                }


                switch (alt29) {
            	case 1 :
            	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:388:7: ESC_SEQ
            	    {
            	    mESC_SEQ(); 


            	    lBuf.append(getText());

            	    }
            	    break;
            	case 2 :
            	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:390:7: normal=~ ( '\\'' | '\\\\' )
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
            	    break loop29;
                }
            } while (true);


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

            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:400:5: ( '\\\\' ( 'n' | 'r' | 't' | 'b' | 'f' | '\"' | '\\'' | '/' | '\\\\' | ( 'u' )+ i= HEX_DIGIT j= HEX_DIGIT k= HEX_DIGIT l= HEX_DIGIT ) )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:400:9: '\\\\' ( 'n' | 'r' | 't' | 'b' | 'f' | '\"' | '\\'' | '/' | '\\\\' | ( 'u' )+ i= HEX_DIGIT j= HEX_DIGIT k= HEX_DIGIT l= HEX_DIGIT )
            {
            match('\\'); 

            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:401:9: ( 'n' | 'r' | 't' | 'b' | 'f' | '\"' | '\\'' | '/' | '\\\\' | ( 'u' )+ i= HEX_DIGIT j= HEX_DIGIT k= HEX_DIGIT l= HEX_DIGIT )
            int alt31=10;
            switch ( input.LA(1) ) {
            case 'n':
                {
                alt31=1;
                }
                break;
            case 'r':
                {
                alt31=2;
                }
                break;
            case 't':
                {
                alt31=3;
                }
                break;
            case 'b':
                {
                alt31=4;
                }
                break;
            case 'f':
                {
                alt31=5;
                }
                break;
            case '\"':
                {
                alt31=6;
                }
                break;
            case '\'':
                {
                alt31=7;
                }
                break;
            case '/':
                {
                alt31=8;
                }
                break;
            case '\\':
                {
                alt31=9;
                }
                break;
            case 'u':
                {
                alt31=10;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                throw nvae;

            }

            switch (alt31) {
                case 1 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:401:17: 'n'
                    {
                    match('n'); 

                    setText("\n");

                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:402:17: 'r'
                    {
                    match('r'); 

                    setText("\r");

                    }
                    break;
                case 3 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:403:17: 't'
                    {
                    match('t'); 

                    setText("\t");

                    }
                    break;
                case 4 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:404:17: 'b'
                    {
                    match('b'); 

                    setText("\b");

                    }
                    break;
                case 5 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:405:17: 'f'
                    {
                    match('f'); 

                    setText("\f");

                    }
                    break;
                case 6 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:406:17: '\"'
                    {
                    match('\"'); 

                    setText("\"");

                    }
                    break;
                case 7 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:407:17: '\\''
                    {
                    match('\''); 

                    setText("\'");

                    }
                    break;
                case 8 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:408:17: '/'
                    {
                    match('/'); 

                    setText("/");

                    }
                    break;
                case 9 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:409:17: '\\\\'
                    {
                    match('\\'); 

                    setText("\\");

                    }
                    break;
                case 10 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:410:17: ( 'u' )+ i= HEX_DIGIT j= HEX_DIGIT k= HEX_DIGIT l= HEX_DIGIT
                    {
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:410:17: ( 'u' )+
                    int cnt30=0;
                    loop30:
                    do {
                        int alt30=2;
                        int LA30_0 = input.LA(1);

                        if ( (LA30_0=='u') ) {
                            alt30=1;
                        }


                        switch (alt30) {
                    	case 1 :
                    	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:410:18: 'u'
                    	    {
                    	    match('u'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt30 >= 1 ) break loop30;
                                EarlyExitException eee =
                                    new EarlyExitException(30, input);
                                throw eee;
                        }
                        cnt30++;
                    } while (true);


                    int iStart1064 = getCharIndex();
                    int iStartLine1064 = getLine();
                    int iStartCharPos1064 = getCharPositionInLine();
                    mHEX_DIGIT(); 
                    i = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, iStart1064, getCharIndex()-1);
                    i.setLine(iStartLine1064);
                    i.setCharPositionInLine(iStartCharPos1064);


                    int jStart1068 = getCharIndex();
                    int jStartLine1068 = getLine();
                    int jStartCharPos1068 = getCharPositionInLine();
                    mHEX_DIGIT(); 
                    j = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, jStart1068, getCharIndex()-1);
                    j.setLine(jStartLine1068);
                    j.setCharPositionInLine(jStartCharPos1068);


                    int kStart1072 = getCharIndex();
                    int kStartLine1072 = getLine();
                    int kStartCharPos1072 = getCharPositionInLine();
                    mHEX_DIGIT(); 
                    k = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, kStart1072, getCharIndex()-1);
                    k.setLine(kStartLine1072);
                    k.setCharPositionInLine(kStartCharPos1072);


                    int lStart1076 = getCharIndex();
                    int lStartLine1076 = getLine();
                    int lStartCharPos1076 = getCharPositionInLine();
                    mHEX_DIGIT(); 
                    l = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, lStart1076, getCharIndex()-1);
                    l.setLine(lStartLine1076);
                    l.setCharPositionInLine(lStartCharPos1076);


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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:414:5: ( '=' ( STRING | ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' )* ) )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:414:9: '=' ( STRING | ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' )* )
            {
            match('='); 

            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:414:13: ( STRING | ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' )* )
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0=='\'') ) {
                alt33=1;
            }
            else {
                alt33=2;
            }
            switch (alt33) {
                case 1 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:414:14: STRING
                    {
                    mSTRING(); 



                                /* String uses setText which drops the '='. Put it back so it is the same as the other branch. */ 
                                setText("=" + getText());
                                

                    }
                    break;
                case 2 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:419:11: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' )*
                    {
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:419:11: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' )*
                    loop32:
                    do {
                        int alt32=2;
                        int LA32_0 = input.LA(1);

                        if ( (LA32_0=='.'||(LA32_0 >= '0' && LA32_0 <= '9')||(LA32_0 >= 'A' && LA32_0 <= 'Z')||(LA32_0 >= 'a' && LA32_0 <= 'z')) ) {
                            alt32=1;
                        }


                        switch (alt32) {
                    	case 1 :
                    	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:
                    	    {
                    	    if ( input.LA(1)=='.'||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
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
                    	    break loop32;
                        }
                    } while (true);


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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:424:10: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:424:12: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:424:22: ( '+' | '-' )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0=='+'||LA34_0=='-') ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:
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


            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:424:33: ( '0' .. '9' )+
            int cnt35=0;
            loop35:
            do {
                int alt35=2;
                int LA35_0 = input.LA(1);

                if ( ((LA35_0 >= '0' && LA35_0 <= '9')) ) {
                    alt35=1;
                }


                switch (alt35) {
            	case 1 :
            	    // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:
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
            	    if ( cnt35 >= 1 ) break loop35;
                        EarlyExitException eee =
                            new EarlyExitException(35, input);
                        throw eee;
                }
                cnt35++;
            } while (true);


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
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:427:11: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:
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

    public void mTokens() throws RecognitionException {
        // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:8: ( T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | TIME_UNIT | OR | AND | NOT | CONFIG_IS | CONFIG_AND | EQUALS | NOTEQUALS | LT | LTEQ | GT | GTEQ | PLUS | MINUS | MULT | DIV | MOD | REGEX | CONTAINS | ALL | ANY | NONE | MAX | MIN | MEAN | MEDIAN | ID | INT | FLOAT | WS | STRING | CONFIG_VAL )
        int alt36=41;
        alt36 = dfa36.predict(input);
        switch (alt36) {
            case 1 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:10: T__39
                {
                mT__39(); 


                }
                break;
            case 2 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:16: T__40
                {
                mT__40(); 


                }
                break;
            case 3 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:22: T__41
                {
                mT__41(); 


                }
                break;
            case 4 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:28: T__42
                {
                mT__42(); 


                }
                break;
            case 5 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:34: T__43
                {
                mT__43(); 


                }
                break;
            case 6 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:40: T__44
                {
                mT__44(); 


                }
                break;
            case 7 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:46: T__45
                {
                mT__45(); 


                }
                break;
            case 8 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:52: T__46
                {
                mT__46(); 


                }
                break;
            case 9 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:58: T__47
                {
                mT__47(); 


                }
                break;
            case 10 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:64: TIME_UNIT
                {
                mTIME_UNIT(); 


                }
                break;
            case 11 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:74: OR
                {
                mOR(); 


                }
                break;
            case 12 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:77: AND
                {
                mAND(); 


                }
                break;
            case 13 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:81: NOT
                {
                mNOT(); 


                }
                break;
            case 14 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:85: CONFIG_IS
                {
                mCONFIG_IS(); 


                }
                break;
            case 15 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:95: CONFIG_AND
                {
                mCONFIG_AND(); 


                }
                break;
            case 16 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:106: EQUALS
                {
                mEQUALS(); 


                }
                break;
            case 17 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:113: NOTEQUALS
                {
                mNOTEQUALS(); 


                }
                break;
            case 18 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:123: LT
                {
                mLT(); 


                }
                break;
            case 19 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:126: LTEQ
                {
                mLTEQ(); 


                }
                break;
            case 20 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:131: GT
                {
                mGT(); 


                }
                break;
            case 21 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:134: GTEQ
                {
                mGTEQ(); 


                }
                break;
            case 22 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:139: PLUS
                {
                mPLUS(); 


                }
                break;
            case 23 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:144: MINUS
                {
                mMINUS(); 


                }
                break;
            case 24 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:150: MULT
                {
                mMULT(); 


                }
                break;
            case 25 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:155: DIV
                {
                mDIV(); 


                }
                break;
            case 26 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:159: MOD
                {
                mMOD(); 


                }
                break;
            case 27 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:163: REGEX
                {
                mREGEX(); 


                }
                break;
            case 28 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:169: CONTAINS
                {
                mCONTAINS(); 


                }
                break;
            case 29 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:178: ALL
                {
                mALL(); 


                }
                break;
            case 30 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:182: ANY
                {
                mANY(); 


                }
                break;
            case 31 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:186: NONE
                {
                mNONE(); 


                }
                break;
            case 32 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:191: MAX
                {
                mMAX(); 


                }
                break;
            case 33 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:195: MIN
                {
                mMIN(); 


                }
                break;
            case 34 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:199: MEAN
                {
                mMEAN(); 


                }
                break;
            case 35 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:204: MEDIAN
                {
                mMEDIAN(); 


                }
                break;
            case 36 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:211: ID
                {
                mID(); 


                }
                break;
            case 37 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:214: INT
                {
                mINT(); 


                }
                break;
            case 38 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:218: FLOAT
                {
                mFLOAT(); 


                }
                break;
            case 39 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:224: WS
                {
                mWS(); 


                }
                break;
            case 40 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:227: STRING
                {
                mSTRING(); 


                }
                break;
            case 41 :
                // /Users/goose/workspace_eclipse_test_remove/swan-sense/interdroid-swan-master/SwanExpression.g:1:234: CONFIG_VAL
                {
                mCONFIG_VAL(); 


                }
                break;

        }

    }


    protected DFA28 dfa28 = new DFA28(this);
    protected DFA36 dfa36 = new DFA36(this);
    static final String DFA28_eotS =
        "\6\uffff";
    static final String DFA28_eofS =
        "\6\uffff";
    static final String DFA28_minS =
        "\1\55\2\56\3\uffff";
    static final String DFA28_maxS =
        "\2\71\1\145\3\uffff";
    static final String DFA28_acceptS =
        "\3\uffff\1\2\1\1\1\3";
    static final String DFA28_specialS =
        "\6\uffff}>";
    static final String[] DFA28_transitionS = {
            "\1\1\1\3\1\uffff\12\2",
            "\1\3\1\uffff\12\2",
            "\1\4\1\uffff\12\2\13\uffff\1\5\37\uffff\1\5",
            "",
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
            return "371:1: FLOAT : ( ( '-' )? ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | ( '-' )? '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '-' )? ( '0' .. '9' )+ EXPONENT );";
        }
    }
    static final String DFA36_eotS =
        "\4\uffff\1\51\5\uffff\6\53\1\uffff\2\45\1\66\2\45\1\74\2\45\1\100"+
        "\1\103\1\105\1\uffff\1\106\3\uffff\4\45\1\uffff\1\113\5\uffff\1"+
        "\53\6\45\2\20\2\uffff\4\45\2\uffff\2\45\10\uffff\4\45\1\uffff\1"+
        "\142\1\143\2\45\1\142\1\143\2\45\1\65\1\150\1\151\1\65\1\150\1\151"+
        "\1\74\1\45\1\74\5\45\2\uffff\1\160\1\45\1\160\1\45\2\uffff\2\163"+
        "\4\45\1\uffff\2\45\1\uffff\2\172\2\45\2\175\1\uffff\2\45\1\uffff"+
        "\2\45\2\u0082\1\uffff";
    static final String DFA36_eofS =
        "\u0083\uffff";
    static final String DFA36_minS =
        "\1\11\3\uffff\1\60\5\uffff\6\60\1\uffff\1\162\1\122\1\46\1\154\1"+
        "\114\1\75\1\157\1\117\1\47\2\75\1\uffff\1\56\3\uffff\1\145\1\105"+
        "\1\157\1\117\1\uffff\1\56\5\uffff\1\60\1\170\1\156\1\141\1\130\1"+
        "\116\1\101\2\60\2\uffff\1\144\1\154\1\104\1\114\2\uffff\1\156\1"+
        "\116\10\uffff\1\147\1\107\1\156\1\116\1\uffff\2\60\1\156\1\151\2"+
        "\60\1\116\1\111\7\60\1\145\1\60\1\105\1\145\1\105\1\164\1\124\2"+
        "\uffff\1\60\1\141\1\60\1\101\2\uffff\2\60\1\170\1\130\1\141\1\101"+
        "\1\uffff\1\156\1\116\1\uffff\2\60\1\151\1\111\2\60\1\uffff\1\156"+
        "\1\116\1\uffff\1\163\1\123\2\60\1\uffff";
    static final String DFA36_maxS =
        "\1\175\3\uffff\1\71\5\uffff\6\172\1\uffff\1\162\1\122\1\46\1\156"+
        "\1\116\1\75\1\157\1\117\1\172\1\76\1\75\1\uffff\1\71\3\uffff\1\145"+
        "\1\105\1\157\1\117\1\uffff\1\145\5\uffff\1\172\1\170\1\156\1\144"+
        "\1\130\1\116\1\104\2\172\2\uffff\1\171\1\154\1\131\1\114\2\uffff"+
        "\1\164\1\124\10\uffff\1\147\1\107\1\156\1\116\1\uffff\2\172\1\156"+
        "\1\151\2\172\1\116\1\111\7\172\1\145\1\172\1\105\1\145\1\105\1\164"+
        "\1\124\2\uffff\1\172\1\141\1\172\1\101\2\uffff\2\172\1\170\1\130"+
        "\1\141\1\101\1\uffff\1\156\1\116\1\uffff\2\172\1\151\1\111\2\172"+
        "\1\uffff\1\156\1\116\1\uffff\1\163\1\123\2\172\1\uffff";
    static final String DFA36_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\uffff\1\5\1\6\1\7\1\10\1\11\6\uffff\1\13"+
        "\13\uffff\1\26\1\uffff\1\30\1\31\1\32\4\uffff\1\44\1\uffff\1\47"+
        "\1\50\1\4\1\46\1\12\11\uffff\1\14\1\17\4\uffff\1\21\1\15\2\uffff"+
        "\1\20\1\16\1\51\1\23\1\22\1\25\1\24\1\27\4\uffff\1\45\26\uffff\1"+
        "\40\1\41\4\uffff\1\36\1\35\6\uffff\1\42\2\uffff\1\37\6\uffff\1\33"+
        "\2\uffff\1\43\4\uffff\1\34";
    static final String DFA36_specialS =
        "\u0083\uffff}>";
    static final String[] DFA36_transitionS = {
            "\2\47\2\uffff\1\47\22\uffff\1\47\1\26\3\uffff\1\40\1\23\1\50"+
            "\1\1\1\2\1\36\1\34\1\3\1\35\1\4\1\37\12\46\1\5\1\uffff\1\32"+
            "\1\31\1\33\1\6\1\7\1\25\1\45\1\44\4\45\1\13\4\45\1\15\1\30\1"+
            "\22\2\45\1\42\1\17\7\45\4\uffff\1\45\1\uffff\1\24\1\45\1\43"+
            "\4\45\1\12\4\45\1\14\1\27\1\21\2\45\1\41\1\16\7\45\1\10\1\20"+
            "\1\11",
            "",
            "",
            "",
            "\12\52",
            "",
            "",
            "",
            "",
            "",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\1\55\3\45\1\57\3"+
            "\45\1\56\11\45\1\54\7\45",
            "\12\45\7\uffff\1\60\3\45\1\62\3\45\1\61\21\45\4\uffff\1\45"+
            "\1\uffff\32\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "",
            "\1\63",
            "\1\64",
            "\1\65",
            "\1\70\1\uffff\1\67",
            "\1\72\1\uffff\1\71",
            "\1\73",
            "\1\75",
            "\1\76",
            "\1\101\6\uffff\1\101\1\uffff\12\101\3\uffff\1\77\3\uffff\32"+
            "\101\6\uffff\32\101",
            "\1\102\1\73",
            "\1\104",
            "",
            "\1\52\1\uffff\12\46",
            "",
            "",
            "",
            "\1\107",
            "\1\110",
            "\1\111",
            "\1\112",
            "",
            "\1\52\1\uffff\12\46\13\uffff\1\52\37\uffff\1\52",
            "",
            "",
            "",
            "",
            "",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\1\114",
            "\1\115",
            "\1\116\2\uffff\1\117",
            "\1\120",
            "\1\121",
            "\1\122\2\uffff\1\123",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "",
            "",
            "\1\124\24\uffff\1\125",
            "\1\126",
            "\1\127\24\uffff\1\130",
            "\1\131",
            "",
            "",
            "\1\133\5\uffff\1\132",
            "\1\135\5\uffff\1\134",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\136",
            "\1\137",
            "\1\140",
            "\1\141",
            "",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\1\144",
            "\1\145",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\1\146",
            "\1\147",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\1\152",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\1\153",
            "\1\154",
            "\1\155",
            "\1\156",
            "\1\157",
            "",
            "",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\1\161",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\1\162",
            "",
            "",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\1\164",
            "\1\165",
            "\1\166",
            "\1\167",
            "",
            "\1\170",
            "\1\171",
            "",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\1\173",
            "\1\174",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "",
            "\1\176",
            "\1\177",
            "",
            "\1\u0080",
            "\1\u0081",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            "\12\45\7\uffff\32\45\4\uffff\1\45\1\uffff\32\45",
            ""
    };

    static final short[] DFA36_eot = DFA.unpackEncodedString(DFA36_eotS);
    static final short[] DFA36_eof = DFA.unpackEncodedString(DFA36_eofS);
    static final char[] DFA36_min = DFA.unpackEncodedStringToUnsignedChars(DFA36_minS);
    static final char[] DFA36_max = DFA.unpackEncodedStringToUnsignedChars(DFA36_maxS);
    static final short[] DFA36_accept = DFA.unpackEncodedString(DFA36_acceptS);
    static final short[] DFA36_special = DFA.unpackEncodedString(DFA36_specialS);
    static final short[][] DFA36_transition;

    static {
        int numStates = DFA36_transitionS.length;
        DFA36_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA36_transition[i] = DFA.unpackEncodedString(DFA36_transitionS[i]);
        }
    }

    class DFA36 extends DFA {

        public DFA36(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 36;
            this.eot = DFA36_eot;
            this.eof = DFA36_eof;
            this.min = DFA36_min;
            this.max = DFA36_max;
            this.accept = DFA36_accept;
            this.special = DFA36_special;
            this.transition = DFA36_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | TIME_UNIT | OR | AND | NOT | CONFIG_IS | CONFIG_AND | EQUALS | NOTEQUALS | LT | LTEQ | GT | GTEQ | PLUS | MINUS | MULT | DIV | MOD | REGEX | CONTAINS | ALL | ANY | NONE | MAX | MIN | MEAN | MEDIAN | ID | INT | FLOAT | WS | STRING | CONFIG_VAL );";
        }
    }
 

}