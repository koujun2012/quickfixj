package quickfix.mina.message;

import junit.framework.TestCase;
import quickfix.DefaultMessageFactory;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.field.BeginString;
import quickfix.field.MsgType;
import quickfix.field.SenderCompID;
import quickfix.field.TargetCompID;
import quickfix.fix40.Logon;

public class MessageUtilsTest extends TestCase {
    public void testGetStringField() throws Exception {
        String messageString = "8=FIX.4.2\0019=12\00135=X\001108=30\00110=049\001";
        assertEquals("wrong value", "FIX.4.2", MessageUtils.getStringField(messageString,
                BeginString.FIELD));
        assertEquals("wrong value", "X", MessageUtils.getStringField(messageString, MsgType.FIELD));
        assertNull(messageString, MessageUtils.getStringField(messageString, SenderCompID.FIELD));
    }

    public void testSessionIdFromMessage() throws Exception {
        Message message = new Logon();
        message.getHeader().setString(SenderCompID.FIELD, "TW");
        message.getHeader().setString(TargetCompID.FIELD, "ISLD");
        SessionID sessionID = MessageUtils.getSessionID(message);
        assertEquals(sessionID.getBeginString(), "FIX.4.0");
        assertEquals("TW", sessionID.getSenderCompID());
        assertEquals("ISLD", sessionID.getTargetCompID());
    }

    public void testRemoteSessionIdFromMessage() throws Exception {
        Message message = new Logon();
        message.getHeader().setString(SenderCompID.FIELD, "TW");
        message.getHeader().setString(TargetCompID.FIELD, "ISLD");
        SessionID sessionID = MessageUtils.getRemoteSessionID(message);
        assertEquals(sessionID.getBeginString(), "FIX.4.0");
        assertEquals("ISLD", sessionID.getSenderCompID());
        assertEquals("TW", sessionID.getTargetCompID());
    }
    
    public void testRemoteSessionIdFromMessageWithMissingFields() throws Exception {
        Message message = new Logon();
        SessionID sessionID = MessageUtils.getRemoteSessionID(message);
        assertEquals(sessionID.getBeginString(), "FIX.4.0");
        assertNull(sessionID.getSenderCompID());
        assertNull(sessionID.getTargetCompID());
    }

    public void testSessionIdFromRawMessage() throws Exception {
        String messageString = "8=FIX.4.09=5635=A34=149=TW52=20060118-16:34:1956=ISLD98=0108=210=223";
        SessionID sessionID = MessageUtils.getSessionID(messageString);
        assertEquals(sessionID.getBeginString(), "FIX.4.0");
        assertEquals("TW", sessionID.getSenderCompID());
        assertEquals("ISLD", sessionID.getTargetCompID());
    }

    public void testRemoteSessionIdFromRawMessage() throws Exception {
        String messageString = "8=FIX.4.09=5635=A34=149=TW52=20060118-16:34:1956=ISLD98=0108=210=223";
        SessionID sessionID = MessageUtils.getRemoteSessionID(messageString);
        assertEquals(sessionID.getBeginString(), "FIX.4.0");
        assertEquals("ISLD", sessionID.getSenderCompID());
        assertEquals("TW", sessionID.getTargetCompID());
    }

    public void testMessageType() throws Exception {
        String messageString = "8=FIX.4.09=5635=A34=149=TW52=20060118-16:34:1956=ISLD98=0108=210=223";
        assertEquals("A", MessageUtils.getMessageType(messageString));
    }

    public void testMessageTypeError() throws Exception {
        String messageString = "8=FIX.4.09=5634=149=TW52=20060118-16:34:1956=ISLD98=0108=210=223";
        try {
            MessageUtils.getMessageType(messageString);
            fail("expected exception");
        } catch (InvalidMessage e) {
            // expected
        }
    }

    public void testMessageTypeError2() throws Exception {
        String messageString = "8=FIX.4.09=5635=1";
        try {
            MessageUtils.getMessageType(messageString);
            fail("expected exception");
        } catch (InvalidMessage e) {
            // expected
        }
    }

    public void testGetNonexistentStringField() throws Exception {
        String messageString = "8=FIX.4.09=5634=149=TW52=20060118-16:34:1956=ISLD98=0108=210=223";
        assertNull(MessageUtils.getStringField(messageString, 35));
    }

    public void testGetStringFieldWithBadValue() throws Exception {
        String messageString = "8=FIX.4.09=5634=149=TW52=20060118-16:34:1956=ISLD98=0108=210=223";
        assertNull(MessageUtils.getStringField(messageString, 10));
    }

    public void testParse() throws Exception {
        String messageString = "8=FIX.4.09=5635=A34=149=TW52=20060118-16:34:1956=ISLD98=0108=210=223";
        MessageUtils.parse(new DefaultMessageFactory(), null, messageString);
    }
}