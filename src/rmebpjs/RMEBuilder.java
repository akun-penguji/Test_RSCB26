package rmebpjs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

/**
 * RMEBuilder - Main class untuk generate RME Bundle
 * Menerima Connection dari luar untuk kompatibilitas dengan KHANZA
 */
public class RMEBuilder {

    private String kodePPK = null; // Akan diambil dari tabel setting jika null
    private Connection conn;

    public RMEBuilder(Connection conn) {
        this.conn = conn;
    }

    public RMEBuilder(Connection conn, String kodePPK) {
        this.conn = conn;
        this.kodePPK = kodePPK;
    }

    /**
     * Build RME Bundle JSON (TANPA enkripsi - untuk preview/debug)
     */
    public String buildRMEJson(String noSEP) throws Exception {
        // Ambil kode PPK dari setting jika belum di-set
        if (kodePPK == null || kodePPK.isEmpty()) {
            kodePPK = getKodePPKFromSetting();
        }

        if (kodePPK == null || kodePPK.isEmpty()) {
            throw new Exception("Kode PPK tidak ditemukan di tabel setting");
        }

        // Load data dari database dengan koneksi yang diberikan
        PatientDAO pdao = new PatientDAO();
        EncounterDAO edao = new EncounterDAO();
        ConditionDAO cdao = new ConditionDAO(conn);
        OrganizationDAO orgdao = new OrganizationDAO();
        PractitionerDAO prdao = new PractitionerDAO();
        MedicationDAO mdao = new MedicationDAO();
        ProcedureDAO procdao = new ProcedureDAO();

        PatientData patient = pdao.getPatientBySEP(noSEP);
        EncounterData encounter = edao.getEncounterBySEP(noSEP);
        List<ConditionData> conditions = cdao.getConditionsBySEP(noSEP);
        OrganizationData organization = orgdao.getOrganizationById(kodePPK);
        PractitionerData practitioner = prdao.getPractitionerBySEP(noSEP);
        List<MedicationData> medications = mdao.getMedicationBySEP(noSEP);
        ProcedureData procedure = procdao.getProceduresBySEP(noSEP);

        // Validasi data wajib
        if (patient == null) {
            throw new Exception("Data pasien tidak ditemukan untuk SEP: " + noSEP);
        }
        if (encounter == null) {
            throw new Exception("Data encounter tidak ditemukan untuk SEP: " + noSEP);
        }
        if (conditions == null || conditions.isEmpty()) {
            throw new Exception("Data diagnosa tidak ditemukan untuk SEP: " + noSEP);
        }
        if (organization == null) {
            throw new Exception("Data organisasi tidak ditemukan untuk kode PPK: " + kodePPK);
        }

        // Build Bundle menggunakan BundleProcessor
        BundleProcessor bp = new BundleProcessor();

        StringBuilder bundle = new StringBuilder();
        bundle.append("{");
        bundle.append("\"resourceType\": \"Bundle\",");
        bundle.append("\"type\": \"transaction\",");
        bundle.append("\"entry\": [");

        boolean first = true;

        // Patient
        String patientJson = bp.buildPatientJson(patient);
        if (patientJson != null && !patientJson.isEmpty()) {
            if (!first) bundle.append(",");
            bundle.append("{\"resource\": ").append(patientJson).append("}");
            first = false;
        }

        // Encounter
        String encounterJson = bp.buildEncounterJson(encounter, patient, conditions);
        if (encounterJson != null && !encounterJson.isEmpty()) {
            if (!first) bundle.append(",");
            bundle.append("{\"resource\": ").append(encounterJson).append("}");
            first = false;
        }

        // Conditions
        String conditionJson = bp.buildConditionJson(conditions);
        if (conditionJson != null && !conditionJson.isEmpty()) {
            if (!first) bundle.append(",");
            bundle.append("{\"resource\": ").append(conditionJson).append("}");
            first = false;
        }

        // Organization
        String orgJson = bp.buildOrganizationJson(organization);
        if (orgJson != null && !orgJson.isEmpty()) {
            if (!first) bundle.append(",");
            bundle.append("{\"resource\": ").append(orgJson).append("}");
            first = false;
        }

        // Practitioner
        if (practitioner != null) {
            String practJson = bp.buildPractitionerJson(practitioner);
            if (practJson != null && !practJson.isEmpty()) {
                if (!first) bundle.append(",");
                bundle.append("{\"resource\": ").append(practJson).append("}");
                first = false;
            }
        }

        // Medications
        if (medications != null && !medications.isEmpty()) {
            String medJson = bp.buildMedicationJson(medications, patient);
            if (medJson != null && !medJson.isEmpty()) {
                if (!first) bundle.append(",");
                bundle.append("{\"resource\": ").append(medJson).append("}");
                first = false;
            }
        }

        bundle.append("]}");

        return bundle.toString();
    }

    /**
     * Ambil kode PPK dari tabel setting
     */
    private String getKodePPKFromSetting() {
        String sql = "SELECT kode_ppk FROM setting LIMIT 1";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("kode_ppk");
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println("Error getKodePPKFromSetting: " + e.getMessage());
        }
        return null;
    }

    public void setKodePPK(String kodePPK) {
        this.kodePPK = kodePPK;
    }

    public String getKodePPK() {
        return kodePPK;
    }
}
