-- user
CREATE TABLE user
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    userId     VARCHAR(20)  NOT NULL,
    password   VARCHAR(100) NOT NULL,
    nickname   VARCHAR(45)  NOT NULL,
    isWithdraw TINYINT  DEFAULT 0,
    isAdmin    TINYINT  DEFAULT 0,
    createdAt  DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- category
CREATE TABLE category
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(45) NOT NULL
);

-- post
CREATE TABLE post
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(45) NOT NULL,
    isAdmin    TINYINT  DEFAULT 0,
    contents   VARCHAR(500),
    views      INT      DEFAULT 0,
    userId     INT,
    categoryId INT,
    fileId     INT,
    createdAt  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updatedAt  DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_post_category FOREIGN KEY (categoryId) REFERENCES category (id),
    CONSTRAINT fk_post_user FOREIGN KEY (userId) REFERENCES user (id)
);

-- tag
CREATE TABLE tag
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(45) NOT NULL,
    url  VARCHAR(45)
);

-- postTag
CREATE TABLE postTag
(
    id     INT AUTO_INCREMENT PRIMARY KEY,
    postId INT NOT NULL,
    tagId  INT NOT NULL,

    CONSTRAINT fk_postTag_post FOREIGN KEY (postId) REFERENCES post (id),
    CONSTRAINT fk_postTag_tag FOREIGN KEY (tagId) REFERENCES tag (id)
);

-- file
CREATE TABLE file
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    path      VARCHAR(100) NOT NULL,
    name      VARCHAR(45)  NOT NULL,
    extension VARCHAR(45),
    postId    INT,

    CONSTRAINT fk_file_post FOREIGN KEY (postId) REFERENCES post (id)
);

-- comment
CREATE TABLE comment
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    postId       INT          NOT NULL,
    contents     VARCHAR(300) NOT NULL,
    subCommentId INT,

    CONSTRAINT fk_comment_post FOREIGN KEY (postId) REFERENCES post (id),
    CONSTRAINT fk_comment_parent FOREIGN KEY (subCommentId) REFERENCES comment (id)
);
