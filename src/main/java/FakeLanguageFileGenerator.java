import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
            write(stringBuilder.toString(), pathFromRootToProject+"/src/main/java/com/jostleme/jostle/ui/localization/RichClientStrings_pl.properties");
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private static void splitter(String line) {
        int numOfParams = takeParameters(line);
        String key[] = line.split("=");

        String value = key[0];
        for (int i = 0; i < numOfParams; i++) {
            value += "{" + i + "}";
        }
        stringBuilder.append(key[0] + "=" + value + "\n");
    }

    private static int takeParameters(String line) {
        int total = 0;
        Set<Integer> set = new HashSet<>();
        while (line.indexOf("{") > 0) {
            char c = line.charAt(line.indexOf("{") + 1);
            int current = Integer.parseInt(String.valueOf(c));
            if (!set.contains(current)) {
                set.add(current);
                total++;
            }
            line = line.substring(line.indexOf("{")+1, line.length());
        }
        return total;
    }

    private static void write(String content, String fileName) {
        try {
            FileWriter fileWriter = new FileWriter(fileName, false);
            fileWriter.write(content);
            fileWriter.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private static void insertFakeLocaleIntoLanguageFile() throws IOException {
        Path path = Paths.get(pathFromRootToProject +"/src/main/java/com/jostleme/jostle/common/domain/Language.java");
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        String target = "JAPANESE";
        stringBuilder = new StringBuilder();

        for (String line : lines) {
            if (line.contains(target)) {
                line = "";
                stringBuilder.append("\tJAPANESE( \"ja\" ),\n\tPOLISH(\"pl\");\n");
            }
            stringBuilder.append(line+"\n");
        }
        write(stringBuilder.toString(), path.toString());
    }

    private static void insertFakeLocaleIntoGwtFile() throws IOException {
        Path path = Paths.get(pathFromRootToProject +"/src/main/java/com/jostleme/jostle/RichClient.gwt.xml");
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        String target = "values=\"ja\"";
        stringBuilder = new StringBuilder();
        boolean flag = false;

        for (String line : lines) {
            if (flag) {
                stringBuilder.append("\t<extend-property name=\"locale\" values=\"pl\"/>\n");
                flag = false;
            }

            if (line.contains(target))
                flag = true;
            stringBuilder.append(line+"\n");
        }
        write(stringBuilder.toString(), path.toString());
    }
}
