# healthcare-companion

This is a project for the course 'Project-oriented research and design methods' at Vienna University Of Technology. It cosists of a personal healthcare companion gadget and its base station.

## Companion

tbd

## Base Station

tbd

## Server

The server is a Python bottle application containing a REST interface.

### Installation & Setup

The server requires Python 2 (implemented and tested with 2.7.10). 

* Install pip (https://pip.pypa.io/en/stable/installing/)
* Optional: install, set up and activate a virtual environment (http://docs.python-guide.org/en/latest/dev/virtualenvs/)
* Install dependencies: `pip install -r server/requirements.txt`
* Run the server with `python server/server.py`

### REST API documentation

#### GET /goals
##### Request
* Parameter: `companion` containing the companion ID

##### Response
* Content-Type: application/json 
```
[
  {
    "id": 1,
    "active": true,
    "text": "daily fruit",
    "emoji": ":apple:",
    "companion": "buddy"
  }
]
```



#### POST /goal
##### Request
* Content-Type: application/json 
```
{
	"companion": <Companion name>,
	"emoji": <String representation of Emoji>,
	"text": <Goal text>
}
```

##### Response
* Content-Type: application/json 
```
{
	"id": 11,
	"companion": <Companion name>,
	"emoji": <String representation of Emoji>,
	"text": <Goal text>
	"active": true,
}
```


#### PUT /goal/<id>
##### Request
* Content-Type: application/json 
```
{
	"active": <boolean>
}
```

##### Response
* Content-Type: application/json 
```
{
	"id": <Companion ID>,
	"companion": <Companion name>,
	"emoji": <String representation of Emoji>,
	"text": <Goal text>
	"active": <boolean>,
}
```