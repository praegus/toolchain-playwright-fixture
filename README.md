# toolchain-playwright-fixture

A FitNesse fixture for writing browser tests using the [Playwright Java API](https://github.com/microsoft/playwright-java).
Based on and inspired by the [HSAC fixtures.](https://github.com/fhoeben/hsac-fitnesse-fixtures)

Only a subset of the available Playwright commands is currently implemented in this fixture.

## Getting started

- Add the fixture as dependency to your FitNesse project.

```xml
<dependency>
    <groupId>nl.praegus</groupId>
    <artifactId>toolchain-playwright-fixture</artifactId>
    <version>1.1.0</version>
</dependency>
```

- Add the fixture to the project imports. For example:

```fitnesse
|Import                                      |
|nl.hsac.fitnesse.fixture                    |
|nl.hsac.fitnesse.fixture.slim               |
|nl.praegus.fitnesse.slim.fixtures.playwright|
```

- To configure and start a browser, use the ```playwright setup``` fixture.

```fitnesse
|script            |playwright setup  |
|set headless      |false             |
|set viewport width|1920|and height|1080|
|start browser     |chromium          |
```

- Start writing tests using the ```playwright fixture```

```fitnesse

^|script| playwright fixture |
|navigate to             |https://praegus.nl/                                   |
|click                   |text=Plan een proof of concept                        |
|assert that page has url|https://praegus.nl/proof-of-concept-aanvraagformulier/|
|ensure                  |is visible  |text=Proof of Concept Aanvraagformulier  |
```

> The first time the fixture is used Playwright will download the Chromium, Firefox and Webkit browsers from the Microsoft CDN. This may take some time. 
> If you are behind a firewall or proxy downloading the browsers might be blocked. The Playwright documentation offers the following solution: [Install behind a firewall or a proxy](https://playwright.dev/java/docs/browsers#install-behind-a-firewall-or-a-proxy)
