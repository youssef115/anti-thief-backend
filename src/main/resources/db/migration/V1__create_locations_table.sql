CREATE TABLE IF NOT EXISTS locations (
    id BIGSERIAL PRIMARY KEY,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    timestamp BIGINT NOT NULL,
    device_id VARCHAR(64) NOT NULL
);

CREATE INDEX idx_locations_timestamp ON locations(timestamp);
CREATE INDEX idx_locations_device_id ON locations(device_id);
