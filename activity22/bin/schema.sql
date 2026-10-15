CREATE TABLE IF NOT EXISTS accounts (
    account_number INTEGER PRIMARY KEY,
    account_holder_name TEXT NOT NULL,
    age INTEGER NOT NULL CHECK (age >= 18),
    balance REAL NOT NULL,
    account_type TEXT NOT NULL,
    status TEXT NOT NULL,
    pin INTEGER,
    opening_date TEXT NOT NULL,
    overdraft_used REAL DEFAULT 0,
    interest_earned REAL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id TEXT PRIMARY KEY,
    timestamp TEXT NOT NULL,
    account_number INTEGER NOT NULL,
    type TEXT NOT NULL,
    amount REAL NOT NULL,
    balance_after REAL NOT NULL,
    status TEXT NOT NULL,
    from_account INTEGER,
    to_account INTEGER,
    description TEXT,
    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
);
