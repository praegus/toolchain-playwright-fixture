package nl.praegus.fitnesse.slim.fixtures.playwright;

import static org.junit.jupiter.api.Assertions.assertThrows;

import fitnesse.slim.fixtureInteraction.CachedInteraction;
import fitnesse.slim.fixtureInteraction.FixtureInteraction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import nl.praegus.fitnesse.slim.fixtures.playwright.exceptions.PlaywrightFitnesseException;
import org.junit.jupiter.api.Test;

class SlimFixtureBaseTest {
    /**
     * Method under test: {@link SlimFixtureBase#aroundSlimInvoke(FixtureInteraction, Method, Object[])}
     */
    @Test
    void testAroundSlimInvoke() throws InvocationTargetException {
        SlimFixtureBase slimFixtureBase = new SlimFixtureBase();
        assertThrows(PlaywrightFitnesseException.class,
                () -> slimFixtureBase.aroundSlimInvoke(new CachedInteraction(), null, "Arguments"));
    }
}

