import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class learnConcept {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage:");
            System.out.println("  java learnConcept <input.txt> [exception.txt]");
            return;
        }

        try {
            // 🔹 Load exception words (optional)
            Set<String> exceptions = new HashSet<>();

            if (args.length >= 2) {
                String exceptionContent = Files.readString(Path.of(args[1]));
                exceptionContent = exceptionContent.replaceAll("[^A-Za-z0-9\\s]", " ");
                String[] exceptionWords = exceptionContent.split("\\s+");

                for (String w : exceptionWords) {
                    if (w.length() >= 2) {
                        exceptions.add(w);
                    }
                }
            }

            // 🔹 Load main input
            String content = Files.readString(Path.of(args[0]));
            content = content.replaceAll("[^A-Za-z0-9\\s]", " ");
            String[] words = content.split("\\s+");

            Map<String, Integer> freq = new HashMap<>();

            for (String w : words) {
                if (w.length() < 2) continue;              // minimal 2 char
                if (!exceptions.isEmpty() && exceptions.contains(w)) continue;

                freq.put(w, freq.getOrDefault(w, 0) + 1);
            }

            List<Map.Entry<String, Integer>> list =
                    new ArrayList<>(freq.entrySet());

            // 🔹 Multi-sort: alfabet → jumlah
            list.sort((a, b) -> {
                int cmp = a.getKey().compareTo(b.getKey());
                if (cmp != 0) return cmp;
                return Integer.compare(b.getValue(), a.getValue());
            });

            // 🔹 Prepare output lines
            List<String> outputLines = new ArrayList<>();

            for (Map.Entry<String, Integer> e : list) {
                String line = e.getKey() + " " + e.getValue();
                System.out.println(line);     // tampil di layar
                outputLines.add(line);        // simpan ke file
            }

            // 🔹 Write to result.txt
            Files.write(Path.of("result.txt"), outputLines);

            System.out.println("\nResult saved to result.txt");

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
