CREATE table books (
	isbn 		LONG 			NOT NULL PRIMARY KEY,
	title 		varchar(100)	NOT NULL,
	author 		varchar(50)		NOT NULL,
	category 	varchar(50),
	price 		double,
	inventoryQuantity 	INT,
	purchaseQuantity 	INT,
	image 		varchar(100)
);

CREATE table reviews (
	id 			LONG			NOT NULL PRIMARY KEY AUTO_INCREMENT,
	isbn 		LONG 			NOT NULL,
	username	VARCHAR(50),
	text		VARCHAR(1024)	NOT NULL UNIQUE
);

ALTER table reviews
	ADD CONSTRAINT book_review_fk
	FOREIGN KEY (isbn)
	REFERENCES books (isbn);



