INSERT INTO users(username,password_hash,role,email)
VALUES
('system_admin','securepass1','Admin','admin@library.com'),
('librarian_desk','securepass2','Librarian','desk@library.com'),
('student_user','securepass3','Member','student@university.edu');


INSERT INTO books(title,author,isbn,category,total_copies,available_copies)
VALUES
('Introduction to Java Programming',
'Y. Daniel Liang',
'978-0134670942',
'Textbooks',
3,
2),

('Design Patterns',
'Erich Gamma',
'978-0201633610',
'Software Architecture',
2,
2);


INSERT INTO transactions(user_id,book_id,borrow_date,due_date,return_date)
VALUES
(3,1,'2026-06-01','2026-06-15',NULL);


INSERT INTO fines(transaction_id,fine_amount,is_paid)
VALUES
(1,5.00,0);