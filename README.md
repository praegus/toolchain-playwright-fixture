# toolchain-playwright-fixture

A FitNesse fixture for writing browser tests using the Playwright Java API. 

## Getting started

- Add the fixture as dependency to your FitNesse project.

```xml
<dependency>
    <groupId>nl.praegus</groupId>
    <artifactId>toolchain-playwright-fixture</artifactId>
    <version>1.0.1</version>
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
