package fr.an.tests.kerberos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.directory.server.kerberos.shared.crypto.encryption.KerberosKeyFactory;
import org.apache.directory.server.kerberos.shared.keytab.Keytab;
import org.apache.directory.server.kerberos.shared.keytab.KeytabEntry;
import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.components.EncryptionKey;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * 
 */
public class CreateKeytab {

	/** A map of default encryption types mapped to cipher names. */
    public static final Map<EncryptionType, String> DEFAULT_CIPHERS;

    static
    {
        Map<EncryptionType, String> map = new HashMap<EncryptionType, String>();

        // map.put( EncryptionType.DES_CBC_MD5, "DES" );
        map.put( EncryptionType.DES3_CBC_SHA1_KD, "DESede" );
        // map.put( EncryptionType.RC4_HMAC, "ArcFourHmac" );
        map.put( EncryptionType.AES128_CTS_HMAC_SHA1_96, "AES128" );
        map.put( EncryptionType.AES256_CTS_HMAC_SHA1_96, "AES256" );

        DEFAULT_CIPHERS = Collections.unmodifiableMap( map );
    }
    
	@RequiredArgsConstructor @Data
	public static class KeytabPrincipalPassphrase {

	    private final String principalName;
	    private final String passPhrase;
	    private final Set<EncryptionType> ciphers; // if null => use default..
	}

	
    /**
     * The main.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args == null || args.length < 3 || args.length % 2 != 1) {
            System.out.println("Kerberos keytab generator");
            System.out.println("-------------------------");
            System.out.println("Usage:");
            System.out.println("java -classpath kerberos-using-apacheds.jar " + CreateKeytab.class.getName()
                    + " <principalName> <passPhrase> [<principalName2> <passPhrase2> ...] <outputKeytabFile>");
        } else {
            final File keytabFile = new File(args[args.length - 1]);
            final List<KeytabPrincipalPassphrase> keytabUsers = new ArrayList<KeytabPrincipalPassphrase>();
            for (int i = 0; i < args.length - 1; i += 2) {
                String principal = args[i];
                String passphrase = args[i + 1];
                keytabUsers.add(new KeytabPrincipalPassphrase(principal, passphrase, null));
                System.out.println("Adding principal " + principal + " with passphrase " + passphrase);
            }
            createKeytab(keytabUsers, keytabFile);
            System.out.println("Keytab file was created: " + keytabFile.getAbsolutePath());
        }
    }

    /**
     * Creates a keytab file for given principal.
     */
    public static void createKeytab(String principalName, String passPhrase, Set<EncryptionType> ciphers,
    		final File keytabFile
    		) throws IOException {
        val users = new ArrayList<KeytabPrincipalPassphrase>();
        users.add(new KeytabPrincipalPassphrase(principalName, passPhrase, ciphers));
        createKeytab(users, keytabFile);
    }

    /**
     * Creates a keytab file for given principals.
     */
    public static void createKeytab(List<KeytabPrincipalPassphrase> keytabUsers, final File keytabFile)
            throws IOException {
        final KerberosTime timeStamp = new KerberosTime();
        final int principalType = 1; // KRB5_NT_PRINCIPAL

        final Keytab keytab = Keytab.getInstance();
        final List<KeytabEntry> entries = new ArrayList<KeytabEntry>();

        for (KeytabPrincipalPassphrase keytabUser : keytabUsers) {
			String principalName = keytabUser.getPrincipalName();
			String passPhrase = keytabUser.getPassPhrase();
			// encoding password with ciphers
            Set<EncryptionType> ciphers = keytabUser.getCiphers();
            if (ciphers == null) {
            	ciphers = DEFAULT_CIPHERS.keySet();
            }
			Map<EncryptionType, EncryptionKey> principalKeys = KerberosKeyFactory.getKerberosKeys(
                    principalName, passPhrase, ciphers);

			// adding encoded KeyTabEntry(ies)
			for (Map.Entry<EncryptionType, EncryptionKey> keyEntry : principalKeys.entrySet()) {
                EncryptionKey key = keyEntry.getValue();
                byte keyVersion = (byte) key.getKeyVersion();
                entries.add(new KeytabEntry(principalName, principalType, timeStamp, keyVersion, key));
            }
        }
        keytab.setEntries(entries);
        keytab.write(keytabFile);
    }

}
