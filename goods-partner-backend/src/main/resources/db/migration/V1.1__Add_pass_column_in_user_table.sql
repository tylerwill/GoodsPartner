ALTER TABLE public.users ADD COLUMN login varchar(70);
ALTER TABLE public.users ADD COLUMN password varchar(70) DEFAULT '$2a$12$tS3dgOCF3cSdBDbgoQZVqO5QWsYzz6bFUZxLoNKOqtH3MwSAamXW2';
