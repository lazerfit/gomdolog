INSERT INTO category (title) values ( 'spring' ),( 'css' ),('Spring');
INSERT INTO post (category_id, created_date, modified_date, views, thumbnail, title, content, is_deleted) values
(1,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP() ,  0, 'Default Thumbnail', '제목', '<p>내용</p>', false  ),
(2,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP() ,  1, 'Default Thumbnail', '제목2', '<p>내용2</p>', false  ),
(2,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP() , 2, 'Default Thumbnail', '제목3', '<p>내용3</p>', true  );
INSERT INTO tag (name) values ('spring' ), ('tag!');
INSERT INTO post_tag (post_id, tag_id) values ( 1, 1 ), (2,2);
