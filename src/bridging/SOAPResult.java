package bridging;

/**
 * Class untuk menyimpan hasil SOAP parsing
 */
public class SOAPResult {

    private String subjective;
    private String objective;
    private String assessment;
    private String plan;
    private String intervention;
    private String evaluation;
    private VitalSigns vitalSigns; // TAMBAHAN

    // Constructor tanpa vital signs (backward compatibility)
    public SOAPResult(String subjective, String objective, String assessment,
            String plan, String intervention, String evaluation) {
        this.subjective = subjective != null ? subjective : "Tidak disebutkan";
        this.objective = objective != null ? objective : "Tidak disebutkan";
        this.assessment = assessment != null ? assessment : "Tidak disebutkan";
        this.plan = plan != null ? plan : "Tidak disebutkan";
        this.intervention = intervention != null ? intervention : "Tidak disebutkan";
        this.evaluation = evaluation != null ? evaluation : "Tidak disebutkan";
        this.vitalSigns = null;
    }

    // Constructor dengan vital signs
    public SOAPResult(String subjective, String objective, String assessment,
            String plan, String intervention, String evaluation, VitalSigns vitalSigns) {
        this.subjective = subjective != null ? subjective : "Tidak disebutkan";
        this.objective = objective != null ? objective : "Tidak disebutkan";
        this.assessment = assessment != null ? assessment : "Tidak disebutkan";
        this.plan = plan != null ? plan : "Tidak disebutkan";
        this.intervention = intervention != null ? intervention : "Tidak disebutkan";
        this.evaluation = evaluation != null ? evaluation : "Tidak disebutkan";
        this.vitalSigns = vitalSigns;
    }

    // Getters
    public String getSubjective() {
        return subjective;
    }

    public String getObjective() {
        return objective;
    }

    public String getAssessment() {
        return assessment;
    }

    public String getPlan() {
        return plan;
    }

    public String getIntervention() {
        return intervention;
    }

    public String getEvaluation() {
        return evaluation;
    }

    public VitalSigns getVitalSigns() {
        return vitalSigns;
    }

    // Setters
    public void setSubjective(String subjective) {
        this.subjective = subjective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public void setIntervention(String intervention) {
        this.intervention = intervention;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    public void setVitalSigns(VitalSigns vitalSigns) {
        this.vitalSigns = vitalSigns;
    }

    public boolean isEmpty() {
        return (subjective == null || subjective.trim().isEmpty() || subjective.equals("Tidak disebutkan"))
                && (objective == null || objective.trim().isEmpty() || objective.equals("Tidak disebutkan"))
                && (assessment == null || assessment.trim().isEmpty() || assessment.equals("Tidak disebutkan"));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SOAPResult{");
        sb.append("S='").append(subjective).append("', ");
        sb.append("O='").append(objective).append("', ");
        sb.append("A='").append(assessment).append("'");
        if (vitalSigns != null) {
            sb.append(", VitalSigns=").append(vitalSigns.toString());
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Inner class untuk menyimpan vital signs
     */
    public static class VitalSigns {

        private String tekananDarah;
        private String nadi;
        private String suhu;
        private String respirasi;
        private String tinggiBadan;
        private String beratBadan;
        private String gcs;
        private String spo2;

        public VitalSigns() {
            // Constructor default
        }

        public VitalSigns(String tekananDarah, String nadi, String suhu, String respirasi,
                String tinggiBadan, String beratBadan, String gcs, String spo2) {
            this.tekananDarah = tekananDarah != null ? tekananDarah : "";
            this.nadi = nadi != null ? nadi : "";
            this.suhu = suhu != null ? suhu : "";
            this.respirasi = respirasi != null ? respirasi : "";
            this.tinggiBadan = tinggiBadan != null ? tinggiBadan : "";
            this.beratBadan = beratBadan != null ? beratBadan : "";
            this.gcs = gcs != null ? gcs : "";
            this.spo2 = spo2 != null ? spo2 : "";
        }

        // Getters
        public String getTekananDarah() {
            return tekananDarah;
        }

        public String getNadi() {
            return nadi;
        }

        public String getSuhu() {
            return suhu;
        }

        public String getRespirasi() {
            return respirasi;
        }

        public String getTinggiBadan() {
            return tinggiBadan;
        }

        public String getBeratBadan() {
            return beratBadan;
        }

        public String getGcs() {
            return gcs;
        }

        public String getSpo2() {
            return spo2;
        }

        // Setters
        public void setTekananDarah(String tekananDarah) {
            this.tekananDarah = tekananDarah;
        }

        public void setNadi(String nadi) {
            this.nadi = nadi;
        }

        public void setSuhu(String suhu) {
            this.suhu = suhu;
        }

        public void setRespirasi(String respirasi) {
            this.respirasi = respirasi;
        }

        public void setTinggiBadan(String tinggiBadan) {
            this.tinggiBadan = tinggiBadan;
        }

        public void setBeratBadan(String beratBadan) {
            this.beratBadan = beratBadan;
        }

        public void setGcs(String gcs) {
            this.gcs = gcs;
        }

        public void setSpo2(String spo2) {
            this.spo2 = spo2;
        }

        /**
         * Check if any vital sign has value
         */
        public boolean hasData() {
            return !isEmpty(tekananDarah) || !isEmpty(nadi) || !isEmpty(suhu)
                    || !isEmpty(respirasi) || !isEmpty(tinggiBadan) || !isEmpty(beratBadan)
                    || !isEmpty(gcs) || !isEmpty(spo2);
        }

        /**
         * Get formatted string of all vital signs
         */
        public String getFormattedString() {
            StringBuilder sb = new StringBuilder();

            if (!isEmpty(tekananDarah)) {
                sb.append("TD: ").append(tekananDarah).append(" mmHg, ");
            }
            if (!isEmpty(nadi)) {
                sb.append("Nadi: ").append(nadi).append(" x/menit, ");
            }
            if (!isEmpty(suhu)) {
                sb.append("Suhu: ").append(suhu).append("°C, ");
            }
            if (!isEmpty(respirasi)) {
                sb.append("RR: ").append(respirasi).append(" x/menit, ");
            }
            if (!isEmpty(gcs)) {
                sb.append("GCS: ").append(gcs).append(", ");
            }
            if (!isEmpty(spo2)) {
                sb.append("SpO2: ").append(spo2).append("%, ");
            }
            if (!isEmpty(tinggiBadan)) {
                sb.append("TB: ").append(tinggiBadan).append(" cm, ");
            }
            if (!isEmpty(beratBadan)) {
                sb.append("BB: ").append(beratBadan).append(" kg, ");
            }

            String result = sb.toString();
            if (result.endsWith(", ")) {
                result = result.substring(0, result.length() - 2);
            }

            return result;
        }

        private boolean isEmpty(String value) {
            return value == null || value.trim().isEmpty();
        }

        @Override
        public String toString() {
            return "VitalSigns{"
                    + "TD='" + tekananDarah + "', "
                    + "Nadi='" + nadi + "', "
                    + "Suhu='" + suhu + "', "
                    + "RR='" + respirasi + "', "
                    + "GCS='" + gcs + "', "
                    + "SpO2='" + spo2 + "', "
                    + "TB='" + tinggiBadan + "', "
                    + "BB='" + beratBadan + "'"
                    + "}";
        }
    }
}
