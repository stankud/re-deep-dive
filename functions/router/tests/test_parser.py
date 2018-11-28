import json
from functions.router.parsing import EventParser

parser = EventParser()

with open('functions/router/tests/sample-s3-event.json') as json_data:
    e = json.load(json_data)

file = parser.parse(e, 's3')

assert(file.params['user'] == 'skudrow')
assert(file.params['stage'] == 'dropbox')
assert(file.ext == '.pdf')
