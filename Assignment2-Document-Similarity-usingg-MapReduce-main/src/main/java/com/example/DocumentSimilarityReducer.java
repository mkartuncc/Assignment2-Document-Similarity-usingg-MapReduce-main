
package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {
	@Override
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		Set<String> words1 = new HashSet<>();
		Set<String> words2 = new HashSet<>();
		for (Text val : values) {
			String[] parts = val.toString().split("\\|");
			if (parts.length == 2) {
				for (String w : parts[0].split(" ")) words1.add(w);
				for (String w : parts[1].split(" ")) words2.add(w);
			}
		}
		Set<String> intersection = new HashSet<>(words1);
		intersection.retainAll(words2);
		Set<String> union = new HashSet<>(words1);
		union.addAll(words2);
		double similarity = union.size() == 0 ? 0.0 : (double) intersection.size() / union.size();
		String result = String.format("Similarity: %.2f", similarity);
		context.write(key, new Text(result));
	}
}
