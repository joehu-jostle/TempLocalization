import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResourceDiffFinder {

    public static ArrayList<String> englishKeys = new ArrayList<>();
    public static Map<String, String> englishMap = new HashMap<>();
    public static Map<String, String> localizationStrMap = new HashMap<>();
    public static Map<String, String> toBeTranslated = new HashMap<>();

    private static final String pathFromRootToProject = new File("").getAbsolutePath();

    private static final String ENGLISH_FILE_PATH = "/src/main/java/com/jostleme/jostle/ui/localization/RichClientStrings.properties";
    private static final String FRENCH_FILE_PATH =  "/src/main/java/com/jostleme/jostle/ui/localization/RichClientStrings_fr.properties";

    private static final String ITALIAN_FILE_PATH = "/src/main/java/com/jostleme/jostle/ui/localization/RichClientStrings_it.properties";

    public static void findResourceDiff() {
        loadStrings(ENGLISH_FILE_PATH);
        loadStrings(ITALIAN_FILE_PATH);

        System.out.println("eng: " + englishKeys.size());
        System.out.println("it: " + localizationStrMap.size());

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
                        int indexOfFirstEqualSign = s.indexOf("=");
                        String key = s.substring(0, indexOfFirstEqualSign);
                        indexOfFirstEqualSign++;
                        String val = s.substring(indexOfFirstEqualSign, s.length());
                        if (filePath.equals(ENGLISH_FILE_PATH)) {
                            englishMap.put(key, val);
                            englishKeys.add(key);
                        } else {
                            localizationStrMap.put(key, val);
                        }
                    });
        } catch (Exception e) { // if there's no already translated data
            if (filePath.equals(ITALIAN_FILE_PATH)) {
                System.out.println("Existing translation file not found.");
                System.out.println("Will find diffs between English and French");
                loadStrings(FRENCH_FILE_PATH);
            }
        }
    }
}
