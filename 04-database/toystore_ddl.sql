SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;
SET default_tablespace = '';
SET default_table_access_method = heap;
CREATE TABLE public.carts (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone,
    quantity integer NOT NULL,
    toy_id bigint,
    user_id bigint
);
ALTER TABLE public.carts OWNER TO postgres;
CREATE SEQUENCE public.carts_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.carts_id_seq OWNER TO postgres;
ALTER SEQUENCE public.carts_id_seq OWNED BY public.carts.id;
CREATE TABLE public.toys (
    id bigint NOT NULL,
    category character varying(255),
    created_at timestamp(6) without time zone,
    description text,
    image_url character varying(255),
    name character varying(255) NOT NULL,
    price numeric(38,2) NOT NULL,
    stock integer NOT NULL,
    updated_at timestamp(6) without time zone
);


ALTER TABLE public.toys OWNER TO postgres;
CREATE SEQUENCE public.toys_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.toys_id_seq OWNER TO postgres;
ALTER SEQUENCE public.toys_id_seq OWNED BY public.toys.id;
CREATE TABLE public.users (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone,
    password character varying(255) NOT NULL,
    role character varying(255),
    username character varying(255) NOT NULL,
    CONSTRAINT users_role_check CHECK (((role)::text = ANY ((ARRAY['USER'::character varying, 'ADMIN'::character varying])::text[])))
);
ALTER TABLE public.users OWNER TO postgres;
CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE public.users_id_seq OWNER TO postgres;
ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;

ALTER TABLE ONLY public.carts ALTER COLUMN id SET DEFAULT nextval('public.carts_id_seq'::regclass);

ALTER TABLE ONLY public.toys ALTER COLUMN id SET DEFAULT nextval('public.toys_id_seq'::regclass);

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);

ALTER TABLE ONLY public.carts
    ADD CONSTRAINT carts_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.toys
    ADD CONSTRAINT toys_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_r43af9ap4edm43mmtq01oddj6 UNIQUE (username);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.carts
    ADD CONSTRAINT fk1sihf3xvctpwudvruf6m4eca FOREIGN KEY (toy_id) REFERENCES public.toys(id);
ALTER TABLE ONLY public.carts
    ADD CONSTRAINT fkb5o626f86h46m4s7ms6ginnop FOREIGN KEY (user_id) REFERENCES public.users(id);


