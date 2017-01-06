-- Create the role that will own the template database
CREATE ROLE tenant WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB LOGIN NOREPLICATION;

-- Create the template database and connect to it --
CREATE DATABASE template_tenant ENCODING = 'UTF8' OWNER = tenant IS_TEMPLATE = true CONNECTION LIMIT = 0;
REVOKE CONNECT, TEMPORARY ON DATABASE template_tenant FROM PUBLIC;