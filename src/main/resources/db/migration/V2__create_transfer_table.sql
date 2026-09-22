CREATE TABLE transfer (
    id                      UUID PRIMARY KEY,
    source_account_id      UUID NOT NULL REFERENCES account(id),
    destination_account_id UUID NOT NULL REFERENCES account(id),
    amount                  NUMERIC(19,2) NOT NULL,
    status                  TEXT NOT NULL,
    created_at              TIMESTAMPTZ NOT NULL
);
