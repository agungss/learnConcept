import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class learnConcept {

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            System.out.println("Usage: java learnConcept input.txt [exception.txt]");
            return;
        }

        Path inputPath = Paths.get(args[0]);
        if (!Files.exists(inputPath)) {
            System.out.println("Input file not found");
            return;
        }

        // Load exception words (optional)
        Set<String> exceptions = new HashSet<>();
        if (args.length >= 2) {
            Path exPath = Paths.get(args[1]);
            if (Files.exists(exPath)) {
                String exContent = Files.readString(exPath, StandardCharsets.UTF_8);
                for (String w : exContent.split("\\s+")) {
                    if (!w.isBlank()) exceptions.add(w.trim());
                }
            }
        }

        Map<String, Integer> freq = new HashMap<>();
        boolean inBlockComment = false;

        List<String> lines = Files.readAllLines(inputPath, StandardCharsets.UTF_8);

        for (String line : lines) {

            String trimmed = line.trim();

            // Ignore full line comment //
            if (!inBlockComment && trimmed.startsWith("//")) {
                continue;
            }

            StringBuilder clean = new StringBuilder();
            int i = 0;

            while (i < line.length()) {

                // Start block comment /*
                if (!inBlockComment && i + 1 < line.length()
                        && line.charAt(i) == '/' && line.charAt(i + 1) == '*') {
                    inBlockComment = true;
                    i += 2;
                    continue;
                }

                // End block comment */
                if (inBlockComment && i + 1 < line.length()
                        && line.charAt(i) == '*' && line.charAt(i + 1) == '/') {
                    inBlockComment = false;
                    i += 2;
                    continue;
                }

                if (!inBlockComment) {
                    clean.append(line.charAt(i));
                }

                i++;
            }

            if (clean.length() == 0) continue;

            String[] words = clean.toString().split("[^A-Za-z0-9]+");

            for (String w : words) {
                if (w.isEmpty()) continue;
                if (w.length() < 2) continue;                 // minimal 2 chars

                // ignore pure numbers
                if (w.chars().allMatch(Character::isDigit)) continue;

                if (!exceptions.isEmpty() && exceptions.contains(w)) continue;

                freq.put(w, freq.getOrDefault(w, 0) + 1);
            }
        }

        // ---------- SORTING ----------

        // 1) Alphabetical
        List<Map.Entry<String, Integer>> alpha = new ArrayList<>(freq.entrySet());
        alpha.sort(Map.Entry.comparingByKey());

        // 2) By frequency (desc), then alphabet
        List<Map.Entry<String, Integer>> byFreq = new ArrayList<>(freq.entrySet());
        byFreq.sort((a, b) -> {
            int c = Integer.compare(b.getValue(), a.getValue());
            return c != 0 ? c : a.getKey().compareTo(b.getKey());
        });

        // ---------- OUTPUT ----------

        StringBuilder out = new StringBuilder();

        out.append("=== SORTED BY ALPHABET ===\n");
        for (Map.Entry<String, Integer> e : alpha) {
            out.append(e.getKey()).append(" ").append(e.getValue()).append("\n");
        }

        out.append("\n=== SORTED BY FREQUENCY ===\n");
        for (Map.Entry<String, Integer> e : byFreq) {
            out.append(e.getKey()).append(" ").append(e.getValue()).append("\n");
        }

        System.out.println(out);

        Files.writeString(
                Paths.get("result.txt"),
                out.toString(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        System.out.println("\nResult saved to result.txt");
    }
}
