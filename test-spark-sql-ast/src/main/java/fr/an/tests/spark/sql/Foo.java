package fr.an.tests.spark.sql;

import lombok.Builder;
import lombok.Data;

//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@ToString
//@EqualsAndHashCode
@Data
@Builder
public class Foo {
	private int field1;
	private int field2;
	private int field3;
	private int field4;
	private int field5;
	private int field6;
	private int field7;

	
	public static void main(String[] args) {
		Foo f = Foo.builder()
			.field1(10)
			.field2(12)
			.build();
		System.out.println("Foo " + f);
	}
}
