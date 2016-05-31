from pymongo import MongoClient

client = MongoClient()
db = client.imdb

moviesCollection = db.movies
moviesGenresCollection = db.movies_genres
genresCollection = db.genres

# First get all movies
progress = 0

for movie in moviesCollection.find({}):
    genres = []

    for movie_genre in moviesGenresCollection.find({'idmovies': movie['idmovies']}):
        genre = genresCollection.find_one({'idgenres': movie_genre['idgenres']})['genre']
        genres.append(genre.lower())

    moviesCollection.update({'idmovies': movie['idmovies']}, {'$set': {'genres': genres}})

    progress += 1

    if progress % 100 == 0:
        print(str(progress / 1356171.0 * 100.0) + '%')