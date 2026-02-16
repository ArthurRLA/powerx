ALTER TABLE incentive ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

CREATE INDEX idx_incentive_status ON incentive(status);