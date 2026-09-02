-- =====================================================================
-- One-time migration for an EXISTING sunrise_dental_clinic database
-- (one that was set up before contact_number became UNIQUE in
-- schema.sql). Run this once against your live database -- it does NOT
-- drop or recreate anything, so your existing appointments/patients are
-- kept.
--
-- If you are setting the database up fresh instead, just run the
-- updated schema.sql -- it already includes this constraint and you do
-- not need this file at all.
--
-- Usage:
--   mysql -u root sunrise_dental_clinic < database/migration_patient_uniqueness.sql
-- =====================================================================

USE sunrise_dental_clinic;

ALTER TABLE patient
    ADD CONSTRAINT uq_patient_contact UNIQUE (contact_number);

-- Running this a second time will fail with "Duplicate key name
-- 'uq_patient_contact'" -- that error just means the migration was
-- already applied; it is safe to ignore.
