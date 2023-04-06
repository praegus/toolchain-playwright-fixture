package nl.praegus.fitnesse.slim.fixtures.playwright.DI;

import dagger.Component;
import nl.praegus.fitnesse.slim.fixtures.playwright.PlaywrightFixture;

@Component
public interface FilesPathComponent {
    void inject(PlaywrightFixture playwrightFixture);
}
