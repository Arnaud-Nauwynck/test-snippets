package fr.an.tests.kerberos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.an.tests.kerberos.CreateKeytab.KeytabPrincipalPassphrase;

public class CreateKeytabTest {

	@Test
	public void testCreateKeytabFile() throws Exception {
		File keytabFile = new File("target/test.keytab");
        List<KeytabPrincipalPassphrase> keytabUsers = new ArrayList<KeytabPrincipalPassphrase>();
        keytabUsers.add(new KeytabPrincipalPassphrase("me@DOMAIN", "password", null));
        CreateKeytab.createKeytab(keytabUsers, keytabFile);
	}
}
