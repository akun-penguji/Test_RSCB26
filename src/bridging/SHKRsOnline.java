package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

/**
 * Pelaporan Skrining Hipotiroid Kongenital (SHK) ke RS Online v3 Kemenkes.
 * Dua tahap: POST /Pasien/shk (data dasar) lalu POST /Pasien/hasilShk (hasil lab).
 * Data bayi/ibu auto dari reg_periksa + pasien (nm_ibu) + wilayah; sisanya manual.
 * Dipanggil dari DlgKamarInap & DlgRawatInap. Pure-code (tanpa file .form).
 */
public final class SHKRsOnline extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private ApiKemenkesCorona api = new ApiKemenkesCorona();
    private ObjectMapper mapper = new ObjectMapper();

    // Info pasien (auto)
    private widget.InternalFrame internalFrame1;
    private widget.panelisi panelAtas, panelBawah;
    private widget.TextBox TNoRw, TNoRM, TPasien, TNikAnak, TglLahir, JK, TUmur, TAlamat, TWilayah, TNmIbu;
    // Data dasar (manual)
    private widget.TextBox TNikIbu, TKodePerujuk, TNmPerujuk, TJnsFaskes, TIdShk;
    private widget.Tanggal TglAmbil, TglKirim, TglLapor;
    // Hasil lab (manual)
    private widget.TextBox TJnsPeriksa, THasil, TLayak, TIdLayak, TIdHasil;
    private widget.Tanggal TglPeriksa, TglHasil, TglTerima;
    // Tabel + tombol
    private widget.ScrollPane Scroll;
    private widget.Table tbData;
    private widget.Button BtnKirimDasar, BtnKirimHasil, BtnSimpan, BtnBatal, BtnHapus, BtnKeluar, BtnCari, BtnAll;
    private widget.Label LCount;
    private widget.TextBox TCari;

    public SHKRsOnline(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(950, 640);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat","No.RM","Nama Bayi","id_shk","id_hasil","Status",
            "NIK Ibu","Nama Ibu","Tgl Ambil","Tgl Kirim","Tgl Lapor",
            "Jns Periksa","Hasil","Tgl Periksa","Tgl Hasil","Layak","id_layak","Tgl Terima"
        }){
            @Override public boolean isCellEditable(int r, int c){return false;}
        };
        tbData.setModel(tabMode);
        tbData.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w={105,70,160,70,70,95,120,150,80,80,80,70,55,80,80,50,55,80};
        for(i=0;i<tabMode.getColumnCount();i++){
            TableColumn c=tbData.getColumnModel().getColumn(i);
            c.setPreferredWidth(w[i]);
        }
        tbData.setDefaultRenderer(Object.class, new WarnaTable());

        TCari.setDocument(new batasInput((int)100).getKata(TCari));
        TNikIbu.setDocument(new batasInput((int)20).getOnlyAngka(TNikIbu));

        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override public void insertUpdate(DocumentEvent e){if(TCari.getText().length()>2){tampil();}}
                @Override public void removeUpdate(DocumentEvent e){if(TCari.getText().length()>2){tampil();}}
                @Override public void changedUpdate(DocumentEvent e){if(TCari.getText().length()>2){tampil();}}
            });
        }

        // Default kode referensi (juknis cuma kasih contoh; user boleh ubah)
        TKodePerujuk.setText(koneksiDB.IDCORONA());
        TNmPerujuk.setText(akses.getnamars());
        TJnsFaskes.setText("2");
        TJnsPeriksa.setText("1");
        THasil.setText("1");
        TLayak.setText("2");
        TIdLayak.setText("2");
    }

    private void initComponents() {
        internalFrame1 = new widget.InternalFrame();
        panelAtas = new widget.panelisi();
        panelBawah = new widget.panelisi();
        TNoRw=new widget.TextBox(); TNoRM=new widget.TextBox(); TPasien=new widget.TextBox();
        TNikAnak=new widget.TextBox(); TglLahir=new widget.TextBox(); JK=new widget.TextBox();
        TUmur=new widget.TextBox(); TAlamat=new widget.TextBox(); TWilayah=new widget.TextBox(); TNmIbu=new widget.TextBox();
        TNikIbu=new widget.TextBox(); TKodePerujuk=new widget.TextBox(); TNmPerujuk=new widget.TextBox();
        TJnsFaskes=new widget.TextBox(); TIdShk=new widget.TextBox();
        TglAmbil=new widget.Tanggal(); TglKirim=new widget.Tanggal(); TglLapor=new widget.Tanggal();
        TJnsPeriksa=new widget.TextBox(); THasil=new widget.TextBox(); TLayak=new widget.TextBox();
        TIdLayak=new widget.TextBox(); TIdHasil=new widget.TextBox();
        TglPeriksa=new widget.Tanggal(); TglHasil=new widget.Tanggal(); TglTerima=new widget.Tanggal();
        Scroll=new widget.ScrollPane(); tbData=new widget.Table();
        BtnKirimDasar=new widget.Button(); BtnKirimHasil=new widget.Button(); BtnSimpan=new widget.Button(); BtnBatal=new widget.Button();
        BtnHapus=new widget.Button(); BtnKeluar=new widget.Button(); BtnCari=new widget.Button(); BtnAll=new widget.Button();
        LCount=new widget.Label(); TCari=new widget.TextBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0,153,0)),
            "::[ Skrining Hipotiroid Kongenital (SHK) - RS Online ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma",0,11), new Color(0,0,0)));
        internalFrame1.setLayout(new BorderLayout(2,8));

        // ===== PANEL ATAS =====
        panelAtas.setLayout(new java.awt.GridLayout(0,1,0,4));
        panelAtas.setPreferredSize(new Dimension(950, 200));
        panelAtas.setBorder(BorderFactory.createEmptyBorder(6,6,8,6));

        // Baris 1: identitas bayi (auto)
        widget.panelisi r1=newRow();
        addLbl(r1,"No.Rawat :",60); TNoRw.setEditable(false); TNoRw.setPreferredSize(new Dimension(105,23)); r1.add(TNoRw);
        addLbl(r1,"No.RM :",42); TNoRM.setEditable(false); TNoRM.setPreferredSize(new Dimension(65,23)); r1.add(TNoRM);
        addLbl(r1,"Nama Bayi :",60); TPasien.setEditable(false); TPasien.setPreferredSize(new Dimension(150,23)); r1.add(TPasien);
        addLbl(r1,"NIK Bayi :",55); TNikAnak.setEditable(false); TNikAnak.setPreferredSize(new Dimension(130,23)); r1.add(TNikAnak);
        addLbl(r1,"J.K :",28); JK.setEditable(false); JK.setPreferredSize(new Dimension(35,23)); r1.add(JK);
        addLbl(r1,"Umur :",36); TUmur.setEditable(false); TUmur.setPreferredSize(new Dimension(60,23)); r1.add(TUmur);
        panelAtas.add(r1);

        // Baris 2: lahir, alamat, wilayah, ibu (auto)
        widget.panelisi r2=newRow();
        addLbl(r2,"Tgl Lahir :",60); TglLahir.setEditable(false); TglLahir.setPreferredSize(new Dimension(85,23)); r2.add(TglLahir);
        addLbl(r2,"Alamat :",45); TAlamat.setEditable(false); TAlamat.setPreferredSize(new Dimension(200,23)); r2.add(TAlamat);
        addLbl(r2,"Wilayah :",50); TWilayah.setEditable(false); TWilayah.setPreferredSize(new Dimension(230,23)); r2.add(TWilayah);
        addLbl(r2,"Nama Ibu :",58); TNmIbu.setEditable(false); TNmIbu.setPreferredSize(new Dimension(150,23)); r2.add(TNmIbu);
        panelAtas.add(r2);

        // Baris 3: data dasar manual
        widget.panelisi r3=newRow();
        addLbl(r3,"NIK Ibu :",60); TNikIbu.setPreferredSize(new Dimension(130,23)); r3.add(TNikIbu);
        addLbl(r3,"Tgl Ambil :",55); setTgl(TglAmbil); r3.add(TglAmbil);
        addLbl(r3,"Tgl Kirim :",52); setTgl(TglKirim); r3.add(TglKirim);
        addLbl(r3,"Tgl Lapor :",55); setTgl(TglLapor); r3.add(TglLapor);
        panelAtas.add(r3);

        // Baris 4: perujuk + id_shk
        widget.panelisi r4=newRow();
        addLbl(r4,"Kd Perujuk :",65); TKodePerujuk.setPreferredSize(new Dimension(80,23)); r4.add(TKodePerujuk);
        addLbl(r4,"Faskes Perujuk :",90); TNmPerujuk.setPreferredSize(new Dimension(220,23)); r4.add(TNmPerujuk);
        addLbl(r4,"Jns Faskes :",65); TJnsFaskes.setPreferredSize(new Dimension(40,23)); r4.add(TJnsFaskes);
        addLbl(r4,"id_shk :",45); TIdShk.setEditable(false); TIdShk.setPreferredSize(new Dimension(90,23)); r4.add(TIdShk);
        panelAtas.add(r4);

        // Baris 5: hasil lab manual
        widget.panelisi r5=newRow();
        addLbl(r5,"Jns Periksa :",65); TJnsPeriksa.setPreferredSize(new Dimension(40,23)); r5.add(TJnsPeriksa);
        addLbl(r5,"Hasil :",38); THasil.setPreferredSize(new Dimension(40,23)); r5.add(THasil);
        addLbl(r5,"Layak :",40); TLayak.setPreferredSize(new Dimension(40,23)); r5.add(TLayak);
        addLbl(r5,"id_layak :",52); TIdLayak.setPreferredSize(new Dimension(40,23)); r5.add(TIdLayak);
        addLbl(r5,"Tgl Periksa :",65); setTgl(TglPeriksa); r5.add(TglPeriksa);
        panelAtas.add(r5);

        // Baris 6: tgl hasil/terima + id_hasil
        widget.panelisi r6=newRow();
        addLbl(r6,"Tgl Hasil :",60); setTgl(TglHasil); r6.add(TglHasil);
        addLbl(r6,"Tgl Terima :",62); setTgl(TglTerima); r6.add(TglTerima);
        addLbl(r6,"id_hasil :",50); TIdHasil.setEditable(false); TIdHasil.setPreferredSize(new Dimension(90,23)); r6.add(TIdHasil);
        panelAtas.add(r6);

        internalFrame1.add(panelAtas, BorderLayout.PAGE_START);

        // ===== TABEL =====
        Scroll.setViewportView(tbData);
        tbData.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent evt){
                if(tbData.getSelectedRow()!=-1) getData();
            }
        });
        internalFrame1.add(Scroll, BorderLayout.CENTER);

        // ===== PANEL BAWAH =====
        panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        panelBawah.setPreferredSize(new Dimension(950, 44));

        // CRUD standar (pola form lain): Simpan, Batal, Hapus, Keluar
        mkBtn(BtnSimpan,"Simpan",100,"/picture/save-16x16.png",'S',"Alt+S : simpan lokal");
        BtnSimpan.addActionListener(evt -> simpanLokal()); panelBawah.add(BtnSimpan);
        mkBtn(BtnBatal,"Batal",100,"/picture/refresh.png",'B',"Alt+B : muat ulang");
        BtnBatal.addActionListener(evt -> { TCari.setText(""); if(!TNoRw.getText().trim().equals("")) isRawat(); }); panelBawah.add(BtnBatal);
        mkBtn(BtnHapus,"Hapus",100,"/picture/stop_f2.png",'H',"Alt+H : hapus lokal");
        BtnHapus.addActionListener(evt -> hapus()); panelBawah.add(BtnHapus);
        mkBtn(BtnKeluar,"Keluar",100,"/picture/exit.png",'K',"Alt+K : keluar");
        BtnKeluar.addActionListener(evt -> dispose()); panelBawah.add(BtnKeluar);

        // Tombol kirim online (setelah Keluar)
        mkBtn(BtnKirimDasar,"Kirim Data Dasar",160,"/picture/49619_globe_search_internet_icon.png",'D',"Alt+D : POST /Pasien/shk");
        BtnKirimDasar.addActionListener(evt -> kirimDasar()); panelBawah.add(BtnKirimDasar);
        mkBtn(BtnKirimHasil,"Kirim Hasil Lab",160,"/picture/49619_globe_search_internet_icon.png",'L',"Alt+L : POST /Pasien/hasilShk");
        BtnKirimHasil.addActionListener(evt -> kirimHasil()); panelBawah.add(BtnKirimHasil);

        addLbl(panelBawah,"Key Word :",60);
        TCari.setPreferredSize(new Dimension(120,23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter(){
            @Override public void keyPressed(KeyEvent e){if(e.getKeyCode()==KeyEvent.VK_ENTER) tampil();}
        });
        panelBawah.add(TCari);
        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setPreferredSize(new Dimension(28,23)); BtnCari.setToolTipText("Cari");
        BtnCari.addActionListener(evt -> tampil()); panelBawah.add(BtnCari);
        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setPreferredSize(new Dimension(28,23)); BtnAll.setToolTipText("Tampilkan semua");
        BtnAll.addActionListener(evt -> { TCari.setText(""); tampil(); }); panelBawah.add(BtnAll);
        LCount.setText("0"); LCount.setPreferredSize(new Dimension(60,23)); panelBawah.add(LCount);

        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(internalFrame1, BorderLayout.CENTER);
    }

    private widget.panelisi newRow(){
        widget.panelisi p=new widget.panelisi();
        p.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
        return p;
    }
    private void addLbl(java.awt.Container panel, String text, int width){
        widget.Label lbl=new widget.Label();
        lbl.setText(text); lbl.setPreferredSize(new Dimension(width,23));
        panel.add(lbl);
    }
    private void setTgl(widget.Tanggal t){
        try{ t.setDisplayFormat("dd-MM-yyyy"); }catch(Exception e){}
        t.setPreferredSize(new Dimension(110,23));
    }
    private void mkBtn(widget.Button b, String text, int w, String icon, char mn, String tip){
        b.setText(text);
        b.setPreferredSize(new Dimension(w,30));
        if(icon!=null){ try{ b.setIcon(new javax.swing.ImageIcon(getClass().getResource(icon))); }catch(Exception e){} }
        if(mn!=0) b.setMnemonic(mn);
        if(tip!=null) b.setToolTipText(tip);
    }

    // ===== Load data pasien (bayi + ibu + wilayah) =====
    private void isRawat(){
        try{
            ps=koneksi.prepareStatement(
                "SELECT reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.no_ktp,pasien.jk,pasien.tgl_lahir,"+
                "pasien.alamat,pasien.nm_ibu,"+
                "CONCAT(DATEDIFF(NOW(),pasien.tgl_lahir),' Hari') AS umur,"+
                "IFNULL(kelurahan.nm_kel,'') AS nm_kel,IFNULL(kecamatan.nm_kec,'') AS nm_kec,"+
                "IFNULL(kabupaten.nm_kab,'') AS nm_kab,IFNULL(propinsi.nm_prop,'') AS nm_prop,"+
                "pasien.kd_kec,pasien.kd_kab,pasien.kd_prop "+
                "FROM reg_periksa INNER JOIN pasien ON reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                "LEFT JOIN kelurahan ON pasien.kd_kel=kelurahan.kd_kel "+
                "LEFT JOIN kecamatan ON pasien.kd_kec=kecamatan.kd_kec "+
                "LEFT JOIN kabupaten ON pasien.kd_kab=kabupaten.kd_kab "+
                "LEFT JOIN propinsi ON pasien.kd_prop=propinsi.kd_prop "+
                "WHERE reg_periksa.no_rawat=?");
            ps.setString(1,TNoRw.getText());
            rs=ps.executeQuery();
            if(rs.next()){
                TNoRM.setText(rs.getString("no_rkm_medis"));
                TPasien.setText(rs.getString("nm_pasien"));
                TNikAnak.setText(rs.getString("no_ktp"));
                JK.setText(rs.getString("jk"));
                TglLahir.setText(rs.getString("tgl_lahir"));
                TAlamat.setText(rs.getString("alamat"));
                TNmIbu.setText(rs.getString("nm_ibu"));
                TUmur.setText(rs.getString("umur"));
                TWilayah.setText(rs.getString("nm_kel")+", "+rs.getString("nm_kec")+", "+rs.getString("nm_kab")+", "+rs.getString("nm_prop"));
                // simpan kode wilayah utk payload
                kdKec=rs.getString("kd_kec"); kdKab=rs.getString("kd_kab"); kdProp=rs.getString("kd_prop");
                nmKec=rs.getString("nm_kec");
            }
        }catch(Exception e){
            System.out.println("Notif isRawat SHK : "+e);
        }finally{
            try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}
        }
        // muat data tersimpan (kalau ada) utk no_rawat ini
        muatTersimpan();
    }
    private String kdKec="",kdKab="",kdProp="",nmKec="";

    private void muatTersimpan(){
        try{
            ps=koneksi.prepareStatement("SELECT * FROM shk WHERE no_rawat=?");
            ps.setString(1,TNoRw.getText());
            rs=ps.executeQuery();
            if(rs.next()){
                TIdShk.setText(n(rs.getString("id_shk")));
                TIdHasil.setText(n(rs.getString("id_hasil")));
                TNikIbu.setText(n(rs.getString("nik_ibu")));
                if(rs.getString("jenis_fasyankes")!=null) TJnsFaskes.setText(rs.getString("jenis_fasyankes"));
                if(rs.getString("kode_perujuk")!=null) TKodePerujuk.setText(rs.getString("kode_perujuk"));
                if(rs.getString("nama_fayankes_perujuk")!=null) TNmPerujuk.setText(rs.getString("nama_fayankes_perujuk"));
            }
        }catch(Exception e){ System.out.println("Notif muatTersimpan : "+e); }
        finally{ try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){} }
    }
    private String n(String s){ return s==null?"":s; }

    public void setNoRawat(String noRw){
        TNoRw.setText(noRw);
        TCari.setText(noRw);
        isRawat();
        tampil();
    }

    public void setNoRm(String noRm, String namaPasien){
        try{
            ps=koneksi.prepareStatement("SELECT no_rawat FROM reg_periksa WHERE no_rkm_medis=? ORDER BY tgl_registrasi DESC, jam_reg DESC LIMIT 1");
            ps.setString(1,noRm); rs=ps.executeQuery();
            if(rs.next()) setNoRawat(rs.getString("no_rawat"));
        }catch(Exception e){ System.out.println("Notif setNoRm : "+e); }
        finally{ try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){} }
    }

    private void tampil(){
        Valid.tabelKosong(tabMode);
        try{
            ps=koneksi.prepareStatement(
                "SELECT s.no_rawat,s.no_rkm_medis,p.nm_pasien,s.id_shk,s.id_hasil,s.status,"+
                "s.nik_ibu,s.nama_ibu,s.tgl_ambil_sampel,s.tgl_kirim_sampel,s.tgl_lapor,"+
                "s.jenis_pemeriksaan,s.hasil_pemeriksaan,s.tgl_periksa,s.tgl_hasil,s.layak_sampel,s.id_layak,s.tgl_terima "+
                "FROM shk s LEFT JOIN pasien p ON s.no_rkm_medis=p.no_rkm_medis "+
                "WHERE s.no_rawat LIKE ? OR s.no_rkm_medis LIKE ? OR p.nm_pasien LIKE ? "+
                "ORDER BY s.updated_at DESC");
            String q="%"+TCari.getText().trim()+"%";
            ps.setString(1,q); ps.setString(2,q); ps.setString(3,q);
            rs=ps.executeQuery();
            while(rs.next()){
                tabMode.addRow(new String[]{
                    rs.getString(1),rs.getString(2),rs.getString(3),n(rs.getString(4)),n(rs.getString(5)),rs.getString(6),
                    n(rs.getString(7)),n(rs.getString(8)),n(rs.getString(9)),n(rs.getString(10)),n(rs.getString(11)),
                    n(rs.getString(12)),n(rs.getString(13)),n(rs.getString(14)),n(rs.getString(15)),n(rs.getString(16)),n(rs.getString(17)),n(rs.getString(18))
                });
            }
        }catch(Exception e){ System.out.println("Notif tampil SHK : "+e); }
        finally{ try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){} }
        LCount.setText(""+tabMode.getRowCount());
    }

    private void getData(){
        int r=tbData.getSelectedRow();
        if(r==-1) return;
        TNoRw.setText(tabMode.getValueAt(r,0).toString());
        isRawat();
    }

    // ===== Simpan lokal (upsert) =====
    private boolean simpanLokal(){
        if(TNoRw.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null,"No.Rawat kosong. Buka dari menu rawat inap."); return false;
        }
        // upsert: hapus lalu insert (PK no_rawat). 21 kolom sesuai sql/create_shk.sql
        Sequel.queryu2("delete from shk where no_rawat=?",1,new String[]{TNoRw.getText()});
        Sequel.menyimpan("shk","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?",21,new String[]{
            TNoRw.getText(),TNoRM.getText(),nz(TIdShk.getText()),nz(TIdHasil.getText()),
            TNikIbu.getText(),TNmIbu.getText(),
            Valid.SetTgl(TglAmbil.getSelectedItem()+""),Valid.SetTgl(TglKirim.getSelectedItem()+""),Valid.SetTgl(TglLapor.getSelectedItem()+""),
            TKodePerujuk.getText(),TNmPerujuk.getText(),TJnsFaskes.getText(),
            TJnsPeriksa.getText(),THasil.getText(),
            Valid.SetTgl(TglPeriksa.getSelectedItem()+""),Valid.SetTgl(TglHasil.getSelectedItem()+""),
            TLayak.getText(),TIdLayak.getText(),Valid.SetTgl(TglTerima.getSelectedItem()+""),
            statusSekarang(),nowStr()
        });
        tampil();
        return true;
    }
    private String statusSekarang(){
        if(!TIdHasil.getText().trim().equals("")) return "Hasil Terkirim";
        if(!TIdShk.getText().trim().equals("")) return "Terkirim";
        return "Baru";
    }
    private String nz(String s){ return s==null?"":s.trim(); }
    private String nowStr(){ return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()); }

    // ===== Kirim Data Dasar: POST /Pasien/shk =====
    private void kirimDasar(){
        if(TNoRw.getText().trim().equals("")){ JOptionPane.showMessageDialog(null,"No.Rawat kosong."); return; }
        try{
            ObjectNode p=mapper.createObjectNode();
            p.put("koders",TKodePerujuk.getText());
            p.put("nik_ibu",nz(TNikIbu.getText()).equals("")?"000000000000000":TNikIbu.getText());
            p.put("nama_ibu",TNmIbu.getText());
            p.put("nik_anak",nz(TNikAnak.getText()).equals("")?"000000000000000":TNikAnak.getText());
            p.put("nama_anak",TPasien.getText());
            p.put("tgllahir",TglLahir.getText());
            p.put("gender",JK.getText().equalsIgnoreCase("L")?"Laki-Laki":"Perempuan");
            p.put("umur",TUmur.getText().replaceAll("[^0-9]",""));
            p.put("alamat",TAlamat.getText());
            p.put("provinsi",kdProp);
            p.put("kabkota",kdKab);
            p.put("kecamatan",nmKec);
            p.put("tgl_ambil_sampel",Valid.SetTgl(TglAmbil.getSelectedItem()+""));
            p.put("tgl_kirim_sampel",Valid.SetTgl(TglKirim.getSelectedItem()+""));
            p.put("tgl_lapor",Valid.SetTgl(TglLapor.getSelectedItem()+""));
            p.put("kode_perujuk",TKodePerujuk.getText());
            p.put("nama_fayankes_perujuk",TNmPerujuk.getText());
            p.put("jenis_fasyankes",TJnsFaskes.getText());
            String resp=postJson("/Pasien/shk",mapper.writeValueAsString(p));
            String id=cariId(resp,"id_shk");
            if(!id.equals("")) TIdShk.setText(id);
            simpanLokal();
            JOptionPane.showMessageDialog(null,"Respon /Pasien/shk:\n"+resp);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Gagal kirim data dasar: "+ex.getMessage());
        }
    }

    // ===== Kirim Hasil Lab: POST /Pasien/hasilShk =====
    private void kirimHasil(){
        if(TIdShk.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null,"id_shk kosong. Kirim Data Dasar dulu."); return;
        }
        try{
            ObjectNode p=mapper.createObjectNode();
            p.put("id_shk",TIdShk.getText());
            p.put("jenis_pemeriksaan",TJnsPeriksa.getText());
            p.put("hasil_pemeriksaan",THasil.getText());
            p.put("tgl_periksa",Valid.SetTgl(TglPeriksa.getSelectedItem()+""));
            p.put("tgl_hasil",Valid.SetTgl(TglHasil.getSelectedItem()+""));
            p.put("layak_sampel",TLayak.getText());
            p.put("id_layak",TIdLayak.getText());
            p.put("tgl_terima",Valid.SetTgl(TglTerima.getSelectedItem()+""));
            p.put("tgllapor",Valid.SetTgl(TglLapor.getSelectedItem()+""));
            String resp=postJson("/Pasien/hasilShk",mapper.writeValueAsString(p));
            String id=cariId(resp,"id_hasil");
            if(!id.equals("")) TIdHasil.setText(id);
            simpanLokal();
            JOptionPane.showMessageDialog(null,"Respon /Pasien/hasilShk:\n"+resp);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Gagal kirim hasil: "+ex.getMessage());
        }
    }

    private String postJson(String path, String json) throws Exception {
        HttpHeaders h=new HttpHeaders();
        h.add("X-rs-id",koneksiDB.IDCORONA());
        h.add("X-Timestamp",String.valueOf(api.GetUTCdatetimeAsString()));
        h.add("X-pass",api.getHmac());
        h.add("Content-Type","application/json");
        HttpEntity<String> req=new HttpEntity<>(json,h);
        return api.getRest().exchange(koneksiDB.URLAPICORONA()+path, HttpMethod.POST, req, String.class).getBody();
    }

    /** Cari nilai field (mis. id_shk/id_hasil) di JSON respon, rekursif. */
    private String cariId(String resp, String field){
        try{
            JsonNode root=mapper.readTree(resp);
            JsonNode f=cariNode(root,field);
            if(f!=null && !f.isNull()) return f.asText();
        }catch(Exception e){}
        return "";
    }
    private JsonNode cariNode(JsonNode node, String field){
        if(node==null) return null;
        if(node.has(field)) return node.get(field);
        if(node.isArray()){
            for(JsonNode c:node){ JsonNode r=cariNode(c,field); if(r!=null) return r; }
        }else if(node.isObject()){
            java.util.Iterator<String> it=node.fieldNames();
            while(it.hasNext()){ JsonNode r=cariNode(node.get(it.next()),field); if(r!=null) return r; }
        }
        return null;
    }

    private void hapus(){
        if(TNoRw.getText().trim().equals("")) return;
        if(JOptionPane.showConfirmDialog(null,"Hapus data SHK lokal no.rawat "+TNoRw.getText()+"?","Konfirmasi",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            Sequel.queryu2("delete from shk where no_rawat=?",1,new String[]{TNoRw.getText()});
            tampil();
        }
    }

    public void isCek(){
        BtnKirimDasar.setEnabled(true); BtnKirimHasil.setEnabled(true);
        BtnSimpan.setEnabled(true); BtnBatal.setEnabled(true); BtnHapus.setEnabled(true);
    }

    public javax.swing.JTable getTable(){ return tbData; }
}
