package nl.praegus.fitnesse.slim.fixtures.playwright;

import fitnesse.ContextConfigurator;
import fitnesse.slim.fixtureInteraction.FixtureInteraction;
import fitnesse.slim.fixtureInteraction.InteractionAwareFixture;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;

public class SlimFixtureBase implements InteractionAwareFixture {

    private static final String FITNESSE_DIR = "wiki";
    private final Path wikiFilesDir = Path.of(ContextConfigurator.DEFAULT_ROOT, "files");

    /**
     * Function to get the path to the wiki files directory.
     * If the fixture is run from the project root (e.g. by using Maven and a junit runner)
     * wiki/FitNesseRoot/files is returned. If run from the wiki FitNesseRoot/files is returned.
     * @return relative path to wiki/FitNesseRoot/files
     */
    public Path getWikiFilesDir() {
        return Boolean.TRUE.equals(runsInFitNesseDir()) ? wikiFilesDir : Path.of(FITNESSE_DIR).resolve(wikiFilesDir);
    }

    private Boolean runsInFitNesseDir() {
        return System.getProperty("user.dir").endsWith(FITNESSE_DIR);
    }

    @Override
    public Object aroundSlimInvoke(FixtureInteraction interaction, Method method, Object... arguments) throws InvocationTargetException {
        Object result;
        try {
            result = invoke(interaction, method, arguments);
        } catch (Throwable t) {
            var realEx = stripReflectionException(t);
            var toThrow = handleException(realEx);
            if (toThrow instanceof RuntimeException) {
                throw (PlaywrightFitnesseException) toThrow;
            }
            if (toThrow instanceof Error) {
                throw (Error) toThrow;
            }
            throw wrapInReflectionException(toThrow);
        }
        return result;
    }

    protected Object invoke(FixtureInteraction interaction, Method method, Object[] arguments) throws Throwable {
        return interaction.methodInvoke(method, this, arguments);
    }

    protected Throwable handleException(Throwable t) {
        return new PlaywrightFitnesseException("<div>" + t.getMessage() + "</div>");
    }

    public static Throwable stripReflectionException(Throwable t) {
        Throwable result = t;
        if (t instanceof InvocationTargetException) {
            InvocationTargetException e = (InvocationTargetException) t;
            if (e.getCause() != null) {
                result = e.getCause();
            } else {
                result = e.getTargetException();
            }
        }
        return result;
    }

    public static InvocationTargetException wrapInReflectionException(Throwable t) {
        InvocationTargetException result;
        if (t instanceof InvocationTargetException) {
            result = (InvocationTargetException) t;
        } else {
            result = new InvocationTargetException(t, t.getMessage());
        }
        return result;
    }
}

