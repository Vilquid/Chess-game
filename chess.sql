-- It creates a database called chess, a role called chess, and gives the role all privileges on all tables in the public schema.
CREATE DATABASE chess;
CREATE ROLE chess WITH LOGIN PASSWORD 'chess';
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA "public" TO chess;