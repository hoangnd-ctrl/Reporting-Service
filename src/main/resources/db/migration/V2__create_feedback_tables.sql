CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================
-- FEEDBACK TABLES
-- ============================================

-- Feedback table
CREATE TABLE feedback (
                          feedback_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

                          listing_id UUID NOT NULL,
                          seller_user_id UUID NOT NULL,

                          check_type VARCHAR(50),                     -- Added - can be NULL in entity
                          status VARCHAR(50) NOT NULL,                -- Changed from 'overall_status' to 'status'

                          ai_confidence_score DECIMAL(5,4),           -- Changed from (5,2) to (5,4)

                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          reviewed_by_staff_id UUID,

                          is_resubmission BOOLEAN NOT NULL DEFAULT FALSE,

                          previous_feedback_id UUID
                              REFERENCES feedback(feedback_id)        -- Changed from feedback_report to feedback
);

-- Feedback Item
CREATE TABLE feedback_item (
                               feedback_item_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),  -- Changed from BIGSERIAL to UUID

                               feedback_id UUID NOT NULL                   -- Changed from feedback_report_id to feedback_id
                                   REFERENCES feedback(feedback_id)        -- Changed reference to feedback
                                       ON DELETE CASCADE,

                               category VARCHAR(50) NOT NULL,
                               severity VARCHAR(50) NOT NULL,              -- Changed from 20 to 50

                               target_attribute VARCHAR(255) NOT NULL,     -- Changed from 100 to 255, added NOT NULL

                               error_message TEXT NOT NULL,
                               suggestion TEXT,

                               detected_by VARCHAR(50) NOT NULL,           -- Changed from 10 to 50

                               is_fixed BOOLEAN NOT NULL DEFAULT FALSE,

                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Feedback Audit
CREATE TABLE feedback_audit (
                                feedback_audit_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),  -- Changed from BIGSERIAL to UUID

                                feedback_id UUID NOT NULL                   -- Changed from feedback_report_id to feedback_id
                                    REFERENCES feedback(feedback_id)        -- Changed reference to feedback
                                        ON DELETE CASCADE,

                                admin_user_id UUID,

                                action_type VARCHAR(50) NOT NULL,

                                previous_state VARCHAR(50) NOT NULL,        -- Added NOT NULL
                                new_state VARCHAR(50) NOT NULL,             -- Added NOT NULL

                                notes TEXT,

                                is_automated BOOLEAN NOT NULL DEFAULT FALSE,

                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);