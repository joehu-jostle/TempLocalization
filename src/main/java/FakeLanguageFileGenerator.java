import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FakeLanguageFileGenerator {

    private static StringBuilder stringBuilder = new StringBuilder();
    private static String pathFromRootToProject = new File("").getAbsolutePath();
    private static Logger logger = Logger.getLogger(FakeLanguageFileGenerator.class.getName());

    public static void main(String args[]) {
        try {
            generateFakeLangPropertiesFile();
            TranslateText.run();
            insertFakeLocaleIntoLanguageFile();
            insertFakeLocaleIntoGwtFile();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private static void generateFakeLangPropertiesFile() throws IOException {
        String filePath = "/src/main/java/com/jostleme/jostle/ui/localization/RichClientStrings.properties";

        try (BufferedReader br = new BufferedReader(new FileReader(pathFromRootToProject +filePath))) {
            br.lines()
                    .filter(s -> !s.isEmpty())
                    .filter(s -> s.charAt(0) != '#')
                    .forEach(FakeLanguageFileGenerator::splitter);
            FileGeneratorHelper.writeFile(stringBuilder.toString(),
                    pathFromRootToProject+"/src/main/java/com/jostleme/jostle/ui/localization/RichClientStrings_pl.properties");
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private static void splitter(String line) {
        int numOfParams = FileGeneratorHelper.getNumOfParameters(line);
        String key[] = line.split("=");

        String value = key[0];
        for (int i = 0; i < numOfParams; i++) {
            value += "{" + i + "}";
        }
        stringBuilder.append(key[0] + "=" + value + "\n");
    }

    private static void insertFakeLocaleIntoLanguageFile() throws IOException {
        Path path = Paths.get(pathFromRootToProject +"/src/main/java/com/jostleme/jostle/common/domain/Language.java");
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        String target = "GERMAN";
        String polishInsertion = "POLISH(\"pl\"),";
        String italianInsertion = "ITALIAN(\"it\"),";
        insertFakeLanguageInfo(lines, target, polishInsertion, italianInsertion);
        FileGeneratorHelper.writeFile(stringBuilder.toString(), path.toString());
    }

    private static String addTabAndNewLine(String str) {
        return "\t" + str + "\n";
    }

    private static void insertFakeLocaleIntoGwtFile() throws IOException {
        Path path = Paths.get(pathFromRootToProject +"/src/main/java/com/jostleme/jostle/RichClient.gwt.xml");
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        String target = "values=\"de\"";
        String polishInsertion = "<extend-property name=\"locale\" values=\"pl\"/>";
        String italianInsertion = "<extend-property name=\"locale\" values=\"it\"/>";
        insertFakeLanguageInfo(lines, target, polishInsertion, italianInsertion);
        FileGeneratorHelper.writeFile(stringBuilder.toString(), path.toString());
    }

    private static void insertFakeLanguageInfo(List<String> lines, String target, String firstInsertion, String secondInsertion) {
        stringBuilder = new StringBuilder();
        boolean hasFakeLanguageAlreadyAdded = false;
        for (String line : lines) {
            if (line.contains(firstInsertion) || line.contains(secondInsertion))
                hasFakeLanguageAlreadyAdded = true;
            if (line.contains(target) && !hasFakeLanguageAlreadyAdded) {
                stringBuilder.append(addTabAndNewLine(firstInsertion));
                stringBuilder.append(addTabAndNewLine(secondInsertion));
            }
            stringBuilder.append(line+"\n");
        }
    }
}
