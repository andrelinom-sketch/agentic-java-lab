ALTER TABLE account
    ADD CONSTRAINT account_balance_non_negative CHECK (balance >= 0);
