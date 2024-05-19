CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    category VARCHAR(255),
    description TEXT,
    stock INT,
    price INT
);

CREATE TABLE transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    amount INT,
    total_item INT,
    product_id INT,
    date DATE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

INSERT INTO products (id, name, category, description, stock, price) VALUES
(6, 'tv-led-4k', 'elektronik', 'Televisi LED dengan resolusi 4K dan teknologi HDR', 25, 8000000),
(7, 'laptop-gaming', 'elektronik', 'Laptop dengan spesifikasi tinggi untuk gaming', 15, 15000000),
(8, 'kamera-slr', 'elektronik', 'Kamera DSLR dengan lensa 24-70mm', 10, 12000000),
(9, 'hp-gaming', 'elektronik', 'Headphone gaming', 30, 1500000),
(10, 'drone-kamera', 'elektronik', 'Drone dengan kamera 4K dan kontrol jarak jauh', 8, 7000000);

INSERT INTO transactions (id, amount, total_item, product_id, date) VALUES
(11, 2, 2, 6, '2023-11-15'),
(12, 1, 1, 7, '2023-12-01'),
(13, 1, 1, 8, '2023-12-10'),
(14, 3, 3, 9, '2024-01-01'),
(15, 1, 1, 10, '2024-01-15'),
(16, 1, 1, 6, '2024-02-01'),
(17, 2, 2, 7, '2024-02-15'),
(18, 1, 1, 8, '2024-03-01'),
(19, 2, 2, 9, '2024-03-15'),
(20, 1, 1, 10, '2024-04-01');