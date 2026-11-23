package fungsi;

import AESsecurity.PasswordSecurity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 *
 * @author Owner
 */

/**
 *
 * @author Owner
 */
public final class akses01 {
    private static final Connection koneksi=koneksiDB.condb();
    private static PreparedStatement ps,ps2;
    private static ResultSet rs,rs2;
    
    private static String kode="",kdbangsal="",alamatip="",namars="",alamatrs="",kabupatenrs="",propinsirs="",kontakrs="",emailrs="",form="",namauser="",kode_ppk="",CopyData = "",CopyData1 = "", CopyData2 = "", CopyData3 = "", CopyData4 = "", CopyData5 = ""; 
    private static int jml1=0,jml2=0,lebar=0,tinggi=0;
    private static boolean aktif=false,admin=false,user=false,vakum=false,aplikasi=false,penyakit=false,obat_penyakit=false,dokter=false,jadwal_praktek=false,petugas=false,pasien=false,registrasi=false,
            tindakan_ralan=false,kamar_inap=false,tindakan_ranap=false,operasi=false,rujukan_keluar=false,rujukan_masuk=false,beri_obat=false,
            resep_pulang=false,pasien_meninggal=false,diet_pasien=false,kelahiran_bayi=false,periksa_lab=false,periksa_radiologi=false,
            kasir_ralan=false,deposit_pasien=false,piutang_pasien=false,peminjaman_berkas=false,barcode=false,presensi_harian=false,
            presensi_bulanan=false,pegawai_admin=false,pegawai_user=false,suplier=false,satuan_barang=false,konversi_satuan=false,jenis_barang=false,
            obat=false,stok_opname_obat=false,stok_obat_pasien=false,pengadaan_obat=false,pemesanan_obat=false,penjualan_obat=false,piutang_obat=false,
            retur_ke_suplier=false,retur_dari_pembeli=false,retur_obat_ranap=false,retur_piutang_pasien=false,keuntungan_penjualan=false,keuntungan_beri_obat=false,
            sirkulasi_obat=false,ipsrs_barang=false,ipsrs_jenis_barang=false,ipsrs_pengadaan_barang=false,ipsrs_stok_keluar=false,ipsrs_rekap_pengadaan=false,ipsrs_rekap_stok_keluar=false,
            ipsrs_pengeluaran_harian=false,inventaris_jenis=false,inventaris_kategori=false,inventaris_merk=false,inventaris_ruang=false,inventaris_produsen=false,
            inventaris_koleksi=false,inventaris_inventaris=false,inventaris_sirkulasi=false,parkir_jenis=false,parkir_in=false,parkir_out=false,
            parkir_rekap_harian=false,parkir_rekap_bulanan=false,informasi_kamar=false,harian_tindakan_poli=false,obat_per_poli=false,obat_per_kamar=false,
            obat_per_dokter_ralan=false,obat_per_dokter_ranap=false,harian_dokter=false,bulanan_dokter=false,harian_paramedis=false,bulanan_paramedis=false,
            pembayaran_ralan=false,pembayaran_ranap=false,rekap_pembayaran_ralan=false,rekap_pembayaran_ranap=false,tagihan_masuk=false,tambahan_biaya=false,
            potongan_biaya=false,resep_obat=false,resume_pasien=false,penyakit_ralan=false,penyakit_ranap=false,kamar=false,tarif_ralan=false,tarif_ranap=false,
            tarif_lab=false,tarif_radiologi=false,tarif_operasi=false,akun_rekening=false,rekening_tahun=false,posting_jurnal=false,buku_besar=false,
            cashflow=false,keuangan=false,pengeluaran=false,setup_pjlab=false,setup_otolokasi=false,setup_jam_kamin=false,setup_embalase=false,tracer_login=false,
            display=false,set_harga_obat=false,set_penggunaan_tarif=false,set_oto_ralan=false,biaya_harian=false,biaya_masuk_sekali=false,set_no_rm=false,
            billing_ralan=false,billing_ranap=false,status=false,jm_ranap_dokter=false,igd=false,barcoderalan=false,barcoderanap=false,set_harga_obat_ralan=false,
            set_harga_obat_ranap=false,penyakit_pd3i=false,surveilans_pd3i=false,surveilans_ralan=false,diagnosa_pasien=false,surveilans_ranap=false,
            pny_takmenular_ranap=false,pny_takmenular_ralan=false,kunjungan_ralan=false,rl32=false,rl33=false,rl37=false,rl38=false,harian_tindakan_dokter=false,
            sms=false,sidikjari=false,jam_masuk=false,jadwal_pegawai=false,parkir_barcode=false,set_nota=false,dpjp_ranap=false,mutasi_barang=false,rl34=false,rl36=false,
            fee_visit_dokter=false,fee_bacaan_ekg=false,fee_rujukan_rontgen=false,fee_rujukan_ranap=false,fee_ralan=false,akun_bayar=false,bayar_pemesanan_obat=false,
            obat_per_dokter_peresep=false,pemasukan_lain=false,pengaturan_rekening=false,closing_kasir=false,keterlambatan_presensi=false,set_harga_kamar=false,
            rekap_per_shift=false,bpjs_cek_nik=false,bpjs_cek_kartu=false,bpjs_cek_riwayat=false,obat_per_cara_bayar=false,kunjungan_ranap=false,bayar_piutang=false,
            payment_point=false,bpjs_cek_nomor_rujukan=false,icd9=false,darurat_stok=false,retensi_rm=false,temporary_presensi=false,jurnal_harian=false,
            sirkulasi_obat2=false,edit_registrasi=false,bpjs_referensi_diagnosa=false,bpjs_referensi_poli=false,industrifarmasi=false,harian_js=false,bulanan_js=false,
            harian_paket_bhp=false,bulanan_paket_bhp=false,piutang_pasien2=false,bpjs_referensi_faskes=false,bpjs_sep=false,pengambilan_utd=false,tarif_utd=false,
            pengambilan_utd2=false,utd_medis_rusak=false,pengambilan_penunjang_utd=false,pengambilan_penunjang_utd2=false,utd_penunjang_rusak=false,
            suplier_penunjang=false,utd_donor=false,bpjs_monitoring_klaim=false,utd_cekal_darah=false,utd_komponen_darah=false,utd_stok_darah=false,
            utd_pemisahan_darah=false,harian_kamar=false,rincian_piutang_pasien=false,keuntungan_beri_obat_nonpiutang=false,reklasifikasi_ralan=false,
            reklasifikasi_ranap=false,utd_penyerahan_darah=false,hutang_obat=false,riwayat_obat_alkes_bhp=false,sensus_harian_poli=false,rl4a=false,
            aplicare_referensi_kamar=false,aplicare_ketersediaan_kamar=false,inacbg_klaim_baru_otomatis=false,inacbg_klaim_baru_manual=false,inacbg_coder_nik=false,
            mutasi_berkas=false,akun_piutang=false,harian_kso=false,bulanan_kso=false,harian_menejemen=false,bulanan_menejemen=false,inhealth_cek_eligibilitas=false,
            inhealth_referensi_jenpel_ruang_rawat=false,inhealth_referensi_poli=false,inhealth_referensi_faskes=false,inhealth_sjp=false,piutang_ralan=false,
            piutang_ranap=false,detail_piutang_penjab=false,lama_pelayanan_ralan=false,catatan_pasien=false,rl4b=false,rl4asebab=false,rl4bsebab=false,
            data_HAIs=false,harian_HAIs=false,bulanan_HAIs=false,hitung_bor=false,perusahaan_pasien=false,resep_dokter=false,lama_pelayanan_apotek=false,
            hitung_alos=false,detail_tindakan=false,rujukan_poli_internal=false,rekap_poli_anak=false,grafik_kunjungan_poli=false,grafik_kunjungan_perdokter=false,
            grafik_kunjungan_perpekerjaan=false,grafik_kunjungan_perpendidikan=false,grafik_kunjungan_pertahun=false,berkas_digital_perawatan=false,
            penyakit_menular_ranap=false,penyakit_menular_ralan=false,grafik_kunjungan_perbulan=false,grafik_kunjungan_pertanggal=false,grafik_kunjungan_demografi=false,
            grafik_kunjungan_statusdaftartahun=false,grafik_kunjungan_statusdaftartahun2=false,grafik_kunjungan_statusdaftarbulan=false,grafik_kunjungan_statusdaftarbulan2=false,
            grafik_kunjungan_statusdaftartanggal=false,grafik_kunjungan_statusdaftartanggal2=false,grafik_kunjungan_statusbataltahun=false,grafik_kunjungan_statusbatalbulan=false,
            pcare_cek_penyakit=false,grafik_kunjungan_statusbataltanggal=false,kategori_barang=false,golongan_barang=false,pemberian_obat_pertanggal=false,
            penjualan_obat_pertanggal=false,pcare_cek_kesadaran=false,pembatalan_periksa_dokter=false,pembayaran_per_unit=false,rekap_pembayaran_per_unit=false,
            grafik_kunjungan_percarabayar=false,ipsrs_pengadaan_pertanggal=false,ipsrs_stokkeluar_pertanggal=false,grafik_kunjungan_ranaptahun=false,
            pcare_cek_rujukan=false,grafik_lab_ralantahun=false,grafik_rad_ralantahun=false,cek_entry_ralan=false,inacbg_klaim_baru_manual2=false,
            permintaan_medis=false,rekap_permintaan_medis=false,surat_pemesanan_medis=false,permintaan_non_medis=false,rekap_permintaan_non_medis=false,
            surat_pemesanan_non_medis=false,grafik_per_perujuk=false,bpjs_cek_prosedur=false,bpjs_cek_kelas_rawat=false,bpjs_cek_dokter=false,
            bpjs_cek_spesialistik=false,bpjs_cek_ruangrawat=false,bpjs_cek_carakeluar=false,bpjs_cek_pasca_pulang=false,detail_tindakan_okvk=false,
            billing_parsial=false,bpjs_cek_nomor_rujukan_rs=false,bpjs_cek_rujukan_kartu_pcare=false,bpjs_cek_rujukan_kartu_rs=false,akses_depo_obat=false,
            bpjs_rujukan_keluar=false,grafik_lab_ralanbulan=false,pengeluaran_stok_apotek=false,grafik_rad_ralanbulan=false,detailjmdokter2=false,
            pengaduan_pasien=false,grafik_lab_ralanhari=false,grafik_rad_ralanhari=false,sensus_harian_ralan=false,metode_racik=false,pembayaran_akun_bayar=false,
            pengguna_obat_resep=false,rekap_pemesanan=false,master_berkas_pegawai=false,berkas_kepegawaian=false,riwayat_jabatan=false,riwayat_pendidikan=false,
            riwayat_naik_gaji=false,kegiatan_ilmiah=false,riwayat_penghargaan=false,riwayat_penelitian=false,penerimaan_non_medis=false,bayar_pesan_non_medis=false,
            hutang_barang_non_medis=false,rekap_pemesanan_non_medis=false,insiden_keselamatan=false,insiden_keselamatan_pasien=false,grafik_ikp_pertahun=false,
            grafik_ikp_perbulan=false,grafik_ikp_pertanggal=false,riwayat_data_batch=false,grafik_ikp_jenis=false,grafik_ikp_dampak=false,piutang_akun_piutang=false,
            grafik_kunjungan_per_agama=false,grafik_kunjungan_per_umur=false,suku_bangsa=false,bahasa_pasien=false,golongan_tni=false,satuan_tni=false,
            jabatan_tni=false,pangkat_tni=false,golongan_polri=false,satuan_polri=false,jabatan_polri=false,pangkat_polri=false,cacat_fisik=false,
            grafik_kunjungan_suku=false,grafik_kunjungan_bahasa=false,booking_operasi=false,mapping_poli_bpjs=false,grafik_kunjungan_per_cacat=false,
            barang_cssd=false,skdp_bpjs=false,booking_registrasi=false,bpjs_cek_propinsi=false,bpjs_cek_kabupaten=false,bpjs_cek_kecamatan=false,
            bpjs_cek_dokterdpjp=false,bpjs_cek_riwayat_rujukanrs=false,bpjs_cek_tanggal_rujukan=false,permintaan_lab=false,permintaan_radiologi=false,
            surat_indeks=false,surat_map=false,surat_almari=false,surat_rak=false,surat_ruang=false,surat_klasifikasi=false,surat_status=false,
            surat_sifat=false,surat_balas=false,surat_masuk=false,pcare_cek_dokter=false,pcare_cek_poli=false,pcare_cek_provider=false,
            pcare_cek_statuspulang=false,pcare_cek_spesialis=false,pcare_cek_subspesialis=false,pcare_cek_sarana=false,pcare_cek_khusus=false,
            pcare_cek_obat=false,pcare_cek_tindakan=false,pcare_cek_faskessubspesialis=false,pcare_cek_faskesalihrawat=false,
            pcare_cek_faskesthalasemia=false,pcare_mapping_obat=false,pcare_mapping_tindakan=false,pcare_club_prolanis=false,
            pcare_mapping_poli=false,pcare_kegiatan_kelompok=false,pcare_mapping_tindakan_ranap=false,pcare_peserta_kegiatan_kelompok=false,
            sirkulasi_obat3=false,bridging_pcare_daftar=false,pcare_mapping_dokter=false,ranap_per_ruang=false,penyakit_ranap_cara_bayar=false,
            anggota_militer_dirawat=false,set_input_parsial=false,lama_pelayanan_radiologi=false,lama_pelayanan_lab=false,bpjs_cek_sep=false,
            catatan_perawatan=false,surat_keluar=false,kegiatan_farmasi=false,stok_opname_logistik=false,sirkulasi_non_medis=false,
            rekap_lab_pertahun=false,perujuk_lab_pertahun=false,rekap_radiologi_pertahun=false,perujuk_radiologi_pertahun=false,
            jumlah_porsi_diet=false,jumlah_macam_diet=false,payment_point2=false,pembayaran_akun_bayar2=false,hapus_nota_salah=false,
            hais_perbangsal=false,ppn_obat=false,saldo_akun_perbulan=false,display_apotek=false,sisrute_referensi_faskes=false,
            sisrute_referensi_alasanrujuk=false,sisrute_referensi_diagnosa=false,sisrute_rujukan_masuk=false,sisrute_rujukan_keluar=false,
            bpjs_cek_skdp=false,data_batch=false,kunjungan_permintaan_lab=false,kunjungan_permintaan_lab2=false,kunjungan_permintaan_radiologi=false,
            kunjungan_permintaan_radiologi2=false,pcare_pemberian_obat=false,pcare_pemberian_tindakan=false,pembayaran_akun_bayar3=false,
            password_asuransi=false,kemenkes_sitt=false,siranap_ketersediaan_kamar=false,grafik_tb_periodelaporan=false,grafik_tb_rujukan=false,
            grafik_tb_riwayat=false,grafik_tb_tipediagnosis=false,grafik_tb_statushiv=false,grafik_tb_skoringanak=false,grafik_tb_konfirmasiskoring5=false,
            grafik_tb_konfirmasiskoring6=false,grafik_tb_sumberobat=false,grafik_tb_hasilakhirpengobatan=false,grafik_tb_hasilteshiv=false,
            kadaluarsa_batch=false,sisa_stok=false,obat_per_resep=false,pemakaian_air_pdam=false,limbah_b3_medis=false,grafik_air_pdam_pertanggal=false,
            grafik_air_pdam_perbulan=false,grafik_limbahb3_pertanggal=false,grafik_limbahb3_perbulan=false,limbah_domestik=false,
            grafik_limbahdomestik_pertanggal=false,grafik_limbahdomestik_perbulan=false,mutu_air_limbah=false,pest_control=false,ruang_perpustakaan=false,
            kategori_perpustakaan=false,jenis_perpustakaan=false,pengarang_perpustakaan=false,penerbit_perpustakaan=false,koleksi_perpustakaan=false,
            inventaris_perpustakaan=false,set_peminjaman_perpustakaan=false,denda_perpustakaan=false,anggota_perpustakaan=false,
            peminjaman_perpustakaan=false,bayar_denda_perpustakaan=false,ebook_perpustakaan=false,jenis_cidera_k3rs=false,penyebab_k3rs=false,
            jenis_luka_k3rs=false,lokasi_kejadian_k3rs=false,dampak_cidera_k3rs=false,jenis_pekerjaan_k3rs=false,bagian_tubuh_k3rs=false,
            peristiwa_k3rs=false,grafik_k3_pertahun=false,grafik_k3_perbulan=false,grafik_k3_pertanggal=false,grafik_k3_perjeniscidera=false,
            grafik_k3_perpenyebab=false,grafik_k3_perjenisluka=false,grafik_k3_lokasikejadian=false,grafik_k3_dampakcidera=false,
            grafik_k3_perjenispekerjaan=false,grafik_k3_perbagiantubuh=false,jenis_cidera_k3rstahun=false,penyebab_k3rstahun=false,
            jenis_luka_k3rstahun=false,lokasi_kejadian_k3rstahun=false,dampak_cidera_k3rstahun=false,jenis_pekerjaan_k3rstahun=false,
            bagian_tubuh_k3rstahun=false,sekrining_rawat_jalan=false,bpjs_histori_pelayanan=false,rekap_mutasi_berkas=false,
            skrining_ralan_pernapasan_pertahun=false,pengajuan_barang_medis=false,pengajuan_barang_nonmedis=false,grafik_kunjungan_ranapbulan=false,
            grafik_kunjungan_ranaptanggal=false,grafik_kunjungan_ranap_peruang=false,kunjungan_bangsal_pertahun=false,grafik_jenjang_jabatanpegawai=false,
            grafik_bidangpegawai=false,grafik_departemenpegawai=false,grafik_pendidikanpegawai=false,grafik_sttswppegawai=false,
            grafik_sttskerjapegawai=false,grafik_sttspulangranap=false,kip_pasien_ranap=false,kip_pasien_ralan=false,bpjs_mapping_dokterdpjp=false,
            data_triase_igd=false,master_triase_skala1=false,master_triase_skala2=false,master_triase_skala3=false,master_triase_skala4=false,
            master_triase_skala5=false,master_triase_pemeriksaan=false,master_triase_macamkasus=false,rekap_permintaan_diet=false,daftar_pasien_ranap=false,
            daftar_pasien_ranaptni=false,pengajuan_asetinventaris=false,item_apotek_jenis=false,item_apotek_kategori=false,item_apotek_golongan=false,
            item_apotek_industrifarmasi=false,obat10_terbanyak_poli=false,grafik_pengajuan_aset_urgensi=false,grafik_pengajuan_aset_status=false,
            grafik_pengajuan_aset_departemen=false,rekap_pengajuan_aset_departemen=false,grafik_kelompok_jabatanpegawai=false,grafik_resiko_kerjapegawai=false,
            grafik_emergency_indexpegawai=false,grafik_inventaris_ruang=false,harian_HAIs2=false,grafik_inventaris_jenis=false,data_resume_pasien=false,
            perkiraan_biaya_ranap=false,rekap_obat_poli=false,rekap_obat_pasien=false,grafik_HAIs_pasienbangsal=false,grafik_HAIs_pasienbulan=false,
            permintaan_perbaikan_inventaris=false,grafik_HAIs_laju_vap=false,grafik_HAIs_laju_iad=false,grafik_HAIs_laju_pleb=false,grafik_HAIs_laju_isk=false,
            grafik_HAIs_laju_ilo=false,grafik_HAIs_laju_hap=false,inhealth_mapping_poli=false,inhealth_mapping_dokter=false,inhealth_mapping_tindakan_ralan=false,
            inhealth_mapping_tindakan_ranap=false,inhealth_mapping_tindakan_radiologi=false,inhealth_mapping_tindakan_laborat=false,
            inhealth_mapping_tindakan_operasi=false,hibah_obat_bhp=false,asal_hibah=false,asuhan_gizi=false,inhealth_kirim_tagihan=false,
            sirkulasi_obat4=false,sirkulasi_obat5=false,sirkulasi_non_medis2=false,monitoring_asuhan_gizi=false,penerimaan_obat_perbulan=false,
            rekap_kunjungan=false,surat_sakit=false,penilaian_awal_keperawatan_ralan=false,permintaan_diet=false,master_masalah_keperawatan=false,
            pengajuan_cuti=false,kedatangan_pasien=false,utd_pendonor=false,toko_suplier=false,toko_jenis=false,toko_set_harga=false,
            toko_barang=false,penagihan_piutang_pasien=false,akun_penagihan_piutang=false,stok_opname_toko=false,toko_riwayat_barang=false,
            toko_surat_pemesanan=false,toko_pengajuan_barang=false,toko_penerimaan_barang=false,toko_pengadaan_barang=false,toko_hutang=false,
            toko_bayar_pemesanan=false,toko_member=false,toko_penjualan=false,registrasi_poli_per_tanggal=false,toko_piutang=false,toko_retur_beli=false,
            ipsrs_returbeli=false,ipsrs_riwayat_barang=false,pasien_corona=false,toko_pendapatan_harian=false,diagnosa_pasien_corona=false,
            perawatan_pasien_corona=false,penilaian_awal_keperawatan_gigi=false,master_masalah_keperawatan_gigi=false,toko_bayar_piutang=false,
            toko_piutang_harian=false,toko_penjualan_harian=false,deteksi_corona=false,penilaian_awal_keperawatan_kebidanan=false,pengumuman_epasien=false,
            surat_hamil=false,set_tarif_online=false,booking_periksa=false,toko_sirkulasi=false,toko_retur_jual=false,toko_retur_piutang=false,
            toko_sirkulasi2=false,toko_keuntungan_barang=false,zis_pengeluaran_penerima_dankes=false,zis_penghasilan_penerima_dankes=false,
            zis_ukuran_rumah_penerima_dankes=false,zis_dinding_rumah_penerima_dankes=false,zis_lantai_rumah_penerima_dankes=false,
            zis_atap_rumah_penerima_dankes=false,zis_kepemilikan_rumah_penerima_dankes=false,zis_kamar_mandi_penerima_dankes=false,
            zis_dapur_rumah_penerima_dankes=false,zis_kursi_rumah_penerima_dankes=false,zis_kategori_phbs_penerima_dankes=false,
            zis_elektronik_penerima_dankes=false,zis_ternak_penerima_dankes=false,zis_jenis_simpanan_penerima_dankes=false,penilaian_awal_keperawatan_anak=false,
            zis_kategori_asnaf_penerima_dankes=false,master_masalah_keperawatan_anak=false,master_imunisasi=false,zis_patologis_penerima_dankes=false,
            pcare_cek_kartu=false,surat_bebas_narkoba=false,surat_keterangan_covid=false,pemakaian_air_tanah=false,grafik_air_tanah_pertanggal=false,
            grafik_air_tanah_perbulan=false,lama_pelayanan_poli=false,hemodialisa=false,grafik_harian_hemodialisa=false,grafik_bulanan_hemodialisa=false,
            grafik_tahunan_hemodialisa=false,grafik_bulanan_meninggal=false,laporan_tahunan_irj=false,perbaikan_inventaris=false,surat_cuti_hamil=false,
            permintaan_stok_obat_pasien=false,pemeliharaan_inventaris=false,klasifikasi_pasien_ranap=false,bulanan_klasifikasi_pasien_ranap,
            harian_klasifikasi_pasien_ranap=false,klasifikasi_pasien_perbangsal=false,soap_perawatan=false,klaim_rawat_jalan=false,
            skrining_gizi=false,lama_penyiapan_rm=false,dosis_radiologi=false,demografi_umur_kunjungan=false,jam_diet_pasien=false,
            rvu_bpjs=false,verifikasi_penerimaan_farmasi=false,verifikasi_penerimaan_logistik=false,pemeriksaan_lab_pa=false,ringkasan_pengajuan_obat=false,
            ringkasan_pemesanan_obat=false,ringkasan_pengadaan_obat=false,ringkasan_penerimaan_obat=false,ringkasan_hibah_obat=false,
            ringkasan_penjualan_obat=false,ringkasan_beri_obat=false,ringkasan_piutang_obat=false,ringkasan_stok_keluar_obat=false,
            ringkasan_retur_suplier_obat=false,ringkasan_retur_pembeli_obat=false,penilaian_awal_keperawatan_ranapkebidanan=false,
            ringkasan_pengajuan_nonmedis=false,ringkasan_pemesanan_nonmedis=false,ringkasan_pengadaan_nonmedis=false,ringkasan_penerimaan_nonmedis=false,
            ringkasan_stokkeluar_nonmedis=false,ringkasan_returbeli_nonmedis=false,omset_penerimaan=false,validasi_penagihan_piutang=false,
            permintaan_ranap=false,bpjs_diagnosa_prb=false,bpjs_obat_prb=false,bpjs_surat_kontrol=false,penggunaan_bhp_ok=false,surat_keterangan_rawat_inap=false,
            surat_keterangan_sehat=false,pendapatan_per_carabayar=false,akun_host_to_host_bank_jateng=false,pembayaran_bank_jateng=false,
            bpjs_surat_pri=false,ringkasan_tindakan=false,lama_pelayanan_pasien=false,surat_sakit_pihak_2=false,tagihan_hutang_obat=false,
            referensi_mobilejkn_bpjs=false,batal_pendaftaran_mobilejkn_bpjs=false,lama_operasi=false,grafik_inventaris_kategori=false,grafik_inventaris_merk=false,
            grafik_inventaris_produsen=false,pengembalian_deposit_pasien=false,validasi_tagihan_hutang_obat=false,piutang_obat_belum_lunas=false,
            integrasi_briapi=false,pengadaan_aset_inventaris=false,akun_aset_inventaris=false,suplier_inventaris=false,penerimaan_aset_inventaris=false,
            bayar_pemesanan_iventaris=false,hutang_aset_inventaris=false,hibah_aset_inventaris=false,titip_faktur_non_medis=false,validasi_tagihan_non_medis=false,
            titip_faktur_aset=false,validasi_tagihan_aset=false,hibah_non_medis=false,pcare_alasan_tacc=false,resep_luar=false,surat_bebas_tbc=false,
            surat_buta_warna=false,surat_bebas_tato=false,surat_kewaspadaan_kesehatan=false,grafik_porsidiet_pertanggal=false,grafik_porsidiet_perbulan=false,
            grafik_porsidiet_pertahun=false,grafik_porsidiet_perbangsal=false,penilaian_awal_medis_ralan=false,master_masalah_keperawatan_mata=false,
            penilaian_awal_keperawatan_mata=false,penilaian_awal_medis_ranap=false,penilaian_awal_medis_ranap_kebidanan=false,penilaian_awal_medis_ralan_kebidanan=false,
            penilaian_awal_medis_igd=false,penilaian_awal_medis_ralan_anak=false,bpjs_referensi_poli_hfis=false,bpjs_referensi_dokter_hfis=false,
            bpjs_referensi_jadwal_hfis=false,penilaian_fisioterapi=false,bpjs_program_prb=false,bpjs_suplesi_jasaraharja=false,bpjs_data_induk_kecelakaan=false,
            bpjs_sep_internal=false,bpjs_klaim_jasa_raharja=false,bpjs_daftar_finger_print=false,bpjs_rujukan_khusus=false,pemeliharaan_gedung=false,
            grafik_perbaikan_inventaris_pertanggal=false,grafik_perbaikan_inventaris_perbulan=false,grafik_perbaikan_inventaris_pertahun=false,
            grafik_perbaikan_inventaris_perpelaksana_status=false,penilaian_mcu=false,peminjam_piutang=false,piutang_lainlain=false,cara_bayar=false,
            audit_kepatuhan_apd=false,bpjs_task_id=false,bayar_piutang_lain=false,pembayaran_akun_bayar4=false,stok_akhir_farmasi_pertanggal=false,
            riwayat_kamar_pasien=false,uji_fungsi_kfr=false,hapus_berkas_digital_perawatan=false,kategori_pengeluaran_harian=false,kategori_pemasukan_lain=false,
            pembayaran_akun_bayar5=false,ruang_ok=false,jasa_tindakan_pasien=false,telaah_resep=false,permintaan_resep_pulang=false,rekap_jm_dokter=false,
            status_data_rm=false,ubah_petugas_lab_pk=false,ubah_petugas_lab_pa=false,ubah_petugas_radiologi=false,gabung_norawat=false,gabung_rm=false,
            ringkasan_biaya_obat_pasien_pertanggal=false,master_masalah_keperawatan_igd=false,penilaian_awal_keperawatan_igd=false,bpjs_referensi_dpho_apotek=false,
            bpjs_referensi_poli_apotek=false,bayar_jm_dokter=false,bpjs_referensi_faskes_apotek=false,bpjs_referensi_spesialistik_apotek=false,
            pembayaran_briva=false,penilaian_awal_keperawatan_ranap=false,nilai_penerimaan_vendor_farmasi_perbulan=false,akun_bayar_hutang=false,
            master_rencana_keperawatan=false,laporan_tahunan_igd=false,obat_bhp_tidakbergerak=false,ringkasan_hutang_vendor_farmasi=false,
            nilai_penerimaan_vendor_nonmedis_perbulan=false,ringkasan_hutang_vendor_nonmedis=false,anggota_polri_dirawat=false,daftar_pasien_ranap_polri=false,
            soap_ralan_polri=false,soap_ranap_polri=false,laporan_penyakit_polri=false,master_rencana_keperawatan_anak=false,jumlah_pengunjung_ralan_polri=false,
            catatan_observasi_igd=false,catatan_observasi_ranap=false,catatan_observasi_ranap_kebidanan=false,catatan_observasi_ranap_postpartum=false,
            penilaian_awal_medis_ralan_tht=false,penilaian_psikologi=false,audit_cuci_tangan_medis=false,audit_pembuangan_limbah=false,ruang_audit_kepatuhan=false,
            audit_pembuangan_benda_tajam=false,audit_penanganan_darah=false,audit_pengelolaan_linen_kotor=false,audit_penempatan_pasien=false,
            audit_kamar_jenazah=false,audit_bundle_iadp=false,audit_bundle_ido=false,audit_fasilitas_kebersihan_tangan=false,audit_fasilitas_apd=false,
            audit_pembuangan_limbah_cair_infeksius=false,audit_sterilisasi_alat=false,penilaian_awal_medis_ralan_psikiatri=false,persetujuan_penolakan_tindakan=false,
            audit_bundle_isk=false,audit_bundle_plabsi=false,audit_bundle_vap=false,akun_host_to_host_bank_papua=false,pembayaran_bank_papua=false,
            penilaian_awal_medis_ralan_penyakit_dalam=false,penilaian_awal_medis_ralan_mata=false,penilaian_awal_medis_ralan_neurologi=false,sirkulasi_obat6=false,
            penilaian_awal_medis_ralan_orthopedi=false,penilaian_awal_medis_ralan_bedah=false,integrasi_khanza_health_services=false,soap_ralan_tni=false,
            soap_ranap_tni=false,jumlah_pengunjung_ralan_tni=false,laporan_penyakit_tni=false,catatan_keperawatan_ranap=false,master_rencana_keperawatan_gigi=false,
            master_rencana_keperawatan_mata=false,master_rencana_keperawatan_igd=false,master_masalah_keperawatan_psikiatri=false,master_rencana_keperawatan_psikiatri=false,
            penilaian_awal_keperawatan_psikiatri=false,pemantauan_pews_anak=false,surat_pulang_atas_permintaan_sendiri=false,template_hasil_radiologi=false,
            laporan_bulanan_irj=false,template_pemeriksaan=false,pemeriksaan_lab_mb=false,ubah_petugas_lab_mb=false,penilaian_pre_operasi=false,penilaian_pre_anestesi=false,
            perencanaan_pemulangan=false,penilaian_lanjutan_resiko_jatuh_dewasa=false,penilaian_lanjutan_resiko_jatuh_anak=false,penilaian_awal_medis_ralan_geriatri=false,
            penilaian_tambahan_pasien_geriatri=false,skrining_nutrisi_dewasa=false,skrining_nutrisi_lansia=false,hasil_pemeriksaan_usg=false,skrining_nutrisi_anak=false,
            akun_host_to_host_bank_jabar=false,pembayaran_bank_jabar=false,surat_pernyataan_pasien_umum=false,konseling_farmasi=false,pelayanan_informasi_obat=false,
            jawaban_pio_apoteker=false,surat_persetujuan_umum=false,transfer_pasien_antar_ruang=false,satu_sehat_referensi_dokter=false,satu_sehat_referensi_pasien=false,
            satu_sehat_mapping_departemen=false,satu_sehat_mapping_lokasi=false,satu_sehat_kirim_encounter=false,catatan_cek_gds=false,satu_sehat_kirim_condition=false,
            checklist_pre_operasi=false,satu_sehat_kirim_observationttv=false,signin_sebelum_anestesi=false,satu_sehat_kirim_procedure=false,operasi_per_bulan=false,
            timeout_sebelum_insisi=false,signout_sebelum_menutup_luka=false,dapur_barang=false,dapur_opname=false,satu_sehat_mapping_vaksin=false,dapur_suplier=false,
            satu_sehat_kirim_Immunization=false,checklist_post_operasi=false,dapur_pembelian=false,dapur_stok_keluar=false,dapur_riwayat_barang=false,permintaan_dapur=false,
            rekonsiliasi_obat=false,biaya_pengadaan_dapur=false,rekap_pengadaan_dapur=false,kesling_limbah_b3medis_cair=false,grafik_limbahb3cair_pertanggal=false,
            grafik_limbahb3cair_perbulan=false,rekap_biaya_registrasi=false,konfirmasi_rekonsiliasi_obat=false,satu_sehat_kirim_clinicalimpression=false,
            penilaian_pasien_terminal=false,surat_persetujuan_rawat_inap=false,monitoring_reaksi_tranfusi=false,penilaian_korban_kekerasan=false,
            penilaian_lanjutan_resiko_jatuh_lansia=false,mpp_skrining=false,penilaian_pasien_penyakit_menular=false,edukasi_pasien_keluarga_rj=false,pemantauan_pews_dewasa=false,
            penilaian_tambahan_bunuh_diri=false,bpjs_antrean_pertanggal=false,penilaian_tambahan_perilaku_kekerasan=false,penilaian_tambahan_beresiko_melarikan_diri=false,
            persetujuan_penundaan_pelayanan=false,sisa_diet_pasien=false,penilaian_awal_medis_ralan_bedah_mulut=false,penilaian_pasien_keracunan=false,
            pemantauan_meows_obstetri=false,catatan_adime_gizi=false,pengajuan_biaya=false,penilaian_awal_keperawatan_ralan_geriatri=false,master_masalah_keperawatan_geriatri=false,
            master_rencana_keperawatan_geriatri=false,checklist_kriteria_masuk_hcu=false,checklist_kriteria_keluar_hcu=false,penilaian_risiko_dekubitus=false,
            master_menolak_anjuran_medis=false,penolakan_anjuran_medis=false,laporan_tahunan_penolakan_anjuran_medis=false,template_laporan_operasi=false,hasil_tindakan_eswl=false,
            checklist_kriteria_masuk_icu=false,checklist_kriteria_keluar_icu=false,akses_dokter_lain_rawat_jalan=false,follow_up_dbd=false,penilaian_risiko_jatuh_neonatus=false,
            persetujuan_pengajuan_biaya=false,pemeriksaan_fisik_ralan_per_penyakit=false,penilaian_lanjutan_resiko_jatuh_geriatri=false,pemantauan_ews_neonatus=false,
            validasi_persetujuan_pengajuan_biaya=false,riwayat_perawatan_icare_bpjs=false,rekap_pengajuan_biaya=false,penilaian_awal_medis_ralan_kulit_kelamin=false,
            akun_host_to_host_bank_mandiri=false,penilaian_medis_hemodialisa=false,penilaian_level_kecemasan_ranap_anak=false,penilaian_lanjutan_resiko_jatuh_psikiatri=false,
            penilaian_lanjutan_skrining_fungsional=false,penilaian_medis_ralan_rehab_medik=false,laporan_anestesi=false,template_persetujuan_penolakan_tindakan=false,
            penilaian_medis_ralan_gawat_darurat_psikiatri=false,bpjs_referensi_setting_apotek=false,bpjs_referensi_obat_apotek=false,bpjs_mapping_obat_apotek=false,
            pembayaran_bank_mandiri=false,penilaian_ulang_nyeri=false,penilaian_terapi_wicara=false,bpjs_obat_23hari_apotek=false,pengkajian_restrain=false,
            bpjs_kunjungan_sep_apotek=false,bpjs_monitoring_klaim_apotek=false,bpjs_daftar_pelayanan_obat_apotek=false,penilaian_awal_medis_ralan_paru=false,
            catatan_keperawatan_ralan=false,catatan_persalinan=false,skor_aldrette_pasca_anestesi=false,skor_steward_pasca_anestesi=false,skor_bromage_pasca_anestesi=false,
            penilaian_pre_induksi=false,hasil_usg_urologi=false,hasil_usg_gynecologi=false,hasil_pemeriksaan_ekg=false,hapus_edit_sep_bpjs=false,satu_sehat_kirim_diet=false,
            satu_sehat_mapping_obat=false,dapur_ringkasan_pembelian=false,satu_sehat_kirim_medication=false,satu_sehat_kirim_medicationrequest=false,
            penatalaksanaan_terapi_okupasi=false,satu_sehat_kirim_medicationdispense=false,hasil_usg_neonatus=false,hasil_endoskopi_faring_laring=false,
            satu_sehat_mapping_radiologi=false,satu_sehat_kirim_servicerequest_radiologi=false,hasil_endoskopi_hidung=false,satu_sehat_kirim_specimen_radiologi=false,
            master_masalah_keperawatan_neonatus=false,master_rencana_keperawatan_neonatus=false,penilaian_awal_keperawatan_ranap_neonatus=false,
            satu_sehat_kirim_observation_radiologi=false,satu_sehat_kirim_diagnosticreport_radiologi=false,hasil_endoskopi_telinga=false,satu_sehat_mapping_lab=false,
            satu_sehat_kirim_servicerequest_lab=false,satu_sehat_kirim_servicerequest_labmb=false,satu_sehat_kirim_specimen_lab=false,satu_sehat_kirim_specimen_labmb=false,
            satu_sehat_kirim_observation_lab=false,satu_sehat_kirim_observation_labmb=false,satu_sehat_kirim_diagnosticreport_lab=false,satu_sehat_kirim_diagnosticreport_labmb=false,
            kepatuhan_kelengkapan_keselamatan_bedah=false,nilai_piutang_perjenis_bayar_per_bulan=false,ringkasan_piutang_jenis_bayar=false,penilaian_pasien_imunitas_rendah=false,
            balance_cairan=false,catatan_observasi_chbp=false,catatan_observasi_induksi_persalinan=false,skp_kategori_penilaian=false,skp_kriteria_penilaian=false,
            skp_penilaian=false,referensi_poli_mobilejknfktp=false,referensi_dokter_mobilejknfktp=false,skp_rekapitulasi_penilaian=false,pembayaran_pihak_ke3_bankmandiri=false,
            metode_pembayaran_bankmandiri=false,bank_tujuan_transfer_bankmandiri=false,kodetransaksi_tujuan_transfer_bankmandiri=false,konsultasi_medik=false,jawaban_konsultasi_medik=false,
            pcare_cek_alergi=false,pcare_cek_prognosa=false,data_sasaran_usiaproduktif=false,data_sasaran_usialansia=false,skrining_perilaku_merokok_sekolah_remaja=false,
            skrining_kekerasan_pada_perempuan=false,skrining_obesitas=false,skrining_risiko_kanker_payudara=false,skrining_risiko_kanker_paru=false,skrining_tbc=false,
            skrining_kesehatan_gigi_mulut_remaja=false,penilaian_awal_keperawatan_ranap_bayi=false,booking_mcu_perusahaan=false,catatan_observasi_restrain_nonfarma=false,
            catatan_observasi_ventilator=false,catatan_anestesi_sedasi=false,skrining_puma=false,satu_sehat_kirim_careplan=false,satu_sehat_kirim_medicationstatement=false,
            skrining_adiksi_nikotin=false,skrining_thalassemia=false,skrining_instrumen_sdq=false,skrining_instrumen_srq=false,checklist_pemberian_fibrinolitik=false,
            skrining_kanker_kolorektal=false,dapur_pemesanan=false,bayar_pesan_dapur=false,hutang_dapur=false,titip_faktur_dapur=false,validasi_tagihan_dapur=false,
            surat_pemesanan_dapur=false,pengajuan_barang_dapur=false,dapur_returbeli=false,hibah_dapur=false,ringkasan_penerimaan_dapur=false,ringkasan_pengajuan_dapur=false,
            ringkasan_pemesanan_dapur=false,ringkasan_returbeli_dapur=false,ringkasan_stokkeluar_dapur=false,dapur_stokkeluar_pertanggal=false,sirkulasi_dapur=false,
            sirkulasi_dapur2=false,verifikasi_penerimaan_dapur=false,nilai_penerimaan_vendor_dapur_perbulan=false,ringkasan_hutang_vendor_dapur=false,penilaian_psikologi_klinis=false,
            penilaian_awal_medis_ranap_neonatus=false,penilaian_derajat_dehidrasi=false,ringkasan_jasa_tindakan_medis=false,pendapatan_per_akun=false,hasil_pemeriksaan_echo=false,
            penilaian_bayi_baru_lahir=false,rl1_3_ketersediaan_kamar=false,pendapatan_per_akun_closing=false,pengeluaran_pengeluaran=false,skrining_diabetes_melitus=false,
            laporan_tindakan=false,pelaksanaan_informasi_edukasi=false,layanan_kedokteran_fisik_rehabilitasi=false,skrining_kesehatan_gigi_mulut_balita=false,skrining_anemia=false,
            layanan_program_kfr=false,skrining_hipertensi=false,skrining_kesehatan_penglihatan=false,catatan_observasi_hemodialisa=false,skrining_kesehatan_gigi_mulut_dewasa=false,
            skrining_risiko_kanker_serviks=false,catatan_cairan_hemodialisa=false,skrining_kesehatan_gigi_mulut_lansia=false,skrining_indra_pendengaran=false,
            catatan_pengkajian_paska_operasi=false,skrining_frailty_syndrome=false,sirkulasi_cssd=false,lama_pelayanan_cssd=false,catatan_observasi_bayi=false,
            riwayat_surat_peringatan=false,master_kesimpulan_anjuran_mcu=false,kategori_piutang_jasa_perusahaan=false,piutang_jasa_perusahaan=false,bayar_piutang_jasa_perusahaan=false,
            piutang_jasa_perusahaan_belum_lunas=false,checklist_kesiapan_anestesi=false,piutang_peminjaman_uang_belum_lunas=false,hasil_pemeriksaan_slit_lamp=false,
            hasil_pemeriksaan_oct=false,beban_hutang_lain=false,poli_asal_pasien_ranap=false,pemberi_hutang_lain=false,dokter_asal_pasien_ranap=false,duta_parkir_rekap_keluar=false,
            surat_keterangan_layak_terbang=false,bayar_beban_hutang_lain=false,surat_persetujuan_pemeriksaan_hiv=false,skrining_instrumen_acrs=false,surat_pernyataan_memilih_dpjp=false;
    
    public static void setCopyData(String datanya) {
        akses01.CopyData = datanya;
    }
    
    public static void setCopyData1(String datanya1) {
        akses01.CopyData1 = datanya1;
    }
    
    public static void setCopyData2(String datanya2) {
        akses01.CopyData2 = datanya2;
    }
    
    public static void setCopyData3(String datanya3) {
        akses01.CopyData3 = datanya3;
    }
    
    public static void setCopyData4(String datanya4) {
        akses01.CopyData4 = datanya4;
    }
    
    public static void setCopyData5(String datanya5) {
        akses01.CopyData5 = datanya5;
    }
    
    public static void setData(String user, String pass) {
        try {
                // --- BCrypt auth via tabel users/admins ---
                boolean bcryptAdmin = PasswordSecurity.authenticateAdmin(koneksi, user, pass);
                boolean bcryptUser = PasswordSecurity.authenticateUser(koneksi, user, pass);

                ps=koneksi.prepareStatement("select * from admins where username=?");
                ps2=koneksi.prepareStatement("select * from users where username=?");
                try {
                    ps.setString(1,user);
                    rs=ps.executeQuery();
                    rs.last();

                    ps2.setString(1,user);
                    rs2=ps2.executeQuery();
                    rs2.last();

                    akses01.jml1=bcryptAdmin ? 1 : 0;
                    akses01.jml2=bcryptUser ? 1 : 0;

                    if(bcryptAdmin){
                        akses01.kode="Admin Utama";
                        akses01.penyakit=true;
                        akses01.obat_penyakit=true;
                        akses01.dokter=true;
                        akses01.jadwal_praktek=true;
                        akses01.petugas=true;
                        akses01.pasien=true;
                        akses01.registrasi=true;
                        akses01.tindakan_ralan=true;
                        akses01.kamar_inap=true;
                        akses01.tindakan_ranap=true;
                        akses01.operasi=true;
                        akses01.rujukan_keluar=true;
                        akses01.rujukan_masuk=true;
                        akses01.beri_obat=true;
                        akses01.resep_pulang=true;
                        akses01.pasien_meninggal=true;
                        akses01.diet_pasien=true;
                        akses01.kelahiran_bayi=true;
                        akses01.periksa_lab=true;
                        akses01.periksa_radiologi=true;
                        akses01.kasir_ralan=true;
                        akses01.deposit_pasien=true;
                        akses01.piutang_pasien=true;
                        akses01.peminjaman_berkas=true;
                        akses01.barcode=true;
                        akses01.presensi_harian=true;
                        akses01.presensi_bulanan=true;
                        akses01.pegawai_admin=true;
                        akses01.pegawai_user=true;
                        akses01.suplier=true;
                        akses01.satuan_barang=true;
                        akses01.konversi_satuan=true;
                        akses01.jenis_barang=true;
                        akses01.obat=true;
                        akses01.stok_opname_obat=true;
                        akses01.stok_obat_pasien=true;
                        akses01.pengadaan_obat=true;
                        akses01.pemesanan_obat=true;
                        akses01.penjualan_obat=true;
                        akses01.piutang_obat=true;
                        akses01.retur_ke_suplier=true;
                        akses01.retur_dari_pembeli=true;
                        akses01.retur_obat_ranap=true;
                        akses01.retur_piutang_pasien=true;
                        akses01.keuntungan_penjualan=true;
                        akses01.keuntungan_beri_obat=true;
                        akses01.sirkulasi_obat=true;
                        akses01.ipsrs_barang=true;
                        akses01.ipsrs_pengadaan_barang=true;
                        akses01.ipsrs_stok_keluar=true;
                        akses01.ipsrs_rekap_pengadaan=true;
                        akses01.ipsrs_rekap_stok_keluar=true;
                        akses01.ipsrs_pengeluaran_harian=true;
                        akses01.ipsrs_jenis_barang=true;
                        akses01.inventaris_jenis=true;
                        akses01.inventaris_kategori=true;
                        akses01.inventaris_merk=true;
                        akses01.inventaris_ruang=true;
                        akses01.inventaris_produsen=true;
                        akses01.inventaris_koleksi=true;
                        akses01.inventaris_inventaris=true;
                        akses01.inventaris_sirkulasi=true;
                        akses01.parkir_jenis=true;
                        akses01.parkir_in=true;
                        akses01.parkir_out=true;
                        akses01.parkir_rekap_harian=true;
                        akses01.parkir_rekap_bulanan=true;
                        akses01.informasi_kamar=true;
                        akses01.harian_tindakan_poli=true;
                        akses01.obat_per_poli=true;
                        akses01.obat_per_kamar=true;
                        akses01.obat_per_dokter_ralan=true;
                        akses01.obat_per_dokter_ranap=true;
                        akses01.harian_dokter=true;
                        akses01.bulanan_dokter=true;
                        akses01.harian_paramedis=true;
                        akses01.bulanan_paramedis=true;
                        akses01.pembayaran_ralan=true;
                        akses01.pembayaran_ranap=true;
                        akses01.rekap_pembayaran_ralan=true;
                        akses01.rekap_pembayaran_ranap=true;
                        akses01.tagihan_masuk=true;
                        akses01.tambahan_biaya=true;
                        akses01.potongan_biaya=true;
                        akses01.resep_obat=true;
                        akses01.resume_pasien=true;
                        akses01.penyakit_ralan=true;
                        akses01.penyakit_ranap=true;
                        akses01.kamar=true;
                        akses01.tarif_ralan=true;
                        akses01.tarif_ranap=true;
                        akses01.tarif_lab=true;
                        akses01.tarif_radiologi=true;
                        akses01.tarif_operasi=true;
                        akses01.akun_rekening=true;
                        akses01.rekening_tahun=true;
                        akses01.posting_jurnal=true;
                        akses01.buku_besar=true;
                        akses01.cashflow=true;
                        akses01.keuangan=true;
                        akses01.pengeluaran=true;
                        akses01.setup_pjlab=true;
                        akses01.setup_otolokasi=true;
                        akses01.setup_jam_kamin=true;
                        akses01.setup_embalase=true;
                        akses01.tracer_login=true;
                        akses01.display=true;
                        akses01.set_harga_obat=true;
                        akses01.set_penggunaan_tarif=true;
                        akses01.set_oto_ralan=true;
                        akses01.biaya_harian=true;
                        akses01.biaya_masuk_sekali=true;
                        akses01.set_no_rm=true;
                        akses01.billing_ralan=true;
                        akses01.billing_ranap=true;
                        akses01.jm_ranap_dokter=true;
                        akses01.igd=true;                    
                        akses01.barcoderalan=true;
                        akses01.barcoderanap=true;
                        akses01.set_harga_obat_ralan=true;
                        akses01.set_harga_obat_ranap=true;
                        akses01.penyakit_pd3i=true;
                        akses01.surveilans_pd3i=true;
                        akses01.surveilans_ralan=true;
                        akses01.diagnosa_pasien=true;
                        akses01.admin=true;
                        akses01.user=true;
                        akses01.vakum=true;
                        akses01.aplikasi=true;
                        akses01.surveilans_ranap=true;
                        akses01.pny_takmenular_ranap=true;
                        akses01.pny_takmenular_ralan=true;
                        akses01.kunjungan_ralan=true;
                        akses01.rl32=true;
                        akses01.rl33=true;
                        akses01.rl37=true;
                        akses01.rl38=true;
                        akses01.harian_tindakan_dokter=true;
                        akses01.sms=true;
                        akses01.sidikjari=true;
                        akses01.jam_masuk=true;
                        akses01.jadwal_pegawai=true;                    
                        akses01.parkir_barcode=true;
                        akses01.set_nota=true;
                        akses01.dpjp_ranap=true;
                        akses01.mutasi_barang=true;
                        akses01.rl34=true;
                        akses01.rl36=true;
                        akses01.fee_visit_dokter=true;
                        akses01.fee_bacaan_ekg=true;
                        akses01.fee_rujukan_rontgen=true;
                        akses01.fee_rujukan_ranap=true;
                        akses01.fee_ralan=true;
                        akses01.akun_bayar=true;
                        akses01.bayar_pemesanan_obat=true;
                        akses01.obat_per_dokter_peresep=true;
                        akses01.pemasukan_lain=true;
                        akses01.pengaturan_rekening=true;
                        akses01.closing_kasir=true;
                        akses01.keterlambatan_presensi=true; 
                        akses01.set_harga_kamar=true;
                        akses01.rekap_per_shift=true;
                        akses01.bpjs_cek_nik=true;
                        akses01.bpjs_cek_kartu=true;
                        akses01.bpjs_cek_riwayat=true;
                        akses01.obat_per_cara_bayar=true;
                        akses01.kunjungan_ranap=true;
                        akses01.bayar_piutang=true;
                        akses01.payment_point=true;
                        akses01.bpjs_cek_nomor_rujukan=true;
                        akses01.icd9=true;
                        akses01.darurat_stok=true;
                        akses01.retensi_rm=true;
                        akses01.temporary_presensi=true;
                        akses01.jurnal_harian=true;
                        akses01.sirkulasi_obat2=true;
                        akses01.edit_registrasi=true;
                        akses01.bpjs_referensi_diagnosa=true;
                        akses01.bpjs_referensi_poli=true;
                        akses01.industrifarmasi=true;
                        akses01.harian_js=true;
                        akses01.bulanan_js=true;
                        akses01.harian_paket_bhp=true;
                        akses01.bulanan_paket_bhp=true;
                        akses01.piutang_pasien2=true;
                        akses01.bpjs_referensi_faskes=true;
                        akses01.bpjs_sep=true;
                        akses01.pengambilan_utd=true;
                        akses01.tarif_utd=true;
                        akses01.pengambilan_utd2=true;
                        akses01.utd_medis_rusak=true;
                        akses01.pengambilan_penunjang_utd=true;
                        akses01.pengambilan_penunjang_utd2=true;
                        akses01.utd_penunjang_rusak=true;
                        akses01.suplier_penunjang=true;
                        akses01.utd_donor=true;
                        akses01.bpjs_monitoring_klaim=true;
                        akses01.utd_cekal_darah=true;
                        akses01.utd_komponen_darah=true;
                        akses01.utd_stok_darah=true;
                        akses01.utd_pemisahan_darah=true;
                        akses01.harian_kamar=true;
                        akses01.rincian_piutang_pasien=true;
                        akses01.keuntungan_beri_obat_nonpiutang=true;
                        akses01.reklasifikasi_ralan=true;
                        akses01.reklasifikasi_ranap=true;
                        akses01.utd_penyerahan_darah=true;
                        akses01.hutang_obat=true;
                        akses01.riwayat_obat_alkes_bhp=true;
                        akses01.sensus_harian_poli=true;
                        akses01.rl4a=true;
                        akses01.aplicare_referensi_kamar=true;
                        akses01.aplicare_ketersediaan_kamar=true;
                        akses01.inacbg_klaim_baru_otomatis=true;
                        akses01.inacbg_klaim_baru_manual=true;
                        akses01.inacbg_coder_nik=true;
                        akses01.mutasi_berkas=true;
                        akses01.akun_piutang=true;
                        akses01.harian_kso=true;
                        akses01.bulanan_kso=true;
                        akses01.harian_menejemen=true;
                        akses01.bulanan_menejemen=true;
                        akses01.inhealth_cek_eligibilitas=true;
                        akses01.inhealth_referensi_jenpel_ruang_rawat=true;
                        akses01.inhealth_referensi_poli=true;
                        akses01.inhealth_referensi_faskes=true;
                        akses01.inhealth_sjp=true;
                        akses01.piutang_ralan=true;
                        akses01.piutang_ranap=true;
                        akses01.detail_piutang_penjab=true;
                        akses01.lama_pelayanan_ralan=true;
                        akses01.catatan_pasien=true;
                        akses01.rl4b=true;
                        akses01.rl4asebab=true;
                        akses01.rl4bsebab=true;
                        akses01.data_HAIs=true;
                        akses01.harian_HAIs=true;
                        akses01.bulanan_HAIs=true;
                        akses01.hitung_bor=true;
                        akses01.perusahaan_pasien=true;
                        akses01.resep_dokter=true;
                        akses01.lama_pelayanan_apotek=true;
                        akses01.hitung_alos=true;
                        akses01.detail_tindakan=true;
                        akses01.rujukan_poli_internal=true;
                        akses01.rekap_poli_anak=true;
                        akses01.grafik_kunjungan_poli=true;
                        akses01.grafik_kunjungan_perdokter=true;
                        akses01.grafik_kunjungan_perpekerjaan=true;
                        akses01.grafik_kunjungan_perpendidikan=true;
                        akses01.grafik_kunjungan_pertahun=true;
                        akses01.berkas_digital_perawatan=true;
                        akses01.penyakit_menular_ranap=true;
                        akses01.penyakit_menular_ralan=true;
                        akses01.grafik_kunjungan_perbulan=true;
                        akses01.grafik_kunjungan_pertanggal=true;
                        akses01.grafik_kunjungan_demografi=true;
                        akses01.grafik_kunjungan_statusdaftartahun=true;
                        akses01.grafik_kunjungan_statusdaftartahun2=true;
                        akses01.grafik_kunjungan_statusdaftarbulan=true;
                        akses01.grafik_kunjungan_statusdaftarbulan2=true;
                        akses01.grafik_kunjungan_statusdaftartanggal=true;
                        akses01.grafik_kunjungan_statusdaftartanggal2=true;
                        akses01.grafik_kunjungan_statusbataltahun=true;
                        akses01.grafik_kunjungan_statusbatalbulan=true;
                        akses01.pcare_cek_penyakit=true;
                        akses01.grafik_kunjungan_statusbataltanggal=true;
                        akses01.kategori_barang=true;
                        akses01.golongan_barang=true;
                        akses01.pemberian_obat_pertanggal=true;                        
                        akses01.penjualan_obat_pertanggal=true;
                        akses01.pcare_cek_kesadaran=true;
                        akses01.pembatalan_periksa_dokter=true;
                        akses01.pembayaran_per_unit=true;
                        akses01.rekap_pembayaran_per_unit=true;
                        akses01.grafik_kunjungan_percarabayar=true;
                        akses01.ipsrs_pengadaan_pertanggal=true;
                        akses01.ipsrs_stokkeluar_pertanggal=true;
                        akses01.grafik_kunjungan_ranaptahun=true;
                        akses01.pcare_cek_rujukan=true;
                        akses01.grafik_lab_ralantahun=true;
                        akses01.grafik_rad_ralantahun=true;;
                        akses01.cek_entry_ralan=true;
                        akses01.inacbg_klaim_baru_manual2=true;
                        akses01.permintaan_medis=true;
                        akses01.rekap_permintaan_medis=true;
                        akses01.surat_pemesanan_medis=true;
                        akses01.permintaan_non_medis=true;
                        akses01.rekap_permintaan_non_medis=true;
                        akses01.surat_pemesanan_non_medis=true;
                        akses01.grafik_per_perujuk=true;
                        akses01.bpjs_cek_prosedur=true;
                        akses01.bpjs_cek_kelas_rawat=true;
                        akses01.bpjs_cek_dokter=true;
                        akses01.bpjs_cek_spesialistik=true;
                        akses01.bpjs_cek_ruangrawat=true;
                        akses01.bpjs_cek_carakeluar=true;
                        akses01.bpjs_cek_pasca_pulang=true;
                        akses01.detail_tindakan_okvk=true;
                        akses01.billing_parsial=true;
                        akses01.bpjs_cek_nomor_rujukan_rs=true;
                        akses01.bpjs_cek_rujukan_kartu_pcare=true;
                        akses01.bpjs_cek_rujukan_kartu_rs=true;
                        akses01.akses_depo_obat=true;
                        akses01.bpjs_rujukan_keluar=true;
                        akses01.grafik_lab_ralanbulan=true;
                        akses01.pengeluaran_stok_apotek=true;
                        akses01.grafik_rad_ralanbulan=true;
                        akses01.detailjmdokter2=true;
                        akses01.pengaduan_pasien=true;
                        akses01.grafik_lab_ralanhari=true;
                        akses01.grafik_rad_ralanhari=true;
                        akses01.sensus_harian_ralan=true;
                        akses01.metode_racik=true;
                        akses01.pembayaran_akun_bayar=true;
                        akses01.pengguna_obat_resep=true;
                        akses01.rekap_pemesanan=true;
                        akses01.master_berkas_pegawai=true;
                        akses01.berkas_kepegawaian=true;
                        akses01.riwayat_jabatan=true;
                        akses01.riwayat_pendidikan=true;
                        akses01.riwayat_naik_gaji=true;
                        akses01.kegiatan_ilmiah=true;
                        akses01.riwayat_penghargaan=true;
                        akses01.riwayat_penelitian=true;
                        akses01.penerimaan_non_medis=true;
                        akses01.bayar_pesan_non_medis=true;
                        akses01.hutang_barang_non_medis=true;
                        akses01.rekap_pemesanan_non_medis=true;
                        akses01.insiden_keselamatan=true;
                        akses01.insiden_keselamatan_pasien=true;
                        akses01.grafik_ikp_pertahun=true;
                        akses01.grafik_ikp_perbulan=true;
                        akses01.grafik_ikp_pertanggal=true;
                        akses01.riwayat_data_batch=true;
                        akses01.grafik_ikp_jenis=true;
                        akses01.grafik_ikp_dampak=true;
                        akses01.piutang_akun_piutang=true;
                        akses01.grafik_kunjungan_per_agama=true;
                        akses01.grafik_kunjungan_per_umur=true;
                        akses01.suku_bangsa=true;
                        akses01.bahasa_pasien=true;
                        akses01.golongan_tni=true;
                        akses01.satuan_tni=true;
                        akses01.jabatan_tni=true;
                        akses01.pangkat_tni=true;
                        akses01.golongan_polri=true;
                        akses01.satuan_polri=true;
                        akses01.jabatan_polri=true;
                        akses01.pangkat_polri=true;
                        akses01.cacat_fisik=true;
                        akses01.grafik_kunjungan_suku=true;
                        akses01.grafik_kunjungan_bahasa=true;
                        akses01.booking_operasi=true;
                        akses01.mapping_poli_bpjs=true;
                        akses01.grafik_kunjungan_per_cacat=true;
                        akses01.barang_cssd=true;
                        akses01.skdp_bpjs=true;
                        akses01.booking_registrasi=true;
                        akses01.bpjs_cek_propinsi=true;
                        akses01.bpjs_cek_kabupaten=true;
                        akses01.bpjs_cek_kecamatan=true;
                        akses01.bpjs_cek_dokterdpjp=true;
                        akses01.bpjs_cek_riwayat_rujukanrs=true;
                        akses01.bpjs_cek_tanggal_rujukan=true;
                        akses01.permintaan_lab=true;
                        akses01.permintaan_radiologi=true;
                        akses01.surat_indeks=true;
                        akses01.surat_map=true;
                        akses01.surat_almari=true;
                        akses01.surat_rak=true;
                        akses01.surat_ruang=true;
                        akses01.surat_klasifikasi=true;
                        akses01.surat_status=true; 
                        akses01.surat_sifat=true;
                        akses01.surat_balas=true;
                        akses01.surat_masuk=true;
                        akses01.pcare_cek_dokter=true;
                        akses01.pcare_cek_poli=true;
                        akses01.pcare_cek_provider=true;
                        akses01.pcare_cek_statuspulang=true;
                        akses01.pcare_cek_spesialis=true;
                        akses01.pcare_cek_subspesialis=true;
                        akses01.pcare_cek_sarana=true;
                        akses01.pcare_cek_khusus=true;
                        akses01.pcare_cek_obat=true;
                        akses01.pcare_cek_tindakan=true;
                        akses01.pcare_cek_faskessubspesialis=true;
                        akses01.pcare_cek_faskesalihrawat=true;
                        akses01.pcare_cek_faskesthalasemia=true;
                        akses01.pcare_mapping_obat=true;
                        akses01.pcare_mapping_tindakan=true;
                        akses01.pcare_club_prolanis=true;
                        akses01.pcare_mapping_poli=true;
                        akses01.pcare_kegiatan_kelompok=true;
                        akses01.pcare_mapping_tindakan_ranap=true;
                        akses01.pcare_peserta_kegiatan_kelompok=true;
                        akses01.sirkulasi_obat3=true;
                        akses01.bridging_pcare_daftar=true;
                        akses01.pcare_mapping_dokter=true;
                        akses01.ranap_per_ruang=true;
                        akses01.penyakit_ranap_cara_bayar=true;
                        akses01.anggota_militer_dirawat=true;
                        akses01.set_input_parsial=true;
                        akses01.lama_pelayanan_radiologi=true;
                        akses01.lama_pelayanan_lab=true;
                        akses01.bpjs_cek_sep=true;
                        akses01.catatan_perawatan=true;
                        akses01.surat_keluar=true;
                        akses01.kegiatan_farmasi=true;
                        akses01.stok_opname_logistik=true;
                        akses01.sirkulasi_non_medis=true;
                        akses01.rekap_lab_pertahun=true;
                        akses01.perujuk_lab_pertahun=true;
                        akses01.rekap_radiologi_pertahun=true;
                        akses01.perujuk_radiologi_pertahun=true;
                        akses01.jumlah_porsi_diet=true;
                        akses01.jumlah_macam_diet=true;
                        akses01.payment_point2=true;
                        akses01.pembayaran_akun_bayar2=true;
                        akses01.hapus_nota_salah=true;
                        akses01.hais_perbangsal=true;
                        akses01.ppn_obat=true;
                        akses01.saldo_akun_perbulan=true;
                        akses01.display_apotek=true;
                        akses01.sisrute_referensi_faskes=true;
                        akses01.sisrute_referensi_alasanrujuk=true;
                        akses01.sisrute_referensi_diagnosa=true;
                        akses01.sisrute_rujukan_masuk=true;
                        akses01.sisrute_rujukan_keluar=true;
                        akses01.bpjs_cek_skdp=true;
                        akses01.data_batch=true;
                        akses01.kunjungan_permintaan_lab=true;
                        akses01.kunjungan_permintaan_lab2=true;
                        akses01.kunjungan_permintaan_radiologi=true;
                        akses01.kunjungan_permintaan_radiologi2=true;
                        akses01.pcare_pemberian_obat=true;
                        akses01.pcare_pemberian_tindakan=true;
                        akses01.pembayaran_akun_bayar3=true;
                        akses01.password_asuransi=true;
                        akses01.kemenkes_sitt=true;
                        akses01.siranap_ketersediaan_kamar=true;
                        akses01.grafik_tb_periodelaporan=true;
                        akses01.grafik_tb_rujukan=true;
                        akses01.grafik_tb_riwayat=true;
                        akses01.grafik_tb_tipediagnosis=true;
                        akses01.grafik_tb_statushiv=true;
                        akses01.grafik_tb_skoringanak=true;
                        akses01.grafik_tb_konfirmasiskoring5=true;
                        akses01.grafik_tb_konfirmasiskoring6=true;
                        akses01.grafik_tb_sumberobat=true;
                        akses01.grafik_tb_hasilakhirpengobatan=true;
                        akses01.grafik_tb_hasilteshiv=true;
                        akses01.kadaluarsa_batch=true;
                        akses01.sisa_stok=true;
                        akses01.obat_per_resep=true;
                        akses01.pemakaian_air_pdam=true;
                        akses01.limbah_b3_medis=true;
                        akses01.grafik_air_pdam_pertanggal=true;
                        akses01.grafik_air_pdam_perbulan=true;
                        akses01.grafik_limbahb3_pertanggal=true;
                        akses01.grafik_limbahb3_perbulan=true;
                        akses01.limbah_domestik=true;
                        akses01.grafik_limbahdomestik_pertanggal=true;
                        akses01.grafik_limbahdomestik_perbulan=true;
                        akses01.mutu_air_limbah=true;
                        akses01.pest_control=true;
                        akses01.ruang_perpustakaan=true;
                        akses01.kategori_perpustakaan=true;
                        akses01.jenis_perpustakaan=true;
                        akses01.pengarang_perpustakaan=true;
                        akses01.penerbit_perpustakaan=true;
                        akses01.koleksi_perpustakaan=true;
                        akses01.inventaris_perpustakaan=true;
                        akses01.set_peminjaman_perpustakaan=true;
                        akses01.denda_perpustakaan=true;
                        akses01.anggota_perpustakaan=true;
                        akses01.peminjaman_perpustakaan=true;
                        akses01.bayar_denda_perpustakaan=true;
                        akses01.ebook_perpustakaan=true;
                        akses01.jenis_cidera_k3rs=true;
                        akses01.penyebab_k3rs=true;
                        akses01.jenis_luka_k3rs=true;
                        akses01.lokasi_kejadian_k3rs=true;
                        akses01.dampak_cidera_k3rs=true;
                        akses01.jenis_pekerjaan_k3rs=true;
                        akses01.bagian_tubuh_k3rs=true;
                        akses01.peristiwa_k3rs=true;
                        akses01.grafik_k3_pertahun=true;
                        akses01.grafik_k3_perbulan=true;
                        akses01.grafik_k3_pertanggal=true;
                        akses01.grafik_k3_perjeniscidera=true;
                        akses01.grafik_k3_perpenyebab=true;
                        akses01.grafik_k3_perjenisluka=true;
                        akses01.grafik_k3_lokasikejadian=true;
                        akses01.grafik_k3_dampakcidera=true;
                        akses01.grafik_k3_perjenispekerjaan=true;
                        akses01.grafik_k3_perbagiantubuh=true;
                        akses01.jenis_cidera_k3rstahun=true;
                        akses01.penyebab_k3rstahun=true;
                        akses01.jenis_luka_k3rstahun=true;
                        akses01.lokasi_kejadian_k3rstahun=true;
                        akses01.dampak_cidera_k3rstahun=true;
                        akses01.jenis_pekerjaan_k3rstahun=true;
                        akses01.bagian_tubuh_k3rstahun=true;
                        akses01.sekrining_rawat_jalan=true;
                        akses01.bpjs_histori_pelayanan=true;
                        akses01.rekap_mutasi_berkas=true;
                        akses01.skrining_ralan_pernapasan_pertahun=true;
                        akses01.pengajuan_barang_medis=true;
                        akses01.pengajuan_barang_nonmedis=true;
                        akses01.grafik_kunjungan_ranapbulan=true;
                        akses01.grafik_kunjungan_ranaptanggal=true;
                        akses01.grafik_kunjungan_ranap_peruang=true;
                        akses01.kunjungan_bangsal_pertahun=true;
                        akses01.grafik_jenjang_jabatanpegawai=true;
                        akses01.grafik_bidangpegawai=true;
                        akses01.grafik_departemenpegawai=true;
                        akses01.grafik_pendidikanpegawai=true;
                        akses01.grafik_sttswppegawai=true;
                        akses01.grafik_sttskerjapegawai=true;
                        akses01.grafik_sttspulangranap=true;
                        akses01.kip_pasien_ranap=true;
                        akses01.kip_pasien_ralan=true;
                        akses01.bpjs_mapping_dokterdpjp=true;
                        akses01.data_triase_igd=true;
                        akses01.master_triase_skala1=true;
                        akses01.master_triase_skala2=true;
                        akses01.master_triase_skala3=true;
                        akses01.master_triase_skala4=true;
                        akses01.master_triase_skala5=true;
                        akses01.master_triase_pemeriksaan=true;
                        akses01.master_triase_macamkasus=true;
                        akses01.rekap_permintaan_diet=true;
                        akses01.daftar_pasien_ranap=true;
                        akses01.daftar_pasien_ranaptni=true;
                        akses01.pengajuan_asetinventaris=true;
                        akses01.item_apotek_jenis=true;
                        akses01.item_apotek_kategori=true;
                        akses01.item_apotek_golongan=true;
                        akses01.item_apotek_industrifarmasi=true;
                        akses01.obat10_terbanyak_poli=true;
                        akses01.grafik_pengajuan_aset_urgensi=true;
                        akses01.grafik_pengajuan_aset_status=true;
                        akses01.grafik_pengajuan_aset_departemen=true;
                        akses01.rekap_pengajuan_aset_departemen=true;
                        akses01.grafik_kelompok_jabatanpegawai=true;
                        akses01.grafik_resiko_kerjapegawai=true;
                        akses01.grafik_emergency_indexpegawai=true;
                        akses01.grafik_inventaris_ruang=true;
                        akses01.harian_HAIs2=true;
                        akses01.grafik_inventaris_jenis=true;
                        akses01.data_resume_pasien=true;
                        akses01.perkiraan_biaya_ranap=true;
                        akses01.rekap_obat_poli=true;
                        akses01.rekap_obat_pasien=true;
                        akses01.grafik_HAIs_pasienbangsal=true;
                        akses01.grafik_HAIs_pasienbulan=true;
                        akses01.permintaan_perbaikan_inventaris=true;
                        akses01.grafik_HAIs_laju_vap=true;
                        akses01.grafik_HAIs_laju_iad=true;
                        akses01.grafik_HAIs_laju_pleb=true;
                        akses01.grafik_HAIs_laju_isk=true;
                        akses01.grafik_HAIs_laju_ilo=true;
                        akses01.grafik_HAIs_laju_hap=true;
                        akses01.inhealth_mapping_poli=true;
                        akses01.inhealth_mapping_dokter=true;
                        akses01.inhealth_mapping_tindakan_ralan=true;
                        akses01.inhealth_mapping_tindakan_ranap=true;
                        akses01.inhealth_mapping_tindakan_radiologi=true;
                        akses01.inhealth_mapping_tindakan_laborat=true;
                        akses01.inhealth_mapping_tindakan_operasi=true;
                        akses01.hibah_obat_bhp=true;
                        akses01.asal_hibah=true;
                        akses01.asuhan_gizi=true;
                        akses01.inhealth_kirim_tagihan=true;
                        akses01.sirkulasi_obat4=true;
                        akses01.sirkulasi_obat5=true;
                        akses01.sirkulasi_non_medis2=true;
                        akses01.monitoring_asuhan_gizi=true;
                        akses01.penerimaan_obat_perbulan=true;
                        akses01.rekap_kunjungan=true;
                        akses01.surat_sakit=true;
                        akses01.penilaian_awal_keperawatan_ralan=true;
                        akses01.permintaan_diet=true;
                        akses01.master_masalah_keperawatan=true;
                        akses01.pengajuan_cuti=true;
                        akses01.kedatangan_pasien=true;
                        akses01.utd_pendonor=true;
                        akses01.toko_suplier=true;
                        akses01.toko_jenis=true;
                        akses01.toko_set_harga=true;
                        akses01.toko_barang=true;
                        akses01.penagihan_piutang_pasien=true;
                        akses01.akun_penagihan_piutang=true;
                        akses01.stok_opname_toko=true;
                        akses01.toko_riwayat_barang=true;
                        akses01.toko_surat_pemesanan=true;
                        akses01.toko_pengajuan_barang=true;
                        akses01.toko_penerimaan_barang=true;
                        akses01.toko_pengadaan_barang=true;
                        akses01.toko_hutang=true;
                        akses01.toko_bayar_pemesanan=true;
                        akses01.toko_member=true;
                        akses01.toko_penjualan=true;
                        akses01.registrasi_poli_per_tanggal=true;
                        akses01.toko_piutang=true;
                        akses01.toko_retur_beli=true;
                        akses01.ipsrs_returbeli=true;
                        akses01.ipsrs_riwayat_barang=true;
                        akses01.pasien_corona=true;
                        akses01.toko_pendapatan_harian=true;
                        akses01.diagnosa_pasien_corona=true;
                        akses01.perawatan_pasien_corona=true;
                        akses01.penilaian_awal_keperawatan_gigi=true;
                        akses01.master_masalah_keperawatan_gigi=true;
                        akses01.toko_bayar_piutang=true;
                        akses01.toko_piutang_harian=true;
                        akses01.toko_penjualan_harian=true;
                        akses01.deteksi_corona=true;
                        akses01.penilaian_awal_keperawatan_kebidanan=true;
                        akses01.pengumuman_epasien=true;
                        akses01.surat_hamil=true;
                        akses01.set_tarif_online=true;
                        akses01.booking_periksa=true;
                        akses01.toko_sirkulasi=true;
                        akses01.toko_retur_jual=true;
                        akses01.toko_retur_piutang=true;
                        akses01.toko_sirkulasi2=true;
                        akses01.toko_keuntungan_barang=true;
                        akses01.zis_pengeluaran_penerima_dankes=true;
                        akses01.zis_penghasilan_penerima_dankes=true;
                        akses01.zis_ukuran_rumah_penerima_dankes=true;
                        akses01.zis_dinding_rumah_penerima_dankes=true;
                        akses01.zis_lantai_rumah_penerima_dankes=true;
                        akses01.zis_atap_rumah_penerima_dankes=true;
                        akses01.zis_kepemilikan_rumah_penerima_dankes=true;
                        akses01.zis_kamar_mandi_penerima_dankes=true;
                        akses01.zis_dapur_rumah_penerima_dankes=true;
                        akses01.zis_kursi_rumah_penerima_dankes=true;
                        akses01.zis_kategori_phbs_penerima_dankes=true;
                        akses01.zis_elektronik_penerima_dankes=true;
                        akses01.zis_ternak_penerima_dankes=true;
                        akses01.zis_jenis_simpanan_penerima_dankes=true;
                        akses01.penilaian_awal_keperawatan_anak=true;
                        akses01.zis_kategori_asnaf_penerima_dankes=true;
                        akses01.master_masalah_keperawatan_anak=true;
                        akses01.master_imunisasi=true;
                        akses01.zis_patologis_penerima_dankes=true;
                        akses01.pcare_cek_kartu=true;
                        akses01.surat_bebas_narkoba=true;
                        akses01.surat_keterangan_covid=true;
                        akses01.pemakaian_air_tanah=true;
                        akses01.grafik_air_tanah_pertanggal=true;
                        akses01.grafik_air_tanah_perbulan=true;
                        akses01.lama_pelayanan_poli=true;
                        akses01.hemodialisa=true;
                        akses01.grafik_harian_hemodialisa=true;
                        akses01.grafik_bulanan_hemodialisa=true;
                        akses01.grafik_tahunan_hemodialisa=true;
                        akses01.grafik_bulanan_meninggal=true;
                        akses01.laporan_tahunan_irj=true;
                        akses01.perbaikan_inventaris=true;
                        akses01.surat_cuti_hamil=true;
                        akses01.permintaan_stok_obat_pasien=true;
                        akses01.pemeliharaan_inventaris=true;
                        akses01.klasifikasi_pasien_ranap=true;
                        akses01.bulanan_klasifikasi_pasien_ranap=true;
                        akses01.harian_klasifikasi_pasien_ranap=true;
                        akses01.klasifikasi_pasien_perbangsal=true;
                        akses01.soap_perawatan=true;
                        akses01.klaim_rawat_jalan=true;
                        akses01.skrining_gizi=true;
                        akses01.lama_penyiapan_rm=true;
                        akses01.dosis_radiologi=true;
                        akses01.demografi_umur_kunjungan=true;
                        akses01.jam_diet_pasien=true;
                        akses01.rvu_bpjs=true;
                        akses01.verifikasi_penerimaan_farmasi=true;
                        akses01.verifikasi_penerimaan_logistik=true;
                        akses01.pemeriksaan_lab_pa=true;
                        akses01.ringkasan_pengajuan_obat=true;
                        akses01.ringkasan_pemesanan_obat=true;
                        akses01.ringkasan_pengadaan_obat=true;
                        akses01.ringkasan_penerimaan_obat=true;
                        akses01.ringkasan_hibah_obat=true;
                        akses01.ringkasan_penjualan_obat=true;
                        akses01.ringkasan_beri_obat=true;
                        akses01.ringkasan_piutang_obat=true;
                        akses01.ringkasan_stok_keluar_obat=true;
                        akses01.ringkasan_retur_suplier_obat=true;
                        akses01.ringkasan_retur_pembeli_obat=true;
                        akses01.penilaian_awal_keperawatan_ranapkebidanan=true;
                        akses01.ringkasan_pengajuan_nonmedis=true;
                        akses01.ringkasan_pemesanan_nonmedis=true;
                        akses01.ringkasan_pengadaan_nonmedis=true;
                        akses01.ringkasan_penerimaan_nonmedis=true;
                        akses01.ringkasan_stokkeluar_nonmedis=true;
                        akses01.ringkasan_returbeli_nonmedis=true;
                        akses01.omset_penerimaan=true;
                        akses01.validasi_penagihan_piutang=true;
                        akses01.permintaan_ranap=true;
                        akses01.bpjs_diagnosa_prb=true;
                        akses01.bpjs_obat_prb=true;
                        akses01.bpjs_surat_kontrol=true;
                        akses01.penggunaan_bhp_ok=true;
                        akses01.surat_keterangan_rawat_inap=true;
                        akses01.surat_keterangan_sehat=true;
                        akses01.pendapatan_per_carabayar=true;
                        akses01.akun_host_to_host_bank_jateng=true;
                        akses01.pembayaran_bank_jateng=true;
                        akses01.bpjs_surat_pri=true;
                        akses01.ringkasan_tindakan=true;
                        akses01.lama_pelayanan_pasien=true;
                        akses01.surat_sakit_pihak_2=true;
                        akses01.tagihan_hutang_obat=true;
                        akses01.referensi_mobilejkn_bpjs=true;
                        akses01.batal_pendaftaran_mobilejkn_bpjs=true;
                        akses01.lama_operasi=true;
                        akses01.grafik_inventaris_kategori=true;
                        akses01.grafik_inventaris_merk=true;
                        akses01.grafik_inventaris_produsen=true;
                        akses01.pengembalian_deposit_pasien=true;
                        akses01.validasi_tagihan_hutang_obat=true;
                        akses01.piutang_obat_belum_lunas=true;
                        akses01.integrasi_briapi=true;
                        akses01.pengadaan_aset_inventaris=true;
                        akses01.akun_aset_inventaris=true;
                        akses01.suplier_inventaris=true;
                        akses01.penerimaan_aset_inventaris=true;
                        akses01.bayar_pemesanan_iventaris=true;
                        akses01.hutang_aset_inventaris=true;
                        akses01.hibah_aset_inventaris=true;
                        akses01.titip_faktur_non_medis=true;
                        akses01.validasi_tagihan_non_medis=true;
                        akses01.titip_faktur_aset=true;
                        akses01.validasi_tagihan_aset=true;
                        akses01.hibah_non_medis=true;
                        akses01.pcare_alasan_tacc=true;
                        akses01.resep_luar=true;
                        akses01.surat_bebas_tbc=true;
                        akses01.surat_buta_warna=true;
                        akses01.surat_bebas_tato=true;
                        akses01.surat_kewaspadaan_kesehatan=true;
                        akses01.grafik_porsidiet_pertanggal=true;
                        akses01.grafik_porsidiet_perbulan=true;
                        akses01.grafik_porsidiet_pertahun=true;
                        akses01.grafik_porsidiet_perbangsal=true;
                        akses01.penilaian_awal_medis_ralan=true;
                        akses01.master_masalah_keperawatan_mata=true;
                        akses01.penilaian_awal_keperawatan_mata=true;
                        akses01.penilaian_awal_medis_ranap=true;
                        akses01.penilaian_awal_medis_ranap_kebidanan=true;
                        akses01.penilaian_awal_medis_ralan_kebidanan=true;
                        akses01.penilaian_awal_medis_igd=true;
                        akses01.penilaian_awal_medis_ralan_anak=true;
                        akses01.bpjs_referensi_poli_hfis=true;
                        akses01.bpjs_referensi_dokter_hfis=true;
                        akses01.bpjs_referensi_jadwal_hfis=true;
                        akses01.penilaian_fisioterapi=true;
                        akses01.bpjs_program_prb=true;
                        akses01.bpjs_suplesi_jasaraharja=true;
                        akses01.bpjs_data_induk_kecelakaan=true;
                        akses01.bpjs_sep_internal=true;
                        akses01.bpjs_klaim_jasa_raharja=true;
                        akses01.bpjs_daftar_finger_print=true;
                        akses01.bpjs_rujukan_khusus=true;
                        akses01.pemeliharaan_gedung=true;
                        akses01.grafik_perbaikan_inventaris_pertanggal=true;
                        akses01.grafik_perbaikan_inventaris_perbulan=true;
                        akses01.grafik_perbaikan_inventaris_pertahun=true;
                        akses01.grafik_perbaikan_inventaris_perpelaksana_status=true;
                        akses01.penilaian_mcu=true;
                        akses01.peminjam_piutang=true;
                        akses01.piutang_lainlain=true;
                        akses01.cara_bayar=true;
                        akses01.audit_kepatuhan_apd=true;
                        akses01.bpjs_task_id=true;
                        akses01.bayar_piutang_lain=true;
                        akses01.pembayaran_akun_bayar4=true;
                        akses01.stok_akhir_farmasi_pertanggal=true;
                        akses01.riwayat_kamar_pasien=true;
                        akses01.uji_fungsi_kfr=true;
                        akses01.hapus_berkas_digital_perawatan=true;
                        akses01.kategori_pengeluaran_harian=true;
                        akses01.kategori_pemasukan_lain=true;
                        akses01.pembayaran_akun_bayar5=true;
                        akses01.ruang_ok=true;
                        akses01.jasa_tindakan_pasien=true;
                        akses01.telaah_resep=true;
                        akses01.permintaan_resep_pulang=true;
                        akses01.rekap_jm_dokter=true;
                        akses01.status_data_rm=true;
                        akses01.ubah_petugas_lab_pk=true;
                        akses01.ubah_petugas_lab_pa=true;
                        akses01.ubah_petugas_radiologi=true;
                        akses01.gabung_norawat=true;
                        akses01.gabung_rm=true;
                        akses01.ringkasan_biaya_obat_pasien_pertanggal=true;
                        akses01.master_masalah_keperawatan_igd=true;
                        akses01.penilaian_awal_keperawatan_igd=true;
                        akses01.bpjs_referensi_dpho_apotek=true;
                        akses01.bpjs_referensi_poli_apotek=true;
                        akses01.bayar_jm_dokter=true;
                        akses01.bpjs_referensi_faskes_apotek=true;
                        akses01.bpjs_referensi_spesialistik_apotek=true;
                        akses01.pembayaran_briva=true;
                        akses01.penilaian_awal_keperawatan_ranap=true;
                        akses01.nilai_penerimaan_vendor_farmasi_perbulan=true;
                        akses01.akun_bayar_hutang=true;
                        akses01.master_rencana_keperawatan=true;
                        akses01.laporan_tahunan_igd=true;
                        akses01.obat_bhp_tidakbergerak=true;
                        akses01.ringkasan_hutang_vendor_farmasi=true;
                        akses01.nilai_penerimaan_vendor_nonmedis_perbulan=true;
                        akses01.ringkasan_hutang_vendor_nonmedis=true;
                        akses01.anggota_polri_dirawat=true;
                        akses01.daftar_pasien_ranap_polri=true;
                        akses01.soap_ralan_polri=true;
                        akses01.soap_ranap_polri=true;
                        akses01.laporan_penyakit_polri=true;
                        akses01.master_rencana_keperawatan_anak=true;
                        akses01.jumlah_pengunjung_ralan_polri=true;
                        akses01.catatan_observasi_igd=true;
                        akses01.catatan_observasi_ranap=true;
                        akses01.catatan_observasi_ranap_kebidanan=true;
                        akses01.catatan_observasi_ranap_postpartum=true;
                        akses01.penilaian_awal_medis_ralan_tht=true;
                        akses01.penilaian_psikologi=true;
                        akses01.audit_cuci_tangan_medis=true;
                        akses01.audit_pembuangan_limbah=true;
                        akses01.ruang_audit_kepatuhan=true;
                        akses01.audit_pembuangan_benda_tajam=true;
                        akses01.audit_penanganan_darah=true;
                        akses01.audit_pengelolaan_linen_kotor=true;
                        akses01.audit_penempatan_pasien=true;
                        akses01.audit_kamar_jenazah=true;
                        akses01.audit_bundle_iadp=true;
                        akses01.audit_bundle_ido=true;
                        akses01.audit_fasilitas_kebersihan_tangan=true;
                        akses01.audit_fasilitas_apd=true;
                        akses01.audit_pembuangan_limbah_cair_infeksius=true;
                        akses01.audit_sterilisasi_alat=true;
                        akses01.penilaian_awal_medis_ralan_psikiatri=true;
                        akses01.persetujuan_penolakan_tindakan=true;
                        akses01.audit_bundle_isk=true;
                        akses01.audit_bundle_plabsi=true;
                        akses01.audit_bundle_vap=true;
                        akses01.akun_host_to_host_bank_papua=true;
                        akses01.pembayaran_bank_papua=true;
                        akses01.penilaian_awal_medis_ralan_penyakit_dalam=true;
                        akses01.penilaian_awal_medis_ralan_mata=true;
                        akses01.penilaian_awal_medis_ralan_neurologi=true;
                        akses01.sirkulasi_obat6=true;
                        akses01.penilaian_awal_medis_ralan_orthopedi=true;
                        akses01.penilaian_awal_medis_ralan_bedah=true;
                        akses01.integrasi_khanza_health_services=true;
                        akses01.soap_ralan_tni=true;
                        akses01.soap_ranap_tni=true;
                        akses01.jumlah_pengunjung_ralan_tni=true;
                        akses01.laporan_penyakit_tni=true;
                        akses01.catatan_keperawatan_ranap=true;
                        akses01.master_rencana_keperawatan_gigi=true;
                        akses01.master_rencana_keperawatan_mata=true;
                        akses01.master_rencana_keperawatan_igd=true;
                        akses01.master_masalah_keperawatan_psikiatri=true;
                        akses01.master_rencana_keperawatan_psikiatri=true;
                        akses01.penilaian_awal_keperawatan_psikiatri=true;
                        akses01.pemantauan_pews_anak=true;
                        akses01.surat_pulang_atas_permintaan_sendiri=true;
                        akses01.template_hasil_radiologi=true;
                        akses01.laporan_bulanan_irj=true;
                        akses01.template_pemeriksaan=true;
                        akses01.pemeriksaan_lab_mb=true;
                        akses01.ubah_petugas_lab_mb=true;
                        akses01.penilaian_pre_operasi=true;
                        akses01.penilaian_pre_anestesi=true;
                        akses01.perencanaan_pemulangan=true;
                        akses01.penilaian_lanjutan_resiko_jatuh_dewasa=true;
                        akses01.penilaian_lanjutan_resiko_jatuh_anak=true;
                        akses01.penilaian_awal_medis_ralan_geriatri=true;
                        akses01.penilaian_tambahan_pasien_geriatri=true;
                        akses01.skrining_nutrisi_dewasa=true;
                        akses01.skrining_nutrisi_lansia=true;
                        akses01.hasil_pemeriksaan_usg=true;
                        akses01.skrining_nutrisi_anak=true;
                        akses01.akun_host_to_host_bank_jabar=true;
                        akses01.pembayaran_bank_jabar=true;
                        akses01.surat_pernyataan_pasien_umum=true;
                        akses01.konseling_farmasi=true;
                        akses01.pelayanan_informasi_obat=true;
                        akses01.jawaban_pio_apoteker=true;
                        akses01.surat_persetujuan_umum=true;
                        akses01.transfer_pasien_antar_ruang=true;
                        akses01.satu_sehat_referensi_dokter=true;
                        akses01.satu_sehat_referensi_pasien=true;
                        akses01.satu_sehat_mapping_departemen=true;
                        akses01.satu_sehat_mapping_lokasi=true;
                        akses01.satu_sehat_kirim_encounter=true;
                        akses01.catatan_cek_gds=true;
                        akses01.satu_sehat_kirim_condition=true;
                        akses01.checklist_pre_operasi=true;
                        akses01.satu_sehat_kirim_observationttv=true;
                        akses01.signin_sebelum_anestesi=true;
                        akses01.satu_sehat_kirim_procedure=true;
                        akses01.operasi_per_bulan=true;
                        akses01.timeout_sebelum_insisi=true;
                        akses01.signout_sebelum_menutup_luka=true;
                        akses01.dapur_barang=true;
                        akses01.dapur_opname=true;
                        akses01.satu_sehat_mapping_vaksin=true;
                        akses01.dapur_suplier=true;
                        akses01.satu_sehat_kirim_Immunization=true;
                        akses01.checklist_post_operasi=true;
                        akses01.dapur_pembelian=true;
                        akses01.dapur_stok_keluar=true;
                        akses01.dapur_riwayat_barang=true;
                        akses01.permintaan_dapur=true;
                        akses01.rekonsiliasi_obat=true;
                        akses01.biaya_pengadaan_dapur=true;
                        akses01.rekap_pengadaan_dapur=true;
                        akses01.kesling_limbah_b3medis_cair=true;
                        akses01.grafik_limbahb3cair_pertanggal=true;
                        akses01.grafik_limbahb3cair_perbulan=true;
                        akses01.rekap_biaya_registrasi=true;
                        akses01.konfirmasi_rekonsiliasi_obat=true;
                        akses01.satu_sehat_kirim_clinicalimpression=true;
                        akses01.penilaian_pasien_terminal=true;
                        akses01.surat_persetujuan_rawat_inap=true;
                        akses01.monitoring_reaksi_tranfusi=true;
                        akses01.penilaian_korban_kekerasan=true;
                        akses01.penilaian_lanjutan_resiko_jatuh_lansia=true;
                        akses01.mpp_skrining=true;
                        akses01.penilaian_pasien_penyakit_menular=true;
                        akses01.edukasi_pasien_keluarga_rj=true;
                        akses01.pemantauan_pews_dewasa=true;
                        akses01.penilaian_tambahan_bunuh_diri=true;
                        akses01.bpjs_antrean_pertanggal=true;
                        akses01.penilaian_tambahan_perilaku_kekerasan=true;
                        akses01.penilaian_tambahan_beresiko_melarikan_diri=true;
                        akses01.persetujuan_penundaan_pelayanan=true;
                        akses01.sisa_diet_pasien=true;
                        akses01.penilaian_awal_medis_ralan_bedah_mulut=true;
                        akses01.penilaian_pasien_keracunan=true;
                        akses01.pemantauan_meows_obstetri=true;
                        akses01.catatan_adime_gizi=true;
                        akses01.pengajuan_biaya=true;
                        akses01.penilaian_awal_keperawatan_ralan_geriatri=true;
                        akses01.master_masalah_keperawatan_geriatri=true;
                        akses01.master_rencana_keperawatan_geriatri=true;
                        akses01.checklist_kriteria_masuk_hcu=true;
                        akses01.checklist_kriteria_keluar_hcu=true;
                        akses01.penilaian_risiko_dekubitus=true;
                        akses01.master_menolak_anjuran_medis=true;
                        akses01.penolakan_anjuran_medis=true;
                        akses01.laporan_tahunan_penolakan_anjuran_medis=true;
                        akses01.template_laporan_operasi=true;
                        akses01.hasil_tindakan_eswl=true;
                        akses01.checklist_kriteria_masuk_icu=true;
                        akses01.checklist_kriteria_keluar_icu=true;
                        akses01.akses_dokter_lain_rawat_jalan=true;
                        akses01.follow_up_dbd=true;
                        akses01.penilaian_risiko_jatuh_neonatus=true;
                        akses01.persetujuan_pengajuan_biaya=true;
                        akses01.pemeriksaan_fisik_ralan_per_penyakit=true;
                        akses01.penilaian_lanjutan_resiko_jatuh_geriatri=true;
                        akses01.pemantauan_ews_neonatus=true;
                        akses01.validasi_persetujuan_pengajuan_biaya=true;
                        akses01.riwayat_perawatan_icare_bpjs=true;
                        akses01.rekap_pengajuan_biaya=true;
                        akses01.penilaian_awal_medis_ralan_kulit_kelamin=true;
                        akses01.akun_host_to_host_bank_mandiri=true;
                        akses01.penilaian_medis_hemodialisa=true;
                        akses01.penilaian_level_kecemasan_ranap_anak=true;
                        akses01.penilaian_lanjutan_resiko_jatuh_psikiatri=true;
                        akses01.penilaian_lanjutan_skrining_fungsional=true;
                        akses01.penilaian_medis_ralan_rehab_medik=true;
                        akses01.laporan_anestesi=true;
                        akses01.template_persetujuan_penolakan_tindakan=true;
                        akses01.penilaian_medis_ralan_gawat_darurat_psikiatri=true;
                        akses01.bpjs_referensi_setting_apotek=true;
                        akses01.bpjs_referensi_obat_apotek=true;
                        akses01.bpjs_mapping_obat_apotek=true;
                        akses01.pembayaran_bank_mandiri=true;
                        akses01.penilaian_ulang_nyeri=true;
                        akses01.penilaian_terapi_wicara=true;
                        akses01.bpjs_obat_23hari_apotek=true;
                        akses01.pengkajian_restrain=true;
                        akses01.bpjs_kunjungan_sep_apotek=true;
                        akses01.bpjs_monitoring_klaim_apotek=true;
                        akses01.bpjs_daftar_pelayanan_obat_apotek=true;
                        akses01.penilaian_awal_medis_ralan_paru=true;
                        akses01.catatan_keperawatan_ralan=true;
                        akses01.catatan_persalinan=true;
                        akses01.skor_aldrette_pasca_anestesi=true;
                        akses01.skor_steward_pasca_anestesi=true;
                        akses01.skor_bromage_pasca_anestesi=true;
                        akses01.penilaian_pre_induksi=true;
                        akses01.hasil_usg_urologi=true;
                        akses01.hasil_usg_gynecologi=true;
                        akses01.hasil_pemeriksaan_ekg=true;
                        akses01.hapus_edit_sep_bpjs=true;
                        akses01.satu_sehat_kirim_diet=true;
                        akses01.satu_sehat_mapping_obat=true;
                        akses01.dapur_ringkasan_pembelian=true;
                        akses01.satu_sehat_kirim_medication=true;
                        akses01.satu_sehat_kirim_medicationrequest=true;
                        akses01.penatalaksanaan_terapi_okupasi=true;
                        akses01.satu_sehat_kirim_medicationdispense=true;
                        akses01.hasil_usg_neonatus=true;
                        akses01.hasil_endoskopi_faring_laring=true;
                        akses01.satu_sehat_mapping_radiologi=true;
                        akses01.satu_sehat_kirim_servicerequest_radiologi=true;
                        akses01.hasil_endoskopi_hidung=true;
                        akses01.satu_sehat_kirim_specimen_radiologi=true;
                        akses01.master_masalah_keperawatan_neonatus=true;
                        akses01.master_rencana_keperawatan_neonatus=true;
                        akses01.penilaian_awal_keperawatan_ranap_neonatus=true;
                        akses01.satu_sehat_kirim_observation_radiologi=true;
                        akses01.satu_sehat_kirim_diagnosticreport_radiologi=true;
                        akses01.hasil_endoskopi_telinga=true;
                        akses01.satu_sehat_mapping_lab=true;
                        akses01.satu_sehat_kirim_servicerequest_lab=true;
                        akses01.satu_sehat_kirim_servicerequest_labmb=true;
                        akses01.satu_sehat_kirim_specimen_lab=true;
                        akses01.satu_sehat_kirim_specimen_labmb=true;
                        akses01.satu_sehat_kirim_observation_lab=true;
                        akses01.satu_sehat_kirim_observation_labmb=true;
                        akses01.satu_sehat_kirim_diagnosticreport_lab=true;
                        akses01.satu_sehat_kirim_diagnosticreport_labmb=true;
                        akses01.kepatuhan_kelengkapan_keselamatan_bedah=true;
                        akses01.nilai_piutang_perjenis_bayar_per_bulan=true;
                        akses01.ringkasan_piutang_jenis_bayar=true;
                        akses01.penilaian_pasien_imunitas_rendah=true;
                        akses01.balance_cairan=true;
                        akses01.catatan_observasi_chbp=true;
                        akses01.catatan_observasi_induksi_persalinan=true;
                        akses01.skp_kategori_penilaian=true;
                        akses01.skp_kriteria_penilaian=true;
                        akses01.skp_penilaian=true;
                        akses01.referensi_poli_mobilejknfktp=true;
                        akses01.referensi_dokter_mobilejknfktp=true;
                        akses01.skp_rekapitulasi_penilaian=true;
                        akses01.pembayaran_pihak_ke3_bankmandiri=true;
                        akses01.metode_pembayaran_bankmandiri=true;
                        akses01.bank_tujuan_transfer_bankmandiri=true;
                        akses01.kodetransaksi_tujuan_transfer_bankmandiri=true;
                        akses01.konsultasi_medik=true;
                        akses01.jawaban_konsultasi_medik=true;
                        akses01.pcare_cek_alergi=true;
                        akses01.pcare_cek_prognosa=true;
                        akses01.data_sasaran_usiaproduktif=true;
                        akses01.data_sasaran_usialansia=true;
                        akses01.skrining_perilaku_merokok_sekolah_remaja=true;
                        akses01.skrining_kekerasan_pada_perempuan=true;
                        akses01.skrining_obesitas=true;
                        akses01.skrining_risiko_kanker_payudara=true;
                        akses01.skrining_risiko_kanker_paru=true;
                        akses01.skrining_tbc=true;
                        akses01.skrining_kesehatan_gigi_mulut_remaja=true;
                        akses01.penilaian_awal_keperawatan_ranap_bayi=true;
                        akses01.booking_mcu_perusahaan=true;
                        akses01.catatan_observasi_restrain_nonfarma=true;
                        akses01.catatan_observasi_ventilator=true;
                        akses01.catatan_anestesi_sedasi=true;
                        akses01.skrining_puma=true;
                        akses01.satu_sehat_kirim_careplan=true;
                        akses01.satu_sehat_kirim_medicationstatement=true;
                        akses01.skrining_adiksi_nikotin=true;
                        akses01.skrining_thalassemia=true;
                        akses01.skrining_instrumen_sdq=true;
                        akses01.skrining_instrumen_srq=true;
                        akses01.checklist_pemberian_fibrinolitik=true;
                        akses01.skrining_kanker_kolorektal=true;
                        akses01.dapur_pemesanan=true;
                        akses01.bayar_pesan_dapur=true;
                        akses01.hutang_dapur=true;
                        akses01.titip_faktur_dapur=true;
                        akses01.validasi_tagihan_dapur=true;
                        akses01.surat_pemesanan_dapur=true;
                        akses01.pengajuan_barang_dapur=true;
                        akses01.dapur_returbeli=true;
                        akses01.hibah_dapur=true;
                        akses01.ringkasan_penerimaan_dapur=true;
                        akses01.ringkasan_pengajuan_dapur=true;
                        akses01.ringkasan_pemesanan_dapur=true;
                        akses01.ringkasan_returbeli_dapur=true;
                        akses01.ringkasan_stokkeluar_dapur=true;
                        akses01.dapur_stokkeluar_pertanggal=true;
                        akses01.sirkulasi_dapur=true;
                        akses01.sirkulasi_dapur2=true;
                        akses01.verifikasi_penerimaan_dapur=true;
                        akses01.nilai_penerimaan_vendor_dapur_perbulan=true;
                        akses01.ringkasan_hutang_vendor_dapur=true;
                        akses01.penilaian_psikologi_klinis=true;
                        akses01.penilaian_awal_medis_ranap_neonatus=true;
                        akses01.penilaian_derajat_dehidrasi=true;
                        akses01.ringkasan_jasa_tindakan_medis=true;
                        akses01.pendapatan_per_akun=true;
                        akses01.hasil_pemeriksaan_echo=true;
                        akses01.penilaian_bayi_baru_lahir=true;
                        akses01.rl1_3_ketersediaan_kamar=true;
                        akses01.pendapatan_per_akun_closing=true;
                        akses01.pengeluaran_pengeluaran=true;
                        akses01.skrining_diabetes_melitus=true;
                        akses01.laporan_tindakan=true;
                        akses01.pelaksanaan_informasi_edukasi=true;
                        akses01.layanan_kedokteran_fisik_rehabilitasi=true;
                        akses01.skrining_kesehatan_gigi_mulut_balita=true;
                        akses01.skrining_anemia=true;
                        akses01.layanan_program_kfr=true;
                        akses01.skrining_hipertensi=true;
                        akses01.skrining_kesehatan_penglihatan=true;
                        akses01.catatan_observasi_hemodialisa=true;
                        akses01.skrining_kesehatan_gigi_mulut_dewasa=true;
                        akses01.skrining_risiko_kanker_serviks=true;
                        akses01.catatan_cairan_hemodialisa=true;
                        akses01.skrining_kesehatan_gigi_mulut_lansia=true;
                        akses01.skrining_indra_pendengaran=true;
                        akses01.catatan_pengkajian_paska_operasi=true;
                        akses01.skrining_frailty_syndrome=true;
                        akses01.sirkulasi_cssd=true;
                        akses01.lama_pelayanan_cssd=true;
                        akses01.catatan_observasi_bayi=true;
                        akses01.riwayat_surat_peringatan=true;
                        akses01.master_kesimpulan_anjuran_mcu=true;
                        akses01.kategori_piutang_jasa_perusahaan=true;
                        akses01.piutang_jasa_perusahaan=true;
                        akses01.bayar_piutang_jasa_perusahaan=true;
                        akses01.piutang_jasa_perusahaan_belum_lunas=true;
                        akses01.checklist_kesiapan_anestesi=true;
                        akses01.piutang_peminjaman_uang_belum_lunas=true;
                        akses01.hasil_pemeriksaan_slit_lamp=true;
                        akses01.hasil_pemeriksaan_oct=true;
                        akses01.beban_hutang_lain=true;
                        akses01.poli_asal_pasien_ranap=true;
                        akses01.pemberi_hutang_lain=true;
                        akses01.dokter_asal_pasien_ranap=true;
                        akses01.duta_parkir_rekap_keluar=true;
                        akses01.surat_keterangan_layak_terbang=true;
                        akses01.bayar_beban_hutang_lain=true;
                        akses01.surat_persetujuan_pemeriksaan_hiv=true;
                        akses01.skrining_instrumen_acrs=true;
                        akses01.surat_pernyataan_memilih_dpjp=true;
                    }else if(bcryptUser && rs2.getRow()>=1){   
                        rs2.beforeFirst();
                        rs2.next();
                        akses01.kode=user;
                        akses01.penyakit=rs2.getBoolean("penyakit");
                        akses01.obat_penyakit=rs2.getBoolean("obat_penyakit");
                        akses01.dokter=rs2.getBoolean("dokter");
                        akses01.jadwal_praktek=rs2.getBoolean("jadwal_praktek");
                        akses01.petugas=rs2.getBoolean("petugas");
                        akses01.pasien=rs2.getBoolean("pasien");
                        akses01.registrasi=rs2.getBoolean("registrasi");
                        akses01.tindakan_ralan=rs2.getBoolean("tindakan_ralan");
                        akses01.kamar_inap=rs2.getBoolean("kamar_inap");
                        akses01.tindakan_ranap=rs2.getBoolean("tindakan_ranap");
                        akses01.operasi=rs2.getBoolean("operasi");
                        akses01.rujukan_keluar=rs2.getBoolean("rujukan_keluar");
                        akses01.rujukan_masuk=rs2.getBoolean("rujukan_masuk");
                        akses01.beri_obat=rs2.getBoolean("beri_obat");
                        akses01.resep_pulang=rs2.getBoolean("resep_pulang");
                        akses01.pasien_meninggal=rs2.getBoolean("pasien_meninggal");
                        akses01.diet_pasien=rs2.getBoolean("diet_pasien");
                        akses01.kelahiran_bayi=rs2.getBoolean("kelahiran_bayi");
                        akses01.periksa_lab=rs2.getBoolean("periksa_lab");
                        akses01.periksa_radiologi=rs2.getBoolean("periksa_radiologi");
                        akses01.kasir_ralan=rs2.getBoolean("kasir_ralan");
                        akses01.deposit_pasien=rs2.getBoolean("deposit_pasien");
                        akses01.piutang_pasien=rs2.getBoolean("piutang_pasien");
                        akses01.peminjaman_berkas=rs2.getBoolean("peminjaman_berkas");
                        akses01.barcode=rs2.getBoolean("barcode");
                        akses01.presensi_harian=rs2.getBoolean("presensi_harian");
                        akses01.presensi_bulanan=rs2.getBoolean("presensi_bulanan");
                        akses01.pegawai_admin=rs2.getBoolean("pegawai_admin");
                        akses01.pegawai_user=rs2.getBoolean("pegawai_user");
                        akses01.suplier=rs2.getBoolean("suplier");
                        akses01.satuan_barang=rs2.getBoolean("satuan_barang");
                        akses01.konversi_satuan=rs2.getBoolean("konversi_satuan");
                        akses01.jenis_barang=rs2.getBoolean("jenis_barang");
                        akses01.obat=rs2.getBoolean("obat");
                        akses01.stok_opname_obat=rs2.getBoolean("stok_opname_obat");
                        akses01.stok_obat_pasien=rs2.getBoolean("stok_obat_pasien");
                        akses01.pengadaan_obat=rs2.getBoolean("pengadaan_obat");
                        akses01.pemesanan_obat=rs2.getBoolean("pemesanan_obat");
                        akses01.penjualan_obat=rs2.getBoolean("penjualan_obat");
                        akses01.piutang_obat=rs2.getBoolean("piutang_obat");
                        akses01.retur_ke_suplier=rs2.getBoolean("retur_ke_suplier");
                        akses01.retur_dari_pembeli=rs2.getBoolean("retur_dari_pembeli");
                        akses01.retur_obat_ranap=rs2.getBoolean("retur_obat_ranap");
                        akses01.retur_piutang_pasien=rs2.getBoolean("retur_piutang_pasien");
                        akses01.keuntungan_penjualan=rs2.getBoolean("keuntungan_penjualan");
                        akses01.keuntungan_beri_obat=rs2.getBoolean("keuntungan_beri_obat");
                        akses01.sirkulasi_obat=rs2.getBoolean("sirkulasi_obat");
                        akses01.ipsrs_barang=rs2.getBoolean("ipsrs_barang");
                        akses01.ipsrs_pengadaan_barang=rs2.getBoolean("ipsrs_pengadaan_barang");
                        akses01.ipsrs_stok_keluar=rs2.getBoolean("ipsrs_stok_keluar");
                        akses01.ipsrs_jenis_barang=rs2.getBoolean("ipsrs_jenis_barang");
                        akses01.ipsrs_rekap_pengadaan=rs2.getBoolean("ipsrs_rekap_pengadaan");
                        akses01.ipsrs_rekap_stok_keluar=rs2.getBoolean("ipsrs_rekap_stok_keluar");
                        akses01.ipsrs_pengeluaran_harian=rs2.getBoolean("ipsrs_pengeluaran_harian");
                        akses01.inventaris_jenis=rs2.getBoolean("inventaris_jenis");
                        akses01.inventaris_kategori=rs2.getBoolean("inventaris_kategori");
                        akses01.inventaris_merk=rs2.getBoolean("inventaris_merk");
                        akses01.inventaris_ruang=rs2.getBoolean("inventaris_ruang");
                        akses01.inventaris_produsen=rs2.getBoolean("inventaris_produsen");
                        akses01.inventaris_koleksi=rs2.getBoolean("inventaris_koleksi");
                        akses01.inventaris_inventaris=rs2.getBoolean("inventaris_inventaris");
                        akses01.inventaris_sirkulasi=rs2.getBoolean("inventaris_sirkulasi");
                        akses01.parkir_jenis=rs2.getBoolean("parkir_jenis");
                        akses01.parkir_in=rs2.getBoolean("parkir_in");
                        akses01.parkir_out=rs2.getBoolean("parkir_out");
                        akses01.parkir_rekap_harian=rs2.getBoolean("parkir_rekap_harian");
                        akses01.parkir_rekap_bulanan=rs2.getBoolean("parkir_rekap_bulanan");
                        akses01.informasi_kamar=rs2.getBoolean("informasi_kamar");
                        akses01.harian_tindakan_poli=rs2.getBoolean("harian_tindakan_poli");
                        akses01.obat_per_poli=rs2.getBoolean("obat_per_poli");
                        akses01.obat_per_kamar=rs2.getBoolean("obat_per_kamar");
                        akses01.obat_per_dokter_ralan=rs2.getBoolean("obat_per_dokter_ralan");
                        akses01.obat_per_dokter_ranap=rs2.getBoolean("obat_per_dokter_ranap");
                        akses01.harian_dokter=rs2.getBoolean("harian_dokter");
                        akses01.bulanan_dokter=rs2.getBoolean("bulanan_dokter");
                        akses01.harian_paramedis=rs2.getBoolean("harian_paramedis");
                        akses01.bulanan_paramedis=rs2.getBoolean("bulanan_paramedis");
                        akses01.pembayaran_ralan=rs2.getBoolean("pembayaran_ralan");
                        akses01.pembayaran_ranap=rs2.getBoolean("pembayaran_ranap");
                        akses01.rekap_pembayaran_ralan=rs2.getBoolean("rekap_pembayaran_ralan");
                        akses01.rekap_pembayaran_ranap=rs2.getBoolean("rekap_pembayaran_ranap");
                        akses01.tagihan_masuk=rs2.getBoolean("tagihan_masuk");
                        akses01.tambahan_biaya=rs2.getBoolean("tambahan_biaya");
                        akses01.potongan_biaya=rs2.getBoolean("potongan_biaya");
                        akses01.resep_obat=rs2.getBoolean("resep_obat");
                        akses01.resume_pasien=rs2.getBoolean("resume_pasien");
                        akses01.penyakit_ralan=rs2.getBoolean("penyakit_ralan");
                        akses01.penyakit_ranap=rs2.getBoolean("penyakit_ranap");
                        akses01.kamar=rs2.getBoolean("kamar");
                        akses01.tarif_ralan=rs2.getBoolean("tarif_ralan");
                        akses01.tarif_ranap=rs2.getBoolean("tarif_ranap");
                        akses01.tarif_lab=rs2.getBoolean("tarif_lab");
                        akses01.tarif_radiologi=rs2.getBoolean("tarif_radiologi");
                        akses01.tarif_operasi=rs2.getBoolean("tarif_operasi");
                        akses01.akun_rekening=rs2.getBoolean("akun_rekening");
                        akses01.rekening_tahun=rs2.getBoolean("rekening_tahun");
                        akses01.posting_jurnal=rs2.getBoolean("posting_jurnal");
                        akses01.buku_besar=rs2.getBoolean("buku_besar");
                        akses01.cashflow=rs2.getBoolean("cashflow");
                        akses01.keuangan=rs2.getBoolean("keuangan");
                        akses01.pengeluaran=rs2.getBoolean("pengeluaran");
                        akses01.setup_pjlab=rs2.getBoolean("setup_pjlab");
                        akses01.setup_otolokasi=rs2.getBoolean("setup_otolokasi");
                        akses01.setup_jam_kamin=rs2.getBoolean("setup_jam_kamin");
                        akses01.setup_embalase=rs2.getBoolean("setup_embalase");
                        akses01.tracer_login=rs2.getBoolean("tracer_login");
                        akses01.display=rs2.getBoolean("display");
                        akses01.set_harga_obat=rs2.getBoolean("set_harga_obat");
                        akses01.set_penggunaan_tarif=rs2.getBoolean("set_penggunaan_tarif");
                        akses01.set_oto_ralan=rs2.getBoolean("set_oto_ralan");
                        akses01.biaya_harian=rs2.getBoolean("biaya_harian");
                        akses01.biaya_masuk_sekali=rs2.getBoolean("biaya_masuk_sekali");
                        akses01.set_no_rm=rs2.getBoolean("set_no_rm");                    
                        akses01.billing_ralan=rs2.getBoolean("billing_ralan"); 
                        akses01.billing_ranap=rs2.getBoolean("billing_ranap"); 
                        akses01.jm_ranap_dokter=rs2.getBoolean("jm_ranap_dokter");   
                        akses01.igd=rs2.getBoolean("igd");                    
                        akses01.barcoderalan=rs2.getBoolean("barcoderalan"); 
                        akses01.barcoderanap=rs2.getBoolean("barcoderanap"); 
                        akses01.set_harga_obat_ralan=rs2.getBoolean("set_harga_obat_ralan"); 
                        akses01.set_harga_obat_ranap=rs2.getBoolean("set_harga_obat_ranap");
                        akses01.penyakit_pd3i=rs2.getBoolean("penyakit_pd3i");
                        akses01.surveilans_pd3i=rs2.getBoolean("surveilans_pd3i");
                        akses01.surveilans_ralan=rs2.getBoolean("surveilans_ralan");
                        akses01.diagnosa_pasien=rs2.getBoolean("diagnosa_pasien");
                        akses01.surveilans_ranap=rs2.getBoolean("surveilans_ranap");
                        akses01.admin=false;
                        akses01.user=false;
                        akses01.vakum=false;
                        akses01.aplikasi=false;
                        akses01.pny_takmenular_ranap=rs2.getBoolean("pny_takmenular_ranap");
                        akses01.pny_takmenular_ralan=rs2.getBoolean("pny_takmenular_ralan");
                        akses01.kunjungan_ralan=rs2.getBoolean("kunjungan_ralan");
                        akses01.rl32=rs2.getBoolean("rl32");
                        akses01.rl33=rs2.getBoolean("rl33");
                        akses01.rl37=rs2.getBoolean("rl37");
                        akses01.rl38=rs2.getBoolean("rl38");
                        akses01.harian_tindakan_dokter=rs2.getBoolean("harian_tindakan_dokter");
                        akses01.sms=rs2.getBoolean("sms");                    
                        akses01.sidikjari=rs2.getBoolean("sidikjari");  
                        akses01.jam_masuk=rs2.getBoolean("jam_masuk");  
                        akses01.jadwal_pegawai=rs2.getBoolean("jadwal_pegawai");  
                        akses01.parkir_barcode=rs2.getBoolean("parkir_barcode"); 
                        akses01.set_nota=rs2.getBoolean("set_nota");
                        akses01.dpjp_ranap=rs2.getBoolean("dpjp_ranap");
                        akses01.mutasi_barang=rs2.getBoolean("mutasi_barang");
                        akses01.rl34=rs2.getBoolean("rl34");
                        akses01.rl36=rs2.getBoolean("rl36");
                        akses01.fee_visit_dokter=rs2.getBoolean("fee_visit_dokter");
                        akses01.fee_bacaan_ekg=rs2.getBoolean("fee_bacaan_ekg");
                        akses01.fee_rujukan_rontgen=rs2.getBoolean("fee_rujukan_rontgen");
                        akses01.fee_rujukan_ranap=rs2.getBoolean("fee_rujukan_ranap");
                        akses01.fee_ralan=rs2.getBoolean("fee_ralan");
                        akses01.akun_bayar=rs2.getBoolean("akun_bayar");
                        akses01.bayar_pemesanan_obat=rs2.getBoolean("bayar_pemesanan_obat");
                        akses01.obat_per_dokter_peresep=rs2.getBoolean("obat_per_dokter_peresep");
                        akses01.pemasukan_lain=rs2.getBoolean("pemasukan_lain");
                        akses01.pengaturan_rekening=rs2.getBoolean("pengaturan_rekening");
                        akses01.closing_kasir=rs2.getBoolean("closing_kasir");
                        akses01.keterlambatan_presensi=rs2.getBoolean("keterlambatan_presensi");
                        akses01.set_harga_kamar=rs2.getBoolean("set_harga_kamar");
                        akses01.rekap_per_shift=rs2.getBoolean("rekap_per_shift");
                        akses01.bpjs_cek_nik=rs2.getBoolean("bpjs_cek_nik");
                        akses01.bpjs_cek_kartu=rs2.getBoolean("bpjs_cek_kartu");
                        akses01.bpjs_cek_riwayat=rs2.getBoolean("bpjs_cek_riwayat");
                        akses01.obat_per_cara_bayar=rs2.getBoolean("obat_per_cara_bayar");
                        akses01.kunjungan_ranap=rs2.getBoolean("kunjungan_ranap");
                        akses01.bayar_piutang=rs2.getBoolean("bayar_piutang");
                        akses01.payment_point=rs2.getBoolean("payment_point");
                        akses01.bpjs_cek_nomor_rujukan=rs2.getBoolean("bpjs_cek_nomor_rujukan");
                        akses01.icd9=rs2.getBoolean("icd9");
                        akses01.darurat_stok=rs2.getBoolean("darurat_stok");
                        akses01.retensi_rm=rs2.getBoolean("retensi_rm");
                        akses01.temporary_presensi=rs2.getBoolean("temporary_presensi");
                        akses01.jurnal_harian=rs2.getBoolean("jurnal_harian");
                        akses01.sirkulasi_obat2=rs2.getBoolean("sirkulasi_obat2");
                        akses01.edit_registrasi=rs2.getBoolean("edit_registrasi");
                        akses01.bpjs_referensi_diagnosa=rs2.getBoolean("bpjs_referensi_diagnosa");
                        akses01.bpjs_referensi_poli=rs2.getBoolean("bpjs_referensi_poli");
                        akses01.industrifarmasi=rs2.getBoolean("industrifarmasi");
                        akses01.harian_js=rs2.getBoolean("harian_js");
                        akses01.bulanan_js=rs2.getBoolean("bulanan_js");
                        akses01.harian_paket_bhp=rs2.getBoolean("harian_paket_bhp");
                        akses01.bulanan_paket_bhp=rs2.getBoolean("bulanan_paket_bhp");
                        akses01.piutang_pasien2=rs2.getBoolean("piutang_pasien2");
                        akses01.bpjs_referensi_faskes=rs2.getBoolean("bpjs_referensi_faskes");
                        akses01.bpjs_sep=rs2.getBoolean("bpjs_sep");
                        akses01.pengambilan_utd=rs2.getBoolean("pengambilan_utd");
                        akses01.tarif_utd=rs2.getBoolean("tarif_utd");
                        akses01.pengambilan_utd2=rs2.getBoolean("pengambilan_utd2");
                        akses01.utd_medis_rusak=rs2.getBoolean("utd_medis_rusak");
                        akses01.pengambilan_penunjang_utd=rs2.getBoolean("pengambilan_penunjang_utd");
                        akses01.pengambilan_penunjang_utd2=rs2.getBoolean("pengambilan_penunjang_utd2");
                        akses01.utd_penunjang_rusak=rs2.getBoolean("utd_penunjang_rusak");
                        akses01.suplier_penunjang=rs2.getBoolean("suplier_penunjang");
                        akses01.utd_donor=rs2.getBoolean("utd_donor");
                        akses01.bpjs_monitoring_klaim=rs2.getBoolean("bpjs_monitoring_klaim");
                        akses01.utd_cekal_darah=rs2.getBoolean("utd_cekal_darah");
                        akses01.utd_komponen_darah=rs2.getBoolean("utd_komponen_darah");
                        akses01.utd_stok_darah=rs2.getBoolean("utd_stok_darah");
                        akses01.utd_pemisahan_darah=rs2.getBoolean("utd_pemisahan_darah");
                        akses01.harian_kamar=rs2.getBoolean("harian_kamar");
                        akses01.rincian_piutang_pasien=rs2.getBoolean("rincian_piutang_pasien");
                        akses01.keuntungan_beri_obat_nonpiutang=rs2.getBoolean("keuntungan_beri_obat_nonpiutang");
                        akses01.reklasifikasi_ralan=rs2.getBoolean("reklasifikasi_ralan");
                        akses01.reklasifikasi_ranap=rs2.getBoolean("reklasifikasi_ranap");
                        akses01.utd_penyerahan_darah=rs2.getBoolean("utd_penyerahan_darah");
                        akses01.hutang_obat=rs2.getBoolean("hutang_obat");
                        akses01.riwayat_obat_alkes_bhp=rs2.getBoolean("riwayat_obat_alkes_bhp");
                        akses01.sensus_harian_poli=rs2.getBoolean("sensus_harian_poli");
                        akses01.rl4a=rs2.getBoolean("rl4a");
                        akses01.aplicare_referensi_kamar=rs2.getBoolean("aplicare_referensi_kamar");
                        akses01.aplicare_ketersediaan_kamar=rs2.getBoolean("aplicare_ketersediaan_kamar");
                        akses01.inacbg_klaim_baru_otomatis=rs2.getBoolean("inacbg_klaim_baru_otomatis");
                        akses01.inacbg_klaim_baru_manual=rs2.getBoolean("inacbg_klaim_baru_manual");
                        akses01.inacbg_coder_nik=rs2.getBoolean("inacbg_coder_nik");
                        akses01.mutasi_berkas=rs2.getBoolean("mutasi_berkas");
                        akses01.akun_piutang=rs2.getBoolean("akun_piutang");
                        akses01.harian_kso=rs2.getBoolean("harian_kso");
                        akses01.bulanan_kso=rs2.getBoolean("bulanan_kso");                        
                        akses01.harian_menejemen=rs2.getBoolean("harian_menejemen");
                        akses01.bulanan_menejemen=rs2.getBoolean("bulanan_menejemen");
                        akses01.inhealth_cek_eligibilitas=rs2.getBoolean("inhealth_cek_eligibilitas");
                        akses01.inhealth_referensi_jenpel_ruang_rawat=rs2.getBoolean("inhealth_referensi_jenpel_ruang_rawat");
                        akses01.inhealth_referensi_poli=rs2.getBoolean("inhealth_referensi_poli");
                        akses01.inhealth_referensi_faskes=rs2.getBoolean("inhealth_referensi_faskes");
                        akses01.inhealth_sjp=rs2.getBoolean("inhealth_sjp");
                        akses01.piutang_ralan=rs2.getBoolean("piutang_ralan");
                        akses01.piutang_ranap=rs2.getBoolean("piutang_ranap");
                        akses01.detail_piutang_penjab=rs2.getBoolean("detail_piutang_penjab");
                        akses01.lama_pelayanan_ralan=rs2.getBoolean("lama_pelayanan_ralan");
                        akses01.catatan_pasien=rs2.getBoolean("catatan_pasien");
                        akses01.rl4b=rs2.getBoolean("rl4b");                        
                        akses01.rl4asebab=rs2.getBoolean("rl4asebab"); 
                        akses01.rl4bsebab=rs2.getBoolean("rl4bsebab"); 
                        akses01.data_HAIs=rs2.getBoolean("data_HAIs");
                        akses01.harian_HAIs=rs2.getBoolean("harian_HAIs");
                        akses01.bulanan_HAIs=rs2.getBoolean("bulanan_HAIs");                        
                        akses01.hitung_bor=rs2.getBoolean("hitung_bor");
                        akses01.perusahaan_pasien=rs2.getBoolean("perusahaan_pasien");
                        akses01.resep_dokter=rs2.getBoolean("resep_dokter");
                        akses01.lama_pelayanan_apotek=rs2.getBoolean("lama_pelayanan_apotek");
                        akses01.hitung_alos=rs2.getBoolean("hitung_alos");
                        akses01.detail_tindakan=rs2.getBoolean("detail_tindakan");
                        akses01.rujukan_poli_internal=rs2.getBoolean("rujukan_poli_internal");
                        akses01.rekap_poli_anak=rs2.getBoolean("rekap_poli_anak");
                        akses01.grafik_kunjungan_poli=rs2.getBoolean("grafik_kunjungan_poli");
                        akses01.grafik_kunjungan_perdokter=rs2.getBoolean("grafik_kunjungan_perdokter");
                        akses01.grafik_kunjungan_perpekerjaan=rs2.getBoolean("grafik_kunjungan_perpekerjaan");
                        akses01.grafik_kunjungan_perpendidikan=rs2.getBoolean("grafik_kunjungan_perpendidikan");
                        akses01.grafik_kunjungan_pertahun=rs2.getBoolean("grafik_kunjungan_pertahun");
                        akses01.berkas_digital_perawatan=rs2.getBoolean("berkas_digital_perawatan");
                        akses01.penyakit_menular_ranap=rs2.getBoolean("penyakit_menular_ranap");
                        akses01.penyakit_menular_ralan=rs2.getBoolean("penyakit_menular_ralan");
                        akses01.grafik_kunjungan_perbulan=rs2.getBoolean("grafik_kunjungan_perbulan");                        
                        akses01.grafik_kunjungan_pertanggal=rs2.getBoolean("grafik_kunjungan_pertanggal");
                        akses01.grafik_kunjungan_demografi=rs2.getBoolean("grafik_kunjungan_demografi");
                        akses01.grafik_kunjungan_statusdaftartahun=rs2.getBoolean("grafik_kunjungan_statusdaftartahun");
                        akses01.grafik_kunjungan_statusdaftartahun2=rs2.getBoolean("grafik_kunjungan_statusdaftartahun2");                        
                        akses01.grafik_kunjungan_statusdaftarbulan=rs2.getBoolean("grafik_kunjungan_statusdaftarbulan");
                        akses01.grafik_kunjungan_statusdaftarbulan2=rs2.getBoolean("grafik_kunjungan_statusdaftarbulan2");
                        akses01.grafik_kunjungan_statusdaftartanggal=rs2.getBoolean("grafik_kunjungan_statusdaftartanggal");
                        akses01.grafik_kunjungan_statusdaftartanggal2=rs2.getBoolean("grafik_kunjungan_statusdaftartanggal2");
                        akses01.grafik_kunjungan_statusbataltahun=rs2.getBoolean("grafik_kunjungan_statusbataltahun");
                        akses01.grafik_kunjungan_statusbatalbulan=rs2.getBoolean("grafik_kunjungan_statusbatalbulan");
                        akses01.pcare_cek_penyakit=rs2.getBoolean("pcare_cek_penyakit");
                        akses01.grafik_kunjungan_statusbataltanggal=rs2.getBoolean("grafik_kunjungan_statusbataltanggal");
                        akses01.kategori_barang=rs2.getBoolean("kategori_barang");
                        akses01.golongan_barang=rs2.getBoolean("golongan_barang");
                        akses01.pemberian_obat_pertanggal=rs2.getBoolean("pemberian_obat_pertanggal");
                        akses01.penjualan_obat_pertanggal=rs2.getBoolean("penjualan_obat_pertanggal");
                        akses01.pcare_cek_kesadaran=rs2.getBoolean("pcare_cek_kesadaran");                        
                        akses01.pembatalan_periksa_dokter=rs2.getBoolean("pembatalan_periksa_dokter");
                        akses01.pembayaran_per_unit=rs2.getBoolean("pembayaran_per_unit");
                        akses01.rekap_pembayaran_per_unit=rs2.getBoolean("rekap_pembayaran_per_unit");                        
                        akses01.grafik_kunjungan_percarabayar=rs2.getBoolean("grafik_kunjungan_percarabayar");
                        akses01.ipsrs_pengadaan_pertanggal=rs2.getBoolean("ipsrs_pengadaan_pertanggal");
                        akses01.ipsrs_stokkeluar_pertanggal=rs2.getBoolean("ipsrs_stokkeluar_pertanggal");
                        akses01.grafik_kunjungan_ranaptahun=rs2.getBoolean("grafik_kunjungan_ranaptahun");
                        akses01.pcare_cek_rujukan=rs2.getBoolean("pcare_cek_rujukan");
                        akses01.grafik_lab_ralantahun=rs2.getBoolean("grafik_lab_ralantahun");                        
                        akses01.grafik_rad_ralantahun=rs2.getBoolean("grafik_rad_ralantahun");
                        akses01.cek_entry_ralan=rs2.getBoolean("cek_entry_ralan");
                        akses01.inacbg_klaim_baru_manual2=rs2.getBoolean("inacbg_klaim_baru_manual2");
                        akses01.permintaan_medis=rs2.getBoolean("permintaan_medis");
                        akses01.rekap_permintaan_medis=rs2.getBoolean("rekap_permintaan_medis");
                        akses01.surat_pemesanan_medis=rs2.getBoolean("surat_pemesanan_medis");
                        akses01.permintaan_non_medis=rs2.getBoolean("permintaan_non_medis");
                        akses01.rekap_permintaan_non_medis=rs2.getBoolean("rekap_permintaan_non_medis");
                        akses01.surat_pemesanan_non_medis=rs2.getBoolean("surat_pemesanan_non_medis");
                        akses01.grafik_per_perujuk=rs2.getBoolean("grafik_per_perujuk");
                        akses01.bpjs_cek_prosedur=rs2.getBoolean("bpjs_cek_prosedur");
                        akses01.bpjs_cek_kelas_rawat=rs2.getBoolean("bpjs_cek_kelas_rawat");
                        akses01.bpjs_cek_dokter=rs2.getBoolean("bpjs_cek_dokter");
                        akses01.bpjs_cek_spesialistik=rs2.getBoolean("bpjs_cek_spesialistik");
                        akses01.bpjs_cek_ruangrawat=rs2.getBoolean("bpjs_cek_ruangrawat");                        
                        akses01.bpjs_cek_carakeluar=rs2.getBoolean("bpjs_cek_carakeluar");
                        akses01.bpjs_cek_pasca_pulang=rs2.getBoolean("bpjs_cek_pasca_pulang");
                        akses01.detail_tindakan_okvk=rs2.getBoolean("detail_tindakan_okvk");
                        akses01.billing_parsial=rs2.getBoolean("billing_parsial");
                        akses01.bpjs_cek_nomor_rujukan_rs=rs2.getBoolean("bpjs_cek_nomor_rujukan_rs");
                        akses01.bpjs_cek_rujukan_kartu_pcare=rs2.getBoolean("bpjs_cek_rujukan_kartu_pcare");
                        akses01.bpjs_cek_rujukan_kartu_rs=rs2.getBoolean("bpjs_cek_rujukan_kartu_rs");
                        akses01.akses_depo_obat=rs2.getBoolean("akses_depo_obat");
                        akses01.bpjs_rujukan_keluar=rs2.getBoolean("bpjs_rujukan_keluar");
                        akses01.grafik_lab_ralanbulan=rs2.getBoolean("grafik_lab_ralanbulan");
                        akses01.pengeluaran_stok_apotek=rs2.getBoolean("pengeluaran_stok_apotek");
                        akses01.grafik_rad_ralanbulan=rs2.getBoolean("grafik_rad_ralanbulan");
                        akses01.detailjmdokter2=rs2.getBoolean("detailjmdokter2");
                        akses01.pengaduan_pasien=rs2.getBoolean("pengaduan_pasien");
                        akses01.grafik_lab_ralanhari=rs2.getBoolean("grafik_lab_ralanhari");
                        akses01.grafik_rad_ralanhari=rs2.getBoolean("grafik_rad_ralanhari");
                        akses01.sensus_harian_ralan=rs2.getBoolean("sensus_harian_ralan");
                        akses01.metode_racik=rs2.getBoolean("metode_racik");
                        akses01.pembayaran_akun_bayar=rs2.getBoolean("pembayaran_akun_bayar");
                        akses01.pengguna_obat_resep=rs2.getBoolean("pengguna_obat_resep");
                        akses01.rekap_pemesanan=rs2.getBoolean("rekap_pemesanan");
                        akses01.master_berkas_pegawai=rs2.getBoolean("master_berkas_pegawai");
                        akses01.berkas_kepegawaian=rs2.getBoolean("berkas_kepegawaian");
                        akses01.riwayat_jabatan=rs2.getBoolean("riwayat_jabatan");
                        akses01.riwayat_pendidikan=rs2.getBoolean("riwayat_pendidikan");
                        akses01.riwayat_naik_gaji=rs2.getBoolean("riwayat_naik_gaji");
                        akses01.kegiatan_ilmiah=rs2.getBoolean("kegiatan_ilmiah");
                        akses01.riwayat_penghargaan=rs2.getBoolean("riwayat_penghargaan");
                        akses01.riwayat_penelitian=rs2.getBoolean("riwayat_penelitian");
                        akses01.penerimaan_non_medis=rs2.getBoolean("penerimaan_non_medis");
                        akses01.bayar_pesan_non_medis=rs2.getBoolean("bayar_pesan_non_medis");
                        akses01.hutang_barang_non_medis=rs2.getBoolean("hutang_barang_non_medis");
                        akses01.rekap_pemesanan_non_medis=rs2.getBoolean("rekap_pemesanan_non_medis");                        
                        akses01.insiden_keselamatan=rs2.getBoolean("insiden_keselamatan");
                        akses01.insiden_keselamatan_pasien=rs2.getBoolean("insiden_keselamatan_pasien");
                        akses01.grafik_ikp_pertahun=rs2.getBoolean("grafik_ikp_pertahun");
                        akses01.grafik_ikp_perbulan=rs2.getBoolean("grafik_ikp_perbulan");
                        akses01.grafik_ikp_pertanggal=rs2.getBoolean("grafik_ikp_pertanggal");
                        akses01.riwayat_data_batch=rs2.getBoolean("riwayat_data_batch");
                        akses01.grafik_ikp_jenis=rs2.getBoolean("grafik_ikp_jenis");
                        akses01.grafik_ikp_dampak=rs2.getBoolean("grafik_ikp_dampak");
                        akses01.piutang_akun_piutang=rs2.getBoolean("piutang_akun_piutang");
                        akses01.grafik_kunjungan_per_agama=rs2.getBoolean("grafik_kunjungan_per_agama");
                        akses01.grafik_kunjungan_per_umur=rs2.getBoolean("grafik_kunjungan_per_umur");
                        akses01.suku_bangsa=rs2.getBoolean("suku_bangsa");
                        akses01.bahasa_pasien=rs2.getBoolean("bahasa_pasien");
                        akses01.golongan_tni=rs2.getBoolean("golongan_tni");
                        akses01.satuan_tni=rs2.getBoolean("satuan_tni");
                        akses01.jabatan_tni=rs2.getBoolean("jabatan_tni");
                        akses01.pangkat_tni=rs2.getBoolean("pangkat_tni");
                        akses01.golongan_polri=rs2.getBoolean("golongan_polri");
                        akses01.satuan_polri=rs2.getBoolean("satuan_polri");
                        akses01.jabatan_polri=rs2.getBoolean("jabatan_polri");
                        akses01.pangkat_polri=rs2.getBoolean("pangkat_polri");
                        akses01.cacat_fisik=rs2.getBoolean("cacat_fisik");
                        akses01.grafik_kunjungan_suku=rs2.getBoolean("grafik_kunjungan_suku");
                        akses01.grafik_kunjungan_bahasa=rs2.getBoolean("grafik_kunjungan_bahasa");
                        akses01.booking_operasi=rs2.getBoolean("booking_operasi");
                        akses01.mapping_poli_bpjs=rs2.getBoolean("mapping_poli_bpjs");
                        akses01.grafik_kunjungan_per_cacat=rs2.getBoolean("grafik_kunjungan_per_cacat");
                        akses01.barang_cssd=rs2.getBoolean("barang_cssd");
                        akses01.skdp_bpjs=rs2.getBoolean("skdp_bpjs");
                        akses01.booking_registrasi=rs2.getBoolean("booking_registrasi");
                        akses01.bpjs_cek_propinsi=rs2.getBoolean("bpjs_cek_propinsi");
                        akses01.bpjs_cek_kabupaten=rs2.getBoolean("bpjs_cek_kabupaten");
                        akses01.bpjs_cek_kecamatan=rs2.getBoolean("bpjs_cek_kecamatan");
                        akses01.bpjs_cek_dokterdpjp=rs2.getBoolean("bpjs_cek_dokterdpjp");
                        akses01.bpjs_cek_riwayat_rujukanrs=rs2.getBoolean("bpjs_cek_riwayat_rujukanrs");
                        akses01.bpjs_cek_tanggal_rujukan=rs2.getBoolean("bpjs_cek_tanggal_rujukan");
                        akses01.permintaan_lab=rs2.getBoolean("permintaan_lab");
                        akses01.permintaan_radiologi=rs2.getBoolean("permintaan_radiologi");
                        akses01.surat_indeks=rs2.getBoolean("surat_indeks");
                        akses01.surat_map=rs2.getBoolean("surat_map");
                        akses01.surat_almari=rs2.getBoolean("surat_almari");
                        akses01.surat_rak=rs2.getBoolean("surat_rak");
                        akses01.surat_ruang=rs2.getBoolean("surat_ruang");
                        akses01.surat_klasifikasi=rs2.getBoolean("surat_klasifikasi");
                        akses01.surat_status=rs2.getBoolean("surat_status");
                        akses01.surat_sifat=rs2.getBoolean("surat_sifat");
                        akses01.surat_balas=rs2.getBoolean("surat_balas");
                        akses01.surat_masuk=rs2.getBoolean("surat_masuk");
                        akses01.pcare_cek_dokter=rs2.getBoolean("pcare_cek_dokter");
                        akses01.pcare_cek_poli=rs2.getBoolean("pcare_cek_poli");
                        akses01.pcare_cek_provider=rs2.getBoolean("pcare_cek_provider");
                        akses01.pcare_cek_statuspulang=rs2.getBoolean("pcare_cek_statuspulang");
                        akses01.pcare_cek_spesialis=rs2.getBoolean("pcare_cek_spesialis");
                        akses01.pcare_cek_subspesialis=rs2.getBoolean("pcare_cek_subspesialis");
                        akses01.pcare_cek_sarana=rs2.getBoolean("pcare_cek_sarana");
                        akses01.pcare_cek_khusus=rs2.getBoolean("pcare_cek_khusus");
                        akses01.pcare_cek_obat=rs2.getBoolean("pcare_cek_obat");
                        akses01.pcare_cek_tindakan=rs2.getBoolean("pcare_cek_tindakan");
                        akses01.pcare_cek_faskessubspesialis=rs2.getBoolean("pcare_cek_faskessubspesialis");
                        akses01.pcare_cek_faskesalihrawat=rs2.getBoolean("pcare_cek_faskesalihrawat");
                        akses01.pcare_cek_faskesthalasemia=rs2.getBoolean("pcare_cek_faskesthalasemia");
                        akses01.pcare_mapping_obat=rs2.getBoolean("pcare_mapping_obat");
                        akses01.pcare_mapping_tindakan=rs2.getBoolean("pcare_mapping_tindakan");
                        akses01.pcare_club_prolanis=rs2.getBoolean("pcare_club_prolanis");
                        akses01.pcare_mapping_poli=rs2.getBoolean("pcare_mapping_poli");
                        akses01.pcare_kegiatan_kelompok=rs2.getBoolean("pcare_kegiatan_kelompok");
                        akses01.pcare_mapping_tindakan_ranap=rs2.getBoolean("pcare_mapping_tindakan_ranap");
                        akses01.pcare_peserta_kegiatan_kelompok=rs2.getBoolean("pcare_peserta_kegiatan_kelompok");
                        akses01.sirkulasi_obat3=rs2.getBoolean("sirkulasi_obat3");
                        akses01.bridging_pcare_daftar=rs2.getBoolean("bridging_pcare_daftar");
                        akses01.pcare_mapping_dokter=rs2.getBoolean("pcare_mapping_dokter");
                        akses01.ranap_per_ruang=rs2.getBoolean("ranap_per_ruang");
                        akses01.penyakit_ranap_cara_bayar=rs2.getBoolean("penyakit_ranap_cara_bayar");
                        akses01.anggota_militer_dirawat=rs2.getBoolean("anggota_militer_dirawat");
                        akses01.set_input_parsial=rs2.getBoolean("set_input_parsial");
                        akses01.lama_pelayanan_radiologi=rs2.getBoolean("lama_pelayanan_radiologi");
                        akses01.lama_pelayanan_lab=rs2.getBoolean("lama_pelayanan_lab");
                        akses01.bpjs_cek_sep=rs2.getBoolean("bpjs_cek_sep");
                        akses01.catatan_perawatan=rs2.getBoolean("catatan_perawatan");
                        akses01.surat_keluar=rs2.getBoolean("surat_keluar");
                        akses01.kegiatan_farmasi=rs2.getBoolean("kegiatan_farmasi");
                        akses01.stok_opname_logistik=rs2.getBoolean("stok_opname_logistik");
                        akses01.sirkulasi_non_medis=rs2.getBoolean("sirkulasi_non_medis");
                        akses01.rekap_lab_pertahun=rs2.getBoolean("rekap_lab_pertahun");
                        akses01.perujuk_lab_pertahun=rs2.getBoolean("perujuk_lab_pertahun");
                        akses01.rekap_radiologi_pertahun=rs2.getBoolean("rekap_radiologi_pertahun");
                        akses01.perujuk_radiologi_pertahun=rs2.getBoolean("perujuk_radiologi_pertahun");
                        akses01.jumlah_porsi_diet=rs2.getBoolean("jumlah_porsi_diet");
                        akses01.jumlah_macam_diet=rs2.getBoolean("jumlah_macam_diet");
                        akses01.payment_point2=rs2.getBoolean("payment_point2");
                        akses01.pembayaran_akun_bayar2=rs2.getBoolean("pembayaran_akun_bayar2");
                        akses01.hapus_nota_salah=rs2.getBoolean("hapus_nota_salah");
                        akses01.hais_perbangsal=rs2.getBoolean("hais_perbangsal");
                        akses01.ppn_obat=rs2.getBoolean("ppn_obat");
                        akses01.saldo_akun_perbulan=rs2.getBoolean("saldo_akun_perbulan");
                        akses01.display_apotek=rs2.getBoolean("display_apotek");
                        akses01.sisrute_referensi_faskes=rs2.getBoolean("sisrute_referensi_faskes");
                        akses01.sisrute_referensi_alasanrujuk=rs2.getBoolean("sisrute_referensi_alasanrujuk");
                        akses01.sisrute_referensi_diagnosa=rs2.getBoolean("sisrute_referensi_diagnosa");
                        akses01.sisrute_rujukan_masuk=rs2.getBoolean("sisrute_rujukan_masuk");
                        akses01.sisrute_rujukan_keluar=rs2.getBoolean("sisrute_rujukan_keluar");
                        akses01.bpjs_cek_skdp=rs2.getBoolean("bpjs_cek_skdp");
                        akses01.data_batch=rs2.getBoolean("data_batch");
                        akses01.kunjungan_permintaan_lab=rs2.getBoolean("kunjungan_permintaan_lab");
                        akses01.kunjungan_permintaan_lab2=rs2.getBoolean("kunjungan_permintaan_lab2");
                        akses01.kunjungan_permintaan_radiologi=rs2.getBoolean("kunjungan_permintaan_radiologi");
                        akses01.kunjungan_permintaan_radiologi2=rs2.getBoolean("kunjungan_permintaan_radiologi2");
                        akses01.pcare_pemberian_obat=rs2.getBoolean("pcare_pemberian_obat");
                        akses01.pcare_pemberian_tindakan=rs2.getBoolean("pcare_pemberian_tindakan");
                        akses01.pembayaran_akun_bayar3=rs2.getBoolean("pembayaran_akun_bayar3");
                        akses01.password_asuransi=rs2.getBoolean("password_asuransi");
                        akses01.kemenkes_sitt=rs2.getBoolean("kemenkes_sitt");
                        akses01.siranap_ketersediaan_kamar=rs2.getBoolean("siranap_ketersediaan_kamar");
                        akses01.grafik_tb_periodelaporan=rs2.getBoolean("grafik_tb_periodelaporan");
                        akses01.grafik_tb_rujukan=rs2.getBoolean("grafik_tb_rujukan");
                        akses01.grafik_tb_riwayat=rs2.getBoolean("grafik_tb_riwayat");
                        akses01.grafik_tb_tipediagnosis=rs2.getBoolean("grafik_tb_tipediagnosis");
                        akses01.grafik_tb_statushiv=rs2.getBoolean("grafik_tb_statushiv");
                        akses01.grafik_tb_skoringanak=rs2.getBoolean("grafik_tb_skoringanak");
                        akses01.grafik_tb_konfirmasiskoring5=rs2.getBoolean("grafik_tb_konfirmasiskoring5");
                        akses01.grafik_tb_konfirmasiskoring6=rs2.getBoolean("grafik_tb_konfirmasiskoring6");
                        akses01.grafik_tb_sumberobat=rs2.getBoolean("grafik_tb_sumberobat");
                        akses01.grafik_tb_hasilakhirpengobatan=rs2.getBoolean("grafik_tb_hasilakhirpengobatan");
                        akses01.grafik_tb_hasilteshiv=rs2.getBoolean("grafik_tb_hasilteshiv");
                        akses01.kadaluarsa_batch=rs2.getBoolean("kadaluarsa_batch");
                        akses01.sisa_stok=rs2.getBoolean("sisa_stok");
                        akses01.obat_per_resep=rs2.getBoolean("obat_per_resep");
                        akses01.pemakaian_air_pdam=rs2.getBoolean("pemakaian_air_pdam");
                        akses01.limbah_b3_medis=rs2.getBoolean("limbah_b3_medis");
                        akses01.grafik_air_pdam_pertanggal=rs2.getBoolean("grafik_air_pdam_pertanggal");
                        akses01.grafik_air_pdam_perbulan=rs2.getBoolean("grafik_air_pdam_perbulan");
                        akses01.grafik_limbahb3_pertanggal=rs2.getBoolean("grafik_limbahb3_pertanggal");
                        akses01.grafik_limbahb3_perbulan=rs2.getBoolean("grafik_limbahb3_perbulan");
                        akses01.limbah_domestik=rs2.getBoolean("limbah_domestik");
                        akses01.grafik_limbahdomestik_pertanggal=rs2.getBoolean("grafik_limbahdomestik_pertanggal");
                        akses01.grafik_limbahdomestik_perbulan=rs2.getBoolean("grafik_limbahdomestik_perbulan");
                        akses01.mutu_air_limbah=rs2.getBoolean("mutu_air_limbah");
                        akses01.pest_control=rs2.getBoolean("pest_control");
                        akses01.ruang_perpustakaan=rs2.getBoolean("ruang_perpustakaan");
                        akses01.kategori_perpustakaan=rs2.getBoolean("kategori_perpustakaan");
                        akses01.jenis_perpustakaan=rs2.getBoolean("jenis_perpustakaan");
                        akses01.pengarang_perpustakaan=rs2.getBoolean("pengarang_perpustakaan");
                        akses01.penerbit_perpustakaan=rs2.getBoolean("penerbit_perpustakaan");
                        akses01.koleksi_perpustakaan=rs2.getBoolean("koleksi_perpustakaan");
                        akses01.inventaris_perpustakaan=rs2.getBoolean("inventaris_perpustakaan");
                        akses01.set_peminjaman_perpustakaan=rs2.getBoolean("set_peminjaman_perpustakaan");
                        akses01.denda_perpustakaan=rs2.getBoolean("denda_perpustakaan");
                        akses01.anggota_perpustakaan=rs2.getBoolean("anggota_perpustakaan");
                        akses01.peminjaman_perpustakaan=rs2.getBoolean("peminjaman_perpustakaan");
                        akses01.bayar_denda_perpustakaan=rs2.getBoolean("bayar_denda_perpustakaan");
                        akses01.ebook_perpustakaan=rs2.getBoolean("ebook_perpustakaan");
                        akses01.jenis_cidera_k3rs=rs2.getBoolean("jenis_cidera_k3rs");
                        akses01.penyebab_k3rs=rs2.getBoolean("penyebab_k3rs");
                        akses01.jenis_luka_k3rs=rs2.getBoolean("jenis_luka_k3rs");
                        akses01.lokasi_kejadian_k3rs=rs2.getBoolean("lokasi_kejadian_k3rs");
                        akses01.dampak_cidera_k3rs=rs2.getBoolean("dampak_cidera_k3rs");
                        akses01.jenis_pekerjaan_k3rs=rs2.getBoolean("jenis_pekerjaan_k3rs");
                        akses01.bagian_tubuh_k3rs=rs2.getBoolean("bagian_tubuh_k3rs");
                        akses01.peristiwa_k3rs=rs2.getBoolean("peristiwa_k3rs");
                        akses01.grafik_k3_pertahun=rs2.getBoolean("grafik_k3_pertahun");
                        akses01.grafik_k3_perbulan=rs2.getBoolean("grafik_k3_perbulan");
                        akses01.grafik_k3_pertanggal=rs2.getBoolean("grafik_k3_pertanggal");
                        akses01.grafik_k3_perjeniscidera=rs2.getBoolean("grafik_k3_perjeniscidera");
                        akses01.grafik_k3_perpenyebab=rs2.getBoolean("grafik_k3_perpenyebab");
                        akses01.grafik_k3_perjenisluka=rs2.getBoolean("grafik_k3_perjenisluka");
                        akses01.grafik_k3_lokasikejadian=rs2.getBoolean("grafik_k3_lokasikejadian");
                        akses01.grafik_k3_dampakcidera=rs2.getBoolean("grafik_k3_dampakcidera");
                        akses01.grafik_k3_perjenispekerjaan=rs2.getBoolean("grafik_k3_perjenispekerjaan");
                        akses01.grafik_k3_perbagiantubuh=rs2.getBoolean("grafik_k3_perbagiantubuh");
                        akses01.jenis_cidera_k3rstahun=rs2.getBoolean("jenis_cidera_k3rstahun");
                        akses01.penyebab_k3rstahun=rs2.getBoolean("penyebab_k3rstahun");
                        akses01.jenis_luka_k3rstahun=rs2.getBoolean("jenis_luka_k3rstahun");
                        akses01.lokasi_kejadian_k3rstahun=rs2.getBoolean("lokasi_kejadian_k3rstahun");
                        akses01.dampak_cidera_k3rstahun=rs2.getBoolean("dampak_cidera_k3rstahun");
                        akses01.jenis_pekerjaan_k3rstahun=rs2.getBoolean("jenis_pekerjaan_k3rstahun");
                        akses01.bagian_tubuh_k3rstahun=rs2.getBoolean("bagian_tubuh_k3rstahun");
                        akses01.sekrining_rawat_jalan=rs2.getBoolean("sekrining_rawat_jalan");
                        akses01.bpjs_histori_pelayanan=rs2.getBoolean("bpjs_histori_pelayanan");
                        akses01.rekap_mutasi_berkas=rs2.getBoolean("rekap_mutasi_berkas");
                        akses01.skrining_ralan_pernapasan_pertahun=rs2.getBoolean("skrining_ralan_pernapasan_pertahun");
                        akses01.pengajuan_barang_medis=rs2.getBoolean("pengajuan_barang_medis");
                        akses01.pengajuan_barang_nonmedis=rs2.getBoolean("pengajuan_barang_nonmedis");
                        akses01.grafik_kunjungan_ranapbulan=rs2.getBoolean("grafik_kunjungan_ranapbulan");
                        akses01.grafik_kunjungan_ranaptanggal=rs2.getBoolean("grafik_kunjungan_ranaptanggal");
                        akses01.grafik_kunjungan_ranap_peruang=rs2.getBoolean("grafik_kunjungan_ranap_peruang");
                        akses01.kunjungan_bangsal_pertahun=rs2.getBoolean("kunjungan_bangsal_pertahun");
                        akses01.grafik_jenjang_jabatanpegawai=rs2.getBoolean("grafik_jenjang_jabatanpegawai");
                        akses01.grafik_bidangpegawai=rs2.getBoolean("grafik_bidangpegawai");
                        akses01.grafik_departemenpegawai=rs2.getBoolean("grafik_departemenpegawai");
                        akses01.grafik_pendidikanpegawai=rs2.getBoolean("grafik_pendidikanpegawai");
                        akses01.grafik_sttswppegawai=rs2.getBoolean("grafik_sttswppegawai");
                        akses01.grafik_sttskerjapegawai=rs2.getBoolean("grafik_sttskerjapegawai");
                        akses01.grafik_sttspulangranap=rs2.getBoolean("grafik_sttspulangranap");
                        akses01.kip_pasien_ranap=rs2.getBoolean("kip_pasien_ranap");
                        akses01.kip_pasien_ralan=rs2.getBoolean("kip_pasien_ralan");
                        akses01.bpjs_mapping_dokterdpjp=rs2.getBoolean("bpjs_mapping_dokterdpjp");
                        akses01.data_triase_igd=rs2.getBoolean("data_triase_igd");
                        akses01.master_triase_skala1=rs2.getBoolean("master_triase_skala1");
                        akses01.master_triase_skala2=rs2.getBoolean("master_triase_skala2");
                        akses01.master_triase_skala3=rs2.getBoolean("master_triase_skala3");
                        akses01.master_triase_skala4=rs2.getBoolean("master_triase_skala4");
                        akses01.master_triase_skala5=rs2.getBoolean("master_triase_skala5");
                        akses01.master_triase_pemeriksaan=rs2.getBoolean("master_triase_pemeriksaan");
                        akses01.master_triase_macamkasus=rs2.getBoolean("master_triase_macamkasus");
                        akses01.rekap_permintaan_diet=rs2.getBoolean("rekap_permintaan_diet");
                        akses01.daftar_pasien_ranap=rs2.getBoolean("daftar_pasien_ranap");
                        akses01.daftar_pasien_ranaptni=rs2.getBoolean("daftar_pasien_ranaptni");
                        akses01.pengajuan_asetinventaris=rs2.getBoolean("pengajuan_asetinventaris");
                        akses01.item_apotek_jenis=rs2.getBoolean("item_apotek_jenis");
                        akses01.item_apotek_kategori=rs2.getBoolean("item_apotek_kategori");
                        akses01.item_apotek_golongan=rs2.getBoolean("item_apotek_golongan");
                        akses01.item_apotek_industrifarmasi=rs2.getBoolean("item_apotek_industrifarmasi");
                        akses01.obat10_terbanyak_poli=rs2.getBoolean("10_obat_terbanyak_poli");
                        akses01.grafik_pengajuan_aset_urgensi=rs2.getBoolean("grafik_pengajuan_aset_urgensi");
                        akses01.grafik_pengajuan_aset_status=rs2.getBoolean("grafik_pengajuan_aset_status");
                        akses01.grafik_pengajuan_aset_departemen=rs2.getBoolean("grafik_pengajuan_aset_departemen");
                        akses01.rekap_pengajuan_aset_departemen=rs2.getBoolean("rekap_pengajuan_aset_departemen");
                        akses01.grafik_kelompok_jabatanpegawai=rs2.getBoolean("grafik_kelompok_jabatanpegawai");
                        akses01.grafik_resiko_kerjapegawai=rs2.getBoolean("grafik_resiko_kerjapegawai");
                        akses01.grafik_emergency_indexpegawai=rs2.getBoolean("grafik_emergency_indexpegawai");
                        akses01.grafik_inventaris_ruang=rs2.getBoolean("grafik_inventaris_ruang");
                        akses01.harian_HAIs2=rs2.getBoolean("harian_HAIs2");
                        akses01.grafik_inventaris_jenis=rs2.getBoolean("grafik_inventaris_jenis");
                        akses01.data_resume_pasien=rs2.getBoolean("data_resume_pasien");
                        akses01.perkiraan_biaya_ranap=rs2.getBoolean("perkiraan_biaya_ranap");
                        akses01.rekap_obat_poli=rs2.getBoolean("rekap_obat_poli");
                        akses01.rekap_obat_pasien=rs2.getBoolean("rekap_obat_pasien");
                        akses01.grafik_HAIs_pasienbangsal=rs2.getBoolean("grafik_HAIs_pasienbangsal");
                        akses01.grafik_HAIs_pasienbulan=rs2.getBoolean("grafik_HAIs_pasienbulan");
                        akses01.permintaan_perbaikan_inventaris=rs2.getBoolean("permintaan_perbaikan_inventaris");
                        akses01.grafik_HAIs_laju_vap=rs2.getBoolean("grafik_HAIs_laju_vap");
                        akses01.grafik_HAIs_laju_iad=rs2.getBoolean("grafik_HAIs_laju_iad");
                        akses01.grafik_HAIs_laju_pleb=rs2.getBoolean("grafik_HAIs_laju_pleb");
                        akses01.grafik_HAIs_laju_isk=rs2.getBoolean("grafik_HAIs_laju_isk");
                        akses01.grafik_HAIs_laju_ilo=rs2.getBoolean("grafik_HAIs_laju_ilo");
                        akses01.grafik_HAIs_laju_hap=rs2.getBoolean("grafik_HAIs_laju_hap");
                        akses01.inhealth_mapping_poli=rs2.getBoolean("inhealth_mapping_poli");
                        akses01.inhealth_mapping_dokter=rs2.getBoolean("inhealth_mapping_dokter");
                        akses01.inhealth_mapping_tindakan_ralan=rs2.getBoolean("inhealth_mapping_tindakan_ralan");
                        akses01.inhealth_mapping_tindakan_ranap=rs2.getBoolean("inhealth_mapping_tindakan_ranap");
                        akses01.inhealth_mapping_tindakan_radiologi=rs2.getBoolean("inhealth_mapping_tindakan_radiologi");
                        akses01.inhealth_mapping_tindakan_laborat=rs2.getBoolean("inhealth_mapping_tindakan_laborat");
                        akses01.inhealth_mapping_tindakan_operasi=rs2.getBoolean("inhealth_mapping_tindakan_operasi");
                        akses01.hibah_obat_bhp=rs2.getBoolean("hibah_obat_bhp");
                        akses01.asal_hibah=rs2.getBoolean("asal_hibah");
                        akses01.asuhan_gizi=rs2.getBoolean("asuhan_gizi");
                        akses01.inhealth_kirim_tagihan=rs2.getBoolean("inhealth_kirim_tagihan");
                        akses01.sirkulasi_obat4=rs2.getBoolean("sirkulasi_obat4");
                        akses01.sirkulasi_obat5=rs2.getBoolean("sirkulasi_obat5");
                        akses01.sirkulasi_non_medis2=rs2.getBoolean("sirkulasi_non_medis2");
                        akses01.monitoring_asuhan_gizi=rs2.getBoolean("monitoring_asuhan_gizi");
                        akses01.penerimaan_obat_perbulan=rs2.getBoolean("penerimaan_obat_perbulan");
                        akses01.rekap_kunjungan=rs2.getBoolean("rekap_kunjungan");
                        akses01.surat_sakit=rs2.getBoolean("surat_sakit");
                        akses01.penilaian_awal_keperawatan_ralan=rs2.getBoolean("penilaian_awal_keperawatan_ralan");
                        akses01.permintaan_diet=rs2.getBoolean("permintaan_diet");
                        akses01.master_masalah_keperawatan=rs2.getBoolean("master_masalah_keperawatan");
                        akses01.pengajuan_cuti=rs2.getBoolean("pengajuan_cuti");
                        akses01.kedatangan_pasien=rs2.getBoolean("kedatangan_pasien");
                        akses01.utd_pendonor=rs2.getBoolean("utd_pendonor");
                        akses01.toko_suplier=rs2.getBoolean("toko_suplier");
                        akses01.toko_jenis=rs2.getBoolean("toko_jenis");
                        akses01.toko_set_harga=rs2.getBoolean("toko_set_harga");
                        akses01.toko_barang=rs2.getBoolean("toko_barang");
                        akses01.penagihan_piutang_pasien=rs2.getBoolean("penagihan_piutang_pasien");
                        akses01.akun_penagihan_piutang=rs2.getBoolean("akun_penagihan_piutang");
                        akses01.stok_opname_toko=rs2.getBoolean("stok_opname_toko");
                        akses01.toko_riwayat_barang=rs2.getBoolean("toko_riwayat_barang");
                        akses01.toko_surat_pemesanan=rs2.getBoolean("toko_surat_pemesanan");
                        akses01.toko_pengajuan_barang=rs2.getBoolean("toko_pengajuan_barang");
                        akses01.toko_penerimaan_barang=rs2.getBoolean("toko_penerimaan_barang");
                        akses01.toko_pengadaan_barang=rs2.getBoolean("toko_pengadaan_barang");
                        akses01.toko_hutang=rs2.getBoolean("toko_hutang");
                        akses01.toko_bayar_pemesanan=rs2.getBoolean("toko_bayar_pemesanan");
                        akses01.toko_member=rs2.getBoolean("toko_member");
                        akses01.toko_penjualan=rs2.getBoolean("toko_penjualan");
                        akses01.registrasi_poli_per_tanggal=rs2.getBoolean("registrasi_poli_per_tanggal");
                        akses01.toko_piutang=rs2.getBoolean("toko_piutang");
                        akses01.toko_retur_beli=rs2.getBoolean("toko_retur_beli");
                        akses01.ipsrs_returbeli=rs2.getBoolean("ipsrs_returbeli");
                        akses01.ipsrs_riwayat_barang=rs2.getBoolean("ipsrs_riwayat_barang");
                        akses01.pasien_corona=rs2.getBoolean("pasien_corona");
                        akses01.toko_pendapatan_harian=rs2.getBoolean("toko_pendapatan_harian");
                        akses01.diagnosa_pasien_corona=rs2.getBoolean("diagnosa_pasien_corona");
                        akses01.perawatan_pasien_corona=rs2.getBoolean("perawatan_pasien_corona");
                        akses01.penilaian_awal_keperawatan_gigi=rs2.getBoolean("penilaian_awal_keperawatan_gigi");
                        akses01.master_masalah_keperawatan_gigi=rs2.getBoolean("master_masalah_keperawatan_gigi");
                        akses01.toko_bayar_piutang=rs2.getBoolean("toko_bayar_piutang");
                        akses01.toko_piutang_harian=rs2.getBoolean("toko_piutang_harian");
                        akses01.toko_penjualan_harian=rs2.getBoolean("toko_penjualan_harian");
                        akses01.deteksi_corona=rs2.getBoolean("deteksi_corona");
                        akses01.penilaian_awal_keperawatan_kebidanan=rs2.getBoolean("penilaian_awal_keperawatan_kebidanan");
                        akses01.pengumuman_epasien=rs2.getBoolean("pengumuman_epasien");
                        akses01.surat_hamil=rs2.getBoolean("surat_hamil");
                        akses01.set_tarif_online=rs2.getBoolean("set_tarif_online");
                        akses01.booking_periksa=rs2.getBoolean("booking_periksa");
                        akses01.toko_sirkulasi=rs2.getBoolean("toko_sirkulasi");
                        akses01.toko_retur_jual=rs2.getBoolean("toko_retur_jual");
                        akses01.toko_retur_piutang=rs2.getBoolean("toko_retur_piutang");
                        akses01.toko_sirkulasi2=rs2.getBoolean("toko_sirkulasi2");
                        akses01.toko_keuntungan_barang=rs2.getBoolean("toko_keuntungan_barang");
                        akses01.zis_pengeluaran_penerima_dankes=rs2.getBoolean("zis_pengeluaran_penerima_dankes");
                        akses01.zis_penghasilan_penerima_dankes=rs2.getBoolean("zis_penghasilan_penerima_dankes");
                        akses01.zis_ukuran_rumah_penerima_dankes=rs2.getBoolean("zis_ukuran_rumah_penerima_dankes");
                        akses01.zis_dinding_rumah_penerima_dankes=rs2.getBoolean("zis_dinding_rumah_penerima_dankes");
                        akses01.zis_lantai_rumah_penerima_dankes=rs2.getBoolean("zis_lantai_rumah_penerima_dankes");
                        akses01.zis_atap_rumah_penerima_dankes=rs2.getBoolean("zis_atap_rumah_penerima_dankes");
                        akses01.zis_kepemilikan_rumah_penerima_dankes=rs2.getBoolean("zis_kepemilikan_rumah_penerima_dankes");
                        akses01.zis_kamar_mandi_penerima_dankes=rs2.getBoolean("zis_kamar_mandi_penerima_dankes");
                        akses01.zis_dapur_rumah_penerima_dankes=rs2.getBoolean("zis_dapur_rumah_penerima_dankes");
                        akses01.zis_kursi_rumah_penerima_dankes=rs2.getBoolean("zis_kursi_rumah_penerima_dankes");
                        akses01.zis_kategori_phbs_penerima_dankes=rs2.getBoolean("zis_kategori_phbs_penerima_dankes");
                        akses01.zis_elektronik_penerima_dankes=rs2.getBoolean("zis_elektronik_penerima_dankes");
                        akses01.zis_ternak_penerima_dankes=rs2.getBoolean("zis_ternak_penerima_dankes");
                        akses01.zis_jenis_simpanan_penerima_dankes=rs2.getBoolean("zis_jenis_simpanan_penerima_dankes");
                        akses01.penilaian_awal_keperawatan_anak=rs2.getBoolean("penilaian_awal_keperawatan_anak");
                        akses01.zis_kategori_asnaf_penerima_dankes=rs2.getBoolean("zis_kategori_asnaf_penerima_dankes");
                        akses01.master_masalah_keperawatan_anak=rs2.getBoolean("master_masalah_keperawatan_anak");
                        akses01.master_imunisasi=rs2.getBoolean("master_imunisasi");
                        akses01.zis_patologis_penerima_dankes=rs2.getBoolean("zis_patologis_penerima_dankes");
                        akses01.pcare_cek_kartu=rs2.getBoolean("pcare_cek_kartu");
                        akses01.surat_bebas_narkoba=rs2.getBoolean("surat_bebas_narkoba");
                        akses01.surat_keterangan_covid=rs2.getBoolean("surat_keterangan_covid");
                        akses01.pemakaian_air_tanah=rs2.getBoolean("pemakaian_air_tanah");
                        akses01.grafik_air_tanah_pertanggal=rs2.getBoolean("grafik_air_tanah_pertanggal");
                        akses01.grafik_air_tanah_perbulan=rs2.getBoolean("grafik_air_tanah_perbulan");
                        akses01.lama_pelayanan_poli=rs2.getBoolean("lama_pelayanan_poli");
                        akses01.hemodialisa=rs2.getBoolean("hemodialisa");
                        akses01.grafik_harian_hemodialisa=rs2.getBoolean("grafik_harian_hemodialisa");
                        akses01.grafik_bulanan_hemodialisa=rs2.getBoolean("grafik_bulanan_hemodialisa");
                        akses01.grafik_tahunan_hemodialisa=rs2.getBoolean("grafik_tahunan_hemodialisa");
                        akses01.grafik_bulanan_meninggal=rs2.getBoolean("grafik_bulanan_meninggal");
                        akses01.laporan_tahunan_irj=rs2.getBoolean("laporan_tahunan_irj");
                        akses01.perbaikan_inventaris=rs2.getBoolean("perbaikan_inventaris");
                        akses01.surat_cuti_hamil=rs2.getBoolean("surat_cuti_hamil");
                        akses01.permintaan_stok_obat_pasien=rs2.getBoolean("permintaan_stok_obat_pasien");
                        akses01.pemeliharaan_inventaris=rs2.getBoolean("pemeliharaan_inventaris");
                        akses01.klasifikasi_pasien_ranap=rs2.getBoolean("klasifikasi_pasien_ranap");
                        akses01.bulanan_klasifikasi_pasien_ranap=rs2.getBoolean("bulanan_klasifikasi_pasien_ranap");
                        akses01.harian_klasifikasi_pasien_ranap=rs2.getBoolean("harian_klasifikasi_pasien_ranap");
                        akses01.klasifikasi_pasien_perbangsal=rs2.getBoolean("klasifikasi_pasien_perbangsal");
                        akses01.soap_perawatan=rs2.getBoolean("soap_perawatan");
                        akses01.klaim_rawat_jalan=rs2.getBoolean("klaim_rawat_jalan");
                        akses01.skrining_gizi=rs2.getBoolean("skrining_gizi");
                        akses01.lama_penyiapan_rm=rs2.getBoolean("lama_penyiapan_rm");
                        akses01.dosis_radiologi=rs2.getBoolean("dosis_radiologi");
                        akses01.demografi_umur_kunjungan=rs2.getBoolean("demografi_umur_kunjungan");
                        akses01.jam_diet_pasien=rs2.getBoolean("jam_diet_pasien");
                        akses01.rvu_bpjs=rs2.getBoolean("rvu_bpjs");
                        akses01.verifikasi_penerimaan_farmasi=rs2.getBoolean("verifikasi_penerimaan_farmasi");
                        akses01.verifikasi_penerimaan_logistik=rs2.getBoolean("verifikasi_penerimaan_logistik");
                        akses01.pemeriksaan_lab_pa=rs2.getBoolean("pemeriksaan_lab_pa");
                        akses01.ringkasan_pengajuan_obat=rs2.getBoolean("ringkasan_pengajuan_obat");
                        akses01.ringkasan_pemesanan_obat=rs2.getBoolean("ringkasan_pemesanan_obat");
                        akses01.ringkasan_pengadaan_obat=rs2.getBoolean("ringkasan_pengadaan_obat");
                        akses01.ringkasan_penerimaan_obat=rs2.getBoolean("ringkasan_penerimaan_obat");
                        akses01.ringkasan_hibah_obat=rs2.getBoolean("ringkasan_hibah_obat");
                        akses01.ringkasan_penjualan_obat=rs2.getBoolean("ringkasan_penjualan_obat");
                        akses01.ringkasan_beri_obat=rs2.getBoolean("ringkasan_beri_obat");
                        akses01.ringkasan_piutang_obat=rs2.getBoolean("ringkasan_piutang_obat");
                        akses01.ringkasan_stok_keluar_obat=rs2.getBoolean("ringkasan_stok_keluar_obat");
                        akses01.ringkasan_retur_suplier_obat=rs2.getBoolean("ringkasan_retur_suplier_obat");
                        akses01.ringkasan_retur_pembeli_obat=rs2.getBoolean("ringkasan_retur_pembeli_obat");
                        akses01.penilaian_awal_keperawatan_ranapkebidanan=rs2.getBoolean("penilaian_awal_keperawatan_ranapkebidanan");
                        akses01.ringkasan_pengajuan_nonmedis=rs2.getBoolean("ringkasan_pengajuan_nonmedis");
                        akses01.ringkasan_pemesanan_nonmedis=rs2.getBoolean("ringkasan_pemesanan_nonmedis");
                        akses01.ringkasan_pengadaan_nonmedis=rs2.getBoolean("ringkasan_pengadaan_nonmedis");
                        akses01.ringkasan_penerimaan_nonmedis=rs2.getBoolean("ringkasan_penerimaan_nonmedis");
                        akses01.ringkasan_stokkeluar_nonmedis=rs2.getBoolean("ringkasan_stokkeluar_nonmedis");
                        akses01.ringkasan_returbeli_nonmedis=rs2.getBoolean("ringkasan_returbeli_nonmedis");
                        akses01.omset_penerimaan=rs2.getBoolean("omset_penerimaan");
                        akses01.validasi_penagihan_piutang=rs2.getBoolean("validasi_penagihan_piutang");
                        akses01.permintaan_ranap=rs2.getBoolean("permintaan_ranap");
                        akses01.bpjs_diagnosa_prb=rs2.getBoolean("bpjs_diagnosa_prb");
                        akses01.bpjs_obat_prb=rs2.getBoolean("bpjs_obat_prb");
                        akses01.bpjs_surat_kontrol=rs2.getBoolean("bpjs_surat_kontrol");
                        akses01.penggunaan_bhp_ok=rs2.getBoolean("penggunaan_bhp_ok");
                        akses01.surat_keterangan_rawat_inap=rs2.getBoolean("surat_keterangan_rawat_inap");
                        akses01.surat_keterangan_sehat=rs2.getBoolean("surat_keterangan_sehat");
                        akses01.pendapatan_per_carabayar=rs2.getBoolean("pendapatan_per_carabayar");
                        akses01.akun_host_to_host_bank_jateng=rs2.getBoolean("akun_host_to_host_bank_jateng");
                        akses01.pembayaran_bank_jateng=rs2.getBoolean("pembayaran_bank_jateng");
                        akses01.bpjs_surat_pri=rs2.getBoolean("bpjs_surat_pri");
                        akses01.ringkasan_tindakan=rs2.getBoolean("ringkasan_tindakan");
                        akses01.lama_pelayanan_pasien=rs2.getBoolean("lama_pelayanan_pasien");
                        akses01.surat_sakit_pihak_2=rs2.getBoolean("surat_sakit_pihak_2");
                        akses01.tagihan_hutang_obat=rs2.getBoolean("tagihan_hutang_obat");
                        akses01.referensi_mobilejkn_bpjs=rs2.getBoolean("referensi_mobilejkn_bpjs");
                        akses01.batal_pendaftaran_mobilejkn_bpjs=rs2.getBoolean("batal_pendaftaran_mobilejkn_bpjs");
                        akses01.lama_operasi=rs2.getBoolean("lama_operasi");
                        akses01.grafik_inventaris_kategori=rs2.getBoolean("grafik_inventaris_kategori");
                        akses01.grafik_inventaris_merk=rs2.getBoolean("grafik_inventaris_merk");
                        akses01.grafik_inventaris_produsen=rs2.getBoolean("grafik_inventaris_produsen");
                        akses01.pengembalian_deposit_pasien=rs2.getBoolean("pengembalian_deposit_pasien");
                        akses01.validasi_tagihan_hutang_obat=rs2.getBoolean("validasi_tagihan_hutang_obat");
                        akses01.piutang_obat_belum_lunas=rs2.getBoolean("piutang_obat_belum_lunas");
                        akses01.integrasi_briapi=rs2.getBoolean("integrasi_briapi");
                        akses01.pengadaan_aset_inventaris=rs2.getBoolean("pengadaan_aset_inventaris");
                        akses01.akun_aset_inventaris=rs2.getBoolean("akun_aset_inventaris");
                        akses01.suplier_inventaris=rs2.getBoolean("suplier_inventaris");
                        akses01.penerimaan_aset_inventaris=rs2.getBoolean("penerimaan_aset_inventaris");
                        akses01.bayar_pemesanan_iventaris=rs2.getBoolean("bayar_pemesanan_iventaris");
                        akses01.hutang_aset_inventaris=rs2.getBoolean("hutang_aset_inventaris");
                        akses01.hibah_aset_inventaris=rs2.getBoolean("hibah_aset_inventaris");
                        akses01.titip_faktur_non_medis=rs2.getBoolean("titip_faktur_non_medis");
                        akses01.validasi_tagihan_non_medis=rs2.getBoolean("validasi_tagihan_non_medis");
                        akses01.titip_faktur_aset=rs2.getBoolean("titip_faktur_aset");
                        akses01.validasi_tagihan_aset=rs2.getBoolean("validasi_tagihan_aset");
                        akses01.hibah_non_medis=rs2.getBoolean("hibah_non_medis");
                        akses01.pcare_alasan_tacc=rs2.getBoolean("pcare_alasan_tacc");
                        akses01.resep_luar=rs2.getBoolean("resep_luar");
                        akses01.surat_bebas_tbc=rs2.getBoolean("surat_bebas_tbc");
                        akses01.surat_buta_warna=rs2.getBoolean("surat_buta_warna");
                        akses01.surat_bebas_tato=rs2.getBoolean("surat_bebas_tato");
                        akses01.surat_kewaspadaan_kesehatan=rs2.getBoolean("surat_kewaspadaan_kesehatan");
                        akses01.grafik_porsidiet_pertanggal=rs2.getBoolean("grafik_porsidiet_pertanggal");
                        akses01.grafik_porsidiet_perbulan=rs2.getBoolean("grafik_porsidiet_perbulan");
                        akses01.grafik_porsidiet_pertahun=rs2.getBoolean("grafik_porsidiet_pertahun");
                        akses01.grafik_porsidiet_perbangsal=rs2.getBoolean("grafik_porsidiet_perbangsal");
                        akses01.penilaian_awal_medis_ralan=rs2.getBoolean("penilaian_awal_medis_ralan");
                        akses01.master_masalah_keperawatan_mata=rs2.getBoolean("master_masalah_keperawatan_mata");
                        akses01.penilaian_awal_keperawatan_mata=rs2.getBoolean("penilaian_awal_keperawatan_mata");
                        akses01.penilaian_awal_medis_ranap=rs2.getBoolean("penilaian_awal_medis_ranap");
                        akses01.penilaian_awal_medis_ranap_kebidanan=rs2.getBoolean("penilaian_awal_medis_ranap_kebidanan");
                        akses01.penilaian_awal_medis_ralan_kebidanan=rs2.getBoolean("penilaian_awal_medis_ralan_kebidanan");
                        akses01.penilaian_awal_medis_igd=rs2.getBoolean("penilaian_awal_medis_igd");
                        akses01.penilaian_awal_medis_ralan_anak=rs2.getBoolean("penilaian_awal_medis_ralan_anak");
                        akses01.bpjs_referensi_poli_hfis=rs2.getBoolean("bpjs_referensi_poli_hfis");
                        akses01.bpjs_referensi_dokter_hfis=rs2.getBoolean("bpjs_referensi_dokter_hfis");
                        akses01.bpjs_referensi_jadwal_hfis=rs2.getBoolean("bpjs_referensi_jadwal_hfis");
                        akses01.penilaian_fisioterapi=rs2.getBoolean("penilaian_fisioterapi");
                        akses01.bpjs_program_prb=rs2.getBoolean("bpjs_program_prb");
                        akses01.bpjs_suplesi_jasaraharja=rs2.getBoolean("bpjs_suplesi_jasaraharja");
                        akses01.bpjs_data_induk_kecelakaan=rs2.getBoolean("bpjs_data_induk_kecelakaan");
                        akses01.bpjs_sep_internal=rs2.getBoolean("bpjs_sep_internal");
                        akses01.bpjs_klaim_jasa_raharja=rs2.getBoolean("bpjs_klaim_jasa_raharja");
                        akses01.bpjs_daftar_finger_print=rs2.getBoolean("bpjs_daftar_finger_print");
                        akses01.bpjs_rujukan_khusus=rs2.getBoolean("bpjs_rujukan_khusus");
                        akses01.pemeliharaan_gedung=rs2.getBoolean("pemeliharaan_gedung");
                        akses01.grafik_perbaikan_inventaris_pertanggal=rs2.getBoolean("grafik_perbaikan_inventaris_pertanggal");
                        akses01.grafik_perbaikan_inventaris_perbulan=rs2.getBoolean("grafik_perbaikan_inventaris_perbulan");
                        akses01.grafik_perbaikan_inventaris_pertahun=rs2.getBoolean("grafik_perbaikan_inventaris_pertahun");
                        akses01.grafik_perbaikan_inventaris_perpelaksana_status=rs2.getBoolean("grafik_perbaikan_inventaris_perpelaksana_status");
                        akses01.penilaian_mcu=rs2.getBoolean("penilaian_mcu");
                        akses01.peminjam_piutang=rs2.getBoolean("peminjam_piutang");
                        akses01.piutang_lainlain=rs2.getBoolean("piutang_lainlain");
                        akses01.cara_bayar=rs2.getBoolean("cara_bayar");
                        akses01.audit_kepatuhan_apd=rs2.getBoolean("audit_kepatuhan_apd");
                        akses01.bpjs_task_id=rs2.getBoolean("bpjs_task_id");
                        akses01.bayar_piutang_lain=rs2.getBoolean("bayar_piutang_lain");
                        akses01.pembayaran_akun_bayar4=rs2.getBoolean("pembayaran_akun_bayar4");
                        akses01.stok_akhir_farmasi_pertanggal=rs2.getBoolean("stok_akhir_farmasi_pertanggal");
                        akses01.riwayat_kamar_pasien=rs2.getBoolean("riwayat_kamar_pasien");
                        akses01.uji_fungsi_kfr=rs2.getBoolean("uji_fungsi_kfr");
                        akses01.hapus_berkas_digital_perawatan=rs2.getBoolean("hapus_berkas_digital_perawatan");
                        akses01.kategori_pengeluaran_harian=rs2.getBoolean("kategori_pengeluaran_harian");
                        akses01.kategori_pemasukan_lain=rs2.getBoolean("kategori_pemasukan_lain");
                        akses01.pembayaran_akun_bayar5=rs2.getBoolean("pembayaran_akun_bayar5");
                        akses01.ruang_ok=rs2.getBoolean("ruang_ok");
                        akses01.jasa_tindakan_pasien=rs2.getBoolean("jasa_tindakan_pasien");
                        akses01.telaah_resep=rs2.getBoolean("telaah_resep");
                        akses01.permintaan_resep_pulang=rs2.getBoolean("permintaan_resep_pulang");
                        akses01.rekap_jm_dokter=rs2.getBoolean("rekap_jm_dokter");
                        akses01.status_data_rm=rs2.getBoolean("status_data_rm");
                        akses01.ubah_petugas_lab_pk=rs2.getBoolean("ubah_petugas_lab_pk");
                        akses01.ubah_petugas_lab_pa=rs2.getBoolean("ubah_petugas_lab_pa");
                        akses01.ubah_petugas_radiologi=rs2.getBoolean("ubah_petugas_radiologi");
                        akses01.gabung_norawat=rs2.getBoolean("gabung_norawat");
                        akses01.gabung_rm=rs2.getBoolean("gabung_rm");
                        akses01.ringkasan_biaya_obat_pasien_pertanggal=rs2.getBoolean("ringkasan_biaya_obat_pasien_pertanggal");
                        akses01.master_masalah_keperawatan_igd=rs2.getBoolean("master_masalah_keperawatan_igd");
                        akses01.penilaian_awal_keperawatan_igd=rs2.getBoolean("penilaian_awal_keperawatan_igd");
                        akses01.bpjs_referensi_dpho_apotek=rs2.getBoolean("bpjs_referensi_dpho_apotek");
                        akses01.bpjs_referensi_poli_apotek=rs2.getBoolean("bpjs_referensi_poli_apotek");
                        akses01.bayar_jm_dokter=rs2.getBoolean("bayar_jm_dokter");
                        akses01.bpjs_referensi_faskes_apotek=rs2.getBoolean("bpjs_referensi_faskes_apotek");
                        akses01.bpjs_referensi_spesialistik_apotek=rs2.getBoolean("bpjs_referensi_spesialistik_apotek");
                        akses01.pembayaran_briva=rs2.getBoolean("pembayaran_briva");
                        akses01.penilaian_awal_keperawatan_ranap=rs2.getBoolean("penilaian_awal_keperawatan_ranap");
                        akses01.nilai_penerimaan_vendor_farmasi_perbulan=rs2.getBoolean("nilai_penerimaan_vendor_farmasi_perbulan");
                        akses01.akun_bayar_hutang=rs2.getBoolean("akun_bayar_hutang");
                        akses01.master_rencana_keperawatan=rs2.getBoolean("master_rencana_keperawatan");
                        akses01.laporan_tahunan_igd=rs2.getBoolean("laporan_tahunan_igd");
                        akses01.obat_bhp_tidakbergerak=rs2.getBoolean("obat_bhp_tidakbergerak");
                        akses01.ringkasan_hutang_vendor_farmasi=rs2.getBoolean("ringkasan_hutang_vendor_farmasi");
                        akses01.nilai_penerimaan_vendor_nonmedis_perbulan=rs2.getBoolean("nilai_penerimaan_vendor_nonmedis_perbulan");
                        akses01.ringkasan_hutang_vendor_nonmedis=rs2.getBoolean("ringkasan_hutang_vendor_nonmedis");
                        akses01.anggota_polri_dirawat=rs2.getBoolean("anggota_polri_dirawat");
                        akses01.daftar_pasien_ranap_polri=rs2.getBoolean("daftar_pasien_ranap_polri");
                        akses01.soap_ralan_polri=rs2.getBoolean("soap_ralan_polri");
                        akses01.soap_ranap_polri=rs2.getBoolean("soap_ranap_polri");
                        akses01.laporan_penyakit_polri=rs2.getBoolean("laporan_penyakit_polri");
                        akses01.master_rencana_keperawatan_anak=rs2.getBoolean("master_rencana_keperawatan_anak");
                        akses01.jumlah_pengunjung_ralan_polri=rs2.getBoolean("jumlah_pengunjung_ralan_polri");
                        akses01.catatan_observasi_igd=rs2.getBoolean("catatan_observasi_igd");
                        akses01.catatan_observasi_ranap=rs2.getBoolean("catatan_observasi_ranap");
                        akses01.catatan_observasi_ranap_kebidanan=rs2.getBoolean("catatan_observasi_ranap_kebidanan");
                        akses01.catatan_observasi_ranap_postpartum=rs2.getBoolean("catatan_observasi_ranap_postpartum");
                        akses01.penilaian_awal_medis_ralan_tht=rs2.getBoolean("penilaian_awal_medis_ralan_tht");
                        akses01.penilaian_psikologi=rs2.getBoolean("penilaian_psikologi");
                        akses01.audit_cuci_tangan_medis=rs2.getBoolean("audit_cuci_tangan_medis");
                        akses01.audit_pembuangan_limbah=rs2.getBoolean("audit_pembuangan_limbah");
                        akses01.ruang_audit_kepatuhan=rs2.getBoolean("ruang_audit_kepatuhan");
                        akses01.audit_pembuangan_benda_tajam=rs2.getBoolean("audit_pembuangan_benda_tajam");
                        akses01.audit_penanganan_darah=rs2.getBoolean("audit_penanganan_darah");
                        akses01.audit_pengelolaan_linen_kotor=rs2.getBoolean("audit_pengelolaan_linen_kotor");
                        akses01.audit_penempatan_pasien=rs2.getBoolean("audit_penempatan_pasien");
                        akses01.audit_kamar_jenazah=rs2.getBoolean("audit_kamar_jenazah");
                        akses01.audit_bundle_iadp=rs2.getBoolean("audit_bundle_iadp");
                        akses01.audit_bundle_ido=rs2.getBoolean("audit_bundle_ido");
                        akses01.audit_fasilitas_kebersihan_tangan=rs2.getBoolean("audit_fasilitas_kebersihan_tangan");
                        akses01.audit_fasilitas_apd=rs2.getBoolean("audit_fasilitas_apd");
                        akses01.audit_pembuangan_limbah_cair_infeksius=rs2.getBoolean("audit_pembuangan_limbah_cair_infeksius");
                        akses01.audit_sterilisasi_alat=rs2.getBoolean("audit_sterilisasi_alat");
                        akses01.penilaian_awal_medis_ralan_psikiatri=rs2.getBoolean("penilaian_awal_medis_ralan_psikiatri");
                        akses01.persetujuan_penolakan_tindakan=rs2.getBoolean("persetujuan_penolakan_tindakan");
                        akses01.audit_bundle_isk=rs2.getBoolean("audit_bundle_isk");
                        akses01.audit_bundle_plabsi=rs2.getBoolean("audit_bundle_plabsi");
                        akses01.audit_bundle_vap=rs2.getBoolean("audit_bundle_vap");
                        akses01.akun_host_to_host_bank_papua=rs2.getBoolean("akun_host_to_host_bank_papua");
                        akses01.pembayaran_bank_papua=rs2.getBoolean("pembayaran_bank_papua");
                        akses01.penilaian_awal_medis_ralan_penyakit_dalam=rs2.getBoolean("penilaian_awal_medis_ralan_penyakit_dalam");
                        akses01.penilaian_awal_medis_ralan_mata=rs2.getBoolean("penilaian_awal_medis_ralan_mata");
                        akses01.penilaian_awal_medis_ralan_neurologi=rs2.getBoolean("penilaian_awal_medis_ralan_neurologi");
                        akses01.sirkulasi_obat6=rs2.getBoolean("sirkulasi_obat6");
                        akses01.penilaian_awal_medis_ralan_orthopedi=rs2.getBoolean("penilaian_awal_medis_ralan_orthopedi");
                        akses01.penilaian_awal_medis_ralan_bedah=rs2.getBoolean("penilaian_awal_medis_ralan_bedah");
                        akses01.integrasi_khanza_health_services=rs2.getBoolean("integrasi_khanza_health_services");
                        akses01.soap_ralan_tni=rs2.getBoolean("soap_ralan_tni");
                        akses01.soap_ranap_tni=rs2.getBoolean("soap_ranap_tni");
                        akses01.jumlah_pengunjung_ralan_tni=rs2.getBoolean("jumlah_pengunjung_ralan_tni");
                        akses01.laporan_penyakit_tni=rs2.getBoolean("laporan_penyakit_tni");
                        akses01.catatan_keperawatan_ranap=rs2.getBoolean("catatan_keperawatan_ranap");
                        akses01.master_rencana_keperawatan_gigi=rs2.getBoolean("master_rencana_keperawatan_gigi");
                        akses01.master_rencana_keperawatan_mata=rs2.getBoolean("master_rencana_keperawatan_mata");
                        akses01.master_rencana_keperawatan_igd=rs2.getBoolean("master_rencana_keperawatan_igd");
                        akses01.master_masalah_keperawatan_psikiatri=rs2.getBoolean("master_masalah_keperawatan_psikiatri");
                        akses01.master_rencana_keperawatan_psikiatri=rs2.getBoolean("master_rencana_keperawatan_psikiatri");
                        akses01.penilaian_awal_keperawatan_psikiatri=rs2.getBoolean("penilaian_awal_keperawatan_psikiatri");
                        akses01.pemantauan_pews_anak=rs2.getBoolean("pemantauan_pews_anak");
                        akses01.surat_pulang_atas_permintaan_sendiri=rs2.getBoolean("surat_pulang_atas_permintaan_sendiri");
                        akses01.template_hasil_radiologi=rs2.getBoolean("template_hasil_radiologi");
                        akses01.laporan_bulanan_irj=rs2.getBoolean("laporan_bulanan_irj");
                        akses01.template_pemeriksaan=rs2.getBoolean("template_pemeriksaan");
                        akses01.pemeriksaan_lab_mb=rs2.getBoolean("pemeriksaan_lab_mb");
                        akses01.ubah_petugas_lab_mb=rs2.getBoolean("ubah_petugas_lab_mb");
                        akses01.penilaian_pre_operasi=rs2.getBoolean("penilaian_pre_operasi");
                        akses01.penilaian_pre_anestesi=rs2.getBoolean("penilaian_pre_anestesi");
                        akses01.perencanaan_pemulangan=rs2.getBoolean("perencanaan_pemulangan");
                        akses01.penilaian_lanjutan_resiko_jatuh_dewasa=rs2.getBoolean("penilaian_lanjutan_resiko_jatuh_dewasa");
                        akses01.penilaian_lanjutan_resiko_jatuh_anak=rs2.getBoolean("penilaian_lanjutan_resiko_jatuh_anak");
                        akses01.penilaian_awal_medis_ralan_geriatri=rs2.getBoolean("penilaian_awal_medis_ralan_geriatri");
                        akses01.penilaian_tambahan_pasien_geriatri=rs2.getBoolean("penilaian_tambahan_pasien_geriatri");
                        akses01.skrining_nutrisi_dewasa=rs2.getBoolean("skrining_nutrisi_dewasa");
                        akses01.skrining_nutrisi_lansia=rs2.getBoolean("skrining_nutrisi_lansia");
                        akses01.hasil_pemeriksaan_usg=rs2.getBoolean("hasil_pemeriksaan_usg");
                        akses01.skrining_nutrisi_anak=rs2.getBoolean("skrining_nutrisi_anak");
                        akses01.akun_host_to_host_bank_jabar=rs2.getBoolean("akun_host_to_host_bank_jabar");
                        akses01.pembayaran_bank_jabar=rs2.getBoolean("pembayaran_bank_jabar");
                        akses01.surat_pernyataan_pasien_umum=rs2.getBoolean("surat_pernyataan_pasien_umum");
                        akses01.konseling_farmasi=rs2.getBoolean("konseling_farmasi");
                        akses01.pelayanan_informasi_obat=rs2.getBoolean("pelayanan_informasi_obat");
                        akses01.jawaban_pio_apoteker=rs2.getBoolean("jawaban_pio_apoteker");
                        akses01.surat_persetujuan_umum=rs2.getBoolean("surat_persetujuan_umum");
                        akses01.transfer_pasien_antar_ruang=rs2.getBoolean("transfer_pasien_antar_ruang");
                        akses01.satu_sehat_referensi_dokter=rs2.getBoolean("satu_sehat_referensi_dokter");
                        akses01.satu_sehat_referensi_pasien=rs2.getBoolean("satu_sehat_referensi_pasien");
                        akses01.satu_sehat_mapping_departemen=rs2.getBoolean("satu_sehat_mapping_departemen");
                        akses01.satu_sehat_mapping_lokasi=rs2.getBoolean("satu_sehat_mapping_lokasi");
                        akses01.satu_sehat_kirim_encounter=rs2.getBoolean("satu_sehat_kirim_encounter");
                        akses01.catatan_cek_gds=rs2.getBoolean("catatan_cek_gds");
                        akses01.satu_sehat_kirim_condition=rs2.getBoolean("satu_sehat_kirim_condition");
                        akses01.checklist_pre_operasi=rs2.getBoolean("checklist_pre_operasi");
                        akses01.satu_sehat_kirim_observationttv=rs2.getBoolean("satu_sehat_kirim_observationttv");
                        akses01.signin_sebelum_anestesi=rs2.getBoolean("signin_sebelum_anestesi");
                        akses01.satu_sehat_kirim_procedure=rs2.getBoolean("satu_sehat_kirim_procedure");
                        akses01.operasi_per_bulan=rs2.getBoolean("operasi_per_bulan");
                        akses01.timeout_sebelum_insisi=rs2.getBoolean("timeout_sebelum_insisi");
                        akses01.signout_sebelum_menutup_luka=rs2.getBoolean("signout_sebelum_menutup_luka");
                        akses01.dapur_barang=rs2.getBoolean("dapur_barang");
                        akses01.dapur_opname=rs2.getBoolean("dapur_opname");
                        akses01.satu_sehat_mapping_vaksin=rs2.getBoolean("satu_sehat_mapping_vaksin");
                        akses01.dapur_suplier=rs2.getBoolean("dapur_suplier");
                        akses01.satu_sehat_kirim_Immunization=rs2.getBoolean("satu_sehat_kirim_Immunization");
                        akses01.checklist_post_operasi=rs2.getBoolean("checklist_post_operasi");
                        akses01.dapur_pembelian=rs2.getBoolean("dapur_pembelian");
                        akses01.dapur_stok_keluar=rs2.getBoolean("dapur_stok_keluar");
                        akses01.dapur_riwayat_barang=rs2.getBoolean("dapur_riwayat_barang");
                        akses01.permintaan_dapur=rs2.getBoolean("permintaan_dapur");
                        akses01.rekonsiliasi_obat=rs2.getBoolean("rekonsiliasi_obat");
                        akses01.biaya_pengadaan_dapur=rs2.getBoolean("biaya_pengadaan_dapur");
                        akses01.rekap_pengadaan_dapur=rs2.getBoolean("rekap_pengadaan_dapur");
                        akses01.kesling_limbah_b3medis_cair=rs2.getBoolean("kesling_limbah_b3medis_cair");
                        akses01.grafik_limbahb3cair_pertanggal=rs2.getBoolean("grafik_limbahb3cair_pertanggal");
                        akses01.grafik_limbahb3cair_perbulan=rs2.getBoolean("grafik_limbahb3cair_perbulan");
                        akses01.rekap_biaya_registrasi=rs2.getBoolean("rekap_biaya_registrasi");
                        akses01.konfirmasi_rekonsiliasi_obat=rs2.getBoolean("konfirmasi_rekonsiliasi_obat");
                        akses01.satu_sehat_kirim_clinicalimpression=rs2.getBoolean("satu_sehat_kirim_clinicalimpression");
                        akses01.penilaian_pasien_terminal=rs2.getBoolean("penilaian_pasien_terminal");
                        akses01.surat_persetujuan_rawat_inap=rs2.getBoolean("surat_persetujuan_rawat_inap");
                        akses01.monitoring_reaksi_tranfusi=rs2.getBoolean("monitoring_reaksi_tranfusi");
                        akses01.penilaian_korban_kekerasan=rs2.getBoolean("penilaian_korban_kekerasan");
                        akses01.penilaian_lanjutan_resiko_jatuh_lansia=rs2.getBoolean("penilaian_lanjutan_resiko_jatuh_lansia");
                        akses01.mpp_skrining=rs2.getBoolean("mpp_skrining");
                        akses01.penilaian_pasien_penyakit_menular=rs2.getBoolean("penilaian_pasien_penyakit_menular");
                        akses01.edukasi_pasien_keluarga_rj=rs2.getBoolean("edukasi_pasien_keluarga_rj");
                        akses01.pemantauan_pews_dewasa=rs2.getBoolean("pemantauan_pews_dewasa");
                        akses01.penilaian_tambahan_bunuh_diri=rs2.getBoolean("penilaian_tambahan_bunuh_diri");
                        akses01.bpjs_antrean_pertanggal=rs2.getBoolean("bpjs_antrean_pertanggal");
                        akses01.penilaian_tambahan_perilaku_kekerasan=rs2.getBoolean("penilaian_tambahan_perilaku_kekerasan");
                        akses01.penilaian_tambahan_beresiko_melarikan_diri=rs2.getBoolean("penilaian_tambahan_beresiko_melarikan_diri");
                        akses01.persetujuan_penundaan_pelayanan=rs2.getBoolean("persetujuan_penundaan_pelayanan");
                        akses01.sisa_diet_pasien=rs2.getBoolean("sisa_diet_pasien");
                        akses01.penilaian_awal_medis_ralan_bedah_mulut=rs2.getBoolean("penilaian_awal_medis_ralan_bedah_mulut");
                        akses01.penilaian_pasien_keracunan=rs2.getBoolean("penilaian_pasien_keracunan");
                        akses01.pemantauan_meows_obstetri=rs2.getBoolean("pemantauan_meows_obstetri");
                        akses01.catatan_adime_gizi=rs2.getBoolean("catatan_adime_gizi");
                        akses01.pengajuan_biaya=rs2.getBoolean("pengajuan_biaya");
                        akses01.penilaian_awal_keperawatan_ralan_geriatri=rs2.getBoolean("penilaian_awal_keperawatan_ralan_geriatri");
                        akses01.master_masalah_keperawatan_geriatri=rs2.getBoolean("master_masalah_keperawatan_geriatri");
                        akses01.master_rencana_keperawatan_geriatri=rs2.getBoolean("master_rencana_keperawatan_geriatri");
                        akses01.checklist_kriteria_masuk_hcu=rs2.getBoolean("checklist_kriteria_masuk_hcu");
                        akses01.checklist_kriteria_keluar_hcu=rs2.getBoolean("checklist_kriteria_keluar_hcu");
                        akses01.penilaian_risiko_dekubitus=rs2.getBoolean("penilaian_risiko_dekubitus");
                        akses01.master_menolak_anjuran_medis=rs2.getBoolean("master_menolak_anjuran_medis");
                        akses01.penolakan_anjuran_medis=rs2.getBoolean("penolakan_anjuran_medis");
                        akses01.laporan_tahunan_penolakan_anjuran_medis=rs2.getBoolean("laporan_tahunan_penolakan_anjuran_medis");
                        akses01.template_laporan_operasi=rs2.getBoolean("template_laporan_operasi");
                        akses01.hasil_tindakan_eswl=rs2.getBoolean("hasil_tindakan_eswl");
                        akses01.checklist_kriteria_masuk_icu=rs2.getBoolean("checklist_kriteria_masuk_icu");
                        akses01.checklist_kriteria_keluar_icu=rs2.getBoolean("checklist_kriteria_keluar_icu");
                        akses01.akses_dokter_lain_rawat_jalan=rs2.getBoolean("akses_dokter_lain_rawat_jalan");
                        akses01.follow_up_dbd=rs2.getBoolean("follow_up_dbd");
                        akses01.penilaian_risiko_jatuh_neonatus=rs2.getBoolean("penilaian_risiko_jatuh_neonatus");
                        akses01.persetujuan_pengajuan_biaya=rs2.getBoolean("persetujuan_pengajuan_biaya");
                        akses01.pemeriksaan_fisik_ralan_per_penyakit=rs2.getBoolean("pemeriksaan_fisik_ralan_per_penyakit");
                        akses01.penilaian_lanjutan_resiko_jatuh_geriatri=rs2.getBoolean("penilaian_lanjutan_resiko_jatuh_geriatri");
                        akses01.pemantauan_ews_neonatus=rs2.getBoolean("pemantauan_ews_neonatus");
                        akses01.validasi_persetujuan_pengajuan_biaya=rs2.getBoolean("validasi_persetujuan_pengajuan_biaya");
                        akses01.riwayat_perawatan_icare_bpjs=rs2.getBoolean("riwayat_perawatan_icare_bpjs");
                        akses01.rekap_pengajuan_biaya=rs2.getBoolean("rekap_pengajuan_biaya");
                        akses01.penilaian_awal_medis_ralan_kulit_kelamin=rs2.getBoolean("penilaian_awal_medis_ralan_kulit_kelamin");
                        akses01.akun_host_to_host_bank_mandiri=rs2.getBoolean("akun_host_to_host_bank_mandiri");
                        akses01.penilaian_medis_hemodialisa=rs2.getBoolean("penilaian_medis_hemodialisa");
                        akses01.penilaian_level_kecemasan_ranap_anak=rs2.getBoolean("penilaian_level_kecemasan_ranap_anak");
                        akses01.penilaian_lanjutan_resiko_jatuh_psikiatri=rs2.getBoolean("penilaian_lanjutan_resiko_jatuh_psikiatri");
                        akses01.penilaian_lanjutan_skrining_fungsional=rs2.getBoolean("penilaian_lanjutan_skrining_fungsional");
                        akses01.penilaian_medis_ralan_rehab_medik=rs2.getBoolean("penilaian_medis_ralan_rehab_medik");
                        akses01.laporan_anestesi=rs2.getBoolean("laporan_anestesi");
                        akses01.template_persetujuan_penolakan_tindakan=rs2.getBoolean("template_persetujuan_penolakan_tindakan");
                        akses01.penilaian_medis_ralan_gawat_darurat_psikiatri=rs2.getBoolean("penilaian_medis_ralan_gawat_darurat_psikiatri");
                        akses01.bpjs_referensi_setting_apotek=rs2.getBoolean("bpjs_referensi_setting_apotek");
                        akses01.bpjs_referensi_obat_apotek=rs2.getBoolean("bpjs_referensi_obat_apotek");
                        akses01.bpjs_mapping_obat_apotek=rs2.getBoolean("bpjs_mapping_obat_apotek");
                        akses01.pembayaran_bank_mandiri=rs2.getBoolean("pembayaran_bank_mandiri");
                        akses01.penilaian_ulang_nyeri=rs2.getBoolean("penilaian_ulang_nyeri");
                        akses01.penilaian_terapi_wicara=rs2.getBoolean("penilaian_terapi_wicara");
                        akses01.bpjs_obat_23hari_apotek=rs2.getBoolean("bpjs_obat_23hari_apotek");
                        akses01.pengkajian_restrain=rs2.getBoolean("pengkajian_restrain");
                        akses01.bpjs_kunjungan_sep_apotek=rs2.getBoolean("bpjs_kunjungan_sep_apotek");
                        akses01.bpjs_monitoring_klaim_apotek=rs2.getBoolean("bpjs_monitoring_klaim_apotek");
                        akses01.bpjs_daftar_pelayanan_obat_apotek=rs2.getBoolean("bpjs_daftar_pelayanan_obat_apotek");
                        akses01.penilaian_awal_medis_ralan_paru=rs2.getBoolean("penilaian_awal_medis_ralan_paru");
                        akses01.catatan_keperawatan_ralan=rs2.getBoolean("catatan_keperawatan_ralan");
                        akses01.catatan_persalinan=rs2.getBoolean("catatan_persalinan");
                        akses01.skor_aldrette_pasca_anestesi=rs2.getBoolean("skor_aldrette_pasca_anestesi");
                        akses01.skor_steward_pasca_anestesi=rs2.getBoolean("skor_steward_pasca_anestesi");
                        akses01.skor_bromage_pasca_anestesi=rs2.getBoolean("skor_bromage_pasca_anestesi");
                        akses01.penilaian_pre_induksi=rs2.getBoolean("penilaian_pre_induksi");
                        akses01.hasil_usg_urologi=rs2.getBoolean("hasil_usg_urologi");
                        akses01.hasil_usg_gynecologi=rs2.getBoolean("hasil_usg_gynecologi");
                        akses01.hasil_pemeriksaan_ekg=rs2.getBoolean("hasil_pemeriksaan_ekg");
                        akses01.hapus_edit_sep_bpjs=rs2.getBoolean("hapus_edit_sep_bpjs");
                        akses01.satu_sehat_kirim_diet=rs2.getBoolean("satu_sehat_kirim_diet");
                        akses01.satu_sehat_mapping_obat=rs2.getBoolean("satu_sehat_mapping_obat");
                        akses01.dapur_ringkasan_pembelian=rs2.getBoolean("dapur_ringkasan_pembelian");
                        akses01.satu_sehat_kirim_medication=rs2.getBoolean("satu_sehat_kirim_medication");
                        akses01.satu_sehat_kirim_medicationrequest=rs2.getBoolean("satu_sehat_kirim_medicationrequest");
                        akses01.penatalaksanaan_terapi_okupasi=rs2.getBoolean("penatalaksanaan_terapi_okupasi");
                        akses01.satu_sehat_kirim_medicationdispense=rs2.getBoolean("satu_sehat_kirim_medicationdispense");
                        akses01.hasil_usg_neonatus=rs2.getBoolean("hasil_usg_neonatus");
                        akses01.hasil_endoskopi_faring_laring=rs2.getBoolean("hasil_endoskopi_faring_laring");
                        akses01.satu_sehat_mapping_radiologi=rs2.getBoolean("satu_sehat_mapping_radiologi");
                        akses01.satu_sehat_kirim_servicerequest_radiologi=rs2.getBoolean("satu_sehat_kirim_servicerequest_radiologi");
                        akses01.hasil_endoskopi_hidung=rs2.getBoolean("hasil_endoskopi_hidung");
                        akses01.satu_sehat_kirim_specimen_radiologi=rs2.getBoolean("satu_sehat_kirim_specimen_radiologi");
                        akses01.master_masalah_keperawatan_neonatus=rs2.getBoolean("master_masalah_keperawatan_neonatus");
                        akses01.master_rencana_keperawatan_neonatus=rs2.getBoolean("master_rencana_keperawatan_neonatus");
                        akses01.penilaian_awal_keperawatan_ranap_neonatus=rs2.getBoolean("penilaian_awal_keperawatan_ranap_neonatus");
                        akses01.satu_sehat_kirim_observation_radiologi=rs2.getBoolean("satu_sehat_kirim_observation_radiologi");
                        akses01.satu_sehat_kirim_diagnosticreport_radiologi=rs2.getBoolean("satu_sehat_kirim_diagnosticreport_radiologi");
                        akses01.hasil_endoskopi_telinga=rs2.getBoolean("hasil_endoskopi_telinga");
                        akses01.satu_sehat_mapping_lab=rs2.getBoolean("satu_sehat_mapping_lab");
                        akses01.satu_sehat_kirim_servicerequest_lab=rs2.getBoolean("satu_sehat_kirim_servicerequest_lab");
                        akses01.satu_sehat_kirim_servicerequest_labmb=rs2.getBoolean("satu_sehat_kirim_servicerequest_labmb");
                        akses01.satu_sehat_kirim_specimen_lab=rs2.getBoolean("satu_sehat_kirim_specimen_lab");
                        akses01.satu_sehat_kirim_specimen_labmb=rs2.getBoolean("satu_sehat_kirim_specimen_labmb");
                        akses01.satu_sehat_kirim_observation_lab=rs2.getBoolean("satu_sehat_kirim_observation_lab");
                        akses01.satu_sehat_kirim_observation_labmb=rs2.getBoolean("satu_sehat_kirim_observation_labmb");
                        akses01.satu_sehat_kirim_diagnosticreport_lab=rs2.getBoolean("satu_sehat_kirim_diagnosticreport_lab");
                        akses01.satu_sehat_kirim_diagnosticreport_labmb=rs2.getBoolean("satu_sehat_kirim_diagnosticreport_labmb");
                        akses01.kepatuhan_kelengkapan_keselamatan_bedah=rs2.getBoolean("kepatuhan_kelengkapan_keselamatan_bedah");
                        akses01.nilai_piutang_perjenis_bayar_per_bulan=rs2.getBoolean("nilai_piutang_perjenis_bayar_per_bulan");
                        akses01.ringkasan_piutang_jenis_bayar=rs2.getBoolean("ringkasan_piutang_jenis_bayar");
                        akses01.penilaian_pasien_imunitas_rendah=rs2.getBoolean("penilaian_pasien_imunitas_rendah");
                        akses01.balance_cairan=rs2.getBoolean("balance_cairan");
                        akses01.catatan_observasi_chbp=rs2.getBoolean("catatan_observasi_chbp");
                        akses01.catatan_observasi_induksi_persalinan=rs2.getBoolean("catatan_observasi_induksi_persalinan");
                        akses01.skp_kategori_penilaian=rs2.getBoolean("skp_kategori_penilaian");
                        akses01.skp_kriteria_penilaian=rs2.getBoolean("skp_kriteria_penilaian");
                        akses01.skp_penilaian=rs2.getBoolean("skp_penilaian");
                        akses01.referensi_poli_mobilejknfktp=rs2.getBoolean("referensi_poli_mobilejknfktp");
                        akses01.referensi_dokter_mobilejknfktp=rs2.getBoolean("referensi_dokter_mobilejknfktp");
                        akses01.skp_rekapitulasi_penilaian=rs2.getBoolean("skp_rekapitulasi_penilaian");
                        akses01.pembayaran_pihak_ke3_bankmandiri=rs2.getBoolean("pembayaran_pihak_ke3_bankmandiri");
                        akses01.metode_pembayaran_bankmandiri=rs2.getBoolean("metode_pembayaran_bankmandiri");
                        akses01.bank_tujuan_transfer_bankmandiri=rs2.getBoolean("bank_tujuan_transfer_bankmandiri");
                        akses01.kodetransaksi_tujuan_transfer_bankmandiri=rs2.getBoolean("kodetransaksi_tujuan_transfer_bankmandiri");
                        akses01.konsultasi_medik=rs2.getBoolean("konsultasi_medik");
                        akses01.jawaban_konsultasi_medik=rs2.getBoolean("jawaban_konsultasi_medik");
                        akses01.pcare_cek_alergi=rs2.getBoolean("pcare_cek_alergi");
                        akses01.pcare_cek_prognosa=rs2.getBoolean("pcare_cek_prognosa");
                        akses01.data_sasaran_usiaproduktif=rs2.getBoolean("data_sasaran_usiaproduktif");
                        akses01.data_sasaran_usialansia=rs2.getBoolean("data_sasaran_usialansia");
                        akses01.skrining_perilaku_merokok_sekolah_remaja=rs2.getBoolean("skrining_perilaku_merokok_sekolah_remaja");
                        akses01.skrining_kekerasan_pada_perempuan=rs2.getBoolean("skrining_kekerasan_pada_perempuan");
                        akses01.skrining_obesitas=rs2.getBoolean("skrining_obesitas");
                        akses01.skrining_risiko_kanker_payudara=rs2.getBoolean("skrining_risiko_kanker_payudara");
                        akses01.skrining_risiko_kanker_paru=rs2.getBoolean("skrining_risiko_kanker_paru");
                        akses01.skrining_tbc=rs2.getBoolean("skrining_tbc");
                        akses01.skrining_kesehatan_gigi_mulut_remaja=rs2.getBoolean("skrining_kesehatan_gigi_mulut_remaja");
                        akses01.penilaian_awal_keperawatan_ranap_bayi=rs2.getBoolean("penilaian_awal_keperawatan_ranap_bayi");
                        akses01.booking_mcu_perusahaan=rs2.getBoolean("booking_mcu_perusahaan");
                        akses01.catatan_observasi_restrain_nonfarma=rs2.getBoolean("catatan_observasi_restrain_nonfarma");
                        akses01.catatan_observasi_ventilator=rs2.getBoolean("catatan_observasi_ventilator");
                        akses01.catatan_anestesi_sedasi=rs2.getBoolean("catatan_anestesi_sedasi");
                        akses01.skrining_puma=rs2.getBoolean("skrining_puma");
                        akses01.satu_sehat_kirim_careplan=rs2.getBoolean("satu_sehat_kirim_careplan");
                        akses01.satu_sehat_kirim_medicationstatement=rs2.getBoolean("satu_sehat_kirim_medicationstatement");
                        akses01.skrining_adiksi_nikotin=rs2.getBoolean("skrining_adiksi_nikotin");
                        akses01.skrining_thalassemia=rs2.getBoolean("skrining_thalassemia");
                        akses01.skrining_instrumen_sdq=rs2.getBoolean("skrining_instrumen_sdq");
                        akses01.skrining_instrumen_srq=rs2.getBoolean("skrining_instrumen_srq");
                        akses01.checklist_pemberian_fibrinolitik=rs2.getBoolean("checklist_pemberian_fibrinolitik");
                        akses01.skrining_kanker_kolorektal=rs2.getBoolean("skrining_kanker_kolorektal");
                        akses01.dapur_pemesanan=rs2.getBoolean("dapur_pemesanan");
                        akses01.bayar_pesan_dapur=rs2.getBoolean("bayar_pesan_dapur");
                        akses01.hutang_dapur=rs2.getBoolean("hutang_dapur");
                        akses01.titip_faktur_dapur=rs2.getBoolean("titip_faktur_dapur");
                        akses01.validasi_tagihan_dapur=rs2.getBoolean("validasi_tagihan_dapur");
                        akses01.surat_pemesanan_dapur=rs2.getBoolean("surat_pemesanan_dapur");
                        akses01.pengajuan_barang_dapur=rs2.getBoolean("pengajuan_barang_dapur");
                        akses01.dapur_returbeli=rs2.getBoolean("dapur_returbeli");
                        akses01.hibah_dapur=rs2.getBoolean("hibah_dapur");
                        akses01.ringkasan_penerimaan_dapur=rs2.getBoolean("ringkasan_penerimaan_dapur");
                        akses01.ringkasan_pengajuan_dapur=rs2.getBoolean("ringkasan_pengajuan_dapur");
                        akses01.ringkasan_pemesanan_dapur=rs2.getBoolean("ringkasan_pemesanan_dapur");
                        akses01.ringkasan_returbeli_dapur=rs2.getBoolean("ringkasan_returbeli_dapur");
                        akses01.ringkasan_stokkeluar_dapur=rs2.getBoolean("ringkasan_stokkeluar_dapur");
                        akses01.dapur_stokkeluar_pertanggal=rs2.getBoolean("dapur_stokkeluar_pertanggal");
                        akses01.sirkulasi_dapur=rs2.getBoolean("sirkulasi_dapur");
                        akses01.sirkulasi_dapur2=rs2.getBoolean("sirkulasi_dapur2");
                        akses01.verifikasi_penerimaan_dapur=rs2.getBoolean("verifikasi_penerimaan_dapur");
                        akses01.nilai_penerimaan_vendor_dapur_perbulan=rs2.getBoolean("nilai_penerimaan_vendor_dapur_perbulan");
                        akses01.ringkasan_hutang_vendor_dapur=rs2.getBoolean("ringkasan_hutang_vendor_dapur");
                        akses01.penilaian_psikologi_klinis=rs2.getBoolean("penilaian_psikologi_klinis");
                        akses01.penilaian_awal_medis_ranap_neonatus=rs2.getBoolean("penilaian_awal_medis_ranap_neonatus");
                        akses01.penilaian_derajat_dehidrasi=rs2.getBoolean("penilaian_derajat_dehidrasi");
                        akses01.ringkasan_jasa_tindakan_medis=rs2.getBoolean("ringkasan_jasa_tindakan_medis");
                        akses01.pendapatan_per_akun=rs2.getBoolean("pendapatan_per_akun");
                        akses01.hasil_pemeriksaan_echo=rs2.getBoolean("hasil_pemeriksaan_echo");
                        akses01.penilaian_bayi_baru_lahir=rs2.getBoolean("penilaian_bayi_baru_lahir");
                        akses01.rl1_3_ketersediaan_kamar=rs2.getBoolean("rl1_3_ketersediaan_kamar");
                        akses01.pendapatan_per_akun_closing=rs2.getBoolean("pendapatan_per_akun_closing");
                        akses01.pengeluaran_pengeluaran=rs2.getBoolean("pengeluaran_pengeluaran");
                        akses01.skrining_diabetes_melitus=rs2.getBoolean("skrining_diabetes_melitus");
                        akses01.laporan_tindakan=rs2.getBoolean("laporan_tindakan");
                        akses01.pelaksanaan_informasi_edukasi=rs2.getBoolean("pelaksanaan_informasi_edukasi");
                        akses01.layanan_kedokteran_fisik_rehabilitasi=rs2.getBoolean("layanan_kedokteran_fisik_rehabilitasi");
                        akses01.skrining_kesehatan_gigi_mulut_balita=rs2.getBoolean("skrining_kesehatan_gigi_mulut_balita");
                        akses01.skrining_anemia=rs2.getBoolean("skrining_anemia");
                        akses01.layanan_program_kfr=rs2.getBoolean("layanan_program_kfr");
                        akses01.skrining_hipertensi=rs2.getBoolean("skrining_hipertensi");
                        akses01.skrining_kesehatan_penglihatan=rs2.getBoolean("skrining_kesehatan_penglihatan");
                        akses01.catatan_observasi_hemodialisa=rs2.getBoolean("catatan_observasi_hemodialisa");
                        akses01.skrining_kesehatan_gigi_mulut_dewasa=rs2.getBoolean("skrining_kesehatan_gigi_mulut_dewasa");
                        akses01.skrining_risiko_kanker_serviks=rs2.getBoolean("skrining_risiko_kanker_serviks");
                        akses01.catatan_cairan_hemodialisa=rs2.getBoolean("catatan_cairan_hemodialisa");
                        akses01.skrining_kesehatan_gigi_mulut_lansia=rs2.getBoolean("skrining_kesehatan_gigi_mulut_lansia");
                        akses01.skrining_indra_pendengaran=rs2.getBoolean("skrining_indra_pendengaran");
                        akses01.catatan_pengkajian_paska_operasi=rs2.getBoolean("catatan_pengkajian_paska_operasi");
                        akses01.skrining_frailty_syndrome=rs2.getBoolean("skrining_frailty_syndrome");
                        akses01.sirkulasi_cssd=rs2.getBoolean("sirkulasi_cssd");
                        akses01.lama_pelayanan_cssd=rs2.getBoolean("lama_pelayanan_cssd");
                        akses01.catatan_observasi_bayi=rs2.getBoolean("catatan_observasi_bayi");
                        akses01.riwayat_surat_peringatan=rs2.getBoolean("riwayat_surat_peringatan");
                        akses01.master_kesimpulan_anjuran_mcu=rs2.getBoolean("master_kesimpulan_anjuran_mcu");
                        akses01.kategori_piutang_jasa_perusahaan=rs2.getBoolean("kategori_piutang_jasa_perusahaan");
                        akses01.piutang_jasa_perusahaan=rs2.getBoolean("piutang_jasa_perusahaan");
                        akses01.bayar_piutang_jasa_perusahaan=rs2.getBoolean("bayar_piutang_jasa_perusahaan");
                        akses01.piutang_jasa_perusahaan_belum_lunas=rs2.getBoolean("piutang_jasa_perusahaan_belum_lunas");
                        akses01.checklist_kesiapan_anestesi=rs2.getBoolean("checklist_kesiapan_anestesi");
                        akses01.piutang_peminjaman_uang_belum_lunas=rs2.getBoolean("piutang_peminjaman_uang_belum_lunas");
                        akses01.hasil_pemeriksaan_slit_lamp=rs2.getBoolean("hasil_pemeriksaan_slit_lamp");
                        akses01.hasil_pemeriksaan_oct=rs2.getBoolean("hasil_pemeriksaan_oct");
                        akses01.beban_hutang_lain=rs2.getBoolean("beban_hutang_lain");
                        akses01.poli_asal_pasien_ranap=rs2.getBoolean("poli_asal_pasien_ranap");
                        akses01.pemberi_hutang_lain=rs2.getBoolean("pemberi_hutang_lain");
                        akses01.dokter_asal_pasien_ranap=rs2.getBoolean("dokter_asal_pasien_ranap");
                        akses01.duta_parkir_rekap_keluar=rs2.getBoolean("duta_parkir_rekap_keluar");
                        akses01.surat_keterangan_layak_terbang=rs2.getBoolean("surat_keterangan_layak_terbang");
                        akses01.bayar_beban_hutang_lain=rs2.getBoolean("bayar_beban_hutang_lain");
                        akses01.surat_persetujuan_pemeriksaan_hiv=rs2.getBoolean("surat_persetujuan_pemeriksaan_hiv");
                        akses01.skrining_instrumen_acrs=rs2.getBoolean("skrining_instrumen_acrs");
                        akses01.surat_pernyataan_memilih_dpjp=rs2.getBoolean("surat_pernyataan_memilih_dpjp");
                    }else if(!bcryptAdmin && !bcryptUser){
                        akses01.kode="";                  
                        akses01.penyakit= false;
                        akses01.obat_penyakit= false;
                        akses01.dokter= false;
                        akses01.jadwal_praktek= false;
                        akses01.petugas= false;
                        akses01.pasien= false;
                        akses01.registrasi= false;
                        akses01.tindakan_ralan= false;
                        akses01.kamar_inap= false;
                        akses01.tindakan_ranap= false;
                        akses01.operasi= false;
                        akses01.rujukan_keluar= false;
                        akses01.rujukan_masuk= false;
                        akses01.beri_obat= false;
                        akses01.resep_pulang= false;
                        akses01.pasien_meninggal= false;
                        akses01.diet_pasien= false;
                        akses01.kelahiran_bayi= false;
                        akses01.periksa_lab= false;
                        akses01.periksa_radiologi= false;
                        akses01.kasir_ralan= false;
                        akses01.deposit_pasien= false;
                        akses01.piutang_pasien= false;
                        akses01.peminjaman_berkas= false;
                        akses01.barcode= false;
                        akses01.presensi_harian= false;
                        akses01.presensi_bulanan= false;
                        akses01.pegawai_admin= false;
                        akses01.pegawai_user= false;
                        akses01.suplier= false;
                        akses01.satuan_barang= false;
                        akses01.konversi_satuan= false;
                        akses01.jenis_barang= false;
                        akses01.obat= false;
                        akses01.stok_opname_obat= false;
                        akses01.stok_obat_pasien= false;
                        akses01.pengadaan_obat= false;
                        akses01.pemesanan_obat= false;
                        akses01.penjualan_obat= false;
                        akses01.piutang_obat= false;
                        akses01.retur_ke_suplier= false;
                        akses01.retur_dari_pembeli= false;
                        akses01.retur_obat_ranap= false;
                        akses01.retur_piutang_pasien= false;
                        akses01.keuntungan_penjualan= false;
                        akses01.keuntungan_beri_obat= false;
                        akses01.sirkulasi_obat= false;
                        akses01.ipsrs_barang= false;
                        akses01.ipsrs_pengadaan_barang= false;
                        akses01.ipsrs_stok_keluar= false;
                        akses01.ipsrs_rekap_pengadaan= false;
                        akses01.ipsrs_rekap_stok_keluar= false;
                        akses01.ipsrs_pengeluaran_harian= false;
                        akses01.ipsrs_jenis_barang=false;
                        akses01.inventaris_jenis= false;
                        akses01.inventaris_kategori= false;
                        akses01.inventaris_merk= false;
                        akses01.inventaris_ruang= false;
                        akses01.inventaris_produsen= false;
                        akses01.inventaris_koleksi= false;
                        akses01.inventaris_inventaris= false;
                        akses01.inventaris_sirkulasi= false;
                        akses01.parkir_jenis= false;
                        akses01.parkir_in= false;
                        akses01.parkir_out= false;
                        akses01.parkir_rekap_harian= false;
                        akses01.parkir_rekap_bulanan= false;
                        akses01.informasi_kamar= false;
                        akses01.harian_tindakan_poli= false;
                        akses01.obat_per_poli= false;
                        akses01.obat_per_kamar= false;
                        akses01.obat_per_dokter_ralan= false;
                        akses01.obat_per_dokter_ranap= false;
                        akses01.harian_dokter= false;
                        akses01.bulanan_dokter= false;
                        akses01.harian_paramedis= false;
                        akses01.bulanan_paramedis= false;
                        akses01.pembayaran_ralan= false;
                        akses01.pembayaran_ranap= false;
                        akses01.rekap_pembayaran_ralan= false;
                        akses01.rekap_pembayaran_ranap= false;
                        akses01.tagihan_masuk= false;
                        akses01.tambahan_biaya= false;
                        akses01.potongan_biaya= false;
                        akses01.resep_obat= false;
                        akses01.resume_pasien= false;
                        akses01.penyakit_ralan= false;
                        akses01.penyakit_ranap= false;
                        akses01.kamar= false;
                        akses01.tarif_ralan= false;
                        akses01.tarif_ranap= false;
                        akses01.tarif_lab= false;
                        akses01.tarif_radiologi= false;
                        akses01.tarif_operasi= false;
                        akses01.akun_rekening= false;
                        akses01.rekening_tahun= false;
                        akses01.posting_jurnal= false;
                        akses01.buku_besar= false;
                        akses01.cashflow= false;
                        akses01.keuangan= false;
                        akses01.pengeluaran= false;
                        akses01.setup_pjlab= false;
                        akses01.setup_otolokasi= false;
                        akses01.setup_jam_kamin= false;
                        akses01.setup_embalase= false;
                        akses01.tracer_login= false;
                        akses01.display= false;
                        akses01.set_harga_obat= false;
                        akses01.set_penggunaan_tarif= false;
                        akses01.set_oto_ralan= false;
                        akses01.biaya_harian= false;
                        akses01.biaya_masuk_sekali= false;
                        akses01.set_no_rm= false;
                        akses01.billing_ralan=false;
                        akses01.billing_ranap=false;
                        akses01.jm_ranap_dokter=false;   
                        akses01.igd=false;   
                        akses01.barcoderalan=false; 
                        akses01.barcoderanap=false;
                        akses01.set_harga_obat_ralan=false; 
                        akses01.set_harga_obat_ranap=false;
                        akses01.admin= false;
                        akses01.user= false;
                        akses01.vakum= false;
                        akses01.aplikasi= false;
                        akses01.penyakit_pd3i=false;
                        akses01.surveilans_pd3i=false;
                        akses01.surveilans_ralan=false;
                        akses01.diagnosa_pasien=false;
                        akses01.surveilans_ranap=false;
                        akses01.pny_takmenular_ranap=false;
                        akses01.pny_takmenular_ralan=false;
                        akses01.kunjungan_ralan=false;
                        akses01.rl32=false;
                        akses01.rl33=false;
                        akses01.rl37=false;
                        akses01.rl38=false;
                        akses01.harian_tindakan_dokter=false;
                        akses01.sms=false;             
                        akses01.sidikjari=false;  
                        akses01.jam_masuk=false;  
                        akses01.jadwal_pegawai=false;   
                        akses01.parkir_barcode=false;
                        akses01.set_nota=false;
                        akses01.dpjp_ranap=false;
                        akses01.mutasi_barang=false;
                        akses01.rl34=false;
                        akses01.rl36=false;
                        akses01.fee_visit_dokter=false;
                        akses01.fee_bacaan_ekg=false;
                        akses01.fee_rujukan_rontgen=false;
                        akses01.fee_rujukan_ranap=false;
                        akses01.fee_ralan=false;
                        akses01.akun_bayar=false;
                        akses01.bayar_pemesanan_obat=false;
                        akses01.obat_per_dokter_peresep=false;
                        akses01.pemasukan_lain=false;
                        akses01.pengaturan_rekening=false;
                        akses01.closing_kasir=false;
                        akses01.keterlambatan_presensi=false; 
                        akses01.set_harga_kamar=false;
                        akses01.rekap_per_shift=false;
                        akses01.bpjs_cek_nik=false;
                        akses01.bpjs_cek_kartu=false;
                        akses01.bpjs_cek_riwayat=false;
                        akses01.obat_per_cara_bayar=false;
                        akses01.kunjungan_ranap=false;
                        akses01.bayar_piutang=false;
                        akses01.payment_point=false;
                        akses01.bpjs_cek_nomor_rujukan=false;
                        akses01.icd9=false;
                        akses01.darurat_stok=false;
                        akses01.retensi_rm=false;
                        akses01.temporary_presensi=false;
                        akses01.jurnal_harian=false;
                        akses01.sirkulasi_obat2=false;
                        akses01.edit_registrasi=false;
                        akses01.bpjs_referensi_diagnosa=false;
                        akses01.bpjs_referensi_poli=false;
                        akses01.industrifarmasi=false;
                        akses01.harian_js=false;
                        akses01.bulanan_js=false;
                        akses01.harian_paket_bhp=false;
                        akses01.bulanan_paket_bhp=false;
                        akses01.piutang_pasien2=false;
                        akses01.bpjs_referensi_faskes=false;
                        akses01.bpjs_sep=false;
                        akses01.pengambilan_utd=false;
                        akses01.tarif_utd=false;
                        akses01.pengambilan_utd2=false;
                        akses01.utd_medis_rusak=false;
                        akses01.pengambilan_penunjang_utd=false;
                        akses01.pengambilan_penunjang_utd2=false;
                        akses01.utd_penunjang_rusak=false;
                        akses01.suplier_penunjang=false;
                        akses01.utd_donor=false;
                        akses01.bpjs_monitoring_klaim=false;
                        akses01.utd_cekal_darah=false;
                        akses01.utd_komponen_darah=false;
                        akses01.utd_stok_darah=false;
                        akses01.utd_pemisahan_darah=false;
                        akses01.harian_kamar=false;
                        akses01.rincian_piutang_pasien=false;
                        akses01.keuntungan_beri_obat_nonpiutang=false;
                        akses01.reklasifikasi_ralan=false;
                        akses01.reklasifikasi_ranap=false;
                        akses01.utd_penyerahan_darah=false;
                        akses01.hutang_obat=false;
                        akses01.riwayat_obat_alkes_bhp=false;
                        akses01.sensus_harian_poli=false;
                        akses01.rl4a=false;
                        akses01.aplicare_referensi_kamar=false;
                        akses01.aplicare_ketersediaan_kamar=false;
                        akses01.inacbg_klaim_baru_otomatis=false;
                        akses01.inacbg_klaim_baru_manual=false;
                        akses01.inacbg_coder_nik=false;
                        akses01.mutasi_berkas=false;
                        akses01.akun_piutang=false;
                        akses01.harian_kso=false;
                        akses01.bulanan_kso=false;                                    
                        akses01.harian_menejemen=false;
                        akses01.bulanan_menejemen=false;
                        akses01.inhealth_cek_eligibilitas=false;
                        akses01.inhealth_referensi_jenpel_ruang_rawat=false;
                        akses01.inhealth_referensi_poli=false;
                        akses01.inhealth_referensi_faskes=false;
                        akses01.inhealth_sjp=false;
                        akses01.piutang_ralan=false;
                        akses01.piutang_ranap=false;
                        akses01.detail_piutang_penjab=false;
                        akses01.lama_pelayanan_ralan=false;
                        akses01.catatan_pasien=false;
                        akses01.rl4b=false;              
                        akses01.rl4asebab=false;
                        akses01.rl4bsebab=false;
                        akses01.data_HAIs=false;
                        akses01.harian_HAIs=false;
                        akses01.bulanan_HAIs=false;
                        akses01.hitung_bor=false;
                        akses01.perusahaan_pasien=false;
                        akses01.resep_dokter=false;
                        akses01.lama_pelayanan_apotek=false;
                        akses01.hitung_alos=false;
                        akses01.detail_tindakan=false;
                        akses01.rujukan_poli_internal=false;
                        akses01.rekap_poli_anak=false;
                        akses01.grafik_kunjungan_poli=false;
                        akses01.grafik_kunjungan_perdokter=false;
                        akses01.grafik_kunjungan_perpekerjaan=false;
                        akses01.grafik_kunjungan_perpendidikan=false;
                        akses01.grafik_kunjungan_pertahun=false;
                        akses01.berkas_digital_perawatan=false;
                        akses01.penyakit_menular_ranap=false;
                        akses01.penyakit_menular_ralan=false;
                        akses01.grafik_kunjungan_perbulan=false;                    
                        akses01.grafik_kunjungan_pertanggal=false;
                        akses01.grafik_kunjungan_demografi=false;
                        akses01.grafik_kunjungan_statusdaftartahun=false;
                        akses01.grafik_kunjungan_statusdaftartahun2=false;                        
                        akses01.grafik_kunjungan_statusdaftarbulan=false;
                        akses01.grafik_kunjungan_statusdaftarbulan2=false;
                        akses01.grafik_kunjungan_statusdaftartanggal=false;
                        akses01.grafik_kunjungan_statusdaftartanggal2=false;
                        akses01.grafik_kunjungan_statusbataltahun=false;
                        akses01.grafik_kunjungan_statusbatalbulan=false;
                        akses01.pcare_cek_penyakit=false;
                        akses01.grafik_kunjungan_statusbataltanggal=false;
                        akses01.kategori_barang=false;
                        akses01.golongan_barang=false;
                        akses01.pemberian_obat_pertanggal=false;
                        akses01.penjualan_obat_pertanggal=false;
                        akses01.pcare_cek_kesadaran=false;
                        akses01.pembatalan_periksa_dokter=false;
                        akses01.pembayaran_per_unit=false;
                        akses01.rekap_pembayaran_per_unit=false;
                        akses01.grafik_kunjungan_percarabayar=false;
                        akses01.ipsrs_pengadaan_pertanggal=false;
                        akses01.ipsrs_stokkeluar_pertanggal=false;
                        akses01.grafik_kunjungan_ranaptahun=false;
                        akses01.pcare_cek_rujukan=false;
                        akses01.grafik_lab_ralantahun=false;
                        akses01.grafik_rad_ralantahun=false;
                        akses01.cek_entry_ralan=false;
                        akses01.inacbg_klaim_baru_manual2=false;
                        akses01.permintaan_medis=false;
                        akses01.rekap_permintaan_medis=false;
                        akses01.surat_pemesanan_medis=false;
                        akses01.permintaan_non_medis=false;
                        akses01.rekap_permintaan_non_medis=false;
                        akses01.surat_pemesanan_non_medis=false;
                        akses01.grafik_per_perujuk=false;
                        akses01.bpjs_cek_prosedur=false;
                        akses01.bpjs_cek_kelas_rawat=false;
                        akses01.bpjs_cek_dokter=false;
                        akses01.bpjs_cek_spesialistik=false;
                        akses01.bpjs_cek_ruangrawat=false;
                        akses01.bpjs_cek_carakeluar=false;
                        akses01.bpjs_cek_pasca_pulang=false;
                        akses01.detail_tindakan_okvk=false;
                        akses01.billing_parsial=false;
                        akses01.bpjs_cek_nomor_rujukan_rs=false;
                        akses01.bpjs_cek_rujukan_kartu_pcare=false;
                        akses01.bpjs_cek_rujukan_kartu_rs=false;
                        akses01.akses_depo_obat=false;
                        akses01.bpjs_rujukan_keluar=false;
                        akses01.grafik_lab_ralanbulan=false;
                        akses01.pengeluaran_stok_apotek=false;
                        akses01.grafik_rad_ralanbulan=false;
                        akses01.detailjmdokter2=false;
                        akses01.pengaduan_pasien=false;
                        akses01.grafik_lab_ralanhari=false;
                        akses01.grafik_rad_ralanhari=false;
                        akses01.sensus_harian_ralan=false;
                        akses01.metode_racik=false;
                        akses01.pembayaran_akun_bayar=false;
                        akses01.pengguna_obat_resep=false;
                        akses01.rekap_pemesanan=false;
                        akses01.master_berkas_pegawai=false;
                        akses01.berkas_kepegawaian=false;
                        akses01.riwayat_jabatan=false;
                        akses01.riwayat_pendidikan=false;
                        akses01.riwayat_naik_gaji=false;
                        akses01.kegiatan_ilmiah=false;
                        akses01.riwayat_penghargaan=false;
                        akses01.riwayat_penelitian=false;
                        akses01.penerimaan_non_medis=false;
                        akses01.bayar_pesan_non_medis=false;
                        akses01.hutang_barang_non_medis=false;
                        akses01.rekap_pemesanan_non_medis=false;                      
                        akses01.insiden_keselamatan=false;
                        akses01.insiden_keselamatan_pasien=false;
                        akses01.grafik_ikp_pertahun=false;
                        akses01.grafik_ikp_perbulan=false;
                        akses01.grafik_ikp_pertanggal=false;
                        akses01.riwayat_data_batch=false;
                        akses01.grafik_ikp_jenis=false;
                        akses01.grafik_ikp_dampak=false;
                        akses01.piutang_akun_piutang=false;
                        akses01.grafik_kunjungan_per_agama=false;
                        akses01.grafik_kunjungan_per_umur=false;
                        akses01.suku_bangsa=false;
                        akses01.bahasa_pasien=false;
                        akses01.golongan_tni=false;
                        akses01.satuan_tni=false;
                        akses01.jabatan_tni=false;
                        akses01.pangkat_tni=false;
                        akses01.golongan_polri=false;
                        akses01.satuan_polri=false;
                        akses01.jabatan_polri=false;
                        akses01.pangkat_polri=false;
                        akses01.cacat_fisik=false;
                        akses01.grafik_kunjungan_suku=false;
                        akses01.grafik_kunjungan_bahasa=false;
                        akses01.booking_operasi=false;
                        akses01.mapping_poli_bpjs=false;
                        akses01.grafik_kunjungan_per_cacat=false;
                        akses01.barang_cssd=false;
                        akses01.skdp_bpjs=false;
                        akses01.booking_registrasi=false;
                        akses01.bpjs_cek_propinsi=false;
                        akses01.bpjs_cek_kabupaten=false;
                        akses01.bpjs_cek_kecamatan=false;
                        akses01.bpjs_cek_dokterdpjp=false;
                        akses01.bpjs_cek_riwayat_rujukanrs=false;
                        akses01.bpjs_cek_tanggal_rujukan=false;
                        akses01.permintaan_lab=false;
                        akses01.permintaan_radiologi=false;
                        akses01.surat_indeks=false;
                        akses01.surat_map=false;
                        akses01.surat_almari=false;
                        akses01.surat_rak=false;
                        akses01.surat_ruang=false;
                        akses01.surat_klasifikasi=false;
                        akses01.surat_status=false;
                        akses01.surat_sifat=false;
                        akses01.surat_balas=false;
                        akses01.surat_masuk=false;
                        akses01.pcare_cek_dokter=false;
                        akses01.pcare_cek_poli=false;
                        akses01.pcare_cek_provider=false;
                        akses01.pcare_cek_statuspulang=false;
                        akses01.pcare_cek_spesialis=false;
                        akses01.pcare_cek_subspesialis=false;
                        akses01.pcare_cek_sarana=false;
                        akses01.pcare_cek_khusus=false;
                        akses01.pcare_cek_obat=false;
                        akses01.pcare_cek_tindakan=false;
                        akses01.pcare_cek_faskessubspesialis=false;
                        akses01.pcare_cek_faskesalihrawat=false;
                        akses01.pcare_cek_faskesthalasemia=false;
                        akses01.pcare_mapping_obat=false;
                        akses01.pcare_mapping_tindakan=false;
                        akses01.pcare_club_prolanis=false;
                        akses01.pcare_mapping_poli=false;
                        akses01.pcare_kegiatan_kelompok=false;
                        akses01.pcare_mapping_tindakan_ranap=false;
                        akses01.pcare_peserta_kegiatan_kelompok=false;
                        akses01.sirkulasi_obat3=false;
                        akses01.bridging_pcare_daftar=false;
                        akses01.pcare_mapping_dokter=false;
                        akses01.ranap_per_ruang=false;
                        akses01.penyakit_ranap_cara_bayar=false;
                        akses01.anggota_militer_dirawat=false;
                        akses01.set_input_parsial=false;
                        akses01.lama_pelayanan_radiologi=false;
                        akses01.lama_pelayanan_lab=false;
                        akses01.bpjs_cek_sep=false;
                        akses01.catatan_perawatan=false;
                        akses01.surat_keluar=false;
                        akses01.kegiatan_farmasi=false;
                        akses01.stok_opname_logistik=false;
                        akses01.sirkulasi_non_medis=false;
                        akses01.rekap_lab_pertahun=false;
                        akses01.perujuk_lab_pertahun=false;
                        akses01.rekap_radiologi_pertahun=false;
                        akses01.perujuk_radiologi_pertahun=false;
                        akses01.jumlah_porsi_diet=false;
                        akses01.jumlah_macam_diet=false;
                        akses01.payment_point2=false;
                        akses01.pembayaran_akun_bayar2=false;
                        akses01.hapus_nota_salah=false;
                        akses01.hais_perbangsal=false;
                        akses01.ppn_obat=false;
                        akses01.saldo_akun_perbulan=false;
                        akses01.display_apotek=false;
                        akses01.sisrute_referensi_faskes=false;
                        akses01.sisrute_referensi_alasanrujuk=false;
                        akses01.sisrute_referensi_diagnosa=false;
                        akses01.sisrute_rujukan_masuk=false;
                        akses01.sisrute_rujukan_keluar=false;
                        akses01.bpjs_cek_skdp=false;
                        akses01.data_batch=false;
                        akses01.kunjungan_permintaan_lab=false;
                        akses01.kunjungan_permintaan_lab2=false;
                        akses01.kunjungan_permintaan_radiologi=false;
                        akses01.kunjungan_permintaan_radiologi2=false;
                        akses01.pcare_pemberian_obat=false;
                        akses01.pcare_pemberian_tindakan=false;
                        akses01.pembayaran_akun_bayar3=false;
                        akses01.password_asuransi=false;
                        akses01.kemenkes_sitt=false;
                        akses01.siranap_ketersediaan_kamar=false;
                        akses01.grafik_tb_periodelaporan=false;
                        akses01.grafik_tb_rujukan=false;
                        akses01.grafik_tb_riwayat=false;
                        akses01.grafik_tb_tipediagnosis=false;
                        akses01.grafik_tb_statushiv=false;
                        akses01.grafik_tb_skoringanak=false;
                        akses01.grafik_tb_konfirmasiskoring5=false;
                        akses01.grafik_tb_konfirmasiskoring6=false;
                        akses01.grafik_tb_sumberobat=false;
                        akses01.grafik_tb_hasilakhirpengobatan=false;
                        akses01.grafik_tb_hasilteshiv=false;
                        akses01.kadaluarsa_batch=false;
                        akses01.sisa_stok=false;
                        akses01.obat_per_resep=false;
                        akses01.pemakaian_air_pdam=false;
                        akses01.limbah_b3_medis=false;
                        akses01.grafik_air_pdam_pertanggal=false;
                        akses01.grafik_air_pdam_perbulan=false;
                        akses01.grafik_limbahb3_pertanggal=false;
                        akses01.grafik_limbahb3_perbulan=false;
                        akses01.limbah_domestik=false;
                        akses01.grafik_limbahdomestik_pertanggal=false;
                        akses01.grafik_limbahdomestik_perbulan=false;
                        akses01.mutu_air_limbah=false;
                        akses01.pest_control=false;
                        akses01.ruang_perpustakaan=false;
                        akses01.kategori_perpustakaan=false;
                        akses01.jenis_perpustakaan=false;
                        akses01.pengarang_perpustakaan=false;
                        akses01.penerbit_perpustakaan=false;
                        akses01.koleksi_perpustakaan=false;
                        akses01.inventaris_perpustakaan=false;
                        akses01.set_peminjaman_perpustakaan=false;
                        akses01.denda_perpustakaan=false;
                        akses01.anggota_perpustakaan=false;
                        akses01.peminjaman_perpustakaan=false;
                        akses01.bayar_denda_perpustakaan=false;
                        akses01.ebook_perpustakaan=false;
                        akses01.jenis_cidera_k3rs=false;
                        akses01.penyebab_k3rs=false;
                        akses01.jenis_luka_k3rs=false;
                        akses01.lokasi_kejadian_k3rs=false;
                        akses01.dampak_cidera_k3rs=false;
                        akses01.jenis_pekerjaan_k3rs=false;
                        akses01.bagian_tubuh_k3rs=false;
                        akses01.peristiwa_k3rs=false;
                        akses01.grafik_k3_pertahun=false;
                        akses01.grafik_k3_perbulan=false;
                        akses01.grafik_k3_pertanggal=false;
                        akses01.grafik_k3_perjeniscidera=false;
                        akses01.grafik_k3_perpenyebab=false;
                        akses01.grafik_k3_perjenisluka=false;
                        akses01.grafik_k3_lokasikejadian=false;
                        akses01.grafik_k3_dampakcidera=false;
                        akses01.grafik_k3_perjenispekerjaan=false;
                        akses01.grafik_k3_perbagiantubuh=false;
                        akses01.jenis_cidera_k3rstahun=false;
                        akses01.penyebab_k3rstahun=false;
                        akses01.jenis_luka_k3rstahun=false;
                        akses01.lokasi_kejadian_k3rstahun=false;
                        akses01.dampak_cidera_k3rstahun=false;
                        akses01.jenis_pekerjaan_k3rstahun=false;
                        akses01.bagian_tubuh_k3rstahun=false;
                        akses01.sekrining_rawat_jalan=false;
                        akses01.bpjs_histori_pelayanan=false;
                        akses01.rekap_mutasi_berkas=false;
                        akses01.skrining_ralan_pernapasan_pertahun=false;
                        akses01.pengajuan_barang_medis=false;
                        akses01.pengajuan_barang_nonmedis=false;
                        akses01.grafik_kunjungan_ranapbulan=false;
                        akses01.grafik_kunjungan_ranaptanggal=false;
                        akses01.grafik_kunjungan_ranap_peruang=false;
                        akses01.kunjungan_bangsal_pertahun=false;
                        akses01.grafik_jenjang_jabatanpegawai=false;
                        akses01.grafik_bidangpegawai=false;
                        akses01.grafik_departemenpegawai=false;
                        akses01.grafik_pendidikanpegawai=false;
                        akses01.grafik_sttswppegawai=false;
                        akses01.grafik_sttskerjapegawai=false;
                        akses01.grafik_sttspulangranap=false;
                        akses01.kip_pasien_ranap=false;
                        akses01.kip_pasien_ralan=false;
                        akses01.bpjs_mapping_dokterdpjp=false;
                        akses01.data_triase_igd=false;
                        akses01.master_triase_skala1=false;
                        akses01.master_triase_skala2=false;
                        akses01.master_triase_skala3=false;
                        akses01.master_triase_skala4=false;
                        akses01.master_triase_skala5=false;
                        akses01.master_triase_pemeriksaan=false;
                        akses01.master_triase_macamkasus=false;
                        akses01.rekap_permintaan_diet=false;
                        akses01.daftar_pasien_ranap=false;
                        akses01.daftar_pasien_ranaptni=false;
                        akses01.pengajuan_asetinventaris=false;
                        akses01.item_apotek_jenis=false;
                        akses01.item_apotek_kategori=false;
                        akses01.item_apotek_golongan=false;
                        akses01.item_apotek_industrifarmasi=false;
                        akses01.obat10_terbanyak_poli=false;
                        akses01.grafik_pengajuan_aset_urgensi=false;
                        akses01.grafik_pengajuan_aset_status=false;
                        akses01.grafik_pengajuan_aset_departemen=false;
                        akses01.rekap_pengajuan_aset_departemen=false;
                        akses01.grafik_kelompok_jabatanpegawai=false;
                        akses01.grafik_resiko_kerjapegawai=false;
                        akses01.grafik_emergency_indexpegawai=false;
                        akses01.grafik_inventaris_ruang=false;
                        akses01.harian_HAIs2=false;
                        akses01.grafik_inventaris_jenis=false;
                        akses01.data_resume_pasien=false;
                        akses01.perkiraan_biaya_ranap=false;
                        akses01.rekap_obat_poli=false;
                        akses01.rekap_obat_pasien=false;
                        akses01.grafik_HAIs_pasienbangsal=false;
                        akses01.grafik_HAIs_pasienbulan=false;
                        akses01.permintaan_perbaikan_inventaris=false;
                        akses01.grafik_HAIs_laju_vap=false;
                        akses01.grafik_HAIs_laju_iad=false;
                        akses01.grafik_HAIs_laju_pleb=false;
                        akses01.grafik_HAIs_laju_isk=false;
                        akses01.grafik_HAIs_laju_ilo=false;
                        akses01.grafik_HAIs_laju_hap=false;
                        akses01.inhealth_mapping_poli=false;
                        akses01.inhealth_mapping_dokter=false;
                        akses01.inhealth_mapping_tindakan_ralan=false;
                        akses01.inhealth_mapping_tindakan_ranap=false;
                        akses01.inhealth_mapping_tindakan_radiologi=false;
                        akses01.inhealth_mapping_tindakan_laborat=false;
                        akses01.inhealth_mapping_tindakan_operasi=false;
                        akses01.hibah_obat_bhp=false;
                        akses01.asal_hibah=false;
                        akses01.asuhan_gizi=false;
                        akses01.inhealth_kirim_tagihan=false;
                        akses01.sirkulasi_obat4=false;
                        akses01.sirkulasi_obat5=false;
                        akses01.sirkulasi_non_medis2=false;
                        akses01.monitoring_asuhan_gizi=false;
                        akses01.penerimaan_obat_perbulan=false;
                        akses01.rekap_kunjungan=false;
                        akses01.surat_sakit=false;
                        akses01.penilaian_awal_keperawatan_ralan=false;
                        akses01.permintaan_diet=false;
                        akses01.master_masalah_keperawatan=false;
                        akses01.pengajuan_cuti=false;
                        akses01.kedatangan_pasien=false;
                        akses01.utd_pendonor=false;
                        akses01.toko_suplier=false;
                        akses01.toko_jenis=false;
                        akses01.toko_set_harga=false;
                        akses01.toko_barang=false;
                        akses01.penagihan_piutang_pasien=false;
                        akses01.akun_penagihan_piutang=false;
                        akses01.stok_opname_toko=false;
                        akses01.toko_riwayat_barang=false;
                        akses01.toko_surat_pemesanan=false;
                        akses01.toko_pengajuan_barang=false;
                        akses01.toko_penerimaan_barang=false;
                        akses01.toko_pengadaan_barang=false;
                        akses01.toko_hutang=false;
                        akses01.toko_bayar_pemesanan=false;
                        akses01.toko_member=false;
                        akses01.toko_penjualan=false;
                        akses01.registrasi_poli_per_tanggal=false;
                        akses01.toko_piutang=false;
                        akses01.toko_retur_beli=false;
                        akses01.ipsrs_returbeli=false;
                        akses01.ipsrs_riwayat_barang=false;
                        akses01.pasien_corona=false;
                        akses01.toko_pendapatan_harian=false;
                        akses01.diagnosa_pasien_corona=false;
                        akses01.perawatan_pasien_corona=false;
                        akses01.penilaian_awal_keperawatan_gigi=false;
                        akses01.master_masalah_keperawatan_gigi=false;
                        akses01.toko_bayar_piutang=false;
                        akses01.toko_piutang_harian=false;
                        akses01.toko_penjualan_harian=false;
                        akses01.deteksi_corona=false;
                        akses01.penilaian_awal_keperawatan_kebidanan=false;
                        akses01.pengumuman_epasien=false;
                        akses01.surat_hamil=false;
                        akses01.set_tarif_online=false;
                        akses01.booking_periksa=false;
                        akses01.toko_sirkulasi=false;
                        akses01.toko_retur_jual=false;
                        akses01.toko_retur_piutang=false;
                        akses01.toko_sirkulasi2=false;
                        akses01.toko_keuntungan_barang=false;
                        akses01.zis_pengeluaran_penerima_dankes=false;
                        akses01.zis_penghasilan_penerima_dankes=false;
                        akses01.zis_ukuran_rumah_penerima_dankes=false;
                        akses01.zis_dinding_rumah_penerima_dankes=false;
                        akses01.zis_lantai_rumah_penerima_dankes=false;
                        akses01.zis_atap_rumah_penerima_dankes=false;
                        akses01.zis_kepemilikan_rumah_penerima_dankes=false;
                        akses01.zis_kamar_mandi_penerima_dankes=false;
                        akses01.zis_dapur_rumah_penerima_dankes=false;
                        akses01.zis_kursi_rumah_penerima_dankes=false;
                        akses01.zis_kategori_phbs_penerima_dankes=false;
                        akses01.zis_elektronik_penerima_dankes=false;
                        akses01.zis_ternak_penerima_dankes=false;
                        akses01.zis_jenis_simpanan_penerima_dankes=false;
                        akses01.penilaian_awal_keperawatan_anak=false;
                        akses01.zis_kategori_asnaf_penerima_dankes=false;
                        akses01.master_masalah_keperawatan_anak=false;
                        akses01.master_imunisasi=false;
                        akses01.zis_patologis_penerima_dankes=false;
                        akses01.pcare_cek_kartu=false;
                        akses01.surat_bebas_narkoba=false;
                        akses01.surat_keterangan_covid=false;
                        akses01.pemakaian_air_tanah=false;
                        akses01.grafik_air_tanah_pertanggal=false;
                        akses01.grafik_air_tanah_perbulan=false;
                        akses01.lama_pelayanan_poli=false;
                        akses01.hemodialisa=false;
                        akses01.grafik_harian_hemodialisa=false;
                        akses01.grafik_bulanan_hemodialisa=false;
                        akses01.grafik_tahunan_hemodialisa=false;
                        akses01.grafik_bulanan_meninggal=false;
                        akses01.laporan_tahunan_irj=false;
                        akses01.perbaikan_inventaris=false;
                        akses01.surat_cuti_hamil=false;
                        akses01.permintaan_stok_obat_pasien=false;
                        akses01.pemeliharaan_inventaris=false;
                        akses01.klasifikasi_pasien_ranap=false;
                        akses01.bulanan_klasifikasi_pasien_ranap=false;
                        akses01.harian_klasifikasi_pasien_ranap=false;
                        akses01.klasifikasi_pasien_perbangsal=false;
                        akses01.soap_perawatan=false;
                        akses01.klaim_rawat_jalan=false;
                        akses01.skrining_gizi=false;
                        akses01.lama_penyiapan_rm=false;
                        akses01.dosis_radiologi=false;
                        akses01.demografi_umur_kunjungan=false;
                        akses01.jam_diet_pasien=false;
                        akses01.rvu_bpjs=false;
                        akses01.verifikasi_penerimaan_farmasi=false;
                        akses01.verifikasi_penerimaan_logistik=false;
                        akses01.pemeriksaan_lab_pa=false;
                        akses01.ringkasan_pengajuan_obat=false;
                        akses01.ringkasan_pemesanan_obat=false;
                        akses01.ringkasan_pengadaan_obat=false;
                        akses01.ringkasan_penerimaan_obat=false;
                        akses01.ringkasan_hibah_obat=false;
                        akses01.ringkasan_penjualan_obat=false;
                        akses01.ringkasan_beri_obat=false;
                        akses01.ringkasan_piutang_obat=false;
                        akses01.ringkasan_stok_keluar_obat=false;
                        akses01.ringkasan_retur_suplier_obat=false;
                        akses01.ringkasan_retur_pembeli_obat=false;
                        akses01.penilaian_awal_keperawatan_ranapkebidanan=false;
                        akses01.ringkasan_pengajuan_nonmedis=false;
                        akses01.ringkasan_pemesanan_nonmedis=false;
                        akses01.ringkasan_pengadaan_nonmedis=false;
                        akses01.ringkasan_penerimaan_nonmedis=false;
                        akses01.ringkasan_stokkeluar_nonmedis=false;
                        akses01.ringkasan_returbeli_nonmedis=false;
                        akses01.omset_penerimaan=false;
                        akses01.validasi_penagihan_piutang=false;
                        akses01.permintaan_ranap=false;
                        akses01.bpjs_diagnosa_prb=false;
                        akses01.bpjs_obat_prb=false;
                        akses01.bpjs_surat_kontrol=false;
                        akses01.penggunaan_bhp_ok=false;
                        akses01.surat_keterangan_rawat_inap=false;
                        akses01.surat_keterangan_sehat=false;
                        akses01.pendapatan_per_carabayar=false;
                        akses01.akun_host_to_host_bank_jateng=false;
                        akses01.pembayaran_bank_jateng=false;
                        akses01.bpjs_surat_pri=false;
                        akses01.ringkasan_tindakan=false;
                        akses01.lama_pelayanan_pasien=false;
                        akses01.surat_sakit_pihak_2=false;
                        akses01.tagihan_hutang_obat=false;
                        akses01.referensi_mobilejkn_bpjs=false;
                        akses01.batal_pendaftaran_mobilejkn_bpjs=false;
                        akses01.lama_operasi=false;
                        akses01.grafik_inventaris_kategori=false;
                        akses01.grafik_inventaris_merk=false;
                        akses01.grafik_inventaris_produsen=false;
                        akses01.pengembalian_deposit_pasien=false;
                        akses01.validasi_tagihan_hutang_obat=false;
                        akses01.piutang_obat_belum_lunas=false;
                        akses01.integrasi_briapi=false;
                        akses01.pengadaan_aset_inventaris=false;
                        akses01.akun_aset_inventaris=false;
                        akses01.suplier_inventaris=false;
                        akses01.penerimaan_aset_inventaris=false;
                        akses01.bayar_pemesanan_iventaris=false;
                        akses01.hutang_aset_inventaris=false;
                        akses01.hibah_aset_inventaris=false;
                        akses01.titip_faktur_non_medis=false;
                        akses01.validasi_tagihan_non_medis=false;
                        akses01.titip_faktur_aset=false;
                        akses01.validasi_tagihan_aset=false;
                        akses01.hibah_non_medis=false;
                        akses01.pcare_alasan_tacc=false;
                        akses01.resep_luar=false;
                        akses01.surat_bebas_tbc=false;
                        akses01.surat_buta_warna=false;
                        akses01.surat_bebas_tato=false;
                        akses01.surat_kewaspadaan_kesehatan=false;
                        akses01.grafik_porsidiet_pertanggal=false;
                        akses01.grafik_porsidiet_perbulan=false;
                        akses01.grafik_porsidiet_pertahun=false;
                        akses01.grafik_porsidiet_perbangsal=false;
                        akses01.penilaian_awal_medis_ralan=false;
                        akses01.master_masalah_keperawatan_mata=false;
                        akses01.penilaian_awal_keperawatan_mata=false;
                        akses01.penilaian_awal_medis_ranap=false;
                        akses01.penilaian_awal_medis_ranap_kebidanan=false;
                        akses01.penilaian_awal_medis_ralan_kebidanan=false;
                        akses01.penilaian_awal_medis_igd=false;
                        akses01.penilaian_awal_medis_ralan_anak=false;
                        akses01.bpjs_referensi_poli_hfis=false;
                        akses01.bpjs_referensi_dokter_hfis=false;
                        akses01.bpjs_referensi_jadwal_hfis=false;
                        akses01.penilaian_fisioterapi=false;
                        akses01.bpjs_program_prb=false;
                        akses01.bpjs_suplesi_jasaraharja=false;
                        akses01.bpjs_data_induk_kecelakaan=false;
                        akses01.bpjs_sep_internal=false;
                        akses01.bpjs_klaim_jasa_raharja=false;
                        akses01.bpjs_daftar_finger_print=false;
                        akses01.bpjs_rujukan_khusus=false;
                        akses01.pemeliharaan_gedung=false;
                        akses01.grafik_perbaikan_inventaris_pertanggal=false;
                        akses01.grafik_perbaikan_inventaris_perbulan=false;
                        akses01.grafik_perbaikan_inventaris_pertahun=false;
                        akses01.grafik_perbaikan_inventaris_perpelaksana_status=false;
                        akses01.penilaian_mcu=false;
                        akses01.peminjam_piutang=false;
                        akses01.piutang_lainlain=false;
                        akses01.cara_bayar=false;
                        akses01.audit_kepatuhan_apd=false;
                        akses01.bpjs_task_id=false;
                        akses01.bayar_piutang_lain=false;
                        akses01.pembayaran_akun_bayar4=false;
                        akses01.stok_akhir_farmasi_pertanggal=false;
                        akses01.riwayat_kamar_pasien=false;
                        akses01.uji_fungsi_kfr=false;
                        akses01.hapus_berkas_digital_perawatan=false;
                        akses01.kategori_pengeluaran_harian=false;
                        akses01.kategori_pemasukan_lain=false;
                        akses01.pembayaran_akun_bayar5=false;
                        akses01.ruang_ok=false;
                        akses01.jasa_tindakan_pasien=false;
                        akses01.telaah_resep=false;
                        akses01.permintaan_resep_pulang=false;
                        akses01.rekap_jm_dokter=false;
                        akses01.status_data_rm=false;
                        akses01.ubah_petugas_lab_pk=false;
                        akses01.ubah_petugas_lab_pa=false;
                        akses01.ubah_petugas_radiologi=false;
                        akses01.gabung_norawat=false;
                        akses01.gabung_rm=false;
                        akses01.ringkasan_biaya_obat_pasien_pertanggal=false;
                        akses01.master_masalah_keperawatan_igd=false;
                        akses01.penilaian_awal_keperawatan_igd=false;
                        akses01.bpjs_referensi_dpho_apotek=false;
                        akses01.bpjs_referensi_poli_apotek=false;
                        akses01.bayar_jm_dokter=false;
                        akses01.bpjs_referensi_faskes_apotek=false;
                        akses01.bpjs_referensi_spesialistik_apotek=false;
                        akses01.pembayaran_briva=false;
                        akses01.penilaian_awal_keperawatan_ranap=false;
                        akses01.nilai_penerimaan_vendor_farmasi_perbulan=false;
                        akses01.akun_bayar_hutang=false;
                        akses01.master_rencana_keperawatan=false;
                        akses01.laporan_tahunan_igd=false;
                        akses01.obat_bhp_tidakbergerak=false;
                        akses01.ringkasan_hutang_vendor_farmasi=false;
                        akses01.nilai_penerimaan_vendor_nonmedis_perbulan=false;
                        akses01.ringkasan_hutang_vendor_nonmedis=false;
                        akses01.anggota_polri_dirawat=false;
                        akses01.daftar_pasien_ranap_polri=false;
                        akses01.soap_ralan_polri=false;
                        akses01.soap_ranap_polri=false;
                        akses01.laporan_penyakit_polri=false;
                        akses01.master_rencana_keperawatan_anak=false;
                        akses01.jumlah_pengunjung_ralan_polri=false;
                        akses01.catatan_observasi_igd=false;
                        akses01.catatan_observasi_ranap=false;
                        akses01.catatan_observasi_ranap_kebidanan=false;
                        akses01.catatan_observasi_ranap_postpartum=false;
                        akses01.penilaian_awal_medis_ralan_tht=false;
                        akses01.penilaian_psikologi=false;
                        akses01.audit_cuci_tangan_medis=false;
                        akses01.audit_pembuangan_limbah=false;
                        akses01.ruang_audit_kepatuhan=false;
                        akses01.audit_pembuangan_benda_tajam=false;
                        akses01.audit_penanganan_darah=false;
                        akses01.audit_pengelolaan_linen_kotor=false;
                        akses01.audit_penempatan_pasien=false;
                        akses01.audit_kamar_jenazah=false;
                        akses01.audit_bundle_iadp=false;
                        akses01.audit_bundle_ido=false;
                        akses01.audit_fasilitas_kebersihan_tangan=false;
                        akses01.audit_fasilitas_apd=false;
                        akses01.audit_pembuangan_limbah_cair_infeksius=false;
                        akses01.audit_sterilisasi_alat=false;
                        akses01.penilaian_awal_medis_ralan_psikiatri=false;
                        akses01.persetujuan_penolakan_tindakan=false;
                        akses01.audit_bundle_isk=false;
                        akses01.audit_bundle_plabsi=false;
                        akses01.audit_bundle_vap=false;
                        akses01.akun_host_to_host_bank_papua=false;
                        akses01.pembayaran_bank_papua=false;
                        akses01.penilaian_awal_medis_ralan_penyakit_dalam=false;
                        akses01.penilaian_awal_medis_ralan_mata=false;
                        akses01.penilaian_awal_medis_ralan_neurologi=false;
                        akses01.sirkulasi_obat6=false;
                        akses01.penilaian_awal_medis_ralan_orthopedi=false;
                        akses01.penilaian_awal_medis_ralan_bedah=false;
                        akses01.integrasi_khanza_health_services=false;
                        akses01.soap_ralan_tni=false;
                        akses01.soap_ranap_tni=false;
                        akses01.jumlah_pengunjung_ralan_tni=false;
                        akses01.laporan_penyakit_tni=false;
                        akses01.catatan_keperawatan_ranap=false;
                        akses01.master_rencana_keperawatan_gigi=false;
                        akses01.master_rencana_keperawatan_mata=false;
                        akses01.master_rencana_keperawatan_igd=false;
                        akses01.master_masalah_keperawatan_psikiatri=false;
                        akses01.master_rencana_keperawatan_psikiatri=false;
                        akses01.penilaian_awal_keperawatan_psikiatri=false;
                        akses01.pemantauan_pews_anak=false;
                        akses01.surat_pulang_atas_permintaan_sendiri=false;
                        akses01.template_hasil_radiologi=false;
                        akses01.laporan_bulanan_irj=false;
                        akses01.template_pemeriksaan=false;
                        akses01.pemeriksaan_lab_mb=false;
                        akses01.ubah_petugas_lab_mb=false;
                        akses01.penilaian_pre_operasi=false;
                        akses01.penilaian_pre_anestesi=false;
                        akses01.perencanaan_pemulangan=false;
                        akses01.penilaian_lanjutan_resiko_jatuh_dewasa=false;
                        akses01.penilaian_lanjutan_resiko_jatuh_anak=false;
                        akses01.penilaian_awal_medis_ralan_geriatri=false;
                        akses01.penilaian_tambahan_pasien_geriatri=false;
                        akses01.skrining_nutrisi_dewasa=false;
                        akses01.skrining_nutrisi_lansia=false;
                        akses01.hasil_pemeriksaan_usg=false;
                        akses01.skrining_nutrisi_anak=false;
                        akses01.akun_host_to_host_bank_jabar=false;
                        akses01.pembayaran_bank_jabar=false;
                        akses01.surat_pernyataan_pasien_umum=false;
                        akses01.konseling_farmasi=false;
                        akses01.pelayanan_informasi_obat=false;
                        akses01.jawaban_pio_apoteker=false;
                        akses01.surat_persetujuan_umum=false;
                        akses01.transfer_pasien_antar_ruang=false;
                        akses01.satu_sehat_referensi_dokter=false;
                        akses01.satu_sehat_referensi_pasien=false;
                        akses01.satu_sehat_mapping_departemen=false;
                        akses01.satu_sehat_mapping_lokasi=false;
                        akses01.satu_sehat_kirim_encounter=false;
                        akses01.catatan_cek_gds=false;
                        akses01.satu_sehat_kirim_condition=false;
                        akses01.checklist_pre_operasi=false;
                        akses01.satu_sehat_kirim_observationttv=false;
                        akses01.signin_sebelum_anestesi=false;
                        akses01.satu_sehat_kirim_procedure=false;
                        akses01.operasi_per_bulan=false;
                        akses01.timeout_sebelum_insisi=false;
                        akses01.signout_sebelum_menutup_luka=false;
                        akses01.dapur_barang=false;
                        akses01.dapur_opname=false;
                        akses01.satu_sehat_mapping_vaksin=false;
                        akses01.dapur_suplier=false;
                        akses01.satu_sehat_kirim_Immunization=false;
                        akses01.checklist_post_operasi=false;
                        akses01.dapur_pembelian=false;
                        akses01.dapur_stok_keluar=false;
                        akses01.dapur_riwayat_barang=false;
                        akses01.permintaan_dapur=false;
                        akses01.rekonsiliasi_obat=false;
                        akses01.biaya_pengadaan_dapur=false;
                        akses01.rekap_pengadaan_dapur=false;
                        akses01.kesling_limbah_b3medis_cair=false;
                        akses01.grafik_limbahb3cair_pertanggal=false;
                        akses01.grafik_limbahb3cair_perbulan=false;
                        akses01.rekap_biaya_registrasi=false;
                        akses01.konfirmasi_rekonsiliasi_obat=false;
                        akses01.satu_sehat_kirim_clinicalimpression=false;
                        akses01.penilaian_pasien_terminal=false;
                        akses01.surat_persetujuan_rawat_inap=false;
                        akses01.monitoring_reaksi_tranfusi=false;
                        akses01.penilaian_korban_kekerasan=false;
                        akses01.penilaian_lanjutan_resiko_jatuh_lansia=false;
                        akses01.mpp_skrining=false;
                        akses01.penilaian_pasien_penyakit_menular=false;
                        akses01.edukasi_pasien_keluarga_rj=false;
                        akses01.pemantauan_pews_dewasa=false;
                        akses01.penilaian_tambahan_bunuh_diri=false;
                        akses01.bpjs_antrean_pertanggal=false;
                        akses01.penilaian_tambahan_perilaku_kekerasan=false;
                        akses01.penilaian_tambahan_beresiko_melarikan_diri=false;
                        akses01.persetujuan_penundaan_pelayanan=false;
                        akses01.sisa_diet_pasien=false;
                        akses01.penilaian_awal_medis_ralan_bedah_mulut=false;
                        akses01.penilaian_pasien_keracunan=false;
                        akses01.pemantauan_meows_obstetri=false;
                        akses01.catatan_adime_gizi=false;
                        akses01.pengajuan_biaya=false;
                        akses01.penilaian_awal_keperawatan_ralan_geriatri=false;
                        akses01.master_masalah_keperawatan_geriatri=false;
                        akses01.master_rencana_keperawatan_geriatri=false;
                        akses01.checklist_kriteria_masuk_hcu=false;
                        akses01.checklist_kriteria_keluar_hcu=false;
                        akses01.penilaian_risiko_dekubitus=false;
                        akses01.master_menolak_anjuran_medis=false;
                        akses01.penolakan_anjuran_medis=false;
                        akses01.laporan_tahunan_penolakan_anjuran_medis=false;
                        akses01.template_laporan_operasi=false;
                        akses01.hasil_tindakan_eswl=false;
                        akses01.checklist_kriteria_masuk_icu=false;
                        akses01.checklist_kriteria_keluar_icu=false;
                        akses01.akses_dokter_lain_rawat_jalan=false;
                        akses01.follow_up_dbd=false;
                        akses01.penilaian_risiko_jatuh_neonatus=false;
                        akses01.persetujuan_pengajuan_biaya=false;
                        akses01.pemeriksaan_fisik_ralan_per_penyakit=false;
                        akses01.penilaian_lanjutan_resiko_jatuh_geriatri=false;
                        akses01.pemantauan_ews_neonatus=false;
                        akses01.validasi_persetujuan_pengajuan_biaya=false;
                        akses01.riwayat_perawatan_icare_bpjs=false;
                        akses01.rekap_pengajuan_biaya=false;
                        akses01.penilaian_awal_medis_ralan_kulit_kelamin=false;
                        akses01.akun_host_to_host_bank_mandiri=false;
                        akses01.penilaian_medis_hemodialisa=false;
                        akses01.penilaian_level_kecemasan_ranap_anak=false;
                        akses01.penilaian_lanjutan_resiko_jatuh_psikiatri=false;
                        akses01.penilaian_lanjutan_skrining_fungsional=false;
                        akses01.penilaian_medis_ralan_rehab_medik=false;
                        akses01.laporan_anestesi=false;
                        akses01.template_persetujuan_penolakan_tindakan=false;
                        akses01.penilaian_medis_ralan_gawat_darurat_psikiatri=false;
                        akses01.bpjs_referensi_setting_apotek=false;
                        akses01.bpjs_referensi_obat_apotek=false;
                        akses01.bpjs_mapping_obat_apotek=false;
                        akses01.pembayaran_bank_mandiri=false;
                        akses01.penilaian_ulang_nyeri=false;
                        akses01.penilaian_terapi_wicara=false;
                        akses01.bpjs_obat_23hari_apotek=false;
                        akses01.pengkajian_restrain=false;
                        akses01.bpjs_kunjungan_sep_apotek=false;
                        akses01.bpjs_monitoring_klaim_apotek=false;
                        akses01.bpjs_daftar_pelayanan_obat_apotek=false;
                        akses01.penilaian_awal_medis_ralan_paru=false;
                        akses01.catatan_keperawatan_ralan=false;
                        akses01.catatan_persalinan=false;
                        akses01.skor_aldrette_pasca_anestesi=false;
                        akses01.skor_steward_pasca_anestesi=false;
                        akses01.skor_bromage_pasca_anestesi=false;
                        akses01.penilaian_pre_induksi=false;
                        akses01.hasil_usg_urologi=false;
                        akses01.hasil_usg_gynecologi=false;
                        akses01.hasil_pemeriksaan_ekg=false;
                        akses01.hapus_edit_sep_bpjs=false;
                        akses01.satu_sehat_kirim_diet=false;
                        akses01.satu_sehat_mapping_obat=false;
                        akses01.dapur_ringkasan_pembelian=false;
                        akses01.satu_sehat_kirim_medication=false;
                        akses01.satu_sehat_kirim_medicationrequest=false;
                        akses01.penatalaksanaan_terapi_okupasi=false;
                        akses01.satu_sehat_kirim_medicationdispense=false;
                        akses01.hasil_usg_neonatus=false;
                        akses01.hasil_endoskopi_faring_laring=false;
                        akses01.satu_sehat_mapping_radiologi=false;
                        akses01.satu_sehat_kirim_servicerequest_radiologi=false;
                        akses01.hasil_endoskopi_hidung=false;
                        akses01.satu_sehat_kirim_specimen_radiologi=false;
                        akses01.master_masalah_keperawatan_neonatus=false;
                        akses01.master_rencana_keperawatan_neonatus=false;
                        akses01.penilaian_awal_keperawatan_ranap_neonatus=false;
                        akses01.satu_sehat_kirim_observation_radiologi=false;
                        akses01.satu_sehat_kirim_diagnosticreport_radiologi=false;
                        akses01.hasil_endoskopi_telinga=false;
                        akses01.satu_sehat_mapping_lab=false;
                        akses01.satu_sehat_kirim_servicerequest_lab=false;
                        akses01.satu_sehat_kirim_servicerequest_labmb=false;
                        akses01.satu_sehat_kirim_specimen_lab=false;
                        akses01.satu_sehat_kirim_specimen_labmb=false;
                        akses01.satu_sehat_kirim_observation_lab=false;
                        akses01.satu_sehat_kirim_observation_labmb=false;
                        akses01.satu_sehat_kirim_diagnosticreport_lab=false;
                        akses01.satu_sehat_kirim_diagnosticreport_labmb=false;
                        akses01.kepatuhan_kelengkapan_keselamatan_bedah=false;
                        akses01.nilai_piutang_perjenis_bayar_per_bulan=false;
                        akses01.ringkasan_piutang_jenis_bayar=false;
                        akses01.penilaian_pasien_imunitas_rendah=false;
                        akses01.balance_cairan=false;
                        akses01.catatan_observasi_chbp=false;
                        akses01.catatan_observasi_induksi_persalinan=false;
                        akses01.skp_kategori_penilaian=false;
                        akses01.skp_kriteria_penilaian=false;
                        akses01.skp_penilaian=false;
                        akses01.referensi_poli_mobilejknfktp=false;
                        akses01.referensi_dokter_mobilejknfktp=false;
                        akses01.skp_rekapitulasi_penilaian=false;
                        akses01.pembayaran_pihak_ke3_bankmandiri=false;
                        akses01.metode_pembayaran_bankmandiri=false;
                        akses01.bank_tujuan_transfer_bankmandiri=false;
                        akses01.kodetransaksi_tujuan_transfer_bankmandiri=false;
                        akses01.konsultasi_medik=false;
                        akses01.jawaban_konsultasi_medik=false;
                        akses01.pcare_cek_alergi=false;
                        akses01.pcare_cek_prognosa=false;
                        akses01.data_sasaran_usiaproduktif=false;
                        akses01.data_sasaran_usialansia=false;
                        akses01.skrining_perilaku_merokok_sekolah_remaja=false;
                        akses01.skrining_kekerasan_pada_perempuan=false;
                        akses01.skrining_obesitas=false;
                        akses01.skrining_risiko_kanker_payudara=false;
                        akses01.skrining_risiko_kanker_paru=false;
                        akses01.skrining_tbc=false;
                        akses01.skrining_kesehatan_gigi_mulut_remaja=false;
                        akses01.penilaian_awal_keperawatan_ranap_bayi=false;
                        akses01.booking_mcu_perusahaan=false;
                        akses01.catatan_observasi_restrain_nonfarma=false;
                        akses01.catatan_observasi_ventilator=false;
                        akses01.catatan_anestesi_sedasi=false;
                        akses01.skrining_puma=false;
                        akses01.satu_sehat_kirim_careplan=false;
                        akses01.satu_sehat_kirim_medicationstatement=false;
                        akses01.skrining_adiksi_nikotin=false;
                        akses01.skrining_thalassemia=false;
                        akses01.skrining_instrumen_sdq=false;
                        akses01.skrining_instrumen_srq=false;
                        akses01.checklist_pemberian_fibrinolitik=false;
                        akses01.skrining_kanker_kolorektal=false;
                        akses01.dapur_pemesanan=false;
                        akses01.bayar_pesan_dapur=false;
                        akses01.hutang_dapur=false;
                        akses01.titip_faktur_dapur=false;
                        akses01.validasi_tagihan_dapur=false;
                        akses01.surat_pemesanan_dapur=false;
                        akses01.pengajuan_barang_dapur=false;
                        akses01.dapur_returbeli=false;
                        akses01.hibah_dapur=false;
                        akses01.ringkasan_penerimaan_dapur=false;
                        akses01.ringkasan_pengajuan_dapur=false;
                        akses01.ringkasan_pemesanan_dapur=false;
                        akses01.ringkasan_returbeli_dapur=false;
                        akses01.ringkasan_stokkeluar_dapur=false;
                        akses01.dapur_stokkeluar_pertanggal=false;
                        akses01.sirkulasi_dapur=false;
                        akses01.sirkulasi_dapur2=false;
                        akses01.verifikasi_penerimaan_dapur=false;
                        akses01.nilai_penerimaan_vendor_dapur_perbulan=false;
                        akses01.ringkasan_hutang_vendor_dapur=false;
                        akses01.penilaian_psikologi_klinis=false;
                        akses01.penilaian_awal_medis_ranap_neonatus=false;
                        akses01.penilaian_derajat_dehidrasi=false;
                        akses01.ringkasan_jasa_tindakan_medis=false;
                        akses01.pendapatan_per_akun=false;
                        akses01.hasil_pemeriksaan_echo=false;
                        akses01.penilaian_bayi_baru_lahir=false;
                        akses01.rl1_3_ketersediaan_kamar=false;
                        akses01.pendapatan_per_akun_closing=false;
                        akses01.pengeluaran_pengeluaran=false;
                        akses01.skrining_diabetes_melitus=false;
                        akses01.laporan_tindakan=false;
                        akses01.pelaksanaan_informasi_edukasi=false;
                        akses01.layanan_kedokteran_fisik_rehabilitasi=false;
                        akses01.skrining_kesehatan_gigi_mulut_balita=false;
                        akses01.skrining_anemia=false;
                        akses01.layanan_program_kfr=false;
                        akses01.skrining_hipertensi=false;
                        akses01.skrining_kesehatan_penglihatan=false;
                        akses01.catatan_observasi_hemodialisa=false;
                        akses01.skrining_kesehatan_gigi_mulut_dewasa=false;
                        akses01.skrining_risiko_kanker_serviks=false;
                        akses01.catatan_cairan_hemodialisa=false;
                        akses01.skrining_kesehatan_gigi_mulut_lansia=false;
                        akses01.skrining_indra_pendengaran=false;
                        akses01.catatan_pengkajian_paska_operasi=false;
                        akses01.skrining_frailty_syndrome=false;
                        akses01.sirkulasi_cssd=false;
                        akses01.lama_pelayanan_cssd=false;
                        akses01.catatan_observasi_bayi=false;
                        akses01.riwayat_surat_peringatan=false;
                        akses01.master_kesimpulan_anjuran_mcu=false;
                        akses01.kategori_piutang_jasa_perusahaan=false;
                        akses01.piutang_jasa_perusahaan=false;
                        akses01.bayar_piutang_jasa_perusahaan=false;
                        akses01.piutang_jasa_perusahaan_belum_lunas=false;
                    }
                } catch (Exception e) {
                    System.out.println("Notifikasi : "+e);
                } finally{
                    if(rs!=null){
                        rs.close();
                    }
                    if(rs2!=null){
                        rs2.close();
                    }
                    if(ps!=null){
                        ps.close();
                    }
                    if(ps2!=null){
                        ps2.close();
                    }
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            }

    }
    
    public static void setLogOut(){
        akses01.kode="";                  
        akses01.penyakit= false;
        akses01.obat_penyakit= false;
        akses01.dokter= false;
        akses01.jadwal_praktek= false;
        akses01.petugas= false;
        akses01.pasien= false;
        akses01.registrasi= false;
        akses01.tindakan_ralan= false;
        akses01.kamar_inap= false;
        akses01.tindakan_ranap= false;
        akses01.operasi= false;
        akses01.rujukan_keluar= false;
        akses01.rujukan_masuk= false;
        akses01.beri_obat= false;
        akses01.resep_pulang= false;
        akses01.pasien_meninggal= false;
        akses01.diet_pasien= false;
        akses01.kelahiran_bayi= false;
        akses01.periksa_lab= false;
        akses01.periksa_radiologi= false;
        akses01.kasir_ralan= false;
        akses01.deposit_pasien= false;
        akses01.piutang_pasien= false;
        akses01.peminjaman_berkas= false;
        akses01.barcode= false;
        akses01.presensi_harian= false;
        akses01.presensi_bulanan= false;
        akses01.pegawai_admin= false;
        akses01.pegawai_user= false;
        akses01.suplier= false;
        akses01.satuan_barang= false;
        akses01.konversi_satuan= false;
        akses01.jenis_barang= false;
        akses01.obat= false;
        akses01.stok_opname_obat= false;
        akses01.stok_obat_pasien= false;
        akses01.pengadaan_obat= false;
        akses01.pemesanan_obat= false;
        akses01.penjualan_obat= false;
        akses01.piutang_obat= false;
        akses01.retur_ke_suplier= false;
        akses01.retur_dari_pembeli= false;
        akses01.retur_obat_ranap= false;
        akses01.retur_piutang_pasien= false;
        akses01.keuntungan_penjualan= false;
        akses01.keuntungan_beri_obat= false;
        akses01.sirkulasi_obat= false;
        akses01.ipsrs_barang= false;
        akses01.ipsrs_pengadaan_barang= false;
        akses01.ipsrs_stok_keluar= false;
        akses01.ipsrs_rekap_pengadaan= false;
        akses01.ipsrs_rekap_stok_keluar= false;
        akses01.ipsrs_pengeluaran_harian= false;
        akses01.ipsrs_jenis_barang=false;
        akses01.inventaris_jenis= false;
        akses01.inventaris_kategori= false;
        akses01.inventaris_merk= false;
        akses01.inventaris_ruang= false;
        akses01.inventaris_produsen= false;
        akses01.inventaris_koleksi= false;
        akses01.inventaris_inventaris= false;
        akses01.inventaris_sirkulasi= false;
        akses01.parkir_jenis= false;
        akses01.parkir_in= false;
        akses01.parkir_out= false;
        akses01.parkir_rekap_harian= false;
        akses01.parkir_rekap_bulanan= false;
        akses01.informasi_kamar= false;
        akses01.harian_tindakan_poli= false;
        akses01.obat_per_poli= false;
        akses01.obat_per_kamar= false;
        akses01.obat_per_dokter_ralan= false;
        akses01.obat_per_dokter_ranap= false;
        akses01.harian_dokter= false;
        akses01.bulanan_dokter= false;
        akses01.harian_paramedis= false;
        akses01.bulanan_paramedis= false;
        akses01.pembayaran_ralan= false;
        akses01.pembayaran_ranap= false;
        akses01.rekap_pembayaran_ralan= false;
        akses01.rekap_pembayaran_ranap= false;
        akses01.tagihan_masuk= false;
        akses01.tambahan_biaya= false;
        akses01.potongan_biaya= false;
        akses01.resep_obat= false;
        akses01.resume_pasien= false;
        akses01.penyakit_ralan= false;
        akses01.penyakit_ranap= false;
        akses01.kamar= false;
        akses01.tarif_ralan= false;
        akses01.tarif_ranap= false;
        akses01.tarif_lab= false;
        akses01.tarif_radiologi= false;
        akses01.tarif_operasi= false;
        akses01.akun_rekening= false;
        akses01.rekening_tahun= false;
        akses01.posting_jurnal= false;
        akses01.buku_besar= false;
        akses01.cashflow= false;
        akses01.keuangan= false;
        akses01.pengeluaran= false;
        akses01.setup_pjlab= false;
        akses01.setup_otolokasi= false;
        akses01.setup_jam_kamin= false;
        akses01.setup_embalase= false;
        akses01.tracer_login= false;
        akses01.display= false;
        akses01.set_harga_obat= false;
        akses01.set_penggunaan_tarif= false;
        akses01.set_oto_ralan= false;
        akses01.biaya_harian= false;
        akses01.biaya_masuk_sekali= false;
        akses01.set_no_rm= false;
        akses01.billing_ralan=false;
        akses01.billing_ranap=false;
        akses01.jm_ranap_dokter=false;   
        akses01.igd=false;   
        akses01.barcoderalan=false; 
        akses01.barcoderanap=false;
        akses01.set_harga_obat_ralan=false; 
        akses01.set_harga_obat_ranap=false;
        akses01.admin= false;
        akses01.user= false;
        akses01.vakum= false;
        akses01.aplikasi= false;
        akses01.penyakit_pd3i=false;
        akses01.surveilans_pd3i=false;
        akses01.surveilans_ralan=false;
        akses01.diagnosa_pasien=false;
        akses01.surveilans_ranap=false;
        akses01.pny_takmenular_ranap=false;
        akses01.pny_takmenular_ralan=false;
        akses01.kunjungan_ralan=false;
        akses01.rl32=false;
        akses01.rl33=false;
        akses01.rl37=false;
        akses01.rl38=false;
        akses01.harian_tindakan_dokter=false;
        akses01.sms=false;             
        akses01.sidikjari=false;  
        akses01.jam_masuk=false;  
        akses01.jadwal_pegawai=false;   
        akses01.parkir_barcode=false;
        akses01.set_nota=false;
        akses01.dpjp_ranap=false;
        akses01.mutasi_barang=false;
        akses01.rl34=false;
        akses01.rl36=false;
        akses01.fee_visit_dokter=false;
        akses01.fee_bacaan_ekg=false;
        akses01.fee_rujukan_rontgen=false;
        akses01.fee_rujukan_ranap=false;
        akses01.fee_ralan=false;
        akses01.akun_bayar=false;
        akses01.bayar_pemesanan_obat=false;
        akses01.obat_per_dokter_peresep=false;
        akses01.pemasukan_lain=false;
        akses01.pengaturan_rekening=false;
        akses01.closing_kasir=false;
        akses01.keterlambatan_presensi=false; 
        akses01.set_harga_kamar=false;
        akses01.rekap_per_shift=false;
        akses01.bpjs_cek_nik=false;
        akses01.bpjs_cek_kartu=false;
        akses01.bpjs_cek_riwayat=false;
        akses01.obat_per_cara_bayar=false;
        akses01.kunjungan_ranap=false;
        akses01.bayar_piutang=false;
        akses01.payment_point=false;
        akses01.bpjs_cek_nomor_rujukan=false;
        akses01.icd9=false;
        akses01.darurat_stok=false;
        akses01.retensi_rm=false;
        akses01.temporary_presensi=false;
        akses01.jurnal_harian=false;
        akses01.sirkulasi_obat2=false;
        akses01.edit_registrasi=false;
        akses01.bpjs_referensi_diagnosa=false;
        akses01.bpjs_referensi_poli=false;
        akses01.industrifarmasi=false;
        akses01.harian_js=false;
        akses01.bulanan_js=false;
        akses01.harian_paket_bhp=false;
        akses01.bulanan_paket_bhp=false;
        akses01.piutang_pasien2=false;
        akses01.bpjs_referensi_faskes=false;
        akses01.bpjs_sep=false;
        akses01.pengambilan_utd=false;
        akses01.tarif_utd=false;
        akses01.pengambilan_utd2=false;
        akses01.utd_medis_rusak=false;
        akses01.pengambilan_penunjang_utd=false;
        akses01.pengambilan_penunjang_utd2=false;
        akses01.utd_penunjang_rusak=false;
        akses01.suplier_penunjang=false;
        akses01.utd_donor=false;
        akses01.bpjs_monitoring_klaim=false;
        akses01.utd_cekal_darah=false;
        akses01.utd_komponen_darah=false;
        akses01.utd_stok_darah=false;
        akses01.utd_pemisahan_darah=false;
        akses01.harian_kamar=false;
        akses01.rincian_piutang_pasien=false;
        akses01.keuntungan_beri_obat_nonpiutang=false;
        akses01.reklasifikasi_ralan=false;
        akses01.reklasifikasi_ranap=false;
        akses01.utd_penyerahan_darah=false;
        akses01.hutang_obat=false;
        akses01.riwayat_obat_alkes_bhp=false;
        akses01.sensus_harian_poli=false;
        akses01.rl4a=false;
        akses01.aplicare_referensi_kamar=false;
        akses01.aplicare_ketersediaan_kamar=false;
        akses01.inacbg_klaim_baru_otomatis=false;
        akses01.inacbg_klaim_baru_manual=false;
        akses01.inacbg_coder_nik=false;
        akses01.mutasi_berkas=false;
        akses01.akun_piutang=false;
        akses01.harian_kso=false;
        akses01.bulanan_kso=false;                                    
        akses01.harian_menejemen=false;
        akses01.bulanan_menejemen=false;
        akses01.inhealth_cek_eligibilitas=false;
        akses01.inhealth_referensi_jenpel_ruang_rawat=false;
        akses01.inhealth_referensi_poli=false;
        akses01.inhealth_referensi_faskes=false;
        akses01.inhealth_sjp=false;
        akses01.piutang_ralan=false;
        akses01.piutang_ranap=false;
        akses01.detail_piutang_penjab=false;
        akses01.lama_pelayanan_ralan=false;
        akses01.catatan_pasien=false;
        akses01.rl4b=false;              
        akses01.rl4asebab=false;
        akses01.rl4bsebab=false;
        akses01.data_HAIs=false;
        akses01.harian_HAIs=false;
        akses01.bulanan_HAIs=false;
        akses01.hitung_bor=false;
        akses01.perusahaan_pasien=false;
        akses01.resep_dokter=false;
        akses01.lama_pelayanan_apotek=false;
        akses01.hitung_alos=false;
        akses01.detail_tindakan=false;
        akses01.rujukan_poli_internal=false;
        akses01.rekap_poli_anak=false;
        akses01.grafik_kunjungan_poli=false;
        akses01.grafik_kunjungan_perdokter=false;
        akses01.grafik_kunjungan_perpekerjaan=false;
        akses01.grafik_kunjungan_perpendidikan=false;
        akses01.grafik_kunjungan_pertahun=false;
        akses01.berkas_digital_perawatan=false;
        akses01.penyakit_menular_ranap=false;
        akses01.penyakit_menular_ralan=false;
        akses01.grafik_kunjungan_perbulan=false;                    
        akses01.grafik_kunjungan_pertanggal=false;
        akses01.grafik_kunjungan_demografi=false;
        akses01.grafik_kunjungan_statusdaftartahun=false;
        akses01.grafik_kunjungan_statusdaftartahun2=false;                        
        akses01.grafik_kunjungan_statusdaftarbulan=false;
        akses01.grafik_kunjungan_statusdaftarbulan2=false;
        akses01.grafik_kunjungan_statusdaftartanggal=false;
        akses01.grafik_kunjungan_statusdaftartanggal2=false;
        akses01.grafik_kunjungan_statusbataltahun=false;
        akses01.grafik_kunjungan_statusbatalbulan=false;
        akses01.pcare_cek_penyakit=false;
        akses01.grafik_kunjungan_statusbataltanggal=false;
        akses01.kategori_barang=false;
        akses01.golongan_barang=false;
        akses01.pemberian_obat_pertanggal=false;
        akses01.penjualan_obat_pertanggal=false;
        akses01.pcare_cek_kesadaran=false;
        akses01.pembatalan_periksa_dokter=false;
        akses01.pembayaran_per_unit=false;
        akses01.rekap_pembayaran_per_unit=false;
        akses01.grafik_kunjungan_percarabayar=false;
        akses01.ipsrs_pengadaan_pertanggal=false;
        akses01.ipsrs_stokkeluar_pertanggal=false;
        akses01.grafik_kunjungan_ranaptahun=false;
        akses01.pcare_cek_rujukan=false;
        akses01.grafik_lab_ralantahun=false;
        akses01.grafik_rad_ralantahun=false;
        akses01.cek_entry_ralan=false;
        akses01.inacbg_klaim_baru_manual2=false;
        akses01.permintaan_medis=false;
        akses01.rekap_permintaan_medis=false;
        akses01.surat_pemesanan_medis=false;
        akses01.permintaan_non_medis=false;
        akses01.rekap_permintaan_non_medis=false;
        akses01.surat_pemesanan_non_medis=false;
        akses01.grafik_per_perujuk=false;
        akses01.bpjs_cek_prosedur=false;
        akses01.bpjs_cek_kelas_rawat=false;
        akses01.bpjs_cek_dokter=false;
        akses01.bpjs_cek_spesialistik=false;
        akses01.bpjs_cek_ruangrawat=false;
        akses01.bpjs_cek_carakeluar=false;
        akses01.bpjs_cek_pasca_pulang=false;
        akses01.detail_tindakan_okvk=false;
        akses01.billing_parsial=false;
        akses01.bpjs_cek_nomor_rujukan_rs=false;
        akses01.bpjs_cek_rujukan_kartu_pcare=false;
        akses01.bpjs_cek_rujukan_kartu_rs=false;
        akses01.akses_depo_obat=false;
        akses01.bpjs_rujukan_keluar=false;
        akses01.grafik_lab_ralanbulan=false;
        akses01.pengeluaran_stok_apotek=false;
        akses01.grafik_rad_ralanbulan=false;
        akses01.detailjmdokter2=false;
        akses01.pengaduan_pasien=false;
        akses01.grafik_lab_ralanhari=false;
        akses01.grafik_rad_ralanhari=false;
        akses01.sensus_harian_ralan=false;
        akses01.metode_racik=false;
        akses01.pembayaran_akun_bayar=false;
        akses01.pengguna_obat_resep=false;
        akses01.rekap_pemesanan=false;
        akses01.master_berkas_pegawai=false;
        akses01.berkas_kepegawaian=false;
        akses01.riwayat_jabatan=false;
        akses01.riwayat_pendidikan=false;
        akses01.riwayat_naik_gaji=false;
        akses01.kegiatan_ilmiah=false;
        akses01.riwayat_penghargaan=false;
        akses01.riwayat_penelitian=false;
        akses01.penerimaan_non_medis=false;
        akses01.bayar_pesan_non_medis=false;
        akses01.hutang_barang_non_medis=false;
        akses01.rekap_pemesanan_non_medis=false;                      
        akses01.insiden_keselamatan=false;
        akses01.insiden_keselamatan_pasien=false;
        akses01.grafik_ikp_pertahun=false;
        akses01.grafik_ikp_perbulan=false;
        akses01.grafik_ikp_pertanggal=false;
        akses01.riwayat_data_batch=false;
        akses01.grafik_ikp_jenis=false;
        akses01.grafik_ikp_dampak=false;
        akses01.piutang_akun_piutang=false;
        akses01.grafik_kunjungan_per_agama=false;
        akses01.grafik_kunjungan_per_umur=false;
        akses01.suku_bangsa=false;
        akses01.bahasa_pasien=false;
        akses01.golongan_tni=false;
        akses01.satuan_tni=false;
        akses01.jabatan_tni=false;
        akses01.pangkat_tni=false;
        akses01.golongan_polri=false;
        akses01.satuan_polri=false;
        akses01.jabatan_polri=false;
        akses01.pangkat_polri=false;
        akses01.cacat_fisik=false;
        akses01.grafik_kunjungan_suku=false;
        akses01.grafik_kunjungan_bahasa=false;
        akses01.booking_operasi=false;
        akses01.mapping_poli_bpjs=false;
        akses01.grafik_kunjungan_per_cacat=false;
        akses01.barang_cssd=false;
        akses01.skdp_bpjs=false;
        akses01.booking_registrasi=false;
        akses01.bpjs_cek_propinsi=false;
        akses01.bpjs_cek_kabupaten=false;
        akses01.bpjs_cek_kecamatan=false;
        akses01.bpjs_cek_dokterdpjp=false;
        akses01.bpjs_cek_riwayat_rujukanrs=false;
        akses01.bpjs_cek_tanggal_rujukan=false;
        akses01.permintaan_lab=false;
        akses01.permintaan_radiologi=false;
        akses01.surat_indeks=false;
        akses01.surat_map=false;
        akses01.surat_almari=false;
        akses01.surat_rak=false;
        akses01.surat_ruang=false;
        akses01.surat_klasifikasi=false;
        akses01.surat_status=false;
        akses01.surat_sifat=false;
        akses01.surat_balas=false;
        akses01.surat_masuk=false;
        akses01.pcare_cek_dokter=false;
        akses01.pcare_cek_poli=false;
        akses01.pcare_cek_provider=false;
        akses01.pcare_cek_statuspulang=false;
        akses01.pcare_cek_spesialis=false;
        akses01.pcare_cek_subspesialis=false;
        akses01.pcare_cek_sarana=false;
        akses01.pcare_cek_khusus=false;
        akses01.pcare_cek_obat=false;
        akses01.pcare_cek_tindakan=false;
        akses01.pcare_cek_faskessubspesialis=false;
        akses01.pcare_cek_faskesalihrawat=false;
        akses01.pcare_cek_faskesthalasemia=false;
        akses01.pcare_mapping_obat=false;
        akses01.pcare_mapping_tindakan=false;
        akses01.pcare_club_prolanis=false;
        akses01.pcare_mapping_poli=false;
        akses01.pcare_kegiatan_kelompok=false;
        akses01.pcare_mapping_tindakan_ranap=false;
        akses01.pcare_peserta_kegiatan_kelompok=false;
        akses01.sirkulasi_obat3=false;
        akses01.bridging_pcare_daftar=false;
        akses01.pcare_mapping_dokter=false;
        akses01.ranap_per_ruang=false;
        akses01.penyakit_ranap_cara_bayar=false;
        akses01.anggota_militer_dirawat=false;
        akses01.set_input_parsial=false;
        akses01.lama_pelayanan_radiologi=false;
        akses01.lama_pelayanan_lab=false;
        akses01.bpjs_cek_sep=false;
        akses01.catatan_perawatan=false;
        akses01.surat_keluar=false;
        akses01.kegiatan_farmasi=false;
        akses01.stok_opname_logistik=false;
        akses01.sirkulasi_non_medis=false;
        akses01.rekap_lab_pertahun=false;
        akses01.perujuk_lab_pertahun=false;
        akses01.rekap_radiologi_pertahun=false;
        akses01.perujuk_radiologi_pertahun=false;
        akses01.jumlah_porsi_diet=false;
        akses01.jumlah_macam_diet=false;
        akses01.payment_point2=false;
        akses01.pembayaran_akun_bayar2=false;
        akses01.hapus_nota_salah=false;
        akses01.hais_perbangsal=false;
        akses01.ppn_obat=false;
        akses01.saldo_akun_perbulan=false;
        akses01.display_apotek=false;
        akses01.sisrute_referensi_faskes=false;
        akses01.sisrute_referensi_alasanrujuk=false;
        akses01.sisrute_referensi_diagnosa=false;
        akses01.sisrute_rujukan_masuk=false;
        akses01.sisrute_rujukan_keluar=false;
        akses01.bpjs_cek_skdp=false;
        akses01.data_batch=false;
        akses01.kunjungan_permintaan_lab=false;
        akses01.kunjungan_permintaan_lab2=false;
        akses01.kunjungan_permintaan_radiologi=false;
        akses01.kunjungan_permintaan_radiologi2=false;
        akses01.pcare_pemberian_obat=false;
        akses01.pcare_pemberian_tindakan=false;
        akses01.pembayaran_akun_bayar3=false;
        akses01.password_asuransi=false;
        akses01.kemenkes_sitt=false;
        akses01.siranap_ketersediaan_kamar=false;
        akses01.grafik_tb_periodelaporan=false;
        akses01.grafik_tb_rujukan=false;
        akses01.grafik_tb_riwayat=false;
        akses01.grafik_tb_tipediagnosis=false;
        akses01.grafik_tb_statushiv=false;
        akses01.grafik_tb_skoringanak=false;
        akses01.grafik_tb_konfirmasiskoring5=false;
        akses01.grafik_tb_konfirmasiskoring6=false;
        akses01.grafik_tb_sumberobat=false;
        akses01.grafik_tb_hasilakhirpengobatan=false;
        akses01.grafik_tb_hasilteshiv=false;
        akses01.kadaluarsa_batch=false;
        akses01.sisa_stok=false;
        akses01.obat_per_resep=false;
        akses01.pemakaian_air_pdam=false;
        akses01.limbah_b3_medis=false;
        akses01.grafik_air_pdam_pertanggal=false;
        akses01.grafik_air_pdam_perbulan=false;
        akses01.grafik_limbahb3_pertanggal=false;
        akses01.grafik_limbahb3_perbulan=false;
        akses01.limbah_domestik=false;
        akses01.grafik_limbahdomestik_pertanggal=false;
        akses01.grafik_limbahdomestik_perbulan=false;
        akses01.mutu_air_limbah=false;
        akses01.pest_control=false;
        akses01.ruang_perpustakaan=false;
        akses01.kategori_perpustakaan=false;
        akses01.jenis_perpustakaan=false;
        akses01.pengarang_perpustakaan=false;
        akses01.penerbit_perpustakaan=false;
        akses01.koleksi_perpustakaan=false;
        akses01.inventaris_perpustakaan=false;
        akses01.set_peminjaman_perpustakaan=false;
        akses01.denda_perpustakaan=false;
        akses01.anggota_perpustakaan=false;
        akses01.peminjaman_perpustakaan=false;
        akses01.bayar_denda_perpustakaan=false;
        akses01.ebook_perpustakaan=false;
        akses01.jenis_cidera_k3rs=false;
        akses01.penyebab_k3rs=false;
        akses01.jenis_luka_k3rs=false;
        akses01.lokasi_kejadian_k3rs=false;
        akses01.dampak_cidera_k3rs=false;
        akses01.jenis_pekerjaan_k3rs=false;
        akses01.bagian_tubuh_k3rs=false;
        akses01.peristiwa_k3rs=false;
        akses01.grafik_k3_pertahun=false;
        akses01.grafik_k3_perbulan=false;
        akses01.grafik_k3_pertanggal=false;
        akses01.grafik_k3_perjeniscidera=false;
        akses01.grafik_k3_perpenyebab=false;
        akses01.grafik_k3_perjenisluka=false;
        akses01.grafik_k3_lokasikejadian=false;
        akses01.grafik_k3_dampakcidera=false;
        akses01.grafik_k3_perjenispekerjaan=false;
        akses01.grafik_k3_perbagiantubuh=false;
        akses01.jenis_cidera_k3rstahun=false;
        akses01.penyebab_k3rstahun=false;
        akses01.jenis_luka_k3rstahun=false;
        akses01.lokasi_kejadian_k3rstahun=false;
        akses01.dampak_cidera_k3rstahun=false;
        akses01.jenis_pekerjaan_k3rstahun=false;
        akses01.bagian_tubuh_k3rstahun=false;
        akses01.sekrining_rawat_jalan=false;
        akses01.bpjs_histori_pelayanan=false;
        akses01.rekap_mutasi_berkas=false;
        akses01.skrining_ralan_pernapasan_pertahun=false;
        akses01.pengajuan_barang_medis=false;
        akses01.pengajuan_barang_nonmedis=false;
        akses01.grafik_kunjungan_ranapbulan=false;
        akses01.grafik_kunjungan_ranaptanggal=false;
        akses01.grafik_kunjungan_ranap_peruang=false;
        akses01.kunjungan_bangsal_pertahun=false;
        akses01.grafik_jenjang_jabatanpegawai=false;
        akses01.grafik_bidangpegawai=false;
        akses01.grafik_departemenpegawai=false;
        akses01.grafik_pendidikanpegawai=false;
        akses01.grafik_sttswppegawai=false;
        akses01.grafik_sttskerjapegawai=false;
        akses01.grafik_sttspulangranap=false;
        akses01.kip_pasien_ranap=false;
        akses01.kip_pasien_ralan=false;
        akses01.bpjs_mapping_dokterdpjp=false;
        akses01.data_triase_igd=false;
        akses01.master_triase_skala1=false;
        akses01.master_triase_skala2=false;
        akses01.master_triase_skala3=false;
        akses01.master_triase_skala4=false;
        akses01.master_triase_skala5=false;
        akses01.master_triase_pemeriksaan=false;
        akses01.master_triase_macamkasus=false;
        akses01.rekap_permintaan_diet=false;
        akses01.daftar_pasien_ranap=false;
        akses01.daftar_pasien_ranaptni=false;
        akses01.pengajuan_asetinventaris=false;
        akses01.item_apotek_jenis=false;
        akses01.item_apotek_kategori=false;
        akses01.item_apotek_golongan=false;
        akses01.item_apotek_industrifarmasi=false;
        akses01.obat10_terbanyak_poli=false;
        akses01.grafik_pengajuan_aset_urgensi=false;
        akses01.grafik_pengajuan_aset_status=false;
        akses01.grafik_pengajuan_aset_departemen=false;
        akses01.rekap_pengajuan_aset_departemen=false;
        akses01.grafik_kelompok_jabatanpegawai=false;
        akses01.grafik_resiko_kerjapegawai=false;
        akses01.grafik_emergency_indexpegawai=false;
        akses01.grafik_inventaris_ruang=false;
        akses01.harian_HAIs2=false;
        akses01.grafik_inventaris_jenis=false;
        akses01.data_resume_pasien=false;
        akses01.perkiraan_biaya_ranap=false;
        akses01.rekap_obat_poli=false;
        akses01.rekap_obat_pasien=false;
        akses01.grafik_HAIs_pasienbangsal=false;
        akses01.grafik_HAIs_pasienbulan=false;
        akses01.permintaan_perbaikan_inventaris=false;
        akses01.grafik_HAIs_laju_vap=false;
        akses01.grafik_HAIs_laju_iad=false;
        akses01.grafik_HAIs_laju_pleb=false;
        akses01.grafik_HAIs_laju_isk=false;
        akses01.grafik_HAIs_laju_ilo=false;
        akses01.grafik_HAIs_laju_hap=false;
        akses01.inhealth_mapping_poli=false;
        akses01.inhealth_mapping_dokter=false;
        akses01.inhealth_mapping_tindakan_ralan=false;
        akses01.inhealth_mapping_tindakan_ranap=false;
        akses01.inhealth_mapping_tindakan_radiologi=false;
        akses01.inhealth_mapping_tindakan_laborat=false;
        akses01.inhealth_mapping_tindakan_operasi=false;
        akses01.hibah_obat_bhp=false;
        akses01.asal_hibah=false;
        akses01.asuhan_gizi=false;
        akses01.inhealth_kirim_tagihan=false;
        akses01.sirkulasi_obat4=false;
        akses01.sirkulasi_obat5=false;
        akses01.sirkulasi_non_medis2=false;
        akses01.monitoring_asuhan_gizi=false;
        akses01.penerimaan_obat_perbulan=false;
        akses01.rekap_kunjungan=false;
        akses01.surat_sakit=false;
        akses01.penilaian_awal_keperawatan_ralan=false;
        akses01.permintaan_diet=false;
        akses01.master_masalah_keperawatan=false;
        akses01.pengajuan_cuti=false;
        akses01.kedatangan_pasien=false;
        akses01.utd_pendonor=false;
        akses01.toko_suplier=false;
        akses01.toko_jenis=false;
        akses01.toko_set_harga=false;
        akses01.toko_barang=false;
        akses01.penagihan_piutang_pasien=false;
        akses01.akun_penagihan_piutang=false;
        akses01.stok_opname_toko=false;
        akses01.toko_riwayat_barang=false;
        akses01.toko_surat_pemesanan=false;
        akses01.toko_pengajuan_barang=false;
        akses01.toko_penerimaan_barang=false;
        akses01.toko_pengadaan_barang=false;
        akses01.toko_hutang=false;
        akses01.toko_bayar_pemesanan=false;
        akses01.toko_member=false;
        akses01.toko_penjualan=false;
        akses01.registrasi_poli_per_tanggal=false;
        akses01.toko_piutang=false;
        akses01.toko_retur_beli=false;
        akses01.ipsrs_returbeli=false;
        akses01.ipsrs_riwayat_barang=false;
        akses01.pasien_corona=false;
        akses01.toko_pendapatan_harian=false;
        akses01.diagnosa_pasien_corona=false;
        akses01.perawatan_pasien_corona=false;
        akses01.penilaian_awal_keperawatan_gigi=false;
        akses01.master_masalah_keperawatan_gigi=false;
        akses01.toko_bayar_piutang=false;
        akses01.toko_piutang_harian=false;
        akses01.toko_penjualan_harian=false;
        akses01.deteksi_corona=false;
        akses01.penilaian_awal_keperawatan_kebidanan=false;
        akses01.pengumuman_epasien=false;
        akses01.surat_hamil=false;
        akses01.set_tarif_online=false;
        akses01.booking_periksa=false;
        akses01.toko_sirkulasi=false;
        akses01.toko_retur_jual=false;
        akses01.toko_retur_piutang=false;
        akses01.toko_sirkulasi2=false;
        akses01.toko_keuntungan_barang=false;
        akses01.zis_pengeluaran_penerima_dankes=false;
        akses01.zis_penghasilan_penerima_dankes=false;
        akses01.zis_ukuran_rumah_penerima_dankes=false;
        akses01.zis_dinding_rumah_penerima_dankes=false;
        akses01.zis_lantai_rumah_penerima_dankes=false;
        akses01.zis_atap_rumah_penerima_dankes=false;
        akses01.zis_kepemilikan_rumah_penerima_dankes=false;
        akses01.zis_kamar_mandi_penerima_dankes=false;
        akses01.zis_dapur_rumah_penerima_dankes=false;
        akses01.zis_kursi_rumah_penerima_dankes=false;
        akses01.zis_kategori_phbs_penerima_dankes=false;
        akses01.zis_elektronik_penerima_dankes=false;
        akses01.zis_ternak_penerima_dankes=false;
        akses01.zis_jenis_simpanan_penerima_dankes=false;
        akses01.penilaian_awal_keperawatan_anak=false;
        akses01.zis_kategori_asnaf_penerima_dankes=false;
        akses01.master_masalah_keperawatan_anak=false;
        akses01.master_imunisasi=false;
        akses01.zis_patologis_penerima_dankes=false;
        akses01.pcare_cek_kartu=false;
        akses01.surat_bebas_narkoba=false;
        akses01.surat_keterangan_covid=false;
        akses01.pemakaian_air_tanah=false;
        akses01.grafik_air_tanah_pertanggal=false;
        akses01.grafik_air_tanah_perbulan=false;
        akses01.lama_pelayanan_poli=false;
        akses01.hemodialisa=false;
        akses01.grafik_harian_hemodialisa=false;
        akses01.grafik_bulanan_hemodialisa=false;
        akses01.grafik_tahunan_hemodialisa=false;
        akses01.grafik_bulanan_meninggal=false;
        akses01.laporan_tahunan_irj=false;
        akses01.perbaikan_inventaris=false;
        akses01.surat_cuti_hamil=false;
        akses01.permintaan_stok_obat_pasien=false;
        akses01.pemeliharaan_inventaris=false;
        akses01.klasifikasi_pasien_ranap=false;
        akses01.bulanan_klasifikasi_pasien_ranap=false;
        akses01.harian_klasifikasi_pasien_ranap=false;
        akses01.klasifikasi_pasien_perbangsal=false;
        akses01.soap_perawatan=false;
        akses01.klaim_rawat_jalan=false;
        akses01.skrining_gizi=false;
        akses01.lama_penyiapan_rm=false;
        akses01.dosis_radiologi=false;
        akses01.demografi_umur_kunjungan=false;
        akses01.jam_diet_pasien=false;
        akses01.rvu_bpjs=false;
        akses01.verifikasi_penerimaan_farmasi=false;
        akses01.verifikasi_penerimaan_logistik=false;
        akses01.pemeriksaan_lab_pa=false;
        akses01.ringkasan_pengajuan_obat=false;
        akses01.ringkasan_pemesanan_obat=false;
        akses01.ringkasan_pengadaan_obat=false;
        akses01.ringkasan_penerimaan_obat=false;
        akses01.ringkasan_hibah_obat=false;
        akses01.ringkasan_penjualan_obat=false;
        akses01.ringkasan_beri_obat=false;
        akses01.ringkasan_piutang_obat=false;
        akses01.ringkasan_stok_keluar_obat=false;
        akses01.ringkasan_retur_suplier_obat=false;
        akses01.ringkasan_retur_pembeli_obat=false;
        akses01.penilaian_awal_keperawatan_ranapkebidanan=false;
        akses01.ringkasan_pengajuan_nonmedis=false;
        akses01.ringkasan_pemesanan_nonmedis=false;
        akses01.ringkasan_pengadaan_nonmedis=false;
        akses01.ringkasan_penerimaan_nonmedis=false;
        akses01.ringkasan_stokkeluar_nonmedis=false;
        akses01.ringkasan_returbeli_nonmedis=false;
        akses01.omset_penerimaan=false;
        akses01.validasi_penagihan_piutang=false;
        akses01.permintaan_ranap=false;
        akses01.bpjs_diagnosa_prb=false;
        akses01.bpjs_obat_prb=false;
        akses01.bpjs_surat_kontrol=false;
        akses01.penggunaan_bhp_ok=false;
        akses01.surat_keterangan_rawat_inap=false;
        akses01.surat_keterangan_sehat=false;
        akses01.pendapatan_per_carabayar=false;
        akses01.akun_host_to_host_bank_jateng=false;
        akses01.pembayaran_bank_jateng=false;
        akses01.bpjs_surat_pri=false;
        akses01.ringkasan_tindakan=false;
        akses01.lama_pelayanan_pasien=false;
        akses01.surat_sakit_pihak_2=false;
        akses01.tagihan_hutang_obat=false;
        akses01.referensi_mobilejkn_bpjs=false;
        akses01.batal_pendaftaran_mobilejkn_bpjs=false;
        akses01.lama_operasi=false;
        akses01.grafik_inventaris_kategori=false;
        akses01.grafik_inventaris_merk=false;
        akses01.grafik_inventaris_produsen=false;
        akses01.pengembalian_deposit_pasien=false;
        akses01.validasi_tagihan_hutang_obat=false;
        akses01.piutang_obat_belum_lunas=false;
        akses01.integrasi_briapi=false;
        akses01.pengadaan_aset_inventaris=false;
        akses01.akun_aset_inventaris=false;
        akses01.suplier_inventaris=false;
        akses01.penerimaan_aset_inventaris=false;
        akses01.bayar_pemesanan_iventaris=false;
        akses01.hutang_aset_inventaris=false;
        akses01.hibah_aset_inventaris=false;
        akses01.titip_faktur_non_medis=false;
        akses01.validasi_tagihan_non_medis=false;
        akses01.titip_faktur_aset=false;
        akses01.validasi_tagihan_aset=false;
        akses01.hibah_non_medis=false;
        akses01.pcare_alasan_tacc=false;
        akses01.resep_luar=false;
        akses01.surat_bebas_tbc=false;
        akses01.surat_buta_warna=false;
        akses01.surat_bebas_tato=false;
        akses01.surat_kewaspadaan_kesehatan=false;
        akses01.grafik_porsidiet_pertanggal=false;
        akses01.grafik_porsidiet_perbulan=false;
        akses01.grafik_porsidiet_pertahun=false;
        akses01.grafik_porsidiet_perbangsal=false;
        akses01.penilaian_awal_medis_ralan=false;
        akses01.master_masalah_keperawatan_mata=false;
        akses01.penilaian_awal_keperawatan_mata=false;
        akses01.penilaian_awal_medis_ranap=false;
        akses01.penilaian_awal_medis_ranap_kebidanan=false;
        akses01.penilaian_awal_medis_ralan_kebidanan=false;
        akses01.penilaian_awal_medis_igd=false;
        akses01.penilaian_awal_medis_ralan_anak=false;
        akses01.bpjs_referensi_poli_hfis=false;
        akses01.bpjs_referensi_dokter_hfis=false;
        akses01.bpjs_referensi_jadwal_hfis=false;
        akses01.penilaian_fisioterapi=false;
        akses01.bpjs_program_prb=false;
        akses01.bpjs_suplesi_jasaraharja=false;
        akses01.bpjs_data_induk_kecelakaan=false;
        akses01.bpjs_sep_internal=false;
        akses01.bpjs_klaim_jasa_raharja=false;
        akses01.bpjs_daftar_finger_print=false;
        akses01.bpjs_rujukan_khusus=false;
        akses01.pemeliharaan_gedung=false;
        akses01.grafik_perbaikan_inventaris_pertanggal=false;
        akses01.grafik_perbaikan_inventaris_perbulan=false;
        akses01.grafik_perbaikan_inventaris_pertahun=false;
        akses01.grafik_perbaikan_inventaris_perpelaksana_status=false;
        akses01.penilaian_mcu=false;
        akses01.peminjam_piutang=false;
        akses01.piutang_lainlain=false;
        akses01.cara_bayar=false;
        akses01.audit_kepatuhan_apd=false;
        akses01.bpjs_task_id=false;
        akses01.bayar_piutang_lain=false;
        akses01.pembayaran_akun_bayar4=false;
        akses01.stok_akhir_farmasi_pertanggal=false;
        akses01.riwayat_kamar_pasien=false;
        akses01.uji_fungsi_kfr=false;
        akses01.hapus_berkas_digital_perawatan=false;
        akses01.kategori_pengeluaran_harian=false;
        akses01.kategori_pemasukan_lain=false;
        akses01.pembayaran_akun_bayar5=false;
        akses01.ruang_ok=false;
        akses01.jasa_tindakan_pasien=false;
        akses01.telaah_resep=false;
        akses01.permintaan_resep_pulang=false;
        akses01.rekap_jm_dokter=false;
        akses01.status_data_rm=false;
        akses01.ubah_petugas_lab_pk=false;
        akses01.ubah_petugas_lab_pa=false;
        akses01.ubah_petugas_radiologi=false;
        akses01.gabung_norawat=false;
        akses01.gabung_rm=false;
        akses01.ringkasan_biaya_obat_pasien_pertanggal=false;
        akses01.master_masalah_keperawatan_igd=false;
        akses01.penilaian_awal_keperawatan_igd=false;
        akses01.bpjs_referensi_dpho_apotek=false;
        akses01.bpjs_referensi_poli_apotek=false;
        akses01.bayar_jm_dokter=false;
        akses01.bpjs_referensi_faskes_apotek=false;
        akses01.bpjs_referensi_spesialistik_apotek=false;
        akses01.pembayaran_briva=false;
        akses01.penilaian_awal_keperawatan_ranap=false;
        akses01.nilai_penerimaan_vendor_farmasi_perbulan=false;
        akses01.akun_bayar_hutang=false;
        akses01.master_rencana_keperawatan=false;
        akses01.laporan_tahunan_igd=false;
        akses01.obat_bhp_tidakbergerak=false;
        akses01.ringkasan_hutang_vendor_farmasi=false;
        akses01.nilai_penerimaan_vendor_nonmedis_perbulan=false;
        akses01.ringkasan_hutang_vendor_nonmedis=false;
        akses01.anggota_polri_dirawat=false;
        akses01.daftar_pasien_ranap_polri=false;
        akses01.soap_ralan_polri=false;
        akses01.soap_ranap_polri=false;
        akses01.laporan_penyakit_polri=false;
        akses01.master_rencana_keperawatan_anak=false;
        akses01.jumlah_pengunjung_ralan_polri=false;
        akses01.catatan_observasi_igd=false;
        akses01.catatan_observasi_ranap=false;
        akses01.catatan_observasi_ranap_kebidanan=false;
        akses01.catatan_observasi_ranap_postpartum=false;
        akses01.penilaian_awal_medis_ralan_tht=false;
        akses01.penilaian_psikologi=false;
        akses01.audit_cuci_tangan_medis=false;
        akses01.audit_pembuangan_limbah=false;
        akses01.ruang_audit_kepatuhan=false;
        akses01.audit_pembuangan_benda_tajam=false;
        akses01.audit_penanganan_darah=false;
        akses01.audit_pengelolaan_linen_kotor=false;
        akses01.audit_penempatan_pasien=false;
        akses01.audit_kamar_jenazah=false;
        akses01.audit_bundle_iadp=false;
        akses01.audit_bundle_ido=false;
        akses01.audit_fasilitas_kebersihan_tangan=false;
        akses01.audit_fasilitas_apd=false;
        akses01.audit_pembuangan_limbah_cair_infeksius=false;
        akses01.audit_sterilisasi_alat=false;
        akses01.penilaian_awal_medis_ralan_psikiatri=false;
        akses01.persetujuan_penolakan_tindakan=false;
        akses01.audit_bundle_isk=false;
        akses01.audit_bundle_plabsi=false;
        akses01.audit_bundle_vap=false;
        akses01.akun_host_to_host_bank_papua=false;
        akses01.pembayaran_bank_papua=false;
        akses01.penilaian_awal_medis_ralan_penyakit_dalam=false;
        akses01.penilaian_awal_medis_ralan_mata=false;
        akses01.penilaian_awal_medis_ralan_neurologi=false;
        akses01.sirkulasi_obat6=false;
        akses01.penilaian_awal_medis_ralan_orthopedi=false;
        akses01.penilaian_awal_medis_ralan_bedah=false;
        akses01.integrasi_khanza_health_services=false;
        akses01.soap_ralan_tni=false;
        akses01.soap_ranap_tni=false;
        akses01.jumlah_pengunjung_ralan_tni=false;
        akses01.laporan_penyakit_tni=false;
        akses01.catatan_keperawatan_ranap=false;
        akses01.master_rencana_keperawatan_gigi=false;
        akses01.master_rencana_keperawatan_mata=false;
        akses01.master_rencana_keperawatan_igd=false;
        akses01.master_masalah_keperawatan_psikiatri=false;
        akses01.master_rencana_keperawatan_psikiatri=false;
        akses01.penilaian_awal_keperawatan_psikiatri=false;
        akses01.pemantauan_pews_anak=false;
        akses01.surat_pulang_atas_permintaan_sendiri=false;
        akses01.template_hasil_radiologi=false;
        akses01.laporan_bulanan_irj=false;
        akses01.template_pemeriksaan=false;
        akses01.pemeriksaan_lab_mb=false;
        akses01.ubah_petugas_lab_mb=false;
        akses01.penilaian_pre_operasi=false;
        akses01.penilaian_pre_anestesi=false;
        akses01.perencanaan_pemulangan=false;
        akses01.penilaian_lanjutan_resiko_jatuh_dewasa=false;
        akses01.penilaian_lanjutan_resiko_jatuh_anak=false;
        akses01.penilaian_awal_medis_ralan_geriatri=false;
        akses01.penilaian_tambahan_pasien_geriatri=false;
        akses01.skrining_nutrisi_dewasa=false;
        akses01.skrining_nutrisi_lansia=false;
        akses01.hasil_pemeriksaan_usg=false;
        akses01.skrining_nutrisi_anak=false;
        akses01.akun_host_to_host_bank_jabar=false;
        akses01.pembayaran_bank_jabar=false;
        akses01.surat_pernyataan_pasien_umum=false;
        akses01.konseling_farmasi=false;
        akses01.pelayanan_informasi_obat=false;
        akses01.jawaban_pio_apoteker=false;
        akses01.surat_persetujuan_umum=false;
        akses01.transfer_pasien_antar_ruang=false;
        akses01.satu_sehat_referensi_dokter=false;
        akses01.satu_sehat_referensi_pasien=false;
        akses01.satu_sehat_mapping_departemen=false;
        akses01.satu_sehat_mapping_lokasi=false;
        akses01.satu_sehat_kirim_encounter=false;
        akses01.catatan_cek_gds=false;
        akses01.satu_sehat_kirim_condition=false;
        akses01.checklist_pre_operasi=false;
        akses01.satu_sehat_kirim_observationttv=false;
        akses01.signin_sebelum_anestesi=false;
        akses01.satu_sehat_kirim_procedure=false;
        akses01.operasi_per_bulan=false;
        akses01.timeout_sebelum_insisi=false;
        akses01.signout_sebelum_menutup_luka=false;
        akses01.dapur_barang=false;
        akses01.dapur_opname=false;
        akses01.satu_sehat_mapping_vaksin=false;
        akses01.dapur_suplier=false;
        akses01.satu_sehat_kirim_Immunization=false;
        akses01.checklist_post_operasi=false;
        akses01.dapur_pembelian=false;
        akses01.dapur_stok_keluar=false;
        akses01.dapur_riwayat_barang=false;
        akses01.permintaan_dapur=false;
        akses01.rekonsiliasi_obat=false;
        akses01.biaya_pengadaan_dapur=false;
        akses01.rekap_pengadaan_dapur=false;
        akses01.kesling_limbah_b3medis_cair=false;
        akses01.grafik_limbahb3cair_pertanggal=false;
        akses01.grafik_limbahb3cair_perbulan=false;
        akses01.rekap_biaya_registrasi=false;
        akses01.konfirmasi_rekonsiliasi_obat=false;
        akses01.satu_sehat_kirim_clinicalimpression=false;
        akses01.penilaian_pasien_terminal=false;
        akses01.surat_persetujuan_rawat_inap=false;
        akses01.monitoring_reaksi_tranfusi=false;
        akses01.penilaian_korban_kekerasan=false;
        akses01.penilaian_lanjutan_resiko_jatuh_lansia=false;
        akses01.mpp_skrining=false;
        akses01.penilaian_pasien_penyakit_menular=false;
        akses01.edukasi_pasien_keluarga_rj=false;
        akses01.pemantauan_pews_dewasa=false;
        akses01.penilaian_tambahan_bunuh_diri=false;
        akses01.bpjs_antrean_pertanggal=false;
        akses01.penilaian_tambahan_perilaku_kekerasan=false;
        akses01.penilaian_tambahan_beresiko_melarikan_diri=false;
        akses01.persetujuan_penundaan_pelayanan=false;
        akses01.sisa_diet_pasien=false;
        akses01.penilaian_awal_medis_ralan_bedah_mulut=false;
        akses01.penilaian_pasien_keracunan=false;
        akses01.pemantauan_meows_obstetri=false;
        akses01.catatan_adime_gizi=false;
        akses01.pengajuan_biaya=false;
        akses01.penilaian_awal_keperawatan_ralan_geriatri=false;
        akses01.master_masalah_keperawatan_geriatri=false;
        akses01.master_rencana_keperawatan_geriatri=false;
        akses01.checklist_kriteria_masuk_hcu=false;
        akses01.checklist_kriteria_keluar_hcu=false;
        akses01.penilaian_risiko_dekubitus=false;
        akses01.master_menolak_anjuran_medis=false;
        akses01.penolakan_anjuran_medis=false;
        akses01.laporan_tahunan_penolakan_anjuran_medis=false;
        akses01.template_laporan_operasi=false;
        akses01.hasil_tindakan_eswl=false;
        akses01.checklist_kriteria_masuk_icu=false;
        akses01.checklist_kriteria_keluar_icu=false;
        akses01.akses_dokter_lain_rawat_jalan=false;
        akses01.follow_up_dbd=false;
        akses01.penilaian_risiko_jatuh_neonatus=false;
        akses01.persetujuan_pengajuan_biaya=false;
        akses01.pemeriksaan_fisik_ralan_per_penyakit=false;
        akses01.penilaian_lanjutan_resiko_jatuh_geriatri=false;
        akses01.pemantauan_ews_neonatus=false;
        akses01.validasi_persetujuan_pengajuan_biaya=false;
        akses01.riwayat_perawatan_icare_bpjs=false;
        akses01.rekap_pengajuan_biaya=false;
        akses01.penilaian_awal_medis_ralan_kulit_kelamin=false;
        akses01.akun_host_to_host_bank_mandiri=false;
        akses01.penilaian_medis_hemodialisa=false;
        akses01.penilaian_level_kecemasan_ranap_anak=false;
        akses01.penilaian_lanjutan_resiko_jatuh_psikiatri=false;
        akses01.penilaian_lanjutan_skrining_fungsional=false;
        akses01.penilaian_medis_ralan_rehab_medik=false;
        akses01.laporan_anestesi=false;
        akses01.template_persetujuan_penolakan_tindakan=false;
        akses01.penilaian_medis_ralan_gawat_darurat_psikiatri=false;
        akses01.bpjs_referensi_setting_apotek=false;
        akses01.bpjs_referensi_obat_apotek=false;
        akses01.bpjs_mapping_obat_apotek=false;
        akses01.pembayaran_bank_mandiri=false;
        akses01.penilaian_ulang_nyeri=false;
        akses01.penilaian_terapi_wicara=false;
        akses01.bpjs_obat_23hari_apotek=false;
        akses01.pengkajian_restrain=false;
        akses01.bpjs_kunjungan_sep_apotek=false;
        akses01.bpjs_monitoring_klaim_apotek=false;
        akses01.bpjs_daftar_pelayanan_obat_apotek=false;
        akses01.penilaian_awal_medis_ralan_paru=false;
        akses01.catatan_keperawatan_ralan=false;
        akses01.catatan_persalinan=false;
        akses01.skor_aldrette_pasca_anestesi=false;
        akses01.skor_steward_pasca_anestesi=false;
        akses01.skor_bromage_pasca_anestesi=false;
        akses01.penilaian_pre_induksi=false;
        akses01.hasil_usg_urologi=false;
        akses01.hasil_usg_gynecologi=false;
        akses01.hasil_pemeriksaan_ekg=false;
        akses01.hapus_edit_sep_bpjs=false;
        akses01.satu_sehat_kirim_diet=false;
        akses01.satu_sehat_mapping_obat=false;
        akses01.dapur_ringkasan_pembelian=false;
        akses01.satu_sehat_kirim_medication=false;
        akses01.satu_sehat_kirim_medicationrequest=false;
        akses01.penatalaksanaan_terapi_okupasi=false;
        akses01.satu_sehat_kirim_medicationdispense=false;
        akses01.hasil_usg_neonatus=false;
        akses01.hasil_endoskopi_faring_laring=false;
        akses01.satu_sehat_mapping_radiologi=false;
        akses01.satu_sehat_kirim_servicerequest_radiologi=false;
        akses01.hasil_endoskopi_hidung=false;
        akses01.satu_sehat_kirim_specimen_radiologi=false;
        akses01.master_masalah_keperawatan_neonatus=false;
        akses01.master_rencana_keperawatan_neonatus=false;
        akses01.penilaian_awal_keperawatan_ranap_neonatus=false;
        akses01.satu_sehat_kirim_observation_radiologi=false;
        akses01.satu_sehat_kirim_diagnosticreport_radiologi=false;
        akses01.hasil_endoskopi_telinga=false;
        akses01.satu_sehat_mapping_lab=false;
        akses01.satu_sehat_kirim_servicerequest_lab=false;
        akses01.satu_sehat_kirim_servicerequest_labmb=false;
        akses01.satu_sehat_kirim_specimen_lab=false;
        akses01.satu_sehat_kirim_specimen_labmb=false;
        akses01.satu_sehat_kirim_observation_lab=false;
        akses01.satu_sehat_kirim_observation_labmb=false;
        akses01.satu_sehat_kirim_diagnosticreport_lab=false;
        akses01.satu_sehat_kirim_diagnosticreport_labmb=false;
        akses01.kepatuhan_kelengkapan_keselamatan_bedah=false;
        akses01.nilai_piutang_perjenis_bayar_per_bulan=false;
        akses01.ringkasan_piutang_jenis_bayar=false;
        akses01.penilaian_pasien_imunitas_rendah=false;
        akses01.balance_cairan=false;
        akses01.catatan_observasi_chbp=false;
        akses01.catatan_observasi_induksi_persalinan=false;
        akses01.skp_kategori_penilaian=false;
        akses01.skp_kriteria_penilaian=false;
        akses01.skp_penilaian=false;
        akses01.referensi_poli_mobilejknfktp=false;
        akses01.referensi_dokter_mobilejknfktp=false;
        akses01.skp_rekapitulasi_penilaian=false;
        akses01.pembayaran_pihak_ke3_bankmandiri=false;
        akses01.metode_pembayaran_bankmandiri=false;
        akses01.bank_tujuan_transfer_bankmandiri=false;
        akses01.kodetransaksi_tujuan_transfer_bankmandiri=false;
        akses01.konsultasi_medik=false;
        akses01.jawaban_konsultasi_medik=false;
        akses01.pcare_cek_alergi=false;
        akses01.pcare_cek_prognosa=false;
        akses01.data_sasaran_usiaproduktif=false;
        akses01.data_sasaran_usialansia=false;
        akses01.skrining_perilaku_merokok_sekolah_remaja=false;
        akses01.skrining_kekerasan_pada_perempuan=false;
        akses01.skrining_obesitas=false;
        akses01.skrining_risiko_kanker_payudara=false;
        akses01.skrining_risiko_kanker_paru=false;
        akses01.skrining_tbc=false;
        akses01.skrining_kesehatan_gigi_mulut_remaja=false;
        akses01.penilaian_awal_keperawatan_ranap_bayi=false;
        akses01.booking_mcu_perusahaan=false;
        akses01.catatan_observasi_restrain_nonfarma=false;
        akses01.catatan_observasi_ventilator=false;
        akses01.catatan_anestesi_sedasi=false;
        akses01.skrining_puma=false;
        akses01.satu_sehat_kirim_careplan=false;
        akses01.satu_sehat_kirim_medicationstatement=false;
        akses01.skrining_adiksi_nikotin=false;
        akses01.skrining_thalassemia=false;
        akses01.skrining_instrumen_sdq=false;
        akses01.skrining_instrumen_srq=false;
        akses01.checklist_pemberian_fibrinolitik=false;
        akses01.skrining_kanker_kolorektal=false;
        akses01.dapur_pemesanan=false;
        akses01.bayar_pesan_dapur=false;
        akses01.hutang_dapur=false;
        akses01.titip_faktur_dapur=false;
        akses01.validasi_tagihan_dapur=false;
        akses01.surat_pemesanan_dapur=false;
        akses01.pengajuan_barang_dapur=false;
        akses01.dapur_returbeli=false;
        akses01.hibah_dapur=false;
        akses01.ringkasan_penerimaan_dapur=false;
        akses01.ringkasan_pengajuan_dapur=false;
        akses01.ringkasan_pemesanan_dapur=false;
        akses01.ringkasan_returbeli_dapur=false;
        akses01.ringkasan_stokkeluar_dapur=false;
        akses01.dapur_stokkeluar_pertanggal=false;
        akses01.sirkulasi_dapur=false;
        akses01.sirkulasi_dapur2=false;
        akses01.verifikasi_penerimaan_dapur=false;
        akses01.nilai_penerimaan_vendor_dapur_perbulan=false;
        akses01.ringkasan_hutang_vendor_dapur=false;
        akses01.penilaian_psikologi_klinis=false;
        akses01.penilaian_awal_medis_ranap_neonatus=false;
        akses01.penilaian_derajat_dehidrasi=false;
        akses01.ringkasan_jasa_tindakan_medis=false;
        akses01.pendapatan_per_akun=false;
        akses01.hasil_pemeriksaan_echo=false;
        akses01.penilaian_bayi_baru_lahir=false;
        akses01.rl1_3_ketersediaan_kamar=false;
        akses01.pendapatan_per_akun_closing=false;
        akses01.pengeluaran_pengeluaran=false;
        akses01.skrining_diabetes_melitus=false;
        akses01.laporan_tindakan=false;
        akses01.pelaksanaan_informasi_edukasi=false;
        akses01.layanan_kedokteran_fisik_rehabilitasi=false;
        akses01.skrining_kesehatan_gigi_mulut_balita=false;
        akses01.skrining_anemia=false;
        akses01.layanan_program_kfr=false;
        akses01.skrining_hipertensi=false;
        akses01.skrining_kesehatan_penglihatan=false;
        akses01.catatan_observasi_hemodialisa=false;
        akses01.skrining_kesehatan_gigi_mulut_dewasa=false;
        akses01.skrining_risiko_kanker_serviks=false;
        akses01.catatan_cairan_hemodialisa=false;
        akses01.skrining_kesehatan_gigi_mulut_lansia=false;
        akses01.skrining_indra_pendengaran=false;
        akses01.catatan_pengkajian_paska_operasi=false;
        akses01.skrining_frailty_syndrome=false;
        akses01.sirkulasi_cssd=false;
        akses01.lama_pelayanan_cssd=false;
        akses01.catatan_observasi_bayi=false;
        akses01.riwayat_surat_peringatan=false;
        akses01.master_kesimpulan_anjuran_mcu=false;
        akses01.kategori_piutang_jasa_perusahaan=false;
        akses01.piutang_jasa_perusahaan=false;
        akses01.bayar_piutang_jasa_perusahaan=false;
        akses01.piutang_jasa_perusahaan_belum_lunas=false;
        akses01.checklist_kesiapan_anestesi=false;
        akses01.piutang_peminjaman_uang_belum_lunas=false;
        akses01.hasil_pemeriksaan_slit_lamp=false;
        akses01.hasil_pemeriksaan_oct=false;
        akses01.beban_hutang_lain=false;
        akses01.poli_asal_pasien_ranap=false;
        akses01.pemberi_hutang_lain=false;
        akses01.dokter_asal_pasien_ranap=false;
        akses01.duta_parkir_rekap_keluar=false;
        akses01.surat_keterangan_layak_terbang=false;
        akses01.bayar_beban_hutang_lain=false;
        akses01.surat_persetujuan_pemeriksaan_hiv=false;
        akses01.skrining_instrumen_acrs=false;
        akses01.surat_pernyataan_memilih_dpjp=false;
    }
    
    public static int getjml1() {return akses01.jml1;}    
    public static int getjml2() {return akses01.jml2;}    
    public static boolean getadmin(){return akses01.admin;}        
    public static boolean getuser(){return akses01.user;} 
    public static boolean getvakum(){return akses01.vakum;} 
    public static boolean getaplikasi(){return akses01.aplikasi;} 
    public static boolean getpenyakit(){return akses01.penyakit;} 
    public static boolean getobat_penyakit(){return akses01.obat_penyakit;} 
    public static boolean getdokter(){return akses01.dokter;} 
    public static boolean getjadwal_praktek(){return akses01.jadwal_praktek;} 
    public static boolean getpetugas(){return akses01.petugas;} 
    public static boolean getpasien(){return akses01.pasien;} 
    public static boolean getregistrasi(){return akses01.registrasi;} 
    public static boolean gettindakan_ralan(){return akses01.tindakan_ralan;} 
    public static boolean getkamar_inap(){return akses01.kamar_inap;} 
    public static boolean gettindakan_ranap(){return akses01.tindakan_ranap;} 
    public static boolean getoperasi(){return akses01.operasi;} 
    public static boolean getrujukan_keluar(){return akses01.rujukan_keluar;} 
    public static boolean getrujukan_masuk(){return akses01.rujukan_masuk;} 
    public static boolean getberi_obat(){return akses01.beri_obat;} 
    public static boolean getresep_pulang(){return akses01.resep_pulang;} 
    public static boolean getpasien_meninggal(){return akses01.pasien_meninggal;} 
    public static boolean getdiet_pasien(){return akses01.diet_pasien;} 
    public static boolean getkelahiran_bayi(){return akses01.kelahiran_bayi;} 
    public static boolean getperiksa_lab(){return akses01.periksa_lab;} 
    public static boolean getperiksa_radiologi(){return akses01.periksa_radiologi;} 
    public static boolean getkasir_ralan(){return akses01.kasir_ralan;} 
    public static boolean getdeposit_pasien(){return akses01.deposit_pasien;} 
    public static boolean getpiutang_pasien(){return akses01.piutang_pasien;} 
    public static boolean getpeminjaman_berkas(){return akses01.peminjaman_berkas;} 
    public static boolean getbarcode(){return akses01.barcode;} 
    public static boolean getpresensi_harian(){return akses01.presensi_harian;} 
    public static boolean getpresensi_bulanan(){return akses01.presensi_bulanan;} 
    public static boolean getpegawai_admin(){return akses01.pegawai_admin;} 
    public static boolean getpegawai_user(){return akses01.pegawai_user;} 
    public static boolean getsuplier(){return akses01.suplier;} 
    public static boolean getsatuan_barang(){return akses01.satuan_barang;} 
    public static boolean getkonversi_satuan(){return akses01.konversi_satuan;} 
    public static boolean getjenis_barang(){return akses01.jenis_barang;} 
    public static boolean getobat(){return akses01.obat;} 
    public static boolean getstok_opname_obat(){return akses01.stok_opname_obat;} 
    public static boolean getstok_obat_pasien(){return akses01.stok_obat_pasien;} 
    public static boolean getpengadaan_obat(){return akses01.pengadaan_obat;} 
    public static boolean getpemesanan_obat(){return akses01.pemesanan_obat;} 
    public static boolean getpenjualan_obat(){return akses01.penjualan_obat;} 
    public static void setpenjualan_obatfalse(){akses01.penjualan_obat=false;} 
    public static boolean getpiutang_obat(){return akses01.piutang_obat;} 
    public static boolean getretur_ke_suplier(){return akses01.retur_ke_suplier;} 
    public static boolean getretur_dari_pembeli(){return akses01.retur_dari_pembeli;} 
    public static boolean getretur_obat_ranap(){return akses01.retur_obat_ranap;} 
    public static boolean getretur_piutang_pasien(){return akses01.retur_piutang_pasien;} 
    public static boolean getkeuntungan_penjualan(){return akses01.keuntungan_penjualan;} 
    public static boolean getkeuntungan_beri_obat(){return akses01.keuntungan_beri_obat;} 
    public static boolean getsirkulasi_obat(){return akses01.sirkulasi_obat;} 
    public static boolean getipsrs_barang(){return akses01.ipsrs_barang;} 
    public static boolean getipsrs_pengadaan_barang(){return akses01.ipsrs_pengadaan_barang;} 
    public static boolean getipsrs_stok_keluar(){return akses01.ipsrs_stok_keluar;} 
    public static boolean getipsrs_rekap_pengadaan(){return akses01.ipsrs_rekap_pengadaan;} 
    public static boolean getipsrs_rekap_stok_keluar(){return akses01.ipsrs_rekap_stok_keluar;} 
    public static boolean getipsrs_pengeluaran_harian(){return akses01.ipsrs_pengeluaran_harian;} 
    public static boolean getipsrs_jenis_barang(){return akses01.ipsrs_jenis_barang;} 
    public static boolean getinventaris_jenis(){return akses01.inventaris_jenis;} 
    public static boolean getinventaris_kategori(){return akses01.inventaris_kategori;} 
    public static boolean getinventaris_merk(){return akses01.inventaris_merk;} 
    public static boolean getinventaris_ruang(){return akses01.inventaris_ruang;} 
    public static boolean getinventaris_produsen(){return akses01.inventaris_produsen;} 
    public static boolean getinventaris_koleksi(){return akses01.inventaris_koleksi;} 
    public static boolean getinventaris_inventaris(){return akses01.inventaris_inventaris;} 
    public static boolean getinventaris_sirkulasi(){return akses01.inventaris_sirkulasi;} 
    public static boolean getparkir_jenis(){return akses01.parkir_jenis;} 
    public static boolean getparkir_in(){return akses01.parkir_in;} 
    public static boolean getparkir_out(){return akses01.parkir_out;} 
    public static boolean getparkir_rekap_harian(){return akses01.parkir_rekap_harian;} 
    public static boolean getparkir_rekap_bulanan(){return akses01.parkir_rekap_bulanan;} 
    public static boolean getinformasi_kamar(){return akses01.informasi_kamar;} 
    public static boolean getharian_tindakan_poli(){return akses01.harian_tindakan_poli;} 
    public static boolean getobat_per_poli(){return akses01.obat_per_poli;} 
    public static boolean getobat_per_kamar(){return akses01.obat_per_kamar;} 
    public static boolean getobat_per_dokter_ralan(){return akses01.obat_per_dokter_ralan;} 
    public static boolean getobat_per_dokter_ranap(){return akses01.obat_per_dokter_ranap;} 
    public static boolean getharian_dokter(){return akses01.harian_dokter;} 
    public static boolean getbulanan_dokter(){return akses01.bulanan_dokter;} 
    public static boolean getharian_paramedis(){return akses01.harian_paramedis;} 
    public static boolean getbulanan_paramedis(){return akses01.bulanan_paramedis;} 
    public static boolean getpembayaran_ralan(){return akses01.pembayaran_ralan;} 
    public static boolean getpembayaran_ranap(){return akses01.pembayaran_ranap;} 
    public static boolean getrekap_pembayaran_ralan(){return akses01.rekap_pembayaran_ralan;} 
    public static boolean getrekap_pembayaran_ranap(){return akses01.rekap_pembayaran_ranap;} 
    public static boolean gettagihan_masuk(){return akses01.tagihan_masuk;} 
    public static boolean gettambahan_biaya(){return akses01.tambahan_biaya;} 
    public static boolean getpotongan_biaya(){return akses01.potongan_biaya;} 
    public static boolean getresep_obat(){return akses01.resep_obat;} 
    public static boolean getresume_pasien(){return akses01.resume_pasien;} 
    public static boolean getpenyakit_ralan(){return akses01.penyakit_ralan;} 
    public static boolean getpenyakit_ranap(){return akses01.penyakit_ranap;} 
    public static boolean getkamar(){return akses01.kamar;} 
    public static boolean gettarif_ralan(){return akses01.tarif_ralan;} 
    public static boolean gettarif_ranap(){return akses01.tarif_ranap;} 
    public static boolean gettarif_lab(){return akses01.tarif_lab;} 
    public static boolean gettarif_radiologi(){return akses01.tarif_radiologi;} 
    public static boolean gettarif_operasi(){return akses01.tarif_operasi;} 
    public static boolean getakun_rekening(){return akses01.akun_rekening;} 
    public static boolean getrekening_tahun(){return akses01.rekening_tahun;} 
    public static boolean getposting_jurnal(){return akses01.posting_jurnal;} 
    public static boolean getbuku_besar(){return akses01.buku_besar;} 
    public static boolean getcashflow(){return akses01.cashflow;} 
    public static boolean getkeuangan(){return akses01.keuangan;} 
    public static boolean getpengeluaran(){return akses01.pengeluaran;} 
    public static boolean getsetup_pjlab(){return akses01.setup_pjlab;} 
    public static boolean getsetup_otolokasi(){return akses01.setup_otolokasi;} 
    public static boolean getsetup_jam_kamin(){return akses01.setup_jam_kamin;} 
    public static boolean getsetup_embalase(){return akses01.setup_embalase;} 
    public static boolean gettracer_login(){return akses01.tracer_login;} 
    public static boolean getdisplay(){return akses01.display;} 
    public static boolean getset_harga_obat(){return akses01.set_harga_obat;} 
    public static boolean getset_penggunaan_tarif(){return akses01.set_penggunaan_tarif;} 
    public static boolean getset_oto_ralan(){return akses01.set_oto_ralan;} 
    public static boolean getbiaya_harian(){return akses01.biaya_harian;} 
    public static boolean getbiaya_masuk_sekali(){return akses01.biaya_masuk_sekali;} 
    public static boolean getset_no_rm(){return akses01.set_no_rm;} 
    public static boolean getbilling_ralan(){return akses01.billing_ralan;} 
    public static boolean getbilling_ranap(){return akses01.billing_ranap;}
    public static String getkode(){return akses01.kode;}
    public static void setkdbangsal(String kdbangsal){akses01.kdbangsal=kdbangsal;}
    public static String getkdbangsal(){return akses01.kdbangsal;} 
    public static void setalamatip(String alamatip){akses01.alamatip=alamatip;}
    public static String getalamatip(){return akses01.alamatip;}     
    public static void setform(String form){akses01.form=form;}
    public static String getform(){return akses01.form;}   
    public static void setnamauser(String namauser){akses01.namauser=namauser;}
    public static String getnamauser(){return akses01.namauser;}   
    public static void setstatus(boolean status){akses01.status=status;}
    public static boolean getstatus(){return akses01.status;}
    public static boolean getjm_ranap_dokter(){return akses01.jm_ranap_dokter;}     
    public static boolean getigd(){return akses01.igd;}     
    public static boolean getbarcoderalan(){return akses01.barcoderalan;}     
    public static boolean getbarcoderanap(){return akses01.barcoderanap;}    
    public static boolean getset_harga_obat_ralan(){return akses01.set_harga_obat_ralan;}  
    public static boolean getset_harga_obat_ranap(){return akses01.set_harga_obat_ranap;}  
    public static boolean getpenyakit_pd3i(){return akses01.penyakit_pd3i;}  
    public static boolean getsurveilans_pd3i(){return akses01.surveilans_pd3i;}  
    public static boolean getsurveilans_ralan(){return akses01.surveilans_ralan;}  
    public static boolean getdiagnosa_pasien(){return akses01.diagnosa_pasien;}  
    public static boolean getsurveilans_ranap(){return akses01.surveilans_ranap;}  
    public static boolean getpny_takmenular_ranap(){return akses01.pny_takmenular_ranap;}  
    public static boolean getpny_takmenular_ralan(){return akses01.pny_takmenular_ralan;}
    public static void setnamars(String namars){akses01.namars=namars;}
    public static void setalamatrs(String alamatrs){akses01.alamatrs=alamatrs;}
    public static void setkabupatenrs(String kabupatenrs){akses01.kabupatenrs=kabupatenrs;}
    public static void setpropinsirs(String propinsirs){akses01.propinsirs=propinsirs;}
    public static void setkontakrs(String kontakrs){akses01.kontakrs=kontakrs;}
    public static void setemailrs(String emailrs){akses01.emailrs=emailrs;}
    public static void setkodeppkbpjs(String kode_ppk){akses01.kode_ppk=kode_ppk;}
    public static String getnamars(){return akses01.namars;}
    public static String getalamatrs(){return akses01.alamatrs;}
    public static String getkabupatenrs(){return akses01.kabupatenrs;}
    public static String getpropinsirs(){return akses01.propinsirs;}
    public static String getkontakrs(){return akses01.kontakrs;}
    public static String getemailrs(){return akses01.emailrs;}
    public static String getkodeppkbpjs(){return akses01.kode_ppk;}
    public static boolean getkunjungan_ralan(){return akses01.kunjungan_ralan;}
    public static boolean getrl32(){return akses01.rl32;}
    public static boolean getrl33(){return akses01.rl33;}
    public static boolean getrl37(){return akses01.rl37;}
    public static boolean getrl38(){return akses01.rl38;}
    public static boolean getharian_tindakan_dokter(){return akses01.harian_tindakan_dokter;}
    public static boolean getsms(){return akses01.sms;}
    public static boolean getsidikjari(){return akses01.sidikjari;} 
    public static boolean getjam_masuk(){return akses01.jam_masuk;}  
    public static boolean getjadwal_pegawai(){return akses01.jadwal_pegawai;}
    public static boolean getparkir_barcode(){return akses01.parkir_barcode;}
    public static boolean getset_nota(){return akses01.set_nota;}
    public static boolean getdpjp_ranap(){return akses01.dpjp_ranap;}
    public static boolean getmutasi_barang(){return akses01.mutasi_barang;}
    public static boolean getrl34(){return akses01.rl34;}
    public static boolean getrl36(){return akses01.rl36;}
    public static boolean getfee_visit_dokter(){return akses01.fee_visit_dokter;}
    public static boolean getfee_bacaan_ekg(){return akses01.fee_bacaan_ekg;}
    public static boolean getfee_rujukan_rontgen(){return akses01.fee_rujukan_rontgen;}
    public static boolean getfee_rujukan_ranap(){return akses01.fee_rujukan_ranap;}
    public static boolean getfee_ralan(){return akses01.fee_ralan;}
    public static boolean getakun_bayar(){return akses01.akun_bayar;}
    public static boolean getbayar_pemesanan_obat(){return akses01.bayar_pemesanan_obat;}
    public static boolean getobat_per_dokter_peresep(){return akses01.obat_per_dokter_peresep;}
    public static boolean getpemasukan_lain(){return akses01.pemasukan_lain;}
    public static boolean getpengaturan_rekening(){return akses01.pengaturan_rekening;}
    public static boolean getclosing_kasir(){return akses01.closing_kasir;}
    public static boolean getketerlambatan_presensi(){return akses01.keterlambatan_presensi;} 
    public static boolean getset_harga_kamar(){return akses01.set_harga_kamar;} 
    public static boolean getrekap_per_shift(){return akses01.rekap_per_shift;}
    public static boolean getbpjs_cek_nik(){return akses01.bpjs_cek_nik;} 
    public static boolean getbpjs_cek_kartu(){return akses01.bpjs_cek_kartu;} 
    public static boolean getbpjs_cek_riwayat(){return akses01.bpjs_cek_riwayat;} 
    public static boolean getobat_per_cara_bayar(){return akses01.obat_per_cara_bayar;} 
    public static boolean getkunjungan_ranap(){return akses01.kunjungan_ranap;} 
    public static boolean getbayar_piutang(){return akses01.bayar_piutang;} 
    public static boolean getpayment_point(){return akses01.payment_point;} 
    public static boolean getbpjs_cek_nomor_rujukan(){return akses01.bpjs_cek_nomor_rujukan;}
    public static boolean geticd9(){return akses01.icd9;}
    public static boolean getdarurat_stok(){return akses01.darurat_stok;}
    public static boolean getretensi_rm(){return akses01.retensi_rm;}
    public static boolean gettemporary_presensi(){return akses01.temporary_presensi;}
    public static boolean getjurnal_harian(){return akses01.jurnal_harian;}
    public static boolean getsirkulasi_obat2(){return akses01.sirkulasi_obat2;}
    public static boolean getedit_registrasi(){return akses01.edit_registrasi;}
    public static boolean getbpjs_referensi_diagnosa(){return akses01.bpjs_referensi_diagnosa;}
    public static boolean getbpjs_referensi_poli(){return akses01.bpjs_referensi_poli;}
    public static boolean getindustrifarmasi(){return akses01.industrifarmasi;}
    public static boolean getharian_js(){return akses01.harian_js;}
    public static boolean getbulanan_js(){return akses01.bulanan_js;}
    public static boolean getharian_paket_bhp(){return akses01.harian_paket_bhp;}
    public static boolean getbulanan_paket_bhp(){return akses01.bulanan_paket_bhp;}
    public static boolean getpiutang_pasien2(){return akses01.piutang_pasien2;}
    public static boolean getbpjs_referensi_faskes(){return akses01.bpjs_referensi_faskes;} 
    public static boolean getbpjs_sep(){return akses01.bpjs_sep;} 
    public static boolean getpengambilan_utd(){return akses01.pengambilan_utd;} 
    public static boolean gettarif_utd(){return akses01.tarif_utd;} 
    public static boolean getpengambilan_utd2(){return akses01.pengambilan_utd2;}  
    public static boolean getutd_medis_rusak(){return akses01.utd_medis_rusak;}  
    public static boolean getpengambilan_penunjang_utd(){return akses01.pengambilan_penunjang_utd;}  
    public static boolean getpengambilan_penunjang_utd2(){return akses01.pengambilan_penunjang_utd2;}  
    public static boolean getutd_penunjang_rusak(){return akses01.utd_penunjang_rusak;}  
    public static boolean getsuplier_penunjang(){return akses01.suplier_penunjang;}  
    public static boolean getutd_donor(){return akses01.utd_donor;}  
    public static boolean getbpjs_monitoring_klaim(){return akses01.bpjs_monitoring_klaim;}  
    public static boolean getutd_cekal_darah(){return akses01.utd_cekal_darah;}  
    public static boolean getutd_komponen_darah(){return akses01.utd_komponen_darah;}  
    public static boolean getutd_stok_darah(){return akses01.utd_stok_darah;}  
    public static boolean getutd_pemisahan_darah(){return akses01.utd_pemisahan_darah;}  
    public static boolean getharian_kamar(){return akses01.harian_kamar;}  
    public static boolean getrincian_piutang_pasien(){return akses01.rincian_piutang_pasien;}  
    public static boolean getkeuntungan_beri_obat_nonpiutang(){return akses01.keuntungan_beri_obat_nonpiutang;}  
    public static boolean getreklasifikasi_ralan(){return akses01.reklasifikasi_ralan;}  
    public static boolean getreklasifikasi_ranap(){return akses01.reklasifikasi_ranap;}  
    public static boolean getutd_penyerahan_darah(){return akses01.utd_penyerahan_darah;} 
    public static void setutd_penyerahan_darahfalse(){akses01.utd_penyerahan_darah=false;} 
    public static boolean gethutang_obat(){return akses01.hutang_obat;}  
    public static boolean getriwayat_obat_alkes_bhp(){return akses01.riwayat_obat_alkes_bhp;}
    public static boolean getsensus_harian_poli(){return akses01.sensus_harian_poli;}
    public static boolean getrl4a(){return akses01.rl4a;}
    public static boolean getaplicare_referensi_kamar(){return akses01.aplicare_referensi_kamar;}
    public static boolean getaplicare_ketersediaan_kamar(){return akses01.aplicare_ketersediaan_kamar;}
    public static boolean getinacbg_klaim_baru_otomatis(){return akses01.inacbg_klaim_baru_otomatis;}
    public static boolean getinacbg_klaim_baru_manual(){return akses01.inacbg_klaim_baru_manual;}
    public static boolean getinacbg_coder_nik(){return akses01.inacbg_coder_nik;}
    public static boolean getmutasi_berkas(){return akses01.mutasi_berkas;}
    public static boolean getakun_piutang(){return akses01.akun_piutang;}
    public static boolean getharian_kso(){return akses01.harian_kso;}
    public static boolean getbulanan_kso(){return akses01.bulanan_kso;}
    public static boolean getharian_menejemen(){return akses01.harian_menejemen;}
    public static boolean getbulanan_menejemen(){return akses01.bulanan_menejemen;}
    public static boolean getinhealth_cek_eligibilitas(){return akses01.inhealth_cek_eligibilitas;}
    public static boolean getinhealth_referensi_jenpel_ruang_rawat(){return akses01.inhealth_referensi_jenpel_ruang_rawat;}
    public static boolean getinhealth_referensi_poli(){return akses01.inhealth_referensi_poli;}
    public static boolean getinhealth_referensi_faskes(){return akses01.inhealth_referensi_faskes;}
    public static boolean getinhealth_sjp(){return akses01.inhealth_sjp;}
    public static boolean getpiutang_ralan(){return akses01.piutang_ralan;}
    public static boolean getpiutang_ranap(){return akses01.piutang_ranap;}
    public static boolean getdetail_piutang_penjab(){return akses01.detail_piutang_penjab;}
    public static boolean getlama_pelayanan_ralan(){return akses01.lama_pelayanan_ralan;}
    public static boolean getcatatan_pasien(){return akses01.catatan_pasien;}
    public static boolean getrl4b(){return akses01.rl4b;}
    public static boolean getrl4asebab(){return akses01.rl4asebab;}
    public static boolean getrl4bsebab(){return akses01.rl4bsebab;}
    public static boolean getdata_HAIs(){return akses01.data_HAIs;}
    public static boolean getharian_HAIs(){return akses01.harian_HAIs;}
    public static boolean getbulanan_HAIs(){return akses01.bulanan_HAIs;}
    public static boolean gethitung_bor(){return akses01.hitung_bor;}
    public static boolean getperusahaan_pasien(){return akses01.perusahaan_pasien;}
    public static boolean getresep_dokter(){return akses01.resep_dokter;}
    public static void setresep_dokterfalse(){akses01.resep_dokter=false;} 
    public static boolean getlama_pelayanan_apotek(){return akses01.lama_pelayanan_apotek;}
    public static boolean gethitung_alos(){return akses01.hitung_alos;}
    public static boolean getdetail_tindakan(){return akses01.detail_tindakan;}
    public static boolean getrujukan_poli_internal(){return akses01.rujukan_poli_internal;}
    public static boolean getrekap_poli_anak(){return akses01.rekap_poli_anak;}
    public static boolean getgrafik_kunjungan_poli(){return akses01.grafik_kunjungan_poli;}
    public static boolean getgrafik_kunjungan_perdokter(){return akses01.grafik_kunjungan_perdokter;}
    public static boolean getgrafik_kunjungan_perpekerjaan(){return akses01.grafik_kunjungan_perpekerjaan;}
    public static boolean getgrafik_kunjungan_perpendidikan(){return akses01.grafik_kunjungan_perpendidikan;}
    public static boolean getgrafik_kunjungan_pertahun(){return akses01.grafik_kunjungan_pertahun;}
    public static boolean getberkas_digital_perawatan(){return akses01.berkas_digital_perawatan;}
    public static boolean getpenyakit_menular_ranap(){return akses01.penyakit_menular_ranap;}
    public static boolean getpenyakit_menular_ralan(){return akses01.penyakit_menular_ralan;}
    public static boolean getgrafik_kunjungan_perbulan(){return akses01.grafik_kunjungan_perbulan;}
    public static boolean getgrafik_kunjungan_pertanggal(){return akses01.grafik_kunjungan_pertanggal;}
    public static boolean getgrafik_kunjungan_demografi(){return akses01.grafik_kunjungan_demografi;}
    public static boolean getgrafik_kunjungan_statusdaftartahun(){return akses01.grafik_kunjungan_statusdaftartahun;}
    public static boolean getgrafik_kunjungan_statusdaftartahun2(){return akses01.grafik_kunjungan_statusdaftartahun2;}                        
    public static boolean getgrafik_kunjungan_statusdaftarbulan(){return akses01.grafik_kunjungan_statusdaftarbulan;}  
    public static boolean getgrafik_kunjungan_statusdaftarbulan2(){return akses01.grafik_kunjungan_statusdaftarbulan2;} 
    public static boolean getgrafik_kunjungan_statusdaftartanggal(){return akses01.grafik_kunjungan_statusdaftartanggal;} 
    public static boolean getgrafik_kunjungan_statusdaftartanggal2(){return akses01.grafik_kunjungan_statusdaftartanggal2;} 
    public static boolean getgrafik_kunjungan_statusbataltahun(){return akses01.grafik_kunjungan_statusbataltahun;} 
    public static boolean getgrafik_kunjungan_statusbatalbulan(){return akses01.grafik_kunjungan_statusbatalbulan;}
    public static boolean getpcare_cek_penyakit(){return akses01.pcare_cek_penyakit;}
    public static boolean getgrafik_kunjungan_statusbataltanggal(){return akses01.grafik_kunjungan_statusbataltanggal;}
    public static boolean getkategori_barang(){return akses01.kategori_barang;}
    public static boolean getgolongan_barang(){return akses01.golongan_barang;}
    public static boolean getpemberian_obat_pertanggal(){return akses01.pemberian_obat_pertanggal;}
    public static boolean getpenjualan_obat_pertanggal(){return akses01.penjualan_obat_pertanggal;}
    public static boolean getpcare_cek_kesadaran(){return akses01.pcare_cek_kesadaran;}
    public static boolean getpembatalan_periksa_dokter(){return akses01.pembatalan_periksa_dokter;}
    public static boolean getpembayaran_per_unit(){return akses01.pembayaran_per_unit;}
    public static boolean getrekap_pembayaran_per_unit(){return akses01.rekap_pembayaran_per_unit;}
    public static boolean getgrafik_kunjungan_percarabayar(){return akses01.grafik_kunjungan_percarabayar;}
    public static boolean getipsrs_pengadaan_pertanggal(){return akses01.ipsrs_pengadaan_pertanggal;}
    public static boolean getipsrs_stokkeluar_pertanggal(){return akses01.ipsrs_stokkeluar_pertanggal;}
    public static boolean getgrafik_kunjungan_ranaptahun(){return akses01.grafik_kunjungan_ranaptahun;}
    public static boolean getpcare_cek_rujukan(){return akses01.pcare_cek_rujukan;}
    public static boolean getgrafik_lab_ralantahun(){return akses01.grafik_lab_ralantahun;}
    public static boolean getgrafik_rad_ralantahun(){return akses01.grafik_rad_ralantahun;}
    public static boolean getcek_entry_ralan(){return akses01.cek_entry_ralan;}
    public static boolean getinacbg_klaim_baru_manual2(){return akses01.inacbg_klaim_baru_manual2;}
    public static boolean getpermintaan_medis(){return akses01.permintaan_medis;}
    public static boolean getrekap_permintaan_medis(){return akses01.rekap_permintaan_medis;}
    public static boolean getsurat_pemesanan_medis(){return akses01.surat_pemesanan_medis;}
    public static boolean getpermintaan_non_medis(){return akses01.permintaan_non_medis;}
    public static boolean getrekap_permintaan_non_medis(){return akses01.rekap_permintaan_non_medis;}
    public static boolean getsurat_pemesanan_non_medis(){return akses01.surat_pemesanan_non_medis;}
    public static boolean getgrafik_per_perujuk(){return akses01.grafik_per_perujuk;}
    public static boolean getbpjs_cek_prosedur(){return akses01.bpjs_cek_prosedur;}
    public static boolean getbpjs_cek_kelas_rawat(){return akses01.bpjs_cek_kelas_rawat;}
    public static boolean getbpjs_cek_dokter(){return akses01.bpjs_cek_dokter;}
    public static boolean getbpjs_cek_spesialistik(){return akses01.bpjs_cek_spesialistik;}
    public static boolean getbpjs_cek_ruangrawat(){return akses01.bpjs_cek_ruangrawat;}
    public static boolean getbpjs_cek_carakeluar(){return  akses01.bpjs_cek_carakeluar;}
    public static boolean getbpjs_cek_pasca_pulang(){return akses01.bpjs_cek_pasca_pulang;} 
    public static boolean getdetail_tindakan_okvk(){return akses01.detail_tindakan_okvk;}
    public static boolean getbilling_parsial(){return akses01.billing_parsial;}
    public static boolean getbpjs_cek_nomor_rujukan_rs(){return akses01.bpjs_cek_nomor_rujukan_rs;}
    public static boolean getbpjs_cek_rujukan_kartu_pcare(){return akses01.bpjs_cek_rujukan_kartu_pcare;}
    public static boolean getbpjs_cek_rujukan_kartu_rs(){return akses01.bpjs_cek_rujukan_kartu_rs;}
    public static boolean getakses_depo_obat(){return akses01.akses_depo_obat;}
    public static boolean getbpjs_rujukan_keluar(){return akses01.bpjs_rujukan_keluar;}
    public static boolean getgrafik_lab_ralanbulan(){return akses01.grafik_lab_ralanbulan;}
    public static boolean getpengeluaran_stok_apotek(){return akses01.pengeluaran_stok_apotek;}
    public static boolean getgrafik_rad_ralanbulan(){return akses01.grafik_rad_ralanbulan;}
    public static boolean getdetailjmdokter2(){return akses01.detailjmdokter2;}
    public static boolean getpengaduan_pasien(){return akses01.pengaduan_pasien;}
    public static boolean getgrafik_lab_ralanhari(){return akses01.grafik_lab_ralanhari;}
    public static boolean getgrafik_rad_ralanhari(){return akses01.grafik_rad_ralanhari;}
    public static boolean getsensus_harian_ralan(){return akses01.sensus_harian_ralan;}
    public static boolean getmetode_racik(){return akses01.metode_racik;}
    public static boolean getpembayaran_akun_bayar(){return akses01.pembayaran_akun_bayar;}
    public static boolean getpengguna_obat_resep(){return akses01.pengguna_obat_resep;}
    public static boolean getrekap_pemesanan(){return akses01.rekap_pemesanan;}
    public static boolean getmaster_berkas_pegawai(){return akses01.master_berkas_pegawai;}
    public static boolean getberkas_kepegawaian(){return akses01.berkas_kepegawaian;}
    public static boolean getriwayat_jabatan(){return akses01.riwayat_jabatan;}
    public static boolean getriwayat_pendidikan(){return akses01.riwayat_pendidikan;}
    public static boolean getriwayat_naik_gaji(){return akses01.riwayat_naik_gaji;}
    public static boolean getkegiatan_ilmiah(){return akses01.kegiatan_ilmiah;}
    public static boolean getriwayat_penghargaan(){return akses01.riwayat_penghargaan;}
    public static boolean getriwayat_penelitian(){return akses01.riwayat_penelitian;}
    public static boolean getpenerimaan_non_medis(){return akses01.penerimaan_non_medis;}
    public static boolean getbayar_pesan_non_medis(){return akses01.bayar_pesan_non_medis;}
    public static boolean gethutang_barang_non_medis(){return akses01.hutang_barang_non_medis;}
    public static boolean getrekap_pemesanan_non_medis(){return akses01.rekap_pemesanan_non_medis;}
    public static boolean getinsiden_keselamatan(){return akses01.insiden_keselamatan;}
    public static boolean getinsiden_keselamatan_pasien(){return akses01.insiden_keselamatan_pasien;}
    public static boolean getgrafik_ikp_pertahun(){return akses01.grafik_ikp_pertahun;}
    public static boolean getgrafik_ikp_perbulan(){return akses01.grafik_ikp_perbulan;}
    public static boolean getgrafik_ikp_pertanggal(){return akses01.grafik_ikp_pertanggal;}
    public static boolean getriwayat_data_batch(){return akses01.riwayat_data_batch;}
    public static boolean getgrafik_ikp_jenis(){return akses01.grafik_ikp_jenis;}
    public static boolean getgrafik_ikp_dampak(){return akses01.grafik_ikp_dampak;}
    public static boolean getpiutang_akun_piutang(){return akses01.piutang_akun_piutang;}
    public static void setresep_obatfalse(){akses01.resep_obat=false;} 
    public static boolean getgrafik_kunjungan_per_agama(){return akses01.grafik_kunjungan_per_agama;}
    public static boolean getgrafik_kunjungan_per_umur(){return akses01.grafik_kunjungan_per_umur;}
    public static boolean getsuku_bangsa(){return akses01.suku_bangsa;}
    public static boolean getbahasa_pasien(){return akses01.bahasa_pasien;}
    public static boolean getgolongan_tni(){return akses01.golongan_tni;}
    public static boolean getsatuan_tni(){return akses01.satuan_tni;}
    public static boolean getjabatan_tni(){return akses01.jabatan_tni;}
    public static boolean getpangkat_tni(){return akses01.pangkat_tni;}
    public static boolean getgolongan_polri(){return akses01.golongan_polri;}
    public static boolean getsatuan_polri(){return akses01.satuan_polri;}
    public static boolean getjabatan_polri(){return akses01.jabatan_polri;}
    public static boolean getpangkat_polri(){return akses01.pangkat_polri;}
    public static boolean getcacat_fisik(){return akses01.cacat_fisik;}
    public static boolean getgrafik_kunjungan_suku(){return akses01.grafik_kunjungan_suku;}
    public static boolean getgrafik_kunjungan_bahasa(){return akses01.grafik_kunjungan_bahasa;}
    public static boolean getbooking_operasi(){return akses01.booking_operasi;}
    public static boolean getmapping_poli_bpjs(){return akses01.mapping_poli_bpjs;}
    public static boolean getgrafik_kunjungan_per_cacat(){return akses01.grafik_kunjungan_per_cacat;}
    public static boolean getbarang_cssd(){return akses01.barang_cssd;}
    public static boolean getskdp_bpjs(){return akses01.skdp_bpjs;}
    public static boolean getbooking_registrasi(){return akses01.booking_registrasi;}
    public static boolean getbpjs_cek_propinsi(){return akses01.bpjs_cek_propinsi;}
    public static boolean getbpjs_cek_kabupaten(){return akses01.bpjs_cek_kabupaten;}
    public static boolean getbpjs_cek_kecamatan(){return akses01.bpjs_cek_kecamatan;}
    public static boolean getbpjs_cek_dokterdpjp(){return akses01.bpjs_cek_dokterdpjp;}
    public static boolean getbpjs_cek_riwayat_rujukanrs(){return akses01.bpjs_cek_riwayat_rujukanrs;}
    public static boolean getbpjs_cek_tanggal_rujukan(){return akses01.bpjs_cek_tanggal_rujukan;}
    public static boolean getpermintaan_lab(){return akses01.permintaan_lab;}
    public static void setperiksalabfalse(){akses01.periksa_lab=false;} 
    public static void setperiksalabpafalse(){akses01.pemeriksaan_lab_pa=false;} 
    public static void setperiksalabmbfalse(){akses01.pemeriksaan_lab_mb=false;} 
    public static void setpermintaanlabfalse(){akses01.permintaan_lab=false;} 
    public static boolean getpermintaan_radiologi(){return akses01.permintaan_radiologi;}
    public static void setperiksaradiologifalse(){akses01.periksa_radiologi=false;} 
    public static void setpermintaanradiologifalse(){akses01.permintaan_radiologi=false;} 
    public static boolean getsurat_indeks(){return akses01.surat_indeks;}
    public static boolean getsurat_map(){return akses01.surat_map;}
    public static boolean getsurat_almari(){return akses01.surat_almari;}
    public static boolean getsurat_rak(){return akses01.surat_rak;}
    public static boolean getsurat_ruang(){return akses01.surat_ruang;}
    public static boolean getsurat_klasifikasi(){return akses01.surat_klasifikasi;}
    public static boolean getsurat_status(){return akses01.surat_status;}
    public static boolean getsurat_sifat(){return akses01.surat_sifat;}
    public static boolean getsurat_balas(){return akses01.surat_balas;}
    public static boolean getsurat_masuk(){return akses01.surat_masuk;}
    public static boolean getpcare_cek_dokter(){return akses01.pcare_cek_dokter;}
    public static boolean getpcare_cek_poli(){return akses01.pcare_cek_poli;}
    public static boolean getpcare_cek_provider(){return akses01.pcare_cek_provider;}
    public static boolean getpcare_cek_statuspulang(){return akses01.pcare_cek_statuspulang;}
    public static boolean getpcare_cek_spesialis(){return akses01.pcare_cek_spesialis;}
    public static boolean getpcare_cek_subspesialis(){return akses01.pcare_cek_subspesialis;}
    public static boolean getpcare_cek_sarana(){return akses01.pcare_cek_sarana;}
    public static boolean getpcare_cek_khusus(){return akses01.pcare_cek_khusus;}
    public static boolean getpcare_cek_obat(){return akses01.pcare_cek_obat;}
    public static boolean getpcare_cek_tindakan(){return akses01.pcare_cek_tindakan;}
    public static boolean getpcare_cek_faskessubspesialis(){return akses01.pcare_cek_faskessubspesialis;}
    public static boolean getpcare_cek_faskesalihrawat(){return akses01.pcare_cek_faskesalihrawat;}
    public static boolean getpcare_cek_faskesthalasemia(){return akses01.pcare_cek_faskesthalasemia;}
    public static boolean getpcare_mapping_obat(){return akses01.pcare_mapping_obat;}
    public static boolean getpcare_mapping_tindakan(){return akses01.pcare_mapping_tindakan;}
    public static boolean getpcare_club_prolanis(){return akses01.pcare_club_prolanis;}
    public static boolean getpcare_mapping_poli(){return akses01.pcare_mapping_poli;}
    public static boolean getpcare_kegiatan_kelompok(){return akses01.pcare_kegiatan_kelompok;}
    public static boolean getpcare_mapping_tindakan_ranap(){return akses01.pcare_mapping_tindakan_ranap;}
    public static boolean getpcare_peserta_kegiatan_kelompok(){return akses01.pcare_peserta_kegiatan_kelompok;}
    public static boolean getsirkulasi_obat3(){return akses01.sirkulasi_obat3;}
    public static boolean getbridging_pcare_daftar(){return akses01.bridging_pcare_daftar;}
    public static boolean getpcare_mapping_dokter(){return akses01.pcare_mapping_dokter;}
    public static boolean getranap_per_ruang(){return akses01.ranap_per_ruang;}
    public static boolean getpenyakit_ranap_cara_bayar(){return akses01.penyakit_ranap_cara_bayar;}
    public static boolean getanggota_militer_dirawat(){return akses01.anggota_militer_dirawat;}
    public static boolean getset_input_parsial(){return akses01.set_input_parsial;}
    public static boolean getlama_pelayanan_radiologi(){return akses01.lama_pelayanan_radiologi;}
    public static boolean getlama_pelayanan_lab(){return akses01.lama_pelayanan_lab;}
    public static boolean getbpjs_cek_sep(){return akses01.bpjs_cek_sep;}
    public static boolean getcatatan_perawatan(){return akses01.catatan_perawatan;}
    public static boolean getsurat_keluar(){return akses01.surat_keluar;}
    public static boolean getkegiatan_farmasi(){return akses01.kegiatan_farmasi;}
    public static boolean getstok_opname_logistik(){return akses01.stok_opname_logistik;}
    public static boolean getsirkulasi_non_medis(){return akses01.sirkulasi_non_medis;} 
    public static boolean getrekap_lab_pertahun(){return akses01.rekap_lab_pertahun;} 
    public static boolean getperujuk_lab_pertahun(){return akses01.perujuk_lab_pertahun;}
    public static boolean getrekap_radiologi_pertahun(){return akses01.rekap_radiologi_pertahun;}
    public static boolean getperujuk_radiologi_pertahun(){return akses01.perujuk_radiologi_pertahun;}
    public static boolean getjumlah_porsi_diet(){return akses01.jumlah_porsi_diet;}
    public static boolean getjumlah_macam_diet(){return akses01.jumlah_macam_diet;}
    public static boolean getpayment_point2(){return akses01.payment_point2;}
    public static boolean getpembayaran_akun_bayar2(){return akses01.pembayaran_akun_bayar2;}
    public static boolean gethapus_nota_salah(){return akses01.hapus_nota_salah;}
    public static boolean gethais_perbangsal(){return akses01.hais_perbangsal;}
    public static boolean getppn_obat(){return akses01.ppn_obat;}
    public static boolean getsaldo_akun_perbulan(){return akses01.saldo_akun_perbulan;}
    public static boolean getdisplay_apotek(){return akses01.display_apotek;}
    public static boolean getsisrute_referensi_faskes(){return akses01.sisrute_referensi_faskes;}
    public static boolean getsisrute_referensi_alasanrujuk(){return akses01.sisrute_referensi_alasanrujuk;}
    public static boolean getsisrute_referensi_diagnosa(){return akses01.sisrute_referensi_diagnosa;}
    public static boolean getsisrute_rujukan_masuk(){return akses01.sisrute_rujukan_masuk;}
    public static boolean getAktif(){return akses01.aktif;}
    public static void setAktif(boolean status){akses01.aktif=status;} 
    public static boolean getsisrute_rujukan_keluar(){return akses01.sisrute_rujukan_keluar;}
    public static boolean getbpjs_cek_skdp(){return akses01.bpjs_cek_skdp;}
    public static boolean getdata_batch(){return akses01.data_batch;}
    public static boolean getkunjungan_permintaan_lab(){return akses01.kunjungan_permintaan_lab;}
    public static boolean getkunjungan_permintaan_lab2(){return akses01.kunjungan_permintaan_lab2;}
    public static boolean getkunjungan_permintaan_radiologi(){return akses01.kunjungan_permintaan_radiologi;}
    public static boolean getkunjungan_permintaan_radiologi2(){return akses01.kunjungan_permintaan_radiologi2;}
    public static boolean getpcare_pemberian_obat(){return akses01.pcare_pemberian_obat;}
    public static boolean getpcare_pemberian_tindakan(){return akses01.pcare_pemberian_tindakan;}
    public static boolean getpembayaran_akun_bayar3(){return akses01.pembayaran_akun_bayar3;}
    public static boolean getpassword_asuransi(){return akses01.password_asuransi;}
    public static boolean getkemenkes_sitt(){return akses01.kemenkes_sitt;}
    public static boolean getsiranap_ketersediaan_kamar(){return akses01.siranap_ketersediaan_kamar;}
    public static boolean getgrafik_tb_periodelaporan(){return akses01.grafik_tb_periodelaporan;}
    public static boolean getgrafik_tb_rujukan(){return akses01.grafik_tb_rujukan;}
    public static boolean getgrafik_tb_riwayat(){return akses01.grafik_tb_riwayat;}
    public static boolean getgrafik_tb_tipediagnosis(){return akses01.grafik_tb_tipediagnosis;}
    public static boolean getgrafik_tb_statushiv(){return akses01.grafik_tb_statushiv;}
    public static boolean getgrafik_tb_skoringanak(){return akses01.grafik_tb_skoringanak;}
    public static boolean getgrafik_tb_konfirmasiskoring5(){return akses01.grafik_tb_konfirmasiskoring5;}
    public static boolean getgrafik_tb_konfirmasiskoring6(){return akses01.grafik_tb_konfirmasiskoring6;}
    public static boolean getgrafik_tb_sumberobat(){return akses01.grafik_tb_sumberobat;}
    public static boolean getgrafik_tb_hasilakhirpengobatan(){return akses01.grafik_tb_hasilakhirpengobatan;}
    public static boolean getgrafik_tb_hasilteshiv(){return akses01.grafik_tb_hasilteshiv;}
    public static boolean getkadaluarsa_batch(){return akses01.kadaluarsa_batch;}
    public static boolean getsisa_stok(){return akses01.sisa_stok;}
    public static boolean getobat_per_resep(){return akses01.obat_per_resep;}
    public static boolean getpemakaian_air_pdam(){return akses01.pemakaian_air_pdam;}
    public static boolean getlimbah_b3_medis(){return akses01.limbah_b3_medis;}
    public static boolean getgrafik_air_pdam_pertanggal(){return akses01.grafik_air_pdam_pertanggal;}
    public static boolean getgrafik_air_pdam_perbulan(){return akses01.grafik_air_pdam_perbulan;}
    public static boolean getgrafik_limbahb3_pertanggal(){return akses01.grafik_limbahb3_pertanggal;}
    public static boolean getgrafik_limbahb3_perbulan(){return akses01.grafik_limbahb3_perbulan;}
    public static boolean getlimbah_domestik(){return akses01.limbah_domestik;}
    public static boolean getgrafik_limbahdomestik_pertanggal(){return akses01.grafik_limbahdomestik_pertanggal;}
    public static boolean getgrafik_limbahdomestik_perbulan(){return akses01.grafik_limbahdomestik_perbulan;}
    public static boolean getmutu_air_limbah(){return akses01.mutu_air_limbah;}
    public static boolean getpest_control(){return akses01.pest_control;}
    public static boolean getruang_perpustakaan(){return akses01.ruang_perpustakaan;}
    public static boolean getkategori_perpustakaan(){return akses01.kategori_perpustakaan;}
    public static boolean getjenis_perpustakaan(){return akses01.jenis_perpustakaan;}
    public static boolean getpengarang_perpustakaan(){return akses01.pengarang_perpustakaan;}
    public static boolean getpenerbit_perpustakaan(){return akses01.penerbit_perpustakaan;}
    public static boolean getkoleksi_perpustakaan(){return akses01.koleksi_perpustakaan;}
    public static boolean getinventaris_perpustakaan(){return akses01.inventaris_perpustakaan;}
    public static boolean getset_peminjaman_perpustakaan(){return akses01.set_peminjaman_perpustakaan;}
    public static boolean getdenda_perpustakaan(){return akses01.denda_perpustakaan;}
    public static boolean getanggota_perpustakaan(){return akses01.anggota_perpustakaan;}
    public static boolean getpeminjaman_perpustakaan(){return akses01.peminjaman_perpustakaan;}
    public static boolean getbayar_denda_perpustakaan(){return akses01.bayar_denda_perpustakaan;}
    public static boolean getebook_perpustakaan(){return akses01.ebook_perpustakaan;}
    public static boolean getjenis_cidera_k3rs(){return akses01.jenis_cidera_k3rs;}
    public static boolean getpenyebab_k3rs(){return akses01.penyebab_k3rs;}
    public static boolean getjenis_luka_k3rs(){return akses01.jenis_luka_k3rs;}
    public static boolean getlokasi_kejadian_k3rs(){return akses01.lokasi_kejadian_k3rs;}
    public static boolean getdampak_cidera_k3rs(){return akses01.dampak_cidera_k3rs;}
    public static boolean getjenis_pekerjaan_k3rs(){return akses01.jenis_pekerjaan_k3rs;}
    public static boolean getbagian_tubuh_k3rs(){return akses01.bagian_tubuh_k3rs;}
    public static boolean getperistiwa_k3rs(){return akses01.peristiwa_k3rs;}
    public static boolean getgrafik_k3_pertahun(){return akses01.grafik_k3_pertahun;}
    public static boolean getgrafik_k3_perbulan(){return akses01.grafik_k3_perbulan;}
    public static boolean getgrafik_k3_pertanggal(){return akses01.grafik_k3_pertanggal;}
    public static boolean getgrafik_k3_perjeniscidera(){return akses01.grafik_k3_perjeniscidera;}
    public static boolean getgrafik_k3_perpenyebab(){return akses01.grafik_k3_perpenyebab;}
    public static boolean getgrafik_k3_perjenisluka(){return akses01.grafik_k3_perjenisluka;}
    public static boolean getgrafik_k3_lokasikejadian(){return akses01.grafik_k3_lokasikejadian;}
    public static boolean getgrafik_k3_dampakcidera(){return akses01.grafik_k3_dampakcidera;}
    public static boolean getgrafik_k3_perjenispekerjaan(){return akses01.grafik_k3_perjenispekerjaan;}
    public static boolean getgrafik_k3_perbagiantubuh(){return akses01.grafik_k3_perbagiantubuh;}
    public static boolean getjenis_cidera_k3rstahun(){return akses01.jenis_cidera_k3rstahun;}
    public static boolean getpenyebab_k3rstahun(){return akses01.penyebab_k3rstahun;}
    public static boolean getjenis_luka_k3rstahun(){return akses01.jenis_luka_k3rstahun;}
    public static boolean getlokasi_kejadian_k3rstahun(){return akses01.lokasi_kejadian_k3rstahun;}
    public static boolean getdampak_cidera_k3rstahun(){return akses01.dampak_cidera_k3rstahun;}
    public static boolean getjenis_pekerjaan_k3rstahun(){return akses01.jenis_pekerjaan_k3rstahun;}
    public static boolean getbagian_tubuh_k3rstahun(){return akses01.bagian_tubuh_k3rstahun;}
    public static boolean getsekrining_rawat_jalan(){return akses01.sekrining_rawat_jalan;}
    public static boolean getbpjs_histori_pelayanan(){return akses01.bpjs_histori_pelayanan;}
    public static boolean getrekap_mutasi_berkas(){return akses01.rekap_mutasi_berkas;}
    public static boolean getskrining_ralan_pernapasan_pertahun(){return akses01.skrining_ralan_pernapasan_pertahun;}
    public static boolean getpengajuan_barang_medis(){return akses01.pengajuan_barang_medis;}
    public static boolean getpengajuan_barang_nonmedis(){return akses01.pengajuan_barang_nonmedis;}
    public static boolean getgrafik_kunjungan_ranapbulan(){return akses01.grafik_kunjungan_ranapbulan;}
    public static boolean getgrafik_kunjungan_ranaptanggal(){return akses01.grafik_kunjungan_ranaptanggal;}
    public static boolean getgrafik_kunjungan_ranap_peruang(){return akses01.grafik_kunjungan_ranap_peruang;}
    public static boolean getkunjungan_bangsal_pertahun(){return akses01.kunjungan_bangsal_pertahun;}
    public static boolean getgrafik_jenjang_jabatanpegawai(){return akses01.grafik_jenjang_jabatanpegawai;}
    public static boolean getgrafik_bidangpegawai(){return akses01.grafik_bidangpegawai;}
    public static boolean getgrafik_departemenpegawai(){return akses01.grafik_departemenpegawai;}
    public static boolean getgrafik_pendidikanpegawai(){return akses01.grafik_pendidikanpegawai;}
    public static boolean getgrafik_sttswppegawai(){return akses01.grafik_sttswppegawai;}
    public static boolean getgrafik_sttskerjapegawai(){return akses01.grafik_sttskerjapegawai;}
    public static boolean getgrafik_sttspulangranap(){return akses01.grafik_sttspulangranap;}
    public static boolean getkip_pasien_ranap(){return akses01.kip_pasien_ranap;}
    public static boolean getkip_pasien_ralan(){return akses01.kip_pasien_ralan;}
    public static boolean getbpjs_mapping_dokterdpjp(){return akses01.bpjs_mapping_dokterdpjp;}
    public static boolean getdata_triase_igd(){return akses01.data_triase_igd;}
    public static boolean getmaster_triase_skala1(){return akses01.master_triase_skala1;}
    public static boolean getmaster_triase_skala2(){return akses01.master_triase_skala2;}
    public static boolean getmaster_triase_skala3(){return akses01.master_triase_skala3;}
    public static boolean getmaster_triase_skala4(){return akses01.master_triase_skala4;}
    public static boolean getmaster_triase_skala5(){return akses01.master_triase_skala5;}
    public static boolean getmaster_triase_pemeriksaan(){return akses01.master_triase_pemeriksaan;}
    public static boolean getmaster_triase_macamkasus(){return akses01.master_triase_macamkasus;}
    public static boolean getrekap_permintaan_diet(){return akses01.rekap_permintaan_diet;}
    public static boolean getdaftar_pasien_ranap(){return akses01.daftar_pasien_ranap;}
    public static boolean getdaftar_pasien_ranaptni(){return akses01.daftar_pasien_ranaptni;}
    public static boolean getpengajuan_asetinventaris(){return akses01.pengajuan_asetinventaris;}
    public static boolean getitem_apotek_jenis(){return akses01.item_apotek_jenis;}
    public static boolean getitem_apotek_kategori(){return akses01.item_apotek_kategori;}
    public static boolean getitem_apotek_golongan(){return akses01.item_apotek_golongan;}
    public static boolean getitem_apotek_industrifarmasi(){return akses01.item_apotek_industrifarmasi;}
    public static boolean getobat10_terbanyak_poli(){return akses01.obat10_terbanyak_poli;}
    public static boolean getgrafik_pengajuan_aset_urgensi(){return akses01.grafik_pengajuan_aset_urgensi;}
    public static boolean getgrafik_pengajuan_aset_status(){return akses01.grafik_pengajuan_aset_status;}
    public static boolean getgrafik_pengajuan_aset_departemen(){return akses01.grafik_pengajuan_aset_departemen;}
    public static boolean getrekap_pengajuan_aset_departemen(){return akses01.rekap_pengajuan_aset_departemen;}
    public static boolean getgrafik_kelompok_jabatanpegawai(){return akses01.grafik_kelompok_jabatanpegawai;}
    public static boolean getgrafik_resiko_kerjapegawai(){return akses01.grafik_resiko_kerjapegawai;}
    public static boolean getgrafik_emergency_indexpegawai(){return akses01.grafik_emergency_indexpegawai;}
    public static boolean getgrafik_inventaris_ruang(){return akses01.grafik_inventaris_ruang;}
    public static boolean getharian_HAIs2(){return akses01.harian_HAIs2;}
    public static boolean getgrafik_inventaris_jenis(){return akses01.grafik_inventaris_jenis;}
    public static boolean getdata_resume_pasien(){return akses01.data_resume_pasien;}
    public static boolean getperkiraan_biaya_ranap(){return akses01.perkiraan_biaya_ranap;}
    public static boolean getrekap_obat_poli(){return akses01.rekap_obat_poli;}
    public static boolean getrekap_obat_pasien(){return akses01.rekap_obat_pasien;}
    public static boolean getgrafik_HAIs_pasienbangsal(){return akses01.grafik_HAIs_pasienbangsal;}
    public static boolean getgrafik_HAIs_pasienbulan(){return akses01.grafik_HAIs_pasienbulan;}
    public static boolean getpermintaan_perbaikan_inventaris(){return akses01.permintaan_perbaikan_inventaris;}
    public static boolean getgrafik_HAIs_laju_vap(){return akses01.grafik_HAIs_laju_vap;} 
    public static boolean getgrafik_HAIs_laju_iad(){return akses01.grafik_HAIs_laju_iad;}
    public static boolean getgrafik_HAIs_laju_pleb(){return akses01.grafik_HAIs_laju_pleb;}
    public static boolean getgrafik_HAIs_laju_isk(){return akses01.grafik_HAIs_laju_isk;}
    public static boolean getgrafik_HAIs_laju_ilo(){return akses01.grafik_HAIs_laju_ilo;}
    public static boolean getgrafik_HAIs_laju_hap(){return akses01.grafik_HAIs_laju_hap;}
    public static boolean getinhealth_mapping_poli(){return akses01.inhealth_mapping_poli;}
    public static boolean getinhealth_mapping_dokter(){return akses01.inhealth_mapping_dokter;}
    public static boolean getinhealth_mapping_tindakan_ralan(){return akses01.inhealth_mapping_tindakan_ralan;}
    public static boolean getinhealth_mapping_tindakan_ranap(){return akses01.inhealth_mapping_tindakan_ranap;}
    public static boolean getinhealth_mapping_tindakan_radiologi(){return akses01.inhealth_mapping_tindakan_radiologi;}
    public static boolean getinhealth_mapping_tindakan_laborat(){return akses01.inhealth_mapping_tindakan_laborat;}
    public static boolean getinhealth_mapping_tindakan_operasi(){return akses01.inhealth_mapping_tindakan_operasi;}
    public static boolean gethibah_obat_bhp(){return akses01.hibah_obat_bhp;}
    public static boolean getasal_hibah(){return akses01.asal_hibah;}
    public static boolean getasuhan_gizi(){return akses01.asuhan_gizi;}
    public static boolean getinhealth_kirim_tagihan(){return akses01.inhealth_kirim_tagihan;}
    public static boolean getsirkulasi_obat4(){return akses01.sirkulasi_obat4;}
    public static boolean getsirkulasi_obat5(){return akses01.sirkulasi_obat5;}
    public static boolean getsirkulasi_non_medis2(){return akses01.sirkulasi_non_medis2;}
    public static boolean getmonitoring_asuhan_gizi(){return akses01.monitoring_asuhan_gizi;}
    public static boolean getpenerimaan_obat_perbulan(){return akses01.penerimaan_obat_perbulan;}
    public static boolean getrekap_kunjungan(){return akses01.rekap_kunjungan;}
    public static boolean getsurat_sakit(){return akses01.surat_sakit;}
    public static boolean getpenilaian_awal_keperawatan_ralan(){return akses01.penilaian_awal_keperawatan_ralan;}
    public static boolean getpermintaan_diet(){return akses01.permintaan_diet;}
    public static boolean getmaster_masalah_keperawatan(){return akses01.master_masalah_keperawatan;}
    public static boolean getpengajuan_cuti(){return akses01.pengajuan_cuti;}
    public static boolean getkedatangan_pasien(){return akses01.kedatangan_pasien;}
    public static boolean getutd_pendonor(){return akses01.utd_pendonor;}
    public static boolean gettoko_suplier(){return akses01.toko_suplier;}
    public static boolean gettoko_jenis(){return akses01.toko_jenis;}
    public static boolean gettoko_set_harga(){return akses01.toko_set_harga;}
    public static boolean gettoko_barang(){return akses01.toko_barang;}
    public static boolean getpenagihan_piutang_pasien(){return akses01.penagihan_piutang_pasien;}
    public static boolean getakun_penagihan_piutang(){return akses01.akun_penagihan_piutang;}
    public static boolean getstok_opname_toko(){return akses01.stok_opname_toko;}
    public static boolean gettoko_riwayat_barang(){return akses01.toko_riwayat_barang;}
    public static boolean gettoko_surat_pemesanan(){return akses01.toko_surat_pemesanan;}
    public static boolean gettoko_pengajuan_barang(){return akses01.toko_pengajuan_barang;}
    public static boolean gettoko_penerimaan_barang(){return akses01.toko_penerimaan_barang;}
    public static boolean gettoko_pengadaan_barang(){return akses01.toko_pengadaan_barang;}
    public static boolean gettoko_hutang(){return akses01.toko_hutang;}
    public static boolean gettoko_bayar_pemesanan(){return akses01.toko_bayar_pemesanan;}
    public static boolean gettoko_member(){return akses01.toko_member;}
    public static boolean gettoko_penjualan(){return akses01.toko_penjualan;}
    public static boolean getregistrasi_poli_per_tanggal(){return akses01.registrasi_poli_per_tanggal;}
    public static boolean gettoko_piutang(){return akses01.toko_piutang;}
    public static boolean gettoko_retur_beli(){return akses01.toko_retur_beli;}
    public static boolean getipsrs_returbeli(){return akses01.ipsrs_returbeli;}
    public static boolean getipsrs_riwayat_barang(){return akses01.ipsrs_riwayat_barang;}
    public static boolean getpasien_corona(){return akses01.pasien_corona;}
    public static boolean gettoko_pendapatan_harian(){return akses01.toko_pendapatan_harian;}
    public static boolean getdiagnosa_pasien_corona(){return akses01.diagnosa_pasien_corona;}
    public static boolean getperawatan_pasien_corona(){return akses01.perawatan_pasien_corona;}
    public static boolean getpenilaian_awal_keperawatan_gigi(){return akses01.penilaian_awal_keperawatan_gigi;}
    public static boolean getmaster_masalah_keperawatan_gigi(){return akses01.master_masalah_keperawatan_gigi;}
    public static boolean gettoko_bayar_piutang(){return akses01.toko_bayar_piutang;}
    public static boolean gettoko_piutang_harian(){return akses01.toko_piutang_harian;}
    public static boolean gettoko_penjualan_harian(){return akses01.toko_penjualan_harian;}
    public static boolean getdeteksi_corona(){return akses01.deteksi_corona;}
    public static boolean getpenilaian_awal_keperawatan_kebidanan(){return akses01.penilaian_awal_keperawatan_kebidanan;}
    public static boolean getpengumuman_epasien(){return akses01.pengumuman_epasien;}
    public static boolean getsurat_hamil(){return akses01.surat_hamil;}
    public static boolean getset_tarif_online(){return akses01.set_tarif_online;}
    public static boolean getbooking_periksa(){return akses01.booking_periksa;}
    public static boolean gettoko_sirkulasi(){return akses01.toko_sirkulasi;}
    public static boolean gettoko_retur_jual(){return akses01.toko_retur_jual;}
    public static boolean gettoko_retur_piutang(){return akses01.toko_retur_piutang;}
    public static boolean gettoko_sirkulasi2(){return akses01.toko_sirkulasi2;}
    public static boolean gettoko_keuntungan_barang(){return akses01.toko_keuntungan_barang;}
    public static boolean getzis_pengeluaran_penerima_dankes(){return akses01.zis_pengeluaran_penerima_dankes;}
    public static boolean getzis_penghasilan_penerima_dankes(){return akses01.zis_penghasilan_penerima_dankes;}
    public static boolean getzis_ukuran_rumah_penerima_dankes(){return akses01.zis_ukuran_rumah_penerima_dankes;}
    public static boolean getzis_dinding_rumah_penerima_dankes(){return akses01.zis_dinding_rumah_penerima_dankes;}
    public static boolean getzis_lantai_rumah_penerima_dankes(){return akses01.zis_lantai_rumah_penerima_dankes;}
    public static boolean getzis_atap_rumah_penerima_dankes(){return akses01.zis_atap_rumah_penerima_dankes;}
    public static boolean getzis_kepemilikan_rumah_penerima_dankes(){return akses01.zis_kepemilikan_rumah_penerima_dankes;}
    public static boolean getzis_kamar_mandi_penerima_dankes(){return akses01.zis_kamar_mandi_penerima_dankes;}
    public static boolean getzis_dapur_rumah_penerima_dankes(){return akses01.zis_dapur_rumah_penerima_dankes;}
    public static boolean getzis_kursi_rumah_penerima_dankes(){return akses01.zis_kursi_rumah_penerima_dankes;}
    public static boolean getzis_kategori_phbs_penerima_dankes(){return akses01.zis_kategori_phbs_penerima_dankes;}
    public static boolean getzis_elektronik_penerima_dankes(){return akses01.zis_elektronik_penerima_dankes;}
    public static boolean getzis_ternak_penerima_dankes(){return akses01.zis_ternak_penerima_dankes;}
    public static boolean getzis_jenis_simpanan_penerima_dankes(){return akses01.zis_jenis_simpanan_penerima_dankes;}
    public static boolean getpenilaian_awal_keperawatan_anak(){return akses01.penilaian_awal_keperawatan_anak;}
    public static boolean getzis_kategori_asnaf_penerima_dankes(){return akses01.zis_kategori_asnaf_penerima_dankes;}
    public static boolean getmaster_masalah_keperawatan_anak(){return akses01.master_masalah_keperawatan_anak;}
    public static boolean getmaster_imunisasi(){return akses01.master_imunisasi;}
    public static boolean getzis_patologis_penerima_dankes(){return akses01.zis_patologis_penerima_dankes;}
    public static boolean getpcare_cek_kartu(){return akses01.pcare_cek_kartu;}
    public static boolean getsurat_bebas_narkoba(){return akses01.surat_bebas_narkoba;}
    public static boolean getsurat_keterangan_covid(){return akses01.surat_keterangan_covid;}
    public static boolean getpemakaian_air_tanah(){return akses01.pemakaian_air_tanah;}
    public static boolean getgrafik_air_tanah_pertanggal(){return akses01.grafik_air_tanah_pertanggal;}
    public static boolean getgrafik_air_tanah_perbulan(){return akses01.grafik_air_tanah_perbulan;}
    public static boolean getlama_pelayanan_poli(){return akses01.lama_pelayanan_poli;}
    public static boolean gethemodialisa(){return akses01.hemodialisa;}
    public static boolean getgrafik_harian_hemodialisa(){return akses01.grafik_harian_hemodialisa;}
    public static boolean getgrafik_bulanan_hemodialisa(){return akses01.grafik_bulanan_hemodialisa;}
    public static boolean getgrafik_tahunan_hemodialisa(){return akses01.grafik_tahunan_hemodialisa;}
    public static boolean getgrafik_bulanan_meninggal(){return akses01.grafik_bulanan_meninggal;}
    public static boolean getlaporan_tahunan_irj(){return akses01.laporan_tahunan_irj;}
    public static boolean getperbaikan_inventaris(){return akses01.perbaikan_inventaris;}
    public static boolean getsurat_cuti_hamil(){return akses01.surat_cuti_hamil;}
    public static boolean getpermintaan_stok_obat_pasien(){return akses01.permintaan_stok_obat_pasien;}
    public static boolean getpemeliharaan_inventaris(){return akses01.pemeliharaan_inventaris;}
    public static boolean getklasifikasi_pasien_ranap(){return akses01.klasifikasi_pasien_ranap;}
    public static boolean getbulanan_klasifikasi_pasien_ranap(){return akses01.bulanan_klasifikasi_pasien_ranap;}
    public static boolean getharian_klasifikasi_pasien_ranap(){return akses01.harian_klasifikasi_pasien_ranap;}
    public static boolean getklasifikasi_pasien_perbangsal(){return akses01.klasifikasi_pasien_perbangsal;}
    public static boolean getsoap_perawatan(){return akses01.soap_perawatan;}
    public static boolean getklaim_rawat_jalan(){return akses01.klaim_rawat_jalan;}
    public static boolean getskrining_gizi(){return akses01.skrining_gizi;}
    public static boolean getlama_penyiapan_rm(){return akses01.lama_penyiapan_rm;}
    public static boolean getdosis_radiologi(){return akses01.dosis_radiologi;}
    public static boolean getdemografi_umur_kunjungan(){return akses01.demografi_umur_kunjungan;}
    public static boolean getjam_diet_pasien(){return akses01.jam_diet_pasien;}
    public static boolean getrvu_bpjs(){return akses01.rvu_bpjs;}
    public static boolean getverifikasi_penerimaan_farmasi(){return akses01.verifikasi_penerimaan_farmasi;}
    public static boolean getverifikasi_penerimaan_logistik(){return akses01.verifikasi_penerimaan_logistik;}
    public static boolean getpemeriksaan_lab_pa(){return akses01.pemeriksaan_lab_pa;}
    public static boolean getringkasan_pengajuan_obat(){return akses01.ringkasan_pengajuan_obat;}
    public static boolean getringkasan_pemesanan_obat(){return akses01.ringkasan_pemesanan_obat;}
    public static boolean getringkasan_pengadaan_obat(){return akses01.ringkasan_pengadaan_obat;}
    public static boolean getringkasan_penerimaan_obat(){return akses01.ringkasan_penerimaan_obat;}
    public static boolean getringkasan_hibah_obat(){return akses01.ringkasan_hibah_obat;}
    public static boolean getringkasan_penjualan_obat(){return akses01.ringkasan_penjualan_obat;}
    public static boolean getringkasan_beri_obat(){return akses01.ringkasan_beri_obat;}
    public static boolean getringkasan_piutang_obat(){return akses01.ringkasan_piutang_obat;}
    public static boolean getringkasan_stok_keluar_obat(){return akses01.ringkasan_stok_keluar_obat;}
    public static boolean getringkasan_retur_suplier_obat(){return akses01.ringkasan_retur_suplier_obat;}
    public static boolean getringkasan_retur_pembeli_obat(){return akses01.ringkasan_retur_pembeli_obat;}
    public static boolean getpenilaian_awal_keperawatan_ranapkebidanan(){return akses01.penilaian_awal_keperawatan_ranapkebidanan;}
    public static boolean getringkasan_pengajuan_nonmedis(){return akses01.ringkasan_pengajuan_nonmedis;}
    public static boolean getringkasan_pemesanan_nonmedis(){return akses01.ringkasan_pemesanan_nonmedis;}
    public static boolean getringkasan_pengadaan_nonmedis(){return akses01.ringkasan_pengadaan_nonmedis;}
    public static boolean getringkasan_penerimaan_nonmedis(){return akses01.ringkasan_penerimaan_nonmedis;}
    public static boolean getringkasan_stokkeluar_nonmedis(){return akses01.ringkasan_stokkeluar_nonmedis;}
    public static boolean getringkasan_returbeli_nonmedis(){return akses01.ringkasan_returbeli_nonmedis;}
    public static boolean getomset_penerimaan(){return akses01.omset_penerimaan;}
    public static boolean getvalidasi_penagihan_piutang(){return akses01.validasi_penagihan_piutang;}
    public static boolean getpermintaan_ranap(){return akses01.permintaan_ranap;}
    public static boolean getbpjs_diagnosa_prb(){return akses01.bpjs_diagnosa_prb;}
    public static boolean getbpjs_obat_prb(){return akses01.bpjs_obat_prb;}
    public static boolean getbpjs_surat_kontrol(){return akses01.bpjs_surat_kontrol;}
    public static boolean getpenggunaan_bhp_ok(){return akses01.penggunaan_bhp_ok;}
    public static boolean getsurat_keterangan_rawat_inap(){return akses01.surat_keterangan_rawat_inap;}
    public static boolean getsurat_keterangan_sehat(){return akses01.surat_keterangan_sehat;}
    public static boolean getpendapatan_per_carabayar(){return akses01.pendapatan_per_carabayar;}
    public static boolean getakun_host_to_host_bank_jateng(){return akses01.akun_host_to_host_bank_jateng;}
    public static boolean getpembayaran_bank_jateng(){return akses01.pembayaran_bank_jateng;}
    public static boolean getbpjs_surat_pri(){return akses01.bpjs_surat_pri;}
    public static boolean getringkasan_tindakan(){return akses01.ringkasan_tindakan;}
    public static boolean getlama_pelayanan_pasien(){return akses01.lama_pelayanan_pasien;}
    public static boolean getsurat_sakit_pihak_2(){return akses01.surat_sakit_pihak_2;}
    public static boolean gettagihan_hutang_obat(){return akses01.tagihan_hutang_obat;}
    public static boolean getreferensi_mobilejkn_bpjs(){return akses01.referensi_mobilejkn_bpjs;}
    public static boolean getbatal_pendaftaran_mobilejkn_bpjs(){return akses01.batal_pendaftaran_mobilejkn_bpjs;}
    public static boolean getlama_operasi(){return akses01.lama_operasi;}
    public static boolean getgrafik_inventaris_kategori(){return akses01.grafik_inventaris_kategori;}
    public static boolean getgrafik_inventaris_merk(){return akses01.grafik_inventaris_merk;}
    public static boolean getgrafik_inventaris_produsen(){return akses01.grafik_inventaris_produsen;}
    public static boolean getpengembalian_deposit_pasien(){return akses01.pengembalian_deposit_pasien;}
    public static boolean getvalidasi_tagihan_hutang_obat(){return akses01.validasi_tagihan_hutang_obat;}
    public static boolean getpiutang_obat_belum_lunas(){return akses01.piutang_obat_belum_lunas;}
    public static boolean getintegrasi_briapi(){return akses01.integrasi_briapi;}
    public static boolean getpengadaan_aset_inventaris(){return akses01.pengadaan_aset_inventaris;}
    public static boolean getakun_aset_inventaris(){return akses01.akun_aset_inventaris;}
    public static boolean getsuplier_inventaris(){return akses01.suplier_inventaris;}
    public static boolean getpenerimaan_aset_inventaris(){return akses01.penerimaan_aset_inventaris;}
    public static boolean getbayar_pemesanan_iventaris(){return akses01.bayar_pemesanan_iventaris;}
    public static boolean gethutang_aset_inventaris(){return akses01.hutang_aset_inventaris;}
    public static boolean gethibah_aset_inventaris(){return akses01.hibah_aset_inventaris;}
    public static boolean gettitip_faktur_non_medis(){return akses01.titip_faktur_non_medis;}
    public static boolean getvalidasi_tagihan_non_medis(){return akses01.validasi_tagihan_non_medis;}
    public static boolean gettitip_faktur_aset(){return akses01.titip_faktur_aset;}
    public static boolean getvalidasi_tagihan_aset(){return akses01.validasi_tagihan_aset;}
    public static boolean gethibah_non_medis(){return akses01.hibah_non_medis;}
    public static boolean getpcare_alasan_tacc(){return akses01.pcare_alasan_tacc;}
    public static boolean getresep_luar(){return akses01.resep_luar;}
    public static boolean getsurat_bebas_tbc(){return akses01.surat_bebas_tbc;}
    public static boolean getsurat_buta_warna(){return akses01.surat_buta_warna;}
    public static boolean getsurat_bebas_tato(){return akses01.surat_bebas_tato;}
    public static boolean getsurat_kewaspadaan_kesehatan(){return akses01.surat_kewaspadaan_kesehatan;}
    public static boolean getgrafik_porsidiet_pertanggal(){return akses01.grafik_porsidiet_pertanggal;}
    public static boolean getgrafik_porsidiet_perbulan(){return akses01.grafik_porsidiet_perbulan;}
    public static boolean getgrafik_porsidiet_pertahun(){return akses01.grafik_porsidiet_pertahun;}
    public static boolean getgrafik_porsidiet_perbangsal(){return akses01.grafik_porsidiet_perbangsal;}
    public static boolean getpenilaian_awal_medis_ralan(){return akses01.penilaian_awal_medis_ralan;}
    public static boolean getmaster_masalah_keperawatan_mata(){return akses01.master_masalah_keperawatan_mata;}
    public static boolean getpenilaian_awal_keperawatan_mata(){return akses01.penilaian_awal_keperawatan_mata;}
    public static boolean getpenilaian_awal_medis_ranap(){return akses01.penilaian_awal_medis_ranap;}
    public static boolean getpenilaian_awal_medis_ranap_kebidanan(){return akses01.penilaian_awal_medis_ranap_kebidanan;}
    public static boolean getpenilaian_awal_medis_ralan_kebidanan(){return akses01.penilaian_awal_medis_ralan_kebidanan;}
    public static boolean getpenilaian_awal_medis_igd(){return akses01.penilaian_awal_medis_igd;}
    public static boolean getpenilaian_awal_medis_ralan_anak(){return akses01.penilaian_awal_medis_ralan_anak;}
    public static boolean getbpjs_referensi_poli_hfis(){return akses01.bpjs_referensi_poli_hfis;}
    public static boolean getbpjs_referensi_dokter_hfis(){return akses01.bpjs_referensi_dokter_hfis;}
    public static boolean getbpjs_referensi_jadwal_hfis(){return akses01.bpjs_referensi_jadwal_hfis;}
    public static boolean getpenilaian_fisioterapi(){return akses01.penilaian_fisioterapi;}
    public static boolean getbpjs_program_prb(){return akses01.bpjs_program_prb;}
    public static boolean getbpjs_suplesi_jasaraharja(){return akses01.bpjs_suplesi_jasaraharja;}
    public static boolean getbpjs_data_induk_kecelakaan(){return akses01.bpjs_data_induk_kecelakaan;}
    public static boolean getbpjs_sep_internal(){return akses01.bpjs_sep_internal;}
    public static boolean getbpjs_klaim_jasa_raharja(){return akses01.bpjs_klaim_jasa_raharja;}
    public static boolean getbpjs_daftar_finger_print(){return akses01.bpjs_daftar_finger_print;}
    public static boolean getbpjs_rujukan_khusus(){return akses01.bpjs_rujukan_khusus;}
    public static boolean getpemeliharaan_gedung(){return akses01.pemeliharaan_gedung;}
    public static boolean getgrafik_perbaikan_inventaris_pertanggal(){return akses01.grafik_perbaikan_inventaris_pertanggal;}
    public static boolean getgrafik_perbaikan_inventaris_perbulan(){return akses01.grafik_perbaikan_inventaris_perbulan;}
    public static boolean getgrafik_perbaikan_inventaris_pertahun(){return akses01.grafik_perbaikan_inventaris_pertahun;}
    public static boolean getgrafik_perbaikan_inventaris_perpelaksana_status(){return akses01.grafik_perbaikan_inventaris_perpelaksana_status;}
    public static boolean getpenilaian_mcu(){return akses01.penilaian_mcu;}
    public static boolean getpeminjam_piutang(){return akses01.peminjam_piutang;}
    public static boolean getpiutang_lainlain(){return akses01.piutang_lainlain;}
    public static boolean getcara_bayar(){return akses01.cara_bayar;}
    public static boolean getaudit_kepatuhan_apd(){return akses01.audit_kepatuhan_apd;}
    public static boolean getbpjs_task_id(){return akses01.bpjs_task_id;}
    public static boolean getbayar_piutang_lain(){return akses01.bayar_piutang_lain;}
    public static boolean getpembayaran_akun_bayar4(){return akses01.pembayaran_akun_bayar4;}
    public static boolean getstok_akhir_farmasi_pertanggal(){return akses01.stok_akhir_farmasi_pertanggal;}
    public static boolean getriwayat_kamar_pasien(){return akses01.riwayat_kamar_pasien;}
    public static boolean getuji_fungsi_kfr(){return akses01.uji_fungsi_kfr;}
    public static boolean gethapus_berkas_digital_perawatan(){return akses01.hapus_berkas_digital_perawatan;}
    public static boolean getkategori_pengeluaran_harian(){return akses01.kategori_pengeluaran_harian;}
    public static boolean getkategori_pemasukan_lain(){return akses01.kategori_pemasukan_lain;}
    public static boolean getpembayaran_akun_bayar5(){return akses01.pembayaran_akun_bayar5;}
    public static boolean getruang_ok(){return akses01.ruang_ok;}
    public static boolean getjasa_tindakan_pasien(){return akses01.jasa_tindakan_pasien;}
    public static boolean gettelaah_resep(){return akses01.telaah_resep;}
    public static boolean getpermintaan_resep_pulang(){return akses01.permintaan_resep_pulang;}
    public static boolean getrekap_jm_dokter(){return akses01.rekap_jm_dokter;}
    public static boolean getstatus_data_rm(){return akses01.status_data_rm;}
    public static boolean getubah_petugas_lab_pk(){return akses01.ubah_petugas_lab_pk;}
    public static boolean getubah_petugas_lab_pa(){return akses01.ubah_petugas_lab_pa;}
    public static boolean getubah_petugas_radiologi(){return akses01.ubah_petugas_radiologi;}
    public static boolean getgabung_norawat(){return akses01.gabung_norawat;}
    public static boolean getgabung_rm(){return akses01.gabung_rm;}
    public static boolean getringkasan_biaya_obat_pasien_pertanggal(){return akses01.ringkasan_biaya_obat_pasien_pertanggal;}
    public static boolean getmaster_masalah_keperawatan_igd(){return akses01.master_masalah_keperawatan_igd;}
    public static boolean getpenilaian_awal_keperawatan_igd(){return akses01.penilaian_awal_keperawatan_igd;}
    public static boolean getbpjs_referensi_dpho_apotek(){return akses01.bpjs_referensi_dpho_apotek;}
    public static boolean getbpjs_referensi_poli_apotek(){return akses01.bpjs_referensi_poli_apotek;}
    public static boolean getbayar_jm_dokter(){return akses01.bayar_jm_dokter;}
    public static boolean getbpjs_referensi_faskes_apotek(){return akses01.bpjs_referensi_faskes_apotek;}
    public static boolean getbpjs_referensi_spesialistik_apotek(){return akses01.bpjs_referensi_spesialistik_apotek;}
    public static boolean getpembayaran_briva(){return akses01.pembayaran_briva;}
    public static boolean getpenilaian_awal_keperawatan_ranap(){return akses01.penilaian_awal_keperawatan_ranap;}
    public static boolean getnilai_penerimaan_vendor_farmasi_perbulan(){return akses01.nilai_penerimaan_vendor_farmasi_perbulan;}
    public static boolean getakun_bayar_hutang(){return akses01.akun_bayar_hutang;}
    public static boolean getmaster_rencana_keperawatan(){return akses01.master_rencana_keperawatan;}
    public static boolean getlaporan_tahunan_igd(){return akses01.laporan_tahunan_igd;}
    public static boolean getobat_bhp_tidakbergerak(){return akses01.obat_bhp_tidakbergerak;}
    public static boolean getringkasan_hutang_vendor_farmasi(){return akses01.ringkasan_hutang_vendor_farmasi;}
    public static boolean getnilai_penerimaan_vendor_nonmedis_perbulan(){return akses01.nilai_penerimaan_vendor_nonmedis_perbulan;}
    public static boolean getringkasan_hutang_vendor_nonmedis(){return akses01.ringkasan_hutang_vendor_nonmedis;}
    public static boolean getanggota_polri_dirawat(){return akses01.anggota_polri_dirawat;}
    public static boolean getdaftar_pasien_ranap_polri(){return akses01.daftar_pasien_ranap_polri;}
    public static boolean getsoap_ralan_polri(){return akses01.soap_ralan_polri;}
    public static boolean getsoap_ranap_polri(){return akses01.soap_ranap_polri;}
    public static boolean getlaporan_penyakit_polri(){return akses01.laporan_penyakit_polri;}
    public static boolean getmaster_rencana_keperawatan_anak(){return akses01.master_rencana_keperawatan_anak;}
    public static boolean getjumlah_pengunjung_ralan_polri(){return akses01.jumlah_pengunjung_ralan_polri;}
    public static boolean getcatatan_observasi_igd(){return akses01.catatan_observasi_igd;}
    public static boolean getcatatan_observasi_ranap(){return akses01.catatan_observasi_ranap;}
    public static boolean getcatatan_observasi_ranap_kebidanan(){return akses01.catatan_observasi_ranap_kebidanan;}
    public static boolean getcatatan_observasi_ranap_postpartum(){return akses01.catatan_observasi_ranap_postpartum;}
    public static boolean getpenilaian_awal_medis_ralan_tht(){return akses01.penilaian_awal_medis_ralan_tht;}
    public static boolean getpenilaian_psikologi(){return akses01.penilaian_psikologi;}
    public static boolean getaudit_cuci_tangan_medis(){return akses01.audit_cuci_tangan_medis;}
    public static boolean getaudit_pembuangan_limbah(){return akses01.audit_pembuangan_limbah;}
    public static boolean getruang_audit_kepatuhan(){return akses01.ruang_audit_kepatuhan;}
    public static boolean getaudit_pembuangan_benda_tajam(){return akses01.audit_pembuangan_benda_tajam;}
    public static boolean getaudit_penanganan_darah(){return akses01.audit_penanganan_darah;}
    public static boolean getaudit_pengelolaan_linen_kotor(){return akses01.audit_pengelolaan_linen_kotor;}
    public static boolean getaudit_penempatan_pasien(){return akses01.audit_penempatan_pasien;}
    public static boolean getaudit_kamar_jenazah(){return akses01.audit_kamar_jenazah;}
    public static boolean getaudit_bundle_iadp(){return akses01.audit_bundle_iadp;}
    public static boolean getaudit_bundle_ido(){return akses01.audit_bundle_ido;}
    public static boolean getaudit_fasilitas_kebersihan_tangan(){return akses01.audit_fasilitas_kebersihan_tangan;}
    public static boolean getaudit_fasilitas_apd(){return akses01.audit_fasilitas_apd;}
    public static boolean getaudit_pembuangan_limbah_cair_infeksius(){return akses01.audit_pembuangan_limbah_cair_infeksius;}
    public static boolean getaudit_sterilisasi_alat(){return akses01.audit_sterilisasi_alat;}
    public static boolean getpenilaian_awal_medis_ralan_psikiatri(){return akses01.penilaian_awal_medis_ralan_psikiatri;}
    public static boolean getpersetujuan_penolakan_tindakan(){return akses01.persetujuan_penolakan_tindakan;}
    public static boolean getaudit_bundle_isk(){return akses01.audit_bundle_isk;}
    public static boolean getaudit_bundle_plabsi(){return akses01.audit_bundle_plabsi;}
    public static boolean getaudit_bundle_vap(){return akses01.audit_bundle_vap;}
    public static boolean getakun_host_to_host_bank_papua(){return akses01.akun_host_to_host_bank_papua;}
    public static boolean getpembayaran_bank_papua(){return akses01.pembayaran_bank_papua;}
    public static boolean getpenilaian_awal_medis_ralan_penyakit_dalam(){return akses01.penilaian_awal_medis_ralan_penyakit_dalam;}
    public static boolean getpenilaian_awal_medis_ralan_mata(){return akses01.penilaian_awal_medis_ralan_mata;}
    public static boolean getpenilaian_awal_medis_ralan_neurologi(){return akses01.penilaian_awal_medis_ralan_neurologi;}
    public static boolean getsirkulasi_obat6(){return akses01.sirkulasi_obat6;}
    public static boolean getpenilaian_awal_medis_ralan_orthopedi(){return akses01.penilaian_awal_medis_ralan_orthopedi;}
    public static boolean getpenilaian_awal_medis_ralan_bedah(){return akses01.penilaian_awal_medis_ralan_bedah;}
    public static boolean getintegrasi_khanza_health_services(){return akses01.integrasi_khanza_health_services;}
    public static boolean getsoap_ralan_tni(){return akses01.soap_ralan_tni;}
    public static boolean getsoap_ranap_tni(){return akses01.soap_ranap_tni;}
    public static boolean getjumlah_pengunjung_ralan_tni(){return akses01.jumlah_pengunjung_ralan_tni;}
    public static boolean getlaporan_penyakit_tni(){return akses01.laporan_penyakit_tni;}
    public static boolean getcatatan_keperawatan_ranap(){return akses01.catatan_keperawatan_ranap;}
    public static boolean getmaster_rencana_keperawatan_gigi(){return akses01.master_rencana_keperawatan_gigi;}
    public static boolean getmaster_rencana_keperawatan_mata(){return akses01.master_rencana_keperawatan_mata;}
    public static boolean getmaster_rencana_keperawatan_igd(){return akses01.master_rencana_keperawatan_igd;}
    public static boolean getmaster_masalah_keperawatan_psikiatri(){return akses01.master_masalah_keperawatan_psikiatri;}
    public static boolean getmaster_rencana_keperawatan_psikiatri(){return akses01.master_rencana_keperawatan_psikiatri;}
    public static boolean getpenilaian_awal_keperawatan_psikiatri(){return akses01.penilaian_awal_keperawatan_psikiatri;}
    public static boolean getpemantauan_pews_anak(){return akses01.pemantauan_pews_anak;}
    public static boolean getsurat_pulang_atas_permintaan_sendiri(){return akses01.surat_pulang_atas_permintaan_sendiri;}
    public static boolean gettemplate_hasil_radiologi(){return akses01.template_hasil_radiologi;}
    public static boolean getlaporan_bulanan_irj(){return akses01.laporan_bulanan_irj;}
    public static boolean gettemplate_pemeriksaan(){return akses01.template_pemeriksaan;}
    public static boolean getpemeriksaan_lab_mb(){return akses01.pemeriksaan_lab_mb;}
    public static boolean getubah_petugas_lab_mb(){return akses01.ubah_petugas_lab_mb;}
    public static boolean getpenilaian_pre_operasi(){return akses01.penilaian_pre_operasi;}
    public static boolean getpenilaian_pre_anestesi(){return akses01.penilaian_pre_anestesi;}
    public static boolean getperencanaan_pemulangan(){return akses01.perencanaan_pemulangan;}
    public static boolean getpenilaian_lanjutan_resiko_jatuh_dewasa(){return akses01.penilaian_lanjutan_resiko_jatuh_dewasa;}
    public static boolean getpenilaian_lanjutan_resiko_jatuh_anak(){return akses01.penilaian_lanjutan_resiko_jatuh_anak;}
    public static boolean getpenilaian_awal_medis_ralan_geriatri(){return akses01.penilaian_awal_medis_ralan_geriatri;}
    public static boolean getpenilaian_tambahan_pasien_geriatri(){return akses01.penilaian_tambahan_pasien_geriatri;}
    public static boolean getskrining_nutrisi_dewasa(){return akses01.skrining_nutrisi_dewasa;}
    public static boolean getskrining_nutrisi_lansia(){return akses01.skrining_nutrisi_lansia;}
    public static boolean gethasil_pemeriksaan_usg(){return akses01.hasil_pemeriksaan_usg;}
    public static boolean getskrining_nutrisi_anak(){return akses01.skrining_nutrisi_anak;}
    public static boolean getakun_host_to_host_bank_jabar(){return akses01.akun_host_to_host_bank_jabar;}
    public static boolean getpembayaran_bank_jabar(){return akses01.pembayaran_bank_jabar;}
    public static boolean getsurat_pernyataan_pasien_umum(){return akses01.surat_pernyataan_pasien_umum;}
    public static boolean getkonseling_farmasi(){return akses01.konseling_farmasi;}
    public static boolean getpelayanan_informasi_obat(){return akses01.pelayanan_informasi_obat;}
    public static boolean getjawaban_pio_apoteker(){return akses01.jawaban_pio_apoteker;}
    public static boolean getsurat_persetujuan_umum(){return akses01.surat_persetujuan_umum;}
    public static boolean gettransfer_pasien_antar_ruang(){return akses01.transfer_pasien_antar_ruang;}
    public static boolean getsatu_sehat_referensi_dokter(){return akses01.satu_sehat_referensi_dokter;}
    public static boolean getsatu_sehat_referensi_pasien(){return akses01.satu_sehat_referensi_pasien;}
    public static boolean getsatu_sehat_mapping_departemen(){return akses01.satu_sehat_mapping_departemen;}
    public static boolean getsatu_sehat_mapping_lokasi(){return akses01.satu_sehat_mapping_lokasi;}
    public static boolean getsatu_sehat_kirim_encounter(){return akses01.satu_sehat_kirim_encounter;}
    public static boolean getcatatan_cek_gds(){return akses01.catatan_cek_gds;}
    public static boolean getsatu_sehat_kirim_condition(){return akses01.satu_sehat_kirim_condition;}
    public static boolean getchecklist_pre_operasi(){return akses01.checklist_pre_operasi;}
    public static boolean getsatu_sehat_kirim_observationttv(){return akses01.satu_sehat_kirim_observationttv;}
    public static boolean getsignin_sebelum_anestesi(){return akses01.signin_sebelum_anestesi;}
    public static boolean getsatu_sehat_kirim_procedure(){return akses01.satu_sehat_kirim_procedure;}
    public static boolean getoperasi_per_bulan(){return akses01.operasi_per_bulan;}
    public static boolean gettimeout_sebelum_insisi(){return akses01.timeout_sebelum_insisi;}
    public static boolean getsignout_sebelum_menutup_luka(){return akses01.signout_sebelum_menutup_luka;}
    public static boolean getdapur_barang(){return akses01.dapur_barang;}
    public static boolean getdapur_opname(){return akses01.dapur_opname;}
    public static boolean getsatu_sehat_mapping_vaksin(){return akses01.satu_sehat_mapping_vaksin;}
    public static boolean getdapur_suplier(){return akses01.dapur_suplier;}
    public static boolean getsatu_sehat_kirim_Immunization(){return akses01.satu_sehat_kirim_Immunization;}
    public static boolean getchecklist_post_operasi(){return akses01.checklist_post_operasi;}
    public static boolean getdapur_pembelian(){return akses01.dapur_pembelian;}
    public static boolean getdapur_stok_keluar(){return akses01.dapur_stok_keluar;}
    public static boolean getdapur_riwayat_barang(){return akses01.dapur_riwayat_barang;}
    public static boolean getpermintaan_dapur(){return akses01.permintaan_dapur;}
    public static boolean getrekonsiliasi_obat(){return akses01.rekonsiliasi_obat;}
    public static boolean getbiaya_pengadaan_dapur(){return akses01.biaya_pengadaan_dapur;}
    public static boolean getrekap_pengadaan_dapur(){return akses01.rekap_pengadaan_dapur;}
    public static boolean getkesling_limbah_b3medis_cair(){return akses01.kesling_limbah_b3medis_cair;}
    public static boolean getgrafik_limbahb3cair_pertanggal(){return akses01.grafik_limbahb3cair_pertanggal;}
    public static boolean getgrafik_limbahb3cair_perbulan(){return akses01.grafik_limbahb3cair_perbulan;}
    public static boolean getrekap_biaya_registrasi(){return akses01.rekap_biaya_registrasi;}
    public static boolean getkonfirmasi_rekonsiliasi_obat(){return akses01.konfirmasi_rekonsiliasi_obat;}
    public static boolean getsatu_sehat_kirim_clinicalimpression(){return akses01.satu_sehat_kirim_clinicalimpression;}
    public static boolean getpenilaian_pasien_terminal(){return akses01.penilaian_pasien_terminal;}
    public static boolean getsurat_persetujuan_rawat_inap(){return akses01.surat_persetujuan_rawat_inap;}
    public static boolean getmonitoring_reaksi_tranfusi(){return akses01.monitoring_reaksi_tranfusi;}
    public static boolean getpenilaian_korban_kekerasan(){return akses01.penilaian_korban_kekerasan;}
    public static boolean getpenilaian_lanjutan_resiko_jatuh_lansia(){return akses01.penilaian_lanjutan_resiko_jatuh_lansia;}
    public static boolean getmpp_skrining(){return akses01.mpp_skrining;}
    public static boolean getpenilaian_pasien_penyakit_menular(){return akses01.penilaian_pasien_penyakit_menular;}
    public static boolean getedukasi_pasien_keluarga_rj(){return akses01.edukasi_pasien_keluarga_rj;}
    public static boolean getpemantauan_pews_dewasa(){return akses01.pemantauan_pews_dewasa;}
    public static boolean getpenilaian_tambahan_bunuh_diri(){return akses01.penilaian_tambahan_bunuh_diri;}
    public static boolean getbpjs_antrean_pertanggal(){return akses01.bpjs_antrean_pertanggal;}
    public static boolean getpenilaian_tambahan_perilaku_kekerasan(){return akses01.penilaian_tambahan_perilaku_kekerasan;}
    public static boolean getpenilaian_tambahan_beresiko_melarikan_diri(){return akses01.penilaian_tambahan_beresiko_melarikan_diri;}
    public static boolean getpersetujuan_penundaan_pelayanan(){return akses01.persetujuan_penundaan_pelayanan;}
    public static boolean getsisa_diet_pasien(){return akses01.sisa_diet_pasien;}
    public static boolean getpenilaian_awal_medis_ralan_bedah_mulut(){return akses01.penilaian_awal_medis_ralan_bedah_mulut;}
    public static boolean getpenilaian_pasien_keracunan(){return akses01.penilaian_pasien_keracunan;}
    public static boolean getpemantauan_meows_obstetri(){return akses01.pemantauan_meows_obstetri;}
    public static boolean getcatatan_adime_gizi(){return akses01.catatan_adime_gizi;}
    public static boolean getpengajuan_biaya(){return akses01.pengajuan_biaya;}
    public static boolean getpenilaian_awal_keperawatan_ralan_geriatri(){return akses01.penilaian_awal_keperawatan_ralan_geriatri;}
    public static boolean getmaster_masalah_keperawatan_geriatri(){return akses01.master_masalah_keperawatan_geriatri;}
    public static boolean getmaster_rencana_keperawatan_geriatri(){return akses01.master_rencana_keperawatan_geriatri;}
    public static boolean getchecklist_kriteria_masuk_hcu(){return akses01.checklist_kriteria_masuk_hcu;}
    public static boolean getchecklist_kriteria_keluar_hcu(){return akses01.checklist_kriteria_keluar_hcu;}
    public static boolean getpenilaian_risiko_dekubitus(){return akses01.penilaian_risiko_dekubitus;}
    public static boolean getmaster_menolak_anjuran_medis(){return akses01.master_menolak_anjuran_medis;}
    public static boolean getpenolakan_anjuran_medis(){return akses01.penolakan_anjuran_medis;}
    public static boolean getlaporan_tahunan_penolakan_anjuran_medis(){return akses01.laporan_tahunan_penolakan_anjuran_medis;}
    public static boolean gettemplate_laporan_operasi(){return akses01.template_laporan_operasi;}
    public static boolean gethasil_tindakan_eswl(){return akses01.hasil_tindakan_eswl;}
    public static boolean getchecklist_kriteria_masuk_icu(){return akses01.checklist_kriteria_masuk_icu;}
    public static boolean getchecklist_kriteria_keluar_icu(){return akses01.checklist_kriteria_keluar_icu;}
    public static boolean getakses_dokter_lain_rawat_jalan(){return akses01.akses_dokter_lain_rawat_jalan;}
    public static boolean getfollow_up_dbd(){return akses01.follow_up_dbd;}
    public static boolean getpenilaian_risiko_jatuh_neonatus(){return akses01.penilaian_risiko_jatuh_neonatus;}
    public static boolean getpersetujuan_pengajuan_biaya(){return akses01.persetujuan_pengajuan_biaya;}
    public static boolean getpemeriksaan_fisik_ralan_per_penyakit(){return akses01.pemeriksaan_fisik_ralan_per_penyakit;}
    public static boolean getpenilaian_lanjutan_resiko_jatuh_geriatri(){return akses01.penilaian_lanjutan_resiko_jatuh_geriatri;}
    public static boolean getpemantauan_ews_neonatus(){return akses01.pemantauan_ews_neonatus;}
    public static boolean getvalidasi_persetujuan_pengajuan_biaya(){return akses01.validasi_persetujuan_pengajuan_biaya;}
    public static boolean getriwayat_perawatan_icare_bpjs(){return akses01.riwayat_perawatan_icare_bpjs;}
    public static boolean getrekap_pengajuan_biaya(){return akses01.rekap_pengajuan_biaya;}
    public static boolean getpenilaian_awal_medis_ralan_kulit_kelamin(){return akses01.penilaian_awal_medis_ralan_kulit_kelamin;}
    public static boolean getakun_host_to_host_bank_mandiri(){return akses01.akun_host_to_host_bank_mandiri;}
    public static boolean getpenilaian_medis_ralan_hemodialisa(){return akses01.penilaian_medis_hemodialisa;}
    public static boolean getpenilaian_level_kecemasan_ranap_anak(){return akses01.penilaian_level_kecemasan_ranap_anak;}
    public static boolean getpenilaian_lanjutan_resiko_jatuh_psikiatri(){return akses01.penilaian_lanjutan_resiko_jatuh_psikiatri;}
    public static boolean getpenilaian_lanjutan_skrining_fungsional(){return akses01.penilaian_lanjutan_skrining_fungsional;}
    public static boolean getpenilaian_medis_ralan_rehab_medik(){return akses01.penilaian_medis_ralan_rehab_medik;}
    public static boolean getlaporan_anastesi(){return akses01.laporan_anestesi;}
    public static boolean gettemplate_persetujuan_penolakan_tindakan(){return akses01.template_persetujuan_penolakan_tindakan;}
    public static boolean getpenilaian_medis_ralan_gawat_darurat_psikiatri(){return akses01.penilaian_medis_ralan_gawat_darurat_psikiatri;}
    public static boolean getbpjs_referensi_setting_apotek(){return akses01.bpjs_referensi_setting_apotek;}
    public static boolean getbpjs_referensi_obat_apotek(){return akses01.bpjs_referensi_obat_apotek;}
    public static boolean getbpjs_mapping_obat_apotek(){return akses01.bpjs_mapping_obat_apotek;}
    public static boolean getpembayaran_bank_mandiri(){return akses01.pembayaran_bank_mandiri;}
    public static boolean getpenilaian_ulang_nyeri(){return akses01.penilaian_ulang_nyeri;}
    public static boolean getpenilaian_terapi_wicara(){return akses01.penilaian_terapi_wicara;}
    public static boolean getbpjs_obat_23hari_apotek(){return akses01.bpjs_obat_23hari_apotek;}
    public static boolean getpengkajian_restrain(){return akses01.pengkajian_restrain;}
    public static boolean getbpjs_kunjungan_sep_apotek(){return akses01.bpjs_kunjungan_sep_apotek;}
    public static boolean getbpjs_monitoring_klaim_apotek(){return akses01.bpjs_monitoring_klaim_apotek;}
    public static boolean getbpjs_daftar_pelayanan_obat_apotek(){return akses01.bpjs_daftar_pelayanan_obat_apotek;}
    public static boolean getpenilaian_awal_medis_ralan_paru(){return akses01.penilaian_awal_medis_ralan_paru;}
    public static boolean getcatatan_keperawatan_ralan(){return akses01.catatan_keperawatan_ralan;}
    public static boolean getcatatan_persalinan(){return akses01.catatan_persalinan;}
    public static boolean getskor_aldrette_pasca_anestesi(){return akses01.skor_aldrette_pasca_anestesi;}
    public static boolean getskor_steward_pasca_anestesi(){return akses01.skor_steward_pasca_anestesi;}
    public static boolean getskor_bromage_pasca_anestesi(){return akses01.skor_bromage_pasca_anestesi;}
    public static boolean getpenilaian_pre_induksi(){return akses01.penilaian_pre_induksi;}
    public static boolean gethasil_usg_urologi(){return akses01.hasil_usg_urologi;}
    public static boolean gethasil_usg_gynecologi(){return akses01.hasil_usg_gynecologi;}
    public static boolean gethasil_pemeriksaan_ekg(){return akses01.hasil_pemeriksaan_ekg;}
    public static boolean gethapus_edit_sep_bpjs(){return akses01.hapus_edit_sep_bpjs;}
    public static boolean getsatu_sehat_kirim_diet(){return akses01.satu_sehat_kirim_diet;}
    public static boolean getsatu_sehat_mapping_obat(){return akses01.satu_sehat_mapping_obat;}
    public static boolean getdapur_ringkasan_pembelian(){return akses01.dapur_ringkasan_pembelian;}
    public static boolean getsatu_sehat_kirim_medication(){return akses01.satu_sehat_kirim_medication;}
    public static boolean getsatu_sehat_kirim_medicationrequest(){return akses01.satu_sehat_kirim_medicationrequest;}
    public static boolean getpenatalaksanaan_terapi_okupasi(){return akses01.penatalaksanaan_terapi_okupasi;}
    public static boolean getsatu_sehat_kirim_medicationdispense(){return akses01.satu_sehat_kirim_medicationdispense;}
    public static boolean gethasil_usg_neonatus(){return akses01.hasil_usg_neonatus;}
    public static boolean gethasil_endoskopi_faring_laring(){return akses01.hasil_endoskopi_faring_laring;}
    public static boolean getsatu_sehat_mapping_radiologi(){return akses01.satu_sehat_mapping_radiologi;}
    public static boolean getsatu_sehat_kirim_servicerequest_radiologi(){return akses01.satu_sehat_kirim_servicerequest_radiologi;}
    public static boolean gethasil_endoskopi_hidung(){return akses01.hasil_endoskopi_hidung;}
    public static boolean getsatu_sehat_kirim_specimen_radiologi(){return akses01.satu_sehat_kirim_specimen_radiologi;}
    public static boolean getmaster_masalah_keperawatan_neonatus(){return akses01.master_masalah_keperawatan_neonatus;}
    public static boolean getmaster_rencana_keperawatan_neonatus(){return akses01.master_rencana_keperawatan_neonatus;}
    public static boolean getpenilaian_awal_keperawatan_ranap_neonatus(){return akses01.penilaian_awal_keperawatan_ranap_neonatus;}
    public static boolean getsatu_sehat_kirim_observation_radiologi(){return akses01.satu_sehat_kirim_observation_radiologi;}
    public static boolean getsatu_sehat_kirim_diagnosticreport_radiologi(){return akses01.satu_sehat_kirim_diagnosticreport_radiologi;}
    public static boolean gethasil_endoskopi_telinga(){return akses01.hasil_endoskopi_telinga;}
    public static boolean getsatu_sehat_mapping_lab(){return akses01.satu_sehat_mapping_lab;}
    public static boolean getsatu_sehat_kirim_servicerequest_lab(){return akses01.satu_sehat_kirim_servicerequest_lab;}
    public static boolean getsatu_sehat_kirim_servicerequest_labmb(){return akses01.satu_sehat_kirim_servicerequest_labmb;}
    public static boolean getsatu_sehat_kirim_specimen_lab(){return akses01.satu_sehat_kirim_specimen_lab;}
    public static boolean getsatu_sehat_kirim_specimen_labmb(){return akses01.satu_sehat_kirim_specimen_labmb;}
    public static boolean getsatu_sehat_kirim_observation_lab(){return akses01.satu_sehat_kirim_observation_lab;}
    public static boolean getsatu_sehat_kirim_observation_labmb(){return akses01.satu_sehat_kirim_observation_labmb;}
    public static boolean getsatu_sehat_kirim_diagnosticreport_lab(){return akses01.satu_sehat_kirim_diagnosticreport_lab;}
    public static boolean getsatu_sehat_kirim_diagnosticreport_labmb(){return akses01.satu_sehat_kirim_diagnosticreport_labmb;}
    public static boolean getkepatuhan_kelengkapan_keselamatan_bedah(){return akses01.kepatuhan_kelengkapan_keselamatan_bedah;}
    public static boolean getnilai_piutang_perjenis_bayar_per_bulan(){return akses01.nilai_piutang_perjenis_bayar_per_bulan;}
    public static boolean getringkasan_piutang_jenis_bayar(){return akses01.ringkasan_piutang_jenis_bayar;}
    public static boolean getpenilaian_pasien_imunitas_rendah(){return akses01.penilaian_pasien_imunitas_rendah;}
    public static boolean getbalance_cairan(){return akses01.balance_cairan;}
    public static boolean getcatatan_observasi_chbp(){return akses01.catatan_observasi_chbp;}
    public static boolean getcatatan_observasi_induksi_persalinan(){return akses01.catatan_observasi_induksi_persalinan;}
    public static boolean getskp_kategori_penilaian(){return akses01.skp_kategori_penilaian;}
    public static boolean getskp_kriteria_penilaian(){return akses01.skp_kriteria_penilaian;}
    public static boolean getskp_penilaian(){return akses01.skp_penilaian;}
    public static boolean getreferensi_poli_mobilejknfktp(){return akses01.referensi_poli_mobilejknfktp;}
    public static boolean getreferensi_dokter_mobilejknfktp(){return akses01.referensi_dokter_mobilejknfktp;}
    public static boolean getskp_rekapitulasi_penilaian(){return akses01.skp_rekapitulasi_penilaian;}
    public static boolean getpembayaran_pihak_ke3_bankmandiri(){return akses01.pembayaran_pihak_ke3_bankmandiri;}
    public static boolean getmetode_pembayaran_bankmandiri(){return akses01.metode_pembayaran_bankmandiri;}
    public static boolean getbank_tujuan_transfer_bankmandiri(){return akses01.bank_tujuan_transfer_bankmandiri;}
    public static boolean getkodetransaksi_tujuan_transfer_bankmandiri(){return akses01.kodetransaksi_tujuan_transfer_bankmandiri;}
    public static boolean getkonsultasi_medik(){return akses01.konsultasi_medik;}
    public static boolean getjawaban_konsultasi_medik(){return akses01.jawaban_konsultasi_medik;}
    public static boolean getpcare_cek_alergi(){return akses01.pcare_cek_alergi;}
    public static boolean getpcare_cek_prognosa(){return akses01.pcare_cek_prognosa;}
    public static boolean getdata_sasaran_usiaproduktif(){return akses01.data_sasaran_usiaproduktif;}
    public static boolean getdata_sasaran_usialansia(){return akses01.data_sasaran_usialansia;}
    public static boolean getskrining_perilaku_merokok_sekolah_remaja(){return akses01.skrining_perilaku_merokok_sekolah_remaja;}
    public static boolean getskrining_kekerasan_pada_perempuan(){return akses01.skrining_kekerasan_pada_perempuan;}
    public static boolean getskrining_obesitas(){return akses01.skrining_obesitas;}
    public static boolean getskrining_risiko_kanker_payudara(){return akses01.skrining_risiko_kanker_payudara;}
    public static boolean getskrining_risiko_kanker_paru(){return akses01.skrining_risiko_kanker_paru;}
    public static boolean getskrining_tbc(){return akses01.skrining_tbc;}
    public static boolean getskrining_kesehatan_gigi_mulut_remaja(){return akses01.skrining_kesehatan_gigi_mulut_remaja;}
    public static boolean getpenilaian_awal_keperawatan_ranap_bayi(){return akses01.penilaian_awal_keperawatan_ranap_bayi;}
    public static boolean getbooking_mcu_perusahaan(){return akses01.booking_mcu_perusahaan;}
    public static boolean getcatatan_observasi_restrain_nonfarma(){return akses01.catatan_observasi_restrain_nonfarma;}
    public static boolean getcatatan_observasi_ventilator(){return akses01.catatan_observasi_ventilator;}
    public static boolean getcatatan_anestesi_sedasi(){return akses01.catatan_anestesi_sedasi;}
    public static boolean getskrining_puma(){return akses01.skrining_puma;}
    public static boolean getsatu_sehat_kirim_careplan(){return akses01.satu_sehat_kirim_careplan;}
    public static boolean getsatu_sehat_kirim_medicationstatement(){return akses01.satu_sehat_kirim_medicationstatement;}
    public static boolean getskrining_adiksi_nikotin(){return akses01.skrining_adiksi_nikotin;}
    public static boolean getskrining_thalassemia(){return akses01.skrining_thalassemia;}
    public static boolean getskrining_instrumen_sdq(){return akses01.skrining_instrumen_sdq;}
    public static boolean getskrining_instrumen_srq(){return akses01.skrining_instrumen_srq;}
    public static boolean getchecklist_pemberian_fibrinolitik(){return akses01.checklist_pemberian_fibrinolitik;}
    public static boolean getskrining_kanker_kolorektal(){return akses01.skrining_kanker_kolorektal;}
    public static boolean getdapur_pemesanan(){return akses01.dapur_pemesanan;}
    public static boolean getbayar_pesan_dapur(){return akses01.bayar_pesan_dapur;}
    public static boolean gethutang_dapur(){return akses01.hutang_dapur;}
    public static boolean gettitip_faktur_dapur(){return akses01.titip_faktur_dapur;}
    public static boolean getvalidasi_tagihan_dapur(){return akses01.validasi_tagihan_dapur;}
    public static boolean getsurat_pemesanan_dapur(){return akses01.surat_pemesanan_dapur;}
    public static boolean getpengajuan_barang_dapur(){return akses01.pengajuan_barang_dapur;}
    public static boolean getdapur_returbeli(){return akses01.dapur_returbeli;}
    public static boolean gethibah_dapur(){return akses01.hibah_dapur;}
    public static boolean getringkasan_penerimaan_dapur(){return akses01.ringkasan_penerimaan_dapur;}
    public static boolean getringkasan_pengajuan_dapur(){return akses01.ringkasan_pengajuan_dapur;}
    public static boolean getringkasan_pemesanan_dapur(){return akses01.ringkasan_pemesanan_dapur;}
    public static boolean getringkasan_returbeli_dapur(){return akses01.ringkasan_returbeli_dapur;}
    public static boolean getringkasan_stokkeluar_dapur(){return akses01.ringkasan_stokkeluar_dapur;}
    public static boolean getdapur_stokkeluar_pertanggal(){return akses01.dapur_stokkeluar_pertanggal;}
    public static boolean getsirkulasi_dapur(){return akses01.sirkulasi_dapur;}
    public static boolean getsirkulasi_dapur2(){return akses01.sirkulasi_dapur2;}
    public static boolean getverifikasi_penerimaan_dapur(){return akses01.verifikasi_penerimaan_dapur;}
    public static boolean getnilai_penerimaan_vendor_dapur_perbulan(){return akses01.nilai_penerimaan_vendor_dapur_perbulan;}
    public static boolean getringkasan_hutang_vendor_dapur(){return akses01.ringkasan_hutang_vendor_dapur;}
    public static boolean getpenilaian_psikologi_klinis(){return akses01.penilaian_psikologi_klinis;}
    public static boolean getpenilaian_awal_medis_ranap_neonatus(){return akses01.penilaian_awal_medis_ranap_neonatus;}
    public static boolean getpenilaian_derajat_dehidrasi(){return akses01.penilaian_derajat_dehidrasi;}
    public static boolean getringkasan_jasa_tindakan_medis(){return akses01.ringkasan_jasa_tindakan_medis;}
    public static boolean getpendapatan_per_akun(){return akses01.pendapatan_per_akun;}
    public static boolean gethasil_pemeriksaan_echo(){return akses01.hasil_pemeriksaan_echo;}
    public static boolean getpenilaian_bayi_baru_lahir(){return akses01.penilaian_bayi_baru_lahir;}
    public static boolean getrl1_3_ketersediaan_kamar(){return akses01.rl1_3_ketersediaan_kamar;}
    public static boolean getpendapatan_per_akun_closing(){return akses01.pendapatan_per_akun_closing;}
    public static boolean getpengeluaran_pengeluaran(){return akses01.pengeluaran_pengeluaran;}
    public static boolean getskrining_diabetes_melitus(){return akses01.skrining_diabetes_melitus;}
    public static boolean getlaporan_tindakan(){return akses01.laporan_tindakan;}
    public static boolean getpelaksanaan_informasi_edukasi(){return akses01.pelaksanaan_informasi_edukasi;}
    public static boolean getlayanan_kedokteran_fisik_rehabilitasi(){return akses01.layanan_kedokteran_fisik_rehabilitasi;}
    public static boolean getskrining_kesehatan_gigi_mulut_balita(){return akses01.skrining_kesehatan_gigi_mulut_balita;}
    public static boolean getskrining_anemia(){return akses01.skrining_anemia;}
    public static boolean getlayanan_program_kfr(){return akses01.layanan_program_kfr;}
    public static boolean getskrining_hipertensi(){return akses01.skrining_hipertensi;}
    public static boolean getskrining_kesehatan_penglihatan(){return akses01.skrining_kesehatan_penglihatan;}
    public static boolean getcatatan_observasi_hemodialisa(){return akses01.catatan_observasi_hemodialisa;}
    public static boolean getskrining_kesehatan_gigi_mulut_dewasa(){return akses01.skrining_kesehatan_gigi_mulut_dewasa;}
    public static boolean getskrining_risiko_kanker_serviks(){return akses01.skrining_risiko_kanker_serviks;}
    public static boolean getcatatan_cairan_hemodialisa(){return akses01.catatan_cairan_hemodialisa;}
    public static boolean getskrining_kesehatan_gigi_mulut_lansia(){return akses01.skrining_kesehatan_gigi_mulut_lansia;}
    public static boolean getskrining_indra_pendengaran(){return akses01.skrining_indra_pendengaran;}
    public static boolean getcatatan_pengkajian_paska_operasi(){return akses01.catatan_pengkajian_paska_operasi;}
    public static boolean getskrining_frailty_syndrome(){return akses01.skrining_frailty_syndrome;}
    public static boolean getsirkulasi_cssd(){return akses01.sirkulasi_cssd;}
    public static boolean getlama_pelayanan_cssd(){return akses01.lama_pelayanan_cssd;}
    public static boolean getcatatan_observasi_bayi(){return akses01.catatan_observasi_bayi;}
    public static boolean getriwayat_surat_peringatan(){return akses01.riwayat_surat_peringatan;}
    public static boolean getmaster_kesimpulan_anjuran_mcu(){return akses01.master_kesimpulan_anjuran_mcu;}
    public static boolean getkategori_piutang_jasa_perusahaan(){return akses01.kategori_piutang_jasa_perusahaan;}
    public static boolean getpiutang_jasa_perusahaan(){return akses01.piutang_jasa_perusahaan;}
    public static boolean getbayar_piutang_jasa_perusahaan(){return akses01.bayar_piutang_jasa_perusahaan;}
    public static boolean getpiutang_jasa_perusahaan_belum_lunas(){return akses01.piutang_jasa_perusahaan_belum_lunas;}
    public static boolean getchecklist_kesiapan_anestesi(){return akses01.checklist_kesiapan_anestesi;}
    public static boolean getpiutang_peminjaman_uang_belum_lunas(){return akses01.piutang_peminjaman_uang_belum_lunas;}
    public static boolean gethasil_pemeriksaan_slit_lamp(){return akses01.hasil_pemeriksaan_slit_lamp;}
    public static boolean gethasil_pemeriksaan_oct(){return akses01.hasil_pemeriksaan_oct;}
    public static boolean getbeban_hutang_lain(){return akses01.beban_hutang_lain;}
    public static boolean getpoli_asal_pasien_ranap(){return akses01.poli_asal_pasien_ranap;}
    public static boolean getpemberi_hutang_lain(){return akses01.pemberi_hutang_lain;}
    public static boolean getdokter_asal_pasien_ranap(){return akses01.dokter_asal_pasien_ranap;}
    public static boolean getduta_parkir_rekap_keluar(){return akses01.duta_parkir_rekap_keluar;}
    public static boolean getsurat_keterangan_layak_terbang(){return akses01.surat_keterangan_layak_terbang;}
    public static boolean getbayar_beban_hutang_lain(){return akses01.bayar_beban_hutang_lain;}
    public static boolean getsurat_persetujuan_pemeriksaan_hiv(){return akses01.surat_persetujuan_pemeriksaan_hiv;}
    public static boolean getskrining_instrumen_acrs(){return akses01.skrining_instrumen_acrs;}
    public static boolean getsurat_pernyataan_memilih_dpjp(){return akses01.surat_pernyataan_memilih_dpjp;}
}   
