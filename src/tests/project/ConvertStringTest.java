package project;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConvertStringTest {
    private final char[] arithSymbols = {'+', '-', '*'};
    private final char[] boolSymbols = {'&', '|' , '=', '>', '<'};

    private static ConvertString cs = new ConvertString("");

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getConvertedString() {
        cs = new ConvertString("filter{(element>10)}%>%filter{(element<20)}");
        assertEquals("filter{((element>10)&(element<20))}%>%map{element}", cs.getConvertedString());

        cs = new ConvertString("filter{(element>10)}%>%map{(element+10)}%>%filter{(element>20)}");
        assertEquals("filter{((element>10)&((element+10)>20))}%>%map{(element+10)}", cs.getConvertedString());

        cs = new ConvertString("");
        assertEquals("SYNTAX ERROR", cs.getConvertedString());

        cs = new ConvertString("map{(element+10)}");
        assertEquals("filter{}%>%map{(element+10)}", cs.getConvertedString());

        cs = new ConvertString("filter{(element>100)}");
        assertEquals("filter{(element>100)}%>%map{element}", cs.getConvertedString());
    }

    @Test
    void checkChain() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            cs.checkAndConvertChain("map{(element>0)}");
        });
        assertEquals("TYPE ERROR", thrown.getMessage());

        thrown = assertThrows(IllegalArgumentException.class, () -> {
            cs.checkAndConvertChain("map{(element>0)");
        });
        assertEquals("SYNTAX ERROR", thrown.getMessage());
    }

    @Test
    void checkExpression() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            cs.checkExpression("(element=0)", arithSymbols);
        });
        assertEquals("TYPE ERROR", thrown.getMessage());

        thrown = assertThrows(IllegalArgumentException.class, () -> {
            cs.checkExpression("((element+10)&(element>10))", arithSymbols);
        });
        assertEquals("TYPE ERROR", thrown.getMessage());

        thrown = assertThrows(IllegalArgumentException.class, () -> {
            cs.checkExpression("((element*element)", arithSymbols);
        });
        assertEquals("SYNTAX ERROR", thrown.getMessage());

        thrown = assertThrows(IllegalArgumentException.class, () -> {
            cs.checkExpression("((element*element) + 10", arithSymbols);
        });
        assertEquals("SYNTAX ERROR", thrown.getMessage());

        assertDoesNotThrow(() -> {
            cs.checkExpression("((element*element)+12230)", arithSymbols);
        });

        assertDoesNotThrow(() -> {
            cs.checkExpression("(-10+((element*element)+12230))", arithSymbols);
        });

        thrown = assertThrows(IllegalArgumentException.class, () -> {
            cs.checkExpression("(((5*element))) + 10)", arithSymbols);
        });
        assertEquals("SYNTAX ERROR", thrown.getMessage());
    }

    @Test
    void tryParseLong() {
        assertFalse(cs.tryParseLong("number"));
        assertFalse(cs.tryParseLong("1.2"));

        assertTrue(cs.tryParseLong("-15"));
        assertTrue(cs.tryParseLong("1000321"));
    }

    @Test
    void containsChar() {
        assertTrue(cs.containsChar('+', arithSymbols));
        assertFalse(cs.containsChar('?', arithSymbols));

        assertTrue(cs.containsChar('=', boolSymbols));
        assertFalse(cs.containsChar('!', boolSymbols));
    }
}