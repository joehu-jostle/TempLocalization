import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RandomValueGenerator {

    private static StringBuilder stringBuilder = new StringBuilder();
    private static String pathFromRootToProject = new File("").getAbsolutePath();
    private static Logger logger = Logger.getLogger(RandomValueGenerator.class.getName());

    private static final String STRING_FILE_PATH = "/src/main/java/com/jostleme/jostle/ui/localization/RichClientStrings.properties";
    private static final String BASE_STRING = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer ne.";

    public static void main(String args[]) {
        try {
            generateRandomLengthString();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private static void generateRandomLengthString() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(pathFromRootToProject + STRING_FILE_PATH))) {
            br.lines()
                    .filter(s -> !s.isEmpty())
                    .filter(s -> s.charAt(0) != '#')
                    .forEach(RandomValueGenerator::generateRandomTextWithLength);
            FileGeneratorHelper.writeFile(stringBuilder.toString(),
                    pathFromRootToProject+"/src/main/java/com/jostleme/jostle/ui/localization/RichClientStrings_it.properties");
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private static void generateRandomTextWithLength(String line) {
        generateRandomTextWithLength(line, 0);
    }

    public static void generateRandomTextWithLength(String line, int maxLength) {
        String value;
        int numOfParams = FileGeneratorHelper.getNumOfParameters(line);
        String key[] = line.split("=");
        if (maxLength != 0)
            value = getRandomlySplitString(maxLength);
        else
            value = getRandomlySplitString();
        for (int i = 0; i < numOfParams; i++) {
            value += "{" + i + "}";
        }
        stringBuilder.append(key[0] + "=" + value + "\n");
    }

    private static String getRandomlySplitString() {
        StringBuilder val = new StringBuilder();
        Random r = new Random();
        int oneToTen = r.nextInt(10) + 1;

        String arr[] = BASE_STRING.split("\\s");
        for (int i = 0; i < oneToTen; i++) {
            if (i == 0)
                val.append(arr[i]);
            else
                val.append(" " + arr[i]);
        }
        return val.toString();
    }

    private static String getRandomlySplitString(int maxLength) {
        StringBuilder val = new StringBuilder();
        String[] arr = BASE_STRING.split("");

        if (BASE_STRING.length() < maxLength) {
            for (int i = 0; val.length() < maxLength; i++) {
                if (i == arr.length)
                    i = 0;
                val.append(arr[i]);
            }
        } else if (BASE_STRING.length() > maxLength) {
            for (int i = 0; i < maxLength; i++) {
                val.append(arr[i]);
            }
        }
        return val.toString();
    }

    public static void write() {
        FileGeneratorHelper.writeFile(stringBuilder.toString(), pathFromRootToProject+"/src/main/java/com/jostleme/jostle/ui/localization/RichClientStrings_it.properties");
    }
}
