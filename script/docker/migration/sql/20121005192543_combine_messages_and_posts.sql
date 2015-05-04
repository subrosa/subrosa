INSERT INTO post SELECT nextval('post_post_id_seq'), p.account_id, null, null,
m.message, null, game_id, 'TEXT', m.created, m.modified
    FROM message m
    JOIN player p using (player_id);

ALTER TABLE comment DROP CONSTRAINT comment_post_id_fkey;
DROP SEQUENCE post_post_id_seq CASCADE;
ALTER TABLE post DROP CONSTRAINT post_pkey;
ALTER TABLE post RENAME TO post_old;

 -- Recreate table

 --
 -- Name: post; Type: TABLE; Schema: public; Owner: engine; Tablespace:
 --

CREATE TABLE post (
     post_id integer NOT NULL,
     post_type character varying(64) NOT NULL,
     content text NOT NULL,
     account_id integer NOT NULL,
     history_id integer,
     accolade_id integer,
     image_id integer,
     game_id integer NOT NULL,
     created timestamp without time zone DEFAULT now() NOT NULL,
     modified timestamp without time zone DEFAULT now() NOT NULL
 );

 ALTER TABLE public.post OWNER TO engine;

 --
 -- Name: post_post_id_seq; Type: SEQUENCE; Schema: public; Owner: engine
 --

 CREATE SEQUENCE post_post_id_seq
     START WITH 1
     INCREMENT BY 1
     NO MAXVALUE
     NO MINVALUE
     CACHE 1;


 ALTER TABLE public.post_post_id_seq OWNER TO engine;

 --
 -- Name: post_post_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: engine
 --

 ALTER SEQUENCE post_post_id_seq OWNED BY post.post_id;


 --
 -- Name: post_id; Type: DEFAULT; Schema: public; Owner: engine
 --

 ALTER TABLE post ALTER COLUMN post_id SET DEFAULT nextval('post_post_id_seq'::regclass);


 --
 -- Name: post_pkey; Type: CONSTRAINT; Schema: public; Owner: engine; Tablespace:
 --

 ALTER TABLE ONLY post
     ADD CONSTRAINT post_pkey PRIMARY KEY (post_id);


 --
 -- Name: post_modified_trigger; Type: TRIGGER; Schema: public; Owner: engine
 --

 CREATE TRIGGER post_modified_trigger
     BEFORE UPDATE ON post
     FOR EACH ROW
     EXECUTE PROCEDURE update_modified_column();


 --
 -- Name: post_accolade_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: engine
 --

 ALTER TABLE ONLY post
     ADD CONSTRAINT post_accolade_id_fkey FOREIGN KEY (accolade_id) REFERENCES accolade(accolade_id);


 --
 -- Name: post_account_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: engine
 --

 ALTER TABLE ONLY post
     ADD CONSTRAINT post_account_id_fkey FOREIGN KEY (account_id) REFERENCES account(account_id);


 --
 -- Name: post_game_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: engine
 --

 ALTER TABLE ONLY post
     ADD CONSTRAINT post_game_id_fkey FOREIGN KEY (game_id) REFERENCES game(game_id);


 --
 -- Name: post_history_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: engine
 --

 ALTER TABLE ONLY post
     ADD CONSTRAINT post_history_id_fkey FOREIGN KEY (history_id) REFERENCES history(history_id);


 --
 -- Name: post_image_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: engine
 --

 ALTER TABLE ONLY post
     ADD CONSTRAINT post_image_id_fkey FOREIGN KEY (image_id) REFERENCES image(image_id);

 --
 -- PostgreSQL database dump complete
 --


INSERT INTO post (content, post_type, game_id, account_id, image_id, history_id, accolade_id, created, modified)
    SELECT content, post_type, game_id, account_id, image_id, history_id, accolade_id, created, modified from post_old
    ORDER BY created;


DROP TABLE post_old;
ALTER TABLE comment ADD CONSTRAINT comment_post_id_fkey FOREIGN KEY (post_id) REFERENCES post (post_id);

DROP TABLE message;