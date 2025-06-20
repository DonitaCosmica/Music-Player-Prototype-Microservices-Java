PGDMP  9                    }            music    17.5    17.5 3    u           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                           false            v           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                           false            w           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                           false            x           1262    16388    music    DATABASE     y   CREATE DATABASE music WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Spanish_Mexico.1252';
    DROP DATABASE music;
                     postgres    false                        3079    16394 	   uuid-ossp 	   EXTENSION     ?   CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;
    DROP EXTENSION "uuid-ossp";
                        false            y           0    0    EXTENSION "uuid-ossp"    COMMENT     W   COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';
                             false    2            �            1259    16445    album    TABLE     1  CREATE TABLE public.album (
    album_id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    title character varying(128) NOT NULL,
    release_date date NOT NULL,
    cover_art_url character varying(512),
    artist_id uuid,
    total_tracks integer NOT NULL,
    record_label character varying(255)
);
    DROP TABLE public.album;
       public         heap r       postgres    false    2            �            1259    16419    artist    TABLE     �   CREATE TABLE public.artist (
    artist_id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    name character varying(128) NOT NULL,
    bio text,
    country_id uuid,
    image_url character varying(512)
);
    DROP TABLE public.artist;
       public         heap r       postgres    false    2            �            1259    16405    country    TABLE     �   CREATE TABLE public.country (
    country_id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    name character varying(64) NOT NULL,
    iso_code2 character varying(2) NOT NULL,
    iso_code3 character varying(3)
);
    DROP TABLE public.country;
       public         heap r       postgres    false    2            �            1259    16434    genre    TABLE     �   CREATE TABLE public.genre (
    genre_id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    name character varying(32) NOT NULL,
    description text
);
    DROP TABLE public.genre;
       public         heap r       postgres    false    2            �            1259    16460    song    TABLE     �  CREATE TABLE public.song (
    song_id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    title character varying(128) NOT NULL,
    duration_seconds integer NOT NULL,
    audio_file_path character varying(512) NOT NULL,
    is_explicit boolean DEFAULT false,
    play_count bigint DEFAULT 0,
    album_id uuid,
    CONSTRAINT song_duration_seconds_check CHECK ((duration_seconds > 0))
);
    DROP TABLE public.song;
       public         heap r       postgres    false    2            �            1259    16478    song_artist    TABLE     �   CREATE TABLE public.song_artist (
    song_id uuid NOT NULL,
    artist_id uuid NOT NULL,
    is_primary boolean DEFAULT false
);
    DROP TABLE public.song_artist;
       public         heap r       postgres    false            �            1259    16496 
   song_genre    TABLE     Z   CREATE TABLE public.song_genre (
    song_id uuid NOT NULL,
    genre_id uuid NOT NULL
);
    DROP TABLE public.song_genre;
       public         heap r       postgres    false            o          0    16445    album 
   TABLE DATA           t   COPY public.album (album_id, title, release_date, cover_art_url, artist_id, total_tracks, record_label) FROM stdin;
    public               postgres    false    221   <       m          0    16419    artist 
   TABLE DATA           M   COPY public.artist (artist_id, name, bio, country_id, image_url) FROM stdin;
    public               postgres    false    219   �<       l          0    16405    country 
   TABLE DATA           I   COPY public.country (country_id, name, iso_code2, iso_code3) FROM stdin;
    public               postgres    false    218   G>       n          0    16434    genre 
   TABLE DATA           <   COPY public.genre (genre_id, name, description) FROM stdin;
    public               postgres    false    220   �>       p          0    16460    song 
   TABLE DATA           t   COPY public.song (song_id, title, duration_seconds, audio_file_path, is_explicit, play_count, album_id) FROM stdin;
    public               postgres    false    222   @       q          0    16478    song_artist 
   TABLE DATA           E   COPY public.song_artist (song_id, artist_id, is_primary) FROM stdin;
    public               postgres    false    223   �@       r          0    16496 
   song_genre 
   TABLE DATA           7   COPY public.song_genre (song_id, genre_id) FROM stdin;
    public               postgres    false    224   jA       �           2606    16452    album album_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.album
    ADD CONSTRAINT album_pkey PRIMARY KEY (album_id);
 :   ALTER TABLE ONLY public.album DROP CONSTRAINT album_pkey;
       public                 postgres    false    221            �           2606    16426    artist artist_pkey 
   CONSTRAINT     W   ALTER TABLE ONLY public.artist
    ADD CONSTRAINT artist_pkey PRIMARY KEY (artist_id);
 <   ALTER TABLE ONLY public.artist DROP CONSTRAINT artist_pkey;
       public                 postgres    false    219            �           2606    16414    country country_iso_code2_key 
   CONSTRAINT     ]   ALTER TABLE ONLY public.country
    ADD CONSTRAINT country_iso_code2_key UNIQUE (iso_code2);
 G   ALTER TABLE ONLY public.country DROP CONSTRAINT country_iso_code2_key;
       public                 postgres    false    218            �           2606    16416    country country_iso_code3_key 
   CONSTRAINT     ]   ALTER TABLE ONLY public.country
    ADD CONSTRAINT country_iso_code3_key UNIQUE (iso_code3);
 G   ALTER TABLE ONLY public.country DROP CONSTRAINT country_iso_code3_key;
       public                 postgres    false    218            �           2606    16412    country country_name_key 
   CONSTRAINT     S   ALTER TABLE ONLY public.country
    ADD CONSTRAINT country_name_key UNIQUE (name);
 B   ALTER TABLE ONLY public.country DROP CONSTRAINT country_name_key;
       public                 postgres    false    218            �           2606    16410    country country_pkey 
   CONSTRAINT     Z   ALTER TABLE ONLY public.country
    ADD CONSTRAINT country_pkey PRIMARY KEY (country_id);
 >   ALTER TABLE ONLY public.country DROP CONSTRAINT country_pkey;
       public                 postgres    false    218            �           2606    16443    genre genre_name_key 
   CONSTRAINT     O   ALTER TABLE ONLY public.genre
    ADD CONSTRAINT genre_name_key UNIQUE (name);
 >   ALTER TABLE ONLY public.genre DROP CONSTRAINT genre_name_key;
       public                 postgres    false    220            �           2606    16441    genre genre_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.genre
    ADD CONSTRAINT genre_pkey PRIMARY KEY (genre_id);
 :   ALTER TABLE ONLY public.genre DROP CONSTRAINT genre_pkey;
       public                 postgres    false    220            �           2606    16483    song_artist song_artist_pkey 
   CONSTRAINT     j   ALTER TABLE ONLY public.song_artist
    ADD CONSTRAINT song_artist_pkey PRIMARY KEY (song_id, artist_id);
 F   ALTER TABLE ONLY public.song_artist DROP CONSTRAINT song_artist_pkey;
       public                 postgres    false    223    223            �           2606    16500    song_genre song_genre_pkey 
   CONSTRAINT     g   ALTER TABLE ONLY public.song_genre
    ADD CONSTRAINT song_genre_pkey PRIMARY KEY (song_id, genre_id);
 D   ALTER TABLE ONLY public.song_genre DROP CONSTRAINT song_genre_pkey;
       public                 postgres    false    224    224            �           2606    16470    song song_pkey 
   CONSTRAINT     Q   ALTER TABLE ONLY public.song
    ADD CONSTRAINT song_pkey PRIMARY KEY (song_id);
 8   ALTER TABLE ONLY public.song DROP CONSTRAINT song_pkey;
       public                 postgres    false    222            �           1259    16459    idx_album_artist_id    INDEX     J   CREATE INDEX idx_album_artist_id ON public.album USING btree (artist_id);
 '   DROP INDEX public.idx_album_artist_id;
       public                 postgres    false    221            �           1259    16458    idx_album_title    INDEX     B   CREATE INDEX idx_album_title ON public.album USING btree (title);
 #   DROP INDEX public.idx_album_title;
       public                 postgres    false    221            �           1259    16433    idx_artist_country_id    INDEX     N   CREATE INDEX idx_artist_country_id ON public.artist USING btree (country_id);
 )   DROP INDEX public.idx_artist_country_id;
       public                 postgres    false    219            �           1259    16432    idx_artist_name    INDEX     B   CREATE INDEX idx_artist_name ON public.artist USING btree (name);
 #   DROP INDEX public.idx_artist_name;
       public                 postgres    false    219            �           1259    16418    idx_country_iso2    INDEX     I   CREATE INDEX idx_country_iso2 ON public.country USING btree (iso_code2);
 $   DROP INDEX public.idx_country_iso2;
       public                 postgres    false    218            �           1259    16417    idx_country_name    INDEX     D   CREATE INDEX idx_country_name ON public.country USING btree (name);
 $   DROP INDEX public.idx_country_name;
       public                 postgres    false    218            �           1259    16444    idx_genre_name    INDEX     @   CREATE INDEX idx_genre_name ON public.genre USING btree (name);
 "   DROP INDEX public.idx_genre_name;
       public                 postgres    false    220            �           1259    16476    idx_song_album_id    INDEX     F   CREATE INDEX idx_song_album_id ON public.song USING btree (album_id);
 %   DROP INDEX public.idx_song_album_id;
       public                 postgres    false    222            �           1259    16495    idx_song_artist_artist_id    INDEX     V   CREATE INDEX idx_song_artist_artist_id ON public.song_artist USING btree (artist_id);
 -   DROP INDEX public.idx_song_artist_artist_id;
       public                 postgres    false    223            �           1259    16494    idx_song_artist_song_id    INDEX     R   CREATE INDEX idx_song_artist_song_id ON public.song_artist USING btree (song_id);
 +   DROP INDEX public.idx_song_artist_song_id;
       public                 postgres    false    223            �           1259    16512    idx_song_genre_genre_id    INDEX     R   CREATE INDEX idx_song_genre_genre_id ON public.song_genre USING btree (genre_id);
 +   DROP INDEX public.idx_song_genre_genre_id;
       public                 postgres    false    224            �           1259    16511    idx_song_genre_song_id    INDEX     P   CREATE INDEX idx_song_genre_song_id ON public.song_genre USING btree (song_id);
 *   DROP INDEX public.idx_song_genre_song_id;
       public                 postgres    false    224            �           1259    16477    idx_song_title    INDEX     @   CREATE INDEX idx_song_title ON public.song USING btree (title);
 "   DROP INDEX public.idx_song_title;
       public                 postgres    false    222            �           2606    16453    album album_artist_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.album
    ADD CONSTRAINT album_artist_id_fkey FOREIGN KEY (artist_id) REFERENCES public.artist(artist_id) ON DELETE SET NULL;
 D   ALTER TABLE ONLY public.album DROP CONSTRAINT album_artist_id_fkey;
       public               postgres    false    221    4796    219            �           2606    16427    artist artist_country_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.artist
    ADD CONSTRAINT artist_country_id_fkey FOREIGN KEY (country_id) REFERENCES public.country(country_id) ON DELETE SET NULL;
 G   ALTER TABLE ONLY public.artist DROP CONSTRAINT artist_country_id_fkey;
       public               postgres    false    218    219    4792            �           2606    16471    song song_album_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.song
    ADD CONSTRAINT song_album_id_fkey FOREIGN KEY (album_id) REFERENCES public.album(album_id) ON DELETE SET NULL;
 A   ALTER TABLE ONLY public.song DROP CONSTRAINT song_album_id_fkey;
       public               postgres    false    4805    221    222            �           2606    16489 &   song_artist song_artist_artist_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.song_artist
    ADD CONSTRAINT song_artist_artist_id_fkey FOREIGN KEY (artist_id) REFERENCES public.artist(artist_id) ON DELETE CASCADE;
 P   ALTER TABLE ONLY public.song_artist DROP CONSTRAINT song_artist_artist_id_fkey;
       public               postgres    false    223    219    4796            �           2606    16484 $   song_artist song_artist_song_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.song_artist
    ADD CONSTRAINT song_artist_song_id_fkey FOREIGN KEY (song_id) REFERENCES public.song(song_id) ON DELETE CASCADE;
 N   ALTER TABLE ONLY public.song_artist DROP CONSTRAINT song_artist_song_id_fkey;
       public               postgres    false    222    223    4811            �           2606    16506 #   song_genre song_genre_genre_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.song_genre
    ADD CONSTRAINT song_genre_genre_id_fkey FOREIGN KEY (genre_id) REFERENCES public.genre(genre_id) ON DELETE CASCADE;
 M   ALTER TABLE ONLY public.song_genre DROP CONSTRAINT song_genre_genre_id_fkey;
       public               postgres    false    224    220    4802            �           2606    16501 "   song_genre song_genre_song_id_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.song_genre
    ADD CONSTRAINT song_genre_song_id_fkey FOREIGN KEY (song_id) REFERENCES public.song(song_id) ON DELETE CASCADE;
 L   ALTER TABLE ONLY public.song_genre DROP CONSTRAINT song_genre_song_id_fkey;
       public               postgres    false    224    4811    222            o   �   x�ͱR�0���y�lL�,G�啅�0t�bK2��%=J�x{B���R�i�H�CA�!�w���;m����m�p�3��7<�	��.��I�!��� 0+�	�6R��ٮ{[f�&ۧ�S��'��R��x��̽Fob����e���~�����;Nw�j� Rs�c��(�2a�)�˪v�Ug[�l�<���F      m   E  x�u�=��0�g�Wp�ٖ��R���-W��܅��DM"����)W��p7��!Т�I	�i�~���䄵;+�('�W��1}k���q�GNn����cZ#�9y.%����`���B���n�8Ϝ7PB<�kEBI��pfR�k�U�n��֨AX��c��oN�2��}��k��x���]G��ߧ��%;+G1�*����i��i�tvtͯT����9��"�t������ه���+�@���m 3�
�@w�W{�%UqFcz1h7�SHZ�$h"2�a���M����wL/Ρ'g=ɭg��˽�����O׶�3@֝E      l   �   x����0 ��+���G��R!1tq[b��~~s:馋E8-CȐBU�T�4�6"T3�A��4v߶�5N�AJ^i�Prn�$'/l��|�e3˼Yv	+��+�//�P<g�̌�DE3_7��z���c�Ow�ߧ���N,�      n     x�E��N�0���)�{�o�n�@B�Ҭ�k�ɗ+��I8�N73ߎA��c�!��!el��p��5iux\��p9OU���E�W�TW�ӗ�.���y������zP�I
9V��&�![!�VZF$��~�#u��~P1U��E #���[ SL�K��N��us�ywJ_4�5���#c�0��}�b��	�-exZ�z�Ew9�ͫ(�=9;��F�$C�Q-����3O��/v�kN>[�b���E7�\J��XL��臻�Mt��uW�-gR���7�5w      p   �   x����n�0 g�+���Del�vk7-�D⸈��kE�.�n8N�[.B,1BC!�.J-$��]�����&�gg.�Z?7yn�n�m������}�q�������|�ƚDIQ4 :tcBh��Ϥ�V�L���	�y�ȹ[?�f�1�۳�e3��O~ۿ��S�$	R���BqV�x�y�2��4M��RB      q   t   x���C!������@P�%~�_B2����Ǡ�rDچv;s�?�uӒ��7d)#(dB�.~�s܃��B|�傕��5��~"�=Ay�!��2Xv�UH���g���X(�      r   �   x���ˍ 1��N/�>z�迄��l��r��T7*vfHi��=�t0~�|�����8�ip�z��BO|��N�P�r��"�ܪ0i��k�G�B(Pa�B`>1�pyO��<��r�H4����D[�y��I�%��F�9Y�Y$&F�:�IK��CP���9��b�K������y��Ss.     