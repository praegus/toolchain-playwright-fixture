package nl.praegus.fitnesse.slim.fixtures.playwright.DI;

import dagger.Component;
import nl.praegus.fitnesse.slim.fixtures.playwright.PlaywrightSetup;

import javax.inject.Singleton;

@Singleton
@Component (modules = PlaywrightModule.class)
public interface PlaywrightComponent {
    void inject(PlaywrightSetup playwrightSetup);
}