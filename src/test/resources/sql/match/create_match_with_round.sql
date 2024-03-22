INSERT INTO match(id, player_id, correct_answers, score, status, credits)
VALUES (999, 1, 0, 0, 'IN_PROGRESS', 3);

INSERT INTO match_round(id, match_id, round_number, first_movie_imdb, second_movie_imdb, status, answer)
VALUES (999, 999, 1, 'tt0092005', 'tt0325980', 'IN_PROGRESS', null);