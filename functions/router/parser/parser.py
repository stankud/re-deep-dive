class S3Event():
    def __init__(self, event):
        self.event = event

    @property
    def s3_record(self):
        return self.event['Records'][0]['s3']

    @property
    def bucket(self):
        return self.s3_record['bucket']['name']

    @property
    def key(self):
        return self.s3_record['object']['key']

class Parser:
    pass
