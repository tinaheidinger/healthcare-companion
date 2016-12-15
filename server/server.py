import os
from bottle import run, request, response, get, post
import dataset
import requests
import json

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
    goal = dict()
    goal['companion'] = request.json['companion']
    goal['emoji'] = request.json['emoji']
    goal['text'] = request.json['text']
    goal['active'] = True
    goal['id'] = table_goals.insert(goal)
    response.status = 200
    response.content_type = 'application/json'
    return json.dumps(goal)

@get('/goals')
def get_goals():
    companion = request.query.companion
    goals = list(table_goals.find(companion=companion))
    response_status = 200
    response.content_type = 'application/json'
    return json.dumps(goals)

run(host='localhost', port=8080, debug=False, reloader=True)