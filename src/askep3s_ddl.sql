-- ============================================================
-- DDL + SEED: Modul Asuhan Keperawatan SDKI/SLKI/SIKI
-- Standar: Tim Pokja DPP PPNI (SDKI Ed.1 2017, SLKI/SIKI Ed.1 2019)
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ==================== MASTER TABLES ==========================

CREATE TABLE IF NOT EXISTS mst_sdki_kategori (
    id          INT NOT NULL,
    kode        VARCHAR(10) NOT NULL,
    nama        VARCHAR(50) NOT NULL,
    subkategori VARCHAR(100),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mst_sdki (
    id          INT NOT NULL,
    kode        VARCHAR(10) NOT NULL,
    label       VARCHAR(255) NOT NULL,
    definisi    TEXT,
    jenis       VARCHAR(20),
    id_kategori INT,
    aktif       TINYINT(1) DEFAULT 1,
    PRIMARY KEY (id),
    KEY fk_sdki_kat (id_kategori)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mst_sdki_tanda_gejala (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    id_sdki     INT NOT NULL,
    tipe        VARCHAR(10),
    kelompok    VARCHAR(20),
    deskripsi   TEXT,
    urutan      INT DEFAULT 0,
    KEY fk_tg_sdki (id_sdki)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mst_sdki_etiologi (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    id_sdki     INT NOT NULL,
    deskripsi   TEXT,
    KEY fk_et_sdki (id_sdki)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mst_slki (
    id          INT NOT NULL,
    kode        VARCHAR(10) NOT NULL,
    label       VARCHAR(255) NOT NULL,
    definisi    TEXT,
    aktif       TINYINT(1) DEFAULT 1,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mst_slki_indikator (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    id_slki       INT NOT NULL,
    label         VARCHAR(255) NOT NULL,
    tipe_skala    VARCHAR(20),
    keterangan_1  VARCHAR(100),
    keterangan_5  VARCHAR(100),
    urutan        INT DEFAULT 0,
    KEY fk_ind_slki (id_slki)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mst_siki (
    id          INT NOT NULL,
    kode        VARCHAR(10) NOT NULL,
    label       VARCHAR(255) NOT NULL,
    definisi    TEXT,
    tipe        VARCHAR(20),
    aktif       TINYINT(1) DEFAULT 1,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS mst_siki_tindakan (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    id_siki     INT NOT NULL,
    komponen    VARCHAR(20),
    deskripsi   TEXT,
    urutan      INT DEFAULT 0,
    KEY fk_td_siki (id_siki)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================== BRIDGE TABLES ==========================

CREATE TABLE IF NOT EXISTS map_sdki_slki (
    id_sdki     INT NOT NULL,
    id_slki     INT NOT NULL,
    prioritas   INT DEFAULT 1,
    PRIMARY KEY (id_sdki, id_slki)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS map_sdki_siki (
    id_sdki     INT NOT NULL,
    id_siki     INT NOT NULL,
    tipe        VARCHAR(20),
    PRIMARY KEY (id_sdki, id_siki)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================== TRANSACTION TABLES =====================

CREATE TABLE IF NOT EXISTS askep_header (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    no_rawat        VARCHAR(20) NOT NULL,
    no_rm           VARCHAR(20),
    id_perawat      VARCHAR(50),
    nm_perawat      VARCHAR(100),
    tgl_askep       DATE NOT NULL,
    catatan         TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    KEY idx_ah_norawat (no_rawat)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS askep_diagnosis (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    id_header       INT NOT NULL,
    id_sdki         INT NOT NULL,
    kode_sdki       VARCHAR(10),
    label_sdki      VARCHAR(255),
    etiologi        TEXT,
    tanda_mayor     TEXT,
    tanda_minor     TEXT,
    tgl_diagnosa    DATE,
    urutan          INT DEFAULT 1,
    status          VARCHAR(20) DEFAULT 'aktif',
    KEY idx_ad_header (id_header)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS askep_luaran (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    id_diagnosis        INT NOT NULL,
    id_slki             INT NOT NULL,
    label_slki          VARCHAR(255),
    skala_awal          INT DEFAULT 1,
    skala_target        INT DEFAULT 3,
    waktu_target_hari   INT DEFAULT 3,
    KEY idx_al_diag (id_diagnosis)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS askep_luaran_indikator (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    id_luaran       INT NOT NULL,
    id_indikator    INT,
    label_indikator VARCHAR(255),
    skala_awal      INT DEFAULT 1,
    skala_target    INT DEFAULT 3,
    KEY idx_ali_luaran (id_luaran)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS askep_intervensi (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    id_diagnosis    INT NOT NULL,
    id_siki         INT NOT NULL,
    label_siki      VARCHAR(255),
    tindakan_dipilih TEXT,
    KEY idx_ai_diag (id_diagnosis)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS askep_implementasi (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    id_diagnosis    INT NOT NULL,
    id_siki         INT,
    tgl_impl        DATE,
    shift           VARCHAR(20),
    catatan         TEXT,
    id_perawat      VARCHAR(50),
    nm_perawat      VARCHAR(100),
    KEY idx_aimpl_diag (id_diagnosis)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS askep_evaluasi (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    id_diagnosis    INT NOT NULL,
    tgl_evaluasi    DATE NOT NULL,
    shift           VARCHAR(20),
    soap_s          TEXT,
    soap_o          TEXT,
    soap_a          TEXT,
    soap_p          TEXT,
    kesimpulan      VARCHAR(50),
    id_perawat      VARCHAR(50),
    nm_perawat      VARCHAR(100),
    KEY idx_ae_diag (id_diagnosis)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS askep_evaluasi_indikator (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    id_evaluasi     INT NOT NULL,
    label_indikator VARCHAR(255),
    skala_awal      INT,
    skala_target    INT,
    skala_saat_ini  INT,
    KEY idx_aei_eval (id_evaluasi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================== SEED DATA ==============================

INSERT IGNORE INTO mst_sdki_kategori (id, kode, nama, subkategori) VALUES
(1,  'F-RS',  'Fisiologis',  'Respirasi'),
(2,  'F-SI',  'Fisiologis',  'Sirkulasi'),
(3,  'F-NC',  'Fisiologis',  'Nutrisi & Cairan'),
(4,  'F-EL',  'Fisiologis',  'Eliminasi'),
(5,  'F-AK',  'Fisiologis',  'Aktivitas & Istirahat'),
(6,  'F-NS',  'Fisiologis',  'Neurosensori'),
(7,  'F-RP',  'Fisiologis',  'Reproduksi & Seksualitas'),
(8,  'F-NY',  'Fisiologis',  'Nyeri & Kenyamanan'),
(9,  'F-IT',  'Fisiologis',  'Integritas Ego'),
(10, 'P-NY',  'Psikologis',  'Nyeri & Kenyamanan'),
(11, 'P-IT',  'Psikologis',  'Integritas Ego'),
(12, 'P-PE',  'Psikologis',  'Pertumbuhan & Perkembangan'),
(13, 'B-PY',  'Perilaku',    'Penyuluhan & Pembelajaran'),
(14, 'B-KS',  'Perilaku',    'Kebersihan Diri'),
(15, 'R-IS',  'Relasional',  'Interaksi Sosial'),
(16, 'L-KA',  'Lingkungan',  'Keamanan & Proteksi');

INSERT IGNORE INTO mst_sdki (id, kode, label, definisi, jenis, id_kategori, aktif) VALUES
(1,  'D.0001', 'Bersihan jalan napas tidak efektif','Ketidakmampuan membersihkan sekret atau obstruksi jalan napas untuk mempertahankan jalan napas tetap paten','aktual',1,1),
(2,  'D.0003', 'Gangguan pertukaran gas','Kelebihan atau kekurangan oksigenasi dan/atau eliminasi karbondioksida pada membran alveolus-kapiler','aktual',1,1),
(3,  'D.0005', 'Pola napas tidak efektif','Inspirasi dan/atau ekspirasi yang tidak memberikan ventilasi adekuat','aktual',1,1),
(4,  'D.0006', 'Risiko aspirasi','Berisiko mengalami masuknya sekresi gastrointestinal, sekresi orofaring, benda cair atau padat ke dalam saluran trakeobronkial','risiko',1,1),
(5,  'D.0008', 'Penurunan curah jantung','Ketidakadekuatan jantung memompa darah untuk memenuhi kebutuhan metabolisme tubuh','aktual',2,1),
(6,  'D.0009', 'Perfusi perifer tidak efektif','Penurunan sirkulasi darah pada level kapiler yang dapat mengganggu metabolisme tubuh','aktual',2,1),
(7,  'D.0012', 'Risiko perdarahan','Berisiko mengalami kehilangan darah baik internal maupun eksternal','risiko',2,1),
(8,  'D.0017', 'Risiko perfusi serebral tidak efektif','Berisiko mengalami penurunan sirkulasi jaringan otak','risiko',2,1),
(9,  'D.0019', 'Defisit nutrisi','Asupan nutrisi tidak cukup untuk memenuhi kebutuhan metabolisme','aktual',3,1),
(10, 'D.0022', 'Diare','Pengeluaran feses yang sering, lunak, dan tidak berbentuk','aktual',3,1),
(11, 'D.0023', 'Hipervolemia','Peningkatan volume cairan intravaskuler, interstisial, dan/atau intraseluler','aktual',3,1),
(12, 'D.0024', 'Hipovolemia','Penurunan volume cairan intravaskuler, interstisial, dan/atau intraseluler','aktual',3,1),
(13, 'D.0032', 'Mual','Perasaan tidak nyaman pada bagian belakang tenggorokan atau lambung yang dapat mengakibatkan muntah','aktual',3,1),
(14, 'D.0033', 'Muntah','Pengeluaran isi lambung melalui mulut','aktual',3,1),
(15, 'D.0036', 'Risiko defisit nutrisi','Berisiko mengalami asupan nutrisi tidak cukup untuk memenuhi kebutuhan metabolisme','risiko',3,1),
(16, 'D.0040', 'Gangguan eliminasi urine','Disfungsi eliminasi urine','aktual',4,1),
(17, 'D.0049', 'Konstipasi','Penurunan defekasi normal yang disertai pengeluaran feses sulit dan tidak tuntas serta feses kering dan banyak','aktual',4,1),
(18, 'D.0050', 'Inkontinensia fekal','Perubahan kebiasaan buang air besar normal yang ditandai dengan pengeluaran feses secara tidak sengaja','aktual',4,1),
(19, 'D.0054', 'Gangguan mobilitas fisik','Keterbatasan dalam gerakan fisik dari satu atau lebih ekstremitas secara mandiri','aktual',5,1),
(20, 'D.0055', 'Gangguan pola tidur','Gangguan kualitas dan kuantitas waktu tidur akibat faktor eksternal','aktual',5,1),
(21, 'D.0056', 'Intoleransi aktivitas','Ketidakcukupan energi untuk melakukan aktivitas sehari-hari','aktual',5,1),
(22, 'D.0057', 'Keletihan','Penurunan kapasitas kerja fisik dan mental yang tidak pulih dengan istirahat','aktual',5,1),
(23, 'D.0077', 'Risiko jatuh','Berisiko mengalami kerusakan fisik dan gangguan kesehatan akibat terjatuh','risiko',5,1),
(24, 'D.0077', 'Nyeri akut','Pengalaman sensorik atau emosional yang berkaitan dengan kerusakan jaringan aktual atau fungsional dengan onset mendadak atau lambat dan berintensitas ringan hingga berat yang berlangsung kurang dari 3 bulan','aktual',8,1),
(25, 'D.0078', 'Nyeri kronis','Pengalaman sensorik atau emosional yang berkaitan dengan kerusakan jaringan aktual atau fungsional dengan onset mendadak atau lambat dan berintensitas ringan hingga berat dan konstan yang berlangsung lebih dari 3 bulan','aktual',8,1),
(26, 'D.0080', 'Ansietas','Kondisi emosi dan pengalaman subjektif individu terhadap objek yang tidak jelas dan spesifik akibat antisipasi bahaya yang memungkinkan individu melakukan tindakan untuk menghadapi ancaman','aktual',11,1),
(27, 'D.0083', 'Defisit pengetahuan','Ketiadaan atau kurangnya informasi kognitif yang berkaitan dengan topik tertentu','aktual',13,1),
(28, 'D.0111', 'Gangguan integritas kulit/jaringan','Kerusakan kulit (dermis dan/atau epidermis) atau jaringan','aktual',16,1),
(29, 'D.0130', 'Hipertermia','Suhu tubuh meningkat di atas rentang normal tubuh','aktual',16,1),
(30, 'D.0142', 'Risiko infeksi','Berisiko mengalami peningkatan terserang organisme patogenik','risiko',16,1);

INSERT IGNORE INTO mst_sdki_tanda_gejala (id_sdki, tipe, kelompok, deskripsi, urutan) VALUES
(1,'mayor','objektif','Batuk tidak efektif',1),(1,'mayor','objektif','Tidak mampu batuk',2),(1,'mayor','objektif','Sputum berlebih',3),(1,'mayor','objektif','Mengi, wheezing, dan/atau ronkhi kering',4),
(1,'minor','subjektif','Dispnea',1),(1,'minor','subjektif','Sulit bicara',2),(1,'minor','objektif','Gelisah',3),(1,'minor','objektif','Sianosis',4),(1,'minor','objektif','Bunyi napas menurun',5),(1,'minor','objektif','Frekuensi napas berubah',6),(1,'minor','objektif','Pola napas berubah',7),
(2,'mayor','subjektif','Dispnea',1),(2,'mayor','objektif','PCO2 meningkat/menurun',2),(2,'mayor','objektif','PO2 menurun',3),(2,'mayor','objektif','Takikardia',4),(2,'mayor','objektif','pH arteri abnormal',5),
(2,'minor','subjektif','Pusing',1),(2,'minor','subjektif','Penglihatan kabur',2),(2,'minor','objektif','Sianosis',3),(2,'minor','objektif','Diaforesis',4),(2,'minor','objektif','Gelisah',5),(2,'minor','objektif','Napas cuping hidung',6),(2,'minor','objektif','Pola napas abnormal',7),(2,'minor','objektif','Warna kulit abnormal (pucat/kebiruan)',8),
(3,'mayor','subjektif','Dispnea',1),(3,'mayor','objektif','Penggunaan otot bantu pernapasan',2),(3,'mayor','objektif','Fase ekspirasi memanjang',3),(3,'mayor','objektif','Pola napas abnormal',4),
(3,'minor','subjektif','Ortopnea',1),(3,'minor','objektif','Pernapasan pursed-lip',2),(3,'minor','objektif','Pernapasan cuping hidung',3),(3,'minor','objektif','Diameter thoraks anterior-posterior meningkat',4),(3,'minor','objektif','Ventilasi semenit menurun',5),(3,'minor','objektif','Kapasitas vital menurun',6),
(9,'mayor','subjektif','Kram/nyeri abdomen',1),(9,'mayor','objektif','Berat badan menurun minimal 10% di bawah rentang ideal',2),
(9,'minor','subjektif','Cepat kenyang setelah makan',1),(9,'minor','subjektif','Nafsu makan menurun',2),(9,'minor','objektif','Bising usus hiperaktif',3),(9,'minor','objektif','Otot mengunyah lemah',4),(9,'minor','objektif','Otot menelan lemah',5),(9,'minor','objektif','Membran mukosa pucat',6),
(19,'mayor','subjektif','Mengeluh sulit menggerakkan ekstremitas',1),(19,'mayor','objektif','Kekuatan otot menurun',2),(19,'mayor','objektif','Rentang gerak (ROM) menurun',3),
(19,'minor','subjektif','Nyeri saat bergerak',1),(19,'minor','subjektif','Enggan melakukan pergerakan',2),(19,'minor','objektif','Sendi kaku',3),(19,'minor','objektif','Gerakan tidak terkoordinasi',4),(19,'minor','objektif','Gerakan terbatas',5),
(24,'mayor','subjektif','Mengeluh nyeri',1),(24,'mayor','objektif','Tampak meringis',2),(24,'mayor','objektif','Bersikap protektif (waspada, posisi menghindari nyeri)',3),(24,'mayor','objektif','Gelisah',4),(24,'mayor','objektif','Frekuensi nadi meningkat',5),(24,'mayor','objektif','Sulit tidur',6),
(24,'minor','objektif','Tekanan darah meningkat',1),(24,'minor','objektif','Pola napas berubah',2),(24,'minor','objektif','Nafsu makan berubah',3),(24,'minor','objektif','Proses berpikir terganggu',4),(24,'minor','objektif','Menarik diri',5),(24,'minor','objektif','Berfokus pada diri sendiri',6),(24,'minor','objektif','Diaforesis',7),
(26,'mayor','subjektif','Merasa bingung',1),(26,'mayor','subjektif','Merasa khawatir dengan akibat dari kondisi yang dihadapi',2),(26,'mayor','subjektif','Sulit berkonsentrasi',3),(26,'mayor','objektif','Tampak gelisah',4),(26,'mayor','objektif','Tampak tegang',5),(26,'mayor','objektif','Sulit tidur',6),
(26,'minor','subjektif','Mengeluh pusing',1),(26,'minor','subjektif','Anoreksia',2),(26,'minor','objektif','Frekuensi napas meningkat',3),(26,'minor','objektif','Frekuensi nadi meningkat',4),(26,'minor','objektif','Tekanan darah meningkat',5),(26,'minor','objektif','Diaforesis',6),(26,'minor','objektif','Tremor',7),
(29,'mayor','objektif','Suhu tubuh di atas nilai normal (>37.5°C)',1),
(29,'minor','subjektif','Kulit merah',1),(29,'minor','objektif','Kejang',2),(29,'minor','objektif','Takikardia',3),(29,'minor','objektif','Takipnea',4),(29,'minor','objektif','Kulit terasa hangat',5);

INSERT IGNORE INTO mst_sdki_etiologi (id_sdki, deskripsi) VALUES
(1,'Spasme jalan napas'),(1,'Hipersekresi jalan napas'),(1,'Disfungsi neuromuskuler'),(1,'Benda asing dalam jalan napas'),(1,'Adanya jalan napas buatan'),(1,'Sekresi yang tertahan'),(1,'Hiperplasia dinding jalan napas'),(1,'Proses infeksi'),(1,'Respon alergi'),(1,'Efek agen farmakologis (anestesi)'),
(2,'Ketidakseimbangan ventilasi-perfusi'),(2,'Perubahan membran alveolus-kapiler'),
(3,'Depresi pusat pernapasan'),(3,'Hambatan upaya napas (nyeri saat bernapas, kelemahan otot napas)'),(3,'Deformitas dinding dada'),(3,'Gangguan neuromuskular'),(3,'Penurunan energi'),(3,'Obesitas'),(3,'Kecemasan'),
(9,'Ketidakmampuan menelan makanan'),(9,'Ketidakmampuan mencerna makanan'),(9,'Ketidakmampuan mengabsorbsi nutrien'),(9,'Peningkatan kebutuhan metabolisme'),(9,'Faktor ekonomi (ketidakmampuan membeli makanan)'),(9,'Faktor psikologis (stres, keengganan makan)'),
(19,'Kerusakan integritas struktur tulang'),(19,'Ketidakbugaran fisik'),(19,'Penurunan kendali otot'),(19,'Penurunan massa otot'),(19,'Penurunan kekuatan otot'),(19,'Keterlambatan perkembangan'),(19,'Kekakuan sendi'),(19,'Kontraktur'),(19,'Malnutrisi'),(19,'Gangguan muskuloskeletal'),(19,'Gangguan neuromuskular'),(19,'Nyeri'),
(24,'Agen pencedera fisiologis (inflamasi, iskemia, neoplasma)'),(24,'Agen pencedera kimiawi (terbakar, bahan kimia iritan)'),(24,'Agen pencedera fisik (abses, amputasi, luka bakar, trauma)'),
(26,'Krisis situasional'),(26,'Kebutuhan tidak terpenuhi'),(26,'Krisis maturasional'),(26,'Ancaman terhadap konsep diri'),(26,'Ancaman terhadap kematian'),(26,'Kekhawatiran mengalami kegagalan'),(26,'Disfungsi sistem keluarga'),(26,'Penyalahgunaan zat'),(26,'Kurang terpapar informasi'),
(29,'Dehidrasi'),(29,'Terpapar lingkungan panas'),(29,'Proses penyakit (infeksi, kanker)'),(29,'Peningkatan laju metabolisme'),(29,'Respons trauma'),(29,'Aktivitas berlebihan'),(29,'Penggunaan inkubator');

INSERT IGNORE INTO mst_slki (id, kode, label, definisi, aktif) VALUES
(1,  'L.01001', 'Bersihan jalan napas','Kemampuan membersihkan sekret atau obstruksi jalan napas untuk mempertahankan jalan napas tetap paten',1),
(2,  'L.01003', 'Pertukaran gas','Oksigenasi dan/atau eliminasi karbondioksida pada membran alveolus-kapiler dalam batas normal',1),
(3,  'L.01004', 'Pola napas','Inspirasi dan/atau ekspirasi yang memberikan ventilasi adekuat',1),
(4,  'L.02008', 'Curah jantung','Keadekuatan jantung memompa darah untuk memenuhi kebutuhan metabolisme tubuh',1),
(5,  'L.02011', 'Perfusi perifer','Keadekuatan aliran darah pembuluh darah distal untuk menunjang fungsi jaringan',1),
(6,  'L.03030', 'Status nutrisi','Keadekuatan asupan nutrisi untuk memenuhi kebutuhan metabolisme',1),
(7,  'L.03024', 'Keseimbangan cairan','Keseimbangan cairan di dalam kompartemen intrasel dan ekstrasel tubuh',1),
(8,  'L.04036', 'Eliminasi fekal','Kemampuan saluran gastrointestinal membentuk dan mengeluarkan feses',1),
(9,  'L.04037', 'Eliminasi urine','Kemampuan sistem urinarius untuk menyaring limbah, toksin, dan zat terlarut lain dari darah',1),
(10, 'L.05042', 'Mobilitas fisik','Kemampuan dalam gerakan fisik dari satu atau lebih ekstremitas secara mandiri',1),
(11, 'L.05044', 'Toleransi aktivitas','Respons fisiologis terhadap aktivitas yang membutuhkan tenaga',1),
(12, 'L.05045', 'Tingkat keletihan','Kapasitas kerja fisik dan mental yang pulih dengan istirahat',1),
(13, 'L.08066', 'Tingkat nyeri','Pengalaman sensorik atau emosional yang berkaitan dengan kerusakan jaringan aktual atau fungsional',1),
(14, 'L.09093', 'Tingkat ansietas','Kondisi emosi dan pengalaman subjektif terhadap objek yang tidak jelas',1),
(15, 'L.10103', 'Tingkat pengetahuan','Kecukupan informasi kognitif yang berkaitan dengan topik tertentu',1),
(16, 'L.12111', 'Integritas kulit dan jaringan','Keutuhan kulit (dermis dan/atau epidermis) atau jaringan',1),
(17, 'L.14134', 'Termoregulasi','Pengaturan suhu tubuh agar tetap berada pada rentang normal',1),
(18, 'L.14137', 'Tingkat infeksi','Derajat infeksi berdasarkan observasi atau sumber informasi',1);

INSERT IGNORE INTO mst_slki_indikator (id_slki, label, tipe_skala, keterangan_1, keterangan_5, urutan) VALUES
(1,'Batuk efektif','meningkat','Menurun','Meningkat',1),(1,'Produksi sputum','menurun','Meningkat','Menurun',2),(1,'Mengi','menurun','Meningkat','Menurun',3),(1,'Wheezing','menurun','Meningkat','Menurun',4),(1,'Dispnea','menurun','Meningkat','Menurun',5),(1,'Sianosis','menurun','Meningkat','Menurun',6),(1,'Frekuensi napas','membaik','Memburuk','Membaik',7),(1,'Pola napas','membaik','Memburuk','Membaik',8),
(2,'Dispnea','menurun','Meningkat','Menurun',1),(2,'PCO2','membaik','Memburuk','Membaik',2),(2,'PO2','membaik','Memburuk','Membaik',3),(2,'Takikardia','membaik','Memburuk','Membaik',4),(2,'pH arteri','membaik','Memburuk','Membaik',5),(2,'Sianosis','menurun','Meningkat','Menurun',6),(2,'Pola napas','membaik','Memburuk','Membaik',7),
(6,'Porsi makan yang dihabiskan','meningkat','Menurun','Meningkat',1),(6,'Kekuatan otot pengunyah','meningkat','Menurun','Meningkat',2),(6,'Kekuatan otot menelan','meningkat','Menurun','Meningkat',3),(6,'Serum albumin','meningkat','Menurun','Meningkat',4),(6,'Nyeri abdomen','menurun','Meningkat','Menurun',5),(6,'Nafsu makan','membaik','Memburuk','Membaik',6),(6,'Berat badan','membaik','Memburuk','Membaik',7),
(10,'Pergerakan ekstremitas','meningkat','Menurun','Meningkat',1),(10,'Kekuatan otot','meningkat','Menurun','Meningkat',2),(10,'Rentang gerak (ROM)','meningkat','Menurun','Meningkat',3),(10,'Nyeri','menurun','Meningkat','Menurun',4),(10,'Kecemasan','menurun','Meningkat','Menurun',5),(10,'Kaku sendi','menurun','Meningkat','Menurun',6),(10,'Gerakan terbatas','menurun','Meningkat','Menurun',7),(10,'Kelemahan fisik','menurun','Meningkat','Menurun',8),
(13,'Kemampuan menuntaskan aktivitas','meningkat','Menurun','Meningkat',1),(13,'Keluhan nyeri','menurun','Meningkat','Menurun',2),(13,'Meringis','menurun','Meningkat','Menurun',3),(13,'Sikap protektif','menurun','Meningkat','Menurun',4),(13,'Gelisah','menurun','Meningkat','Menurun',5),(13,'Kesulitan tidur','menurun','Meningkat','Menurun',6),(13,'Frekuensi nadi','membaik','Memburuk','Membaik',7),(13,'Tekanan darah','membaik','Memburuk','Membaik',8),(13,'Pola napas','membaik','Memburuk','Membaik',9),
(14,'Verbalisasi kebingungan','menurun','Meningkat','Menurun',1),(14,'Verbalisasi khawatir','menurun','Meningkat','Menurun',2),(14,'Perilaku gelisah','menurun','Meningkat','Menurun',3),(14,'Perilaku tegang','menurun','Meningkat','Menurun',4),(14,'Konsentrasi','membaik','Memburuk','Membaik',5),(14,'Pola tidur','membaik','Memburuk','Membaik',6),(14,'Frekuensi nadi','membaik','Memburuk','Membaik',7),(14,'Tekanan darah','membaik','Memburuk','Membaik',8),
(17,'Menggigil','menurun','Meningkat','Menurun',1),(17,'Kulit merah','menurun','Meningkat','Menurun',2),(17,'Kejang','menurun','Meningkat','Menurun',3),(17,'Suhu tubuh','membaik','Memburuk','Membaik',4),(17,'Suhu kulit','membaik','Memburuk','Membaik',5),(17,'Tekanan darah','membaik','Memburuk','Membaik',6);

INSERT IGNORE INTO mst_siki (id, kode, label, definisi, tipe, aktif) VALUES
(1,  'I.01006', 'Latihan batuk efektif','Melatih pasien yang tidak memiliki kemampuan batuk secara efektif untuk membersihkan laring, trakea, dan bronkiolus dari sekret atau benda asing','utama',1),
(2,  'I.01011', 'Manajemen jalan napas','Mengidentifikasi dan mengelola kepatenan jalan napas','utama',1),
(3,  'I.01014', 'Pemantauan respirasi','Mengumpulkan dan menganalisis data untuk memastikan kepatenan jalan napas dan keadekuatan pertukaran gas','pendukung',1),
(4,  'I.01018', 'Pemberian oksigen','Memberikan tambahan oksigen untuk mencegah dan mengatasi kondisi kekurangan oksigen jaringan','pendukung',1),
(5,  'I.02001', 'Perawatan jantung','Mengidentifikasi, merawat, dan membatasi komplikasi akibat ketidakseimbangan antara suplai dan konsumsi oksigen miokard','utama',1),
(6,  'I.02062', 'Pemantauan tanda vital','Mengumpulkan dan menganalisis data kardiovaskular, pernapasan, dan suhu tubuh untuk menentukan dan mencegah komplikasi','pendukung',1),
(7,  'I.03119', 'Manajemen nutrisi','Mengidentifikasi dan mengelola asupan nutrisi yang seimbang','utama',1),
(8,  'I.03123', 'Pemantauan nutrisi','Mengumpulkan dan menganalisis data yang berhubungan dengan asupan gizi','pendukung',1),
(9,  'I.03136', 'Pemberian makanan','Memberikan asupan nutrisi kepada pasien yang tidak mampu menelan makanan secara mandiri','pendukung',1),
(10, 'I.04148', 'Manajemen eliminasi fekal','Mengidentifikasi dan mengelola pola eliminasi fekal','utama',1),
(11, 'I.04152', 'Manajemen eliminasi urine','Mengidentifikasi dan mengelola pola eliminasi urine yang abnormal','utama',1),
(12, 'I.05173', 'Dukungan ambulasi','Memfasilitasi pasien untuk meningkatkan aktivitas berpindah','utama',1),
(13, 'I.05178', 'Dukungan mobilisasi','Memfasilitasi pasien untuk meningkatkan aktivitas pergerakan fisik','utama',1),
(14, 'I.05186', 'Pencegahan jatuh','Mengidentifikasi dan menurunkan risiko terjatuh akibat perubahan kondisi fisik atau lingkungan','utama',1),
(15, 'I.08238', 'Manajemen nyeri','Mengidentifikasi dan mengelola pengalaman sensorik atau emosional yang berkaitan dengan kerusakan jaringan','utama',1),
(16, 'I.08243', 'Pemberian analgesik','Menyiapkan dan memberikan agen farmakologis untuk mengurangi atau menghilangkan rasa sakit','pendukung',1),
(17, 'I.08247', 'Teknik distraksi','Mengalihkan perhatian untuk mengurangi rasa tidak nyaman','pendukung',1),
(18, 'I.08249', 'Teknik relaksasi','Menggunakan teknik peregangan untuk mengurangi tanda dan gejala ketidaknyamanan','pendukung',1),
(19, 'I.09275', 'Reduksi ansietas','Meminimalkan kondisi individu dan pengalaman subyektif terhadap objek yang tidak jelas dan spesifik','utama',1),
(20, 'I.10340', 'Edukasi kesehatan','Mengajarkan pengelolaan faktor risiko penyakit dan perilaku hidup bersih serta sehat','utama',1),
(21, 'I.11348', 'Manajemen hipertermia','Mengidentifikasi dan mengelola peningkatan suhu tubuh akibat disfungsi termoregulasi','utama',1),
(22, 'I.14539', 'Pencegahan infeksi','Mengidentifikasi dan menurunkan risiko terserang organisme patogenik','utama',1),
(23, 'I.11353', 'Regulasi temperatur','Mempertahankan suhu tubuh dalam rentang normal','pendukung',1),
(24, 'I.12360', 'Perawatan integritas kulit','Mengidentifikasi dan merawat kulit untuk menjaga keutuhan, kelembaban, dan mencegah perkembangan mikroorganisme','utama',1);

INSERT IGNORE INTO mst_siki_tindakan (id_siki, komponen, deskripsi, urutan) VALUES
(1,'observasi','Identifikasi kemampuan batuk',1),(1,'observasi','Monitor adanya retensi sputum',2),(1,'observasi','Monitor tanda dan gejala infeksi saluran napas',3),(1,'observasi','Monitor input dan output cairan (jumlah dan karakteristik)',4),
(1,'terapeutik','Atur posisi semi-Fowler atau Fowler',1),(1,'terapeutik','Pasang perlak dan bengkok di pangkuan pasien',2),(1,'terapeutik','Buang sekret pada tempat sputum',3),
(1,'edukasi','Jelaskan tujuan dan prosedur batuk efektif',1),(1,'edukasi','Anjurkan tarik napas dalam melalui hidung selama 4 detik, ditahan 2 detik, keluarkan dari mulut dengan bibir mencucu selama 8 detik',2),(1,'edukasi','Anjurkan mengulangi tarik napas dalam hingga 3 kali',3),(1,'edukasi','Anjurkan batuk dengan kuat langsung setelah tarik napas dalam yang ke-3',4),
(1,'kolaborasi','Kolaborasi pemberian mukolitik atau ekspektoran, jika perlu',1),
(2,'observasi','Monitor pola napas (frekuensi, kedalaman, usaha napas)',1),(2,'observasi','Monitor bunyi napas tambahan (gurgling, mengi, wheezing, ronkhi kering)',2),(2,'observasi','Monitor sputum (jumlah, warna, aroma)',3),
(2,'terapeutik','Pertahankan kepatenan jalan napas dengan head-tilt dan chin-lift',1),(2,'terapeutik','Posisikan semi-Fowler atau Fowler',2),(2,'terapeutik','Berikan minum hangat',3),(2,'terapeutik','Lakukan fisioterapi dada, jika perlu',4),(2,'terapeutik','Berikan oksigen, jika perlu',5),
(2,'edukasi','Anjurkan asupan cairan 2000 ml/hari, jika tidak kontraindikasi',1),(2,'edukasi','Ajarkan teknik batuk efektif',2),
(2,'kolaborasi','Kolaborasi pemberian bronkodilator, ekspektoran, mukolitik, jika perlu',1),
(7,'observasi','Identifikasi status nutrisi',1),(7,'observasi','Identifikasi alergi dan intoleransi makanan',2),(7,'observasi','Identifikasi makanan yang disukai',3),(7,'observasi','Identifikasi kebutuhan kalori dan jenis nutrien',4),(7,'observasi','Monitor asupan makanan',5),(7,'observasi','Monitor berat badan',6),
(7,'terapeutik','Lakukan oral hygiene sebelum makan, jika perlu',1),(7,'terapeutik','Sajikan makanan secara menarik dan suhu yang sesuai',2),(7,'terapeutik','Berikan makanan tinggi kalori dan tinggi protein',3),(7,'terapeutik','Berikan suplemen makanan, jika perlu',4),
(7,'edukasi','Anjurkan posisi duduk, jika mampu',1),(7,'edukasi','Ajarkan diet yang diprogramkan',2),
(7,'kolaborasi','Kolaborasi dengan ahli gizi untuk menentukan jumlah kalori dan jenis nutrien yang dibutuhkan',1),
(13,'observasi','Identifikasi adanya nyeri atau keluhan fisik lainnya',1),(13,'observasi','Identifikasi toleransi fisik melakukan pergerakan',2),(13,'observasi','Monitor frekuensi jantung dan tekanan darah sebelum memulai mobilisasi',3),(13,'observasi','Monitor kondisi umum selama melakukan mobilisasi',4),
(13,'terapeutik','Fasilitasi aktivitas mobilisasi dengan alat bantu (pagar tempat tidur)',1),(13,'terapeutik','Fasilitasi melakukan pergerakan, jika perlu',2),(13,'terapeutik','Libatkan keluarga untuk membantu pasien dalam meningkatkan pergerakan',3),
(13,'edukasi','Jelaskan tujuan dan prosedur mobilisasi',1),(13,'edukasi','Anjurkan melakukan mobilisasi dini',2),(13,'edukasi','Ajarkan mobilisasi sederhana yang harus dilakukan',3),
(15,'observasi','Identifikasi lokasi, karakteristik, durasi, frekuensi, kualitas, intensitas nyeri',1),(15,'observasi','Identifikasi skala nyeri',2),(15,'observasi','Identifikasi respons nyeri non verbal',3),(15,'observasi','Identifikasi faktor yang memperberat dan memperingan nyeri',4),(15,'observasi','Monitor keberhasilan terapi komplementer yang sudah diberikan',5),(15,'observasi','Monitor efek samping penggunaan analgetik',6),
(15,'terapeutik','Berikan teknik nonfarmakologis untuk mengurangi rasa nyeri',1),(15,'terapeutik','Kontrol lingkungan yang memperberat rasa nyeri',2),(15,'terapeutik','Fasilitasi istirahat dan tidur',3),
(15,'edukasi','Jelaskan penyebab, periode, dan pemicu nyeri',1),(15,'edukasi','Jelaskan strategi meredakan nyeri',2),(15,'edukasi','Anjurkan memonitor nyeri secara mandiri',3),(15,'edukasi','Ajarkan teknik nonfarmakologis untuk mengurangi rasa nyeri',4),
(15,'kolaborasi','Kolaborasi pemberian analgetik, jika perlu',1),
(19,'observasi','Identifikasi saat tingkat ansietas berubah (kondisi, waktu, stressor)',1),(19,'observasi','Identifikasi kemampuan mengambil keputusan',2),(19,'observasi','Monitor tanda-tanda ansietas (verbal dan non verbal)',3),
(19,'terapeutik','Ciptakan suasana terapeutik untuk menumbuhkan kepercayaan',1),(19,'terapeutik','Temani pasien untuk mengurangi kecemasan, jika memungkinkan',2),(19,'terapeutik','Gunakan pendekatan yang tenang dan meyakinkan',3),(19,'terapeutik','Diskusikan perencanaan realistis tentang peristiwa yang akan datang',4),
(19,'edukasi','Jelaskan prosedur, termasuk sensasi yang mungkin dialami',1),(19,'edukasi','Informasikan secara faktual mengenai diagnosis, pengobatan, dan prognosis',2),(19,'edukasi','Anjurkan mengungkapkan perasaan dan persepsi',3),(19,'edukasi','Latih teknik relaksasi',4),
(19,'kolaborasi','Kolaborasi pemberian obat antiansietas, jika perlu',1),
(21,'observasi','Identifikasi penyebab hipertermia',1),(21,'observasi','Monitor suhu tubuh',2),(21,'observasi','Monitor kadar elektrolit',3),(21,'observasi','Monitor haluaran urine',4),(21,'observasi','Monitor komplikasi akibat hipertermia',5),
(21,'terapeutik','Sediakan lingkungan yang dingin',1),(21,'terapeutik','Longgarkan atau lepaskan pakaian',2),(21,'terapeutik','Basahi dan kipasi permukaan tubuh',3),(21,'terapeutik','Berikan cairan oral',4),(21,'terapeutik','Lakukan pendinginan eksternal (kompres dingin pada dahi, leher, dada, abdomen, aksila)',5),(21,'terapeutik','Berikan oksigen, jika perlu',6),
(21,'edukasi','Anjurkan tirah baring',1),
(21,'kolaborasi','Kolaborasi pemberian cairan dan elektrolit intravena, jika perlu',1),(21,'kolaborasi','Kolaborasi pemberian antipiretik, jika perlu',2),
(22,'observasi','Monitor tanda dan gejala infeksi lokal dan sistemik',1),
(22,'terapeutik','Batasi jumlah pengunjung',1),(22,'terapeutik','Cuci tangan sebelum dan sesudah kontak dengan pasien dan lingkungan pasien',2),(22,'terapeutik','Pertahankan teknik aseptik pada pasien berisiko tinggi',3),
(22,'edukasi','Jelaskan tanda dan gejala infeksi',1),(22,'edukasi','Ajarkan cara mencuci tangan dengan benar',2),(22,'edukasi','Anjurkan meningkatkan asupan nutrisi',3),(22,'edukasi','Anjurkan meningkatkan asupan cairan',4),
(22,'kolaborasi','Kolaborasi pemberian imunisasi, jika perlu',1);

INSERT IGNORE INTO map_sdki_slki (id_sdki, id_slki, prioritas) VALUES
(1,1,1),(1,3,2),(2,2,1),(2,3,2),(3,3,1),(3,1,2),(5,4,1),(5,5,2),(6,5,1),
(9,6,1),(11,7,1),(12,7,1),(19,10,1),(19,11,2),(21,11,1),(24,13,1),(25,13,1),
(26,14,1),(27,15,1),(29,17,1),(30,18,1);

INSERT IGNORE INTO map_sdki_siki (id_sdki, id_siki, tipe) VALUES
(1,1,'utama'),(1,2,'utama'),(1,3,'pendukung'),(1,4,'pendukung'),
(2,2,'utama'),(2,3,'utama'),(2,4,'utama'),(2,6,'pendukung'),
(3,2,'utama'),(3,3,'utama'),(3,4,'pendukung'),(3,6,'pendukung'),
(5,5,'utama'),(5,6,'pendukung'),
(9,7,'utama'),(9,8,'pendukung'),(9,9,'pendukung'),
(19,13,'utama'),(19,12,'utama'),(19,14,'pendukung'),
(24,15,'utama'),(24,16,'pendukung'),(24,17,'pendukung'),(24,18,'pendukung'),
(25,15,'utama'),(25,17,'pendukung'),(25,18,'pendukung'),
(26,19,'utama'),(26,18,'pendukung'),
(27,20,'utama'),
(29,21,'utama'),(29,23,'pendukung'),(29,6,'pendukung'),
(30,22,'utama'),(30,24,'pendukung');

SET FOREIGN_KEY_CHECKS = 1;
-- ============================================================
-- END: Run once to initialize module SDKI/SLKI/SIKI
-- ============================================================
