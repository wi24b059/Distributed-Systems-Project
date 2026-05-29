CREATE TABLE usage_data (
                            usage_hour TIMESTAMP PRIMARY KEY,
                            community_produced DOUBLE PRECISION NOT NULL DEFAULT 0,
                            community_used DOUBLE PRECISION NOT NULL DEFAULT 0,
                            grid_used DOUBLE PRECISION NOT NULL DEFAULT 0
);

CREATE TABLE current_percentage (
                                    usage_hour TIMESTAMP PRIMARY KEY,
                                    community_depleted DOUBLE PRECISION NOT NULL DEFAULT 0,
                                    grid_portion DOUBLE PRECISION NOT NULL DEFAULT 0
);