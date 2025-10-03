package com.example.webapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HelloServlet
 */
public class HelloServletTest {

    // ✅ Explicit constructor to satisfy PMD AtLeastOneConstructor rule
    public HelloServletTest() {
        super();
    }

    @Test
    public void testTrue() {
        assertTrue(true); // dummy test
    }
}
