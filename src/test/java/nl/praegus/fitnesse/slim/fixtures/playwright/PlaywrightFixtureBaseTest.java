package nl.praegus.fitnesse.slim.fixtures.playwright;

import static org.junit.jupiter.api.Assertions.assertThrows;

import fitnesse.slim.fixtureInteraction.CachedInteraction;
import fitnesse.slim.fixtureInteraction.FixtureInteraction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import nl.praegus.fitnesse.slim.fixtures.playwright.exceptions.PlaywrightFitnesseException;
import org.junit.jupiter.api.Test;

class PlaywrightFixtureBaseTest {
    /**
     * Method under test: {@link PlaywrightFixtureBase#aroundSlimInvoke(FixtureInteraction, Method, Object[])}
     */
    @Test
    void testAroundSlimInvoke() throws InvocationTargetException {
        PlaywrightFixtureBase playwrightFixtureBase = new PlaywrightFixtureBase();
        assertThrows(PlaywrightFitnesseException.class,
                () -> playwrightFixtureBase.aroundSlimInvoke(new CachedInteraction(), null, "Arguments"));
    }
}

