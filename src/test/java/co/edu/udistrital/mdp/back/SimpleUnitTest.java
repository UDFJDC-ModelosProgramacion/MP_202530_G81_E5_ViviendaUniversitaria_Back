package co.edu.udistrital.mdp.back;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleUnitTest {

    @Test
    void simpleAssertion() {
        // simple unit test that doesn't start Spring context
        assertTrue(1 + 1 == 2);
    }
}
