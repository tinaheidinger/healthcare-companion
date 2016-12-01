import os
from bottle import run, request, get, post
import dataset
import requests

# database
db = dataset.connect('sqlite:///' + os.path.dirname(os.path.abspath(__file__)) + '/database.db')
table_goals = db['goals']

# use @expect_json decorator if request content type must be
# 'application/json' and body must not be empty. executes function
# if content type is correct, else returns HTTP 415 if content type
# is wrong or HTTP 400 if JSON body is empty.
def expect_json(fn):
    def inner(*args, **kwargs):
        if request.get_header('Content-Type') != 'application/json':
            return http_error(415, 'Wrong content type.')
        if not request.json:
            return http_error(400, 'Empty JSON body')
        return fn()
    return inner

#############################################################################
###### REST API
#############################################################################

@expect_json
@post('/goal')
def post_goal():
    companion = request.json['companion']
    emoji = request.json['emoji']
    text = request.json['text']
    new_goal_id = table_goals.insert(dict(companion = companion, emoji = emoji, text = text, state = False))
    print(new_goal_id)

run(host='localhost', port=8080, debug=False, reloader = True)