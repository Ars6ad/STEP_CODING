import java.util.*;

public class PlagiarismDetectorApp {
    private Map<String, Set<String>> ngramDb = new HashMap<>();
    
    public List<String> extractNGrams(String text, int n) {
        List<String> ngrams = new ArrayList<>();
        String[] words = text.toLowerCase().split("\\W+");
        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder ngram = new StringBuilder();
            for(int j=0; j<n; j++) {
                ngram.append(words[i+j]).append(" ");
            }
            ngrams.add(ngram.toString().trim());
        }
        return ngrams;
    }
    
    public void analyzeDocument(String docId, String text) {
        List<String> docNgrams = extractNGrams(text, 5);
        System.out.println("-> Extracted " + docNgrams.size() + " n-grams from " + docId);
        
        Map<String, Integer> commonNgramsCount = new HashMap<>();
        
        for (String ngram : docNgrams) {
            Set<String> docsWithNgram = ngramDb.get(ngram);
            if (docsWithNgram != null) {
                for (String otherDocId : docsWithNgram) {
                    commonNgramsCount.put(otherDocId, commonNgramsCount.getOrDefault(otherDocId, 0) + 1);
                }
            }
            // Add to DB
            ngramDb.computeIfAbsent(ngram, k -> new HashSet<>()).add(docId);
        }
        
        for (Map.Entry<String, Integer> entry : commonNgramsCount.entrySet()) {
            double similarity = (entry.getValue() * 100.0) / docNgrams.size();
            String status = similarity > 50 ? "(PLAGIARISM DETECTED)" : "(suspicious)";
            System.out.println("-> Found " + entry.getValue() + " matching n-grams with \"" + entry.getKey() + "\"");
            System.out.printf("-> Similarity: %.1f%% %s\n", similarity, status);
        }
    }
    
    public static void main(String[] args) {
        PlagiarismDetectorApp app = new PlagiarismDetectorApp();
        
        String baseDoc = "The quick brown fox jumps over the lazy dog and the cat watches.";
        String partialCopy = "A small brown fox jumps over the lazy dog in the field.";
        String fullCopy = "The quick brown fox jumps over the lazy dog and the cat watches closely.";

        app.analyzeDocument("essay_089.txt", baseDoc);
        app.analyzeDocument("essay_092.txt", fullCopy);
        System.out.println("analyzeDocument(\"essay_123.txt\")");
        app.analyzeDocument("essay_123.txt", partialCopy);
    }
}
