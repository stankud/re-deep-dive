from os import path

class S3File:
    def __init__(self, bucket, key, ext, params):
        self.bucket = bucket
        self.key = key
        self.ext = ext
        self.params = params

    @property
    def full_path(self):
        return f's3://{self.bucket}/{self.key}'

class EventParser:

    def _parse_s3(self, event):
        record = event['Records'][0]['s3']
        bucket = record['bucket']['name']
        key = record['object']['key']
        ext = path.splitext(key)[1]

        params = {}
        seperator = '='

        for frag in key.split('/'):
            key, sep, value = frag.partition(seperator)

            if sep == seperator:
                params[key] = value

        f = S3File(bucket, key, ext, params)

        return f

    def parse(self, event, type):
        if type == 's3':
            return self._parse_s3(event)
        else:
            raise Exception(f'Unsupported event type: {type}')
