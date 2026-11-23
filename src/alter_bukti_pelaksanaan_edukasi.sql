ALTER TABLE bukti_pelaksanaan_informasi_edukasi ADD COLUMN IF NOT EXISTS tandatangan VARCHAR(200) DEFAULT '' AFTER photo;
