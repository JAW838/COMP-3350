INSERT INTO collections (type, artist_id, title, year, liked)
VALUES 
('ALBUM', 3, 'Abbey Road', 1969, 0),
('ALBUM', 4, 'IV', 1971, 0),
('ALBUM', 3, 'Let It Be', 1970, 0),
('ALBUM', 5, 'Hotel California', 1977, 0),
('ALBUM', 5, 'Eagles', 1972, 0),
('ALBUM', 5, 'One of These Nights', 1975, 0),
('ALBUM', 5, 'Desperado', 1973, 0),
('ALBUM', 5, 'Their Greatest Hits (1971-1975)', 1976, 0),
('ALBUM', 9, 'Night Train', 1963, 0),
('PLAYLIST', 1, 'Oldies', 2025, 1)
ON CONFLICT(type, artist_id, title) DO NOTHING;