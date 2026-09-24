ALTER TABLE transfer
    ADD CONSTRAINT transfer_status_valid CHECK (status IN ('COMPLETED', 'REVERSED'));
