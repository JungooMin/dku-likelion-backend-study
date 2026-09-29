# 기존에 a2 데이터베이스가 존재 한다면 삭제
drop database if exists 'as';

# 새 데이터베이스(`a2`) 생성
create database 'a2';

# 새 데이터베이스(`a2`) 선택
use 'a2';

# article 테이블 생성(id, regDate, title, body)
create table ariclte(
	id int,
	regDate DATETIME,
	title VARCHAR(100),
	'body' text
);
 
# article 테이블 조회(*)
select *
from 'artilec';

# article 테이블에 data insert (regDate = NOW(), title = '제목', body = '내용')
insert into article
set regDate = now(),
title = '제목',
'body' = '내용';

# article 테이블에 data insert (regDate = NOW(), title = '제목', body = '내용')
insert into article
set regDate = now(),
title = '제목',
'body' = '내용';

# article 테이블 조회(*)
## id가 NULL인 데이터 생성이 가능하네?
select *
from 'article';

# id 데이터는 꼭 필수 이기 때문에 NULL을 허용하지 않게 바꾼다.(alter table, not null)
## 기존의 NULL값 때문에 실패가 뜬다.
alter table article modify id int not null;
##기존의 null인값들 때문에 오류

# 기존의 NULL값을 0으로 바꾼다.
update article set id = 0;

# NULL을 허용하지 않게 바꾼다.(alter table, not null)
alter table article modify id int not null;

# article 테이블 조회(*)
select *
from 'article';

# 생각해 보니 모든 행(row)의 id 값은 유니크 해야한다.(ADD PRIMARY KEY(id))
## 오류가 난다. 왜냐하면 기존의 데이터 중에서 중복되는게 있기 때문에
alter table article add primary key(id);
##기존의 id때문에 오류발생

# id가 0인 것 중에서 1개를 id 1로 바꾼다.
update article
set id = 1
where id = 0
limit 1;

# article 테이블 조회(*)
select *
from article;

# id가 0인것을 id 2로 바꾼다.
update article
set id = 2
where id = 0;


# 생각해 보니 모든 행(row)의 id 값은 유니크 해야한다.(ADD PRIMARY KEY(id))
## 이제 적용이 잘 된다.
alter table article add primary key(id);

# id 칼럼에 auto_increment 를 건다.--> 자동으로 id+1 수행
## auto_increment 를 걸기전에 해당 칼럼은 무조건 key 여야 한다.
alter table article modif column id int not nul auto_increment;

# article 테이블 구조확인(desc)
desc article;

# 나머지 칼럼 모두에도 not null을 적용해주세요.
alter table article modif column regDate DATETIME NOT NULL;
alter table article modif column title VARCHAR(100) NOT NULL;
alter table article modif column `body` TEXT NOT NULL;

# id 칼럼에 UNSIGNED 속성을 추가하세요.--> 음수금지
alter table article modif column id INT UNSIGNED NOT NULL AUTO_INCREMENT;
desc article;

# 작성자(writer) 칼럼을 title 칼럼 다음에 추가해주세요.
ALTER TABLE article ADD COLUMN writer VARCHAR(100) NOT NULL AFTER title;
# 작성자(writer) 칼럼의 이름을 nickname 으로 변경해주세요.(ALTER TABLE article CHANGE oldName newName TYPE 조건)
ALTER TABLE article CHANGE `writer` `nickname` VARCHAR(100) NOT NULL;
DESC article;

# nickname 칼럼의 위치를 body 밑으로 보내주세요.(MODIFY COLUMN nickname)
ALTER TABLE article MODIFY COLUMN nickname VARCHAR(100) NOT NULL AFTER body;
DESC article;

# hit 조회수 칼럼 추가 한 후 삭제해주세요.
ALTER TABLE article ADD COLUMN hit INT UNSIGNED NOT NULL AFTER nickname;
DESC article;
ALTER TABLE article DROP COLUMN hit;
DESC article;

# hit 조회수 칼럼을 다시 추가
ALTER TABLE article ADD COLUMN hit INT UNSIGNED NOT NULL AFTER nickname;
DESC article;

# 기존의 비어있는 닉네임 채워넣기(무명)
UPDATE article
SET nickname = '무명'
WHERE nickname = '';

# article 테이블에 데이터 추가(regDate = NOW(), title = '제목3', body = '내용3', nickname = '홍길순', hit = 10)
INSERT INTO article
SET regDate = NOW(),
title = '제목3',
`body` = '내용3',
nickname = '홍길순',
hit = 10;

# article 테이블에 데이터 추가(regDate = NOW(), title = '제목4', body = '내용4', nickname = '홍길동', hit = 55)
INSERT INTO article
SET regDate = NOW(),
title = '제목4',
`body` = '내용4',
nickname = '홍길동',
hit = 55;

# article 테이블에 데이터 추가(regDate = NOW(), title = '제목5', body = '내용5', nickname = '홍길동', hit = 10)
INSERT INTO article
SET regDate = NOW(),
title = '제목5',
`body` = '내용5',
nickname = '홍길동',
hit = 10;

# article 테이블에 데이터 추가(regDate = NOW(), title = '제목6', body = '내용6', nickname = '임꺽정', hit = 100)
INSERT INTO article
SET regDate = NOW(),
title = '제목6',
`body` = '내용6',
nickname = '임꺽정',
hit = 100;

# 조회수 가장 많은 게시물 3개 만 보여주세요., 힌트 : ORDER BY, LIMIT
select * from articl order by ht desc limit 3;

# 작성자명이 '홍길'로 시작하는 게시물만 보여주세요., 힌트 : LIKE '홍길%'
select * from article where nickame LIKE '홍길%';

# 조회수가 10 이상 55 이하 인것만 보여주세요., 힌트 : WHERE 조건1 AND 조건2selet * from article
selet * from article WHERE hit >= 10 AND hit <= 55;

# 작성자가 '무명'이 아니고 조회수가 50 이하인 것만 보여주세요., 힌트 : != == <>
selet * from article WHERE nickname != '무명' AND hit <= 50;

# 작성자가 '무명' 이거나 조회수가 55 이상인 게시물을 보여주세요. 힌트 : OR
selet * from article WHERE nickname = '무명' OR hit >= 55;