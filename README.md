Running the Application

./mvnw spring-boot:run

Core & Concurrency (P1–P2): Built REST endpoints for darshan slots and bookings, backed by pessimistic locking to prevent ticket over-allocation under heavy concurrent traffic.

Safe Retries & Idempotency (P3): Implemented idempotency keys with unique database constraints to safely handle network retries without duplicate bookings.

Cancellations (P4): Added time-validated booking cancellations that automatically restore inventory back to the slot pool.

Receipt Tracking (P5): Automated the generation of unique, human-readable receipt numbers (e.g., TDB-XXXXXX) with dedicated lookup endpoints for gate verification.