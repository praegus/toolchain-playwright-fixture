package nl.praegus.fitnesse.slim.fixtures.playwright;

import fitnesse.ContextConfigurator;

import javax.inject.Inject;
import java.nio.file.Path;

public class WikiFilesPath {

    private static final String FITNESSE_DIR = "wiki";
    private static final Path wikiFilesDir = Path.of(ContextConfigurator.DEFAULT_ROOT, "files");

    /**
     * Method to get the path to the wiki files directory.
     * If the fixture is run from the project root (e.g. by using Maven and a junit runner)
     * wiki/FitNesseRoot/files is returned. If run from the wiki FitNesseRoot/files is returned.
     *
     * @return relative path to wiki/FitNesseRoot/files
     */
    public Path getWikiFilesDir() {
        return Boolean.TRUE.equals(runsInFitNesseDir()) ? wikiFilesDir : Path.of(FITNESSE_DIR).resolve(wikiFilesDir);
    }

    private Boolean runsInFitNesseDir() {
        return System.getProperty("user.dir").endsWith(FITNESSE_DIR);
    }
}
