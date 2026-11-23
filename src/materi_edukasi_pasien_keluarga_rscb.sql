CREATE TABLE `materi_edukasi_pasien_keluarga_rscb` (
  `no_rawat` varchar(17) NOT NULL,
  `tanggal` date NOT NULL,
  `jam` time NOT NULL,
  `materi_edukasi` varchar(100) DEFAULT NULL,
  `kode_leaflet` varchar(50) DEFAULT NULL,
  `lama_edukasi` varchar(10) DEFAULT NULL,
  `hasil_verifikasi` enum('Sudah Mengerti','Re - Edukasi','Re - Demonstrasi') DEFAULT NULL,
  `tgl_re_edukasi` date DEFAULT NULL,
  `nip` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`no_rawat`,`tanggal`,`jam`),
  KEY `nip` (`nip`),
  CONSTRAINT `materi_edukasi_pasien_keluarga_rscb_ibfk_1` FOREIGN KEY (`no_rawat`) REFERENCES `reg_periksa` (`no_rawat`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `materi_edukasi_pasien_keluarga_rscb_ibfk_2` FOREIGN KEY (`nip`) REFERENCES `pegawai` (`nik`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
