package fr.an.tests.dsljson;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.runtime.Settings;

public class TstDslJsonMain {

	public static void main(String[] args) {
		
		final DslJson<Object> dslJson = new DslJson<Object>(Settings.basicSetup());
		
		// dslJson.deserialize(null, null, 0);
	}
}
