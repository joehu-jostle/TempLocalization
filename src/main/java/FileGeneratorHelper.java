import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileGeneratorHelper {

    private static Logger logger = Logger.getLogger(FileGeneratorHelper.class.getName());

    public static void writeFile(String content, String fileName) {
        try {
            FileWriter fileWriter = new FileWriter(fileName, false);
            fileWriter.write(content);
            fileWriter.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public static int getNumOfParameters(String line) {
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
}
