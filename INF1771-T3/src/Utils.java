import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import static java.util.Collections.reverseOrder;

public class Utils {
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		Map<K, V> result = new LinkedHashMap<>();
		Stream<Map.Entry<K, V>> st = map.entrySet().stream();

		st.sorted(Map.Entry.comparingByValue()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));

		return result;
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDesc(Map<K, V> map) {
		Map<K, V> result = new LinkedHashMap<>();
		Stream<Map.Entry<K, V>> st = map.entrySet().stream();

		st.sorted(reverseOrder(Map.Entry.comparingByValue())).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));

		return result;
	}
}
