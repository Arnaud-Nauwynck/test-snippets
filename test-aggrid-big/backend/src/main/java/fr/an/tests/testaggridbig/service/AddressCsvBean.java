package fr.an.tests.testaggridbig.service;

import com.opencsv.bean.CsvBindByName;
import de.siegmar.fastcsv.reader.CsvRecord;
import de.siegmar.fastcsv.reader.NamedCsvRecord;
import lombok.val;

public class AddressCsvBean {

    @CsvBindByName
    public String uid_adresse;

    @CsvBindByName
    public String cle_interop;

    @CsvBindByName
    public String commune_insee;

    @CsvBindByName
    public String commune_nom;

    @CsvBindByName
    public String commune_deleguee_insee;

    @CsvBindByName
    public String commune_deleguee_nom;

    @CsvBindByName
    public String voie_nom;

    @CsvBindByName
    public String lieudit_complement_nom;

    @CsvBindByName
    public int numero;

    @CsvBindByName
    public String suffixe;

    @CsvBindByName
    public String position;

    @CsvBindByName
    public float x;

    @CsvBindByName
    public float y;

    @CsvBindByName(column="long")
    public float longitude;
    @CsvBindByName(column="lat")
    public float lattitude;

    @CsvBindByName
    public String cad_parcelles;

    @CsvBindByName
    public String source;

    @CsvBindByName
    public String date_der_maj;

    @CsvBindByName
    public String certification_commune;


    public static AddressCsvBean namedCsvRecordToAddressBean(NamedCsvRecord src) {
        val res = new AddressCsvBean();
        res.uid_adresse = src.getField("uid_adresse");
        res.cle_interop = src.getField("cle_interop");
        res.commune_insee = src.getField("commune_insee");
        res.commune_nom = src.getField("commune_nom");
        res.commune_deleguee_insee = src.getField("commune_deleguee_insee");
        res.commune_deleguee_nom = src.getField("commune_deleguee_nom");
        res.voie_nom = src.getField("voie_nom");
        res.lieudit_complement_nom = src.getField("lieudit_complement_nom");
        res.numero = safeParseInt(src.getField("numero"));
        res.suffixe = src.getField("suffixe");
        res.position = src.getField("position");
        res.x = safeParseFloat(src.getField("x"));
        res.y = safeParseFloat(src.getField("y"));
        res.longitude = safeParseFloat(src.getField("long"));
        res.lattitude = safeParseFloat(src.getField("lat"));
        res.cad_parcelles = src.getField("cad_parcelles");
        res.source = src.getField("source");
        res.date_der_maj = src.getField("date_der_maj");
        res.certification_commune = src.getField("certification_commune");
        return res;
    }

    public static AddressCsvBean csvRecordToAddressBean(CsvRecord src) {
        val res = new AddressCsvBean();
        int idx = 0;
        res.uid_adresse = src.getField(idx++);
        res.cle_interop = src.getField(idx++);
        res.commune_insee = src.getField(idx++);
        res.commune_nom = src.getField(idx++);
        res.commune_deleguee_insee = src.getField(idx++);
        res.commune_deleguee_nom = src.getField(idx++);
        res.voie_nom = src.getField(idx++);
        res.lieudit_complement_nom = src.getField(idx++);
        res.numero = safeParseInt(src.getField(idx++));
        res.suffixe = src.getField(idx++);
        res.position = src.getField(idx++);
        res.x = safeParseFloat(src.getField(idx++));
        res.y = safeParseFloat(src.getField(idx++));
        res.longitude = safeParseFloat(src.getField(idx++));
        res.lattitude = safeParseFloat(src.getField(idx++));
        res.cad_parcelles = src.getField(idx++);
        res.source = src.getField(idx++);
        res.date_der_maj = src.getField(idx++);
        res.certification_commune = src.getField(idx++);
        return res;
    }

    private static int safeParseInt(String text) {
        if (text == null || text.isEmpty()) return 0;
        try {
            return Integer.parseInt(text);
        } catch(NumberFormatException e) {
            return 0;
        }
    }

    private static float safeParseFloat(String text) {
        if (text == null || text.isEmpty()) return 0;
        try {
            return Float.parseFloat(text);
        } catch(NumberFormatException e) {
            return 0;
        }
    }
}
