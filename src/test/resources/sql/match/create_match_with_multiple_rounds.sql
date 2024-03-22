INSERT INTO match(id, player_id, correct_answers, score, status, credits)
VALUES (999, 1, 3, 0, 'IN_PROGRESS', 1);

INSERT INTO match_round(id, match_id, round_number, first_movie_imdb, second_movie_imdb, status, answer)
VALUES (995, 999, 1, 'tt0074958', 'tt0032138', 'FINISHED', 'CORRECT');
INSERT INTO match_round(id, match_id, round_number, first_movie_imdb, second_movie_imdb, status, answer)
VALUES (996, 999, 2, 'tt1028532', 'tt0476735', 'FINISHED', 'CORRECT');
INSERT INTO match_round(id, match_id, round_number, first_movie_imdb, second_movie_imdb, status, answer)
VALUES (997, 999, 3, 'tt0758758', 'tt4016934', 'FINISHED', 'CORRECT');
INSERT INTO match_round(id, match_id, round_number, first_movie_imdb, second_movie_imdb, status, answer)
VALUES (998, 999, 4, 'tt0070047', 'tt15097216', 'FINISHED', 'WRONG');
INSERT INTO match_round(id, match_id, round_number, first_movie_imdb, second_movie_imdb, status, answer)
VALUES (999, 999, 5, 'tt0381681', 'tt0113247', 'FINISHED', 'WRONG');
INSERT INTO match_round(id, match_id, round_number, first_movie_imdb, second_movie_imdb, status, answer)
VALUES (1000, 999, 6, 'tt0092005', 'tt0325980', 'IN_PROGRESS', null);