package fr.an.tests.orastoredproc;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

public class StructArrayOraTest {

	private Connection con;
	
	
	@Before
	public void setup() {
		// con = TODO...
	}
	
	@Ignore
	@Test
	public void testParamHolder() throws SQLException {
		ParamHolder[] paramArray = new ParamHolder[] { 
				new ParamHolder("param1", "param2", "param3"), //
				new ParamHolder("param1", "param2", "param3"), //
				new ParamHolder("param1", "param2", "param3"), //
				new ParamHolder("param1", "param2", "param3") // 
				};
		
		callProc_SQLData(paramArray);
		callProc_Struct(paramArray);
	}


	/*
	 * CREATE OR REPLACE TYPE PARAM_HOLDER_OBJ AS OBJECT ( 
	 * PARAM1 VARCHAR2(200), PARAM2 VARCHAR2(200), PARAM3 VARCHAR3(200));
	 * 
	 * CREATE OR REPLACE TYPE PARAM_HOLDER_OBJ_TABLE IS TABLE OF PARAM_HOLDER_OBJ
	 * 
	 */

	protected void callProc_SQLData(ParamHolder[] paramHolders) throws SQLException {
		ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor("PARAM_HOLDER_OBJ_TABLE", con);
		Array array = new ARRAY(descriptor, con, paramHolders);
		
		try (CallableStatement stmnt = con.prepareCall("{ call custom(?) }")) {
	        stmnt.setArray(1, array);
	        stmnt.execute();
		}
	}
	
	protected void callProc_Struct(ParamHolder[] paramHolders) throws SQLException {
		// convert object to struct
		Object[][] structArray = new Object[paramHolders.length][];
		for(int i = 0; i < paramHolders.length; i++) {
			ParamHolder p = paramHolders[i];
			structArray[i] = new Object[] {
				p.getParam1(), p.getParam2(), p.getParam3()
			};
		}
		
		ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor("PARAM_HOLDER_OBJ_TABLE", con);
		Array array = new ARRAY(descriptor, con, structArray);
		
		try (CallableStatement stmnt = con.prepareCall("{ call custom(?) }")) {
	        stmnt.setArray(1, array);
	        stmnt.execute();
		}
	}
	
	
	
	public static class ParamHolder implements SQLData {

		private String param1;

		private String param2;

		private String param3;

		public ParamHolder(String param1, String param2, String param3) {
			super();
			this.param1 = param1;
			this.param2 = param2;
			this.param3 = param3;
		}

		public String getParam1() {
			return param1;
		}

		public void setParam1(String param1) {
			this.param1 = param1;
		}

		public String getParam2() {
			return param2;
		}

		public void setParam2(String param2) {
			this.param2 = param2;
		}

		public String getParam3() {
			return param3;
		}
		
		// @Override
		public void readSQL(SQLInput stream, String typeName) throws SQLException {
			throw new UnsupportedOperationException("NOT IMPLEMENTED");
		}

		// @Override
		public void writeSQL (SQLOutput stream) throws SQLException {
			stream.writeString(param1);
			stream.writeString(param2);
			stream.writeString(param3);
		}

		public String getSQLTypeName() {
			return "PARAM_HOLDER_OBJ";
		}

	}

	
}
