CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================
-- REPORT TABLES
-- ============================================

-- Report table
CREATE TABLE report (
                        report_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

                        reporter_user_id UUID NOT NULL,
                        reported_user_id UUID,

                        reported_entity_type VARCHAR(50) NOT NULL,  -- Changed from 20 to 50
                        reported_entity_id BIGINT NOT NULL,         -- Changed from UUID to BIGINT (matches entity)

                        report_type VARCHAR(50) NOT NULL,
                        priority_level VARCHAR(50) NOT NULL,        -- Changed from 10 to 50
                        status VARCHAR(50) NOT NULL,                -- Changed from 20 to 50

                        title VARCHAR(500) NOT NULL,                -- Changed from 255 to 500
                        description TEXT NOT NULL,

                        assigned_admin_id UUID,
                        resolution_notes TEXT,

                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP,
                        resolved_at TIMESTAMP,

                        ai_severity_score DECIMAL(5,4),             -- Changed from (5,2) to (5,4)
                        ai_verified BOOLEAN NOT NULL DEFAULT FALSE
);

-- Report Evidence
CREATE TABLE report_evidence (
                                 evidence_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

                                 report_id UUID NOT NULL
                                     REFERENCES report(report_id)
                                         ON DELETE CASCADE,

                                 evidence_type VARCHAR(50) NOT NULL,         -- Changed from 30 to 50

                                 file_url VARCHAR(2048) NOT NULL,            -- Changed from 500 to 2048
                                 file_size BIGINT NOT NULL,
                                 mime_type VARCHAR(255) NOT NULL,            -- Changed from 100 to 255

                                 description TEXT,
                                 metadata TEXT,

                                 uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 is_verified BOOLEAN NOT NULL DEFAULT FALSE,
                                 verification_notes TEXT
);

-- Report Audit
CREATE TABLE report_audit (
                              report_audit_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

                              report_id UUID NOT NULL
                                  REFERENCES report(report_id)
                                      ON DELETE CASCADE,

                              admin_user_id UUID,

                              action_type VARCHAR(50) NOT NULL,

                              previous_state VARCHAR(50),
                              new_state VARCHAR(50),

                              notes TEXT,

                              is_automated BOOLEAN NOT NULL DEFAULT FALSE,

                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
