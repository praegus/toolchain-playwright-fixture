package nl.praegus.fitnesse.slim.fixtures.playwright.di;

import dagger.Component;
import nl.praegus.fitnesse.slim.fixtures.playwright.PlaywrightFixture;

@Component
public interface FilesPathComponent {
    void inject(PlaywrightFixture playwrightFixture);
}
