
package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DocumentSimilarityMapper extends Mapper<Object, Text, Text, Text> {
	private Map<String, Set<String>> docWords = new HashMap<>();

	@Override
	protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		String line = value.toString().trim();
		if (line.isEmpty()) return;
		String[] parts = line.split(" ", 2);
		if (parts.length < 2) return;
		String docId = parts[0];
		String[] words = parts[1].toLowerCase().split("\\s+");
		Set<String> wordSet = new HashSet<>();
		for (String word : words) {
			wordSet.add(word);
		}
		docWords.put(docId, wordSet);
	}

	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		// Emit all unique pairs
		String[] docs = docWords.keySet().toArray(new String[0]);
		for (int i = 0; i < docs.length; i++) {
			for (int j = i + 1; j < docs.length; j++) {
				String docPair = docs[i] + "," + docs[j];
				String words1 = String.join(" ", docWords.get(docs[i]));
				String words2 = String.join(" ", docWords.get(docs[j]));
				context.write(new Text(docPair), new Text(words1 + "|" + words2));
			}
		}
	}
}
