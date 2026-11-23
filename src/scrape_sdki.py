#!/usr/bin/env python3
"""
Scraper SDKI/SLKI/SIKI dari perawat.org
Menghasilkan: src/askep3s_seed_lengkap.sql (append / replace data master)

Usage:
  pip3 install requests beautifulsoup4
  python3 src/scrape_sdki.py
"""

import re
import time
import sys
import requests
from bs4 import BeautifulSoup
from urllib.parse import urljoin

# ── Daftar semua URL diagnosis ──────────────────────────────────────────────
DIAGNOSIS_URLS = [
    # Fisiologis – Respirasi
    "https://perawat.org/bersihan-jalan-napas-tidak-efektif",
    "https://perawat.org/gangguan-penyapihan-ventilator/",
    "https://perawat.org/gangguan-pertukaran-gas/",
    "https://perawat.org/gangguan-ventilasi-spontan/",
    "https://perawat.org/pola-napas-tidak-efektif/",
    "https://perawat.org/risiko-aspirasi/",
    # Fisiologis – Sirkulasi
    "https://perawat.org/gangguan-sirkulasi-spontan/",
    "https://perawat.org/penurunan-curah-jantung/",
    "https://perawat.org/perfusi-perifer-tidak-efektif/",
    "https://perawat.org/risiko-gangguan-sirkulasi-spontan/",
    "https://perawat.org/risiko-penurunan-curah-jantung/",
    "https://perawat.org/risiko-perdarahan/",
    "https://perawat.org/risiko-perfusi-gastrointestinal-tidak-efektif/",
    "https://perawat.org/risiko-perfusi-miokard-tidak-efektif/",
    "https://perawat.org/risiko-perfusi-perifer-tidak-efektif/",
    "https://perawat.org/risiko-perfusi-renal-tidak-efektif/",
    "https://perawat.org/risiko-perfusi-serebral-tidak-efektif/",
    # Fisiologis – Nutrisi & Cairan
    "https://perawat.org/berat-badan-lebih",
    "https://perawat.org/defisit-nutrisi",
    "https://perawat.org/diare",
    "https://perawat.org/disfungsi-motilitas-gastrointestinal",
    "https://perawat.org/hipervolemia",
    "https://perawat.org/hipovolemia",
    "https://perawat.org/ikterik-neonatus",
    "https://perawat.org/kesiapan-peningkatan-keseimbangan-cairan",
    "https://perawat.org/kesiapan-peningkatan-nutrisi",
    "https://perawat.org/ketidakstabilan-kadar-glukosa-darah",
    "https://perawat.org/menyusui-efektif",
    "https://perawat.org/menyusui-tidak-efektif",
    "https://perawat.org/obesitas",
    "https://perawat.org/risiko-berat-badan-lebih",
    "https://perawat.org/risiko-defisit-nutrisi",
    "https://perawat.org/risiko-disfungsi-motilitas-gastrointestinal",
    "https://perawat.org/risiko-hipovolemia",
    "https://perawat.org/risiko-ikterik-neonatus",
    "https://perawat.org/risiko-ketidakseimbangan-cairan",
    "https://perawat.org/risiko-ketidakseimbangan-elektrolit",
    "https://perawat.org/risiko-ketidakstabilan-kadar-glukosa-darah",
    "https://perawat.org/risiko-syok",
    # Fisiologis – Eliminasi
    "https://perawat.org/gangguan-eliminasi-urin/",
    "https://perawat.org/inkontinensia-fekal/",
    "https://perawat.org/inkontinensia-urin-berlanjut/",
    "https://perawat.org/inkontinensia-urin-berlebih/",
    "https://perawat.org/inkontinensia-urin-fungsional/",
    "https://perawat.org/inkontinensia-urin-refleks/",
    "https://perawat.org/inkontinensia-urin-stres/",
    "https://perawat.org/inkontinensia-urin-urgensi/",
    "https://perawat.org/kesiapan-peningkatan-eliminasi-urin/",
    "https://perawat.org/konstipasi/",
    "https://perawat.org/retensi-urin/",
    "https://perawat.org/risiko-inkontinensia-urin-urgensi/",
    "https://perawat.org/risiko-konstipasi",
    # Fisiologis – Aktivitas & Istirahat
    "https://perawat.org/disorganisasi-perilaku-bayi/",
    "https://perawat.org/gangguan-mobilitas-fisik/",
    "https://perawat.org/gangguan-pola-tidur/",
    "https://perawat.org/intoleransi-aktivitas/",
    "https://perawat.org/keletihan/",
    "https://perawat.org/kesiapan-peningkatan-tidur/",
    "https://perawat.org/risiko-disorganisasi-perilaku-bayi",
    "https://perawat.org/risiko-intoleransi-aktivitas/",
    # Fisiologis – Neurosensori
    "https://perawat.org/disrefleksia-otonom/",
    "https://perawat.org/gangguan-memori/",
    "https://perawat.org/gangguan-menelan/",
    "https://perawat.org/konfusi-akut/",
    "https://perawat.org/konfusi-kronis/",
    "https://perawat.org/penurunan-kapasitas-adaptif-intrakranial/",
    "https://perawat.org/risiko-disfungsi-neurovaskuler-perifer/",
    "https://perawat.org/risiko-konfusi-akut/",
    # Fisiologis – Reproduksi & Seksualitas
    "https://perawat.org/disfungsi-seksual/",
    "https://perawat.org/kesiapan-persalinan/",
    "https://perawat.org/pola-seksual-tidak-efektif/",
    "https://perawat.org/risiko-disfungsi-seksual/",
    "https://perawat.org/risiko-kehamilan-tidak-dikehendaki/",
    # Fisiologis – Nyeri & Kenyamanan
    "https://perawat.org/gangguan-rasa-nyaman/",
    "https://perawat.org/ketidaknyamanan-pasca-partum/",
    "https://perawat.org/nausea/",
    "https://perawat.org/nyeri-akut/",
    "https://perawat.org/nyeri-kronis/",
    "https://perawat.org/nyeri-melahirkan/",
    # Psikologis
    "https://perawat.org/ansietas",
    "https://perawat.org/berduka",
    "https://perawat.org/distres-spiritual",
    "https://perawat.org/gangguan-citra-tubuh",
    "https://perawat.org/gangguan-identitas-diri",
    "https://perawat.org/gangguan-persepsi-sensori",
    "https://perawat.org/harga-diri-rendah-kronis",
    "https://perawat.org/harga-diri-rendah-situasional",
    "https://perawat.org/keputusasaan",
    "https://perawat.org/kesiapan-peningkatan-konsep-diri",
    "https://perawat.org/kesiapan-peningkatan-koping-keluarga",
    "https://perawat.org/kesiapan-peningkatan-koping-komunitas",
    "https://perawat.org/ketidakberdayaan",
    "https://perawat.org/ketidakmampuan-koping-keluarga",
    "https://perawat.org/koping-defensif",
    "https://perawat.org/koping-komunitas-tidak-efektif",
    "https://perawat.org/koping-tidak-efektif",
    "https://perawat.org/penurunan-koping-keluarga",
    "https://perawat.org/penyangkalan-tidak-efektif",
    "https://perawat.org/perilaku-kesehatan-cenderung-berisiko",
    "https://perawat.org/risiko-distres-spiritual",
    "https://perawat.org/risiko-harga-diri-rendah-kronis",
    "https://perawat.org/risiko-harga-diri-rendah-situasional",
    "https://perawat.org/risiko-ketidakberdayaan",
    "https://perawat.org/waham",
    # Perilaku – Tumbuh Kembang
    "https://perawat.org/gangguan-tumbuh-kembang",
    "https://perawat.org/risiko-gangguan-perkembangan",
    "https://perawat.org/risiko-gangguan-pertumbuhan",
    # Perilaku – Kebersihan Diri
    "https://perawat.org/defisit-perawatan-diri",
    # Perilaku – Penyuluhan & Pembelajaran
    "https://perawat.org/defisit-kesehatan-komunitas",
    "https://perawat.org/defisit-pengetahuan",
    "https://perawat.org/kesiapan-peningkatan-manajemen-kesehatan",
    "https://perawat.org/kesiapan-peningkatan-pengetahuan",
    "https://perawat.org/ketidakpatuhan",
    "https://perawat.org/manajemen-kesehatan-keluarga-tidak-efektif",
    "https://perawat.org/manajemen-kesehatan-tidak-efektif",
    "https://perawat.org/pemeliharaan-kesehatan-tidak-efektif",
    # Relasional
    "https://perawat.org/gangguan-interaksi-sosial",
    "https://perawat.org/gangguan-komunikasi-verbal",
    "https://perawat.org/gangguan-proses-keluarga",
    "https://perawat.org/isolasi-sosial",
    "https://perawat.org/kesiapan-peningkatan-proses-keluarga",
    "https://perawat.org/ketegangan-peran-pemberi-asuhan",
    "https://perawat.org/penampilan-peran-tidak-efektif",
    "https://perawat.org/risiko-gangguan-perlekatan",
    "https://perawat.org/risiko-proses-pengasuhan-tidak-efektif",
    # Lingkungan – Keamanan & Proteksi
    "https://perawat.org/gangguan-integritas-kulit-jaringan",
    "https://perawat.org/hipertermia",
    "https://perawat.org/hipotermia",
    "https://perawat.org/perilaku-kekerasan",
    "https://perawat.org/perlambatan-pemulihan-pascabedah",
    "https://perawat.org/risiko-alergi",
    "https://perawat.org/risiko-bunuh-diri",
    "https://perawat.org/risiko-cedera",
    "https://perawat.org/risiko-cedera-pada-ibu",
    "https://perawat.org/risiko-cedera-pada-janin",
    "https://perawat.org/risiko-gangguan-integritas-kulit-jaringan",
    "https://perawat.org/risiko-hipotermia",
    "https://perawat.org/risiko-hipotermia-perioperatif",
    "https://perawat.org/risiko-infeksi",
    "https://perawat.org/risiko-jatuh",
    "https://perawat.org/risiko-luka-tekan",
    "https://perawat.org/risiko-mutilasi-diri",
    "https://perawat.org/risiko-perilaku-kekerasan",
    "https://perawat.org/risiko-perlambatan-pemulihan-pascabedah",
    "https://perawat.org/risiko-termoregulasi-tidak-efektif",
    "https://perawat.org/termoregulasi-tidak-efektif",
]

SESSION = requests.Session()
SESSION.headers.update({
    "User-Agent": "Mozilla/5.0 (X11; Linux x86_64; rv:115.0) Gecko/20100101 Firefox/115.0",
    "Accept-Language": "id-ID,id;q=0.9,en;q=0.8",
})

def escape_sql(s):
    if s is None:
        return ""
    return s.replace("\\", "\\\\").replace("'", "\\'").replace("\n", " ").replace("\r", "")

def clean_text(s):
    if not s:
        return ""
    s = re.sub(r'\s+', ' ', s.strip())
    return s

def fetch_page(url):
    """Fetch URL, retry up to 3 times."""
    url = url.rstrip('/') + '/'
    for attempt in range(3):
        try:
            r = SESSION.get(url, timeout=20)
            if r.status_code == 200:
                return r.text
            print(f"  HTTP {r.status_code} for {url}", file=sys.stderr)
        except Exception as e:
            print(f"  Error fetching {url}: {e}", file=sys.stderr)
        time.sleep(2)
    return None

def parse_diagnosis(html, url):
    """Parse diagnosis page, return dict with all data."""
    soup = BeautifulSoup(html, 'html.parser')
    article = soup.find('article')
    if not article:
        return None

    result = {
        'url': url,
        'label': '',
        'kode': '',
        'jenis': 'aktual',
        'kategori': '',
        'subkategori': '',
        'definisi': '',
        'etiologi': [],
        'gejala_mayor_ds': [],
        'gejala_mayor_do': [],
        'gejala_minor_ds': [],
        'gejala_minor_do': [],
        'slki_kode': '',
        'slki_label': '',
        'slki_indikator': [],
        'siki': [],   # list of {'kode': ..., 'label': ..., 'tindakan': {'observasi':[], 'terapeutik':[], 'edukasi':[], 'kolaborasi':[]}}
    }

    content = article.get_text(separator='\n')
    content_html = str(article)

    # ── Kode SDKI (D.XXXX) ──
    kode_match = re.search(r'D\.(\d{4})', content)
    if kode_match:
        result['kode'] = 'D.' + kode_match.group(1)

    # ── Jenis diagnosis ──
    if any(x in content.lower() for x in ['diagnosis risiko', 'risiko terjadinya', 'risiko mengalami']):
        result['jenis'] = 'risiko'
    elif 'kesiapan peningkatan' in url.lower() or 'kesiapan-peningkatan' in url.lower():
        result['jenis'] = 'promosi kesehatan'

    # ── Label diagnosis dari judul h1 atau strong pertama ──
    h1 = article.find('h1')
    if h1:
        result['label'] = clean_text(h1.get_text())
    else:
        # dari <title> atau first strong
        title = soup.find('title')
        if title:
            t = title.get_text().split('|')[0].split('–')[0].strip()
            result['label'] = clean_text(t)
    # Bersihkan suffix "[SDKI D.XXXX] - ..." dari label
    result['label'] = re.sub(r'\s*\[SDKI D\.\d+\].*$', '', result['label']).strip()
    result['label'] = re.sub(r'\s*-\s*perawat\.org.*$', '', result['label'], flags=re.IGNORECASE).strip()

    # ── Definisi (paragraf pertama article) ──
    entry = article.find(class_='entry-content') or article
    first_p = entry.find('p')
    if first_p:
        strong = first_p.find('strong')
        if strong:
            result['definisi'] = clean_text(strong.get_text())
        else:
            result['definisi'] = clean_text(first_p.get_text())

    # ── Kategori & subkategori dari tag / breadcrumb ──
    tags = [a.get_text() for a in article.find_all('a', href=re.compile(r'/tag/'))]
    for t in tags:
        t_lower = t.lower()
        if any(x in t_lower for x in ['fisiologis', 'psikologis', 'perilaku', 'relasional', 'lingkungan']):
            result['kategori'] = clean_text(t)
        elif t_lower not in ['sdki', 'siki', 'slki']:
            result['subkategori'] = clean_text(t)

    # Kategori dari URL/konten
    if not result['kategori']:
        if any(x in content.lower() for x in ['kategori fisiologis', 'fisiologis, subkategori', 'subkategori respirasi', 'subkategori sirkulasi', 'subkategori nutrisi', 'subkategori eliminasi', 'subkategori aktivitas', 'subkategori neurosensori', 'subkategori reproduksi', 'subkategori nyeri']):
            result['kategori'] = 'Fisiologis'
        elif 'kategori psikologis' in content.lower() or 'psikologis, subkategori' in content.lower():
            result['kategori'] = 'Psikologis'
        elif 'kategori perilaku' in content.lower() or 'perilaku, subkategori' in content.lower():
            result['kategori'] = 'Perilaku'
        elif 'kategori relasional' in content.lower() or 'relasional, subkategori' in content.lower():
            result['kategori'] = 'Relasional'
        elif 'kategori lingkungan' in content.lower() or 'lingkungan, subkategori' in content.lower():
            result['kategori'] = 'Lingkungan'

    # ── Tanda & Gejala ──
    # Find "Tanda dan Gejala" heading
    tanda_heading = article.find(id=re.compile(r'tanda.*gejala', re.I))
    if tanda_heading:
        # Walk sibling elements
        current_group = None  # 'ds' or 'do'
        current_tipe = 'mayor'  # 'mayor' or 'minor'
        for sibling in tanda_heading.find_next_siblings():
            tag = sibling.name
            text = clean_text(sibling.get_text())
            if tag in ['h2', 'h3'] and any(x in text.lower() for x in ['penyebab', 'etiologi', 'penulisan', 'luaran', 'intervensi', 'referensi']):
                break
            if tag in ['h3', 'h4']:
                if 'minor' in text.lower():
                    current_tipe = 'minor'
                elif 'mayor' in text.lower():
                    current_tipe = 'mayor'
            if tag == 'p':
                if re.match(r'^DS\s*:', text, re.I):
                    current_group = 'ds'
                elif re.match(r'^DO\s*:', text, re.I):
                    current_group = 'do'
            if tag == 'ul' and current_group:
                items = [clean_text(li.get_text()) for li in sibling.find_all('li') if clean_text(li.get_text())]
                if current_tipe == 'mayor':
                    if current_group == 'ds':
                        result['gejala_mayor_ds'].extend(items)
                    else:
                        result['gejala_mayor_do'].extend(items)
                else:
                    if current_group == 'ds':
                        result['gejala_minor_ds'].extend(items)
                    else:
                        result['gejala_minor_do'].extend(items)

    # ── Etiologi / Penyebab ──
    etio_heading = article.find(id=re.compile(r'penyebab|etiologi', re.I))
    if etio_heading:
        for sibling in etio_heading.find_next_siblings():
            tag = sibling.name
            text = clean_text(sibling.get_text())
            if tag in ['h2', 'h3'] and any(x in text.lower() for x in ['penulisan', 'luaran', 'intervensi', 'referensi', 'tanda']):
                break
            if tag in ['ol', 'ul']:
                items = [clean_text(li.get_text()) for li in sibling.find_all('li') if clean_text(li.get_text())]
                result['etiologi'].extend(items)
                break

    # ── SLKI (Luaran) ──
    luaran_heading = article.find(id=re.compile(r'luaran', re.I))
    if luaran_heading:
        for sibling in luaran_heading.find_next_siblings():
            tag = sibling.name
            text = clean_text(sibling.get_text())
            if tag in ['h2'] and any(x in text.lower() for x in ['intervensi', 'referensi', 'diagnosis terkait']):
                break
            # SLKI kode
            if not result['slki_kode']:
                m = re.search(r'L\.(\d{5})', text)
                if m:
                    result['slki_kode'] = 'L.' + m.group(1)
            # Indikator
            if tag == 'ol' and 'kriteria hasil' in content_html.lower():
                items = [clean_text(li.get_text()) for li in sibling.find_all('li') if clean_text(li.get_text())]
                if items and not result['slki_indikator']:
                    result['slki_indikator'] = items
            # SLKI label
            if not result['slki_label'] and result['slki_kode']:
                m = re.search(r'([a-zA-ZÀ-ÿ\s]+)\s+' + re.escape(result['slki_kode']), text)
                if not m:
                    m = re.search(r'luaran[^\"]+?adalah[^\"]*?\*\*["\']?([^\"*]+)["\']?\*\*', text, re.I)
                if not m:
                    # Label sering muncul setelah "luaran utama adalah:"
                    m2 = re.search(r'luaran utama[^\:]*?:\s*[\"\'"]?([^.\"\']+)[\"\'"]?', text, re.I)
                    if m2:
                        result['slki_label'] = clean_text(m2.group(1)).strip('"\'""')

    # Look for SLKI label from strong tags near kode
    if result['slki_kode'] and not result['slki_label']:
        for strong in article.find_all('strong'):
            t = clean_text(strong.get_text())
            if result['slki_kode'] in t:
                # label is the sibling text before kode
                result['slki_label'] = t.replace(result['slki_kode'], '').strip('() ').strip()
                break
        if not result['slki_label']:
            # Get from text pattern
            m = re.search(r'(?:luaran utama.*?adalah.*?[:\"\']?\s*)([A-Za-zÀ-ÿ\s\-]+)\s*(?:menurun|meningkat|membaik)', content, re.I)
            if m:
                result['slki_label'] = clean_text(m.group(1))

    # ── SIKI (Intervensi) ──
    intervensi_heading = article.find(id=re.compile(r'^intervensi$', re.I))
    if intervensi_heading:
        current_siki = None
        for sibling in intervensi_heading.find_next_siblings():
            tag = sibling.name
            text = clean_text(sibling.get_text())
            if tag in ['h2'] and any(x in text.lower() for x in ['referensi', 'diagnosis terkait']):
                break
            # h3 = new SIKI intervensi
            if tag == 'h3':
                kode_m = re.search(r'I\.(\d{5})', text)
                label = re.sub(r'\(I\.\d+\)', '', text).strip()
                current_siki = {
                    'kode': 'I.' + kode_m.group(1) if kode_m else '',
                    'label': label,
                    'tindakan': {'Observasi': [], 'Terapeutik': [], 'Edukasi': [], 'Kolaborasi': []}
                }
                result['siki'].append(current_siki)
            # h4 = tindakan group (Observasi, Terapeutik, Edukasi, Kolaborasi)
            elif tag == 'h4' and current_siki:
                current_komponen = text
            # ul = tindakan list
            elif tag == 'ul' and current_siki:
                items = [clean_text(li.get_text()) for li in sibling.find_all('li') if clean_text(li.get_text())]
                # determine which komponen
                found = False
                for komp in ['Observasi', 'Terapeutik', 'Edukasi', 'Kolaborasi']:
                    try:
                        if komp.lower() in current_komponen.lower():
                            current_siki['tindakan'][komp].extend(items)
                            found = True
                            break
                    except:
                        pass
                if not found and items:
                    current_siki['tindakan']['Observasi'].extend(items)
            elif tag == 'p' and current_siki:
                t_lower = text.lower()
                for komp in ['observasi', 'terapeutik', 'edukasi', 'kolaborasi']:
                    if text.strip().lower() == komp:
                        current_komponen = komp.capitalize()

    return result


def generate_sql(diagnoses):
    """Generate SQL INSERT statements."""
    lines = []
    lines.append("-- ============================================================")
    lines.append("-- SEED DATA SDKI/SLKI/SIKI - Generated by scrape_sdki.py")
    lines.append(f"-- Total diagnosis: {len(diagnoses)}")
    lines.append("-- ============================================================")
    lines.append("")

    # ── Kategori ──
    lines.append("-- Kategori SDKI")
    kategori_map = {
        'Fisiologis': 1, 'Psikologis': 2, 'Perilaku': 3,
        'Relasional': 4, 'Lingkungan': 5
    }
    for nama, id_kat in kategori_map.items():
        lines.append(f"INSERT IGNORE INTO mst_sdki_kategori (id, nama) VALUES ({id_kat}, '{nama}');")
    lines.append("")

    # ── Collect unique SLKI and SIKI ──
    slki_map = {}   # kode -> {label, indikator}
    siki_map = {}   # kode -> {label, tindakan}

    # ── SDKI ──
    lines.append("-- Diagnosis SDKI")
    lines.append("DELETE FROM mst_sdki_etiologi WHERE id_sdki IN (SELECT id FROM mst_sdki WHERE id > 30);")
    lines.append("DELETE FROM mst_sdki_tanda_gejala WHERE id_sdki IN (SELECT id FROM mst_sdki WHERE id > 30);")
    lines.append("")

    sdki_id = 100  # start from 100 to not conflict with existing 30
    sdki_ids = {}   # url_slug -> sdki_id

    for d in diagnoses:
        if not d or not d.get('kode'):
            continue
        slug = d['url'].rstrip('/').split('/')[-1]
        id_kat = kategori_map.get(d['kategori'], 1)
        # Tentukan jenis dari label (lebih akurat dari konten)
        label_lower = d['label'].lower()
        if label_lower.startswith('risiko ') or label_lower.startswith('risiko-'):
            jenis = 'risiko'
        elif 'kesiapan peningkatan' in label_lower:
            jenis = 'promosi kesehatan'
        else:
            jenis = 'aktual'

        # Single-line INSERT untuk menghindari masalah regex post-processing
        lines.append(f"-- {d['kode']} {d['label']}")
        lines.append(f"INSERT IGNORE INTO mst_sdki (id, kode, label, definisi, jenis, id_kategori) VALUES ({sdki_id}, '{escape_sql(d['kode'])}', '{escape_sql(d['label'][:200])}', '{escape_sql(d['definisi'][:500])}', '{jenis}', {id_kat});")
        lines.append("")

        # Tanda gejala mayor
        urutan = 1
        for g in d['gejala_mayor_ds']:
            lines.append(f"INSERT IGNORE INTO mst_sdki_tanda_gejala (id_sdki, tipe, kelompok, deskripsi, urutan) VALUES ({sdki_id}, 'mayor', 'subjektif', '{escape_sql(g)}', {urutan});")
            urutan += 1
        for g in d['gejala_mayor_do']:
            lines.append(f"INSERT IGNORE INTO mst_sdki_tanda_gejala (id_sdki, tipe, kelompok, deskripsi, urutan) VALUES ({sdki_id}, 'mayor', 'objektif', '{escape_sql(g)}', {urutan});")
            urutan += 1
        # Tanda gejala minor
        urutan = 1
        for g in d['gejala_minor_ds']:
            lines.append(f"INSERT IGNORE INTO mst_sdki_tanda_gejala (id_sdki, tipe, kelompok, deskripsi, urutan) VALUES ({sdki_id}, 'minor', 'subjektif', '{escape_sql(g)}', {urutan});")
            urutan += 1
        for g in d['gejala_minor_do']:
            lines.append(f"INSERT IGNORE INTO mst_sdki_tanda_gejala (id_sdki, tipe, kelompok, deskripsi, urutan) VALUES ({sdki_id}, 'minor', 'objektif', '{escape_sql(g)}', {urutan});")
            urutan += 1
        # Etiologi
        for e in d['etiologi']:
            lines.append(f"INSERT IGNORE INTO mst_sdki_etiologi (id_sdki, deskripsi) VALUES ({sdki_id}, '{escape_sql(e)}');")

        lines.append("")

        sdki_ids[slug] = sdki_id

        # Collect SLKI
        if d['slki_kode'] and d['slki_kode'] not in slki_map:
            slki_map[d['slki_kode']] = {
                'label': d['slki_label'] or d['slki_kode'],
                'indikator': d['slki_indikator']
            }

        # Collect SIKI
        for siki in d['siki']:
            if siki['kode'] and siki['kode'] not in siki_map:
                siki_map[siki['kode']] = {
                    'label': siki['label'],
                    'tindakan': siki['tindakan']
                }

        sdki_id += 1

    # ── SLKI ──
    lines.append("")
    lines.append("-- ── SLKI (Luaran) ──────────────────────────────────────────")
    slki_id = 100
    slki_kode_to_id = {}
    for kode, data in slki_map.items():
        label = data['label'] or kode
        lines.append(f"INSERT IGNORE INTO mst_slki (id, kode, label, definisi) VALUES ({slki_id}, '{escape_sql(kode)}', '{escape_sql(label[:200])}', '');")
        for ind in data['indikator']:
            # Determine tipe_skala based on keyword
            if any(x in ind.lower() for x in ['menurun', 'berkurang', 'turun']):
                tipe = 'menurun'
            elif any(x in ind.lower() for x in ['meningkat', 'bertambah', 'naik']):
                tipe = 'meningkat'
            else:
                tipe = 'membaik'
            lines.append(f"INSERT IGNORE INTO mst_slki_indikator (id_slki, label, tipe_skala, urutan) VALUES ({slki_id}, '{escape_sql(ind)}', '{tipe}', 1);")
        slki_kode_to_id[kode] = slki_id
        slki_id += 1
    lines.append("")

    # ── SIKI ──
    lines.append("-- ── SIKI (Intervensi) ──────────────────────────────────────")
    siki_id = 100
    siki_kode_to_id = {}
    for kode, data in siki_map.items():
        label = data['label'] or kode
        lines.append(f"INSERT IGNORE INTO mst_siki (id, kode, label, tipe, definisi) VALUES ({siki_id}, '{escape_sql(kode)}', '{escape_sql(label[:200])}', 'utama', '');")
        for komponen, items in data['tindakan'].items():
            for idx, t in enumerate(items, 1):
                lines.append(f"INSERT IGNORE INTO mst_siki_tindakan (id_siki, komponen, deskripsi, urutan) VALUES ({siki_id}, '{komponen}', '{escape_sql(t)}', {idx});")
        siki_kode_to_id[kode] = siki_id
        siki_id += 1
    lines.append("")

    # ── Mapping SDKI → SLKI ──
    lines.append("-- ── Mapping SDKI → SLKI ────────────────────────────────────")
    for d in diagnoses:
        if not d or not d.get('kode'):
            continue
        slug = d['url'].rstrip('/').split('/')[-1]
        did = sdki_ids.get(slug)
        if did and d['slki_kode'] and d['slki_kode'] in slki_kode_to_id:
            lines.append(f"INSERT IGNORE INTO map_sdki_slki (id_sdki, id_slki, prioritas) VALUES ({did}, {slki_kode_to_id[d['slki_kode']]}, 1);")
    lines.append("")

    # ── Mapping SDKI → SIKI ──
    lines.append("-- ── Mapping SDKI → SIKI ────────────────────────────────────")
    for d in diagnoses:
        if not d or not d.get('kode'):
            continue
        slug = d['url'].rstrip('/').split('/')[-1]
        did = sdki_ids.get(slug)
        if did:
            for siki in d['siki']:
                if siki['kode'] and siki['kode'] in siki_kode_to_id:
                    lines.append(f"INSERT IGNORE INTO map_sdki_siki (id_sdki, id_siki, tipe) VALUES ({did}, {siki_kode_to_id[siki['kode']]}, 'utama');")
    lines.append("")
    lines.append("-- End of seed")

    return '\n'.join(lines)


def main():
    print(f"Memulai scraping {len(DIAGNOSIS_URLS)} diagnosis SDKI dari perawat.org...")
    print("Delay 2 detik per halaman untuk menghindari rate limiting.\n")

    diagnoses = []
    failed = []

    for i, url in enumerate(DIAGNOSIS_URLS, 1):
        slug = url.rstrip('/').split('/')[-1]
        print(f"[{i:3d}/{len(DIAGNOSIS_URLS)}] {slug}...", end=' ', flush=True)
        html = fetch_page(url)
        if not html:
            print("GAGAL")
            failed.append(url)
            continue
        d = parse_diagnosis(html, url)
        if d:
            diagnoses.append(d)
            print(f"OK  {d.get('kode','?')} | {d.get('label','')[:50]}")
        else:
            print("PARSE ERROR")
            failed.append(url)
        time.sleep(2)

    print(f"\nBerhasil: {len(diagnoses)}/{len(DIAGNOSIS_URLS)}")
    if failed:
        print(f"Gagal ({len(failed)}):")
        for f in failed:
            print(f"  {f}")

    # Generate SQL
    sql = generate_sql(diagnoses)

    out_path = "src/askep3s_seed_149sdki.sql"
    with open(out_path, 'w', encoding='utf-8') as f:
        f.write(sql)
    print(f"\nSQL disimpan ke: {out_path}")
    print(f"Total baris SQL: {sql.count(chr(10))}")
    print("\nJalankan di database:")
    print(f"  mysql -u root -prosmayanti -h localhost sikrscb < {out_path}")


if __name__ == '__main__':
    main()
