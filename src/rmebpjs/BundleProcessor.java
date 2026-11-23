package rmebpjs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import simrskhanza.DlgKamarInap;
import rekammedis.RMRiwayatPerawatan;
import rmebpjs.PatientData;
import rmebpjs.PatientDAO;
import rmebpjs.ConditionData;
import rmebpjs.ConditionDAO;
import rmebpjs.EncounterData;
import rmebpjs.EncounterDAO;
import rmebpjs.ProcedureData;
import rmebpjs.ProcedureDAO;
import rmebpjs.PractitionerData;
import rmebpjs.PractitionerDAO;
import rmebpjs.OrganizationData;
import rmebpjs.OrganizationDAO;
import rmebpjs.MedicationData;
import rmebpjs.MedicationDAO;


import java.io.*;
import java.util.*;
import java.util.zip.GZIPOutputStream;
import java.util.Base64;

public class BundleProcessor {

    public String loadJsonTemplate(String filePath) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public String fillTemplate(String json, Map<String, String> map) {
        for (Map.Entry<String, String> e : map.entrySet()) {
            json = json.replace("{" + e.getKey() + "}", e.getValue());
        }
        return json;
    }

    public byte[] gzip(String data) throws Exception {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream);
        gzipStream.write(data.getBytes("UTF-8"));
        gzipStream.close();
        return byteStream.toByteArray();
    }

    public String gzipToBase64(String json) throws Exception {
        return Base64.getEncoder().encodeToString(gzip(json));
    }
    
    
    public String buildPatientJson(PatientData p) {
    StringBuilder sb = new StringBuilder();
    
        sb.append("{");
        sb.append("\"resourceType\": \"Patient\",");

        sb.append("\"id\": \"{ID_PATIENT}\",");

        // identifier
        sb.append("\"identifier\": [");

        sb.append("{");
        sb.append("\"use\": \"usual\",");
        sb.append("\"type\": {\"coding\": [{");
        sb.append("\"system\": \"http://hl7.org/fhir/v2/0203\",");
        sb.append("\"code\": \"MB\"");
        sb.append("}]},");
        sb.append("\"value\": \"" + p.norm + "\",");
        sb.append("\"assigner\": {\"display\": \"NOMOR REKAM MEDIS\"}");
        sb.append("},");

        sb.append("{");
        sb.append("\"use\": \"official\",");
        sb.append("\"type\": {\"coding\": [{");
        sb.append("\"system\": \"http://hl7.org/fhir/v2/0203\",");
        sb.append("\"code\": \"BPJS\"");
        sb.append("}]},");
        sb.append("\"value\": \"" + p.noka + "\",");
        sb.append("\"assigner\": {\"display\": \"BPJS KESEHATAN\"}");
        sb.append("},");

        sb.append("{");
        sb.append("\"use\": \"official\",");
        sb.append("\"type\": {\"coding\": [{");
        sb.append("\"system\": \"http://hl7.org/fhir/v2/0203\",");
        sb.append("\"code\": \"NNIDN\"");
        sb.append("}]},");
        sb.append("\"value\": \"" + p.ktp + "\",");
        sb.append("\"assigner\": {\"display\": \"KEMDAGRI\"}");
        sb.append("}");
        sb.append("],");

        // active
        sb.append("\"active\": true,");

        // name
        sb.append("\"name\": [{");
        sb.append("\"use\": \"official\",");
        sb.append("\"text\": \"" + p.nama + "\"");
        sb.append("}],");

        // telecom
        sb.append("\"telecom\": [");

        sb.append("{\"system\": \"phone\", \"value\": \"" + p.hp + "\", \"use\": \"work\"},");
        sb.append("{\"system\": \"phone\", \"value\": \"" + p.hp + "\", \"use\": \"mobile\"},");
        sb.append("{\"system\": \"phone\", \"value\": \"" + p.hp + "\", \"use\": \"home\"}");

        sb.append("],");

        // gender & birth
        sb.append("\"gender\": \"" + p.jk + "\",");
        sb.append("\"birthDate\": \"" + p.tglLahir + "\",");
        sb.append("\"deceasedBoolean\": false,");

        // address
        sb.append("\"address\": [{");
        sb.append("\"use\": \"home\",");
        sb.append("\"type\": \"both\",");
        sb.append("\"text\": \"" + p.alamat + "\",");
        sb.append("\"line\": [\"" + p.alamat + "\"],");
        sb.append("\"city\": \"" + p.kota + "\",");
        sb.append("\"district\": \"" + p.kecamatan + "\",");
        sb.append("\"state\": \"" + p.provinsi + "\",");
        sb.append("\"postalCode\": \"" + p.kdPos + "\"");
        sb.append("}],");

        // maritalStatus
        sb.append("\"maritalStatus\": {");
        sb.append("\"coding\": [{");
        sb.append("\"system\": \"http://hl7.org/fhir/v3/MaritalStatus\",");
        sb.append("\"code\": \"U\"");
        sb.append("}]},");

        // managingOrganization
        sb.append("\"managingOrganization\": {");
        sb.append("\"reference\": \"Organization/{ID_ORGANIZATION}\",");
        sb.append("\"display\": \"{NAMARS}\"");
        sb.append("}");

        sb.append("}");

        return sb.toString();
}

    public String buildMedicationJson(List<MedicationData> meds, PatientData p) {
    StringBuilder sb = new StringBuilder();
    //sb.append("["); // array awal

    for (int i = 0; i < meds.size(); i++) {
        MedicationData m = meds.get(i);

        sb.append("{");
        sb.append("\"resourceType\": \"MedicationRequest\",");
        sb.append("\"meta\": {\"lastUpdated\": \"{lastupdate}\"},");

        sb.append("\"text\": {\"div\": \"" + m.text + "\"},");

        sb.append("\"identifier\": {");
        sb.append("\"system\": \"id_resep_pulang\",");
        sb.append("\"value\": \"{ID_PATIENT}\"");
        sb.append("},");
        
        sb.append("\"subject\": {");
        sb.append("\"reference\": \"Patient/{ID_PATIENT}\",");
        sb.append("\"display\": \"" + p.nama + "\"");
        sb.append("},");

        sb.append("\"intent\": \"final\",");

        sb.append("\"medicationCodeableConcept\": {");
        sb.append("\"coding\": [{");
        sb.append("\"system\": \"http://{NAMARS}.co.id/drug\",");
        sb.append("\"code\": \"" + m.code + "\",");
        //sb.append("\"display\": \"Active\"");
        sb.append("}],");
        sb.append("\"text\": \"" + m.text + "\"");
        sb.append("},");

        sb.append("\"requester\": {");
        sb.append("\"agent\": {");
        sb.append("\"display\": \"{NAMADPJP}\",");
        sb.append("\"reference\": \"Practitioner/{ID_PRACTITIONER}\"");
        sb.append("},");
        sb.append("\"onBehalfOf\": {");
        sb.append("\"reference\": \"Organization/{ID_ORGANIZATION}\"");
        sb.append("}");
        sb.append("},");

        sb.append("\"dosageInstruction\": [{");
        sb.append("\"additionalInstruction\": [{\"text\": \"" + m.aturanpakai + "\"}],");
        sb.append("\"timing\": {\"repeat\": {\"frequency\": 1, \"period\": 1, \"periodUnit\": \"d\"}},");
        sb.append("\"route\": {\"coding\": [{");
        sb.append("\"system\": \"http://snomed.info/sct\",");
        sb.append("\"code\": \"" + m.routecode + "\",");
        sb.append("\"display\": \"" + m.routedisplay + "\"");
        sb.append("}]},");
        sb.append("\"doseQuantity\": {");
        sb.append("\"value\": 1,");
        sb.append("\"unit\": \"TAB\",");
        sb.append("\"system\": \"http://unitsofmeasure.org\",");
        sb.append("\"code\": \"TAB\"");
        sb.append("}");
        sb.append("}]");

        sb.append("}"); // close resource
        

        if (i < meds.size() - 1) sb.append(",");
    }

    //sb.append("]"); // closing array JSON
    return sb.toString();
}
    
    public String buildConditionJson(List<ConditionData> conditions) {
    StringBuilder sb = new StringBuilder();

    // Tidak memakai array outer jika Anda mengikuti pola Medication (langsung concat item)
    for (int i = 0; i < conditions.size(); i++) {
        ConditionData c = conditions.get(i);

        sb.append("{");
        sb.append("\"resourceType\": \"Condition\",");
        sb.append("\"id\": \"{ID_CONDITION}\",");
        sb.append("\"clinicalStatus\": \"active\",");
        sb.append("\"verificationStatus\": \"confirmed\",");

        // Category
        sb.append("\"category\": [");
        sb.append("{\"coding\": [");
        sb.append("{");
        sb.append("\"system\": \"http://hl7.org/fhir/condition-category\",");
        sb.append("\"code\": \"encounter-diagnosis\",");
        sb.append("\"display\": \"Encounter Diagnosis\"");
        sb.append("}");
        sb.append("]}]");
        sb.append(",");  // ← komanya di sini, setelah category

        // Code block
        sb.append("\"code\": {");
        sb.append("\"coding\": [");
        sb.append("{");
        sb.append("\"system\": \"http://hl7.org/fhir/sid/icd-10\",");
        sb.append("\"code\": \"" + c.code + "\",");
        sb.append("\"display\": \"" + c.display + "\"");
        sb.append("}");
        sb.append("],");
        sb.append("\"text\": \"" + c.display + "\"");
        sb.append("},");

        // Subject
        sb.append("\"subject\": {");
        sb.append("\"reference\": \"Patient/{ID_PATIENT}\"");
        sb.append("},");

        // Onset
        sb.append("\"onsetDateTime\": \"" + c.onset + "\"");

        sb.append("}");


        // Tambahkan koma antar item jika bukan item terakhir
        if (i < conditions.size() - 1) {
            sb.append(",");
        }
    }

    return sb.toString();
}
    
    public String buildEncounterJson(EncounterData e, PatientData p, List<ConditionData> conditions) {
    StringBuilder sb = new StringBuilder();

    sb.append("{");
    sb.append("\"resourceType\": \"Encounter\",");

    // id
    sb.append("\"id\": \"{ID_ENCOUNTER}\",");

    // identifier (SEP)
    sb.append("\"identifier\": [{");
    sb.append("\"system\": \"http://api.bpjs-kesehatan.go.id:8080/Vclaim-rest/SEP/\",");
    sb.append("\"value\": \"{NOSEP}\"");
    sb.append("}],");
    
    // subject (patient)
    sb.append("\"subject\": {");
    sb.append("\"reference\": \"Patient/{ID_PATIENT}\",");
    sb.append("\"display\": \"" + p.nama + "\",");
    sb.append("\"noSep\": \"{NOSEP}\"");
    sb.append("},");

    // class
    sb.append("\"class\": {");
    sb.append("\"system\": \"http://hl7.org/fhir/v3/ActCode\",");
    sb.append("\"code\": \"IMP\",");
    sb.append("\"display\": \"inpatient encounter\"");
    sb.append("},");

    // incomingReferral
    sb.append("\"incomingReferral\": [{");
    sb.append("\"identifier\": [");
    sb.append("{\"system\": \"nomor_rujukan_bpjs\", \"value\": \"" + e.rujukan + "\"},");
    sb.append("{\"system\": \"nomor_rujukan_internal_rs\", \"value\": \"" + e.nomorrawat + "\"}");
    sb.append("]");
    sb.append("}],");

    // ===== reason (pakai diagnosis pertama kalau ada) =====
    if (conditions != null && !conditions.isEmpty()) {
        ConditionData first = conditions.get(0);

        sb.append("\"reason\": [{");
        sb.append("\"coding\": [{");
        sb.append("\"code\": \"" + first.code + "\",");
        sb.append("\"display\": \"" + first.display + "\",");
        sb.append("\"system\": \"http://hl7.org/fhir/sid/icd-10\"");

        sb.append("}],");
        sb.append("\"text\": \"" + first.display + "\"");
        sb.append("}],");
    }

    // ===== diagnosis =====
    sb.append("\"diagnosis\": [");
    for (int i = 0; i < conditions.size(); i++) {
        ConditionData c = conditions.get(i);

        sb.append("{");

        sb.append("\"condition\": {");
        sb.append("\"reference\": \"Condition/{ID_CONDITION}\",");
       

        // role sekarang di luar condition
        sb.append("\"role\": {");
        sb.append("\"coding\": [{");
        sb.append("\"system\": \"http://hl7.org/fhir/diagnosis-role\",");
        sb.append("\"code\": \"DD\",");
        sb.append("\"display\": \"Discharge Diagnosis\"");
        sb.append("}]");
        sb.append("},");

        // rank
        sb.append("\"rank\": ").append(i + 1);

        sb.append("}");
        sb.append("}");

        if (i < conditions.size() - 1) sb.append(",");
    }
    sb.append("],");

    // hospitalization
    sb.append("\"hospitalization\": {");
    sb.append("\"dischargeDisposition\": [{");
    sb.append("\"coding\": [{");
    sb.append("\"system\": \"http://hl7.org/fhir/discharge-disposition\",");
    sb.append("\"code\": \"home\",");
    sb.append("\"display\": \"Home\"");
    sb.append("}]");
    sb.append("}]");
    sb.append("},");



    // period
    sb.append("\"period\": {");
    sb.append("\"start\": \"{STARTENCOUNTER}\",");
    sb.append("\"end\": \"{ENDENCOUNTER}\"");
    sb.append("},");
    
    // status
    sb.append("\"status\": \"finished\",");

    // text
    sb.append("\"text\": {");
    sb.append("\"status\": \"generated\",");
    sb.append("\"div\": \"Admitted Encounter\"");
    sb.append("}");

    sb.append("}");
    return sb.toString();
}


    
public String buildReasonCodeJson(List<ConditionData> conditions) {
    StringBuilder sb = new StringBuilder();
    sb.append("\"reasonCode\": [");

    for (int i = 0; i < conditions.size(); i++) {
        ConditionData c = conditions.get(i);

        if (i > 0) sb.append(",");

        sb.append("{");
        sb.append("\"coding\": [");
        sb.append("{");
        sb.append("\"system\": \"http://hl7.org/fhir/sid/icd-10\",");
        sb.append("\"code\": \"" + c.code + "\",");
        sb.append("\"display\": \"" + c.display + "\"");
        sb.append("}");
        sb.append("],");
        sb.append("\"text\": \"" + c.display + "\"");
        sb.append("},");
    }

    sb.append("]");
    return sb.toString();
}


public String buildOrganizationJson(OrganizationData o) {
    StringBuilder sb = new StringBuilder();

    sb.append("{");
    sb.append("\"resourceType\": \"Organization\",");

    // id
    sb.append("\"id\": \"{ID_ORGANIZATION}\",");

    // identifier (SEP)
    sb.append("\"identifier\": [{");
    sb.append("\"use\": \"official\",");
    sb.append("\"system\": \"urn:oid:bpjs\",");
    sb.append("\"value\": \""+ o.kdPpk +"\"");
    sb.append("},");
    
    sb.append("{");
    sb.append("\"use\": \"official\",");
    sb.append("\"system\": \"urn:oid:kemkes\",");
    sb.append("\"value\": \""+ o.kdPpkKemenkes +"\"");
    sb.append("}],");
    
    
    sb.append("\"type\": [{");
    sb.append("\"coding\": [{");
    sb.append("\"code\": \"prov\",");
    sb.append("\"display\": \"Healthcare Provider\",");
    sb.append("\"system\": \"http://hl7.org/fhir/organization-type\"");
    sb.append("}]");
    sb.append("}],");

    // class
    sb.append("\"name\": \"Rawat Jalan - Poliklinik\",");
    sb.append("\"alias\": [");
    sb.append("\"RJ\"");
    sb.append("],");
    sb.append("\"telecom\": [{");
    sb.append("\"use\": \"work\",");
    sb.append("\"system\": \"phone\",");
    sb.append("\"value\": \""+ o.kdPpkKemenkes +"\"");
    sb.append("}],");
    
    sb.append("\"address\": [{");
    sb.append("\"use\": \"work\",");
    sb.append("\"text\": \""+ o.alamat + "," + o.kota + "," + o.provinsi +"\",");
    sb.append("\"line\": [");
    sb.append("\""+ o.alamat +"\"");
    sb.append("],");
    sb.append("\"city\": \""+ o.kota +"\",");
    sb.append("\"state\": \""+ o.provinsi +"\",");
    sb.append("\"postalCode\": \"123456\",");
    sb.append("\"country\": \"Indonesia\"");
    sb.append("}],");
    
    sb.append("\"contact\": [{");
    sb.append("\"purpose\": {");
    sb.append("\"coding\": [{");
    sb.append("\"code\": \"PATINF\",");
    sb.append("\"system\": \"http://hl7.org/fhir/contactentity-type\"");
    sb.append("}]");
    sb.append("},");
    
    sb.append("\"telecom\": [{");
    sb.append("\"value\": \"123456\",");
    sb.append("\"system\": \"http://hl7.org/fhir/contactentity-type\"");
    sb.append("}]");
    sb.append("}]");
    sb.append("}");
    
    return sb.toString();
}

public String buildPractitionerJson(PractitionerData pr) {
    StringBuilder sb = new StringBuilder();

    sb.append("{");
    sb.append("\"resourceType\": \"Practitioner\",");

    // id
    sb.append("\"id\": \"{ID_PRACTITIONER}\",");

    // identifier (SEP)
    sb.append("\"identifier\": [{");
    sb.append("\"use\": \"official\",");
    sb.append("\"system\": \"urn:oid:nomor_sip\",");
    sb.append("\"value\": \""+ pr.sip +"\"");
    sb.append("},");
    
    sb.append("{");
    sb.append("\"use\": \"official\",");
    sb.append("\"type\": {");
    sb.append("\"coding\": [{");
    sb.append("\"code\": \"NNIDN\",");
    sb.append("\"system\": \"http://hl7.org/fhir/v2/0203\"");
    sb.append("}]");
    sb.append("},");
    sb.append("\"value\": \""+ pr.nik +"\",");
    sb.append("\"assigner\": {");
    sb.append("\"display\": \"KEMDAGRI\"");
    sb.append("}}],");

    sb.append("\"name\": [{");
    sb.append("\"use\": \"official\",");
    sb.append("\"text\": \""+ pr.nama +"\"");
    sb.append("}],");
    

    sb.append("\"telecom\": [{");
    sb.append("\"use\": \"work\",");
    sb.append("\"system\": \"phone\",");
    sb.append("\"value\": \""+ pr.phone +"\"");
    sb.append("},");
    sb.append("{");
    sb.append("\"use\": \"work\",");
    sb.append("\"system\": \"email\",");
    sb.append("\"value\": \"email@gmail.com\"");
    sb.append("},");
    sb.append("{");
    sb.append("\"use\": \"work\",");
    sb.append("\"system\": \"fax\",");
    sb.append("\"value\": \"fax\"");
    sb.append("},");
    sb.append("{");
    sb.append("\"use\": \"home\",");
    sb.append("\"system\": \"home\",");
    sb.append("\"value\": \"\"");
    sb.append("}],");
    
    sb.append("\"address\": [{");
    sb.append("\"use\": \"home\",");
    sb.append("\"text\": \"" + pr.alamat + "\",");
    sb.append("\"line\": [");
    sb.append("\""+ pr.alamat +"\"");
    sb.append("],");
    sb.append("\"city\": \"Mamuju\",");
    sb.append("\"state\": \"Sulawesi Barat\",");
    sb.append("\"postalCode\": \"123456\",");
    sb.append("\"country\": \"Indonesia\"");
    sb.append("}],");

    sb.append("\"gender\": \"female\",");
    sb.append("\"birthDate\": \"" + pr.tgllahir +"\"");
    sb.append("}");

   
    
    return sb.toString();
}




}
