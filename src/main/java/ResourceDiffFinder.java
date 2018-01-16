import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ResourceDiffFinder {

    public static ArrayList<String> englishKeys = new ArrayList<>();
    public static Map<String, String> englishMap = new HashMap<>();
    public static Map<String, String> localizationStrMap = new HashMap<>();
    public static Map<String, String> toBeTranslated = new HashMap<>();

    private static String pathFromRootToProject = new File("").getAbsolutePath();
    private static Logger logger = Logger.getLogger(ResourceDiffFinder.class.getName());

    private static final String ENGLISH_FILE_PATH = "/src/main/java/com/jostleme/jostle/ui/localization/RichClientStrings.properties";
    private static final String FRENCH_FILE_PATH = "/src/main/java/com/jostleme/jostle/ui/localization/RichClientStrings_fr.properties";

    public static void findResourceDiff() {
        loadStrings(ENGLISH_FILE_PATH);
        loadStrings(FRENCH_FILE_PATH);
        System.out.println("eng: " + englishKeys.size());
        System.out.println("fr: " + localizationStrMap.size());

        for (String key : englishKeys) {
            if (!localizationStrMap.containsKey(key)) {
                toBeTranslated.put(key, englishMap.get(key));
            }
        }
        System.out.println("to be translated: " + toBeTranslated.size());
    }

    private static void loadStrings(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(pathFromRootToProject + filePath))) {
            br.lines()
                    .filter(s -> !s.isEmpty())
                    .filter(s -> s.charAt(0) != '#')
                    .forEach(s -> {
                        String key = s.split("=")[0];
                        String val = s.split("=")[1];
                        if (filePath.equals(ENGLISH_FILE_PATH)) {
                            englishMap.put(key, val);
                            englishKeys.add(key);
                        } else {
                            localizationStrMap.put(key, val);
                        }
                    });
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
