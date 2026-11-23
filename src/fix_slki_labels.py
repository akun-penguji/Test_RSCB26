#!/usr/bin/env python3
"""
Fix SLKI labels di askep3s_seed_149sdki.sql
Mengambil label SLKI yang benar dari halaman SDKI di perawat.org
(Pattern: "{label} diberi kode L.XXXXX dalam SLKI")
"""

import re, time, sys
import requests
from bs4 import BeautifulSoup

SQL_FILE = "src/askep3s_seed_149sdki.sql"

DIAGNOSIS_URLS = [
    "https://perawat.org/bersihan-jalan-napas-tidak-efektif",
    "https://perawat.org/gangguan-penyapihan-ventilator/",
    "https://perawat.org/gangguan-pertukaran-gas/",
    "https://perawat.org/gangguan-ventilasi-spontan/",
    "https://perawat.org/pola-napas-tidak-efektif/",
    "https://perawat.org/risiko-aspirasi/",
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
    "https://perawat.org/disorganisasi-perilaku-bayi/",
    "https://perawat.org/gangguan-mobilitas-fisik/",
    "https://perawat.org/gangguan-pola-tidur/",
    "https://perawat.org/intoleransi-aktivitas/",
    "https://perawat.org/keletihan/",
    "https://perawat.org/kesiapan-peningkatan-tidur/",
    "https://perawat.org/risiko-disorganisasi-perilaku-bayi",
    "https://perawat.org/risiko-intoleransi-aktivitas/",
    "https://perawat.org/disrefleksia-otonom/",
    "https://perawat.org/gangguan-memori/",
    "https://perawat.org/gangguan-menelan/",
    "https://perawat.org/konfusi-akut/",
    "https://perawat.org/konfusi-kronis/",
    "https://perawat.org/penurunan-kapasitas-adaptif-intrakranial/",
    "https://perawat.org/risiko-disfungsi-neurovaskuler-perifer/",
    "https://perawat.org/risiko-konfusi-akut/",
    "https://perawat.org/disfungsi-seksual/",
    "https://perawat.org/kesiapan-persalinan/",
    "https://perawat.org/pola-seksual-tidak-efektif/",
    "https://perawat.org/risiko-disfungsi-seksual/",
    "https://perawat.org/risiko-kehamilan-tidak-dikehendaki/",
    "https://perawat.org/gangguan-rasa-nyaman/",
    "https://perawat.org/ketidaknyamanan-pasca-partum/",
    "https://perawat.org/nausea/",
    "https://perawat.org/nyeri-akut/",
    "https://perawat.org/nyeri-kronis/",
    "https://perawat.org/nyeri-melahirkan/",
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
    "https://perawat.org/gangguan-tumbuh-kembang",
    "https://perawat.org/risiko-gangguan-perkembangan",
    "https://perawat.org/risiko-gangguan-pertumbuhan",
    "https://perawat.org/defisit-perawatan-diri",
    "https://perawat.org/defisit-kesehatan-komunitas",
    "https://perawat.org/defisit-pengetahuan",
    "https://perawat.org/kesiapan-peningkatan-manajemen-kesehatan",
    "https://perawat.org/kesiapan-peningkatan-pengetahuan",
    "https://perawat.org/ketidakpatuhan",
    "https://perawat.org/manajemen-kesehatan-keluarga-tidak-efektif",
    "https://perawat.org/manajemen-kesehatan-tidak-efektif",
    "https://perawat.org/pemeliharaan-kesehatan-tidak-efektif",
    "https://perawat.org/gangguan-interaksi-sosial",
    "https://perawat.org/gangguan-komunikasi-verbal",
    "https://perawat.org/gangguan-proses-keluarga",
    "https://perawat.org/isolasi-sosial",
    "https://perawat.org/kesiapan-peningkatan-proses-keluarga",
    "https://perawat.org/ketegangan-peran-pemberi-asuhan",
    "https://perawat.org/penampilan-peran-tidak-efektif",
    "https://perawat.org/risiko-gangguan-perlekatan",
    "https://perawat.org/risiko-proses-pengasuhan-tidak-efektif",
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
SESSION.headers.update({"User-Agent": "Mozilla/5.0 (X11; Linux x86_64; rv:115.0) Gecko/20100101 Firefox/115.0"})

def fetch_slki_mapping(url):
    """Fetch page and return dict {kode: label}."""
    url = url.rstrip('/') + '/'
    try:
        r = SESSION.get(url, timeout=20)
        if r.status_code != 200:
            return {}
        soup = BeautifulSoup(r.text, 'html.parser')
        article = soup.find('article')
        if not article:
            return {}
        text = article.get_text()
        # Pattern: "{label} diberi kode L.XXXXX dalam SLKI"
        mapping = {}
        for m in re.finditer(r'([A-Za-z][^\n.]{3,80}?)\s+diberi kode\s+(L\.\d+)\s+dalam SLKI', text, re.I):
            label = m.group(1).strip()
            kode = m.group(2).strip()
            # Capitalize properly
            label = ' '.join(w.capitalize() for w in label.split())
            mapping[kode] = label
        return mapping
    except Exception as e:
        return {}

def main():
    # First, read current SQL to find which SLKI kodes need labels
    with open(SQL_FILE, 'r', encoding='utf-8') as f:
        sql = f.read()

    # Find SLKI kodes that have label = kode (unfixed)
    needs_fix = {}
    for m in re.finditer(r"INSERT IGNORE INTO mst_slki .+? VALUES \((\d+), '(L\.\d+)', '\2', ''", sql):
        slki_id = m.group(1)
        kode = m.group(2)
        needs_fix[kode] = slki_id

    print(f"SLKI yang perlu diperbaiki: {len(needs_fix)}")

    # Collect SLKI labels from all pages
    slki_labels = {}  # kode -> label
    seen_kodes = set()

    for i, url in enumerate(DIAGNOSIS_URLS, 1):
        slug = url.rstrip('/').split('/')[-1]
        mapping = fetch_slki_mapping(url)
        new_found = {k: v for k, v in mapping.items() if k in needs_fix and k not in slki_labels}
        if new_found:
            slki_labels.update(new_found)
            for k, v in new_found.items():
                print(f"  [{i:3d}] {k} → {v}")

        # Stop if we found all
        if len(slki_labels) >= len(needs_fix):
            print(f"\nSemua {len(needs_fix)} SLKI label ditemukan di halaman {i}!")
            break

        time.sleep(1.5)

    print(f"\nDitemukan {len(slki_labels)}/{len(needs_fix)} label SLKI")

    # Apply fixes to SQL
    fixed = 0
    for kode, label in slki_labels.items():
        slki_id = needs_fix[kode]
        old = f"INSERT IGNORE INTO mst_slki (id, kode, label, definisi) VALUES ({slki_id}, '{kode}', '{kode}', '');"
        new = f"INSERT IGNORE INTO mst_slki (id, kode, label, definisi) VALUES ({slki_id}, '{kode}', '{label}', '');"
        if old in sql:
            sql = sql.replace(old, new)
            fixed += 1

    # Also fix mst_slki_indikator entries where label comes from indikator text
    # The indikator INSERT uses the luaran ID which matches slki_id

    with open(SQL_FILE, 'w', encoding='utf-8') as f:
        f.write(sql)

    print(f"Berhasil fix {fixed} label SLKI di {SQL_FILE}")

    # Print remaining unfixed
    still_unfixed = [k for k in needs_fix if k not in slki_labels]
    if still_unfixed:
        print(f"Masih belum ter-fix ({len(still_unfixed)}): {still_unfixed[:10]}")

if __name__ == '__main__':
    main()
