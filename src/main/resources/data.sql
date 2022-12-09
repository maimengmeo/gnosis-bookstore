INSERT INTO books (isbn, title, author, category, price, inventoryQuantity, image)
VALUES (9780385660075, 'The Kite Runner', 'Khaled Hosseini', 'Fiction',	23.0, 80, 'image/TheKiteRunner.jpg'),
		(9780399592553, 'Atlas Of the Heart', 'Brene Brown', 'Non-Fiction', 20.99, 65, 'image/AtlasOfTheHeart.jpg'),
		(9780553380163, 'A Brief History of Time', 'Stephan Hawking', 'Science', 19.80, 50, 'image/ABriefHistoryOfTime.jpeg'),
		(9780393609394, 'Astrophysics For People In A Hurry', 'Neil Degrasse Tyson', 'Science', 24.95, 2, 'image/AstrophysicsForPeopleInAHurry.jpeg'),
		(9780063213210, 'The Song Of Achilles', 'Madeline Miller', 'History', 21.99, 0, 'image/TheSongOfAchilles.jpeg'),
		(9780735219106, 'Where The Crawdads Sing', 'Delia Owens', 'Fiction', 17.30, 150, 'image/WhereTheCrawdadSing.jpeg')
		;
		
		
INSERT INTO reviews (username, text, isbn)
VALUES ('coolcat', 'A wonderfully brave story about childhood, betrayal and forgiveness, 
		this book will change the way you look at the world.',9780385660075),
		('badbunny', 'An amazing first novel - set in Afghanistan, 
		it explores the differences between classes in the society.', 9780385660075),
		('tweetybird', 'This book by Stephen Hawking was excellent becasue he explained each layer of the theories very well. 
		The pictures and diagrams explained the idea of the theories.', 9780553380163)