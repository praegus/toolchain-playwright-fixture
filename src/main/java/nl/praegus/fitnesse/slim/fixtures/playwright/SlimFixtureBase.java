package nl.praegus.fitnesse.slim.fixtures.playwright;

import fitnesse.ContextConfigurator;
import fitnesse.slim.fixtureInteraction.FixtureInteraction;
import fitnesse.slim.fixtureInteraction.InteractionAwareFixture;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SlimFixtureBase implements InteractionAwareFixture {
    protected final Path wikiFilesDir = Paths.get(ContextConfigurator.DEFAULT_ROOT, "files");

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

