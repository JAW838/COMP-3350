INSERT INTO songs (artist_id, genre_id, title, year, duration)
VALUES
(3, 1, 'Come Together', 1969, 258),
(3, 1, 'Something', 1969, 183),
(3, 2, 'The Long and Winding Road', 1970, 220),
(3, 2, 'Let It Be', 1970, 230),
(4, 1, 'Stairway to Heaven', 1971, 483),
(5, 1, 'Hotel California', 1977, 392),
(6, 1, 'Bohemian Rhapsody', 1975, 356),
(5, 1, 'Take It Easy', 1972, 209),
(5, 1, 'Peaceful Easy Feeling', 1972, 256),
(5, 1, 'Lyin Eyes', 1975, 381),
(5, 1, 'One of These Nights', 1975, 291),
(5, 1, 'Desperado', 1973, 243),
(5, 1, 'Tequila Sunrise', 1973, 239),
(7, 2, 'Cant Help Falling In Love', 1961, 181),
(7, 1, 'Suspicious Minds', 1969, 274),
(7, 1, 'Jailhouse Rock', 1959, 156),
(8, 7, 'Time in a Bottle', 1972, 149),
(9, 3, 'Hymn to Freedom', 1963, 331),
(9, 3, 'I Got It Bad and That Aint Good', 1963, 308),
(10, 2, 'Treasure', 2012, 179)
ON CONFLICT(artist_id, title) DO NOTHING;