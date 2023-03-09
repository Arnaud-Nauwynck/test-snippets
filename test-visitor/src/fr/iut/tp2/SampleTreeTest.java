package fr.iut.tp2;

public class SampleTreeTest {

	/**
	 * <PRE>
	 * {
	 *   "a": null,
	 *   "b": true,
	 *   "c": 3.1415,
	 *   "d": "hello",
	 *   "e": [ null, "hello"],
	 *   "f": { "a": null, "b": {} }
	 * }
	 * 
	 * </PRE>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ObjectJsonNode res = new ObjectJsonNode.Builder()
			.putNull("a")
			.put("b", true)
			.put("c", 3.1415)
			.put("d", "hello")
			.putArray("d", b -> {
				b.addNull();
				b.add("hello");
			})
			.putObj("f", b -> {
				b.put("a", new NullJsonNode());
				b.put("b", new ObjectJsonNode());
			})
			.build();

		String json = res.toJson();
		System.out.println("json:" + json);
	}

}
