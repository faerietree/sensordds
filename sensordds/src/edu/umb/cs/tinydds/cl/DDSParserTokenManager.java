package edu.umb.cs.tinydds.cl;

/* Generated By:JavaCC: Do not edit this line. DDSParserTokenManager.java */
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Enumeration;

public class DDSParserTokenManager implements DDSParserConstants
{
  public static  java.io.PrintStream debugStream = System.out;
  public static  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private static final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x60L) != 0L)
         {
            jjmatchedKind = 22;
            return 52;
         }
         return -1;
      case 1:
         if ((active0 & 0x60L) != 0L)
         {
            jjmatchedKind = 22;
            jjmatchedPos = 1;
            return 52;
         }
         return -1;
      case 2:
         if ((active0 & 0x60L) != 0L)
         {
            jjmatchedKind = 22;
            jjmatchedPos = 2;
            return 52;
         }
         return -1;
      case 3:
         if ((active0 & 0x60L) != 0L)
         {
            jjmatchedKind = 22;
            jjmatchedPos = 3;
            return 52;
         }
         return -1;
      case 4:
         if ((active0 & 0x60L) != 0L)
         {
            jjmatchedKind = 22;
            jjmatchedPos = 4;
            return 52;
         }
         return -1;
      case 5:
         if ((active0 & 0x40L) != 0L)
         {
            jjmatchedKind = 22;
            jjmatchedPos = 5;
            return 52;
         }
         if ((active0 & 0x20L) != 0L)
            return 52;
         return -1;
      case 6:
         if ((active0 & 0x40L) != 0L)
         {
            jjmatchedKind = 22;
            jjmatchedPos = 6;
            return 52;
         }
         return -1;
      default :
         return -1;
   }
}
private static final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
static private final int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
static private final int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
static private final int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 40:
         return jjStopAtPos(0, 12);
      case 41:
         return jjStopAtPos(0, 13);
      case 44:
         return jjStopAtPos(0, 18);
      case 60:
         jjmatchedKind = 16;
         return jjMoveStringLiteralDfa1_0(0x8000L);
      case 61:
         return jjStopAtPos(0, 14);
      case 62:
         return jjStopAtPos(0, 17);
      case 80:
         return jjMoveStringLiteralDfa1_0(0x20L);
      case 84:
         return jjMoveStringLiteralDfa1_0(0x40L);
      default :
         return jjMoveNfa_0(1, 0);
   }
}
static private final int jjMoveStringLiteralDfa1_0(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 62:
         if ((active0 & 0x8000L) != 0L)
            return jjStopAtPos(1, 15);
         break;
      case 101:
         return jjMoveStringLiteralDfa2_0(active0, 0x40L);
      case 104:
         return jjMoveStringLiteralDfa2_0(active0, 0x20L);
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
static private final int jjMoveStringLiteralDfa2_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(0, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 101:
         return jjMoveStringLiteralDfa3_0(active0, 0x20L);
      case 109:
         return jjMoveStringLiteralDfa3_0(active0, 0x40L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
static private final int jjMoveStringLiteralDfa3_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 110:
         return jjMoveStringLiteralDfa4_0(active0, 0x20L);
      case 112:
         return jjMoveStringLiteralDfa4_0(active0, 0x40L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
static private final int jjMoveStringLiteralDfa4_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(2, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 111:
         return jjMoveStringLiteralDfa5_0(active0, 0x60L);
      default :
         break;
   }
   return jjStartNfa_0(3, active0);
}
static private final int jjMoveStringLiteralDfa5_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(3, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 109:
         if ((active0 & 0x20L) != 0L)
            return jjStartNfaWithStates_0(5, 5, 52);
         break;
      case 114:
         return jjMoveStringLiteralDfa6_0(active0, 0x40L);
      default :
         break;
   }
   return jjStartNfa_0(4, active0);
}
static private final int jjMoveStringLiteralDfa6_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(4, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa7_0(active0, 0x40L);
      default :
         break;
   }
   return jjStartNfa_0(5, active0);
}
static private final int jjMoveStringLiteralDfa7_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(5, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(6, active0);
      return 7;
   }
   switch(curChar)
   {
      case 108:
         if ((active0 & 0x40L) != 0L)
            return jjStartNfaWithStates_0(7, 6, 52);
         break;
      default :
         break;
   }
   return jjStartNfa_0(6, active0);
}
static private final void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
static private final void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
static private final void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}
static private final void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}
static private final void jjCheckNAddStates(int start)
{
   jjCheckNAdd(jjnextStates[start]);
   jjCheckNAdd(jjnextStates[start + 1]);
}
static private final int jjMoveNfa_0(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 52;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 1:
                  if ((0x3fe000000000000L & l) != 0L)
                  {
                     if (kind > 19)
                        kind = 19;
                     jjCheckNAddStates(0, 2);
                  }
                  else if (curChar == 48)
                  {
                     if (kind > 19)
                        kind = 19;
                     jjCheckNAdd(47);
                  }
                  else if (curChar == 46)
                     jjCheckNAdd(5);
                  break;
               case 52:
               case 7:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 22)
                     kind = 22;
                  jjCheckNAddTwoStates(6, 7);
                  break;
               case 4:
                  if (curChar == 46)
                     jjCheckNAdd(5);
                  break;
               case 5:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjCheckNAdd(5);
                  break;
               case 46:
                  if (curChar != 48)
                     break;
                  if (kind > 19)
                     kind = 19;
                  jjCheckNAdd(47);
                  break;
               case 47:
                  if (curChar == 46)
                     jjCheckNAdd(48);
                  break;
               case 48:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjCheckNAdd(48);
                  break;
               case 49:
                  if ((0x3fe000000000000L & l) == 0L)
                     break;
                  if (kind > 19)
                     kind = 19;
                  jjCheckNAddStates(0, 2);
                  break;
               case 50:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 19)
                     kind = 19;
                  jjCheckNAdd(50);
                  break;
               case 51:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(51, 47);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 1:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 22)
                        kind = 22;
                     jjCheckNAddTwoStates(6, 7);
                  }
                  if (curChar == 97)
                     jjAddStates(3, 4);
                  else if (curChar == 65)
                     jjAddStates(5, 6);
                  else if (curChar == 98)
                     jjAddStates(7, 8);
                  else if (curChar == 66)
                     jjAddStates(9, 10);
                  else if (curChar == 79)
                     jjstateSet[jjnewStateCnt++] = 2;
                  else if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 0;
                  break;
               case 52:
               case 6:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 22)
                     kind = 22;
                  jjCheckNAddTwoStates(6, 7);
                  break;
               case 0:
                  if (curChar == 114 && kind > 11)
                     kind = 11;
                  break;
               case 2:
                  if (curChar == 82 && kind > 11)
                     kind = 11;
                  break;
               case 3:
                  if (curChar == 79)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               case 8:
                  if (curChar == 66)
                     jjAddStates(9, 10);
                  break;
               case 9:
                  if (curChar == 69 && kind > 7)
                     kind = 7;
                  break;
               case 10:
                  if (curChar == 82)
                     jjstateSet[jjnewStateCnt++] = 9;
                  break;
               case 11:
                  if (curChar == 79)
                     jjstateSet[jjnewStateCnt++] = 10;
                  break;
               case 12:
                  if (curChar == 70)
                     jjstateSet[jjnewStateCnt++] = 11;
                  break;
               case 13:
                  if (curChar == 69)
                     jjstateSet[jjnewStateCnt++] = 12;
                  break;
               case 14:
                  if (curChar == 78 && kind > 9)
                     kind = 9;
                  break;
               case 15:
                  if (curChar == 69)
                     jjstateSet[jjnewStateCnt++] = 14;
                  break;
               case 16:
                  if (curChar == 69)
                     jjstateSet[jjnewStateCnt++] = 15;
                  break;
               case 17:
                  if (curChar == 87)
                     jjstateSet[jjnewStateCnt++] = 16;
                  break;
               case 18:
                  if (curChar == 84)
                     jjstateSet[jjnewStateCnt++] = 17;
                  break;
               case 19:
                  if (curChar == 69)
                     jjstateSet[jjnewStateCnt++] = 18;
                  break;
               case 20:
                  if (curChar == 98)
                     jjAddStates(7, 8);
                  break;
               case 21:
                  if (curChar == 101 && kind > 7)
                     kind = 7;
                  break;
               case 22:
                  if (curChar == 114)
                     jjstateSet[jjnewStateCnt++] = 21;
                  break;
               case 23:
                  if (curChar == 111)
                     jjstateSet[jjnewStateCnt++] = 22;
                  break;
               case 24:
                  if (curChar == 102)
                     jjstateSet[jjnewStateCnt++] = 23;
                  break;
               case 25:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 24;
                  break;
               case 26:
                  if (curChar == 110 && kind > 9)
                     kind = 9;
                  break;
               case 27:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 26;
                  break;
               case 28:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 27;
                  break;
               case 29:
                  if (curChar == 119)
                     jjstateSet[jjnewStateCnt++] = 28;
                  break;
               case 30:
                  if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 29;
                  break;
               case 31:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 30;
                  break;
               case 32:
                  if (curChar == 65)
                     jjAddStates(5, 6);
                  break;
               case 33:
                  if (curChar == 82 && kind > 8)
                     kind = 8;
                  break;
               case 34:
                  if (curChar == 69)
                     jjstateSet[jjnewStateCnt++] = 33;
                  break;
               case 35:
                  if (curChar == 84)
                     jjstateSet[jjnewStateCnt++] = 34;
                  break;
               case 36:
                  if (curChar == 70)
                     jjstateSet[jjnewStateCnt++] = 35;
                  break;
               case 37:
                  if (curChar == 68 && kind > 10)
                     kind = 10;
                  break;
               case 38:
                  if (curChar == 78)
                     jjstateSet[jjnewStateCnt++] = 37;
                  break;
               case 39:
                  if (curChar == 97)
                     jjAddStates(3, 4);
                  break;
               case 40:
                  if (curChar == 114 && kind > 8)
                     kind = 8;
                  break;
               case 41:
                  if (curChar == 101)
                     jjstateSet[jjnewStateCnt++] = 40;
                  break;
               case 42:
                  if (curChar == 116)
                     jjstateSet[jjnewStateCnt++] = 41;
                  break;
               case 43:
                  if (curChar == 102)
                     jjstateSet[jjnewStateCnt++] = 42;
                  break;
               case 44:
                  if (curChar == 100 && kind > 10)
                     kind = 10;
                  break;
               case 45:
                  if (curChar == 110)
                     jjstateSet[jjnewStateCnt++] = 44;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 52 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   50, 51, 47, 43, 45, 36, 38, 25, 31, 13, 19, 
};
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, "\120\150\145\156\157\155", 
"\124\145\155\160\157\162\141\154", null, null, null, null, null, "\50", "\51", "\75", "\74\76", "\74", "\76", 
"\54", null, null, null, null, };
public static final String[] lexStateNames = {
   "DEFAULT", 
};
static final long[] jjtoToken = {
   0x5fffe1L, 
};
static final long[] jjtoSkip = {
   0x1eL, 
};
static protected SimpleCharStream input_stream;
static private final int[] jjrounds = new int[52];
static private final int[] jjstateSet = new int[104];
static protected char curChar;
public DDSParserTokenManager(SimpleCharStream stream){
   if (input_stream != null)
      throw new TokenMgrError("ERROR: Second call to constructor of static lexer. You must use ReInit() to initialize the static variables.", TokenMgrError.STATIC_LEXER_ERROR);
   input_stream = stream;
}
public DDSParserTokenManager(SimpleCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}
static public void ReInit(SimpleCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
static private final void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 52; i-- > 0;)
      jjrounds[i] = 0x80000000;
}
static public void ReInit(SimpleCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}
static public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

static protected Token jjFillToken()
{
   Token t = Token.newToken(jjmatchedKind);
   t.kind = jjmatchedKind;
   String im = jjstrLiteralImages[jjmatchedKind];
   t.image = (im == null) ? input_stream.GetImage() : im;
   t.beginLine = input_stream.getBeginLine();
   t.beginColumn = input_stream.getBeginColumn();
   t.endLine = input_stream.getEndLine();
   t.endColumn = input_stream.getEndColumn();
   return t;
}

static int curLexState = 0;
static int defaultLexState = 0;
static int jjnewStateCnt;
static int jjround;
static int jjmatchedPos;
static int jjmatchedKind;

public static Token getNextToken() 
{
  int kind;
  Token specialToken = null;
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {   
   try   
   {     
      curChar = input_stream.BeginToken();
   }     
   catch(java.io.IOException e)
   {        
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   try { input_stream.backup(0);
      while (curChar <= 32 && (0x100002600L & (1L << curChar)) != 0L)
         curChar = input_stream.BeginToken();
   }
   catch (java.io.IOException e1) { continue EOFLoop; }
   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

}
