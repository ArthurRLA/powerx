CREATE TABLE passage (
    id BIGSERIAL PRIMARY KEY,
    reference_date DATE NOT NULL,
    customer_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    user_id BIGINT,
    passage_number INTEGER NOT NULL,
    CONSTRAINT fk_passage_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
    CONSTRAINT fk_passage_employee FOREIGN KEY (employee_id) REFERENCES employee(id),
    CONSTRAINT fk_passage_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_passage_number ON passage(passage_number);
CREATE INDEX idx_passage_customer ON passage(customer_id);
CREATE INDEX idx_passage_date ON passage(reference_date);
CREATE INDEX idx_passage_user ON passage(user_id);
