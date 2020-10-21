package fr.an.tests.testsparkxml.dto;

import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLStreamReader;

import lombok.Data;

@XmlRootElement(name = "user")
// @XmlType(propOrder = { "firstName", "lastName", "birthYear" })
@Data
public class UserJaxbDTO {

	private String firstName;
	private String lastName;
	private int birthYear;

	public static UserJaxbDTO jaxbUnmarshal(XMLStreamReader source) throws JAXBException, IOException {
	    JAXBContext context = JAXBContext.newInstance(UserJaxbDTO.class);
	    return (UserJaxbDTO) context.createUnmarshaller()
	      .unmarshal(source);
	}
	
}
