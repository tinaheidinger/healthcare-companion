import os
from bottle import run, request, response, get, post, put, delete
import dataset
import requests
import json
import datetime

# database
db = dataset.connect('sqlite:///' + os.path.dirname(os.path.abspath(__file__)) + '/database.db')
table_goals = db['goals']
table_reminders = db['reminders']

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

# returns current time
@get('/time')
def get_time():
    response_status = 200
    response.content_type = 'application/json'
    return json.dumps(dict(time=datetime.datetime.now()))

# creates a new goal
@expect_json
@post('/goal')
def post_goal():
    goal = dict()
    goal['companion'] = request.json['companion']
    goal['emoji'] = request.json['emoji']
    goal['text'] = request.json['text']
    goal['active'] = True
    goal['id'] = table_goals.insert(goal)
    response.status = 201
    response.content_type = 'application/json'
    return json.dumps(goal)

# lists all goals for a specified companion
@get('/goals')
def get_goals():
    companion = request.query.companion
    goals = list(table_goals.find(companion=companion))
    response_status = 200
    response.content_type = 'application/json'
    return json.dumps(goals)

# edits a specified goal
@expect_json
@put('/goal/<id:int>')
def edit_goal(id):
    table_goals.update(dict(id=id, text=request.json['text'], emoji=request.json['emoji']), ['id'])
    goal = table_goals.find_one(id=id)
    response_status = 200
    response.content_type = 'application/json'
    return json.dumps(goal)

# deletes a goal
@delete('/goal/<id:int>')
def delete_goal(id):
    table_goals.delete(id=id)
    response_status = 200
    return

# creates a new reminder
@expect_json
@post('/reminder')
def post_goal():
    reminder = dict()
    reminder['companion'] = request.json['companion']
    reminder['emoji'] = request.json['emoji']
    reminder['text'] = request.json['text']
    if 'date' in request.json:
        reminder['date'] = request.json['date']
    if 'weekday' in request.json:
        reminder['weekday'] = request.json['weekday']
    reminder['active'] = True
    reminder['id'] = table_reminders.insert(reminder)
    response.status = 201
    response.content_type = 'application/json'
    return json.dumps(reminder)

# lists all reminders for a specified companion
@get('/reminders')
def get_reminders():
    companion = request.query.companion
    reminders = list(table_reminders.find(companion=companion))
    response_status = 200
    response.content_type = 'application/json'
    return json.dumps(reminders)

# edits a specified reminder
@expect_json
@put('/reminder/<id:int>')
def edit_reminder(id):
    reminder = dict()
    print(request.json)
    reminder['companion'] = request.json['companion']
    reminder['emoji'] = request.json['emoji']
    reminder['text'] = request.json['text']
    if 'date' in request.json:
        reminder['date'] = request.json['date']
    if 'weekday' in request.json:
        reminder['weekday'] = request.json['weekday']
    reminder['id'] = id
    table_reminders.update(reminder, ['id'])
    reminder_from_db = table_reminders.find_one(id=id)
    response_status = 200
    response.content_type = 'application/json'
    return json.dumps(reminder_from_db)

# deletes a reminder
@delete('/reminder/<id:int>')
def delete_reminder(id):
    table_reminders.delete(id=id)
    response_status = 200
    return

# returns fluid statistics
@get('/fluid')
def get_fluid():
    companion = request.query.companion
    period = request.query.period
    fluid_data = []
    fluid_data.append(dict(date='2017-01-18', amount=450))
    fluid_data.append(dict(date='2017-01-17', amount=200))
    fluid_data.append(dict(date='2017-01-16', amount=950))
    fluid = dict(companion=companion, period=period, fluid=fluid_data)
    response.status = 200
    response.content_type = 'application/json'
    return json.dumps(fluid)

# returns steps statistics
@get('/steps')
def get_steps():
    companion = request.query.companion
    period = request.query.period
    steps_data = []
    steps_data.append(dict(date='2017-01-18', amount=9440))
    steps_data.append(dict(date='2017-01-17', amount=8031))
    steps_data.append(dict(date='2017-01-16', amount=11741))
    steps = dict(companion=companion, period=period, steps=steps_data)
    response.status = 200
    response.content_type = 'application/json'
    return json.dumps(steps)


run(host='0.0.0.0', port=8080, debug=False, reloader=True)